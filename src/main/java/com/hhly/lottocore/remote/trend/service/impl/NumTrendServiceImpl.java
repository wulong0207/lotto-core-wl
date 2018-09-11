package com.hhly.lottocore.remote.trend.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.persistence.trend.num.dao.DltTrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.F3dTrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.Pl3TrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.Pl5TrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.QlcTrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.QxcTrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.SsqTrendDaoMapper;
import com.hhly.lottocore.persistence.trend.num.dao.TrendDaoMapper;
import com.hhly.lottocore.remote.trend.service.INumTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;

/**
 * @desc 各彩种遗漏/走势的公共服务接口
 * @author huangb
 * @date 2017年3月8日
 * @company 益彩网络
 * @version v1.0
 */
@Service("numTrendService")
public class NumTrendServiceImpl implements INumTrendService {

	private static Logger logger = LoggerFactory.getLogger(NumTrendServiceImpl.class);
	
	protected static final String R="r";
	protected static final String B="b";
	protected static final String GW = "g";
	protected static final String SW = "s";
	protected static final String BW = "b";
	protected static final String QW = "q";
	protected static final String WW = "w";
	protected static final String SWW = "sw";
	protected static final String BWW = "bw";
	
	/** 双色球遗漏走势数据接口 */
	@Autowired
	protected SsqTrendDaoMapper ssqTrendDaoMapper;
	/** 大乐透遗漏走势数据接口 */
	@Autowired
	protected DltTrendDaoMapper dltTrendDaoMapper;
	/** 福彩3D遗漏走势数据接口 */
	@Autowired
	protected F3dTrendDaoMapper f3dTrendDaoMapper;
	/** 排列三遗漏走势数据接口 */
	@Autowired
	protected Pl3TrendDaoMapper pl3TrendDaoMapper;
	/** 排列五遗漏走势数据接口 */
	@Autowired
	protected Pl5TrendDaoMapper pl5TrendDaoMapper;
	/** 七乐彩遗漏走势数据接口 */
	@Autowired
	protected QlcTrendDaoMapper qlcTrendDaoMapper;
	/** 七星彩遗漏走势数据接口 */
	@Autowired
	protected QxcTrendDaoMapper qxcTrendDaoMapper;
	/**
	 * 缓存工具
	 */
	@Autowired 
	protected RedisUtil redisUtil;
	
	/**
	 * @desc 统一获取各彩种遗漏走势的数据接口
	 * @author huangb
	 * @date 2017年3月8日
	 * @param lotteryCode
	 *            彩种code
	 * @return 统一获取各彩种遗漏走势的数据接口
	 */
	protected TrendDaoMapper getTrendDaoMapper(Integer lotteryCode) {
		Lottery lot = Lottery.getLottery(lotteryCode);
		switch (lot) {
		case SSQ:
			return ssqTrendDaoMapper;
		case DLT:
			return dltTrendDaoMapper;
		case F3D:
			return f3dTrendDaoMapper;
		case PL3:
			return pl3TrendDaoMapper;
		case PL5:
			return pl5TrendDaoMapper;
		case QLC:
			return qlcTrendDaoMapper;
		case QXC:
			return qxcTrendDaoMapper;
		default:
			Assert.paramLegal(false, "lotteryCode");
			return null;
		}
	}
	
	/*****************************各彩种首页-遗漏、冷热、概率数据接口 *********************************/
	
