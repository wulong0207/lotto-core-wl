package com.hhly.lottocore.persistence.lottery.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * @desc 彩种限号的数据接口
 * @author huangb
 * @date 2017年2月14日
 * @company 益彩网络
 * @version v1.0
 */
public interface LotteryLimitMapper {

	/**************************** Used to CMS ******************************/



	/**************************** Used to LOTTO ******************************/
	/**
	 * @desc 前端接口：查询单个限号信息(包含对应多个限号内容)
	 * @author huangb
	 * @date 2017年3月6日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询单个限号信息(包含对应多个限号内容)
	 */
	LimitNumberInfoBO findSingleLimitFront(LotteryVO lotteryVO);

	/**
	 * @desc 前端接口：查询多个限号信息(包含对应多个限号内容)
	 * @author huangb
	 * @date 2017年3月6日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询多个限号信息(包含对应多个限号内容)
	 */
	List<LimitNumberInfoBO> findMultipleLimitFront(LotteryVO lotteryVO);

	/**
	 * @desc   查询限号，根据参数只查询特定子玩法的限号信息
	 * @author Tony Wang
	 * @create 2017年3月28日
	 * @param vo
	 * @return 
	 */
	List<LimitNumberDetailBO> findMultipleLimitFrontByChild(LotteryVO vo);
}
