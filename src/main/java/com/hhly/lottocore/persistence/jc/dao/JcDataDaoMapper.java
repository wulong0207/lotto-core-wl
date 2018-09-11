package com.hhly.lottocore.persistence.jc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.Match;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsMeanBO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsMeanVO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.GjDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcOldDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcOldDataSpBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.MatchDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbSSSBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbWFBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbWSBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataFbWDFBO;
import com.hhly.skeleton.lotto.base.sport.vo.GjParamVO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

/**
 * @auth lgs on
 * @date 2017/2/22.
 * @desc 竞彩显示持久层
 * @compay 益彩网络科技有限公司
 * @vsersion 1.0
 */
public interface JcDataDaoMapper {

    List<JczqDaoBO> findJczqData(JcParamVO vo);

    Integer findJcSaleEndDataTotal(JcParamVO vo);

    List<JczqDaoBO> findJczqSaleEndData(JcParamVO vo);

    List<SportDataFbWDFBO> findJczqWdfSpData(@Param("sportAgainstInfoId") Long sportAgainstInfoId);

    List<MatchDataBO> findMatchTotal(JcParamVO vo);

    JczqOrderBO findJczqOrderDataBySystemCode(JcParamVO vo);

    List<JclqDaoBO> findJclqData(JcParamVO vo);

    List<JclqDaoBO> findJclqSaleEndData(JcParamVO vo);

    JclqOrderBO findJclqOrderDataBySystemCode(JcParamVO vo);
    
    List<JcOldDataSpBO> findJcOldDataSp(JcParamVO vo);

    List<SportDataBbWFBO> findJclqWfSpData(@Param("sportAgainstInfoId") Long sportAgainstInfoId);

    List<SportDataBbWSBO> findJclqWSSpData(@Param("sportAgainstInfoId") Long sportAgainstInfoId);

    List<SportDataBbSSSBO> findJclqSssSpData(@Param("sportAgainstInfoId") Long sportAgainstInfoId);
    
    List<JcOldDataBO> findJcOldData(JcParamVO vo);

    List<JcOldDataSpBO> findJcOldDataSp(@Param("issueCode") String issueCode, @Param("lotteryCode") String lotteryCode);

    List<JczqOrderBO> findJczqOrderDataByIssueCode(JcParamVO vo);

    List<BjDaoBO> findBjData(JcParamVO vo);

    BjDaoBO findBjDataBySystemCode(JcParamVO vo);

    List<BjDaoBO> findSaleEndTimeBjData(JcParamVO vo);

    BjDaoBO findBjDataByBjNum(@Param("lotteryCode") String lotteryCode, @Param("bjNum") String bjNum);

    List<MatchDataBO> findBjMatch(JcParamVO vo);

    JczqDaoBO findFootBallSpById(@Param("id") Long id);

    List<OddsFbEuropeOddsMeanBO> findAvgOddsBySourceId(OddsFbEuropeOddsMeanVO vo);

    List<OddsFbEuropeOddsBO> findOddsByEuropeId(OddsFbEuropeOddsVO vo);

    List<OddsFbEuropeOddsBO> findAvgOdds(OddsFbEuropeOddsVO vo);

    List<GjDataBO> findGjData(GjParamVO queryVO);

    Integer findGjDataCount(GjParamVO queryVO);

    List<Map<Integer,String>> getSelectTeam();

    List<JczqDaoBO> findFootBallSpBySystemCode(@Param("lotteryCode") String lotteryCode,@Param("matchs") List<Match> matchs);
}