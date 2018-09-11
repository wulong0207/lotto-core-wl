package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.k3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jsk3OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Jsk3ChaseValidate extends K3ChaseValidate {

	@Autowired
	private Jsk3OrderDetailValidate jsk3OrderDetailValidator;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return jsk3OrderDetailValidator;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.JSK3_MAX_CHASE;
	}
}
