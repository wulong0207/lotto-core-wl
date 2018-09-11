package com.hhly.lottocore.persistence.group.dao;


import com.hhly.lottocore.persistence.group.po.OrderGroupContentPO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupContentBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderMyGroupBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderGroupContentMapper {


    int insert(OrderGroupContentPO orderGroupContentPO);


    int updateByPrimaryKeySelective(OrderGroupContentPO orderGroupContentPO);


    List<OrderMyGroupBO> queryOrderGroupContentByUserId(@Param("orderCode") String orderCode, @Param("userId") Integer userId);

    OrderGroupContentBO queryOrderGroupContentById(Integer id);


    int queryOrderGroupContentCount(OrderGroupContentVO orderGroupContentVO);

    List<OrderMyGroupBO> queryOrderGroupContentList(OrderGroupContentVO orderGroupContentVO);

    /**
     * 查找跟单表里面的发单人记录
     * @param orderCode
     * @return
     */
    OrderGroupContentBO findOrderGroupRecord(String orderCode);

    /**
     * 通过UserId 和 订单编号查找是否属于跟单
     * @param orderCode
     * @param userId
     * @return
     */
    int findByOrderCodeAndUserId(@Param("orderCode") String orderCode, @Param("userId") Integer userId);

}