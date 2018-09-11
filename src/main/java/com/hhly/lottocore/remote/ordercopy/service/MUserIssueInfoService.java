package com.hhly.lottocore.remote.ordercopy.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.CommissionBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;

/**
 * @author lgs on
 * @version 1.0
 * @desc 发单用户service
 * @date 2017/9/21.
 * @company 益彩网络科技有限公司
 */
public interface MUserIssueInfoService {

    /**
     * 新增发单用户
     *
     * @param vo
     * @return
     */
    int insert(MUserIssueInfoVO vo);

    /**
     * 根据用户id查询 发单信息。
     *
     * @param userId
     * @return
     */
    MUserIssueInfoBO findUserIssueInfoBoByUserId(Long userId);


    /**
     * 根据用户id查询 发单信息。
     *
     * @param id
     * @return
     */
    MUserIssueInfoBO findUserIssueInfoBoById(Long id);


    /**
     * 查询用户是否发过单
     *
     * @param userId
     * @return
     */
    Integer findUserIssueInfoCountByUserId(Long userId);

    /**
     * 专家列表/查询我的关注-发单用户信息
     * @author longguoyou
     * @date 2017年9月28日
     * @param queryVO
     * @return
     */
    ResultBO<?> queryUserIssueInfo(QueryVO queryVO);

    /**
     * 专家列表/查询我的关注-发单用户信息 总记录数
     * @author longguoyou
     * @date 2017年10月10日
     * @param queryVO
     * @return
     */
    int queryUserIssueInfoCount(QueryVO queryVO);


    /***
     * 更新用户发单信息
     * @param vo
     * @return
     */
    int updateUserIssueInfo(MUserIssueInfoVO vo);


    /**
     * 查询发单用户进7天统计信息
     *
     * @param vo
     * @return
     */
    ResultBO<?> findUserIssuePrizeCount(MUserIssueInfoVO vo);
    
    List<CommissionBO> getCommissionDetailsSumCommission(QueryVO queryVO);
}
