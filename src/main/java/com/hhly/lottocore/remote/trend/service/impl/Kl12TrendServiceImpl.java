package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.area.dao.Kl12DaoMapper;
import com.hhly.lottocore.remote.trend.service.IKl12TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.Kl12TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 
 * @desc 快乐12
 * @author chenghougui
 * @Date 2018年1月19日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("kl12TrendService")
public class Kl12TrendServiceImpl extends HighTrendService2Impl implements IKl12TrendService {
	
	@Autowired
	private Kl12DaoMapper kl12DaoMapper;
	
	/*****************************快乐12走势图数据接口 *********************************/
	
	/**
	 * 基本走势
	 */	
	
	
	
//	private List<Integer> genBaseDigits(K3BaseBO baseTrend, String preField,int begin,int end) {
//		List<Integer> tmp = new ArrayList<>();
//		for (int i = begin; i <= end; i++) {
//			tmp.add(ClassUtil.getField(baseTrend, preField + i, Integer.class));
//		}
//		return tmp;
//	}
	
	/*****************************开奖信息 *********************************/
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
		/** 冷热数据  **/
		LotteryTrendVO vo = new LotteryTrendVO();
		vo.setLotteryCode(param.getLotteryCode());
		vo.setQryCount(param.getQryCount());
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_coldhot_" + param.getIssueCode() + param.getQryFlag() + param.getQryCount();
		ColdHotOmitBo target = (ColdHotOmitBo) redisUtil.getObj(key);
		if(target!=null){
			return new ResultBO<ColdHotOmitBo>(target);
		}
		List<TrendBaseBO> trendList = kl12DaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		List<NumTimeVo> coldHotList = dealWithColdHotData((Kl12TrendBaseBO)base,"b",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new ColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_7, Constants.NUM_12);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}
	
	private List<NumTimeVo> dealWithColdHotData(Kl12TrendBaseBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_50);
		NumTimeVo vo = null;
		String sufField = "";
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
