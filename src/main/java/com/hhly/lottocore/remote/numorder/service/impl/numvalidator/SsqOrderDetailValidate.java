package com.hhly.lottocore.remote.numorder.service.impl.numvalidator;

import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 双色球订单明细校验(仅提供明细内容验证，其它验证统一在主流程处理)
 * @author huangb
 * @date 2017年3月13日
 * @company 益彩网络
 * @version v1.0
 */
@Component("ssqOrderDetailValidate")
public class SsqOrderDetailValidate extends OrderDetailValidate {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(SsqOrderDetailValidate.class);

	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("SsqOrderDetailValidate verifyContentType begin!");

		// 1.按内容类型，将投注内容分割并校验长度
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型(子玩法下面分内容类型)
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		if (null == lotChild) {
			throw new ResultJsonException(ResultBO.err("40437", planContent));
		}
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		if (null == type) {
			throw new ResultJsonException(ResultBO.err("40401"));
		}
		switch (lotChild) {
		case SSQ_PT:
			// 普通玩法(包含单式和复式)
			switch (type) {
			case SINGLE:
				if (!Pattern.matches(NUMConstants.SSQ_REGEX_BET_SINGLE, planContent)) {
					throw new ResultJsonException(ResultBO.err("40402", planContent));
				}
				break;
			case MULTIPLE:
				if (!Pattern.matches(NUMConstants.SSQ_REGEX_BET_MULTIPLE, planContent)) {
					throw new ResultJsonException(ResultBO.err("40402", planContent));
				}
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		case SSQ_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				if (!Pattern.matches(NUMConstants.SSQ_REGEX_BET_DANTUO, planContent)) {
					throw new ResultJsonException(ResultBO.err("40402", planContent));
				}
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
		logger.debug("SsqOrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 投注内容默认分割长度
		String[] betCode = planContent.split("\\" + SymbolConstants.VERTICAL_BAR);
		switch (lotChild) {
		case SSQ_PT:
			return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
					ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)));
		case SSQ_DT:
			betCode = planContent.split(SymbolConstants.MIDDLE_PARENTHESES_LEFT + SymbolConstants.NUMBER_SIGN + "\\"
					+ SymbolConstants.VERTICAL_BAR + SymbolConstants.MIDDLE_PARENTHESES_RIGHT);
			return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
					ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)),
					ArrayUtil.numLeftAddZ(betCode[2].split(SymbolConstants.COMMA)));
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getValue()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("SsqOrderDetailValidate genBetNum begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		switch (lotChild) {
		case SSQ_PT:
			int redLenPT = betContent.getArea1().length;// 红球
			int blueLenPT = betContent.getArea2().length;// 蓝球
			// 从选中的红球集合中任选6个的组合数
			int combCountPT = (int) NumberUtil.getCombinationCount(redLenPT, NUMConstants.SSQ_RED_BASE_COUNT);
			// 注数 = 红球组合数 * 篮球数
			return combCountPT * blueLenPT;
		case SSQ_DT:
			int danLenDT = betContent.getArea1().length;// 红胆
			int tuoLenDT = betContent.getArea2().length;// 红拖
			int blueLenDT = betContent.getArea3().length;// 蓝球
			// 胆码固定，从托码中任选(6-_danMas)个的组合数
			int combCountDT = (int) NumberUtil.getCombinationCount(tuoLenDT, (NUMConstants.SSQ_RED_BASE_COUNT - danLenDT));
			// 注数 = 红球组合数 * 篮球数
			return combCountDT * blueLenDT;
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getValue()));
		}
	}

	@Override
	protected ResultBO<?> verifyBetContent(OrderDetailVO orderDetailVO, BetContentVO betContent)
			throws ResultJsonException {
		logger.debug("SsqOrderDetailValidate verifyBetContent begin!");

		LotteryChild lotChild = LotteryChild.valueOf(orderDetailVO.getLotteryChildCode());
		switch (lotChild) {
		case SSQ_PT:
			return verifyPT(orderDetailVO, betContent);
		case SSQ_DT:
			return verifyDT(orderDetailVO, betContent);
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getValue()));
		}
	}

	/**
	 * @desc 校验普通投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验普通投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyPT(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("SSQ verifyPT begin!");

		// 验证普通投注基本信息:
		String[] redArr = betContent.getArea1();// 红球
		String[] blueArr = betContent.getArea2();// 蓝球
		if (ArrayUtil.isEmpty(redArr) || ArrayUtil.isEmpty(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40405"));
		}
		// 1.选号的个数
		if (!validLenRange(redArr.length, NUMConstants.SSQ_CHOOSE_RED6, NUMConstants.SSQ_CHOOSE_RED33)) {
			throw new ResultJsonException(ResultBO.err("40406", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_RED6, NUMConstants.SSQ_CHOOSE_RED33));
		}
		if (!validLenRange(blueArr.length, NUMConstants.SSQ_CHOOSE_BLUE1, NUMConstants.SSQ_CHOOSE_BLUE16)) {
			throw new ResultJsonException(ResultBO.err("40407", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_BLUE1, NUMConstants.SSQ_CHOOSE_BLUE16));
		}

		// 2.选号的数字范围
		for (String red : redArr) {
			if (!validNumRange(red, NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40408", orderDetail.getPlanContent(), red,
						NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX));
			}
		}
		for (String blue : blueArr) {
			if (!validNumRange(blue, NUMConstants.SSQ_BLUE_MIN, NUMConstants.SSQ_BLUE_MAX)) {
				throw new ResultJsonException(ResultBO.err("40410", orderDetail.getPlanContent(), blue,
						NUMConstants.SSQ_BLUE_MIN, NUMConstants.SSQ_BLUE_MAX));
			}
		}

		// 2.验证普通投注号码重复性
		if (ArrayUtil.isRepeat(redArr)) {
			throw new ResultJsonException(ResultBO.err("40411", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40412", orderDetail.getPlanContent()));
		}

		logger.debug("SSQ verifyPT end!");
		return ResultBO.ok();
	}

	/**
	 * @desc 校验胆拖投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验胆拖投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyDT(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("SSQ verifyDT begin!");

		// 验证胆拖投注基本信息:
		String[] danArr = betContent.getArea1();// 红胆
		String[] tuoArr = betContent.getArea2();// 红拖
		String[] blueArr = betContent.getArea3();// 蓝球
		if (ArrayUtil.isEmpty(danArr) || ArrayUtil.isEmpty(tuoArr) || ArrayUtil.isEmpty(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40405"));
		}
		// 1.选号的个数
		if (!validLenRange(danArr.length, NUMConstants.SSQ_CHOOSE_DAN1, NUMConstants.SSQ_CHOOSE_DAN5)) {
			throw new ResultJsonException(ResultBO.err("40413", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_DAN1, NUMConstants.SSQ_CHOOSE_DAN5));
		}
		if (!validLenRange(tuoArr.length, NUMConstants.SSQ_CHOOSE_TUO1, NUMConstants.SSQ_CHOOSE_TUO33)) {
			throw new ResultJsonException(ResultBO.err("40414", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_TUO1, NUMConstants.SSQ_CHOOSE_TUO33));
		}
		if (!validLenRange((danArr.length + tuoArr.length), NUMConstants.SSQ_CHOOSE_DANTUO6,
				NUMConstants.SSQ_CHOOSE_DANTUO33)) {
			throw new ResultJsonException(ResultBO.err("40415", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_DANTUO6, NUMConstants.SSQ_CHOOSE_DANTUO33));
		}
		if (!validLenRange(blueArr.length, NUMConstants.SSQ_CHOOSE_BLUE1, NUMConstants.SSQ_CHOOSE_BLUE16)) {
			throw new ResultJsonException(ResultBO.err("40407", orderDetail.getPlanContent(),
					NUMConstants.SSQ_CHOOSE_BLUE1, NUMConstants.SSQ_CHOOSE_BLUE16));
		}

		// 2.选号的数字范围
		for (String dan : danArr) {
			if (!validNumRange(dan, NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40416", orderDetail.getPlanContent(), dan,
						NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX));
			}
		}
		for (String tuo : tuoArr) {
			if (!validNumRange(tuo, NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40417", orderDetail.getPlanContent(), tuo,
						NUMConstants.SSQ_RED_MIN, NUMConstants.SSQ_RED_MAX));
			}
		}
		for (String blue : blueArr) {
			if (!validNumRange(blue, NUMConstants.SSQ_BLUE_MIN, NUMConstants.SSQ_BLUE_MAX)) {
				throw new ResultJsonException(ResultBO.err("40410", orderDetail.getPlanContent(), blue,
						NUMConstants.SSQ_BLUE_MIN, NUMConstants.SSQ_BLUE_MAX));
			}
		}

		// 3.验证胆拖投注号码重复性
		if (ArrayUtil.isRepeat(danArr)) {
			throw new ResultJsonException(ResultBO.err("40418", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(tuoArr)) {
			throw new ResultJsonException(ResultBO.err("40419", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40412", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeatCompareTo(danArr, tuoArr)) {
			throw new ResultJsonException(ResultBO.err("40420", orderDetail.getPlanContent()));
		}

		logger.debug("SSQ verifyDT end!");
		return ResultBO.ok();
	}
	
}
