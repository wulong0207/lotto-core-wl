package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.area.dao.KzcDaoMapper;
import com.hhly.lottocore.remote.trend.service.IKzcTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.KzcTrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 
 * @desc 快赢481
 * @author chenghougui
 * @Date 2018年1月19日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("kzcTrendService")
public class KzcTrendServiceImpl extends HighTrendService2Impl implements IKzcTrendService {
	
	@Autowired
	private KzcDaoMapper kzcDaoMapper;
	
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
		List<TrendBaseBO> trendList = kzcDaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO base = totalMap.get("occTimes");
		OmitTrendUtil.assemble(base);
		List<NumTimeVo> coldHotList = dealWithColdHotData((KzcTrendBaseBO)base,"g",Constants.NUM_20);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new ColdHotOmitBo();
		//SubList 没有实现序列化
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_8);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_12, Constants.NUM_20);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		//特别号码
		coldHotList = dealWithColdHotData((KzcTrendBaseBO)base,"s",Constants.NUM_4);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		List<NumTimeVo> coldOtherList = coldHotList.subList(Constants.NUM_0, Constants.NUM_1);
		target.setColdOtherList(new ArrayList<>(coldOtherList));
		List<NumTimeVo> hotOtherList = coldHotList.subList(Constants.NUM_3, Constants.NUM_4);
		target.setHotOtherList(new ArrayList<>(hotOtherList));
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}
	
	private List<NumTimeVo> dealWithColdHotData(KzcTrendBaseBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(20);
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
