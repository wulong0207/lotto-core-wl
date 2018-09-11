package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator;

import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class RnValidator extends AbstractChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		/*
		 * 十一选五任:二单式,两个1~11之间的数字,如"01,11"
		 * 快3二不同号：1,2
		 * 快3三不同号：1,2,3
		 * 快乐扑克：任选N的格式，以任三为例 单式：A,2,3
		 * 时时彩：1,2,3(组六)  0,1,1(组三) 0,1(组二)
		 */
		String planContent = orderDetail.getPlanContent();
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 选号个数
		Assert.isTrue(betNums.length == getMinSelect(), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		/*
		 * 十一选五：任二复式,3至11个1~11之间的数字,如"01,02,11"
		 * 快3二不同号：1,2,3
		 * 快3三不同号：1,2,3,4
		 * 快乐扑克任选N的格式，以任三为例 复式：A,2,3,4,5,6,7
		 * 时时彩：复式：2,3,4,5,6
		 */
		String planContent = orderDetail.getPlanContent();
		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 选号个数
		Assert.isTrue(betNums.length > getMinSelect() && betNums.length <= getNumRange().length, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重新计算注数
		return (int)NumberUtil.getCombinationCount(betNums.length, getMinSelect());
	}

	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		/*
		 * 十一选五：1,2#4,5,6,7,8,9
		 * 快3二不同号：1#2,3,4
		 * 快3三不同号：1,2#3,4
		 * 快乐扑克（任三）：A,2#4,5,6,7,8,9
		 * 时时彩：1,2#3,4,5,6
		 */
		String planContent = orderDetail.getPlanContent();
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
		// 胆拖包含胆码和拖码两部分
		Assert.isTrue(planContents.length==2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.NUMBER_SIGN);
		// 胆码加拖码个数[getMinSelect(),getNumRange().length]个
		Assert.isTrue((betNums.length >= getMinSelect() && betNums.length <= getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 验证胆码加拖码的个数
		String [] danma = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] tuoma = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		// 胆码[1,getMinSelect())个
		Assert.isTrue((danma.length >= 1 && danma.length < getMinSelect()), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 拖码[1,13)个
		Assert.isTrue((tuoma.length >= 1 && tuoma.length < getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		return (int)NumberUtil.getCombinationCount(tuoma.length, getMinSelect() - danma.length);
	}
	
//	@Override
//	protected int validateSum(OrderDetailVO orderDetail) {
//		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
//	}
}
