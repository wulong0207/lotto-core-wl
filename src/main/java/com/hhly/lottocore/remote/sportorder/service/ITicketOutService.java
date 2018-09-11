package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.lotto.base.ticket.bo.O2OTicketBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;
/**
 * @desc 人工出票
 * @author jiangwei
 * @date 2018年7月10日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface ITicketOutService {
	/**
	 * 获取缓存票
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月10日 上午9:26:35
	 * @param vo
	 * @return
	 */
	TicketInfoBO getTikcet(TicketVO vo);
	
	/**
	 * 解析格式
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月10日 上午9:26:51
	 * @param tib
	 * @return
	 */
	O2OTicketBO getOtoTicket(TicketInfoBO tib,String machineKey);
	/**
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月25日 下午5:22:49
	 * @param ticketInfoBO
	 * @param vo
	 */
	void setEqualMachineKey(TicketInfoBO ticketInfoBO,TicketVO vo);
}
