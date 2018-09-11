package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3OmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3RecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3RecentTrendOutBO;

/**
 * @desc 排列三遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
public interface IPl3TrendService extends INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/
	
	/*****************************排列三首页-遗漏、冷热、概率数据接口 *********************************/
	/**
	 * @desc 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param param 参数对象(issueCode)
	 * @return 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 */
	ResultBO<Pl3DrawOtherBO> findLatestDrawOther(LotteryVO param);
	
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
	 * @return 前端接口：所有遗漏(1：直选-普通遗漏；2：组三-包号遗漏|组六-普通遗漏|组六-胆拖遗漏；3：直选-和值遗漏；4：组三-和值遗漏
	 *         ；5：组六-和值遗漏 )：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception
	 */
	ResultBO<List<Pl3OmitOutBO>> findOmitChanceColdHotAll(LotteryVO param) throws Exception;
	
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	ResultBO<List<Pl3RecentTrendOutBO>> findRecentTrend(LotteryVO param);

	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 */
	ResultBO<List<Pl3RecentTrendBO>> findRecentTrendSimple(LotteryVO param);
	
	/*****************************排列三走势图数据接口 *********************************/
	
}
