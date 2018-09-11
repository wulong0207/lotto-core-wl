package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    直选
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class SscDirectValidator extends SscChildValidator{
	
	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		/*
		 * 五星直选和通选
		 * 单式：1|2|3|4|5
		 */
		String planContent = orderDetail.getPlanContent();
		String reg = "^([0-9])(\\|[0-9]){"+ (getMinSelect()-1) +"}$";
		// 选号可重复，验证个数及数值范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		/*
		 * 五星直选和通选
		 * 复式：1,2,3,4|2|3,4|4|5,6,9
		 */
		String planContent = orderDetail.getPlanContent();
		String reg = "^([0-9])(,[0-9]){0,9}(\\|([0-9])(,[0-9]){0,9}){"+(getMinSelect()-1)+"}$";
		// 验证个数及数值范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 验证选号个数，如五星直选复式，所有位置的号码至少6个，而最多号码正则已验证过
		String [] allNums = StringUtils.tokenizeToStringArray(planContent, ",|");
		Assert.isTrue(allNums.length > getMinSelect(), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 不同位置的选号可重复，但同位置的选号不可重复，如"1,2,3,4|3,4|4|5,6,9|6,6,6,6"会通过正则验证，但实际是非法值
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
		int betNums = 1;
		for(String positionNums : planContents) {
			String [] positionNumArr = StringUtils.tokenizeToStringArray(positionNums, SymbolConstants.COMMA);
			Assert.isFalse(ArrayUtil.isRepeat(positionNumArr), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
			betNums *= positionNumArr.length;
		}
		return betNums;
	}

}
