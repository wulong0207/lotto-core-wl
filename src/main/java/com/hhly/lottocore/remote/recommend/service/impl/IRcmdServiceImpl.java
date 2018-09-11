package com.hhly.lottocore.remote.recommend.service.impl;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueLinkDaoMapper;
import com.hhly.lottocore.persistence.recommend.dao.RcmdInfoMapper;
import com.hhly.lottocore.persistence.recommend.dao.RcmdUserCheckMapper;
import com.hhly.lottocore.persistence.recommend.dao.RcmdUserDetailMapper;
import com.hhly.lottocore.persistence.recommend.dao.RcmdUserTypeMapper;
import com.hhly.lottocore.persistence.recommend.po.RcmdUserCheckPO;
import com.hhly.lottocore.remote.recommend.service.IRcmdService;
import com.hhly.lottocore.remote.recommend.service.RcmdInfoService;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.SportsOrderValidate;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.page.AbstractStatisticsPage;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.util.*;
import com.hhly.skeleton.lotto.base.recommend.bo.*;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdPersonVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdQueryVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdSingleVO;
import com.hhly.skeleton.lotto.base.recommend.vo.*;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import com.hhly.skeleton.user.bo.KeywordBO;
import com.hhly.skeleton.user.bo.UserInfoBO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description 
 * @Author longguoyou
 * @Date  2018/8/10 16:30
 * @Since 1.8
 */
@Service("iRcmdService")
public class IRcmdServiceImpl implements IRcmdService{

    private static Logger logger = LoggerFactory.getLogger(IRcmdServiceImpl.class);

    @Autowired
    private RcmdInfoService rcmdInfoService;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private RcmdUserCheckMapper rcmdUserCheckMapper;

    @Autowired
    private MUserIssueInfoDaoMapper mUserIssueInfoDaoMapper;

    @Autowired
    private RcmdUserDetailMapper rcmdUserDetailMapper;

    @Autowired
    private MUserIssueLinkDaoMapper mUserIssueLinkDaoMapper;

    @Autowired
    private IPageService pageService;

    @Autowired
    private RcmdInfoMapper rcmdInfoMapper;

    @Value("${before_file_url}")
    protected String beforeFileUrl;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RcmdUserTypeMapper rcmdUserTypeMapper;

    @Autowired
    @Qualifier("sportsOrderValidate")
    private SportsOrderValidate sportsOrderValidate;



    @Override
    public ResultBO<?> queryRcmdInfoDetail(RcmdQueryVO rcmdQueryVO) throws Exception {
        return rcmdInfoService.queryRcmdInfoDetailPagingBO(rcmdQueryVO);
    }

    @Override
    public ResultBO<?> findRcmdInfoDetail(RcmdQueryVO rcmdQueryVO) throws Exception {
        return rcmdInfoService.getRcmdInfoDetail(rcmdQueryVO);
    }

    @Override
    public ResultBO<?> updateClick(String rcmdCode) throws Exception {
        return rcmdInfoService.updateClick(rcmdCode);
    }

    @Override
    public ResultBO<?> queryRcmdUserLikeAccountName(RcmdQueryVO rcmdQueryVO) throws Exception {
        return rcmdInfoService.queryRcmdUserLikeAccountName(rcmdQueryVO);
    }

