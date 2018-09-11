package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    三同号单选
 * @author  Tony Wang
 * @date    2017年6月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class K3Td3Validator extends K3ChildValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.K3_TD3_RANGE;
	}
	
	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 没有胆拖玩法
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
}
