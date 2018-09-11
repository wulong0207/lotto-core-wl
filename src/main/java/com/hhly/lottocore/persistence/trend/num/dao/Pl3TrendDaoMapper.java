package com.hhly.lottocore.persistence.trend.num.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3RecentTrendBO;

/**
 * @desc 排列三各种遗漏走势的数据接口
 * @author huangb
 * @date 2017年6月28日
 * @company 益彩网络
 * @version v1.0
 */
public interface Pl3TrendDaoMapper extends TrendDaoMapper {

	/**************************** Used to CMS ********************************/

	/**************************** Used to LOTTO ******************************/
	/****************排列三首页-遗漏、冷热、概率数据专用********************/
	/**
	 * @desc 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：查询最新开奖的开奖其它信息(eg:和值、跨度、奇偶比、大小比等)
	 */
	Pl3DrawOtherBO findLatestDrawOtherFront(LotteryVO param);

	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @return 前端接口：百、十、个位 分位遗漏：查询最大的遗漏期数数据
	 */
	TrendBaseBO findMaxCode3TrendFront();

	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：百、十、个位 分位遗漏：查询单条遗漏走势
	 */
	TrendBaseBO findSingleCode3TrendFront(LotteryVO param);

	/**
	 * @desc 前端接口：百、十、个位 分位遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode,qryCount)
	 * @return 前端接口：百、十、个位 分位遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 */
	List<TrendBaseBO> findRangeCode3TrendFront(LotteryVO param);

	/**
	 * @desc 前端接口：和值遗漏：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @return 前端接口：和值遗漏：查询最大的遗漏期数数据
	 */
	TrendBaseBO findMaxSumTrendFront();

	/**
	 * @desc 前端接口：和值遗漏：查询单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：和值遗漏：查询单条遗漏走势
	 */
	TrendBaseBO findSingleSumTrendFront(LotteryVO param);

	/**
	 * @desc 前端接口：和值遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(issueCode,qryCount)
	 * @return 前端接口：和值遗漏：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 */
	List<TrendBaseBO> findRangeSumTrendFront(LotteryVO param);
	
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	List<Pl3RecentTrendBO> findRecentTrend(LotteryVO param);

	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 */
	List<Pl3RecentTrendBO> findRecentTrendSimple(LotteryVO param);
	
	
	/****************排列三走势图专用********************/
	
}