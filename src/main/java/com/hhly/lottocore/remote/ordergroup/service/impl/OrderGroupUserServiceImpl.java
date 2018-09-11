package com.hhly.lottocore.remote.ordergroup.service.impl;

import com.hhly.lottocore.persistence.group.dao.OrderGroupUserMapper;
import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupUserService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupPersonLatelyInfoBO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 
 * @Author longguoyou
 * @Date  2018/7/19 9:59
 * @Since 1.8
 */
@Service("orderGroupUserService")
public class OrderGroupUserServiceImpl implements OrderGroupUserService{

    private static Logger logger = LoggerFactory.getLogger(OrderGroupUserServiceImpl.class);

    @Autowired
    private OrderGroupUserMapper orderGroupUserMapper;

    @Override
    public ResultBO<?> queryGroupRankingList() throws Exception {
        List<OrderGroupPersonLatelyInfoBO> list =  orderGroupUserMapper.queryGroupRankingList();
        logger.info("查询到排行榜数据："+list.size());
        return ResultBO.ok(list);
    }

    @Override
    public ResultBO<?> queryGroupFamousList() throws Exception {
        List<String> list = orderGroupUserMapper.queryGroupFamousList();
        logger.info("查询到名人列表："+list.size());
        return ResultBO.ok(list);
    }
}
