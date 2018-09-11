package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.trend.service.IQlcTrendService;
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
import com.hhly.skeleton.lotto.base.trend.bo.QlcColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcOmitOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcRecentTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcRecentTrendOutBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.qlc.QlcTrendBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * @desc 七乐彩遗漏走势的服务接口
 * @author huangb
 * @date 2017年6月27日
 * @company 益彩网络
 * @version v1.0
 */
@Service("qlcTrendService")
public class QlcTrendServiceImpl extends NumTrendServiceImpl implements IQlcTrendService {

	private static Logger logger = LoggerFactory.getLogger(QlcTrendServiceImpl.class);

	/*****************************七乐彩首页-遗漏、冷热、概率数据接口 *********************************/
	@Override
	public ResultBO<QlcOmitOutBO> findOmitChanceColdHotAll(LotteryVO param) throws Exception {
		logger.debug("查询七乐彩遗漏、冷热、概率数据");
		// 0.断言彩种合法
		Assert.paramNotNull(param, "lotteryCode");
		Assert.paramLegal(Lottery.contain(param.getLotteryCode()), "lotteryCode");
		Assert.paramNotNull(param.getQryFlag(), "qryFlag");

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_chance_coldhot_all_"
				+ param.getIssueCode() + param.getQryFlag() + param.getQryCount() + param.getOmitType();
		QlcOmitOutBO target = (QlcOmitOutBO) redisUtil.getObj(key);
		if (null != target) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取并格式化输出
		ResultBO<TrendBaseBO> rs = findOmitChanceColdHot(param);
		target = new QlcOmitOutBO(param.getQryFlag(), rs.getData());

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<QlcRecentTrendOutBO>> findRecentTrend(LotteryVO param) {
		// 0.参数设置
		param = (null == param) ? new LotteryVO(Lottery.QLC.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.QLC.getName() + "_recent_trend_" + param.getQryCount();
		List<QlcRecentTrendOutBO> target = (List<QlcRecentTrendOutBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}

		// 2.数据库获取并格式化输出
		target = new ArrayList<>();
		List<TrendBaseBO> rs = qlcTrendDaoMapper.findTrendRangeFront(param);
		if (!ObjectUtil.isBlank(rs)) {
			for (TrendBaseBO temp : rs) {
				if (temp == null) {
					continue;
				}
				target.add(new QlcRecentTrendOutBO(temp));
			}
		}

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}


	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<QlcRecentTrendBO>> findRecentTrendSimple(LotteryVO param) {
		param = (null == param) ? new LotteryVO(Lottery.QLC.getName()) : param;
		// 可为空，默认10；范围1~50，如果不在该范围，也默认10
		if (null == param.getQryCount() || param.getQryCount() < NUMConstants.NUM_1 || param.getQryCount() > NUMConstants.NUM_50) {
			param.setQryCount(NUMConstants.NUM_10);
		}

		// 1.获取缓存
		String key = CacheConstants.N_CORE_LOTTO_OMIT + Lottery.QLC.getName() + "_recent_trend_simple_" + param.getQryCount();
		List<QlcRecentTrendBO> target = (List<QlcRecentTrendBO>) redisUtil.getObj(key);
		if (!ObjectUtil.isBlank(target)) {
			return ResultBO.ok(target);
		}
		
		// 2.数据库获取
		target = qlcTrendDaoMapper.findRecentTrendSimple(param);

		// 3.设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	/*****************************七乐彩走势图数据接口 *********************************/
	
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception {
		param.setQryCount(Constants.NUM_100);
		param.setQryFlag(Constants.NUM_2);
		ResultBO<TrendBaseBO> omit = findOmitChanceColdHot(param);
		QlcTrendBO data =(QlcTrendBO) omit.getData();
		Comparator<NumTimeVo> compare = new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		};
		QlcColdHotOmitBo bo = new QlcColdHotOmitBo();
		//七乐彩 30位  取八位
		List<NumTimeVo> omitData = dealWithOmitData(data,B,Constants.NUM_30);
		Collections.sort(omitData,compare);
		bo.setFirColdBase(omitData.get(Constants.NUM_0));
		bo.setSecColdBase(omitData.get(Constants.NUM_1));
		bo.setThrColdBase(omitData.get(Constants.NUM_2));
		bo.setFouColdBase(omitData.get(Constants.NUM_3));
		bo.setFriColdBase(omitData.get(Constants.NUM_4));
		bo.setSixColdBase(omitData.get(Constants.NUM_5));
		bo.setSevColdBase(omitData.get(Constants.NUM_6));
		bo.setEigColdBase(omitData.get(Constants.NUM_7));

		Collections.reverse(omitData);

		bo.setFirHotBase(omitData.get(Constants.NUM_0));
		bo.setSecHotBase(omitData.get(Constants.NUM_1));
		bo.setThrHotBase(omitData.get(Constants.NUM_2));
		bo.setFouHotBase(omitData.get(Constants.NUM_3));
		bo.setFriHotBase(omitData.get(Constants.NUM_4));
		bo.setSixHotBase(omitData.get(Constants.NUM_5));
		bo.setSevHotBase(omitData.get(Constants.NUM_6));
		bo.setEigHotBase(omitData.get(Constants.NUM_7));
		
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
	private List<NumTimeVo> dealWithOmitData(QlcTrendBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_30);
		String sufField = ""; // 字段后缀
		NumTimeVo vo = null;
		for (int i = 1; i <= count; i++) {
			vo = new NumTimeVo();
			sufField = (i >= 1 && i <= 9) ? "0" + i : String.valueOf(i); // 1~9的数字补0
			vo.setCode(sufField);
			vo.setTime(ClassUtil.getField(baseTrend, preField + sufField, Integer.class));
			list.add(vo);
		}
		return list;
	}
	
}
