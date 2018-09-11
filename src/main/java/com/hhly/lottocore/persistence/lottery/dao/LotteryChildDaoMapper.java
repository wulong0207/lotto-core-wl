package com.hhly.lottocore.persistence.lottery.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotChildVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

public interface LotteryChildDaoMapper {

	/**************************** Used to CMS ******************************/


	/**************************** Used to LOTTO ******************************/

	/**
	 * @desc 前端接口：查询单条子玩法配置
	 * @author huangb
	 * @date 2017年3月7日
	 * @param lotChildVO
	 *            参数对象
	 * @return 前端接口：查询单条子玩法配置
	 */
	LotChildBO findSingleFront(LotChildVO lotChildVO);

	/**
	 * @desc 前端接口：查询多条子玩法配置
	 * @author huangb
	 * @date 2017年3月7日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询多条子玩法配置
	 */
	List<LotChildBO> findMultipleFront(LotteryVO lotteryVO);

	/**
	 * @desc   查询符合条件的子玩法记录数
	 * @author Tony Wang
	 * @create 2017年4月10日
	 * @param vo
	 * @return 
	 */
	int count(LotChildVO vo);
}