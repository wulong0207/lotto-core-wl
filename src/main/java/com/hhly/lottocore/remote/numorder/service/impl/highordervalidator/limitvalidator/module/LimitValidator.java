package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module;

import java.util.List;

import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public interface LimitValidator {
	void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator);
}
