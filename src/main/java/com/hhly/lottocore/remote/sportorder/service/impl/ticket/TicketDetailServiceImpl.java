package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hhly.lottocore.base.util.DESUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.group.dao.OrderGroupContentMapper;
import com.hhly.lottocore.persistence.jc.dao.JcDataDaoMapper;
import com.hhly.lottocore.persistence.ticket.dao.TicketInfoDaoMapper;
import com.hhly.lottocore.rabbitmq.provider.impl.MessageProviderImpl;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.lottocore.remote.sportorder.service.ITicketDetailService;
import com.hhly.lottocore.remote.sportorder.service.ITicketOutService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.PayConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.page.AbstractStatisticsPage;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupContentBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.NumTicketDetailInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.O2OTicketBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketChannelVO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc
 * @date 2017/11/1 10:46
 * @company 益彩网络科技公司
 */
@Service("ticketDetailService")
public class TicketDetailServiceImpl implements ITicketDetailService {

    private static Logger logger = Logger.getLogger(TicketDetailServiceImpl.class);
    

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private IOrderSearchService orderSearchService;

    @Autowired
    private TicketInfoDaoMapper ticketInfoDaoMapper;

    @Autowired
    private OrderGroupContentMapper orderGroupContentMapper;

    @Autowired
    private IPageService pageService;
    
    @Autowired
    private MessageProviderImpl ticketDetailService;

    @Autowired
    private JcDataDaoMapper jcDataDaoMapper;
    /**
     * 竞彩足球票明细
     */
    @Resource(name="jczqHandleTicketDetails")
    private JczqHandleTicketDetails jczqHandleDetails;
    
    /**
     * 竞彩篮球票明细
     */
    @Resource(name="jclqHandleTicketDetails")
    private JclqHandleTicketDetails jclqHandleDetails;
    
    /**
     * 北京单场票明细
     */
    @Resource(name="bjdcHandleTicketDetails")
    private BjdcHandleTicketDetails bjdcHandleDetails;


    @Resource(name="gYJHandleTicketDetails")
    private GYJHandleTicketDetails gYJHandleTicketDetails;

	@Autowired
	private ITicketOutService ticketOutService;

    @Override
    public ResultBO<?> queryTicketDetailInfo(final OrderQueryVo orderQueryVo) throws Exception{
        TicketOrderInfoBO ticketOrderInfoBO = new TicketOrderInfoBO();
        try {
        	ResultBO<?> result = userInfoCacheService.checkToken(orderQueryVo.getToken());
            if(result.isError())             	
            	return result;
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            ResultBO<?> resultBO = orderSearchService.queryOrderInfo(orderQueryVo.getOrderCode(), orderQueryVo.getToken());
            if (resultBO.isOK()) {
                OrderBaseInfoBO orderBaseInfoBO = (OrderBaseInfoBO) resultBO.getData();
                //出票成功可以继续查询出票明细
                boolean isContinue = false;
                if (orderBaseInfoBO.getPayStatus() == Integer.valueOf(PayConstants.PayStatusEnum.PAYMENT_SUCCESS.getKey())
                        && orderBaseInfoBO.getOrderStatus() == OrderEnum.OrderStatus.TICKETED.getValue()) {//支付成功且出票成功
                    isContinue = true;
                }
                //第一版，跟单的全部不能看出票明细
                if(Constants.NUM_3 == orderBaseInfoBO.getOrderType()){//跟单
                    isContinue = false;
                }
                if (isContinue == false) {
                    return ResultBO.err(MessageCodeConstants.ORDER_STATUS_CAN_NOT_SEE_TICKET);
                }
                setOrderInfo(ticketOrderInfoBO, orderBaseInfoBO);
                //判断是否为合买发单人
                if(!isFollow(orderQueryVo.getOrderCode(),userInfo.getId())){
                    orderQueryVo.setUserId(userInfo.getId());
                }

                PagingBO<TicketInfoBO> pageData = pageService.getPageData(orderQueryVo,
                        new AbstractStatisticsPage<TicketInfoBO>() {
                            int total = 0;

                            @Override
                            public int getTotal() {
                                total = ticketInfoDaoMapper.findTicketListCount(orderQueryVo);
                                return total;
                            }
                            @Override
                            public List<TicketInfoBO> getData() {
                                List<TicketInfoBO> result = ticketInfoDaoMapper.findTicketList(orderQueryVo);
                                return result;
                            }

                            @Override
                            public Object getOther() {
                                return null;
                            }
                        }
                );
                setTicketInfo(ticketOrderInfoBO, orderBaseInfoBO, pageData);
            }
        }catch (Exception e){
            logger.error("查询出票明细列表失败！",e);
            throw e;
        }
        return ResultBO.ok(ticketOrderInfoBO);
    }

