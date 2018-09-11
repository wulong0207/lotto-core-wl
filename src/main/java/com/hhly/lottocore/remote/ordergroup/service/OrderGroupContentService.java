package com.hhly.lottocore.remote.ordergroup.service;

import com.hhly.lottocore.persistence.group.po.OrderGroupContentPO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;

/**
 * Created by longgy607 on 2018/4/28.
 */
public interface OrderGroupContentService {
    /**
     * 新增合买申购信息
     * @param orderGroupContentPO
     * @return
     * @throws Exception
     */
    ResultBO<?> addOrderGroupContent(OrderGroupContentPO orderGroupContentPO) throws Exception;

    /**
     * 跟单时 插入合买跟单表（支付对接）
     * @param orderGroupContentVO
     * @return
     * @throws Exception
     */
    ResultBO<?> insertOrderGroupContent(OrderGroupContentVO orderGroupContentVO) throws Exception;


    /**
     * 查询全部合买用户
     * @param orderGroupContentVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupContentList(OrderGroupContentVO orderGroupContentVO) throws Exception;




}
