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
 * @desc 排列三追号验证服务
 * @author huangb
 * @date 2017年6月21日
 * @company 益彩网络
 * @version v1.0
 */
@Component("pl3ChaseValidate")
public class Pl3ChaseValidate extends AbstractChaseValidate {
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(Pl3ChaseValidate.class);
	
	/** 排列三订单明细校验 */
	@Resource(name = "pl3OrderDetailValidate")
	private OrderDetailValidate pl3OrderDetailValidate;

	@Override
	protected AbstractOrderDetailValidate getOrderDetailValidator() {
		return pl3OrderDetailValidate;
	}

	@Override
	protected int getMaxChase() {
		return NUMConstants.CHASE_COUNT_100;
	}

	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		logger.debug("排列三追号验证=>排列三不支持随机追号  end!");
		throw new ResultJsonException(ResultBO.err("40697"));
	}

	@Override
	protected void verifyChaseType(Short chaseType) throws ResultJsonException {
		// 福彩3d仅支持选号追号
		Assert.isTrue(ChaseType.FIXED_NUMBER.getValue() == chaseType, "40697");
	}
}
