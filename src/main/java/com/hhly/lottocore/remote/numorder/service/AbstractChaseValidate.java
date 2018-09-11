package com.hhly.lottocore.remote.numorder.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseStopType;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseType;
import com.hhly.skeleton.base.common.ChaseEnum.ClientType;
import com.hhly.skeleton.base.common.ChaseEnum.YesOrNo;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.common.OrderEnum.CodeWay;
import com.hhly.skeleton.base.common.OrderEnum.DltAdd;
import com.hhly.skeleton.base.common.OrderEnum.PlatformType;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.issue.entity.NewIssueBO;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotWinningBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddIssueVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @desc 追号验证主流程
 * @author huangb
 * @date 2017年3月28日
 * @company 益彩网络
 * @version v1.0
 */
public abstract class AbstractChaseValidate extends BaseValidateService {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(AbstractChaseValidate.class);

	/**
	 * @desc 追号主流程入口
	 * @author huangb
	 * @date 2017年3月28日
	 * @param chase
	 *            追号对象
	 * @return 追号主流程入口
	 */
	public ResultBO<?> handleProcess(OrderAddVO chase) throws ResultJsonException {
		logger.info("=============追号验证流程 begin!=============");

		Assert.notNull(chase, "40501");
		logger.info("彩种：" + chase.getLotteryCode());
		// 初始化配置数据
		Map<String, Object> initCfg = initCfg(chase);
		
		// 1.验证追号计划主信息
		verifyOrderAdd(chase, initCfg);
		
		// 2.验证彩种的的销售状态,不分当前期和非当前期(只要彩种不可销售，则所有动作都不能进行(eg:代购、追号、合买等))
		LotteryBO curLottery = getCfg(Constants.NUM_1, initCfg);
		verifySaleStatus(curLottery, chase.getPlatform());

		// 3.追号列表中包含的当前期,验证彩期的销售状态(仅当前期校验；非当前期的不校验，待到生成订单时校验)
		IssueBO curIssue = getCfg(Constants.NUM_2, initCfg);
		// 是否包含当前期
		boolean existCurIssueFlag = existCurIssue(chase.getOrderAddIssueList(), curIssue.getIssueCode());
		setContainCurIssue(chase, existCurIssueFlag ? YesOrNo.Y.getValue() : YesOrNo.N.getValue()); // 设置是否包含当前期
		// 是否包含非当前期
		boolean existNoCurIssueFlag = existNoCurIssue(chase.getOrderAddIssueList(), existCurIssueFlag);
		if (existCurIssueFlag) {
			try {
				verifyIssueSaleStatus(curIssue);
			} catch (ResultJsonException ex) {
				throw new ResultJsonException(ResultBO.err("40514", curIssue.getIssueCode()));// 消息重定义
			}
		}

		// 4.验证追号内容(如果是选号追号，则需要校验内容；随机追号的不需要校验)
		ChaseType chaseType = ChaseType.getChaseType(chase.getAddType());
		// 每期的追号注数(选号追号的注数是投注栏总注数；随机追号的注数是输入注数)
		int eachChaseBetNum = 0;
		// 每期的追号金额(选号追号的金额是投注栏总金额；随机追号的金额是输入注数*2(单注金额))
		double eachChaseMoney = 0;// 所有内容明细总金额
		switch (chaseType) {
		case FIXED_NUMBER:
			List<OrderDetailVO> chaseContents = chase.getOrderAddContentList();
			for (OrderDetailVO content : chaseContents) {
				// 验证固定追号内容
				verifyFixedContent(chase, content, initCfg, existCurIssueFlag, curIssue, existNoCurIssueFlag);
				// 计算所有内容明细总注数
				eachChaseBetNum += content.getBuyNumber().intValue();
				// 计算所有内容明细总金额
				eachChaseMoney = NumberUtil.sum(eachChaseMoney, content.getAmount());
			}
			break;
		case RANDOM_NUMBER:
			// 验证随机追号内容
			verifyRandomContent(chase, initCfg, existCurIssueFlag, curIssue, existNoCurIssueFlag);
			eachChaseBetNum = chase.getAddCount().intValue(); // 该注数是前台输入的
			// 随机追号的金额是输入注数*2(单注金额)(不包含倍数的金额)
			eachChaseMoney = eachChaseBetNum * Constants.getPriceByLotChild(getRandomContentChildCode(), Constants.getPrice(chase.getIsDltAdd()));
			break;
		default:
			throw new ResultJsonException(ResultBO.err("40506"));
		}

		// 4.1.处理追号彩期列表 （针对移动端）
		handleAddIssueList(chase, eachChaseBetNum, eachChaseMoney);
		
		// 5.追号期号信息验证
		int issueTotalMulti = 0;
		double issueTotalAmount = 0d;
		Set<String> issueCodeSet = new HashSet<String>(); // 存储期号，用于期号重复性验证
		List<OrderAddIssueVO> chaseIssues = chase.getOrderAddIssueList();
		for (OrderAddIssueVO issue : chaseIssues) {
			verifyOrderAddIssue(issue, initCfg, eachChaseBetNum, eachChaseMoney, curIssue);
			// 计算所有彩期明细总倍数
			issueTotalMulti += issue.getMultiple().intValue();
			// 计算所有彩期明细总金额
			issueTotalAmount = NumberUtil.sum(issueTotalAmount, issue.getBuyAmount());
			// 存期号，用于重复性验证
			issueCodeSet.add(issue.getIssueCode());
		}
		
		// 5.1.期号重复性验证
		Assert.isTrue(chaseIssues.size() == issueCodeSet.size(), "40546");
		
		// 6. 追号计划追号注数（每期追号注数），总倍数与总金额匹配验证
		verifyOrderAddMatch(chase, eachChaseBetNum, issueTotalMulti, issueTotalAmount);

		// 7.设置支付截止时间
		setPayEndTime(chase, initCfg);
		
		logger.info("=============追号验证流程 end!=============");
		return ResultBO.ok();
	}
	
	
	/**
	 * 追号订单  不验证 （仅提供部分关键字段的设值）,仅供内容调用； 年会活动有用到
	 * @desc 
	 * @create 2018年1月9日
	 * @param chase
	 * @return
	 * @throws ResultJsonException ResultBO<?>
	 */
	public ResultBO<?> handleProcessWithoutVerify(OrderAddVO chase) throws ResultJsonException {
		logger.info("=============追号不验证流程 begin!=============");
		// 初始化配置数据
		Map<String, Object> initCfg = initShortCfg(chase);
		IssueBO curIssue = getCfg(Constants.NUM_2, initCfg);
				
		// 注：下面的字段可不传，其它字段必须传，且正确性由调用方控制
		// 1.确定平台(默认：h5)、渠道(默认：6,本站wap)的默认值设值
		if (null == chase.getPlatform() || !PlatformType.contain(chase.getPlatform())) {
			chase.setPlatform(PlatformType.WAP.getValue());
		}
		if (StringUtil.isBlank(chase.getChannelId())) {
			chase.setChannelId(String.valueOf(Constants.NUM_6));
		}
		// 2.停追类型的默认值设值
		if (null == chase.getStopType() || !ChaseStopType.contain(chase.getStopType())) {
			chase.setStopType(ChaseStopType.ALWAYS.getValue());
		}
		// 3.是否大乐透追加的默认值设值
		if (null == chase.getIsDltAdd() || !DltAdd.contain(chase.getIsDltAdd())) {
			chase.setIsDltAdd(DltAdd.NO.getValue());
		}
		// 4.追号类型的默认值设值
		chase.setAddType(ChaseType.FIXED_NUMBER.getValue());
		// 4.1追号主表的彩期默认值设值
		if(StringUtil.isBlank(chase.getIssueCode())) {
			chase.setIssueCode(curIssue.getIssueCode());
		}

		// 5.对象中必须包含userId的设值
		int noPayCount = findNoPayOrderCount(chase.getLotteryCode(), chase.getUserId(), curIssue.getIssueCode());
		setNoPayCount(chase, noPayCount + 1);// 设置未支付订单数量(下单成功后返回前端用)，这里+1是把当前下的单也包含进来

		// 6.是否包含当前期的设值
		boolean existCurIssueFlag = existCurIssue(chase.getOrderAddIssueList(), curIssue.getIssueCode());
		setContainCurIssue(chase, existCurIssueFlag ? YesOrNo.Y.getValue() : YesOrNo.N.getValue()); // 设置是否包含当前期

		// 7.设置支付截止时间
		setPayEndTime(chase, initCfg);

		logger.info("=============追号不验证流程 end!=============");
		return ResultBO.ok();
	}
	/**
	 * @desc 验证追号计划主信息
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划信息
	 * @param initCfg
	 *            初始化配置
	 * @return 验证追号计划主信息
	 */
	private ResultBO<?> verifyOrderAdd(OrderAddVO chase, Map<String, Object> initCfg) {
		// 1.验证追号计划主信息非空字段
		verifyChaseRequired(chase);
		
		// 1.1.验证并初始化追号彩期列表 （区分PC端和移动端）
		verifyAddIssueList(chase, initCfg);

		// 2.验证追号计划主信息正确性
		verifyOrderAddLegal(chase, initCfg);
		
		// 3.验证投注方案最大数量,选号追号需验证，随机追号不验证
		if (ChaseType.FIXED_NUMBER.getValue() == chase.getAddType().shortValue()) {
			verifyBetPlanMaxCount(chase.getOrderAddContentList().size(), Constants.NUM_1, Constants.MAX_BET_PLAN);
		}
		
		return ResultBO.ok();
	}

