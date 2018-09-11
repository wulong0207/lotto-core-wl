package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    区分位置的校验，如11选5的直三分万、千、百位
 * @author  Tony Wang
 * @date    2017年6月20日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class PositionLimitValidator extends AbstractLimitValidator {

	@Autowired
	private LimitValidator positionSingleModuleLimitValidator;
	
	@Override
	protected void validateSingleLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		/*
		 * 11选5直三  1,2,3|2|1,2,3
		 * 
		 * 时时彩
		 * A.五星直选和通选
		 * a)单式：1|2|3|4|5
		 * b)复式：1,2,3,4|2|3,4|4|5,6,9
		 */
		positionSingleModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}

	@Override
	protected void validateMultiLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		validateSingleLimit(orderDetail, limits, limitTranslator);
	}
}
