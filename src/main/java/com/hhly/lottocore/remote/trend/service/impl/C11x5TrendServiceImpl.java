package com.hhly.lottocore.remote.trend.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.high.dao.HighLotteryDaoMapper;
import com.hhly.lottocore.remote.trend.service.IC11x5TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TreadStatistics;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseArrayBO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.bo.X115ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitDataBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.X115BaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.X115TrendBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.c11x5.C11x5TrendBetBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.c11x5.C11x5TrendBetHotchBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighOmitBetVO;

/**
 * 11选5走势 
 * @desc 
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("c11x5TrendService")
public class C11x5TrendServiceImpl extends HighTrendService2Impl implements IC11x5TrendService {
	
	private static final Integer RX = 1;
	private static final Integer Q1 = 2;
	private static final Integer Q2ZX = 5;
	private static final Integer Q3ZX = 6;
	@Override
	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findRecentOmit(T recentVO) {
		logger.debug("高频彩查询近期遗漏数据,彩种:"+recentVO.getLotteryCode());
		if(recentVO.getQryCount() < 0 || recentVO.getQryCount() > HighConstants.QRY_MAX_ISSUE_COUNT) {
			recentVO.setQryCount(HighConstants.QRY_DEFAULT_ISSUE_COUNT);
		}
		String key = CacheConstants.N_CORE_LOTTO_OMIT+recentVO.getLotteryCode()+"_recent_"+recentVO.getQryFlag()+recentVO.getQryCount();
		if(recentVO.getOmitTypes()!=null){
			for(Integer type:recentVO.getOmitTypes()){
				key+=type;
			}
			
		}
		if(recentVO.getSubPlays()!=null){
			for(Integer type:recentVO.getSubPlays()){
				key+=type;
			}	
		}		
		@SuppressWarnings("unchecked")
		List<HighOmitBaseBO> recentOmit2 =(List<HighOmitBaseBO>)redisUtil.getObj(key);		
		if(recentOmit2==null){
			// 查出来的list是按issue desc排序的
			List<HighOmitBaseBO> recentOmit = getLotteryDaoMapper(recentVO).findRecentOmit(recentVO);
			 recentOmit2 = new ArrayList<>();
			 // 要添加子玩法信息，给前端做区别，以便把不同子玩法的数据渲染到页面中的不同位置
			for(Integer subPlay : recentVO.getSubPlays()) {
				HighOmitBaseBO subPlayRecentOmit = new HighOmitBaseBO(subPlay.toString(), new ArrayList<HighOmitBaseBO>());
				recentOmit2.add(subPlayRecentOmit);
			}
			for(HighOmitBaseBO omit : recentOmit) {
				for(HighOmitBaseBO subPlayRecentOmit : recentOmit2) {
					// 把从数据库查询出来的数据按不同的子玩法分区
					if(Objects.equals(subPlayRecentOmit.getSubPlay(), omit.getSubPlay())) {
						// 保持倒序
						subPlayRecentOmit.getHistory().add(0, omit);
						break;
					}
				}
			}
			//判断获取的遗憾是否最新期，不是最新期则更新缓存
			CurrentAndPreIssueBO issueBo = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(recentVO.getLotteryCode()).getData();	
			if(issueBo!=null&&recentOmit2!=null&&recentOmit2.size()>0){
				if(Objects.equals(issueBo.getPreIssue(), recentOmit2.get(0).getIssue())){
					redisUtil.addObj(key, recentOmit2, (long)Constants.NUM_600);
				}
			}
		}				
		return ResultBO.ok(new HighOmitDataBO(null, recentOmit2));
	}
	
	
	
	
	
	/*****************************11x5走势图数据接口 *********************************/

	
	
	/**
	 * 基本走势
	 */	
	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo) {
		logger.debug("高频彩查询基本走势,彩种:{}",vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(), vo.getStartIssue()==null?"0":vo.getStartIssue(), vo.getEndIssue()==null?"0":vo.getEndIssue(),vo.getQryCount(),"base");
			@SuppressWarnings("unchecked")
			List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
			if(target!=null && target.size()>0){
				return ResultBO.ok(target);
			}
			HighLotteryDaoMapper dao = getLotteryDaoMapper(lotteryVo);
			target = dao.findBaseTrend(vo);
			// deal with data
			List<TrendBaseBO> result = new ArrayList<>();
			for (TrendBaseBO trendBaseBO : target) {
				TrendBaseArrayBO bo = new TrendBaseArrayBO();
				bo.setIssue(trendBaseBO.getIssue());
				bo.setDrawCode(trendBaseBO.getDrawCode());
				bo.setBlueList(genBaseDigits((X115BaseBO)trendBaseBO,"b",Constants.NUM_11));
				result.add(bo);
			}
			redisUtil.addObj(key, result, (long)Constants.DAY_1);
			return ResultBO.ok(result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ResultBO.ok(null);
	}
	
	
	private List<Integer> genBaseDigits(X115BaseBO baseTrend, String preField,int count) {
		List<Integer> tmp = new ArrayList<>();
		boolean flag = false;
		String sufField = ""; // 字段后缀
		for (int i = 1; i <= count; i++) {
			sufField = (i >= 1 && i <= 9) ? "0" + i : String.valueOf(i); // 1~9的数字补0
			Integer field = ClassUtil.getField(baseTrend, preField + sufField, Integer.class);
			if(field!=null){
				flag = true;	
				tmp.add(field);
			}else{
				tmp.add(0);
			}
		}
		if(flag){
			return tmp;
		}
		return Collections.emptyList();
	}

	
	/**
	 * 遗漏投注
	 */
	public ResultBO<List<TreadStatistics>> findOmitBet(HighOmitBetVO omitVO){
		logger.debug("高频彩查询近期遗漏投注,彩种:"+omitVO.getLotteryCode());
		List<TreadStatistics> omitBet = null;
		boolean flag = false;
		for (String num : omitVO.getFatypes()) {
			if(!num.startsWith("rx")){
				flag = true;
			}
		}
		if(flag){
			//如果是前选
			omitBet = x115DaoMapper.findOmitBetOfQT(omitVO);	
		}else{
			//如果是任选
			omitBet = x115DaoMapper.findOmitBetOfLX(omitVO);
		}
		return new ResultBO<List<TreadStatistics>>(omitBet);
	}
	

	/**
	 * 走势投注  包括 任选,直选,组选
	 * @desc 
	 * @create 2018年3月30日
	 * @param vo
	 * @return ResultBO<TrendBaseBO>
	 */
	@Override
	public ResultBO<TrendBaseBO> findTrendBetting(LotteryTrendVO vo){
		logger.debug("高频彩查询走势,彩种:{}", vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			//切期的时候会进行清除，所以不担心过期的问题
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(),
					vo.getStartIssue() == null ? "0" : vo.getStartIssue(),
					vo.getEndIssue() == null ? "0" : vo.getEndIssue(), vo.getQryCount(), "trend_bet");
			C11x5TrendBetHotchBO target = (C11x5TrendBetHotchBO) redisUtil.getObj(key);
			if (target != null) {
				return ResultBO.ok(target);
			}
			
			/**
			 * 1.查询任选，前一，前二，前三的号码遗漏以及相应的大小奇偶
			 * 2.根据号码遗漏，统计出统计数据
			 * 3.查询100期的任选，前一，前二组选，前三组选 统计出近30,50,100期的冷热数据
			 * 目前就做任选，前选遗漏
			 */
			//任选1  万位2 千位3 百位4 前二组选5 前三组选6
			target = new C11x5TrendBetHotchBO();
			// 任选
			vo.setBtype(RX);
			dealwithDiffType(lotteryVo, vo, target);
			//前选
			vo.setBtype(Q1);
			dealwithDiffType(lotteryVo, vo, target);
			//前二组选
			vo.setBtype(Q2ZX);
			dealwithDiffType(lotteryVo, vo, target);
			//前三组选
			vo.setBtype(Q3ZX);
			dealwithDiffType(lotteryVo, vo, target);
			redisUtil.addObj(key, target, (long)Constants.DAY_1);
			return ResultBO.ok(target);
		} catch (Exception e) {
			logger.error("彩种：{},查询走势发生异常");
			e.printStackTrace();
		}
		return ResultBO.<TrendBaseBO>err(new TrendBaseBO());
	}
	
	
	/**
	 * 处理各种不同玩法
	 * @desc 
	 * @create 2018年3月31日
	 * @param trendList
	 * @param btype void
	 * @throws Exception 
	 */
	private C11x5TrendBetHotchBO dealwithDiffType(HighLotteryVO lotteryVo,LotteryTrendVO vo,C11x5TrendBetHotchBO bo) throws Exception{
		if(vo==null || vo.getBtype()==null){
			throw new Exception();
		}
		Integer btype = vo.getBtype();
		if(bo==null){
			bo = new C11x5TrendBetHotchBO();
		}
		List<C11x5TrendBetBO> codeList = new ArrayList<>();
		//对于直选的玩法，特殊处理下
		List<TrendBaseBO> trendList = getLotteryDaoMapper(lotteryVo).findRecentTrend(vo);
		for (TrendBaseBO trendBaseBO : trendList) {
			X115BaseBO x115Bo = (X115BaseBO) trendBaseBO;
			List<Integer> code = genBaseDigits(x115Bo, "b", Constants.NUM_11);
			//如果没有开奖号码，则肯定没有遗漏统计，给前端返回空
			codeList.add(new C11x5TrendBetBO(x115Bo.getIssue(),x115Bo.getDrawCode()==null?"":x115Bo.getDrawCode(),code));
			
		}
		//统计信息
		Map<String, TrendBaseBO> trendMap = OmitTrendUtil.getTrendTotalInfoWithinLC(trendList, trendList.get(0).getClass());
		X115BaseBO occTimes = (X115BaseBO) trendMap.get("occTimes");
		X115BaseBO avgMiss = (X115BaseBO) trendMap.get("avgMiss");
		X115BaseBO maxMiss = (X115BaseBO) trendMap.get("maxMiss");
		X115BaseBO maxCont = (X115BaseBO) trendMap.get("maxCont");
		switch (btype) {
		//任选
		case Constants.NUM_1:
			bo.setBaseData(codeList);
			bo.setRxOccTimesSummy(genBaseDigits(occTimes, "b", Constants.NUM_11));
			bo.setRxAvgMissSummy(genBaseDigits(avgMiss, "b", Constants.NUM_11));
			bo.setRxMaxMissSummy(genBaseDigits(maxMiss, "b", Constants.NUM_11));
			bo.setRxMaxContSummy(genBaseDigits(maxCont, "b", Constants.NUM_11));
			break;
		//直选	
		case  Constants.NUM_2:
			//万
			List<Integer> occTimesList = genBaseDigits(occTimes, "b", Constants.NUM_11);
			List<Integer> avgMissList = genBaseDigits(avgMiss, "b", Constants.NUM_11);
			List<Integer> maxMissList = genBaseDigits(maxMiss, "b", Constants.NUM_11);
			List<Integer> maxContList = genBaseDigits(maxCont, "b", Constants.NUM_11);
			//千位
			vo.setBtype(3);
			trendList = getLotteryDaoMapper(lotteryVo).findRecentTrend(vo);
			int i = 0;
			for (TrendBaseBO trendBaseBO : trendList) {
				X115BaseBO x115Bo = (X115BaseBO) trendBaseBO;
				List<Integer> code = genBaseDigits(x115Bo, "b", Constants.NUM_11);
				//将
				codeList.get(i++).getCode5Digits().addAll(code);
//				codeList.add(new C11x5TrendBetBO(x115Bo.getIssue(),x115Bo.getDrawCode(),code));
			}
			//统计信息
			trendMap = OmitTrendUtil.getTrendTotalInfoWithinLC(trendList, trendList.get(0).getClass());
			occTimes = (X115BaseBO) trendMap.get("occTimes");
			avgMiss = (X115BaseBO) trendMap.get("avgMiss");
			maxMiss = (X115BaseBO) trendMap.get("maxMiss");
			maxCont = (X115BaseBO) trendMap.get("maxCont");
			occTimesList.addAll(genBaseDigits(occTimes, "b", Constants.NUM_11));
			avgMissList.addAll(genBaseDigits(avgMiss, "b", Constants.NUM_11));
			maxMissList.addAll(genBaseDigits(maxMiss, "b", Constants.NUM_11));
			maxContList.addAll(genBaseDigits(maxCont, "b", Constants.NUM_11));
			//百位
			vo.setBtype(4);
			trendList = getLotteryDaoMapper(lotteryVo).findRecentTrend(vo);
			i = 0;
			for (TrendBaseBO trendBaseBO : trendList) {
				X115BaseBO x115Bo = (X115BaseBO) trendBaseBO;
				List<Integer> code = genBaseDigits(x115Bo, "b", Constants.NUM_11);
				codeList.get(i++).getCode5Digits().addAll(code);
//				codeList.add(new C11x5TrendBetBO(x115Bo.getIssue(),x115Bo.getDrawCode(),code));
			}
			//统计信息
			trendMap = OmitTrendUtil.getTrendTotalInfoWithinLC(trendList, trendList.get(0).getClass());
			occTimes = (X115BaseBO) trendMap.get("occTimes");
			avgMiss = (X115BaseBO) trendMap.get("avgMiss");
			maxMiss = (X115BaseBO) trendMap.get("maxMiss");
			maxCont = (X115BaseBO) trendMap.get("maxCont");
			occTimesList.addAll(genBaseDigits(occTimes, "b", Constants.NUM_11));
			avgMissList.addAll(genBaseDigits(avgMiss, "b", Constants.NUM_11));
			maxMissList.addAll(genBaseDigits(maxMiss, "b", Constants.NUM_11));
			maxContList.addAll(genBaseDigits(maxCont, "b", Constants.NUM_11));
			bo.setQxOccTimesSummy(occTimesList);
			bo.setQxAvgMissSummy(avgMissList);
			bo.setQxMaxMissSummy(maxMissList);
			bo.setQxMaxContSummy(maxContList);
			bo.setQxBaseData(codeList);
		//前二组选	
		case  Constants.NUM_5:
			bo.setQ2BaseData(codeList);
			bo.setQ2OccTimesSummy(genBaseDigits(occTimes, "b", Constants.NUM_11));
			bo.setQ2AvgMissSummy(genBaseDigits(avgMiss, "b", Constants.NUM_11));
			bo.setQ2MaxMissSummy(genBaseDigits(maxMiss, "b", Constants.NUM_11));
			bo.setQ2MaxContSummy(genBaseDigits(maxCont, "b", Constants.NUM_11));
			break;
		//前三组选	
		case  Constants.NUM_6:
			bo.setQ3BaseData(codeList);
			bo.setQ3OccTimesSummy(genBaseDigits(occTimes, "b", Constants.NUM_11));
			bo.setQ3AvgMissSummy(genBaseDigits(avgMiss, "b", Constants.NUM_11));
			bo.setQ3MaxMissSummy(genBaseDigits(maxMiss, "b", Constants.NUM_11));
			bo.setQ3MaxContSummy(genBaseDigits(maxCont, "b", Constants.NUM_11));
			break;
		default:
			break;
		}
		return bo;
	}
	
		
	/*****************************11x5开奖冷热/遗漏数据接口 *********************************/
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
		//冷热数据
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_coldhot_" + param.getIssueCode() + param.getQryFlag() + param.getQryCount();
		X115ColdHotOmitBo target = (X115ColdHotOmitBo) redisUtil.getObj(key);
		if(target!=null){
			return new ResultBO<ColdHotOmitBo>(target);
		}
		HighLotteryVO vo = new HighLotteryVO();
		vo.setSubPlays(Arrays.asList(1));
		vo.setOmitTypes(Arrays.asList(1));
		vo.setLotteryCode(param.getLotteryCode());
		List<TrendBaseBO> trendList = x115DaoMapper.findTrendRangeFront(param);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO bo = totalMap.get("occTimes");
		OmitTrendUtil.assemble(bo);
		List<NumTimeVo> coldHotList = dealWithColdHotData((X115TrendBO)bo,"b",11);
		Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		});
		target =new X115ColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_5, Constants.NUM_10);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		/**遗漏数据**/
		HighOmitBetVO omitVO = new HighOmitBetVO();
		omitVO.setDesc(true);
		omitVO.setLotteryCode(param.getLotteryCode());
		omitVO.setSortField("lastomit");
		//前三
		omitVO.setFatypes(Arrays.asList("qx3"));
		omitVO.setSontypes(Arrays.asList("ds"));
		List<TreadStatistics> qx3Statistics = x115DaoMapper.findOmitBetOfQT(omitVO);
		target.setQx3List(dealWithOmit(qx3Statistics.subList(Constants.NUM_0, Constants.NUM_5)));
		//任5
		omitVO.setFatypes(Arrays.asList("rx5"));
		omitVO.setSontypes(Arrays.asList("m5"));
		List<TreadStatistics> r5Statistics = x115DaoMapper.findOmitBetOfLX(omitVO);
		target.setR5List(dealWithOmit(r5Statistics.subList(Constants.NUM_0, Constants.NUM_5)));
		//任7
		omitVO.setFatypes(Arrays.asList("rx7"));
		omitVO.setSontypes(Arrays.asList("m7"));
		List<TreadStatistics> r7Statistics = x115DaoMapper.findOmitBetOfLX(omitVO);
		target.setR7List(dealWithOmit(r7Statistics.subList(Constants.NUM_0, Constants.NUM_5)));
		//任8
		omitVO.setFatypes(Arrays.asList("rx8"));
		omitVO.setSontypes(Arrays.asList("m8"));
		List<TreadStatistics> r8Statistics = x115DaoMapper.findOmitBetOfLX(omitVO);
		target.setR8List(dealWithOmit(r8Statistics.subList(Constants.NUM_0, Constants.NUM_5)));
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}

	public List<NumTimeVo> dealWithOmit(List<TreadStatistics> list){
		List<NumTimeVo> timeList = new ArrayList<>();
		if(list==null){
			return timeList;
		}
		NumTimeVo vo = null;
		for (TreadStatistics treadStatistics : list) {
			vo = new NumTimeVo();
			vo.setCode(treadStatistics.getYlCode());
			vo.setTime(treadStatistics.getLastOmit());
			timeList.add(vo);
		}
		return timeList;
	}
	
	
	//处理冷热数据
	private List<NumTimeVo> dealWithColdHotData(X115TrendBO baseTrend, String preField,int count) {
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
	
}
