package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.remote.sportorder.service.ValidateOrderPayTimeService;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.SportsOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryPr;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

import net.sf.json.JSONObject;

@Service("validateOrderPayTimeService")
public class ValidateOrderPayTimeServiceImpl implements ValidateOrderPayTimeService {
	private Logger logger = LoggerFactory.getLogger(ValidateOrderPayTimeServiceImpl.class);
	
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	@Autowired
	@Qualifier("sportsOrderValidate")
	private SportsOrderValidate sportsOrderValidate;

	@Override
	public boolean checkPayEndTime(String orderCode) throws Exception {
		logger.debug("订单支付判断是否在可支付时间内接收参数,订单编号:{}",orderCode);
		OrderInfoBO orderInfoBO = orderInfoDaoMapper.getOrderInfo(orderCode);
		logger.debug("订单支付判断是否在可支付时间,订单信息:{}",JSONObject.fromObject(orderInfoBO));
		if(!ObjectUtil.isBlank(orderInfoBO)){
			Integer lotteryCode = orderInfoBO.getLotteryCode();
			//官方截止时间
			Date endTicketTime = orderInfoBO.getEndTicketTime();
			//本站订单系统截止时间
			Date endSysTime = orderInfoBO.getEndSysTime();
		    //提前截止时间差
		    long d = DateUtil.getDifferenceTime(endTicketTime, endSysTime);
		    
			Date orderPayEndTime = Constants.getOrderPayEndTime(lotteryCode,d,endSysTime);
			Date date = new Date();
			logger.debug("订单支付判断是否在可支付时间,当前时间:"+DateUtil.convertDateToStr(date, "yyyy-MM-dd HH:mm:ss")+",官方截止时间:"+DateUtil.convertDateToStr(endTicketTime, "yyyy-MM-dd HH:mm:ss")+",本站订单系统截止时间:"+DateUtil.convertDateToStr(endSysTime, "yyyy-MM-dd HH:mm:ss")+",提前截止时间之差多少毫秒:{}"+d);
			//当前时间小于支付截止时间
			if(date.before(orderPayEndTime)){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * 检查截止时间
	 */
	@Override
	public ResultBO<?> checkFbAndBbEndTime(OrderInfoVO orderInfoVO, List<LotBettingMulBO> betMulBO ,LotteryBO lotteryBO, Integer endTime, SportAgainstInfoBO againstInfoBO) throws Exception {
		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		Date[] dates = this.getOrderEndTime(orderInfoVO, betMulBO, endTime, againstInfoBO);
		Date orderEndTime = dates[0];//支付截止时间， 用于业务判断，不存库
		Date endTicketTime = dates[1];//官方截止时间，存库
		Date orderPayEndTime = dates[2];//支付截止时间，存库
		Date endLocalTime = dates[3];//本站截止时间
		orderInfoVO.setEndTicketTime(endTicketTime);
		orderInfoVO.setEndSysTime(orderPayEndTime);
		orderInfoVO.setEndLocalTime(endLocalTime);
		LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(lotteryCode));
		Date now = new Date();
		ResultBO<?> resultBO = ResultBO.ok();
		switch (lott) {
			case GPC://高频彩
			case SZC://数字彩
			case ZC://足彩
			case GYJ://冠亚军
				if(now.after(orderEndTime)){
					return ResultBO.err(MessageCodeConstants.PRE_BUY_BETNUM_LIMIT_SERVICE,orderInfoVO.getLotteryIssue());
				}
				break;
			case JJC://竞技彩
			case BJDC://北单
				logger.debug("方案截止时间:{},当前系统时间:{}",DateUtil.convertDateToStr(orderEndTime, "yyyy-MM-dd HH:mm:ss"),DateUtil.convertDateToStr(now, "yyyy-MM-dd HH:mm:ss"));
				//(延期比赛，存在两种情况：一、期号编号，对阵编号变；二、只变开赛时间，和截止销售时间。)
				if(now.after(orderEndTime)){
					return ResultBO.err(MessageCodeConstants.PRE_BUY_BETNUM_LIMIT_SERVICE);
				}
				break;
			default:
				break;
		}
		return resultBO;
	}
	
	/**
	 * 
	 * @Description: 获取方案截止时间
	 * @param orderInfoVO 下单信息
	 * @param betMulBOList 注数、倍数与时间关系集合，按时间降序
	 * @param endTime 需要提前的时间(单位：正数秒)
	 * @return Date[] 截止时间   下标 0:本站订单系统截止时间  1：订单官方截止时间  2:支付截止时间 
	 * @throws Exception
	 * @author wuLong
	 * @date 2017年3月28日 上午9:54:00
	 */
	public Date[] getOrderEndTime(OrderInfoVO orderInfoVO, List<LotBettingMulBO> betMulBOList, Integer endTime, SportAgainstInfoBO againstInfoBO) throws Exception {
		String lotteryCodeStr = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		Integer lotteryCode = Integer.valueOf(lotteryCodeStr);//彩种
		String issueCode = orderInfoVO.getLotteryIssue();//彩期
		String buyScreen = orderInfoVO.getBuyScreen();//购买赛事编号
		int multiple = orderInfoVO.getMultipleNum();//总倍数
		OrderDetailVO orderDetailVO = getMaxBuyNumberObjectFromOrderInfoVO(orderInfoVO);//最大注数明细对象
		
		Date[] dates = new Date[5];
		Date orderEndTime = null;
		IssueBO lotteryIssueBO = null;
		
		Date officialEndTime = null;
//		Date endTicketTime   = null;
		
	    if(ObjectUtil.isBlank(lotteryIssueBO)){
	    	lotteryIssueBO = lotteryIssueDaoMapper.findSingleFront(new LotteryVO(lotteryCode,issueCode));
	    }
	    logger.info("lotteryCode:"+lotteryCode+",issueCode:"+issueCode);
	    if(ObjectUtil.isBlank(lotteryIssueBO)){
	    	throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE));
	    }
	    officialEndTime = lotteryIssueBO.getOfficialEndTime();//彩期官方截止销售时间
	    Date issueEndTime = lotteryIssueBO.getSaleEndTime();//彩期本站销售截止时间
	    //倒序
	    Collections.sort(betMulBOList, new Comparator<LotBettingMulBO>() {
			@Override
			public int compare(LotBettingMulBO o1, LotBettingMulBO o2) {
				return o2.getBettindNum().compareTo(o1.getBettindNum());
			}
	    	
		});
		LotteryPr lott = LotteryEnum.getLottery(lotteryCode);
	    if(ObjectUtil.isBlank(orderEndTime)){
	    	switch (lott) {
	    	case GPC://高频彩
	    	case SZC://数字彩
	    	case ZC://足彩
			case GYJ://冠亚军
	    		orderEndTime = issueEndTime;
	    		break;
	    	case JJC://竞技彩
	    	case BJDC://北单
	    		List<String> matchs = Arrays.asList(buyScreen.split(SymbolConstants.COMMA));
	    		//根据彩种id和赛事id集合，查询对阵赛事信息
				//v1.0
//	    		List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstEndMatch(lotteryCode, matchs);
//	    		SportAgainstInfoBO againstInfoBO = listAgainstInfoBO.get(0);
				//v1.1
	    		orderEndTime = againstInfoBO.getSaleEndTime();//最早一场销售截止时间(还可以下单的截止时间)
	    		//新需求 v.1.5 
	    		LotteryVO lotteryVO = new LotteryVO(lotteryCode,issueCode);//通过下单彩期编号参数，获取下一期彩期信息，无论彩期是否已经切换，不受影响
	    		IssueBO nextIssue = lotteryIssueDaoMapper.findNextIssueByLotteryCodeAndIssueCode(lotteryVO);//查询下一期彩期信息
				logger.info("进入getOfficialEndTime方法前：getOfficialEndTime_officialEndTime=" + officialEndTime + ",getOfficialEndTime_systemCode="+againstInfoBO.getSystemCode());
				officialEndTime = getOfficialEndTime(againstInfoBO, nextIssue, lotteryIssueBO.getOfficialEndTime());
				logger.info("进入getOfficialEndTime方法后：officialEndTime="+officialEndTime);
	    		break;
	    	default://足彩
	    		break;
	    	}
	    }
	    dates[3] = orderEndTime;//本站截止时间：没作任何处理
	    /**根据注数倍数对应关系，提前本站截止时间*/
		if(!ObjectUtil.isBlank(lotteryIssueBO) && !ObjectUtil.isBlank(betMulBOList)){//betMulBOList 已经按endTime desc 排列
			for(LotBettingMulBO bettingMulBO : betMulBOList){
				if(orderDetailVO.getMultiple() * orderDetailVO.getBuyNumber() > bettingMulBO.getBettindNum() || multiple >bettingMulBO.getMultipleNum()){
					orderEndTime = DateUtil.addSecond(orderEndTime, -bettingMulBO.getEndTime());
					break;
				}
			}
		}
		dates[0] = orderEndTime;//本站截止时间(用于逻辑判断，不存库)

		dates[1] = officialEndTime;//官方截止时间(存库，end_ticket_time)
		if(lott.name().equals(LotteryPr.GYJ.name())){//冠亚军支付截止时间 购买时间+五分钟
			dates[2] = DateUtil.addMinute(new Date(),5);//支付截止时间(存库，end_sys_time)
		}else{
			dates[2] = orderEndTime;//支付截止时间(存库，end_sys_time)
		}
		return dates;
	}
	
