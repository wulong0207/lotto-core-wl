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
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 七乐彩订单明细校验(仅提供明细内容验证，其它验证统一在主流程处理)
 * @author huangb
 * @date 2017年6月14日
 * @company 益彩网络
 * @version v1.0
 */
@Component("qlcOrderDetailValidate")
public class QlcOrderDetailValidate extends OrderDetailValidate {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(QlcOrderDetailValidate.class);
	
	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("QlcOrderDetailValidate verifyContentType begin!");

		// 1.按内容类型，将投注内容分割并校验长度
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型(子玩法下面分内容类型)
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		Assert.notNull(lotChild, "40437", planContent);
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		Assert.notNull(type, "40401");

		switch (lotChild) {
		case QLC_PT:
			// 普通玩法(包含单式、复式)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.QLC_REGEX_PT_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.QLC_REGEX_PT_MULTIPLE, planContent), "40402", planContent);
				break;
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
			return ResultBO.ok();
		case QLC_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				Assert.isTrue(Pattern.matches(NUMConstants.QLC_REGEX_DT_DANTUO, planContent), "40402", planContent);
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
		logger.debug("QlcOrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		
		switch (lotChild) {
		case QLC_PT:
			// 普通玩法(包含单式、复式)
			switch (type) {
			case SINGLE:
			case MULTIPLE:
				return new BetContentVO(ArrayUtil.numLeftAddZ(planContent.split(SymbolConstants.COMMA)));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case QLC_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				String[] betCode = planContent.split(SymbolConstants.NUMBER_SIGN);
				return new BetContentVO(ArrayUtil.numLeftAddZ(betCode[0].split(SymbolConstants.COMMA)),
						ArrayUtil.numLeftAddZ(betCode[1].split(SymbolConstants.COMMA)));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("QlcOrderDetailValidate genBetNum begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());

		switch (lotChild) {
		case QLC_PT:
			// 普通玩法(包含单式、复式)
			switch (type) {
			case SINGLE:
				return Constants.NUM_1;
			case MULTIPLE:
				int area1Len = betContent.getArea1().length;// 选号个数
				// 注数 = 选号个数中选7个的组合数
				return (int) NumberUtil.getCombinationCount(area1Len, NUMConstants.QLC_CHOOSE_7);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case QLC_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				int danLen = betContent.getArea1().length;// 胆个数
				int tuoLen = betContent.getArea2().length;// 拖个数
				// 注数 = 拖选号个数中选（7-胆个数）个的组合数
				return (int) NumberUtil.getCombinationCount(tuoLen, (NUMConstants.QLC_CHOOSE_7 - danLen));
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
		logger.debug("QlcOrderDetailValidate verifyBetContent begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		
		switch (lotChild) {
		case QLC_PT:
			// 普通玩法(包含单式、复式)
			switch (type) {
			case SINGLE:
				return verifyPTSingle(orderDetail, betContent);
			case MULTIPLE:
				return verifyPTMultiple(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		case QLC_DT:
			// 胆拖玩法(包含胆拖)
			switch (type) {
			case DANTUO:
				return verifyDT(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	/**
	 * @desc 校验普通玩法-单式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验普通玩法-单式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyPTSingle(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("QLC verifyPTSingle begin!");
		// 验证选号基本信息:
		String[] area1Arr = betContent.getArea1();// 选号
		
		// 2.选号的数字范围(01-30)
		for (String area1 : area1Arr) {
			Assert.isTrue(validNumRange(area1, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX), "40598", orderDetail.getPlanContent(), area1, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX);
		}
		
		// 3.选号重复性(不可重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40599", orderDetail.getPlanContent());

		logger.debug("QLC verifyPTSingle end!");
		return ResultBO.ok();
	}
	
	/**
	 * @desc 校验普通玩法-复式投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验普通玩法-复式投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyPTMultiple(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("QLC verifyPTMultiple begin!");
		
		// 同单式验证
		verifyPTSingle(orderDetail, betContent);
		
		logger.debug("QLC verifyPTMultiple end!");
		return ResultBO.ok();
	}
	/**
	 * @desc 校验胆拖玩法-胆拖投注内容
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 校验胆拖玩法-胆拖投注内容
	 * @throws ResultJsonException
	 */
	private ResultBO<?> verifyDT(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("QLC verifyDT begin!");
		// 验证胆、拖区域基本信息:
		String[] area1Arr = betContent.getArea1();// 胆区号码
		String[] area2Arr = betContent.getArea2();// 拖区号码
		
		
		// 1.选号的个数(胆1~6个,拖1~29个，胆+拖7~30个)
		
		Assert.isTrue(validLenRange(area1Arr.length + area2Arr.length, NUMConstants.QLC_CHOOSE_7, NUMConstants.QLC_CHOOSE_30), "40600", orderDetail.getPlanContent(), NUMConstants.QLC_CHOOSE_7, NUMConstants.QLC_CHOOSE_30);
		
		// 2.选号的数字范围(01-30) (胆拖区都是该范围)
		for (String area1 : area1Arr) {
			Assert.isTrue(validNumRange(area1, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX), "40598", orderDetail.getPlanContent(), area1, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX);
		}
		for (String area2 : area2Arr) {
			Assert.isTrue(validNumRange(area2, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX), "40598", orderDetail.getPlanContent(), area2, NUMConstants.QLC_NUM_MIN, NUMConstants.QLC_NUM_MAX);
		}

		// 3.选号重复性(胆不重复，拖不重复，胆拖不重复)
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40601", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40602", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeatCompareTo(area1Arr, area2Arr), "40603", orderDetail.getPlanContent());

		logger.debug("QLC verifyDT end!");
		return ResultBO.ok();
	}
}
