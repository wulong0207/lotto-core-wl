package com.hhly.lottocore.persistence.trend.num.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * @desc 彩种公用的遗漏/走势数据接口
 * @author huangb
 * @date 2017年3月8日
 * @company 益彩网络
 * @version v1.0
 */
public interface TrendDaoMapper {

	/**************************** Used to CMS ********************************/

	/**************************** Used to LOTTO ******************************/
	
	/****************各彩种首页-遗漏、冷热、概率数据专用********************/
	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询最大的遗漏期数数据
	 */
	TrendBaseBO findMaxTrendFront();

	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询单条遗漏走势
	 */
	TrendBaseBO findSingleFront(LotteryVO param);

	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询范围内的遗漏走势集合(即从指定期开始的近多少期)
	 */
	List<TrendBaseBO> findTrendRangeFront(LotteryVO param);
	
	
	
	/****************后续彩种走势图专用********************/
	/**
	 * 奇偶走势
	 * @desc 
	 * @create 2017年11月15日
	 * @param param
	 * @return
	 * @throws Exception ResultBO<TrendBaseBO>
	 */
	List<TrendBaseBO> findOETrend(LotteryVO param);
	
	/**
	 * 大小走势
	 * @desc 
	 * @create 2017年11月15日
	 * @param param
	 * @return
	 * @throws Exception ResultBO<TrendBaseBO>
	 */
	List<TrendBaseBO> findBSTrend(LotteryVO param);
	
	List<TrendBaseBO> findBaseTrend(LotteryVO param);
	
}