	/**
	 * @desc 验证追号计划提交字段数据的正确性
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划对象
	 * @return 验证追号计划提交字段数据的正确性
	 */
	private ResultBO<?> verifyChaseRequired(OrderAddVO chase) {
		/*** 追号计划各字段校验 ***/
		// 1.彩种
		Assert.notNull(chase.getLotteryCode(), "40502");
		Assert.isTrue(Lottery.contain(chase.getLotteryCode()), "40502");
		// 2.彩期
		Assert.hasText(chase.getIssueCode(), "40503");
		// 3.追号计划总额
		Assert.isTrue((chase.getAddAmount() != null && NumberUtil.compareTo(chase.getAddAmount(), 1d) > 0), "40504");
		
		// 4.追号计划总倍数（该字段无实际操作意义）
		Assert.notNull(chase.getMultipleNum(), "40505");
		// 5.追号类型
		Assert.notNull(chase.getAddType(), "40506");
		verifyChaseType(chase.getAddType());
		// 6.追号投注数(投注数不能为空,随机追号注数为输入，固定追号注数为追号内容总注数)
		Assert.notNull(chase.getAddCount(), "40507");
		// 7.追号投注内容(addType=1(选号追号)，内容不能为空)
		if (ChaseType.FIXED_NUMBER.getValue() == chase.getAddType().shortValue()) {
			Assert.notEmpty(chase.getOrderAddContentList(), "40508");
		}
		// 8.追号彩期
		/*if (ObjectUtil.isBlank(chase.getOrderAddIssueList())) {
			throw new ResultJsonException(ResultBO.err("40509"));
		}*/
		// 9.用户ID    看13点
		/*if (ObjectUtil.isBlank(chase.getUserId())) {
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.USER_TOKEN_IS_NULL_FIELD));
		}*/
		// 10.停追类型
		Assert.notNull(chase.getStopType(), "40536");
		Assert.isTrue(ChaseStopType.contain(chase.getStopType()), "40536");
		// 11.停追条件
		if (ChaseStopType.ALWAYS.getValue() != chase.getStopType().shortValue()) {
			Assert.hasText(chase.getStopCondition(), "40537");
			Assert.isTrue(chase.getStopCondition().length() <= Constants.NUM_10, "40537");
		}
		// 12.仅有大乐透才能追加
		Assert.notNull(chase.getIsDltAdd(), "40538");
		Assert.isTrue(DltAdd.contain(chase.getIsDltAdd()), "40538");
		if (DltAdd.YES.getValue() == chase.getIsDltAdd().shortValue()) {
			Assert.isTrue(Lottery.DLT.getName() == chase.getLotteryCode().intValue(), "40539");
		}
		// 13.用户token
		Assert.hasText(chase.getToken(), MessageCodeConstants.USER_TOKEN_IS_NULL_FIELD);
		// 14.渠道id
		Assert.hasText(chase.getChannelId(), "40260");
		// 15.投注平台
		Assert.notNull(chase.getPlatform(), "40547");
		Assert.isTrue(PlatformType.contain(chase.getPlatform()), "40547");
		
		return ResultBO.ok();
	}
	
