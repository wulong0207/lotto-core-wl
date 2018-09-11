package com.hhly.lottocore.remote.trend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.trend.service.IQxcTrendService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.QxcColdHotOmitBo;
import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.QxcTrendBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.QxcDrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.vo.NumTimeVo;

/**
 * @desc    七星彩遗漏走势的服务接口
 * @author  Tony Wang
 * @date    2017年7月31日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service("qxcTrendService")
public class QxcTrendServiceImpl extends NumTrendServiceImpl implements IQxcTrendService {

	
	@Override
	public ResultBO<List<TrendBaseBO>> findRecentDrawIssue(LotteryVO vo) {
		return ResultBO.ok(qxcTrendDaoMapper.findRecentDrawIssue(vo));
	}

	@Override
	public ResultBO<QxcDrawOtherBO> findLatestDrawOther(LotteryVO lotteryVO) {
		return ResultBO.ok(qxcTrendDaoMapper.findLatestDrawOtherFront(lotteryVO));
	}
	
	/*****************************开奖信息冷热数据 *********************************/
	@Override
	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) throws Exception {
		param.setQryCount(Constants.NUM_100);
		param.setQryFlag(Constants.NUM_2);
		ResultBO<TrendBaseBO> omit = findOmitChanceColdHot(param);
		QxcTrendBO data =(QxcTrendBO) omit.getData();
		Comparator<NumTimeVo> compare = new Comparator<NumTimeVo>() {
			@Override
			public int compare(NumTimeVo o1, NumTimeVo o2) {
				return o1.getTime()-o2.getTime();
			}
		};
		QxcColdHotOmitBo bo = new QxcColdHotOmitBo();
		//七星彩 个 十 百 千 万 十万 百万
		List<NumTimeVo> omitData = dealWithOmitData(data,GW);
		Collections.sort(omitData,compare);
		bo.setGwColdBase(omitData.get(0));
		bo.setGwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,SW);
		Collections.sort(omitData,compare);
		bo.setSwColdBase(omitData.get(0));
		bo.setSwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,BW);
		Collections.sort(omitData,compare);
		bo.setBwColdBase(omitData.get(0));
		bo.setBwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,QW);
		Collections.sort(omitData,compare);
		bo.setQwColdBase(omitData.get(0));
		bo.setQwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,WW);
		Collections.sort(omitData,compare);
		bo.setWwColdBase(omitData.get(0));
		bo.setWwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,SWW);
		Collections.sort(omitData,compare);
		bo.setSwwColdBase(omitData.get(0));
		bo.setSwwHotBase(omitData.get(Constants.NUM_9));
		
		omitData = dealWithOmitData(data,BWW);
		Collections.sort(omitData,compare);
		bo.setBwwColdBase(omitData.get(0));
		bo.setBwwHotBase(omitData.get(Constants.NUM_9));
		
		return ResultBO.ok((ColdHotOmitBo)bo);
	}

	/**
	 * 为开奖信息处理数据返回
	 * @desc 
	 * @create 2018年1月6日
	 * @param baseTrend
	 * @param preField
	 * @param count
	 * @return List<NumTimeVo>
	 */
	private List<NumTimeVo> dealWithOmitData(QxcTrendBO baseTrend,String pos) {
		int count=0;
		switch(pos){
		case BWW:
			count=0;
			break;
		case SWW:
			count=1;
			break;
		case WW:
			count=2;
			break;
		case QW:
			count=3;
			break;
		case BW:
			count=4;
			break;
		case SW:
			count=5;
			break;
		case GW:
			count=6;
			break;
		}
		//所有的号码遗漏
		ArrayList<Object> omits = baseTrend.getOmits();
		List<NumTimeVo> list = new ArrayList<>();
		NumTimeVo vo = null;
		for (int i = 10*count; i < 10*(count+1); i++) {
			vo = new NumTimeVo();
			vo.setCode(i+"");
			vo.setTime((Integer)omits.get(i));
			list.add(vo);
		}
		return list;
	}
}
