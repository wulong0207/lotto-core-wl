/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.Arrays;
import java.util.List;

import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import org.springframework.stereotype.Component;

/**
 * 
 * @author longguoyou
 * @date 2017年3月3日 上午11:08:55
 * @desc 竞技足球订单验证 
 */
@Component("footballOrderValidate")
public class FootballOrderValidate extends FootballOrderBaseValidate implements Validator{
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年2月7日 下午2:55:09
	 * @desc 竞彩足球验证处理中心：投注内容、过关方式
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @param list
	 * @return
	 */
	public ResultBO<?> handle(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		return handleChildFooball(orderDetailVO, orderInfoVO, list);
	}
	
	/**
	 * 获取对应彩种的插件
	 * @param orderDetailVO 订单
	 * @param orderInfoVO   订单详情
	 * @param list 限号
	 * @return
	 */
	private ResultBO<?> handleChildFooball(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
		if(Constants.NUM_0 == Integer.valueOf(orderInfoVO.getIsSingleOrder())) {
			//1、验证过关方式相关：包括，格式合法性、内容合法性、最高串关
			ResultBO<?> resultBO = super.varifyPasswayRelated(betContent, orderDetailVO, orderInfoVO);
			if (resultBO.isError()) {
				return resultBO;
			}
			//2、验证投注内容合法性
			resultBO = super.varifyContentRelated(betContent, orderDetailVO, orderInfoVO);
			if (resultBO.isError()) {
				return resultBO;
			}
			//3、验证单一玩法最高串关数
			resultBO = this.verifyChildBetContent(orderDetailVO, betContent, orderInfoVO);
			if (resultBO.isError()) {
				return resultBO;
			}
			//缓存取数据
			List<SportAgainstInfoBO> listAgainstInfoBO = super.getSportAgainstInfoBOs(Arrays.asList(orderDetailVO), Integer.valueOf(String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3)));
			//4、场次(对阵)开关，对阵里面的子玩法
			resultBO = super.verifyAgainstInfo(orderInfoVO, orderDetailVO, listAgainstInfoBO, betContent);
			if (resultBO.isError()) {
				return resultBO;
			}
			//5、验证对阵编号合法性 //此处是验证外层的赛事编号和明细的是否一一匹配。单式上传可能会出现匹配不一致的情况，所以忽悠
			resultBO = super.verifySystemCodes(orderInfoVO, orderDetailVO);
			if (resultBO.isError()) {
				return resultBO;
			}
		}
		//6、竞技彩：每个方案倍数、注数、金额校验(同投注内容对比.大乐透追号价钱为3元), 限号
		return super.verifyBetContent(betContent, orderDetailVO, orderInfoVO.getLotteryCode(), list,orderInfoVO.getIsSingleOrder());
	}
	
	/**
	 * 验证单一玩法最高串关数
	 * @author longguoyou
	 * @date 2017年3月13日
	 * @param orderDetailVO
	 * @return
	 */
	public ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, String[] betContent, OrderInfoVO orderInfoVO) {
		String content = null;
		ResultBO<?> result = null;
		if(!ObjectUtil.isBlank(betContent) && betContent.length >= Constants.NUM_2){
			content = betContent[1];//获得过关方式[2_1,3_1,4_1]
		}
		if(ObjectUtil.isBlank(content)){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		//验证contentType与投注内容关系合法性
		result = super.varifyContentType(betContent, orderDetailVO,orderInfoVO);
		if(result.isError()){
			return result;
		}
		if(!ObjectUtil.isBlank(content)){
			for(String passway : content.split(SymbolConstants.COMMA)){
				 if(JCZQConstants.verifyLimitPassway(passway,orderDetailVO.getLotteryChildCode())){
					 return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
				 }
			}
		} 
		return ResultBO.ok();
	}
}
