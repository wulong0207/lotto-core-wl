package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    前一
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Direct2Validator extends DirectValidator{

	@Override
	protected int getMinSelect() {
		return 2;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 十一选五前二复式,如"01|02,03",号码总个数[3,11]
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue(Pattern.matches("^(0[1-9]|10|11)(,0[1-9]|,10|,11){0,10}\\|(0[1-9]|10|11)(,0[1-9]|,10|,11){0,10}$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		String [] tenThousand = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] thousand = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		Assert.isFalse((ArrayUtil.isRepeat(tenThousand) || ArrayUtil.isRepeat(thousand)), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 万位和千位选号至少3个
		Assert.isTrue((tenThousand.length + thousand.length) >= getMinSelect()+1, MessageCodeConstants.HIGH_BET_NUM_LENGTH, planContent);
		// 重新计算注数
		return tenThousand.length * thousand.length - ArrayUtil.intersect(tenThousand, thousand).length;
	}
}
