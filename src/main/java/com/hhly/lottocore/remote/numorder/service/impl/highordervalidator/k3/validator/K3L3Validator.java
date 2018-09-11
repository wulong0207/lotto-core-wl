package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    三连号通选
 * @author  Tony Wang
 * @date    2017年6月16日planContent
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class K3L3Validator extends K3ChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue("3L".equals(planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		return 1;
	}
	
	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 没有复式玩法
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 没有胆拖玩法
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
}
