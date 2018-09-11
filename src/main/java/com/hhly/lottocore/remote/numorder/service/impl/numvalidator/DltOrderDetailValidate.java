package com.hhly.lottocore.remote.numorder.service.impl.numvalidator;

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
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 大乐透订单明细校验(仅提供明细内容验证，其它验证统一在主流程处理)
 * @author huangb
 * @date 2017年3月13日
 * @company 益彩网络
 * @version v1.0
 */
@Component("dltOrderDetailValidate")
public class DltOrderDetailValidate extends OrderDetailValidate {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(DltOrderDetailValidate.class);

	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("DltOrderDetailValidate verifyContentType begin!");

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
		case DLT_PT:
			// 普通玩法(包含单式和复式)
			switch (type) {
			case SINGLE:
				if (!Pattern.matches(NUMConstants.DLT_REGEX_BET_SINGLE, planContent)) {
					throw new ResultJsonException(ResultBO.err("40402", planContent));
				}
				break;
			case MULTIPLE:
				if (!Pattern.matches(NUMConstants.DLT_REGEX_BET_MULTIPLE, planContent)) {
					throw new ResultJsonException(ResultBO.err("40402", planContent));
				}
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		case DLT_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				/**
				 * 大乐透胆拖格式支持两种<br>
				 * 一.01,02#03,04,05,06,07,08|08#11,12<br>
				 * 二.01,02#03,04,05,06,07,08|11, 12
				 */
				if (!Pattern.matches(NUMConstants.DLT_REGEX_BET_DANTUO, planContent)) {
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
		logger.debug("DltOrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 投注内容默认分割长度
		String[] betCode = planContent.split("\\" + SymbolConstants.VERTICAL_BAR);
		switch (lotChild) {
		case DLT_PT:
			return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
					ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)));
		case DLT_DT:
			/**
			 * 大乐透胆拖格式支持两种<br>
			 * 一.01,02#03,04,05,06,07,08|08#11,12<br>
			 * 二.01,02#03,04,05,06,07,08|11, 12
			 */
			betCode = planContent.split(SymbolConstants.MIDDLE_PARENTHESES_LEFT + SymbolConstants.NUMBER_SIGN + "\\"
					+ SymbolConstants.VERTICAL_BAR + SymbolConstants.MIDDLE_PARENTHESES_RIGHT);
			if (betCode.length == NUMConstants.NUM_4) {
				// 1.如果长度为4(前后区都有胆)
				return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
						ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)),
						ArrayUtil.numLeftAddZ(betCode[2].split(SymbolConstants.COMMA)),
						ArrayUtil.numLeftAddZ(betCode[3].split(SymbolConstants.COMMA)));
			} else {
				// 2.如果长度为3(后区无胆)
				return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
						ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)), new String[0],
						ArrayUtil.numLeftAddZ(betCode[2].split(SymbolConstants.COMMA)));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getValue()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("DltOrderDetailValidate genBetNum begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		switch (lotChild) {
		case DLT_PT:
			int frontLenPT = betContent.getArea1().length;// 红球
			int backLenPT = betContent.getArea2().length;// 蓝球
			// 从选中的红球集合中任选5个的组合数
			int combCountFrontPT = (int) NumberUtil.getCombinationCount(frontLenPT, NUMConstants.DLT_RED_BASE_COUNT);
			// 从选中的蓝球集合中任选2个的组合数
			int combCountBackPT = (int) NumberUtil.getCombinationCount(backLenPT, NUMConstants.DLT_BLUE_BASE_COUNT);
			// 注数 = 红球组合数 * 篮球组合数
			return combCountFrontPT * combCountBackPT;
		case DLT_DT:
			int frontDanLenDT = betContent.getArea1().length;// 红胆
			int frontTuoLenDT = betContent.getArea2().length;// 红拖
			int backDanLenDT = betContent.getArea3().length;// 蓝胆(可能为0)
			int backTuoLenDT = betContent.getArea4().length;// 蓝拖
			// 红胆固定，从红拖中任选(5-_danMas)个的组合数
			int combCountFrontDT = (int) NumberUtil.getCombinationCount(frontTuoLenDT, (NUMConstants.DLT_RED_BASE_COUNT - frontDanLenDT));
			// 蓝胆固定，从蓝拖中任选(2-_danMas)个的组合数
			int combCountBackDT = (int) NumberUtil.getCombinationCount(backTuoLenDT, (NUMConstants.DLT_BLUE_BASE_COUNT - backDanLenDT));
			// 注数 = 红球组合数 * 篮球组合数
			return combCountFrontDT * combCountBackDT;
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getValue()));
		}
	}

	@Override
	protected ResultBO<?> verifyBetContent(OrderDetailVO orderDetailVO, BetContentVO betContent)
			throws ResultJsonException {
		logger.debug("DltOrderDetailValidate verifyBetContent begin!");

		LotteryChild lotChild = LotteryChild.valueOf(orderDetailVO.getLotteryChildCode());
		switch (lotChild) {
		case DLT_PT:
			return verifyPT(orderDetailVO, betContent);
		case DLT_DT:
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
		logger.debug("DLT verifyPT begin!");

		// 验证普通投注基本信息:
		String[] redArr = betContent.getArea1();// 红球
		String[] blueArr = betContent.getArea2();// 蓝球
		if (ArrayUtil.isEmpty(redArr) || ArrayUtil.isEmpty(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40405"));
		}
		// 1.选号的个数
		if (!validLenRange(redArr.length, NUMConstants.DLT_CHOOSE_RED5, NUMConstants.DLT_CHOOSE_RED35)) {
			throw new ResultJsonException(ResultBO.err("40406", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_RED5, NUMConstants.DLT_CHOOSE_RED35));
		}
		if (!validLenRange(blueArr.length, NUMConstants.DLT_CHOOSE_BLUE1, NUMConstants.DLT_CHOOSE_BLUE12)) {
			throw new ResultJsonException(ResultBO.err("40407", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_BLUE1, NUMConstants.DLT_CHOOSE_BLUE12));
		}

		// 2.选号的数字范围
		for (String red : redArr) {
			if (!validNumRange(red, NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40408", orderDetail.getPlanContent(), red,
						NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX));
			}
		}
		for (String blue : blueArr) {
			if (!validNumRange(blue, NUMConstants.DLT_BLUE_MIN, NUMConstants.DLT_BLUE_MAX)) {
				throw new ResultJsonException(ResultBO.err("40410", orderDetail.getPlanContent(), blue,
						NUMConstants.DLT_BLUE_MIN, NUMConstants.DLT_BLUE_MAX));
			}
		}

		// 2.验证普通投注号码重复性
		if (ArrayUtil.isRepeat(redArr)) {
			throw new ResultJsonException(ResultBO.err("40411", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(blueArr)) {
			throw new ResultJsonException(ResultBO.err("40412", orderDetail.getPlanContent()));
		}

		logger.debug("DLT verifyPT end!");
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
		logger.debug("DLT verifyDT begin!");

		// 验证胆拖投注基本信息:
		String[] frontDanArr = betContent.getArea1();// 红胆
		String[] frontTuoArr = betContent.getArea2();// 红拖
		String[] backDanArr = betContent.getArea3();// 蓝胆(长度可能为0)
		String[] backTuoArr = betContent.getArea4();// 蓝拖
		if (ArrayUtil.isEmpty(frontDanArr) || ArrayUtil.isEmpty(frontTuoArr) || ArrayUtil.isEmpty(backTuoArr)) {
			throw new ResultJsonException(ResultBO.err("40405"));
		}
		// 1.选号的个数
		if (!validLenRange(frontDanArr.length, NUMConstants.DLT_CHOOSE_RED_DAN1, NUMConstants.DLT_CHOOSE_RED_DAN4)) {
			throw new ResultJsonException(ResultBO.err("40421", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_RED_DAN1, NUMConstants.DLT_CHOOSE_RED_DAN4));
		}
		if (!validLenRange(frontTuoArr.length, NUMConstants.DLT_CHOOSE_RED_TUO1, NUMConstants.DLT_CHOOSE_RED_TUO34)) {
			throw new ResultJsonException(ResultBO.err("40422", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_RED_TUO1, NUMConstants.DLT_CHOOSE_RED_TUO34));
		}
		if (!validLenRange((frontDanArr.length + frontTuoArr.length), NUMConstants.DLT_CHOOSE_RED_DANTUO5,
				NUMConstants.DLT_CHOOSE_RED_DANTUO35)) {
			throw new ResultJsonException(ResultBO.err("40423", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_RED_DANTUO5, NUMConstants.DLT_CHOOSE_RED_DANTUO35));
		}

		if (!validLenRange(backDanArr.length, NUMConstants.DLT_CHOOSE_BLUE_DAN0, NUMConstants.DLT_CHOOSE_BLUE_DAN1)) {
			throw new ResultJsonException(ResultBO.err("40424", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_BLUE_DAN0, NUMConstants.DLT_CHOOSE_BLUE_DAN1));
		}
		if (!validLenRange(backTuoArr.length, NUMConstants.DLT_CHOOSE_BLUE_TUO1, NUMConstants.DLT_CHOOSE_BLUE_TUO12)) {
			throw new ResultJsonException(ResultBO.err("40425", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_BLUE_TUO1, NUMConstants.DLT_CHOOSE_BLUE_TUO12));
		}
		if (!validLenRange((backDanArr.length + backTuoArr.length), NUMConstants.DLT_CHOOSE_BLUE_DANTUO2,
				NUMConstants.DLT_CHOOSE_BLUE_DANTUO12)) {
			throw new ResultJsonException(ResultBO.err("40426", orderDetail.getPlanContent(),
					NUMConstants.DLT_CHOOSE_BLUE_DANTUO2, NUMConstants.DLT_CHOOSE_BLUE_DANTUO12));
		}

		// 2.选号的数字范围
		for (String dan : frontDanArr) {
			if (!validNumRange(dan, NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40427", orderDetail.getPlanContent(), dan,
						NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX));
			}
		}
		for (String tuo : frontTuoArr) {
			if (!validNumRange(tuo, NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX)) {
				throw new ResultJsonException(ResultBO.err("40428", orderDetail.getPlanContent(), tuo,
						NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX));
			}
		}
		for (String dan : backDanArr) {
			if (!validNumRange(dan, NUMConstants.DLT_BLUE_MIN, NUMConstants.DLT_BLUE_MAX)) {
				throw new ResultJsonException(ResultBO.err("40429", orderDetail.getPlanContent(), dan,
						NUMConstants.DLT_BLUE_MIN, NUMConstants.DLT_BLUE_MAX));
			}
		}
		for (String tuo : backTuoArr) {
			if (!validNumRange(tuo, NUMConstants.DLT_BLUE_MIN, NUMConstants.DLT_BLUE_MAX)) {
				// 有无蓝胆的提示信息不一样
				if (backDanArr.length > Constants.NUM_0) {
					throw new ResultJsonException(ResultBO.err("40430", orderDetail.getPlanContent(), tuo,
							NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX));
				} else {
					throw new ResultJsonException(ResultBO.err("40410", orderDetail.getPlanContent(), tuo,
							NUMConstants.DLT_RED_MIN, NUMConstants.DLT_RED_MAX));
				}
			}
		}

		// 3.验证胆拖投注号码重复性
		if (ArrayUtil.isRepeat(frontDanArr)) {
			throw new ResultJsonException(ResultBO.err("40431", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(frontTuoArr)) {
			throw new ResultJsonException(ResultBO.err("40432", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeatCompareTo(frontDanArr, frontTuoArr)) {
			throw new ResultJsonException(ResultBO.err("40433", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(backDanArr)) {
			throw new ResultJsonException(ResultBO.err("40434", orderDetail.getPlanContent()));
		}
		if (ArrayUtil.isRepeat(backTuoArr)) {
			// 有无蓝胆的提示信息不一样
			if (backDanArr.length > Constants.NUM_0) {
				throw new ResultJsonException(ResultBO.err("40435", orderDetail.getPlanContent()));
			} else {
				throw new ResultJsonException(ResultBO.err("40412", orderDetail.getPlanContent()));
			}
		}
		// 有无蓝胆的提示信息不一样
		if (backDanArr.length > Constants.NUM_0) {
			if (ArrayUtil.isRepeatCompareTo(backDanArr, backTuoArr)) {
				throw new ResultJsonException(ResultBO.err("40436", orderDetail.getPlanContent()));
			}
		}

		logger.debug("DLT verifyDT end!");
		return ResultBO.ok();
	}

}
