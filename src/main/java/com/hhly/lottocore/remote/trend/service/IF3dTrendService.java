package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.f3d.F3dDrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.f3d.F3dOmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.f3d.F3dRecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.f3d.F3dRecentTrendOutBO;

/**
 * @desc 福彩3d遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
public interface IF3dTrendService extends INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/
	
	/*****************************福彩3D首页-遗漏、冷热、概率数据接口 *********************************/
	/**
	 * @desc 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param param 参数对象(issueCode)
	 * @return 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 */
	ResultBO<F3dDrawOtherBO> findLatestDrawOther(LotteryVO param);
	
	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @return 前端接口：百、十、个位 分位遗漏：查询最大的遗漏期数数据
	 */
	ResultBO<TrendBaseBO> findMaxCode3Trend();

	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：百、十、个位 分位遗漏：查询单条遗漏走势
	 */
	ResultBO<TrendBaseBO> findSingleCode3Trend(LotteryVO param);

	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode,qryCount)
	 * @return 前端接口：百、十、个位 分位遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 */
	ResultBO<List<TrendBaseBO>> findRangeCode3Trend(LotteryVO param);
	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：百、十、个位 分位遗漏：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception 
	 */
	ResultBO<TrendBaseBO> findOmitChanceColdHotCode3(LotteryVO param) throws Exception;

	/**
	 * @desc 前端接口：和值遗漏：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @return 前端接口：和值遗漏：查询最大的遗漏期数数据
	 */
	ResultBO<TrendBaseBO> findMaxSumTrend();

	/**
	 * @desc 前端接口：和值遗漏：查询单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：和值遗漏：查询单条遗漏走势
	 */
	ResultBO<TrendBaseBO> findSingleSumTrend(LotteryVO param);

	/**
	 * @desc 前端接口：和值遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode,qryCount)
	 * @return 前端接口：和值遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 */
	ResultBO<List<TrendBaseBO>> findRangeSumTrend(LotteryVO param);
	/**
	 * @desc 前端接口：和值遗漏：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：和值遗漏：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception 
	 */
	ResultBO<TrendBaseBO> findOmitChanceColdHotSum(LotteryVO param) throws Exception;
	
	/**
	 * @desc 前端接口：所有遗漏(1：直选-普通遗漏；2：组三-包号遗漏|组六-普通遗漏|组六-胆拖遗漏；3：直选-和值遗漏；4：组三-和值遗漏；5
	 *       ：组六-和值遗漏 )：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：所有遗漏(1：直选-普通遗漏；2：组三-包号遗漏|组六-普通遗漏|组六-胆拖遗漏；3：直选-和值遗漏；4：组三-和值遗漏；5
	 *       ：组六-和值遗漏 )：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception
	 */
	ResultBO<List<F3dOmitOutBO>> findOmitChanceColdHotAll(LotteryVO param) throws Exception;
	
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	ResultBO<List<F3dRecentTrendOutBO>> findRecentTrend(LotteryVO param);

	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 */
	ResultBO<List<F3dRecentTrendBO>> findRecentTrendSimple(LotteryVO param);
	
	/*****************************福彩3D走势图数据接口 *********************************/
	
}
