package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcOmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcRecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcRecentTrendOutBO;

/**
 * @desc 七乐彩遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
public interface IQlcTrendService extends INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/

	/*****************************
	 * 七乐彩首页-遗漏、冷热、概率数据接口
	 *********************************/
	/**
	 * @desc 前端接口：所有遗漏(七乐彩只有1种遗漏)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：所有遗漏(七乐彩只有1种遗漏)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception
	 */
	ResultBO<QlcOmitOutBO> findOmitChanceColdHotAll(LotteryVO param) throws Exception;

	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势
	 */
	ResultBO<List<QlcRecentTrendOutBO>> findRecentTrend(LotteryVO param);
	
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 */
	ResultBO<List<QlcRecentTrendBO>> findRecentTrendSimple(LotteryVO param);

	/***************************** 七乐彩走势图数据接口 *********************************/

}
