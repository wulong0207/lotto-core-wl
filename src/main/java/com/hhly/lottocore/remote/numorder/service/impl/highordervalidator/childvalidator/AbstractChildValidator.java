package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class AbstractChildValidator implements ChildValidator {

	protected int getMinSelect() {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.OPERATION_NOT_SUPPORTED));
	}
	
	protected int validateSingle(OrderDetailVO orderDetail) {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	protected int validateSum(OrderDetailVO orderDetail) {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	protected String[] getNumRange() {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Override
	public int validate(OrderDetailVO orderDetail) {
		// 选号方式
		BetContentType contentType = BetContentType.getContentType(orderDetail.getContentType());
		switch (contentType) {
		// 单式
		case SINGLE:
			return validateSingle(orderDetail);
		// 复式
		case MULTIPLE:
			return validateMulti(orderDetail);
		// 胆拖
		case DANTUO:
			return validateDantuo(orderDetail);
		// 和值
		case SUM:
			return validateSum(orderDetail);
		default:
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
		}
	}
}
