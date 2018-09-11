package com.hhly.lottocore.persistence.sport.dao;



import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.sport.po.SportAgainstInfoPO;
import com.hhly.skeleton.lotto.base.sport.bo.JcMathSPBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcMathWinSPBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;


public interface SportAgainstInfoDaoMapper {

    int deleteByPrimaryKey(Long sportAgainstInfoId);

    int insert(SportAgainstInfoPO record);

    int updateByPrimaryKey(SportAgainstInfoPO record);

    /**
     * 根据对阵Id,和彩种编号（大彩种）查询对阵信息
     * @param systemCode
     * @param lotteryCode
     * @return
     */
    List<SportAgainstInfoBO> findSportAgainstInfoBySystemCode(@Param("systemCode")String systemCode,@Param("lotteryCode")Integer lotteryCode);
    /**
     * 
     * @Description: 根据list对阵Id,和彩种编号（大彩种）查询对阵信息
     * @param systemCodes
     * @param lotteryCode
     * @return
     * @author wuLong
     * @date 2017年3月24日 上午11:09:17
     */
    List<SportAgainstInfoBO> findSportAgainstInfoBySystemCodeS (@Param("systemCodes")List<String> systemCodes,@Param("lotteryCode")Integer lotteryCode);

    /**
     *
     * @Description: 根据list对阵Id,和彩种编号（大彩种）查询对阵信息 没有排序
     * @param systemCodes
     * @param lotteryCode
     * @return
     * @author wuLong
     * @date 2017年3月24日 上午11:09:17
     */
    List<SportAgainstInfoBO> findSportAgainstInfoBySystemCodes (@Param("systemCodes")List<String> systemCodes,@Param("lotteryCode")Integer lotteryCode);
    
    /**
     * 
     * @Description: 根据list对阵Id,和彩种编号（大彩种）查询对阵信息(用于获取最早开赛的时间)
     * @param systemCodes
     * @param lotteryCode
     * @return
     * @author wuLong
     * @date 2017年3月24日 上午11:09:17
     */
    List<SportAgainstInfoBO> findFirstBeginTimeOfSportAgainstInfo (@Param("systemCodes")List<String> systemCodes,@Param("lotteryCode")Integer lotteryCode);
    
    /**
     * 单式上传：根据参数查询对阵信息
     * @author longguoyou
     * @date 2017年6月13日
     * @param lotteryCode 彩种
     * @param issueCode  彩期
     * @param systemCodes 系统编号
     * @param officialMatchCodes 官方赛事编号
     * @return
     */
    List<SportAgainstInfoBO> findSportAgainstInfo(@Param("lotteryCode")Integer lotteryCode,@Param("issueCodes")List<String> issueCodes,@Param("systemCodes")List<String> systemCodes,@Param("officialMatchCodes")List<String> officialMatchCodes);

    /**
     * 查询竞彩篮球对阵sp信息
     * @param systemCode
     * @param lotteryCode
     * @return
     */
    List<JcMathSPBO> findSportMatchBBSPInfo(@Param("lotteryCode")Integer lotteryCode,@Param("issueCode")String issueCode,@Param("queryDate")String queryDate);
    /**
     * 查询竞彩足球对阵sp信息
     * @param systemCode
     * @param lotteryCode
     * @return
     */
    List<JcMathSPBO> findSportMatchFBSPInfo(@Param("lotteryCode")Integer lotteryCode,@Param("issueCode")String issueCode,@Param("queryDate")String queryDate);

    /**
     * 查询对阵信息
     * @param lotteryCode
     * @param issueCode
     * @param systemCode
     * @return
     */
    SportAgainstInfoBO querySportMatchInfo(@Param("lotteryCode")Integer lotteryCode,@Param("lotteryIssue")String issueCode,@Param("systemCode")String systemCode);

    /**
     * 查询单场至胜时间相同情况下配对赛事信息（首页接口）
     * @param lotteryCode
     * @param issueCode
     * @return
     */
    JcMathWinSPBO findSportFBMatchDCZSInfo(@Param("lotteryCode")Integer lotteryCode,@Param("issueCode")String issueCode,@Param("date")Date date, @Param("systemCode")String systemCode);
    /**
     * 查询单场至胜比赛时间最相近的配对赛事信息（首页接口）
     * @param lotteryCode
     * @param issueCode
     * @return
     */
    JcMathWinSPBO findSportFBMatchDCZSLatelyTime(@Param("id")long id,@Param("lotteryCode")Integer lotteryCode,@Param("issueCode")String issueCode,@Param("date")Date date, @Param("systemCode")String systemCode);
    /**
     * 根据彩种审核赛事比分
     * @param param
     * @return
     * @date 2017年5月11日下午6:15:28
     * @author cheng.chen
     */
    int updCheckScore(Map<String, String> param);

}