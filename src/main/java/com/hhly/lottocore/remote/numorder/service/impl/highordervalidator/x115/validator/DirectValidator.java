package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class DirectValidator extends X115ChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		 // a)单式：1|2|3
		String planContent = orderDetail.getPlanContent();
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		Assert.isTrue(betNums.length == getMinSelect(), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}
}
