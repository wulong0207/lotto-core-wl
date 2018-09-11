package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.OrderInfoCacheService;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.dic.dao.DicDataDetailDaoMapper;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryBettingMulDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryLimitMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderAddDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.order.po.OrderInfoPO;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.IOrderService;
import com.hhly.lottocore.remote.sportorder.service.ValidateService;
import com.hhly.lottocore.remote.sportorder.service.impl.singleupload.SingleUploadOrderExecutor;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.SportsOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryPr;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.PayConstants;
import com.hhly.skeleton.base.constants.SportConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.model.DicDataEnum;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.OrderNoUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.sportsutil.SportsZsUtil;
import com.hhly.skeleton.cms.dicmgr.bo.DicDataDetailBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoLimitBO;
import com.hhly.skeleton.lotto.base.order.bo.WinBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.WinVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdConditionVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdDetailVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author huangb
 *
 * @Date 2016年11月30日
 *
 * @Desc 订单服务
 */
@Service("orderService")
public class OrderServiceImpl implements IOrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	/**
	 * 彩种
	 */
	@Autowired
	private LotteryTypeDaoMapper lotteryTypeDaoMapper;
	/**
	 * 倍数、注数和时间关系
	 */
	@Autowired
	private LotteryBettingMulDaoMapper  lotteryBettingMulDaoMapper ;
	/**
	 * 子玩法
	 */
	@Autowired
	public LotteryChildDaoMapper lotteryChildDaoMapper;
	/**
	 * 彩期
	 */
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;
	@Autowired
	@Qualifier("sportsOrderValidate")
	private SportsOrderValidate sportsOrderValidate;
	/**
	 * 限号
	 */
	@Autowired
	private LotteryLimitMapper lotteryLimitDaoMapper;
	/**
	 * 订单数据接口
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	/**
	 * 追号计划数据接口
	 */
	@Autowired
	private OrderAddDaoMapper orderAddDaoMapper;

	/**
	 * 数据字典接口
	 */
	@Autowired
	private DicDataDetailDaoMapper dicDataDetailDaoMapper;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Value("${limit_order_num}")
	private String limitCount;
	
	/**
	 * 分析师每天发单最多次数
	 */
	@Value("${rmcd_day_max_count}")
	private int dayMaxCount;
	
	@Autowired
	private OrderInfoBatchService orderInfoBatchService;
	
	/**
	 * redis cache 总开关
	 */
	@Value("${flag}")
	private String flag;
	
	/**
	 * 订单最高金额限制
	 */
	@Value("${limit_order_amount}")
	private String limitAmount;

	/**
	 * 订单验证服务
	 */
	@Resource(name="validateService")
	private ValidateService validateService;
	
	@Resource(name="orderInfoCacheService")
	private OrderInfoCacheService orderInfoCacheService;
	
	@Resource(name="userInfoCacheService")
	private UserInfoCacheService userInfoCacheService;
	
	/**
	 * 单式上传业务处理
	 */
	@Resource(name="singleUploadOrderExcetor")
	private SingleUploadOrderExecutor singleUploadOrderExcetor;

	@Resource(name="jcDataService")
	private IJcDataService jcDataService;

	@Override
	public ResultBO<?> addOrder(OrderInfoVO orderInfo)throws Exception {
		//1.校验和组装订单信息
		ResultBO<?> resultBO = validateAndBuildOrderInfo(orderInfo);
		if(resultBO.isError()){
			return resultBO;
		}
		Map<String,Integer> tempMap = (Map<String,Integer>)resultBO.getData();
		OrderInfoPO orderPO = new OrderInfoPO(orderInfo);
		logger.info("==========OrderInfoPO入库前[buyTime="+orderPO.getBuyTime()+",endTicketTime="+orderPO.getEndTicketTime()+",endCheckTime="+orderPO.getEndCheckTime()+",endSysTime="+orderPO.getEndSysTime()+",endLocalTime="+orderPO.getEndLocalTime()+"]");
		orderInfoDaoMapper.addOrder(orderPO);
		//2.订单明细入库
		orderInfoBatchService.addOrderDetailInfoList(orderPO.getOrderDetailList());
		OrderInfoBO orderInfoBO = buildFrontDataAndUpdateNoPayCount(orderInfo, tempMap.get("userId"), tempMap.get("count"), orderPO.getId());
		return ResultBO.ok(orderInfoBO);
	}
	

	/**
	 * 校验和组装订单信息
	 * @param orderInfo
	 * @return
	 * @throws Exception
	 */
	private ResultBO<?> validateAndBuildOrderInfo(OrderInfoVO orderInfo) throws Exception{
		ResultBO<?> resultBO = this.resubmitValidate(orderInfo);
		if(resultBO.isError()){
			return resultBO;
		}
		if(ObjectUtil.isBlank(orderInfo.getLotteryCode()) || ObjectUtil.isBlank(orderInfo.getLotteryIssue())){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}
		setLotteryChildCode(orderInfo);
		ConcurrentHashMap<String, Object>  map = dealDAO(orderInfo);
		UserInfoBO userInfoBO = (UserInfoBO)map.get("userInfoBO");
		orderInfo.setLotteryBO((LotteryBO)map.get("lotteryBO"));//设置彩种信息，用于verifyChildStatus验证
		// 0.判断当前用户未支付订单情况
		if(orderInfo.getBuyType().intValue() != PayConstants.BuyTypeEnum.JOINT_PURCHASE.getKey()){
			resultBO  = orderInfoCacheService.verifyOrderNoPayCount(orderInfo, (UserInfoBO)map.get("userInfoBO"));
		}else{
			resultBO  = orderInfoCacheService.verifyOrderGroupNoPayCount(orderInfo, (UserInfoBO)map.get("userInfoBO"));
		}
		if(resultBO.isError()){
			return resultBO;
		}
		Integer count = (Integer)resultBO.getData();
		// 1.订单入库前校验
		ResultBO<?> result = validateService.validateOrder(orderInfo, map);
		if(result.isError()){
			logger.error("校验和组装订单信息", orderInfo);
			return result;
		}
		// 2.订单基本信息入库
		// 订单编号
		setOtherOrderInfo(orderInfo, map);
		if(MathUtil.compareTo(orderInfo.getOrderAmount(), Double.valueOf(limitAmount)) > 0){
			return ResultBO.err(MessageCodeConstants.ORDER_AMOUNT_LIMIT_SERVICE);
		}
		Map<String,Integer> tempMap = new ConcurrentHashMap<String,Integer>();
		tempMap.put("userId",userInfoBO.getId());
		tempMap.put("count",count);
		return ResultBO.ok(tempMap);
	}

	/**
	 * 设置订单其他信息
	 * @param orderInfo
	 * @param map
	 */
	private void setOtherOrderInfo(OrderInfoVO orderInfo, ConcurrentHashMap<String, Object> map) {
		if(orderInfo.getBuyType().intValue() != Constants.NUM_3){
			orderInfo.setOrderCode(OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_D));//代购
		}else{
			orderInfo.setOrderCode(OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_H));//合买
		}
		orderInfo.setPayStatus(PayConstants.PayStatusEnum.WAITTING_PAYMENT.getKey());
		if(!ObjectUtil.isBlank(orderInfo.getRedeemCode())){
			orderInfo.setPayStatus(PayConstants.PayStatusEnum.PAYMENT_SUCCESS.getKey());//兑换码不为空，支付状态为：支付成功--对接人：黄诚芳
		}
		orderInfo.setOrderStatus((short)OrderEnum.OrderStatus.WAITING_SPLIT_TICKET.getValue());
		orderInfo.setWinningStatus(OrderEnum.OrderWinningStatus.NOT_DRAW_WINNING.getValue());

		//彩种名称
		LotteryBO lotteryBO = (LotteryBO)map.get("lotteryBO");
		LotChildBO lotChildBO = (LotChildBO)map.get("lotChildBO");

		if(!ObjectUtil.isBlank(lotteryBO)){
			//设置彩种名称
			orderInfo.setLotteryName(ObjectUtil.isBlank(lotChildBO)?lotteryBO.getLotteryName():lotChildBO.getChildName());
			//最后检票时间 = 彩期官方截止时间 + 彩种最后检票提前时间秒
			Date date = DateUtil.addSecond(orderInfo.getEndTicketTime(), lotteryBO.getEndCheckTime());
			if(!ObjectUtil.isBlank(date)){
				orderInfo.setEndCheckTime(date);
				logger.info("===========setOtherOrderInfo方法，最终检票时间为："+date);
			}
		}
	}

	/**
	 * 组建前端需要参数和返回未支付订单数
	 * @param orderInfo
	 * @param userId
	 * @param count
	 * @param id
	 * @return
	 */
	private OrderInfoBO buildFrontDataAndUpdateNoPayCount(OrderInfoVO orderInfo, Integer userId, Integer count,Long id) {
		ResultBO<?> resultBO = null;
		OrderInfoBO orderInfoBO = new OrderInfoBO();
		orderInfoBO.setId(id);
		//4.拼装返回参数(订单编号等)   添加总金额验证及拼装返回参数BO
		orderInfoBO.setCounter(count + 1);
		orderInfoBO.setOrderCode(orderInfo.getOrderCode());
		orderInfoBO.setBuyType(orderInfo.getBuyType());
		//更新缓存未支付订单数,活动订单/单式上传不参与未支付订单的校验
		if(!ObjectUtil.isBlank(orderInfo.getActivityCode()) || orderInfo.getIsSingleOrder() == Constants.NUM_1){//活动订单/单式上传不参加未支付的校验，永远返回0即可
			return orderInfoBO;
		}else{
		    if(orderInfo.getBuyType().intValue() != PayConstants.BuyTypeEnum.JOINT_PURCHASE.getKey()){
                resultBO = orderInfoCacheService.updateOrderNoPayCount(orderInfo.getLotteryCode(), 1, userId);
            }else{
                resultBO = orderInfoCacheService.updateOrderGroupNoPayCount(orderInfo.getLotteryCode(), 1, userId);
            }
			if(resultBO.isError()){
				logger.error(resultBO.getMessage());
			}
		}
		return orderInfoBO;
	}

	private void setLotteryChildCode(OrderInfoVO orderInfo) {
		if(JCZQConstants.checkLotteryId(orderInfo.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfo.getLotteryCode())){
			orderInfo.setLotteryChildCode(orderInfo.getLotteryCode());
		}else{
			if(!ObjectUtil.isBlank(orderInfo.getOrderDetailList())){
				orderInfo.setLotteryChildCode(orderInfo.getOrderDetailList().get(0).getLotteryChildCode());
			}
		}
	}


	/**
	 * 重复提交验证(排除1-2秒内同一用户,相同的投注内容生成订单)
	 * @param orderInfoVO
	 * @return
	 * @throws Exception
	 */
	private ResultBO<?> resubmitValidate(OrderInfoVO orderInfoVO)throws Exception{
		//开关判断
		if(ObjectUtil.isBlank(orderInfoVO.getVerifyOpen()) || Constants.YES != orderInfoVO.getVerifyOpen()){
			String betCache = orderInfoCacheService.getOrderByToken(orderInfoVO.getToken());
			String newBet = packageBetContent(orderInfoVO);
			if(!ObjectUtil.isBlank(newBet)){
				if(!ObjectUtil.isBlank(betCache) && betCache.equals(newBet))
					return ResultBO.err(MessageCodeConstants.RESUBMIT_BET_SERVICE);
				orderInfoCacheService.updataOrderByToken(orderInfoVO.getToken(),newBet);
			}
		}

		return ResultBO.ok();
	}
	
	/**
	 * 简单组装投注内容(重复提交验证使用)
	 * @param orderInfoVO
	 * @return
	 */
	private String packageBetContent(OrderInfoVO orderInfoVO){
		StringBuilder sbBet = new StringBuilder();
		List<OrderDetailVO> odvs = orderInfoVO.getOrderDetailList();
		for(OrderDetailVO tempODV : odvs){
			sbBet.append(tempODV.getPlanContent()).append(tempODV.getMultiple());
		}
		return sbBet.append(orderInfoVO.getMultipleNum()).toString();
	}

	/**
	 * 处理与Oracle数据或者缓存服务器取数据交互
	 * @author longguoyou
	 * @date 2017年3月30日
	 * @param orderInfoVO
	 * @return
	 */
	private ConcurrentHashMap<String,Object> dealDAO(OrderInfoVO orderInfoVO){
		long begin = System.currentTimeMillis();
		//1、查缓存
		//2、判断从缓存获取是否存在
		//3、如不存在，查数据库，并加到缓存
		//************************************彩种信息*******************************************
		//竞技彩传递子玩法，所以截取前三位为大彩种
		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		//彩种
		LotteryBO lotteryBO = null;
		if(ObjectUtil.isBlank(lotteryBO)){
			lotteryBO = lotteryTypeDaoMapper.findSingleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
		}
		
		//子玩法集合
		List<LotChildBO> listLotChild = null;
		if(ObjectUtil.isBlank(listLotChild)){
			listLotChild = lotteryChildDaoMapper.findMultipleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
		}
		//子玩法翻译用
		LotChildBO lotChildBO = null;
		for(int i = 0; i < listLotChild.size(); i++){
			//竞技彩
			if(String.valueOf(orderInfoVO.getLotteryCode()).length() > 3 && listLotChild.get(i).getLotteryCode().equals(Integer.valueOf(lotteryCode)) 
					&& listLotChild.get(i).getLotteryChildCode().equals(orderInfoVO.getLotteryChildCode())){
				lotChildBO = listLotChild.get(i);
				break;
			}
			//北单
			if(!ObjectUtil.isBlank(orderInfoVO.getLotteryChildCode()) && BJDCConstants.checkLotteryId(orderInfoVO.getLotteryChildCode())){
				if(listLotChild.get(i).getLotteryCode().equals(Integer.valueOf(lotteryCode)) 
					&& listLotChild.get(i).getLotteryChildCode().equals(orderInfoVO.getLotteryChildCode())){
					lotChildBO = listLotChild.get(i);
					break;
				}
			}
		}
		//设置彩种子玩法集
		if(!ObjectUtil.isBlank(lotteryBO)){
			lotteryBO.setListLotChildBO(listLotChild);
		}
		//************************************投注注数、倍数与时间关系信息*******************************

		List<LotBettingMulBO> betMulBOList = null;
		if(ObjectUtil.isBlank(betMulBOList)){
			betMulBOList = lotteryBettingMulDaoMapper.findMultipleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
		}
		
		//************************************彩期信息***********************************************

		IssueBO issueBO = null;
		if(ObjectUtil.isBlank(issueBO)){
			issueBO = lotteryIssueDaoMapper.findSingleFront(new LotteryVO(Integer.valueOf(lotteryCode), orderInfoVO.getLotteryIssue()));
		}
		
		//***********************************限号****************************************************
		//限号参数
		LotteryVO lotteryVO = new LotteryVO(Integer.valueOf(lotteryCode));
		// 1：启用；2：禁用；3：过期
		lotteryVO.setStatus((short)1);//限号状态
		lotteryVO.setLimitDate(new Date());//限号时间
		//限号信息
		List<LimitNumberInfoBO> limitNumberInfoBOList = null;
		if(ObjectUtil.isBlank(limitNumberInfoBOList)){
			limitNumberInfoBOList = lotteryLimitDaoMapper.findMultipleLimitFront(lotteryVO);
		}

		//*************************************渠道限制彩种销售状态**************************************
		List<DicDataDetailBO> listDicData = dicDataDetailDaoMapper.findByCode(DicDataEnum.LOTTO_LIMIT_CHANNEL.getDicCode());

		//************************************获取销售截止时间******************************************
		boolean isIssueEnd = false;
		Date[] dates = new Date[1];
		Date orderEndTime = null;
		SportAgainstInfoBO againstInfoBO = null;
	    if(ObjectUtil.isBlank(issueBO)){
	    	issueBO = lotteryIssueDaoMapper.findSingleFront(new LotteryVO(Integer.valueOf(lotteryCode), orderInfoVO.getLotteryIssue()));
	    }
	    if(ObjectUtil.isBlank(issueBO)){
	    	throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE));
	    }
	    Date issueEndTime = issueBO.getSaleEndTime();
    	LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(lotteryCode));
    	switch (lott) {
    	case GPC://高频彩
    	case SZC://数字彩
    	case ZC://足彩
		case GYJ://冠亚军
    		orderEndTime = issueEndTime;
    		break;
    	case JJC://竞技彩
    	case BJDC://北单
    		//验证buyScreen是否为空
    		if(ObjectUtil.isBlank(orderInfoVO.getBuyScreen())){
    			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE));
    		}
    		List<String> matchs = Arrays.asList(orderInfoVO.getBuyScreen().split(SymbolConstants.COMMA));
    		//根据彩种id和赛事id集合，查询对阵赛事信息
