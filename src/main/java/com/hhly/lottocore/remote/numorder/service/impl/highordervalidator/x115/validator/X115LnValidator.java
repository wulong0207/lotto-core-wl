package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.LnValidator;
import com.hhly.skeleton.base.constants.HighConstants;

public abstract class X115LnValidator extends LnValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.X115_NUM_RANGE;
	}
}
