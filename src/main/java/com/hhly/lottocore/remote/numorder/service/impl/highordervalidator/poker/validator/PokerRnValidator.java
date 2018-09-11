package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.RnValidator;
import com.hhly.skeleton.base.constants.HighConstants;

public abstract class PokerRnValidator extends RnValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.POKER_NUM_RANGE;
	}
	
}
