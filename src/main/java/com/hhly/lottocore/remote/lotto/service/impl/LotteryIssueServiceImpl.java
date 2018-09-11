package com.hhly.lottocore.remote.lotto.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.cache.service.LotteryIssueCacheService;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryBettingMulDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryLimitMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.lotto.service.ILotteryIssueService;
import com.hhly.lottocore.remote.trend.service.IF3dTrendService;
import com.hhly.lottocore.remote.trend.service.IPl3TrendService;
import com.hhly.lottocore.remote.trend.service.IPl5TrendService;
import com.hhly.lottocore.remote.trend.service.IQxcTrendService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.ConIssue;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryCategory;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryLimitStatus;
import com.hhly.skeleton.base.common.LotteryEnum.SaleStatus;
import com.hhly.skeleton.base.common.LotteryEnum.SynIssue;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.issue.IssueHandler;
import com.hhly.skeleton.base.issue.entity.ChaseIssueBO;
import com.hhly.skeleton.base.issue.entity.NewIssueBO;
import com.hhly.skeleton.base.issue.entity.NewTypeBO;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.dic.bo.DicDataDetailBO;
import com.hhly.skeleton.lotto.base.issue.bo.CurrentAndPreIssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueDrawBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueHomeBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueLottJCBO;
import com.hhly.skeleton.lotto.base.issue.vo.LottoIssueVO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.news.bo.OperteNewsHomeTopBO;
import com.hhly.skeleton.lotto.base.sport.bo.JcMathSPBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.f3d.F3dDrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl3.Pl3DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.Pl5DrawOtherBO;
import com.hhly.skeleton.lotto.base.trend.num.bo.pl5.QxcDrawOtherBO;

/**
 * @desc 前端接口服务：彩期
 * @author huangb
 * @date 2017年3月6日
 * @company 益彩网络
 * @version v1.0
 */
@Service("lotteryIssueService")
public class LotteryIssueServiceImpl implements ILotteryIssueService {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(LotteryIssueServiceImpl.class);

	/**
	 * 彩期数据接口
	 */
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;

	/**
	 * 彩种数据接口
	 */
	@Autowired
	private LotteryTypeDaoMapper lotteryTypeDaoMapper;
	/**
	 * 彩种注、倍数配置接口
	 */
	@Autowired
	private LotteryBettingMulDaoMapper lotteryBettingMulDaoMapper;
	/**
	 * 彩种子玩法配置接口
	 */
	@Autowired
	private LotteryChildDaoMapper lotteryChildDaoMapper;
	/**
	 * 彩种限号配置接口
	 */
	@Autowired
	private LotteryLimitMapper lotteryLimitMapper;
	@Autowired
	private IPageService pageService;
	@Autowired
	private IJcDataService  iJcDataService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private LotteryIssueCacheService lotteryIssueCacheService;
	
	/**
	 * 图片域名地址
	 */
	@Value("${before_file_url}")
	protected String beforeFileUrl;
	/**
	 * 开奖公告地址
	 */
	@Value("${lotto_draw_url}")
	protected String lottoDrawUrl;
	/**
	 * 福彩3d遗漏走势的服务接口
	 */
	@Autowired
	private IF3dTrendService f3dTrendService;
	/**
	 * 排列三遗漏走势的服务接口
	 */
	@Autowired
	private IPl3TrendService pl3TrendService;
	/**
	 * 排列五遗漏走势的服务接口
	 */
	@Autowired
	private IPl5TrendService pl5TrendService;
	/**
	 * 七星彩遗漏走势的服务接口
	 */
	@Autowired
	private IQxcTrendService qxcTrendService;
	
