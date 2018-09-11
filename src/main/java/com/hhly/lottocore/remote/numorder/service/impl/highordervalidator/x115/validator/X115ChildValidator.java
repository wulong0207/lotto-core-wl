package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.AbstractChildValidator;
import com.hhly.skeleton.base.constants.HighConstants;

public abstract class X115ChildValidator extends AbstractChildValidator {

	@Override
	protected final String [] getNumRange() {
		return HighConstants.X115_NUM_RANGE;
	}
}
