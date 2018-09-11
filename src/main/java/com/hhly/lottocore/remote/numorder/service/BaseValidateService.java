package com.hhly.lottocore.remote.numorder.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryBettingMulDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryLimitMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.persistence.lottery.dao.LotteryWinningDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.remote.lotto.service.ILotteryIssueService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryEnum.ConIssue;
import com.hhly.skeleton.base.common.LotteryEnum.LotIssueSaleStatus;
import com.hhly.skeleton.base.common.LotteryEnum.SaleStatus;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotWinningBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotChildVO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @desc 验证基础服务,提供配置验证相关的服务
 * @author huangb
 * @date 2017年3月21日
 * @company 益彩网络
 * @version v1.0
 */
@Service
public class BaseValidateService {

	/**
	 * 彩种数据接口
	 */
	@Autowired
	private LotteryTypeDaoMapper lotteryTypeDaoMapper;
	/**
	 * 彩期数据接口
	 */
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;
	/**
	 * 彩期子玩法数据接口
	 */
	@Autowired
	private LotteryChildDaoMapper lotteryChildDaoMapper;
	/**
	 * 彩期投注注数，倍数配置的数据接口
	 */
	@Autowired
	private LotteryBettingMulDaoMapper lotteryBettingMulDaoMapper;
	/**
	 * 彩种限号配置接口
	 */
	@Autowired
	private LotteryLimitMapper lotteryLimitMapper;
	/**
	 * 彩种奖项数据接口
	 */
	@Autowired
	private LotteryWinningDaoMapper lotteryWinningDaoMapper;
	/**
	 * 用户信息
	 */
	@Autowired
	private UserUtil userUtil;
	/**
	 * 订单数据接口(验证未支付订单数量)
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	
	/**
	 * 未支付订单限制数,默认16
	 */
	@Value("${limit_order_num}")
	protected String limitCount = "8";
	
	/** 彩期服务：获取指定彩种的追号彩期列表 */
	@Autowired
	private ILotteryIssueService lotteryIssueService;
	
	/**
	 * @desc 查询彩种
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种code
	 * @return 查询彩种
	 */
	public LotteryBO findLottery(Integer lotteryCode) {
		return lotteryTypeDaoMapper.findSingleFront(new LotteryVO(lotteryCode));
	}

	/**
	 * @desc 查询彩种当前彩期
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 * @return 查询彩种当前彩期
	 */
	public IssueBO findCurIssue(Integer lotteryCode) {
		return lotteryIssueDaoMapper.findSingleFront(new LotteryVO(lotteryCode, ConIssue.CURRENT.getValue()));
	}

	/**
	 * @desc 查询彩种对应彩期
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种
	 * @param lotteryIssue
	 *            彩期
	 * @return 查询彩种对应彩期
	 */
	protected IssueBO findIssue(Integer lotteryCode, String lotteryIssue) {
		return lotteryIssueDaoMapper.findSingleFront(new LotteryVO(lotteryCode, lotteryIssue));
	}

	/**
	 * @desc 查询彩种子玩法
	 * @author huangb
	 * @date 2017年3月22日
	 * @param lotteryCode
	 *            彩种
	 * @param targetChildCode
	 *            子玩法编号
	 * @return 查询彩种子玩法
	 */
	public LotChildBO findLotteryChild(Integer lotteryCode, Integer targetChildCode) {
		return lotteryChildDaoMapper.findSingleFront(new LotChildVO(lotteryCode, targetChildCode));
	}

	/**
	 * @desc 查询彩种子玩法列表
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种
	 * @return 查询彩种子玩法列表
	 */
	public List<LotChildBO> findLotteryChild(Integer lotteryCode) {
		return lotteryChildDaoMapper.findMultipleFront(new LotteryVO(lotteryCode));
	}

	/**
	 * @desc 查询符合条件的子玩法记录数
	 * @author Tony Wang
	 * @create 2017年4月10日
	 * @param vo
	 * @return
	 */
	public int countLotteryChild(LotChildVO vo) {
		return lotteryChildDaoMapper.count(vo);
	}

	/**
	 * @desc 查询注/倍数配置列表
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种
	 * @return 查询注/倍数配置列表
	 */
	public List<LotBettingMulBO> findBetMulList(Integer lotteryCode) {
		return lotteryBettingMulDaoMapper.findMultipleFront(new LotteryVO(lotteryCode));
	}

