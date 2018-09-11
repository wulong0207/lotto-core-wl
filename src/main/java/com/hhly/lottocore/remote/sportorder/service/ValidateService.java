/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service;

import java.util.Map;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * @author scott
 * @desc 验证服务接口
 */
public interface ValidateService {
	
	
	/**
	 * 验证订单信息方法 
	 * @param orderInfoVO 订单参数
	 * @param map 一些预验证/比较的数据源
	 * @return
	 */
	ResultBO<?> validateOrder(OrderInfoVO orderInfoVO, Map<String, Object> map)throws Exception;
}
