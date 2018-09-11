package com.hhly.lottocore.remote.ordergroup.service;

import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.skeleton.base.bo.ResultBO;

/**
 * @Description
 * @Author longguoyou
 * @Date  2018/7/19 9:56 
 * @Since 1.8
 */
public interface OrderGroupUserService {

    /**
     * 查询合买中奖排行榜
     * @return
     * @throws Exception
     */
    ResultBO<?> queryGroupRankingList()throws Exception;

    /**
     * 查询合买名人列表
     * @return
     * @throws Exception
     */
    ResultBO<?> queryGroupFamousList()throws Exception;
}
