package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import org.springframework.stereotype.Component;

/**
 * @desc    三星组六
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc3z6Validator extends SscGroupValidator {
	
	@Override
	protected int getMinSelect() {
		return 3;
	}
}
