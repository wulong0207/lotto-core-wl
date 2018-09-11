package com.hhly.lottocore.cache.service;

import com.hhly.skeleton.base.bo.ResultBO;

/**
 * 操作用户相关缓存数据接口服务
 * @author longguoyou
 * @date 2017年5月16日
 * @compay 益彩网络科技有限公司
 */
public interface UserInfoCacheService {
	
	
	
	/**
	 * 检查Token
	 * @param token
	 * @return 成功返回对应token的缓存数据(userinfo)
	 * @throws Exception
	 */
	ResultBO<?> checkToken(String token);

	/**
	 * 检查token是否失效
	 * @param token
	 * @return 成功返回对应token的缓存数据(userinfo)
	 * @throws Exception
	 */
	ResultBO<?> checkNoUseToken(String token);
	
}
