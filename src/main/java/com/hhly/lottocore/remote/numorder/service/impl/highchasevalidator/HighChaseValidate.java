package com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator;

import org.apache.log4j.Logger;

import com.hhly.lottocore.remote.numorder.service.AbstractChaseValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseType;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;

/**
 * @desc    高频彩追号验证
 * @author  Tony Wang
 * @date    2017年3月23日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class HighChaseValidate extends AbstractChaseValidate {

	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(HighChaseValidate.class);
	
	@Override
	protected void verifyChaseType(Short chaseType) throws ResultJsonException {
		// 高频彩目前都只能固定追号
		Assert.isTrue(ChaseType.FIXED_NUMBER.getValue() == chaseType, "40697");
	}
	
	@Override
	protected Integer getRandomContentChildCode() throws ResultJsonException {
		logger.debug("高频彩追号验证=>高频彩都不支持随机追号  end!");
		throw new ResultJsonException(ResultBO.err("40697"));
	}
}
