package com.hhly.lottocore.persistence.group.dao;

import com.hhly.skeleton.lotto.base.group.bo.OrderGroupLotteryBO;

import java.util.List;

public interface OrderGroupLotteryMapper {

    int insert(OrderGroupLotteryBO record);

    List<OrderGroupLotteryBO> queryOrderGroupLotteryInfo(Integer userId);


}