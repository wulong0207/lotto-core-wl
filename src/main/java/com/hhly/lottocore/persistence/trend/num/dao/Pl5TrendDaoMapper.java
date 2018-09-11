package com.hhly.lottocore.persistence.trend.num.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5RecentTrendBO;

/**
 * @desc 排列五各种遗漏走势的数据接口
 * @author huangb
 * @date 2017年6月28日
 * @company 益彩网络
 * @version v1.0
 */
public interface Pl5TrendDaoMapper extends TrendDaoMapper {

	/**************************** Used to CMS ********************************/

	/**************************** Used to LOTTO ******************************/
	/****************排列五首页-遗漏、冷热、概率数据专用********************/
	/**
	 * @desc 前端接口：查询最新开奖的开奖其它信息(eg:和值、奇偶比、大小比等)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param param
	 *            参数对象(issueCode)
	 * @return 前端接口：查询最新开奖的开奖其它信息(eg:和值、奇偶比、大小比等)
	 */
	Pl5DrawOtherBO findLatestDrawOtherFront(LotteryVO param);
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	List<Pl5RecentTrendBO> findRecentTrend(LotteryVO param);

	/**
	 * @desc 前端接口：走势投注：查询走势投注信息(返回的数据结构在Pl5RecentTrendBO里面扩展，该接口比最近开奖详情列表的返回字段要更多)
	 * @author huangb
	 * @date 2017年10月24日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询走势投注信息
	 */
	List<Pl5RecentTrendBO> findTrendBettingInfo(LotteryVO param);

	
	/****************排列五走势图专用********************/
	
}