package com.hhly.lottocore.remote.trend.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.trend.high.dao.HighLotteryDaoMapper;
import com.hhly.lottocore.remote.trend.service.IK3TrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ClassUtil;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.trendutil.OmitTrendUtil;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.K3ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseK3BO;
import com.hhly.skeleton.lotto.base.trend.high.bo.K3BaseBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;
import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;

/**
 * 快3走势 
 * @desc 
 * @author chenghougui
 * @Date 2018年1月3日
 * @Company 益彩网络科技公司
 * @version
 */
@Service("k3TrendService")
public class K3TrendServiceImpl extends HighTrendService2Impl implements IK3TrendService {

	
	/*****************************快3走势图数据接口 *********************************/
	
	/**
	 * 基本走势
	 */	
	@Override
	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo) {
		logger.debug("高频彩查询基本走势,彩种:{}",vo.getLotteryCode());
		HighLotteryVO lotteryVo = new HighLotteryVO();
		try {
			BeanUtils.copyProperties(lotteryVo, vo);
			String key = CacheConstants.getLotteryTrendKey(vo.getLotteryCode(), vo.getStartIssue()==null?"0":vo.getStartIssue(), vo.getEndIssue()==null?"0":vo.getEndIssue(), vo.getQryCount(),"base");
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
				TrendBaseK3BO bo = new TrendBaseK3BO();
				bo.setIssue(trendBaseBO.getIssue());
				bo.setDrawCode(trendBaseBO.getDrawCode());
				//基础号码
				bo.setBaseList(genBaseDigits((K3BaseBO)trendBaseBO,"b",Constants.NUM_1,Constants.NUM_6));
				bo.setSumList(genBaseDigits((K3BaseBO)trendBaseBO,"s",Constants.NUM_3,Constants.NUM_18));
				result.add(bo);
			}
			redisUtil.addObj(key, result, (long)Constants.DAY_1);
			return ResultBO.ok(result);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ResultBO.ok(null);
	}
	
	//K3
	private List<Integer> genBaseDigits(K3BaseBO baseTrend, String preField,int begin,int end) {
		List<Integer> tmp = new ArrayList<>();
		for (int i = begin; i <= end; i++) {
			tmp.add(ClassUtil.getField(baseTrend, preField + i, Integer.class));
		}
		return tmp;
	}
	
	/*****************************开奖信息 *********************************/
	
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
		/** 冷热数据  **/
		LotteryTrendVO vo = new LotteryTrendVO();
		vo.setLotteryCode(param.getLotteryCode());

		String key = CacheConstants.N_CORE_LOTTO_OMIT + param.getLotteryCode() + "_omit_coldhot_" + param.getIssueCode() + param.getQryFlag() + param.getQryCount();
		K3ColdHotOmitBo result = (K3ColdHotOmitBo) redisUtil.getObj(key);
		if(result!=null){
			return new ResultBO<ColdHotOmitBo>(result);
		}
		Map<String, TrendBaseBO> totalMap = null;
		K3ColdHotOmitBo bo =new K3ColdHotOmitBo();

		/**今天，昨天，前天**/
		//今天
		String convertDateToStr = DateUtil.convertDateToStr(new Date(),DATE_FORMAT);
		vo.setStartIssue(convertDateToStr+"001");
		List<TrendBaseBO> trendList = k3DaoMapper.findBaseTrend(vo);
		List<NumTimeVo> today = new ArrayList<>();
		List<NumTimeVo> yesterday =  new ArrayList<>();
		List<NumTimeVo> before =  new ArrayList<>();
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			TrendBaseBO target = totalMap.get("occTimes");
			OmitTrendUtil.assemble(target);
			K3BaseBO trendBaseBO = (K3BaseBO) target;
			List<NumTimeVo> coldHotList = dealWithColdHotData(trendBaseBO,"s",Constants.NUM_18);
			Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
				@Override
				public int compare(NumTimeVo o1, NumTimeVo o2) {
					return o1.getTime()-o2.getTime();
				}
			});
			List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
			bo.setTodayCold(new ArrayList<>(coldList));
			List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_8, Constants.NUM_13);
			Collections.reverse(hotList);
			bo.setTodayHot(new ArrayList<>(hotList));
			today.add(new NumTimeVo("t3",trendBaseBO.getT3()));
			today.add(new NumTimeVo("bt3",trendBaseBO.getBt3()));
			today.add(new NumTimeVo("l3",trendBaseBO.getL3()));
			today.add(new NumTimeVo("bt2",trendBaseBO.getBt2()));
			today.add(new NumTimeVo("tf2",trendBaseBO.getTf2()));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有今天的遗漏数据");
		}
		//昨天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-1, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr+"001");
		//一天总期数不会超过100
		vo.setEndIssue(convertDateToStr+Constants.NUM_100);
		trendList = k3DaoMapper.findBaseTrend(vo);
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			TrendBaseBO target = totalMap.get("occTimes");
			OmitTrendUtil.assemble(target);
			K3BaseBO trendBaseBO = (K3BaseBO) target;
			List<NumTimeVo> coldHotList = dealWithColdHotData(trendBaseBO,"s",Constants.NUM_18);
			Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
				@Override
				public int compare(NumTimeVo o1, NumTimeVo o2) {
					return o1.getTime()-o2.getTime();
				}
			});
			List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
			bo.setYesterdayCold(new ArrayList<>(coldList));
			List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_8, Constants.NUM_13);
			Collections.reverse(hotList);
			bo.setYesterdayHot(new ArrayList<>(hotList));
			yesterday.add(new NumTimeVo("t3",trendBaseBO.getT3()));
			yesterday.add(new NumTimeVo("bt3",trendBaseBO.getBt3()));
			yesterday.add(new NumTimeVo("l3",trendBaseBO.getL3()));
			yesterday.add(new NumTimeVo("bt2",trendBaseBO.getBt2()));
			yesterday.add(new NumTimeVo("tf2",trendBaseBO.getTf2()));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有昨天的遗漏数据");
		}
		
		//前天
		convertDateToStr = DateUtil.getBeforeOrAfterDate(-2, DATE_FORMAT);
		vo.setStartIssue(convertDateToStr+"001");
		vo.setEndIssue(convertDateToStr+Constants.NUM_100);
		trendList = k3DaoMapper.findBaseTrend(vo);
		try {
			totalMap = OmitTrendUtil.getTrendTotalInfo(trendList, trendList.get(0).getClass());
			TrendBaseBO target = totalMap.get("occTimes");
			OmitTrendUtil.assemble(target);
			K3BaseBO trendBaseBO = (K3BaseBO) target;
			List<NumTimeVo> coldHotList = dealWithColdHotData(trendBaseBO,"s",Constants.NUM_18);
			Collections.sort(coldHotList,new Comparator<NumTimeVo>() {
				@Override
				public int compare(NumTimeVo o1, NumTimeVo o2) {
					return o1.getTime()-o2.getTime();
				}
			});
			List<NumTimeVo> coldList = coldHotList.subList(Constants.NUM_0, Constants.NUM_5);
			bo.setBeforeCold(new ArrayList<>(coldList));
			List<NumTimeVo> hotList = coldHotList.subList(Constants.NUM_8, Constants.NUM_13);
			Collections.reverse(new ArrayList<>(hotList));
			bo.setBeforeHot(hotList);
			before.add(new NumTimeVo("t3",trendBaseBO.getT3()));
			before.add(new NumTimeVo("bt3",trendBaseBO.getBt3()));
			before.add(new NumTimeVo("l3",trendBaseBO.getL3()));
			before.add(new NumTimeVo("bt2",trendBaseBO.getBt2()));
			before.add(new NumTimeVo("tf2",trendBaseBO.getTf2()));
		} catch (Exception e) {
			logger.info(param.getLotteryCode()+"没有前天的遗漏数据");
		}
		bo.setToday(today);
		bo.setYesterday(yesterday);
		bo.setBefore(before);
		redisUtil.addObj(key, bo, (long) Constants.DAY_1);
		return new ResultBO<ColdHotOmitBo>(bo);
		
	}
	
	
	private List<NumTimeVo> dealWithColdHotData(K3BaseBO baseTrend, String preField,int count) {
		List<NumTimeVo> list = new ArrayList<>(Constants.NUM_50);
		NumTimeVo vo = null;
		for (int i = 3; i <= count; i++) {
			vo = new NumTimeVo();
			vo.setCode(i+"");
			vo.setTime(ClassUtil.getField(baseTrend, preField + i, Integer.class));
			list.add(vo);
		}
		return list;
	}

}
