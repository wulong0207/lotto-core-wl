package com.hhly.lottocore.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.user.bo.UserInfoBO;
/**
 * 操作用户相关缓存数据接口服务
 * @author longguoyou
 * @date 2017年5月16日
 * @compay 益彩网络科技有限公司
 */
@Service("userInfoCacheService")
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

	@Autowired
	private UserUtil userUtil;
	
	@Override
	public ResultBO<?> checkToken(String token) {
		if (ObjectUtil.isBlank(token)) 
			return ResultBO.err(MessageCodeConstants.TOKEN_IS_NULL_FIELD);
		UserInfoBO userInfoBO = userUtil.getUserByToken(token);
		if(ObjectUtil.isBlank(userInfoBO) || ObjectUtil.isBlank(userInfoBO.getId()))
			return ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE);
		return ResultBO.ok(userInfoBO);
		
	}

	@Override
	public ResultBO<?> checkNoUseToken(String token) {
		UserInfoBO userInfoBO = userUtil.getUserByToken(token);
		if(ObjectUtil.isBlank(userInfoBO) || ObjectUtil.isBlank(userInfoBO.getId()))
			return ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE);
		return ResultBO.ok(userInfoBO);

	}
}
