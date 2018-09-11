package com.hhly.lottocore.persistence.lottery.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.cms.lotterymgr.bo.LotteryWinningBO;
import com.hhly.skeleton.cms.lotterymgr.vo.LotteryWinningVO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotWinningBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * @desc 彩种奖项数据接口
 * @author huangb
 * @date 2017年2月24日
 * @company 益彩网络
 * @version v1.0
 */
public interface LotteryWinningDaoMapper {

	/**************************** Used to CMS ********************************/
	/**
	 * @desc 查询：单条记录
	 * @author huangb
	 * @date 2017年2月24日
	 * @param lotteryWinningVO
	 *            参数对象
	 * @return 查询：单条记录
	 */
	LotteryWinningBO findSingle(LotteryWinningVO lotteryWinningVO);

	
	/**************************** Used to LOTTO ******************************/
	/**
	 * @desc 前端接口：查询彩种的奖项集合
	 * @author huangb
	 * @date 2017年3月6日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询彩种的奖项集合
	 */
	List<LotWinningBO> findMultipleFront(LotteryVO lotteryVO);

	/**
	 * 获取奖项名称
	 * @param code
	 * @param lotteryCode
	 * @return
	 */
	List<LotWinningBO> findLotteryWinningName(@Param("code")String code,@Param("lotteryCode")Integer lotteryCode);

}