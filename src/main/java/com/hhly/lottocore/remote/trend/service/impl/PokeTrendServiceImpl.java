package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.high.dao.PokerDaoMapper;
import com.hhly.lottocore.remote.trend.service.IPokeTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.SscColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.PokerTrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 快乐10分走势 
 * @desc 
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("pokeTrendService")
public class PokeTrendServiceImpl extends HighTrendService2Impl implements IPokeTrendService {
	
	@Autowired
	private PokerDaoMapper pokeDaoMapper;
	
	/*****************************快乐扑克走势图数据接口 *********************************/
	
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
		SscColdHotOmitBo target = (SscColdHotOmitBo) redisUtil.getObj(key);
		if(target!=null){
			return new ResultBO<ColdHotOmitBo>(target);
		}
	
		List<TrendBaseBO> trendList = pokeDaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO bo = totalMap.get("occTimes");
		OmitTrendUtil.assemble(bo);

		List<NumTimeVo> coldHotList = dealWithColdHotData((PokerTrendBaseBO)bo,"p",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new SscColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_3);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_10, Constants.NUM_13);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		/**今天，昨天，前天**/
		//今天
		vo.setQryCount(null);
		String convertDateToStr = DateUtil.convertDateToStr(new Date(),DATE_FORMAT_YYMMDD);
		vo.setStartIssue(convertDateToStr+"01");
		trendList = pokeDaoMapper.findBaseTrend(vo);
		List<NumTimeVo> today = new ArrayList<>();
		List<NumTimeVo> yesterday = new ArrayList<>();
		List<NumTimeVo> before = new ArrayList<>();
		//同化顺
		int ths_b =0;
		int ths_r =0;
		int ths_m =0;
		int ths_f =0;
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			PokerTrendBaseBO trendBaseBO = (PokerTrendBaseBO) trendTotalInfo.get("occTimes");
			today.add(new NumTimeVo("th_b",trendBaseBO.getPt1()));
			today.add(new NumTimeVo("th_r",trendBaseBO.getPt2()));
			today.add(new NumTimeVo("th_m",trendBaseBO.getPt3()));
			today.add(new NumTimeVo("th_f",trendBaseBO.getPt4()));
			today.add(new NumTimeVo("bz",trendBaseBO.getPb()));
			today.add(new NumTimeVo("sz",trendBaseBO.getPx()));
			today.add(new NumTimeVo("dz",trendBaseBO.getPd()));
			for (TrendBaseBO baseBO : trendList) {
				PokerTrendBaseBO poke = (PokerTrendBaseBO) baseBO;
				if(poke.getType()==1){
					if(poke.getPt1()==0){
						ths_b++;
					}
					if(poke.getPt2()==0){
						ths_r++;
					}
					if(poke.getPt3()==0){
						ths_m++;
					}
					if(poke.getPt4()==0){
						ths_f++;
					}
					
				}
			}
		today.add(new NumTimeVo("ths_b",ths_b));
		today.add(new NumTimeVo("ths_r",ths_r));
		today.add(new NumTimeVo("ths_m",ths_m));
		today.add(new NumTimeVo("ths_f",ths_f));

		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有今天的遗漏数据");
		}
		//昨天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-1, DATE_FORMAT_YYMMDD);
		vo.setStartIssue(convertDateToStr+"01");
		   //最大追期数是两天，所以取一半
		vo.setEndIssue(convertDateToStr+HighConstants.SDPOKER_MAX_CHASE/2);
		trendList = pokeDaoMapper.findBaseTrend(vo);
		ths_b =0;
		ths_r =0;
		ths_m =0;
		ths_f =0;
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			PokerTrendBaseBO trendBaseBO = (PokerTrendBaseBO) trendTotalInfo.get("occTimes");
			yesterday.add(new NumTimeVo("th_b",trendBaseBO.getPt1()));
			yesterday.add(new NumTimeVo("th_r",trendBaseBO.getPt2()));
			yesterday.add(new NumTimeVo("th_m",trendBaseBO.getPt3()));
			yesterday.add(new NumTimeVo("th_f",trendBaseBO.getPt4()));
			yesterday.add(new NumTimeVo("bz",trendBaseBO.getPb()));
			yesterday.add(new NumTimeVo("sz",trendBaseBO.getPx()));
			yesterday.add(new NumTimeVo("dz",trendBaseBO.getPd()));
			for (TrendBaseBO baseBO : trendList) {
				PokerTrendBaseBO poke = (PokerTrendBaseBO) baseBO;
				if(poke.getType()==1){
					if(poke.getPt1()==0){
						ths_b++;
					}
					if(poke.getPt2()==0){
						ths_r++;
					}
					if(poke.getPt3()==0){
						ths_m++;
					}
					if(poke.getPt4()==0){
						ths_f++;
					}
					
				}
			}
		yesterday.add(new NumTimeVo("ths_b",ths_b));
		yesterday.add(new NumTimeVo("ths_r",ths_r));
		yesterday.add(new NumTimeVo("ths_m",ths_m));
		yesterday.add(new NumTimeVo("ths_f",ths_f));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有昨天的遗漏数据");
		}
		
		//前天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-2, DATE_FORMAT_YYMMDD);
		vo.setStartIssue(convertDateToStr+"01");
		vo.setEndIssue(convertDateToStr+HighConstants.SDPOKER_MAX_CHASE/2);
		trendList = pokeDaoMapper.findBaseTrend(vo);
		ths_b =0;
		ths_r =0;
		ths_m =0;
		ths_f =0;
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			PokerTrendBaseBO trendBaseBO = (PokerTrendBaseBO) trendTotalInfo.get("occTimes");
			before.add(new NumTimeVo("th_b",trendBaseBO.getPt1()));
			before.add(new NumTimeVo("th_r",trendBaseBO.getPt2()));
			before.add(new NumTimeVo("th_m",trendBaseBO.getPt3()));
			before.add(new NumTimeVo("th_f",trendBaseBO.getPt4()));
			before.add(new NumTimeVo("bz",trendBaseBO.getPb()));
			before.add(new NumTimeVo("sz",trendBaseBO.getPx()));
			before.add(new NumTimeVo("dz",trendBaseBO.getPd()));
			for (TrendBaseBO baseBO : trendList) {
				PokerTrendBaseBO poke = (PokerTrendBaseBO) baseBO;
				if(poke.getType()==1){
					if(poke.getPt1()==0){
						ths_b++;
					}
					if(poke.getPt2()==0){
						ths_r++;
					}
					if(poke.getPt3()==0){
						ths_m++;
					}
					if(poke.getPt4()==0){
						ths_f++;
					}
					
				}
			}
		before.add(new NumTimeVo("ths_b",ths_b));
		before.add(new NumTimeVo("ths_r",ths_r));
		before.add(new NumTimeVo("ths_m",ths_m));
		before.add(new NumTimeVo("ths_f",ths_f));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有前天的遗漏数据");
		}
		target.setToday(today);
		target.setYesterday(yesterday);
		target.setBefore(before);
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
		
	}
	
	private List<NumTimeVo> dealWithColdHotData(PokerTrendBaseBO baseTrend, String preField,int count) {
		String[] arr = new String[]{"a","2","3","4","5","6","7","8","9","10","j","q","k"};
		List<NumTimeVo> list = new ArrayList<>();
		NumTimeVo vo = null;
		String sufField = "";
		for (int i = 0; i <= count; i++) {
			vo = new NumTimeVo();
			sufField = arr[i];
			vo.setCode(sufField);
			vo.setTime(ClassUtil.getField(baseTrend, preField + sufField, Integer.class));
			list.add(vo);
		}
		return list;
	}
}
