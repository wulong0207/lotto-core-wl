package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.kl10;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.Dkl10OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Dkl10ChaseValidate extends Kl10ChaseValidate {

	@Autowired
	private Dkl10OrderDetailValidate dkl10OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return dkl10OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.DKL10_MAX_CHASE;
	}
}
