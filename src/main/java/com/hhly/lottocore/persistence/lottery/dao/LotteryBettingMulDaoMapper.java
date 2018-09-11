package com.hhly.lottocore.persistence.lottery.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

public interface LotteryBettingMulDaoMapper {

	/**************************** Used to CMS ******************************/

	
	/**************************** Used to LOTTO ******************************/
	/**
	 * @desc 前端接口：查询多条彩种注、倍数配置
	 * @author huangb
	 * @date 2017年3月7日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询多条彩种注、倍数配置
	 */
	List<LotBettingMulBO> findMultipleFront(LotteryVO lotteryVO);
	

}