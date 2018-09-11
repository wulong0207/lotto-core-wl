package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * 
 * @desc 大乐透走势
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
public interface IDltTrendService extends INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/
	@Override
	ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param) throws Exception;
}
