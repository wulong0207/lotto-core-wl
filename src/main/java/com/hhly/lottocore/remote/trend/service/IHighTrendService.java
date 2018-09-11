package com.hhly.lottocore.remote.trend.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TreadStatistics;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitDataBO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighOmitBetVO;

/**
 * @desc    高频彩服务接口
 * @author  Tony Wang
 * @date    2017年3月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface IHighTrendService {

	/**
	 * @desc   查询遗漏(1:冷热数据;2:当前遗漏;3:最大遗漏;4:上次遗漏)
	 * @author Tony Wang
	 * @create 2017年3月15日
	 * @param resultVO
	 * @return 因为qryFlag为1和2时返回的数据结构不一样，所以返回ResultBO<?>
	 */
	<T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultOmit(T resultVO);
	
	/**
	 * @desc   查询近期走势
	 * @author Tony Wang
	 * @create 2017年3月29日
	 * @param recentVO
	 * @return 
	 */
	<T extends HighLotteryVO> ResultBO<HighOmitDataBO> findRecentOmit(T recentVO);
	
	/**
	 * @desc   查询遗漏和近期走势
	 * @author Tony Wang
	 * @create 2017年3月29日
	 * @param resultVO
	 * @param recentVO
	 * @return 
	 */
	<T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultAndRecentOmit(T resultVO, T recentVO);
	
	/**
	 * @desc   查询最近开奖
	 * @author Tony Wang
	 * @create 2017年7月19日
	 * @param vo
	 * @return 
	 */
	ResultBO<List<HighOmitBaseBO>> findRecentIssue(HighLotteryVO vo);
	
	/**
	 * 遗漏投注
	 * @desc 
	 * @create 2017年11月10日
	 * @param vo
	 * @return ResultBO<List<HighOmitBaseBO>>
	 */
	ResultBO<List<TreadStatistics>> findOmitBet(HighOmitBetVO vo);
	
	/**
	 * 基本走势
	 * @desc 
	 * @create 2017年11月15日
	 * @param vo
	 * @return ResultBO<List<LotteryTrend>>
	 */
	ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo);
	
	/**
	 * 
	 * @desc 开奖信息中查找冷/热,遗漏数据
	 * @create 2018年1月5日
	 * @param param
	 * @return ResultBO<List<ColdHotOmitBo>>
	 */
	ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param);	
	
	
	/**
	 * 走势投注
	 * @desc 
	 * @create 2018年3月29日
	 * @param vo
	 * @return ResultBO<SscTrendBetHotchBO>
	 */
	ResultBO<TrendBaseBO> findTrendBetting(LotteryTrendVO vo);
}