    @Override
    public ResultBO<?> queryRecommendPersonInfo(RcmdPersonVO rcmdPersonVO) throws Exception{
        RcmdRersonInfoBO rcmdRersonInfoBO = new RcmdRersonInfoBO();
        //1.判断进哪个页面（发单人，普通用户，其他人）
        //我的推荐入口
        if(Constants.NUM_1 == rcmdPersonVO.getSeeType().intValue()){
            ResultBO<?> result = userInfoCacheService.checkNoUseToken(rcmdPersonVO.getToken());
            if(result.isError()) {
                return result;
            }
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            //2.判断是分析师还是普通用户
            int isRcmdPerson =1;
            RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfo(userInfo.getId());//查询审核表
            if(!ObjectUtil.isBlank(rcmdUserCheckBO) ){//是分析师，且审核通过
                isRcmdPerson = 2;
            }
            rcmdRersonInfoBO.setIsRcmdPerson(isRcmdPerson);
            if(isRcmdPerson == Constants.NUM_2){//发单人专有，发单人头部信息，战绩信息
                RcmdRersonInfoBO rcmdRersonInfoBO1 = mUserIssueInfoDaoMapper.queryUserIssueInfoByUserId(userInfo.getId());
                rcmdRersonInfoBO = rcmdRersonInfoBO1;
                rcmdRersonInfoBO.setIsRcmdPerson(isRcmdPerson);
                if (Base64.isBase64(rcmdUserCheckBO.getSummary())) {
                    rcmdUserCheckBO.setSummary(new String(Base64.decodeBase64(rcmdUserCheckBO.getSummary().getBytes("UTF-8"))));
                }
                rcmdRersonInfoBO.setSummary(checkKeyword(rcmdUserCheckBO.getSummary()));
                rcmdRersonInfoBO.setHeadPic(getUrl(beforeFileUrl,rcmdRersonInfoBO1.getHeadPic()));
                //3.战绩信息
                setPersonStatis(userInfo.getId(), rcmdRersonInfoBO, rcmdUserCheckBO);
            }
            //4.我的推单，已付费，我的关注，都是单独查询接口
        }else{//推荐列表进来
            if(!ObjectUtil.isBlank(rcmdPersonVO.getToken())){
                ResultBO<?> result = userInfoCacheService.checkNoUseToken(rcmdPersonVO.getToken());
                if(result.isError()) {
                    return result;
                }
                UserInfoBO userInfo = (UserInfoBO) result.getData();
                if(!ObjectUtil.isBlank(rcmdPersonVO.getUserId())){
                    if(userInfo.getId().intValue() == rcmdPersonVO.getUserId()){//登录人查看自己的推单（生成了推文，一定是分析师也就是发单人）
                        //发单人页面
                        RcmdRersonInfoBO rcmdRersonInfoBO1 = mUserIssueInfoDaoMapper.queryUserIssueInfoByUserId(rcmdPersonVO.getUserId());
                        RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfo(rcmdPersonVO.getUserId());//查询审核表
                        rcmdRersonInfoBO = rcmdRersonInfoBO1;
                        rcmdRersonInfoBO.setIsRcmdPerson(Constants.NUM_2);
                        if (Base64.isBase64(rcmdUserCheckBO.getSummary())) {
                            rcmdUserCheckBO.setSummary(new String(Base64.decodeBase64(rcmdUserCheckBO.getSummary().getBytes("UTF-8"))));
                        }
                        rcmdRersonInfoBO.setSummary(checkKeyword(rcmdUserCheckBO.getSummary()));
                        rcmdRersonInfoBO.setHeadPic(getUrl(beforeFileUrl,rcmdRersonInfoBO1.getHeadPic()));
                        //3.战绩信息
                        setPersonStatis(rcmdPersonVO.getUserId(), rcmdRersonInfoBO, rcmdUserCheckBO);
                    }
                }else{//查询其他人的推单个人主页
                    //推单人个人主页
                    setPersonInfo(rcmdPersonVO.getUserId(), rcmdRersonInfoBO);
                }
            }else{//没登录，一律进推单人主页
                //推单人个人主页
                setPersonInfo(rcmdPersonVO.getUserId(), rcmdRersonInfoBO);
            }
        }
        return ResultBO.ok(rcmdRersonInfoBO);
    }

    private void setPersonInfo(Integer userId, RcmdRersonInfoBO rcmdRersonInfoBO) throws Exception{
        RcmdRersonInfoBO rcmdRersonInfoBO1 = mUserIssueInfoDaoMapper.queryUserIssueInfoByUserId(userId);
        RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfo(userId);//查询审核表
        rcmdRersonInfoBO.setHeadPic(getUrl(beforeFileUrl,rcmdRersonInfoBO1.getHeadPic()));
        rcmdRersonInfoBO.setAccountName(rcmdRersonInfoBO1.getAccountName());
        rcmdRersonInfoBO.setIssueNum(rcmdRersonInfoBO1.getIssueNum());
        rcmdRersonInfoBO.setFanNum(rcmdRersonInfoBO1.getFanNum());
        rcmdRersonInfoBO.setIsRcmdPerson(Constants.NUM_3);
        if (Base64.isBase64(rcmdUserCheckBO.getSummary())) {
            rcmdUserCheckBO.setSummary(new String(Base64.decodeBase64(rcmdUserCheckBO.getSummary().getBytes("UTF-8"))));
        }
        rcmdRersonInfoBO.setSummary(checkKeyword(rcmdUserCheckBO.getSummary()));
        rcmdRersonInfoBO.setHeadPic(getUrl(beforeFileUrl,rcmdRersonInfoBO1.getHeadPic()));
    }

