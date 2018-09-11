/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.*;

import com.hhly.skeleton.cms.dicmgr.bo.DicDataDetailBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryChildEnum.SaleStatus;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.LotteryEnum.LotIssueSaleStatus;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryPr;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.CalculatorUtil;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.RegularValidateUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 所有彩票验证基础方法集
 * @author longguoyou
 * @date 2017年12月1日
 * @compay 益彩网络科技有限公司
 */
@Component
public class OrderValidateMethod {
	
	@Autowired
	public RedisUtil redisUtil;
	
	
	/**
	 * 验证OrderInfoVO对象的必填值
	 * @param orderInfoVO
	 * @return
	 */
	public  ResultBO<?> verifyOrderRequired(OrderInfoVO orderInfoVO) {
		//验证tab值是否为空
		if((JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode())) && 
				ObjectUtil.isBlank(orderInfoVO.getTabType())){
			return ResultBO.err(MessageCodeConstants.TAB_TYPE_IS_NULL_FIELD);
		}
		//验证彩种参数长度合法性
		if(!isLotteryCodeParamRight(orderInfoVO.getLotteryCode())){
			return ResultBO.err(MessageCodeConstants.LOTTERY_CODE_IS_ILIEGAL_SERVICE);
		}
		//彩种id
		String childCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		if (!Lottery.contain(Integer.valueOf(childCode))) {
			return ResultBO.err(MessageCodeConstants.LOTTERY_CODE_IS_NULL_FIELD);
		}
		//用户token
		if (ObjectUtil.isBlank(orderInfoVO.getToken())) {			
			return ResultBO.err(MessageCodeConstants.USER_TOKEN_IS_NULL_FIELD);
		}
		// 彩期
		if (ObjectUtil.isBlank(orderInfoVO.getLotteryIssue())) {
			return ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_NULL_FIELD);
		}
		//订单总额
		if (ObjectUtil.isBlank(orderInfoVO.getOrderAmount())) {
			return ResultBO.err(MessageCodeConstants.ORDER_ACCOUNT_IS_NULL_FIELD);
		}
		//渠道ID
		if (ObjectUtil.isBlank(orderInfoVO.getChannelId())) {
			orderInfoVO.setChannelId(Constants.NUM_7+"");
//			return ResultBO.err(MessageCodeConstants.CHANNEL_ID_IS_NULL_FIELD);
		}
		//订单详情
		List<OrderDetailVO> orderDetailVO = orderInfoVO.getOrderDetailList();
		if (ObjectUtil.isBlank(orderDetailVO)) {
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_IS_NULL_FIELD);
		}
		//是否大乐透追号
		if(orderInfoVO.getIsDltAdd() == null){
			return ResultBO.err(MessageCodeConstants.IS_DLT_ADD_NULL_FIELD);
		}
		//购买方式
		if(ObjectUtil.isBlank(orderInfoVO.getBuyType())){
			return ResultBO.err(MessageCodeConstants.BUY_TYPE_IS_NULL_FIELD);
		}
		//平台类型
		if(ObjectUtil.isBlank(orderInfoVO.getPlatform())){
			return ResultBO.err(MessageCodeConstants.PLATFORM_IS_NULL_FIELD);
		}
		//限制竞足、竞篮的PC、H5端 预计奖金金额空判断
