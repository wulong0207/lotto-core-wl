package com.hhly.lottocore.remote.recommend.service.impl;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.recommend.dao.RcmdInfoMapper;
import com.hhly.lottocore.persistence.recommend.po.RcmdInfoPO;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.ordergroup.service.impl.OrderGroupServiceImpl;
import com.hhly.lottocore.remote.recommend.service.RcmdInfoService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.cms.ordermgr.bo.OrderGroupBO;
import com.hhly.skeleton.cms.recommend.bo.RcmdMsgBO;
import com.hhly.skeleton.lotto.base.recommend.bo.*;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdQueryVO;
import com.hhly.skeleton.lotto.base.sport.bo.JcBaseBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
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

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description 
 * @Author longguoyou
 * @Date  2018/8/10 16:32
 * @Since 1.8
 */
@Service("rcmdInfoService")
public class RcmdInfoServiceImpl implements RcmdInfoService{

    private static Logger logger = LoggerFactory.getLogger(OrderGroupServiceImpl.class);

    @Autowired
    private RcmdInfoMapper rmcdInfoMapper;

    @Autowired
    private IPageService pageService;

    @Resource(name="jcDataService")
    private IJcDataService jcDataService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    private static final String DEFAULT_LET_NUM = "让球";

    @Value("${before_file_url}")
    protected String beforeFileUrl;

    @Autowired
    @Qualifier("rcmdClickMessageProvider")
    private MessageProvider messageProvider;

