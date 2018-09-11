package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator;

import java.util.List;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public class AbstractLimitValidator implements LimitValidator {

	protected void validateSingleLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}

	protected void validateMultiLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}

	protected void validateDantuoLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	protected void validateSumLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Override
	public void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		// 选号方式
		BetContentType contentType = BetContentType.getContentType(orderDetail.getContentType());
		switch (contentType) {
		// 单式
		case SINGLE:
			validateSingleLimit(orderDetail, limits, limitTranslator);
			break;
		// 复式
		case MULTIPLE:
			validateMultiLimit(orderDetail, limits, limitTranslator);
			break;
		// 胆拖
		case DANTUO:
			validateDantuoLimit(orderDetail, limits, limitTranslator);
			break;
		// 和值
		case SUM:
			validateSumLimit(orderDetail, limits, limitTranslator);
			break;
		default:
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
		}
	}
}
