package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Xj11x5OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Xj11x5ChaseValidate extends X115ChaseValidate {

	@Autowired
	private Xj11x5OrderDetailValidate xj11x5OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return xj11x5OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.XJX115_MAX_CHASE;
	}
}
