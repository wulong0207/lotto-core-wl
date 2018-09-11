package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    二同号单选
 * @author  Tony Wang
 * @date    2017年6月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class K3Td2Validator extends K3ChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		// 11#2
		String planContent = orderDetail.getPlanContent();
		// 选号个数和范围
		Assert.isTrue(Pattern.matches("^(11|22|33|44|55|66)#[1-6]$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
		// 重复性
		Assert.isTrue(betNums[0].indexOf(betNums[1])==-1, MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		// 11#2,3  11,22#3
		String planContent = orderDetail.getPlanContent();
		// 选号范围
		Assert.isTrue(Pattern.matches("^(11|22|33|44|55|66)(,11|,22|,33|,44|,55|,66){0,5}#[1-6](,[1-6]){0,5}$", planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
		// 同号
		String [] same = StringUtils.tokenizeToStringArray(betNums[0], SymbolConstants.COMMA);
		// 不同号
		String [] diff = StringUtils.tokenizeToStringArray(betNums[1], SymbolConstants.COMMA);
		// 选号个数
		int len = same.length + diff.length;
		Assert.isTrue(len >= 3 && len <= 12, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(same) || ArrayUtil.isRepeat(diff), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 验证每个不同号，在同号数组中都不重复
		//for(String d : diff) {
		//	for(String s : same)
		//		Assert.isTrue(s.indexOf(d) == -1, MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		//}
		// 2017-7-18,需求改为11,22#1,2也能下注，入订单按用户选择内容入库，但出票时要作去重处理
		// 重新计算注数，同号个数长度*不同号个数长度 -重复的注数
		int repeat = 0;
		for(String d : diff) {
			for(String s : same)
				if(s.indexOf(d) > -1) 
					repeat++;
		}
		return same.length * diff.length - repeat;
	}
	
	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		// 没有胆拖玩法
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
}
