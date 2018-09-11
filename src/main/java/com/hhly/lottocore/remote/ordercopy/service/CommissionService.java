package com.hhly.lottocore.remote.ordercopy.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.CommissionBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;

/**
 * 返佣相关服务 Service
 * @author longguoyou
 * @date 2017年10月11日
 * @compay 益彩网络科技有限公司
 */
public interface CommissionService {
       
	  /**
     * 返佣情况
     * @author longguoyou
     * @date 2017年10月11日
     * @param userId 发单用户ID
     * @return
     */
    ResultBO<?> queryCommissions(QueryVO queryVO);
    /**
     * 返佣情况 总记录数
     * @author longguoyou
     * @date 2017年10月11日
     * @return
     */
    int queryCommissionsCount();
    /**
     * 返佣明细
     * @author longguoyou
     * @date 2017年10月11日
     * @param orderCode 订单编号
     * @return
     */
    ResultBO<?> queryCommissionsDetails(QueryVO queryVO);
    
    /**
     * 返佣明细 总记录数
     * @author longguoyou
     * @date 2017年10月11日
     * @return
     */
    int queryCommissionsDetailsCount();
    /**
     * 获取提成累计金额
     * @author longguoyou
     * @date 2017年11月20日
     * @param queryVO
     * @return
     */
    List<CommissionBO> getCommissionDetailsSumCommission(QueryVO queryVO);
}
