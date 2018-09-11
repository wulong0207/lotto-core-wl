/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

/**
 * 
 * @author longguoyou
 * @date 2017年3月3日 上午11:08:55
 * @desc 竞技篮球订单验证 
 */
@Component("basketballOrderValidate")
public class BasketballOrderValidate extends BasketballOrderBaseValidate implements Validator{
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年2月7日 下午2:55:09
	 * @desc 竞技篮球验证处理中心：投注内容、过关方式
	 * @param orderInfoVO
	 * @return
	 */
	public ResultBO<?> handle(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		return handleChildBasketball(orderDetailVO, orderInfoVO, list);
	}
	
	/**
	 * 获取对应彩种的插件
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @param list
	 * @return
	 */
	protected ResultBO<?> handleChildLotteryContent(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		return handleChildBasketball(orderDetailVO,orderInfoVO,list); 
	}
	
	/**
	 * 竞彩篮球订单详情验证处理
	 * @author longguoyou
	 * @date 2017年3月20日
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @param list
	 * @return
	 */
	protected ResultBO<?> handleChildBasketball(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
		if(Constants.NUM_0 == Integer.valueOf(orderInfoVO.getIsSingleOrder())) {
			//1、验证过关方式相关：包括，格式合法性、内容合法性、最高串关
			ResultBO<?> resultBO = this.varifyPasswayRelated(orderDetailVO, orderInfoVO);
			if (resultBO.isError()) {
				return resultBO;
			}
			if(ObjectUtil.isBlank(betContent) || betContent.length < 2){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			//2、验证单/复式与投注内容合法性
			resultBO = this.verifyChildBetContent(orderDetailVO, betContent, orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
			//3、验证投注内容合法性
			resultBO = this.varifyContentRelated(betContent, orderDetailVO, orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
			//4、缓存取数据
			List<SportAgainstInfoBO> listAgainstInfoBO  = getSportAgainstInfoBOs(Arrays.asList(orderDetailVO), Integer.valueOf(String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3)));
			//场次(对阵)开关，对阵里面的子玩法
			resultBO = super.verifyBBAgainstInfo(orderInfoVO, orderDetailVO, listAgainstInfoBO, betContent);
			if(resultBO.isError()){
				return resultBO;
			}
			//5、验证对阵编号合法性 //此处是验证外层的赛事编号和明细的是否一一匹配。单式上传可能会出现匹配不一致的情况，所以忽悠
			resultBO = super.verifySystemCodes(orderInfoVO, orderDetailVO);
			if (resultBO.isError()) {
				return resultBO;
			}
		}
		//竞技彩：每个方案倍数、注数、金额校验(同投注内容对比.大乐透追号价钱为3元), 限号
		return this.verifyBetContent(betContent, orderDetailVO, orderDetailVO.getLotteryChildCode(), list,orderInfoVO.getIsSingleOrder());
	}
	
	/**
	 * 对应子玩法和混合过关方式判断处理
	 */
	public ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, String[] betContent, OrderInfoVO orderInfoVO) {
		ResultBO<?> result = isPass(orderDetailVO, betContent);
		if(result.isError()){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//验证contentType与投注内容关系合法性
		result = super.varifyContentType(betContent, orderDetailVO, orderInfoVO);
		if(result.isError()){
			return result;
		}
		return ResultBO.ok();
	}

	public ResultBO<?> isPass(OrderDetailVO orderDetailVO, String[] betContent){
		switch (orderDetailVO.getLotteryChildCode()) { 
		case JCLQConstants.ID_JCLQ_SF://篮球胜负
			return deal(orderDetailVO,JCLQConstants.ID_JCLQ_SF);
		case JCLQConstants.ID_JCLQ_RF://篮球让胜平负
			return deal(orderDetailVO,JCLQConstants.ID_JCLQ_RF);
		case JCLQConstants.ID_JCLQ_DXF://篮球大小分
			return deal(orderDetailVO,JCLQConstants.ID_JCLQ_DXF);
		case JCLQConstants.ID_JCLQ_SFC://篮球胜分差
			return deal(orderDetailVO,JCLQConstants.ID_JCLQ_SFC);
		case JCLQConstants.ID_JCLQ_HHGG://篮球混合过关
			return super.varifyPasswayPermit(orderDetailVO, betContent);
		default:
			return null;
		}
	}
	/**
	 * 单一玩法处理验证
	 * @author longguoyou
	 * @date 2017年3月20日
	 * @param orderDetailVO
	 * @param lotteryCode
	 * @return
	 */
	public ResultBO<?> deal(OrderDetailVO orderDetailVO, Integer lotteryCode){
		ResultBO<?> resultBO =  super.validateGameFormate(orderDetailVO, lotteryCode);
		if (resultBO.isError()) {
			return resultBO;
		}
		resultBO =  super.validateGameContent(orderDetailVO);
		if (resultBO.isError()) {
			return resultBO;
		}
		return ResultBO.ok();
	}
}
