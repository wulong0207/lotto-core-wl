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
public class Direct1Validator extends DirectValidator{

	@Override
	protected int getMinSelect() {
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 十一选五前一复式,2至11个1~11之间的数字,如"01,11"
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue(Pattern.matches("^(0[1-9]|10|11)(,0[1-9]|,10|,11){1,10}$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 重新计算注数，1个号码即1注，号码个数则为注数
		return betNums.length;
	}
}
