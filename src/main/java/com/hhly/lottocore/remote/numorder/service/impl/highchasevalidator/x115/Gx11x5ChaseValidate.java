package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Gx11x5OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Gx11x5ChaseValidate extends X115ChaseValidate {

	@Autowired
	private Gx11x5OrderDetailValidate gx11x5OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return gx11x5OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.GXX115_MAX_CHASE;
	}
}
