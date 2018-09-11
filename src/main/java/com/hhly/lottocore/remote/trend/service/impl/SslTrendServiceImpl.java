package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.area.dao.SslDaoMapper;
import com.hhly.lottocore.remote.trend.service.ISslTrendService;
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
import com.hhly.skeleton.lotto.base.trend.high.bo.SslTrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * 
 * @desc 时时乐
 * @author chenghougui
 * @Date 2018年1月19日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("sslTrendService")
public class SslTrendServiceImpl extends HighTrendService2Impl implements ISslTrendService {
	
	@Autowired
	private SslDaoMapper sslDaoMapper;
	
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
		/** 冷热数据 **/
		LotteryTrendVO vo = new LotteryTrendVO();
		vo.setLotteryCode(param.getLotteryCode());
		vo.setQryCount(param.getQryCount());
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_coldhot_"
				+ param.getIssueCode() + param.getQryFlag() + param.getQryCount();
		SscColdHotOmitBo target = (SscColdHotOmitBo) redisUtil.getObj(key);
//		if(target!=null){
//			return new ResultBO<ColdHotOmitBo>(target);
//		}
		List<TrendBaseBO> trendList = sslDaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO bo = totalMap.get("occTimes");
		OmitTrendUtil.assemble(bo);
		List<NumTimeVo> coldHotList = dealWithColdHotData((SslTrendBaseBO) bo, "b", Constants.NUM_9);
		Collections.sort(coldHotList, new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime() - o2.getTime();
			}
		});
		target = new SscColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_3);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_7, Constants.NUM_10);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		/** 今天，昨天，前天 **/
		List<NumTimeVo> today  = new ArrayList<>();;
		List<NumTimeVo> yesterday = new ArrayList<>();;
		List<NumTimeVo> before = new ArrayList<>();;
		// 今天
		vo.setQryCount(null);
		String convertDateToStr = DateUtil.convertDateToStr(new Date(), DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		trendList = sslDaoMapper.findBaseTrend(vo);
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SslTrendBaseBO trendBaseBO = (SslTrendBaseBO) trendTotalInfo.get("occTimes");
			today.add(new NumTimeVo("z3", trendBaseBO.getZ3()));
			today.add(new NumTimeVo("z6", trendBaseBO.getZ6()));
			today.add(new NumTimeVo("bz", trendBaseBO.getBz()));
			today.add(new NumTimeVo("h2dz", trendBaseBO.getH2dz()));
			today.add(new NumTimeVo("h2lh", trendBaseBO.getH2lh()));
			today.add(new NumTimeVo("z3lc", dealWithLC(trendList,"z3")));
			today.add(new NumTimeVo("z6lc", dealWithLC(trendList,"z6")));
	
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有今天的遗漏数据");
		}
		// 昨天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-1, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		// 最大追期数是两天，所以取一半
		vo.setEndIssue(convertDateToStr + HighConstants.SHSSL_MAX_CHASE / 2);
		trendList = sscDaoMapper.findBaseTrend(vo);
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SslTrendBaseBO trendBaseBO = (SslTrendBaseBO) trendTotalInfo.get("occTimes");
			yesterday.add(new NumTimeVo("z3", trendBaseBO.getZ3()));
			yesterday.add(new NumTimeVo("z6", trendBaseBO.getZ6()));
			yesterday.add(new NumTimeVo("bz", trendBaseBO.getBz()));
			yesterday.add(new NumTimeVo("h2dz", trendBaseBO.getH2dz()));
			yesterday.add(new NumTimeVo("h2lh", trendBaseBO.getH2lh()));
			yesterday.add(new NumTimeVo("z3lc", dealWithLC(trendList,"z3")));
			yesterday.add(new NumTimeVo("z6lc", dealWithLC(trendList,"z6")));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有昨天的遗漏数据");
		}
		// 前天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-2, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		vo.setEndIssue(convertDateToStr + HighConstants.SHSSL_MAX_CHASE / 2);
		trendList = sscDaoMapper.findBaseTrend(vo);
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SslTrendBaseBO trendBaseBO = (SslTrendBaseBO) trendTotalInfo.get("occTimes");
			before.add(new NumTimeVo("z3", trendBaseBO.getZ3()));
			before.add(new NumTimeVo("z6", trendBaseBO.getZ6()));
			before.add(new NumTimeVo("bz", trendBaseBO.getBz()));
			before.add(new NumTimeVo("h2dz", trendBaseBO.getH2dz()));
			before.add(new NumTimeVo("h2lh", trendBaseBO.getH2lh()));
			before.add(new NumTimeVo("z3lc", dealWithLC(trendList,"z3")));
			before.add(new NumTimeVo("z6lc", dealWithLC(trendList,"z6")));
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
	
	// 统计最大连出,彩期必须是连续的
	private int dealWithLC(List<TrendBaseBO> trendList,String attr) {
		int curCount = 0;
		int maxCount = 0;
		for (TrendBaseBO baseBo : trendList) {
			SslTrendBaseBO bo = (SslTrendBaseBO) baseBo;
			Integer val = 0;
			//三星组三连出
			if("z3".equals(attr)){
				val = bo.getZ3()==null?0:bo.getZ3();
			}
			if("z6".equals(attr)){
				val = bo.getZ6()==null?0:bo.getZ6();
			}
			if (val == 0) {
				curCount++;
				if(maxCount<curCount){
					maxCount = curCount;
				}
			} else {
				curCount = 0;
			}
		}
		return maxCount;
	}
	
	private List<NumTimeVo> dealWithColdHotData(SslTrendBaseBO baseTrend, String preField,int count) {
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
