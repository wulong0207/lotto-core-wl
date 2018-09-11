package com.hhly.lottocore.persistence.ordercopy.dao;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.lotto.base.ordercopy.bo.*;
import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.ordercopy.po.OrderIssueInfoPO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;

public interface OrderIssueInfoDaoMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(OrderIssueInfoPO record);

    int insertSelective(OrderIssueInfoPO record);

    int findOrderIssueBOCountByOrderCode(@Param("orderCode") String orderCode);

    List<QueryBO> selectByCondition(QueryVO queryVO);
    
    int selectByConditionCount(QueryVO queryVO);
    
    List<QueryBO> selectByQueryType(QueryVO queryVO);
    
    int selectByQueryTypeCount(QueryVO queryVO);

    int updateByPrimaryKeySelective(OrderIssueInfoPO record);

    int updateByPrimaryKey(OrderIssueInfoPO record);

    int getDynamicUpdateCount();

    OrderIssueInfoBO findIssueBOById(@Param("id") Integer id);
    
    List<Map<String,String>> getOrderDetailPlanContentByOrderCode(@Param("orderCodes")List<String> listOrderCode);
    
    List<Map<Integer,Long>> getNumOfOrderIssue(@Param("userIssueIds")List<Integer> listUserIssueIds);

    OrderCopyInfoBO findOrderCopyInfoBOById(@Param("id") Long id);

    OrderIssueInfoBO findOrderIssueByOrderCode(String orderCode);

    OrderCopyViewBO queryOrderInfoStatistics(@Param("userId") Integer userId);

    int updateUserIssueInfo(OrderCopyViewBO bo);

    OrderCopyPayInfoBO getCopyOrderInfoForPay(Integer orderIssueId);
}