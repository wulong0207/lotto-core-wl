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
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc 排列五订单明细校验(仅提供明细内容验证，其它验证统一在主流程处理)
 * @author huangb
 * @date 2017年6月14日
 * @company 益彩网络
 * @version v1.0
 */
@Component("pl5OrderDetailValidate")
public class Pl5OrderDetailValidate extends OrderDetailValidate {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(Pl5OrderDetailValidate.class);
	
	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		logger.debug("Pl5OrderDetailValidate verifyContentType begin!");

		// 1.按内容类型，将投注内容分割并校验长度
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型(子玩法下面分内容类型)
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		Assert.notNull(lotChild, "40437", planContent);
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		Assert.notNull(type, "40401");

		switch (lotChild) {
		case PL5_DIRECT:
			// 直选(包含单式、复式)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL5_REGEX_DIRECT_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.PL5_REGEX_DIRECT_MULTIPLE, planContent), "40402", planContent);
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
		logger.debug("Pl5OrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		// 投注内容分割长度
		String[] betCode = null;
		switch (lotChild) {
		case PL5_DIRECT:
			// 直选(包含单式、复式)
			switch (type) {
			case SINGLE:
			case MULTIPLE:
				betCode = planContent.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
				return new BetContentVO(betCode[0].split(SymbolConstants.COMMA),
						betCode[1].split(SymbolConstants.COMMA), betCode[2].split(SymbolConstants.COMMA),
						betCode[3].split(SymbolConstants.COMMA), betCode[4].split(SymbolConstants.COMMA));
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("Pl5OrderDetailValidate genBetNum begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());

		switch (lotChild) {
		case PL5_DIRECT:
			// 直选(包含单式、复式)
			switch (type) {
			case SINGLE:
				return Constants.NUM_1;
			case MULTIPLE:
				int area1Len = betContent.getArea1().length;// 万位号码数
				int area2Len = betContent.getArea2().length;// 千位号码数
				int area3Len = betContent.getArea3().length;// 百位号码数
				int area4Len = betContent.getArea4().length;// 十位号码数
				int area5Len = betContent.getArea5().length;// 个位号码数
				// 注数 = 万区域选号数*千区域选号数*百区域选号数*十区域选号数*个区域选号数
				return area1Len * area2Len * area3Len * area4Len * area5Len;
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
		logger.debug("Pl5OrderDetailValidate verifyBetContent begin!");

		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		switch (lotChild) {
		case PL5_DIRECT:
			// 直选(包含单式、复式)
			switch (type) {
			case SINGLE:
				// 单式不用另外在验证，验证内容类型时的正则匹配能完全验证
				return ResultBO.ok();
			case MULTIPLE:
				return verifyDIRMultiple(orderDetail, betContent);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
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
		logger.debug("PL5 verifyDIRMultiple begin!");
		// 验证万、千、百、十、个区域基本信息:
		String[] area1Arr = betContent.getArea1();// 万位号码数
		String[] area2Arr = betContent.getArea2();// 千位号码数
		String[] area3Arr = betContent.getArea3();// 百位号码数
		String[] area4Arr = betContent.getArea4();// 十位号码数
		String[] area5Arr = betContent.getArea5();// 个位号码数
		
		
		// 1.选号的个数(万、千、百、十、个区域每个区域1~10个号码)(正则处理了);;5个区域的号码个数之和应该在6~50(区别于单式)
		
		// 5个区域的号码个数之和应该在6~50(区别于单式)
		Assert.isTrue(validLenRange((area1Arr.length + area2Arr.length + area3Arr.length + area4Arr.length + area5Arr.length), NUMConstants.PL5_CHOOSE_6, NUMConstants.PL5_CHOOSE_50), "40402", orderDetail.getPlanContent());
		
		// 2.各区域选号的数字范围(0-9)(正则处理了);

		// 3.选号重复性(各区域不可重复) 40596  40597
		Assert.isTrue(!ArrayUtil.isRepeat(area1Arr), "40596", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area2Arr), "40597", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area3Arr), "40556", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area4Arr), "40557", orderDetail.getPlanContent());
		Assert.isTrue(!ArrayUtil.isRepeat(area5Arr), "40558", orderDetail.getPlanContent());

		logger.debug("PL5 verifyDIRMultiple end!");
		return ResultBO.ok();
	}

}
