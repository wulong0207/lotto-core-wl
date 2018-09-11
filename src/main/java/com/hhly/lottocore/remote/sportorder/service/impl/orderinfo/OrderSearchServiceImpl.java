package com.hhly.lottocore.remote.sportorder.service.impl.orderinfo;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.hhly.lottocore.persistence.group.dao.OrderGroupContentMapper;
import com.hhly.lottocore.persistence.group.dao.OrderGroupMapper;
import com.hhly.skeleton.base.util.*;
import com.hhly.skeleton.lotto.base.group.bo.OrderDetailGroupInfoBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupContentBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderMyGroupBO;
import com.hhly.skeleton.lotto.base.order.bo.*;
import com.hhly.skeleton.task.order.vo.OrderChannelVO;
import com.hhly.skeleton.user.bo.KeywordBO;
import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryWinningDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderAddDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderFlowInfoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueLinkDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.OrderFollowedInfoDaoMapper;
import com.hhly.lottocore.persistence.sport.dao.SportAgainstInfoDaoMapper;
import com.hhly.lottocore.persistence.ticket.dao.TicketInfoDaoMapper;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.ordercopy.service.OrderIssueInfoService;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.SportsOrderValidate;

import com.hhly.skeleton.activity.bo.OrderInfoDetailBo;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.OrderEnum.AddStatus;
import com.hhly.skeleton.base.common.OrderEnum.Category;
import com.hhly.skeleton.base.common.OrderEnum.OrderAddStopType;
import com.hhly.skeleton.base.common.OrderEnum.OrderStatus;
import com.hhly.skeleton.base.common.OrderEnum.OrderWinningStatus;
import com.hhly.skeleton.base.common.PokerEnum;
import com.hhly.skeleton.base.common.SingleUploadEnum;
import com.hhly.skeleton.base.common.TicketEnum;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.PayConstants.PayStatusEnum;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.constants.UserConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.issue.entity.NewIssueBO;
import com.hhly.skeleton.base.page.AbstractStatisticsPage;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.cms.lotterymgr.bo.LotteryTypeBO;
import com.hhly.skeleton.cms.lotterymgr.bo.LotteryWinningBO;
import com.hhly.skeleton.cms.lotterymgr.vo.LotteryTypeVO;
import com.hhly.skeleton.cms.lotterymgr.vo.LotteryWinningVO;
import com.hhly.skeleton.cms.ticketmgr.bo.TicketInfoSingleBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueOfficialTimeBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotWinningBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.vo.ActivityOrderQueryInfoVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.OrderStatisticsQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.UserChaseDetailQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.UserNumOrderDetailQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.UserSportOrderDetailQueryVO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.OrderFollowedInfoBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcOldDataBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 订单查询业务
 * @date 2017/3/15 16:41
 * @company 益彩网络科技公司
 */
@Service("orderSearchService")
public class OrderSearchServiceImpl implements IOrderSearchService {

	@Resource
	private OrderIssueInfoService orderIssueInfoService;

	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;

	@Autowired
	private IPageService pageService;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private IJcDataService jcDataService;

	@Autowired
	private OrderAddDaoMapper orderAddDaoMapper;

	@Autowired
	private TicketInfoDaoMapper ticketInfoDaoMapper;

	@Autowired
	private LotteryWinningDaoMapper lotteryWinningDaoMapper;

	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;

	@Autowired
	private LotteryTypeDaoMapper lotteryTypeDaoMapper;

	@Autowired
	private LotteryChildDaoMapper lotteryChildDaoMapper;

	@Autowired
	private SportAgainstInfoDaoMapper sportAgainstInfoDaoMapper;

	@Autowired
	private OrderFlowInfoMapper orderFlowInfoMapper;

	@Autowired
	private UserInfoCacheService userInfoCacheService;

	@Autowired
	private OrderFollowedInfoDaoMapper orderFollowedInfoDaoMapper;

	@Autowired
	private MUserIssueLinkDaoMapper mUserIssueLinkDaoMapper;

	@Autowired
	private OrderGroupMapper orderGroupMapper;

	@Autowired
	private OrderGroupContentMapper orderGroupContentMapper;
	
	@Autowired
	private UserUtil userUtil;

	@Value("${single_upload_dir}")
	private String singleUploadDir;

	/** 7牛accessKey  **/
	@Value("${accessKey}")
	private String accessKey;
	/** 7牛secretKey **/
	@Value("${secretKey}")
	private String secretKey;
	/** bucketName **/
	@Value("${bucketName}")
	private String bucketName;
	/** 允许批量上传文件数量  **/
	@Value("${uploadLimit}")
	private Integer uploadLimit;
	/** 允许上传文件类型 **/
	@Value("${fileType}")
	private String fileType;
	/** 文件访问路径  **/
	@Value("${uploadURL}")
	private String uploadURL;
	/**域名和文件名中间的路径*/
	@Value("${savePath}")
	private String savePath;
	/**允许批量上传文件大小*/
	@Value("${limitSize}")
	private String limitSize;

    @Autowired
	@Qualifier("sportsOrderValidate")
    private SportsOrderValidate sportsOrderValidate;

    Logger logger = Logger.getLogger(OrderSearchServiceImpl.class);



	@Override
	public ResultBO<?> queryOrderListInfo(final OrderQueryVo orderQueryVo) throws Exception {
		Integer userId =0;
		if(Constants.NUM_2 !=orderQueryVo.getSource()){
			ResultBO<?> result = userInfoCacheService.checkToken(orderQueryVo.getToken());
			if(result.isError())
				return result;
			UserInfoBO userInfo = (UserInfoBO) result.getData();
			userId = userInfo.getId();
		}else{
			userId = orderQueryVo.getUserId();
		}
		if(orderQueryVo.getBuyType()==Constants.NUM_2 && (orderQueryVo.getType()==Constants.NUM_2 || orderQueryVo.getType()==Constants.NUM_3)){//追号计划没有中奖和未开奖的状态
			PagingBO<OrderBaseInfoBO> pageData = new PagingBO<>();
			pageData.setData(new ArrayList<OrderBaseInfoBO>());
			pageData.setTotal(0);
			return ResultBO.ok(pageData);
		}
		orderQueryVo.setUserId(userId);
		PagingBO<OrderBaseInfoBO> pageData = pageService.getPageData(orderQueryVo,
				new AbstractStatisticsPage<OrderBaseInfoBO>() {
			        int total = 0;
					@Override
					public int getTotal() {
						if (orderQueryVo.getBuyType().equals(Constants.NUM_4)) {
							total = orderInfoDaoMapper.querySingleCopyOrderListCount(orderQueryVo);
						} else {
							total = orderInfoDaoMapper.queryOrderListInfoCount(orderQueryVo);
						}
						orderQueryVo.setTotal(total);
						return total;
					}

					@Override
					public List<OrderBaseInfoBO> getData() {
						List<OrderBaseInfoBO> result;
						if (orderQueryVo.getBuyType().equals(Constants.NUM_4)) {
							//抄单
							result = orderInfoDaoMapper.querySingleCopyOrderList(orderQueryVo);
						} else {
							result = orderInfoDaoMapper.queryOrderListInfo(orderQueryVo);
						}
						if (!ObjectUtil.isBlank(result)) {
							Map<Integer, LotteryBO> allLotteryInfoMap = new HashMap<>();
							// 从缓存取出彩种信息
							Map<Integer, LotteryBO> lotteryInfoMap = null;//去掉缓存
							for (OrderBaseInfoBO orderListInfoBO : result) {
								setOrderListInfo(allLotteryInfoMap, lotteryInfoMap, orderListInfoBO, orderQueryVo,Constants.NUM_1);
							}
						}
						return result;
					}

					@Override
					public Object getOther() {
						return null;
					}
				});
		if(orderQueryVo.getTotalFlag()){
			OrderPageBO<OrderBaseInfoBO> orderPageBO = new OrderPageBO<OrderBaseInfoBO>();
			orderPageBO.setTotal(pageData.getTotal());
			orderPageBO.setData(pageData.getData());
			orderPageBO.setOther(pageData.getOther());
			orderQueryVo.setType(1);
			orderPageBO.setDealOrderTotal(orderInfoDaoMapper.queryOrderListInfoCount(orderQueryVo));
			orderQueryVo.setType(2);
			orderPageBO.setWinOrderTotal(orderInfoDaoMapper.queryOrderListInfoCount(orderQueryVo));
			return ResultBO.ok(orderPageBO);
		}
		return ResultBO.ok(pageData);
	}