    @Override
    public ResultBO<?> queryRcmdInfoDetailPagingBO(RcmdQueryVO rcmdQueryVO) throws Exception {

        //判断空
        Assert.paramNotNull(rcmdQueryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(rcmdQueryVO.getPageIndex(), "pageIndex");
        Assert.paramNotNull(rcmdQueryVO.getLotteryCode(), "lotteryCode");
        Assert.paramNotNull(rcmdQueryVO.getPassway(), "passway");
        //设置排序
        //默认排序
        rcmdQueryVO.setSortField("a.is_recommend DESC,c.return_rate");
        rcmdQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        //用户操作触发
        ResultBO<?> resultBO = setOrderBy(rcmdQueryVO);
        if(resultBO.isError()){return resultBO;}
        //设值
        PagingBO<RcmdQueryDetailBO> pageData = pageService.getPageData(rcmdQueryVO,
                new ISimplePage<RcmdQueryDetailBO>() {
                    @Override
                    public int getTotal() {
                        return rmcdInfoMapper.queryRcmdQueryDetailListCount(rcmdQueryVO);
                    }

                    @Override
                    public List<RcmdQueryDetailBO> getData() {
                        return rmcdInfoMapper.queryRcmdQueryDetailList(rcmdQueryVO);
                    }
                });
        logger.info("新版推单 --> 查询推文列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("新版推单 --> 查询推文列表信息：detailList=" + pageData.getData().size() + " 条");
        resultBO = commonProcess(pageData.getData(),rcmdQueryVO.getLotteryCode());
        if(resultBO.isError()){return resultBO;}
        return ResultBO.ok(pageData);
    }

    @Override
    public ResultBO<?> getRcmdInfoDetail(RcmdQueryVO rcmdQueryVO) throws Exception {
        Integer userId = null;
        if(!ObjectUtil.isBlank(rcmdQueryVO.getToken())){
            ResultBO<?> result = userInfoCacheService.checkToken(rcmdQueryVO.getToken());
            if(result.isError()) {return result;}
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            userId = userInfo.getId();
        }
        RcmdInfoDetailBO rcmdInfoDetailBO = rmcdInfoMapper.findRcmdInfoByRcmdCode(rcmdQueryVO.getRcmdCode(),userId);
        //判断是否为发单人推单
        int flag = rmcdInfoMapper.findRcmdInfoByRcmdCodeAndUserId(rcmdQueryVO.getRcmdCode(),userId);
        //推单信息
        RcmdInfoBO rcmdInfoBO = rmcdInfoMapper.findRcmdInfoBOById(rcmdQueryVO.getRcmdCode());
        if(rcmdInfoBO.getStatus().intValue()==Constants.NUM_2){//已完成，公开
            rcmdInfoDetailBO.setLocker(Constants.NUM_0);
        }else{
            if(!ObjectUtil.isBlank(rcmdInfoBO.getPayType()) && rcmdInfoBO.getPayType() == Constants.NUM_2){//付费查看
                if(flag > Constants.NUM_0){//本人查看
                    rcmdInfoDetailBO.setLocker(Constants.NUM_0);
                }/*else{//别人查看,别人查看，使用sql里面的lock
                rcmdInfoDetailBO.setLocker(Constants.NUM_1);
            }*/
            }else{
                rcmdInfoDetailBO.setLocker(Constants.NUM_0);//免费查看
            }
        }
        if(rcmdInfoDetailBO.getLocker().intValue()==Constants.NUM_0){
            String[] contents = FormatConversionJCUtil.singleBetContentAnalysis(rcmdInfoDetailBO.getPlanContent());
            String[] betContents = FormatConversionJCUtil.betContentDetailsAnalysis(contents[0]);
            List<String> listMatchs = Arrays.asList(rcmdInfoDetailBO.getScreens().split(SymbolConstants.COMMA));
            SportAgainstInfoBO firstEndSportInfoBO = getFirstEndSaleSportAgainstInfoBO(rcmdInfoDetailBO.getLotteryCode(), listMatchs);
            if(rcmdInfoDetailBO.getLotteryCode() == JCZQConstants.ID_JCZQ_B){//足球
                List<MatchsBO> list = new ArrayList<MatchsBO>();
                basic(rcmdInfoDetailBO, betContents, firstEndSportInfoBO, list,null);
                rcmdInfoDetailBO.setListMatchsBO(list);
            }else if(rcmdInfoDetailBO.getLotteryCode() == JCLQConstants.ID_JCLQ_B){//篮球
                //TODO 竞篮，可参考竞足，能公用就用，不能用，拷贝一份basic、singleWay进行改造
            }
        }
        clearValue(rcmdInfoDetailBO);
        if (!ObjectUtil.isBlank(rcmdInfoDetailBO.getTitle())) {
            if (Base64.isBase64(rcmdInfoDetailBO.getTitle())) {
                rcmdInfoDetailBO.setTitle(new String(Base64.decodeBase64(rcmdInfoDetailBO.getTitle().getBytes("UTF-8"))));
            }
            rcmdInfoDetailBO.setTitle(checkKeyword(rcmdInfoDetailBO.getTitle()));
        }
        if (!ObjectUtil.isBlank(rcmdInfoDetailBO.getReason())) {
            if (Base64.isBase64(rcmdInfoDetailBO.getReason())) {
                rcmdInfoDetailBO.setReason(new String(Base64.decodeBase64(rcmdInfoDetailBO.getReason().getBytes("UTF-8"))));
            }
            rcmdInfoDetailBO.setReason(checkKeyword(rcmdInfoDetailBO.getReason()));
        }
        return ResultBO.ok(rcmdInfoDetailBO);
    }

    /**
     * 清空属性值
     * @param rcmdInfoDetailBO
     */
    private void clearValue(RcmdInfoDetailBO rcmdInfoDetailBO) {
        rcmdInfoDetailBO.setLotteryCode(null);
        rcmdInfoDetailBO.setLotteryChildCode(null);
        rcmdInfoDetailBO.setScreens(null);
        rcmdInfoDetailBO.setPlanContent(null);
    }

    /**
     * 足球、篮球 所有玩法处理
     * @param rcmdInfoDetailBO
     * @param betContents
     * @param firstEndSportInfoBO
     * @param list
     * @param mixSystemCode 混投解析的systemCode
     */
    private void basic(RcmdInfoDetailBO rcmdInfoDetailBO, String[] betContents, SportAgainstInfoBO firstEndSportInfoBO, List<MatchsBO> list, String mixSystemCode) {
        Set<String> systemCodeSet = new HashSet<String>();
        for(String eachMatch : betContents){
            if(eachMatch.contains(SymbolConstants.UNDERLINE)){//混合玩法
                String[] mixBetSp = eachMatch.split(SymbolConstants.UNDERLINE);
                //数组第一个元素为赛事systemCode 17089006_R[+1](1@1.23,3@3.26)_S(3@1.11)|17089007_S(3@1.11)^2_1^1
                for(int i=1;i<mixBetSp.length;i++){
                    RcmdInfoDetailBO rcmdInfo = new RcmdInfoDetailBO();
                    rcmdInfo.setLotteryCode(JCZQConstants.ID_JCZQ_B);
                    if(mixBetSp[i].contains("R")){
                        rcmdInfo.setLotteryChildCode(JCZQConstants.ID_RQS);
                    }else if(mixBetSp[i].contains("S")){
                        rcmdInfo.setLotteryChildCode(JCZQConstants.ID_JCZQ);
                    }else{
                        return;
                    }
                    singleWay(rcmdInfo,firstEndSportInfoBO, list, mixBetSp[0], mixBetSp[i],systemCodeSet);
                }
            }else{//单一玩法
                singleWay(rcmdInfoDetailBO, firstEndSportInfoBO, list, mixSystemCode, eachMatch,systemCodeSet);
            }
        }
    }

    /**
     * 足球、篮球单一玩法 处理
     * @param rcmdInfoDetailBO
     * @param firstEndSportInfoBO
     * @param list
     * @param mixSystemCode
     * @param eachMatch
     */
    private void singleWay(RcmdInfoDetailBO rcmdInfoDetailBO, SportAgainstInfoBO firstEndSportInfoBO, List<MatchsBO> list, String mixSystemCode, String eachMatch,Set<String> systemCodeSet) {
        MatchsBO matchsBO = null;
        /**list中是否已经存在与systemCode对应MatchsBO对象*/
        boolean flag = systemCodeSet.contains(mixSystemCode);
        String systemCode = ObjectUtil.isBlank(mixSystemCode)? JCConstants.getSystemCode(eachMatch,rcmdInfoDetailBO.getLotteryChildCode()):mixSystemCode;
        systemCodeSet.add(systemCode);
        List<String> matchs = Arrays.asList(systemCode);
        String betSps = FormatConversionJCUtil.singleGameBetContentSubstring(eachMatch);
        Map<String,JczqOrderBO> map = jcDataService.findJczqOrderBOBySystemCodes(matchs);//足球
        //JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);//篮球
        if(ObjectUtil.isBlank(mixSystemCode)){
            matchsBO = getMatchsBO(rcmdInfoDetailBO, firstEndSportInfoBO, matchs);
        }else{
            if(ObjectUtil.isBlank(list)){
                matchsBO = getMatchsBO(rcmdInfoDetailBO, firstEndSportInfoBO, matchs);
            }else{
                if(flag){
                    matchsBO = list.get(0);
                }else{
                    matchsBO = getMatchsBO(rcmdInfoDetailBO, firstEndSportInfoBO, matchs);
                }
            }
        }
        JczqOrderBO jczqOrderBO = map.get(systemCode);//最新赔率,补充时使用
        List<MatchSpInfo> listMatchSpInfo = new ArrayList<MatchSpInfo>();
        MatchSpInfo matchSpInfo = new MatchSpInfo();
        List<SpInfoBO> listSpInfoBO = new ArrayList<SpInfoBO>();
        if(betSps.length() == Constants.NUM_3) {//推荐某场赛事包含：胜、平、负，赔率以推荐时为准
            String[] sps = betSps.split(SymbolConstants.COMMA);//[3@1.23，1@2.35,0@3.26]
            if(rcmdInfoDetailBO.getLotteryChildCode() == JCZQConstants.ID_JCZQ){//胜平负
                setAllRcmd(listSpInfoBO, sps);
            }else if(rcmdInfoDetailBO.getLotteryChildCode() == JCZQConstants.ID_RQS){//让胜平负
                matchSpInfo.setInfo(DEFAULT_LET_NUM + JCZQConstants.getInfoFromSingleBetContent(eachMatch,rcmdInfoDetailBO.getLotteryChildCode()));
                setAllRcmd(listSpInfoBO, sps);
            }else if(rcmdInfoDetailBO.getLotteryChildCode() == JCZQConstants.ID_FHT){//混投

            }
        }else {//其它情况，需取最新陪率补充。首先全部使用最新赔率，然后，更新推荐时的赔率替换最新赔率
            if(rcmdInfoDetailBO.getLotteryChildCode() == JCZQConstants.ID_JCZQ){
                setListSpInfoBO(listSpInfoBO, jczqOrderBO,false);
                String[] sps = betSps.split(SymbolConstants.COMMA);//[3@1.23]
                for(String sp : sps){
                    String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sp);//推荐内容
                    updateSp(listSpInfoBO,betSp);
                }
            }else if(rcmdInfoDetailBO.getLotteryChildCode() == JCZQConstants.ID_RQS){
                matchSpInfo.setInfo(DEFAULT_LET_NUM + JCZQConstants.getInfoFromSingleBetContent(eachMatch,rcmdInfoDetailBO.getLotteryChildCode()));
                setListSpInfoBO(listSpInfoBO, jczqOrderBO,true);
                String[] sps = betSps.split(SymbolConstants.COMMA);//[1@2.35,0@3.26]
                for(String sp : sps){
                    String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sp);//推荐内容
                    updateSp(listSpInfoBO,betSp);
                }
            }
        }
        matchSpInfo.setListSpInfoBO(listSpInfoBO);
        if(ObjectUtil.isBlank(mixSystemCode)){
            listMatchSpInfo.add(matchSpInfo);
            matchsBO.setListMatchsSpInfo(listMatchSpInfo);
            list.add(matchsBO);
        }else{
            List<MatchSpInfo> listMatchSpInfoOld = matchsBO.getListMatchsSpInfo();
            if(ObjectUtil.isBlank(listMatchSpInfoOld)){
                listMatchSpInfoOld = new ArrayList<MatchSpInfo>();
            }
            listMatchSpInfoOld.add(matchSpInfo);
            matchsBO.setListMatchsSpInfo(listMatchSpInfoOld);
            if(!flag){
                list.add(matchsBO);
            }
        }
    }

