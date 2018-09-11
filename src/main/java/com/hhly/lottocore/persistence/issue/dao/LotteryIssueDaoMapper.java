package com.hhly.lottocore.persistence.issue.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.skeleton.base.issue.entity.NewIssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueDrawBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottJCBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueOfficialTimeBO;
import com.hhly.skeleton.lotto.base.issue.vo.LottoIssueVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * @desc 彩期
 * @author jiangwei
 * @date 2017-2-16
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface LotteryIssueDaoMapper {

	/**************************** Used to CMS ********************************/

	
	/**************************** Used to LOTTO ******************************/

	/**
	 * @desc 前端接口：查询单个彩期信息
	 * @author huangb
	 * @date 2017年3月6日
	 * @param lotteryVO
	 *            参数对象
	 * @return 前端接口：查询单个彩期信息
	 */
	IssueBO findSingleFront(LotteryVO lotteryVO);
	/**
	 * 前端接口：查询所有符合条件的彩期信息，默认查所有，可以按条件
	 * @author longguoyou
	 * @date 2017年3月28日
	 * @param lotteryVO
	 * @return
	 */
	List<IssueBO> findMultipleFront(LotteryVO lotteryVO);

	/**
	 * @desc 前端接口：查询最新开奖彩期(即当前期的上一期)
	 * @author huangb
	 * @date 2017年3月6日
	 * @param issueVO
	 *            参数对象
	 * @return 前端接口：查询最新开奖彩期(即当前期的上一期)
	 */
	IssueDrawBO findLatestDrawIssue(LotteryVO lotteryVO);

	/**
	 * @desc 前端接口：查询最近开奖详情列表
	 * @author huangb
	 * @date 2017年3月6日
	 * @param issueVO
	 *            参数对象
	 * @return 前端接口：查询最近开奖详情列表
	 */
	List<IssueDrawBO> findRecentDrawIssue(LotteryVO lotteryVO);
    /**
     * 获取彩期信息用于生成模拟数据
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2017年3月8日 下午3:45:08
     * @param lotteryCode
     * @param issue
     * @return
     */
	NewIssueBO findLotteryIssue(@Param("lotteryCode")int lotteryCode, @Param("issueCode")String issueCode);

	/**
	 * 查询当前期下一期彩期新
	 *
	 * @param lotteryVO
	 * @return
	 */
	IssueBO findNextIssue(LotteryVO lotteryVO);

	/**
	 *
	 * 通过下单彩种编号和彩期编号，查询下一期彩期信息(用于订单入库校验查询)
	 *
	 * @param lotteryVO
	 * @return
	 */

	IssueBO findNextIssueByLotteryCodeAndIssueCode(LotteryVO lotteryVO);
	
	/**
	 * 查询当天有开奖的彩种
	 * @return
	 */
	List<IssueLottBO> findDrawNameToday();
	
	/**
	 *  查询当前期和之后的预售期组成列表 
	 * @return
	 */
	List<IssueLottJCBO> findIssueByCode(@Param("lotteryCode") int lotteryCode);

	/**
	 * 查询彩期列表记录数
	 * @desc 
	 * @create 2017年12月19日
	 * @param vo
	 * @return Integer
	 */
	Integer findIssueListByCodeTotal(LottoIssueVO vo);

	/**
	 * 根据彩种查询彩期列表。支持分页查询
	 *
	 * @return
	 */
	List<IssueLottJCBO> findIssueListByCode(LottoIssueVO vo);

	/**
	 * 根据彩种查询后5期的彩期列表。支持分页查询
	 *
	 * @return
	 */
	List<IssueLottJCBO> findAfterFiveIssueListByCode(LottoIssueVO vo);
	
	/**
	 * @desc 前端接口：用户中心-查询低频彩等待出票的官方出票时间段
	 * @author huangb
	 * @date 2017年4月19日
	 * @param lotteryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询低频彩等待出票的官方出票时间段
	 */
	IssueOfficialTimeBO findNumOfficialTime(LotteryVO lotteryVO);

	/**
	 * @desc 前端接口：用户中心-查询高频彩等待出票的官方出票时间段
	 * @author huangb
	 * @date 2017年4月19日
	 * @param lotteryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询高频彩等待出票的官方出票时间段
	 */
	IssueOfficialTimeBO findHighOfficialTime(LotteryVO lotteryVO);
	
	/**
	 * @desc 前端接口：用户中心-查询竞技彩等待出票的官方出票时间段
	 * @author huangb
	 * @date 2017年4月19日
	 * @param lotteryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询竞技彩等待出票的官方出票时间段
	 */
	List<IssueOfficialTimeBO> findSportOfficialTime(LotteryVO lotteryVO);
	
	/**
	 * 查询当前期和上一期信息
	 * @return
	 */
	CurrentAndPreIssueBO findIssueAndPreIssueByCode(@Param("lotteryCode") int lotteryCode);

	/**
	 * 查询所有当前期和上一期信息列表
	 * @return
	 */
	List<CurrentAndPreIssueBO> findAllIssueAndPreIssue();
	
	/**
	 * 查询彩期是不是当前期
	 * @param lotteryCode
	 * @param issueCode
	 * @return
	 */
	Boolean isCurrentIssue(@Param("lotteryCode") Integer lotteryCode,@Param("issueCode") String issueCode);
	
	/**
	 * 根据彩种编码查询所有彩期
	 * @param lotteryCode
	 * @return
	 * @date 2017年9月23日上午11:24:02
	 * @author cheng.chen
	 */
	List<String> queryIssueByLottery(LotteryVO lotteryVO);
}