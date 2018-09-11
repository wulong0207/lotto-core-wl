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
 * @desc 大乐透追号验证服务
 * @author huangb
 * @date 2017年3月28日
 * @company 益彩网络
 * @version v1.0
 */
@Component("dltChaseValidate")
public class DltChaseValidate extends AbstractChaseValidate {

	/** 大乐透订单明细校验 */
	@Resource(name = "dltOrderDetailValidate")
	private OrderDetailValidate dltOrderDetailValidate;

	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return dltOrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.DEFAULT_CHASE_COUNT;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		return LotteryChild.DLT_PT.getValue();
	}
}
