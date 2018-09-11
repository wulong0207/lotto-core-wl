package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.kl10;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.Cqkl10OrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class Cqkl10ChaseValidate extends Kl10ChaseValidate {

	@Autowired
	private Cqkl10OrderDetailValidate cqkl10OrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return cqkl10OrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.CQKL10_MAX_CHASE;
	}
}
