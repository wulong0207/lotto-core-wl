package com.hhly.lottocore.remote.trend.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.LotteryIssueCacheService;
import com.hhly.lottocore.persistence.trend.high.dao.HighLotteryDaoMapper;
import com.hhly.lottocore.persistence.trend.high.dao.K3DaoMapper;
import com.hhly.lottocore.persistence.trend.high.dao.Kl10DaoMapper;
import com.hhly.lottocore.persistence.trend.high.dao.PokerDaoMapper;
import com.hhly.lottocore.persistence.trend.high.dao.SscDaoMapper;
import com.hhly.lottocore.persistence.trend.high.dao.X115DaoMapper;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TreadStatistics;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitDataBO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighOmitBetVO;

@Service("highTrendService")
public class HighTrendService2Impl extends AbstractHighTrendService {
	protected static final String  DATE_FORMAT= "yyyyMMdd";
	protected static final String  DATE_FORMAT_YYMMDD= "yyMMdd";
	
	
	protected static Logger logger = LoggerFactory.getLogger(HighTrendService2Impl.class);
	
	@Autowired 
	protected LotteryIssueCacheService lotteryIssueCacheService;
	@Autowired 
	protected RedisUtil redisUtil;
	/** 快3 */
	@Autowired
	protected K3DaoMapper k3DaoMapper;
	/** 时时彩 */
	@Autowired
	protected SscDaoMapper sscDaoMapper;
	/** 快乐扑克 */
	@Autowired
	private PokerDaoMapper pokerDaoMapper;
	@Autowired
	private Kl10DaoMapper kl10DaoMapper;
	@Autowired
	protected X115DaoMapper x115DaoMapper;
	
	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultOmit(T resultVO) {
		logger.debug("高频彩查询历史遗漏数据,彩种:{}", resultVO.getLotteryCode());
		String key = CacheConstants.getLotteryOmitKey(resultVO.getLotteryCode(),resultVO.getOmitTypes(),resultVO.getQryCount(),resultVO.getSubPlays(), "findResultOmit");
		HighOmitDataBO target = (HighOmitDataBO) redisUtil.getObj(key);
		if(target!=null ){
			return ResultBO.ok(target);
		}
		List<HighOmitBaseBO> resultOmit = getLotteryDaoMapper(resultVO).findResultOmit(resultVO);
		boolean isNull = true;
		for(HighOmitBaseBO omit : resultOmit) {
			if(omit != null) {
				isNull = false;
				break;
			}
		}
		// 防止给前端返回空字符串
		resultOmit = isNull ? Collections.<HighOmitBaseBO>emptyList() : resultOmit;
		OmitTrendUtil.assemble(resultOmit);
		target = new HighOmitDataBO(resultOmit);
		redisUtil.addObj(key, target,(long)Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findRecentOmit(T recentVO) {
		logger.debug("高频彩查询近期遗漏数据,彩种:{}", recentVO.getLotteryCode());
		/*
		 * 按近按近N期查询时，"80期冷热"、"当前遗漏"、"当前遗漏"、"上次遗漏"不同动态计算
		 */
		// 查出来的list是按issue desc排序的
		String key = CacheConstants.getLotteryOmitKey(recentVO.getLotteryCode(), recentVO.getOmitTypes(),recentVO.getQryCount(),recentVO.getSubPlays(), "findRecentOmit");
		HighOmitDataBO target = (HighOmitDataBO) redisUtil.getObj(key);
		if(target!=null){
			return ResultBO.ok(target);
		}
		List<HighOmitBaseBO> recentOmit = getLotteryDaoMapper(recentVO).findRecentOmit(recentVO);
		boolean isNull = true;
		for(HighOmitBaseBO omit : recentOmit) {
			if(omit != null) {
				isNull = false;
				break;
			}
		}
		// 防止给前端返回空字符串
		recentOmit = isNull ? Collections.<HighOmitBaseBO>emptyList() : recentOmit;
		OmitTrendUtil.assemble(recentOmit);
		target = new HighOmitDataBO(null, recentOmit);
		redisUtil.addObj(key, target, (long)Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<HighOmitBaseBO>> findRecentIssue(HighLotteryVO vo) {
		logger.debug("高频彩查询近期开奖,彩种:{}",vo.getLotteryCode());
		String key=CacheConstants.getLotteryOmitKey(vo.getLotteryCode(),vo.getOmitTypes(),vo.getQryCount(),vo.getSubPlays(),"findRecentIssue");
		@SuppressWarnings("unchecked")
		List<HighOmitBaseBO> list= (List<HighOmitBaseBO>) redisUtil.getObj(key);
		if(list!=null && list.size()>0){
			return ResultBO.ok(list);
		}
		list = getLotteryDaoMapper(vo).findRecentIssue(vo);
		OmitTrendUtil.assemble(list);
		redisUtil.addObj(key, list, (long)Constants.DAY_1);
		return ResultBO.ok(list);
	}
	
	/**
	 * @desc   根据彩种获取Dao
	 * @author Tony Wang
	 * @create 2017年3月13日
	 * @param lotteryCode
	 * @return 
	 */
	protected HighLotteryDaoMapper getLotteryDaoMapper(HighLotteryVO vo) {
		Lottery lot = Lottery.getLottery(vo.getLotteryCode());
		switch (lot) {
		case JSK3:
		case JXK3:	
			return k3DaoMapper;
		case CQSSC:
			return sscDaoMapper;
		case SDPOKER:
			return pokerDaoMapper;
		case CQKL10:
//		case DKL10:
			return kl10DaoMapper;
		case JX11X5:
		case XJ11X5:
		case D11X5:
		case GX11X5:
		case SD11X5:	
			return x115DaoMapper;
		default:
			throw new IllegalArgumentException("不存在或不支持此高频彩种,彩种编码:"+vo.getLotteryCode()+"！");
		}
	}

	@Override
	public ResultBO<List<TreadStatistics>> findOmitBet(HighOmitBetVO vo) {
		return null;
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo) {
		logger.debug("高频彩查询基本走势,彩种:{}",vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(), vo.getStartIssue()==null?"0":vo.getStartIssue(), vo.getEndIssue()==null?"0":vo.getEndIssue(),vo.getQryCount(), "base");
			@SuppressWarnings("unchecked")
			List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
			if(target!=null && target.size()>0){
				return ResultBO.ok(target);
			}
			HighLotteryDaoMapper dao = getLotteryDaoMapper(lotteryVo);
			target = dao.findBaseTrend(vo);
			redisUtil.addObj(key, target, (long)Constants.DAY_1);
			return ResultBO.ok(target);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
		
		return null;
	}

	@Override
	public ResultBO<TrendBaseBO> findTrendBetting(LotteryTrendVO vo) {
		return null;
	}	
}