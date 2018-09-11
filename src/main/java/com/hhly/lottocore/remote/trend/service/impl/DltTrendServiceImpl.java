package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.num.dao.TrendDaoMapper;
import com.hhly.lottocore.remote.trend.service.IDltTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseArrayBO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.DltTrendBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 大乐透走势
 * @desc 
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("dltTrendService")
public class DltTrendServiceImpl extends NumTrendServiceImpl implements IDltTrendService {

	
	/*****************************大乐透走势图数据接口 *********************************/
	
	/**
	 * 基本走势
	 */
	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO param) throws Exception {
		Integer lotteryCode = param.getLotteryCode();
		String key=CacheConstants.getLotteryTrendKey(lotteryCode, param.getStartIssue()==null?"0":param.getStartIssue(),param.getEndIssue()==null?"0":param.getEndIssue(),param.getQryCount(), "BASE");
		@SuppressWarnings("unchecked")
		List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
		if(target!=null){
			return ResultBO.ok(target);
		}
		TrendDaoMapper dao = getTrendDaoMapper(lotteryCode);
		target = dao.findBaseTrend(param);
		//数据处理
		List<TrendBaseBO> result = new ArrayList<>();
		for (TrendBaseBO trendBaseBO : target) {
			TrendBaseArrayBO bo = new TrendBaseArrayBO();
			bo.setIssue(trendBaseBO.getIssue());
			bo.setDrawCode(trendBaseBO.getDrawCode());
			bo.setRedList(genBaseDigits((DltTrendBO)trendBaseBO,R,Constants.NUM_35));
			bo.setBlueList(genBaseDigits((DltTrendBO)trendBaseBO,B,Constants.NUM_12));
			result.add(bo);
		}
		redisUtil.addObj(key, result, (long)Constants.DAY_1);
		return ResultBO.ok(result);
	}
	
	
	
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception {
		param.setQryCount(Constants.NUM_100);
		param.setQryFlag(Constants.NUM_2);
		ResultBO<TrendBaseBO> omit = findOmitChanceColdHot(param);
		DltTrendBO data =(DltTrendBO) omit.getData();
		Comparator<NumTimeVo> compare = new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		};
		ColdHotOmitBo bo = new ColdHotOmitBo();
		//大乐透选5个红球，2个蓝球
		List<NumTimeVo> omitData = dealWithOmitData(data,R,Constants.NUM_35);
		Collections.sort(omitData,compare);
		bo.setColdBaseList(new ArrayList<>(omitData.subList(Constants.NUM_0, Constants.NUM_5)));
		List<NumTimeVo> HotBaseList = omitData.subList(Constants.NUM_30, Constants.NUM_35);
		Collections.reverse(HotBaseList);
		bo.setHotBaseList(new ArrayList<>(HotBaseList));
		omitData =  dealWithOmitData(data,B,Constants.NUM_12);
		Collections.sort(omitData,compare);
		bo.setColdOtherList(new ArrayList<>(omitData.subList(Constants.NUM_0, Constants.NUM_2)));
		List<NumTimeVo> hotOtherList = omitData.subList(Constants.NUM_10, Constants.NUM_12);
		Collections.reverse(hotOtherList);
		bo.setHotOtherList(new ArrayList<>(hotOtherList));
		return ResultBO.ok(bo);
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
	private List<NumTimeVo> dealWithOmitData(DltTrendBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_50);
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
	
	
	private List<Integer> genBaseDigits(DltTrendBO baseTrend, String preField,int count) {
		List<Integer> tmp = new ArrayList<>();
		String sufField = ""; // 字段后缀
		for (int i = 1; i <= count; i++) {
			sufField = (i >= 1 && i <= 9) ? "0" + i : String.valueOf(i); // 1~9的数字补0
			Integer times = ClassUtil.getField(baseTrend, preField + sufField, Integer.class);
			tmp.add(times);
		}
		return tmp;
	}
}
