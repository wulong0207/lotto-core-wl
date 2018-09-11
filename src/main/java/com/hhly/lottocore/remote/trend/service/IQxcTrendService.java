package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.QxcDrawOtherBO;

/**
 * @desc    七星彩遗漏走势的服务接口
 * @author  Tony Wang
 * @date    2017年7月31日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface IQxcTrendService extends INumTrendService {

	/**
	 * @desc   查询最近开奖
	 * @author Tony Wang
	 * @create 2017年7月31日
	 * @param vo
	 * @return 
	 */
	ResultBO<List<TrendBaseBO>> findRecentDrawIssue(LotteryVO vo);

	/**
	 * @desc   前端接口：查询最新开奖的开奖其它信息(eg:奇偶比、大小比等)
	 * @author Tony Wang
	 * @create 2017年8月4日
	 * @param lotteryVO
	 * @return 
	 */
	ResultBO<QxcDrawOtherBO> findLatestDrawOther(LotteryVO lotteryVO);
	
}
