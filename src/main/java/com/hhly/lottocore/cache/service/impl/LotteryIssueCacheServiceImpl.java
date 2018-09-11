package com.hhly.lottocore.cache.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.LotteryIssueCacheService;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
/**
 * 彩期信息缓存数据接口服务
 * @author lidecheng
 * @date 2017年6月12日
 * @compay 益彩网络科技有限公司
 */
@Service("lotteryIssueCacheService")
public class LotteryIssueCacheServiceImpl implements LotteryIssueCacheService {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;		

	/**
	 * 查询某个彩种当前期和最新开奖期信息缓存
	 * @param lotteryCode  彩种id
	 * @return
	 */
	@Override
	public ResultBO<?> getCurrentAndPreIssue(Integer lotteryCode) {
		if (ObjectUtil.isBlank(lotteryCode)) 
			return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
		String key = CacheConstants.C_COMM_LOTTERY_ISSUE_CURRENT_AND_LAST+lotteryCode;
		CurrentAndPreIssueBO  issueBO = (CurrentAndPreIssueBO)redisUtil.getObj(key);
		if(issueBO==null){
			issueBO = lotteryIssueDaoMapper.findIssueAndPreIssueByCode(lotteryCode);
			redisUtil.addObj(key, issueBO, (long)Constants.DAY_1);
		}
		return ResultBO.ok(issueBO);
	}

	/**
	 * 查询所有缓存的彩种当前期和最新开奖期信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<?> getAllCurrentAndPreIssue() {
		String key = CacheConstants.C_COMM_LOTTERY_ISSUE_ALL_CURRENT_AND_LAST;
		List<CurrentAndPreIssueBO>  issueList = (List<CurrentAndPreIssueBO>)redisUtil.getObj(key);
		if(issueList==null||issueList.size()==0){
			issueList = lotteryIssueDaoMapper.findAllIssueAndPreIssue();
			redisUtil.addObj(key, issueList, (long)Constants.DAY_1);
		}
		return ResultBO.ok(issueList);
	}	
}
