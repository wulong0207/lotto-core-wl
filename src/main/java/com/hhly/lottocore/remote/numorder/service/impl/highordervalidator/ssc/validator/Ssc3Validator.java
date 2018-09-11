package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.constants.HighConstants;

/**
 * @desc    三星直选
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Ssc3Validator extends SscDirectSumValidator {
	
	private static final Map<String, Integer> SUM_MAP;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("0", 1);
        map.put("1", 3);
        map.put("2", 6);
        map.put("3", 10);
        map.put("4", 15);
        map.put("5", 21);
        map.put("6", 28);
        map.put("7", 36);
        map.put("8", 45);
        map.put("9", 55);
        map.put("10", 63);
        map.put("11", 69);
        map.put("12", 73);
        map.put("13", 75);
        map.put("14", 75);
        map.put("15", 73);
        map.put("16", 69);
        map.put("17", 63);
        map.put("18", 55);
        map.put("19", 45);
        map.put("20", 36);
        map.put("21", 28);
        map.put("22", 21);
        map.put("23", 15);
        map.put("24", 10);
        map.put("25", 6);
        map.put("26", 3);
        map.put("27", 1);
        SUM_MAP = Collections.unmodifiableMap(map);
    }
	
	@Override
	protected int getMinSelect() {
		return 3;
	}

	@Override
	protected String[] getSumRange() {
		return HighConstants.SSC_3_SUM_RANGE;
	}

	@Override
	protected Map<String, Integer> getSumMap() {
		return SUM_MAP;
	}
}
