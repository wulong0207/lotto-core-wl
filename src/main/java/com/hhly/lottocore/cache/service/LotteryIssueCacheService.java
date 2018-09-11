package com.hhly.lottocore.cache.service;

import com.hhly.skeleton.base.bo.ResultBO;

/**
 * 彩期信息缓存数据接口服务
 * @author lidecheng
 * @date 2017年6月12日
 * @compay 益彩网络科技有限公司
 */
public interface LotteryIssueCacheService {
	
	
	/**
	 * 查询某个彩种当前期和最新开奖期信息缓存
	 * @param lotteryCode  彩种id
	 * @return
	 */
	ResultBO<?> getCurrentAndPreIssue(Integer lotteryCode);
	/**
	 * 查询所有缓存的彩种当前期和最新开奖期信息
	 * @param lotteryCode  彩种id
	 * @return
	 */
	ResultBO<?> getAllCurrentAndPreIssue();
}
