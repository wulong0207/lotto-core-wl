package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.ssc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.CqsscOrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class CqsscChaseValidate extends SscChaseValidate {

	@Autowired
	private CqsscOrderDetailValidate cqsscOrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return cqsscOrderDetailValidate;
	}
	
	@Override
	protected int getMaxChase() {
		return HighConstants.CQSSC_MAX_CHASE;
	}
}
