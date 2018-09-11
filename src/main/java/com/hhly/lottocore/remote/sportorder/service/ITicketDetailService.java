package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.ticket.bo.O2OTicketBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketChannelVO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 出票明细service
 * @date 2017/11/1 10:45
 * @company 益彩网络科技公司
 */
public interface ITicketDetailService {

    /**
     * 获取出票明细信息
     * @param orderQueryVo
     * @return
     */
    ResultBO<?> queryTicketDetailInfo(OrderQueryVo orderQueryVo) throws Exception;
    /**
     * 获取渠道商id
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年6月24日 下午5:36:50
     * @param vo
     * @return
     */
	String getChannel(TicketChannelVO vo);
    /**
     * 获取票信息
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年6月24日 下午5:36:59
     * @param vo
     * @return
     */
    O2OTicketBO getTicket(TicketVO vo);
    /**
     * 修改票信息
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年6月24日 下午5:37:08
     * @param vo
     * @return
     */
	int updateTicket(TicketVO vo);

    /**
     * 获取线下出票列表
     * @param vo
     * @return
     */
    PagingBO<TicketInfoBO> findTicketInfo(TicketVO vo);

    /**
     * 根据票id获取票信息
     * @param vo
     * @return
     */
    O2OTicketBO getTicketById(TicketVO vo);
}