    /**
     * 组装MatchsBO
     * @param rcmdInfoDetailBO
     * @param firstEndSportInfoBO
     * @param matchs
     * @return
     */
    private MatchsBO getMatchsBO(RcmdInfoDetailBO rcmdInfoDetailBO, SportAgainstInfoBO firstEndSportInfoBO, List<String> matchs) {
        MatchsBO matchsBO;
        matchsBO = new MatchsBO();
        SportAgainstInfoBO againstInfoBO = getFirstEndSaleSportAgainstInfoBO(rcmdInfoDetailBO.getLotteryCode(), matchs);
//        matchsBO.setWinningStatus(rcmdInfoDetailBO.getWinningStatus());//是否命中
        matchsBO.setMatchName(againstInfoBO.getMatchName());//赛事名称
        matchsBO.setOfficialMatchCode(againstInfoBO.getOfficialMatchCode());//赛事编号
        matchsBO.setHomeName(againstInfoBO.getHomeName());//主队名称
        matchsBO.setVisitName(againstInfoBO.getVisitiName());//客队名称
        matchsBO.setHomeUrl(againstInfoBO.getHomeLogo());
        matchsBO.setVisitUrl(againstInfoBO.getGuestLogo());
        matchsBO.setEndTime(DateUtil.convertDateToStr(firstEndSportInfoBO.getSaleEndTime(),DateUtil.DEFAULT_FORMAT));//截止时间
        return matchsBO;
    }

