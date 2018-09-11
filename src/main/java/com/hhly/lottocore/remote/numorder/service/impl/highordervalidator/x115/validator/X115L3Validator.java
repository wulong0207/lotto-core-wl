package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import org.springframework.stereotype.Component;

/**
 * @desc    乐3
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class X115L3Validator extends X115LnValidator{

	@Override
	protected int getMinSelect() {
		return 3;
	}
}
