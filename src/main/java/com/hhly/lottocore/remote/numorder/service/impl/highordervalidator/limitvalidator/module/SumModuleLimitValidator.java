package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module;

import java.util.List;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    和值限号验证
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class SumModuleLimitValidator implements LimitValidator {

	private static Logger logger = LoggerFactory.getLogger(SumModuleLimitValidator.class);

	@Override
	public void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		// 如果没有限号信息，直接返回
		if(CollectionUtils.isEmpty(limits)) {
			logger.warn("当前限号信息为空！！！！");
			return;
		}
		/*
		 * 支持单式或复式方案的选号由,#|;分隔， 如：1,2,3  1#2#3  1|2|3 1;2;3，也支持11#2,3
		 * 快3二星直选
		 * 和值：1,2,3,4  后台设置的限号格式(对应的单式)：1|1
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
					 * 快3二星直选：1|1
					 */
					List<Integer> limitContent = StringUtil.toIntList(limitDetail.getLimitContent(),",#|;");
					Integer sum = null;
					if(!CollectionUtils.isEmpty(limitContent)) {
						sum = 0;
						for(int limit : limitContent) {
							sum += limit;
						}
					}
					// 验证每个选号都不等于限号的和
					for(int betNum : betNums) {
						Assert.isFalse(Objects.equals(betNum, sum),MessageCodeConstants.BET_CONTENT_LIMIT, orderDetail.getPlanContent(), sum);
					}
				}
			}
		}
	}
}
