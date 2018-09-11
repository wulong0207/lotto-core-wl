package com.hhly.lottocore.remote.lotto.service.impl;

import com.hhly.lottocore.remote.lotto.service.IFootBallAnalysisService;
import com.hhly.skeleton.base.util.HttpUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.ybf.vo.AnalysisVO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author lgs on
 * @version 1.0
 * @desc
 * @date 2017/11/10.
 * @company 益彩网络科技有限公司
 */
@Service("iFootBallAnalysisService")
public class FootBallAnalysisServiceImpl implements IFootBallAnalysisService {

    @Value("${lotto_crawl_url}")
    private String urlHead;

    /**
     * 获取两队近期交战战绩统计
     *
     * @param vo
     * @return
     */
    @Override
    public String getFootBallWarCountBO(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/war", param);
        return result;
    }

    /**
     * 获取主客队近期战绩走势
     *
     * @param vo
     * @return
     */
    @Override
    public String getFootBallRecentRecordCount(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/recent-record", param);
        return result;
    }

    /**
     * 球队历史对阵
     *
     * @param vo
     * @return
     */
    @Override
    public String getFootBallMatchAnsBO(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/history-match", param);
        return result;
    }

    /**
     * 获取球队双方排名
     *
     * @param vo
     * @return
     */
    @Override
    public String getMatchRankCount(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/match-rank", param);
        return result;
    }

    /**
     * 获取赛果统计
     *
     * @param vo
     * @return
     */
    @Override
    public String getMatchResultCount(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/result-count", param);
        return result;
    }

    /**
     * 获取赛果统计
     *
     * @param vo
     * @return
     */
    @Override
    public String getFootBallFutureMatch(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/future-match", param);
        return result;
    }


    /**
     * 足球球队最近对阵信息
     *
     * @param vo
     * @return
     */
    @Override
    public String getFootBallMatchCount(AnalysisVO vo) throws IOException, URISyntaxException {
        Map<String, String> param = ObjectUtil.objectToMapStringValue(vo);
        String result = HttpUtil.doGet(urlHead + "football-analysis/match-count", param);
        return result;
    }
}
