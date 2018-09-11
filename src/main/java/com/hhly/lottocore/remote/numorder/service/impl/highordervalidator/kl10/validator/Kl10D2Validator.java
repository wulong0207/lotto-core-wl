package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    选二连直
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Kl10D2Validator extends Kl10DirectValidator{

	@Override
	protected int getMinSelect() {
		return 2;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 复式：03,04|14
		String planContent = orderDetail.getPlanContent();
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		// 包含两个位置的选号
		Assert.isTrue(planContents.length == 2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] first = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] second = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		// 选号个数
		Assert.isTrue((first.length > 0 && first.length <= getNumRange().length) && (second.length > 0 && second.length <= getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号范围
		Assert.isTrue((ArrayUtil.containsAll(getNumRange(), first) && ArrayUtil.containsAll(getNumRange(), second)), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重复性
		Assert.isFalse((ArrayUtil.isRepeat(first) || ArrayUtil.isRepeat(second)), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 万位和千位选号至少3个
		Assert.isTrue((first.length + second.length) > getMinSelect(), MessageCodeConstants.HIGH_BET_NUM_LENGTH, planContent);
		// 重新计算注数
		return first.length * second.length - ArrayUtil.intersect(first, second).length;
	}
}
