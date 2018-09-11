package com.hhly.lottocore.remote.lotto.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * 
 * @author longguoyou

 * @date 2017年2月6日 上午9:39:24

 * @desc  竞彩足球订单服务接口
 *
 */
@SuppressWarnings({ "rawtypes" })
public interface IJczqOrderService {
	/**
	 * 
	 * @author longguoyou
	
	 * @date 2017年2月6日 上午9:42:47
	
	 * @desc 下订单操作
	 *
	 * @param orderInfo
	 * @return 
	 * @throws Exception
	 */
	ResultBO addOrder(OrderInfoVO orderInfo) throws Exception;
}