	/**
	 * @desc 验证追号信息合法性
	 * @author huangb
	 * @date 2017年4月1日
	 * @param chase
	 *            追号计划信息
	 * @param initCfg
	 *            初始化配置
	 * @return 验证追号信息合法性
	 */
	private ResultBO<?> verifyOrderAddLegal(OrderAddVO chase, Map<String, Object> initCfg) {
		ChaseStopType type = ChaseStopType.getChaseStopType(chase.getStopType());
		// 1.追号类型按奖项、金额验证追号条件合法性
		switch (type) {
		case PRIZE:
			// 彩种奖项列表
			Map<Integer, LotWinningBO> winningMap = getCfg(Constants.NUM_7, initCfg);
			Assert.isTrue(NumberUtil.isDigits(chase.getStopCondition()), "40541");
			Assert.notEmpty(winningMap, "40541");
			Assert.isTrue(winningMap.containsKey(Integer.valueOf(chase.getStopCondition())), "40541");
			break;
		case AMOUNT:
			Assert.isTrue(NumberUtil.isNumeric(chase.getStopCondition()), "40540");// 1.正整数
			Long tmp = Long.valueOf(chase.getStopCondition());
			Assert.isTrue(tmp < Constants.NUM_1BIL, "40540");// 2.小于10亿
			chase.setStopCondition(String.valueOf(tmp));// 重新赋值（避免像001111这种数字存入）
			break;
		}
		
		// 1.1.追号计划期号必须是当前期
		IssueBO curIssue = getCfg(Constants.NUM_2, initCfg);
		Assert.isTrue(chase.getIssueCode().equals(curIssue.getIssueCode()), MessageCodeConstants.NOT_CURRENT_ISSUE, chase.getIssueCode());

		// 2.验证追号用户合法性并设置用户id
		UserInfoBO loginUser = getLoginUser(chase.getToken());
		Assert.notNull(loginUser, MessageCodeConstants.USERNAME_IS_NOT_FOUND_SERVICE);
		Assert.isTrue(null != loginUser.getAccountStatus() && Constants.NUM_1 == loginUser.getAccountStatus().intValue(), MessageCodeConstants.ACCOUNT_IS_FORBIDDEN_SERVICE);
		setUserId(chase, loginUser.getId());// 设置追号用户id
		
		// 2.1 账号实名认证 20170609    // 20170621取消该验证，统一支付时验证
		// Assert.hasText(loginUser.getIdCard(), MessageCodeConstants.ACCOUNT_NOT_REALNAME_AUTHENTICTION_SERVICE);
		
		// 3.验证当前用户未支付订单情况
		int noPayCount = findNoPayOrderCount(chase.getLotteryCode(), loginUser.getId(), curIssue.getIssueCode());
		Assert.isTrue(noPayCount < Integer.valueOf(limitCount), MessageCodeConstants.ORDER_NOT_PAY_COUNT_BEYONG);
		setNoPayCount(chase, noPayCount + 1);// 设置未支付订单数量(下单成功后返回前端用)，这里+1是把当前下的单也包含进来
		
		return ResultBO.ok();
	}
	
	/**
	 * @desc 追号计划总倍数与总金额匹配验证
	 * @author huangb
	 * @date 2017年3月28日
	 * @param chase
	 *            追号计划信息
	 * @param eachChaseBetNum
	 *            每期追号注数
	 * @param issueTotalMulti
	 *            彩期总倍数
	 * @param issueTotalAmount
	 *            彩期总金额
	 * @return 追号计划总倍数与总金额匹配验证
	 */
	private ResultBO<?> verifyOrderAddMatch(OrderAddVO chase, int eachChaseBetNum, int issueTotalMulti, double issueTotalAmount) {

		// 0.每期追号注数是否和内容注数相等 （主要针对固定内容选号注数的匹配）
		Assert.isTrue(eachChaseBetNum == chase.getAddCount().intValue(), "40545");

		// 1.追号总倍数 = 每期的追号倍数之和
		Assert.isTrue(issueTotalMulti == chase.getMultipleNum().intValue(), "40534");

		// 2.追号计划总金额 = 每期的追号金额之和
		Assert.isTrue(NumberUtil.compareTo(chase.getAddAmount(), issueTotalAmount) == Constants.NUM_0, "40535");

		return ResultBO.ok();
	}

