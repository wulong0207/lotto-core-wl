package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module;

import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    单式限号验证(一般，单式和复式限号验证方法一样)
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class SingleModuleLimitValidator implements LimitValidator {

	private static Logger logger = LoggerFactory.getLogger(SingleModuleLimitValidator.class);

	@Override
	public void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		// 如果没有限号信息，直接返回
		if(CollectionUtils.isEmpty(limits)) {
			logger.warn("当前限号信息为空！！！！");
			return;
		}
		/*
		 * 支持单式或复式方案的选号由,#|;分隔， 如：1,2,3  1#2#3  1|2|3 1;2;3，也支持11#2,3
		 * 11选5任n 1,2,3,4,5
		 * 快3二同号复选投注 复式：11*;22*;33*
		 * 
		 * 快3二同号单选投注(对子单选)
		 * 单式:11#2
		 * 复式:11#2,3
		 */
		String [] betNums = StringUtils.tokenizeToStringArray(orderDetail.getPlanContent(),",#|;");
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
					String limitContent = limitDetail.getLimitContent();
					String planContent = orderDetail.getPlanContent();
					String [] limitContentArr = StringUtils.tokenizeToStringArray(limitContent,",#|");
					if(limitTranslator.whetherTraslate(orderDetail.getLotteryChildCode())) {
						limitContent = limitTranslator.translate(limitContent, orderDetail.getLotteryChildCode());
						planContent = limitTranslator.translate(planContent, orderDetail.getLotteryChildCode());
					}
					Assert.isFalse(ArrayUtil.containsAll(betNums, limitContentArr),MessageCodeConstants.BET_CONTENT_LIMIT, planContent, limitContent);
				}
			}
		}
	}
}
