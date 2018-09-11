package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.HighConstants;

/**
 * @desc    二星直选
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc2Validator extends SscDirectSumValidator {
	
	private static final Map<String, Integer> SUM_MAP;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("0", 1);
        map.put("1", 2);
        map.put("2", 3);
        map.put("3", 4);
        map.put("4", 5);
        map.put("5", 6);
        map.put("6", 7);
        map.put("7", 8);
        map.put("8", 9);
        map.put("9", 10);
        map.put("10", 9);
        map.put("11", 8);
        map.put("12", 7);
        map.put("13", 6);
        map.put("14", 5);
        map.put("15", 4);
        map.put("16", 3);
        map.put("17", 2);
        map.put("18", 1);
        SUM_MAP = Collections.unmodifiableMap(map);
    }
    
	@Override
	protected int getMinSelect() {
		return 2;
	}

//	/**
//	 * 直选目前只有二星、三星才有和值投注
//	 */
//	@Override
//	protected int validateSum(OrderDetailVO orderDetail) {
//		/*
//		 * 和值单式：1
//		 * 和值复式：2,3,4,18
//		 */
//		String planContent = orderDetail.getPlanContent();
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
//		// 个数
//		Assert.isTrue(planContents.length <= HighConstants.SSC_SUM_RANGE.length);
//		// 是否重复
//		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		// 数值范围
//		Assert.isTrue(ArrayUtil.containsAll(HighConstants.SSC_SUM_RANGE, planContents), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		int betNums = 0;
//		for(String tmp : planContents) {
//			betNums += SUM_MAP.get(tmp);
//		}
//		return betNums;
//	}

	@Override
	protected String[] getSumRange() {
		return HighConstants.SSC_2_SUM_RANGE;
	}

	@Override
	protected Map<String, Integer> getSumMap() {
		return SUM_MAP;
	}
}
