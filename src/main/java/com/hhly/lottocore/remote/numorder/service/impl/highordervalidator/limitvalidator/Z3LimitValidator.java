package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    组三限号验证，支持单式、复式、胆拖验证
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class Z3LimitValidator extends AbstractLimitValidator {

	private static Logger logger = LoggerFactory.getLogger(Z3LimitValidator.class);

	@Autowired
	private LimitValidator singleModuleLimitValidator;
	
	@Autowired
	private LimitValidator dantuoModuleLimitValidator;

	@Override
	protected void validateSingleLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		// 如果没有限号信息，直接返回
		if(CollectionUtils.isEmpty(limits)) {
			logger.warn("当前限号信息为空！！！！");
			return;
		}
		/*
		 * 时时彩组3:8,8,9,限号8,9,9,则投注可通过
		 */
		List<Integer> betNums = StringUtil.toIntList(orderDetail.getPlanContent(),",#|;");
		@SuppressWarnings("unchecked")
		List<LimitNumberInfoBO> myLimits = (List<LimitNumberInfoBO>)limits;
		// ValidateServiceImpl查询限号信息时，应该加上期数和时间约束
		for(LimitNumberInfoBO limitLottery : myLimits) {
			for(LimitNumberDetailBO limitDetail : limitLottery.getLimitNumberList()) {
				if( Objects.equals(orderDetail.getLotteryChildCode() , limitDetail.getLotteryChildCode()) ){
					/*
					 * 支持限号方案(后台注意只能配单式)格式的选号由,#|;分隔， 如：1,2,3  1#2#3  1|2|3 1;2;3
					 * 11选5任n 1,2,3,4,5
					 * 快3二同号复选投注 复式：11*;22*;33*
					 */
					List<Integer> limitContent = StringUtil.toIntList(limitDetail.getLimitContent(),",#|");
					Collections.sort(limitContent);
					Collections.sort(betNums);
					// 注意限号信息只能配单式，否则验证有问题。断言 [8,8,9]与[8,9,9]不一样
					boolean same = true;
					for(int i = 0 ; i < betNums.size() ; i++) {
						if(!Objects.equals(betNums.get(i), limitContent.get(i))) {
							// 只要有一位不一样，则不会被限号
							same = false;
							break;
						}
					}
					Assert.isFalse(same,MessageCodeConstants.HIGH_BET_NUM_LIMIT, orderDetail.getPlanContent() );
				}
			}
		}
	}

	@Override
	protected void validateMultiLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		singleModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}

	@Override
	protected void validateDantuoLimit(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		dantuoModuleLimitValidator.validate(orderDetail, limits, limitTranslator);
	}
}
