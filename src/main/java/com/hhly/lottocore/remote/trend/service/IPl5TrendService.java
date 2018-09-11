package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5OmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5RecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5RecentTrendOutBO;

/**
 * @desc 排列五遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
public interface IPl5TrendService extends INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/

	/*****************************
	 * 排列五首页-遗漏、冷热、概率数据接口
	 *********************************/
	/**
	 * @desc 前端接口：查询最新开奖的开奖其它信息(eg:和值、奇偶比、大小比等)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param param 参数对象(issueCode)
	 * @return 前端接口：查询最新开奖的开奖其它信息(eg:和值、奇偶比、大小比等)
	 */
	ResultBO<Pl5DrawOtherBO> findLatestDrawOther(LotteryVO param);
	
	/**
	 * @desc 前端接口：所有遗漏(排列五只有1种遗漏)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：所有遗漏(排列五只有1种遗漏)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception
	 */
	ResultBO<Pl5OmitOutBO> findOmitChanceColdHotAll(LotteryVO param) throws Exception;

	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	ResultBO<List<Pl5RecentTrendBO>> findRecentTrend(LotteryVO param);
	
	/**
	 * @desc 前端接口：走势投注：查询走势投注信息(返回的数据结构在Pl5RecentTrendBO里面扩展，该接口比最近开奖详情列表的返回字段要更多)
	 * @author huangb
	 * @date 2017年10月24日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询走势投注信息
	 */
	ResultBO<List<Pl5RecentTrendOutBO>> findTrendBettingInfo(LotteryVO param);

	/***************************** 排列五走势图数据接口 *********************************/

}
