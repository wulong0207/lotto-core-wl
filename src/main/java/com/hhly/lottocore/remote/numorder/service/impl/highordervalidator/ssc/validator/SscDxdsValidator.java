package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    大小单双
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class SscDxdsValidator extends SscChildValidator{
	/*
	 * 大小单双说明：大2 小1 单3 双4
	 */
	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		// 单式：1|1
		String planContent = orderDetail.getPlanContent();
		String reg = "^[1-4]\\|[1-4]$";
		// 选号可重复，验证个数及数值范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}

	// 2017-7-27 产品要求不支持复式投注
//	@Override
//	protected int validateMulti(OrderDetailVO orderDetail) {
//		// 复式：1,2|1,3
//		String planContent = orderDetail.getPlanContent();
//		String reg = "^([1-4])(,[1-4]){0,3}\\|([1-4])(,[1-4]){0,3}$";
//		// 选号可重复，验证个数及数值范围
//		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
//		String [] ten = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
//		String [] unit = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
//		// 复式至少3个选号,至多8个选号已由正则表达式验证
//		Assert.isTrue(ten.length + unit.length >= 3, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 验证十位选号不重复、个位选号不重复
//		Assert.isFalse(ArrayUtil.isRepeat(ten) || ArrayUtil.isRepeat(unit), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		// 重算注数
//		return ten.length * unit.length;
//	}
	
}
