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
 * @desc 七乐彩追号验证服务
 * @author huangb
 * @date 2017年6月21日
 * @company 益彩网络
 * @version v1.0
 */
@Component("qlcChaseValidate")
public class QlcChaseValidate extends AbstractChaseValidate {
	
	/** 七乐彩订单明细校验 */
	@Resource(name = "qlcOrderDetailValidate")
	private OrderDetailValidate qlcOrderDetailValidate;

	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return qlcOrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.CHASE_COUNT_100;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		return LotteryChild.QLC_PT.getValue();
	}
}
