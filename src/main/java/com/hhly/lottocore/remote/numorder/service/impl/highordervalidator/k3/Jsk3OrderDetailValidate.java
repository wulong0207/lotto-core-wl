package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.highutil.HighUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

@Component
public class Jsk3OrderDetailValidate extends K3OrderDetailValidate {

	@Override
	public void validateLotteryChildCode(OrderDetailVO orderDetailVO) {
		Integer lotteryChildCode = orderDetailVO.getLotteryChildCode();
		Assert.isTrue(HighUtil.isJsk3(lotteryChildCode), MessageCodeConstants.LOTTERY_CHILD_CODE_IS_NULL_FIELD,
				lotteryChildCode);
	}

	private static final LimitTranslator K3_LIMIT_TRANSLATOR = new LimitTranslator() {
		@Override
		public String translate(String originalLimitContent, Integer lotteryChildCode) {
			String translated;
			if (Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.JSK3_L3.getValue())) {
				translated = originalLimitContent.replaceAll("3L", "三连号通选");
			} else if (Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.JSK3_TT3.getValue())) {
				translated = originalLimitContent.replaceAll("3T", "三同号通选");
			} else {
				translated = originalLimitContent;
			}
			return translated;
		}

		@Override
		public boolean whetherTraslate(Integer lotteryChildCode) {
			// 快三只有三同号通选及三连号通选需要转化
			// 新加快三彩种要增加相应的子玩法判断
			return (Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.JSK3_L3.getValue())
					|| Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.JSK3_TT3.getValue()));
		}
	};

	@Override
	public LimitTranslator getLimitTranslator() {
		return K3_LIMIT_TRANSLATOR;
	}

}