//    		List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstEndMatch(Integer.valueOf(lotteryCode), matchs);
//    		againstInfoBO = listAgainstInfoBO.get(0);
//    		orderEndTime = againstInfoBO.getSaleEndTime();
			againstInfoBO = getFirstSaleEndTimeAgainstInfoBO(Integer.valueOf(lotteryCode), matchs);
			logger.info("=========buyScreen[" + orderInfoVO.getBuyScreen()+"],firstEnd:[systemCode="+againstInfoBO.getSystemCode()+",startTime="+DateUtil.convertDateToStr(againstInfoBO.getStartTime(),DateUtil.DEFAULT_FORMAT)+",endTime="+DateUtil.convertDateToStr(againstInfoBO.getSaleEndTime(),DateUtil.DEFAULT_FORMAT)+"]=========");
			if(!ObjectUtil.isBlank(againstInfoBO)){
				orderEndTime = againstInfoBO.getSaleEndTime();
			}
    		break;
    	default://足彩
    		break;
    	}
    	dates[0] = orderEndTime;
        //用户信息
    	ResultBO<?> result = userInfoCacheService.checkToken(orderInfoVO.getToken());		
    	if(result.isError()){
    		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE));
    	}
    	
    	UserInfoBO userInfoBO = (UserInfoBO)result.getData();

    	ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<String,Object>();
    	if(ObjectUtil.isBlank(lotteryBO)){
    		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE));
    	}
		map.put("lotteryBO", lotteryBO);
		if(ObjectUtil.isBlank(betMulBOList)){
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_BETING_CONFIG_NOT_EXIST_SERVICE, orderInfoVO.getLotteryCode()));
		}
		map.put("betMulBO", betMulBOList);
		if(ObjectUtil.isBlank(issueBO)){
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE));
		}
		map.put("issueBO", issueBO);
		if(!ObjectUtil.isBlank(limitNumberInfoBOList)){
			map.put("limitCode", limitNumberInfoBOList);//限号
		}
		if(!ObjectUtil.isBlank(againstInfoBO)){
			map.put("againstInfoBO", againstInfoBO);//最早截止赛事对象(仅saleEndTime、startTime有值)
		}
		map.put("orderEndTime", dates);//销售截止时间 供varifyBoundary内部使用，数字彩和竞技彩有区别
		if(ObjectUtil.isBlank(userInfoBO)){
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE));
		}
		orderInfoVO.setUserId(userInfoBO.getId());
		map.put("userInfoBO", userInfoBO);//用户信息
		logger.debug("userInfoBO=" + userInfoBO);
		map.put("isIssueEnd", isIssueEnd);//是否取彩期截止销售时间
		if(!ObjectUtil.isBlank(lotChildBO)){
			map.put("lotChildBO", lotChildBO);//用于翻译竞技彩子玩法存order_info表lotteryCode字段情况
		}
		map.put("flag", false);//是否奖金优化
		map.put("listDicData",listDicData);//渠道限制彩种销售
		logger.debug("dealDAO耗时：" + (System.currentTimeMillis() - begin)/1000 + "秒");
		return map;
	}

	/**
	 * 通过redis缓存获取对阵最早截止的比赛
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private SportAgainstInfoBO getFirstSaleEndTimeAgainstInfoBO(Integer lotteryCode, List<String> matchs){
		LotteryEnum.Lottery lot = LotteryEnum.Lottery.getLottery(lotteryCode);
		switch (lot) {
			case FB:
				return getJczqFirstEndSportAgainstInfoBOs(lotteryCode, matchs);
			case BB:
				return getJclqFirstEndSportAgainstInfoBOs(lotteryCode, matchs);
			case SFGG:
			case BJDC:
				return getBjdcFirstEndSportAgainstInfoBOs(lotteryCode, matchs);
			default:
				return null;
		}
	}
	/**
	 * 根据彩种id和赛事id集合，查询北单赛事最早截止的时间
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private SportAgainstInfoBO getBjdcFirstEndSportAgainstInfoBOs(Integer lotteryCode, List<String> matchs) {
		List<SportAgainstInfoBO> listSportAgainstInfoBOs = new ArrayList<SportAgainstInfoBO>();
		for(String systemCode : matchs){
			BjDaoBO bjDaoBO = jcDataService.findBjDataBOBySystemCode(systemCode, String.valueOf(lotteryCode));
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			if(!ObjectUtil.isBlank(bjDaoBO)){
				sportAgainstInfoBO.setSystemCode(bjDaoBO.getSystemCode());
				sportAgainstInfoBO.setSaleEndTime(bjDaoBO.getSaleEndDate());
				sportAgainstInfoBO.setStartTime(bjDaoBO.getStartTimeStamp());
				listSportAgainstInfoBOs.add(sportAgainstInfoBO);
			}
		}
		this.sortByEndTimeAsc(listSportAgainstInfoBOs);
		return ObjectUtil.isBlank(listSportAgainstInfoBOs)?null:listSportAgainstInfoBOs.get(0);
	}

	/**
	 * 根据彩种id和赛事id集合，查询竞彩篮球赛事最早截止的时间
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private SportAgainstInfoBO getJclqFirstEndSportAgainstInfoBOs(Integer lotteryCode, List<String> matchs) {
		List<SportAgainstInfoBO> listSportAgainstInfoBOs = new ArrayList<SportAgainstInfoBO>();
		for(String systemCode : matchs){
			JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			if(!ObjectUtil.isBlank(jclqOrderBO)){
				sportAgainstInfoBO.setSystemCode(jclqOrderBO.getSystemCode());
				sportAgainstInfoBO.setSaleEndTime(jclqOrderBO.getSaleEndDate());
				sportAgainstInfoBO.setStartTime(jclqOrderBO.getStartTimeStamp());
				listSportAgainstInfoBOs.add(sportAgainstInfoBO);
			}
		}
		this.sortByEndTimeAsc(listSportAgainstInfoBOs);
		return ObjectUtil.isBlank(listSportAgainstInfoBOs)?null:listSportAgainstInfoBOs.get(0);
	}

	/**
	 * 根据彩种id和赛事id集合，查询竞彩足球赛事最早截止的时间
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private SportAgainstInfoBO getJczqFirstEndSportAgainstInfoBOs(Integer lotteryCode, List<String> matchs) {
		List<SportAgainstInfoBO> listSportAgainstInfoBOs = new ArrayList<SportAgainstInfoBO>();
		Map<String,JczqOrderBO> map = jcDataService.findJczqOrderBOBySystemCodes(matchs);
		for(String key : map.keySet()){
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			JczqOrderBO jczqOrderBO = map.get(key);
			if(!ObjectUtil.isBlank(jczqOrderBO)){
				sportAgainstInfoBO.setSystemCode(jczqOrderBO.getSystemCode());
				sportAgainstInfoBO.setSaleEndTime(jczqOrderBO.getSaleEndDate());
				sportAgainstInfoBO.setStartTime(jczqOrderBO.getStartTimeStamp());
				listSportAgainstInfoBOs.add(sportAgainstInfoBO);
			}
		}
		this.sortByEndTimeAsc(listSportAgainstInfoBOs);
		return ObjectUtil.isBlank(listSportAgainstInfoBOs)?null:listSportAgainstInfoBOs.get(0);
	}

	/**
	 * 按销售截止时间顺序排序，用于取最早截止的
	 * @param listSportAgainstInfoBOs
	 */
	private void sortByEndTimeAsc(List<SportAgainstInfoBO> listSportAgainstInfoBOs){
		StringBuffer stringBuffer = new StringBuffer();
		for(SportAgainstInfoBO bean : listSportAgainstInfoBOs){
			 stringBuffer.append("systemCode="+bean.getSystemCode()+",startTime="+DateUtil.convertDateToStr(bean.getStartTime(),DateUtil.DEFAULT_FORMAT)+",endTime="+DateUtil.convertDateToStr(bean.getSaleEndTime(),DateUtil.DEFAULT_FORMAT)+";");
		}
		logger.info("===========ListSportAgainstInfoBOs["+stringBuffer.toString()+"]=========");
		Collections.sort(listSportAgainstInfoBOs, new Comparator<SportAgainstInfoBO>() {
			@Override
			public int compare(SportAgainstInfoBO o1, SportAgainstInfoBO o2) {
				return o1.getSaleEndTime().compareTo(o2.getSaleEndTime());
			}
		});
	}

	@Override
	public ResultBO<?> updateOrderStatus(OrderSingleQueryVo orderSingleQueryVo) throws Exception{
		if(ObjectUtil.isBlank(orderSingleQueryVo.getLotteryCode())){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		ResultBO<?> result = userInfoCacheService.checkToken(orderSingleQueryVo.getToken());
		if(result.isError())             	
			return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		if(orderSingleQueryVo.getBuyType()!=Integer.valueOf(OrderEnum.BuyType.BUY_CHASE_PLAN.getValue())){//非追号计划
			orderInfoDaoMapper.updateSingleOrderStatus(orderSingleQueryVo.getPayStatus(), orderSingleQueryVo.getOrderCode(),userInfo.getId());
		}else{//追号计划
			List<String> addOrderCodeList = new ArrayList<String>();
			addOrderCodeList.add(orderSingleQueryVo.getOrderCode());
			orderAddDaoMapper.batchCancelAddOrderList(addOrderCodeList,userInfo.getId(),OrderEnum.PayStatus.USER_CANCLE_PAY.getValue());
		}
		//更新未支付订单缓存数
		result = orderInfoCacheService.updateOrderNoPayCount(Arrays.asList(orderSingleQueryVo.getOrderCode()), userInfo.getId(), Integer.valueOf(orderSingleQueryVo.getLotteryCode()), orderSingleQueryVo.getToken());
        if(result.isError()){
        	logger.error(result.getMessage());
        }
		return ResultBO.ok();
	}

	@Override
	public ResultBO<?> updateOrderStatus(OrderEnum.OrderStatus orderStatus, Integer orderId,String modifyBy) {
		orderInfoDaoMapper.updateOrderStatus(Arrays.asList(orderId),(short)orderStatus.getValue(),modifyBy);
		return ResultBO.ok();
	}

	@Override
	public ResultBO<?> batchCancelOrderList(List<String> orderCodes, String token, Integer lotteryCode) {
		if(ObjectUtil.isBlank(lotteryCode)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
		}
		ResultBO<?> result = userInfoCacheService.checkToken(token);
		if(result.isError())             	
	       	return result;
		UserInfoBO userInfo = (UserInfoBO) result.getData();
		List<OrderInfoLimitBO> orderBaseInfoBOs = orderInfoDaoMapper.queryNoPayOrderList(orderCodes, userInfo.getId(), null, null, null);
		if(orderBaseInfoBOs.size()!=orderCodes.size()){
			return ResultBO.err(MessageCodeConstants.ORDER_NOT_EXIST_OR_INVALILD);
		}
		List<String> orderCodeList = new ArrayList<String>();
		List<String> addOrderCodeList = new ArrayList<String>();
		for(OrderInfoLimitBO orderBaseInfoBO : orderBaseInfoBOs){
           if(orderBaseInfoBO.getBuyType() == Integer.valueOf(OrderEnum.BuyType.BUY_CHASE_PLAN.getValue())){//追号计划订单
			   addOrderCodeList.add(orderBaseInfoBO.getOrderCode());
		   }else{
			   orderCodeList.add(orderBaseInfoBO.getOrderCode());
		   }
		}
		if(!ObjectUtil.isBlank(orderCodeList)){
           orderInfoDaoMapper.batchCancelOrderList(orderCodeList,userInfo.getId(),OrderEnum.PayStatus.USER_CANCLE_PAY.getValue());
		}
		if(!ObjectUtil.isBlank(addOrderCodeList)){
			orderAddDaoMapper.batchCancelAddOrderList(addOrderCodeList,userInfo.getId(),OrderEnum.PayStatus.USER_CANCLE_PAY.getValue());
		}
		//更新未支付订单缓存数
		result = orderInfoCacheService.updateOrderNoPayCount(orderCodes, userInfo.getId(), lotteryCode, token);
		if(result.isError()){
			logger.error(result.getMessage());
		}
		return ResultBO.ok();
	}

	@Override
	public ResultBO<?> addSingleUploadOrder(OrderInfoSingleUploadVO orderInfoSingleUploadVO) throws Exception{
		//logger.debug("单式上传，下单入参："+orderInfoSingleUploadVO.toString());
		//1.单式上传特有业务处理，包括单式上传的校验和组装标准下单对象
		ResultBO<?> resultBO = singleUploadOrderExcetor.execute(orderInfoSingleUploadVO);
		if(resultBO.isError()){
			return resultBO;
		}
		OrderInfoVO orderInfo =(OrderInfoVO)resultBO.getData();
		//2.走标准下单流程
		resultBO = addOrder(orderInfo);
		return resultBO;
	}
	
	@Override
	public List<WinBO> findWinInfo(WinVO win) {
		return orderInfoDaoMapper.findWinInfo(win);
	}

	/**
	 * @desc   查询符合条件的订单数
	 * @author Tony Wang
	 * @create 2017年8月15日
	 * @param vo
	 * @return 
	 */
	@Override
	public int countOrderInfo(OrderInfoQueryVO vo) {
		return orderInfoDaoMapper.count(vo);
	}

	/**
	 * 获取订单信息
	 * @param orderCode
	 * @return
	 */
    @Override
	public OrderInfoBO getOrderInfo(String orderCode){
		return orderInfoDaoMapper.getOrderInfo(orderCode);
	}

	@Override
	public ResultBO<?> addRcmdOrder(RcmdInfoVO rcmdInfoVO) throws Exception{
		// 用户合法性验证
    	ResultBO<?> result = userInfoCacheService.checkToken(rcmdInfoVO.getToken());		
    	if(result.isError()){
    		return ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE);
    	}
    	
    	// 彩种和彩期不能为空
    	if(ObjectUtil.isBlank(rcmdInfoVO.getLotteryCode()) || ObjectUtil.isBlank(rcmdInfoVO.getLotteryIssue())){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}
    	
    	// 是否支持推荐彩种
    	if(rcmdInfoVO.getLotteryCode() < 300){
			return ResultBO.err(MessageCodeConstants.LOTTERY_CODE_IS_ILIEGAL_SERVICE);
		}
    	
    	// 今天推荐方案数已达限定值，请明天继续推荐
    	int orderCount = orderInfoDaoMapper.getRcmdDayOrderCount(rcmdInfoVO);
    	if(orderCount >= dayMaxCount){
    		return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_LIMIT_SERVICE);
    	}
    	
    	if(rcmdInfoVO.getPayType() == null){
    		return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
    	}
    	else if(rcmdInfoVO.getPayType().intValue() == 1){
    		if(rcmdInfoVO.getPayAmount() != null && rcmdInfoVO.getPayAmount().intValue() != 0)
    		{
    			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
    		}
    	}else if(rcmdInfoVO.getPayType().intValue() == 2){
    		if(rcmdInfoVO.getPayAmount() != null && rcmdInfoVO.getPayAmount().intValue() > 0)
    		{
    			return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
    		}
    	}else{
    		return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
    	}
    	
		RcmdConditionVO rcmdConditionVO = new RcmdConditionVO(rcmdInfoVO);
		// 设置推荐编号
		String rcmdCode = OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_RCMD);
		rcmdInfoVO.setRcmdCode(rcmdCode);
		Date saleEndTime = null;
    	// 检查方案内容格式合法性
    	for(RcmdDetailVO rcmdDetail:rcmdInfoVO.getRcmdDetailList()){
    		String content = rcmdDetail.getPlanContent();
    		String[] contents = FormatConversionJCUtil.singleBetContentAnalysis(content);
    		if(ObjectUtil.isBlank(contents) || contents.length < 3){
    			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
    		}
    		// 验证过关方式格式合法性
    		if(!JCConstants.checkFormatJCPassWay(contents[1])) {
				return ResultBO.err(MessageCodeConstants.PASS_FORMAT_ILLEGAL_SERVICE);
			}
    		
    		String[] betContents = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
    		String pattern = JCConstants.SPF_CONTENT_REGEX;
    		if(rcmdDetail.getLotteryChildCode().intValue() == JCZQConstants.ID_JCZQ){
    			//胜平负
    			pattern = JCConstants.SPF_CONTENT_REGEX;
    		}else if(rcmdDetail.getLotteryChildCode().intValue() == JCZQConstants.ID_RQS){
    			// 让球胜平负
    			pattern = JCConstants.RF_SPF_CONTENT_REGEX;
    		}else if(rcmdDetail.getLotteryChildCode().intValue() == JCZQConstants.ID_FHT){
    			// 混投
    		}else{
    			return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_ILIEGAL_SERVICE);
    		}
    		if(SportConstants.CHUAN_GUAN_2_1.equals(contents[1]) && rcmdInfoVO.getPassWay() == 2){
    			// 2串1 最多选择2场比赛
    			if(betContents.length != 2){
    				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
    			}
    			//最低返奖率须大于145%：返奖率=中奖总金额 / 发单总金额 * 100%
    			double mixPrize = 1.00 * 2;
    			if(contents[0].contains("_"+JCZQConstants.S) && contents[0].contains("_"+JCZQConstants.R)){
    				// 混投
    				ResultBO<?> rs = checkHTContent(betContents);
    				if(rs.isError()){return rs;}else{mixPrize = (Double)rs.getData();}
    			}else{
    				for(String betContent:betContents){
        				// 验证格式
        				if(!Pattern.matches(pattern, betContent)){
        					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
        				}
        				double minSp = getMinSp(betContent);
        				mixPrize = mixPrize * minSp;
        			}
    			}
    			// 计算单倍金额
    			int betCount = SportsZsUtil.getSportsManyNote(content, rcmdDetail.getLotteryChildCode());
    			if(betCount != rcmdDetail.getPlanNumber().intValue()){
    				logger.warn("【推荐方案提交接口提示】注数不正确");
    			}
    			double money  = betCount*2;
    			if(money != rcmdDetail.getPlanAmount().doubleValue()){
    				logger.warn("【推荐方案提交接口提示】金额不正确");
    			}
    			double rate = mixPrize/money;
    			if(rate < JCConstants.ORDER_MIN_PRIZE_MULTIPLE){
    				return ResultBO.err(MessageCodeConstants.ORDER_MIN_PRIZE_RATE);
    			}
    			
    		}
    		else if(SportConstants.DAN_GUAN.equals(contents[1]) && rcmdInfoVO.getPassWay() == 1){
				// 单关 最多选择1场比赛
    			if(betContents.length != 1){
    				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
    			}
    			// 验证格式
    			if(!Pattern.matches(pattern, betContents[0])){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
				}
    			
    			int start = betContents[0].indexOf(SymbolConstants.PARENTHESES_LEFT);
    			int end = betContents[0].indexOf(SymbolConstants.PARENTHESES_RIGHT);
    			String sps = betContents[0].substring(start, end);
    			String[] spa = sps.split(SymbolConstants.COMMA);
    			if(spa.length > 1){
    				for(String spStr: spa){
        				String[] spr = spStr.split(SymbolConstants.AT);
        				double sp = Double.parseDouble(spr[1]);
        				// 多选时2个推荐项的赔率必须都大于2.45
        				if(sp < JCConstants.BET_MORE_MIN_RATE){
        					return ResultBO.err(MessageCodeConstants.BET_MORE_MIN_RATE);
        				}
        			}
    			}else{
    				String[] spr = spa[0].split(SymbolConstants.AT);
    				double sp = Double.parseDouble(spr[1]);
    				// 请选择赔率大于1.45的选项
    				if(sp < JCConstants.BET_ONE_MIN_RATE){
    					return ResultBO.err(MessageCodeConstants.BET_MORE_MIN_RATE);
    				}
    			}
			}else{
				return ResultBO.err(MessageCodeConstants.PASS_FORMAT_ILLEGAL_SERVICE);	
			}
    		String[] sa = rcmdDetail.getScreens().split(",");
    		// 场次是否截止销售
        	List<String> matchs = Arrays.asList(sa);
    		//根据彩种id和赛事id集合，查询对阵赛事信息
    		List<SportAgainstInfoBO> listAgainstInfoBO = sportsOrderValidate.getSportGameFirstBeginMatch(rcmdInfoVO.getLotteryCode().intValue(), matchs);
    		SportAgainstInfoBO againstInfoBO = listAgainstInfoBO.get(0);
    		Date orderEndTime = againstInfoBO.getSaleEndTime();//最早一场销售截止时间(还可以下单的截止时间)
    		if(saleEndTime == null){
    			saleEndTime = orderEndTime;
    		}else {
    			if(DateUtil.compareAndGetSeconds(saleEndTime, orderEndTime) > 0){
    				saleEndTime = orderEndTime;
        		}
    		}
    		//相同串关场次不能重复推荐
    		rcmdConditionVO.setScreens(rcmdDetail.getScreens());
    		rcmdConditionVO.setLotteryChildCode(rcmdDetail.getLotteryChildCode());
    		int count = orderInfoDaoMapper.findRcmdRepeatOrder(rcmdConditionVO);
    		if(count > 0){
    			return ResultBO.err(MessageCodeConstants.ORDER_NOT_REPEAT);
    		}
    		rcmdDetail.setRcmdCode(rcmdCode);
    	}
    	if(new Date().after(saleEndTime)){
			return ResultBO.err(MessageCodeConstants.AGAINST_IS_ENDING_SALE_SERVICE);
		}
    	if(!StringUtil.isBlank(rcmdInfoVO.getTitle())){
    		rcmdInfoVO.setTitle(new String(Base64.encodeBase64(rcmdInfoVO.getTitle().getBytes("UTF-8"))) );	
    	}
		if(!StringUtil.isBlank(rcmdInfoVO.getReason())){
			rcmdInfoVO.setReason(new String(Base64.encodeBase64(rcmdInfoVO.getReason().getBytes("UTF-8"))) );
		}
    	rcmdInfoVO.setSaleEndTime(saleEndTime);
		logger.info("【分析师推荐】方案信息验证通过，入库前[推荐编号="+rcmdInfoVO.getRcmdCode()+",用户id:"+rcmdInfoVO.getUserId()+",推荐标题："+rcmdInfoVO.getTitle());
		orderInfoDaoMapper.addRcmdOrder(rcmdInfoVO);
		//2.推荐方案明细入库
		orderInfoDaoMapper.addRcmdDetailList(rcmdInfoVO.getRcmdDetailList());
		return ResultBO.ok();
	}
	
	private double getMinSp(String betContent){
		int start = betContent.indexOf(SymbolConstants.PARENTHESES_LEFT);
		int end = betContent.indexOf(SymbolConstants.PARENTHESES_RIGHT);
		String sps = betContent.substring(start, end);
		String[] spa = sps.split(SymbolConstants.COMMA);
		double minSp = 0.00;
		for(String spStr: spa){
			String[] spr = spStr.split(SymbolConstants.AT);
			double sp = Double.parseDouble(spr[1]);
			if(sp < minSp || minSp == 0.00){
				minSp = sp;
			}
		}
		return minSp;
	}
	
	/**
	 * 
	 * @Description 混投内容验证 
	 * @author HouXiangBao289
	 * @param betContents
	 * @param mixPrize
	 * @return
	 */
	private ResultBO<?> checkHTContent(String[] betContents){
		double mixPrize = 1.00 * 2;
		// 混投
		for(String betContent:betContents){
			if(StringUtil.appearNumber(betContent,"_"+JCZQConstants.S) > 1 
					|| StringUtil.appearNumber(betContent,"_"+JCZQConstants.R) > 1){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
			}
			
			String[] htContents = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.UNDERLINE, true);
			double minSp = 0.00;
			for(int i=1;i<htContents.length;i++){
				String htc = htContents[i];
				if(htc.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
					// 混投让球胜平负格式
    				if(!Pattern.matches(JCConstants.FHT_RF_CONTENT_REGEX, htc)){
    					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
    				}
				}else{
					// 混投胜平负格式
    				if(!Pattern.matches(JCConstants.FHT_CONTENT_REGEX, htc)){
    					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ERR);
    				}
				}
				int start = htc.indexOf(SymbolConstants.PARENTHESES_LEFT);
				int end = htc.indexOf(SymbolConstants.PARENTHESES_RIGHT);
				String sps = htc.substring(start, end);
				String[] spa = sps.split(SymbolConstants.COMMA);
				for(String spStr: spa){
					String[] spr = spStr.split(SymbolConstants.AT);
					double sp = Double.parseDouble(spr[1]);
					if(sp < minSp || minSp == 0.00){
						minSp = sp;
					}
				}
			}
			mixPrize = mixPrize * minSp;
		}
		return ResultBO.ok(mixPrize);
	}
}
