package com.hhly.lottocore.remote.recommend.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.recommend.vo.*;


/**
 * @Description 新版推单服务接口
 * @Author longguoyou
 * @Date  2018/8/10 16:27
 * @Since 1.8
 */
public interface IRcmdService {

    ResultBO<?> queryRcmdInfoDetail(RcmdQueryVO rcmdQueryVO) throws Exception;
    /**
     * 推单主页
     * @param rcmdPersonVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryRecommendPersonInfo(RcmdPersonVO rcmdPersonVO) throws Exception;

    /**
     * 我的关注列表
     * @param rcmdSingleVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryMyAttentionList(RcmdSingleVO rcmdSingleVO) throws Exception;

    /**
     * 我的推单/已付费单
     * @param rcmdSingleVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryPersonRcmdList(RcmdSingleVO rcmdSingleVO) throws Exception;

    /**
     * 申请成为发单人
     * @param rcmdUserCheckVO
     * @return
     * @throws Exception
     */
    ResultBO<?> applyRcmdPerson(RcmdUserCheckVO rcmdUserCheckVO) throws Exception;

    /**
     * 推文校验
     * @param rcmdValidVO
     * @return
     * @throws Exception
     */
    ResultBO<?> validRcmdInfo(RcmdValidVO rcmdValidVO)throws Exception;

    /**
     * 推文详情
     * @param rcmdQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> findRcmdInfoDetail(RcmdQueryVO rcmdQueryVO)throws Exception;

    /**
     * 更新浏览量
     * @param rcmdCode
     * @return
     * @throws Exception
     */
    ResultBO<?> updateClick(String rcmdCode)throws Exception;

    /**
     * 查询推单用户
     * @param rcmdQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryRcmdUserLikeAccountName(RcmdQueryVO rcmdQueryVO)throws  Exception;

    /**
     * 校验是否是分析师
     * @param token
     * @return
     * @throws Exception
     */
    ResultBO<?> validIsRcmdPerson(String token)throws  Exception;

}
