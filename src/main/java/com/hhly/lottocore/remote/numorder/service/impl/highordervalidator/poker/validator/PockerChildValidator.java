package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.validator;

import org.springframework.util.StringUtils;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.AbstractChildValidator;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class PockerChildValidator extends AbstractChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		// 1T 2T AT
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue(ArrayUtil.contains(getNumRange(), planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 1T,2T
		String planContent = orderDetail.getPlanContent();
		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 选号个数, 扑克的所有子玩法的复式，都是2个或以上选号
		Assert.isTrue(betNums.length >= 2 && betNums.length <= getNumRange().length, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重新计算注数
		return betNums.length;
	}
}
