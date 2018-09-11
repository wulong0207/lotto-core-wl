package com.hhly.lottocore.remote.lotto.service;

import java.util.List;

import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.issue.entity.ChaseIssueBO;
import com.hhly.skeleton.base.issue.entity.NewIssueBO;
import com.hhly.skeleton.lotto.base.dic.bo.DicDataDetailBO;
import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueDrawBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueHomeBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottJCBO;
import com.hhly.skeleton.lotto.base.issue.vo.LottoIssueVO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.news.bo.OperteNewsHomeTopBO;

/**
 * @desc 前端接口服务：彩期
 * @author huangb
 * @date 2017年3月6日
 * @company 益彩网络
 * @version v1.0
 */
public interface ILotteryIssueService {

	/**
	 * @desc   : 查询每个彩种的基础信息	 
	 * @author : Bruce Liu
	 * @create : 2017年3月8日
	 * @return
	 */
	ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode);
	
	/**
	 * @desc 查询每个彩种的基础信息 （added to 20171110 彩种发布平台限制，前端接口兼容性处理 （后端统一处理彩种限制状态））
	 * @author huangb
	 * @date 2017年11月10日
	 * @param lotteryCode
	 *            彩种
	 * @param platform
	 *            平台
	 * @return 查询每个彩种的基础信息
	 */
	ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode, Short platform);
	
	/**
	 * 
	 * @desc 
	 * @create 2018年6月26日
	 * @param lotteryCode
	 * @param platform
	 * @param channelId
	 * @return ResultBO<LotteryIssueBaseBO>
	 */
	ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode, Short platform,String channelId,List<DicDataDetailBO> dicDataList);
	/**
	 * @desc 前端接口：查询最近开奖详情列表
	 * @author huangb
	 * @date 2017年3月6日
	 * @param issueVO
	 *            参数对象
	 * @return 前端接口：查询最近开奖详情列表
	 */
	ResultBO<List<IssueDrawBO>> findRecentDrawIssue(LotteryVO lotteryVO);
	/**
	 * 获取模拟彩期信息
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2017年3月8日 下午3:38:26
	 * @param lotteryCode 彩种id
	 * @param num 生成期号
	 * @param issue 开始期号
	 * @return
	 */
	List<NewIssueBO> listLotteryIssue(int lotteryCode,int num,String issue);
	
	/**
	 * 查询首页开奖公告
	 * @return
	 */
	ResultBO<IssueHomeBO>  findHomeDrawLott();

	
	/**
	 * @desc   查询限号信息
	 * @author Tony Wang
	 * @create 2017年3月28日
	 * @param vo
	 * @return 
	 */
	ResultBO<List<LimitNumberDetailBO>> findLimit(LotteryVO vo);
	
	/**
	 * 查询开奖公告上显示的双色球或者大乐透的开奖信息
	 * @return
	 */
	ResultBO<IssueLottBO> findNewsHomeDraw();
	
	/**
	 * 查询当天有开奖的彩种
	 * @return
	 */
	ResultBO<List<IssueLottBO>> findDrawNameToday();
	
	 /**
	 * 查询资讯首页头部信息
	 * @return
	 */
	ResultBO<OperteNewsHomeTopBO> findNewsHomeTop() ;
	
	/**
	 *  查询当前期和之后的预售期组成列表 
	 * @return
	 */
	ResultBO<List<IssueLottJCBO>> findIssueByCode(int lotteryCode);

	/**
	 * 根据彩种查询彩期列表。支持分页查询
	 *
	 * @return
	 */
	ResultBO<PagingBO<IssueLottJCBO>> findIssueListByCode(final LottoIssueVO vo);

	/**
	 * 根据彩种查询当前期后5期彩期列表。支持分页查询
	 *
	 * @return
	 */
	ResultBO<List<IssueLottJCBO>> findAfterFiveIssueListByCode(final LottoIssueVO vo);
	
	/**
	 * 查询当前期和上一期信息
	 *
	 * @return
	 */
	ResultBO<CurrentAndPreIssueBO> findIssueAndPreIssueByCode(Integer lotteryCode);	
	
	IssueBO findSingleFront(LotteryVO lotteryVO);
	
	/**
	 * 根据彩种编码查询所有彩期
	 * @param lotteryCode
	 * @return
	 * @date 2017年9月23日上午11:24:02
	 * @author cheng.chen
	 */
	List<String> queryIssueByLottery(LotteryVO lotteryVO);
	
	/**
	 * 
	 * @Description 获取彩种追号期列表
	 * @author HouXiangBao289
	 * @param curIssue 当前期
	 * @param issueCount 追号期数
	 * @return
	 */
	ResultBO<List<ChaseIssueBO>> findChaseIssue(Integer lotCode,String curIssue,Integer issueCount);
}