    /**
     * 是否属于跟单
     * @param orderCode 订单编号
     * @param userId 跟单用户ID
     * @return
     */
    private boolean  isFollow(String orderCode, Integer userId){
        OrderGroupContentBO orderGroupContentBO = orderGroupContentMapper.findOrderGroupRecord(orderCode);
        if(!ObjectUtil.isBlank(orderGroupContentBO) && orderGroupContentBO.getUserId().equals(userId)){
            return false;
        }
        return true;
    }

    /**
     * 设置票明细信息
     * @param ticketOrderInfoBO
     * @param orderBaseInfoBO
     * @param pageData
     */
    private void setTicketInfo(TicketOrderInfoBO ticketOrderInfoBO, OrderBaseInfoBO orderBaseInfoBO, PagingBO<TicketInfoBO> pageData) {
        ticketOrderInfoBO.setTotal(pageData.getTotal());
        Integer lotteryType = Constants.getLotteryType(orderBaseInfoBO.getLotteryCode());
        if(lotteryType==Constants.NUM_1){//数字彩或者高频彩
            ticketOrderInfoBO.setNumList(getNumTicketList(pageData.getData(),orderBaseInfoBO.getIsDltAdd()));
        }else if(lotteryType==Constants.NUM_2 || lotteryType==Constants.NUM_4){//竞技彩：竞彩足球、竞彩篮球、北京单场、胜负过关
        	Lottery lottery = Lottery.getLottery(orderBaseInfoBO.getLotteryCode());
        	switch(lottery){
        	case FB:
        		TicketOrderInfoBO football = jczqHandleDetails.handle(orderBaseInfoBO, pageData.getData(), null);
        		ticketOrderInfoBO.setSportList(football.getSportList());
        		break;
        	case BB:
        		TicketOrderInfoBO baseketball = jclqHandleDetails.handle(orderBaseInfoBO, pageData.getData(), null);
        		ticketOrderInfoBO.setSportList(baseketball.getSportList());
        		break;
        	case BJDC:
        	case SFGG:
        		TicketOrderInfoBO bjdc = bjdcHandleDetails.handle(orderBaseInfoBO, pageData.getData(), null);
        		ticketOrderInfoBO.setSportList(bjdc.getSportList());
        		break;
            case CHP:
            case FNL:
                TicketOrderInfoBO gyj = gYJHandleTicketDetails.handle(orderBaseInfoBO, pageData.getData(), null);
                ticketOrderInfoBO.setSportList(gyj.getSportList());
                break;
        	}
        }else if(lotteryType==Constants.NUM_3){// 四场进球彩、十四场胜负彩、九场胜负彩、六场半全场
        	ticketOrderInfoBO.setNumList(getNumTicketList(pageData.getData(),orderBaseInfoBO.getIsDltAdd()));
        }
    }

    /**
     * 设置订单信息
     * @param ticketOrderInfoBO
     * @param orderBaseInfoBO
     */
    private void setOrderInfo(TicketOrderInfoBO ticketOrderInfoBO, OrderBaseInfoBO orderBaseInfoBO) {
        ticketOrderInfoBO.setDrawCode(orderBaseInfoBO.getDrawCode());
        ticketOrderInfoBO.setIssueCode(orderBaseInfoBO.getLotteryIssue());
        ticketOrderInfoBO.setLotteryCode(orderBaseInfoBO.getLotteryCode());
        ticketOrderInfoBO.setLotteryName(orderBaseInfoBO.getLotteryName());
        ticketOrderInfoBO.setWinningStatus(orderBaseInfoBO.getWinningStatus());
    }

