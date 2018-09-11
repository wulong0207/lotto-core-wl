package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hhly.lottocore.persistence.ticket.dao.TicketInfoDaoMapper;
import com.hhly.lottocore.remote.sportorder.service.ITicketOutService;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.PaperDispose;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.O2OTicketBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;

@Service
public class TicketOutServiceImpl implements ITicketOutService, ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = Logger.getLogger(TicketOutServiceImpl.class);

	private static Map<String, List<TicketInfoBO>> TICKET = new ConcurrentHashMap<>();

	private static final ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(5);

	@Autowired
	private TicketInfoDaoMapper ticketInfoDaoMapper;

	// 控制刷新
	private Map<String, String> channelKey = new ConcurrentHashMap<>();
	// 记录机器码上一个票
	private Map<String, EqualTicket> machineKeyValue = new ConcurrentHashMap<>();

	//private static Map<String, LongAdder> ADDER = new ConcurrentHashMap<>();

	/**
	 * 竞彩足球票明细
	 */
	@Resource(name = "jczqHandleTicketDetails")
	private JczqHandleTicketDetails jczqHandleDetails;

	@Autowired
	private PaperDispose paperDispose;

	@Override
	public TicketInfoBO getTikcet(final TicketVO vo) {
		String key = getTicketKey(vo);
		if (TICKET.get(key) == null) {
			THREAD_POOL.execute(new Runnable() {
				@Override
				public void run() {
					initCache(vo);
				}
			});
			return null;
		} else {
			// 用计算，每个机器码每次从缓存取多少条数据后，需要从数据库取一次数据，保证不会存在有没有释放的票，和快截止的票
			/*String adderKey = key + vo.getMachineKey();
			LongAdder adder = ADDER.get(adderKey);
			if (adder == null) {
				adder = new LongAdder();
				ADDER.put(adderKey, adder);
			} else {
				adder.increment();
				if (adder.intValue() % 10 == 0) {
					return null;
				}
			}*/
			return getTikcetCache(vo);
		}
	}

	private TicketInfoBO getTikcetCache(TicketVO vo) {
		String ticketkey = getTicketKey(vo);
		String machineKey = machineKey(vo);
		EqualTicket value = machineKeyValue.get(machineKey);
		List<TicketInfoBO> list = TICKET.get(ticketkey);
		TicketInfoBO ticketInfoBO = null;
		if (list.isEmpty()) {
			return null;
		}
		if (StringUtils.isEmpty(value)) {
			ticketInfoBO = list.get(0);
			ticketInfoBO.getO2oTicketBO().setEqual(0);
		} else {
			boolean isEqualOrder = false;
			for (TicketInfoBO bo : list) {
				//相同投注内容票
				if (ticketInfoBO == null && value.getContentKey().equals(bo.getContentKey())) {
					ticketInfoBO = bo;
				}
				//相同订单的票
				if (value.getOrderCode().equals(bo.getOrderCode())) {
					ticketInfoBO = bo;
					//判断相同订单票的投注内容是否一样
					if (value.getContentKey().equals(bo.getContentKey())) {
						ticketInfoBO.getO2oTicketBO().setEqual(1);
					} else {
						ticketInfoBO.getO2oTicketBO().setEqual(0);
					}
					isEqualOrder = true;
					break;
				}
			}
			if (!isEqualOrder && ticketInfoBO != null) {
				ticketInfoBO.getO2oTicketBO().setEqual(1);
			}

			if (ticketInfoBO == null) {
				ticketInfoBO = list.get(0);
				ticketInfoBO.getO2oTicketBO().setEqual(0);
			}
		}
		if (ticketInfoBO != null) {
			list.remove(ticketInfoBO);
			String content = ticketInfoBO.getO2oTicketBO().getPrintContent();
			if (!StringUtils.isEmpty(content)) {
				content = String.format(content, vo.getMachineKey());
				ticketInfoBO.getO2oTicketBO().setPrintContent(content);
			}
		}
		return ticketInfoBO;
	}

	private String machineKey(TicketVO vo) {
		String machineKey = vo.getTicketChannelId() + vo.getMachineKey() + vo.getLotteryCode();
		return machineKey;
	}

	private String getTicketKey(TicketVO vo) {
		return vo.getTicketChannelId() + ";" + vo.getLotteryCode();
	}

	/**
	 * 同步初始缓存
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月7日 下午6:29:46
	 * @param vo
	 */
	private void initCache(TicketVO vo) {
		String key = getTicketKey(vo);
		String status = channelKey.get(key);
		if ("1".equals(status)) {
			return;
		}
		synchronized (TicketOutServiceImpl.class) {
			if ("1".equals(status)) {
				return;
			}
			channelKey.put(key, "1");
		}
		try {
			initTicket(vo, key);
		} catch (Exception e) {
			logger.info("加载缓存数据异常", e);
		} finally {
			channelKey.remove(vo.getTicketChannelId());
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月7日 下午6:29:30
	 * @param vo
	 */
	private void initTicket(TicketVO vo, String key) {
		logger.info("人工出票加载数据:" + key);
		List<TicketInfoBO> list = ticketInfoDaoMapper.getTicketInfoCache(vo);
		for (TicketInfoBO ticketInfoBO : list) {
			ticketInfoBO.setContentKey(getContentKey(ticketInfoBO));
			O2OTicketBO oto = getOtoTicket(ticketInfoBO, "%s");
			ticketInfoBO.setO2oTicketBO(oto);
		}
		try {
			Collections.synchronizedCollection(list);
			TICKET.put(key, list);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		logger.info("人工出票加载数据结束" + vo.getTicketChannelId() + ",缓存数据条数：" + list.size());
	}

	/**
	 * 获取类容key
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月24日 下午12:04:06
	 * @param ticketInfoBO
	 * @return
	 */
	private String getContentKey(TicketInfoBO ticketInfoBO) {
		StringBuilder sb = new StringBuilder();
		if (ticketInfoBO.getLotteryCode() == 300) {
			boolean isAdd = true;
			for (char c : ticketInfoBO.getTicketContent().toCharArray()) {
				switch (c) {
				case '@':
					isAdd = false;
					break;
				case ',':
				case ')':
					isAdd = true;
				default:
					if (isAdd) {
						sb.append(c);
					}
					break;
				}
			}
		} else {
			sb.append(ticketInfoBO.getTicketContent());
		}
		sb.append(ticketInfoBO.getTicketMoney());
		sb.append(ticketInfoBO.getMultipleNum());
		return sb.toString();
	}

	@Override
	public O2OTicketBO getOtoTicket(TicketInfoBO tib, String machineKey) {
		String printContent = "";
		// 避免格式转换影响出票业务
		try {
			printContent = getPrintContent(tib, machineKey);
		} catch (Exception e) {
			logger.info("人工出票格式转换异常", e);
		}
		O2OTicketBO o2OTicketBO = new O2OTicketBO();
		if (tib.getLotteryCode() == 300) {
			OrderBaseInfoBO orderBaseInfoBO = new OrderBaseInfoBO();
			orderBaseInfoBO.setLotteryChildCode(tib.getLotteryChildCode());
			List<TicketInfoBO> list = new ArrayList<>();
			list.add(tib);
			TicketOrderInfoBO football = jczqHandleDetails.handle(orderBaseInfoBO, list, null);
			o2OTicketBO.setSportList(football.getSportList());
		} else {
			o2OTicketBO.setTicketContent(Arrays.asList(tib.getTicketContent().split(";")));
		}
		o2OTicketBO.setId(tib.getId());
		o2OTicketBO.setMultipleNum(tib.getMultipleNum());
		o2OTicketBO.setPrintContent(printContent);
		o2OTicketBO.setPrintUrl("");
		return o2OTicketBO;
	}

	/**
	 * 获取打印字符串
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月18日 上午9:56:42
	 * @param tib
	 * @return
	 */
	private String getPrintContent(TicketInfoBO tib, String machineKey) {
		Ticket pt = new Ticket();
		String[] cg = null;
		if (tib.getLotteryCode() == 300) {
			String[] str = tib.getTicketContent().split("\\^");
			pt.setChildType(str[1]);
			cg = str[1].split("_");
		}
		pt.setMachineKey(machineKey);
		pt.setContent(tib.getTicketContent());
		pt.setChildCode(tib.getLotteryChildCode());
		pt.setContentType(tib.getContentType() + "");
		pt.setMultiple(tib.getMultipleNum());
		pt.setLotteryCode(tib.getLotteryCode());
		pt.setLottoAdd(tib.getLottoAdd() == null ? 0 : tib.getLottoAdd().intValue());
		StringBuilder sb = new StringBuilder(paperDispose.coordinate(pt));
		if (sb.length() > 0) {
			sb.append(";票号:");
			sb.append(tib.getId());
			sb.append(" 机器:");
			sb.append(machineKey);
			sb.append(";");
			sb.append(getPlayCode(tib.getLotteryChildCode()));
			sb.append(",");
			sb.append(tib.getTicketMoney());
			sb.append("元,");
			sb.append(tib.getMultipleNum());
			sb.append("倍");
			if (cg != null) {
				sb.append(",");
				sb.append(cg[0]);
				sb.append("串");
				sb.append(cg[1]);
			}
		}
		return sb.toString();
	}

	private String getPlayCode(int childCode) {
		switch (childCode) {
		// 足球
		case 30001:// 胜平负
			return "足球混合投注";
		case 30002:// 胜平负
			return "足球胜平负";
		case 30003:// 让胜平负
			return "足球让胜平负";
		case 30004:// 比分
			return "足球比分";
		case 30005:// 总进球
			return "足球总进球";
		case 30006:// 半全场
			return "足球半全场";
		case 10201:// 大乐透
			return "大乐透普通投注";
		case 10202:// 大乐透
			return "大乐透胆拖投注";
		default:
			break;
		}
		throw new RuntimeException("子玩法错误");
	}

	/**
	 * 刷新缓存数据
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月7日 下午6:29:11
	 */
	private void refresh() {
		logger.info("刷新缓存开始");
		for (Map.Entry<String, List<TicketInfoBO>> entry : TICKET.entrySet()) {
			String[] key = entry.getKey().split(";");
			TicketVO vo = new TicketVO();
			vo.setLotteryCode(key[1]);
			vo.setTicketChannelId(key[0]);
			initCache(vo);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					THREAD_POOL.scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							try {
								refresh();
							} catch (Exception e) {
								logger.info("人工出票缓存刷新异常", e);
							}

						}
					}, 0, 8, TimeUnit.MINUTES);
				}
			}, 30000);
		}
	}

	@Override
	public void setEqualMachineKey(TicketInfoBO ticketInfoBO, TicketVO vo) {
		String machineKey = machineKey(vo);
		EqualTicket et = new EqualTicket();
		et.setContentKey(ticketInfoBO.getContentKey() == null ? "" : ticketInfoBO.getContentKey());
		et.setOrderCode(ticketInfoBO.getOrderCode());
		machineKeyValue.put(machineKey, et);
	}

	/**
	 * 相同票
	 * 
	 * @desc
	 * @author jiangwei
	 * @date 2018年7月25日
	 * @company 益彩网络科技公司
	 * @version 1.0
	 */
	public class EqualTicket {

		private String orderCode;

		private String contentKey;

		public String getOrderCode() {
			return orderCode;
		}

		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}

		public String getContentKey() {
			return contentKey;
		}

		public void setContentKey(String contentKey) {
			this.contentKey = contentKey;
		}

	}
}
