package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    区分位置的校验，只支持单式验证
 * @author  Tony Wang
 * @date    2017年6月20日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class PositionSingleLimitValidator extends AbstractLimitValidator {

	@Autowired
	private LimitValidator positionSingleModuleLimitValidator;
	
	@Override
	protected void validateSingleLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		positionSingleModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}
}
