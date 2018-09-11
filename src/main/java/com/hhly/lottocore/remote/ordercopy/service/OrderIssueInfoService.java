package com.hhly.lottocore.remote.ordercopy.service;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderCopyInfoBO;

import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.OrderIssueInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 抄单-实单服务Service
 * @author longguoyou
 * @date 2017年9月23日
 * @compay 益彩网络科技有限公司
 */
public interface OrderIssueInfoService {

    /**
     * 新增发单记录
     *
     * @param vo
     * @return
     */
    ResultBO<?> addOrderIssueInfo(OrderIssueInfoVO vo) throws Exception;
    /**
     * 查询实单总记录数
     * @author longguoyou
     * @date 2017年9月30日
     * @param queryVO
     * @return
     */
    int queryIssueInfoCount(QueryVO queryVO);
    /**
     * 查询实单列表
     * @author longguoyou
     * @date 2017年9月30日
     * @param queryVO
     * @return
     */
    ResultBO<?> queryIssueInfo(QueryVO queryVO);
    
    /**
     * 查询专家详情页，实单列表
     * @author longguoyou
     * @date 2017年12月8日
     * @param queryVO
     * @return
     */
    ResultBO<?> queryByQueryTypeThree(QueryVO queryVO);
    /**
     * 查询专家详情页，实单列表总记录数
     * @author longguoyou
     * @date 2017年12月8日
     * @param queryVO
     * @return
     */
    int queryByQueryTypeThreeCount(QueryVO queryVO);

    /**
     * 根据订单id查询方案详情
     *
     * @return
     */
    OrderCopyInfoBO findOrderCopyIssueInfoBOById(Long id);
    
    /**
     * 获取当天动态更新数
     * @author longguoyou
     * @date 2017年10月12日
     * @return
     */
    int getDynamicUpdateCount();

    /**
     * 验证订单是否能够抄单
     *
     * @param orderCode
     * @return
     */
    ResultBO<?> validateOrderCopy(String orderCode, UserInfoBO userInfoBO,OrderBaseInfoBO orderBaseInfoBO) throws Exception;
    
    /**
     * 获取过关方式
     * @author longguoyou
     * @date 2017年10月18日
     * @param listOrderCode
     * @return
     */
    List<Map<String,String>> getOrderDetailPlanContentByOrderCode(List<String> listOrderCode);
    
    /**
     * 获取发单用户，当前未截止可跟投的方案数量
     * @author longguoyou
     * @date 2017年10月20日
     * @param listUserIssueIds
     * @return
     */
    List<Map<Integer,Long>> getNumOfOrderIssue(List<Integer> listUserIssueIds);
}
