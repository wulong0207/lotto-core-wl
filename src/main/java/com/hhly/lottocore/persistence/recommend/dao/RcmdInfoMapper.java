package com.hhly.lottocore.persistence.recommend.dao;

import com.hhly.lottocore.persistence.recommend.po.RcmdInfoPO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdInfoBO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdInfoDetailBO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdQueryDetailBO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdQueryVO;
import org.apache.ibatis.annotations.Param;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdSingleVO;

import java.util.List;

public interface RcmdInfoMapper {


    int insert(RcmdInfoBO record);

    Integer queryRunningRcmdCount(Integer userId);

    List queryRcmdQueryDetailList(RcmdQueryVO rcmdQueryVO);

    int queryRcmdQueryDetailListCount(RcmdQueryVO rcmdQueryVO);

    RcmdInfoDetailBO findRcmdInfoByRcmdCode(String rcmdCode);

    List<RcmdQueryDetailBO> queryPersonRcmdList(RcmdSingleVO rcmdSingleVO);

    int queryPersonRcmdCount(RcmdSingleVO rcmdSingleVO);


    List<RcmdQueryDetailBO> queryPayRcmdList(RcmdSingleVO rcmdSingleVO);

    int queryPayRcmdCount(RcmdSingleVO rcmdSingleVO);

    int queryIsRcmdMatch(@Param("userId") Integer userId,@Param("screens") String screens);

    int queryTodayRcmdCount(@Param("userId") Integer userId,@Param("passWay") Integer passWay);


    RcmdInfoDetailBO findRcmdInfoByRcmdCode(@Param("rcmdCode") String rcmdCode, @Param("userId") Integer userId);

    /**
     * 判断当前用户是否为该推单人
     * @param rcmdCode
     * @param userId
     * @return
     */
    int findRcmdInfoByRcmdCodeAndUserId(@Param("rcmdCode") String rcmdCode, @Param("userId") Integer userId);

    /**
     * 查询推单记录
     * @param rcmdCode
     * @return
     */
    RcmdInfoBO findRcmdInfoBOById(String rcmdCode);

    /**
     * 更新浏览量
     * @param rcmdInfoPO
     * @return
     */
    int updateClick(RcmdInfoPO rcmdInfoPO);

    /**
     * 通过推单用户账号名称模糊查询
     * @param rcmdQueryVO
     * @return
     */
    List queryRcmdUserLikeAccountName(RcmdQueryVO rcmdQueryVO);

    /**
     * 通过推单用户账号名称模糊查询
     * @param rcmdQueryVO
     * @return
     */
    int queryRcmdUserLikeAccountNameCount(RcmdQueryVO rcmdQueryVO);

}