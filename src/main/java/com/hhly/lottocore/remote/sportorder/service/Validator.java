package com.hhly.lottocore.remote.sportorder.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
/**
 * 
 * @author longguoyou
 * @date 2017年3月3日 下午12:36:44
 * @desc 各大彩种必须实现该接口
 */
public interface Validator {
	/**
	 * 彩种具体处理
	 * @author longguoyou
	 * @date 2017年3月9日 上午11:34:00
	 * @param orderDetailVO 订单详情
	 * @param orderInfoVO  订单
	 * @param list 限号列表
	 * @return
	 */
	ResultBO<?> handle(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list);
}
