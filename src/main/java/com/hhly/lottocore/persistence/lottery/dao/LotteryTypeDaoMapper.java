package com.hhly.lottocore.persistence.lottery.dao;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.bo.LotteryDetailBO;
import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.base.issue.entity.NewTypeBO;
import com.hhly.skeleton.cms.lotterymgr.bo.LotteryTypeBO;
import com.hhly.skeleton.cms.lotterymgr.vo.LotteryTypeVO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * @desc 彩种相关的数据接口
 * @author huangb
 * @date 2017年3月2日
 * @company 益彩网络
 * @version v1.0
 */
public interface LotteryTypeDaoMapper {

	/**************************** Used to CMS ********************************/

	/**
	 * @param lotteryTypeVO
	 *            参数对象
	 * @return 单个查询对象
	 * @desc 查询唯一的数据记录
	 */
	LotteryTypeBO findSingle(LotteryTypeVO lotteryTypeVO);


	/**************************** Used to LOTTO ******************************/
	/**
	 * @desc 前端接口：查询单个彩种信息
	 * @author huangb
	 * @date 2017年3月6日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询单个彩种信息
	 */
	LotteryBO findSingleFront(LotteryVO lotteryVO);
	/**
	 * 查询所有大彩种信息
	 * @author longguoyou
	 * @date 2017年3月22日
	 * @param lotteryVO
	 * @return
	 */
	List<LotteryBO> findMultipleFront(LotteryVO lotteryVO);
    /**
     * 查询彩种信息用于生成彩期
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2017年3月8日 下午3:40:44
     * @param lotteryCode
     * @return
     */
	NewTypeBO findTypeUseAddIssue(@Param("lotteryCode")int lotteryCode);
	
	/**
	 * 查询彩种信息
	 * @return
	 */
	List<LotteryTypeBO> findAllLotteryType();
	
	/**
	 * 查询彩种下拉或筛选彩种集合
	 * @return
	 * @date 2017年9月23日上午10:43:51
	 * @author cheng.chen
	 */
	List<LotteryBO> queryLotterySelectList(LotteryVO vo);

	/**
	 * 查询全国所有在售彩种
	 * @return
	 */
	List<LotteryDetailBO> findLotteryList();

	/**
	 * 查询彩种开奖公告
	 * @return
	 */
	List<LotteryDetailBO> findLotteryDrawList();

	/**
	 * 查询竞篮竞足当前彩期可投注赛事场次
	 * @param lotteryCode
	 * @return
	 */
	int findSportBetNum(@Param("lotteryCode") Integer lotteryCode);

	/**
	 * 查询当前高频彩已销售期数
	 * @param lotteryCode
	 * @return
	 */
	int findLotterySalesIssue(@Param("lotteryCode") Integer lotteryCode);
}