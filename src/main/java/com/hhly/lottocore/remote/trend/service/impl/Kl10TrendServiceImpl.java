package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.high.dao.Kl10DaoMapper;
import com.hhly.lottocore.remote.trend.service.IKl10TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.SscColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.Kl10TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 快乐10分走势 
 * @desc 
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("kl10TrendService")
public class Kl10TrendServiceImpl extends HighTrendService2Impl implements IKl10TrendService {
	
	@Autowired
	private Kl10DaoMapper kl10DaoMapper;
	
	/*****************************快乐10分走势图数据接口 *********************************/
	
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
		ColdHotOmitBo target = (SscColdHotOmitBo) redisUtil.getObj(key);
		if(target!=null){
			return new ResultBO<>(target);
		}
		List<TrendBaseBO> trendList = kl10DaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO bo = totalMap.get("occTimes");
		OmitTrendUtil.assemble(bo);
		List<NumTimeVo> coldHotList = dealWithColdHotData((Kl10TrendBaseBO)bo,"b",Constants.NUM_20);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new SscColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_8);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_12, Constants.NUM_20);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		/**今天，昨天，前天**/
		
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
		
	}
	
	private List<NumTimeVo> dealWithColdHotData(Kl10TrendBaseBO baseTrend, String preField,int count) {
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
