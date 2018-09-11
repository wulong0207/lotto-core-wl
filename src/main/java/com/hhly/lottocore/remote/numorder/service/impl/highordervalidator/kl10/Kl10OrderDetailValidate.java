package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.HighOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.ChildValidator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitValidator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    快乐10分订单验证抽象类
 * @author  Tony Wang
 * @date    2017年7月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class Kl10OrderDetailValidate extends HighOrderDetailValidate {

	@Autowired
	private ChildValidator kl10StValidator;
	
	@Autowired
	private ChildValidator kl10HtValidator;
	
	@Autowired
	private ChildValidator kl10R2Validator;
	
	@Autowired
	private ChildValidator kl10R3Validator;
	
	@Autowired
	private ChildValidator kl10R4Validator;
	
	@Autowired
	private ChildValidator kl10R5Validator;
	
	@Autowired
	private ChildValidator kl10D2Validator;
	
	@Autowired
	private ChildValidator kl10D3Validator;
	
	@Autowired
	private LimitValidator commonLimitValidator;
	
	@Autowired
	private LimitValidator singleMultiLimitValidator;
	
	@Autowired
	private LimitValidator positionLimitValidator;
	
	
	@Override
	public int validatePlanContent(OrderDetailVO orderDetailVO) {
		ChildValidator childValidator = null;
		// 子玩法
		Integer childCode = orderDetailVO.getLotteryChildCode();
		switch (LotteryChild.valueOf(childCode)) {
			case CQKL10_ST:
			case DKL10_ST:
				childValidator = kl10StValidator;
				break;
			case CQKL10_HT:
			case DKL10_HT:
				childValidator = kl10HtValidator;
				break;
			case CQKL10_R2:
			case CQKL10_G2:
			case DKL10_R2:
			case DKL10_G2:
				childValidator = kl10R2Validator;
				break;
			case CQKL10_D2:
			case DKL10_D2:
				childValidator = kl10D2Validator;
				break;
			case CQKL10_R3:
			case CQKL10_G3:
			case DKL10_R3:
			case DKL10_G3:
				childValidator = kl10R3Validator;
				break;
			case CQKL10_D3:
			case DKL10_D3:
				childValidator = kl10D3Validator;
				break;
			case CQKL10_R4:
			case DKL10_R4:
				childValidator = kl10R4Validator;
				break;
			case CQKL10_R5:
			case DKL10_R5:
				childValidator = kl10R5Validator;
				break;
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		return childValidator.validate(orderDetailVO);
	}
	
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
			case CQKL10_ST:
			case CQKL10_HT:
			case DKL10_ST:
			case DKL10_HT:
				limitValidator = singleMultiLimitValidator;
				break;
			case CQKL10_R2:
			case CQKL10_G2:
			case CQKL10_R3:
			case CQKL10_G3:
			case CQKL10_R4:
			case CQKL10_R5:
			// 广东快乐10分	
			case DKL10_R2:
			case DKL10_G2:
			case DKL10_R3:
			case DKL10_G3:
			case DKL10_R4:
			case DKL10_R5:
				limitValidator = commonLimitValidator;
				break;
			case CQKL10_D2:
			case CQKL10_D3:
			case DKL10_D2:
			case DKL10_D3:
				limitValidator = positionLimitValidator;
				break;
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		limitValidator.validate(orderDetailVO, limits, limitTranslator);
	}
}
