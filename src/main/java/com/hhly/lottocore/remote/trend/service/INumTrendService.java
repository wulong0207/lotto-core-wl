/**
 * 
 */
package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * @desc 低频彩遗漏走势的服务接口
 * @author huangb
 * @date 2017年3月8日
 * @company 益彩网络
 * @version v1.0
 */
public interface INumTrendService {
	/*************
	 * 公共的远程服务接口：提供findSingle和findMultiple处理大部分的查询需求，如有其它差异性的查询在上面单独提供
	 ************/
	
	/*****************************各彩种首页-遗漏、冷热、概率数据接口 *********************************/
	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询指定彩种指定彩期的单条遗漏走势
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(lotteryCode,issueCode)
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询单条遗漏走势
	 */
	ResultBO<TrendBaseBO> findSingle(LotteryVO param);

	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询指定彩种指定彩期范围内的遗漏走势集合(即从指定彩种指定彩期开始的近多少期)
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(lotteryCode,issueCode,qryCount)
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询指定彩种指定彩期范围内的遗漏走势集合(即从指定彩种指定彩期开始的近多少期)
	 */
	ResultBO<List<TrendBaseBO>> findTrendRange(LotteryVO param);

	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询最大的遗漏期数数据
	 * @author huangb
	 * @date 2017年3月8日
	 * @param param
	 *            参数对象(lotteryCode)
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询最大的遗漏期数数据
	 */
	ResultBO<TrendBaseBO> findMaxTrend(LotteryVO param);

	/**
	 * @desc 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @author huangb
	 * @date 2017年3月23日
	 * @param param
	 *            参数对象
	 * @return 前端接口：基础信息遗漏(对应彩种base结尾基础表)：查询遗漏/概率/冷热(按查询标识分别查询)
	 * @throws Exception 
	 */
	ResultBO<TrendBaseBO> findOmitChanceColdHot(LotteryVO param) throws Exception;
	
	
	
	/*****************************后续各彩种走势图数据接口 *********************************/	
	
	/**
	 * 奇偶走势
	 * @desc 
	 * @create 2017年11月15日
	 * @param param
	 * @return
	 * @throws Exception ResultBO<TrendBaseBO>
	 */
	ResultBO<List<TrendBaseBO>> findOETrend(LotteryTrendVO param) throws Exception;
	
	/**
	 * 大小走势
	 * @desc 
	 * @create 2017年11月15日
	 * @param param
	 * @return
	 * @throws Exception ResultBO<TrendBaseBO>
	 */
	ResultBO<List<TrendBaseBO>> findBSTrend(LotteryTrendVO param) throws Exception;
	
	
	ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param) throws Exception;
	
	
	/**************************开奖信息****************************/
	
	/**
	 * 
	 * @desc 开奖信息中查找冷/热,遗漏数据
	 * @create 2018年1月5日
	 * @param param
	 * @return ResultBO<List<ColdHotOmitBo>>
	 */
	ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception;
}
