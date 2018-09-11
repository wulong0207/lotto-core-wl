package com.hhly.lottocore.remote.ordercopy.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.OrderFollowedInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;

/**
 * @author lgs on
 * @version 1.0
 * @desc 跟单详情service
 * @date 2017/10/9.
 * @company 益彩网络科技有限公司
 */
public interface OrderFollowedInfoService {

    /**
     * 用户进行跟单
     *
     * @param vo
     * @return
     */
    ResultBO<?> orderFollowed(OrderFollowedInfoVO vo) throws Exception;
    
    /**
     * 查询抄单明细
     * @author longguoyou
     * @date 2017年10月14日
     * @param queryVO
     * @return
     */
    ResultBO<?> queryFollowedDetails(QueryVO queryVO);
}
