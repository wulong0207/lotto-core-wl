package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.highutil.HighUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

@Component
public class Dkl10OrderDetailValidate extends Kl10OrderDetailValidate {
	
	@Override
	public void validateLotteryChildCode(OrderDetailVO orderDetailVO) {
		Integer lotteryChildCode = orderDetailVO.getLotteryChildCode();
		Assert.isTrue(HighUtil.isDkl10(lotteryChildCode), MessageCodeConstants.LOTTERY_CHILD_CODE_IS_NULL_FIELD,lotteryChildCode);
	}
}