    /**
     * 获取数字彩票明细集合
     * @param ticketInfoBOs
     * @return
     */
    private List<NumTicketDetailInfoBO> getNumTicketList(List<TicketInfoBO> ticketInfoBOs,Integer isDltAdd){
        if(!ObjectUtil.isBlank(ticketInfoBOs)){
            List<NumTicketDetailInfoBO> numTicketDetailInfoBOs = new ArrayList<NumTicketDetailInfoBO>();
            for(TicketInfoBO ticketInfoBO : ticketInfoBOs){
                NumTicketDetailInfoBO numTicketDetailInfoBO = new NumTicketDetailInfoBO();
                numTicketDetailInfoBO.setWinningStatus(Integer.valueOf(ticketInfoBO.getWinningStatus()));
                /*int note = ticketDetailHandler.getNumberManyNote(ticketInfoBO.getTicketContent(),ticketInfoBO.getLotteryCode(),
                        ticketInfoBO.getLotteryChildCode(),ticketInfoBO.getContentType());*/
                Double note=0d;
                //数字彩山东11选5有几个彩种的单注金额不是2块或3块
                /*if(Constants.NUM_1 == isDltAdd){//是大乐透追号
                    Double amount1 =NumberUtil.mul(ticketInfoBO.getMultipleNum(),3);
                    note = NumberUtil.div(ticketInfoBO.getTicketMoney(),amount1,2);
                }else{
                    Double amount1 =NumberUtil.mul(ticketInfoBO.getMultipleNum(),2);
                    note = NumberUtil.div(ticketInfoBO.getTicketMoney(),amount1,2);
                }*/
                double signAmount = Constants.getPriceByLotChild(ticketInfoBO.getLotteryChildCode(),
                        Constants.getPrice(isDltAdd.shortValue()));
                Double amount1 = NumberUtil.mul(ticketInfoBO.getMultipleNum(),signAmount);
                note = NumberUtil.div(ticketInfoBO.getTicketMoney(),amount1,2);
                numTicketDetailInfoBO.setBetNum(note.intValue());
                numTicketDetailInfoBO.setMoney(ticketInfoBO.getTicketMoney());
                numTicketDetailInfoBO.setMultiple(ticketInfoBO.getMultipleNum());
                numTicketDetailInfoBO.setPlanContent(ticketInfoBO.getTicketContent());
                numTicketDetailInfoBO.setPlayType(ticketInfoBO.getContentType());
                numTicketDetailInfoBO.setPreBonus(ticketInfoBO.getPreBonus());
                numTicketDetailInfoBO.setTicketStatus(Integer.valueOf(ticketInfoBO.getTicketStatus()));
                numTicketDetailInfoBO.setChildCode(ticketInfoBO.getLotteryChildCode());
                numTicketDetailInfoBO.setChildName(ticketInfoBO.getLotteryChildName());
                numTicketDetailInfoBOs.add(numTicketDetailInfoBO);
            }
            return numTicketDetailInfoBOs;
        }
        return null;
    }

	@Override
	public String getChannel(TicketChannelVO vo) {
		vo.setPassword(DESUtil.encrypt(vo.getPassword(), "YGWLGPGJ"));
		return ticketInfoDaoMapper.getTicketChannel(vo);
	}

