package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module;

import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    区分位置的单式限号验证(如直选三)(一般，单式和复式限号验证方法一样)
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class PositionSingleModuleLimitValidator implements LimitValidator {

	private static Logger logger = LoggerFactory.getLogger(PositionSingleModuleLimitValidator.class);

	@Override
	public void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		/*
		 * 11选5直三  1,2,3|2|1,2,3
		 */
		// 如果没有限号信息，直接返回
		if(CollectionUtils.isEmpty(limits)) {
			logger.warn("当前限号信息为空！！！！");
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<LimitNumberInfoBO> myLimits = (List<LimitNumberInfoBO>)limits;
		// ValidateServiceImpl查询限号信息时，应该加上期数和时间约束
		for(LimitNumberInfoBO limitLottery : myLimits) {
			for(LimitNumberDetailBO limitDetail : limitLottery.getLimitNumberList()) {
				// 找到对应的子玩法限号信息,选号包含限号内容，则不能购买
				// 后台人员设置限号信息时会保证前导0一致
				Integer child = orderDetail.getLotteryChildCode();
				if( Objects.equals(child , limitDetail.getLotteryChildCode()) ){
					String[] limitContentArr = StringUtils.tokenizeToStringArray(limitDetail.getLimitContent(), ",#|");
					// 直选的要区分万、千、百位置
					// "01,02,11|03|02"
					boolean same = true;
					String[] betNums = StringUtils.tokenizeToStringArray(orderDetail.getPlanContent(), SymbolConstants.VERTICAL_BAR);
					for(int i = 0 ; i < betNums.length ; i++) {
						String[] positions = StringUtils.tokenizeToStringArray(betNums[i], SymbolConstants.COMMA);
						if(!ArrayUtil.contains(positions, limitContentArr[i])) {
							same = false;
							break;
						}
					}
					// 断言投注号码跟限号不同
					Assert.isFalse(same, MessageCodeConstants.BET_CONTENT_LIMIT, orderDetail.getPlanContent(), limitDetail.getLimitContent());
				}
			}
		}
	}
}
