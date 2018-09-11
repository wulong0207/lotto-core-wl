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
 * @desc    单式限号验证(一般，单式和复式限号验证方法一样)
 * @author  Tony Wang
 * @date    2017年6月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class DantuoModuleLimitValidator implements LimitValidator {

	private static Logger logger = LoggerFactory.getLogger(DantuoModuleLimitValidator.class);

	@Override
	public void validate(OrderDetailVO orderDetail, List<?> limits, LimitTranslator limitTranslator) {
		/*
		 * 11选5任n 1,2,3#4,5,6,7,8,9
		 * 快乐扑克任选N  胆拖   A,2#4,5,6,7,8,9
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
				if( Objects.equals(orderDetail.getLotteryChildCode() , limitDetail.getLotteryChildCode()) ){
					// 注意后台配置限号只能配单式
					String limitContent = limitDetail.getLimitContent();
					String [] limitContentArr = StringUtils.tokenizeToStringArray(limitContent,",#|");
					// 拆成胆码和拖码
					String planContent = orderDetail.getPlanContent();
					String [] planContentArr = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
					// 若胆码没在限号中，则投注方案一定不会包含限号，直接continue
					String [] danma = StringUtils.tokenizeToStringArray(planContentArr[0], SymbolConstants.COMMA);
					if(!ArrayUtil.containsAll(limitContentArr, danma)) {
						continue;
					}
					// 如果胆码和拖码都在限号内容中，则符合限号规则
					String [] betNums = StringUtils.tokenizeToStringArray(planContent,",#|");
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