	@Override
	public ResultBO<?> queryAddOrderListInfo(final OrderQueryVo orderQueryVo) throws Exception {
		ResultBO<?> result = userInfoCacheService.checkToken(orderQueryVo.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		orderQueryVo.setUserId(userInfo.getId());
		PagingBO<OrderBaseInfoBO> pageData = pageService.getPageData(orderQueryVo,
				new AbstractStatisticsPage<OrderBaseInfoBO>() {
					@Override
					public int getTotal() {
						int total = orderInfoDaoMapper.queryAddOrderListCount(orderQueryVo);
						orderQueryVo.setTotal(total);
						return total;
					}

					@Override
					public List<OrderBaseInfoBO> getData() {
						List<OrderBaseInfoBO> result = orderInfoDaoMapper.queryAddOrderList(orderQueryVo);
						if (!ObjectUtil.isBlank(result)) {
							Map<Integer, LotteryBO> allLotteryInfoMap = new HashMap<>();
							// 从缓存取出彩种信息
							Map<Integer, LotteryBO> lotteryInfoMap = null;//去掉缓存
							for (OrderBaseInfoBO orderListInfoBO : result) {
								setOrderListInfo(allLotteryInfoMap, lotteryInfoMap, orderListInfoBO, orderQueryVo,Constants.NUM_1);
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
	 * 设置订单列表其他信息
	 * 
	 * @param allLotteryInfoMap
	 * @param lotteryInfoMap
	 * @param orderListInfoBO
	 * @param orderQueryVo
	 * @param source
	 *            1:订单列表 2：其他
	 */
	private void setOrderListInfo(Map<Integer, LotteryBO> allLotteryInfoMap, Map<Integer, LotteryBO> lotteryInfoMap,
			OrderBaseInfoBO orderListInfoBO, OrderQueryVo orderQueryVo, int source) {
		orderStatusTransfer(orderListInfoBO);
		Integer lotteryCode = orderListInfoBO.getLotteryCode();
		// 处理之前的异常数据,因为之前的单lotteryCode存是子编码，这样会查不到记录
		lotteryCode = Integer.valueOf(String.valueOf(lotteryCode).substring(Constants.NUM_0, Constants.NUM_3));
		// 设置彩种名称
		LotteryBO lotteryBO = lotteryInfoMap == null ? null : lotteryInfoMap.get(lotteryCode);
		if (lotteryBO == null) {
			if (!allLotteryInfoMap.containsKey(lotteryCode)) {
				allLotteryInfoMap.putAll(getLotteryInfo(lotteryCode));
			}
			// 设置彩种名称
			lotteryBO = allLotteryInfoMap.get(lotteryCode);
		}
		if (lotteryBO != null) {
			orderListInfoBO.setLotteryName(lotteryBO.getLotteryName());
			orderListInfoBO.setLotteryChildName(getLotteryChildName(lotteryBO.getListLotChildBO(), orderListInfoBO.getLotteryChildCode(),
					orderListInfoBO.getLotteryCode()));
		}
		// 设置彩种类型
		orderListInfoBO.setLotteryType(Constants.getLotteryType(lotteryCode));
		//处理合买订单状态和加奖,合买类型
		setOrderGroupInfo(orderListInfoBO);
		//统一返回订单相关的状态显示
		buildAllOrderInfo(orderListInfoBO);
		if (Constants.NUM_2 == source) {
			orderListInfoBO.setLotteryTime(null);
			orderListInfoBO.setIssueOfficialTimeBO(null);
		}

		//设置推单标识  前端只有未推，已推，其余不显示
		setOrderType(orderListInfoBO, orderQueryVo.getUserId(),true);
	}

	private void setOrderGroupInfo(OrderBaseInfoBO orderListInfoBO) {
		if(orderListInfoBO.getBuyType().intValue() == OrderEnum.BuyType.BUY_TOGETHER.getValue()){
			//1.合买状态
			OrderGroupContentBO orderGroupContentBO = orderGroupContentMapper.queryOrderGroupContentById(orderListInfoBO.getOrderGroupContentId());
			if(ObjectUtil.isBlank(orderGroupContentBO)){
				return;
			}
			OrderGroupBO orderGroupBO = orderGroupMapper.queryOrderGroupByOrderCode(orderListInfoBO.getOrderCode());
			if(ObjectUtil.isBlank(orderGroupBO)){
				return;
			}
			orderListInfoBO.setGrpbuyStatus(orderGroupBO.getGrpbuyStatus());
			//2.加奖和购买类型
			OrderGroupContentBO groupContentBO = orderGroupContentMapper.findOrderGroupRecord(orderListInfoBO.getOrderCode());//发单人记录
			if(ObjectUtil.isBlank(groupContentBO)){
				return;
			}
			if(groupContentBO.getId().intValue() == orderListInfoBO.getOrderGroupContentId().intValue()){//当前单是发单记录
				orderListInfoBO.setBuyFlag(Constants.NUM_1);
				if(orderGroupBO.getBonusFlag().intValue() == Constants.NUM_2){//给发起人不需要处理，直接取订单的加奖,给所有人，直接取跟单表记录
					orderListInfoBO.setAddedBonus(orderGroupContentBO.getAddedBonus());
					orderListInfoBO.setGetRedAmount(orderGroupContentBO.getSiteAddedBonus());
				}
			}else {//当前单是跟单
				orderListInfoBO.setBuyFlag(Constants.NUM_2);
				if(orderGroupBO.getBonusFlag().intValue() == Constants.NUM_2){//给所有人，去跟单表
					orderListInfoBO.setAddedBonus(orderGroupContentBO.getAddedBonus());
					orderListInfoBO.setGetRedAmount(orderGroupContentBO.getSiteAddedBonus());
				}else{//给发起人，清空加奖
					orderListInfoBO.setAddedBonus(null);
					orderListInfoBO.setGetRedAmount(null);
				}
			}
		}
	}

	/**
	 * 设置前端推单标识
	 * @param orderListInfoBO
	 * @param userId
	 */
	private void setOrderType(OrderBaseInfoBO orderListInfoBO, Integer userId,boolean isOrderList) {
		//为了前端统一，订单类型都统一到buyType一个字段
		if (orderListInfoBO.getBuyType().shortValue() != OrderEnum.BuyType.BUY_TOGETHER.getValue() && orderListInfoBO.getOrderType()!=null && orderListInfoBO.getOrderType() == Constants.NUM_1) {
            //如果订单状态是未推单
            try {
                UserInfoBO userInfoBO = new UserInfoBO();
                userInfoBO.setId(userId);
                ResultBO<?> validateBO = orderIssueInfoService.validateOrderCopy(orderListInfoBO.getOrderCode(), userInfoBO, null);
                if (validateBO.isError()) {
                    orderListInfoBO.setOrderType(Constants.NUM_0);
                }/*else{
                	if(isOrderList){//只针对订单列表有改变
						orderListInfoBO.setBuyType(Constants.NUM_5);//推单（可以推）
					}
				}*/
            } catch (Exception e) {
                logger.error("验证订单是否能推单异常：" + e.getMessage());
            }
        }
		/*if (isOrderList && orderListInfoBO.getOrderType()!=null && orderListInfoBO.getOrderType() == Constants.NUM_2) {//只针对订单列表有改变
			orderListInfoBO.setBuyType(Constants.NUM_6);//已推单
		}*/
		//合买不支持抄单
		if(orderListInfoBO.getBuyType().shortValue() == OrderEnum.BuyType.BUY_TOGETHER.getValue()){
			orderListInfoBO.setOrderType(Constants.NUM_0);
		}
	}

	/**
	 * 转换普通代购订单统一状态(订单状态时使用)
	 *
	 * @param orderListInfoBO
	 * @return
	 */
	private OrderEnum.OrderUnityStatus convertOrderUnionStatus(OrderBaseInfoBO orderListInfoBO) {
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderListInfoBO.getBuyType().shortValue()) {
			return null;
		}
		Short payStatus = orderListInfoBO.getPayStatus().shortValue();
		Short orderStatus = orderListInfoBO.getOrderStatus().shortValue();

		if(orderListInfoBO.getBuyType().shortValue() == OrderEnum.BuyType.BUY_TOGETHER.getValue()) {
			//处理合买状态
			if(orderListInfoBO.getGrpbuyStatus().shortValue() == OrderEnum.GRPBuyStatus.RECRUIT.getValue()){//招募中
				return OrderEnum.OrderUnityStatus.RECRUIT;
			}
			if(orderListInfoBO.getGrpbuyStatus().shortValue() == OrderEnum.GRPBuyStatus.ABORTION.getValue()){//合买流产
				return OrderEnum.OrderUnityStatus.ABORTION;
			}
			if(orderListInfoBO.getGrpbuyStatus().shortValue() == OrderEnum.GRPBuyStatus.CANCEL_ORDER.getValue()){//合买撤单
				return OrderEnum.OrderUnityStatus.CANCEL_ORDER;
			}
		}else {
			// 支付状态:等待支付 订单状态:待拆票 --> 待支付
			if (payStatus == PayStatusEnum.WAITTING_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderUnityStatus.NO_PAY;
			}
			// 支付状态:支付中 订单状态:待拆票 --> 待支付
			if (payStatus == PayStatusEnum.BEING_PAID.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderUnityStatus.NO_PAY;
			}
			// 支付状态:未支付过期 订单状态:待拆票 --> 未支付过期
			if (payStatus == PayStatusEnum.OVERDUE_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderUnityStatus.NO_PAY_OVERDUE;
			}
			// 支付状态:用户取消 订单状态:待拆票 --> 投注失败
			if (payStatus == PayStatusEnum.USER_CANCELLED_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				orderListInfoBO.setRemark(PayStatusEnum.USER_CANCELLED_PAYMENT.getValue());
				return OrderEnum.OrderUnityStatus.BET_FAIL;
			}
			// 支付状态:支付失败 订单状态:待拆票 --> 投注失败
			if (payStatus == PayStatusEnum.PAYMENT_FAILURE.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				orderListInfoBO.setRemark(PayStatusEnum.PAYMENT_FAILURE.getValue());
				return OrderEnum.OrderUnityStatus.BET_FAIL;
			}
			if (payStatus != PayStatusEnum.PAYMENT_SUCCESS.getKey()) {
				return null;
			}
		}
		// 支付状态:支付成功 订单状态:待拆票, 拆票中, 拆票失败,待出票 --> 等待出票
		if (orderStatus == OrderStatus.WAITING_TICKET.getValue() || orderStatus == OrderStatus.SPLITING_TICKET.getValue()
				|| orderStatus == OrderStatus.SPLITING_FAIL.getValue() || orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
			IssueOfficialTimeBO issueOfficialTimeBO = queryOfficialTime(orderListInfoBO.getLotteryCode());
			orderListInfoBO.setIssueOfficialTimeBO(issueOfficialTimeBO);
			return OrderEnum.OrderUnityStatus.WAITING_TICKET;
		}
		// 支付状态:支付成功 订单状态:出票中，不在送票时间范围内 --> 等待出票，在送票时间范围内 --> 出票中
		if (orderStatus == OrderStatus.TICKETING.getValue()) {
			IssueOfficialTimeBO issueOfficialTimeBO = queryOfficialTime(orderListInfoBO.getLotteryCode());
			orderListInfoBO.setIssueOfficialTimeBO(issueOfficialTimeBO);
			String officialStartTimeStr = issueOfficialTimeBO.getOfficialStartTimeStr();
			String officialEndTimeStr = issueOfficialTimeBO.getOfficialEndTimeStr();
			if (!ObjectUtil.isBlank(officialStartTimeStr) && !ObjectUtil.isBlank(officialEndTimeStr)) {
				String nowTimeStr = DateUtil.convertDateToStr(new Date(), DateUtil.FORMAT_H_M);
				// start <= nowTime <= end出票中
				if (nowTimeStr.compareTo(officialStartTimeStr) >= 0 && nowTimeStr.compareTo(officialEndTimeStr) <= 0) {
					return OrderEnum.OrderUnityStatus.TICKETTING;
				}
			}
			Date officialStartTime = issueOfficialTimeBO.getOfficialStartTime();
			Date officialEndTime = issueOfficialTimeBO.getOfficialEndTime();
			if (officialStartTime != null && officialEndTime != null) {
				Date nowTime = new Date();
				// start <= nowTime <= end出票中
				if (!nowTime.before(officialStartTime) && !nowTime.after(officialEndTime)) {
					return OrderEnum.OrderUnityStatus.TICKETTING;
				}
			}
			return OrderEnum.OrderUnityStatus.WAITING_TICKET;
		}
		// 支付状态:支付成功 订单状态:已出票 --> 投注成功
		if (orderStatus == OrderStatus.TICKETED.getValue()) {
			return OrderEnum.OrderUnityStatus.BET_SUCCESS;
		}
		// 支付状态:支付成功 订单状态:撤单中,出票失败 --> 投注失败
		if (orderStatus == OrderStatus.FAILING_TICKET.getValue() || orderStatus == OrderStatus.WITHDRAWING.getValue()) {
			orderListInfoBO.setRemark(OrderStatus.FAILING_TICKET.getDesc());
			return OrderEnum.OrderUnityStatus.BET_FAIL;
		}
		// 支付状态:支付成功 订单状态:撤单中,出票失败 ,已撤单--> 投注失败
		if (orderStatus == OrderStatus.WITHDRAW.getValue()) {
			return OrderEnum.OrderUnityStatus.BET_FAIL;
		}
		return null;
	}

	/**
	 * 转换普通代购订单统一状态(订单详情时使用)
	 * @param orderListInfoBO
	 * @return
	 */
	private OrderEnum.OrderFlowUnityStatus convertOrderFlowUnionStatus(OrderBaseInfoBO orderListInfoBO,Integer grpbuyStatus) {
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderListInfoBO.getBuyType().shortValue()) {
			return null;
		}
		Short payStatus = orderListInfoBO.getPayStatus().shortValue();
		Short orderStatus = orderListInfoBO.getOrderStatus().shortValue();
		Short buyType = orderListInfoBO.getBuyType().shortValue();


		if(buyType != OrderEnum.BuyType.BUY_TOGETHER.getValue()){
            // 支付状态:等待支付 订单状态:待拆票 --> 待支付
			if (payStatus == PayStatusEnum.WAITTING_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderFlowUnityStatus.NO_PAY;
			}
			// 支付状态:支付中 订单状态:待拆票 --> 待支付
			if (payStatus == PayStatusEnum.BEING_PAID.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderFlowUnityStatus.NO_PAY;
			}
			// 支付状态:未支付过期 订单状态:待拆票 --> 未支付过期
			if (payStatus == PayStatusEnum.OVERDUE_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderFlowUnityStatus.NO_PAY_OVERDUE;
			}
			// 支付状态:用户取消 订单状态:待拆票 --> 用户取消
			if (payStatus == PayStatusEnum.USER_CANCELLED_PAYMENT.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderFlowUnityStatus.NO_PAY_USER_CANCEL;
			}
			// 支付状态:支付失败 订单状态:待拆票 --> 支付失败
			if (payStatus == PayStatusEnum.PAYMENT_FAILURE.getKey() && orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
				return OrderEnum.OrderFlowUnityStatus.PAY_FAIL;
			}
			if (payStatus != PayStatusEnum.PAYMENT_SUCCESS.getKey()) {
				return null;
			}
		}else{//合买
            if(grpbuyStatus.shortValue() == OrderEnum.GRPBuyStatus.RECRUIT.getValue()){//招募中
				return OrderEnum.OrderFlowUnityStatus.RECRUIT;
			}
			if(grpbuyStatus.shortValue() == OrderEnum.GRPBuyStatus.ABORTION.getValue()){//合买流产
				return OrderEnum.OrderFlowUnityStatus.ABORTION;
			}
			if(grpbuyStatus.shortValue() == OrderEnum.GRPBuyStatus.CANCEL_ORDER.getValue()){//合买撤单
				return OrderEnum.OrderFlowUnityStatus.CANCEL_ORDER;
			}
		}
		// 支付状态:支付成功 订单状态:待拆票, 拆票中, 拆票失败,待出票 --> 等待出票
		if (orderStatus == OrderStatus.WAITING_TICKET.getValue() || orderStatus == OrderStatus.SPLITING_TICKET.getValue()
				|| orderStatus == OrderStatus.SPLITING_FAIL.getValue() || orderStatus == OrderStatus.WAITING_SPLIT_TICKET.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.WAITING_TICKET;
		}
		// 支付状态:支付成功 订单状态:出票中，不在送票时间范围内 --> 等待出票，在送票时间范围内 --> 出票中
		if (orderStatus == OrderStatus.TICKETING.getValue()) {
			IssueOfficialTimeBO issueOfficialTimeBO  = orderListInfoBO.getIssueOfficialTimeBO();
			if(issueOfficialTimeBO == null) {
				issueOfficialTimeBO = queryOfficialTime(orderListInfoBO.getLotteryCode());
				orderListInfoBO.setIssueOfficialTimeBO(issueOfficialTimeBO);
			}
			String officialStartTimeStr = issueOfficialTimeBO.getOfficialStartTimeStr();
			String officialEndTimeStr = issueOfficialTimeBO.getOfficialEndTimeStr();
			if (!ObjectUtil.isBlank(officialStartTimeStr) && !ObjectUtil.isBlank(officialEndTimeStr)) {
				String nowTimeStr = DateUtil.convertDateToStr(new Date(), DateUtil.FORMAT_H_M);
				// start <= nowTime <= end出票中
				if (nowTimeStr.compareTo(officialStartTimeStr) >= 0 && nowTimeStr.compareTo(officialEndTimeStr) <= 0) {
					return OrderEnum.OrderFlowUnityStatus.TICKETTING;
				}
			}
			Date officialStartTime = issueOfficialTimeBO.getOfficialStartTime();
			Date officialEndTime = issueOfficialTimeBO.getOfficialEndTime();
			if (officialStartTime != null && officialEndTime != null) {
				Date nowTime = new Date();
				// start <= nowTime <= end出票中
				if (!nowTime.before(officialStartTime) && !nowTime.after(officialEndTime)) {
					return OrderEnum.OrderFlowUnityStatus.TICKETTING;
				}
			}
			return OrderEnum.OrderFlowUnityStatus.WAITING_TICKET;
		}
		// 支付状态:支付成功 订单状态:撤单中,出票失败 --> 出票失败
		if (orderStatus == OrderStatus.FAILING_TICKET.getValue() || orderStatus == OrderStatus.WITHDRAWING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.TICKET_FAIL;
		}
		// 支付状态:支付成功 订单状态:已撤单--> 已退款
		if (orderStatus == OrderStatus.FAILING_TICKET.getValue() || orderStatus == OrderStatus.WITHDRAW.getValue()
				|| orderStatus == OrderStatus.WITHDRAWING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.TICKET_REFUND;
		}
		if (orderStatus != OrderStatus.TICKETED.getValue()) {
			return null;
		}
		Short winningStatus = orderListInfoBO.getWinningStatus().shortValue();
		// 支付状态:支付成功 订单状态:已出票 开奖状态:未开奖 --> 等待开奖
		if (winningStatus == OrderEnum.OrderWinningStatus.NOT_DRAW_WINNING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.WAITING_WINNING;
		}
		// 支付状态:支付成功 订单状态:已出票 开奖状态:未中奖 --> 未中奖
		if (winningStatus == OrderEnum.OrderWinningStatus.NOT_WINNING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.NOT_WINNING;
		}
		// 支付状态:支付成功 订单状态:已出票 开奖状态:已中奖 --> 已中奖
		if (winningStatus == OrderEnum.OrderWinningStatus.WINNING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.WAITING_SEND;
		}
		// 支付状态:支付成功 订单状态:已出票 开奖状态:已派奖 --> 已派奖
		if (winningStatus == OrderEnum.OrderWinningStatus.GET_WINNING.getValue()) {
			return OrderEnum.OrderFlowUnityStatus.SENDED;
		}
		return null;
	}

	/**
	 * 转换追号计划订单统一状态(订单列表)
	 * @param orderListInfoBO
	 * @return
	 */
	private OrderEnum.AddOrderUnityStatus convertAddOrderUnionStatus(OrderBaseInfoBO orderListInfoBO) {
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() != orderListInfoBO.getBuyType().shortValue()) {
			return null;
		}
		Short payStatus = orderListInfoBO.getPayStatus().shortValue();
		Short addStatus = orderListInfoBO.getAddStatus().shortValue();
		// 支付状态:等待支付 追号状态:追号中 --> 待支付
		if (payStatus == PayStatusEnum.WAITTING_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderUnityStatus.NO_PAY;
		}
		// 支付状态:支付中 追号状态:追号中 --> 待支付
		if (payStatus == PayStatusEnum.BEING_PAID.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderUnityStatus.NO_PAY;
		}
		// 支付状态:未支付过期 追号状态:追号中 --> 未支付过期
		if (payStatus == PayStatusEnum.OVERDUE_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderUnityStatus.NO_PAY_OVERDUE;
		}
		// 支付状态:用户取消 追号状态:追号中 --> 投注失败
		if (payStatus == PayStatusEnum.USER_CANCELLED_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			orderListInfoBO.setRemark(PayStatusEnum.USER_CANCELLED_PAYMENT.getValue());
			return OrderEnum.AddOrderUnityStatus.BET_FAIL;
		}
		// 支付状态:支付失败 追号状态:追号中 --> 投注失败
		if (payStatus == PayStatusEnum.PAYMENT_FAILURE.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			orderListInfoBO.setRemark(PayStatusEnum.PAYMENT_FAILURE.getValue());
			return OrderEnum.AddOrderUnityStatus.BET_FAIL;
		}
		if (payStatus != PayStatusEnum.PAYMENT_SUCCESS.getKey()) {
			return null;
		}
		// 支付状态:支付成功 追号状态:追号中 --> 追号中
		if (addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderUnityStatus.ADDING;
		}
		// 支付状态:支付成功 追号状态:追号结束 --> 追号结束
		if (addStatus == AddStatus.CHASE_FINISH.getKey()) {
			return OrderEnum.AddOrderUnityStatus.ADDED_STOP;
		}
		// 支付状态:支付成功 追号状态:追号结束 --> 中奖停追
		if (addStatus == AddStatus.CHASE_STOP.getKey()) {
			return OrderEnum.AddOrderUnityStatus.WINNING_STOP;
		}
		// 支付状态:支付成功 追号状态:用户撤单 --> 追号撤单
		if (addStatus == AddStatus.USER_CANCEL.getKey()) {
			return OrderEnum.AddOrderUnityStatus.ADDED_REVOKE;
		}
		// 支付状态:支付成功 追号状态:系统撤单 --> 追号撤单
		if (addStatus == AddStatus.SYSTEM_UNDO.getKey()) {
			return OrderEnum.AddOrderUnityStatus.ADDED_REVOKE;
		}
		return null;
	}

	/**
	 * 转换追号计划订单统一状态(订单详情使用)
	 * @param orderListInfoBO
	 * @return
	 */
	private OrderEnum.AddOrderFlowUnityStatus convertAddOrderFlowUnionStatus(OrderBaseInfoBO orderListInfoBO) {
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() != orderListInfoBO.getBuyType().shortValue()) {
			return null;
		}
		Short payStatus = orderListInfoBO.getPayStatus().shortValue();
		Short addStatus = orderListInfoBO.getAddStatus().shortValue();
		// 支付状态:等待支付 追号状态:追号中 --> 待支付
		if (payStatus == PayStatusEnum.WAITTING_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.NO_PAY;
		}
		// 支付状态:支付中 追号状态:追号中 --> 待支付
		if (payStatus == PayStatusEnum.BEING_PAID.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.NO_PAY;
		}
		// 支付状态:未支付过期 追号状态:追号中 --> 未支付过期
		if (payStatus == PayStatusEnum.OVERDUE_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.NO_PAY_OVERDUE;
		}
		// 支付状态:用户取消 追号状态:追号中 --> 用户取消
		if (payStatus == PayStatusEnum.USER_CANCELLED_PAYMENT.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.NO_PAY_USER_CANCEL;
		}
		// 支付状态:支付失败 追号状态:追号中 --> 支付失败
		if (payStatus == PayStatusEnum.PAYMENT_FAILURE.getKey() && addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.PAY_FAIL;
		}
		if (payStatus != PayStatusEnum.PAYMENT_SUCCESS.getKey()) {
			return null;
		}
		// 支付状态:支付成功 追号状态:追号中 --> 追号中
		if (addStatus == AddStatus.CHASING.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.ADDING;
		}
		// 支付状态:支付成功 追号状态:追号结束 --> 追号结束
		if (addStatus == AddStatus.CHASE_FINISH.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.ADDED_STOP;
		}
		// 支付状态:支付成功 追号状态:追号结束 --> 中奖停追
		if (addStatus == AddStatus.CHASE_STOP.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.WINNING_STOP;
		}
		// 支付状态:支付成功 追号状态:用户撤单 --> 追号撤单
		if (addStatus == AddStatus.USER_CANCEL.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.ADDED_REVOKE;
		}
		// 支付状态:支付成功 追号状态:系统撤单 --> 追号撤单
		if (addStatus == AddStatus.SYSTEM_UNDO.getKey()) {
			return OrderEnum.AddOrderFlowUnityStatus.ADDED_REVOKE;
		}
		return null;
	}

	/**
	 * 获取子玩法名称
	 * @param lotChildBOs
	 * @param lotteryChildCode
	 * @return
	 */
	private String getLotteryChildName(List<LotChildBO> lotChildBOs,Integer lotteryChildCode,Integer lotteryCode){
		if(!ObjectUtil.isBlank(lotChildBOs)){
			if(JCZQConstants.ID_JCZQ_B == lotteryCode || JCLQConstants.ID_JCLQ_B == lotteryCode){//足球篮球取出子玩法
				for(LotChildBO lotChildBO : lotChildBOs){
					if(lotChildBO.getLotteryChildCode().equals(lotteryChildCode)){
						return lotChildBO.getChildName();
					}
				}
			}
		}
		return null;
	}


	@Override
	public ResultBO<?> queryOrderDetailInfo(String orderCode, String token,Integer source,Integer userId,Integer orderGroupContentId) throws Exception {
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		OrderFullDetailInfoBO orderFullDetailInfo = new OrderFullDetailInfoBO();
        if(Constants.NUM_2!=source){
			// 1.取用户信息
			if(!ObjectUtil.isBlank(token)){
				ResultBO<?> result = userInfoCacheService.checkNoUseToken(token);
				if(result.isError()) {
					return result;
				}
				UserInfoBO userInfo = (UserInfoBO) result.getData();
				orderFullDetailInfo.setUserId(userInfo.getId());
				orderFullDetailInfo.setAccount(userInfo.getAccount());
				orderFullDetailInfo.setNickName(userInfo.getNickname());
				orderFullDetailInfo.setHeadUrl(userInfo.getHeadUrl());
				userId = userInfo.getId();
			}
		}
		// 2.获取订单基本信息
		OrderBaseInfoBO orderBaseInfoBO = null;
		if(Constants.NUM_2==source){
			orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderCode, userId);
			Map<Integer, LotteryBO> lotteryInfoMap = getLotteryInfo(orderBaseInfoBO.getLotteryCode());
			if (!ObjectUtil.isBlank(lotteryInfoMap)) {
				LotteryBO lotteryBO = lotteryInfoMap.get(orderBaseInfoBO.getLotteryCode());
				orderBaseInfoBO.setLotteryName(lotteryBO.getLotteryName());
			}
		}else{
			ResultBO<?> resultBO = this.queryOrderInfo(orderCode, token);
			if(resultBO.isError()){
				return resultBO;
			}
			orderBaseInfoBO = (OrderBaseInfoBO)resultBO.getData();
		}

		orderBaseInfoBO.setLotteryLogoUrl(uploadURL + orderBaseInfoBO.getLotteryLogoUrl());
		if (ObjectUtil.isBlank(orderBaseInfoBO)) {
			return ResultBO.err(MessageCodeConstants.ORDER_IS_NOT_EXIST);
		}
		orderStatusTransfer(orderBaseInfoBO);
		Integer lotteryCode = orderBaseInfoBO.getLotteryCode();
		//处理之前的异常数据，之前的单lotteryCode存的是子彩种编码，会导致查不到彩种信息
		lotteryCode = Integer.valueOf(
				String.valueOf(lotteryCode).substring(Constants.NUM_0, Constants.NUM_3));
		// 3.订单方案详情
		Integer lotteryType = Constants.getLotteryType(lotteryCode);
		Integer grpbuyStatus = 0;

		if (ObjectUtil.isBlank(orderBaseInfoBO.getBettingUrl())) {
			//设置合买信息
			OrderDetailGroupInfoBO orderDetailGroupInfoBO = null;
			//设置合买订单详情信息
			if(orderBaseInfoBO.getBuyType() == Constants.NUM_3){
				orderDetailGroupInfoBO = orderBaseInfoBO.getOrderDetailGroupInfoBO();
				grpbuyStatus = orderDetailGroupInfoBO.getGrpbuyStatus();
				setOrderGroupOtherInfo(orderDetailGroupInfoBO,userId,orderCode,orderGroupContentId,orderDetailGroupInfoBO.getUserId(),orderBaseInfoBO);
			}
			if (lotteryType==Constants.NUM_2 || lotteryType==Constants.NUM_3 || lotteryType==Constants.NUM_4) {//竞技彩,北单,老足彩,冠亚军
				//add by cheng.chen 不等于奖金优化, 和单场致胜的 竞技彩才需要查询
				boolean flag = true;
				if (Objects.equals(orderBaseInfoBO.getCategoryId(), Category.BONUS.getValue()) ) {
					flag = false;
				}
				if(flag){
					orderDetailVO.setOrderCode(orderCode);
					//orderDetailVO.setUserId(userId);
					List<OrderDetailInfoBO> orderDetailInfoBOs = orderInfoDaoMapper.queryOrderDetailInfo(orderDetailVO);
					orderBaseInfoBO.setContentType(orderDetailInfoBOs.get(0).getContentType());
					List<String> jcPlanContent = new ArrayList<>();
					// 设置串关和注数
					setOrderBetAndBunch(lotteryCode, orderBaseInfoBO, orderDetailInfoBOs);

					// 从缓存取对阵信息，彩果等
					List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<>();
					boolean isCanSee = true;
					Integer orderVisibleType = 0;
					for (OrderDetailInfoBO orderDetailInfoBO : orderDetailInfoBOs) {// 每一个订单详情可能包含多场赛事
						//对于抄单的跟单用户，赛事根据权限显示
						if(orderBaseInfoBO.getOrderType()== Constants.NUM_3){
							OrderFollowedInfoBO orderFollowedInfoBO = orderFollowedInfoDaoMapper.queryFollowedDetail(orderCode);
							orderVisibleType = orderFollowedInfoBO.getOrderVisibleType();
							isCanSee = isCanSeeMatch(orderFollowedInfoBO.getOrderVisibleType(),orderBaseInfoBO.getPayStatus().shortValue(),orderBaseInfoBO.getWinningStatus().shortValue(),userId,orderFollowedInfoBO.getUserIssueId());
							if(!isCanSee){
								break;
							}
						}
						//对于合买跟单用户，赛事根据权限显示
						if(orderBaseInfoBO.getBuyType() == Constants.NUM_3){
							isCanSee = isCanSeeMatchForGroup(orderCode,userId,orderBaseInfoBO.getWinningStatus(),orderDetailGroupInfoBO,orderGroupContentId);
							if(!isCanSee){
								break;
							}
							orderDetailGroupInfoBO.setHavaGrContPermiss(Constants.NUM_1);
						}
						//单场至胜的赛事另外的接口查询
						if(Objects.equals(orderBaseInfoBO.getCategoryId(), Category.DCZS.getValue())){
							isCanSee = false;
							if(!isCanSee){
								break;
							}
						}

						// 设置对阵赛事信息
						setOrderDetailInfo(orderBaseInfoBO.getBuyScreen(),lotteryCode, orderDetailInfoBO.getLotteryIssue(), orderDetailInfoBO.getBetContent(), orderMatchInfoBOs,
								orderDetailInfoBO.getLotteryChildCode(),orderBaseInfoBO.getWinningStatus());
						jcPlanContent.add(orderDetailInfoBO.getBetContent());
					}
					if(!isCanSee){
						orderBaseInfoBO.setOrderVisibleType(orderVisibleType);
						orderBaseInfoBO.setJcPlanContent(null);
						orderFullDetailInfo.setOrderMatchInfoBOs(null);
					}else{
						orderBaseInfoBO.setJcPlanContent(jcPlanContent);
						orderFullDetailInfo.setOrderMatchInfoBOs(orderMatchInfoBOs);
					}
				}
			}else if(lotteryType == Constants.NUM_1) {// 数字彩方案详情。暂时先这样，方案上传和合买的方案详情到时再搞分支
				if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderBaseInfoBO.getBuyType().shortValue()) {// 追号计划方案详情
					UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO();
					queryVO.setOrderAddCode(orderCode);
					queryVO.setToken(token);
					queryVO.setPageIndex(Constants.NUM_0);
					if(Constants.NUM_1 == source){
						queryVO.setPageSize(Constants.NUM_5);//移动端默认五条
					}else{
						queryVO.setPageSize(Constants.NUM_10);// PC追号内容一次查出,即最大1000条
					}
					PagingBO<UserNumOrderDetailBO> pageContentData = (PagingBO<UserNumOrderDetailBO>) this.queryUserChaseContent(queryVO)
							.getData();
					if(Constants.NUM_1 == source){
						queryVO.setPageSize(Constants.NUM_5);//移动端默认五条
					}else{
						queryVO.setPageSize(Constants.NUM_10);
					}
					PagingBO<UserChaseDetailBO> pageData = (PagingBO<UserChaseDetailBO>) this.queryUserChaseDetail(queryVO)
							.getData();
					orderFullDetailInfo.setPageContentData(pageContentData);
					orderFullDetailInfo.setAddDetailBOPagingBO(pageData);
					if(!ObjectUtil.isBlank(pageData.getData())) {
						for(UserChaseDetailBO userChaseDetailBO : pageData.getData()) {
							if(!ObjectUtil.isBlank(userChaseDetailBO.getDrawCode())) {
								userChaseDetailBO.setDrawCodeType(getDrawCodeType(lotteryCode, userChaseDetailBO.getDrawCode()));
							}
						}
					}
					//追号中则显示正在执行的期号
					if(Integer.valueOf(OrderEnum.PayStatus.SUCCESS_PAY.getValue()) == orderBaseInfoBO.getPayStatus() &&
							Integer.valueOf(OrderEnum.AddStatus.CHASING.getKey()) == orderBaseInfoBO.getAddStatus()){
						//设置正在执行的彩期
						orderBaseInfoBO.setCurAddLotteryIssue(orderAddDaoMapper.findCurChasingIssue(queryVO));
					}
					//从哪期停止追号(中奖停追，用户撤单，系统撤单)
					if(Integer.valueOf(OrderEnum.AddStatus.CHASING.getKey()) != orderBaseInfoBO.getAddStatus()
							&& Integer.valueOf(OrderEnum.AddStatus.CHASE_FINISH.getKey()) != orderBaseInfoBO.getAddStatus()){
						orderBaseInfoBO.setStopAddLotteryIssue(orderAddDaoMapper.findStopChasingIssue(queryVO));
					}
					// 最新一期开奖号码
					orderBaseInfoBO.setDrawCode(orderAddDaoMapper.queryAddOrderDrawCode(orderBaseInfoBO.getOrderCode()));
					//设置方案详情顶部的信息
					setOrderDetailTopInfo(orderCode, orderBaseInfoBO);
				} else {// 数字彩方案详
					//对于合买跟单用户，赛事根据权限显示
					if(orderBaseInfoBO.getBuyType() == Constants.NUM_3){
						Boolean isCanSee = isCanSeeMatchForGroup(orderCode,userId,orderBaseInfoBO.getWinningStatus(),orderDetailGroupInfoBO,orderGroupContentId);
						if(isCanSee){
							orderDetailGroupInfoBO.setHavaGrContPermiss(Constants.NUM_1);
							setUserNumPageInfo(orderCode, token, source, userId, orderDetailVO, orderFullDetailInfo, orderBaseInfoBO, lotteryCode);
						}
					}else{
						setUserNumPageInfo(orderCode, token, source, userId, orderDetailVO, orderFullDetailInfo, orderBaseInfoBO, lotteryCode);
					}
				}
			}
		} else {
			setSingleUploadList(orderBaseInfoBO, orderCode, orderDetailVO, orderFullDetailInfo);
		}
		//处理订单相关状态显示
		buildAllOrderInfo(orderBaseInfoBO);
		//订单流程状态
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderBaseInfoBO.getBuyType().shortValue()) {// 追号计划
			OrderEnum.AddOrderFlowUnityStatus addOrderFlowUnionStatus = convertAddOrderFlowUnionStatus(orderBaseInfoBO);
			if (addOrderFlowUnionStatus != null) {
				orderBaseInfoBO.setAddOrderFlowUnionStatus(addOrderFlowUnionStatus.getValue());
				orderBaseInfoBO.setAddOrderFlowUnionStatusText(addOrderFlowUnionStatus.getDesc());
			}
		} else {
			OrderEnum.OrderFlowUnityStatus OrderFlowUnionStatus = convertOrderFlowUnionStatus(orderBaseInfoBO,grpbuyStatus);
			if (OrderFlowUnionStatus != null) {
				orderBaseInfoBO.setOrderFlowUnionStatus(OrderFlowUnionStatus.getValue());
				orderBaseInfoBO.setOrderFlowUnionStatusText(OrderFlowUnionStatus.getDesc());
			}
		}
		//最新流程信息
		List<OrderFlowInfoBO> orderFlowInfoBOs = orderFlowInfoMapper.queryOrderFlowInfoList(orderCode, null);
		if(!ObjectUtil.isBlank(orderFlowInfoBOs)){
			//合买招募中的订单可能已经出票了，还是显示招募中，也就是流程的第一条
			if(orderBaseInfoBO.getBuyType().shortValue() == OrderEnum.BuyType.BUY_TOGETHER.getValue() && grpbuyStatus.shortValue() == OrderEnum.GRPBuyStatus.RECRUIT.getValue()){
				orderBaseInfoBO.setOrderFlowInfoBO(orderFlowInfoBOs.get(0));
			}else{
				orderBaseInfoBO.setOrderFlowInfoBO(orderFlowInfoBOs.get(orderFlowInfoBOs.size()-1));
			}
		}
		orderFullDetailInfo.setOrderBaseInfoBO(orderBaseInfoBO);
		return ResultBO.ok(orderFullDetailInfo);
	}

