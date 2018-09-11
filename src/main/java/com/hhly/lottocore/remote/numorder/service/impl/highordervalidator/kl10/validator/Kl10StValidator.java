package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.validator;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.HighConstants;

/**
 * @desc    前一数投
 * @author  Tony Wang
 * @date    2017年7月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Kl10StValidator extends Kl10BetValidator {
	
	@Override
	protected final String [] getNumRange() {
		return HighConstants.KL10_ST_NUM_RANGE;
	}
}