	@Override
	public ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode) {
		// 0.参数合法性
		Assert.paramLegal(Lottery.contain(lotteryCode), "lotteryCode");
		// 1.缓存获取
		String key = CacheConstants.getLotteryTopKey(lotteryCode, null);
		LotteryIssueBaseBO target = (LotteryIssueBaseBO) redisUtil.getObj(key);
		if (target != null) {
			return ResultBO.ok(target);
		}
		logger.debug("测试用-1：" + JsonUtil.objectToJson(target));
		// 2.分彩种获取基本信息
		Lottery lot = Lottery.getLottery(lotteryCode);
		switch (lot) {
		case F3D:
			// 2.1.福彩3d除了彩种公共信息外，还要查询最新开奖中的开奖其它信息(如和值、跨度、奇偶比、大小比等)
			target = getLotteryIssueBase(lotteryCode);
			// 查询开奖其它信息并设值
			if(null != target && null != target.getLatestIssue()) {
				ResultBO<F3dDrawOtherBO> rs = f3dTrendService.findLatestDrawOther(new LotteryVO(lotteryCode, target.getLatestIssue().getIssueCode()));
				target.getLatestIssue().setOther(rs.getData());
			}
			break;
		case PL3:
			// 2.2.排列三除了彩种公共信息外，还要查询最新开奖中的开奖其它信息(如和值、跨度、奇偶比、大小比等)
			target = getLotteryIssueBase(lotteryCode);
			// 查询开奖其它信息并设值
			if(null != target && null != target.getLatestIssue()) {
				ResultBO<Pl3DrawOtherBO> rs = pl3TrendService.findLatestDrawOther(new LotteryVO(lotteryCode, target.getLatestIssue().getIssueCode()));
				target.getLatestIssue().setOther(rs.getData());
			}
			break;
		case PL5:
			// 2.3.排列五除了彩种公共信息外，还要查询最新开奖中的开奖其它信息(如和值、奇偶比、大小比等)
			target = getLotteryIssueBase(lotteryCode);
			// 查询开奖其它信息并设值
			if(null != target && null != target.getLatestIssue()) {
				ResultBO<Pl5DrawOtherBO> rs = pl5TrendService.findLatestDrawOther(new LotteryVO(lotteryCode, target.getLatestIssue().getIssueCode()));
				target.getLatestIssue().setOther(rs.getData());
			}
			break;
		case QXC:
			// 2.4.七星彩除了彩种公共信息外，还要查询最新开奖中的开奖其它信息(如大小比、奇偶比等)
			target = getLotteryIssueBase(lotteryCode);
			// 查询开奖其它信息并设值
			if(null != target && null != target.getLatestIssue()) {
				ResultBO<QxcDrawOtherBO> rs = qxcTrendService.findLatestDrawOther(new LotteryVO(lotteryCode, target.getLatestIssue().getIssueCode()));
				target.getLatestIssue().setOther(rs.getData());
			}
			break;
		default:
			// 2.2.默认其它彩种都公用
			target = getLotteryIssueBase(lotteryCode);
		}
		// 3.缓存设值
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}
	
	@Override
	public ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode, Short platform) {
		// 0.参数合法性
		Assert.paramLegal(Lottery.contain(lotteryCode), "lotteryCode");
		// 1.缓存获取
		String key = CacheConstants.getLotteryTopKey(lotteryCode, platform);
		LotteryIssueBaseBO target = (LotteryIssueBaseBO) redisUtil.getObj(key);
		if (target != null) {
			return ResultBO.ok(target);
		}
		logger.debug("测试用-2：" + JsonUtil.objectToJson(target));
		// 2.获取基本信息
		target = findLotteryIssueBase(lotteryCode).getData();
		// 3.按平台限制重置彩种状态
		resetLotSaleStatus(target.getCurLottery(), platform);
		// 4.缓存设值
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}
	
	@Override
	public ResultBO<LotteryIssueBaseBO> findLotteryIssueBase(Integer lotteryCode, Short platform,String channelId,List<DicDataDetailBO> dicDataList) {
		// 0.参数合法性
		Assert.paramLegal(Lottery.contain(lotteryCode), "lotteryCode");
		// 1.缓存获取
		String key = CacheConstants.getLotteryTopKey(lotteryCode, platform);
//		LotteryIssueBaseBO target = (LotteryIssueBaseBO) redisUtil.getObj(key);
//		if (target != null) {
//			return ResultBO.ok(target);
//		}
//		logger.debug("测试用-2：" + JsonUtil.objectToJson(target));
		// 2.获取基本信息
		LotteryIssueBaseBO target = findLotteryIssueBase(lotteryCode).getData();
		// 3.按平台限制重置彩种状态
		resetLotSaleStatus(target.getCurLottery(), platform);
		// 按渠道号限制重置彩种状态
		resetLotSaleStatusByChannel(target.getCurLottery(), channelId,dicDataList);
		// 4.缓存设值
		redisUtil.addObj(key, target, (long) Constants.DAY_1);
		return ResultBO.ok(target);
	}

	/**
	 * @desc 重新设置彩种销售状态<br>
	 *       1.根据限制平台重新设置销售状态,最终给到前端的状态只有（0：暂停销售；1：正常销售 ；）<br>
	 *       a>若彩种实际状态为（1：正常销售）,则不处理<br>
	 *       b>若彩种实际状态为（2：停止销售）,则直接重置为（ 0：暂停销售）<br>
	 *       c>若彩种实际状态为（0：暂停销售）,则判断彩种限制平台列表中是否包含api接口调用平台，若包含则不重置,否则重置销售状态为（1：正常销售）
	 * @author huangb
	 * @date 2017年11月9日
	 * @param target
	 *            目标彩种
	 * @param platform
	 * 		      	平台
	 */
	private void resetLotSaleStatus(LotteryBO target, Short platform) {
		if (null == target || null == target.getSaleStatus()) {
			return;
		}
		if (SaleStatus.STOP_SALE.getValue() == target.getSaleStatus().shortValue()) {
			target.setSaleStatus(SaleStatus.SUSPEND_SALE.getValue());
		} else if (SaleStatus.SUSPEND_SALE.getValue() == target.getSaleStatus().shortValue()) {
			if (ObjectUtil.isBlank(target.getPlatform()) || null == platform) {
				return;
			}
			// 判断彩种限制平台列表中是否包含api接口调用平台
			if (!(Arrays.asList(target.getPlatform().split(SymbolConstants.COMMA)).contains(String.valueOf(platform)))) {
				target.setSaleStatus(SaleStatus.NORMAL_SALE.getValue());
			}
		}
	}
	
	/**
	 * 根据渠道限制彩种状态
	 * @desc 
	 * @create 2018年6月26日
	 * @param target
	 * @param channelId void
	 */
	private void resetLotSaleStatusByChannel(LotteryBO target,String channelId,List<DicDataDetailBO> dicDataList){
		if (null == target || null == target.getSaleStatus()) {
			return;
		}
		if(dicDataList==null || dicDataList.size()<=0){
			return;
		}
		
		for (DicDataDetailBO dicDataDetailBO : dicDataList) {
			if(!dicDataDetailBO.getDicDataName().equals(target.getLotteryCode()+"")){
				continue;
			}
			String[] channels = dicDataDetailBO.getDicDataValue().split(",");
			for (String channel : channels) {
				if(channel.equals(channelId)){
					target.setSaleStatus(SaleStatus.SUSPEND_SALE.getValue());
					return;
				}
			}
		}
	}
	
	/**
	 * @desc 获取各彩种公共的基础信息(包括彩种、彩期、最新开奖、注/倍数配置、子玩法配置、限号配置)
	 * @author huangb
	 * @date 2017年6月28日
	 * @param lotteryCode
	 *            彩种编号
	 * @return 获取各彩种公共的基础信息(包括彩种、彩期、最新开奖、注/倍数配置、子玩法配置、限号配置)
	 */
	private LotteryIssueBaseBO getLotteryIssueBase(Integer lotteryCode) {
		// 2.查询获取
		LotteryVO temp = new LotteryVO(lotteryCode);
		LotteryBO curLottery = lotteryTypeDaoMapper.findSingleFront(temp);
		if (curLottery != null && !ObjectUtil.isBlank(curLottery.getLotteryLogoUrl())) { // 彩种logo地址处理
			curLottery.setLotteryLogoUrl(beforeFileUrl + curLottery.getLotteryLogoUrl());
		}
		IssueBO curIssue = lotteryIssueDaoMapper.findSingleFront(new LotteryVO(lotteryCode, ConIssue.CURRENT.getValue()));
        IssueBO nextIssue = lotteryIssueDaoMapper.findNextIssue(temp);
        IssueDrawBO latestIssue = lotteryIssueDaoMapper.findLatestDrawIssue(temp);
		List<LotBettingMulBO> lotBetMulList = lotteryBettingMulDaoMapper.findMultipleFront(temp);
		List<LotChildBO> lotChildList = lotteryChildDaoMapper.findMultipleFront(temp);
		temp.setStatus(LotteryLimitStatus.ENABLE.getValue()); // 限号状态(1：启用；2：禁用；3：过期)
		temp.setLimitDate(new Date()); // 设置限号时间
		List<LimitNumberInfoBO> limitInfoList = lotteryLimitMapper.findMultipleLimitFront(temp);

        return new LotteryIssueBaseBO(curLottery, curIssue, latestIssue, nextIssue, lotBetMulList, lotChildList, limitInfoList);
    }

	@Override
	public ResultBO<List<IssueDrawBO>> findRecentDrawIssue(LotteryVO lotteryVO) {
		return new ResultBO<List<IssueDrawBO>>(lotteryIssueDaoMapper.findRecentDrawIssue(lotteryVO));
	}

	@Override
	public List<NewIssueBO> listLotteryIssue(int lotteryCode, int num, String issueCode) {
		NewTypeBO lotteryBO = lotteryTypeDaoMapper.findTypeUseAddIssue(lotteryCode);
		Assert.notNull(lotteryBO, "参数错误,彩种不存在");
		// 只关心能生成彩期列表，不关心是系统生成还是抓取第三方生成（当彩种配置为同步彩期时会去抓取第三方生成，但并不是所有彩种都支持第三方抓取，所以这里配置为系统生成，稳定生成期号列表）
		lotteryBO.setSynIssue(SynIssue.NO_SYN.getValue()); 
		NewIssueBO issue = lotteryIssueDaoMapper.findLotteryIssue(lotteryCode, issueCode);
		List<NewIssueBO> list = IssueHandler.createIssue(lotteryBO, num, issue);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<IssueHomeBO> findHomeDrawLott() {
		List<CurrentAndPreIssueBO> prelist = (List<CurrentAndPreIssueBO>)lotteryIssueCacheService.getAllCurrentAndPreIssue().getData();
		IssueHomeBO issueHomeBO = new IssueHomeBO();
		for(CurrentAndPreIssueBO bo :prelist){
			if(bo.getLotteryCategory()==LotteryCategory.NUM.getValue()){
				if(DrawLotteryConstant.getLotteryKey(bo.getLotteryCode())!=null)
				bo.setDrawDetailUrl( lottoDrawUrl+ DrawLotteryConstant.getLotteryKey(bo.getLotteryCode()) + "/" + bo.getIssueCode() + ".html");
				issueHomeBO.getNumList().add(new IssueLottBO(bo,ConIssue.LAST_CURRENT.getValue()));
			}
			if(bo.getLotteryCategory()==LotteryCategory.HIGH.getValue()){
				if(DrawLotteryConstant.getLotteryKey(bo.getLotteryCode())!=null)
				bo.setDrawDetailUrl( lottoDrawUrl+ DrawLotteryConstant.getLotteryKey(bo.getLotteryCode()) + "/" + DateUtil.getNow(DateUtil.DATE_FORMAT_NO_LINE) + ".html");		
				issueHomeBO.getHighList().add(new IssueLottBO(bo,ConIssue.LAST_CURRENT.getValue()));
			}
			if(bo.getLotteryCategory()==LotteryCategory.SPORT.getValue()){
				if(bo.getLotteryCode()==Lottery.SFC.getName()||bo.getLotteryCode()==Lottery.ZC6.getName()||bo.getLotteryCode()==Lottery.JQ4.getName()||bo.getLotteryCode()==Lottery.ZC_NINE.getName()){
					if(DrawLotteryConstant.getLotteryKey(bo.getLotteryCode())!=null)
					bo.setDrawDetailUrl( lottoDrawUrl+ DrawLotteryConstant.getLotteryKey(bo.getLotteryCode()) + "/" + bo.getIssueCode() + ".html");
				}else{
					if(DrawLotteryConstant.getLotteryKey(bo.getLotteryCode())!=null)
					bo.setDrawDetailUrl( lottoDrawUrl+ DrawLotteryConstant.getLotteryKey(bo.getLotteryCode()) + "/");
				}				
				//任选9奖池数据拼接到14场中
				if(bo.getLotteryCode()==Lottery.ZC_NINE.getName())continue;
				if(bo.getLotteryCode()==Lottery.SFC.getName()){
					bo.setLotteryName(Lottery.SFC.getDesc());
					CurrentAndPreIssueBO rx9 = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(Lottery.ZC_NINE.getName()).getData();
					if(rx9!=null&&Objects.equals(rx9.getPreIssue(),bo.getPreIssue())){
						String r9detail = rx9.getPreDrawDetail()==null?"":rx9.getPreDrawDetail();
						r9detail =r9detail.replace(Constants.FIRST_PRIZE, Constants.RX9_PRIZE);
						if(!StringUtil.isBlank(bo.getPreDrawDetail())){
							bo.setPreDrawDetail(bo.getPreDrawDetail()+SymbolConstants.COMMA+r9detail);
						}else{
							bo.setPreDrawDetail(r9detail);
						}
					}
				}
				issueHomeBO.getSportList().add(new IssueLottBO(bo,ConIssue.LAST_CURRENT.getValue()));
			}
		}
		return new ResultBO<IssueHomeBO>(issueHomeBO);
	}
	
	/**
	 * 查询开奖公告上显示的双色球或者大乐透的开奖信息
	 * @return
	 */
	public ResultBO<IssueLottBO> findNewsHomeDraw(){
		CurrentAndPreIssueBO dltBo = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(Lottery.DLT.getName()).getData();
		CurrentAndPreIssueBO ssqBo = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(Lottery.SSQ.getName()).getData();
		IssueLottBO issueLottBO = null;
		if(dltBo.getPreLotteryTime()==null||ssqBo.getPreLotteryTime()==null){
			ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS);
		}
		if(dltBo.getPreLotteryTime().getTime()>dltBo.getPreLotteryTime().getTime()){
			issueLottBO = new IssueLottBO(dltBo,ConIssue.LAST_CURRENT.getValue());
		}else{
			issueLottBO = new IssueLottBO(ssqBo,ConIssue.LAST_CURRENT.getValue());
		}
		return new ResultBO<IssueLottBO>(issueLottBO);
	}
	
	/**
	 * 查询当天有开奖的低频彩彩种
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultBO<List<IssueLottBO>> findDrawNameToday(){
		List<CurrentAndPreIssueBO> prelist = (List<CurrentAndPreIssueBO>)lotteryIssueCacheService.getAllCurrentAndPreIssue().getData();
		List<IssueLottBO> retList= new ArrayList<IssueLottBO>();
		for(CurrentAndPreIssueBO bo : prelist){
			if(bo.getLotteryCategory()==(short)LotteryCategory.NUM.getValue()&&DateUtil.isToday(bo.getLotteryTime())){
				retList.add(new IssueLottBO(bo,ConIssue.CURRENT.getValue()));
			}
		}
		return new ResultBO<List<IssueLottBO>>(lotteryIssueDaoMapper.findDrawNameToday());
	}
	/**
	 * 查询资讯首页头部信息
	 * @return
	 */
	public ResultBO<OperteNewsHomeTopBO> findNewsHomeTop() {
		OperteNewsHomeTopBO bo = new OperteNewsHomeTopBO();
		bo.setDrawNotic(findNewsHomeDraw().getData());
		bo.setDrawTaday(findDrawNameToday().getData());	
	
		
		bo.setJcList(getNewJcList());
		return  ResultBO.ok(bo);
	}
	
	/**
	 * 根据顾着获取资讯头部的竞彩信息
	 * @return
	 */
	List<JcMathSPBO> getNewJcList(){
		CurrentAndPreIssueBO issueFB = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(Lottery.FB.getName()).getData();
		CurrentAndPreIssueBO issueBB = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(Lottery.BB.getName()).getData();
		if(issueFB.getPreLotteryTime()==null||issueBB.getPreLotteryTime()==null){
			ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS);
		}
		//查询竞彩篮球竞彩足球对阵信息
		List<JcMathSPBO>  fbList=  iJcDataService.findSportMatchFBSPInfo(Lottery.FB.getName(),issueFB.getIssueCode(),null);
		List<JcMathSPBO>  bbList=  iJcDataService.findSportMatchBBSPInfo(Lottery.BB.getName(),issueBB.getIssueCode(),null);
		//规则是如果竞彩足球两个位置，竞彩篮球一个位置
		List<JcMathSPBO> spList = new ArrayList<JcMathSPBO>();
		if(bbList ==null||bbList.size()==0){
			spList = fbList;	
		}
		if(fbList ==null||fbList.size()==0){
			spList = bbList;	
		}
		if(fbList !=null&&bbList !=null&&bbList.size()>0&&fbList.size()>0){
			if(fbList.size()>Constants.NUM_2){
				fbList = fbList.subList(0, Constants.NUM_2);
			}
			spList.addAll(fbList);
			spList.addAll(bbList);
		}
		
		if(spList!=null&&spList.size()>Constants.NUM_3){
			spList = spList.subList(0, Constants.NUM_3);
		}
		return spList;
	}
	/**
	 * @desc   查询限号信息
	 * @author Tony Wang
	 * @create 2017年3月28日
	 * @param vo
	 * @return 
	 */
	@Override
	public ResultBO<List<LimitNumberDetailBO>> findLimit(LotteryVO vo) {
		// 如果时间为空，则查询当前时间
		if(vo.getLimitDate() == null)
			vo.setLimitDate(new Date());
		/*
		 * 只查询有效的限号信息
		 * 此状态为lottery_limit表的，1：启用；2：禁用；3：过期
		 * lottery_limit_info的状态判断写在sql中
		 */
		vo.setStatus((short)1);
		return new ResultBO<List<LimitNumberDetailBO>>(lotteryLimitMapper.findMultipleLimitFrontByChild(vo));
	}
	
	/**
	 *  查询当前期和之后的预售期组成列表 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultBO<List<IssueLottJCBO>> findIssueByCode(int lotteryCode) {
		String key =  CacheConstants.C_COMM_LOTTERY_ISSUE_FIND_ISSUE_BY_CODE+lotteryCode;
		List<IssueLottJCBO> list = (List<IssueLottJCBO>)redisUtil.getObj(key);
		if(list==null){
			list = lotteryIssueDaoMapper.findIssueByCode(lotteryCode);
			redisUtil.addObj(key, list, (long)Constants.DAY_1);
		}
		return new ResultBO<>(list);
	}

	/**
	 * 根据彩种查询彩期列表。支持分页查询
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public ResultBO<PagingBO<IssueLottJCBO>> findIssueListByCode(final LottoIssueVO vo) {
		PagingBO<IssueLottJCBO> bo = pageService.getPageData(vo, new ISimplePage<IssueLottJCBO>() {
			@Override
			public int getTotal() {
				return lotteryIssueDaoMapper.findIssueListByCodeTotal(vo);
			}

			@Override
			public List<IssueLottJCBO> getData() {
				return lotteryIssueDaoMapper.findIssueListByCode(vo);
			}
		});
		return ResultBO.ok(bo);
	}

	/**
	 * 根据彩种查询当前期后5期彩期列表。支持分页查询
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public ResultBO<List<IssueLottJCBO>> findAfterFiveIssueListByCode(final LottoIssueVO vo) {
		vo.setPageIndex(0);
		vo.setPageSize(5);
		return ResultBO.ok(lotteryIssueDaoMapper.findAfterFiveIssueListByCode(vo));
	}

	/**
	 *  查询当前期和上一期信息
	 */
	@Override
	public ResultBO<CurrentAndPreIssueBO> findIssueAndPreIssueByCode(Integer lotteryCode) {
		CurrentAndPreIssueBO retBo = (CurrentAndPreIssueBO)lotteryIssueCacheService.getCurrentAndPreIssue(lotteryCode).getData();
		return new ResultBO<CurrentAndPreIssueBO>(retBo);

	}

	@Override
	public IssueBO findSingleFront(LotteryVO lotteryVO) {
		return lotteryIssueDaoMapper.findSingleFront(lotteryVO);
	}

	@Override
	public List<String> queryIssueByLottery(LotteryVO lotteryVO) {
		Assert.paramNotNull(lotteryVO, "lotteryCode");
		Assert.paramNotNull(lotteryVO.getLotteryCode(), "lotteryCode");
		Integer qryFlag = lotteryVO.getQryFlag();
		if(qryFlag == null) {
			lotteryVO.setQryFlag(Constants.NUM_1);
		} else {
			Assert.paramLegal(qryFlag ==1 || qryFlag == 2, "qryFlag");
		}
		return lotteryIssueDaoMapper.queryIssueByLottery(lotteryVO);
	}

	@Override
	public ResultBO<List<ChaseIssueBO>> findChaseIssue(Integer lotCode,String curIssue, Integer issueCount) {
		IssueBO issueBO = null;
		LotteryVO lotteryVO = new LotteryVO();
		lotteryVO.setLotteryCode(lotCode);
		if(StringUtil.isBlank(curIssue)){
			lotteryVO.setCurrentIssue((short)1);// TODO 当前期
			issueBO = lotteryIssueDaoMapper.findSingleFront(lotteryVO);
		}
		else
		{
			lotteryVO.setIssueCode(curIssue);
			issueBO = lotteryIssueDaoMapper.findSingleFront(lotteryVO);
		}
		if(issueCount == null)
			issueCount = NUMConstants.getLotChaseCount(lotCode);
		List<NewIssueBO> list = listLotteryIssue(lotCode, issueCount-1, issueBO.getIssueCode());
		List<ChaseIssueBO> issues = new ArrayList<ChaseIssueBO>();
		// 由于列表中不包含当前期，所有要特殊处理下(加入当前期，删除最后一期)
		ChaseIssueBO curIssueBO = new ChaseIssueBO(issueBO.getIssueCode(),issueBO.getLotteryTime());
		issues.add(curIssueBO);
		if (!ObjectUtil.isBlank(list)) {
			for (int i = 0; i < list.size(); i++) {
				NewIssueBO bo = list.get(i);
				issues.add(new ChaseIssueBO(bo.getIssueCode(),bo.getLotteryTime()));
			}
		}
		return new ResultBO<List<ChaseIssueBO>>(issues);
	}
}