	private void setUserNumPageInfo(String orderCode, String token, Integer source, Integer userId, OrderDetailVO orderDetailVO, OrderFullDetailInfoBO orderFullDetailInfo, OrderBaseInfoBO orderBaseInfoBO, Integer lotteryCode) {
		orderDetailVO.setOrderCode(orderCode);
		//orderDetailVO.setUserId(userId);
		List<OrderDetailInfoBO> orderDetailInfoBOs = orderInfoDaoMapper.queryOrderDetailInfo(orderDetailVO);
		orderBaseInfoBO.setContentType(orderDetailInfoBOs.get(0).getContentType());
		// 设置串关和注数
		setOrderBetAndBunch(lotteryCode, orderBaseInfoBO, orderDetailInfoBOs);
		UserNumOrderDetailQueryVO queryVO = new UserChaseDetailQueryVO();
		queryVO.setOrderCode(orderCode);
		queryVO.setToken(token);
		queryVO.setPageIndex(Constants.NUM_0);
		queryVO.setSource(source);
		queryVO.setUserId(userId);
		if(Constants.NUM_1 == source){
            queryVO.setPageSize(Constants.NUM_5);//移动端默认五条
        }else if(Constants.NUM_0 == source){
            queryVO.setPageSize(Constants.NUM_10);
        }else{
            queryVO.setPageSize(Constants.NUM_1000);
        }
		PagingBO<UserNumOrderDetailBO> pageData = (PagingBO<UserNumOrderDetailBO>) this
                .queryUserNumOrderDetail(queryVO).getData();
		orderFullDetailInfo.setUserNumPage(pageData);
	}

	/**
	 * 设置方案是否可见
	 * @param orderVisibleType
	 * @param payStatus
	 * @param userId
	 * @param userIssueId
	 * @return
	 */
	private boolean isCanSeeMatch(Integer orderVisibleType,short payStatus,short winningStatus,Integer userId,Integer userIssueId){
		boolean canSee = true;
		if (payStatus ==OrderEnum.PayStatus.SUCCESS_PAY.getValue() ) {//支付成功
			if(winningStatus == OrderEnum.OrderWinningStatus.NOT_DRAW_WINNING.getValue()){
				if (orderVisibleType == OrderCopyEnum.OrderVisibleTypeEnum.DRAW_SHOW.getValue()) {//开奖后可见
					canSee = false;
				}
				if (orderVisibleType == OrderCopyEnum.OrderVisibleTypeEnum.FOLLOW_SHOW.getValue()) { //关注后可见，开奖后无论是否关注都可见，
					int total = mUserIssueLinkDaoMapper.selectCountByUserId(userId, userIssueId.longValue());
					if (total < 1) {//没有关注，且没有开奖，不能看
						canSee = false;
					}
				}
			}
		}else{//非支付成功
			if(orderVisibleType == OrderCopyEnum.OrderVisibleTypeEnum.FOLLOW_SHOW.getValue()){//关注的未支付，可见
				int total = mUserIssueLinkDaoMapper.selectCountByUserId(userId, userIssueId.longValue());
				if (total < 1) {//没有关注，且没有开奖，不能看
					canSee = false;
				}
			}
			if(orderVisibleType == OrderCopyEnum.OrderVisibleTypeEnum.DRAW_SHOW.getValue()){//开奖后可见
				canSee = false;
			}
			if(orderVisibleType == OrderCopyEnum.OrderVisibleTypeEnum.SPECIFY_SHOW.getValue()){//抄单后可见
				canSee = false;
			}
		}
		//仅对抄单人公开，抄单人一直看见,公开，全部可见
		return canSee;
	}

	/**
	 * 开奖号码类型
	 * @param lotteryCode
	 * @param drawCode
	 * @return
	 */
	private Integer getDrawCodeType(Integer lotteryCode, String drawCode) {
		String[] drawCodes = null;
		LotteryEnum.Lottery lottery = LotteryEnum.Lottery.getLottery(lotteryCode);
		switch (lottery) {
		case F3D:// 福彩3D
		case PL3:// 排列3
			drawCodes = drawCode.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
		case CQSSC:// 重庆时时彩
			if(drawCodes == null) {
				drawCodes = drawCode.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
				if (drawCodes != null && drawCodes.length == 5) {
					drawCodes = new String[] { drawCodes[2], drawCodes[3], drawCodes[4] };
				}
			}
			if (drawCodes != null && drawCodes.length == Constants.NUM_3) {
				int num = new HashSet<String>(Arrays.asList(drawCodes)).size();
				switch (num) {
				case Constants.NUM_3:
					return LotteryEnum.DrawCodeType.Z6.getValue();
				case Constants.NUM_2:
					return LotteryEnum.DrawCodeType.Z3.getValue();
				case Constants.NUM_1:
					return LotteryEnum.DrawCodeType.BZ.getValue();
				}
			}
			break;
		case SDPOKER:
			List<String> pokerColorList = new ArrayList<String>();
			List<String> pokerNameList = new ArrayList<String>();
			// 花色_牌
			Pattern p = Pattern.compile("(\\d)_([0-9A-Z]+)");
			Matcher m = p.matcher(drawCode);
			while (m.find()) {
				pokerColorList.add(m.group(1));
				pokerNameList.add(m.group(2));
			}
			if (pokerColorList.size() == Constants.NUM_3 && pokerNameList.size() == Constants.NUM_3) {
				int pokerNameNum = new HashSet<String>(pokerNameList).size();
				if (pokerNameNum == Constants.NUM_1) {// 豹子
					return LotteryEnum.DrawCodeType.BZ.getValue();
				}
				if (pokerNameNum == Constants.NUM_2) {// 对子
					return LotteryEnum.DrawCodeType.DZ.getValue();
				}
				boolean isSZ = PokerEnum.isSz(pokerNameList.get(0), pokerNameList.get(1), pokerNameList.get(2));
				boolean isTH = Constants.NUM_1 == new HashSet<String>(pokerColorList).size();
				if (isSZ) {// 顺子，同花顺
					return isTH ? LotteryEnum.DrawCodeType.THS.getValue() : LotteryEnum.DrawCodeType.SZ.getValue();
				}
				if (isTH) {// 同花
					return LotteryEnum.DrawCodeType.TH.getValue();
				}
			}
			break;
		}
		return 0;
	}

