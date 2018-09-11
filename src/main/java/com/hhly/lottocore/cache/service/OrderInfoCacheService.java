package com.hhly.lottocore.cache.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 操作订单相关缓存数据接口服务
 * @author longguoyou
 * @date 2017年5月16日
 * @compay 益彩网络科技有限公司
 */
public interface OrderInfoCacheService {
       /**
        * 获取未支付订单缓存数
        * @author longguoyou
        * @date 2017年5月16日
        * @param orderInfo
        * @param userInfoBO 用户信息对象 
        * @return
        */
	   ResultBO<?> verifyOrderNoPayCount(OrderInfoVO orderInfo, UserInfoBO userInfoBO);

	/**
	 *  获取合买未支付订单缓存数
	 * @param orderInfo
	 * @param userInfoBO
	 * @return
	 */
	   ResultBO<?> verifyOrderGroupNoPayCount(OrderInfoVO orderInfo, UserInfoBO userInfoBO);
	   
	   /**
	    *  更新未支付订单缓存数
	    * @author longguoyou
	    * @date 2017年5月16日
	    * @param lotteryCode
	    * @param count 正数： 加一；负数：减一
	    * @param userId 用户ID
	    */
	   ResultBO<?> updateOrderNoPayCount(Integer lotteryCode, int count, Integer userId);

		/**
		 * 更新合买未支付订单缓存数
		 * @param lotteryCode
		 * @param count
		 * @param userId
		 * @return
		 */
		ResultBO<?> updateOrderGroupNoPayCount(Integer lotteryCode, int count, Integer userId);
	   /**
	    * 对接批量删除订单后更新未支付订单缓存数
	    * @author longguoyou
	    * @date 2017年5月24日
	    * @param orderCodes 取消订单的订单编号集合
	    * @param userId
	    * @param lotteryCode 彩种
	    * @param token 
	    * @return
	    */
	   ResultBO<?> updateOrderNoPayCount(List<String> orderCodes, Integer userId, Integer lotteryCode, String token);
	   
	   /**
	    * 通过token 得到用户投注内容信息缓存
	    * @param key
	    * @return
	    */
	   String getOrderByToken(String token);
	   
	   
	   /**
	    * 通过token 更新用户投注内容信息缓存
	    * @param token
	    */
	   void updataOrderByToken(String token,String betString);
	   
}
