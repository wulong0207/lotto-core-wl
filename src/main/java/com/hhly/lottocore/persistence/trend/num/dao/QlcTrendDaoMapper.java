package com.hhly.lottocore.persistence.trend.num.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcRecentTrendBO;

/**
 * @desc 七乐彩各种遗漏走势的数据接口
 * @author huangb
 * @date 2017年6月28日
 * @company 益彩网络
 * @version v1.0
 */
public interface QlcTrendDaoMapper extends TrendDaoMapper {

	/**************************** Used to CMS ********************************/

	/**************************** Used to LOTTO ******************************/
	/****************七乐彩首页-遗漏、冷热、概率数据专用********************/
	/**
	 * @desc 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 * @author huangb
	 * @date 2017年6月30日
	 * @param param
	 *            参数对象(qryCount)
	 * @return 前端接口：近期开奖：查询近期遗漏走势(简易版-手机端使用)
	 */
	List<QlcRecentTrendBO> findRecentTrendSimple(LotteryVO param);
	
	
	/****************七乐彩走势图专用********************/
	
}