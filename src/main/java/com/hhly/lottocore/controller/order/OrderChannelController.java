package com.hhly.lottocore.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderChannelBO;
import com.hhly.skeleton.task.order.vo.OrderChannelVO;
import com.hhly.skeleton.task.order.vo.OrderInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderChannelController {

    @Autowired
    private IOrderSearchService orderSearchService;

    @RequestMapping(value = "/channel/orderList", method = RequestMethod.POST)
    public ResultBO<?> queryChannelOrderList(@RequestBody OrderChannelVO vo) {
        List<OrderChannelBO> list = orderSearchService.queryChannelOrderList(vo);
        return ResultBO.ok(list);
    }
}