	/**
	 * @desc 查询彩种限号列表
	 * @author huangb
	 * @date 2017年3月28日
	 * @param lotteryCode
	 *            彩种
	 * @return 查询彩种限号列表
	 */
	public List<LimitNumberInfoBO> findLimitList(LotteryVO vo) {
		return lotteryLimitMapper.findMultipleLimitFront(vo);
	}

	/**
	 * @desc 查询彩种奖项列表
	 * @author huangb
	 * @date 2017年3月28日
	 * @param lotteryCode
	 *            彩种
	 * @return 查询彩种奖项列表
	 */
	protected List<LotWinningBO> findLotWinningList(Integer lotteryCode) {
		return lotteryWinningDaoMapper.findMultipleFront(new LotteryVO(lotteryCode));
	}

	/**
	 * @desc 获取登录用户
	 * @author huangb
	 * @date 2017年4月14日
	 * @param token 用户令牌
	 * @return 获取登录用户
	 */
	protected UserInfoBO getLoginUser(String token) {
		return userUtil.getUserByToken(token);
	}

	/**
	 * @desc 查询未支付订单（包括订单和追号计划两者）数量
	 * @author huangb
	 * @date 2017年5月5日
	 * @param lotteryCode
	 *            彩种
	 * @param userId
	 *            用户id
	 * @param issueCode
	 *            期号
	 * @return 查询未支付订单（包括订单和追号计划两者）数量
	 */
	public int findNoPayOrderCount(Integer lotteryCode, Integer userId, String issueCode) {
		return orderInfoDaoMapper.queryNoPayOrderListCount(null, userId.intValue(), lotteryCode +"", issueCode);
	}
	 
	/**
	 * @desc 验证彩种的的销售状态
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种
	 * @return 验证彩种的的销售状态
	 */
	protected ResultBO<?> verifySaleStatus(Integer lotteryCode) {

		Assert.notNull(lotteryCode, MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE);
		return verifySaleStatus(findLottery(lotteryCode));
	}

	/**
	 * @desc 验证彩种的的销售状态
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryType
	 *            彩种对象
	 * @return 验证彩种的的销售状态
	 */
	protected ResultBO<?> verifySaleStatus(LotteryBO lotteryType) {

		Assert.notNull(lotteryType, MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE);
		Assert.notNull(lotteryType.getSaleStatus(), MessageCodeConstants.LOTTERY_STOP_SALE);
		Assert.isTrue(SaleStatus.NORMAL_SALE.getValue() == lotteryType.getSaleStatus().shortValue(), MessageCodeConstants.LOTTERY_STOP_SALE);
		return ResultBO.ok();
	}
	
