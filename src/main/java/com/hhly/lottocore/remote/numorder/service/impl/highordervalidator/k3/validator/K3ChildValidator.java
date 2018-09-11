package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import org.springframework.util.StringUtils;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.AbstractChildValidator;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class K3ChildValidator extends AbstractChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		/* 和值：1
		 * 三同号通选投注(豹子全包) 3T
		 * 三同号单选投注(豹子单选) 111
		 * 三不同号 1,2,3
		 * 三连号通选投注(顺子全包) 3L
		 * 二同号复选投注(对子复选) 11*
		 * 二同号单选投注(对子单选) 11#2
		 * 二不同号投注 1,2
		 */
		String planContent = orderDetail.getPlanContent();
		Assert.isTrue(ArrayUtil.contains(getNumRange(), planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		/* 和值：4,5,6
		 * 三同号通选投注(豹子全包) 无复式
		 * 三同号单选投注(豹子单选) 111,222,333
		 * 三不同号 1,2,3,4
		 * 三连号通选投注(顺子全包) 无复式
		 * 二同号复选投注(对子复选) 11*;22*;33*
		 * 二同号单选投注(对子单选) 11#2,3
		 * 二不同号投注 1,2,3
		 */
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
	
//	protected int validateDantuo(OrderDetailVO orderDetail) {
//		/*
//		 * 只有三不同号和二不同号有胆拖
//		 * 1,2#3,4  1#2,3,4
//		 */
//		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
//	}
}
