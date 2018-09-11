package com.hhly.lottocore.cache.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.OrderInfoCacheService;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.user.bo.UserInfoBO;
/**
 * 操作订单相关缓存数据接口服务
 * @author longguoyou
 * @date 2017年5月16日
 * @compay 益彩网络科技有限公司
 */
@Service("orderInfoCacheService")
public class OrderInfoCacheServiceImpl implements OrderInfoCacheService {
	
	private static Logger logger = LoggerFactory.getLogger(OrderInfoCacheServiceImpl.class);
	
	
	/**
	 * 订单数据接口
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	
	@Resource(name="userInfoCacheService")
	private UserInfoCacheService userInfoCacheService;
	
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 代购、追号订单数量最大限制
	 */
	@Value("${limit_order_num}")
	private String limitCount;

	/**
	 * 合买订单数量最大限制
	 */
	@Value("${limit_order_group_num}")
	private String limitOrderGroupCount;

	/**
	 * 获取代购、追号未支付订单缓存数
	 * @param orderInfo
	 * @param userInfoBO 用户信息对象
	 * @return
	 */
	@Override
	public ResultBO<?> verifyOrderNoPayCount(OrderInfoVO orderInfo, UserInfoBO userInfoBO) {
		if(!ObjectUtil.isBlank(orderInfo.getActivityCode()) || orderInfo.getIsSingleOrder() == Constants.NUM_1){//活动订单/单式上传不参加未支付的校验，永远返回0即可
			return ResultBO.ok(0);
		}
		if(ObjectUtil.isBlank(orderInfo.getLotteryCode()) || ObjectUtil.isBlank(userInfoBO)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}
		String countCache = null;
		try{
			//key: CacheConstants.ORDER_NO_PAY_COUNT + userId + 彩种ID
			String lotteryCode = String.valueOf(orderInfo.getLotteryCode()).substring(0, 3);
			String combineKey = CacheConstants.getNoPayOrderCacheKey(userInfoBO.getId(), Integer.valueOf(lotteryCode));
			countCache = redisUtil.getString(combineKey);
			if(ObjectUtil.isBlank(countCache) || Integer.valueOf(countCache) <= 0 || Integer.valueOf(countCache) > Integer.valueOf(limitCount)){//缓存为空同步数据库,写缓存
				//查库
				Integer dbCount = orderInfoDaoMapper.queryNoPayOrderListCount(null, userInfoBO.getId(), lotteryCode, null);
				//判断值范围
				getCount(dbCount, Integer.valueOf(limitCount), userInfoBO.getId());
				//只将数据库订单数写缓存
				redisUtil.incr(combineKey, dbCount - (ObjectUtil.isBlank(countCache)?0:Integer.valueOf(countCache)));
				//返回次数
				//return ResultBO.ok(dbCount);//数据库值
				//未支付订单超限
				if(dbCount >= Integer.valueOf(limitCount)){
					return ResultBO.err(MessageCodeConstants.ORDER_NOT_PAY_COUNT_BEYONG);
				}else{
					return ResultBO.ok(dbCount);
				}
			}
		}catch(Exception e){
			logger.error("获取代购、追号未支付订单缓存数", e);
		}
		//缓存有值,直接返回数据
		//return ResultBO.ok(Integer.valueOf(countCache));
		if(Integer.valueOf(countCache) >= Integer.valueOf(limitCount)){
			return ResultBO.err(MessageCodeConstants.ORDER_NOT_PAY_COUNT_BEYONG);
		}else{
			return ResultBO.ok(Integer.valueOf(countCache));
		}
	}