	@Override
	public O2OTicketBO getTicket(TicketVO vo) {
		long start = System.currentTimeMillis();
	    //缓存取票
		TicketInfoBO result = getCacheTicket(vo);
		if(result == null){
			//正常取票
			TicketInfoBO tib = null;
			for(;;){
				tib = ticketInfoDaoMapper.getTicketInfo(vo);
				if(tib == null){
					break;
				}else{
					if(StringUtil.isEmpty(tib.getThirdNum())){
						int num = ticketInfoDaoMapper.updateTicketThirdNum(tib.getId().toString(),vo.getMachineKey(),vo.getTicketChannelId());
						if(num == 1){
							result = tib;
	                        break;
						}
					}else{
						result = tib;
						break;
					}
				}
			}
			long ticketTime = System.currentTimeMillis();
			if(result != null){
				O2OTicketBO  o2oTicketBO = ticketOutService.getOtoTicket(tib,vo.getMachineKey());
				o2oTicketBO.setEqual(0);
	            result.setO2oTicketBO(o2oTicketBO);
	            long end = System.currentTimeMillis();
				logger.info("人工出票获取票时间日志（毫秒）：票:" + (ticketTime - start) +",解析:"+ (end - ticketTime));
	        }
		}else{
			logger.info("从缓存加载数据成功");
		}
		if(result != null){
			ticketOutService.setEqualMachineKey(result, vo);
			return result.getO2oTicketBO();
		}else{
			return null;
		}
	}
    /**
     * 从缓存获取票
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月7日 下午5:09:41
     * @param vo
     * @return
     */
	private TicketInfoBO getCacheTicket(TicketVO vo) {
		TicketInfoBO tib = null;
		for(;;){
			TicketInfoBO temp = ticketOutService.getTikcet(vo);
			if(temp != null){
				int num = ticketInfoDaoMapper.updateTicketThirdNum(temp.getId().toString(),vo.getMachineKey(),vo.getTicketChannelId());
				if(num == 1){
					tib = temp;
					break;
				}
			}else{
				break;
			}
		}
		return tib;
	}

	@Override
	public int updateTicket(TicketVO vo) {
		int num = 0;
		if("0".equals(vo.getType())){
			vo.setStatus("-2");
		}else if("1".equals(vo.getType())){
			if("300".equals(vo.getLotteryCode())){
				TicketInfoBO ticketContent = ticketInfoDaoMapper.getTicketInfoOut(vo.getId());
				List<Match> matchs = getMatch(ticketContent);
	            List<JczqDaoBO> jczqDaoBO = jcDataDaoMapper.findFootBallSpBySystemCode(vo.getLotteryCode(),matchs);
	            String receiptContent = getReceiptContent(matchs,jczqDaoBO);
	            String receiptContentDetail = getReceiptContentDetail(receiptContent,ticketContent.getTicketContent());
	            vo.setReceiptContent(receiptContent);
	            vo.setReceiptContentDetail(receiptContentDetail);	
			}
            vo.setStatus("4");
		}else if("2".equals(vo.getType())){
			return ticketInfoDaoMapper.updateTicketThirdNumClear(vo.getId().toString());
		}else{
			throw new ResultJsonException("状态错误");
		}
		num =  ticketInfoDaoMapper.updateTicketStatus(vo);
		if(num == 0){
			logger.info("修改出票状态失败：" + vo);
		}
		ticketDetailService.sendMessage("people_out_ticket", vo.getId());
		return num;
	}

	/**
	 * 获取出票列表
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public PagingBO<TicketInfoBO> findTicketInfo(final TicketVO vo) {
		vo.setEndTicketTime(DateUtil.addDay(new Date(), -1));
		return pageService.getPageData(vo,
				new AbstractStatisticsPage<TicketInfoBO>() {
					int total = 0;

					@Override
					public int getTotal() {
						total = ticketInfoDaoMapper.findO2OTicketCount(vo);
						return total;
					}
					@Override
					public List<TicketInfoBO> getData() {
						List<TicketInfoBO> result = ticketInfoDaoMapper.findO2OTicketList(vo);
						return result;
					}

					@Override
					public Object getOther() {
						return null;
					}
				}
		);
	}

	/**
	 * 根据票id获取票信息
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public O2OTicketBO getTicketById(TicketVO vo) {
		TicketInfoBO tib = ticketInfoDaoMapper.getTicketInfoById(vo);
		if(tib == null){
			return null;
		}
		return ticketOutService.getOtoTicket(tib,vo.getMachineKey());
	}

	/**
	 * 解析出牌sp放入投资内容
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年6月25日 下午5:25:23
	 * @param receiptContent
	 * @param ticketContent
	 * @return
	 */
    private String getReceiptContentDetail(String receiptContent, String ticketContent) {
		String[] all = receiptContent.split("@");
		String[] odds = all[0].split("[-A]");
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		int num = 0;
		boolean isAdd = true;
		for (char c : ticketContent.toCharArray()) {
			switch (c) {
			case '[':
				sb.append(c);
				isAdd = false;
				break;
			case ']':
				sb.append(temp);
				sb.append(c);
				temp.setLength(0);
				isAdd = true;
				break;
			case '@':
				sb.append(c);
				isAdd = false;
				break;
			case ')':
			case ',':
				sb.append(String.format("%.2f", Double.valueOf(odds[num]))).append(c);
				num++;
				temp.setLength(0);
				isAdd = true;
				break;
			default:
				if (isAdd) {
					sb.append(c);
				} else {
					temp.append(c);
				}
				break;
			}
		}
		return sb.toString();
	}

