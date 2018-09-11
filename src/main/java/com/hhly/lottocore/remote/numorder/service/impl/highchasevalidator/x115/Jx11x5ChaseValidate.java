package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Jx11x5OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Jx11x5ChaseValidate extends X115ChaseValidate {

	@Autowired
	private Jx11x5OrderDetailValidate jx11x5OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return jx11x5OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.JXX115_MAX_CHASE;
	}
}
