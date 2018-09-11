package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    支持和值投注的直选玩法
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class SscDirectSumValidator extends SscDirectValidator {
	
//	private static final Map<String, Integer> SUM_MAP;
//    static {
//        Map<String, Integer> map = new HashMap<>();
//        map.put("0", 1);
//        map.put("1", 2);
//        map.put("2", 3);
//        map.put("3", 4);
//        map.put("4", 5);
//        map.put("5", 6);
//        map.put("6", 7);
//        map.put("7", 8);
//        map.put("8", 9);
//        map.put("9", 10);
//        map.put("10", 9);
//        map.put("11", 8);
//        map.put("12", 7);
//        map.put("13", 6);
//        map.put("14", 5);
//        map.put("15", 4);
//        map.put("16", 3);
//        map.put("17", 2);
//        map.put("18", 1);
//        SUM_MAP = Collections.unmodifiableMap(map);
//    }
    
	/**
	 * 直选目前只有二星、三星才有和值投注
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
		Assert.isTrue(planContents.length <= getSumRange().length);
		// 是否重复
		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 数值范围
		Assert.isTrue(ArrayUtil.containsAll(getSumRange(), planContents), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		int betNums = 0;
		for(String tmp : planContents) {
			betNums += getSumMap().get(tmp);
		}
		return betNums;
	}

	protected abstract String[] getSumRange();

	protected abstract Map<String, Integer> getSumMap();
}
