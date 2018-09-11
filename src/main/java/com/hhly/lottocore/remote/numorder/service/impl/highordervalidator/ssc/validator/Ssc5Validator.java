package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import org.springframework.stereotype.Component;

/**
 * @desc    五星直选和通选，两都的投注验证方式一样，只是开奖方式不同
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc5Validator extends SscDirectValidator {
	
	@Override
	protected int getMinSelect() {
		return 5;
	}
}
