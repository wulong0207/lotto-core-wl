package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.poker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.HighChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.SdPokerOrderDetailValidate;
import com.hhly.skeleton.base.constants.HighConstants;

@Component
public class SdPokerChaseValidate extends HighChaseValidate {

	@Autowired
	private SdPokerOrderDetailValidate sdPokerOrderDetailValidate;
	
	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return sdPokerOrderDetailValidate;
	}
	@Override
	protected int getMaxChase() {
		return HighConstants.SDPOKER_MAX_CHASE;
	}
	
}
