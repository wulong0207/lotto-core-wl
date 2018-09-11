package com.hhly.lottocore.persistence.trend.high.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;

/**
 * @desc    高频彩Dao
 * @author  Tony Wang
 * @date    2017年3月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface HighLotteryDaoMapper {
	
	/**
	 * @desc   查询近期遗漏(之前为了复用OmitTrendUtil的计算方法,所以返回TrendBaseBO)
	 * @author Tony Wang
	 * @create 2017年3月15日
	 * @param vo
	 * @return 
	 */
	List<HighOmitBaseBO> findRecentOmit(HighLotteryVO vo);
	
	/**
	 * @desc   查询遗漏
	 * @author Tony Wang
	 * @create 2017年3月15日
	 * @param vo
	 * @return 
	 */
	List<HighOmitBaseBO> findResultOmit(HighLotteryVO vo);

	/**
	 * @desc   按条件查询最近彩期
	 * @author Tony Wang
	 * @create 2017年7月28日
	 * @param vo
	 * @return 
	 */
	List<HighOmitBaseBO> findRecentIssue(HighLotteryVO vo);

	/**
	 * @desc   查询近期遗漏(之前为了复用OmitTrendUtil的计算方法,所以返回TrendBaseBO)
	 * @author Tony Wang
	 * @create 2017年3月15日
	 * @param vo
	 * @return 
	 
	List<TrendBaseBO> findOmitRecent(HighOmitBaseVO vo);*/
	
	
	List<TrendBaseBO> findBaseTrend(LotteryTrendVO vo);
	
	/**
	 * 查询走势数据
	 * @desc 
	 * @create 2018年3月29日
	 * @param vo
	 * @return List<TrendBaseBO>
	 */	
	List<TrendBaseBO> findRecentTrend(LotteryTrendVO vo);
	
}