//		if((JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode()))){
//			if(orderInfoVO.getPlatform() == PlatformType.WEB.getValue() || orderInfoVO.getPlatform() == PlatformType.WAP.getValue()){
//				//奖金优化、单式上传允许通过
//				if(!ObjectUtil.isBlank(orderInfoVO.getCategoryId()) && orderInfoVO.getCategoryId() != Constants.NUM_3 && 
//						!ObjectUtil.isBlank(orderInfoVO.getCategoryId()) && orderInfoVO.getCategoryId() != Constants.NUM_6){
//					return isIllegalMaxBonus(orderInfoVO);
//				}
//			}
//		}
		return ResultBO.ok();
	}
	
	/**
	 * 判断最高奖金参数合法性
	 * @author longguoyou
	 * @date 2017年11月15日
	 * @param orderInfoVO
	 * @return
	 */
	private static ResultBO<?> isIllegalMaxBonus(OrderInfoVO orderInfoVO){
		//空判断
		if(ObjectUtil.isBlank(orderInfoVO.getMaxBonus())){
			return ResultBO.err(MessageCodeConstants.MAX_BONUS_IS_NULL_FIELD);
		}
		//是否包含“-”
		if(!orderInfoVO.getMaxBonus().contains(SymbolConstants.TRAVERSE_SLASH)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//数组大小
		String[] minMaxBonus = orderInfoVO.getMaxBonus().split(SymbolConstants.TRAVERSE_SLASH);
		if(minMaxBonus.length != 2){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//最小值是否数字、小数
		if(!minMaxBonus[0].matches(RegularValidateUtil.REGULAR_ACCOUNT2) && !minMaxBonus[0].matches(RegularValidateUtil.REGULAR_POINT_NUM)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//最大值是否数字、小数
        if(!minMaxBonus[1].matches(RegularValidateUtil.REGULAR_ACCOUNT2) && !minMaxBonus[1].matches(RegularValidateUtil.REGULAR_POINT_NUM)){
        	return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		} 
		return ResultBO.ok();
	}
	
	public static void main(String[] args) {
//		OrderInfoVO orderInfoVO = new OrderInfoVO();
//		orderInfoVO.setMaxBonus("12.00-12.23");
//		ResultBO<?> result = isIllegalMaxBonus(orderInfoVO);
//		System.out.println(result.getMessage());
//		System.out.println("12.00".matches(RegularValidateUtil.REGULAR_ACCOUNT2));
//		System.out.println("12.23".matches(RegularValidateUtil.REGULAR_POINT_NUM));

		LotteryBO lotteryTypeBO = new LotteryBO();
		lotteryTypeBO.setSaleStatus(Short.valueOf("2"));
		System.out.println(lotteryTypeBO.getSaleStatus()==SaleStatus.STOP.getValue());
	}
	
    /**
     * 彩种参数传参是否正确:<br>
     * 竞技彩：lotteryCode 传子玩法<br>
     * 其它彩种：lotteryCode 传彩种
     * @author longguoyou
     * @date 2017年8月9日
     * @param lotteryCode
     * @return
     */
	private static boolean isLotteryCodeParamRight(Integer lotteryCode){
		LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(String.valueOf(lotteryCode).substring(0,3)));
		switch(lott){
		case JJC:
			return String.valueOf(lotteryCode).length() > Constants.NUM_3;
		case BJDC:
		case GPC:
		case SZC:
		case ZC:
			return String.valueOf(lotteryCode).length() == Constants.NUM_3;
		}
		return true;
	}
	
	/**
	 * 彩种开关(大)
	 * @param orderInfoVO
	 * @param map
	 * @return
	 */
	public ResultBO<?> controlLotterSwitch(OrderInfoVO orderInfoVO, Map<String,Object> map){
		LotteryBO lotteryBO = (LotteryBO)map.get("lotteryBO");
		List<DicDataDetailBO> listDicData = (List<DicDataDetailBO>)map.get("listDicData");//渠道限制彩种销售状态
		//验证彩种分平台限制
		if(!ObjectUtil.isBlank(lotteryBO.getPlatform())){
			if(lotteryBO.getPlatform().contains(String.valueOf(orderInfoVO.getPlatform()))){
				return ResultBO.err(MessageCodeConstants.PLATFORM_LIMIT);
			}
		}
		//验证彩种渠道限制
		for(DicDataDetailBO dicDataDetailBO : listDicData){
            String lotteryCode = null;
		    if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode())){
                lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
            }else{
                lotteryCode = String.valueOf(orderInfoVO.getLotteryCode());
            }
			if(dicDataDetailBO.getDicDataName().equals(lotteryCode)){//根据订单彩种编号
				String[] vals = dicDataDetailBO.getDicDataValue().split(SymbolConstants.COMMA);
				for(String val : vals){
					if(val.equals(orderInfoVO.getChannelId())){
						return ResultBO.err(MessageCodeConstants.LOTTERY_TYPE_IS_STOP_SALE_SERVICE);
					}
				}
			}
		}
		boolean flag = false;
		//验证彩种最低投注倍数限制，只验证竞足
		if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode())){
			if(!ObjectUtil.isBlank(orderInfoVO.getCategoryId())){
				if(orderInfoVO.getCategoryId() != Constants.NUM_3 && orderInfoVO.getCategoryId() != Constants.NUM_2){//排除单场制胜和奖金优化
					if(lotteryBO.getMinMultiple() != null && MathUtil.compareTo(orderInfoVO.getMultipleNum(),lotteryBO.getMinMultiple()) < Constants.NUM_0){
						flag = true;
					}
				}
			}else{
				if(lotteryBO.getMinMultiple() != null && MathUtil.compareTo(orderInfoVO.getMultipleNum(),lotteryBO.getMinMultiple()) < Constants.NUM_0){
					flag = true;
				}
			}
		}
		//验证彩种最低投注金额限制
		if(lotteryBO.getMinBet() != null && MathUtil.compareTo(orderInfoVO.getOrderAmount(),lotteryBO.getMinBet()) < Constants.NUM_0 || flag){
			return ResultBO.err(MessageCodeConstants.LOTTERY_MIN_BET_LIMIT_SERVICE, lotteryBO.getMinMultiple(),lotteryBO.getMinBet());
		}
		//如果是竞技彩, 则需要通过截取前三位号码，反查大彩种开关
		if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode())){
			String childCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);

			return deal(Integer.valueOf(childCode), lotteryBO);//反查大彩种
		} else {
			return deal(orderInfoVO.getLotteryCode(), lotteryBO);//本身子玩法/其它彩种
		}
	}
	
	/**
	 * 处理判断彩种开关
	 * @author longguoyou
	 * @date 2017年3月14日
	 * @param lotteryCode 子玩法
	 * @param lotteryBO   大彩种
	 * @return
	 */
	private ResultBO<?> deal(Integer lotteryCode, LotteryBO lotteryBO){
		String lotCode = lotteryCode.toString();
		LotteryBO lotteryTypeBO = lotteryBO;
		if(ObjectUtil.isBlank(lotteryTypeBO)){
			return ResultBO.err(MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE);
		}
		//判断彩种开关
		if(lotteryTypeBO.getSaleStatus() == SaleStatus.STOP.getValue()){
			return ResultBO.err(MessageCodeConstants.LOTTERY_TYPE_IS_STOP_SALE_SERVICE);
		}
		
		//验证子玩法
		if(lotCode.length() > 3){
			List<LotChildBO> childBOs = lotteryTypeBO.getListLotChildBO();
			if(ObjectUtil.isBlank(childBOs)){
				return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_ILIEGAL_SERVICE, lotCode + SymbolConstants.ENPTY_STRING);
			}
			LotChildBO lotChildBO = null;
			for(LotChildBO childBO : childBOs){
				if(childBO.getLotteryChildCode().equals(lotteryCode)){
					lotChildBO = childBO;
					break;
				}
			}
			if(ObjectUtil.isBlank(lotChildBO)){
				return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE);
			}
			//子玩法开关
			if(lotChildBO.getSaleStatus() == SaleStatus.SUSPEND.getValue()){
				return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_STOP_SALE_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	
	
	/**
	 * 验证期号信息 (期号开关;开售时间;销售截止时间)
	 * @param orderInfoVO
	 * @param issueBO
	 * @return
	 * @throws Exception 
	 */
	public ResultBO<?> verifyIssueInfo(OrderInfoVO orderInfoVO, IssueBO issueBO){
		//除了竞彩足球,篮球都验，竞足、竞篮存在跨期情况，前端参数，目前只传最早截止的一个彩期编号
		if(!JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) && !JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode())){
			String lotteryIssue = orderInfoVO.getLotteryIssue();
			IssueBO lotteryIssueBO = null;
			lotteryIssueBO = issueBO;
			//期号是否存在
			if(ObjectUtil.isBlank(lotteryIssueBO)){
				return ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE);
			}
			//期号开关
			if(lotteryIssueBO.getSaleStatus() == LotIssueSaleStatus.SUSPEND_SALE.getValue()){
				return ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_STOP_SALE_SERVICE, lotteryIssue);
			}
			//检查方案开售状态
			ResultBO<?> resultBO = this.checkLotIssueSaleStatus(lotteryIssueBO,orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 检查方案开售状态
	 * @author longguoyou
	 * @date 2017年12月1日
	 * @param lotteryIssueBO
	 * @param orderInfoVO
	 * @return
	 * @throws Exception
	 */
	public ResultBO<?> checkLotIssueSaleStatus(IssueBO lotteryIssueBO, OrderInfoVO orderInfoVO){
		String lotteryIssue = orderInfoVO.getLotteryIssue();
		ResultBO<?> resultBO = ResultBO.ok();
		LotIssueSaleStatus issueSaleStatus = LotIssueSaleStatus.getLotIssueSaleStatus(lotteryIssueBO.getSaleStatus());
		switch (issueSaleStatus) {
			case NOT_SALE:
				resultBO = ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_NOT_START_SALE_SERVICE, lotteryIssue);
				break;
			case STOP_SALE:	
				resultBO = ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_ENDING_SALE_SERVICE, lotteryIssue);
				break;
			case SUSPEND_SALE:
				resultBO = ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_STOP_SALE_SERVICE, lotteryIssue);
				break;
			case SALING:
			case PRE_SALING:
				break;
			default:
				resultBO = ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_ENDING_SALE_SERVICE, lotteryIssue);
				break;
		}
		return resultBO;
	}

	/**
	 * 验证用户基础信息 (是否锁定,黑白名单,类别控制)
	 * @param userInfoBO
	 * @return
	 */
	public ResultBO<?> verifyUserInfo(UserInfoBO userInfoBO){
		
		//查询用户是否存在
		if(ObjectUtil.isBlank(userInfoBO)){
			return ResultBO.err(MessageCodeConstants.USERNAME_IS_NOT_FOUND_SERVICE);
		}
		return ResultBO.ok();
	}
	
	
	/**
	 * 验证订单详情对象的必填值
	 * @param orderDetailVO
	 * @return
	 */
	public ResultBO<?> verifyOrderDetailRequired(OrderDetailVO orderDetailVO, LotteryBO lotteryBO) {
		//子玩法
		if (!ObjectUtil.isBlank(lotteryBO.getListLotChildBO()) && ObjectUtil.isBlank(orderDetailVO.getLotteryChildCode()) && 
				!LotteryChildEnum.LotteryChild.contain(orderDetailVO.getLotteryChildCode())) {
			return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_ILIEGAL_SERVICE);
		}
		//投注内容
		if (ObjectUtil.isBlank(orderDetailVO.getPlanContent())) {
			return ResultBO.err(MessageCodeConstants.PLAN_CONTENT_IS_NULL_FIELD);
		}
		//投注金额
		if(ObjectUtil.isBlank(orderDetailVO.getAmount())){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_AMOUNT_IS_NULL_FIELD);
		}
		//投注注数
		if(ObjectUtil.isBlank(orderDetailVO.getBuyNumber())){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_IS_NULL_FIELD);
		}
		//投注倍数
		if(ObjectUtil.isBlank(orderDetailVO.getMultiple())){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_MULTIPLE_IS_NULL_FIELD);
		}
		//选号方式
		if(ObjectUtil.isBlank(orderDetailVO.getCodeWay())){
			return ResultBO.err(MessageCodeConstants.BET_CODE_WAY_ILLEGAL);
		}
		//投注方式
		if(ObjectUtil.isBlank(orderDetailVO.getContentType())){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_CONTENT_TYPE_IS_NULL_FIELD);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 彩种开关(子玩法,订单和详情彩种的关联验证)
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @param lotteryBO
	 * @return
	 * @throws Exception 
	 */
	public ResultBO<?> controlLotterChildSwitch(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, LotteryBO lotteryBO){
		if(!ObjectUtil.isBlank(orderDetailVO.getLotteryChildCode()) && !ObjectUtil.isBlank(lotteryBO.getListLotChildBO())){
			//订单和详情彩种的关联验证.(竞彩足球,篮球,北单需要验证小彩种和大彩种的关系,其它彩种验证彩种数据的一致性就可以)
			String childCode = String.valueOf(orderDetailVO.getLotteryChildCode());
			String lotteryChildCode = String.valueOf(orderInfoVO.getLotteryChildCode());
			ResultBO<?> result = null;
			if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || 
					BJDCConstants.checkLotteryId(orderInfoVO.getLotteryChildCode())){
				if(!childCode.equals(lotteryChildCode)){
					return ResultBO.err(MessageCodeConstants.LOTTERY_CODE_CHILD_CODE_ILLEGAL_SERVICE);
				}
			}else{
				if(!ObjectUtil.isBlank(lotteryBO.getListLotChildBO())){//有子玩法
					boolean flag = true;
					for(LotChildBO childBO : lotteryBO.getListLotChildBO()){
						if(childBO.getLotteryChildCode().equals(Integer.valueOf(childCode))){
							flag = false;
							break;
						}
					}
					if(flag){
						return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE);
					}
				}
			}
			result = deal(orderDetailVO.getLotteryChildCode(), lotteryBO);
			if(result.isError()){
				return result;
			}
		}
		
		return ResultBO.ok();
	}
	
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年2月27日 上午10:55:41
	 * @desc 验证方案条目数
	 * @param orderInfoVO
	 * @return
	 */
	public ResultBO<?> verifyTotalDetailsNum(OrderInfoVO orderInfoVO){
		if(orderInfoVO.getOrderDetailList().size() > Constants.NUM_1000){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_LIMIT_SERVICE);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证方案金额是否等于 方案注数 X 方案倍数 X 2
	 * @author longguoyou
	 * @date 2017年3月9日 上午9:43:36
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultBO<?> varifyBoundary(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, Map<String,Object> map){ 
		List<LotBettingMulBO> betMulBOList = (List<LotBettingMulBO>)map.get("betMulBO");
		boolean isIssueEnd = (boolean)map.get("isIssueEnd");
		Date[] dates = (Date[])map.get("orderEndTime");
		Integer betNum   = 0;//方案里面
		Integer multiple = 0;//订单总倍 
		Integer endTime = 0;//提前时间秒
		long deadline = 0;//当前时间与彩期销售截止时间差毫秒
		//(本站销售截止时间毫秒 - 系统当前时间毫秒)/1000 > CMS系统配置截止时间秒数
		if(dates[0].before(new Date())){
			if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || 
					BJDCConstants.checkLotteryId(orderInfoVO.getLotteryChildCode())){
				return ResultBO.err(isIssueEnd?MessageCodeConstants.LOTTERY_ISSUE_IS_ENDING_SALE_SERVICE:MessageCodeConstants.BET_CONTENT_CONTAIN_END_MATCH);
			}else{
				return ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_IS_ENDING_SALE_SERVICE);
			}
		}
		if(!ObjectUtil.isBlank(dates[0]) && !ObjectUtil.isBlank(betMulBOList)){//betMulBOList 已经按endTime desc 排列
			deadline = 	(dates[0].getTime() - new Date().getTime())/1000 ;
			int size = betMulBOList.size();
			if(deadline <= betMulBOList.get(0).getEndTime()){//比配置最大时间小
				for(int i = 0; i < betMulBOList.size(); i++){
					if(deadline <= betMulBOList.get(i).getEndTime()){
						if(i < size-Constants.NUM_1){ 
							betNum = betMulBOList.get(i+1).getBettindNum();
							multiple = betMulBOList.get(i+1).getMultipleNum();
							endTime = betMulBOList.get(i+1).getEndTime();
						}else{//0 < endTime && endTime < 最后一条配置的截止时间，则取最后一条配置 
							betNum = betMulBOList.get(size-1).getBettindNum();
							multiple = betMulBOList.get(size-1).getMultipleNum();
							endTime = betMulBOList.get(size-1).getEndTime();
						}
					}
				}
			}else{//大于配置最大一条，则取第一条配置
				betNum = betMulBOList.get(0).getBettindNum();
				multiple = betMulBOList.get(0).getMultipleNum();
				endTime = betMulBOList.get(0).getEndTime();
			}
		}
		
		//需要提前的时间
		map.put("endTime", endTime);
		
		//单个方案的投注金额验证(传参)  
    	double singleAmount = Constants.getPriceByLotChild(orderDetailVO.getLotteryChildCode(), Constants.getPrice(orderInfoVO.getIsDltAdd()));
		double eachAmount = CalculatorUtil.calculateAmount(orderDetailVO.getBuyNumber(), orderDetailVO.getMultiple(),singleAmount);

		if(MathUtil.compareTo(eachAmount, orderDetailVO.getAmount()) != 0){
			return ResultBO.err(MessageCodeConstants.ORDER_PARAM_CAL_NOT_EQUAL_SERVICE);
		}
		
		//无配置,不作限制,前端可以下单
		if(betNum == 0 && multiple == 0){
			return ResultBO.ok();
		}
		
		//单个方案的投注注数验证
		if(orderDetailVO.getBuyNumber() * orderDetailVO.getMultiple() > betNum){
			return ResultBO.err(MessageCodeConstants.LOTTERY_ORDER_DETAIL_BETNUM_LIMIT_SERVICE);
		}
		
		//竞技彩：订单的投注倍数验证
		//如果业务固定常量倍数比配置的大，则取配置倍数，否则，取业务固定常量
		LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(String.valueOf(orderInfoVO.getLotteryCode()).substring(0,3)));
		OrderInfoBO errorOrderInfoBO = new OrderInfoBO();
		switch (lott) {
			case JJC://竞技彩
			case GYJ://冠亚军
			multiple = multiple > JCConstants.MAX_LIMIT_MULTIPLE ? JCConstants.MAX_LIMIT_MULTIPLE : multiple;
			if(orderInfoVO.getMultipleNum() > multiple){
//    			orderInfoVO.setMultipleNum(multiple);
				errorOrderInfoBO.setMultiple(multiple);
				return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE, errorOrderInfoBO, null);
    		}
    		break;
    	case ZC://老足彩
    	case SZC://数字彩
    	case GPC://高频彩
    	case BJDC://北京单场
    		if(orderInfoVO.getMultipleNum() > multiple){
				errorOrderInfoBO.setMultiple(multiple);
    			return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE, errorOrderInfoBO, null);
    		}
    		break;
    	}
		
		return ResultBO.ok();
	}
	
	/**
	 * 检查参数与彩种之间关系及参数取值范围<br>
	 * 1、isDltAdd 是否大乐透追号<br>
	 * 2、buyType 购买类型<br>
	 * 3、platform 平台类型<br>
	 * 4、categoryId 投注类型<br>
	 * 
	 * @author longguoyou
	 * @date 2017年4月18日
	 * @param orderInfoVO
	 * @return
	 */
	public ResultBO<?> checkRelationsOfParamForLotteryType(OrderInfoVO orderInfoVO) {
		//buyScreen 与竞彩关系，非空
		if((JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode())
		|| JCConstants.gyjCheckLotteryId(orderInfoVO.getLotteryCode())) && ObjectUtil.isBlank(orderInfoVO.getBuyScreen())){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//isDltAdd参数取值范围
		if(orderInfoVO.getIsDltAdd() != Constants.NUM_0 && orderInfoVO.getIsDltAdd() != Constants.NUM_1){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//isDltAdd与彩种之间关系
		if(orderInfoVO.getIsDltAdd() == OrderEnum.DltAdd.YES.getValue() && orderInfoVO.getLotteryCode() != Lottery.DLT.getName()){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//buyType参数取值范围
		if(orderInfoVO.getBuyType() != Constants.NUM_1 && orderInfoVO.getBuyType() != Constants.NUM_2 && orderInfoVO.getBuyType() != Constants.NUM_3){
		    //&& orderInfoVO.getBuyType() != Constants.NUM_4
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//buyType与redeemCode关系
//		if((orderInfoVO.getBuyType() == Constants.NUM_4 && ObjectUtil.isBlank(orderInfoVO.getRedeemCode())) ||
//				(orderInfoVO.getBuyType() != Constants.NUM_4 && !ObjectUtil.isBlank(orderInfoVO.getRedeemCode())) ){
//			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
//		}
        //buyType参数与彩种之间
		LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(String.valueOf(orderInfoVO.getLotteryCode()).substring(0,3)));
    	switch (lott) {
    	case JJC://竞技彩
    		if(orderInfoVO.getBuyType() == OrderEnum.BuyType.BUY_CHASE.getValue()){
    			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
    		}
    		break;
    	default:
    		break;
    	}
    	//平台类型值范围验证
		if(orderInfoVO.getPlatform() != Constants.NUM_1 && orderInfoVO.getPlatform() != Constants.NUM_2 
				&& orderInfoVO.getPlatform() != Constants.NUM_3 && orderInfoVO.getPlatform() != Constants.NUM_4){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		//投注类型值范围验证 ：非空 且 值不在1至6范围内
		if(!(null == orderInfoVO.getCategoryId()) && !(orderInfoVO.getCategoryId() >= Constants.NUM_1 && orderInfoVO.getCategoryId() <= Constants.NUM_6)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 处理对应彩种解析投注注数及单注金额，并验证注数、倍数和金额关系
	 * @author longguoyou
	 * @date 2017年3月14日
	 * @param lotteryCode 彩种ID
	 * @param orderDetailVO 订单详情
	 * @param resultBO 附带投注解析注数
	 * @param flag 是否奖金优化
	 * @return
	 */
	public ResultBO<?> deal(Short addType, Integer lotteryCode, OrderDetailVO orderDetailVO, ResultBO<?> resultBO, boolean flag){
		Integer betNum = 0;
		Integer multiple = 1;
		double singleAmount = 0;
		Object object = resultBO.getData();
		if(!ObjectUtil.isBlank(object)){
			@SuppressWarnings("unchecked")
			Map<String, Object> returnMap = (HashMap<String, Object>) object;
			betNum = (Integer)returnMap.get(Constants.BET_NUM_KEY);//投注解析注数
			multiple = (Integer)returnMap.get(Constants.BET_MULTIPLE_KEY);//投注解析的倍数
			singleAmount = Constants.getPriceByLotChild(orderDetailVO.getLotteryChildCode(), Constants.getPrice(addType));
		}
		if(JCZQConstants.checkLotteryId(lotteryCode) || JCLQConstants.checkLotteryId(lotteryCode) || Lottery.BJDC.getName() == lotteryCode || Lottery.SFGG.getName() == lotteryCode){
			if(!multiple.equals(orderDetailVO.getMultiple())){
				return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_MULTIPLE_NOT_EQUAL_PARAM_MULTIPLE_SERVICE);
			}
		}
		double eachAmount = CalculatorUtil.calculateAmount(betNum, orderDetailVO.getMultiple(), singleAmount);
		//1、比较解析注数
		if(MathUtil.compareTo(orderDetailVO.getBuyNumber(), betNum) != 0){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE);
		}
		//2、比较总金额
		if(MathUtil.compareTo(eachAmount,orderDetailVO.getAmount()) != 0){
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE);
		}
		return ResultBO.ok();
	}
}
