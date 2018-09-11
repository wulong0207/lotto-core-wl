package com.hhly.lottocore.persistence.trend.num.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.QxcDrawOtherBO;

public interface QxcTrendDaoMapper extends TrendDaoMapper {

	/**
	 * @desc   查询最近开奖
	 * @author Tony Wang
	 * @create 2017年7月31日
	 * @param vo
	 * @return 
	 */
	List<TrendBaseBO> findRecentDrawIssue(LotteryVO vo);

	/**
	 * @desc   前端接口：查询最新开奖的开奖其它信息(eg:奇偶比、大小比等)
	 * @author Tony Wang
	 * @create 2017年8月4日
	 * @param lotteryVO
	 * @return 
	 */
	QxcDrawOtherBO findLatestDrawOtherFront(LotteryVO lotteryVO);

}
