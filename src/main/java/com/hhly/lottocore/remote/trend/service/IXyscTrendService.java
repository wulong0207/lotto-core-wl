package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * 
 * @desc 幸运赛车走势
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
public interface IXyscTrendService extends IHighTrendService {
	
	/******************** 幸运赛车的走势服务   ********************/
	
	/**
	 * 基本走势
	 */
	@Override
	ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param);
	
}
