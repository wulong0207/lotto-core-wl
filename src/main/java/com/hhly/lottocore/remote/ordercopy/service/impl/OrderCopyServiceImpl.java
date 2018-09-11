package com.hhly.lottocore.remote.ordercopy.service.impl;


import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueLinkDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderFollowedInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLinkPO;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.ordercopy.service.*;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.common.OrderCopyEnum.OrderVisibleTypeEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.*;
import com.hhly.skeleton.lotto.base.order.bo.*;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.*;
import com.hhly.skeleton.lotto.base.ordercopy.vo.*;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.ticket.bo.BetContentBO;
import com.hhly.skeleton.user.bo.UserInfoBO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author lgs on
 * @version 1.0
 * @desc 抄单项目service实现。
 * @date 2017/9/19.
 * @company 益彩网络科技有限公司
 */
@Service("iOrderCopyService")
public class OrderCopyServiceImpl implements IOrderCopyService {

    private static Logger logger = LoggerFactory.getLogger(OrderCopyServiceImpl.class);

    @Autowired
    private MUserIssueInfoService mUserIssueInfoService;

    @Autowired
    private OrderIssueInfoService orderIssueInfoService;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private FocusService focusService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private OrderFollowedInfoService orderFollowedInfoService;

    @Autowired
    private OrderInfoDaoMapper orderInfoDaoMapper;

    @Autowired
    private IJcDataService jcDataService;

    @Autowired
    private OrderFollowedInfoDaoMapper orderFollowedInfoDaoMapper;

    @Autowired
    private MUserIssueLinkDaoMapper mUserIssueLinkDaoMapper;

    @Autowired
    private MUserIssueLevelService mUserIssueLevelService;

    @Autowired
    private OrderIssueInfoDaoMapper orderIssueInfoDaoMapper;



    @Value("${uploadURL}")
    private String urlHead;

    private static final DecimalFormat df = new DecimalFormat("##.0");

    /**
     * 新增发单记录
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> insertOrderCopy(OrderIssueInfoVO vo) throws Exception {
        return orderIssueInfoService.addOrderIssueInfo(vo);
    }

    /**
     * 根据发单用户id查询发单用户信息
     *
     * @return
     */
    @Override
    public ResultBO<?> findUserIssueInfoBoById(Long id, String token) {
        MUserIssueInfoBO resultBO = mUserIssueInfoService.findUserIssueInfoBoById(id);
        UserInfoBO userInfoBO = null;
        if (!StringUtil.isBlank(token)) {
            ResultBO<?> result = userInfoCacheService.checkToken(token);
            if(result.isError())
                return result;
            userInfoBO = (UserInfoBO) result.getData();
        }

        if (ObjectUtil.isBlank(resultBO) && !ObjectUtil.isBlank(userInfoBO)) {
            resultBO = mUserIssueInfoService.findUserIssueInfoBoByUserId(userInfoBO.getId().longValue());
        }
        //如果还是差不出来数据全部设为默认值0
        if (ObjectUtil.isBlank(resultBO)) {
            resultBO = new MUserIssueInfoBO();
            resultBO.setNickName(userInfoBO.getNickname());
            resultBO.setLevel(0);
            resultBO.setContinueHit(IssueUtil.getContinueHitStr(0));
            resultBO.setCommissionAmount(0f);
            resultBO.setFocusNum(0);
            resultBO.setFollowAmount(0);
            resultBO.setHitMoney(0f);
            resultBO.setProfitRate(IssueUtil.getOnlyPercent(0d));
            resultBO.setIssueAmount(0);
            resultBO.setIssueNum(0);
            resultBO.setHitRate(IssueUtil.getOnlyPercent(0d));
            return ResultBO.ok(resultBO);
        }

        resultBO.setHeadUrl(UserInfoBOUtil.getHeadUrl(resultBO.getHeadUrl(),urlHead));
        MUserIssueLinkVO mUserIssueLinkVO = new MUserIssueLinkVO();
        mUserIssueLinkVO.setUserIssueId(resultBO.getId());
        mUserIssueLinkVO.setDataStatus(true);
        resultBO.setFocusNum(mUserIssueLinkDaoMapper.selectByConditionCount(mUserIssueLinkVO));
        resultBO.setHitRate(IssueUtil.getOnlyPercent(resultBO.getHitRateTemp()));
        resultBO.setProfitRate(IssueUtil.getOnlyPercent(resultBO.getProfitRateTemp()));
        resultBO.setRecentRecord(IssueUtil.getRecentRecordStr(resultBO.getRecentRecordTemp()));
        if (!ObjectUtil.isBlank(resultBO.getContinueHitStr())) {
            resultBO.setContinueHit(IssueUtil.getContinueHitStr(resultBO.getContinueHitStr()));
        }


        if (!ObjectUtil.isBlank(userInfoBO) && !userInfoBO.getId().equals(resultBO.getUserId())) {
            resultBO.setCommissionAmount(null);
        }


        Integer level = mUserIssueLevelService.getUserIssueLevel(resultBO.getId());
        resultBO.setLevel(level);

        return ResultBO.ok(resultBO);
    }

    /**
     * 查询是否已关注
     */
    @Override
    public ResultBO<?> findIfFocus(Integer userIssueId, String token) {
        ResultBO<?> result = userInfoCacheService.checkToken(token);
        if(result.isError())
            return result;
        UserInfoBO userInfoBO = (UserInfoBO) result.getData();
        MUserIssueLinkVO mUserIssueLinkVO = new MUserIssueLinkVO();
        mUserIssueLinkVO.setUserIssueId(userIssueId.intValue());
        mUserIssueLinkVO.setUserId(userInfoBO.getId());
        mUserIssueLinkVO.setDataStatus(true);

        return ResultBO.ok(focusService.isFocus(mUserIssueLinkVO) ? Constants.NUM_1 : Constants.NUM_0);
    }

    /**
     * 关注/取消关注
     */
    @Override
    public ResultBO<?> updateFocus(MUserIssueLinkVO mUserIssueLinkVO) {
        MUserIssueLinkPO mUserIssueLinkPO = new MUserIssueLinkPO();
//        mUserIssueLinkPO.setId(mUserIssueLinkVO.getId());
        mUserIssueLinkPO.setToken(mUserIssueLinkVO.getToken());
        mUserIssueLinkPO.setUserIssueId(mUserIssueLinkVO.getUserIssueId());
        return focusService.updateFocus(mUserIssueLinkPO, mUserIssueLinkVO.getFlag());
    }

    /**
     * 查询关注列表
     */
    @Override
    public ResultBO<?> queryFocusByMUserIssueLinkVO(MUserIssueLinkVO mUserIssueLinkVO) {
        mUserIssueLinkVO.setDataStatus(true);
        mUserIssueLinkVO.setSortField("a.begin_time");
        mUserIssueLinkVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        return focusService.queryFocusByMUserIssueLinkVO(mUserIssueLinkVO);
    }

