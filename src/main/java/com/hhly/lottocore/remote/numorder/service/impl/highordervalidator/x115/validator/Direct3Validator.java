package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    前三
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Direct3Validator extends DirectValidator{

	@Override
	protected int getMinSelect() {
		return 3;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 十一选五前三复式,如"01,02,03|02|01,02,03",号码总个数[4,11]
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue(Pattern.matches("^(0[1-9]|10|11)(,0[1-9]|,10|,11){0,10}\\|(0[1-9]|10|11)(,0[1-9]|,10|,11){0,10}\\|(0[1-9]|10|11)(,0[1-9]|,10|,11){0,10}$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		String [] tenThousand = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] thousand = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		String [] hundred = StringUtils.tokenizeToStringArray(planContents[2], SymbolConstants.COMMA);
		Assert.isFalse((ArrayUtil.isRepeat(tenThousand) || ArrayUtil.isRepeat(thousand) || ArrayUtil.isRepeat(hundred)), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 万位和千位选号至少4个
		Assert.isTrue((tenThousand.length + thousand.length + hundred.length) >= getMinSelect()+1, MessageCodeConstants.HIGH_BET_NUM_LENGTH, planContent);
		// 重新计算注数
		Map<String, Integer> occurrence = ArrayUtil.occurrence(tenThousand, thousand, hundred);
		int duplication = 0, n1 = tenThousand.length, n2 = thousand.length, n3 = hundred.length;
		String q3Key;
		int val;
		for( Map.Entry<String, Integer> entry : occurrence.entrySet() ) {
			q3Key = entry.getKey();
			val = entry.getValue();
			// 重复出现2次,注数重复数量为没有出现此选码的对应选号集合的长度
			if(val==2)
				duplication += !Arrays.asList(tenThousand).contains(q3Key) ? n1 :
											 !Arrays.asList(thousand).contains(q3Key) ? n2 : n3;
			// 重复出现3次,注数重复数量为万、千、百位两两重复2次的数量+重复3次的数量(即为1)
			else if(val==3)
				duplication += (n1 + n2 + n3 - 3) + 1;
		}
		return n1 * n2 * n3 - duplication;
	}
}