	/**
	 * 获取合买未支付订单缓存数
	 * @param orderInfo
	 * @param userInfoBO
	 * @return
	 */
	@Override
	public ResultBO<?> verifyOrderGroupNoPayCount(OrderInfoVO orderInfo, UserInfoBO userInfoBO) {
		if(!ObjectUtil.isBlank(orderInfo.getActivityCode()) || orderInfo.getIsSingleOrder() == Constants.NUM_1){//活动订单/单式上传不参加未支付的校验，永远返回0即可
			return ResultBO.ok(0);
		}
		if(ObjectUtil.isBlank(orderInfo.getLotteryCode()) || ObjectUtil.isBlank(userInfoBO)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}
		String countCache = null;
		try{
			//key: CacheConstants.ORDER_NO_PAY_COUNT + userId + 彩种ID
			String lotteryCode = String.valueOf(orderInfo.getLotteryCode()).substring(0, 3);
			String combineKey = CacheConstants.getNoPayOrderGroupCacheKey(userInfoBO.getId(), Integer.valueOf(lotteryCode));
			countCache = redisUtil.getString(combineKey);
			if(ObjectUtil.isBlank(countCache) || Integer.valueOf(countCache) <= 0 || Integer.valueOf(countCache) > Integer.valueOf(limitOrderGroupCount)){//缓存为空同步数据库,写缓存
				//查库
				Integer dbCount = orderInfoDaoMapper.queryNoPayOrderGroupListCount(userInfoBO.getId(), lotteryCode);
				//判断值范围
				getCount(dbCount, Integer.valueOf(limitOrderGroupCount), userInfoBO.getId());
				//只将数据库订单数写缓存
				redisUtil.incr(combineKey, dbCount - (ObjectUtil.isBlank(countCache)?0:Integer.valueOf(countCache)));
				//返回次数
				//return ResultBO.ok(dbCount);//数据库值
				//未支付订单超限
				if(dbCount >= Integer.valueOf(limitOrderGroupCount)){
					return ResultBO.err(MessageCodeConstants.ORDER_NOT_PAY_COUNT_BEYONG);
				}else{
					return ResultBO.ok(dbCount);
				}
			}
		}catch(Exception e){
			logger.error("获取合买未支付订单缓存数", e);
		}
		//缓存有值,直接返回数据
		//return ResultBO.ok(Integer.valueOf(countCache));
		if(Integer.valueOf(countCache) >= Integer.valueOf(limitOrderGroupCount)){
			return ResultBO.err(MessageCodeConstants.ORDER_NOT_PAY_COUNT_BEYONG);
		}else{
			return ResultBO.ok(Integer.valueOf(countCache));
		}
	}
	
	
	/**
	 * 得到需要写回缓存的值
	 * @param dbCount 数据库同步值
	 * @param max 最大值
	 * @param userId 用户ID
	 * @return
	 */
	private Integer getCount(Integer dbCount, Integer max, Integer userId){
		//判断值范围
		if(dbCount > max){
			//写日志
			logger.error("未支付订单数量数据异常:" + dbCount +" -- userId:"+userId);
			return max;
		}
		return dbCount < 0 ? 0 : dbCount;
	}
	
	/**
	 * 保证缓存未支付订单数值 [0,limit]
	 */
	@Override
	public ResultBO<?>  updateOrderNoPayCount(Integer lotCode, int count, Integer userId) {
		
		if(ObjectUtil.isBlank(lotCode) || ObjectUtil.isBlank(userId)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}
		
		//验证count 最大值 目前16  这个次数在外面验证 长限了 直接返错误
		if((count + Integer.valueOf(limitCount) < 0) || (count - Integer.valueOf(limitCount) > 0)){
			return ResultBO.err(MessageCodeConstants.PARAM_INVALID);
		}
		String lotteryCode = String.valueOf(lotCode).substring(0, 3);
		logger.debug("彩种：" + lotteryCode + ", 用户ID：" + userId + ", 次数：" + count);
		try{
			//key: CacheConstants.ORDER_NO_PAY_COUNT + userId + 彩种ID 
			String combineKey = CacheConstants.getNoPayOrderCacheKey(userId, Integer.valueOf(lotteryCode));
			String countCache = redisUtil.getString(combineKey);
			Integer dbCount = 0;
			if(ObjectUtil.isBlank(countCache) || Integer.valueOf(countCache) < 0 || Integer.valueOf(countCache) > Integer.valueOf(limitCount)){//缓存为空同步数据库,写缓存
				//查库
				dbCount = orderInfoDaoMapper.queryNoPayOrderListCount(null, userId, lotteryCode, null);
				//判断值范围
				getCount(dbCount, Integer.valueOf(limitCount), userId.intValue());
				//将数据库订单数与加减订单数和写缓存
				redisUtil.incr(combineKey, dbCount + count - (ObjectUtil.isBlank(countCache)?0:Integer.valueOf(countCache)));
			}else {
				//获得写缓存后的值
				Long increAfterNum = redisUtil.incr(combineKey, count);
				//出现
				if(increAfterNum > Integer.valueOf(limitCount) || increAfterNum < 0){
					//数据异常同步数据库
					dbCount = orderInfoDaoMapper.queryNoPayOrderListCount(null, userId, lotteryCode, null);
					//判断值范围
					getCount(dbCount, Integer.valueOf(limitCount), userId);
					//
					redisUtil.incr(combineKey, dbCount - increAfterNum.intValue());
				}
			}
		}catch(Exception e){
			logger.error("保证缓存未支付订单数值", e);
		}
		
	    return ResultBO.ok();
	}

