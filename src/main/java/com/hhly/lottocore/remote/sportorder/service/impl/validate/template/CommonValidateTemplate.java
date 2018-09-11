package com.hhly.lottocore.remote.sportorder.service.impl.validate.template;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.RegularValidateUtil;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.sportorder.service.ValidateOrderPayTimeService;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.OrderValidateMethod;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 公共验证方法模板
 * @author longguoyou
 * @date 2017年12月2日
 * @compay 益彩网络科技有限公司
 */
public class CommonValidateTemplate extends OrderValidateMethod{
	
	 @Autowired
	 private ValidateOrderPayTimeService orderPayTimeService;
     // 提供相关方法，随意封装 ,如没必要，则直接调用父类OrderValidateMethod中原始方法
	
	/**
	 * 订单信息基础验证
	 * @author longguoyou
	 * @date 2017年12月2日
	 * @param orderInfoVO
	 * @param map
	 * @return
	 */
	public ResultBO<?> validateOrderInfoBase(OrderInfoVO orderInfoVO, Map<String, Object> map){
		//0.对象空判断
	    if(ObjectUtil.isBlank(orderInfoVO)){
	    	return ResultBO.err(MessageCodeConstants.OBJECT_IS_NULL);
	    }
		//1.验证必填字段(验空值和填充默认值)
		ResultBO<?> resultBO = super.verifyOrderRequired(orderInfoVO);
		if (resultBO.isError()) {
			return resultBO;
		}
		//检查参数正确性
		ResultBO<?> result = super.checkRelationsOfParamForLotteryType(orderInfoVO);
		if(result.isError()){
			return result;
		}
		//2.彩种开关(大彩种)
		resultBO = super.controlLotterSwitch(orderInfoVO,map);
		if (resultBO.isError()) {
			return resultBO;
		}
		//3.验证期号信息 (期号开关;开售时间;销售截止时间,竞彩足球,篮球,北单不用验证开售时间和销售截止时间,详情验证时在验) 
		resultBO = super.verifyIssueInfo(orderInfoVO, (IssueBO)map.get("issueBO"));
		if (resultBO.isError()) {
			return resultBO;
		}
		//4.验证用户基础信息 (是否锁定,黑白名单,类别控制)
		resultBO = super.verifyUserInfo((UserInfoBO)map.get("userInfoBO"));
		if (resultBO.isError()) {
			return resultBO;
		}
		//5.方案详情条目数验证(如:最大不超过1000,具体数值确认需求) 单式上传条目数没有限制
		if(Constants.NUM_0 == Integer.valueOf(orderInfoVO.getIsSingleOrder())) {
			resultBO = super.verifyTotalDetailsNum(orderInfoVO);
			if (resultBO.isError()) {
				return resultBO;
			}
		}
		//6、验证预计奖金值，合法性判断
		if(!ObjectUtil.isBlank(orderInfoVO.getMaxBonus())){
			String bonus[] = orderInfoVO.getMaxBonus().split(SymbolConstants.TRAVERSE_SLASH);
			if (bonus.length == 2) {
				for(String val : bonus){
					if(!val.matches(RegularValidateUtil.REGULAR_POINT_NUM)){
						return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
					}
				}
			}else{
				//考虑到IOS、android版本更新不及时,暂时允许一个数字通过验证入库
				if(!orderInfoVO.getMaxBonus().matches(RegularValidateUtil.REGULAR_POINT_NUM)){
					return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 订单详情验证
	 * @author longguoyou
	 * @date 2017年12月2日
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @param map
	 * @return
	 */
	public ResultBO<?> validateOrderDetailInfoBase(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, Map<String, Object> map){
	    
		//0.对象空判断
		if(ObjectUtil.isBlank(orderDetailVO)){
			return ResultBO.err(MessageCodeConstants.OBJECT_IS_NULL);
		}
		//1.验证必填字段(验空值和填充默认值)
		ResultBO<?> resultBO = super.verifyOrderDetailRequired(orderDetailVO, (LotteryBO)map.get("lotteryBO"));
		if (resultBO.isError()) {
			return resultBO;
		}
		//2.彩种开关(子玩法,订单和详情彩种的关联验证)
		resultBO = super.controlLotterChildSwitch(orderInfoVO, orderDetailVO, (LotteryBO)map.get("lotteryBO"));
		if (resultBO.isError()) {
			return resultBO;
		}
	
		//3.详情倍数,注数,金额验证(值范围); 
		resultBO = super.varifyBoundary(orderInfoVO, orderDetailVO, map);
		if(resultBO.isError()){
			return resultBO;
		}
		
		return ResultBO.ok();
	}
	
	
	/**
	 * 订单金额和订单详情总金额验证是否一致
	 * @author longguoyou
	 * @date 2017年12月5日
	 * @param orderInfoVO
	 * @return
	 */
	public ResultBO<?> validateOrderInfoAmountAndDetailInfoAmount(OrderInfoVO orderInfoVO, Double total ,int betNum){
	    int flagEquals = MathUtil.compareTo(orderInfoVO.getOrderAmount(), MathUtil.mul(total, new Double(orderInfoVO.getMultipleNum())));
	    if(flagEquals != 0){
		    return ResultBO.err(MessageCodeConstants.ORDER_AMOUNT_NOT_EQUAL_ORDER_DETAIL_AMOUNT);
	    }
	    //总注数设值
	    orderInfoVO.setBuyNumber(betNum);
	    return ResultBO.ok();
	}
	/**
	 * 检查支付截止时间
	 * @author longguoyou
	 * @date 2017年12月5日
	 * @return
	 * @throws Exception 
	 */
	public ResultBO<?> validateEndSysTime(OrderInfoVO orderInfoVO, Map<String, Object> map) throws Exception{
		return orderPayTimeService.checkFbAndBbEndTime(orderInfoVO, (List<LotBettingMulBO>)map.get("betMulBO"), (LotteryBO)map.get("lotteryBO"), (Integer)map.get("endTime"), (SportAgainstInfoBO)map.get("againstInfoBO"));
	}
	
}
