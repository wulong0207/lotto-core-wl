package com.hhly.lottocore.persistence.group.dao;


import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupPersonLatelyInfoBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupQueryVO;

import java.util.List;

public interface OrderGroupMapper {


    int insert(OrderGroupPO orderGroupPO);


    int updateByPrimaryKeySelective(OrderGroupPO orderGroupPO);


    List queryOrderGroupList(OrderGroupQueryVO orderGroupQueryVO);

    /**
     * 合买二期
     * @param orderGroupQueryVO
     * @return
     */
    List queryOrderGroupListV11(OrderGroupQueryVO orderGroupQueryVO);

    int  queryOrderGroupListCount(OrderGroupQueryVO orderGroupQueryVO);

    /**
     * 合买二期
     * @param orderGroupQueryVO
     * @return
     */
    int  queryOrderGroupListCountV11(OrderGroupQueryVO orderGroupQueryVO);

    OrderGroupBO queryOrderGroupByOrderCode(String orderCode);

    List<OrderGroupPersonLatelyInfoBO> queryLatelyThreeRecord(Integer userId);
}