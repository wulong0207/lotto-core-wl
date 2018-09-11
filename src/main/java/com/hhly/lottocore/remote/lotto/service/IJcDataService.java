package com.hhly.lottocore.remote.lotto.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsMeanBO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsMeanVO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsVO;
import com.hhly.skeleton.lotto.base.sport.bo.*;
import com.hhly.skeleton.lotto.base.sport.vo.GjParamVO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author longguoyou

 * @date 2017年2月6日 下午4:01:10

 * @desc  竞彩足球前端数据展示交互服务接口
 *
 */
public interface IJcDataService {
    /**
     * 查询竞彩足球受注赛程数据
     *
     * @param vo
     * @return
     */
    ResultBO<JczqMainDataBO> findJczqData(JcParamVO vo);


    /**
     * 获取竞彩足球对阵 json 数据
     *
     * @param vo
     * @return
     */
    String getJczqDataJson(JcParamVO vo);


    ResultBO<?> findGjData(GjParamVO queryVO);


    /**
     * 查询竞彩足球历史SP值
     *
     * @return
     */
    ResultBO<List<String[]>> findJczqWdfSpData(Long sportAgainstInfoId, Short type);

    /**
     * 查询竞彩销售截止的赛程数量。
     *
     * @return
     */
    Integer findJcSaleEndDataTotal();

    /**
     * 查询竞彩足球销售截止的赛程。
     *
     * @return
     */
    ResultBO<JczqMainDataBO> findJczqSaleEndData();
    /**
     * 根据系统编号获取竞彩足球订单对阵信息
     * @param systemCode
     * @return
     */
    JczqOrderBO findJczqOrderBOBySystemCode(String systemCode);

    /**
     * 根据多个系统编号获取竞彩足球订单对阵信息
     *
     * @param systemCodes
     * @return
     */
    Map<String, JczqOrderBO> findJczqOrderBOBySystemCodes(List<String> systemCodes);

    /**
     * 查询竞彩篮球受注赛程数据以及SP值
     *
     * @param vo
     * @return
     */
    ResultBO<JclqMainDataBO> findJclqData(JcParamVO vo);


    /**
     * 根据系统编号获取竞彩篮球订单对阵信息
     *
     * @param systemCode
     * @return
     */
    JclqOrderBO findJclqOrderBOBySystemCode(String systemCode);

    /**
     * 查询竞彩篮球胜负和让球胜负历史SP值
     *
     * @return
     */
    ResultBO<List<String[]>> findJclqWfHistorySpData(Long sportAgainstInfoId, Short type);

    /**
     * 查询竞彩篮球比分差历史SP值
     *
     * @return
     */
    ResultBO<List<Map<String, Object>>> findJclqWSHistorySpData(Long sportAgainstInfoId);

    /**
     * 查询竞彩篮球大小分历史SP值
     *
     * @return
     */
    ResultBO<List<String[]>> findJclqSssHistorySpData(Long sportAgainstInfoId);


    /**
     * 竞彩篮球json数据序列化对象
     * 减少lotto json转换压力
     *
     * @param vo
     * @return
     */
    String findJclqDataJson(JcParamVO vo);


    /**
     * 根据老足彩彩期查询老足投注赛程数据
     *
     * @param issueCode
     * @param lotteryCode
     * @return
     */
    ResultBO<List<JcOldDataBO>> findJcOldData(String issueCode, String lotteryCode);
    

    /**
     * 查询比赛对阵信息+期号信息
     *
     * @param lotteryCode
     * @return
     */
    ResultBO<HomeJCBO> findHomeSportMatchInfo(Integer lotteryCode) ;


    /**
     * 根据彩期获取竞彩足球订单对阵信息
     * @param issueCode
     * @return
     */
    ResultBO<List<JczqOrderBO>> findJczqOrderDataByIssueCode(String issueCode);

    /**
     * 查看最新SP值
     *
     * @param type
     * @return
     */
    String findJcTrendSP(String type);
    /**
     * 查询快投单场致胜信息
     * @param lotteryCode
     * @param issueCode
     * @param saleEndTime
     * @return
     */
    JcMathWinSPBO findSportFBMatchDCZSInfo(long id,int lotteryCode,String issueCode,Date saleEndTime,Date startTime, String systemCode);
    /**
     * 查询竞彩足球推荐赛事信息
     * @param lotteryCode
     * @param issueCode
     * @return
     */
    List<JcMathSPBO>  findSportMatchFBSPInfo(int lotteryCode,String issueCode,String queryDate);
    /**
     * 查询竞彩蓝球推荐赛事信息
     * @param lotteryCode
     * @param issueCode
     * @return
     */
    List<JcMathSPBO>  findSportMatchBBSPInfo(int lotteryCode,String issueCode,String queryDate);

    /**
     * 单式上传对阵
     *
     * @param vo
     * @return 单式上传对阵json字符串
     */
    String findSingleUpMatchData(JcParamVO vo);

    /**
     * 根据周几001 获取对应
     *
     * @param officialCode
     * @return 单式上传对阵json字符串
     */
    JczqDaoBO findSingleUpMatchDataByOfficialCode(String officialCode);

    /**
     * 获取北京单场数据
     * @param vo
     * @return
     */
    List<BjDaoBO> findBjDataBO(JcParamVO vo);

    /**
     * 根据北京单场系统编号获取获取北京单场数据
     *
     * @param systemCode
     * @return
     */
    BjDaoBO findBjDataBOBySystemCode(String systemCode, String lotteryCode);

    /**
     * 查询北单数据，返回已经缓存的json字符串
     *
     * @param vo
     * @return
     */
    String findBjMainData(JcParamVO vo);

    /**
     * 查询北单比赛赛事筛选框
     *
     * @param vo
     * @return
     */
    ResultBO<List<MatchDataBO>> findBjMatch(JcParamVO vo);


    /**
     * 查询北单单式上传数据
     *
     * @param vo
     * @return
     */
    ResultBO<List<BjSingleUpMatchBO>> findBjSingleData(JcParamVO vo);

    /**
     * 根据北单赛事编号。查询北单单式上传数据
     *
     * @param bjNum
     * @param bjNum
     * @return
     */
    BjDaoBO findBjSingleDataByBjNum(String bjNum, String lotteryCode);

    /**
     * 查询竞彩足球sp值
     *
     * @param id
     * @return
     */
    String findFootBallSpById(Long id);

    /**
     * 获取一比分即时比分数据
     *
     * @return
     */
    String getYbfImmediateScore();

    /**
     * 获取篮球即时比分
     *
     * @return
     */
    ResultBO<?> getBasketBallImmediateScore();

    /**
     * 获取正在比赛的足球比分数据
     */
    ResultBO<?> getYbfJishiBifen();

    /**
     * 获取平均欧赔
     *
     * @param vo
     * @return
     */
    ResultBO<?> findAvgOddsBySourceId(OddsFbEuropeOddsMeanVO vo);


    /**
     * 获取平均凯利
     *
     * @param vo
     * @return
     */
    ResultBO<?> findAvgKellyBySourceId(OddsFbEuropeOddsMeanVO vo);


    /**
     * 获取投注页面平均欧赔
     *
     * @param vo
     * @return
     */
    List<OddsFbEuropeOddsBO> findAvgOdds(OddsFbEuropeOddsVO vo);

    /**
     * 获取某一个公司及时欧赔
     *
     * @param vo
     * @return
     */
    List<OddsFbEuropeOddsBO> findOddsByEuropeId(OddsFbEuropeOddsVO vo);

}