	/**
	 * 设置订单详情顶部信息
	 * @param orderCode
	 * @param orderBaseInfoBO
	 */
	private void setOrderDetailTopInfo(String orderCode, OrderBaseInfoBO orderBaseInfoBO) {
		//彩期倒序查询来，遇到追号成功，或失败。这就是最新的追号中的彩期
		List<OrderAddedIssueBO> orderAddedIssueBOs = orderAddDaoMapper.queryOrderAddedIssueList(orderCode);
		if(!ObjectUtil.isBlank(orderAddedIssueBOs)){
            //1.追号中
            if(OrderEnum.AddStatus.CHASING.getKey() == orderBaseInfoBO.getAddStatus().shortValue() &&
                    OrderEnum.PayStatus.SUCCESS_PAY.getValue() == orderBaseInfoBO.getPayStatus().shortValue()){
               //取最近的追号期号状态，期号，时间
                boolean isFlag = false;//是否是追号成功或失败
                for(OrderAddedIssueBO orderAddedIssueBO : orderAddedIssueBOs){
                    if(ChaseEnum.ChaseIssueStatus.CHASE_SUCCESS.getValue() == orderAddedIssueBO.getAddStatus().shortValue()){//追号成功
                        orderBaseInfoBO.setAddTime(orderAddedIssueBO.getAddTime());
                        orderBaseInfoBO.setLastAddIssueStatus(orderAddedIssueBO.getAddStatus());
                        orderBaseInfoBO.setAddIssueCode(orderAddedIssueBO.getIssueCode());
                        isFlag = true;
                        break;
                    }
                    if(ChaseEnum.ChaseIssueStatus.CHASE_FAILED.getValue() == orderAddedIssueBO.getAddStatus().shortValue() ||
                            ChaseEnum.ChaseIssueStatus.SYSTEM_CANCEL.getValue() == orderAddedIssueBO.getAddStatus().shortValue()){//追号失败,系统撤单
                        orderBaseInfoBO.setAddTime(orderAddedIssueBO.getUpdateTime());
                        orderBaseInfoBO.setLastAddIssueStatus(orderAddedIssueBO.getAddStatus());
                        orderBaseInfoBO.setAddIssueCode(orderAddedIssueBO.getIssueCode());
                        isFlag = true;
                        break;
                    }
                }
                //假如没有成功或者失败。那直接去取第一期
                if(!isFlag){
                    orderBaseInfoBO.setAddTime(orderAddedIssueBOs.get(orderAddedIssueBOs.size()-1).getCreateTime());
                    orderBaseInfoBO.setLastAddIssueStatus(orderAddedIssueBOs.get(orderAddedIssueBOs.size()-1).getAddStatus());
                    orderBaseInfoBO.setAddIssueCode(orderAddedIssueBOs.get(orderAddedIssueBOs.size()-1).getIssueCode());
                }
            }
         }
	}

