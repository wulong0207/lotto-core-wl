package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    三星组三
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc3z3Validator extends SscGroupValidator {
	
	@Override
	protected int getMinSelect() {
		return 2;
	}

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		// 单式：0,1,1(组三)
		String planContent = orderDetail.getPlanContent();
		String reg = "^([0-9])(,[0-9]){2}$";
		// 验证个数及数值范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 验证有且仅有两个数重复
		Set<String> mySet = new HashSet<String>(Arrays.asList(planContents));
		Assert.isTrue(mySet.size()==2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		/*
		 * 复式：2,3,4,5,6
		 */
		String planContent = orderDetail.getPlanContent();
		// 组三选2~10个选号
		String reg = String.format("^([0-9])(,[0-9]){%d,%d}$", (getMinSelect()-1), 9);
		// 个数和范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 重复性
		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 重算注数, 2个号码 2*1 3个号码 3*2，类推
		return planContents.length * (planContents.length-1);
	}

	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		/*
		 * 胆拖：1#3,4,5,6
		 */
		String planContent = orderDetail.getPlanContent();
		String reg = "^[0-9]#[0-9](,[0-9]){0,8}$";
		// 个数和范围
		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重复性		
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.NUMBER_SIGN);
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
		// 验证胆码
		String [] tuoma = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		// 三星组三胆拖的注数为拖码个数*2
		return tuoma.length * 2;
	}
	
}