	@Override
	public ResultBO<TrendBaseBO> findSingle(LotteryVO param) {
		// 0.断言彩种合法,彩期不为空
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");
		Assert.paramNotNull(param.getIssueCode(), "issueCode");

		// 2.数据库获取
		TrendDaoMapper dao = getTrendDaoMapper(param.getLotteryCode());
		TrendBaseBO target = dao.findSingleFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findTrendRange(LotteryVO param) {
		// 0.断言彩种合法
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");

		// 2.数据库获取
		TrendDaoMapper dao = getTrendDaoMapper(param.getLotteryCode());
		List<TrendBaseBO> target = dao.findTrendRangeFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findMaxTrend(LotteryVO param) {
		// 0.断言彩种合法
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");

		// 2.数据库获取
		TrendDaoMapper dao = getTrendDaoMapper(param.getLotteryCode());
		TrendBaseBO target = dao.findMaxTrendFront();

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findOmitChanceColdHot(LotteryVO param) throws Exception {
		List<TrendBaseBO> trendList = null;
		Map<String, TrendBaseBO> totalMap = null;
		// 0.断言查询标识不为空
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");
		
		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_chance_coldhot_" + param.getIssueCode() + param.getQryFlag() + param.getQryCount();
		TrendBaseBO target = (TrendBaseBO) redisUtil.getObj(key);
		if (null != target) {
			return ResultBO.ok(target);
		}
		
		// 2.数据库获取
		switch (param.getQryFlag()) {
		case Constants.NUM_1:
			// 如果不传期号，则默认查最大遗漏期数据
			if (StringUtil.isBlank(param.getIssueCode())) {
				target = findMaxTrend(param).getData();
			} else {
				target = findSingle(param).getData();
			}
			// 断言查询数据存在
			Assert.dataExist(target);
			break;
		case Constants.NUM_2:
			logger.debug("查询冷热数据");
			// 断言查询条数不为空
			Assert.paramNotNull(param.getQryCount(), "qryCount");
			// 查询期数范围1~200,否则默认50
			if (param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_200) {
				param.setQryCount(NUMConstants.NUM_50);
			}
			trendList = findTrendRange(param).getData();
			// 断言查询数据存在
			Assert.dataExist(trendList);
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			target = totalMap.get("occTimes");
			break;
		case Constants.NUM_3:
			logger.debug("查询概率数据");
			// 断言查询条数不为空
			Assert.paramNotNull(param.getQryCount(), "qryCount");
			// 查询期数范围1~200,否则默认50
			if (param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_200) {
				param.setQryCount(NUMConstants.NUM_50);
			}
			trendList = findTrendRange(param).getData();
			// 断言查询数据存在
			Assert.dataExist(trendList);
			// 当前遗漏值（就取集合第一条）
			TrendBaseBO curMiss = trendList.get(0);
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, curMiss.getClass());
			// 平均遗漏值
			TrendBaseBO avgMiss = totalMap.get("avgMiss");
			// 出号概率=号码当前遗漏值/号码历史平均遗漏值
			target = OmitTrendUtil.getProbability(curMiss, avgMiss, curMiss.getClass());
			break;
		default:
			Assert.paramLegal(false, "qryFlag");
		}
		OmitTrendUtil.assemble(target);
		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	
	
	@Override
	public ResultBO<List<TrendBaseBO>> findOETrend(LotteryTrendVO param) throws Exception {
		Integer lotteryCode = param.getLotteryCode();
		String key=CacheConstants.getLotteryTrendKey(lotteryCode, param.getStartIssue(),param.getEndIssue(),param.getQryCount(),"OE");
		@SuppressWarnings("unchecked")
		List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
		if(target!=null){
			return ResultBO.ok(target);
		}
		TrendDaoMapper dao = getTrendDaoMapper(lotteryCode);
		target = dao.findOETrend(param);
		redisUtil.addObj(key, target, (long)Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findBSTrend(LotteryTrendVO param) throws Exception {
		Integer lotteryCode = param.getLotteryCode();
		String key=CacheConstants.getLotteryTrendKey(lotteryCode, param.getStartIssue(),param.getEndIssue(),param.getQryCount(), "BS");
		@SuppressWarnings("unchecked")
		List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
		if(target!=null){
			return ResultBO.ok(target);
		}
		TrendDaoMapper dao = getTrendDaoMapper(lotteryCode);
		target = dao.findBSTrend(param);
		redisUtil.addObj(key, target, (long)Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param) throws Exception {
		Integer lotteryCode = param.getLotteryCode();
		String key=CacheConstants.getLotteryTrendKey(lotteryCode, param.getStartIssue()==null?"0":param.getStartIssue(),param.getEndIssue()==null?"0":param.getEndIssue(),param.getQryCount() ,"BASE");
		@SuppressWarnings("unchecked")
		List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
		if(target!=null){
			return ResultBO.ok(target);
		}
		TrendDaoMapper dao = getTrendDaoMapper(lotteryCode);
		target = dao.findBaseTrend(param);
		redisUtil.addObj(key, target, (long)Constants.DAY_1);
		return ResultBO.ok(target);
	}




	/*****************************后续各彩种走势图数据接口 *********************************/
	
	
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}
	
}