	/**
	 * 获取明细中最大的注数
	 * @author longguoyou
	 * @date 2017年11月9日
	 * @param orderInfoVO
	 * @return
	 */
	private static OrderDetailVO getMaxBuyNumberObjectFromOrderInfoVO(OrderInfoVO orderInfoVO){
		List<OrderDetailVO> listOrderDetails = orderInfoVO.getOrderDetailList();
		OrderDetailVO maxBuyNumber = Collections.max(listOrderDetails, new Comparator<OrderDetailVO>() {
			@Override
			public int compare(OrderDetailVO o1, OrderDetailVO o2) {
				return o1.getBuyNumber() - o2.getBuyNumber();
			}
		});
		return maxBuyNumber;
	}
	
	/**
	 * 获取官方截止时间(有逻辑判断运算)
	 * @author longguoyou
	 * @date 2017年11月8日
	 * @param againstInfoBO 最早开赛的对阵信息对象
	 * @param nextIssue 下一期 彩期对象
	 * @param currentOfficialEndTime 当前彩期官方截止时间
	 * @return
	 */
	private Date getOfficialEndTime(SportAgainstInfoBO againstInfoBO, IssueBO nextIssue, Date currentOfficialEndTime) {
		if(ObjectUtil.isBlank(againstInfoBO)){
			logger.info("获取最早一场截止比赛对象出错：againstInfoBO[" + againstInfoBO+"]");
		}
		logger.info("进入条件比较前：=systemCode="+againstInfoBO.getSystemCode() + ",=againstInfoBOStartTime=" + againstInfoBO.getStartTime() + ",=currentOfficialEndTime=" + currentOfficialEndTime + ",=nextOfficialEndTime=" + nextIssue.getOfficialStartTime());
		//当前彩期官方截止时间
//		Date currentOfficialEndTime = officialEndTime;
		//下一期彩期官方开始销售时间+30分钟
		Date nextOfficialEndTime = DateUtil.addMinute(nextIssue.getOfficialStartTime(), Constants.NUM_30);
		if(DateUtil.compare(againstInfoBO.getStartTime(), currentOfficialEndTime) == -1 ||
				DateUtil.compare(againstInfoBO.getStartTime(), nextOfficialEndTime) >= 0){
			logger.info("进入条件比较后(取最早一场截止比赛的开赛时间)：=systemCode=" + againstInfoBO.getSystemCode() + ",=againstInfoBOStartTime=" + againstInfoBO.getStartTime());
			return againstInfoBO.getStartTime();
		}else{
			logger.info("进入条件比较后(取当前彩期官方截止时间)：=systemCode=" + againstInfoBO.getSystemCode() + ",=againstInfoBOStartTime=" + againstInfoBO.getStartTime() + ",=currentOfficialEndTime=" + currentOfficialEndTime + ",=nextOfficialEndTime=" + nextOfficialEndTime);
			return currentOfficialEndTime;
		}
	}


	public static void main(String[] args) {
//		System.out.println("123|456".split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR).length);
//		System.out.println(DateUtil.getNow(DateUtil.DATE_FORMAT)+ " " + "08:00" + ":00");
//		System.out.println(DateUtil.convertStrToDate(DateUtil.getNow(DateUtil.DATE_FORMAT)+ " " + "08:00" + ":00"));
//		System.out.println(DateUtil.convertDateToStr(DateUtil.getDateDit(new Date(), 1), DateUtil.DATE_FORMAT));
		OrderInfoVO orderInfoVO = new OrderInfoVO();
		OrderDetailVO o1 = new OrderDetailVO();
		o1.setBuyNumber(10);
		OrderDetailVO o2 = new OrderDetailVO();
		o2.setBuyNumber(15);
		OrderDetailVO o3 = new OrderDetailVO();
		o3.setBuyNumber(12);
		List<OrderDetailVO> list = new ArrayList<OrderDetailVO>();
		list.add(o1);list.add(o2);list.add(o3);
		orderInfoVO.setOrderDetailList(list);
		System.out.println(getMaxBuyNumberObjectFromOrderInfoVO(orderInfoVO).getBuyNumber());
	}
}
