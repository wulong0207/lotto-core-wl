package com.hhly.lottocore.remote.lotto.service.impl;

import static com.hhly.skeleton.base.constants.CacheConstants.C_COMM_SPORT_YBF_BASKETBALL_JISHI_BIFEN;
import static com.hhly.skeleton.base.constants.CacheConstants.FIVE_MINUTES;
import static com.hhly.skeleton.base.constants.CacheConstants.ONE_DAY;
import static com.hhly.skeleton.base.constants.CacheConstants.S_COMM_SPORT_BB_MATCH_LIST;
import static com.hhly.skeleton.base.constants.CacheConstants.S_COMM_SPORT_FB_MATCH_LIST;
import static com.hhly.skeleton.base.constants.CacheConstants.S_COMM_SPORT_FB_MATCH_LIST_SALE_END;
import static com.hhly.skeleton.base.constants.CacheConstants.TWO_HOURS;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportBBBSWsHistorySpCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportBbIssueCodeMatchListCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportBbSystemCodeMatchListCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportBbWfHistorySpCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportBbWsHistorySpCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportFbMatchHistorySpCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportFbSystemCodeMatchListCacheKey;
import static com.hhly.skeleton.base.constants.CacheConstants.getSportOldMatchListCacheKey;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.jc.dao.JcDataDaoMapper;
import com.hhly.lottocore.persistence.jc.dao.MatchDataDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.persistence.sport.dao.SportAgainstInfoDaoMapper;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.lotto.service.ILotteryIssueService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.DicEnum.PlatFormEnum;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.ConIssue;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.CopyUtil;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.NumberFormatUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.OddsUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.valid.MatchPattern;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsMeanBO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsMeanVO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsVO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottJCBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;
import com.hhly.skeleton.lotto.base.sport.bo.BasketBallScoreBO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.BjSingleUpMatchBO;
import com.hhly.skeleton.lotto.base.sport.bo.GjDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.HomeJCBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcBaseBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcMathSPBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcMathWinSPBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcOldDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcOldDataSpBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqMainDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqMobileDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqMainDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqSingleUpMatchBO;
import com.hhly.skeleton.lotto.base.sport.bo.MatchDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbSSSBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbWFBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataBbWSBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportDataFbWDFBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportScoreSpBO;
import com.hhly.skeleton.lotto.base.sport.vo.GjParamVO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

@Service("jcDataService")
public class JcDataServiceImpl implements IJcDataService {

    private static final Logger logger = Logger.getLogger(JcDataServiceImpl.class);

    @Value("${before_file_url}")
    protected String beforeFileUrl;
    @Autowired
    private JcDataDaoMapper jcDataDaoMapper;
    @Autowired
    private MatchDataDaoMapper matchDataDaoMapper;
    @Autowired
    private SportAgainstInfoDaoMapper sportAgainstInfoDaoMapper;
    @Autowired
    private LotteryIssueDaoMapper lotteryIssueDaoMapper;
    @Autowired
    private ILotteryIssueService lotteryIssueService;
    
    /**
     * 彩种数据接口
     */
    @Autowired
    private LotteryTypeDaoMapper lotteryTypeDaoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IPageService pageService;

