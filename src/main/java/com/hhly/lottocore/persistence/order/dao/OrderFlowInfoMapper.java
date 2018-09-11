package com.hhly.lottocore.persistence.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.lotto.base.order.bo.OrderFlowInfoBO;


/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 订单流程
 * @date 2017/4/15 16:39
 * @company 益彩网络科技公司
 */
public interface OrderFlowInfoMapper {

	/**
	 * 查询订单流程信息
	 * @param orderCode
	 * @param userId
	 * @return
	 */
	List<OrderFlowInfoBO> queryOrderFlowInfoList(@Param("orderCode")String orderCode,@Param("userId")Integer userId);


} 
