package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.validator;

import java.util.Arrays;
import java.util.Map;

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
public class Kl10D3Validator extends Kl10DirectValidator{

	@Override
	protected int getMinSelect() {
		return 3;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 复式：03,04|14
		String planContent = orderDetail.getPlanContent();
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		// 包含3个位置的选号
		Assert.isTrue(planContents.length == 3, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] first = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] second = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		String [] third = StringUtils.tokenizeToStringArray(planContents[2], SymbolConstants.COMMA);
		// 选号个数
		Assert.isTrue((first.length > 0 && first.length <= getNumRange().length) && (second.length > 0 && second.length <= getNumRange().length) && (third.length > 0 && third.length <= getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号范围
		Assert.isTrue((ArrayUtil.containsAll(getNumRange(), first) && ArrayUtil.containsAll(getNumRange(), second) && ArrayUtil.containsAll(getNumRange(), third)), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重复性
		Assert.isFalse((ArrayUtil.isRepeat(first) || ArrayUtil.isRepeat(second) || ArrayUtil.isRepeat(third)), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 各位置选号至少4个
		Assert.isTrue((first.length + second.length+third.length) > getMinSelect(), MessageCodeConstants.HIGH_BET_NUM_LENGTH, planContent);
		// 重新计算注数
		Map<String, Integer> occurrence = ArrayUtil.occurrence(first, second, third);
		int duplication = 0, n1 = first.length, n2 = second.length, n3 = third.length;
		String q3Key;
		int val;
		for( Map.Entry<String, Integer> entry : occurrence.entrySet() ) {
			q3Key = entry.getKey();
			val = entry.getValue();
			// 重复出现2次,注数重复数量为没有出现此选码的对应选号集合的长度
			if(val==2)
				duplication += !Arrays.asList(first).contains(q3Key) ? n1 :
											 !Arrays.asList(second).contains(q3Key) ? n2 : n3;
			// 重复出现3次,注数重复数量为万、千、百位两两重复2次的数量+重复3次的数量(即为1)
			else if(val==3)
				duplication += (n1 + n2 + n3 - 3) + 1;
		}
		return n1 * n2 * n3 - duplication;
	}
}
