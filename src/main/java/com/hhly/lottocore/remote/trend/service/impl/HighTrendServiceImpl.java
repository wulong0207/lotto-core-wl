//package com.hhly.lottocore.remote.trend.service.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.hhly.lottocore.base.util.RedisUtil;
//import com.hhly.lottocore.cache.service.LotteryIssueCacheService;
//import com.hhly.lottocore.persistence.trend.high.dao.HighLotteryDaoMapper;
//import com.hhly.lottocore.persistence.trend.high.dao.X115DaoMapper;
//import com.hhly.skeleton.base.bo.ResultBO;
//import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
//import com.hhly.skeleton.base.constants.CacheConstants;
//import com.hhly.skeleton.base.constants.Constants;
//import com.hhly.skeleton.base.constants.HighConstants;
//import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
//import com.hhly.skeleton.lotto.base.lottery.vo.LotteryTrendVO;
//import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
//import com.hhly.skeleton.lotto.base.trend.bo.ColdHotOmitBo;
//import com.hhly.skeleton.lotto.base.trend.bo.TrendBaseBO;
//import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitBaseBO;
//import com.hhly.skeleton.lotto.base.trend.high.bo.HighOmitDataBO;
//import com.hhly.skeleton.lotto.base.trend.vo.high.HighLotteryVO;
//
///**
// * @desc    高频彩种遗漏走势的服务接口
// * @author  Tony Wang
// * @date    2017年3月13日
// * @company 益彩网络科技公司
// * @version 1.0
// */
//@Service("highTrendService")
//public class HighTrendServiceImpl extends AbstractHighTrendService {
//
//	private static Logger logger = LoggerFactory.getLogger(HighTrendServiceImpl.class);
//	
//	/** 山东十一选五遗漏走势数据接口 */
//	@Autowired
//	private X115DaoMapper x115DaoMapper;
//
//	@Autowired 
//	private RedisUtil redisUtil;
//	
//	@Autowired 
//	private LotteryIssueCacheService lotteryIssueCacheService;
//	
//	@Override
//	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findResultOmit(T resultVO) {
//		logger.info("高频彩查询历史遗漏数据,彩种:"+resultVO.getLotteryCode());
//		String key = CacheConstants.N_CORE_LOTTO_OMIT+resultVO.getLotteryCode()+"_result_"+resultVO.getQryFlag()+resultVO.getQryCount();
//		if(resultVO.getOmitTypes()!=null){
//			for(Integer type:resultVO.getOmitTypes()){
//				key+=type;
//			}
//		}
//		if(resultVO.getSubPlays()!=null){
//			for(Integer type:resultVO.getSubPlays()){
//				key+=type;
//			}
//		}		
//		HighOmitDataBO retBO = (HighOmitDataBO)redisUtil.getObj(key);
//		if(retBO==null){
//			retBO = new HighOmitDataBO(getLotteryDaoMapper(resultVO).findResultOmit(resultVO));
//			redisUtil.addObj(key, retBO, (long)Constants.NUM_600);
//		}
//		return ResultBO.ok(retBO);
//	}
//
//	@Override
//	public <T extends HighLotteryVO> ResultBO<HighOmitDataBO> findRecentOmit(T recentVO) {
//		logger.debug("高频彩查询近期遗漏数据,彩种:"+recentVO.getLotteryCode());
//		if(recentVO.getQryCount() < 0 || recentVO.getQryCount() > HighConstants.QRY_MAX_ISSUE_COUNT) {
//			recentVO.setQryCount(HighConstants.QRY_DEFAULT_ISSUE_COUNT);
//		}
//		String key = CacheConstants.N_CORE_LOTTO_OMIT+recentVO.getLotteryCode()+"_recent_"+recentVO.getQryFlag()+recentVO.getQryCount();
//		if(recentVO.getOmitTypes()!=null){
//			for(Integer type:recentVO.getOmitTypes()){
//				key+=type;
//			}
//			
//		}
//		if(recentVO.getSubPlays()!=null){
//			for(Integer type:recentVO.getSubPlays()){
//				key+=type;
//			}	
//		}		
//		@SuppressWarnings("unchecked")
//		List<HighOmitBaseBO> recentOmit2 =(List<HighOmitBaseBO>)redisUtil.getObj(key);		
//		if(recentOmit2==null){
//			// 查出来的list是按issue desc排序的
//			List<HighOmitBaseBO> recentOmit = getLotteryDaoMapper(recentVO).findRecentOmit(recentVO);
//			 recentOmit2 = new ArrayList<>();
//			 // 要添加子玩法信息，给前端做区别，以便把不同子玩法的数据渲染到页面中的不同位置
//			for(Integer subPlay : recentVO.getSubPlays()) {
//				HighOmitBaseBO subPlayRecentOmit = new HighOmitBaseBO(subPlay.toString(), new ArrayList<HighOmitBaseBO>());
//				recentOmit2.add(subPlayRecentOmit);
//			}
//			for(HighOmitBaseBO omit : recentOmit) {
//				for(HighOmitBaseBO subPlayRecentOmit : recentOmit2) {
//					// 把从数据库查询出来的数据按不同的子玩法分区
//					if(Objects.equals(subPlayRecentOmit.getSubPlay(), omit.getSubPlay())) {
//						// 保持倒序
//						subPlayRecentOmit.getHistory().add(0, omit);
//						break;
//					}
//				}
//			}
//			//判断获取的遗憾是否最新期，不是最新期则更新缓存
//			CurrentAndPreIssueBO issueBo = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(recentVO.getLotteryCode()).getData();	
//			if(issueBo!=null&&recentOmit2!=null&&recentOmit2.size()>0){
//				if(Objects.equals(issueBo.getPreIssue(), recentOmit2.get(0).getIssue())){
//					redisUtil.addObj(key, recentOmit2, (long)Constants.NUM_600);
//				}
//			}
//		}				
//		return ResultBO.ok(new HighOmitDataBO(null, recentOmit2));
//	}
//	
//	/**
//	 * @desc   根据彩种获取Dao
//	 * @author Tony Wang
//	 * @create 2017年3月13日
//	 * @param lotteryCode
//	 * @return 
//	 */
//	private HighLotteryDaoMapper getLotteryDaoMapper(HighLotteryVO vo) {
//		Lottery lot = Lottery.getLottery(vo.getLotteryCode());
//		switch (lot) {
//		case SD11X5:
//		case D11X5:
//		case XJ11X5:
//		case JX11X5:
//		case GX11X5:	
//			return x115DaoMapper;
//		default:
//			throw new IllegalArgumentException("不存在此高频彩,彩种编码:"+vo.getLotteryCode()+"！");
//		}
//	}
//
//
//	@Override
//	public ResultBO<List<TrendBaseBO>> findBaseTrend(LotteryTrendVO vo) {
//		return null;
//	}
//
//	@Override
//	public ResultBO<ColdHotOmitBo> findDrawColdHotOmit(LotteryVO param) {
//	
//		return null;
//	}
//	
//}
