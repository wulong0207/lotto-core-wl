package com.hhly.lottocore.remote.sportorder.service;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
/**
 * 各大彩种查询出票明细必须实现该接口: 作用接口方法规范
 * @author longguoyou
 * @date 2017年11月2日
 * @compay 益彩网络科技有限公司
 */
public interface HandleTicketDetails {
	/**
	 * 票明细处理接口：1)拼装内容
	 * @author longguoyou
	 * @date 2017年11月2日
	 * @param orderBaseInfoBO 订单信息
	 * @param listTicketInfoBO 一张票信息集合
	 * @param map 其它信息
	 * @return
	 */
	TicketOrderInfoBO handle(OrderBaseInfoBO orderBaseInfoBO, List<TicketInfoBO> listTicketInfoBO, Map<String,Object> map);
}
