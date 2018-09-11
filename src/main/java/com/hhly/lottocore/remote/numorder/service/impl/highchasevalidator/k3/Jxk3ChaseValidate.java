package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.k3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jxk3OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Jxk3ChaseValidate extends K3ChaseValidate {

	@Autowired
	private Jxk3OrderDetailValidate jxk3OrderDetailValidator;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return jxk3OrderDetailValidator;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.JXK3_MAX_CHASE;
	}
}
