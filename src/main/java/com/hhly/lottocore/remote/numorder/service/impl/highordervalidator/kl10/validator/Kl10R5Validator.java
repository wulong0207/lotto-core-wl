package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.validator;

import org.springframework.stereotype.Component;

/**
 * @desc    任五
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Kl10R5Validator extends Kl10RnValidator{

	@Override
	protected int getMinSelect() {
		return 5;
	}
}
