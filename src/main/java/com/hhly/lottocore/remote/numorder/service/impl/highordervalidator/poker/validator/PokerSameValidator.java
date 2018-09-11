package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.validator;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.HighConstants;

/**
 * @desc    同花
 * @author  Tony Wang
 * @date    2017年6月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class PokerSameValidator extends PockerChildValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.POKER_SAME_NUM_RANGE;
	}
}