	/**
	 * 保证缓存合买未支付订单数值 [0,limit]
	 */
	@Override
	public ResultBO<?>  updateOrderGroupNoPayCount(Integer lotCode, int count, Integer userId) {

		if(ObjectUtil.isBlank(lotCode) || ObjectUtil.isBlank(userId)){
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		}

		//验证count 最大值 目前16  这个次数在外面验证 长限了 直接返错误
		if((count + Integer.valueOf(limitCount) < 0) || (count - Integer.valueOf(limitCount) > 0)){
			return ResultBO.err(MessageCodeConstants.PARAM_INVALID);
		}
		String lotteryCode = String.valueOf(lotCode).substring(0, 3);
		logger.debug("彩种：" + lotteryCode + ", 用户ID：" + userId + ", 次数：" + count);
		try{
			//key: CacheConstants.ORDER_NO_PAY_COUNT + userId + 彩种ID
			String combineKey = CacheConstants.getNoPayOrderGroupCacheKey(userId, Integer.valueOf(lotteryCode));
			String countCache = redisUtil.getString(combineKey);
			Integer dbCount = 0;
			if(ObjectUtil.isBlank(countCache) || Integer.valueOf(countCache) < 0 || Integer.valueOf(countCache) > Integer.valueOf(limitCount)){//缓存为空同步数据库,写缓存
				//查库
				dbCount = orderInfoDaoMapper.queryNoPayOrderGroupListCount( userId, lotteryCode);
				//判断值范围
				getCount(dbCount, Integer.valueOf(limitCount), userId.intValue());
				//将数据库订单数与加减订单数和写缓存
				redisUtil.incr(combineKey, dbCount + count - (ObjectUtil.isBlank(countCache)?0:Integer.valueOf(countCache)));
			}else {
				//获得写缓存后的值
				Long increAfterNum = redisUtil.incr(combineKey, count);
				//出现
				if(increAfterNum > Integer.valueOf(limitCount) || increAfterNum < 0){
					//数据异常同步数据库
					dbCount = orderInfoDaoMapper.queryNoPayOrderGroupListCount( userId, lotteryCode);
					//判断值范围
					getCount(dbCount, Integer.valueOf(limitCount), userId);
					//
					redisUtil.incr(combineKey, dbCount - increAfterNum.intValue());
				}
			}
		}catch(Exception e){
			logger.error("保证缓存合买未支付订单数值", e);
		}

		return ResultBO.ok();
	}

	@Override
	public ResultBO<?> updateOrderNoPayCount(List<String> orderCodes, Integer userId , Integer lotteryCode, String token) {
		if(!ObjectUtil.isBlank(orderCodes)){
			//强制同步数据库
			return this.updateOrderNoPayCount(lotteryCode, -orderCodes.size(), userId);
		}
		return ResultBO.err();
	}

	@Override
	public String getOrderByToken(String token) {
		return redisUtil.getString(CacheConstants.getBetContentCacheKey(token));
	}

	@Override
	public void updataOrderByToken(String token,String betString) {
		redisUtil.addString(CacheConstants.getBetContentCacheKey(token), betString, CacheConstants.ONE_SECONDS);
	}
}
