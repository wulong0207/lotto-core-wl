package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    二同号复选
 * @author  Tony Wang
 * @date    2017年6月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class K3Tf2Validator extends K3ChildValidator {

	@Override
	protected String[] getNumRange() {
		return HighConstants.K3_TF2_RANGE;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 11*;22*;33*, 换成逗号分隔11*,22*,33*
		String planContent = orderDetail.getPlanContent();
		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 选号个数
		Assert.isTrue(betNums.length >= 2 && betNums.length <= getNumRange().length, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重新计算注数
		return betNums.length;
	}
	
	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 没有胆拖玩法
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
}