	/**
	 * 设置串关和在注数
	 * 
	 * @param orderDetailInfoBOs
	 * @param orderListInfoBO
	 */
	private void setOrderBetAndBunch(Integer lotteryCode, OrderBaseInfoBO orderListInfoBO,
			List<OrderDetailInfoBO> orderDetailInfoBOs) {
		Integer betNum = 0;
		//StringBuffer stringBuffer = new StringBuffer();
		// 3.订单方案详情
		//Integer lotteryType = Constants.getLotteryType(lotteryCode);
		if (!ObjectUtil.isBlank(orderDetailInfoBOs)) {
			for (OrderDetailInfoBO orderDetailInfoBO : orderDetailInfoBOs) {
				betNum = betNum + orderDetailInfoBO.getBetNum();
				/*if (lotteryType==Constants.NUM_2) {// 竞彩篮球，竞彩足球,北京单场，胜负过关才有串关
					String[] betContent = FormatConversionJCUtil
							.singleBetContentAnalysis(orderDetailInfoBO.getBetContent());
					// 设置串关
					//stringBuffer.append(betContent[1] + SymbolConstants.COMMA);
				}*/

			}
		}
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() != orderListInfoBO.getBuyType().shortValue()) {// 追号计划里面有总注数了，竞技彩的需要统计重新设置
			if(ObjectUtil.isBlank(orderListInfoBO.getBetNum())){
				orderListInfoBO.setBetNum(betNum);
			}
		}
		/*if (stringBuffer.length() > 0) {
			orderListInfoBO.setBunchStr(stringBuffer.substring(0, stringBuffer.length() - 1));
		}*/
        //设置过关方式
		setOrderBunch(lotteryCode,orderListInfoBO,orderDetailInfoBOs);

	}


	/**
	 * 设置串关
	 *
	 * @param orderDetailInfoBOs
	 * @param orderListInfoBO
	 */
	private void setOrderBunch(Integer lotteryCode, OrderBaseInfoBO orderListInfoBO,
							   List<OrderDetailInfoBO> orderDetailInfoBOs) {
		Integer lotteryType = Constants.getLotteryType(lotteryCode);
		if(lotteryType == Constants.NUM_4){//冠亚军游戏
			orderListInfoBO.setBunchStr("1_1");
			return;
		}
		StringBuffer stringBuffer = new StringBuffer();
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
	 *
	 * 设置赛事对阵信息
	 * @param buyScreen
	 * @param lotteryCode
	 * @param lotteryIssue
	 * @param betContent
	 * @param orderMatchInfoBOs
	 * @param lotteryChildCode
	 */
	private void setOrderDetailInfo(String buyScreen,Integer lotteryCode, String lotteryIssue, String betContent,
			List<OrderMatchInfoBO> orderMatchInfoBOs, Integer lotteryChildCode,Integer winningStatus) {
		Integer lotteryType = Constants.getLotteryType(lotteryCode);
		if (lotteryType==Constants.NUM_2) {// 竞彩篮球和足球和北京单场和胜负过关
			// 设置对阵信息
			String[] betContentStr = FormatConversionJCUtil.singleBetContentAnalysis(betContent);
			String gameContent = betContentStr[0];
			// 获取赛事对阵集合
			List<OrderMatchInfoBO> orderMatchInfoChildBOs = getJCOrderMatchInfoBos(lotteryCode,
					gameContent, lotteryChildCode);
			orderMatchInfoBOs.addAll(orderMatchInfoChildBOs);
		} else if (lotteryType==Constants.NUM_3) {// 老足彩
			ResultBO<List<JcOldDataBO>> jcOldDataResult = jcDataService
					.findJcOldData(lotteryIssue, String.valueOf(lotteryCode));
			List<JcOldDataBO> jcOldDataBOs = jcOldDataResult.getData();
			if (!ObjectUtil.isBlank(jcOldDataBOs)) {
				List<Integer> unUseList = getUnUseGameList(betContent);
				for (int i = 0; i < jcOldDataBOs.size(); i++) {
					// 获取老足彩赛事对阵信息
					OrderMatchInfoBO orderMatchInfoBO = getLZCOrderMatchInfoBO(betContent, jcOldDataBOs.get(i), i);
					if (!ObjectUtil.isBlank(unUseList)) {
						for(int j=0;j<unUseList.size();j++){
							if (unUseList.get(j) == i) {//本场赛事没有投注
								orderMatchInfoBO.setIsChooseMatchInfo(Constants.NUM_2);
							}
						}
					}
					orderMatchInfoBOs.add(orderMatchInfoBO);
				}
			}

		} else if (lotteryType == Constants.NUM_4){//冠亚军游戏
			orderMatchInfoBOs.addAll(getGYJOrderMatchInfoBO(buyScreen, lotteryCode, betContent));
		}
	}

	private List<OrderMatchInfoBO> getGYJOrderMatchInfoBO(String buyScreen, Integer lotteryCode, String betContent) {
		List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<OrderMatchInfoBO>();
		if(!ObjectUtil.isBlank(buyScreen)){
            List<String> systemCodes = Arrays.asList(buyScreen.split(SymbolConstants.COMMA));
            List<SportAgainstInfoBO> sportAgainstInfoBOs = sportAgainstInfoDaoMapper.findSportAgainstInfoBySystemCodes(systemCodes,lotteryCode);
if(!ObjectUtil.isBlank(sportAgainstInfoBOs)){
                for(SportAgainstInfoBO sportAgainstInfoBO : sportAgainstInfoBOs){
                    OrderMatchInfoBO orderMatchInfoBO = new OrderMatchInfoBO();
                    //orderMatchInfoBO.setId(jczqOrderBO.getId());
                    //orderMatchInfoBO.setOfficalMatchCode(sportAgainstInfoBO.getOfficialMatchCode());
                    orderMatchInfoBO.setSystemCode(sportAgainstInfoBO.getSystemCode());
                    orderMatchInfoBO.setHomeName(sportAgainstInfoBO.getHomeName());
                    orderMatchInfoBO.setVisitiName(sportAgainstInfoBO.getVisitiName());
                    orderMatchInfoBO.setMatchShortName(sportAgainstInfoBO.getMatchName());
                    /*orderMatchInfoBO.setDate(jczqOrderBO.getDate());
                    orderMatchInfoBO.setTime(jczqOrderBO.getTime());*/
                    /*OrderMatchZQBO orderMatchZQBO = new OrderMatchZQBO();
                    orderMatchZQBO.setFullScore(jczqOrderBO.getFullScore());
                    orderMatchZQBO.setHalfScore(jczqOrderBO.getHalfScore());
                    orderMatchZQBO.setLetNum(jczqOrderBO.getLetNum());
                    orderMatchZQBO.setFullSpf(jczqOrderBO.getFullSpf());
                    orderMatchZQBO.setLetSpf(jczqOrderBO.getLetSpf());
                    orderMatchZQBO.setScore(jczqOrderBO.getScore());
                    orderMatchZQBO.setGoalNum(jczqOrderBO.getGoalNum());
                    orderMatchZQBO.setHfWdf(jczqOrderBO.getHfWdf());*/
                    //orderMatchInfoBO.setOrderMatchZQBO(orderMatchZQBO);
                    orderMatchInfoBO.setMatchStatus(Integer.valueOf(sportAgainstInfoBO.getMatchStatus()));
                    orderMatchInfoBO.setBetGameContent(getBetGameContent(betContent,sportAgainstInfoBO.getSystemCode()));
                    //设置彩果
                    if(sportAgainstInfoBO.getMatchStatus().intValue()==Constants.NUM_18){//状态为已淘汰时显示已淘汰
                        orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_1);
                    }else if(sportAgainstInfoBO.getMatchStatus().intValue()==Constants.NUM_15 || sportAgainstInfoBO.getMatchStatus().intValue()==Constants.NUM_16 || sportAgainstInfoBO.getMatchStatus().intValue()==Constants.NUM_17){//已审核，已开奖，已派奖，直接显示对应的队伍
						orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_2);
                    }else {
						orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_3);
					}
					orderMatchInfoBOs.add(orderMatchInfoBO);
                }
            }
        }
        return orderMatchInfoBOs;
	}

	/**
	 * 获取每场对阵的显示内容
	 * @param betContent
	 * @return
	 */
	private String getBetGameContent(String betContent,String matchSystemCode){
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(betContent);//解析投注详情
		//投注内容的场次编号
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
		for(String content : betContentArr){
			String systemCode = content.split(SymbolConstants.AT)[0];
			if(systemCode.equals(matchSystemCode)){
				return SymbolConstants.AT+content.split(SymbolConstants.AT)[1];
			}
		}
		return "";
	}

	/**
	 * 老足彩：过滤没有选择的赛事
	 * 
	 * @param betContent
	 * @return
	 */
	private List<Integer> getUnUseGameList(String betContent) {
		List<Integer> unUseList = new ArrayList<Integer>();
		String content1 = betContent.replaceAll(SymbolConstants.COMMA, SymbolConstants.ENPTY_STRING);
		// 获取所有的老足彩赛事
		List<String> list = getLZCGameContent(content1);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(SymbolConstants.UNDERLINE)) {// 剔除没有选择的赛事
				unUseList.add(i);
			}
		}
		return unUseList;
	}

	/**
	 * 获取每场老足彩赛事对阵信息
	 * 
	 * @param betContent
	 * @param jcOldDataBO
	 * @param i
	 * @return
	 */
	private OrderMatchInfoBO getLZCOrderMatchInfoBO(String betContent, JcOldDataBO jcOldDataBO, int i) {
		OrderMatchInfoBO orderMatchInfoBO = new OrderMatchInfoBO();
		// 获取胆的下标，老足彩任九存在多个胆，#前面的一场赛事就是胆。其他老足彩不存在胆的概率
		List<Integer> danListIndex = getDanList(betContent);
		if (!ObjectUtil.isBlank(danListIndex)) {
			for (int index : danListIndex) {
				if (i == (index - 1)) {// 这场赛事是胆
					orderMatchInfoBO.setIsDan(com.hhly.skeleton.base.constants.Constants.NUM_1);
				}
			}
		}
		// 设置每场赛事的投注内容
		List<String> lzcGameContent = getLZCGameContent(betContent);
		if (!ObjectUtil.isBlank(lzcGameContent)) {
			for (int j = 0; j < lzcGameContent.size(); j++) {
				if (i == j) {
					orderMatchInfoBO.setBetGameContent(lzcGameContent.get(j));
				}
			}
		}
		orderMatchInfoBO.setOfficalMatchCode(jcOldDataBO.getOfficialMatchCode());
		orderMatchInfoBO.setHomeName(ObjectUtil.isBlank(jcOldDataBO.getHomeShortName())?jcOldDataBO.getHomeFullName():jcOldDataBO.getHomeShortName());
		orderMatchInfoBO.setVisitiName(ObjectUtil.isBlank(jcOldDataBO.getGuestShortName())?jcOldDataBO.getGuestFullName():jcOldDataBO.getGuestShortName());
		orderMatchInfoBO.setDate(jcOldDataBO.getDate());
		orderMatchInfoBO.setTime(jcOldDataBO.getTime());
		orderMatchInfoBO.setMatchShortName(ObjectUtil.isBlank(jcOldDataBO.getMatchShortName())?jcOldDataBO.getMatchFullName():jcOldDataBO.getMatchShortName());
		OrderMatchLZCBO orderMatchLZCBO = new OrderMatchLZCBO();
		orderMatchLZCBO.setFourGoal(jcOldDataBO.getFourGoal());
		orderMatchLZCBO.setFourteenWdf(jcOldDataBO.getFourteenWdf());
		orderMatchLZCBO.setFullScore(jcOldDataBO.getFullScore());
		orderMatchLZCBO.setHalfScore(jcOldDataBO.getHalfScore());
		orderMatchLZCBO.setSixHfWdf(jcOldDataBO.getSixHfWdf());
		orderMatchInfoBO.setOrderMatchLZCBO(orderMatchLZCBO);
		orderMatchInfoBO.setMatchStatus(Integer.valueOf(jcOldDataBO.getMatchStatus()));
		return orderMatchInfoBO;
	}

	/**
	 * 获取竞彩每场赛事的对阵信息
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
        if(gameDetails.length>1){//有胆
			//胆的赛事
			String danContent = gameDetails[0];
			String danDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(danContent);
			for(String gameCon : danDetails){
				String systemCode = getSystemCodeAndContent(gameCon,lotteryChildCode)[0];
				// 足球,篮球
				OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
						systemCode);
				orderMatchInfoBO.setIsDan(com.hhly.skeleton.base.constants.Constants.NUM_1);
				orderMatchInfoBOs.add(orderMatchInfoBO);
			}
            //非胆的赛事
			String nodanContent = gameDetails[1];
			String nodanDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(nodanContent);
			for(String gameCon : nodanDetails){
				String systemCode = getSystemCodeAndContent(gameCon,lotteryChildCode)[0];
				// 足球,篮球
				OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
						systemCode);
				orderMatchInfoBOs.add(orderMatchInfoBO);
			}
		}else{//无胆
			String details[] = FormatConversionJCUtil.betContentDetailsAnalysis(gameContent);
			for(String gameCon : details){
				String systemCode = getSystemCodeAndContent(gameCon,lotteryChildCode)[0];
				// 足球,篮球
				OrderMatchInfoBO orderMatchInfoBO = getOrderMatchInfo(lotteryCode,getSystemCodeAndContent(gameCon, lotteryChildCode)[1],
						systemCode);
				orderMatchInfoBOs.add(orderMatchInfoBO);
			}
		}
		return orderMatchInfoBOs;
	}

	/**
	 * 获取老足彩每场赛事内容
	 * 
	 * @param betContent
	 * @return
	 */
	private List<String> getLZCGameContent(String betContent) {
		String conttent = betContent.replaceAll(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
		String newContent[] = conttent.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		return Arrays.asList(newContent);
	}

	/**
	 * 老足彩获取胆的下标
	 * 任九存在多个胆，#前面的一场赛事就是胆。其他老足彩不存在胆的概率
	 * @param betContent
	 * @return
	 */
	private List<Integer> getDanList(String betContent) {
		if (!betContent.contains(SymbolConstants.NUMBER_SIGN)) {
			return null;
		}
		List<Integer> list = new ArrayList<>();
		String s = betContent.replaceAll(SymbolConstants.COMMA + String.valueOf(Constants.NUM_3),
				SymbolConstants.ENPTY_STRING);
		String s1 = s.replaceAll(SymbolConstants.COMMA + String.valueOf(Constants.NUM_1), SymbolConstants.ENPTY_STRING);
		String s2 = s1.replaceAll(SymbolConstants.COMMA + String.valueOf(Constants.NUM_0),
				SymbolConstants.ENPTY_STRING);
		String str[] = s2.split(SymbolConstants.NUMBER_SIGN);
		int count = 0;
		for (int i = 0; i < str.length; i++) {
			String s3[] = str[i].split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
			if (s3.length > 1) {
				count = count + s3.length;
				list.add(count);
			} else {
				count = count + 1;
				list.add(count);
			}
		}
		// 最后一场赛事不是胆则剔除
		if (!betContent.substring(betContent.length() - 1, betContent.length()).equals(SymbolConstants.NUMBER_SIGN)) {
			list.remove(list.size() - 1);
		}
		return list;
	}

	/**
	 * 设置竞彩内容
	 * 
	 * @param betGameContent
	 * @param systemCode
	 * @return
	 */
	private OrderMatchInfoBO getOrderMatchInfo(Integer lotteryCode, String betGameContent, String systemCode) {
		OrderMatchInfoBO orderMatchInfoBO = new OrderMatchInfoBO();
		if (JCZQConstants.ID_JCZQ_B == lotteryCode) {
			JczqOrderBO jczqOrderBO = jcDataService.findJczqOrderBOBySystemCode(systemCode);
			if (!ObjectUtil.isBlank(jczqOrderBO)) {
				orderMatchInfoBO.setId(jczqOrderBO.getId());
				orderMatchInfoBO.setOfficalMatchCode(jczqOrderBO.getOfficialMatchCode());
				orderMatchInfoBO.setSystemCode(systemCode);
				orderMatchInfoBO.setHomeName(JCConstants.getName(jczqOrderBO.getHomeShortName(),jczqOrderBO.getHomeFullName()));
				orderMatchInfoBO.setVisitiName(JCConstants.getName(jczqOrderBO.getGuestShortName(),jczqOrderBO.getGuestFullName()));
				orderMatchInfoBO.setMatchShortName(JCConstants.getName(jczqOrderBO.getMatchShortName(),jczqOrderBO.getMatchFullName()));
				orderMatchInfoBO.setDate(jczqOrderBO.getDate());
				orderMatchInfoBO.setTime(jczqOrderBO.getTime());
				OrderMatchZQBO orderMatchZQBO = new OrderMatchZQBO();
				orderMatchZQBO.setFullScore(jczqOrderBO.getFullScore());
				orderMatchZQBO.setHalfScore(jczqOrderBO.getHalfScore());
				orderMatchZQBO.setLetNum(jczqOrderBO.getLetNum());
				orderMatchZQBO.setFullSpf(jczqOrderBO.getFullSpf());
				orderMatchZQBO.setLetSpf(jczqOrderBO.getLetSpf());
				orderMatchZQBO.setScore(jczqOrderBO.getScore());
				orderMatchZQBO.setGoalNum(jczqOrderBO.getGoalNum());
				orderMatchZQBO.setHfWdf(jczqOrderBO.getHfWdf());
				orderMatchInfoBO.setOrderMatchZQBO(orderMatchZQBO);
				orderMatchInfoBO.setMatchStatus(Integer.valueOf(jczqOrderBO.getMatchStatus()));
			}
		} else if (JCLQConstants.ID_JCLQ_B == lotteryCode) {
			JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
			if (!ObjectUtil.isBlank(jclqOrderBO)) {
				orderMatchInfoBO.setId(jclqOrderBO.getId());
				orderMatchInfoBO.setOfficalMatchCode(jclqOrderBO.getOfficialMatchCode());
				orderMatchInfoBO.setSystemCode(systemCode);
				orderMatchInfoBO.setHomeName(JCConstants.getName(jclqOrderBO.getHomeShortName(),jclqOrderBO.getHomeFullName()));
				orderMatchInfoBO.setVisitiName(JCConstants.getName(jclqOrderBO.getGuestShortName(),jclqOrderBO.getGuestFullName()));
				orderMatchInfoBO.setMatchShortName(JCConstants.getName(jclqOrderBO.getMatchShortName(),jclqOrderBO.getMatchFullName()));
				orderMatchInfoBO.setDate(jclqOrderBO.getDate());
				orderMatchInfoBO.setTime(jclqOrderBO.getTime());
				OrderMatchLQBO orderMatchLQBO = new OrderMatchLQBO();
				orderMatchLQBO.setFullScore(jclqOrderBO.getFullScore());
				orderMatchLQBO.setFullWf(jclqOrderBO.getFullWf());
				// orderMatchLQBO.setLetScore(jclqOrderBO.getLetScore());
				orderMatchLQBO.setLetWf(jclqOrderBO.getLetWf());
				orderMatchLQBO.setDxfWF(jclqOrderBO.getSizeScore());
				orderMatchLQBO.setSfcWF(jclqOrderBO.getWinScore());
				orderMatchInfoBO.setOrderMatchLQBO(orderMatchLQBO);
				orderMatchInfoBO.setMatchStatus(Integer.valueOf(jclqOrderBO.getMatchStatus()));
			}
		}else if(BJDCConstants.ID_BJDC_B == lotteryCode || BJDCConstants.ID_SFGG_B == lotteryCode){
			BjDaoBO bo = jcDataService.findBjDataBOBySystemCode(systemCode, lotteryCode.toString());
			if (!ObjectUtil.isBlank(bo)) {
				orderMatchInfoBO.setId(bo.getId());
				orderMatchInfoBO.setOfficalMatchCode(bo.getOfficialMatchCode());
				orderMatchInfoBO.setBjNum(bo.getBjNum());
				orderMatchInfoBO.setSystemCode(systemCode);
				orderMatchInfoBO.setHomeName(JCConstants.getName(bo.getHomeShortName(),bo.getHomeFullName()));
				orderMatchInfoBO.setVisitiName(JCConstants.getName(bo.getGuestShortName(),bo.getGuestFullName()));
				orderMatchInfoBO.setMatchShortName(JCConstants.getName(bo.getMatchShortName(),bo.getMatchFullName()));
				orderMatchInfoBO.setDate(bo.getDate());
				orderMatchInfoBO.setTime(bo.getTime());
				OrderMatchBJBO orderMatchBJBO = new OrderMatchBJBO();
				if(BJDCConstants.ID_BJDC_B == lotteryCode) {
					orderMatchBJBO.setFullScore(bo.getFullScore());
					orderMatchBJBO.setHalfScore(bo.getHalfScore());
					orderMatchBJBO.setLetNum(bo.getLetNum());
					orderMatchBJBO.setLetWdf(bo.getLetWdf());
					orderMatchBJBO.setScore(bo.getScore());
					orderMatchBJBO.setGoalNum(bo.getGoalNum());
					orderMatchBJBO.setHfWdf(bo.getHfWdf());
					orderMatchBJBO.setUdSd(bo.getUdSd());
					orderMatchBJBO.setSpLetWdf(bo.getSpLetWdf());
					orderMatchBJBO.setSpGoalNum(bo.getSpGoalNum());
					orderMatchBJBO.setSpHfWdf(bo.getSpHfWdf());
					orderMatchBJBO.setSpUdSd(bo.getSpUdSd());
					orderMatchBJBO.setSpScore(bo.getSpScore());
				}else{
					orderMatchBJBO.setFullScore(bo.getFullScore());
					orderMatchBJBO.setLetNum(bo.getLetNum());
					orderMatchBJBO.setLetWdf(bo.getLetSf());
					orderMatchBJBO.setSpLetSf(bo.getSpLetSf());
				}
				orderMatchInfoBO.setOrderMatchBJBO(orderMatchBJBO);
				orderMatchInfoBO.setMatchStatus(Integer.valueOf(bo.getMatchStatus()));
			}
		}
		orderMatchInfoBO.setBetGameContent(betGameContent);
		return orderMatchInfoBO;
	}

	/**
	 * 取出系统编号，和系统编号后面的投注内容
	 * 
	 * @param gameDetail
	 * @return
	 */
	private String[] getSystemCodeAndContent(String gameDetail,Integer lotteryChildCode) {
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
		String contents[] = { systemCode, content };
		return contents;
	}

	/**
	 * 开奖时间、派奖时间
	 * @param orderBaseInfoBO
	 */
	private void setLotteryTime(OrderBaseInfoBO orderBaseInfoBO) {
		Integer lotteryCode = orderBaseInfoBO.getLotteryCode();
		lotteryCode = Integer.valueOf(String.valueOf(lotteryCode).substring(Constants.NUM_0, Constants.NUM_3));
		LotteryEnum.LotteryPr lott = LotteryEnum.getLottery(lotteryCode);
		NewIssueBO newIssueBO = null;
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderBaseInfoBO.getBuyType().shortValue()) {// 追号计划没有开奖时间
			return;
		}
		// 已开奖
		if (OrderWinningStatus.NOT_DRAW_WINNING.getValue() != orderBaseInfoBO.getWinningStatus()
				&& orderBaseInfoBO.getLotteryTime() != null) {
			orderBaseInfoBO.setThrowTime(DateUtil.addMinute(orderBaseInfoBO.getLotteryTime(), Constants.NUM_10));// 派奖时间
			return;
		}
		// 未开奖
		Date lotteryTime = null;
		switch (lott) {
		case BJDC:// 北单
		case JJC:// 竞技彩
			if (!ObjectUtil.isBlank(orderBaseInfoBO.getMaxBuyScreen())) {// 根据最晚比赛时间取获取开奖时间
				SportAgainstInfoBO sportAgainstInfoBO = sportAgainstInfoDaoMapper.querySportMatchInfo(lotteryCode, null,
						orderBaseInfoBO.getMaxBuyScreen());
				if (!ObjectUtil.isBlank(sportAgainstInfoBO)) {
					lotteryTime = DateUtil.addMinute(sportAgainstInfoBO.getStartTime(), Constants.NUM_120);
				}
			} else if (!ObjectUtil.isBlank(orderBaseInfoBO.getBuyScreen())) {
				List<String> matchs = Arrays.asList(orderBaseInfoBO.getBuyScreen().split(SymbolConstants.COMMA));
				List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstEndMatch(lotteryCode, matchs);
				lotteryTime = DateUtil.addMinute(listAgainstInfoBO.get(listAgainstInfoBO.size() - 1).getStartTime(), Constants.NUM_120);
			}
			break;
		case GPC:// 高频彩
			newIssueBO = lotteryIssueDaoMapper.findLotteryIssue(lotteryCode, orderBaseInfoBO.getLotteryIssue());
			if (!ObjectUtil.isBlank(newIssueBO)) {
				lotteryTime = DateUtil.addSecond(newIssueBO.getLotteryTime(), Constants.NUM_30);
			}
			break;
		default:// 数字彩 足彩
			newIssueBO = lotteryIssueDaoMapper.findLotteryIssue(lotteryCode, orderBaseInfoBO.getLotteryIssue());
			if (!ObjectUtil.isBlank(newIssueBO)) {
				lotteryTime = newIssueBO.getLotteryTime();
			}
			break;
		}
		orderBaseInfoBO.setLotteryTime(lotteryTime);
		if (lotteryTime != null) {
			orderBaseInfoBO.setThrowTime(DateUtil.addMinute(orderBaseInfoBO.getLotteryTime(), Constants.NUM_10));// 派奖时间
		}
	}
	
	/**
	 * 处理订单数据
	 * @param orderBaseInfoBO
	 */
	public void buildAllOrderInfo(OrderBaseInfoBO orderBaseInfoBO) {
		buildBaseOrderInfo(orderBaseInfoBO);
		if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderBaseInfoBO.getBuyType().shortValue()) {// 追号计划
			buildAddOrderInfo(orderBaseInfoBO);
		} else {
			buildOrderInfo(orderBaseInfoBO);
		}
	}
	
	
	/**
	 * 追号订单
	 * @param orderBaseInfoBO
	 */
	private void buildAddOrderInfo(OrderBaseInfoBO orderBaseInfoBO) {
		// 追号订单统一状态
		OrderEnum.AddOrderUnityStatus addOrderUnityStatus = convertAddOrderUnionStatus(orderBaseInfoBO);
		if (addOrderUnityStatus != null) {
			orderBaseInfoBO.setAddOrderUnionStatus(addOrderUnityStatus.getValue());
			orderBaseInfoBO.setAddOrderUnionStatusText(addOrderUnityStatus.getDesc());
		}
		// 设置追号计划订单中奖状态
		if (orderBaseInfoBO.getPreBonus() != null && orderBaseInfoBO.getPreBonus() > 0) {
			orderBaseInfoBO.setWinningStatus((int) OrderEnum.OrderWinningStatus.WINNING.getValue());
			orderBaseInfoBO.setWinningText(MathUtil.formatAmountToStr(orderBaseInfoBO.getPreBonus()));
		} else {
			orderBaseInfoBO.setWinningStatus((int) OrderEnum.OrderWinningStatus.NOT_WINNING.getValue());
			orderBaseInfoBO.setWinningText(OrderEnum.OrderWinningStatus.NOT_WINNING.getDesc());
		}
		// 停追条件
		if (!ObjectUtil.isBlank(orderBaseInfoBO.getStopType()) && !ObjectUtil.isBlank(orderBaseInfoBO.getStopCondition())) {
			if (OrderAddStopType.AWARDS.getValue() == orderBaseInfoBO.getStopType()) {// 奖项停追
				try {
					LotteryWinningVO lotteryWinningVO = new LotteryWinningVO();
					lotteryWinningVO.setCode(Integer.parseInt(orderBaseInfoBO.getStopCondition()));
					LotteryWinningBO lotteryWinningBO = lotteryWinningDaoMapper.findSingle(lotteryWinningVO);
					if (!ObjectUtil.isBlank(lotteryWinningBO)) {
						orderBaseInfoBO.setStopCondition(lotteryWinningBO.getName());
					}
				} catch (Exception e) {
					logger.error("奖项停追查询失败, 订单编号:" + orderBaseInfoBO.getOrderCode(), e);
				}
			}
		}
	}

	/**
	 * 代购，合买，追号 订单
	 * @param orderBaseInfoBO
	 */
	private void buildOrderInfo(OrderBaseInfoBO orderBaseInfoBO) {
		// 代购,合买统一状态
		OrderEnum.OrderUnityStatus orderUnionStatus = convertOrderUnionStatus(orderBaseInfoBO);
		if (orderUnionStatus != null) {
			orderBaseInfoBO.setOrderUnionStatus(orderUnionStatus.getValue());
			orderBaseInfoBO.setOrderUnionStatusText(orderUnionStatus.getDesc());
		}
		// 追号代购，出票中显示当前期
		short buyType = orderBaseInfoBO.getBuyType().shortValue();
		if (OrderEnum.BuyType.BUY_CHASE.getValue() == buyType) {
			Boolean isCurrentIssue = lotteryIssueDaoMapper.isCurrentIssue(orderBaseInfoBO.getLotteryCode(),
					orderBaseInfoBO.getLotteryIssue());
			orderBaseInfoBO.setPeriod((isCurrentIssue!= null && isCurrentIssue) ? "当前期" : "");
		}
		short orderStatus = orderBaseInfoBO.getOrderStatus().shortValue();
		short winningStatus = orderBaseInfoBO.getWinningStatus().shortValue();
		// 开奖文案
		Double preBonus = orderBaseInfoBO.getPreBonus() == null ? 0d : orderBaseInfoBO.getPreBonus();
		if (winningStatus == OrderEnum.OrderWinningStatus.NOT_DRAW_WINNING.getValue()) {// 未开奖
			orderBaseInfoBO.setWinningText(OrderEnum.OrderWinningStatus.NOT_DRAW_WINNING.getDesc());
		} else if (winningStatus == OrderEnum.OrderWinningStatus.NOT_WINNING.getValue()) {// 未中奖
			orderBaseInfoBO.setWinningText(OrderEnum.OrderWinningStatus.NOT_WINNING.getDesc());
		} else if (orderStatus == OrderStatus.TICKETED.getValue() && winningStatus == OrderEnum.OrderWinningStatus.WINNING.getValue()
				&& preBonus == 0) {// 待官方公布，出票成功，已中奖且奖金为0
			orderBaseInfoBO.setWinningText("待官方公布");
		} else {// 直接显示奖金
			orderBaseInfoBO.setWinningText(MathUtil.formatAmountToStr(orderBaseInfoBO.getPreBonus()));
		}
		// 开奖派奖时间处理
		setLotteryTime(orderBaseInfoBO);
	}

	private void buildBaseOrderInfo(OrderBaseInfoBO orderBaseInfoBO) {
		Integer lotteryCode = Integer.valueOf(String.valueOf(orderBaseInfoBO.getLotteryCode()).substring(Constants.NUM_0, Constants.NUM_3));
		if(!ObjectUtil.isBlank(orderBaseInfoBO.getDrawCode())) {
			orderBaseInfoBO.setDrawCodeType(getDrawCodeType(lotteryCode, orderBaseInfoBO.getDrawCode()));
		}
	}

	@Override
	public ResultBO<?> queryOrderStatisInfo(OrderStatisticsQueryVo orderStatisticsQueryVo) throws Exception {
		ResultBO<?> result = userInfoCacheService.checkToken(orderStatisticsQueryVo.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		
		orderStatisticsQueryVo.setUserId(userInfo.getId());
		OrderStatisticsInfoBO orderStatisticsInfoBO = orderInfoDaoMapper.queryOrderStatisInfo(orderStatisticsQueryVo);
		return ResultBO.ok(orderStatisticsInfoBO);
	}

	@Override
	public ResultBO<?> queryHomeOrderList(OrderQueryVo orderQueryVo) throws Exception {
		ResultBO<?> result = userInfoCacheService.checkToken(orderQueryVo.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		orderQueryVo.setUserId(userInfo.getId());
		OrderHomeInfoBO orderHomeInfoBO = new OrderHomeInfoBO();
		List<OrderBaseInfoBO> orderListInfoBOs = orderInfoDaoMapper.queryHomeOrderList(orderQueryVo);
		if (!ObjectUtil.isBlank(orderListInfoBOs)) {
            Map<Integer, LotteryBO> allLotteryInfoMap = new HashMap<Integer, LotteryBO>();
            // 从缓存取出彩种信息
            Map<Integer, LotteryBO> lotteryInfoMap = null;//去掉缓存
            for (OrderBaseInfoBO orderListInfoBO : orderListInfoBOs) {
                setOrderListInfo(allLotteryInfoMap, lotteryInfoMap, orderListInfoBO, orderQueryVo,Constants.NUM_2);
            }
            orderHomeInfoBO.setOrderListInfoBOs(orderListInfoBOs);
		}
		OrderStatisBO orderStatisBO = orderInfoDaoMapper.statisOrderBetAndWinCount(orderQueryVo);
		Integer addBetCount = orderInfoDaoMapper.statisAddOrderBetAndWinCount(userInfo.getId());
		Integer betCount = orderStatisBO.getBetCount() == null ? 0 : orderStatisBO.getBetCount();
		Integer count = betCount+addBetCount;
		orderHomeInfoBO.setBetNum(count);
		Integer winCount = orderStatisBO.getWinCount() == null ? 0 : orderStatisBO.getWinCount();
		orderHomeInfoBO.setWinNum(winCount);
		return ResultBO.ok(orderHomeInfoBO);
	}

	@Override
	public ResultBO<?> queryOrderInfo(String orderCode, String token) {
		UserInfoBO userInfo = new UserInfoBO();
		if(!Constants.TOKEN_NO_LOGIN.equals(token)){
			if(!ObjectUtil.isBlank(token)){
				ResultBO<?> result = userInfoCacheService.checkNoUseToken(token);
				if(result.isError()) {
					return result;
				}
				userInfo = (UserInfoBO) result.getData();
			}
		}
		// 查询普通订单
		OrderBaseInfoBO orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderCode, null);//合买的订单的userId是发起人的，跟单人查不到
		if (ObjectUtil.isBlank(orderBaseInfoBO)) {// 普通订单没有则查追号订单
			orderBaseInfoBO = orderAddDaoMapper.queryOrderAddInfo(orderCode, userInfo.getId());
		}
		if (!ObjectUtil.isBlank(orderBaseInfoBO)) {
			//因为查询订单没有用userId过滤，所以非合买单校验当前单是否是否当前用户。到了这段代码，非合买单，此时一定有用户登录信息
			if(orderBaseInfoBO.getBuyType().shortValue() != OrderEnum.BuyType.BUY_TOGETHER.getValue()){
				//正常逻辑不会走这段代码，但是为了保险点，还是校验下
				if(ObjectUtil.isBlank(userInfo) || ObjectUtil.isBlank(userInfo.getId())){
					return ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE);
				}
                 if(orderBaseInfoBO.getUserId().intValue()!=userInfo.getId().intValue()){
                 	return ResultBO.err(MessageCodeConstants.ORDER_IS_NOT_EXIST);
				 }
			}
			//处理异常数据
			if(ObjectUtil.isBlank(orderBaseInfoBO.getLotteryChildCode())){
				orderBaseInfoBO.setLotteryChildCode(orderBaseInfoBO.getLotteryCode());
			}
			Integer lotteryCode = orderBaseInfoBO.getLotteryCode();
			//处理之前的异常数据,因为之前的单lotteryCode存是子编码，这样会查不到记录
			lotteryCode = Integer.valueOf(
					String.valueOf(lotteryCode).substring(Constants.NUM_0, Constants.NUM_3));
			Map<Integer, LotteryBO> lotteryInfoMap = getLotteryInfo(lotteryCode);
			if (!ObjectUtil.isBlank(lotteryInfoMap)) {
				LotteryBO lotteryBO = lotteryInfoMap.get(lotteryCode);
				orderBaseInfoBO.setLotteryName(lotteryBO.getLotteryName());
			}
			Integer lotteryType = Constants.getLotteryType(orderBaseInfoBO.getLotteryCode());
			orderBaseInfoBO.setLotteryType(lotteryType);

			//抄单奖金相关设置 已跟单，已出票，以中奖或者已派件，查询佣金和佣金比例
			setCopyBonusAmount(orderCode, orderBaseInfoBO);

			//设置推单标识  前端只有未推，已推，其余不显示
			setOrderType(orderBaseInfoBO, userInfo.getId(),false);

            //设置合买信息
			if(orderBaseInfoBO.getBuyType().shortValue() == OrderEnum.BuyType.BUY_TOGETHER.getValue()){
				OrderGroupBO orderGroupBO = orderGroupMapper.queryOrderGroupByOrderCode(orderCode);
				if(ObjectUtil.isBlank(orderGroupBO)){
					return ResultBO.err(MessageCodeConstants.NO_EXIST_ORDER_GROUP_INFO);
				}
				OrderDetailGroupInfoBO orderDetailGroupInfoBO = getOrderGroupInfo(orderGroupBO,orderBaseInfoBO.getOrderAmount(),orderBaseInfoBO.getAftBonus());
				if(!ObjectUtil.isBlank(userInfo) && !ObjectUtil.isBlank(userInfo.getId())){
					if(userInfo.getId().equals(orderBaseInfoBO.getUserId()) ){//当前用户是发起人，并且凭密码认购，展示密码
						if(!ObjectUtil.isBlank(orderGroupBO.getApplyWay()) && orderGroupBO.getApplyWay().intValue() == Constants.NUM_2){
							orderDetailGroupInfoBO.setApplyCode(orderGroupBO.getApplyCode());
						}
					}
				}
				orderDetailGroupInfoBO.setApplyWay(orderGroupBO.getApplyWay());
				orderBaseInfoBO.setGrpbuyStatus(orderDetailGroupInfoBO.getGrpbuyStatus());
				orderBaseInfoBO.setOrderDetailGroupInfoBO(orderDetailGroupInfoBO);
			}

		}else{
			logger.info("订单数据为空orderCode:"+orderCode);
		}
		return ResultBO.ok(orderBaseInfoBO);
	}

	/**
	 * 设置抄单相关金额
	 * @param orderCode
	 * @param orderBaseInfoBO
	 */
	private void setCopyBonusAmount(String orderCode, OrderBaseInfoBO orderBaseInfoBO) {
		boolean isMatch = false;
		//推单和跟单
		if( (orderBaseInfoBO.getOrderType().intValue()== Constants.NUM_2 ||orderBaseInfoBO.getOrderType().intValue()== Constants.NUM_3 ) && orderBaseInfoBO.getOrderStatus().intValue() ==Constants.NUM_6 &&  (orderBaseInfoBO.getWinningStatus().intValue()==Constants.NUM_3 || orderBaseInfoBO.getWinningStatus().intValue()==Constants.NUM_4)){
			isMatch = true;
            OrderFollowedInfoBO orderFollowedInfoBO = null;
            if(orderBaseInfoBO.getOrderType().intValue()== Constants.NUM_3){//跟单
				orderFollowedInfoBO = orderFollowedInfoDaoMapper.queryFollowedDetail(orderCode);
			}else {//推单
				orderFollowedInfoBO = orderFollowedInfoDaoMapper.queryIssueDetail(orderCode);
			}
            if(ObjectUtil.isBlank(orderFollowedInfoBO)){
            	return;
			}
            orderBaseInfoBO.setCommissionRate(orderFollowedInfoBO.getCommissionRate());
            orderBaseInfoBO.setUserIssueId(orderFollowedInfoBO.getUserIssueId());
            //提成拥金=（税后奖金-购彩金额）*提成比例 直接去跟单表的佣金
            Double commissionAmount = ObjectUtil.isBlank(orderFollowedInfoBO.getCommissionAmount())?0d:Double.valueOf(orderFollowedInfoBO.getCommissionAmount()) ;
			commissionAmount = NumberUtil.round(commissionAmount,2, BigDecimal.ROUND_HALF_UP) ;
            orderBaseInfoBO.setCommissionAmount(commissionAmount);
			if(orderBaseInfoBO.getOrderType().intValue()== Constants.NUM_3){//跟单
				//展示税后奖金=税后奖金-提成拥金
				Double bonusAmount= NumberUtil.sub(orderBaseInfoBO.getAftBonus(),commissionAmount);
				orderBaseInfoBO.setBonusAmount(bonusAmount);
			}else {//推单
				//展示税后奖金=税后奖金+提成拥金
				Double bonusAmount= NumberUtil.sum(orderBaseInfoBO.getAftBonus(),commissionAmount);
				orderBaseInfoBO.setBonusAmount(bonusAmount);
			}
            if(!ObjectUtil.isBlank(orderFollowedInfoBO.getCopyHeadUrl())){
				orderBaseInfoBO.setCopyHeadUrl(uploadURL + orderFollowedInfoBO.getCopyHeadUrl());
			}
			orderBaseInfoBO.setCopyNickName(orderFollowedInfoBO.getCopyNickName());
			//orderBaseInfoBO.setOrderVisibleType(orderFollowedInfoBO.getOrderVisibleType());
        }

		//不是已中奖的跟单只显示提成比例，发单人信息
		if(!isMatch && orderBaseInfoBO.getOrderType().intValue() == Constants.NUM_3){
			OrderFollowedInfoBO orderFollowedInfoBO = orderFollowedInfoDaoMapper.queryFollowedDetail(orderCode);
			orderBaseInfoBO.setCommissionRate(orderFollowedInfoBO.getCommissionRate());
			orderBaseInfoBO.setUserIssueId(orderFollowedInfoBO.getUserIssueId());
			if(!ObjectUtil.isBlank(orderFollowedInfoBO.getCopyHeadUrl())){
				orderBaseInfoBO.setCopyHeadUrl(uploadURL + orderFollowedInfoBO.getCopyHeadUrl());
			}
			orderBaseInfoBO.setCopyNickName(orderFollowedInfoBO.getCopyNickName());
		}
	}

	@Override
	public ResultBO<?> queryLotteryFailReason(OrderSingleQueryVo orderSingleQueryVo) throws Exception {
		ResultBO<?> result = userInfoCacheService.checkToken(orderSingleQueryVo.getToken());
	       if(result.isError())             	
	       	return result;
		List<TicketInfoSingleBO> ticketInfoSingleBOs = ticketInfoDaoMapper
				.queryFailTicketInfo(orderSingleQueryVo.getOrderCode(), TicketEnum.TicketStatus.OUT_FAIL.getValue());
		if (ObjectUtil.isBlank(ticketInfoSingleBOs)) {
			return ResultBO.err(MessageCodeConstants.TICKET_NOT_EXIST);
		}
		OrderInfoSingleBO orderInfoSingleBO = new OrderInfoSingleBO();
		orderInfoSingleBO.setOrderCode(orderSingleQueryVo.getOrderCode());
		orderInfoSingleBO.setBetFailMsg(ticketInfoSingleBOs.get(0).getChannelRemark());
		return ResultBO.ok(orderInfoSingleBO);
	}

	@Override
	public ResultBO<?> queryAddOrderStopReason(OrderSingleQueryVo orderSingleQueryVo) throws Exception {
		ResultBO<?> result = userInfoCacheService.checkToken(orderSingleQueryVo.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		OrderAddInfoSingleBO orderAddInfoSingleBO = orderAddDaoMapper
				.queryOrderAddStopReason(orderSingleQueryVo.getOrderCode(), userInfo.getId());
		if (ObjectUtil.isBlank(orderAddInfoSingleBO)) {
			return ResultBO.err(MessageCodeConstants.ORDER_IS_NOT_EXIST);
		}
		if (OrderEnum.OrderAddStopType.AWARDS.getValue() == orderAddInfoSingleBO.getStopType()) {// 奖项时还需要查询
																									// lottery_winning
																									// 获取名称
			List<LotWinningBO> lotWinningBOs = lotteryWinningDaoMapper
					.findLotteryWinningName(orderAddInfoSingleBO.getStopCondition(), Integer.valueOf(String
							.valueOf(orderSingleQueryVo.getLotteryCode()).substring(Constants.NUM_0, Constants.NUM_3)));
			if (!ObjectUtil.isBlank(lotWinningBOs)) {
				orderAddInfoSingleBO.setStopCondition(lotWinningBOs.get(0).getName());
			}
		}
		return ResultBO.ok(orderAddInfoSingleBO);
	}

	@Override
	public ResultBO<?> queryUserNumOrderDetail(final UserNumOrderDetailQueryVO queryVO) throws ResultJsonException {
		// 1.入参验证：orderCode，token，pageIndex，pageSize
		Assert.paramNotNull(queryVO.getOrderCode(), "orderCode");
		Assert.paramLegal((null != queryVO.getPageIndex() && Constants.NUM_0 <= queryVO.getPageIndex()), "pageIndex");
		Assert.paramLegal((null != queryVO.getPageSize() && Constants.NUM_0 < queryVO.getPageSize()), "pageSize");
		/*if(Constants.NUM_2!=queryVO.getSource()){
			// 2.登录用户信息
			ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
			if(result.isError())
				return result;
			UserInfoBO userInfo = (UserInfoBO) result.getData();
			// 3.设置查询用户id; 分页组件中页索引是从0开始的，
			queryVO.setUserId(userInfo.getId());
		}*/
		return queryPageInfo(queryVO);
	}

	private ResultBO<?> queryPageInfo(final UserNumOrderDetailQueryVO userNumOrderDetailQueryVO) {
		// 2.获取订单基本信息
		OrderBaseInfoBO orderBaseInfoBO=null;
		if(Constants.NUM_2==userNumOrderDetailQueryVO.getSource()){
			orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(userNumOrderDetailQueryVO.getOrderCode(), userNumOrderDetailQueryVO.getUserId());
		}else{
			orderBaseInfoBO = (OrderBaseInfoBO) this.queryOrderInfo(userNumOrderDetailQueryVO.getOrderCode(), userNumOrderDetailQueryVO.getToken()).getData();
		}
		Integer lotteryCode = orderBaseInfoBO.getLotteryCode();
		//处理之前的异常数据，之前的单lotteryCode存的是子彩种编码，会导致查不到彩种信息
		lotteryCode = Integer.valueOf(
				String.valueOf(lotteryCode).substring(Constants.NUM_0, Constants.NUM_3));
		// 3.订单方案详情
		Integer lotteryType = Constants.getLotteryType(lotteryCode);
		if (lotteryType == Constants.NUM_1) {
			// 数字彩方案详情。暂时先这样，方案上传和合买的方案详情到时再搞分支
			if (OrderEnum.BuyType.BUY_CHASE_PLAN.getValue() == orderBaseInfoBO.getBuyType().shortValue()) {
				// 追号计划方案详情
				UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO();
				queryVO.setOrderAddCode(userNumOrderDetailQueryVO.getOrderCode());
				queryVO.setToken(userNumOrderDetailQueryVO.getToken());
				queryVO.setPageIndex(userNumOrderDetailQueryVO.getPageIndex());
				if (Constants.NUM_1 == userNumOrderDetailQueryVO.getSource()) {
					queryVO.setPageSize(Constants.NUM_5);//移动端默认五条
				} else {
					queryVO.setPageSize(Constants.NUM_10);// PC追号内容一次查出,即最大1000条
				}
				PagingBO<UserNumOrderDetailBO> pageContentData = (PagingBO<UserNumOrderDetailBO>) this.queryUserChaseContent(queryVO)
						.getData();
				if (Constants.NUM_1 == userNumOrderDetailQueryVO.getSource()) {
					queryVO.setPageSize(Constants.NUM_5);//移动端默认五条
				} else {
					queryVO.setPageSize(Constants.NUM_10);
				}
				PagingBO<UserChaseDetailBO> pageData = (PagingBO<UserChaseDetailBO>) this.queryUserChaseDetail(queryVO)
						.getData();
				if (!ObjectUtil.isBlank(pageData.getData())) {
					for (UserChaseDetailBO userChaseDetailBO : pageData.getData()) {
						if (!ObjectUtil.isBlank(userChaseDetailBO.getDrawCode())) {
							userChaseDetailBO.setDrawCodeType(getDrawCodeType(lotteryCode, userChaseDetailBO.getDrawCode()));
						}
					}
				}
				//追号中则显示正在执行的期号
				if (Integer.valueOf(OrderEnum.PayStatus.SUCCESS_PAY.getValue()) == orderBaseInfoBO.getPayStatus() &&
						Integer.valueOf(OrderEnum.AddStatus.CHASING.getKey()) == orderBaseInfoBO.getAddStatus()) {
					//设置正在执行的彩期
					orderBaseInfoBO.setCurAddLotteryIssue(orderAddDaoMapper.findCurChasingIssue(queryVO));
				}
				//从哪期停止追号(中奖停追，用户撤单，系统撤单)
				if (Integer.valueOf(OrderEnum.AddStatus.CHASING.getKey()) != orderBaseInfoBO.getAddStatus()
						&& Integer.valueOf(OrderEnum.AddStatus.CHASE_FINISH.getKey()) != orderBaseInfoBO.getAddStatus()) {
					orderBaseInfoBO.setStopAddLotteryIssue(orderAddDaoMapper.findStopChasingIssue(queryVO));
				}
				// 最新一期开奖号码
				orderBaseInfoBO.setDrawCode(orderAddDaoMapper.queryAddOrderDrawCode(orderBaseInfoBO.getOrderCode()));
				//设置方案详情顶部的信息
				setOrderDetailTopInfo(userNumOrderDetailQueryVO.getOrderCode(), orderBaseInfoBO);
				return ResultBO.ok(pageContentData);
			} else {
				// 数字彩方案详
				userNumOrderDetailQueryVO.setUserId(null);
				PagingBO<UserNumOrderDetailBO> pageData = pageService.getPageData(userNumOrderDetailQueryVO,
						new ISimplePage<UserNumOrderDetailBO>() {
							@Override
							public int getTotal() {
								return orderInfoDaoMapper.findCountUserOrderDetail(userNumOrderDetailQueryVO);
							}

							@Override
							public List<UserNumOrderDetailBO> getData() {
								List<UserNumOrderDetailBO> list = orderInfoDaoMapper.findPagingUserOrderDetail(userNumOrderDetailQueryVO);
								return list;
							}
						});
				return ResultBO.ok(pageData);
			}
		}
		return ResultBO.ok();
	}
	

	@Override
	public ResultBO<?> queryUserSportOrderDetail(final UserSportOrderDetailQueryVO queryVO) throws ResultJsonException {
		// 1.入参验证：orderCode，token，pageIndex，pageSize
		Assert.paramNotNull(queryVO.getOrderCode(), "orderCode");
		Assert.paramLegal((null != queryVO.getPageIndex() && Constants.NUM_0 <= queryVO.getPageIndex()), "pageIndex");
		Assert.paramLegal((null != queryVO.getPageSize() && Constants.NUM_0 < queryVO.getPageSize()), "pageSize");		
		// 2.登录用户信息
		Integer userId = 0;
		if(!ObjectUtil.isBlank(queryVO.getToken())){
			ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
			if(result.isError())
				return result;
			UserInfoBO userInfo = (UserInfoBO) result.getData();
			userId = userInfo.getId();
		}
		// 3.设置查询用户id; 分页组件中页索引是从0开始的，
		//queryVO.setUserId(userId);
		// 4.分页查询
		PagingBO<UserSportOrderDetailBO> pageData = pageService.getPageData(queryVO,
				new ISimplePage<UserSportOrderDetailBO>() {
					@Override
					public int getTotal() {
						return orderInfoDaoMapper.findCountUserSportOrderDetail(queryVO);
					}

					@Override
					public List<UserSportOrderDetailBO> getData() {
						
						List<UserSportOrderDetailBO> list = orderInfoDaoMapper.findPagingUserSportOrderDetail(queryVO);
						
						for (UserSportOrderDetailBO userSportOrderDetailBO : list) {
							List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<OrderMatchInfoBO>();
							
							setOrderDetailInfo(null,userSportOrderDetailBO.getLotteryCode(),
									userSportOrderDetailBO.getLotteryIssue(), userSportOrderDetailBO.getPlanContent(),
									orderMatchInfoBOs, userSportOrderDetailBO.getLotteryChildCode(),null);
							userSportOrderDetailBO.setOrderMatchInfoBOList(orderMatchInfoBOs);
						}
						return list;
					}
				});		
		return ResultBO.ok(pageData);
	}

	@Override
	public ResultBO<?> queryUserChaseContent(final UserChaseDetailQueryVO queryVO) throws ResultJsonException {
		// 1.入参验证：orderAddCode，token
		Assert.paramNotNull(queryVO.getOrderAddCode(), "orderAddCode");
		Assert.paramNotNull(queryVO.getToken(), "token");
		Assert.paramLegal((null != queryVO.getPageIndex() && Constants.NUM_0 <= queryVO.getPageIndex()), "pageIndex");
		Assert.paramLegal((null != queryVO.getPageSize() && Constants.NUM_0 < queryVO.getPageSize()), "pageSize");
		// 2.登录用户信息
		ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		// 3.设置查询用户id;
		queryVO.setUserId(userInfo.getId());
		// queryVO.setPageIndex(queryVO.getPageIndex());
		// 4.分页查询追号内容
		PagingBO<UserNumOrderDetailBO> pageData = pageService.getPageData(queryVO,
				new ISimplePage<UserNumOrderDetailBO>() {
					@Override
					public int getTotal() {
						return orderAddDaoMapper.findCountUserChaseContent(queryVO);
					}

					@Override
					public List<UserNumOrderDetailBO> getData() {
						return orderAddDaoMapper.findPagingUserChaseContent(queryVO);
					}
				});
		return ResultBO.ok(pageData);
	}

	@Override
	public ResultBO<?> queryUserChaseDetail(final UserChaseDetailQueryVO queryVO) throws ResultJsonException {
		// 1.入参验证：orderAddCode，token，pageIndex，pageSize
		Assert.paramNotNull(queryVO.getOrderAddCode(), "orderAddCode");
		Assert.paramLegal((null != queryVO.getPageIndex() && Constants.NUM_0 <= queryVO.getPageIndex()), "pageIndex");
		Assert.paramLegal((null != queryVO.getPageSize() && Constants.NUM_0 < queryVO.getPageSize()), "pageSize");
		// 2.登录用户信息
		ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		// 3.设置查询用户id; 分页组件中页索引是从0开始的
		queryVO.setUserId(userInfo.getId());
		queryVO.setPageIndex(queryVO.getPageIndex());
		// 4.分页查询
		PagingBO<UserChaseDetailBO> pageData = pageService.getPageData(queryVO, new ISimplePage<UserChaseDetailBO>() {
			@Override
			public int getTotal() {
				return orderAddDaoMapper.findCountUserChaseDetail(queryVO);
			}

			@Override
			public List<UserChaseDetailBO> getData() {
				return orderAddDaoMapper.findPagingUserChaseDetail(queryVO);
			}
		});
		return ResultBO.ok(pageData);
	}

	@Override
	public ResultBO<?> queryUserChaseWinningDetail(UserChaseDetailQueryVO queryVO) {
		// 1.入参验证：orderAddCode，token
		Assert.paramNotNull(queryVO.getOrderAddCode(), "orderAddCode");
		// 2.登录用户信息
		ResultBO<?> result = userInfoCacheService.checkToken(queryVO.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		// 3.设置查询用户id;
		queryVO.setUserId(userInfo.getId());
		// 4.查询追号内容
		List<UserChaseWinningDetailBO> data = orderAddDaoMapper.findUserChaseWinningDetail(queryVO);

		return ResultBO.ok(data);
	}

	/**
	 * 获取彩种信息
	 * 
	 * @param lotteryCode
	 * @return
	 */
	private Map<Integer, LotteryBO> getLotteryInfo(Integer lotteryCode) {
		Map<Integer, LotteryBO> lotteryInfoMap = new HashMap<Integer, LotteryBO>();
		LotteryBO lotteryBO = lotteryTypeDaoMapper.findSingleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
		List<LotChildBO> listLotChild = lotteryChildDaoMapper
				.findMultipleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
		lotteryBO.setListLotChildBO(listLotChild);
		lotteryInfoMap.put(lotteryBO.getLotteryCode(), lotteryBO);
		return lotteryInfoMap;
	}

	/**
	 * @desc 前端接口：用户中心-查询等待出票的官方出票时间段(分彩种)
	 * @author huangb
	 * @date 2017年4月19日
	 * @param lotteryCode 彩种id
	 * @return 前端接口：用户中心-查询等待出票的官方出票时间段(分彩种)
	 */
	public IssueOfficialTimeBO queryOfficialTime(Integer lotteryCode) {
		// 1.入参验证：彩种合法
		Assert.paramLegal(Lottery.contain(lotteryCode), "lotteryCode");
		// 2.分彩种类型查询出票时间段
		IssueOfficialTimeBO data = null;
		Lottery lottery = Lottery.getLottery(lotteryCode);
		switch (lottery) {
		case SSQ:// 双色球
		case QLC:// 七乐彩
		case DLT:// 大乐透
		case PL5:// 排列5
		case PL3:// 排列3
		case F3D:// 福彩3D
		case LHC:// 六合彩
		case QXC:// 七星彩
			data = lotteryIssueDaoMapper.findNumOfficialTime(new LotteryVO(lotteryCode));
			break;
		case FB:// 竞技彩足彩
		case BB:// 竞技彩篮彩
		case JQ4:// 四场进球彩
		case SFC:// 十四场胜负彩
		case ZC_NINE:// 九场胜负彩
		case ZC6:// 六场半全场
		case BJDC:// 北京单场
		case SFGG:// 胜负过关
			List<IssueOfficialTimeBO> list = lotteryIssueDaoMapper.findSportOfficialTime(new LotteryVO(lotteryCode));
			if (ObjectUtil.isBlank(list)) {
				break;
			}
			if (list.size() == Constants.NUM_2) {
				data = new IssueOfficialTimeBO(list.get(0).getOfficialEndTime(), list.get(1).getOfficialStartTime(),
						list.get(1).getOfficialEndTime());
			} else if (list.size() == Constants.NUM_1) {
				data = new IssueOfficialTimeBO(null, list.get(0).getOfficialStartTime(),
						list.get(0).getOfficialEndTime());
			}
			break;
		default:
			LotteryTypeVO lotteryTypeVO = new LotteryTypeVO();
			lotteryTypeVO.setLotteryCode(lotteryCode);
			LotteryTypeBO lotteryType = lotteryTypeDaoMapper.findSingle(lotteryTypeVO);
			String comeOutTime = lotteryType.getComeOutTime();
			IssueOfficialTimeBO timeBO = new IssueOfficialTimeBO();
			if (ObjectUtil.isBlank(comeOutTime)) {
				timeBO.setOfficialStartTimeStr("00:00");
				timeBO.setOfficialEndTimeStr("23:59");
			} else {
				String regex = MessageFormat.format("({0})\\|(\\d\\d:\\d\\d)[^-]*-(\\d\\d:\\d\\d)[^-]*", DateUtil.dayForWeek(new Date()));
				Matcher m = Pattern.compile(regex).matcher(comeOutTime);
				if(m.find()) {
					timeBO.setOfficialStartTimeStr(m.group(2));
					timeBO.setOfficialEndTimeStr(m.group(3));
				}else{
					timeBO.setOfficialStartTimeStr("00:00");
					timeBO.setOfficialEndTimeStr("23:59");
				}
			}
			data = timeBO;
			break;
		}
		return data;
	}

	@Override
	public ResultBO<?> queryOrderFlowInfoList(String orderCode,String token) {
		ResultBO<?> result = userInfoCacheService.checkToken(token);
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		OrderBaseInfoBO orderBaseInfoBO = (OrderBaseInfoBO)this.queryOrderInfo(orderCode,token).getData();
		OrderFlowInfoFullBO orderFlowInfoFullBO = new OrderFlowInfoFullBO();
		if(!ObjectUtil.isBlank(orderBaseInfoBO)){
			List<OrderFlowInfoBO> orderFlowInfoBOs = orderFlowInfoMapper.queryOrderFlowInfoList(orderCode, null);
			orderFlowInfoFullBO.setLotteryName(orderBaseInfoBO.getLotteryName());
			orderFlowInfoFullBO.setLotteryIssue(orderBaseInfoBO.getLotteryIssue());
			orderFlowInfoFullBO.setOrderFlowInfoBOs(orderFlowInfoBOs);
		}
		return ResultBO.ok(orderFlowInfoFullBO);
	}
	
	@Override
	public ResultBO<?> queryNoPayOrderDetailList(OrderQueryVo orderQueryVO) {
		ResultBO<?> result = userInfoCacheService.checkToken(orderQueryVO.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		Integer lottCode = orderQueryVO.getLotteryCode();
		if(JCLQConstants.checkLotteryId(orderQueryVO.getLotteryCode())){
			lottCode = Lottery.BB.getName();
		}
		if(JCZQConstants.checkLotteryId(orderQueryVO.getLotteryCode())){
			lottCode = Lottery.FB.getName();
		}
		List<OrderInfoLimitBO> orderInfoLimitBOList = orderInfoDaoMapper.queryNoPayOrderList(null, userInfo.getId(), lottCode +"",orderQueryVO.getLotteryIssue(), null);
		List<String> listDetailOrderCode = new ArrayList<String>();
		List<String> listAddedContentOrderCode = new ArrayList<String>();
		List<OrderInfoDetailLimitBO> detailList = null;//所有D开头订单的明细
		List<OrderInfoDetailLimitBO> addedContentList = null;//Z开头订单的明细
		if(!ObjectUtil.isBlank(orderInfoLimitBOList)){
			for(int i = 0; i < orderInfoLimitBOList.size(); i++){
				if(orderInfoLimitBOList.get(i).getOrderCode().contains("D")){
					listDetailOrderCode.add(orderInfoLimitBOList.get(i).getOrderCode());
				}else{
					listAddedContentOrderCode.add(orderInfoLimitBOList.get(i).getOrderCode());
				}
			}
		}
		//按时间降序排
		Collections.sort(orderInfoLimitBOList, new Comparator<OrderInfoLimitBO>() {
			@Override
			public int compare(OrderInfoLimitBO infoOne, OrderInfoLimitBO infoTwo) {
				Long l = infoTwo.getShowDate().getTime() - infoOne.getShowDate().getTime();
				return l.intValue();
			}
		});
		if(!ObjectUtil.isBlank(listDetailOrderCode)){
			detailList = orderInfoDaoMapper.queryListFromOrderDetail(listDetailOrderCode);
		}
		if(!ObjectUtil.isBlank(listAddedContentOrderCode)){
			addedContentList = orderInfoDaoMapper.queryListFromOrderAddedContent(listAddedContentOrderCode);
		}
		for(int i = 0; i < orderInfoLimitBOList.size(); i++){
			List<OrderInfoDetailLimitBO> detail = new ArrayList<OrderInfoDetailLimitBO>();//具体D开头订单的明细
			List<OrderInfoDetailLimitBO> addedContent = new ArrayList<OrderInfoDetailLimitBO>();//具体Z开头订单的明细
			if(orderInfoLimitBOList.get(i).getOrderCode().contains("D")){
				for(int j = 0; j < detailList.size(); j++){
					if(orderInfoLimitBOList.get(i).getOrderCode().equals(detailList.get(j).getOrderCode())){
						detail.add(detailList.get(j));
					}
				}
				orderInfoLimitBOList.get(i).setOrderInfoDetailLimitBO(detail);
			}else{
				for(int j = 0; j < addedContentList.size(); j++){
					if(orderInfoLimitBOList.get(i).getOrderCode().equals(addedContentList.get(j).getOrderCode())){
						addedContent.add(addedContentList.get(j));
					}
				}
				orderInfoLimitBOList.get(i).setOrderInfoDetailLimitBO(addedContent);
			}
		}
		return ResultBO.ok(orderInfoLimitBOList);
	}

	@Override
	public ResultBO<?> queryOrderListForOrderCodes(List<OrderQueryVo> orderQueryVoList, String token) {
		ResultBO<?> result = userInfoCacheService.checkToken(token);
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		List<String> orderCodeList=null;
		List<String> addCodeList= null;
		List<OrderBaseInfoBO> orderBaseInfoBOs = null;
        if(!ObjectUtil.isBlank(orderQueryVoList)){
           for(OrderQueryVo orderQueryVo:orderQueryVoList){
			   if(orderQueryVo.getBuyType()==Constants.NUM_1){//代购
				   if(orderCodeList==null){
					   orderCodeList= new ArrayList<String>();
				   }
				   orderCodeList.add(orderQueryVo.getOrderCode());
			   }else if(orderQueryVo.getBuyType()==Constants.NUM_4){//追号计划
				   if(addCodeList==null){
					   addCodeList= new ArrayList<String>();
				   }
				   addCodeList.add(orderQueryVo.getOrderCode());
			   }
		   }
			orderBaseInfoBOs = orderInfoDaoMapper.queryOrderListForOrderCodes(orderCodeList, addCodeList, userInfo.getId());
			/*if(!ObjectUtil.isBlank(orderBaseInfoBOs)){
				for(OrderBaseInfoBO orderBaseInfoBO : orderBaseInfoBOs){
					Integer lotteryCode = Integer.valueOf(
							String.valueOf(orderBaseInfoBO.getLotteryCode()).substring(Constants.NUM_0, Constants.NUM_3));
					//官方截止时间
					Date endTicketTime = orderBaseInfoBO.getEndTicketTime();
					//本站订单系统截止时间
					Date endSaleTime = orderBaseInfoBO.getEndSaleTime();
					if(!ObjectUtil.isBlank(endTicketTime) && !ObjectUtil.isBlank(endSaleTime)){
						//提前截止时间差
						long d = DateUtil.getDifferenceTime(endTicketTime, endSaleTime);
						//支付截止时间
						Date orderPayEndTime = Constants.getOrderPayEndTime(lotteryCode,d,endSaleTime);
						orderBaseInfoBO.setOrderPayEndTime(orderPayEndTime);
					}
				}
			}*/
		}
		return ResultBO.ok(orderBaseInfoBOs);
	}

	/**
	 * 订单相关状态转换
	 */
	private void orderStatusTransfer(OrderBaseInfoBO orderBaseInfoBO){
		//处理异常数据
		if(ObjectUtil.isBlank(orderBaseInfoBO.getLotteryChildCode())){
			orderBaseInfoBO.setLotteryChildCode(orderBaseInfoBO.getLotteryCode());
		}
		//原有的三个字段返回DB原有的值，新加一个字段，把订单状态整到一起
		//拆票相关状态统一返回待出票
        /*if(Integer.valueOf(OrderEnum.OrderStatus.WAITING_SPLIT_TICKET.getValue())==orderBaseInfoBO.getOrderStatus()
				|| Integer.valueOf(OrderEnum.OrderStatus.SPLITING_TICKET.getValue()) ==orderBaseInfoBO.getOrderStatus()
				|| Integer.valueOf(OrderEnum.OrderStatus.SPLITING_FAIL.getValue())==orderBaseInfoBO.getOrderStatus()){
			orderBaseInfoBO.setOrderStatus(Integer.valueOf(OrderEnum.OrderStatus.WAITING_TICKET.getValue()));
		}

		//出票失败返回出票中
		if(Integer.valueOf(OrderEnum.OrderStatus.FAILING_TICKET.getValue())==orderBaseInfoBO.getOrderStatus()){
			orderBaseInfoBO.setOrderStatus(Integer.valueOf(OrderEnum.OrderStatus.TICKETING.getValue()));
		}
		//用户取消返回支付失败
		if(Integer.valueOf(OrderEnum.PayStatus.USER_CANCLE_PAY.getValue())==orderBaseInfoBO.getPayStatus()){
			orderBaseInfoBO.setPayStatus(Integer.valueOf(OrderEnum.PayStatus.FAILING_PAY.getValue()));
		}*/
	}

    /**
     * 设置单式上传详情内容
     * @param orderBaseInfoBO
     * @param orderCode
     * @param orderDetailVO
     * @param orderFullDetailInfo
     */
	private void setSingleUploadList(OrderBaseInfoBO orderBaseInfoBO, String orderCode, OrderDetailVO orderDetailVO, OrderFullDetailInfoBO orderFullDetailInfo) {
        //读取文件，从文件里读取每行数据
        if (!ObjectUtil.isBlank(orderBaseInfoBO.getBettingUrl())) {//获取文件url
			QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, savePath, Long.parseLong(limitSize));
			qiniuUploadVO.setUploadURL(uploadURL);

            String downloadPath = orderBaseInfoBO.getBettingUrl().replace("_2_","_1_");
			//downloadPath =downloadPath.substring(downloadPath.indexOf("/")+1,downloadPath.length());
			orderFullDetailInfo.setDownloadUrl(uploadURL+downloadPath+UserConstants.DOWNLOAD_TAIL);

			SingleUploadJCVO singleUploadJCVO = new SingleUploadJCVO();
			singleUploadJCVO.setFilePath(orderBaseInfoBO.getBettingUrl());
			ResultBO<?> resultBO = FileUtil.readFileFromQiniu(singleUploadJCVO, qiniuUploadVO,  SingleUploadEnum.EncodingType.UFT8.getShortName());
			if(resultBO.isError()){
				return ;
			}
			List<String> ret = (List<String>)resultBO.getData();
			String[] singleArray = ret.toArray(new String[ret.size()]);
            logger.info("++++++++++++++++++++++++========================"+singleArray[0].toString());
            orderDetailVO.setOrderCode(orderCode);
            List<OrderDetailInfoBO> orderDetailInfoBOList = orderInfoDaoMapper.querySingleUploadDetailInfo(orderDetailVO);
            if (!ObjectUtil.isBlank(orderDetailInfoBOList)) {
                for (int i = 0; i<= singleArray.length-1; i++) {
                    if (i == orderDetailInfoBOList.size() || i == singleArray.length) {
                        break;
                    }
                    orderDetailInfoBOList.get(i).setBetContent(singleArray[i]);
                }
            }

            orderFullDetailInfo.setSingleUploadDeatailList(orderDetailInfoBOList);
            //将从文件读取的内容存入缓存中
            redisUtil.addObj(CacheConstants.getOrderSingleCacheKey(orderCode), orderFullDetailInfo.getSingleUploadDeatailList(), CacheConstants.TWELVE_HOURS);
        }
    }

	@Override
	public ResultBO<?> queryJoinActivityOrderCount(ActivityOrderQueryInfoVO activityOrderQueryInfoVO)  throws Exception{
		try {
			Integer count = orderInfoDaoMapper.queryJoinActivityOrderCount(activityOrderQueryInfoVO);
			return ResultBO.ok(count);
		}catch (Exception e){
			logger.error("查询用户是否参与过竟足/竟篮首单立减活动信息失败！",e);
			return ResultBO.err();
		}
	}

	@Override
	public ResultBO<?> queryNoPayActivityOrderNo(ActivityOrderQueryInfoVO activityOrderQueryInfoVO) throws Exception {
		try {
			List<String> orderNos = orderInfoDaoMapper.queryNoPayActivityOrderNo(activityOrderQueryInfoVO);
			return ResultBO.ok(orderNos);
		}catch (Exception e){
			logger.error("查询当前用户未支付的活动订单编号失败！",e);
			return ResultBO.err();
		}
	}

	@Override
	public ResultBO<?> queryUserWinInfo() throws Exception {
		List<UserWinInfoBO> winInfoList = null;//orderInfoDaoMapper.queryUserWinInfo();
		List<String> list = new ArrayList<>();
		for (int i = 0; i<winInfoList.size(); i++) {
			StringBuffer str = new StringBuffer();
			if (!ObjectUtil.isBlank(winInfoList.get(i).getNickName()) && !ObjectUtil.isBlank(winInfoList.get(i).getPreBonus()) && !ObjectUtil.isBlank(winInfoList.get(i).getLotteryName())) {
				str.append(UserConstants.CONGRATULATION );
				str.append(StringUtil.encrptionNickname(winInfoList.get(i).getNickName()));
				str.append(UserConstants.IN);
				if (winInfoList.get(i).getLotteryCode().equals(Lottery.FB.getName())) {
					str.append(Lottery.FB.getDesc());
				} else if (winInfoList.get(i).getLotteryCode().equals(Lottery.BB.getName())) {
					str.append(Lottery.BB.getDesc());
				} else {
					str.append(winInfoList.get(i).getLotteryName());
				}
				str.append(UserConstants.WIN );
				str.append(NumberFormatUtil.dispose(winInfoList.get(i).getPreBonus()));
				list.add(str.toString());
			}
		}
		return ResultBO.ok(list);
	}

	@Override
	public ResultBO<?> queryYearOrderInfoDetail(OrderQueryVo orderQueryVo) {
		//彩种 彩期  订单详情 开奖时间
		UserInfoBO userInfo = userUtil.getUserByToken(orderQueryVo.getToken());
		Assert.notNull(userInfo, MessageCodeConstants.TOKEN_LOSE_SERVICE);
		orderQueryVo.setUserId(userInfo.getId());
		OrderInfoDetailBo detail = orderInfoDaoMapper.queryYearOrderInfoDetail(orderQueryVo);	
		if(detail==null){
			return new ResultBO<OrderInfoDetailBo>(detail);
		}
		//防止手动更新时间，开奖时间从彩期表中同步
		NewIssueBO issue = lotteryIssueDaoMapper.findLotteryIssue(Integer.parseInt(detail.getLotteryCode()), detail.getLotteryIssue());
		detail.setLotteryTime(issue.getLotteryTime());
		//中奖状态
		return new ResultBO<OrderInfoDetailBo>(detail);
	}

	/**
	 *
	 * 查询渠道订单接口
	 * @param vo
	 * @author zhouyang
	 * @return
	 */
	@Override
	public List<OrderChannelBO> queryChannelOrderList(OrderChannelVO vo) {
		List<OrderChannelBO> oList = orderInfoDaoMapper.queryChannelOrderList(vo);
		if (!ObjectUtil.isBlank(oList)) {
			for (OrderChannelBO orderChannelBO : oList) {
				if (!ObjectUtil.isBlank(orderChannelBO.getChannelMemberId()) && orderChannelBO.getChannelMemberId().contains("_")) {
					String memberId = orderChannelBO.getChannelMemberId().split("_")[1].toString().trim();
					orderChannelBO.setChannelMemberId(memberId);
				}
			}
		}
		return oList;
	}


	/**
	 *
	 * @param orderCode
	 * @return
	 */
	/**
	 * 合买用户能否查看赛事内容
	 * @param orderCode
	 * @param userId 当前登录用户ID
	 * @return
	 */
	private Boolean isCanSeeMatchForGroup(String orderCode,Integer userId,Integer winningStatus,OrderDetailGroupInfoBO orderDetailGroupInfoBO,Integer orderGroupContentId){
		if(userId.intValue() == orderDetailGroupInfoBO.getUserId().intValue()){//订单详情 当前用户和发单人是同一个人
			return true;
		}

		if(Constants.NUM_1 == orderDetailGroupInfoBO.getVisibleType().intValue()){//完全公开
			return true;
		}

		if(Constants.NUM_2 == orderDetailGroupInfoBO.getVisibleType().intValue()){//跟单后公开
			//userId,orderCode查跟单表有记录，则true
			List<OrderMyGroupBO> orderGroupContentBOs = orderGroupContentMapper.queryOrderGroupContentByUserId(orderCode,userId);//合买详情页点进去，只要之前已经跟单了，页可以看到
			if(ObjectUtil.isBlank(orderGroupContentBOs)){
				return false;
			}else{
				return true;
			}
		}
		if(Constants.NUM_3 == orderDetailGroupInfoBO.getVisibleType().intValue()){//开奖后公开
			if(winningStatus.shortValue() != OrderWinningStatus.NOT_DRAW_WINNING.getValue()){
               return true;
			}else {
				return false;
			}
		}
        return true;
	}

	/**
	 * 订单详情，合买详情 设置合买信息
	 * @param orderGroupBO
	 * @param orderAmount
	 * @return
	 */
	private OrderDetailGroupInfoBO getOrderGroupInfo(OrderGroupBO orderGroupBO,Double orderAmount,Double afterBonus){
		OrderDetailGroupInfoBO orderDetailGroupInfoBO = new OrderDetailGroupInfoBO();
		try {
			if(!ObjectUtil.isBlank(orderGroupBO.getTitle()) && Base64.isBase64(orderGroupBO.getTitle())){
				orderDetailGroupInfoBO.setTitle(new String(Base64.decodeBase64(orderGroupBO.getTitle().getBytes(SysUtil.getSystemEncoding()))));
			}else{
				orderDetailGroupInfoBO.setTitle(orderGroupBO.getTitle());
			}
			if(!ObjectUtil.isBlank(orderGroupBO.getDescription()) &&  Base64.isBase64(orderGroupBO.getDescription())){
				orderDetailGroupInfoBO.setRemark(new String(Base64.decodeBase64(orderGroupBO.getDescription().getBytes(SysUtil.getSystemEncoding()))));
			}else{
				orderDetailGroupInfoBO.setRemark(orderGroupBO.getDescription());
			}
		}catch (UnsupportedEncodingException e){
			logger.error("合买标题，描述解码失败！",e);
		}
		orderDetailGroupInfoBO.setTitle(checkKeyword(orderDetailGroupInfoBO.getTitle()));
		orderDetailGroupInfoBO.setRemark(checkKeyword(orderDetailGroupInfoBO.getRemark()));
		orderDetailGroupInfoBO.setGroupNick(orderGroupBO.getNickName());
		orderDetailGroupInfoBO.setProgress(orderGroupBO.getProgress());
		orderDetailGroupInfoBO.setProgressAmount(orderGroupBO.getProgressAmount());
		orderDetailGroupInfoBO.setGrpbuyStatus(orderGroupBO.getGrpbuyStatus());
		orderDetailGroupInfoBO.setGuaranteeRatio(orderGroupBO.getGuaranteeRatio());
		orderDetailGroupInfoBO.setGuaranteeAmount(orderGroupBO.getGuaranteeAmount());
		orderDetailGroupInfoBO.setGroupRatio(orderGroupBO.getMinBuyRatio());
		orderDetailGroupInfoBO.setGroupAmount(orderGroupBO.getMinBuyAmount());
		orderDetailGroupInfoBO.setCommissionRatio(orderGroupBO.getCommissionRatio());
		orderDetailGroupInfoBO.setCommissionAmount(orderGroupBO.getCommissionAmount());
		Double residualRatio = NumberUtil.sub(100d,orderGroupBO.getProgress());//100-合买进度（不包括保底）
		Double residualAmount = NumberUtil.sub(orderAmount,orderGroupBO.getProgressAmount());//订单金额-合买进度金额（不包括保底）
		orderDetailGroupInfoBO.setResidualRatio(residualRatio);
		orderDetailGroupInfoBO.setResidualAmount(residualAmount);
		orderDetailGroupInfoBO.setBuyCount(orderGroupBO.getBuyCount());
		orderDetailGroupInfoBO.setUserId(orderGroupBO.getUserId());
		orderDetailGroupInfoBO.setVisibleType(orderGroupBO.getVisibleType());
		orderDetailGroupInfoBO.setBonusFlag(orderGroupBO.getBonusFlag());
		if(!ObjectUtil.isBlank(afterBonus)){
			Double totalBonus=0d;
			if(!ObjectUtil.isBlank(orderGroupBO.getCommissionAmount())){
				totalBonus = NumberUtil.sub(afterBonus,orderGroupBO.getCommissionAmount());
			}else{
				totalBonus = afterBonus;
			}
			orderDetailGroupInfoBO.setTotalBonus(totalBonus);
		}
		return orderDetailGroupInfoBO;
	}

	/**
	 * 设置合买其他相关信息
	 */
	private  void setOrderGroupOtherInfo(OrderDetailGroupInfoBO orderDetailGroupInfoBO, Integer userId, String orderCode, Integer orderGroupContentId,Integer orderUserId,OrderBaseInfoBO orderBaseInfoBO){
		if(!ObjectUtil.isBlank(orderGroupContentId)){//订单详情有此Id,合买详情没有此ID,则不需要显示本次认购金额
			OrderGroupContentBO orderGroupContentBO = orderGroupContentMapper.queryOrderGroupContentById(orderGroupContentId);
			//PC订单详情需要展示本次跟单 认购金额，认购比例，派奖金额
			orderDetailGroupInfoBO.setMyBuyRatio(orderGroupContentBO.getBuyRatio());
			orderDetailGroupInfoBO.setMyBuyAmount(orderGroupContentBO.getBuyAmount());
			orderDetailGroupInfoBO.setMyAfterBonus(orderGroupContentBO.getSendBonus());

			OrderGroupContentBO groupContentBO = orderGroupContentMapper.findOrderGroupRecord(orderCode);//发单人记录
			if(groupContentBO.getId().intValue() == orderGroupContentId.intValue()){//当前单是发单记录
				if(orderDetailGroupInfoBO.getBonusFlag().intValue() == Constants.NUM_2){//给发起人不需要处理，直接取订单的加奖,给所有人，直接取跟单表记录
					orderBaseInfoBO.setAddedBonus(orderGroupContentBO.getAddedBonus());
					orderBaseInfoBO.setGetRedAmount(orderGroupContentBO.getSiteAddedBonus());
				}
			}else {//当前单是跟单
				if(orderDetailGroupInfoBO.getBonusFlag().intValue() == Constants.NUM_2){//给所有人，去跟单表
					orderBaseInfoBO.setAddedBonus(orderGroupContentBO.getAddedBonus());
					orderBaseInfoBO.setGetRedAmount(orderGroupContentBO.getSiteAddedBonus());
				}else{//给发起人，清空加奖
					orderBaseInfoBO.setAddedBonus(null);
					orderBaseInfoBO.setGetRedAmount(null);
				}
			}
		}

        //我的认购 可能跟单多次
		List<OrderMyGroupBO> orderGroupContentBOs = orderGroupContentMapper.queryOrderGroupContentByUserId(orderCode,userId);
		orderDetailGroupInfoBO.setOrderMyGroupList(orderGroupContentBOs);

        //发单人中奖次数，累计奖金
		OrderStatisticsQueryVo orderStatisticsQueryVo = new OrderStatisticsQueryVo();
		orderStatisticsQueryVo.setSource(Constants.NUM_2);
		orderStatisticsQueryVo.setUserId(orderUserId);
		OrderStatisticsInfoBO orderStatisticsInfoBO = orderInfoDaoMapper.queryOrderStatisInfo(orderStatisticsQueryVo);

		orderDetailGroupInfoBO.setGroupWinCount(orderStatisticsInfoBO.getWinCount());
		orderDetailGroupInfoBO.setGroupTotalBonus(orderStatisticsInfoBO.getWinTotalAmount());

	}

	/**
	 *
	 * 过滤敏感词
	 * @param string
	 * @return
	 */
	public String checkKeyword(String string){
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



}
