package com.hhly.lottocore.remote.numorder.service.impl.numvalidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 排列三订单明细校验(仅提供明细内容验证，其它验证统一在主流程处理)
 * @author huangb
 * @date 2017年6月14日
 * @company 益彩网络
 * @version v1.0
 */
@Component("pl3OrderDetailValidate")
public class Pl3OrderDetailValidate extends OrderDetailValidate {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(Pl3OrderDetailValidate.class);
	
	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("Pl3OrderDetailValidate verifyContentType begin!");

		// 1.按内容类型，将投注内容分割并校验长度
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型(子玩法下面分内容类型)
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		Assert.notNull(lotChild, "40437", planContent);
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		Assert.notNull(type, "40401");
		
		switch (lotChild) {
		case PL3_DIRECT:
			// 直选(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_DIRECT_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_DIRECT_MULTIPLE, planContent), "40402", planContent);
				break;
			case SUM:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_DIRECT_SUM, planContent), "40402", planContent);
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		case PL3_G3:
			// 组三(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G3_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G3_MULTIPLE, planContent), "40402", planContent);
				break;
			case SUM:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G3_SUM, planContent), "40402", planContent);
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		case PL3_G6:
			// 组六(包含单式、复式、胆拖、和值)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G6_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G6_MULTIPLE, planContent), "40402", planContent);
				break;
			case SUM:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G6_SUM, planContent), "40402", planContent);
				break;
			case DANTUO:
				Assert.isTrue(Pattern.matches(NUMConstants.PL3_REGEX_G6_DANTUO, planContent), "40402", planContent);
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected BetContentVO genBetContent(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("Pl3OrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		// 投注内容分割长度
		String[] betCode = null;
		switch (lotChild) {
		case PL3_DIRECT:
			// 直选(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
			case MULTIPLE:
				betCode = planContent.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
				return new BetContentVO(betCode[0].split(SymbolConstants.COMMA),
						betCode[1].split(SymbolConstants.COMMA), betCode[2].split(SymbolConstants.COMMA));
			case SUM:
				return new BetContentVO(planContent.split(SymbolConstants.COMMA));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G3:
			// 组三(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				// 20170701 add 单式两种格式（1,1,0 或 1|0）
			case MULTIPLE:
				// 20170701 add 复式两种格式（0,1,2,3 或 0,1,2,3|0,1,2,3）
				if(planContent.contains(SymbolConstants.VERTICAL_BAR)) { // 包含“|”
					betCode = planContent.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
					return new BetContentVO(betCode[0].split(SymbolConstants.COMMA), betCode[1].split(SymbolConstants.COMMA));
				} else {
					return new BetContentVO(planContent.split(SymbolConstants.COMMA));
				}
			case SUM:
				return new BetContentVO(planContent.split(SymbolConstants.COMMA));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G6:
			// 组六(包含单式、复式、胆拖、和值)
			switch (type) {
			case SINGLE:
			case MULTIPLE:
			case SUM:
				return new BetContentVO(planContent.split(SymbolConstants.COMMA));
			case DANTUO:
				betCode = planContent.split(SymbolConstants.NUMBER_SIGN);
				return new BetContentVO(betCode[0].split(SymbolConstants.COMMA),
						betCode[1].split(SymbolConstants.COMMA));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("Pl3OrderDetailValidate genBetNum begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());

		switch (lotChild) {
		case PL3_DIRECT:
			// 直选(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				return Constants.NUM_1;
			case MULTIPLE:
				int area1Len = betContent.getArea1().length;// 百位号码数
				int area2Len = betContent.getArea2().length;// 十位号码数
				int area3Len = betContent.getArea3().length;// 个位号码数
				// 注数 = 百区域选号数*十区域选号数*个区域选号数
				return area1Len * area2Len * area3Len;
			case SUM:
				// 注数 = 各和值对应注数之和
				return getSumTotalNum(betContent.getArea1(), NUMConstants.PL3_DIRECT_SUM_BETNUM);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G3:
			// 组三(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				// 20170701 add 单式两种格式（1,1,0 或 1|0）(无论哪种都是1注)
				return Constants.NUM_1;
			case MULTIPLE:
				// 20170701 add 复式两种格式（0,1,2,3或0,1,2,3|0,1,2,3）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
				if (betContent.getArea1() != null && betContent.getArea2() != null) {
					// 注数 = 区域1选号个数*区域2选号个数-(区域1选号与区域2选号的交集(重叠)个数)(eg:0,1,2,3|1,2,3,4 = 4*4-3)
					int intersectLen = ArrayUtil.intersect(betContent.getArea1(), betContent.getArea2()).length; // 交集个数
					return betContent.getArea1().length * betContent.getArea2().length - intersectLen;
				} else {
					int area1Len = betContent.getArea1().length;// 选号个数
					// 注数 = 选号个数*（选号个数-1）
					return area1Len * (area1Len - Constants.NUM_1);
				}
			case SUM:
				// 注数 = 各和值对应注数之和
				return getSumTotalNum(betContent.getArea1(), NUMConstants.PL3_G3_SUM_BETNUM);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G6:
			// 组六(包含单式、复式、胆拖、和值)
			switch (type) {
			case SINGLE:
				return Constants.NUM_1;
			case MULTIPLE:
				int area1Len = betContent.getArea1().length;// 选号个数
				// 注数 = 选号个数中选3个的组合数
				return (int) NumberUtil.getCombinationCount(area1Len, NUMConstants.PL3_CHOOSE_3);
			case SUM:
				// 注数 = 各和值对应注数之和
				return getSumTotalNum(betContent.getArea1(), NUMConstants.PL3_G6_SUM_BETNUM);
			case DANTUO:
				int danLen = betContent.getArea1().length;// 胆个数
				int tuoLen = betContent.getArea2().length;// 拖个数
				// 注数 = 拖选号个数中选（3-胆个数）个的组合数
				return (int) NumberUtil.getCombinationCount(tuoLen, (NUMConstants.PL3_CHOOSE_3 - danLen));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected ResultBO<?> verifyBetContent(OrderDetailVO orderDetail, BetContentVO betContent)
			throws ResultJsonException {
		logger.debug("Pl3OrderDetailValidate verifyBetContent begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		switch (lotChild) {
		case PL3_DIRECT:
			// 直选(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				return verifyDIRSingle(orderDetail, betContent);
			case MULTIPLE:
				return verifyDIRMultiple(orderDetail, betContent);
			case SUM:
				return verifyDIRSum(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G3:
			// 组三(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
				return verifyG3Single(orderDetail, betContent);
			case MULTIPLE:
				return verifyG3Multiple(orderDetail, betContent);
			case SUM:
				return verifyG3Sum(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case PL3_G6:
			// 组六(包含单式、复式、胆拖、和值)
			switch (type) {
			case SINGLE:
				return verifyG6Single(orderDetail, betContent);
			case MULTIPLE:
				return verifyG6Multiple(orderDetail, betContent);
			case SUM:
				return verifyG6Sum(orderDetail, betContent);
			case DANTUO:
				return verifyG6DT(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	/**
	 * @desc 获取和值列表总注数
	 * @author huangb
	 * @date 2017年6月15日
	 * @param sumArr
	 *            和值列表
	 * @param sumBetNumMap
	 *            和值-注数的对应表
	 * @return 获取和值列表总注数
	 */
	private int getSumTotalNum(String[] sumArr, Map<Integer, Integer> sumBetNumMap) {
		// 注数 = 各和值对应注数之和
		int totalNum = 0;
		Integer betNum = null;
		for (String tmp : sumArr) {
			betNum = sumBetNumMap.get(Integer.valueOf(tmp));
			totalNum = totalNum + (betNum == null ? Constants.NUM_0 : betNum);
		}
		return totalNum;
	}

	/**
	 * @desc 校验直选-单式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验直选-单式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyDIRSingle(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyDIRSingle begin!");
		// 验证百、十、个区域基本信息:
//		String[] area1Arr = betContent.getArea1();// 百位号码数
//		String[] area2Arr = betContent.getArea2();// 十位号码数
//		String[] area3Arr = betContent.getArea3();// 个位号码数
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
//		Assert.isTrue(!ArrayUtil.isEmpty(area2Arr), "40405");
//		Assert.isTrue(!ArrayUtil.isEmpty(area3Arr), "40405");
		
		// 1.选号的个数(百、十、个区域各1个号码)
//		Assert.isTrue(area1Arr.length == NUMConstants.F3D_CHOOSE_1, "40550", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1);
//		Assert.isTrue(area2Arr.length == NUMConstants.F3D_CHOOSE_1, "40551", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1);
//		Assert.isTrue(area3Arr.length == NUMConstants.F3D_CHOOSE_1, "40552", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1);
		
		// 2.选号的数字范围(0-9)
//		for (String area1 : area1Arr) {
//			Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40553", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}
//		for (String area2 : area2Arr) {
//			Assert.isTrue(validNumRange(area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40554", orderDetail.getPlanContent(), area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}
//		for (String area3 : area3Arr) {
//			Assert.isTrue(validNumRange(area3, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40555", orderDetail.getPlanContent(), area3, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}

		// 3.选号重复性(各区域不可重复)
//		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40556", orderDetail.getPlanContent());
//		Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40557", orderDetail.getPlanContent());
//		Assert.isTrue(!ArrayUtil.isRepeat(area3Arr), "40558", orderDetail.getPlanContent());

		logger.debug("PL3 verifyDIRSingle end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验直选-复式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验直选-复式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyDIRMultiple(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyDIRMultiple begin!");
		// 验证百、十、个区域基本信息:
		String[] area1Arr = betContent.getArea1();// 百位号码数
		String[] area2Arr = betContent.getArea2();// 十位号码数
		String[] area3Arr = betContent.getArea3();// 个位号码数
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
//		Assert.isTrue(!ArrayUtil.isEmpty(area2Arr), "40405");
//		Assert.isTrue(!ArrayUtil.isEmpty(area3Arr), "40405");
		
		// 1.选号的个数(百、十、个区域每个区域1~10个号码);;3个区域的号码个数之和应该在4~30(区别于单式)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10), "40559", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10);
//		Assert.isTrue(validLenRange(area2Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10), "40560", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10);
//		Assert.isTrue(validLenRange(area3Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10), "40561", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10);
		// 3个区域的号码个数之和应该在4~30(区别于单式)
		Assert.isTrue(validLenRange((area1Arr.length + area2Arr.length + area3Arr.length), NUMConstants.PL3_CHOOSE_4, NUMConstants.PL3_CHOOSE_30), "40402", orderDetail.getPlanContent());
		
		// 2.选号的数字范围(0-9)
//		for (String area1 : area1Arr) {
//			Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40553", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}
//		for (String area2 : area2Arr) {
//			Assert.isTrue(validNumRange(area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40554", orderDetail.getPlanContent(), area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}
//		for (String area3 : area3Arr) {
//			Assert.isTrue(validNumRange(area3, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40555", orderDetail.getPlanContent(), area3, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}

		// 3.选号重复性(各区域不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40556", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40557", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area3Arr), "40558", orderDetail.getPlanContent());

		logger.debug("PL3 verifyDIRMultiple end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验直选-和值投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验直选-和值投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyDIRSum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyDIRSum begin!");
		// 验证和值基本信息:
		String[] area1Arr = betContent.getArea1();// 和值号码
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
		
		// 1.选号的个数(1~28)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_28), "40562", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_28);
		
		// 2.选号的数字范围(0-27)
		for (String area1 : area1Arr) {
			Assert.isTrue(validNumRange(area1, NUMConstants.PL3_DIRECT_SUM_MIN_0, NUMConstants.PL3_DIRECT_SUM_MAX_27), "40563", orderDetail.getPlanContent(), area1, NUMConstants.PL3_DIRECT_SUM_MIN_0, NUMConstants.PL3_DIRECT_SUM_MAX_27);
		}

		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40564", orderDetail.getPlanContent());

		logger.debug("PL3 verifyDIRSum end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组三-单式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组三-单式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG3Single(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG3Single begin!");
		
		// 20170701 add 单式两种格式（1,1,0 或 1|0）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
		
		if (betContent.getArea1() != null && betContent.getArea2() != null) {
			// 验证选号基本信息:
			String[] area1Arr = betContent.getArea1();// 选号
			String[] area2Arr = betContent.getArea2();// 选号
//			Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
//			Assert.isTrue(!ArrayUtil.isEmpty(area2Arr), "40405");
			
			// 1.前后区各1个号码
//			Assert.isTrue(area1Arr.length == NUMConstants.F3D_CHOOSE_1, "40591", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1);
//			Assert.isTrue(area2Arr.length == NUMConstants.F3D_CHOOSE_1, "40591", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1);	
				
			// 2.选号的数字范围(0-9)
//			for (String area1 : area1Arr) {
//				Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40566", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}
//			for (String area2 : area2Arr) {
//				Assert.isTrue(validNumRange(area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40566", orderDetail.getPlanContent(), area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}
			
			// 3.选号重复性(前后区数字不可重复)
			Assert.isTrue(!ArrayUtil.isRepeatCompareTo(area1Arr, area2Arr), "40592", orderDetail.getPlanContent());
		} else {
			// 验证选号基本信息:
			String[] area1Arr = betContent.getArea1();// 选号
			
//			Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
			
			// 1.选号的个数(3个)
//			Assert.isTrue(area1Arr.length == NUMConstants.F3D_CHOOSE_3, "40565", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_3);
			
			// 2.选号的数字范围(0-9)
//			for (String area1 : area1Arr) {
//				Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40566", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}

			// 3.选号重复性(有且只能重复2个号码)
			Assert.isTrue((ArrayUtil.filterDuplicate(area1Arr).size() == area1Arr.length - Constants.NUM_1), "40567", orderDetail.getPlanContent());

		}
		
		logger.debug("PL3 verifyG3Single end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组三-复式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组三-复式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG3Multiple(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG3Multiple begin!");
		
		// 20170701 add 复式两种格式（0,1,2,3 或 0,1,2,3|0,1,2,3）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
		
		if (betContent.getArea1() != null && betContent.getArea2() != null) {
			// 验证选号基本信息:
			String[] area1Arr = betContent.getArea1();// 选号
			String[] area2Arr = betContent.getArea2();// 选号
//			Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
//			Assert.isTrue(!ArrayUtil.isEmpty(area2Arr), "40405");
			
			// 1.选号的个数(前后区各1~10个号码；2个区域的号码个数之和应该在3~20(区别于单式))
//			Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10), "40593", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10);
//			Assert.isTrue(validLenRange(area2Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10), "40593", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_10);
			// 2个区域的号码个数之和应该在3~20(区别于单式))
			Assert.isTrue(validLenRange((area1Arr.length + area2Arr.length), NUMConstants.PL3_CHOOSE_3, NUMConstants.PL3_CHOOSE_20), "40402", orderDetail.getPlanContent());
			
			// 2.选号的数字范围(0-9)
//			for (String area1 : area1Arr) {
//				Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40569", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}
//			for (String area2 : area2Arr) {
//				Assert.isTrue(validNumRange(area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40569", orderDetail.getPlanContent(), area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}
			
			// 3.选号重复性(前区号码不可重复，后区号码不可重复；当前区号码为1个或者后区号码为1个时，前后区号码不可重复)
			Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40594", orderDetail.getPlanContent());
			Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40594", orderDetail.getPlanContent());
			
			// 20170712 add 下面的限制取消，即支持1|1,2这种格式的复式投注，订单入库还是复式类型，出票为一注单式票
			/*if(area1Arr.length == Constants.NUM_1 || area2Arr.length == Constants.NUM_1) {
				Assert.isTrue(!ArrayUtil.isRepeatCompareTo(area1Arr, area2Arr), "40595", orderDetail.getPlanContent());
			}*/
		} else {
			// 验证选号基本信息:
			String[] area1Arr = betContent.getArea1();// 选号
			
//			Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
			
			// 1.选号的个数(2~10个)
//			Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_2, NUMConstants.F3D_CHOOSE_10), "40568", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_2, NUMConstants.F3D_CHOOSE_10);
			
			// 2.选号的数字范围(0-9)
//			for (String area1 : area1Arr) {
//				Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40569", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//			}

			// 3.选号重复性(不可重复)
			Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40570", orderDetail.getPlanContent());
		}
				
		logger.debug("PL3 verifyG3Multiple end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组三-和值投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组三-和值投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG3Sum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG3Sum begin!");
		// 验证和值基本信息:
		String[] area1Arr = betContent.getArea1();// 和值号码
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
		
		// 1.选号的个数(1~26个)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_26), "40571", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_26);
		
		// 2.选号的数字范围(1-26)
		for (String area1 : area1Arr) {
			Assert.isTrue(validNumRange(area1, NUMConstants.PL3_G3_SUM_MIN_1, NUMConstants.PL3_G3_SUM_MAX_26), "40572", orderDetail.getPlanContent(), area1, NUMConstants.PL3_G3_SUM_MIN_1, NUMConstants.PL3_G3_SUM_MAX_26);
		}

		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40573", orderDetail.getPlanContent());

		logger.debug("PL3 verifyG3Sum end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组六-单式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组六-单式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG6Single(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG6Single begin!");
		// 验证选号基本信息:
		String[] area1Arr = betContent.getArea1();// 选号
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
		
		// 1.选号的个数(3个)
//		Assert.isTrue(area1Arr.length == NUMConstants.F3D_CHOOSE_3, "40574", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_3);
		
		// 2.选号的数字范围(0-9)
//		for (String area1 : area1Arr) {
//			Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40575", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}

		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40576", orderDetail.getPlanContent());

		logger.debug("PL3 verifyG6Single end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组六-复式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组六-复式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG6Multiple(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG6Multiple begin!");
		// 验证选号基本信息:
		String[] area1Arr = betContent.getArea1();// 选号
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
		
		// 1.选号的个数(4~10)  (排除3，区别于单式)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_4, NUMConstants.F3D_CHOOSE_10), "40577", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_4, NUMConstants.F3D_CHOOSE_10);
		
		// 2.选号的数字范围(0-9)
//		for (String area1 : area1Arr) {
//			Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40578", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}

		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40579", orderDetail.getPlanContent());
		
		logger.debug("PL3 verifyG6Multiple end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组六-和值投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组六-和值投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG6Sum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG6Sum begin!");
		// 验证和值基本信息:
		String[] area1Arr = betContent.getArea1();// 和值号码
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
		
		// 1.选号的个数(1~22)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_22), "40580", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_22);
		
		// 2.选号的数字范围(3-24)
		for (String area1 : area1Arr) {
			Assert.isTrue(validNumRange(area1, NUMConstants.PL3_G6_SUM_MIN_3, NUMConstants.PL3_G6_SUM_MAX_24), "40581", orderDetail.getPlanContent(), area1, NUMConstants.PL3_G6_SUM_MIN_3, NUMConstants.PL3_G6_SUM_MAX_24);
		}

		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40582", orderDetail.getPlanContent());

		logger.debug("PL3 verifyG6Sum end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验组六-胆拖投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验组六-胆拖投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyG6DT(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("PL3 verifyG6DT begin!");
		// 验证胆、拖区域基本信息:
		String[] area1Arr = betContent.getArea1();// 胆区号码
		String[] area2Arr = betContent.getArea2();// 拖区号码
		
//		Assert.isTrue(!ArrayUtil.isEmpty(area1Arr), "40405");
//		Assert.isTrue(!ArrayUtil.isEmpty(area2Arr), "40405");
		
		// 1.选号的个数(胆1~2个，拖1~9个，胆+拖3~10个)
//		Assert.isTrue(validLenRange(area1Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_2), "40583", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_2);
//		Assert.isTrue(validLenRange(area2Arr.length, NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_9), "40584", orderDetail.getPlanContent(), NUMConstants.F3D_CHOOSE_1, NUMConstants.F3D_CHOOSE_9);
		Assert.isTrue(validLenRange(area1Arr.length + area2Arr.length, NUMConstants.PL3_CHOOSE_3, NUMConstants.PL3_CHOOSE_10), "40585", orderDetail.getPlanContent(), NUMConstants.PL3_CHOOSE_3, NUMConstants.PL3_CHOOSE_10);
		
		// 2.选号的数字范围(0-9)
//		for (String area1 : area1Arr) {
//			Assert.isTrue(validNumRange(area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40586", orderDetail.getPlanContent(), area1, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}
//		for (String area2 : area2Arr) {
//			Assert.isTrue(validNumRange(area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9), "40587", orderDetail.getPlanContent(), area2, NUMConstants.F3D_MIN_0, NUMConstants.F3D_MAX_9);
//		}

		// 3.选号重复性(胆不重复，拖不重复，胆拖不重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40588", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40589", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeatCompareTo(area1Arr, area2Arr), "40590", orderDetail.getPlanContent());

		logger.debug("PL3 verifyG6DT end!");
		return ResultBO.ok();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResultBO<?> verifyLimitNum(OrderDetailVO orderDetail, BetContentVO betContent, List<?> list)
			throws ResultJsonException {
		// 无限号配置，不做限号处理
		if (ObjectUtil.isBlank(list)) {
			logger.info("无限号配置，不做限号处理！！！！");
			return ResultBO.ok();
		}
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		try {
			// 1.限号集合(同一彩种可以配多条限号；每条限号下根据玩法又可以配多条限号内容(同玩法都可以配多条内容))
			List<LimitNumberInfoBO> limitList = (List<LimitNumberInfoBO>) list;
			for (LimitNumberInfoBO limit : limitList) {
				for (LimitNumberDetailBO limitDtl : limit.getLimitNumberList()) {
					// 1.1.目标方案的子玩法存在限号配置，则做限号验证
					if (lotChild.getValue() != limitDtl.getLotteryChildCode().intValue()) {
						continue;
					}
					if (ObjectUtil.isBlank(limitDtl.getLimitContent())) {
						logger.info("限号内容为空，不做限号处理！！！！");
						continue;
					}
					/**20170619确认：：限号配置中只会按每种玩法的单式内容形式进行配置，内容分割规则对应单式玩法内容规则(但是这里不硬性要求符号对应，支持下面三种符号分割，只要分割后的数字个数与单式玩法的数字个数一致即可)
					 * 如果是不符合的限号配置会直接忽略
					 */
					// 1.2.配置的限号数字集合,且每种玩法的单式內容都是3个号码，所以这里统一验证一下
					List<String> limitNumList = StringUtil.toStrList(limitDtl.getLimitContent(), ",#|");
					if (ObjectUtil.isBlank(limitNumList) || limitNumList.size() != NUMConstants.PL3_CHOOSE_3) {
						logger.info("配置的限号内容：" + limitDtl.getLimitContent() + ",不符合规则，只能配置单式内容类型，忽略该配置项！！！！");
						continue;
					}
					// 1.3.分子玩法-内容类型分别验证限号(注：这里用if，不用switch，判断更直观)
					if (lotChild.getValue() == LotteryChild.PL3_DIRECT.getValue()) {
						
						if (type.getValue() == BetContentType.SINGLE.getValue()
								|| type.getValue() == BetContentType.MULTIPLE.getValue()) {
							verifyLimitDIRDefault(orderDetail, betContent, limitNumList);
						} else if (type.getValue() == BetContentType.SUM.getValue()) {
							verifyLimitSum(orderDetail, betContent, limitNumList);
						}
					} else if (lotChild.getValue() == LotteryChild.PL3_G3.getValue()) {
						
						if (type.getValue() == BetContentType.SINGLE.getValue()) {
							verifyLimitG3Single(orderDetail, betContent, limitNumList);
						} else if (type.getValue() == BetContentType.MULTIPLE.getValue()) {
							verifyLimitG3Multiple(orderDetail, betContent, limitNumList);
						} else if (type.getValue() == BetContentType.SUM.getValue()) {
							verifyLimitSum(orderDetail, betContent, limitNumList);
						}
					} else if (lotChild.getValue() == LotteryChild.PL3_G6.getValue()) {
						
						if (type.getValue() == BetContentType.SINGLE.getValue()
								|| type.getValue() == BetContentType.MULTIPLE.getValue()) {
							verifyLimitG6Default(orderDetail, betContent, limitNumList);
						} else if (type.getValue() == BetContentType.SUM.getValue()) {
							verifyLimitSum(orderDetail, betContent, limitNumList);
						} else if (type.getValue() == BetContentType.DANTUO.getValue()) {
							verifyLimitG6DT(orderDetail, betContent, limitNumList);
						}
					}
					
				}
			}
		} catch (ResultJsonException e) {
			throw e;
		} catch (Exception e) {
			logger.error("限号配置错误或不符合规则！", e);
			throw new ResultJsonException(ResultBO.err("40699"));
		}
		return ResultBO.ok();
	}

	/**
	 * @desc 验证直选(单式、复式)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证直选(单式、复式)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitDIRDefault(OrderDetailVO orderDetail, BetContentVO betContent,
			List<String> limitNumList) throws ResultJsonException {
		logger.debug("PL3 verifyLimitDIRDefault begin!");
		// 验证百、十、个区域限号信息:
		String[] area1Arr = betContent.getArea1();// 百位号码数
		String[] area2Arr = betContent.getArea2();// 十位号码数
		String[] area3Arr = betContent.getArea3();// 个位号码数

		// 1.若每个区域的号码包含配置限号数字各区域的号码，则说明被限号
		boolean r1 = Arrays.asList(area1Arr).contains(limitNumList.get(0)); // 第一个元素对应百位号码配置
		boolean r2 = Arrays.asList(area2Arr).contains(limitNumList.get(1)); // 第二个元素对应十位号码配置
		boolean r3 = Arrays.asList(area3Arr).contains(limitNumList.get(2)); // 第三个元素对应个位号码配置

		// 只要有一个区域不包含，则认为没有限号(如果都包含了，说明该投注内容被限号了)
		Assert.isTrue(!r1 || !r2 || !r3, "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.VERTICAL_BAR));

		logger.debug("PL3 verifyLimitDIRDefault end!");
		return ResultBO.ok();
	}
	
	/**
	 * @desc 验证组三(单式)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证组三(单式)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitG3Single(OrderDetailVO orderDetail, BetContentVO betContent,
			List<String> limitNumList) throws ResultJsonException {
		logger.debug("PL3 verifyLimitG3Single begin!");
		// 20170701 add 单式两种格式（1,1,0 或 1|0）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
		
		if (betContent.getArea1() != null && betContent.getArea2() != null) {
			// 1.分区的可共用组三复式限号
			verifyLimitG3Multiple(orderDetail, betContent, limitNumList); 
		} else {
			// 验证组三限号信息:
			String[] area1Arr = betContent.getArea1();// 组三号码

			// 1.若目标组三号码列表(单式的号码列表),重号数字与限号号码中的重号数字一样且非重号数字与限号号码中的非重号数字一样，则说明被限号
			Map<String, Integer> grpTarget = ArrayUtil.group(area1Arr);
			Map<String, Integer> grpLimit = ArrayUtil.group(limitNumList.toArray(new String[limitNumList.size()]));
			if (grpTarget != null && grpTarget.size() == Constants.NUM_2 && grpLimit != null && grpLimit.size() == Constants.NUM_2) {
				// 2.过滤出目标号码和限号号码中的重复数字与非重复数字
				String targetRepeatNum = ""; // 重复数字
				String targetNoReatNum = ""; // 非重复数字
				for (Entry<String, Integer> tmp : grpTarget.entrySet()) {
					if (tmp.getValue().intValue() == Constants.NUM_1) {
						targetNoReatNum = tmp.getKey();
					} else if (tmp.getValue().intValue() == Constants.NUM_2) {
						targetRepeatNum = tmp.getKey();
					}
				}
				String limitRepeatNum = ""; // 重复数字
				String limitNoReatNum = ""; // 非重复数字
				for (Entry<String, Integer> tmp : grpLimit.entrySet()) {
					if (tmp.getValue().intValue() == Constants.NUM_1) {
						limitNoReatNum = tmp.getKey();
					} else if (tmp.getValue().intValue() == Constants.NUM_2) {
						limitRepeatNum = tmp.getKey();
					}
				}
				// 3.判断目标号码的重号数字与限号中的重号数字一样且非重号数字与限号中的非重号数字一样
				Assert.isTrue(!(targetRepeatNum.equals(limitRepeatNum) && targetNoReatNum.equals(limitNoReatNum)), "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.COMMA));
			}
		}
		
		logger.debug("PL3 verifyLimitG3Single end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 验证组三(复式)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证组三(复式)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitG3Multiple(OrderDetailVO orderDetail, BetContentVO betContent,
			List<String> limitNumList) throws ResultJsonException {
		logger.debug("PL3 verifyLimitG3Multiple begin!");
		// 20170701 add 复式两种格式（0,1,2,3 或 0,1,2,3|0,1,2,3）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
		
		if (betContent.getArea1() != null && betContent.getArea2() != null) {
			// 验证组三限号信息:
			String[] area1Arr = betContent.getArea1();// 组三号码前区
			String[] area2Arr = betContent.getArea2();// 组三号码后区

			// 1.若前区包含限号中的重号数字且后区包含非重号数字，则说明被限号
			Map<String, Integer> grp = ArrayUtil.group(limitNumList.toArray(new String[limitNumList.size()]));
			if (grp != null && grp.size() == Constants.NUM_2) {
				// 2.过滤出限号号码中的重复数字与非重复数字
				String repeatNum = ""; // 重复数字
				String noReatNum = ""; // 非重复数字
				for (Entry<String, Integer> tmp : grp.entrySet()) {
					if (tmp.getValue().intValue() == Constants.NUM_1) {
						noReatNum = tmp.getKey();
					} else if (tmp.getValue().intValue() == Constants.NUM_2) {
						repeatNum = tmp.getKey();
					}
				}
				// 3.判断前区包含限号中的重号数字且后区包含非重号数字
				boolean r1 = Arrays.asList(area1Arr).contains(repeatNum);
				boolean r2 = Arrays.asList(area2Arr).contains(noReatNum);

				Assert.isTrue(!(r1 && r2), "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.COMMA));
			}
		} else {
			// 验证组三限号信息:
			String[] area1Arr = betContent.getArea1();// 组三号码

			// 1.若目标组三号码列表(复式的号码列表)包含所有的配置限号号码，则说明被限号
			boolean r1 = Arrays.asList(area1Arr).containsAll(limitNumList);

			Assert.isTrue(!r1, "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.COMMA));
		}
		
		logger.debug("PL3 verifyLimitG3Multiple end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 验证组六(单式、复式)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证组六(单式、复式)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitG6Default(OrderDetailVO orderDetail, BetContentVO betContent,
			List<String> limitNumList) throws ResultJsonException {
		logger.debug("PL3 verifyLimitG6Default begin!");
		// 验证组六限号信息:
		String[] area1Arr = betContent.getArea1();// 组六号码

		// 1.若目标组六号码列表(单式的号码列表或复式的号码列表)包含所有的配置限号号码，则说明被限号
		boolean r1 = Arrays.asList(area1Arr).containsAll(limitNumList);

		Assert.isTrue(!r1, "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.COMMA));

		logger.debug("PL3 verifyLimitG6Default end!");
		return ResultBO.ok();
	}

	/**
	 * @desc 验证直选,组三，组六(和值)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证直选,组三，组六(和值)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitSum(OrderDetailVO orderDetail, BetContentVO betContent, List<String> limitNumList)
			throws ResultJsonException {
		logger.debug("PL3 verifyLimitSum begin!");
		// 验证和值基本信息:
		String[] area1Arr = betContent.getArea1();// 和值号码

		// 1.计算限号配置的和值
		int sum = 0;
		for (String temp : limitNumList) {
			sum += Integer.valueOf(temp);
		}
		// 2.若目标和值列表包含配置和值，则说明被限号
		boolean r1 = Arrays.asList(area1Arr).contains(String.valueOf(sum));

		Assert.isTrue(!r1, "40698", orderDetail.getPlanContent(), String.valueOf(sum));

		logger.debug("PL3 verifyLimitSum end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 验证组六(胆拖)限号
	 * @author huangb
	 * @date 2017年6月19日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param limitNumList
	 *            配置的限号数字集合
	 * @return 验证组六(胆拖)限号
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyLimitG6DT(OrderDetailVO orderDetail, BetContentVO betContent,
			List<String> limitNumList) throws ResultJsonException {
		logger.debug("PL3 verifyLimitG6DT begin!");
		// 验证胆、拖限号:
		String[] area1Arr = betContent.getArea1();// 胆区号码
		String[] area2Arr = betContent.getArea2();// 拖区号码

		/**
		 * 若配置号码包含所有胆号码且胆+拖包含所有配置号码，则说明被限号 eg:目标号码0,1#2,3,4,5,6,7,8,9 ， 配置号码1,2,3则不在限号内；若配置号码0,1,2则说明目标号码限号
		 */
		// 1.配置号码包含所有胆号码
		boolean d1 = limitNumList.containsAll(Arrays.asList(area1Arr));

		// 2.胆+拖包含所有配置号码
		String[] dtArr = ArrayUtil.addAll(area1Arr, area2Arr);
		boolean r1 = Arrays.asList(dtArr).containsAll(limitNumList);
		
		Assert.isTrue(!(d1 && r1), "40698", orderDetail.getPlanContent(), StringUtil.collectionToDelimitedString(limitNumList, SymbolConstants.COMMA));
		
		logger.debug("PL3 verifyLimitG6DT end!");
		return ResultBO.ok();
	}
}
