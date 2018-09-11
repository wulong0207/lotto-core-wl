package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.highutil.HighUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    江西十一选五
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Jx11x5OrderDetailValidate extends X115OrderDetailValidate {
	
	@Override
	public void validateLotteryChildCode(OrderDetailVO orderDetailVO) {
		Integer lotteryChildCode = orderDetailVO.getLotteryChildCode();
		Assert.isTrue(HighUtil.isJX11x5(lotteryChildCode), MessageCodeConstants.LOTTERY_CHILD_CODE_IS_NULL_FIELD,lotteryChildCode);
	}
}
