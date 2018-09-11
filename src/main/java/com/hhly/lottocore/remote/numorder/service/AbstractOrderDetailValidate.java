package com.hhly.lottocore.remote.numorder.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 提供数字彩，高频彩订单详情的公共验证方法（定义这样一个公共验证类，是为了在追号验证流程中能复用，也便于后续扩展）
 * @author huangb
 * @date 2017年12月16日
 * @company 益彩网络
 * @version v1.0
 */
public abstract class AbstractOrderDetailValidate {
	
	/**
	 * @desc 订单明细内容的验证主方法（主要提供给追号验证流程复用）
	 * @author huangb
	 * @date 2017年12月16日
	 * @param orderDetail 订单明细内容/追号内容
	 * @param list 限号集合
	 * @return 订单明细内容的验证主方法（主要提供给追号验证流程复用）
	 * @throws ResultJsonException
	 */
	public abstract ResultBO<?> handleProcess(OrderDetailVO orderDetail, List<?> list) throws ResultJsonException;
}