	/**
	 * @desc 彩种销售状态除了正常销售时可购买；暂停销售且不在停售限制平台中的也可购买 (20171019 add)
	 * @author huangb
	 * @date 2017年10月19日
	 * @param lotteryCode
	 *            彩种编号
	 * @param targetPlatform
	 *            目标平台
	 * @return 验证彩种的的销售状态
	 */
	protected ResultBO<?> verifySaleStatus(Integer lotteryCode, Short targetPlatform) {

		Assert.notNull(lotteryCode, MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE);
		return verifySaleStatus(findLottery(lotteryCode), targetPlatform);
	}
	/**
	 * @desc 彩种销售状态除了正常销售时可购买；暂停销售且不在停售限制平台中的也可购买  (20171019 add)
	 * @author huangb
	 * @date 2017年10月19日
	 * @param lotteryType
	 *            彩种对象
	 * @param targetPlatform
	 *            目标平台
	 * @return 验证彩种的的销售状态
	 */
	protected ResultBO<?> verifySaleStatus(LotteryBO lotteryType, Short targetPlatform) {

		Assert.notNull(lotteryType, MessageCodeConstants.LOTTERY_TYPE_NOT_EXIST_SERVICE);
		Assert.notNull(lotteryType.getSaleStatus(), MessageCodeConstants.LOTTERY_STOP_SALE);
		if (SaleStatus.STOP_SALE.getValue() == lotteryType.getSaleStatus().shortValue()) {
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_STOP_SALE));
		} else if (SaleStatus.SUSPEND_SALE.getValue() == lotteryType.getSaleStatus().shortValue()) {
			// 若包含目标平台，则不能购买
			if (!ObjectUtil.isBlank(lotteryType.getPlatform()) && targetPlatform != null
					&& Arrays.asList(lotteryType.getPlatform().split(SymbolConstants.COMMA)).contains(targetPlatform.toString())) {
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_STOP_SALE));
			}
		}
		return ResultBO.ok();
	}

	/**
	 * @desc 验证彩期的的销售状态
	 * @author huangb
	 * @date 2017年3月21日
	 * @param lotteryCode
	 *            彩种
	 * @param lotteryIssue
	 *            彩期
	 * @return 验证彩期的的销售状态
	 */
	protected ResultBO<?> verifyIssueSaleStatus(Integer lotteryCode, String lotteryIssue) {

		if (lotteryCode == null || StringUtil.isBlank(lotteryIssue)) {
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE));
		}
		return verifyIssueSaleStatus(findIssue(lotteryCode, lotteryIssue));
	}

	/**
	 * @desc 验证彩期的的销售状态
	 * @author huangb
	 * @date 2017年3月21日
	 * @param issue
	 *            彩期对象
	 * @return 验证彩期的的销售状态
	 */
	protected ResultBO<?> verifyIssueSaleStatus(IssueBO issue) {

		Assert.notNull(issue, MessageCodeConstants.LOTTERY_ISSUE_NOT_EXIST_SERVICE);
		Assert.notNull(issue.getSaleStatus(), "40512", issue.getIssueCode());
		Assert.isTrue(LotIssueSaleStatus.SALING.getValue() == issue.getSaleStatus().shortValue(), "40512",
				issue.getIssueCode());
		// 通过彩期销售截止倒计时(毫秒)判断
		long saleDownCount = 0;
		if (issue.getSaleEndTime() != null) {
			saleDownCount = issue.getSaleEndTime().getTime() - new Date().getTime();
		}
		Assert.isTrue(saleDownCount > Constants.NUM_0, "40513", issue.getIssueCode());
		return ResultBO.ok();
	}

	/**
	 * @param targetCount
	 *            方案目标数量
	 * @param minCount
	 *            最小方案数量
	 * @param maxCount
	 *            最大方案数量
	 * @return 验证结果
	 * @Desc 校验订单明细的最大数量，即投注方案数
	 */
	protected ResultBO<?> verifyBetPlanMaxCount(int targetCount, int minCount, int maxCount) {
		Assert.isTrue(targetCount <= maxCount, MessageCodeConstants.ORDER_DETAIL_BETNUM_LIMIT_SERVICE);
		Assert.isTrue(targetCount >= minCount, "40510", minCount);
		return ResultBO.ok();
	}

	/**
	 * @param childCodeMap
	 *            子玩法列表
	 * @param targetChildCode
	 *            目标子玩法
	 * @return 验证结果
	 * @throws Exception
	 * @Desc 验证彩期子玩法的销售状态
	 */
	protected ResultBO<?> verifySubPlaySaleStatus(Map<Integer, LotChildBO> childCodeMap, Integer targetChildCode) {
		Assert.notEmpty(childCodeMap, "40516");
		Assert.notNull(targetChildCode, "40516");

		LotChildBO lotteryChild = childCodeMap.get(targetChildCode);
		Assert.notNull(lotteryChild, "40516");
		Assert.notNull(lotteryChild.getSaleStatus(), "40517", lotteryChild.getChildName());
		Assert.isTrue(LotteryChildEnum.SaleStatus.NORMAL.getValue() == lotteryChild.getSaleStatus().shortValue(),
				"40517", lotteryChild.getChildName());

		return ResultBO.ok();
	}

	/**
	 * @desc 验证一次投注的最大注数，即一条明细的号码组合数
	 * @author huangb
	 * @date 2017年3月23日
	 * @param issue
	 *            彩期对象
	 * @param betMulList
	 *            注/倍数配置列表
	 * @param betContent
	 *            投注内容
	 * @param targetBetNum
	 *            目标投注数
	 * @return 验证一次投注的最大注数，即一条明细的号码组合数
	 */
	protected ResultBO<?> verifyOneBetMaxNum(IssueBO issue, List<LotBettingMulBO> betMulList, String betContent,
			long targetBetNum) {
		long saleDownCount = 0;
		if (issue != null && issue.getSaleEndTime() != null) {
			saleDownCount = issue.getSaleEndTime().getTime() - new Date().getTime();
		}
		return verifyOneBetMaxNum(saleDownCount, betMulList, betContent, targetBetNum);
	}

	/**
	 * @desc 验证一次投注的最大注数，即一条明细的号码组合数
	 * @author huangb
	 * @date 2017年3月23日
	 * @param saleDownCount
	 *            彩期销售截止倒计时(毫秒)
	 * @param betMulList
	 *            注/倍数配置列表
	 * @param betContent
	 *            投注内容
	 * @param targetBetNum
	 *            目标投注数
	 * @return 验证一次投注的最大注数，即一条明细的号码组合数
	 */
	protected ResultBO<?> verifyOneBetMaxNum(long saleDownCount, List<LotBettingMulBO> betMulList, String betContent,
			long targetBetNum) {
		// 最大注数 默认0
		int oneBetMaxNum = Constants.NUM_0;
		if (!ObjectUtil.isBlank(betMulList)) {
			for (LotBettingMulBO temp : betMulList) {
				// 如果倒计时秒数 > 配置的时间点
				if ((saleDownCount / Constants.NUM_1000) > temp.getEndTime().intValue()) {
					oneBetMaxNum = temp.getBettindNum();
					break;
				}
			}
			// 20161219 如果在注/倍数配置表中没找到符合条件的配置，则以最小配置为限制条件
			if (oneBetMaxNum == Constants.NUM_0) {
				oneBetMaxNum = betMulList.get(betMulList.size() - 1).getBettindNum();
			}
		}
		if (targetBetNum < Constants.NUM_1 || targetBetNum > oneBetMaxNum) {
			// 保存当前时间点的注数信息，供外层重新定义消息用
			throw new ResultJsonException(
					ResultBO.err("40520", oneBetMaxNum, new Object[] { betContent, oneBetMaxNum }));
		}
		return ResultBO.ok();
	}

	/**
	 * @desc 验证投注最大倍数(是订单总倍数)
	 * @author huangb
	 * @date 2017年3月27日
	 * @param issue
	 *            彩期
	 * @param betMulList
	 *            投注倍数的配置列表
	 * @param targetMulti
	 *            目标倍数
	 * @return 验证投注最大倍数(是订单总倍数)
	 */
	public ResultBO<?> verifyBetMaxMulti(IssueBO issue, List<LotBettingMulBO> betMulList, int targetMulti) {
		long saleDownCount = 0;
		if (issue != null && issue.getSaleEndTime() != null) {
			saleDownCount = issue.getSaleEndTime().getTime() - new Date().getTime();
		}
		return verifyBetMaxMulti(saleDownCount, betMulList, targetMulti);
	}

	/**
	 * @desc 验证投注最大倍数(是订单总倍数)
	 * @author huangb
	 * @date 2017年3月27日
	 * @param saleDownCount
	 *            彩期销售截止倒计时(毫秒)
	 * @param betMulList
	 *            投注倍数的配置列表
	 * @param targetMulti
	 *            目标倍数
	 * @return 验证投注最大倍数(是订单总倍数)
	 */
	public ResultBO<?> verifyBetMaxMulti(long saleDownCount, List<LotBettingMulBO> betMulList, int targetMulti) {
		// 最大倍数 默认0
		int maxMultiple = Constants.NUM_0;
		if (!ObjectUtil.isBlank(betMulList)) {
			for (LotBettingMulBO temp : betMulList) {
				// 如果倒计时秒数 > 配置的时间点
				if ((saleDownCount / Constants.NUM_1000) > temp.getEndTime().intValue()) {
					maxMultiple = temp.getMultipleNum();
					break;
				}
			}
			// 20161219 如果在注/倍数配置表中没找到符合条件的配置，则以最小配置为限制条件
			if (maxMultiple == Constants.NUM_0) {
				maxMultiple = betMulList.get(betMulList.size() - 1).getMultipleNum();
			}
		}
		if (targetMulti < Constants.NUM_1 || targetMulti > maxMultiple) {
			// 保存当前时间点的倍数信息，供外层重新定义消息用
			throw new ResultJsonException(ResultBO.err("40532", maxMultiple, new Object[] { maxMultiple }));
		}
		return ResultBO.ok();
	}

	public String getLimitCount() {
		return limitCount;
	}

	public ILotteryIssueService getLotteryIssueService() {
		return lotteryIssueService;
	}
}
