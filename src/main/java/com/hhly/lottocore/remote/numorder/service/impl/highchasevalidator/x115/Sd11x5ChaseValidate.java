package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Sd11x5OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Sd11x5ChaseValidate extends X115ChaseValidate {

	@Autowired
	private Sd11x5OrderDetailValidate sd11x5OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return sd11x5OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.SDX115_MAX_CHASE;
	}
}
