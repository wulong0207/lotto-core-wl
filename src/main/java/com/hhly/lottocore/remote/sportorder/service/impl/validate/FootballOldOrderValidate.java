/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.OldFootballConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * 
 * @author longguoyou
 * @date 2017年3月3日 上午11:08:55
 * @desc 老足彩：14场胜平负/任九/四场进球彩/六场半全场订单验证 
 */
@Component("footballOldOrderValidate")
public class FootballOldOrderValidate extends SportsOrderValidate implements Validator{
	
	
	public ResultBO<?> handle(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		return this.verifyChildBetContent(orderDetailVO, orderInfoVO, list);
	}

	/**
	 * 
	 * 老足彩：14场胜平负/任九/四场进球彩/六场半全场 验证处理中心：投注内容
	 * @author longguoyou
	 * @date 2017年2月7日 下午2:55:09
	 * @param orderDetailVO 订单详情
	 * @param orderInfoVO 订单
	 * @param list 限号列表
	 * @return
	 *
	 */
	private ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		ResultBO<?> result = null;
		if(ObjectUtil.isBlank(orderDetailVO)){
			return ResultBO.err(MessageCodeConstants.OBJECT_IS_NULL);
		}
		//1、投注内容验证
		String content = orderDetailVO.getPlanContent();
		String[] splitStr = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		if(com.hhly.skeleton.base.constants.Constants.NUM_0 == Integer.valueOf(orderInfoVO.getIsSingleOrder())) {
			if (orderInfoVO.getLotteryCode() == Lottery.SFC.getName()) {//十四场
				if (splitStr.length != 14) {
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				//投注内容包含胆，不合法
				if(content.contains(SymbolConstants.NUMBER_SIGN)){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				//投注内容最后一位不能为“|”
				if(content.substring(content.length()-Constants.NUM_1).equals(SymbolConstants.VERTICAL_BAR)){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				//1、单式投注
				if (orderDetailVO.getContentType() == BetContentType.SINGLE.getValue()) {
					result = verifySingle(splitStr, OldFootballConstants.ID_FOURTEEN, content.contains(SymbolConstants.COMMA));
					if (result.isError()) {
						return result;
					}
				}
				//2、复式投注
				if (orderDetailVO.getContentType() == BetContentType.MULTIPLE.getValue()) {
					result = verifyMultiple(splitStr, OldFootballConstants.ID_FOURTEEN, content.contains(SymbolConstants.COMMA));
					if (result.isError()) {
						return result;
					}
				}
			} else if (orderInfoVO.getLotteryCode() == Lottery.ZC6.getName() || orderInfoVO.getLotteryCode() == Lottery.JQ4.getName()) {//足彩:六场半全场/四场进球彩
				if (orderInfoVO.getLotteryCode() == Lottery.JQ4.getName() && splitStr.length != Constants.NUM_8) {
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				if (orderInfoVO.getLotteryCode() == Lottery.ZC6.getName() && splitStr.length != 12) {
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				result = dealHalfSixAndFourGoal(orderDetailVO.getContentType(), splitStr, orderInfoVO.getLotteryCode());
				if (result.isError()) {
					return result;
				}
			} else if (orderInfoVO.getLotteryCode() == Lottery.ZC_NINE.getName()) {//九场胜负彩
				//任九胆拖
				result = verifyEachNineDanTuo(orderDetailVO.getContentType(), splitStr, OldFootballConstants.ID_FOURTEEN, content, orderDetailVO.getLotteryChildCode());
				if (result.isError()) {
					return result;
				}
			}
		}
		//2、限号、解析投注注数
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
		return this.verifyBetContent(betContent, orderDetailVO, orderInfoVO.getLotteryCode(), list,orderInfoVO.getIsSingleOrder());
	}

	/**
	 * 验证四场进球彩/六场半全场投注内容合法性
	 * @author longguoyou
	 * @date 2017年4月17日 
	 * @param contentType 是否复式
	 * @param splitStr 以竖线|截取后的数组
	 * @param lotteryId  老足彩大彩种ID
	 * @return
	 */
	private ResultBO<?> dealHalfSixAndFourGoal(int contentType, String[] splitStr, int lotteryId){
		if(contentType == BetContentType.MULTIPLE.getValue()){//复式
			return dealMultiple(splitStr, lotteryId);
		}else if(contentType == BetContentType.SINGLE.getValue()){//单式
			return dealSingle(splitStr, lotteryId);
		}
		return ResultBO.ok();
	}
	/**
	 * 处理单式验证
	 * @author longguoyou
	 * @date 2017年4月19日
	 * @param splitStr
	 * @param lotteryId
	 * @return
	 */
	private ResultBO<?> dealSingle(String[] splitStr, int lotteryId){
		for(int i = 0; i < splitStr.length; i++){
			if(splitStr[i].contains(SymbolConstants.COMMA)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			if(!OldFootballConstants.checkBetContentGame(lotteryId, splitStr[i])){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	/**
	 * 处理复式验证
	 * @author longguoyou
	 * @date 2017年4月19日
	 * @param splitStr
	 * @param lotteryId
	 * @return
	 */
	private ResultBO<?> dealMultiple(String[] splitStr, int lotteryId){
		boolean flag = false;
		for(int i = 0; i < splitStr.length; i++){
			if(splitStr[i].contains(SymbolConstants.COMMA)){
				flag = true;
			}
			if(!OldFootballConstants.checkBetContentGame(lotteryId, splitStr[i])){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		if(!flag){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}

	/**
	 * 验证单式投注内容合法性
	 * e.g. 单式：3|1|0|0|0|1|1|3|3|3|0|1|0|3
	 * @author longguoyou
	 * @date 2017年3月6日 下午5:03:15
	 * @param splitStr 以竖线|截取后的数组
	 * @param lotteryId 老足彩子玩法
	 * @param has 是否包含 逗号
	 * @return
	 */
	private ResultBO<?> verifySingle(String[] splitStr, int lotteryId, boolean has){
		if(has){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		for(String betContent : splitStr){
			if(!OldFootballConstants.checkBetContentGame(lotteryId, betContent)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证复式投注内容合法性
	 * e.g. 复式：3,1|1,0|3,0|_|_|3,1|3,1|3|3|3|3,0|1|3,0|3
	 * @author longguoyou
	 * @date 2017年3月6日 下午5:04:01
	 * @param splitStr 以竖线|截取后的数组
	 * @param lotteryId 老足彩子玩法
	 * @param has 是否包含逗号
	 * @return
	 */
	private ResultBO<?> verifyMultiple(String[] splitStr, int lotteryId, boolean has){
		ResultBO<?> result = null;
		if(!has){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		for(String betContent : splitStr){
			result = deal(betContent, lotteryId);
			if(result.isError()){
				return result;
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 任九胆拖投注内容合法性
	 * e.g. 任九胆拖：3,1#1,0|3,0|_|_|3,1|3,1|3,1#3|3|3,0|1|3,0|3 
	 * @author longguoyou
	 * @date 2017年3月6日 下午5:05:27
	 * @param contentType 单/复 式
	 * @param splitStr 以竖线|截取后的数组
	 * @param lotteryId 彩种编号
	 * @param content 原始投注内容
	 * @return
	 */
	private ResultBO<?> verifyEachNineDanTuo(Integer contentType, String[] splitStr, int lotteryId, String content, Integer lotteryChildCode){
		//最后一位为“|”，投注内容不合法
		if(content.substring(content.length()-Constants.NUM_1).equals(SymbolConstants.VERTICAL_BAR)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//根据子玩法判断投注内容合法性
		if(LotteryChild.ID_NINE_BET.getValue() == lotteryChildCode){//普通投注包含胆，不合法
			if(content.contains(SymbolConstants.NUMBER_SIGN)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		if(LotteryChild.ID_NINE_BANKERS_BET.getValue() == lotteryChildCode){//胆拖投注不包含胆，不合法
			if(!content.contains(SymbolConstants.NUMBER_SIGN)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		//根据contentType 参数判断投注内容合法性
		if(contentType == BetContentType.DANTUO.getValue()){//胆拖投注，不包含胆，不合法
			if(!content.contains(SymbolConstants.NUMBER_SIGN)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		int count = 0;//有效投注场数，不包含占位符
		String[]  csSplitStr = null;
		if(content.contains(SymbolConstants.NUMBER_SIGN)){//胆拖
			//1、胆码不能超8个
			String[] danSplitStr = content.split(SymbolConstants.NUMBER_SIGN);
			if(danSplitStr.length > Constants.NUM_9){//数组长度最大为9
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			//2、场数数组
		    content = content.replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
		    csSplitStr = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		}else{//普通
		    csSplitStr = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		}
		//场数小于9，大于14场
		if(csSplitStr.length < Constants.NUM_9 || csSplitStr.length > 14){//格式合法性，连占位符也算
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
	    for(int i = 0; i < csSplitStr.length; i++){
	    	if(!csSplitStr[i].equals(SymbolConstants.UNDERLINE) && !csSplitStr[i].equals(SymbolConstants.STAR)){
	    		count++;
	    	}
	    }
	    if(count < Constants.NUM_9 || count > 14){
	    	return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
	    }
		//判断参数与内容对应关系合法性
		if(contentType == BetContentType.MULTIPLE.getValue()){//复式
			if(!content.contains(SymbolConstants.COMMA) && count == Constants.NUM_9){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(contentType == BetContentType.SINGLE.getValue()){//单式
			if(content.contains(SymbolConstants.COMMA) || count > Constants.NUM_9){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		ResultBO<?> result = null;
		for(String betContent : splitStr){
			if(betContent.contains(SymbolConstants.NUMBER_SIGN)){
				for(String bet: betContent.split(SymbolConstants.NUMBER_SIGN)){
					result = deal(bet, lotteryId);
					if(result.isError()){
						return result;
					}
				}
			}else{
				result = deal(betContent, lotteryId);
				if(result.isError()){
					return result;
				}
			}
		}
		return ResultBO.ok();
	}
	/**
	 * 处理判断
	 * @author longguoyou
	 * @date 2017年3月6日 下午7:03:11
	 * @param betContent 投注选项
	 * @param lotteryId 彩种编号
	 * @return
	 */
	private ResultBO<?> deal(String betContent, int lotteryId){
		if(betContent.split(SymbolConstants.COMMA).length > 1){
			for(String bet: betContent.split(SymbolConstants.COMMA)){
				if(!OldFootballConstants.checkBetContentGame(lotteryId, bet)){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}else{
			if(!betContent.equals(SymbolConstants.UNDERLINE) && !betContent.equals(SymbolConstants.STAR) && 
					!OldFootballConstants.checkBetContentGame(lotteryId, betContent)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
        return ResultBO.ok();
	}
}