	/**
     * 获取sp
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年6月25日 下午4:26:14
     * @param matchs
     * @param jczqDaoBOs
     * @return
     */
	private String getReceiptContent(List<Match> matchs, List<JczqDaoBO> jczqDaoBOs) {
		StringBuilder sb = new StringBuilder();
		for (Match match : matchs) {
			JczqDaoBO jb = null;
			 for (JczqDaoBO jczqDaoBO : jczqDaoBOs) {
				if(match.getMatchId().equals(jczqDaoBO.getSystemCode())){
					jb = jczqDaoBO;
					break;
				}
			}
			String sp = getSp(match,jb);
			if(sb.length()>1){
				sb.append("-");
			}
			sb.append(sp);
		}
		return sb.toString();
	}
    /**
     * 通过反射获取玩花的sp
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年6月25日 下午5:25:55
     * @param match
     * @param jb
     * @return
     */
	private String getSp(Match match, JczqDaoBO jb) {
		StringBuilder sb = new StringBuilder();
		for (String c : match.getContent()) {
			if(sb.length() > 1){
				sb.append("A");
			}
			String key = match.getMatchType() + "_" + c;
			String field = match.getField(key);
			sb.append(ClassUtil.getField(jb, field, JczqDaoBO.class));
		}
		return sb.toString();
	}

	/**
	 * 解析投资内容为比赛信息
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年6月25日 下午5:27:57
	 * @param ticketContent
	 * @return
	 */
	private List<Match> getMatch(TicketInfoBO ticketContent) {
		List<Match> matchs = new ArrayList<>();
		StringBuilder temp = new StringBuilder();
		Match match = null;
		boolean isAdd = true;
		for (char c : ticketContent.getTicketContent().toCharArray()) {
		    switch (c) {
			case '[':
				isAdd = false;
				break;
			case '(':
				match =new Match();
				String matchId = temp.toString();
				if(matchId.indexOf("_") == -1){
					match.setMatchId(matchId);
					match.setMatchType(ticketContent.getLotteryChildCode().toString());
				}else{
					String[] str =  matchId.split("_");
					match.setMatchId(str[0]);
					match.setMatchType(getChildCode(str[1]));
				}
				matchs.add(match);
				temp.setLength(0);
				break;
			case ']':
				isAdd = true;
				break;
			case '@':
				match.addContent(temp.toString());
				temp.setLength(0);
				break;
			case ',':
			case '|':
				temp.setLength(0);
				break;
			default:
				if(isAdd){
					temp.append(c);	
				}
				break;
			}	
		}
		return matchs;
	}
	/**
	 * 转换子玩法
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年6月25日 下午5:27:40
	 * @param code
	 * @return
	 */
	private String getChildCode(String code){
		switch (code) {
		case "S":// 胜平负
			return "30002";
		case "R":// 让胜平负
			return "30003";
		case "Q":// 比分
			return "30004";
		case  "Z":// 总进球
			return "30005";
		case "B":// 半全场
			return "30006";
		default:
			break;
		}
		return "";
	}
}
