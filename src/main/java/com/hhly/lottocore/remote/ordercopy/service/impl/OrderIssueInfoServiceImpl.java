package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.util.*;

import com.hhly.skeleton.base.common.OrderCopyEnum;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderFollowedInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.po.OrderIssueInfoPO;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.ordercopy.service.MUserIssueInfoService;
import com.hhly.lottocore.remote.ordercopy.service.MUserIssueLevelService;
import com.hhly.lottocore.remote.ordercopy.service.OrderIssueInfoService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.mq.msg.MessageModel;
import com.hhly.skeleton.base.mq.msg.OperateNodeMsg;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.IssueUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderCopyInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderCopyViewBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.QueryBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLevelVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.OrderIssueInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

@Service("orderIssueInfoService")
public class OrderIssueInfoServiceImpl implements OrderIssueInfoService {
    private static Logger logger = LoggerFactory.getLogger(OrderIssueInfoServiceImpl.class);

    @Autowired
    private IPageService pageService;

    @Autowired
    private OrderIssueInfoDaoMapper orderIssueInfoDaoMapper;

    @Autowired
    private MUserIssueInfoService mUserIssueInfoService;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private OrderInfoDaoMapper orderInfoDaoMapper;

    @Autowired
    private OrderFollowedInfoDaoMapper orderFollowedInfoDaoMapper;

    @Autowired
    private MUserIssueLevelService mUserIssueLevelService;

    @Value("${ordercopy_commission_rate_default}")
    private int orderCopyCommissionRateDefaultValue;

    @Value("${ordercopy_commission_rate_max}")
    private int orderCopyCommissionRateMaxValue;

    @Value("${before_file_url}")
    private String beforeFileUrl;

    @Autowired
    @Qualifier("orderCopyMessageProvider")
    private MessageProvider messageProvider;

    @Value("${msg_queue}")
    private String msgQueue;

    @Override
    public ResultBO<?> addOrderIssueInfo(OrderIssueInfoVO vo) throws Exception {

        if (ObjectUtil.isBlank(vo.getOrderVisibleType())) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_ORDER_VISIBLE_TYPE);
        }

        if (ObjectUtil.isBlank(vo.getCommissionRate())) {
            float commissionRate = (float) orderCopyCommissionRateDefaultValue / (float) 100;
            vo.setCommissionRate(commissionRate);
        }

        //不能大于配置文件最大值
        if (vo.getCommissionRate() > orderCopyCommissionRateMaxValue) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_COMMISSION_RATE);
        }

        vo.setCommissionRate((float) NumberUtil.div(Double.valueOf(vo.getCommissionRate()), 100, 2));

        ResultBO<?> resultBO = userInfoCacheService.checkToken(vo.getToken());
        if(resultBO.isError())             	
           	return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();

        ResultBO<?> validateBO = validateOrderCopy(vo.getOrderCode(), userInfoBO,null);

        if (validateBO.isError()) {
            return validateBO;
        }

        OrderBaseInfoBO orderBaseInfoBO = (OrderBaseInfoBO) validateBO.getData();

        //如果从未发单 新增发单用户几率 并且查询出发单用户信息
        Integer count = mUserIssueInfoService.findUserIssueInfoCountByUserId(Long.valueOf(userInfoBO.getId()));
        if (ObjectUtil.isBlank(count)) {
            MUserIssueInfoVO mUserIssueInfoVO = new MUserIssueInfoVO();
            mUserIssueInfoVO.setUserId(userInfoBO.getId());
            mUserIssueInfoVO.setIssueNum(0); //默认发单次数为0
            mUserIssueInfoVO.setIssueAmount(0f);
            int total = mUserIssueInfoService.insert(mUserIssueInfoVO);
            logger.info("新增记录发单用户记录【total】" + total);


        }
        MUserIssueInfoBO bo = mUserIssueInfoService.findUserIssueInfoBoByUserId(Long.valueOf(userInfoBO.getId()));
        if (ObjectUtil.isBlank(count)) {
            Integer levelTotal = mUserIssueLevelService.getUserIssueLevel(bo.getId());
            if (ObjectUtil.isBlank(levelTotal)) {
                MUserIssueLevelVO mUserIssueLevelVO = new MUserIssueLevelVO();
                mUserIssueLevelVO.setLotteryCode((short) LotteryEnum.Lottery.FB.getName());
                mUserIssueLevelVO.setLevel(0);
                mUserIssueLevelVO.setIsAutomatic(0);
                mUserIssueLevelVO.setUserIssueInfoId(bo.getId());
                mUserIssueLevelService.addUserIssueLevel(mUserIssueLevelVO);

                mUserIssueLevelVO = new MUserIssueLevelVO();
                mUserIssueLevelVO.setLotteryCode((short) LotteryEnum.Lottery.BB.getName());
                mUserIssueLevelVO.setLevel(0);
                mUserIssueLevelVO.setIsAutomatic(0);
                mUserIssueLevelVO.setUserIssueInfoId(bo.getId());
                mUserIssueLevelService.addUserIssueLevel(mUserIssueLevelVO);
            }
        }


