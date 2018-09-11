package com.hhly.lottocore.remote.trend.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.high.dao.HighLotteryDaoMapper;
import com.hhly.lottocore.remote.trend.service.ISscTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.SscColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseSscBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.SscBaseBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.ssc.SscTrendBetBO;
import com.hhly.skeleton.lotto.base.trend.high.bo.ssc.SscTrendBetHotchBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;

/**
 * 时时彩走势
 * 
 * @desc
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("sscTrendService")
public class SscTrendServiceImpl extends HighTrendService2Impl implements ISscTrendService {

	/***************************** 时时彩走势图数据接口 *********************************/

	/**
	 * 基本走势
	 */
	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo) {
		logger.debug("高频彩查询基本走势,彩种:{}", vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(),
					vo.getStartIssue() == null ? "0" : vo.getStartIssue(),
					vo.getEndIssue() == null ? "0" : vo.getEndIssue(), vo.getQryCount(), "base");
			@SuppressWarnings("unchecked")
			List<TrendBaseBO> target = (List<TrendBaseBO>) redisUtil.getObj(key);
			if (target != null && target.size() > 0) {
				return ResultBO.ok(target);
			}
			HighLotteryDaoMapper dao = getLotteryDaoMapper(lotteryVo);
			target = dao.findBaseTrend(vo);
			// deal with data
			List<TrendBaseBO> result = new ArrayList<>();
			for (TrendBaseBO trendBaseBO : target) {
				TrendBaseSscBO bo = new TrendBaseSscBO();
				bo.setIssue(trendBaseBO.getIssue());
				bo.setDrawCode(trendBaseBO.getDrawCode());
				// 基础号码
				bo.setBaseList(genBaseDigits((SscBaseBO) trendBaseBO, "b", Constants.NUM_9));
				bo.setWbList(genBaseDigits((SscBaseBO) trendBaseBO, "wb", Constants.NUM_9));
				bo.setQbList(genBaseDigits((SscBaseBO) trendBaseBO, "qb", Constants.NUM_9));
				bo.setBbList(genBaseDigits((SscBaseBO) trendBaseBO, "bb", Constants.NUM_9));
				bo.setSbList(genBaseDigits((SscBaseBO) trendBaseBO, "sb", Constants.NUM_9));
				bo.setGbList(genBaseDigits((SscBaseBO) trendBaseBO, "gb", Constants.NUM_9));
				result.add(bo);
			}
			redisUtil.addObj(key, result, (long) Constants.DAY_1);
			return ResultBO.ok(result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ResultBO.ok(null);
	}

	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
		/** 冷热数据 **/
		LotteryTrendVO vo = new LotteryTrendVO();
		vo.setLotteryCode(param.getLotteryCode());
		vo.setQryCount(param.getQryCount());
		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_coldhot_" + param.getIssueCode()
				+ param.getQryFlag() + param.getQryCount();
		SscColdHotOmitBo target = (SscColdHotOmitBo) redisUtil.getObj(key);
		if (target != null) {
			return new ResultBO<ColdHotOmitBo>(target);
		}
		List<TrendBaseBO> trendList = sscDaoMapper.findBaseTrend(vo);
		Map<String, TrendBaseBO> totalMap = null;
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TrendBaseBO bo = totalMap.get("occTimes");
		OmitTrendUtil.assemble(bo);
		List<NumTimeVo> coldHotList = dealWithColdHotData((SscBaseBO) bo, "b", Constants.NUM_9);
		Collections.sort(coldHotList, new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime() - o2.getTime();
			}
		});
		target = new SscColdHotOmitBo();
		List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
		target.setColdBaseList(new ArrayList<>(coldList));
		List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_5, Constants.NUM_10);
		Collections.reverse(hotList);
		target.setHotBaseList(new ArrayList<>(hotList));
		/** 今天，昨天，前天 **/
		// 今天
		vo.setQryCount(null);
		String convertDateToStr = DateUtil.convertDateToStr(new Date(), DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		trendList = sscDaoMapper.findBaseTrend(vo);
		List<NumTimeVo> today = null;
		List<NumTimeVo> yesterday = null;
		List<NumTimeVo> before = null;
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SscBaseBO trendBaseBO = (SscBaseBO) trendTotalInfo.get("occTimes");
			today = new ArrayList<>();
			today.add(new NumTimeVo("h3z3", trendBaseBO.getH3z3()));
			today.add(new NumTimeVo("h3z6", trendBaseBO.getH3z6()));
			today.add(new NumTimeVo("h3bz", trendBaseBO.getH3bz()));
			today.add(new NumTimeVo("2xdz", trendBaseBO.getH2dz()));
			today.add(new NumTimeVo("2xlh", trendBaseBO.getH2lh()));
			// 统计连出
			today.add(new NumTimeVo("h3z3lc", dealWithLC(trendList, "h3z3")));
			today.add(new NumTimeVo("h3z6lc", dealWithLC(trendList, "h3z6")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 昨天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-1, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		// 一个总期数不超过100
		vo.setEndIssue(convertDateToStr + Constants.NUM_100);
		trendList = sscDaoMapper.findBaseTrend(vo);
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SscBaseBO trendBaseBO = (SscBaseBO) trendTotalInfo.get("occTimes");
			yesterday = new ArrayList<>();
			yesterday.add(new NumTimeVo("h3z3", trendBaseBO.getH3z3()));
			yesterday.add(new NumTimeVo("h3z6", trendBaseBO.getH3z6()));
			yesterday.add(new NumTimeVo("h3bz", trendBaseBO.getH3bz()));
			yesterday.add(new NumTimeVo("2xdz", trendBaseBO.getH2dz()));
			yesterday.add(new NumTimeVo("2xlh", trendBaseBO.getH2lh()));
			yesterday.add(new NumTimeVo("h3z3lc", dealWithLC(trendList, "h3z3")));
			yesterday.add(new NumTimeVo("h3z6lc", dealWithLC(trendList, "h3z6")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 前天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-2, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr + "001");
		vo.setEndIssue(convertDateToStr + Constants.NUM_100);
		trendList = sscDaoMapper.findBaseTrend(vo);
		try {
			Map<String, TrendBaseBO> trendTotalInfo = OmitTrendUtil.getTrendTotalInfo(trendList,
					trendList.get(0).getClass());
			SscBaseBO trendBaseBO = (SscBaseBO) trendTotalInfo.get("occTimes");
			before = new ArrayList<>();
			before.add(new NumTimeVo("h3z3", trendBaseBO.getH3z3()));
			before.add(new NumTimeVo("h3z6", trendBaseBO.getH3z6()));
			before.add(new NumTimeVo("h3bz", trendBaseBO.getH3bz()));
			before.add(new NumTimeVo("2xdz", trendBaseBO.getH2dz()));
			before.add(new NumTimeVo("2xlh", trendBaseBO.getH2lh()));
			before.add(new NumTimeVo("h3z3lc", dealWithLC(trendList, "h3z3")));
			before.add(new NumTimeVo("h3z6lc", dealWithLC(trendList, "h3z6")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		target.setToday(today);
		target.setYesterday(yesterday);
		target.setBefore(before);
		// 设置缓存
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(target);
	}

	// 统计最大连出,彩期必须是连续的
	private int dealWithLC(List<TrendBaseBO> trendList, String attr) {
		int curCount = 0;
		int maxCount = 0;
		for (TrendBaseBO baseBo : trendList) {
			SscBaseBO bo = (SscBaseBO) baseBo;
			Integer val = 0;
			// 三星组三连出
			if ("h3z3".equals(attr)) {
				val = bo.getH3z3() == null ? 0 : bo.getH3z3();
			}
			if ("h3z6".equals(attr)) {
				val = bo.getH3z6() == null ? 0 : bo.getH3z6();
			}
			if (val == 0) {
				curCount++;
				if (maxCount < curCount) {
					maxCount = curCount;
				}
			} else {
				curCount = 0;
			}
		}
		return maxCount;
	}

	private List<NumTimeVo> dealWithColdHotData(SscBaseBO baseTrend, String preField, int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_50);
		NumTimeVo vo = null;
		for (int i = 0; i <= count; i++) {
			vo = new NumTimeVo();
			vo.setCode(i + "");
			vo.setTime(ClassUtil.getField(baseTrend, preField + i, Integer.class));
			list.add(vo);
		}
		return list;
	}

	// 不同于其他的，时时彩开始号码0,而且不需要补0
	private List<Integer> genBaseDigits(SscBaseBO baseTrend, String preField, int count) {
		List<Integer> tmp = new ArrayList<>();
		// String sufField = ""; // 字段后缀
		boolean flag = false;
		for (int i = 0; i <= count; i++) {
			// sufField = (i >= 1 && i <= 9) ? i : String.valueOf(i); //
			// 1~9的数字补0
			Integer field = ClassUtil.getField(baseTrend, preField + i, Integer.class);
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

	// ------------------------时时彩走势图-------------------------------

	/**
	 * 时时彩走势投注 1.五星直选,通选 2.三星 直选 ,组三，组六 3.二星号码投注 4.一星号码投注 5.大小单双
	 * 
	 * @desc
	 * @create 2018年3月27日
	 * @param resultVO
	 * @return ResultBO<HighOmitDataBO>
	 */
	@Override
	public ResultBO<TrendBaseBO> findTrendBetting(LotteryTrendVO vo) {
		logger.debug("高频彩查询走势,彩种:{}", vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(),
					vo.getStartIssue() == null ? "0" : vo.getStartIssue(),
					vo.getEndIssue() == null ? "0" : vo.getEndIssue(), vo.getQryCount(), "trend_bet");
			SscTrendBetHotchBO target = (SscTrendBetHotchBO) redisUtil.getObj(key);
			if (target != null) {
				return ResultBO.ok(target);
			}
			//基本数据 sscBaseBo
			List<TrendBaseBO> trendList = getLotteryDaoMapper(lotteryVo).findRecentTrend(vo);
			SscBaseBO sscBO = null;
			List<SscTrendBetBO> baseData = new ArrayList<>();
			for (TrendBaseBO trendBaseBO : trendList) {
				//分位号码
				sscBO = (SscBaseBO)trendBaseBO;
				SscTrendBetBO bo = calcEveryType(sscBO);
				baseData.add(bo);
			}
			//统计信息
			Map<String, TrendBaseBO> trendMap = OmitTrendUtil.getTrendTotalInfoWithinLC(trendList, trendList.get(0).getClass());
			SscBaseBO occTimes = (SscBaseBO) trendMap.get("occTimes");
			SscBaseBO avgMiss = (SscBaseBO) trendMap.get("avgMiss");
			SscBaseBO maxMiss = (SscBaseBO) trendMap.get("maxMiss");
			SscBaseBO maxCont = (SscBaseBO) trendMap.get("maxCont");
			
			SscTrendBetBO occTimesSummy = calcEveryType(occTimes);
			SscTrendBetBO avgMissSummy = calcEveryType(avgMiss);
			SscTrendBetBO maxMissSummy = calcEveryType(maxMiss);
			SscTrendBetBO maxContSummy = calcEveryType(maxCont);
			target = new SscTrendBetHotchBO(baseData,occTimesSummy,avgMissSummy,maxMissSummy,maxContSummy);
			redisUtil.addObj(key, target, (long)Constants.DAY_1);
			return ResultBO.ok(target);
		} catch (Exception e) {
			logger.error("彩种：{},查询走势发生异常");
			e.printStackTrace();
		}
		return ResultBO.err(new TrendBaseBO());
	}
	
	/**
	 * 计算不同类型
	 * @desc 
	 * @create 2018年3月29日
	 * @param sscBO
	 * @return SscTrendBetBO
	 */
	private SscTrendBetBO calcEveryType(SscBaseBO sscBO){
		String drawCode = sscBO.getDrawCode();
		String issue = sscBO.getIssue();
		List<Integer> code5Digits=new ArrayList<>(50);
		//不分位号码
		List<Integer> z2CodeList = genBaseDigits(sscBO,"groupTwo",Constants.NUM_9);
		List<Integer> z3CodeList = genBaseDigits(sscBO,"groupThree",Constants.NUM_9);
		List<Integer> gbBaseDigits = genBaseDigits(sscBO,"gb",Constants.NUM_9);
		List<Integer> sbnBaseDigits = genBaseDigits(sscBO,"sb",Constants.NUM_9);
		List<Integer> bbnBaseDigits = genBaseDigits(sscBO,"bb",Constants.NUM_9);
		List<Integer> qbnBaseDigits = genBaseDigits(sscBO,"qb",Constants.NUM_9);
		List<Integer> wbBaseDigits = genBaseDigits(sscBO,"wb",Constants.NUM_9);
		//万 千 百 十 个
		code5Digits.addAll(wbBaseDigits);
		code5Digits.addAll(qbnBaseDigits);
		code5Digits.addAll(bbnBaseDigits);
		code5Digits.addAll(sbnBaseDigits);
		code5Digits.addAll(gbBaseDigits);
		SscTrendBetBO data = new SscTrendBetBO();
		//组二拼上组三
		z2CodeList.addAll(z3CodeList);
		data.setBaseDigits(z2CodeList);
		data.setCode5Digits(code5Digits);
		data.setDrawCode(drawCode==null?"":drawCode);
		if(StringUtils.isNotBlank(issue)){
			data.setIssue(issue.substring(issue.length()-3, issue.length()));
		}
		//HitTimes  拆分成组二,组三 
		if(drawCode!=null){
			String z2DrawCode = drawCode.substring(6);
			String z3DrawCode = drawCode.substring(4);
			List<Integer> hitTimes = genHitTimes(z2DrawCode);
			hitTimes.addAll(genHitTimes(z3DrawCode));
			data.setHitTimes(hitTimes);
			//三星 类型走势    豹子 组三 组六
			Integer[] z3 = new Integer[]{sscBO.getH3bz(),sscBO.getH3z3(),sscBO.getH3z6()};
			//大小单双   个大小 个单双  十大小 十单双
			Integer[] bsoe = new Integer[]{sscBO.getGbig(),sscBO.getGsmall(),sscBO.getGodd(),sscBO.getGeven(),
										   sscBO.getSbig(),sscBO.getSsmall(),sscBO.getSodd(),sscBO.getSeven()};
			data.setBsoe(bsoe);
			data.setTypeDigits(z3);
			
		}
		return data;
	}
	//计算重复
	public List<Integer> genHitTimes(String drawCode) {
		if (ObjectUtil.isBlank(drawCode)) {
			return new ArrayList<>();
		}
		List<Integer> merges = new ArrayList<>();
		// 其实根据"|"就可以了
		List<Integer> drawCodes = StringUtil.toIntList(drawCode,
				SymbolConstants.COMMA + SymbolConstants.NUMBER_SIGN + SymbolConstants.VERTICAL_BAR);
		Map<Integer, Integer> drawCodeMap = new HashMap<Integer, Integer>();
		for (Integer tmp : drawCodes) {
			if (drawCodeMap.get(tmp) == null) {
				drawCodeMap.put(tmp, Constants.NUM_1);
				continue;
			}
			drawCodeMap.put(tmp, drawCodeMap.get(tmp) + Constants.NUM_1);
		}
		for (int i = 0; i <= Constants.NUM_9; i++) {
			merges.add(drawCodeMap.containsKey(i) ? drawCodeMap.get(i) : Constants.NUM_0);
		}
		return merges;
	}
}