    /**
     * 全推荐的情况
     * @param listSpInfoBO
     * @param sps
     */
    private void setAllRcmd(List<SpInfoBO> listSpInfoBO, String[] sps) {
        SpInfoBO spInfoBO;
        spInfoBO = new SpInfoBO();
        String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps[0]);
        spInfoBO.setSp(Float.valueOf(betSp[1]));
        spInfoBO.setFlag(Constants.NUM_1);//胜
        listSpInfoBO.add(spInfoBO);
        spInfoBO = new SpInfoBO();
        betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps[1]);
        spInfoBO.setSp(Float.valueOf(betSp[1]));
        spInfoBO.setFlag(Constants.NUM_1);//平
        listSpInfoBO.add(spInfoBO);
        spInfoBO = new SpInfoBO();
        betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps[2]);
        spInfoBO.setSp(Float.valueOf(betSp[1]));
        spInfoBO.setFlag(Constants.NUM_1);//负
        listSpInfoBO.add(spInfoBO);
    }

    /**
     * 默认设置最新赔率，默认不是推荐项
     * @param listSpInfoBO
     * @param jczqOrderBO
     * @param flag 是否让球
     */
    private void setListSpInfoBO(List<SpInfoBO> listSpInfoBO, JczqOrderBO jczqOrderBO, boolean flag) {
        SpInfoBO spInfoBO;
        spInfoBO = new SpInfoBO();
        spInfoBO.setSp(flag?jczqOrderBO.getNewestSpWin():jczqOrderBO.getNewestLetSpWin());
        spInfoBO.setFlag(Constants.NUM_0);
        listSpInfoBO.add(spInfoBO);
        spInfoBO = new SpInfoBO();
        spInfoBO.setSp(flag?jczqOrderBO.getNewestSpDraw():jczqOrderBO.getNewestLetSpDraw());
        spInfoBO.setFlag(Constants.NUM_0);
        listSpInfoBO.add(spInfoBO);
        spInfoBO = new SpInfoBO();
        spInfoBO.setSp(flag?jczqOrderBO.getNewestSpFail():jczqOrderBO.getNewestLetSpFail());
        spInfoBO.setFlag(Constants.NUM_0);
        listSpInfoBO.add(spInfoBO);
    }

    /**
     *  更新对应的sp值为推荐时对应sp值、是否推荐
     * @param listSpInfoBO 已经按胜、平、负 顺序排好
     * @param betSp 如：[3,1.23]
     */
    private void updateSp(List<SpInfoBO> listSpInfoBO,String[] betSp) {
        if(Integer.valueOf(betSp[0]) == Constants.NUM_3){
            SpInfoBO spInfoBO = listSpInfoBO.get(0);
            spInfoBO.setSp(Float.valueOf(betSp[1]));
            spInfoBO.setFlag(Constants.NUM_1);
        }
        if(Integer.valueOf(betSp[0]) == Constants.NUM_1){
            SpInfoBO spInfoBO = listSpInfoBO.get(1);
            spInfoBO.setSp(Float.valueOf(betSp[1]));
            spInfoBO.setFlag(Constants.NUM_1);
        }
        if(Integer.valueOf(betSp[0]) == Constants.NUM_0){
            SpInfoBO spInfoBO = listSpInfoBO.get(2);
            spInfoBO.setSp(Float.valueOf(betSp[1]));
            spInfoBO.setFlag(Constants.NUM_1);
        }
    }

    public static void main(String[] args) {
        String eachMatch = "18020101(1@1.23,3@3.26)";
        String systemCode = eachMatch.substring(0,eachMatch.indexOf(SymbolConstants.PARENTHESES_LEFT));
        String sp = eachMatch.substring(eachMatch.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1,eachMatch.indexOf(SymbolConstants.PARENTHESES_RIGHT));
        List<String> matchs = Arrays.asList(systemCode);
        System.out.println(systemCode + ":"+sp + ":" + matchs);
        System.out.println("1710146021(3@2.01)|1710146022(3@2.40,1@3.15)^2_1^1".split(SymbolConstants.UP_CAP).length);
        System.out.println(eachMatch.indexOf("18020101"));
    }

    public ResultBO<?> commonProcess(List<RcmdQueryDetailBO> listRcmdQueryDetailBO, Integer lotteryCode)throws Exception{
        ResultBO<?> resultBO = null;
        for(RcmdQueryDetailBO bean : listRcmdQueryDetailBO) {
            if (!ObjectUtil.isBlank(bean.getTitle())) {
                if (Base64.isBase64(bean.getTitle())) {
                    bean.setTitle(new String(Base64.decodeBase64(bean.getTitle().getBytes("UTF-8"))));
                }
                bean.setTitle(checkKeyword(bean.getTitle()));
            }
            bean.setHeadUrl(getUrl(beforeFileUrl,bean.getHeadUrl()));

            //近期战况 7中6
            /*if(!ObjectUtil.isBlank(bean.getRecentRecord())){
                bean.setRecentRecord(bean.getRecentRecord().replace(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR,"中"));
            }*/
            //过关方式翻译
            /*if(Integer.valueOf(bean.getPassway()) == Constants.NUM_1){
               bean.setPassway("单关");
            }else if(Integer.valueOf(bean.getPassway()) == Constants.NUM_2){
                bean.setPassway("2串1");
            }*/
            resultBO = setValue(bean,lotteryCode);
            if(resultBO.isError()){return resultBO;}
        }
        return ResultBO.ok();
    }

    @Override
    public ResultBO<?> updateClick(String rcmdCode) throws Exception {
        Assert.paramNotNull(rcmdCode, "rcmdCode");
        RcmdInfoPO rcmdInfoPO = new RcmdInfoPO();
        rcmdInfoPO.setRcmdCode(rcmdCode);
        rmcdInfoMapper.updateClick(rcmdInfoPO);

        //更新浏览量
        RcmdMsgBO rcmdMsgBO = new RcmdMsgBO();
        rcmdMsgBO.setType(Constants.NUM_4);
        rcmdMsgBO.setRcmdCode(rcmdCode);
        messageProvider.sendMessage(MQConstants.RECOMMEND_ORDER_STATIS_QUEUENAME,rcmdMsgBO);
        return ResultBO.ok();
    }

    @Override
    public ResultBO<?> queryRcmdUserLikeAccountName(RcmdQueryVO rcmdQueryVO) throws Exception {
        Assert.paramNotNull(rcmdQueryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(rcmdQueryVO.getPageIndex(), "pageIndex");
        Assert.paramNotNull(rcmdQueryVO.getAccountName(), "accountName");
        if(!ObjectUtil.isBlank(rcmdQueryVO.getToken())){
            ResultBO<?> result = userInfoCacheService.checkToken(rcmdQueryVO.getToken());
            if(result.isError()) {return result;}
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            rcmdQueryVO.setUserId(userInfo.getId());
        }
        //设值
        PagingBO<RcmdAttentionBO> pageData = pageService.getPageData(rcmdQueryVO,
                new ISimplePage<RcmdAttentionBO>() {
                    @Override
                    public int getTotal() {
                        return rmcdInfoMapper.queryRcmdUserLikeAccountNameCount(rcmdQueryVO);
                    }

                    @Override
                    public List<RcmdAttentionBO> getData() {
                        return rmcdInfoMapper.queryRcmdUserLikeAccountName(rcmdQueryVO);
                    }
                });
        logger.info("专家模糊查询 --> 查询列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("专家模糊查询 --> 查询列表信息：detailList=" + pageData.getData().size() + " 条");

        for(RcmdAttentionBO bean : pageData.getData()){
            if(!ObjectUtil.isBlank(bean.getRecentRecord())){
                bean.setRecentRecord(bean.getRecentRecord().replace(SymbolConstants.VERTICAL_BAR,"中"));
            }
            if(!ObjectUtil.isBlank(bean.getSummary())){
                if (Base64.isBase64(bean.getSummary())) {
                    bean.setSummary(new String(Base64.decodeBase64(bean.getSummary().getBytes("UTF-8"))));
                }
                bean.setSummary(checkKeyword(bean.getSummary()));
            }
        }
        return ResultBO.ok(pageData);
    }

    public ResultBO<?> setValue(RcmdQueryDetailBO rcmdQueryDetailBO,Integer lotteryCode){
        String[] strs = ObjectUtil.isBlank(rcmdQueryDetailBO.getPlanContent())?null:FormatConversionJCUtil.singleBetContentAnalysis(rcmdQueryDetailBO.getPlanContent());
        if(strs.length != Constants.NUM_3){
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        //截止时间 MM-dd HH:mm
        List<String> matchs = Arrays.asList(rcmdQueryDetailBO.getScreens().split(SymbolConstants.COMMA));
        //获取最早截止的赛事对象
        //SportAgainstInfoBO againstInfoBO = getFirstEndSaleSportAgainstInfoBO(lotteryCode, matchs);
        if(rcmdQueryDetailBO.getSaleEndTime()!=null){//售卖截止时间比最早比赛时间早五分钟
            rcmdQueryDetailBO.setEndTime(DateUtil.addMinute(rcmdQueryDetailBO.getSaleEndTime(),-5));
        }
        //解析投注内容
        List<MatchsBO> listMatchsBO = getMatchsBO(lotteryCode,matchs);
        rcmdQueryDetailBO.setListMatchsBO(listMatchsBO);
        rcmdQueryDetailBO.setScreens(null);
        rcmdQueryDetailBO.setPlanContent(null);
        return ResultBO.ok();
    }

    public SportAgainstInfoBO getFirstEndSaleSportAgainstInfoBO(Integer lotteryCode, List<String> matchs) {
        LotteryEnum.Lottery lot = LotteryEnum.Lottery.getLottery(lotteryCode);
        switch (lot) {
            case FB:
                return getJczqFirstEndSportAgainstInfoBOs(matchs);
            case BB:
                return getJclqFirstEndSportAgainstInfoBOs(matchs);
            default:
                return null;
        }
    }

    public List<MatchsBO> getMatchsBO(Integer lotteryCode,List<String> matchs){
        LotteryEnum.Lottery lot = LotteryEnum.Lottery.getLottery(lotteryCode);
        switch (lot) {
            case FB:
                return getJczqMatchsBO( matchs);
            case BB:
                return getJclqMatchsBO(matchs);
            default:
                return null;
        }
    }

    private List<MatchsBO> getJczqMatchsBO(List<String> matchs) {
        List<SportAgainstInfoBO> list = getJczqSportAgainstInfoBOs(matchs);
        List<MatchsBO> retList = getMatchsBO(list);
        return retList;
    }

    private List<MatchsBO> getJclqMatchsBO( List<String> matchs) {
        List<SportAgainstInfoBO> list = getJclqSportAgainstInfoBOs(matchs);
        List<MatchsBO> retList = getMatchsBO(list);
        return retList;
    }

    private List<MatchsBO> getMatchsBO(List<SportAgainstInfoBO> list){
        List<MatchsBO> retList = new ArrayList<MatchsBO>();
        for(SportAgainstInfoBO infoBO : list){
            MatchsBO matchsBO = new MatchsBO();
            matchsBO.setHomeName(infoBO.getHomeName());
            matchsBO.setVisitName(infoBO.getVisitiName());
            matchsBO.setMatchName(infoBO.getMatchName());
            retList.add(matchsBO);
        }
        return retList;
    }


    /**
     * 根据彩种id和赛事id集合，查询竞彩篮球赛事最早截止的时间
     * @param matchs
     * @return
     */
    private SportAgainstInfoBO getJclqFirstEndSportAgainstInfoBOs(List<String> matchs) {
        List<SportAgainstInfoBO> listSportAgainstInfoBOs = getJclqSportAgainstInfoBOs(matchs);
        this.sortByEndTimeAsc(listSportAgainstInfoBOs);
        return ObjectUtil.isBlank(listSportAgainstInfoBOs)?null:listSportAgainstInfoBOs.get(0);
    }

    /**
     * 从缓存获取竞彩篮球对阵相关信息
     * @param matchs
     * @return
     */
    private  List<SportAgainstInfoBO> getJclqSportAgainstInfoBOs(List<String> matchs){
        List<SportAgainstInfoBO> listSportAgainstInfoBOs = new ArrayList<SportAgainstInfoBO>();
        for(String systemCode : matchs){
            JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
            if(!ObjectUtil.isBlank(jclqOrderBO)){
                listSportAgainstInfoBOs.add(getSportAgainstInfoBOList(jclqOrderBO));
            }
        }
        return listSportAgainstInfoBOs;
    }

    private SportAgainstInfoBO getSportAgainstInfoBOList(JcBaseBO jcBaseBO){
        SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
        sportAgainstInfoBO.setSystemCode(jcBaseBO.getSystemCode());
        sportAgainstInfoBO.setSaleEndTime(jcBaseBO.getSaleEndDate());
        sportAgainstInfoBO.setStartTime(jcBaseBO.getStartTimeStamp());
        sportAgainstInfoBO.setHomeName(jcBaseBO.getHomeShortName());
        sportAgainstInfoBO.setVisitiName(jcBaseBO.getGuestShortName());
        sportAgainstInfoBO.setMatchName(jcBaseBO.getMatchShortName());
        sportAgainstInfoBO.setHomeLogo(jcBaseBO.getHomeLogo());
        sportAgainstInfoBO.setGuestLogo(jcBaseBO.getGuestLogo());
        sportAgainstInfoBO.setOfficialMatchCode(jcBaseBO.getOfficialMatchCode());
        return sportAgainstInfoBO;
    }

    /**
     * 根据彩种id和赛事id集合，查询竞彩足球赛事最早截止的时间
     * @param matchs
     * @return
     */
    private SportAgainstInfoBO getJczqFirstEndSportAgainstInfoBOs(List<String> matchs) {
        List<SportAgainstInfoBO> listSportAgainstInfoBOs = getJczqSportAgainstInfoBOs(matchs);
        this.sortByEndTimeAsc(listSportAgainstInfoBOs);
        return ObjectUtil.isBlank(listSportAgainstInfoBOs)?null:listSportAgainstInfoBOs.get(0);
    }

    /**
     * 从缓存获取竞彩足球对阵相关信息
     * @param matchs
     * @return
     */
    private  List<SportAgainstInfoBO> getJczqSportAgainstInfoBOs(List<String> matchs){
        List<SportAgainstInfoBO> listSportAgainstInfoBOs = new ArrayList<SportAgainstInfoBO>();
        Map<String,JczqOrderBO> map = jcDataService.findJczqOrderBOBySystemCodes(matchs);
        for(String key : map.keySet()){
            JczqOrderBO jczqOrderBO = map.get(key);
            if(!ObjectUtil.isBlank(jczqOrderBO)){
                listSportAgainstInfoBOs.add(getSportAgainstInfoBOList(jczqOrderBO));
            }
        }
        return listSportAgainstInfoBOs;
    }

    /**
     * 按销售截止时间顺序排序，用于取最早截止的
     * @param listSportAgainstInfoBOs
     */
    private void sortByEndTimeAsc(List<SportAgainstInfoBO> listSportAgainstInfoBOs){
        StringBuffer stringBuffer = new StringBuffer();
        for(SportAgainstInfoBO bean : listSportAgainstInfoBOs){
            stringBuffer.append("systemCode="+bean.getSystemCode()+",startTime="+ DateUtil.convertDateToStr(bean.getStartTime(),DateUtil.DEFAULT_FORMAT)+",endTime="+DateUtil.convertDateToStr(bean.getSaleEndTime(),DateUtil.DEFAULT_FORMAT)+";");
        }
        logger.info("===========ListSportAgainstInfoBOs["+stringBuffer.toString()+"]=========");
        Collections.sort(listSportAgainstInfoBOs, new Comparator<SportAgainstInfoBO>() {
            @Override
            public int compare(SportAgainstInfoBO o1, SportAgainstInfoBO o2) {
                return o1.getSaleEndTime().compareTo(o2.getSaleEndTime());
            }
        });
    }

    /**
     * 设置排序条件,判断是否超多个排序字段
     * @param rcmdQueryVO
     */
    private ResultBO<?> setOrderBy(final RcmdQueryVO rcmdQueryVO) {
        if(!ObjectUtil.isBlank(rcmdQueryVO.getOrderType())){
            switch (rcmdQueryVO.getOrderType()){
                case Constants.NUM_1:
                    rcmdQueryVO.setSortField("c.return_rate");
                    break;
                case Constants.NUM_2:
                    rcmdQueryVO.setSortField("c.continue_hit DESC, c.return_rate");
                    break;
                case Constants.NUM_3:
                    rcmdQueryVO.setSortField("c.recent_status DESC, c.return_rate");
                    break;
                case Constants.NUM_4:
                    rcmdQueryVO.setSortField("c.hit_rate DESC, c.return_rate");
                    break;
                case Constants.NUM_5:
                    rcmdQueryVO.setSortField("c.pay_counts DESC, c.return_rate");
                    break;
                case Constants.NUM_6:
                    rcmdQueryVO.setSortField("a.create_time DESC, c.return_rate");
                    break;
                case Constants.NUM_7:
                    rcmdQueryVO.setSortField("c.pay_amount DESC, c.return_rate");
                    break;
                case Constants.NUM_8: //自购金额
                    rcmdQueryVO.setSortField("f.self_buy DESC, c.return_rate");
                    break;
                default:
                    return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
            }
            rcmdQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        }
        return ResultBO.ok();
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

}
