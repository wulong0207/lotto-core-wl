package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.area.dao.XyscDaoMapper;
import com.hhly.lottocore.remote.trend.service.IXyscTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.bo.XyscColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.high.bo.XyscTrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 
 * @desc 幸运赛车
 * @author chenghougui
 * @Date 2018年1月19日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("xyscTrendService")
public class XyscTrendServiceImpl extends HighTrendService2Impl implements IXyscTrendService {
	
	@Autowired
	private XyscDaoMapper xyscDaoMapper;
	
	/*****************************走势图数据接口 *********************************/
	
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
		XyscColdHotOmitBo target = (XyscColdHotOmitBo) redisUtil.getObj(key);
		if(target!=null){
			return new ResultBO<ColdHotOmitBo>(target);
		}
		List<TrendBaseBO> trendList = xyscDaoMapper.findBaseTrend(vo);
		//拆分成冠亚军
		List<TrendBaseBO> goldList=new ArrayList<>();
		List<TrendBaseBO> silverList=new ArrayList<>();
		List<TrendBaseBO> copperList=new ArrayList<>();
		int btype = 0;
		for (TrendBaseBO trendBaseBO : trendList) {
			XyscTrendBaseBO bo = (XyscTrendBaseBO) trendBaseBO;
			if(bo==null){
				continue;
			}
			btype = bo.getBtype();
			if(btype==1){
				goldList.add(bo);
			}else if(btype==2){
				silverList.add(bo);
			}else if(btype==3){
				copperList.add(bo);
			}	
		}
		Map<String, TrendBaseBO> totalMap = null;
		//冠军
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(goldList, goldList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		List<NumTimeVo> coldHotList = dealWithColdHotData((XyscTrendBaseBO)base,"b",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		ColdHotOmitBo gold =new ColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_2);
		gold.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_10, Constants.NUM_12);
		Collections.reverse(hotList);
		gold.setHotBaseList(new ArrayList<>(hotList));
		//亚军
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(silverList, silverList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		coldHotList = dealWithColdHotData((XyscTrendBaseBO)base,"b",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		ColdHotOmitBo silver =new ColdHotOmitBo();
		coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_2);
		silver.setColdBaseList(new ArrayList<>(coldList));
		hotList = coldHotList.subList(Constants.NUM_10, Constants.NUM_12);
		Collections.reverse(hotList);
		silver.setHotBaseList(new ArrayList<>(hotList));
		//季军
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(copperList, silverList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		coldHotList = dealWithColdHotData((XyscTrendBaseBO)base,"b",Constants.NUM_12);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		ColdHotOmitBo copper =new ColdHotOmitBo();
		coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_2);
		copper.setColdBaseList(new ArrayList<>(coldList));
		hotList = coldHotList.subList(Constants.NUM_10, Constants.NUM_12);
		Collections.reverse(hotList);
		copper.setHotBaseList(new ArrayList<>(hotList));
		// 设置缓存
		target = new XyscColdHotOmitBo();
		target.setChampion(gold);
		target.setSilver(silver);
		target.setThird(copper);
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}
	
	private List<NumTimeVo> dealWithColdHotData(XyscTrendBaseBO baseTrend, String preField,int count) {
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
