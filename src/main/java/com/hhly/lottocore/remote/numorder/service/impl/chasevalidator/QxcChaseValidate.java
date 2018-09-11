package com.hhly.lottocore.remote.numorder.service.impl.chasevalidator;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractChaseValidate;
import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.OrderDetailValidate;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;

@Component
public class QxcChaseValidate extends AbstractChaseValidate {

	/** 大乐透订单明细校验 */
	@Resource(name = "qxcOrderDetailValidate")
	private OrderDetailValidate qxcOrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return qxcOrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.CHASE_COUNT_100;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		return LotteryChild.QXC_PT.getValue();
	}

}
