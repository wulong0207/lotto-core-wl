package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * 
 * @desc 快3走势
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
public interface IK3TrendService extends IHighTrendService {
	
	
	/******************** 快3的走势服务   ********************/
	
	/**
	 * 基本走势
	 */
	@Override
	ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param);
}