    /**
     * 查询实单列表
     */
    @Override
    public ResultBO<?> queryIssueInfo(QueryVO queryVO) {
        /**展示*/
        queryVO.setIsShow(1);
        /**有效*/
        queryVO.setDataStatus(1);
        Assert.paramNotNull(queryVO.getPageIndex(), "pageIndex");
        Assert.paramNotNull(queryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(queryVO.getQueryType(), "queryType");
        ResultBO<?> result = this.processDif(queryVO);
        if(result.isError())
            return result;
        switch (queryVO.getQueryType()) {
            case 1:
            case 2:
            case 5:
            case 6:
                return orderIssueInfoService.queryIssueInfo(queryVO);
            case 4:
            case 3:
                return orderIssueInfoService.queryByQueryTypeThree(queryVO);
            default:
                return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
    }

    @Override
    public ResultBO<?> queryIssueInfoCount(QueryVO queryVO) {
        /**展示*/
        queryVO.setIsShow(1);
        /**有效*/
        queryVO.setDataStatus(1);
        Assert.paramNotNull(queryVO.getPageIndex(), "pageIndex");
        Assert.paramNotNull(queryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(queryVO.getQueryType(), "queryType");
        ResultBO<?> result = this.processDif(queryVO);
        if(result.isError())
            return result;
        switch (queryVO.getQueryType()) {
            case 1:
            case 2:
            case 5:
                return ResultBO.ok(orderIssueInfoService.queryIssueInfoCount(queryVO));
            case 4:
            case 3:
                return ResultBO.ok(orderIssueInfoService.queryByQueryTypeThreeCount(queryVO));
            default:
                return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
    }

    /**
     * 查询专家列表
     */
    @Override
    public ResultBO<?> queryUserIssueInfo(QueryVO queryVO) {
        QueryVO innerQueryVO = new QueryVO();
        innerQueryVO.setPageIndex(queryVO.getPageIndex());
        innerQueryVO.setPageSize(queryVO.getPageSize());
        innerQueryVO.setQueryType(queryVO.getQueryType());
        //命中率、推荐最多、最大猜中, 默认按命中率排序
        innerQueryVO.setSortCondition(ObjectUtil.isBlank(queryVO.getSortCondition()) ? 1 : queryVO.getSortCondition());
        /**专家推荐专家列表*/
        if (queryVO.getQueryType() == 1) {
            Assert.paramNotNull(queryVO.getLotteryCode(), "lotteryCode");
            innerQueryVO.setLotteryCode(queryVO.getLotteryCode());
        }
        /**动态列表我的关注*/
        else if (queryVO.getQueryType() == 2) {
            //验证token
            ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
            if(result.isError())
                return result;
            UserInfoBO userInfoBO = (UserInfoBO) result.getData();
            queryVO.setUserId(userInfoBO.getId());
            Assert.paramNotNull(queryVO.getUserId(), "userId");
            innerQueryVO.setUserId(queryVO.getUserId());
        }
        /**查询盈利率60%以上专家*/
        else {
            innerQueryVO.setLotteryCode(null);
            innerQueryVO.setPercent(0.0);
        }
        return mUserIssueInfoService.queryUserIssueInfo(innerQueryVO);
    }

    @Override
    public ResultBO<?> queryUserIssueInfoCount(QueryVO queryVO) {
        //验证token
        ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
        if(result.isError())
            return result;

        QueryVO innerQueryVO = new QueryVO();
        innerQueryVO.setToken(queryVO.getToken());
        innerQueryVO.setPageIndex(queryVO.getPageIndex());
        innerQueryVO.setPageSize(queryVO.getPageSize());
        return ResultBO.ok(mUserIssueInfoService.queryUserIssueInfoCount(innerQueryVO));
    }

    /**
     * 查询返佣情况
     */
    @Override
    public ResultBO<?> queryCommissions(QueryVO queryVO) {
        ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
        if(result.isError())
            return result;
        UserInfoBO userInfoBO = (UserInfoBO) result.getData();
        QueryVO innerQueryVO = new QueryVO();
        innerQueryVO.setToken(queryVO.getToken());
        innerQueryVO.setUserId(userInfoBO.getId());
        innerQueryVO.setPageIndex(queryVO.getPageIndex());
        innerQueryVO.setPageSize(queryVO.getPageSize());
        innerQueryVO.setDaysNum(queryVO.getDaysNum());
        innerQueryVO.setSortField("a.create_time");
        innerQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        return commissionService.queryCommissions(innerQueryVO);
    }

    /**
     * 查询返佣明细
     */
    @Override
    public ResultBO<?> queryCommissionsDetails(QueryVO queryVO) {
        QueryVO innerQueryVO = new QueryVO();
        innerQueryVO.setOrderCode(queryVO.getOrderCode());
        innerQueryVO.setPageIndex(queryVO.getPageIndex());
        innerQueryVO.setPageSize(queryVO.getPageSize());
        innerQueryVO.setSortField("b.create_time");
        innerQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        return commissionService.queryCommissionsDetails(innerQueryVO);
    }


    /**
     * 处理实单查询条件差异：<br>
     *
     * @param queryVO
     * @author longguoyou
     * @date 2017年9月30日
     */
    private ResultBO<?> processDif(QueryVO queryVO) {
        ResultBO<?> result = null;
        switch (queryVO.getQueryType()) {
            //首页实单查询，不一定是专家
            case 1:
            case 6:
                /** 1、运营设置推荐内容 ;2、若无设置内容，则按照赛事发布时间最新倒序排序;3、默认显示最近20条，每次加载20条 */
                //判断
                Assert.paramNotNull(queryVO.getLotteryCode(), "lotteryCode");
                //设值 、排序
                queryVO.setFlag(1);
                queryVO.setSortField("b.is_top desc,b.is_recommend desc,b.max_roi desc,b.create_time");
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                break;
            //实单综合查询：
            case 2:
                /** 1、统计未开赛的实单数据 ;2、查询条件：足彩方案(竞彩足球、竞彩篮球)、专家级别(专家方案、用户方案)、排序字段(抄单最多、回报率最大、最大连红) */
                //判
                Assert.paramNotNull(queryVO.getSortCondition(), "sortCondition");
                //设值
                if (queryVO.getSortCondition() == Constants.NUM_1) {
                    queryVO.setSortField("b.follow_num");
                } else if (queryVO.getSortCondition() == Constants.NUM_2) {
                    queryVO.setSortField("b.max_roi");
                } else if (queryVO.getSortCondition() == Constants.NUM_3) {
                    queryVO.setSortField("b.continue_hit");
                }
                queryVO.setIssueUserId(null);
                //未开赛
                queryVO.setFlag(1);
                //排序
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                break;
            //专家详情页实单: 发单用户 ，一定是专家
            case 3:
                //判断
                Assert.paramNotNull(queryVO.getLotteryCode(), "lotteryCode");
                Assert.paramNotNull(queryVO.getIssueUserId(), "issueUserId");//专家页面跳转过来，传的发单用户ID
                //设值
                /** 1、发单用户ID为查看的指定专家;2、按推单时间倒序排序*/
                /**专家*/
//                queryVO.setLevel(1);
                /**所有实单*/
//                queryVO.setAll(1);
                queryVO.setSortField("a.winning_status,b.create_time");
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                break;
            //专家页面实单 ：发单用户，不一定是专家
            case 4://和3查询结果一样，入参不一样token和issueUserId区别
                //判断
                Assert.paramNotNull(queryVO.getLotteryCode(), "lotteryCode");
                Assert.paramNotNull(queryVO.getToken(), "token");//个人中心，我的推荐，传的token
                //验证token
                result = userInfoCacheService.checkToken(queryVO.getToken());
                if(result.isError())
                    return result;
                UserInfoBO userInfoBO = (UserInfoBO) result.getData();
                queryVO.setUserId(userInfoBO.getId());
                //设值、排序
                /** 1、发单用户TOKEN为本人;2、按推单时间倒序排序 */
                queryVO.setSortField("a.winning_status,b.create_time");
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                //验证token
                //issueUserId用于查询发单用户
                MUserIssueInfoBO mUserInfoBO = mUserIssueInfoService.findUserIssueInfoBoByUserId(Long.valueOf(queryVO.getUserId()+SymbolConstants.ENPTY_STRING));
                //如果用户还没有发过单，则设置issueUserId为-1，表示还没发过单
                queryVO.setIssueUserId(ObjectUtil.isBlank(mUserInfoBO)?-1:mUserInfoBO.getId());
                queryVO.setUserId(null);
                break;
            //动态列表实单-与我相关 ，不一定是专家
            case 5:
                //判断
//                Assert.paramNotNull(queryVO.getToken(), "token");
                //设值
                /** 1、按方案截止时间倒序排序 */
                queryVO.setSortField("b.create_time");
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                //验证token
                result = userInfoCacheService.checkToken(queryVO.getToken());
                if(result.isError())
                    return result;
                UserInfoBO userInfo = (UserInfoBO) result.getData();
                queryVO.setUserId(userInfo.getId());
                break;
            default:
                return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        return ResultBO.ok();
    }




    /**
     * 查询发单用户中奖统计
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> findUserIssuePrizeCount(MUserIssueInfoVO vo) {
        return mUserIssueInfoService.findUserIssuePrizeCount(vo);
    }


    /**
     * 用户跟单
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> orderFollowed(OrderFollowedInfoVO vo) throws Exception {
        return orderFollowedInfoService.orderFollowed(vo);
    }

    /**
     * 查询抄单明细
     */
    @Override
    public ResultBO<?> queryFollowedDetails(QueryVO queryVO) {
        queryVO.setSortField("a.create_time");
        queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        return orderFollowedInfoService.queryFollowedDetails(queryVO);
    }

    /**
     * 构造订单信息包括对阵，彩果，最新sp值
     *
     * @param orderCode
     * @return
     * @throws Exception
     */
    public OrderFullDetailInfoBO buildOrderInfo(String orderCode) throws Exception {
        OrderFullDetailInfoBO orderFullDetailInfo = new OrderFullDetailInfoBO();
        OrderBaseInfoBO orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderCode, null);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderCode(orderCode);
        List<OrderDetailInfoBO> orderDetailInfoBOs = orderInfoDaoMapper.queryOrderDetailInfo(orderDetailVO);
        //orderBaseInfoBO.setContentType(orderDetailInfoBOs.get(0).getContentType());
        // 设置串关
        setOrderBunch(orderBaseInfoBO.getLotteryCode(), orderBaseInfoBO, orderDetailInfoBOs);
        // 从缓存取对阵信息，彩果等
        List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<>();
        for (OrderDetailInfoBO orderDetailInfoBO : orderDetailInfoBOs) {// 每一个订单详情可能包含多场赛事
            // 设置对阵赛事信息
            setOrderDetailInfo(orderBaseInfoBO.getLotteryCode(), orderDetailInfoBO.getBetContent(), orderMatchInfoBOs,
                    orderDetailInfoBO.getLotteryChildCode());
        }
        //中奖后，显示实际奖金
        if(orderBaseInfoBO.getWinningStatus() == OrderEnum.OrderWinningStatus.WINNING.getValue() || orderBaseInfoBO.getWinningStatus() == OrderEnum.OrderWinningStatus.GET_WINNING.getValue()){
            //税前奖金
            orderBaseInfoBO.setMaxBonusStr(orderBaseInfoBO.getPreBonus()+"-"+orderBaseInfoBO.getPreBonus());
            orderFullDetailInfo.setWinStatus(orderBaseInfoBO.getWinningStatus());
        }
        orderFullDetailInfo.setOrderMatchInfoBOs(orderMatchInfoBOs);
        orderFullDetailInfo.setOrderBaseInfoBO(orderBaseInfoBO);
        return orderFullDetailInfo;
    }

    /**
     * 设置赛事对阵信息
     *
     * @param lotteryCode
     * @param betContent
     * @param orderMatchInfoBOs
     * @param lotteryChildCode
     */
    private void setOrderDetailInfo(Integer lotteryCode, String betContent,
                                    List<OrderMatchInfoBO> orderMatchInfoBOs, Integer lotteryChildCode) {
        Integer lotteryType = Constants.getLotteryType(lotteryCode);
        if (lotteryType == Constants.NUM_2) {// 竞彩篮球和足球和北京单场和胜负过关
            // 设置对阵信息
            String[] betContentStr = FormatConversionJCUtil.singleBetContentAnalysis(betContent);
            String gameContent = betContentStr[0];
            // 获取赛事对阵集合
            List<OrderMatchInfoBO> orderMatchInfoChildBOs = getJCOrderMatchInfoBos(lotteryCode,
                    gameContent, lotteryChildCode);
            orderMatchInfoBOs.addAll(orderMatchInfoChildBOs);
        }
    }

    /**
     * 获取竞彩每场赛事的对阵信息
     *
     * @param lotteryCode
     * @param gameContent
     * @param lotteryChildCode
     * @return
     */
    private List<OrderMatchInfoBO> getJCOrderMatchInfoBos(Integer lotteryCode,
                                                          String gameContent, Integer lotteryChildCode) {
        List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<OrderMatchInfoBO>();
        //竞彩篮球和足球#前面都是胆
        String gameDetails[] = gameContent.split(SymbolConstants.NUMBER_SIGN);
        if (gameDetails.length > 1) {//有胆
            //胆的赛事
            String danContent = gameDetails[0];
            String danDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(danContent);
            for (String gameCon : danDetails) {
                String systemCode = getSystemCodeAndContent(gameCon, lotteryChildCode)[0];
                // 足球,篮球
                OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,lotteryChildCode, getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
                        systemCode,gameCon);
                orderMatchInfoBO.setIsDan(com.hhly.skeleton.base.constants.Constants.NUM_1);
                orderMatchInfoBOs.add(orderMatchInfoBO);
            }
            //非胆的赛事
            String nodanContent = gameDetails[1];
            String nodanDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(nodanContent);
            for (String gameCon : nodanDetails) {
                String systemCode = getSystemCodeAndContent(gameCon, lotteryChildCode)[0];
                // 足球,篮球
                OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,lotteryChildCode, getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
                        systemCode,gameCon);
                orderMatchInfoBOs.add(orderMatchInfoBO);
            }
        } else {//无胆
            String details[] = FormatConversionJCUtil.betContentDetailsAnalysis(gameContent);
            for (String gameCon : details) {
                String systemCode = getSystemCodeAndContent(gameCon, lotteryChildCode)[0];
                // 足球,篮球
                OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,lotteryChildCode, getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
                        systemCode,gameCon);
                orderMatchInfoBOs.add(orderMatchInfoBO);
            }
        }
        return orderMatchInfoBOs;
    }

    /**
     * 取出系统编号，和系统编号后面的投注内容
     *
     * @param gameDetail
     * @return
     */
    private String[] getSystemCodeAndContent(String gameDetail, Integer lotteryChildCode) {
        String systemCode = "";
        String content = "";
        if (JCLQConstants.ID_JCLQ_HHGG == lotteryChildCode || JCZQConstants.ID_FHT == lotteryChildCode) {// 混合
            systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.UNDERLINE, true)[0];
            content = gameDetail.split(systemCode + SymbolConstants.UNDERLINE)[1];
        } else {
            // 让分胜平负和大小分
            String systemCodestr[] = FormatConversionJCUtil.stringSplitArray(gameDetail,
                    SymbolConstants.MIDDLE_PARENTHESES_LEFT, true);
            if (systemCodestr.length >= 2) {// 是让分胜负或者大小分
                systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail,
                        SymbolConstants.MIDDLE_PARENTHESES_LEFT, true)[0];
                content = gameDetail.split(systemCode)[1];
            } else {// 其他玩法
                systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.PARENTHESES_LEFT,
                        true)[0];
                content = gameDetail.split(systemCode)[1];
            }
        }
        String contents[] = {systemCode, content};
        return contents;
    }

    /**
     * 设置竞彩内容
     *
     * @param betGameContent
     * @param systemCode
     * @return
     */
    private OrderMatchInfoBO getOrderMatchInfo(Integer lotteryCode,Integer lotteryChildCode,String betGameContent, String systemCode,String betContent) {
        OrderMatchInfoBO orderMatchInfoBO = new OrderMatchInfoBO();
        if (JCZQConstants.ID_JCZQ_B == lotteryCode) {
            JczqOrderBO jczqOrderBO = jcDataService.findJczqOrderBOBySystemCode(systemCode);
            if (!ObjectUtil.isBlank(jczqOrderBO)) {
                orderMatchInfoBO.setId(jczqOrderBO.getId());
                orderMatchInfoBO.setOfficalMatchCode(jczqOrderBO.getOfficialMatchCode());
                orderMatchInfoBO.setSystemCode(systemCode);
                orderMatchInfoBO.setHomeName(ObjectUtil.isBlank(jczqOrderBO.getHomeShortName()) ? jczqOrderBO.getHomeFullName() : jczqOrderBO.getHomeShortName());
                orderMatchInfoBO.setVisitiName(ObjectUtil.isBlank(jczqOrderBO.getGuestShortName()) ? jczqOrderBO.getGuestFullName() : jczqOrderBO.getGuestShortName());
                orderMatchInfoBO.setMatchShortName(ObjectUtil.isBlank(jczqOrderBO.getMatchShortName()) ? jczqOrderBO.getMatchFullName() : jczqOrderBO.getMatchShortName());
                orderMatchInfoBO.setDate(jczqOrderBO.getDate());
                orderMatchInfoBO.setTime(jczqOrderBO.getTime());
                orderMatchInfoBO.setHomeLogo(jczqOrderBO.getHomeLogo());
                orderMatchInfoBO.setGuestLogo(jczqOrderBO.getGuestLogo());
                /*OrderMatchZQBO orderMatchZQBO = new OrderMatchZQBO();
                orderMatchZQBO.setFullScore(jczqOrderBO.getFullScore());
                orderMatchZQBO.setHalfScore(jczqOrderBO.getHalfScore());
                orderMatchZQBO.setLetNum(String.valueOf(jczqOrderBO.getNewestLetNum()));
                orderMatchZQBO.setFullSpf(jczqOrderBO.getFullSpf());
                orderMatchZQBO.setLetSpf(jczqOrderBO.getLetSpf());
                orderMatchZQBO.setScore(jczqOrderBO.getScore());
                orderMatchZQBO.setGoalNum(jczqOrderBO.getGoalNum());
                orderMatchZQBO.setHfWdf(jczqOrderBO.getHfWdf());
                //最新SP值
                orderMatchZQBO.setNewestLetSpWin(NumberFormatUtil.format(jczqOrderBO.getNewestLetSpWin()));
                orderMatchZQBO.setNewestLetSpDraw(NumberFormatUtil.format(jczqOrderBO.getNewestLetSpDraw()));
                orderMatchZQBO.setNewestLetSpFail(NumberFormatUtil.format(jczqOrderBO.getNewestLetSpFail()));
                orderMatchZQBO.setNewestSpWin(NumberFormatUtil.format(jczqOrderBO.getNewestSpWin()));
                orderMatchZQBO.setNewestSpDraw(NumberFormatUtil.format(jczqOrderBO.getNewestSpDraw()));
                orderMatchZQBO.setNewestSpFail(NumberFormatUtil.format(jczqOrderBO.getNewestSpFail()));
                orderMatchZQBO.setBetContentBOs(getMatchResult(betContent,lotteryCode,lotteryChildCode,jczqOrderBO,null));
                orderMatchInfoBO.setOrderMatchZQBO(orderMatchZQBO);*/
                orderMatchInfoBO.setBetContentBOs(getMatchResult(betContent,lotteryCode,lotteryChildCode,jczqOrderBO,null));
                orderMatchInfoBO.setMatchStatus(Integer.valueOf(jczqOrderBO.getMatchStatus()));
            }
        } else if (JCLQConstants.ID_JCLQ_B == lotteryCode) {
            JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
            if (!ObjectUtil.isBlank(jclqOrderBO)) {
                orderMatchInfoBO.setId(jclqOrderBO.getId());
                orderMatchInfoBO.setOfficalMatchCode(jclqOrderBO.getOfficialMatchCode());
                orderMatchInfoBO.setSystemCode(systemCode);
                orderMatchInfoBO.setHomeName(ObjectUtil.isBlank(jclqOrderBO.getHomeShortName()) ? jclqOrderBO.getHomeFullName() : jclqOrderBO.getHomeShortName());
                orderMatchInfoBO.setVisitiName(ObjectUtil.isBlank(jclqOrderBO.getGuestShortName()) ? jclqOrderBO.getGuestFullName() : jclqOrderBO.getGuestShortName());
                orderMatchInfoBO.setMatchShortName(ObjectUtil.isBlank(jclqOrderBO.getMatchShortName()) ? jclqOrderBO.getMatchFullName() : jclqOrderBO.getMatchShortName());
                orderMatchInfoBO.setDate(jclqOrderBO.getDate());
                orderMatchInfoBO.setTime(jclqOrderBO.getTime());
                orderMatchInfoBO.setHomeLogo(jclqOrderBO.getHomeLogo());
                orderMatchInfoBO.setGuestLogo(jclqOrderBO.getGuestLogo());
                if(!ObjectUtil.isBlank(jclqOrderBO.getFullScore())){
                    String[] scores = jclqOrderBO.getFullScore().split(SymbolConstants.COLON);
                    if(!ObjectUtil.isBlank(scores) && scores.length == Constants.NUM_2){
                        orderMatchInfoBO.setGuestScore(Integer.valueOf(scores[0]));
                        orderMatchInfoBO.setHomeScore(Integer.valueOf(scores[1]));
                    }
                }
                /*OrderMatchLQBO orderMatchLQBO = new OrderMatchLQBO();
                orderMatchLQBO.setFullScore(jclqOrderBO.getFullScore());
                orderMatchLQBO.setFullWf(jclqOrderBO.getFullWf());
                orderMatchLQBO.setLetScore(String.valueOf(jclqOrderBO.getNewestLetScore()));
                orderMatchLQBO.setLetWf(jclqOrderBO.getLetWf());
                orderMatchLQBO.setDxfWF(jclqOrderBO.getSizeScore());
                orderMatchLQBO.setSfcWF(jclqOrderBO.getWinScore());
                //最新SP值
                orderMatchLQBO.setNewestLetSpWin(NumberFormatUtil.format(jclqOrderBO.getNewestLetSpWin()));
                orderMatchLQBO.setNewestLetSpFail(NumberFormatUtil.format(jclqOrderBO.getNewestLetSpFail()));
                orderMatchLQBO.setNewestSpWin(NumberFormatUtil.format(jclqOrderBO.getNewestSpWin()));
                orderMatchLQBO.setNewestSpFail(NumberFormatUtil.format(jclqOrderBO.getNewestSpFail()));
                orderMatchLQBO.setBetContentBOs(getMatchResult(betContent,lotteryCode,lotteryChildCode,null,jclqOrderBO));
                orderMatchInfoBO.setOrderMatchLQBO(orderMatchLQBO);*/
                orderMatchInfoBO.setBetContentBOs(getMatchResult(betContent,lotteryCode,lotteryChildCode,null,jclqOrderBO));
                orderMatchInfoBO.setMatchStatus(Integer.valueOf(jclqOrderBO.getMatchStatus()));
            }
        }
        orderMatchInfoBO.setBetGameContent(betGameContent);//盘口，投注项，混合过关的玩法从此取。SP值从实体对象取
        return orderMatchInfoBO;
    }

    /**
     * 设置串关
     *
     * @param orderDetailInfoBOs
     * @param orderListInfoBO
     */
    private void setOrderBunch(Integer lotteryCode, OrderBaseInfoBO orderListInfoBO,
                               List<OrderDetailInfoBO> orderDetailInfoBOs) {
        StringBuffer stringBuffer = new StringBuffer();
        Integer lotteryType = Constants.getLotteryType(lotteryCode);
        Set<String> bunchSet = new TreeSet<String>();//串关去重
        if (!ObjectUtil.isBlank(orderDetailInfoBOs)) {
            for (OrderDetailInfoBO orderDetailInfoBO : orderDetailInfoBOs) {
                if (lotteryType == Constants.NUM_2) {// 竞彩篮球，竞彩足球,北京单场，胜负过关才有串关
                    String[] betContent = FormatConversionJCUtil
                            .singleBetContentAnalysis(orderDetailInfoBO.getBetContent());
                    String bunchArray[] = betContent[1].split(SymbolConstants.COMMA);
                    bunchSet.addAll(Arrays.asList(bunchArray));
                }
            }
        }
        if (bunchSet.size() > 0) {
            Iterator<String> it = bunchSet.iterator();
            while (it.hasNext()) {
                stringBuffer.append(it.next() + SymbolConstants.COMMA);
            }
            orderListInfoBO.setBunchStr(stringBuffer.substring(0, stringBuffer.length() - 1));
        }
    }

    /**
     * 解析赛事彩果
     * @param content
     * @param lotteryCode
     * @param lotteryChildCode
     * @return
     */
    private List<BetContentBO> getMatchResult(String content,Integer lotteryCode,Integer lotteryChildCode,JczqOrderBO jczqOrderBO,JclqOrderBO jclqOrderBO){
        List<BetContentBO> listBetContent = new ArrayList<BetContentBO>();
        if(content.split(SymbolConstants.UNDERLINE).length>=2){//选了多项子玩法
            String betContent[] = content.split(SymbolConstants.UNDERLINE);
            for(int i=1;i<betContent.length;i++){//第一个是系统编号
                listBetContent.addAll(getBetContents(lotteryCode,lotteryChildCode,betContent[i],jczqOrderBO,jclqOrderBO));
            }
        }else{
            listBetContent = getBetContents(lotteryCode,lotteryChildCode,content,jczqOrderBO,jclqOrderBO);
        }
        return  listBetContent;
    }

    private List<BetContentBO> getBetContents(Integer lotteryCode,Integer lotteryChildCode,String content,JczqOrderBO jczqOrderBO,JclqOrderBO jclqOrderBO){
        List<BetContentBO> listBetContent = new ArrayList<BetContentBO>();
        String betSps = FormatConversionJCUtil.singleGameBetContentSubstring(content);
        for(String sps : betSps.split(SymbolConstants.COMMA)){
            String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps);
            //投注项
            String bet = betSp[0];
            //赔率
            String sp = betSp[1];
            BetContentBO betContentBO = new BetContentBO();
            betContentBO.setSp(NumberFormatUtil.format(Float.valueOf(sp)));//赔率
            if(lotteryCode == JCZQConstants.ID_JCZQ_B){//足球
                betContentBO.setPlanContent(JCZQConstants.translate(lotteryChildCode, bet, content));//投注内容
                betContentBO.setFlag(judeFlag(bet,getZQCaiguoByLotteryChildCode(lotteryChildCode,jczqOrderBO,content)));//是否标红
                betContentBO.setPanKou(getZQInfoFromSingleBetContent(content,lotteryChildCode));//让分：如[+1]
            }else if(lotteryCode == JCLQConstants.ID_JCLQ_B){//篮球
                betContentBO.setPlanContent(JCLQConstants.translate(lotteryChildCode, bet, content,getLQInfoFromSingleBetContent(content,lotteryChildCode)));//投注内容
                String pankou = getLQInfoFromSingleBetContent(content,lotteryChildCode);
                //betContentBO.setFlag(judeFlag(bet,getLQCaiguoByLotteryChildCode(lotteryChildCode,jclqOrderBO,content)));//是否标红
                betContentBO.setFlag(judeFlagLq(lotteryChildCode,bet,getLQCaiguoByLotteryChildCode(lotteryChildCode,jclqOrderBO,content),pankou,content));//是否标红
                betContentBO.setPanKou(pankou);//让分：如[+1]
            }
            listBetContent.add(betContentBO);
        }
        return listBetContent;
    }

    ///////////////////////足球设置彩果start
    /**
     * 竞彩足球通过子玩法获取赛果
     * @author longguoyou
     * @date 2017年11月3日
     * @param lotteryChildCode
     * @return
     */
    private String getZQCaiguoByLotteryChildCode(Integer lotteryChildCode, JczqOrderBO jczqOrderBO, String initContent) {
        switch(lotteryChildCode){
            case JCZQConstants.ID_FBCQ:
                return jczqOrderBO.getHfWdf();
            case JCZQConstants.ID_FBF:
                return jczqOrderBO.getScore();
            case JCZQConstants.ID_FZJQ:
                return jczqOrderBO.getGoalNum();
            case JCZQConstants.ID_JCZQ:
                return jczqOrderBO.getFullSpf();
            case JCZQConstants.ID_RQS:
                return jczqOrderBO.getLetSpf();
            case JCZQConstants.ID_FHT:
                return getZQCaiguoByLotteryChildCodeMix(jczqOrderBO,initContent);
        }
        return null;
    }

    private String getZQCaiguoByLotteryChildCodeMix(JczqOrderBO jczqOrderBO, String initContent) {
        if(initContent.contains("R")){
            return getZQCaiguoByLotteryChildCode(JCZQConstants.ID_RQS,jczqOrderBO,null);
        }
        if(initContent.contains("S")){
            return getZQCaiguoByLotteryChildCode(JCZQConstants.ID_JCZQ,jczqOrderBO,null);
        }
        if(initContent.contains("Q")){
            return getZQCaiguoByLotteryChildCode(JCZQConstants.ID_FBF,jczqOrderBO,null);
        }
        if(initContent.contains("B")){
            return getZQCaiguoByLotteryChildCode(JCZQConstants.ID_FBCQ,jczqOrderBO,null);
        }
        if(initContent.contains("Z")){
            return getZQCaiguoByLotteryChildCode(JCZQConstants.ID_FZJQ,jczqOrderBO,null);
        }
        return null;
    }

    /**
     *足球 获取附加信息：让分[+10]<br>
     * 1)1711024024[-1](1@3.35)<br>
     * 2)1711024024_R[+10](3@1.33)<br>
     * @author longguoyou
     * @date 2017年11月4日
     * @param content
     * @param lotteryChildCode
     * @return 如：[+10]
     */
    private String getZQInfoFromSingleBetContent(String content, Integer lotteryChildCode) {
        //让胜平负玩法 :1711024024[-1](1@3.35)
        if(JCZQConstants.ID_RQS == lotteryChildCode){
            return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
        }
        //混投：1711024024_R[+10](3@1.33)
        if(JCZQConstants.ID_FHT == lotteryChildCode && content.contains("R")){
            return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
        }
        return SymbolConstants.ENPTY_STRING;
    }


    /**
     * 判断是否标红
     * @author longguoyou
     * @date 2017年11月3日
     * @param bet
     * @param caiguo
     * @return
     */
    private Integer judeFlag(String bet, String caiguo) {
        return bet.equals(caiguo)?1:0;
    }
    ///////////////////////足球设置彩果end

    ///////////////////////篮球设置彩果start

    /**
     * 竞篮：<br>
     *  大小分：157.5|99,157.5|99<br>
     *  让分：//-4.5|0,-5.5|0
     * @param lotteryCode
     * @param bet
     * @param caiguo
     * @return
     */
    protected Integer judeFlagLq(Integer lotteryCode, String bet, String caiguo, String panKou, String initContent){
        if(!ObjectUtil.isBlank(caiguo)){
            if(lotteryCode == JCLQConstants.ID_JCLQ_DXF || lotteryCode == JCLQConstants.ID_JCLQ_RF ||
                    (lotteryCode == JCLQConstants.ID_JCLQ_HHGG && initContent.contains("R")) ||
                    (lotteryCode == JCLQConstants.ID_JCLQ_HHGG && initContent.contains("D"))){
                String[] panKouAndCaiguos = caiguo.split(SymbolConstants.COMMA);
                boolean flag = false;
                for(String panKouAndCaiguo : panKouAndCaiguos){
                    String[] bets = panKouAndCaiguo.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
                    if(!ObjectUtil.isBlank(bets) && bets.length == Constants.NUM_2){
                        String panKouWithoutSymbol = getPankouWithoutSymbol(panKou);
                        //panKouWithoutSymbol = panKouWithoutSymbol.substring(0,panKouWithoutSymbol.length()-1);
                        if(panKouWithoutSymbol.indexOf(bets[0]) > -1 && bets[1].equals(bet)){
                            flag = true;
                        }
                    }
                }
                if(flag==true){
                    return Constants.NUM_1;
                }else{
                    return Constants.NUM_0;
                }
            }else{
                return judeFlag(bet,caiguo);
            }
        }
        return Constants.NUM_0;
    }

    /**
     * 获取盘口，没有前后符号，如[112.3] , 取112.3
     * @param panKou
     * @return
     */
    private String getPankouWithoutSymbol(String panKou){
        if(!ObjectUtil.isBlank(panKou)){
            return panKou.substring(panKou.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT)+Constants.NUM_1, panKou.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT));
        }
        return null;
    }
    /**
     * 竞彩篮球通过子玩法获取赛果
     * @author longguoyou
     * @date 2017年11月3日
     * @param lotteryChildCode
     * @return
     */
    private String getLQCaiguoByLotteryChildCode(Integer lotteryChildCode, JclqOrderBO jclqOrderBO, String initContent) {
        switch(lotteryChildCode){
            case JCLQConstants.ID_JCLQ_DXF:
                return jclqOrderBO.getSizeScore();
            case JCLQConstants.ID_JCLQ_RF:
                return jclqOrderBO.getLetWf();
            case JCLQConstants.ID_JCLQ_SF:
                return jclqOrderBO.getFullWf();
            case JCLQConstants.ID_JCLQ_SFC:
                return jclqOrderBO.getWinScore();
            case JCLQConstants.ID_JCLQ_HHGG:
                return getLQCaiguoByLotteryChildCodeMix(jclqOrderBO,initContent);
        }
        return null;
    }

    /**
     * 混投过关
     * @author longguoyou
     * @date 2017年11月7日
     * @param jclqOrderBO
     * @param initContent
     * @return
     */
    private String getLQCaiguoByLotteryChildCodeMix(JclqOrderBO jclqOrderBO, String initContent){
        if(initContent.contains("R")){
            return getLQCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_RF,jclqOrderBO,null);
        }
        if(initContent.contains("D")){
            return getLQCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_DXF,jclqOrderBO,null);
        }
        if(initContent.contains("S")){
            return getLQCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_SF,jclqOrderBO,null);
        }
        if(initContent.contains("C")){
            return getLQCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_SFC,jclqOrderBO,null);
        }
        return null;
    }

    /**
     * 获取附加信息：让分[+10]/大小分D[210.5]<br>
     * 1)1711024024[-10](1@3.35)<br>
     * 2)1711024024_D[210.5](99@1.33)<br>
     * @author longguoyou
     * @date 2017年11月4日
     * @param content
     * @param lotteryChildCode
     * @return 如：[+10]
     */
    private String getLQInfoFromSingleBetContent(String content, Integer lotteryChildCode) {
        //让胜平负玩法 :1711024024[-10](1@3.35)
        if(JCLQConstants.ID_JCLQ_RF == lotteryChildCode || JCLQConstants.ID_JCLQ_DXF == lotteryChildCode){
            return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
        }
        //混投：1711024024_D[210.5](99@1.33)/1711024024_R[+10.5](3@1.33)
        if(JCLQConstants.ID_JCLQ_HHGG == lotteryChildCode){
            if(content.contains("R") || content.contains("D")){
                return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
            }
        }
        return SymbolConstants.ENPTY_STRING;
    }

    ///////////////////////篮球设置彩果end


    /**
     * 查询用户方案详情
     *
     * @param id
     * @return
     */
    @Override
    public ResultBO<?> findOrderCopyIssueInfoBOById(Long id, String token) throws Exception {
        OrderCopyInfoBO bo = orderIssueInfoService.findOrderCopyIssueInfoBOById(id);

        if (ObjectUtil.isBlank(bo)) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NULL);
        }

        OrderFullDetailInfoBO orderFullDetailInfoBO = buildOrderInfo(bo.getOrderCode());
        bo.setOrderFullDetailInfoBO(orderFullDetailInfoBO);
        bo.setMinMoney(NumberUtil.div(orderFullDetailInfoBO.getOrderBaseInfoBO().getOrderAmount(), orderFullDetailInfoBO.getOrderBaseInfoBO().getMultipleNum(), 2));

        bo.setHeadUrl(UserInfoBOUtil.getHeadUrl(bo.getHeadUrl(),urlHead));
        //中奖后，显示实际回报率
        if(!ObjectUtil.isBlank(orderFullDetailInfoBO.getWinStatus()) && orderFullDetailInfoBO.getWinStatus() == OrderEnum.OrderWinningStatus.WINNING.getValue()){
            bo.setMaxRoi(String.valueOf(IssueUtil.getMaxBackRate(orderFullDetailInfoBO.getOrderBaseInfoBO().getPreBonus(),orderFullDetailInfoBO.getOrderBaseInfoBO().getOrderAmount())));
        }else{
            bo.setMaxRoi(String.valueOf(bo.getMaxRoiStr()));
        }
        bo.setHitRate(IssueUtil.getOnlyPercent(bo.getHitRateTemp()));
        bo.setProfitRate(IssueUtil.getOnlyPercent(bo.getProfitRateTemp()));
        bo.setContinueHit(IssueUtil.getContinueHitStr(bo.getContinueHitTemp()));
        if (!ObjectUtil.isNull(bo.getIsShow()) && bo.getIsShow() == OrderCopyEnum.IsShowEnum.HIDE.getValue()) {
            bo.setRecommendReason(null);
        }
        Integer level = mUserIssueLevelService.getUserIssueLevel(bo.getUserIssueId());
        bo.setLevel(level);
        bo.setRecentRecord(IssueUtil.getRecentRecordStr(bo.getRecentRecordStr()));
        if(!ObjectUtil.isBlank(bo.getRecommendReason())){
            if(Base64.isBase64(bo.getRecommendReason())){
                bo.setRecommendReason(new String(Base64.decodeBase64(bo.getRecommendReason().getBytes("UTF-8"))));
            }
        }

        if(bo.getCommissionRateTemp()==0){
            bo.setCommissionRate("免佣金");
        }else{
            bo.setCommissionRate(IssueUtil.getOnlyPercent(bo.getCommissionRateTemp()));
        }


        //全部可见
        if (bo.getOrderVisibleType() == OrderVisibleTypeEnum.ALL_SHOW.getValue()) {
            return ResultBO.ok(bo);
        }

        if (!(orderFullDetailInfoBO.getOrderBaseInfoBO().getWinningStatus() == OrderEnum.OrderWinningStatus.GET_WINNING.getValue() ||
                orderFullDetailInfoBO.getOrderBaseInfoBO().getWinningStatus() == OrderEnum.OrderWinningStatus.WINNING.getValue() || orderFullDetailInfoBO.getOrderBaseInfoBO().getWinningStatus() == OrderEnum.OrderWinningStatus.NOT_WINNING.getValue())) {
            if (StringUtil.isBlank(token)) {
                orderFullDetailInfoBO.setAddDetailBOPagingBO(null);
                orderFullDetailInfoBO.setOrderMatchInfoBOs(null);
                orderFullDetailInfoBO.getOrderBaseInfoBO().setJcPlanContent(null);
            } else {
                ResultBO<?> result = userInfoCacheService.checkToken(token);
                if(result.isError())
                    return result;
                UserInfoBO userInfoBO = (UserInfoBO) result.getData();
                //如果是用户自己 可以不用根据权限查看
                if (userInfoBO.getId().equals(bo.getUserId())) {
                    return ResultBO.ok(bo);
                }

                if (bo.getOrderVisibleType() == OrderVisibleTypeEnum.DRAW_SHOW.getValue()) {
                    orderFullDetailInfoBO.setAddDetailBOPagingBO(null);
                    orderFullDetailInfoBO.setOrderMatchInfoBOs(null);
                    orderFullDetailInfoBO.getOrderBaseInfoBO().setJcPlanContent(null);
                }

                if (bo.getOrderVisibleType() == OrderVisibleTypeEnum.FOLLOW_SHOW.getValue()) { //关注后可见
                    int total = mUserIssueLinkDaoMapper.selectCountByUserId(userInfoBO.getId(), bo.getUserIssueId().longValue());
                    if (total < 1) {
                        orderFullDetailInfoBO.setAddDetailBOPagingBO(null);
                        orderFullDetailInfoBO.setOrderMatchInfoBOs(null);
                        orderFullDetailInfoBO.getOrderBaseInfoBO().setJcPlanContent(null);
                    }
                } else if (bo.getOrderVisibleType() == OrderVisibleTypeEnum.SPECIFY_SHOW.getValue()) {//抄单后可见
                    int total = orderFollowedInfoDaoMapper.selectCountByUserIdAndOrderIssueId(userInfoBO.getId(), bo.getId());
                    if (total < 1) {
                        orderFullDetailInfoBO.setAddDetailBOPagingBO(null);
                        orderFullDetailInfoBO.setOrderMatchInfoBOs(null);
                        orderFullDetailInfoBO.getOrderBaseInfoBO().setJcPlanContent(null);
                    }
                }
            }

        }

        return ResultBO.ok(bo);
    }

    /**
     * 验证订单是否能够发单
     *
     * @param orderCode
     * @param token
     * @return
     */
    @Override
    public ResultBO<?> validateOrderCopy(String orderCode, String token) throws Exception {
        ResultBO<?> result = userInfoCacheService.checkToken(token);
        if(result.isError())
            return result;
        UserInfoBO userInfoBO = (UserInfoBO) result.getData();

        result = orderIssueInfoService.validateOrderCopy(orderCode, userInfoBO,null);
        if (!result.isError()) {
            result.setData(null);
        }
        return result;
    }

    /**
     * 根据发单编号和用户token，获取篮球盘口变化数据
     * @param issueOrderId
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public ResultBO<?> getHandicapChange(Long issueOrderId, String token)throws Exception{
        ResultBO<?> result = userInfoCacheService.checkToken(token);
        if(result.isError())
            return result;
        UserInfoBO userInfoBO = (UserInfoBO) result.getData();
        List<HandicapBO> list = new ArrayList<HandicapBO>();
        OrderCopyInfoBO orderCopyInfoBO = orderIssueInfoService.findOrderCopyIssueInfoBOById(issueOrderId);
        if(ObjectUtil.isBlank(orderCopyInfoBO)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        HandicapViewBO handicapViewBO = new HandicapViewBO();
        MUserIssueLinkVO mUserIssueLinkVO = new MUserIssueLinkVO();
        mUserIssueLinkVO.setUserIssueId(orderCopyInfoBO.getUserIssueId());
        mUserIssueLinkVO.setUserId(userInfoBO.getId());
        mUserIssueLinkVO.setDataStatus(true);
        boolean focus = focusService.isFocus(mUserIssueLinkVO);//是否已关注

        //1、查询订单基本信息
        OrderInfoBO orderBaseInfoBO = orderInfoDaoMapper.getOrderInfo(orderCopyInfoBO.getOrderCode());
        //存放最新盘口值
        List<HashMap<String,HandicapBO>> newHandicap = new ArrayList<HashMap<String,HandicapBO>>();
        //组装最新盘口数据
        setNewHandicapMap(orderBaseInfoBO, newHandicap);

        //存放方案详情投注内容，原始盘口值
        List<HashMap<String,HandicapBO>> initHandicap = new ArrayList<HashMap<String,HandicapBO>>();
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderCode(orderCopyInfoBO.getOrderCode());
        //2、查询方案详情基本信息
        List<OrderDetailInfoBO> listOrderDetailInfoBO = orderInfoDaoMapper.queryOrderDetailInfo(orderDetailVO);
        //3、解析投注内容，并组装原始盘口数据
        setInitHandicapMap(initHandicap, listOrderDetailInfoBO);

        //4、处理返回盘口变化数据
        process(list, initHandicap, newHandicap);

        //1：开奖后可见；2：全部可见；3：仅对抄单人可见；4：仅对关注人可见
        //1、公开可见 2、关注后可见（用户已关注）3、专家本人，则可见
        if(orderCopyInfoBO.getOrderVisibleType() == 2 || (orderCopyInfoBO.getOrderVisibleType() == 4 && focus) || orderBaseInfoBO.getUserId().equals(userInfoBO.getId())){
            handicapViewBO.setViewType(1);
            //5、设置盘口变化数据
            handicapViewBO.setListHandicapBO(list);
        }else{//1、抄单后可见；2、开奖后可见；3、关注后可见（用户未关注），则隐藏
            handicapViewBO.setViewType(0);
            if(!ObjectUtil.isBlank(list)){
                list.clear();
                list.add(new HandicapBO(SymbolConstants.TRAVERSE_SLASH+SymbolConstants.TRAVERSE_SLASH,SymbolConstants.TRAVERSE_SLASH+SymbolConstants.TRAVERSE_SLASH,SymbolConstants.TRAVERSE_SLASH+SymbolConstants.TRAVERSE_SLASH,SymbolConstants.TRAVERSE_SLASH+SymbolConstants.TRAVERSE_SLASH));
                handicapViewBO.setListHandicapBO(list);
            }
        }
        return ResultBO.ok(handicapViewBO);
    }

    /**
     * 处理设置返回结果list
     * @param list
     * @param initHandicap
     * @param newHandicap
     */
    private void process(List<HandicapBO> list, List<HashMap<String, HandicapBO>> initHandicap, List<HashMap<String, HandicapBO>> newHandicap) {
        long begin = System.currentTimeMillis();
        if(!ObjectUtil.isBlank(initHandicap)){
            for(HashMap<String, HandicapBO> initMatchMap : initHandicap){
                for(String systemCode : initMatchMap.keySet()){
                    if(!ObjectUtil.isBlank(newHandicap)){
                        for(HashMap<String, HandicapBO> newMatchMap : newHandicap){
                            if(!ObjectUtil.isBlank(newMatchMap.get(systemCode)) && !ObjectUtil.isBlank(newMatchMap.get(systemCode).getNewSp())
                                    && newMatchMap.get(systemCode).getPlay().equals(initMatchMap.get(systemCode).getPlay())){
                                String newSymbol = "";
                                String initSymbol= "";
                                String newSp = newMatchMap.get(systemCode).getNewSp();
                                String initSp = initMatchMap.get(systemCode).getNewSp();
                                if(newSp.contains(SymbolConstants.ADD)){
                                    newSymbol = newSp.substring(0,1);//保存符号(+)
                                    newSp = newSp.substring(1);//处理成无符号
                                }else{
                                    if(Double.valueOf(newSp) > 0 && !initMatchMap.get(systemCode).getPlay().equals("D")){
                                        newSymbol = SymbolConstants.ADD;
                                    }
                                }
                                if(initSp.contains(SymbolConstants.ADD)){// || initSp.contains(SymbolConstants.TRAVERSE_SLASH)
                                    initSymbol = initSp.substring(0,1);//保存符号(+)
                                    initSp = initSp.substring(1);//处理成无符号
                                }
                                String initSpWithoutSymbol = df.format(Double.valueOf(initSp));
                                if(!newSp.equals(initSp)){//盘口有变化
                                    HandicapBO handicapBO = new HandicapBO();
                                    handicapBO.setSerNum(newMatchMap.get(systemCode).getSerNum());//赛事编号
                                    handicapBO.setPreSp(initSymbol + initSpWithoutSymbol);//方案盘口SP
                                    handicapBO.setNewSp(newSymbol + newSp);//最新盘口SP
                                    handicapBO.setTime(DateUtil.convertDateToStr(DateUtil.convertStrToDate(newMatchMap.get(systemCode).getTime()),DateUtil.FORMAT_M_D_H_M));//更新时间
                                    if(MathUtil.compareTo(Double.valueOf(newSp),Double.valueOf(initSp)) > 0){//比较使用无符号
                                        handicapBO.setFlag(1);//箭头方向
                                        list.add(handicapBO);
                                    }else if(MathUtil.compareTo(Double.valueOf(newSp),Double.valueOf(initSp)) < 0){//比较使用无符号
                                        handicapBO.setFlag(0);//箭头方向
                                        list.add(handicapBO);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.info("竞篮盘口变化数据处理耗时：" + (begin-System.currentTimeMillis())/1000 + "秒");
    }


    /**
     * 设置原始盘口数据: 从投注内容中解析出来
     * @param initHandicap
     * @param listOrderDetailInfoBO
     */
    private void setInitHandicapMap(List<HashMap<String, HandicapBO>> initHandicap, List<OrderDetailInfoBO> listOrderDetailInfoBO) {
        for(OrderDetailInfoBO orderDetailInfoBO : listOrderDetailInfoBO){
            //第一类：单一玩法
            //让分：1711094304[-8.5](3@1.75,0@1.75)|1711094303[-3.5](3@1.75,0@1.75)^2_1^1
            //大小分：1711094304[159.50](99@1.75,00@1.75)|1711094303[159.50](99@1.80,00@1.69)^2_1^1

            //第二类：混投玩法
            //1801033303_R[+1](3@1.57)_S(3@2.35,0@2.14)_C(11@1.78,13@1.78)_D[110.5](99@2.31)|1801033304_D[112.5](00@2.31)|1801033305_R[-2](3@3.33)^2_1,3_1^100
            String[] betContents = null;
            if(orderDetailInfoBO.getBetContent().contains(SymbolConstants.NUMBER_SIGN)){
                betContents = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailInfoBO.getBetContent().replace(SymbolConstants.NUMBER_SIGN,SymbolConstants.VERTICAL_BAR));
            }else{
                betContents = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailInfoBO.getBetContent());
            }

            if(!ObjectUtil.isBlank(betContents)){
                String[] matchBetContents = FormatConversionJCUtil.betContentDetailsAnalysis(betContents[0]);
                if(!betContents[0].contains(SymbolConstants.UNDERLINE)){//单一玩法
                    for(String matchBetContent : matchBetContents){
                        if(matchBetContent.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
                            HashMap<String,HandicapBO> mapMatchBet = new HashMap<String,HandicapBO>();
                            String info = FormatConversionJCUtil.letNumBetContentSubstring(matchBetContent);//[115.2]/[+12.5]
                            String systemCode = matchBetContent.substring(0, matchBetContent.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT));
                            if(!ObjectUtil.isBlank(info)){//只允许让分胜负 和 大小分
                                if(orderDetailInfoBO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_RF){
                                    mapMatchBet.put(systemCode,new HandicapBO(systemCode,info,"R"));
                                }else if(orderDetailInfoBO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_DXF){
                                    mapMatchBet.put(systemCode,new HandicapBO(systemCode,info,"D"));
                                }
//                                mapMatchBet.put(systemCode,new HandicapBO(systemCode,info));
                                if(!ObjectUtil.isBlank(mapMatchBet)){
                                    initHandicap.add(mapMatchBet);
                                }
                            }
                        }
                    }
                } else{//混投玩法
                    for(String matchBetContent : matchBetContents){
                        //所有玩法投注内容
                        String[] manyPlayBetContents = FormatConversionJCUtil.singleMatchBetContentAnalysis(matchBetContent);
                        String systemCode = manyPlayBetContents[0];
                        for(String playBetContents : manyPlayBetContents){
                            //只处理:1、包含R,让分胜负  2、包含D，大小分
                            if(playBetContents.contains("R") || playBetContents.contains("D")){
                                HashMap<String,HandicapBO> mapMatchBet = new HashMap<String,HandicapBO>();
                                String info = FormatConversionJCUtil.letNumBetContentSubstring(playBetContents);//[115.2]/[+12.5]
                                mapMatchBet.put(systemCode,new HandicapBO(systemCode, info, playBetContents.contains("R")?"R":"D"));
                                initHandicap.add(mapMatchBet);
                            }
                        }

                    }

                }
            }
        }
    }

    /**
     * 设置最新盘口数据
     * @param orderBaseInfoBO
     * @param newHandicap
     */
    private void setNewHandicapMap(OrderInfoBO orderBaseInfoBO, List<HashMap<String, HandicapBO>> newHandicap) {
        //赛事系统编号
        String[] systemCodes = orderBaseInfoBO.getBuyScreen().split(SymbolConstants.COMMA);
        for(String systemCode : systemCodes){
            //变化盘口数据 , 对应前端页面同步
            JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
            HashMap<String,HandicapBO> newMatchBet = new HashMap<String,HandicapBO>();
            if(orderBaseInfoBO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_RF){
                jclqRF(systemCode, jclqOrderBO, newMatchBet, false);
            }else if(orderBaseInfoBO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_DXF){
                jclqDxf(systemCode, jclqOrderBO, newMatchBet, false);
            }else if(orderBaseInfoBO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_HHGG){
                HashMap<String,HandicapBO> hhggRf = new HashMap<String,HandicapBO>();
                jclqRF(systemCode, jclqOrderBO, hhggRf, true);
                HashMap<String,HandicapBO> hhggDxf = new HashMap<String,HandicapBO>();
                jclqDxf(systemCode, jclqOrderBO, hhggDxf, true);
                if(!ObjectUtil.isBlank(hhggRf)){
                    newHandicap.add(hhggRf);
                }
                if(!ObjectUtil.isBlank(hhggDxf)){
                    newHandicap.add(hhggDxf);
                }
            }
            if(!ObjectUtil.isBlank(newMatchBet)){
                newHandicap.add(newMatchBet);
            }
        }
    }

    /**
     * 竞彩篮球 大小分
     * @param systemCode
     * @param jclqOrderBO
     * @param newMatchBet
     * @param flag
     */
    private void jclqDxf(String systemCode, JclqOrderBO jclqOrderBO, HashMap<String, HandicapBO> newMatchBet, boolean flag) {
        ResultBO<List<String[]>> resultBO;
        List<String[]> listSp;
        String releaseTime;
        String dxf;
        resultBO = jcDataService.findJclqSssHistorySpData(jclqOrderBO.getId());
        //历史SP，已按releaseTime 倒序, presetScore : 162.5
        listSp = resultBO.getData();
        if(!ObjectUtil.isBlank(listSp)){
            releaseTime = listSp.get(0)[3];//更新时间
            if(DateUtil.compare(DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime()),releaseTime) >= 0 ){
                releaseTime =  DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime());
                dxf = ObjectUtil.isBlank(jclqOrderBO.getNewestPresetScore())?null:jclqOrderBO.getNewestPresetScore().toString();//大小分实时盘口
            }else{
                dxf = listSp.get(0)[0];//大小分最新盘口
            }
        }else{//为空时，取当前时间，sp盘口取对阵sp值
            releaseTime = ObjectUtil.isBlank(jclqOrderBO.getUpdateTime())?DateUtil.getNow():DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime());//如果获取不到时间，则取当前时间
            dxf = ObjectUtil.isBlank(jclqOrderBO.getNewestPresetScore())?null:jclqOrderBO.getNewestPresetScore().toString();//大小分实时盘口
        }

        newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,dxf, "D"));
//        if(!flag){
//            newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,dxf,null));
//        }else{
//            newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,dxf, "D"));
//        }
    }

    /**
     * 竞彩篮球让分胜负
     * @param systemCode
     * @param jclqOrderBO
     * @param newMatchBet
     * @param flag
     */
    private void jclqRF(String systemCode, JclqOrderBO jclqOrderBO, HashMap<String, HandicapBO> newMatchBet, boolean flag) {
        ResultBO<List<String[]>> resultBO;
        List<String[]> listSp;
        String releaseTime;
        String lefWf;//历史SP, 已按releaseTime 倒序 , lefScore : 4.5 / -6.5
        resultBO = jcDataService.findJclqWfHistorySpData(jclqOrderBO.getId(), SportEnum.WfTypeEnum.LET.getValue());
        listSp = resultBO.getData();
        if(!ObjectUtil.isBlank(listSp)){
            releaseTime = listSp.get(0)[3];//更新时间
            if(DateUtil.compare(DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime()),releaseTime) >= 0 ){
               releaseTime =  DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime());
               lefWf = ObjectUtil.isBlank(jclqOrderBO.getNewestLetScore())?null:jclqOrderBO.getNewestLetScore().toString();//让分实时盘口
            }else{
               lefWf = listSp.get(0)[0];//让分值最新盘口
            }
        }else{//为空时，取当前时间，sp盘口取对阵sp值
            releaseTime = ObjectUtil.isBlank(jclqOrderBO.getUpdateTime())?DateUtil.getNow():DateUtil.convertDateToStr(jclqOrderBO.getUpdateTime());//如果获取不到时间，则取当前时间
            lefWf = ObjectUtil.isBlank(jclqOrderBO.getNewestLetScore())?null:jclqOrderBO.getNewestLetScore().toString();//让分实时盘口
        }

        newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,lefWf,"R"));
//        if(!flag){
//            newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,lefWf,null));
//        }else{
//            newMatchBet.put(systemCode,new HandicapBO(jclqOrderBO.getOfficialMatchCode(),releaseTime,lefWf,"R"));
//        }
    }

    @Override
    public ResultBO<?> getCommissionDetailsSumCommission(QueryVO queryVO) {
        List<CommissionBO> listCommission = mUserIssueInfoService.getCommissionDetailsSumCommission(queryVO);
        return ResultBO.ok(IssueUtil.getSumCommission(listCommission));
    }

    @Override
    public ResultBO<?> getCopyOrderInfoForPay(Integer orderIssueId,String token) throws Exception {
        if(ObjectUtil.isBlank(orderIssueId) || ObjectUtil.isBlank(token)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        ResultBO<?> result = userInfoCacheService.checkToken(token);
        if(result.isError()) {
            return result;
        }
        OrderCopyPayInfoBO orderCopyPayInfoBO = orderIssueInfoDaoMapper.getCopyOrderInfoForPay(orderIssueId);
        return ResultBO.ok(orderCopyPayInfoBO);
    }
}
