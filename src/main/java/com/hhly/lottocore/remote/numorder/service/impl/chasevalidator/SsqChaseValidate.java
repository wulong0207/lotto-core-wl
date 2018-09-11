package com.hhly.lottocore.remote.numorder.service.impl.chasevalidator;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractChaseValidate;
import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.OrderDetailValidate;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;

/**
 * @desc 双色球追号验证服务
 * @author huangb
 * @date 2017年3月28日
 * @company 益彩网络
 * @version v1.0
 */
@Component("ssqChaseValidate")
public class SsqChaseValidate extends AbstractChaseValidate {

	/** 双色球订单明细校验 */
	@Resource(name = "ssqOrderDetailValidate")
	private OrderDetailValidate ssqOrderDetailValidate;

	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return ssqOrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.DEFAULT_CHASE_COUNT;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		return LotteryChild.SSQ_PT.getValue();
	}
}
