package com.hhly.lottocore.remote.recommend.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdQueryDetailBO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdQueryVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

import java.util.List;

/**
 * Created by longgy607 on 2018/8/10.
 */
public interface RcmdInfoService {
    /**
     * 查询推文列表
     * @param rcmdQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryRcmdInfoDetailPagingBO(RcmdQueryVO rcmdQueryVO)throws Exception;
    /**
     * 获取推单详情
     * @param rcmdQueryVO
     */
    ResultBO<?> getRcmdInfoDetail(RcmdQueryVO rcmdQueryVO)throws Exception;

    /**
     * 处理返回数据
     * @param listRcmdQueryDetailBO
     * @param lotteryCode
     * @return
     * @throws Exception
     */
    ResultBO<?> commonProcess(List<RcmdQueryDetailBO> listRcmdQueryDetailBO, Integer lotteryCode)throws Exception;

    /**
     * 内部方法暴露给其他类使用
     * @param lotteryCode
     * @param matchs
     * @return
     * @throws Exception
     */
    SportAgainstInfoBO getFirstEndSaleSportAgainstInfoBO(Integer lotteryCode, List<String> matchs) throws Exception;

    /**
     * 更新浏览量
     * @param rcmdCode
     * @return
     * @throws Exception
     */
    ResultBO<?> updateClick(String rcmdCode)throws Exception;

    /**
     *
     * @param rcmdQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryRcmdUserLikeAccountName(RcmdQueryVO rcmdQueryVO)throws  Exception;
}
