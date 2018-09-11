package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.RnValidator;
import com.hhly.skeleton.base.constants.HighConstants;

public abstract class X115RnValidator extends RnValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.X115_NUM_RANGE;
	}

//	@Override
//	protected int validateSingle(OrderDetailVO orderDetail) {
//		 // 十一选五任二单式,两个1~11之间的数字,如"01,11"
//		String planContent = orderDetail.getPlanContent();
//		//Assert.isTrue(Pattern.matches("^(0[1-9]|10|11),(0[1-9]|10|11)$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
//		Assert.isTrue(betNums.length == getMinSelect(), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 若单式通过格式验证，则注数一定为1注
//		return 1;
//	}
//
//	@Override
//	protected int validateMulti(OrderDetailVO orderDetail) {
//		// 十一选五任二复式,3至11个1~11之间的数字,如"01,02,11"
//		String planContent = orderDetail.getPlanContent();
//		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
//		Assert.isTrue(betNums.length > getMinSelect() && betNums.length <= getNumRange().length, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 重新计算注数
//		return (int)NumberUtil.getCombinationCount(betNums.length, getMinSelect());
//	}
//
//	@Override
//	protected int validateDantuo(OrderDetailVO orderDetail) {
//		String planContent = orderDetail.getPlanContent();
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
//		// 胆拖包含胆码和拖码两部分
//		Assert.isTrue(planContents.length==2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 验证胆码和拖码的个数不超过11个(正则已验证)，且各自不重复及不相互重复
//		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.NUMBER_SIGN);
//		// (deprecated)胆拖必须2元以上,所以getMinSelect+1
//		// 需求变为页面可以下一注的胆拖，入订单时也是胆拖模式，但拆票时会折成单式票
//		// 胆码加拖码个数[getMinSelect(),11]个
//		Assert.isTrue((betNums.length >= getMinSelect() && betNums.length <= getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 验证胆码加拖码的个数及重复性
//		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		String [] danma = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
//		String [] tuoma = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
//		// 胆码[1,getMinSelect())个
//		Assert.isTrue((danma.length >= 1 && danma.length < getMinSelect()), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 拖码[1,11)个
//		Assert.isTrue((tuoma.length >= 1 && tuoma.length < getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		return (int)NumberUtil.getCombinationCount(tuoma.length, getMinSelect() - danma.length);
//	}
}
