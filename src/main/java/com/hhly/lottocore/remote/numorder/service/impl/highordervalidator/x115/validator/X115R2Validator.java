package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.validator;

import org.springframework.stereotype.Component;

/**
 * @desc    前二组选与任二的验证一样，只是算奖时以开奖号码前两位为范围
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class X115R2Validator extends X115RnValidator{

	@Override
	protected int getMinSelect() {
		return 2;
	}
}
