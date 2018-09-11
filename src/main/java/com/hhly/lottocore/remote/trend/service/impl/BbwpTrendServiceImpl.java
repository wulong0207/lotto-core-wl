package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.area.dao.BbwpDaoMapper;
import com.hhly.lottocore.remote.trend.service.IBbwpTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.BbwpTrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 
 * @desc 快乐12
 * @author chenghougui
 * @Date 2018年1月19日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("bbwpTrendService")
public class BbwpTrendServiceImpl extends HighTrendService2Impl implements IBbwpTrendService {
	
	@Autowired
	private BbwpDaoMapper bbwpDaoMapper;
	
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
		List<TrendBaseBO> trendList = bbwpDaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		List<NumTimeVo> coldHotList = dealWithColdHotData((BbwpTrendBaseBO)base,"p",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new ColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_6);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_6, Constants.NUM_12);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}
	
	private List<NumTimeVo> dealWithColdHotData(BbwpTrendBaseBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_50);
		List<String> numList = Arrays.asList("A","2","3","4","5","6","7","8","9","10","J","Q","K");
		NumTimeVo vo = null;
		for (int i = 0; i <= count; i++) {
			vo = new NumTimeVo();
			vo.setCode(numList.get(i));
			vo.setTime(ClassUtil.getField(baseTrend, preField + numList.get(i), Integer.class));
			list.add(vo);
		}
		return list;
	}
}
