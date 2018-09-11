package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator;

import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public interface ChildValidator {

	/**
	 * @desc   验证子玩法投注内容
	 * @author Tony Wang
	 * @create 2017年6月13日
	 * @return 重新计算的注数
	 */
	int validate(OrderDetailVO orderDetail);
}
