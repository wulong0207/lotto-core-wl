package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hhly.lottocore.remote.sportorder.service.IOrderService;
import com.hhly.skeleton.base.util.NumberUtil;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderFollowedInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.po.OrderFollowedInfoPO;
import com.hhly.lottocore.remote.ordercopy.service.OrderFollowedInfoService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.OrderNoUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderDetailInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.FollowedDetailsBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.OrderFollowedInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author lgs on
 * @version 1.0
 * @desc 抄单之跟单service impl
 * @date 2017/10/9.
 * @company 益彩网络科技有限公司
 */
@Service
public class OrderFollowedInfoServiceImpl implements OrderFollowedInfoService {
	private static Logger logger = LoggerFactory.getLogger(OrderFollowedInfoServiceImpl.class);
	
	@Autowired
	private IPageService pageService;

    @Autowired
    private OrderFollowedInfoDaoMapper orderFollowedInfoDaoMapper;

    @Autowired
    private OrderIssueInfoDaoMapper orderIssueInfoDaoMapper;

    @Autowired
    private OrderInfoDaoMapper orderInfoDaoMapper;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private IOrderService orderService;
    /**
     * 用户进行跟单
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> orderFollowed(OrderFollowedInfoVO vo) throws Exception {
        OrderIssueInfoBO orderIssueInfoBO = orderIssueInfoDaoMapper.findIssueBOById(vo.getOrderIssueId());

        // 发单列表不能为null
        if (ObjectUtil.isBlank(orderIssueInfoBO) || ObjectUtil.isBlank(orderIssueInfoBO.getOrderCode())) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_FIND);
        }

        //如果为null 默认设置一倍
        if (ObjectUtil.isBlank(vo.getMultipleNum())) {
            vo.setMultipleNum(Constants.NUM_1);
        }

        OrderInfoBO orderInfoBO = orderInfoDaoMapper.getOrderInfo(orderIssueInfoBO.getOrderCode());

        //订单必须出票才能够跟单
        if (!orderInfoBO.getPayStatus().equals(OrderEnum.PayStatus.SUCCESS_PAY.getValue()) || orderInfoBO.getOrderStatus() != OrderEnum.OrderStatus.TICKETED.getValue()) {
            return ResultBO.err(MessageCodeConstants.ORDER_COPY_IS_NOT_TICK);
        }


        ResultBO<?> resultBO = userInfoCacheService.checkToken(vo.getToken());
        if(resultBO.isError())             	
           	return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
        //组装订单信息
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        orderInfoVO.setBuyScreen(orderInfoBO.getBuyScreen());
        orderInfoVO.setBuyType(orderInfoBO.getBuyType());
        orderInfoVO.setChannelId(orderInfoBO.getChannelId());
        orderInfoVO.setIsDltAdd(orderInfoBO.getLottoAdd());
        orderInfoVO.setLotteryCode(orderInfoBO.getLotteryChildCode());
        orderInfoVO.setLotteryChildCode(orderInfoBO.getLotteryChildCode());
        orderInfoVO.setLotteryIssue(orderInfoBO.getLotteryIssue());
        orderInfoVO.setPlatform(orderInfoBO.getPlatform());
        orderInfoVO.setToken(vo.getToken());
        // 更新订单状态 为跟单
        orderInfoVO.setOrderType(OrderEnum.OrderTypeEnum.YDD.getValue());
        orderInfoVO.setMultipleNum(vo.getMultipleNum());
        orderInfoVO.setOrderCode(OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_D));
        orderInfoVO.setCategoryId(orderInfoBO.getCategoryId());

        String maxBonus = NumberUtil.mul(NumberUtil.div(orderInfoBO.getMinBonus(), orderInfoBO.getMultipleNum(), 2), vo.getMultipleNum()) + "-" + NumberUtil.mul(NumberUtil.div(orderInfoBO.getMaxBonus(), orderInfoBO.getMultipleNum(), 2), vo.getMultipleNum());
        orderInfoVO.setMaxBonus(maxBonus);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderCode(orderIssueInfoBO.getOrderCode());
        orderDetailVO.setUserId(orderInfoBO.getUserId());
        List<OrderDetailInfoBO> orderDetailInfoBOS = orderInfoDaoMapper.queryOrderDetailInfo(orderDetailVO);

        List<OrderDetailVO> orderDetailVOS = new ArrayList<>();
        //组装订单详情
        for (OrderDetailInfoBO bo : orderDetailInfoBOS) {
            OrderDetailVO tempVO = new OrderDetailVO();
            tempVO.setAmount(bo.getAmount().doubleValue());
            tempVO.setBuyNumber(bo.getBetNum());
            tempVO.setMultiple(bo.getMultipleNum());
            tempVO.setPlanContent(bo.getBetContent());
            tempVO.setLotteryChildCode(bo.getLotteryChildCode());
            tempVO.setCodeWay(bo.getCodeWay());
            tempVO.setContentType(bo.getContentType());
            if (bo.getBetContent().contains("1_1")) {
                orderInfoVO.setTabType(Constants.NUM_2);
            } else {
                orderInfoVO.setTabType(Constants.NUM_1);
            }
            orderDetailVOS.add(tempVO);
        }


        orderInfoVO.setOrderDetailList(orderDetailVOS);

        //如果为1倍直接跟单直接赋值
        if (orderInfoBO.getMultipleNum() == Constants.NUM_1) {
            orderInfoVO.setOrderAmount(new BigDecimal(vo.getMultipleNum()).multiply(new BigDecimal(orderInfoBO.getOrderAmount())).doubleValue());
        } else {
            Integer oldOrderMultipleNum = orderInfoBO.getMultipleNum();
            Double orderOrderAmount = orderInfoBO.getOrderAmount();
            //计算单倍投注金额
            BigDecimal oneMultipleNumAmount = new BigDecimal(orderOrderAmount).divide(new BigDecimal(oldOrderMultipleNum));
            orderInfoVO.setOrderAmount(new BigDecimal(vo.getMultipleNum()).multiply(oneMultipleNumAmount).doubleValue());
        }

        //订单入库
        ResultBO<?> orderResultBO = orderService.addOrder(orderInfoVO);
        if (orderResultBO.isError()) {
            return orderResultBO;
        }

        //设置跟单表信息
        vo.setOrderCode(orderInfoVO.getOrderCode());
        vo.setUserId(userInfoBO.getId());
        vo.setNickName(ObjectUtil.isBlank(userInfoBO.getNickname()) ? userInfoBO.getAccount() : userInfoBO.getNickname());
        vo.setDataStatus(OrderCopyEnum.DataStatusEnum.NOT_TICK.getValue());
        orderFollowedInfoDaoMapper.insertSelective(new OrderFollowedInfoPO(vo));
        return orderResultBO;
    }
    
	@Override
	public ResultBO<?> queryFollowedDetails(final QueryVO queryVO) {
		PagingBO<FollowedDetailsBO> pageData = pageService.getPageData(queryVO, new ISimplePage<FollowedDetailsBO>() {

            @Override
            public int getTotal() {
                return orderFollowedInfoDaoMapper.queryFollowedDetailsCount(queryVO);
            }

            @Override
            public List<FollowedDetailsBO> getData() {
                return orderFollowedInfoDaoMapper.queryFollowedDetails(queryVO);
            }

        });
        logger.info("查询到关注信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询到关注信息：detailList=" + pageData.getData().size() + " 条");
        return ResultBO.ok(pageData.getData());
	}
}

