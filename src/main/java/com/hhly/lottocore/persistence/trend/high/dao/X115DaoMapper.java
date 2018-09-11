package com.hhly.lottocore.persistence.trend.high.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TreadStatistics;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighOmitBetVO;

/**
 * @desc    十一选五Dao
 * @author  Tony Wang
 * @date    2017年3月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface X115DaoMapper extends HighLotteryDaoMapper {
	
	
	/**
	 * 遗漏投注
	 * @desc 
	 * @create 2017年11月10日
	 * @param omitVO
	 * @return ResultBO<List<TreadStatistics>>
	 */
	List<TreadStatistics> findOmitBetOfLX(HighOmitBetVO omitVO);
	
	List<TreadStatistics> findOmitBetOfQT(HighOmitBetVO omitVO);
	
	List<TrendBaseBO> findBaseTrend(LotteryTrendVO vo);
	
	List<TrendBaseBO> findTrendRangeFront(LotteryVO param);
	
	List<TrendBaseBO> findRecentTrend(LotteryTrendVO vo);
}
