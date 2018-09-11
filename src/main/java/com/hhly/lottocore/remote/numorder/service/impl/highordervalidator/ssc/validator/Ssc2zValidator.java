package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    二星组选
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc2zValidator extends SscGroupValidator {
	
	private static final Map<String, Integer> SUM_MAP;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("0", 1);
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 2);
        map.put("4", 3);
        map.put("5", 3);
        map.put("6", 4);
        map.put("7", 4);
        map.put("8", 5);
        map.put("9", 5);
        map.put("10", 5);
        map.put("11", 4);
        map.put("12", 4);
        map.put("13", 3);
        map.put("14", 3);
        map.put("15", 2);
        map.put("16", 2);
        map.put("17", 1);
        map.put("18", 1);
        SUM_MAP = Collections.unmodifiableMap(map);
    }
	
	@Override
	protected int getMinSelect() {
		return 2;
	}
	
	/**
	 * 组选只有二星才有和值投注
	 */
	@Override
	protected int validateSum(OrderDetailVO orderDetail) {
		/*
		 * 和值单式：1
		 * 和值复式：2,3,4,18
		 */
		String planContent = orderDetail.getPlanContent();
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
		// 个数
		Assert.isTrue(planContents.length <= HighConstants.SSC_2_SUM_RANGE.length);
		// 是否重复
		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 数值范围
		Assert.isTrue(ArrayUtil.containsAll(HighConstants.SSC_2_SUM_RANGE, planContents), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		int betNums = 0;
		for(String tmp : planContents) {
			betNums += SUM_MAP.get(tmp);
		}
		return betNums;
	}
}
