package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    支持单式、复式、胆拖、和值限号验证
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class SingleMultiDantuoSumLimitValidator extends AbstractLimitValidator {

	@Autowired
	private LimitValidator singleModuleLimitValidator;
	@Autowired
	private LimitValidator dantuoModuleLimitValidator;
	@Autowired
	private LimitValidator sumModuleLimitValidator;
	
	@Override
	protected void validateSingleLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		singleModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}

	@Override
	protected void validateMultiLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		/*
		 * 11选5任n 1,2,3,4,5,6,7
		 */
		validateSingleLimit(orderDetail, limits, limitTranslator);
	}

	
	@Override
	protected void validateDantuoLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		dantuoModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}

	@Override
	protected void validateSumLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		sumModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}
	
}