    private void setPersonStatis(Integer userId, RcmdRersonInfoBO rcmdRersonInfoBO, RcmdUserCheckBO rcmdUserCheckBO) {
        if(rcmdUserCheckBO.getIsShowRecord().intValue() == Constants.NUM_1){//展示战绩
            //把7天，15 二串一，单关四个战绩查出来
            List<RcmdPersonStatisInfo> rcmdPersonStatisInfoList = rcmdUserDetailMapper.queryPersonStatisInfo(userId);
            if(!ObjectUtil.isBlank(rcmdPersonStatisInfoList)){
                String recentStatusTrend2_17 = null;
                String recentStatusTrendSingle7 = null;
                for(RcmdPersonStatisInfo rcmdPersonStatisInfo : rcmdPersonStatisInfoList){
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_1 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_1){
                        recentStatusTrendSingle7 = rcmdPersonStatisInfo.getRecentStatusTrend();
                    }
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_1 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_2){
                        recentStatusTrend2_17 = rcmdPersonStatisInfo.getRecentStatusTrend();
                    }
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_2 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_1){
                        if(!ObjectUtil.isBlank(recentStatusTrendSingle7)){
                            rcmdPersonStatisInfo.setRecentStatusTrend(recentStatusTrendSingle7);
                        }else{
                            rcmdPersonStatisInfo.setRecentStatusTrend(null);
                        }
                        rcmdRersonInfoBO.setRcmdSingle7(rcmdPersonStatisInfo);
                    }
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_2 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_2){
                        if(!ObjectUtil.isBlank(recentStatusTrend2_17)){
                            rcmdPersonStatisInfo.setRecentStatusTrend(recentStatusTrend2_17);
                        }else{
                            rcmdPersonStatisInfo.setRecentStatusTrend(null);
                        }
                        rcmdRersonInfoBO.setRcmd2C17(rcmdPersonStatisInfo);
                    }
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_3 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_1){
                        if(!ObjectUtil.isBlank(recentStatusTrendSingle7)){
                            rcmdPersonStatisInfo.setRecentStatusTrend(recentStatusTrendSingle7);
                        }else{
                            rcmdPersonStatisInfo.setRecentStatusTrend(null);
                        }
                        rcmdRersonInfoBO.setRcmdSingle15(rcmdPersonStatisInfo);
                    }
                    if(rcmdPersonStatisInfo.getType().intValue() == Constants.NUM_3 && rcmdPersonStatisInfo.getPassWay().intValue() == Constants.NUM_2){
                        if(!ObjectUtil.isBlank(recentStatusTrend2_17)){
                            rcmdPersonStatisInfo.setRecentStatusTrend(recentStatusTrend2_17);
                        }else{
                            rcmdPersonStatisInfo.setRecentStatusTrend(null);
                        }
                        rcmdRersonInfoBO.setRcmd2C115(rcmdPersonStatisInfo);
                    }
                }
            }
        }
    }

    @Override
    public ResultBO<?> queryMyAttentionList(RcmdSingleVO rcmdSingleVO ) throws Exception {
        ResultBO<?> result = userInfoCacheService.checkNoUseToken(rcmdSingleVO.getToken());
        if(result.isError()) {
            return result;
        }
        UserInfoBO userInfo = (UserInfoBO) result.getData();
        rcmdSingleVO.setUserId(userInfo.getId());
        PagingBO<RcmdAttentionBO> pageData = pageService.getPageData(rcmdSingleVO,
                new AbstractStatisticsPage<RcmdAttentionBO>() {
                    @Override
                    public int getTotal() {
                        return  mUserIssueLinkDaoMapper.queryMyAttentionCount(rcmdSingleVO);
                    }

                    @Override
                    public List<RcmdAttentionBO> getData() {
                        List<RcmdAttentionBO> result = mUserIssueLinkDaoMapper.queryMyAttentionList(rcmdSingleVO);
                        if (!ObjectUtil.isBlank(result)) {
                            for (RcmdAttentionBO rcmdAttentionBO : result) {
                                 Integer runningCount = rcmdInfoMapper.queryRunningRcmdCount(rcmdAttentionBO.getRcmdUserId());
                                rcmdAttentionBO.setRunningRcmdCount(runningCount);
                                rcmdAttentionBO.setHeadUrl(getUrl(beforeFileUrl,rcmdAttentionBO.getHeadUrl()));
                                if (Base64.isBase64(rcmdAttentionBO.getSummary())) {
                                    try {
                                        rcmdAttentionBO.setSummary(new String(Base64.decodeBase64(rcmdAttentionBO.getSummary().getBytes("UTF-8"))));
                                    }catch (UnsupportedEncodingException e){
                                        logger.error("",e);
                                    }
                                }
                                rcmdAttentionBO.setSummary(checkKeyword(rcmdAttentionBO.getSummary()));
                            }
                        }
                        return result;
                    }

                    @Override
                    public Object getOther() {
                        return null;
                    }
                });
        return ResultBO.ok(pageData);
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
     *
     * 过滤敏感词
     * @param string
     * @return
     */
    private String checkKeyword(String string){
        if(ObjectUtil.isBlank(string)){
            return string;
        }
        List<KeywordBO> keywordBOs = redisUtil.getObj(CacheConstants.C_CORE_ACCOUNT_KEYWORD, new ArrayList<KeywordBO>());
        if(ObjectUtil.isBlank(keywordBOs)){
            return string;
        }
        for (KeywordBO keyword : keywordBOs) {
            if(ObjectUtil.isBlank(keyword) || ObjectUtil.isBlank(keyword.getKeyword())){
                continue;
            }
            if(string.indexOf(keyword.getKeyword())>=0){
                string = string.replaceAll(keyword.getKeyword(),keyword.getReplaced()==null?"":keyword.getReplaced());
            }
        }
        return string;
    }


    @Override
    public ResultBO<?> queryPersonRcmdList(RcmdSingleVO rcmdSingleVO) throws Exception {
        ResultBO<?> result = userInfoCacheService.checkToken(rcmdSingleVO.getToken());
        if(result.isError()) {
            return result;
        }
        UserInfoBO userInfo = (UserInfoBO) result.getData();
        rcmdSingleVO.setUserId(userInfo.getId());
        PagingBO<RcmdQueryDetailBO> pageData = null;
        if(rcmdSingleVO.getQueryType().intValue() == Constants.NUM_1){//我的推荐
            pageData = pageService.getPageData(rcmdSingleVO,
                     new AbstractStatisticsPage<RcmdQueryDetailBO>() {
                        @Override
                        public int getTotal() {
                            return  rcmdInfoMapper.queryPersonRcmdCount(rcmdSingleVO);
                        }

                        @Override
                        public List<RcmdQueryDetailBO> getData() {
                            return  rcmdInfoMapper.queryPersonRcmdList(rcmdSingleVO);

                        }

                        @Override
                        public Object getOther() {
                            return null;
                        }
            });
        }else if(rcmdSingleVO.getQueryType().intValue() == Constants.NUM_2 ){//已付费
            pageData = pageService.getPageData(rcmdSingleVO,
                    new AbstractStatisticsPage<RcmdQueryDetailBO>() {
                        @Override
                        public int getTotal() {
                            return  rcmdInfoMapper.queryPayRcmdCount(rcmdSingleVO);
                        }

                        @Override
                        public List<RcmdQueryDetailBO> getData() {
                            List<RcmdQueryDetailBO> rcmdQueryDetailBOs = rcmdInfoMapper.queryPayRcmdList(rcmdSingleVO);
                            if(!ObjectUtil.isBlank(rcmdQueryDetailBOs)){
                                for(RcmdQueryDetailBO rcmdQueryDetailBO : rcmdQueryDetailBOs){
                                    RcmdUserTypeBO rcmdUserTypeBO = rcmdUserTypeMapper.queryRcmdUserTypeByUserId(rcmdQueryDetailBO.getUserId());
                                    if(rcmdUserTypeBO!=null){
                                        rcmdQueryDetailBO.setLevelType(rcmdUserTypeBO.getLevel());
                                    }
                                    rcmdQueryDetailBO.setHeadUrl(getUrl(beforeFileUrl,rcmdQueryDetailBO.getHeadUrl()));
                                }
                            }
                            return rcmdQueryDetailBOs;
                        }

                        @Override
                        public Object getOther() {
                            return null;
                        }
            });
        }

        if(ObjectUtil.isBlank(pageData.getData())){
            return ResultBO.ok(pageData);
        }
        result = rcmdInfoService.commonProcess(pageData.getData(), JCZQConstants.ID_JCZQ_B);
        if(result.isError()){return result;}
        return ResultBO.ok(pageData);
    }

    @Override
    public ResultBO<?> applyRcmdPerson(RcmdUserCheckVO rcmdUserCheckVO) throws Exception {
        ResultBO<?> result = userInfoCacheService.checkToken(rcmdUserCheckVO.getToken());
        if(result.isError()) {
            return result;
        }
        UserInfoBO userInfo = (UserInfoBO) result.getData();
        RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfo(userInfo.getId());//查询审核表
        if(!ObjectUtil.isBlank(rcmdUserCheckBO) && rcmdUserCheckBO.getStatus().intValue() == Constants.NUM_3){
            return ResultBO.err(MessageCodeConstants.IS_FENXISHI);
        }
        if(rcmdUserCheckBO==null){
            RcmdUserCheckPO rcmdUserCheckPO = new RcmdUserCheckPO();
            rcmdUserCheckPO.setUserId(userInfo.getId());
            rcmdUserCheckPO.setApplySource(rcmdUserCheckVO.getApplySource());
            rcmdUserCheckPO.setLotteryCode(rcmdUserCheckVO.getLotteryCode());
            rcmdUserCheckPO.setAdeptMatch(rcmdUserCheckVO.getAdeptMatch());
            rcmdUserCheckPO.setSummary(new String(Base64.encodeBase64(rcmdUserCheckVO.getSummary().getBytes("UTF-8"))));
            rcmdUserCheckPO.setStatus(Constants.NUM_1);
            rcmdUserCheckPO.setApplyType(1);//初始化数据，默认是专家类型表的第一条 分析师
            rcmdUserCheckMapper.insert(rcmdUserCheckPO);
        }else {
            RcmdUserCheckPO rcmdUserCheckPO = new RcmdUserCheckPO();
            rcmdUserCheckPO.setId(rcmdUserCheckBO.getId());
            rcmdUserCheckPO.setUserId(userInfo.getId());
            rcmdUserCheckPO.setApplySource(rcmdUserCheckVO.getApplySource());
            rcmdUserCheckPO.setLotteryCode(rcmdUserCheckVO.getLotteryCode());
            rcmdUserCheckPO.setAdeptMatch(rcmdUserCheckVO.getAdeptMatch());
            rcmdUserCheckPO.setSummary(new String(Base64.encodeBase64(rcmdUserCheckVO.getSummary().getBytes("UTF-8"))));
            rcmdUserCheckPO.setStatus(Constants.NUM_1);
            rcmdUserCheckPO.setApplyType(1);
            rcmdUserCheckMapper.update(rcmdUserCheckPO);
        }

        return ResultBO.ok();
    }

    @Override
    public ResultBO<?> validRcmdInfo(RcmdValidVO rcmdValidVO) throws Exception {
        //公共校验
        ResultBO<?> result = userInfoCacheService.checkToken(rcmdValidVO.getToken());
        if(result.isError()) {
            return result;
        }
        UserInfoBO userInfo = (UserInfoBO) result.getData();
        //1.是否是分析师
        boolean isRcmdPerson = false;
        RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfo(userInfo.getId());//查询审核表
        if(!ObjectUtil.isBlank(rcmdUserCheckBO) ){//是分析师，且审核通过
            isRcmdPerson = true;
        }
        if(!isRcmdPerson){
            return ResultBO.err(MessageCodeConstants.RCMD_NO_EXPERTS);
        }
        if(rcmdValidVO.getPassWay().intValue() == Constants.NUM_2){//二串一
             //2.返奖率>=145%
            Double returnRate = NumberUtil.div(rcmdValidVO.getLowestBonus(),rcmdValidVO.getPlanAmount(),2);
           if(NumberUtil.compareTo(returnRate,1.45d)<0){
               return ResultBO.err(MessageCodeConstants.ORDER_MIN_PRIZE_RATE);
           }
           //3.最多两场比赛
            String matchs[] = rcmdValidVO.getScreens().split(SymbolConstants.COMMA);
           if(matchs.length>2){
               return ResultBO.err(MessageCodeConstants.MOST_TWO_MATCH);
           }
           //4.相同赛事编号不能重复推荐
           int count = rcmdInfoMapper.queryIsRcmdMatch(userInfo.getId(),rcmdValidVO.getScreens().trim());
           if(count>0){
               return ResultBO.err(MessageCodeConstants.ORDER_NOT_REPEAT);
           }
           //5.今天推荐场次是否达到限值
           Integer day = DateUtil.dayForWeek(new Date());
           Integer todayRcmdCount = rcmdInfoMapper.queryTodayRcmdCount(userInfo.getId(),Constants.NUM_2);
           if(Constants.WEEK1_5.contains(day)){//每天最多三场
                if(todayRcmdCount>=Constants.NUM_3){
                    return ResultBO.err(MessageCodeConstants.ORDER_MAX_COUNT);
                }
           }else if(Constants.WEEK6_7.contains(day)){//每天最多4场
               if(todayRcmdCount>=Constants.NUM_4){
                   return ResultBO.err(MessageCodeConstants.ORDER_MAX_COUNT);
               }
           }
           //6.销售截止时间
            List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstBeginMatch(JCZQConstants.ID_JCZQ_B, Arrays.asList(matchs));
            SportAgainstInfoBO againstInfoBO = listAgainstInfoBO.get(0);
           if(DateUtil.compare(againstInfoBO.getSaleEndTime(),new Date())<=0){
                   return ResultBO.err(MessageCodeConstants.TIME_OUT);
           }

        }else if(rcmdValidVO.getPassWay().intValue() == Constants.NUM_1){//单关
           //7.让球与非让球不能混合，前端校验
            //8.最多一场比赛
            String matchs[] = rcmdValidVO.getScreens().split(SymbolConstants.COMMA);
            if(matchs.length>1){
                return ResultBO.err(MessageCodeConstants.MOST_ONE_MATCH);
            }
            //9.不能全包，双选的赔率大于等于2.45，单选赔率必须大于等于1.45
            String sps[]=rcmdValidVO.getSps().split(SymbolConstants.COMMA);
            if(sps.length==3){
                return ResultBO.err(MessageCodeConstants.BET_ALL);
            }
            if(sps.length==2){
                Double sp1 = Double.valueOf(sps[0]);
                Double sp2 = Double.valueOf(sps[1]);
                if(NumberUtil.compareTo(sp1,2.45d)<0 || NumberUtil.compareTo(sp2,2.45d)<0){
                    return ResultBO.err(MessageCodeConstants.BET_MORE_MIN_RATE);
                }
            }
            if(sps.length==1){
                Double sp1 = Double.valueOf(sps[0]);
                if(NumberUtil.compareTo(sp1,1.45d)<0 ){
                    return ResultBO.err(MessageCodeConstants.BET_ONE_MIN_RATE);
                }
            }
            //10.不能重复推荐同场比赛
            int count = rcmdInfoMapper.queryIsRcmdMatch(userInfo.getId(),rcmdValidVO.getScreens().trim());
            if(count>0){
                return ResultBO.err(MessageCodeConstants.CANT_SAME_MATCH);
            }
            //11.方案数是否达到限值,每人不超过三单
            Integer todayRcmdCount = rcmdInfoMapper.queryTodayRcmdCount(userInfo.getId(),Constants.NUM_1);
            if(todayRcmdCount>=3){
                return ResultBO.err(MessageCodeConstants.ORDER_MAX_COUNT);
            }
            //12.销售截止时间
            List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstBeginMatch(JCZQConstants.ID_JCZQ_B, Arrays.asList(matchs));
            SportAgainstInfoBO againstInfoBO = listAgainstInfoBO.get(0);
            if(DateUtil.compare(againstInfoBO.getSaleEndTime(),new Date())<=0){
                return ResultBO.err(MessageCodeConstants.TIME_OUT);
            }

        }
        return ResultBO.ok();
    }

    @Override
    public ResultBO<?> validIsRcmdPerson(String token) throws Exception {
        ResultBO<?> result = userInfoCacheService.checkToken(token);
        if(result.isError()) {
            return result;
        }
        UserInfoBO userInfo = (UserInfoBO) result.getData();
        RcmdUserCheckBO rcmdUserCheckBO = rcmdUserCheckMapper.queryUserCheckInfoNoFilterStatus(userInfo.getId());
        if(rcmdUserCheckBO!=null){
            if(!ObjectUtil.isBlank(rcmdUserCheckBO.getSummary())){
                if (Base64.isBase64(rcmdUserCheckBO.getSummary())) {
                    rcmdUserCheckBO.setSummary(new String(Base64.decodeBase64(rcmdUserCheckBO.getSummary().getBytes("UTF-8"))));
                }
                rcmdUserCheckBO.setSummary(checkKeyword(rcmdUserCheckBO.getSummary()));
            }
        }
        return ResultBO.ok(rcmdUserCheckBO);

    }
}
