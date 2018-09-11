package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.trend.service.IPl5TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.P5dColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5OmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5RecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5RecentTrendOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5TrendBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * @desc 排列五遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
@Service("pl5TrendService")
public class Pl5TrendServiceImpl extends NumTrendServiceImpl implements IPl5TrendService {

	private static Logger logger = LoggerFactory.getLogger(Pl5TrendServiceImpl.class);

	/*****************************排列五首页-遗漏、冷热、概率数据接口 *********************************/
	
	@Override
	public ResultBO<Pl5DrawOtherBO> findLatestDrawOther(LotteryVO param) {
		// 0.断言彩期不为空
		Assert.paramNotNull(param, "issueCode");
		Assert.paramNotNull(param.getIssueCode(), "issueCode");
		
		// 1.缓存获取
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL5.getName() + "_latest_draw_other_" + param.getIssueCode();
		Pl5DrawOtherBO target = (Pl5DrawOtherBO) redisUtil.getObj(key);
		if (target != null) {
			return ResultBO.ok(target);
		}
		// 2.数据库获取
		target = pl5TrendDaoMapper.findLatestDrawOtherFront(param);

		// 3.添加缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}
	
	@Override
	public ResultBO<Pl5OmitOutBO> findOmitChanceColdHotAll(LotteryVO param) throws Exception {
		logger.debug("查询排列五遗漏、冷热、概率数据");
		// 0.断言彩种合法
		Assert.paramNotNull(param, "lotteryCode");
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_chance_coldhot_all_"
				+ param.getIssueCode() + param.getQryFlag() + param.getQryCount() + param.getOmitType();
		Pl5OmitOutBO target = (Pl5OmitOutBO) redisUtil.getObj(key);
		if (null != target) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取并格式化输出
		ResultBO<TrendBaseBO> rs = findOmitChanceColdHot(param);
		target = new Pl5OmitOutBO(param.getQryFlag(), rs.getData());

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<Pl5RecentTrendBO>> findRecentTrend(LotteryVO param) {
		// 0.参数设置
		param = (null == param) ? new LotteryVO(Lottery.PL5.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL5.getName() + "_recent_trend_" + param.getQryCount();
		List<Pl5RecentTrendBO> target = (List<Pl5RecentTrendBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取
		target = pl5TrendDaoMapper.findRecentTrend(param);

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<Pl5RecentTrendOutBO>> findTrendBettingInfo(LotteryVO param) {
		// 0.参数设置
		param = (null == param) ? new LotteryVO(Lottery.PL5.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.PL5.getName() + "_trend_betting_info_" + param.getQryCount();
		List<Pl5RecentTrendOutBO> target = (List<Pl5RecentTrendOutBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取并格式化输出
		target = new ArrayList<>();
		List<Pl5RecentTrendBO> rs = pl5TrendDaoMapper.findTrendBettingInfo(param);
		if (!ObjectUtil.isBlank(rs)) {
			for (Pl5RecentTrendBO temp : rs) {
				if (temp == null) {
					continue;
				}
				target.add(new Pl5RecentTrendOutBO(temp));
			}
		}

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}
	

	/*****************************排列五走势图数据接口 *********************************/
	
	
	
	
	/*****************************开奖信息冷热数据 *********************************/
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception {
		param.setQryCount(Constants.NUM_100);
		param.setQryFlag(Constants.NUM_2);
		ResultBO<TrendBaseBO> omit = findOmitChanceColdHot(param);
		Pl5TrendBO data =(Pl5TrendBO) omit.getData();
		Comparator<NumTimeVo> compare = new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		};
		P5dColdHotOmitBo bo = new P5dColdHotOmitBo();
		//排列5 个十百千万
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
		
		omitData = dealWithOmitData(data,QW,Constants.NUM_9);
		Collections.sort(omitData,compare);
		bo.setQwColdBase(omitData.get(0));
		bo.setQwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,WW,Constants.NUM_9);
		Collections.sort(omitData,compare);
		bo.setWwColdBase(omitData.get(0));
		bo.setWwHotBase(omitData.get(Constants.NUM_9));
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
	private List<NumTimeVo> dealWithOmitData(Pl5TrendBO baseTrend, String preField,int count) {
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
