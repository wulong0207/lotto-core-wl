package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.RnValidator;
import com.hhly.skeleton.base.constants.HighConstants;

public abstract class Kl10RnValidator extends RnValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.KL10_NUM_RANGE;
	}
}
