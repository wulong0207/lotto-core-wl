package com.hhly.lottocore.remote.lotto.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsMeanBO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsMeanVO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsVO;
import com.hhly.skeleton.lotto.base.sport.bo.*;
import com.hhly.skeleton.lotto.base.sport.vo.GjParamVO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

import java.util.List;
import java.util.Map;

/**
 * @author lgs on
 * @version 1.0
 * @desc
 * @date 2017/12/13.
 * @company 益彩网络科技有限公司
 */
public interface ISportDataService {

    /**
     * 获取可以销售的竞彩足球对阵
     *
     * @param vo
     * @return
     */
    List<JczqDaoBO> findJczqData(JcParamVO vo);

    /**
     * 获取销售截止的竞彩足球对阵
     *
     * @param vo
     * @return
     */
    List<JczqDaoBO> findSaleEndJczqData(JcParamVO vo);

    /**
     * 获取销售中对阵赛事
     *
     * @param vo
     * @return
     */
    List<MatchDataBO> findMatchData(JcParamVO vo);

    /**
     * 查询受注赛程统计数量
     *
     * @param vo
     * @return
     */
    List<MatchDataBO> findMatchTotal(JcParamVO vo);

    /**
     * 查询销售截止场次数
     *
     * @return
     */
    Integer findJcSaleEndDataTotal(JcParamVO vo);

    /**
     * 查询竞彩足球对阵胜平负赔率历史变化
     *
     * @param sportAgainstInfoId
     * @return
     */
    List<SportDataFbWDFBO> findJczqWdfSpData(Long sportAgainstInfoId);

    /**
     * 获取竞彩篮球受注赛程
     *
     * @param vo
     * @return
     */
    List<JclqDaoBO> findJclqData(JcParamVO vo);

    /**
     * 获取竞彩篮球销售截止赛事
     *
     * @param vo
     * @return
     */
    List<JclqDaoBO> findJclqSaleEndData(JcParamVO vo);

    /**
     * 查询竞彩篮球胜平负赔率历史变化
     *
     * @param sportAgainstInfoId
     * @return
     */
    List<SportDataBbWFBO> findJclqWfHistorySpData(Long sportAgainstInfoId);

    /**
     * 查询大小分赔率历史变化
     *
     * @param sportAgainstInfoId
     */
    List<SportDataBbSSSBO> findJclqSssHistorySpData(Long sportAgainstInfoId);

    /**
     * 查询老足彩赛事
     *
     * @param vo
     * @return
     */
    List<JcOldDataBO> findJcOldData(JcParamVO vo);

    /**
     * 查询老足彩对阵赔率
     *
     * @param vo
     * @return
     */
    List<JcOldDataSpBO> findJcOldDataSp(JcParamVO vo);


    /**
     * 获取北单销售对阵
     *
     * @return
     */
    List<BjDaoBO> findBjData(JcParamVO vo);

    /**
     * 获取北单销售对阵
     *
     * @return
     */
    List<BjDaoBO> findBjDataBO(JcParamVO vo);

    /**
     * 获取北单销售截止对阵
     *
     * @return
     */
    List<BjDaoBO> findSaleEndTimeBjData(JcParamVO vo);

    /**
     * 获取北单赛事信息
     *
     * @param vo
     * @return
     */
    List<MatchDataBO> findBjMatch(JcParamVO vo);

    /**
     * 根据id查询对阵sp值
     *
     * @param id
     * @return
     */
    JczqDaoBO findFootBallSpById(Long id);

    /**
     * 获取平均欧赔
     *
     * @param vo
     * @return
     */
    List<OddsFbEuropeOddsMeanBO> findAvgOddsBySourceId(OddsFbEuropeOddsMeanVO vo);

    /**
     * 获取投注页面每个平均欧赔
     *
     * @param vo
     * @return
     */
    List<OddsFbEuropeOddsBO> findAvgOdds(OddsFbEuropeOddsVO vo);

    /**
     * 根据id获取欧赔
     *
     * @param vo
     * @return
     */
    List<OddsFbEuropeOddsBO> findOddsByEuropeId(OddsFbEuropeOddsVO vo);

    /**
     * 世界杯：前端展示数据
     * @param queryVO
     * @return
     */
    ResultBO<?> findGjData(GjParamVO queryVO);

    /**
     * 世界杯：获取筛选球队数据
     * @return
     */
    List<Map<Integer,String>> findSelectTeam();
}