//        //todo 为了测试全给100元
//        if (ObjectUtil.isBlank(orderBaseInfoBO.getMaxBonus())) {
//            orderBaseInfoBO.setMaxBonusStr("100-100");
//        }
        //最高回报率 需要计算。
        vo.setMaxRoi(IssueUtil.getMaxBackRate(orderBaseInfoBO.getMaxBonus(), orderBaseInfoBO.getOrderAmount()).floatValue());

        //设置发单记录 发单的用户发单表id
        vo.setUserIssueId(bo.getId());
        if (!ObjectUtil.isBlank(bo.getHitRateTemp())) {
            vo.setHitRate(bo.getHitRateTemp().floatValue());
        }

        vo.setContinueHit(bo.getContinueHitStr());
        vo.setRecentRecord(bo.getRecentRecordTemp());

        if (!ObjectUtil.isBlank(vo.getRecommendReason())) {
            if (vo.getRecommendReason().length() >= 1500) {
                return ResultBO.err(MessageCodeConstants.ORDER_COPY_RECOMMEND_REASON_LENGTH);
            }
            vo.setRecommendReason(new String(Base64.encodeBase64(vo.getRecommendReason().getBytes("UTF-8"))));
        }

        int isTrue = orderIssueInfoDaoMapper.insertSelective(new OrderIssueInfoPO(vo));

        if (isTrue > 0) {
            // 更新订单状态 为已推单
            OrderInfoVO orderInfoVO = new OrderInfoVO();
            orderInfoVO.setOrderType(OrderEnum.OrderTypeEnum.YTD.getValue());
            orderInfoVO.setId(orderBaseInfoBO.getId().longValue());
            orderInfoDaoMapper.updOrderType(orderInfoVO);

            if (orderBaseInfoBO.getOrderStatus() == OrderEnum.OrderStatus.TICKETED.getValue()) {
                OrderCopyViewBO orderCopyViewBO = orderIssueInfoDaoMapper.queryOrderInfoStatistics(userInfoBO.getId());
                logger.info("更新抄单用户记录：" + JsonUtil.objectToJson(orderCopyViewBO) + ";请求用户id:" + userInfoBO.getId() + ";订单编号：" + orderInfoVO.getOrderCode());
                if (orderCopyViewBO.getIssueCount() != 0) {
                    orderCopyViewBO.setUserIssueId(bo.getId());
                    orderIssueInfoDaoMapper.updateUserIssueInfo(orderCopyViewBO);
                }
            }
            MessageModel messageModel = new MessageModel();
            OperateNodeMsg msg = new OperateNodeMsg();
            msg.setNodeId(17);
            msg.setNodeData(userInfoBO.getId() + SymbolConstants.SEMICOLON + orderBaseInfoBO.getOrderCode());
            messageModel.setKey("nodeMsgSend");
            messageModel.setMessage(msg);
            messageModel.setMessageSource("lotto");
            messageProvider.sendMessage(msgQueue, messageModel);
            return ResultBO.ok();
        }

        return ResultBO.err();
    }


    @Override
    public ResultBO<?> queryIssueInfo(final QueryVO queryVO) {
        PagingBO<QueryBO> pageData = pageService.getPageData(queryVO, new ISimplePage<QueryBO>() {

            @Override
            public int getTotal() {
                return orderIssueInfoDaoMapper.selectByConditionCount(queryVO);
            }

            @Override
            public List<QueryBO> getData() {
                return orderIssueInfoDaoMapper.selectByCondition(queryVO);
            }

        });
        if(Constants.NUM_6==queryVO.getQueryType()){//抄单详情的更多推荐，当前专家没有可抄单，取其他专家可抄前五条（命中率排序）
            if(pageData.getTotal()==Constants.NUM_0){
                //其他专家推荐
                queryVO.setIssueUserId(null);
                queryVO.setPageIndex(Constants.NUM_0);
                queryVO.setPageSize(Constants.NUM_5);

                queryVO.setSortField("c.hit_rate");
                queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());

                pageData = pageService.getPageData(queryVO, new ISimplePage<QueryBO>() {

                    @Override
                    public int getTotal() {
                        return orderIssueInfoDaoMapper.selectByConditionCount(queryVO);
                    }

                    @Override
                    public List<QueryBO> getData() {
                        return orderIssueInfoDaoMapper.selectByCondition(queryVO);
                    }

                });
            }
        }
        List<QueryBO> list = pageData.getData();
        logger.info("查询到实单列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询到实单列表信息：detailList=" + list.size() + " 条");

        /**处理 抄单人、近期战绩、发单时间、连红*/
        if (!ObjectUtil.isBlank(list)) {
            for (QueryBO bean : list) {
                if (queryVO.getQueryType() == 3 || queryVO.getQueryType() == 4) {
                    List<Map<String, String>> listBetContent = orderIssueInfoDaoMapper.getOrderDetailPlanContentByOrderCode(Arrays.asList(bean.getOrderCode()));
                    if (!ObjectUtil.isBlank(listBetContent)) {
                    	bean.setBetContent(listBetContent.get(0).get("betContent"));
//                        bean.setPassway(IssueUtil.getPasswayFromBetContent(listBetContent.get(0).get("betContent")));
                    }
                }
            }
        }
        /** 获取 过关方式*/
        return ResultBO.ok(list);
    }

    /**
     * 查询方案详情
     *
     * @return
     */
    @Override
    public OrderCopyInfoBO findOrderCopyIssueInfoBOById(Long id) {
        return orderIssueInfoDaoMapper.findOrderCopyInfoBOById(id);
    }


    @Override
    public int queryIssueInfoCount(QueryVO queryVO) {
        return orderIssueInfoDaoMapper.selectByConditionCount(queryVO);
    }


    @Override
    public int getDynamicUpdateCount() {
        return orderIssueInfoDaoMapper.getDynamicUpdateCount();
    }

    @Override
    public ResultBO<?> queryByQueryTypeThree(final QueryVO queryVO) {
        long begin = System.currentTimeMillis();
        PagingBO<QueryBO> pageData = pageService.getPageData(queryVO, new ISimplePage<QueryBO>() {

            @Override
            public int getTotal() {
                return orderIssueInfoDaoMapper.selectByQueryTypeCount(queryVO);
            }

            @Override
            public List<QueryBO> getData() {
                return orderIssueInfoDaoMapper.selectByQueryType(queryVO);
            }

        });
        List<QueryBO> list = pageData.getData();
        logger.info("查询到实单列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询到实单列表信息：detailList=" + list.size() + " 条");

        /**处理 抄单人、近期战绩、发单时间、连红*/
        List<String> listOrderCode = new ArrayList<String>();
        if (!ObjectUtil.isBlank(list)) {
            for (QueryBO bean : list) {
                if (queryVO.getQueryType() == 3 || queryVO.getQueryType() == 4) {
                    listOrderCode.add(bean.getOrderCode());
                }
            }
        }
        if(!ObjectUtil.isBlank(list)){
            List<Map<String, String>> listBetContent = orderIssueInfoDaoMapper.getOrderDetailPlanContentByOrderCode(listOrderCode);
            for(Map<String,String> map: listBetContent){
                for (QueryBO bean : list){
                    if(map.get("orderCode").equals(bean.getOrderCode())){
                        bean.setBetContent(map.get("betContent"));
                        break;
                    }
                }
            }
        }
        logger.info("专家实单列表查询耗时：" + (System.currentTimeMillis() - begin)/1000 + "秒");
        return ResultBO.ok(list);
    }


    @Override
    public int queryByQueryTypeThreeCount(QueryVO queryVO) {
        return orderIssueInfoDaoMapper.selectByQueryTypeCount(queryVO);
    }


    /**
     * 验证订单是否能够抄单
     *
     * @param orderCode
     * @return
     */
    @Override
    public ResultBO<?> validateOrderCopy(String orderCode, UserInfoBO userInfoBO,OrderBaseInfoBO orderBaseInfoBO) throws Exception {
        int orderIssueBOTotal = orderIssueInfoDaoMapper.findOrderIssueBOCountByOrderCode(orderCode);

        if (orderIssueBOTotal > 0) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_ISSUE_TRUE);
        }

        //跟单不能够发单
        int followTotal = orderFollowedInfoDaoMapper.selectCountByOrderCode(orderCode);
        if (followTotal > 0) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_NOT_FOLLOW);
        }


        //查询发单表 订单信息
        if(ObjectUtil.isBlank(orderBaseInfoBO)){
            orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderCode, userInfoBO.getId());
        }

        if (ObjectUtil.isBlank(orderBaseInfoBO)) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_BY_USER);
        }

        if (!LotteryEnum.getLottery(orderBaseInfoBO.getLotteryCode()).name().equals(LotteryEnum.LotteryPr.JJC.name())) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_SPORT_LOTTERY);
        }

        //判断订单是否已经支付
        if (orderBaseInfoBO.getPayStatus() != OrderEnum.PayStatus.SUCCESS_PAY.getValue()) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_PAY);
        }

        //判断订单是否已经支付
        if (orderBaseInfoBO.getOrderStatus() > OrderEnum.OrderStatus.TICKETED.getValue()) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_TICKETED_ERROR, OrderEnum.OrderStatus.parseOrderStatus(orderBaseInfoBO.getOrderStatus()).getDesc());
        }

        //发单支付时间必须小于订单截止时间
        if (ObjectUtil.isNotNull(orderBaseInfoBO.getEndLocalTime()) && orderBaseInfoBO.getEndLocalTime().getTime() < new Date().getTime()) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_PAY_TIME_GT_NOW);
        }

        //判断订单是否已经是代购
        if (orderBaseInfoBO.getBuyType() != OrderEnum.BuyType.BUY.getValue()) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_BUY);
        }

        //判断订单是否是单式上传
        if (!ObjectUtil.isBlank(orderBaseInfoBO.getCategoryId()) && (orderBaseInfoBO.getCategoryId().equals(OrderEnum.Category.SINGLE_UPLOAD.getValue()) || orderBaseInfoBO.getCategoryId().equals(OrderEnum.Category.BONUS.getValue()) || orderBaseInfoBO.getCategoryId().equals(OrderEnum.Category.DCZS.getValue()))) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_SINGLE);
        }

        if (ObjectUtil.isBlank(orderBaseInfoBO.getMaxBonus())) {
            logger.info("【maxBonus】 maxBonus is null ");
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_BY_MAX_BONUS);
        }

        //理论奖金大于本金50%方可发送抄单
        Double profit = NumberUtil.sub(orderBaseInfoBO.getMaxBonus(), orderBaseInfoBO.getOrderAmount());
        Double profitRate = NumberUtil.div(profit, orderBaseInfoBO.getOrderAmount(), Constants.NUM_2);
        if (profitRate < 0.5) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_GT_FIFTY);
        }


        if (orderBaseInfoBO.getLotteryCode().equals(LotteryEnum.Lottery.FB.getName())) {

        } else if (orderBaseInfoBO.getLotteryCode().equals(LotteryEnum.Lottery.BB.getName())) {
            //竞蓝只推胜平负玩法
//            if (!orderBaseInfoBO.getLotteryChildCode().equals(LotteryChildEnum.LotteryChild.ID_JCLQ_SF.getValue())) {
//                return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_JCZQ_SF);
//            }
        } else { //其他玩法现在不能抄单
            return ResultBO.err();
        }

        return ResultBO.ok(orderBaseInfoBO);
    }


    @Override
    public List<Map<String, String>> getOrderDetailPlanContentByOrderCode(List<String> listOrderCode) {
        return orderIssueInfoDaoMapper.getOrderDetailPlanContentByOrderCode(listOrderCode);
    }


    @Override
    public List<Map<Integer, Long>> getNumOfOrderIssue(List<Integer> listUserIssueIds) {
        return orderIssueInfoDaoMapper.getNumOfOrderIssue(listUserIssueIds);
    }


}
