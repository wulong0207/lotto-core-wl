package com.hhly.lottocore.remote.ordergroup.service;

import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupQueryVO;

/**
 * Created by longgy607 on 2018/4/28.
 */
public interface OrderGroupService {

    /**
     * 发起合买
     * @param orderGroupPO
     * @return
     * @throws Exception
     */
     ResultBO<?> addOrderGroup(OrderGroupPO orderGroupPO)throws Exception;

    /**
     * 查询合买列表
     * @param orderGroupQueryVO
     * @return
     * @throws Exception
     */
     ResultBO<?> queryOrderGroup(OrderGroupQueryVO orderGroupQueryVO)throws Exception;

    /**
     * 查询合买列表 V1.1
     * @param orderGroupQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupV11(OrderGroupQueryVO orderGroupQueryVO)throws Exception;

}
