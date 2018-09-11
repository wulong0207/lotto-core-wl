package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.trend.service.IPl3TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OmitEnum.Pl3OmitType;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.F3dColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3Code3TrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3OmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3RecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3RecentTrendOutBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * @desc 排列三遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
@Service("pl3TrendService")
public class Pl3TrendServiceImpl extends NumTrendServiceImpl implements IPl3TrendService {

	private static Logger logger = LoggerFactory.getLogger(Pl3TrendServiceImpl.class);
	/*****************************排列三首页-遗漏、冷热、概率数据接口 *********************************/
	@Override
	public ResultBO<Pl3DrawOtherBO> findLatestDrawOther(LotteryVO param) {
		// 0.断言彩期不为空
		Assert.paramNotNull(param, "issueCode");
		Assert.paramNotNull(param.getIssueCode(), "issueCode");
		
		// 1.缓存获取
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL3.getName() + "_latest_draw_other_" + param.getIssueCode();
		Pl3DrawOtherBO target = (Pl3DrawOtherBO) redisUtil.getObj(key);
		if (target != null) {
			return ResultBO.ok(target);
		}
		// 2.数据库获取
		target = pl3TrendDaoMapper.findLatestDrawOtherFront(param);

		// 3.添加缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findMaxCode3Trend() {

		// 2.数据库获取
		TrendBaseBO target = pl3TrendDaoMapper.findMaxCode3TrendFront();

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findSingleCode3Trend(LotteryVO param) {
		// 0.断言彩种合法,彩期不为空
		Assert.paramNotNull(param.getIssueCode(), "issueCode");

		// 2.数据库获取
		TrendBaseBO target = pl3TrendDaoMapper.findSingleCode3TrendFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findRangeCode3Trend(LotteryVO param) {

		// 2.数据库获取
		List<TrendBaseBO> target = pl3TrendDaoMapper.findRangeCode3TrendFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findOmitChanceColdHotCode3(LotteryVO param) throws Exception {
		List<TrendBaseBO> trendList = null;
		Map<String, TrendBaseBO> totalMap = null;
		// 0.断言查询标识不为空
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");
		
		TrendBaseBO target = null;
		// 2.数据库获取
		switch (param.getQryFlag()) {
		case Constants.NUM_1:
			// 如果不传期号，则默认查最大遗漏期数据
			if (StringUtil.isBlank(param.getIssueCode())) {
				target = findMaxCode3Trend().getData();
			} else {
				target = findSingleCode3Trend(param).getData();
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
			trendList = findRangeCode3Trend(param).getData();
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
			trendList = findRangeCode3Trend(param).getData();
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
		
		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findMaxSumTrend() {

		// 2.数据库获取
		TrendBaseBO target = pl3TrendDaoMapper.findMaxSumTrendFront();

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findSingleSumTrend(LotteryVO param) {
		// 0.断言彩种合法,彩期不为空
		Assert.paramNotNull(param.getIssueCode(), "issueCode");

		// 2.数据库获取
		TrendBaseBO target = pl3TrendDaoMapper.findSingleSumTrendFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<List<TrendBaseBO>> findRangeSumTrend(LotteryVO param) {
		// 0.断言彩种合法
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");

		// 2.数据库获取
		List<TrendBaseBO> target = pl3TrendDaoMapper.findRangeSumTrendFront(param);

		return ResultBO.ok(target);
	}

	@Override
	public ResultBO<TrendBaseBO> findOmitChanceColdHotSum(LotteryVO param) throws Exception {
		List<TrendBaseBO> trendList = null;
		Map<String, TrendBaseBO> totalMap = null;
		// 0.断言查询标识不为空
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");

		TrendBaseBO target = null;
		// 2.数据库获取
		switch (param.getQryFlag()) {
		case Constants.NUM_1:
			// 如果不传期号，则默认查最大遗漏期数据
			if (StringUtil.isBlank(param.getIssueCode())) {
				target  = findMaxSumTrend().getData();
			} else {
				target = findSingleSumTrend(param).getData();
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
			trendList = findRangeSumTrend(param).getData();
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
			trendList = findRangeSumTrend(param).getData();
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

		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<Pl3OmitOutBO>> findOmitChanceColdHotAll(LotteryVO param) throws Exception {
		// 0.断言彩种合法
		Assert.paramNotNull(param, "lotteryCode");
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");
		
		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL3.getName() + "_omit_chance_coldhot_all_"
				+ param.getIssueCode() + param.getQryFlag() + param.getQryCount() + param.getOmitType();
		List<Pl3OmitOutBO> target = (List<Pl3OmitOutBO>) redisUtil.getObj(key);
		if (null != target) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取
		target = new ArrayList<>();
		if (null == param.getOmitType()) {
			// 1：直选-普通遗漏；
			ResultBO<TrendBaseBO> rs = findOmitChanceColdHotCode3(param);
			target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.D_PT, rs.getData()));
			// 2：组三-包号遗漏|组六-普通遗漏|组六-胆拖遗漏
			rs = findOmitChanceColdHot(param);
			target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G3_BH_G6_PT_DT, rs.getData()));
			// 3：直选-和值遗漏；
			rs = findOmitChanceColdHotSum(param);
			target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.D_SUM, rs.getData()));
			// 4：组三-和值遗漏；
			target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G3_SUM, rs.getData()));
			// 5：组六-和值遗漏
			target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G6_SUM, rs.getData()));
		} else {
			ResultBO<TrendBaseBO> rs;
			// 分遗漏类型
			if (param.getOmitType().intValue() == Constants.NUM_1) {
				// 1：直选-普通遗漏；
				rs = findOmitChanceColdHotCode3(param);
				target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.D_PT, rs.getData()));
			} else if (param.getOmitType().intValue() == Constants.NUM_2) {
				// 2：组三-包号遗漏|组六-普通遗漏|组六-胆拖遗漏
				rs = findOmitChanceColdHot(param);
				target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G3_BH_G6_PT_DT, rs.getData()));
			} else if (param.getOmitType().intValue() == Constants.NUM_3) {
				// 3：直选-和值遗漏；
				rs = findOmitChanceColdHotSum(param);
				target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.D_SUM, rs.getData()));
			} else if (param.getOmitType().intValue() == Constants.NUM_4) {
				// 4：组三-和值遗漏；
				rs = findOmitChanceColdHotSum(param);
				target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G3_SUM, rs.getData()));
			} else if (param.getOmitType().intValue() == Constants.NUM_5) {
				// 5：组六-和值遗漏
				rs = findOmitChanceColdHotSum(param);
				target.add(new Pl3OmitOutBO(param.getQryFlag(), Pl3OmitType.G6_SUM, rs.getData()));
			}
		}

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<Pl3RecentTrendOutBO>> findRecentTrend(LotteryVO param) {
		// 0.参数设置
		param = (null == param) ? new LotteryVO(Lottery.PL3.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL3.getName() + "_recent_trend_" + param.getQryCount();
		List<Pl3RecentTrendOutBO> target = (List<Pl3RecentTrendOutBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取并格式化输出
		target = new ArrayList<>();
		List<Pl3RecentTrendBO> rs = pl3TrendDaoMapper.findRecentTrend(param);
		if (!ObjectUtil.isBlank(rs)) {
			for (Pl3RecentTrendBO temp : rs) {
				if (temp == null) {
					continue;
				}
				target.add(new Pl3RecentTrendOutBO(temp));
			}
		}

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<Pl3RecentTrendBO>> findRecentTrendSimple(LotteryVO param) {
		param = (null == param) ? new LotteryVO(Lottery.PL3.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL3.getName() + "_recent_trend_simple_" + param.getQryCount();
		List<Pl3RecentTrendBO> target = (List<Pl3RecentTrendBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}
		
		// 2.数据库获取
		target = pl3TrendDaoMapper.findRecentTrendSimple(param);

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}
	
	/*****************************排列三走势图数据接口 *********************************/
	
	
	
	/*****************************开奖信息冷热数据 *********************************/
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception {
		param.setQryCount(Constants.NUM_100);
		param.setQryFlag(Constants.NUM_2);
		ResultBO<TrendBaseBO> omit = findOmitChanceColdHotCode3(param);
		Pl3Code3TrendBO data =(Pl3Code3TrendBO) omit.getData();
		Comparator<NumTimeVo> compare = new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		};
		F3dColdHotOmitBo bo = new F3dColdHotOmitBo();
		//排列3 个十百
		List<NumTimeVo> omitData = dealWithOmitData(data,GW,Constants.NUM_9);
		Collections.sort(omitData,compare);
		bo.setGwColdBase(omitData.get(0));
		bo.setGwHotBase(omitData.get(Constants.NUM_9));
		omitData = dealWithOmitData(data,SW,Constants.NUM_9);
		Collections.sort(omitData,compare);
		bo.setSwColdBase(omitData.get(0));
		bo.setSwHotBase(omitData.get(Constants.NUM_9));
		omitData = dealWithOmitData(data,BW,Constants.NUM_9);
		Collections.sort(omitData,compare);
		bo.setBwColdBase(omitData.get(0));
		bo.setBwHotBase(omitData.get(Constants.NUM_9));
		return ResultBO.ok((ColdHotOmitBo)bo);
	}

	/**
	 * 为开奖信息处理数据返回
	 * @desc 
	 * @create 2018年1月6日
	 * @param baseTrend
	 * @param preField
	 * @param count
	 * @return List<NumTimeVo>
	 */
	private List<NumTimeVo> dealWithOmitData(Pl3Code3TrendBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>();
		NumTimeVo vo = null;
		for (int i = 0; i <= count; i++) {
			vo = new NumTimeVo();
			vo.setCode(i+"");
			vo.setTime(ClassUtil.getField(baseTrend, preField + i, Integer.class));
			list.add(vo);
		}
		return list;
	}
}