	/**
	 * @desc 单独验证追号彩期，PC端（验证期数列表）和移动端（验证追号期数和倍数）
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划信息
	 * @param initCfg
	 *            初始化配置
	 * @return 单独验证追号彩期，PC端（验证期数列表）和移动端（验证追号期数和倍数）
	 */
	protected ResultBO<?> verifyAddIssueList(OrderAddVO chase, Map<String, Object> initCfg) {
		
		if(ClientType.PC.getValue() == chase.getSource().shortValue()) {// 如果是pc，验证追号彩期列表
			// 8.追号彩期 
			Assert.notEmpty(chase.getOrderAddIssueList(), "40509");
		} else { // 如果是移动端
			// 20171014 add  移动端添加高级追号，移动端要区分普通追号和高级追号（高级追号提交的数据结构同pc端追号的数据结构）;如果追号期列表为空则认为是普通追号,走普通追号的设置;不为空则认为是高级追号
			// 根据追号期列表判断是否是高级追号
			chase.setHighChase(ObjectUtil.isBlank(chase.getOrderAddIssueList()) ? false : true);
			if(!chase.isHighChase()) {// (移动端普通追号)
				List<String> chaseIssueList = getCfg(Constants.NUM_6, initCfg);
				// 1.追号期数不为空
				Assert.notNull(chase.getAddIssues(), "40542");
				// 2.追号期数范围
				Assert.isTrue(chase.getAddIssues().intValue() >= Constants.NUM_1, "40543", chaseIssueList.size());
				Assert.isTrue(chase.getAddIssues().intValue() <= chaseIssueList.size(), "40543", chaseIssueList.size());
				// 3.追号倍数不为空
				Assert.notNull(chase.getAddMultiples(), "40544");
				// 4.初始化追号彩期列表
				initAddIssueList(chase, initCfg);
			}
		}
		return ResultBO.ok();
	}
	/**
	 * @desc 初始化追号彩期列表,即先初始化彩期和倍数，注数和金额不处理；(移动端专用)
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划信息
	 * @param initCfg
	 *            初始化配置
	 * @return 初始化追号彩期列表,即先初始化彩期和倍数，注数和金额不处理；(移动端专用)
	 */
	private ResultBO<?> initAddIssueList(OrderAddVO chase, Map<String, Object> initCfg) {

		if (ClientType.PC.getValue() != chase.getSource().shortValue() && !chase.isHighChase()) {// (移动端普通追号)
			List<String> chaseIssueList = getCfg(Constants.NUM_6, initCfg);
			// 1.初始化追号彩期列表,先赋值彩期和倍数
			List<OrderAddIssueVO> orderAddIssueList = new ArrayList<OrderAddIssueVO>();
			OrderAddIssueVO temp = null;
			for (int i = 0; i < chase.getAddIssues().intValue(); i++) {
				temp = new OrderAddIssueVO(chaseIssueList.get(i), null, chase.getAddMultiples(), null);
				orderAddIssueList.add(temp);
			}
			// 设值
			chase.setOrderAddIssueList(orderAddIssueList);
		}
		return ResultBO.ok();
	}
	