    @Override
    public ResultBO<JczqMainDataBO> findJczqData(JcParamVO vo) {
        String cacheKey = jczqCacheKey(vo);

        ResultBO<JczqMainDataBO> bo = redisUtil.getObj(cacheKey, new ResultBO<JczqMainDataBO>());
        if (!ObjectUtil.isBlank(bo) && !ObjectUtil.isBlank(bo.getData())) {
            return bo;
        }

        vo.setMatchType(SportEnum.MatchTypeEnum.FOOTBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        if (StringUtil.isBlank(vo.getIssueCode())) {
            //如果为null 则查询所有在销售的比赛
            vo.setMatchStatus(String.valueOf(SportEnum.JcMatchStatusEnum.SALE.getCode()));
        }


        JczqMainDataBO mainDataBO = new JczqMainDataBO();
        List<MatchDataBO> matchDataBOs = jcDataDaoMapper.findMatchTotal(vo);
        mainDataBO.setSaleEndTotal(findJcSaleEndDataTotal());
        if (CollectionUtils.isEmpty(matchDataBOs)) {
            return ResultBO.ok(mainDataBO);
        }
        List<JczqDaoBO> daoBos = new ArrayList<>();
        //是否查询销售截止赛事 如果为1则查询销售截止数据
        if (!StringUtil.isBlank(vo.getIsEnd())) {
            daoBos.addAll(findJczqSaleEndData().getData().getDaoBOs());
        }
        //查询受注赛程数据
        daoBos.addAll(jcDataDaoMapper.findJczqData(vo));

        if (!CollectionUtils.isEmpty(daoBos)) {
            for (JczqDaoBO daoBO : daoBos) {
                setLogo(daoBO);
            }
        }

        mainDataBO.setDaoBOs(daoBos);
        //查询赛事信息 匹配上统计赛事对阵数量
        List<MatchDataBO> lists = matchDataDaoMapper.findMatchData(vo);
        for (MatchDataBO matchDataBO : lists) {
            for (MatchDataBO tempBo : matchDataBOs) {
                if (matchDataBO.getId().equals(tempBo.getId())) {
                    matchDataBO.setTotal(tempBo.getTotal());
                }
            }
        }
        mainDataBO.setMatchInfo(lists);

        ResultBO<JczqMainDataBO> resultBO = new ResultBO<>(mainDataBO);

        redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        return resultBO;
    }


    /**
     * 获取竞彩足球对阵 json 数据
     *
     * @param vo
     * @return
     */
    @Override
    public String getJczqDataJson(JcParamVO vo) {
        String cacheKey = jczqJsonCacheKey(vo);

        String result = redisUtil.getString(cacheKey);

        if (!StringUtil.isBlank(result)) {
            return result;
        }
        ResultBO<JczqMainDataBO> resultBO = findJczqData(vo);

        List<JczqDataBO> resultBos = null;
        JczqMainDataBO mainDataBO = resultBO.getData();
        List<JczqDaoBO> daoBos = resultBO.getData().getDaoBOs();

        //默认是胜平负和让分胜平负
        if (vo.getPlayFlag() == null || vo.getPlayFlag().length == 0 || vo.getPlayFlag().length > 3) {
            resultBos = getWdfResult(daoBos);
            mainDataBO.setResultBo(resultBos);
        } else {
            //根据参数标识获取相对应的SP值
            if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.WDF.getValue()) ||
                    JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.LET_WDF.getValue())) {
                resultBos = getWdfResult(daoBos);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.MI.getValue())) {
                resultBos = getMiResult(daoBos);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.GOAL.getValue())) {
                resultBos = getGoalResult(daoBos);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.SCORE.getValue())) {
                resultBos = getScoreResult(daoBos);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.HF.getValue())) {
                resultBos = getHfResult(daoBos);
            } else {
                resultBos = getWdfResult(daoBos);
            }
            mainDataBO.setResultBo(resultBos);

        }

        result = JsonUtil.objectToJcakJson(resultBO);
        redisUtil.addString(cacheKey, result, ONE_DAY);
        return result;
    }

    @Override
    public ResultBO<?> findGjData(GjParamVO queryVO) {
        PagingBO<GjDataBO> pageData = pageService.getPageData(queryVO, new ISimplePage<GjDataBO>() {

            @Override
            public int getTotal() {
                return jcDataDaoMapper.findGjDataCount(queryVO);
            }

            @Override
            public List<GjDataBO> getData() {
                return jcDataDaoMapper.findGjData(queryVO);
            }
        });
        List<GjDataBO> list = pageData.getData();
        logger.info("查询冠军或冠亚军展示数据列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询冠军或冠亚军展示数据列表信息：detailList=" + list.size() + " 条");
        return ResultBO.ok(list);
    }


    /**
     * 竞彩足球赛程缓存key
     *
     * @param vo
     * @return
     */
    private String jczqCacheKey(JcParamVO vo) {
        StringBuilder sb = new StringBuilder(S_COMM_SPORT_FB_MATCH_LIST);
        //缓存加上彩期
        if (!StringUtil.isBlank(vo.getIssueCode())) {
            sb.append(vo.getIssueCode());
        }
        //缓存加上是否销售截止时间
        if (!StringUtil.isBlank(vo.getIsEnd()) && vo.getIsEnd().equals("1")) {
            sb.append(SymbolConstants.UNDERLINE).append(vo.getIsEnd());
        }
        return sb.toString();
    }

    /**
     * 获取
     *
     * @param vo
     * @return
     */
    private String jczqJsonCacheKey(JcParamVO vo) {
        StringBuilder sb = new StringBuilder(jczqCacheKey(vo));
        //根据不同玩法取不同玩法对应缓存
        if (vo.getPlayFlag() == null || vo.getPlayFlag().length == 0 || vo.getPlayFlag().length > 3) {
            sb.append(SymbolConstants.UNDERLINE).append(SportEnum.SportPayFlagEnum.WDF.getValue());
        } else {
            if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.WDF.getValue()) ||
                    JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.LET_WDF.getValue())) {
                sb.append(SportEnum.SportPayFlagEnum.WDF.getValue()).append(SymbolConstants.UNDERLINE);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.MI.getValue())) {
                sb.append(SportEnum.SportPayFlagEnum.MI.getValue()).append(SymbolConstants.UNDERLINE);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.GOAL.getValue())) {
                sb.append(SportEnum.SportPayFlagEnum.GOAL.getValue()).append(SymbolConstants.UNDERLINE);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.SCORE.getValue())) {
                sb.append(SportEnum.SportPayFlagEnum.SCORE.getValue()).append(SymbolConstants.UNDERLINE);
            } else if (JCConstants.getPlayFlag(vo, SportEnum.SportPayFlagEnum.HF.getValue())) {
                sb.append(SportEnum.SportPayFlagEnum.HF.getValue()).append(SymbolConstants.UNDERLINE);
            } else {
                sb.append(SymbolConstants.UNDERLINE).append(SportEnum.SportPayFlagEnum.WDF.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * 获取胜平负和让球胜平负结果集
     *
     * @param list
     * @return
     */
    private List<JczqDataBO> getWdfResult(List<JczqDaoBO> list) {
        List<JczqDataBO> resultBos = CopyUtil.copyPropertiesList(JczqDataBO.class, list);
        for (int i = 0; resultBos != null && i < resultBos.size(); i++) {
            JczqDataBO dataBO = resultBos.get(i);
            JczqDaoBO beanBo = list.get(i);
            dataBO.setWdf(getWdf(beanBo));
        }
        return resultBos;
    }

    /***
     * 获取胜平负让球胜平负的sp值
     * @desc [胜, 平, 负, 让球数, 让胜, 让平, 让负] 里面表示对应SP值,让球数例外.
     * @param beanBo
     * @return
     */
    private String[] getWdf(JczqDaoBO beanBo) {
        String[] wdf = new String[]{NumberFormatUtil.format(beanBo.getNewestSpWin()),
                NumberFormatUtil.format(beanBo.getNewestSpDraw()),
                NumberFormatUtil.format(beanBo.getNewestSpFail()),
                NumberFormatUtil.format(beanBo.getNewestLetNum(), NumberFormatUtil.DEFAULT),
                NumberFormatUtil.format(beanBo.getNewestLetSpWin()),
                NumberFormatUtil.format(beanBo.getNewestLetSpDraw()),
                NumberFormatUtil.format(beanBo.getNewestLetSpFail())
        };

        return wdf;
    }

    /**
     * 获取混投结果集
     *
     * @param list
     * @return
     */
    private List<JczqDataBO> getMiResult(List<JczqDaoBO> list) {
        List<JczqDataBO> resultBos = CopyUtil.copyPropertiesList(JczqDataBO.class, list);
        if (!ObjectUtil.isBlank(resultBos)) {
            for (int i = 0; i < resultBos.size(); i++) {
                JczqDataBO dataBO = resultBos.get(i);
                JczqDaoBO beanBo = list.get(i);

                dataBO.setWdf(getWdf(beanBo));
                dataBO.setHf(getHF(beanBo));
                dataBO.setScore(getScoreBo(beanBo));
                dataBO.setGoal(getGoal(beanBo));
            }
        }
        return resultBos;
    }

    /**
     * 获取进球结果集
     *
     * @param list
     * @return
     */
    private List<JczqDataBO> getGoalResult(List<JczqDaoBO> list) {
        List<JczqDataBO> resultBos = CopyUtil.copyPropertiesList(JczqDataBO.class, list);
        for (int i = 0; resultBos != null && i < resultBos.size(); i++) {
            JczqDataBO dataBO = resultBos.get(i);
            JczqDaoBO beanBo = list.get(i);
            dataBO.setGoal(getGoal(beanBo));
        }
        return resultBos;
    }

    /**
     * 获取进球结果
     *
     * @param beanBo
     * @return
     * @desc [0, 1, 2, 3, 4, 5, 6, 7+] 表示总进球数 进球的sp值 其中7+表示 等7个球或者大于7个球
     */
    private String[] getGoal(JczqDaoBO beanBo) {
        String[] goal = new String[]{NumberFormatUtil.format(beanBo.getNewestSp0Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp1Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp2Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp3Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp4Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp5Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp6Goal()),
                NumberFormatUtil.format(beanBo.getNewestSp7Goal()),
        };

        return goal;
    }


    /**
     * 获取半全场胜负结果集
     *
     * @param list
     * @return
     */
    private List<JczqDataBO> getHfResult(List<JczqDaoBO> list) {
        List<JczqDataBO> resultBos = CopyUtil.copyPropertiesList(JczqDataBO.class, list);
        for (int i = 0; resultBos != null && i < resultBos.size(); i++) {
            JczqDataBO dataBO = resultBos.get(i);
            JczqDaoBO beanBo = list.get(i);
            dataBO.setHf(getHF(beanBo));
        }
        return resultBos;
    }

    /**
     * 获取半全场SP值
     * 半全场胜负SP值 w表示主胜 d表示主平 f表示主负 例如ww
     * 代表主队上半场胜下半场胜,数据格式为[ww,wd,wf,dw,dd,df,fw,fd,ff]
     *
     * @param beanBo
     * @return
     */
    private String[] getHF(JczqDaoBO beanBo) {
        String[] hf = new String[]{
                NumberFormatUtil.format(beanBo.getNewestSpWW()),
                NumberFormatUtil.format(beanBo.getNewestSpWD()),
                NumberFormatUtil.format(beanBo.getNewestSpWF()),
                NumberFormatUtil.format(beanBo.getNewestSpDW()),
                NumberFormatUtil.format(beanBo.getNewestSpDD()),
                NumberFormatUtil.format(beanBo.getNewestSpDF()),
                NumberFormatUtil.format(beanBo.getNewestSpFW()),
                NumberFormatUtil.format(beanBo.getNewestSpFD()),
                NumberFormatUtil.format(beanBo.getNewestSpFF())
        };

        return hf;
    }

    /**
     * 获取比分胜负结果集
     *
     * @param list
     * @return
     */
    private List<JczqDataBO> getScoreResult(List<JczqDaoBO> list) {
        List<JczqDataBO> resultBos = CopyUtil.copyPropertiesList(JczqDataBO.class, list);
        for (int i = 0; resultBos != null && i < resultBos.size(); i++) {
            JczqDataBO dataBO = resultBos.get(i);
            JczqDaoBO beanBo = list.get(i);
            dataBO.setScore(getScoreBo(beanBo));
        }
        return resultBos;
    }

    /**
     * 全场比分SP值
     * w:比分主队胜SP值的key value:[1:0,2:0,2:1,3:0,3:1,3:2,4:0,4:1,4:2,5:0,5:1,5:2,胜其他]
     * d:比分主队与客队平手SP值的key value:[1:1,2:2,3:3,平其他]
     * f:比分主队负SP值的key value:[0:1,0:2,1:2,0:3,1:3,2:3,0:4,1:4,2:4,0:5,1:5,2:5,负其他]
     *
     * @param beanBo
     * @return
     */
    private SportScoreSpBO getScoreBo(JczqDaoBO beanBo) {
        SportScoreSpBO bo = new SportScoreSpBO();
        bo.setW(new String[]{
                NumberFormatUtil.format(beanBo.getNewestSp10()),
                NumberFormatUtil.format(beanBo.getNewestSp20()),
                NumberFormatUtil.format(beanBo.getNewestSp21()),
                NumberFormatUtil.format(beanBo.getNewestSp30()),
                NumberFormatUtil.format(beanBo.getNewestSp31()),
                NumberFormatUtil.format(beanBo.getNewestSp32()),
                NumberFormatUtil.format(beanBo.getNewestSp40()),
                NumberFormatUtil.format(beanBo.getNewestSp41()),
                NumberFormatUtil.format(beanBo.getNewestSp42()),
                NumberFormatUtil.format(beanBo.getNewestSp50()),
                NumberFormatUtil.format(beanBo.getNewestSp51()),
                NumberFormatUtil.format(beanBo.getNewestSp52()),
                NumberFormatUtil.format(beanBo.getNewestSpWOther())
        });

        bo.setD(new String[]{
                NumberFormatUtil.format(beanBo.getNewestSp00()),
                NumberFormatUtil.format(beanBo.getNewestSp11()),
                NumberFormatUtil.format(beanBo.getNewestSp22()),
                NumberFormatUtil.format(beanBo.getNewestSp33()),
                NumberFormatUtil.format(beanBo.getNewestSpDOther())
        });

        bo.setF(new String[]{
                NumberFormatUtil.format(beanBo.getNewestSp01()),
                NumberFormatUtil.format(beanBo.getNewestSp02()),
                NumberFormatUtil.format(beanBo.getNewestSp12()),
                NumberFormatUtil.format(beanBo.getNewestSp03()),
                NumberFormatUtil.format(beanBo.getNewestSp13()),
                NumberFormatUtil.format(beanBo.getNewestSp23()),
                NumberFormatUtil.format(beanBo.getNewestSp04()),
                NumberFormatUtil.format(beanBo.getNewestSp14()),
                NumberFormatUtil.format(beanBo.getNewestSp24()),
                NumberFormatUtil.format(beanBo.getNewestSp05()),
                NumberFormatUtil.format(beanBo.getNewestSp15()),
                NumberFormatUtil.format(beanBo.getNewestSp25()),
                NumberFormatUtil.format(beanBo.getNewestSpFOther())
        });

        return bo;
    }


    @Override
    public Integer findJcSaleEndDataTotal() {
        JcParamVO vo = new JcParamVO();
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        return jcDataDaoMapper.findJcSaleEndDataTotal(vo);
    }

    @Override
    public ResultBO<JczqMainDataBO> findJczqSaleEndData() {
        ResultBO<JczqMainDataBO> cacheBO = redisUtil.getObj(S_COMM_SPORT_FB_MATCH_LIST_SALE_END, new ResultBO<JczqMainDataBO>());
        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }

        JcParamVO vo = new JcParamVO();
        vo.setMatchType(SportEnum.MatchTypeEnum.FOOTBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        JczqMainDataBO bo = new JczqMainDataBO();
        List<JczqDaoBO> daoBos = jcDataDaoMapper.findJczqSaleEndData(vo);
        bo.setDaoBOs(daoBos);
        ResultBO<JczqMainDataBO> resultBO = ResultBO.ok(bo);
        redisUtil.addObj(S_COMM_SPORT_FB_MATCH_LIST_SALE_END, resultBO, FIVE_MINUTES);
        return resultBO;
    }

    /**
     * 查询竞彩足球历史SP值
     *
     * @return
     */
    @Override
    public ResultBO<List<String[]>> findJczqWdfSpData(Long sportAgainstInfoId, Short type) {
        String cacheKey = getSportFbMatchHistorySpCacheKey(sportAgainstInfoId, type);
        ResultBO<List<String[]>> cacheBO = redisUtil.getObj(cacheKey, new ResultBO<List<String[]>>());
        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }
        List<SportDataFbWDFBO> list = jcDataDaoMapper.findJczqWdfSpData(sportAgainstInfoId);
        List<String[]> listArrays = new ArrayList<>();
        for (int i = 0; list != null && i < list.size(); i++) {
            SportDataFbWDFBO bo = list.get(i);

            if (bo.getSpType().equals(type)) {
                if (bo.getSpType().equals(SportEnum.WfTypeEnum.NOT_LET.getValue())) {
                    listArrays.add(new String[]{
                            NumberFormatUtil.format(bo.getSpWin()),
                            NumberFormatUtil.format(bo.getSpDraw()),
                            NumberFormatUtil.format(bo.getSpFail()),
                            DateUtil.convertDateToStr(bo.getReleaseTime())
                    });
                } else if (bo.getSpType().equals(SportEnum.WfTypeEnum.LET.getValue())) {
                    listArrays.add(new String[]{
                            NumberFormatUtil.format(bo.getSpWin()),
                            NumberFormatUtil.format(bo.getSpDraw()),
                            NumberFormatUtil.format(bo.getSpFail()),
                            DateUtil.convertDateToStr(bo.getReleaseTime())
                    });
                }

            }
        }
        ResultBO<List<String[]>> resultBO = new ResultBO<>(listArrays);
        redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        return resultBO;
    }


    /**
     * 根据系统编号获取竞彩订单对阵信息
     *
     * @param systemCode
     * @return
     */
    @Override
    public JczqOrderBO findJczqOrderBOBySystemCode(String systemCode) {
        String cacheKey = getSportFbSystemCodeMatchListCacheKey(systemCode);
        JczqOrderBO cacheBO = redisUtil.getObj(cacheKey, new JczqOrderBO());
        if (!ObjectUtil.isBlank(cacheBO)) {
            return cacheBO;
        }
        JcParamVO vo = new JcParamVO();
        vo.setMatchType(SportEnum.MatchTypeEnum.FOOTBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        vo.setSystemCode(systemCode);
        JczqOrderBO resultBO = jcDataDaoMapper.findJczqOrderDataBySystemCode(vo);
        if(!ObjectUtil.isBlank(resultBO)){
            resultBO.setHomeLogo(getUrl(beforeFileUrl, resultBO.getHomeLogo()));
            resultBO.setGuestLogo(getUrl(beforeFileUrl, resultBO.getGuestLogo()));
            redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        }
        return resultBO;
    }

    /**
     * 获取球队赛事url
     *
     * @param beforeFileUrl
     * @param url
     * @return
     */
    private String getUrl(String beforeFileUrl, String url) {
        if (!StringUtil.isBlank(url)) {
            if (url.contains("http")) {
                return url;
            } else {
                return beforeFileUrl + url;
            }
        }

        return null;
    }

    /**
     * 根据系统编号获取竞彩足球订单对阵信息
     *
     * @param systemCodes
     * @return
     */
    @Override
    public Map<String, JczqOrderBO> findJczqOrderBOBySystemCodes(List<String> systemCodes) {
        Map<String, JczqOrderBO> resultMap = new HashMap<>();
        for (String systemCode : systemCodes) {
            resultMap.put(systemCode, findJczqOrderBOBySystemCode(systemCode));
        }
        return resultMap;
    }

    /**
     * 查询竞彩篮球受注赛程数据以及SP值
     *
     * @param vo
     * @return
     */
    //@ReadThroughAssignCache(namespace = CacheConstants.S_COMM_SPORT_BB_MATCH_LIST, cacheType = RedisCacheType.String, serializeEnable = true, expireTime = 60 * 60 * 5)
    @Override
    public ResultBO<JclqMainDataBO> findJclqData(JcParamVO vo) {

        if (StringUtil.isBlank(vo.getIssueCode())) {
            ResultBO<JclqMainDataBO> bo = redisUtil.getObj(S_COMM_SPORT_BB_MATCH_LIST, new ResultBO<JclqMainDataBO>());
            if (!ObjectUtil.isBlank(bo) && !ObjectUtil.isBlank(bo.getData())) {
                return bo;
            }
        } else {
            ResultBO<JclqMainDataBO> bo = redisUtil.getObj(getSportBbIssueCodeMatchListCacheKey(vo.getIssueCode()), new ResultBO<JclqMainDataBO>());
            if (!ObjectUtil.isBlank(bo) && !ObjectUtil.isBlank(bo.getData())) {
                return bo;
            }
        }

        vo.setMatchType(SportEnum.MatchTypeEnum.BASKETBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.BB.getName()));

        if (StringUtil.isBlank(vo.getIssueCode())) {
            //如果为null 则查询所有在销售的比赛
            vo.setMatchStatus(String.valueOf(SportEnum.JcMatchStatusEnum.SALE.getCode()));
        }
        List<JclqDaoBO> daoBOs = new ArrayList<>();
        if (!StringUtil.isBlank(vo.getIsEnd())) {
            daoBOs.addAll(jcDataDaoMapper.findJclqSaleEndData(vo));
        }

        daoBOs.addAll(jcDataDaoMapper.findJclqData(vo));
        for (int i = 0; i < daoBOs.size(); i++) {
            JclqDaoBO jclqDaoBO = daoBOs.get(i);
            setLogo(jclqDaoBO);
            setSizeScore(jclqDaoBO);
            setLefWf(jclqDaoBO);
            jclqDaoBO.setScore(getBasketBallScoreBO(jclqDaoBO));

        }
        List<JclqDataBO> dataBOs = CopyUtil.copyPropertiesList(JclqDataBO.class, daoBOs);

        JclqMainDataBO mainDataBO = new JclqMainDataBO();
        mainDataBO.setData(dataBOs);
        mainDataBO.setMatchInfo(matchDataDaoMapper.findMatchData(vo));

        ResultBO<JclqMainDataBO> resultBO = ResultBO.ok(mainDataBO);
        if (StringUtil.isBlank(vo.getIssueCode())) {
            redisUtil.addObj(S_COMM_SPORT_BB_MATCH_LIST, resultBO, FIVE_MINUTES);
        } else {
            redisUtil.addObj(getSportBbIssueCodeMatchListCacheKey(vo.getIssueCode()), resultBO, TWO_HOURS);
        }

        return resultBO;
    }


    /**
     * 竞彩篮球json数据序列化对象
     * 减少lotto json转换压力
     *
     * @param vo
     * @return
     */
    @Override
    public String findJclqDataJson(JcParamVO vo) {

        String union = vo.toString();

        ResultBO<JclqMainDataBO> resultBO = findJclqData(vo);
        String resultJson = null;
        if (StringUtil.isBlank(vo.getChannel())) {
            resultJson = JsonUtil.objectToJcakJson(resultBO);
        } else {
            List<JclqDataBO> list = resultBO.getData().getData();
            if (CollectionUtils.isEmpty(list)) {
                return JsonUtil.objectToJcakJson(ResultBO.ok());
            }

            List<JclqMobileDataBO> result = new ArrayList<>();

            for (JclqDataBO bo : list) {
                JclqMobileDataBO mobileDataBO = new JclqMobileDataBO();
                BeanUtils.copyProperties(bo, mobileDataBO);
                String[] winArray = new String[]{
                        NumberFormatUtil.format(bo.getNewestSpWin15()),
                        NumberFormatUtil.format(bo.getNewestSpWin610()),
                        NumberFormatUtil.format(bo.getNewestSpWin1115()),
                        NumberFormatUtil.format(bo.getNewestSpWin1620()),
                        NumberFormatUtil.format(bo.getNewestSpWin2125()),
                        NumberFormatUtil.format(bo.getNewestSpWin26())
                };

                String[] failArray = new String[]{
                        NumberFormatUtil.format(bo.getNewestSpFail15()),
                        NumberFormatUtil.format(bo.getNewestSpFail610()),
                        NumberFormatUtil.format(bo.getNewestSpFail1115()),
                        NumberFormatUtil.format(bo.getNewestSpFail1620()),
                        NumberFormatUtil.format(bo.getNewestSpFail2125()),
                        NumberFormatUtil.format(bo.getNewestSpFail26())};


                mobileDataBO.setWins(winArray);
                mobileDataBO.setLosts(failArray);
                setName(mobileDataBO);
                result.add(mobileDataBO);
            }

            ResultBO<List<JclqMobileDataBO>> jsonBO = ResultBO.ok(result);
            resultJson = JsonUtil.objectToJcakJson(jsonBO);
        }
        return resultJson;
    }

    /**
     * @param bo
     * @return
     */
    private BasketBallScoreBO getBasketBallScoreBO(JclqDaoBO bo) {
        BasketBallScoreBO basketBallScoreBO = new BasketBallScoreBO();
        if (StringUtil.isBlank(bo.getFirstScore())) {
            return basketBallScoreBO;
        }

        if (!StringUtil.isBlank(bo.getFirstScore())) {
            String[] first = bo.getFirstScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethOne(Integer.valueOf(first[1]));
            basketBallScoreBO.setgOne(Integer.valueOf(first[0]));
        }

        if (!StringUtil.isBlank(bo.getSecondScore())) {
            String[] second = bo.getSecondScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethTwo(Integer.valueOf(second[1]));
            basketBallScoreBO.setgTwo(Integer.valueOf(second[0]));
        }

        if (!StringUtil.isBlank(bo.getThirdScore())) {
            String[] third = bo.getThirdScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethThree(Integer.valueOf(third[1]));
            basketBallScoreBO.setgThree(Integer.valueOf(third[0]));
        }

        if (!StringUtil.isBlank(bo.getFourthScore())) {
            String[] four = bo.getFourthScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethFour(Integer.valueOf(four[1]));
            basketBallScoreBO.setgFour(Integer.valueOf(four[0]));
        }

        if (!StringUtil.isBlank(bo.getOutTimeScore())) {
            String[] outTime = bo.getOutTimeScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethOut(Integer.valueOf(outTime[1]));
            basketBallScoreBO.setgOut(Integer.valueOf(outTime[0]));
        }

        if (!StringUtil.isBlank(bo.getFullScore())) {
            String[] full = bo.getFullScore().split(SymbolConstants.COLON);
            basketBallScoreBO.sethScore(Integer.valueOf(full[1]));
            basketBallScoreBO.setgScore(Integer.valueOf(full[0]));
        }

        int hTop = 0;
        int gTop = 0;
        int hDown = 0;
        int gDown = 0;

        if (!ObjectUtil.isBlank(basketBallScoreBO.gethOne())) {
            hTop += basketBallScoreBO.gethOne();
        }

        if (!ObjectUtil.isBlank(basketBallScoreBO.gethTwo())) {
            hTop += basketBallScoreBO.gethTwo();
        }
        if (!ObjectUtil.isBlank(basketBallScoreBO.getgOne())) {
            gTop += basketBallScoreBO.getgOne();
        }

        if (!ObjectUtil.isBlank(basketBallScoreBO.getgTwo())) {
            gTop += basketBallScoreBO.getgTwo();
        }

        if (!ObjectUtil.isBlank(basketBallScoreBO.gethThree())) {
            hDown += basketBallScoreBO.gethThree();
        }

        if (!ObjectUtil.isBlank(basketBallScoreBO.gethFour())) {
            hDown += basketBallScoreBO.gethFour();
        }


        if (!ObjectUtil.isBlank(basketBallScoreBO.getgThree())) {
            gDown += basketBallScoreBO.getgThree();
        }

        if (!ObjectUtil.isBlank(basketBallScoreBO.getgFour())) {
            gDown += basketBallScoreBO.getgFour();
        }


        if (hTop != 0) {
            basketBallScoreBO.sethTop(hTop);
        }

        if (gTop != 0) {
            basketBallScoreBO.setgTop(gTop);
        }

        if (hDown != 0) {
            basketBallScoreBO.sethDown(hDown);
        }

        if (gDown != 0) {
            basketBallScoreBO.setgDown(gDown);
        }
        return basketBallScoreBO;
    }

    /**
     * 设置大小分。
     *
     * @param jclqDaoBO
     */
    private void setSizeScore(JclqDaoBO jclqDaoBO) {
        if (!StringUtil.isBlank(jclqDaoBO.getSizeScore())) {
            String sizeScore = jclqDaoBO.getSizeScore();
            if (sizeScore.contains(SymbolConstants.COMMA)) {
                sizeScore = sizeScore.substring(sizeScore.lastIndexOf(SymbolConstants.COMMA) + 1, sizeScore.length());
            }
            jclqDaoBO.setSizeScore(sizeScore.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR)[1]);
        }
    }

    /**
     * 设置胜分差
     *
     * @param jclqDaoBO
     */
    private void setLefWf(JclqDaoBO jclqDaoBO) {
        if (!StringUtil.isBlank(jclqDaoBO.getLetWf())) {
            String letWf = jclqDaoBO.getLetWf();
            if (letWf.contains(SymbolConstants.COMMA)) {
                //取最后一个值,获取最后结果
                letWf = letWf.substring(letWf.lastIndexOf(SymbolConstants.COMMA) + 1, letWf.length());
            }
            //以|截取获取最后结果
            jclqDaoBO.setLetWf(letWf.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR)[1]);
        }
    }


    /**
     * 根据系统编号获取竞彩篮球订单对阵信息
     *
     * @param systemCode
     * @return
     */
    @Override
    public JclqOrderBO findJclqOrderBOBySystemCode(String systemCode) {
        String cacheKey = getSportBbSystemCodeMatchListCacheKey(systemCode);
        JclqOrderBO cacheBO = redisUtil.getObj(cacheKey, new JclqOrderBO());
        if (!ObjectUtil.isBlank(cacheBO)) {
            return cacheBO;
        }

        JcParamVO vo = new JcParamVO();
        vo.setMatchType(SportEnum.MatchTypeEnum.BASKETBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.BB.getName()));
        vo.setSystemCode(systemCode);
        JclqOrderBO resultBO = jcDataDaoMapper.findJclqOrderDataBySystemCode(vo);
        if(!ObjectUtil.isBlank(resultBO)){
            resultBO.setHomeLogo(getUrl(beforeFileUrl, resultBO.getHomeLogo()));
            resultBO.setGuestLogo(getUrl(beforeFileUrl, resultBO.getGuestLogo()));
            redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        }
        return resultBO;
    }

    /**
     * 查询竞彩篮球胜负和让球胜负历史SP值
     *
     * @return
     */
    //@ReadThroughAssignCache(namespace = CacheConstants.S_COMM_SPORT_BB_WF_HISTORY_SP, cacheType = RedisCacheType.String, serializeEnable = true, expireTime = 60 * 60 * 5)
    @Override
    public ResultBO<List<String[]>> findJclqWfHistorySpData(Long sportAgainstInfoId, Short type) {
        String cacheKey = getSportBbWfHistorySpCacheKey(sportAgainstInfoId, type);
        ResultBO<List<String[]>> cacheBO = redisUtil.getObj(cacheKey, new ResultBO<List<String[]>>());
        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }

        List<SportDataBbWFBO> results = jcDataDaoMapper.findJclqWfSpData(sportAgainstInfoId);
        List<String[]> listArrays = new ArrayList<>();
        for (int i = 0; results != null && i < results.size(); i++) {
            SportDataBbWFBO bo = results.get(i);
            if (bo.getSpType().equals(type)) {
                if (bo.getSpType().equals(SportEnum.WfTypeEnum.NOT_LET.getValue())) {
                    listArrays.add(new String[]{
                            NumberFormatUtil.format(bo.getSpWin()),
                            NumberFormatUtil.format(bo.getSpFail()),
                            DateUtil.convertDateToStr(bo.getReleaseTime())
                    });
                } else if (bo.getSpType().equals(SportEnum.WfTypeEnum.LET.getValue())) {
                    listArrays.add(new String[]{
                            NumberFormatUtil.format(bo.getLetScore(), NumberFormatUtil.ONE_DECIMAL_POINT),
                            NumberFormatUtil.format(bo.getSpWin()),
                            NumberFormatUtil.format(bo.getSpFail()),
                            DateUtil.convertDateToStr(bo.getReleaseTime())
                    });
                }

            }
        }
        ResultBO<List<String[]>> resultBO = ResultBO.ok(listArrays);
        redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        return resultBO;
    }

    /**
     * 查询竞彩篮球比分差历史SP值
     *
     * @return
     */
    //@ReadThroughAssignCache(namespace = CacheConstants.S_COMM_SPORT_BB_WS_HISTORY_SP, cacheType = RedisCacheType.String, serializeEnable = true, expireTime = 60 * 60 * 5)
    @Override
    public ResultBO<List<Map<String, Object>>> findJclqWSHistorySpData(Long sportAgainstInfoId) {
        String cacheKey = getSportBbWsHistorySpCacheKey(sportAgainstInfoId);
        ResultBO<List<Map<String, Object>>> cacheBO = redisUtil.getObj(cacheKey, new ResultBO<List<Map<String, Object>>>());

        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }

        List<SportDataBbWSBO> results = jcDataDaoMapper.findJclqWSSpData(sportAgainstInfoId);
        List<Map<String, Object>> resultArray = new ArrayList<>();

        for (int i = 0; results != null && i < results.size(); i++) {
            SportDataBbWSBO bo = results.get(i);

            Map<String, Object> map = new HashMap<>();
            String[] winArray = new String[]{
                    NumberFormatUtil.format(bo.getSpWin15()),
                    NumberFormatUtil.format(bo.getSpWin610()),
                    NumberFormatUtil.format(bo.getSpWin1115()),
                    NumberFormatUtil.format(bo.getSpWin1620()),
                    NumberFormatUtil.format(bo.getSpWin2125()),
                    NumberFormatUtil.format(bo.getSpWin26()),
                    DateUtil.convertDateToStr(bo.getReleaseTime())};

            String[] failArray = new String[]{
                    NumberFormatUtil.format(bo.getSpFail15()),
                    NumberFormatUtil.format(bo.getSpFail610()),
                    NumberFormatUtil.format(bo.getSpFail1115()),
                    NumberFormatUtil.format(bo.getSpFail1620()),
                    NumberFormatUtil.format(bo.getSpFail2125()),
                    NumberFormatUtil.format(bo.getSpFail26()),
                    DateUtil.convertDateToStr(bo.getReleaseTime())};
            map.put("win", winArray);
            map.put("lost", failArray);
            resultArray.add(map);
        }
        ResultBO<List<Map<String, Object>>> resultBO = ResultBO.ok(resultArray);
        redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        return resultBO;
    }


    /**
     * 查询竞彩篮球大小分历史SP值
     *
     * @return
     */
    // @ReadThroughAssignCache(namespace = CacheConstants.S_COMM_SPORT_BB_SSS_HISTORY_SP, cacheType = RedisCacheType.String, serializeEnable = true, expireTime = 60 * 60 * 5)
    @Override
    public ResultBO<List<String[]>> findJclqSssHistorySpData(Long sportAgainstInfoId) {
        String cacheKey = getSportBBBSWsHistorySpCacheKey(sportAgainstInfoId);
        ResultBO<List<String[]>> cacheBO = redisUtil.getObj(cacheKey, new ResultBO<List<String[]>>());
        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }

        List<SportDataBbSSSBO> results = jcDataDaoMapper.findJclqSssSpData(sportAgainstInfoId);
        List<String[]> listArrays = new ArrayList<>();
        for (int i = 0; results != null && i < results.size(); i++) {
            SportDataBbSSSBO bo = results.get(i);
            listArrays.add(new String[]{
                    NumberFormatUtil.format(bo.getPresetScore(), NumberFormatUtil.ONE_DECIMAL_POINT),
                    NumberFormatUtil.format(bo.getSpBig()),
                    NumberFormatUtil.format(bo.getSpSmall()),
                    DateUtil.convertDateToStr(bo.getReleaseTime())
            });
        }

        ResultBO<List<String[]>> resultBO = ResultBO.ok(listArrays);
        redisUtil.addObj(cacheKey, resultBO, ONE_DAY);
        return resultBO;
    }

    /**
     * 根据老足彩彩期查询老足投注赛程数据
     *
     * @param issueCode
     * @param lotteryCode
     * @return
     */
    @Override
    public ResultBO<List<JcOldDataBO>> findJcOldData(String issueCode, String lotteryCode) {
        if (lotteryCode.equals(String.valueOf(Lottery.ZC_NINE.getName()))) {
            lotteryCode = String.valueOf(Lottery.SFC.getName());
        }
        String key = getSportOldMatchListCacheKey(Integer.valueOf(lotteryCode), issueCode);
        ResultBO<List<JcOldDataBO>> cacheBO = redisUtil.getObj(key, new ResultBO<List<JcOldDataBO>>());
        if (!ObjectUtil.isBlank(cacheBO) && !ObjectUtil.isBlank(cacheBO.getData())) {
            return cacheBO;
        }
        JcParamVO vo = new JcParamVO();
        vo.setIssueCode(issueCode);
        vo.setLotteryCode(lotteryCode);
        List<JcOldDataBO> result = jcDataDaoMapper.findJcOldData(vo);
        List<JcOldDataSpBO> spResult = jcDataDaoMapper.findJcOldDataSp(vo);


        for (JcOldDataBO dataBo : result) {
            setName(dataBo);
            setLogo(dataBo);
            for (JcOldDataSpBO spBo : spResult) {
                if (!ObjectUtil.isBlank(dataBo.getOfficialId()) && !ObjectUtil.isBlank(spBo.getOfficialId()) && dataBo.getOfficialId().equals(spBo.getOfficialId())) {
                    dataBo.setSpWin(spBo.getSpWin());
                    dataBo.setSpDraw(spBo.getSpDraw());
                    dataBo.setSpFail(spBo.getSpFail());
                }
            }
        }

        ResultBO<List<JcOldDataBO>> resultBO = ResultBO.ok(result);
        redisUtil.addObj(key, resultBO, ONE_DAY);
        return resultBO;
    }


    @Override
    public ResultBO<HomeJCBO> findHomeSportMatchInfo(Integer lotteryCode) {
        List<IssueLottJCBO> issueList = lotteryIssueDaoMapper.findIssueByCode(lotteryCode);
        if (issueList.isEmpty()) {
            throw new ResultJsonException(ResultBO.err("10008"));
        }
        List<JcOldDataBO> jcMatchList = null;
        for (IssueLottJCBO issueBO : issueList) {
            if (issueBO.getCurrentIssue().equals(ConIssue.CURRENT.getValue())) {
                jcMatchList = findJcOldData(issueBO.getIssueCode(), lotteryCode.toString()).getData();
                break;
            }
        }
        LotteryBO curLottery = null;
        LotteryIssueBaseBO issueBo=	lotteryIssueService.findLotteryIssueBase(lotteryCode,PlatFormEnum.WEB.getValue()).getData();
        if(issueBo!=null){
        	curLottery=issueBo.getCurLottery();
        }
        curLottery.setLotteryLogoUrl(beforeFileUrl + curLottery.getLotteryLogoUrl());
        return ResultBO.ok(new HomeJCBO(issueList, jcMatchList, curLottery));
    }


    /**
     * 根据彩期获取竞彩订单对阵信息
     *
     * @param issueCode
     * @return
     */
    @Override
    public ResultBO<List<JczqOrderBO>> findJczqOrderDataByIssueCode(String issueCode) {
        JcParamVO vo = new JcParamVO();
        vo.setMatchType(SportEnum.MatchTypeEnum.FOOTBALL.getCode());
        vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        vo.setIssueCode(issueCode);
        return ResultBO.ok(jcDataDaoMapper.findJczqOrderDataByIssueCode(vo));
    }

    @Override
    public String findJcTrendSP(String type) {
        if (type.equals(String.valueOf(SportEnum.MatchTypeEnum.FOOTBALL.getCode()))) {
            String result = redisUtil.getString(CacheConstants.S_COMM_SPORT_FB_TREND_SP);
            if (StringUtil.isBlank(result)) {
                return "{}";
            }
            return result;
        } else if ((type.equals(String.valueOf(SportEnum.MatchTypeEnum.BASKETBALL.getCode())))) {
            String result = redisUtil.getString(CacheConstants.S_COMM_SPORT_BB_TREND_SP);
            if (StringUtil.isBlank(result)) {
                return "{}";
            }
            return result;
        }
        return "{}";
    }

    /**
     * 查询快投单场致胜信息
     *
     * @param lotteryCode
     * @param issueCode
     * @param saleEndTime
     * @return
     */
    public JcMathWinSPBO findSportFBMatchDCZSInfo(long id, int lotteryCode, String issueCode, Date saleEndTime, Date startTime, String systemCode) {
        String key = CacheConstants.getFBMatchSpCacheKey(lotteryCode, systemCode, saleEndTime);
        JcMathWinSPBO jcMathWinSPBO = (JcMathWinSPBO) redisUtil.getObj(key);
        if (jcMathWinSPBO != null) {
            if (DateUtil.isOver(new Timestamp(jcMathWinSPBO.getSaleEndTime().getTime()))) {
                //时间过去清理缓存
                jcMathWinSPBO = null;
            }
        }
        if (jcMathWinSPBO == null || Objects.equals(systemCode, jcMathWinSPBO.getSystemCode())) {
            //查询单场至胜时间相同情况下配对赛事信息（首页接口）
            JcMathWinSPBO jcMathWinSPBOTime = sportAgainstInfoDaoMapper.findSportFBMatchDCZSLatelyTime(id, lotteryCode, issueCode, startTime, systemCode);
            jcMathWinSPBO = sportAgainstInfoDaoMapper.findSportFBMatchDCZSInfo(lotteryCode, issueCode, jcMathWinSPBOTime.getStartTime(), systemCode);
            redisUtil.addObj(key, jcMathWinSPBO, (long) Constants.DAY_1);
        }
        if (!StringUtil.isBlank(jcMathWinSPBO.getHomeLogoUrl())) {
            jcMathWinSPBO.setHomeLogoUrl(beforeFileUrl + jcMathWinSPBO.getHomeLogoUrl());
        }
        if (!StringUtil.isBlank(jcMathWinSPBO.getVisitiLogoUrl())) {
            jcMathWinSPBO.setVisitiLogoUrl(beforeFileUrl + jcMathWinSPBO.getVisitiLogoUrl());
        }


        return jcMathWinSPBO;
    }

    /**
     * 查询竞彩足球对阵信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<JcMathSPBO> findSportMatchFBSPInfo(int lotteryCode, String issueCode, String queryDate) {
    	String key = CacheConstants.getFBMatchSpCacheKey(lotteryCode, queryDate);
        List<JcMathSPBO> list = (List<JcMathSPBO>) redisUtil.getObj(key);
        if (list != null) {
            //删除过期数据
            Iterator<JcMathSPBO> it = list.iterator();
            while (it.hasNext()) {
                JcMathSPBO bo = it.next();
                if (DateUtil.isOver(new Timestamp(bo.getSaleEndTime().getTime()))) {
                    it.remove();
                }
            }
        }
        if (list == null || list.size() == 0) {
            list = sportAgainstInfoDaoMapper.findSportMatchFBSPInfo(lotteryCode, issueCode, queryDate);
            redisUtil.addObj(key, list, (long) Constants.DAY_1);
        }
        for (JcMathSPBO bo : list) {
            if (!StringUtil.isBlank(bo.getHomeLogoUrl()) && !bo.getHomeLogoUrl().contains("http"))
                bo.setHomeLogoUrl(beforeFileUrl + bo.getHomeLogoUrl());
            if (!StringUtil.isBlank(bo.getVisitiLogoUrl()) && !bo.getVisitiLogoUrl().contains("http"))
                bo.setVisitiLogoUrl(beforeFileUrl + bo.getVisitiLogoUrl());
            bo.setNewestSpDraw(NumberFormatUtil.format(bo.getNewestSpDraw()));
            bo.setNewestSpWin(NumberFormatUtil.format(bo.getNewestSpWin()));
            bo.setNewestSpFail(NumberFormatUtil.format(bo.getNewestSpFail()));
        }
        return list;
    }

    /**
     * 查询竞彩篮球对阵信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<JcMathSPBO> findSportMatchBBSPInfo(int lotteryCode, String issueCode, String queryDate) {
        String key = CacheConstants.getBBMatchSpCacheKey(lotteryCode, queryDate) ;
        List<JcMathSPBO> list = (List<JcMathSPBO>) redisUtil.getObj(key);
        if (list != null) {
            //删除过期数据
            Iterator<JcMathSPBO> it = list.iterator();
            while (it.hasNext()) {
                JcMathSPBO bo = it.next();
                if (DateUtil.isOver(new Timestamp(bo.getSaleEndTime().getTime()))) {
                    it.remove();
                }
            }
        }
        if (list == null || list.size() == 0) {
            list = sportAgainstInfoDaoMapper.findSportMatchBBSPInfo(lotteryCode, issueCode, queryDate);
            redisUtil.addObj(key, list, (long) Constants.DAY_1);
        }
        for (JcMathSPBO bo : list) {
            if (!StringUtil.isBlank(bo.getHomeLogoUrl()) && !bo.getHomeLogoUrl().contains("http"))
                bo.setHomeLogoUrl(beforeFileUrl + bo.getHomeLogoUrl());
            if (!StringUtil.isBlank(bo.getVisitiLogoUrl()) && !bo.getVisitiLogoUrl().contains("http"))
                bo.setVisitiLogoUrl(beforeFileUrl + bo.getVisitiLogoUrl());
            bo.setNewestSpDraw(NumberFormatUtil.format(bo.getNewestSpDraw()));
            bo.setNewestSpWin(NumberFormatUtil.format(bo.getNewestSpWin()));
            bo.setNewestSpFail(NumberFormatUtil.format(bo.getNewestSpFail()));
        }
        return list;
    }


    /**
     * 单式上传对阵
     *
     * @param vo
     * @return 单式上传对阵json字符串
     */
    @Override
    public String findSingleUpMatchData(JcParamVO vo) {
        String cacheKey = CacheConstants.S_COMM_SPORT_FB_MATCH_LIST_SINGLE_UP_MATCH_DATA;
        String result = redisUtil.getString(cacheKey);

        if (!StringUtil.isBlank(result)) {
            return result;
        }
        ResultBO<JczqMainDataBO> resultBO = findJczqData(vo);
        List<JczqDaoBO> list = resultBO.getData().getDaoBOs();
        if (CollectionUtils.isEmpty(list)) {
            return JsonUtil.objectToJcakJson(ResultBO.ok());
        }
        List<JczqSingleUpMatchBO> singleUpMatchBOS = CopyUtil.copyPropertiesList(JczqSingleUpMatchBO.class, list);
        ResultBO singleUpResultBO = ResultBO.ok(singleUpMatchBOS);
        result = JsonUtil.objectToJcakJson(singleUpResultBO);
        redisUtil.addString(cacheKey, result, CacheConstants.FIVE_MINUTES);
        return result;
    }

    /**
     * 根据周几001 或者1001 获取对应对象
     *
     * @param officialCode
     * @return 对应对象
     */
    @Override
    public JczqDaoBO findSingleUpMatchDataByOfficialCode(String officialCode) {

        if (officialCode.matches(MatchPattern.NUM)) {
            String weekNum = officialCode.substring(0, 1);
            String weekCode = officialCode.substring(1, officialCode.length());
            officialCode = JCConstants.weekTxtMap.get(weekNum) + weekCode;
        }

        String cacheKey = CacheConstants.S_COMM_SPORT_FB_MATCH_LIST_OFFICIAL_CODE_MAP;
        Map<String, JczqDaoBO> map = redisUtil.getObj(cacheKey, new HashMap<String, JczqDaoBO>());
        JczqDaoBO result = null;

        try {
            String officialCodeMd5Value = DigestUtils.md5DigestAsHex(officialCode.getBytes("UTF-8"));

            if (map != null && !map.isEmpty()) {
                result = map.get(officialCodeMd5Value);
                return result;
            }

            ResultBO<JczqMainDataBO> resultBO = findJczqData(new JcParamVO());
            List<JczqDaoBO> list = resultBO.getData().getDaoBOs();
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }


            map = new HashMap<>();

            for (JczqDaoBO bo : list) {
                String code = bo.getOfficialMatchCode();
                String key = DigestUtils.md5DigestAsHex(code.getBytes("UTF-8"));
                map.put(key, bo);
            }
            result = map.get(officialCodeMd5Value);
            redisUtil.addObj(cacheKey, map, CacheConstants.ONE_DAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<BjDaoBO> findBjDataBO(JcParamVO vo) {
        logger.debug("JcParamVO:::" + vo.toString());
        if (JCConstants.getPlayFlag(vo, String.valueOf(Lottery.SFGG.getName()))) {
            vo.setLotteryCode(String.valueOf(Lottery.SFGG.getName()));
        } else {
            vo.setLotteryCode(String.valueOf(Lottery.BJDC.getName()));
        }
        List<BjDaoBO> daoBOS = new ArrayList<>();
        if (!ObjectUtil.isBlank(vo.getIsEnd()) && ObjectUtil.isBlank(vo.getIssueCode())) {
            daoBOS.addAll(jcDataDaoMapper.findSaleEndTimeBjData(vo));
        }
        daoBOS.addAll(jcDataDaoMapper.findBjData(vo));

        return daoBOS;
    }

    /**
     * 根据北京单场系统编号获取北京单场数据
     *
     * @param systemCode
     * @return
     */
    @Override
    public BjDaoBO findBjDataBOBySystemCode(String systemCode, String lotteryCode) {
        JcParamVO vo = new JcParamVO();
        vo.setSystemCode(systemCode);
        vo.setLotteryCode(lotteryCode);
        return jcDataDaoMapper.findBjDataBySystemCode(vo);
    }


    /**
     * 查询北单数据，返回已经缓存的json字符串
     *
     * @param vo
     * @return
     */
    @Override
    public String findBjMainData(JcParamVO vo) {
        List<BjDaoBO> daoBOS = findBjDataBO(vo);
        List<BjDataBO> resultBOs = new ArrayList<>();
        if (JCConstants.getPlayFlag(vo, String.valueOf(Lottery.SFGG.getName()))) {
            for (BjDaoBO bo : daoBOS) {

                if (ObjectUtil.isBlank(bo.getStatusLetWf()) || bo.getStatusLetWf().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] sfs = new String[]{
                        !ObjectUtil.isBlank(bo.getLetScore()) && bo.getLetScore() > 0 ? "+" + NumberFormatUtil.formatMaximum(bo.getLetScore(), Constants.NUM_1) : NumberFormatUtil.formatMaximum(bo.getLetScore(), Constants.NUM_1),
                        NumberFormatUtil.format(bo.getSpWinWf()),
                        NumberFormatUtil.format(bo.getSpFailWf())
                };

                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setSfs(sfs);
                resultBOs.add(dataBO);
            }
        } else if (JCConstants.getPlayFlag(vo, String.valueOf(LotteryChildEnum.LotteryChild.ID_BD_FBCQ.getValue()))) {
            for (BjDaoBO bo : daoBOS) {
                if (ObjectUtil.isBlank(bo.getStatusHfWdf()) || bo.getStatusHfWdf().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] hfWdf = new String[]{
                        NumberFormatUtil.format(bo.getSpWW()),
                        NumberFormatUtil.format(bo.getSpWD()),
                        NumberFormatUtil.format(bo.getSpWF()),
                        NumberFormatUtil.format(bo.getSpDW()),
                        NumberFormatUtil.format(bo.getSpDD()),
                        NumberFormatUtil.format(bo.getSpDF()),
                        NumberFormatUtil.format(bo.getSpFW()),
                        NumberFormatUtil.format(bo.getSpFD()),
                        NumberFormatUtil.format(bo.getSpFF())
                };


                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setHfwdfs(hfWdf);
                resultBOs.add(dataBO);
            }
        } else if (JCConstants.getPlayFlag(vo, String.valueOf(LotteryChildEnum.LotteryChild.ID_BD_FZJQ.getValue()))) {
            for (BjDaoBO bo : daoBOS) {

                if (ObjectUtil.isBlank(bo.getStatusGoal()) || bo.getStatusGoal().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] goals = new String[]{
                        NumberFormatUtil.format(bo.getSp0Goal()),
                        NumberFormatUtil.format(bo.getSp1Goal()),
                        NumberFormatUtil.format(bo.getSp2Goal()),
                        NumberFormatUtil.format(bo.getSp3Goal()),
                        NumberFormatUtil.format(bo.getSp4Goal()),
                        NumberFormatUtil.format(bo.getSp5Goal()),
                        NumberFormatUtil.format(bo.getSp6Goal()),
                        NumberFormatUtil.format(bo.getSp7Goal())
                };

                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setGoals(goals);
                resultBOs.add(dataBO);
            }
        } else if (JCConstants.getPlayFlag(vo, String.valueOf(LotteryChildEnum.LotteryChild.ID_BD_FBF.getValue()))) {
            for (BjDaoBO bo : daoBOS) {

                if (ObjectUtil.isBlank(bo.getStatusScore()) || bo.getStatusScore().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] wins = new String[]{
                        NumberFormatUtil.format(bo.getSp10()),
                        NumberFormatUtil.format(bo.getSp20()),
                        NumberFormatUtil.format(bo.getSp21()),
                        NumberFormatUtil.format(bo.getSp30()),
                        NumberFormatUtil.format(bo.getSp31()),
                        NumberFormatUtil.format(bo.getSp32()),
                        NumberFormatUtil.format(bo.getSp40()),
                        NumberFormatUtil.format(bo.getSp41()),
                        NumberFormatUtil.format(bo.getSp42()),
                        NumberFormatUtil.format(bo.getSpWOther())
                };

                String[] draws = new String[]{
                        NumberFormatUtil.format(bo.getSp00()),
                        NumberFormatUtil.format(bo.getSp11()),
                        NumberFormatUtil.format(bo.getSp22()),
                        NumberFormatUtil.format(bo.getSp33()),
                        NumberFormatUtil.format(bo.getSpDOther())
                };

                String[] losts = new String[]{
                        NumberFormatUtil.format(bo.getSp01()),
                        NumberFormatUtil.format(bo.getSp02()),
                        NumberFormatUtil.format(bo.getSp12()),
                        NumberFormatUtil.format(bo.getSp03()),
                        NumberFormatUtil.format(bo.getSp13()),
                        NumberFormatUtil.format(bo.getSp23()),
                        NumberFormatUtil.format(bo.getSp04()),
                        NumberFormatUtil.format(bo.getSp14()),
                        NumberFormatUtil.format(bo.getSp24()),
                        NumberFormatUtil.format(bo.getSpFOther())
                };

                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setWins(wins);
                dataBO.setDraws(draws);
                dataBO.setLosts(losts);
                resultBOs.add(dataBO);
            }
        } else if (JCConstants.getPlayFlag(vo, String.valueOf(LotteryChildEnum.LotteryChild.ID_BD_SXDX.getValue()))) {
            for (BjDaoBO bo : daoBOS) {

                if (ObjectUtil.isBlank(bo.getStatusUdSd()) || bo.getStatusUdSd().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] uds = new String[]{
                        NumberFormatUtil.format(bo.getSpUpSingle()),
                        NumberFormatUtil.format(bo.getSpUpDouble()),
                        NumberFormatUtil.format(bo.getSpDownSingle()),
                        NumberFormatUtil.format(bo.getSpDownDouble())
                };

                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setUds(uds);
                resultBOs.add(dataBO);
            }
        } else {
            for (BjDaoBO bo : daoBOS) {

                if (ObjectUtil.isBlank(bo.getStatusLetWdf()) || bo.getStatusLetWdf().equals(SportEnum.SportBJSaleStatusEnum.STOP_SALE.getValue())) {
                    continue;
                }

                BjDataBO dataBO = new BjDataBO();
                BeanUtils.copyProperties(bo, dataBO);
                String[] letWdf = new String[]{
                        !ObjectUtil.isBlank(bo.getLetNum()) && bo.getLetNum() > 0 ? "+" + NumberFormatUtil.formatMaximum(bo.getLetNum(), Constants.NUM_0) : NumberFormatUtil.formatMaximum(bo.getLetNum(), Constants.NUM_0),
                        NumberFormatUtil.format(bo.getSpWin()),
                        NumberFormatUtil.format(bo.getSpDraw()),
                        NumberFormatUtil.format(bo.getSpFail())
                };

                setStatus(dataBO);
                //设置简称全称
                if (!ObjectUtil.isBlank(vo.getChannel()) && vo.getChannel().equals("1")) {

                } else {
                    setName(dataBO);
                }
                setLogo(dataBO);
                dataBO.setWdfs(letWdf);
                resultBOs.add(dataBO);
            }
        }
        String resultJson = JsonUtil.objectToJcakJson(ResultBO.ok(resultBOs));
        return resultJson;
    }


    /**
     * 查询北单比赛赛事筛选框
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<List<MatchDataBO>> findBjMatch(JcParamVO vo) {
        if (ObjectUtil.isBlank(vo.getLotteryCode()) || !vo.getLotteryCode().equals(String.valueOf(Lottery.BJDC.getName())) || !vo.getLotteryCode().equals(String.valueOf(Lottery.SFGG.getName()))) {
            ResultBO.ok();
        }
        return ResultBO.ok(jcDataDaoMapper.findBjMatch(vo));
    }


    /**
     * 查询北单单式上传数据
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<List<BjSingleUpMatchBO>> findBjSingleData(JcParamVO vo) {
        List<BjDaoBO> bjDaoBOS = findBjDataBO(vo);
        List<BjSingleUpMatchBO> resultList = new ArrayList<>();
        for (BjDaoBO bjDaoBO : bjDaoBOS) {
            BjSingleUpMatchBO bo = new BjSingleUpMatchBO();
            setName(bjDaoBO);
            BeanUtils.copyProperties(bjDaoBO, bo);
            resultList.add(bo);
        }
        return ResultBO.ok(resultList);
    }


    /**
     * 根据北单赛事编号。查询北单单式上传数据
     *
     * @param bjNum
     * @param lotteryCode
     * @return
     */
    @Override
    public BjDaoBO findBjSingleDataByBjNum(String bjNum, String lotteryCode) {
        return jcDataDaoMapper.findBjDataByBjNum(lotteryCode, bjNum);
    }

    /**
     * 设置BO去除简称
     *
     * @param bo
     */
    private void setName(JcBaseBO bo) {
        if (StringUtil.isBlank(bo.getHomeShortName())) {
            bo.setHomeShortName(bo.getHomeFullName());
        }

        if (StringUtil.isBlank(bo.getGuestShortName())) {
            bo.setGuestShortName(bo.getGuestFullName());
        }

        if (StringUtil.isBlank(bo.getMatchShortName())) {
            bo.setMatchShortName(bo.getMatchFullName());
        }

        bo.setGuestFullName(null);
        bo.setHomeFullName(null);
        bo.setMatchFullName(null);
    }

    /**
     * 设置球队logo
     *
     * @param bo
     */
    private void setLogo(JcBaseBO bo) {
        if (!StringUtil.isBlank(bo.getHomeLogo())) {
            bo.setHomeLogo(beforeFileUrl + bo.getHomeLogo());
        }
        if (!StringUtil.isBlank(bo.getGuestLogo())) {
            bo.setGuestLogo(beforeFileUrl + bo.getGuestLogo());
        }
        if (!StringUtil.isBlank(bo.getMatchLogo())) {
            bo.setMatchLogo(beforeFileUrl + bo.getMatchLogo());
        }
    }

    /**
     * 设置玩法状态为null 前端不需要
     *
     * @param bo
     */
    private void setStatus(BjDataBO bo) {
        bo.setStatusGoal(null);
        bo.setStatusUdSd(null);
        bo.setStatusScore(null);
        bo.setStatusHfWdf(null);
        bo.setStatusLetWf(null);
        bo.setStatusLetWdf(null);
    }


    /**
     * 查询竞彩足球sp值
     *
     * @param id
     * @return
     */
    @Override
    public String findFootBallSpById(Long id) {
        if (ObjectUtil.isBlank(id)) {
            return JsonUtil.objectToJcakJson(ResultBO.err());
        }
        JczqDaoBO spBO = jcDataDaoMapper.findFootBallSpById(id);
        if (ObjectUtil.isBlank(spBO)) {
            return JsonUtil.objectToJcakJson(ResultBO.err());
        }
        JczqDataBO resultBO = new JczqDataBO();

        resultBO.setGoal(getGoal(spBO));
        resultBO.setHf(getHF(spBO));
        resultBO.setScore(getScoreBo(spBO));
        return JsonUtil.objectToJcakJson(ResultBO.ok(resultBO));
    }

    /**
     * 获取一比分即时比分数据
     *
     * @return
     */
    @Override
    public String getYbfImmediateScore() {
        return redisUtil.getString(CacheConstants.C_COMM_SPORT_YBF_JISHI_BIFEN);
    }


    /**
     * 获取正在比赛的足球比分数据
     */
    @Override
    public ResultBO<?> getYbfJishiBifen() {
        return ResultBO.ok(redisUtil.getObj(CacheConstants.C_COMM_SPORT_YBF_JISHI_BIFEN));
    }


    /**
     * 获取篮球即时比分
     *
     * @return
     */
    @Override
    public ResultBO<?> getBasketBallImmediateScore() {
        return ResultBO.ok(redisUtil.getObj(C_COMM_SPORT_YBF_BASKETBALL_JISHI_BIFEN));
    }

    /**
     * 获取
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> findAvgOddsBySourceId(OddsFbEuropeOddsMeanVO vo) {
        List<OddsFbEuropeOddsMeanBO> bos = jcDataDaoMapper.findAvgOddsBySourceId(vo);
        List<String[]> list = new ArrayList<>();

        for (int i = 0; i < bos.size(); i++) {
            OddsFbEuropeOddsMeanBO bo = bos.get(i);
            String[] temp = new String[]{
                    NumberFormatUtil.format(bo.getHomewinN()),
                    NumberFormatUtil.format(bo.getDrawN()),
                    NumberFormatUtil.format(bo.getGuestwinN()),
                    DateUtil.convertDateToStr(bo.getUpdateTime(), DateUtil.DEFAULT_FORMAT)
            };
            list.add(temp);
            if (i == (bos.size() - 1)) {
                String[] temp2 = new String[]{
                        NumberFormatUtil.format(bo.getHomewinF()),
                        NumberFormatUtil.format(bo.getDrawF()),
                        NumberFormatUtil.format(bo.getGuestwinF()),
                        DateUtil.convertDateToStr(bo.getUpdateTime(), DateUtil.DEFAULT_FORMAT)
                };
                list.add(temp2);
            }

        }

        return ResultBO.ok(list);
    }


    /**
     * 获取平均凯利
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> findAvgKellyBySourceId(OddsFbEuropeOddsMeanVO vo) {
        List<OddsFbEuropeOddsMeanBO> bos = jcDataDaoMapper.findAvgOddsBySourceId(vo);
        List<String[]> list = new ArrayList<>();

        for (int i = 0; i < bos.size(); i++) {
            OddsFbEuropeOddsMeanBO bo = bos.get(i);
            String[] temp = new String[]{
                    NumberFormatUtil.format(OddsUtil.getKelly(bo.getHomewinN(), bo.getProbabilityHN())),
                    NumberFormatUtil.format(OddsUtil.getKelly(bo.getDrawN(), bo.getProbabilityDN())),
                    NumberFormatUtil.format(OddsUtil.getKelly(bo.getGuestwinN(), bo.getProbabilityGN())),
                    DateUtil.convertDateToStr(bo.getUpdateTime(), DateUtil.DEFAULT_FORMAT)
            };
            list.add(temp);
            if (i == (bos.size() - 1)) {
                String[] temp2 = new String[]{
                        NumberFormatUtil.format(OddsUtil.getKelly(bo.getHomewinF(), bo.getProbabilityHF())),
                        NumberFormatUtil.format(OddsUtil.getKelly(bo.getDrawF(), bo.getProbabilityDF())),
                        NumberFormatUtil.format(OddsUtil.getKelly(bo.getGuestwinF(), bo.getProbabilityGF())),
                        DateUtil.convertDateToStr(bo.getUpdateTime(), DateUtil.DEFAULT_FORMAT)
                };
                list.add(temp2);
            }

        }

        return ResultBO.ok(list);
    }


    /**
     * 获取投注页面平均欧赔
     *
     * @param vo
     * @return
     */
    @Override
    public List<OddsFbEuropeOddsBO> findAvgOdds(OddsFbEuropeOddsVO vo) {
        return jcDataDaoMapper.findAvgOdds(vo);
    }


    /**
     * 获取某一个公司及时欧赔
     *
     * @param vo
     * @return
     */
    @Override
    public List<OddsFbEuropeOddsBO> findOddsByEuropeId(OddsFbEuropeOddsVO vo) {
        return jcDataDaoMapper.findOddsByEuropeId(vo);
    }


}
