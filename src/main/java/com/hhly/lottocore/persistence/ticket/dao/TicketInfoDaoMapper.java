package com.hhly.lottocore.persistence.ticket.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.cms.ticketmgr.bo.TicketInfoSingleBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketChannelVO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;

/**
 * @desc 票信息数据接口
 * @author huangb
 * @date 2017年2月20日
 * @company 益彩网络
 * @version v1.0
 */
public interface TicketInfoDaoMapper {


	/**
	 * 查询出票失败的票
	 * @param orderCode
	 * @param ticketStatus
	 * @return
	 */
	List<TicketInfoSingleBO> queryFailTicketInfo(@Param("orderCode")String orderCode,@Param("ticketStatus")Integer ticketStatus);


	///////////////////////////////////前端出票明细页面接口////////////////////////////////////////////

	/**
	 * 出票明细，分页接口
	 * @param orderQueryVo
	 * @return
	 */
	List<com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO> findTicketList(OrderQueryVo orderQueryVo);

	/**
	 * 出票明细分页接口总数量
	 * @param orderQueryVo
	 * @return
	 */
	int findTicketListCount(OrderQueryVo orderQueryVo);


	String getTicketChannel(TicketChannelVO vo);

    
	TicketInfoBO getTicketInfo(TicketVO vo);

    
	int updateTicketThirdNum(@Param("id")String id, @Param("machineKey")String machineKey,@Param("ticketChannelId")String ticketChannelId);


	int updateTicketStatus(TicketVO vo);


	int updateTicketThirdNumClear(@Param("id")String id);


	TicketInfoBO getTicketInfoOut(@Param("id")String id);


	/**
	 * 出票明细分页接口总数量
	 * @param vo
	 * @return
	 */
	int findO2OTicketCount(TicketVO vo);

	List<TicketInfoBO> findO2OTicketList(TicketVO vo);

	TicketInfoBO getTicketInfoById(TicketVO vo);

	List<TicketInfoBO> getTicketInfoCache(TicketVO vo);


	int getTicketInfoCount(TicketVO vo);

}