	/**
	 * @desc 处理追号彩期列表，主要处理注数和金额 ；(移动端专用)
	 * @author huangb
	 * @date 2017年4月7日
	 * @param chase
	 *            追号计划信息
	 * @param eachChaseBetNum
	 *            每期追号注数
	 * @param eachChaseMoney
	 *            每期追号内容金额
	 * @return 处理追号彩期列表，主要处理注数和金额 ；(移动端专用)
	 */
	protected ResultBO<?> handleAddIssueList(OrderAddVO chase, int eachChaseBetNum, double eachChaseMoney) {
		if (ClientType.PC.getValue() != chase.getSource().shortValue() && !chase.isHighChase()) {// (移动端普通追号)
			// 处理注数和金额
			for (OrderAddIssueVO temp : chase.getOrderAddIssueList()) {
				temp.setChaseBetNum(eachChaseBetNum);
				temp.setBuyAmount(NumberUtil.mul(temp.getMultiple(), eachChaseMoney));
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * @desc 验证固定追号内容
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划信息
	 * @param content
	 *            追号内容信息
	 * @param initCfg
	 *            初始化配置
	 * @param existCurIssueFlag
	 *            是否包含当期标识 true/false
	 * @param existNoCurIssueFlag
	 *            是否包含非当期标识 true/false
	 * @param curIssue
	 *            当前期
	 * @return 验证固定追号内容
	 */
	private ResultBO<?> verifyFixedContent(OrderAddVO chase, OrderDetailVO content, Map<String, Object> initCfg,
			boolean existCurIssueFlag, IssueBO curIssue, boolean existNoCurIssueFlag) {
		// 1.验证追号内容非空字段
		verifyContentRequired(content);

		// 2.处理追号内容，包括验证追号内容及追号注数返回（各彩种单独实现）
		List<LimitNumberInfoBO> limitList = getCfg(Constants.NUM_5, initCfg);
		// 2.1.加个判断，如果不包含当前期，则不验证限号，即限号列表置空
		limitList = existCurIssueFlag ? limitList : Collections.<LimitNumberInfoBO> emptyList();
		ResultBO<?> result = handleChaseContent(content, limitList);

		// 3.验证追号内容一致性，包括注数、倍数和金额关系(解析注数)
		verifyFixedContentMatch(chase, content, result);

		// 注数配置
		List<LotBettingMulBO> lotBetMulCfg = getCfg(Constants.NUM_4, initCfg);
		// 4.验证当前期与非当前期的注数限制
		if (existCurIssueFlag) {
			// a.验证追号内容子玩法销售状态(不分当前期与非当前期)
			Map<Integer, LotChildBO> lotChildCfg = getCfg(Constants.NUM_3, initCfg);
			verifySubPlaySaleStatus(lotChildCfg, content.getLotteryChildCode());

			// b.验证固定追号一次投注的最大注数
			try {
				verifyOneBetMaxNum(curIssue, lotBetMulCfg, content.getPlanContent(), content.getBuyNumber());
			} catch (ResultJsonException ex) {
				throw new ResultJsonException(ResultBO.err("40521", curIssue.getIssueCode(), content.getPlanContent(),
						ex.getResult().getData()));
			}
		}
		// 验证非当前期
		if (existNoCurIssueFlag) {
			// a.验证固定追号一次投注的最大注数(非当前期则假设开奖时间无限大(这里用Integer.MAX_VALUE)，并且以最大的注数限制条件限制)
			try {
				verifyOneBetMaxNum(Integer.MAX_VALUE, lotBetMulCfg, content.getPlanContent(), content.getBuyNumber());
			} catch (ResultJsonException ex) {
				throw new ResultJsonException(
						ResultBO.err("40522", content.getPlanContent(), ex.getResult().getData()));
			}
		}
		return ResultBO.ok();
	}

	/**
	 * @desc 验证随机追号内容
	 * @author huangb
	 * @date 2017年3月20日
	 * @param chase
	 *            追号计划信息
	 * @param content
	 *            追号内容信息
	 * @param initCfg
	 *            初始化配置
	 * @param existCurIssueFlag
	 *            是否包含当期标识 true/false
	 * @param curIssue
	 *            当前期
	 * @param existNoCurIssueFlag
	 *            是否包含非当期标识 true/false
	 * @return 验证随机追号内容
	 */
	private ResultBO<?> verifyRandomContent(OrderAddVO chase, Map<String, Object> initCfg, boolean existCurIssueFlag,
			IssueBO curIssue, boolean existNoCurIssueFlag) {

		// 注数配置
		List<LotBettingMulBO> lotBetMulCfg = getCfg(Constants.NUM_4, initCfg);
		// 1.验证当前期与非当前期的注数限制
		if (existCurIssueFlag) {
			// a.验证随机追号子玩法销售状态(随机追号属于普通玩法)
			Map<Integer, LotChildBO> lotChildCfg = getCfg(Constants.NUM_3, initCfg);
			verifySubPlaySaleStatus(lotChildCfg, getRandomContentChildCode());

			// b.验证随机追号一次投注的最大注数 (随机追号时可以把输入的注数作为一注复式票的注数，进而进行限制)
			try {
				verifyOneBetMaxNum(curIssue, lotBetMulCfg, "", chase.getAddCount());
			} catch (ResultJsonException ex) {
				throw new ResultJsonException(ResultBO.err("40523", curIssue.getIssueCode(), ex.getResult().getData()));
			}
		}
		// 验证非当前期
		if (existNoCurIssueFlag) {
			// a.验证随机追号一次投注的最大注数(非当前期则假设开奖时间无限大(这里用Integer.MAX_VALUE)，并且以最大的注数限制条件限制)
			try {
				verifyOneBetMaxNum(Integer.MAX_VALUE, lotBetMulCfg, "", chase.getAddCount());
			} catch (ResultJsonException ex) {
				throw new ResultJsonException(ResultBO.err("40524", ex.getResult().getData()));
			}
		}
		return ResultBO.ok();
	}

	/**
	 * @desc 验证追号内容数据的正确性
	 * @author huangb
	 * @date 2017年3月20日
	 * @param content
	 *            追号内容
	 * @return 验证追号内容数据的正确性
	 */
	private ResultBO<?> verifyContentRequired(OrderDetailVO content) {

		// a.投注内容
		Assert.hasText(content.getPlanContent(), MessageCodeConstants.PLAN_CONTENT_IS_NULL_FIELD);
		// b.投注注数
		Assert.notNull(content.getBuyNumber(), MessageCodeConstants.ORDER_DETAIL_BETNUM_IS_NULL_FIELD, content.getPlanContent());
		// c.投注倍数
		Assert.notNull(content.getMultiple(), MessageCodeConstants.ORDER_DETAIL_MULTIPLE_IS_NULL_FIELD, content.getPlanContent());
		// d.投注金额
		Assert.notNull(content.getAmount(), MessageCodeConstants.ORDER_DETAIL_AMOUNT_IS_NULL_FIELD, content.getPlanContent());
		// e.子玩法
		Assert.notNull(content.getLotteryChildCode(), "40437", content.getPlanContent());
		Assert.isTrue(LotteryChild.contain(content.getLotteryChildCode()), "40437", content.getPlanContent());
		// f.选号方式
		Assert.notNull(content.getCodeWay(), MessageCodeConstants.BET_CODE_WAY_ILLEGAL, content.getPlanContent());
		Assert.isTrue(CodeWay.contain(content.getCodeWay()), MessageCodeConstants.BET_CODE_WAY_ILLEGAL,
				content.getPlanContent());
		// g.内容类型
		Assert.notNull(content.getContentType(), "40515", content.getPlanContent());
		Assert.isTrue(BetContentType.contain(content.getContentType()), "40515", content.getPlanContent());

		return ResultBO.ok();
	}

	/**
	 * @desc 验证固定追号内容一致性，包括注数、倍数和金额关系
	 * @author huangb
	 * @date 2017年3月21日
	 * @param chase
	 *            追号对象
	 * @param content
	 *            追号内容
	 * @param result
	 *            追号内容处理的结果(其中包含计算的注数)
	 * @return 验证固定追号内容一致性，包括注数、倍数和金额关系
	 */
	@SuppressWarnings("unchecked")
	private ResultBO<?> verifyFixedContentMatch(OrderAddVO chase, OrderDetailVO content, ResultBO<?> result) {
		// 追号内容的注数
		int targetBetNum = Constants.NUM_0;
		// 1.追号内容的单注金额
		double singleAmount = Constants.getPriceByLotChild(content.getLotteryChildCode(), Constants.getPrice(chase.getIsDltAdd()));
		if (null != result.getData()) {
			Map<String, Integer> dataMap = (Map<String, Integer>) result.getData();
			if (null != dataMap.get(Constants.BET_NUM_KEY)) {
				targetBetNum = dataMap.get(Constants.BET_NUM_KEY);// 投注解析注数
			}
		}
		// 2.注数匹配
		Assert.isTrue(targetBetNum == content.getBuyNumber().intValue(), "40518", content.getPlanContent());
		// 3.金额匹配；金额 = 注数 * 2(单注金额)*单注倍数
		double targetBetMoney = targetBetNum * content.getMultiple().intValue() * singleAmount;
		Assert.isTrue(NumberUtil.compareTo(targetBetMoney, content.getAmount()) == Constants.NUM_0, "40519",
				content.getPlanContent());

		return ResultBO.ok();
	}

	/**
	 * @desc 追号彩期验证
	 * @author huangb
	 * @date 2017年3月20日
	 * @param issue
	 *            追号彩期信息
	 * @param initCfg
	 *            初始化配置
	 * @param eachChaseBetNum
	 *            每期投注数
	 * @param eachChaseMoney
	 *            每期投注金额
	 * @return 追号彩期验证
	 */
	private ResultBO<?> verifyOrderAddIssue(OrderAddIssueVO issue, Map<String, Object> initCfg, int eachChaseBetNum,
			double eachChaseMoney, IssueBO curIssue) {
		// 1.验证追号彩期非空字段
		verifyIssueRequired(issue);

		// 2.验证追号彩期一致性，包括注数、倍数和金额关系
		verifyIssueMatch(issue, initCfg, eachChaseBetNum, eachChaseMoney);

		// 倍数配置
		List<LotBettingMulBO> lotBetMulCfg = getCfg(Constants.NUM_4, initCfg);
		// 3.验证当前期与非当前期的倍数限制
		try {
			if (issue.getIssueCode().equals(curIssue.getIssueCode())) {
				// 当前期，取当前期的可投倍数限制
				verifyBetMaxMulti(curIssue, lotBetMulCfg, issue.getMultiple());
			} else {
				// 非当前期，(非当前期则假设开奖时间无限大(这里用Integer.MAX_VALUE)，并且以最大的倍数限制条件限制)
				verifyBetMaxMulti(Integer.MAX_VALUE, lotBetMulCfg, issue.getMultiple());
			}
		} catch (ResultJsonException ex) {
			throw new ResultJsonException(ResultBO.err("40533", issue.getIssueCode(), ex.getResult().getData()));
		}

		return ResultBO.ok();
	}
	/**
	 * @desc 验证追号彩期数据的正确性
	 * @author huangb
	 * @date 2017年3月20日
	 * @param issue
	 *            追号彩期
	 * @return 验证追号彩期数据的正确性
	 */
	private ResultBO<?> verifyIssueRequired(OrderAddIssueVO issue) {

		// a.期号
		Assert.hasText(issue.getIssueCode(), "40525");
		// b.每期注数
		Assert.notNull(issue.getChaseBetNum(), "40526", issue.getIssueCode());
		// c.每期倍数
		Assert.notNull(issue.getMultiple(), "40527", issue.getIssueCode());
		// d.每期金额
		Assert.notNull(issue.getBuyAmount(), "40528", issue.getIssueCode());
		
		return ResultBO.ok();
	}

	/**
	 * @desc 验证追号彩期一致性，包括注数、倍数和金额关系
	 * @author huangb
	 * @date 2017年3月21日
	 * @param issue
	 *            追号彩期
	 * @param initCfg
	 *            配置数据
	 * @param eachChaseBetNum
	 *            每期投注数
	 * @param eachChaseMoney
	 *            每期投注金额
	 * @return 验证追号彩期一致性，包括注数、倍数和金额关系
	 */
	private ResultBO<?> verifyIssueMatch(OrderAddIssueVO issue, Map<String, Object> initCfg, int eachChaseBetNum, double eachChaseMoney) {

		// 1.追号期号是否合法(即目标彩期是否在可选的期号列表中)
		List<String> chaseIssueList = getCfg(Constants.NUM_6, initCfg);
		Assert.isTrue(containChaseIssue(chaseIssueList, issue.getIssueCode()), "40529", issue.getIssueCode());
		
		// 2.追号注数匹配
		Assert.isTrue(eachChaseBetNum == issue.getChaseBetNum().intValue(), "40530", issue.getIssueCode());

		// 3.追号金额匹配; 追号金额 = 每期的投注金額*追号倍数
		double targetChaseMoney = issue.getMultiple().intValue() * eachChaseMoney;
		Assert.isTrue(NumberUtil.compareTo(targetChaseMoney, issue.getBuyAmount()) == Constants.NUM_0, "40531", issue.getIssueCode());
		
		return ResultBO.ok();
	}

	/**
	 * @desc 是否包含目标追号期号(即是否是在可选的期号列表中)
	 * @author huangb
	 * @date 2017年3月21日
	 * @param chaseIssueList
	 *            可追的彩期列表
	 * @param targetIssue
	 *            目标追号彩期
	 * @return 是否包含目标追号期号(即是否是在可选的期号列表中)
	 */
	private boolean containChaseIssue(List<String> chaseIssueList, String targetIssue) {
		if (ObjectUtil.isBlank(chaseIssueList)) {
			return false;
		}
		/*for (String temp : chaseIssueList) {
			if (temp.equals(targetIssue)) {
				return true;
			}
		}*/
		return chaseIssueList.contains(targetIssue);
	}

	/**
	 * @desc 追号期号列表中是否包含当前期期号
	 * @author huangb
	 * @date 2017年3月21日
	 * @param chaseIssueList
	 *            追号期号列表
	 * @param curIssueCode
	 *            当前期期号
	 * @return
	 */
	private boolean existCurIssue(List<OrderAddIssueVO> chaseIssueList, String curIssueCode) {
		if (ObjectUtil.isBlank(chaseIssueList)) {
			return false;
		}
		for (OrderAddIssueVO temp : chaseIssueList) {
			if (temp != null && !StringUtil.isBlank(temp.getIssueCode()) && temp.getIssueCode().equals(curIssueCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @desc 追号期号列表中是否包含非当前期期号
	 * @author huangb
	 * @date 2017年3月21日
	 * @param chaseIssueList
	 *            追号期号列表
	 * @param existCurIssueFlag
	 *            是否存在当前期true/false
	 * @return 追号期号列表中是否包含非当前期期号
	 */
	private boolean existNoCurIssue(List<OrderAddIssueVO> chaseIssueList, boolean existCurIssueFlag) {
		if ((!existCurIssueFlag && chaseIssueList.size() > Constants.NUM_0)
				|| (existCurIssueFlag && chaseIssueList.size() > Constants.NUM_1)) {
			return true;
		}
		return false;
	}

	/**
	 * @desc 获取初始化的验证配置数据(包括追号彩种、追号彩期、当前期、子玩法配置、注/倍数配置、限号列表、追号彩期列表),
	 *       这样可以避免在后续多次查询数据库
	 * @author huangb
	 * @date 2017年3月21日
	 * @param chase
	 *            追号计划
	 * @return
	 */
	private Map<String, Object> initCfg(OrderAddVO chase) {
		Map<String, Object> initCfg = new HashMap<String, Object>();
		Integer chaseLotCode = chase.getLotteryCode();
		Assert.notNull(chaseLotCode, "40502");
		// 1.追号彩种
		LotteryBO curLottery = findLottery(chaseLotCode);
		Assert.notNull(curLottery, "40502");
		initCfg.put("curLottery", curLottery);
		// 2.当前期
		IssueBO curIssue = findCurIssue(chaseLotCode);
		Assert.notNull(curIssue, MessageCodeConstants.CURRENT_ISSUE_EMPTY);
		initCfg.put("curIssue", curIssue);
		// 3.彩种对应的子玩法配置
		List<LotChildBO> subPlayList = findLotteryChild(chaseLotCode);
		if (!ObjectUtil.isBlank(subPlayList)) {
			Map<Integer, LotChildBO> subPlayMap = new HashMap<Integer, LotChildBO>();
			for (LotChildBO temp : subPlayList) {
				subPlayMap.put(temp.getLotteryChildCode(), temp);
			}
			initCfg.put("lotChildCfg", subPlayMap);
		}
		// 4.彩种注数和倍数截止时间配置
		List<LotBettingMulBO> betMulList = findBetMulList(chaseLotCode);
		if (!ObjectUtil.isBlank(betMulList)) {
			initCfg.put("lotBetMulCfg", betMulList);
		}
		// 5.限号列表配置
		LotteryVO lotteryVO = new LotteryVO(chaseLotCode);
		lotteryVO.setStatus((short) 1); // 限号状态 1：启用；2：禁用；3：过期
		lotteryVO.setLimitDate(new Date()); // 限号时间
		List<LimitNumberInfoBO> limitList = findLimitList(lotteryVO);
		if (!ObjectUtil.isBlank(limitList)) {
			initCfg.put("limitList", limitList);
		}
		// 6.追号彩期列表
		List<String> chaseIssueList = findChaseIssueRange(chaseLotCode, curIssue.getIssueCode());
		if (!ObjectUtil.isBlank(chaseIssueList)) {
			initCfg.put("chaseIssueList", chaseIssueList);
		}
		// 7.彩种奖项列表
		List<LotWinningBO> winningList = findLotWinningList(chaseLotCode);
		if (!ObjectUtil.isBlank(winningList)) {
			Map<Integer, LotWinningBO> winningMap = new HashMap<Integer, LotWinningBO>();
			for (LotWinningBO temp : winningList) {
				winningMap.put(temp.getCode(), temp);
			}
			initCfg.put("winningMap", winningMap);
		}
		return initCfg;
	}
	
	/**
	 * @desc 初始化快捷接口配置； 注：仅快捷接口调用； 年会活动有用到
	 * @author huangb
	 * @date 2018年1月11日
	 * @param chase
	 * @return 初始化快捷接口配置； 注：仅快捷接口调用； 年会活动有用到
	 */
	private Map<String, Object> initShortCfg(OrderAddVO chase) {
		Map<String, Object> initCfg = new HashMap<String, Object>();
		Integer chaseLotCode = chase.getLotteryCode();
		// 2.当前期
		IssueBO curIssue = findCurIssue(chaseLotCode);
		Assert.notNull(curIssue, MessageCodeConstants.CURRENT_ISSUE_EMPTY);
		initCfg.put("curIssue", curIssue);
		
		// 4.彩种注数和倍数截止时间配置
		List<LotBettingMulBO> betMulList = findBetMulList(chaseLotCode);
		if (!ObjectUtil.isBlank(betMulList)) {
			initCfg.put("lotBetMulCfg", betMulList);
		}
		return initCfg;
	}

	/**
	 * @desc 获取指定配置项
	 * @author huangb
	 * @date 2017年3月27日
	 * @param item
	 *            配置项编号(1-彩种；2-彩期；3-子玩法；4-注、倍数;5-限号列表；6-追号彩期列表)
	 * @param initCfg
	 *            配置项列表
	 * @return 获取指定配置项
	 */
	@SuppressWarnings("unchecked")
	private <T> T getCfg(int item, Map<String, Object> initCfg) {
		if (ObjectUtil.isBlank(initCfg)) {
			return null;
		}
		switch (item) {
		case Constants.NUM_1:
			return initCfg.get("curLottery") == null ? null : (T) initCfg.get("curLottery");
		case Constants.NUM_2:
			return initCfg.get("curIssue") == null ? null : (T) initCfg.get("curIssue");
		case Constants.NUM_3:
			return initCfg.get("lotChildCfg") == null ? null : (T) initCfg.get("lotChildCfg");
		case Constants.NUM_4:
			return initCfg.get("lotBetMulCfg") == null ? null : (T) initCfg.get("lotBetMulCfg");
		case Constants.NUM_5:
			return initCfg.get("limitList") == null ? null : (T) initCfg.get("limitList");
		case Constants.NUM_6:
			return initCfg.get("chaseIssueList") == null ? null : (T) initCfg.get("chaseIssueList");
		case Constants.NUM_7:
			return initCfg.get("winningMap") == null ? null : (T) initCfg.get("winningMap");
		default:
			return null;
		}
	}

	/**
	 * @desc 设置用户id
	 * @author huangb
	 * @date 2017年5月25日
	 * @param chase
	 *            追号计划
	 * @param userId
	 *            用户id
	 */
	private void setUserId(OrderAddVO chase, Integer userId) {
		chase.setUserId(userId);
	}

	/**
	 * @desc 设置未支付数量
	 * @author huangb
	 * @date 2017年5月25日
	 * @param chase
	 *            追号计划
	 * @param noPayCount
	 *            未支付数量
	 */
	private void setNoPayCount(OrderAddVO chase, int noPayCount) {
		chase.setNoPayCount(noPayCount);
	}

	/**
	 * @desc 设置是否包含当前期标识
	 * @author huangb
	 * @date 2017年5月25日
	 * @param chase
	 *            追号计划
	 * @param flag
	 *            是否包含当前期标识
	 */
	private void setContainCurIssue(OrderAddVO chase, Short flag) {
		chase.setContainCurIssue(flag);
	}

	/**
	 * @desc 设置支付结束时间
	 * @author huangb
	 * @date 2017年5月25日
	 * @param chase
	 *            追号计划
	 * @param initCfg
	 *            初始化配置
	 */
	private void setPayEndTime(OrderAddVO chase, Map<String, Object> initCfg) {

		// 1.当前期
		IssueBO curIssue = getCfg(Constants.NUM_2, initCfg);
		// 2.注数配置
		List<LotBettingMulBO> lotBetMulCfg = getCfg(Constants.NUM_4, initCfg);
		// 3.按规则计算出支付截止时间
		Date payEndTime = NUMConstants.getPayEndTime(lotBetMulCfg, curIssue.getSaleEndTime(), getMaxBetNum(chase),
				getCurIssueMulNum(chase, curIssue));
		// 4.设置支付时间节点
		chase.setPayEndTime(payEndTime);
	}
	
	/**
	 * @desc 获取追号内容明细列表中的最大注数
	 * @author huangb
	 * @date 2017年11月21日
	 * @param chase
	 *            追号计划
	 * @return 获取追号内容明细列表中的最大注数
	 */
	protected int getMaxBetNum(OrderAddVO chase) {
		// 不包含当前期，默认1注
		if (YesOrNo.Y.getValue() != chase.getContainCurIssue().shortValue()) {
			return Constants.NUM_1;
		}
		// 含当前期，返回内容明细中的最大注数
		ChaseType chaseType = ChaseType.getChaseType(chase.getAddType());
		switch (chaseType) {
		case FIXED_NUMBER:
			int targetBetNum = Constants.NUM_1;
			// 选号追号的最大注数取明细列表中注数最大的
			for (OrderDetailVO content : chase.getOrderAddContentList()) {
				if (content.getBuyNumber().intValue() > targetBetNum) {
					targetBetNum = content.getBuyNumber();
				}
			}
			return targetBetNum;
		case RANDOM_NUMBER:
			// 随机追号的最大注数就是追号注数
			return chase.getAddCount();
		default:
			return Constants.NUM_1;
		}
	}

	/**
	 * @desc 获取追号期列表中当前期对应的倍数（如果不存在，则默认1倍）
	 * @author huangb
	 * @date 2017年11月21日
	 * @param chase
	 *            追号计划
	 * @return 获取追号期列表中当前期对应的倍数（如果不存在，则默认1倍）
	 */
	protected int getCurIssueMulNum(OrderAddVO chase, IssueBO curIssue) {
		// 不包含当前期，默认1倍
		if (YesOrNo.Y.getValue() != chase.getContainCurIssue().shortValue()) {
			return Constants.NUM_1;
		}
		// 特殊处理，高频彩那边的实现，移动端下单时OrderAddIssueList还没有封装，所以没法遍历
		if(ObjectUtil.isBlank(chase.getOrderAddIssueList())) {
			return chase.getAddMultiples();
		}
		// 含当前期，返回当前期倍数
		for (OrderAddIssueVO issue : chase.getOrderAddIssueList()) {
			if (issue.getIssueCode().equals(curIssue.getIssueCode())) {
				return issue.getMultiple();
			}
		}
		return Constants.NUM_1;
	}
	
	/**
	 * @desc 追号内容处理(分彩种自行验证);注:包括验证追号内容及追号注数返回
	 * @author huangb
	 * @date 2017年3月28日
	 * @param content
	 *            追号内容
	 * @param list
	 *            限号配置列表
	 * @return 追号内容处理(分彩种自行验证);注:包括验证追号内容及追号注数返回
	 * @throws ResultJsonException
	 */
	private ResultBO<?> handleChaseContent(OrderDetailVO content, List<?> list) throws ResultJsonException {
		AbstractOrderDetailValidate orderDetailValidator = getOrderDetailValidator();
		return orderDetailValidator.handleProcess(content, list);
	}

	/**
	 * @desc 获取订单详情验证器 (分数字彩和高频彩)
	 * @author huangb
	 * @date 2017年12月16日
	 * @return 获取订单详情验证器(分数字彩和高频彩)
	 */
	protected abstract AbstractOrderDetailValidate getOrderDetailValidator();

	/**
	 * @desc 查询追号彩期范围(用于验证追号彩期期号的正确性)
	 * @author huangb
	 * @date 2017年3月28日
	 * @param chaseLotCode
	 *            追号计划彩种
	 * @param curIssue
	 *            当前彩期
	 * @return 查询追号彩期范围(用于验证追号彩期期号的正确性)
	 * @throws ResultJsonException
	 */
	private List<String> findChaseIssueRange(Integer chaseLotCode, String curIssue) throws ResultJsonException {
		List<NewIssueBO> issueList = getLotteryIssueService().listLotteryIssue(chaseLotCode, getMaxChase(), curIssue);
		List<String> issues = new ArrayList<String>();
		// 由于列表中不包含当前期，所有要特殊处理下(加入当前期，删除最后一期)
		issues.add(curIssue);
		if (!ObjectUtil.isBlank(issueList)) {
			for (int i = 0; i < issueList.size() - 1; i++) {
				issues.add(issueList.get(i).getIssueCode());
			}
		}
		return issues;
	}
	
	/**
	 * @desc 获取对应彩种追号最大的期数
	 * @author huangb
	 * @date 2017年12月16日
	 * @return 获取对应彩种追号最大的期数
	 */
	protected abstract int getMaxChase();
	
	/**
	 * @desc 获取随机追号内容对应的子玩法编号
	 * @author huangb
	 * @date 2017年4月10日
	 * @return 获取随机追号内容对应的子玩法编号
	 * @throws ResultJsonException
	 */
	protected abstract Integer getRandomContentChildCode() throws ResultJsonException;
	
	/**
	 * @desc 验证追号类型(有些彩种支持选号追号和随机追号两种 eg:双色球；有些彩种只支持选号追号；eg:福彩3D)
	 * @author huangb
	 * @date 2017年7月6日
	 * @param chaseType
	 *            追号类型
	 * @throws ResultJsonException
	 */
	protected void verifyChaseType(Short chaseType) throws ResultJsonException {
		Assert.isTrue(ChaseType.contain(chaseType), "40506");
	};
}
