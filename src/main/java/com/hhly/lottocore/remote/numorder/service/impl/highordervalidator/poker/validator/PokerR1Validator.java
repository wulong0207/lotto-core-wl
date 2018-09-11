package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.validator;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    任一
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class PokerR1Validator extends PokerRnValidator{

	@Override
	protected int getMinSelect() {
		return 1;
	}
	
	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 任一没有胆拖
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
}
