package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.RnValidator;
import com.hhly.skeleton.base.constants.HighConstants;

/**
 * @desc    三不同号
 * @author  Tony Wang
 * @date    2017年6月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class K3Bt3Validator extends RnValidator {

	@Override
	protected int getMinSelect() {
		return 3;
	}

	@Override
	protected String[] getNumRange() {
		return HighConstants.K3_NUM_RANGE;
	}
}
