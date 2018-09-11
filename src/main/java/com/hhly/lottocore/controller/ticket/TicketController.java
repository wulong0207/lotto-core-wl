package com.hhly.lottocore.controller.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hhly.lottocore.remote.sportorder.service.ITicketDetailService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.ticket.bo.O2OTicketBO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketChannelVO;
import com.hhly.skeleton.lotto.base.ticket.vo.TicketVO;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private ITicketDetailService ticketDetailService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResultBO<?> login(@RequestBody TicketChannelVO vo) {
        String channelId = ticketDetailService.getChannel(vo);
        if (StringUtils.isEmpty(channelId)) {
            return ResultBO.err("45201");
        }
        return ResultBO.ok(channelId);
    }

    @RequestMapping(value = "/getTicket", method = RequestMethod.POST)
    public ResultBO<?> getTicket(@RequestBody TicketVO vo) {
        O2OTicketBO infoBO = null;
        if (vo.getId() == null) {
            infoBO = ticketDetailService.getTicket(vo);
        } else {
            infoBO = ticketDetailService.getTicketById(vo);
        }
        if (infoBO == null) {
            return ResultBO.err("45200");
        }
        return ResultBO.ok(infoBO);
    }

    @RequestMapping(value = "/updateTicket", method = RequestMethod.POST)
    public ResultBO<?> updateTicket(@RequestBody TicketVO vo) {
        int num = ticketDetailService.updateTicket(vo);
        return ResultBO.ok(num);
    }


    @RequestMapping(value = "/outTicketList", method = RequestMethod.POST)
    public ResultBO<?> findOutTicketList(@RequestBody TicketVO vo) {
        return ResultBO.ok(ticketDetailService.findTicketInfo(vo));
    }
}
