package com.hhly.lottocore.remote.numorder.service.impl.chasevalidator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractChaseValidate;
import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.OrderDetailValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseType;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;

/**
 * @desc 福彩3d追号验证服务
 * @author huangb
 * @date 2017年6月21日
 * @company 益彩网络
 * @version v1.0
 */
@Component("f3dChaseValidate")
public class F3dChaseValidate extends AbstractChaseValidate {
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(F3dChaseValidate.class);
	
	/** 福彩3d订单明细校验 */
	@Resource(name = "f3dOrderDetailValidate")
	private OrderDetailValidate f3dOrderDetailValidate;

	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return f3dOrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.CHASE_COUNT_100;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		logger.debug("福彩3D追号验证=>福彩3D不支持随机追号  end!");
		throw new ResultJsonException(ResultBO.err("40697"));
	}

	@Override
	protected void verifyChaseType(Short chaseType) throws ResultJsonException {
		// 福彩3d仅支持选号追号
		Assert.isTrue(ChaseType.FIXED_NUMBER.getValue() == chaseType, "40697");
	}
}
