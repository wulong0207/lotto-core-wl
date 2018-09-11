package com.hhly.lottocore.persistence.group.dao;

import com.hhly.skeleton.lotto.base.group.bo.OrderGroupPersonLatelyInfoBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupUserBO;

import java.util.List;

public interface OrderGroupUserMapper {

    int insert(OrderGroupUserBO record);

    OrderGroupUserBO queryOrderGroupUserInfo(Integer userId);

    List<OrderGroupPersonLatelyInfoBO> queryGroupRankingList();

    List<String> queryGroupFamousList();

}