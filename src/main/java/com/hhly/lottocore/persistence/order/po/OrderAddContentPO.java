package com.hhly.lottocore.persistence.order.po;

import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @author huangb
 *
 * @Date 2016年12月16日
 *
 * @Desc 追号投注内容(与订单明细内容基本一致，生成订单时即作为明细内容入表)
 */
public class OrderAddContentPO extends OrderDetailPO {

	/**
	 * 追号计划编号(对应追号计划实体)
	 */
	private String orderAddCode;

	public OrderAddContentPO() {
	}

	/**
	 * 构造方法
	 * 
	 * @param orderAddCode
	 *            追号计划编号
	 * @param orderDetail
	 *            追号内容
	 */
	public OrderAddContentPO(String orderAddCode, OrderDetailVO orderDetail) {
		super(orderDetail);
		this.orderAddCode = orderAddCode;
	}

	public String getOrderAddCode() {
		return orderAddCode;
	}

	public void setOrderAddCode(String orderAddCode) {
		this.orderAddCode = orderAddCode;
	}

}
