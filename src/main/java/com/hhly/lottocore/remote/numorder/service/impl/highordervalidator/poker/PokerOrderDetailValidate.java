package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.HighOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.ChildValidator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

public abstract class PokerOrderDetailValidate extends HighOrderDetailValidate {
	
	@Autowired
	private ChildValidator pokerR1Validator;

	@Autowired
	private ChildValidator pokerR2Validator;
	
	@Autowired
	private ChildValidator pokerR3Validator;
	
	@Autowired
	private ChildValidator pokerR4Validator;
	
	@Autowired
	private ChildValidator pokerR5Validator;
	
	@Autowired
	private ChildValidator pokerR6Validator;
	
	@Autowired
	private ChildValidator pokerSameValidator;
	
	@Autowired
	private ChildValidator pokerSequenceValidator;
	
	@Autowired
	private ChildValidator pokerDzValidator;
	
	@Autowired
	private ChildValidator pokerBzValidator;
	
	@Autowired
	private LimitValidator commonLimitValidator;
	@Autowired
	private LimitValidator singleMultiLimitValidator;

	/**
	 * @desc   验证注数、倍数是否超过限制
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO
	 */
	@Override
	public void validateLimit(OrderDetailVO orderDetailVO, List<?> limits, LimitTranslator limitTranslator) {
		LimitValidator limitValidator = null;
		// 子玩法
		Integer childCode = orderDetailVO.getLotteryChildCode();
		switch (LotteryChild.valueOf(childCode)) {
		case POKER_R2:
		case POKER_R3:
		case POKER_R4:
		case POKER_R5:
		case POKER_R6:
			limitValidator = commonLimitValidator;
			break;
		case POKER_R1:
		case POKER_TH:
		case POKER_SZ:
		case POKER_DZ:
		case POKER_BZ:
			limitValidator = singleMultiLimitValidator;
			break;
		default:
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
	}
		limitValidator.validate(orderDetailVO, limits, limitTranslator);
	}
	
	@Override
	public int validatePlanContent(OrderDetailVO orderDetailVO) {
		ChildValidator childValidator = null;
		// 子玩法
		Integer childCode = orderDetailVO.getLotteryChildCode();
		switch (LotteryChild.valueOf(childCode)) {
			case POKER_R1:
				childValidator = pokerR1Validator;
				break;
			case POKER_R2:
				childValidator = pokerR2Validator;
				break;
			case POKER_R3:
				childValidator = pokerR3Validator;
				break;
			case POKER_R4:
				childValidator = pokerR4Validator;
				break;
			case POKER_R5:
				childValidator = pokerR5Validator;
				break;
			case POKER_R6:
				childValidator = pokerR6Validator;
				break;
			case POKER_TH:
				childValidator = pokerSameValidator;
				break;
			case POKER_SZ:
				childValidator = pokerSequenceValidator;
				break;
			case POKER_DZ:
				childValidator = pokerDzValidator;
				break;
			case POKER_BZ:
				childValidator = pokerBzValidator;
				break;
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		return childValidator.validate(orderDetailVO);
	}

	private static final LimitTranslator POKER_LIMIT_TRANSLATOR = new LimitTranslator() {
		@Override
		public String translate(String originalLimitContent,Integer lotteryChildCode) {
			String translated;
			if(Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_TH.getValue())){
				translated = originalLimitContent.replaceAll("1T", "黑桃同花")
						.replaceAll("2T", "红心同花")
						.replaceAll("3T", "梅花同花")
						.replaceAll("4T", "方块同花")
						.replaceAll("AT", "同花全包");
			}
			else if(Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_SZ.getValue())) {
				translated = originalLimitContent.replaceAll("XYZ", "顺子全包");
			}
			else if(Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_DZ.getValue())) {
				translated = originalLimitContent.replaceAll("XX", "对子全包");
			}
			else if(Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_BZ.getValue())) {
				translated = originalLimitContent.replaceAll("YYY", "豹子全包");
			}
			else {
				translated = originalLimitContent;
			}
			return translated;
		}
		@Override
		public boolean whetherTraslate(Integer lotteryChildCode) {
			return (
					Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_TH.getValue()) ||
					Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_SZ.getValue()) ||
					Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_DZ.getValue()) ||
					Objects.equals(lotteryChildCode, LotteryChildEnum.LotteryChild.POKER_BZ.getValue()) 
					);
		}
	};
	
	@Override
	public LimitTranslator getLimitTranslator() {
		return POKER_LIMIT_TRANSLATOR;
	}
}
