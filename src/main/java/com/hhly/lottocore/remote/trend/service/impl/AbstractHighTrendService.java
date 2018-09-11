package com.hhly.lottocore.remote.trend.service.impl;

import java.util.List;

import com.hhly.lottocore.remote.trend.service.IHighTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.trend.bo.TreadStatistics;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitDataBO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighOmitBetVO;

public abstract class AbstractHighTrendService implements IHighTrendService {

	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultOmit(T vo) {
		throw new ResultJsonException("请重写");
	}
	
	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findRecentOmit(T recentVO) {
		throw new ResultJsonException("请重写");
	}

	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultAndRecentOmit(T omitVO, T recentVO) {
		return ResultBO.ok(new HighOmitDataBO(findResultOmit(omitVO).getData().getBaseOmit(), findRecentOmit(recentVO).getData().getRecentOmit()));
	}
	
	@Override
	public ResultBO<List<HighOmitBaseBO>> findRecentIssue(HighLotteryVO vo) {
		throw new ResultJsonException("请重写");
	}

	public ResultBO<List<TreadStatistics>> findOmitBet(HighOmitBetVO vo){
		throw new ResultJsonException("请重写");
	}	
	
}
