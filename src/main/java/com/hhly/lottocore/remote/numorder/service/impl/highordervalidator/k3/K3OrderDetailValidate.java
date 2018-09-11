package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3;

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
 * @desc    快3公共验证方法
 * @author  Tony Wang
 * @date    2017年3月30日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class K3OrderDetailValidate extends HighOrderDetailValidate {

	/**
	 * 和值
	 */
	@Autowired
	private ChildValidator k3SumValidator;

	/**
	 * 三同号通选
	 */
	@Autowired
	private ChildValidator k3Tt3Validator;
	
	/**
	 * 三同号单选
	 */
	@Autowired
	private ChildValidator k3Td3Validator;
	
	/**
	 * 三不同号
	 */
	@Autowired
	private ChildValidator k3Bt3Validator;
	
	/**
	 * 三连号通选
	 */
	@Autowired
	private ChildValidator k3L3Validator;
	
	/**
	 * 二同号复选
	 */
	@Autowired
	private ChildValidator k3Tf2Validator;
	
	/**
	 * 二同号单选
	 */
	@Autowired
	private ChildValidator k3Td2Validator;
	
	/**
	 * 二不同号
	 */
	@Autowired
	private ChildValidator k3Bt2Validator;
	
	@Autowired
	private LimitValidator singleMultiLimitValidator;
	
	@Autowired
	private LimitValidator singleLimitValidator;
	
	@Autowired
	private LimitValidator commonLimitValidator;
	
	
	/**
	 * @desc  验证投注内容  
	 * 注意：不同的子玩法有不同的投注方式
	 * 只有三不同和二不同才有胆拖，三同号通选和三连号通选只有单式
	 * 1. 根据子玩法及投注方式，用正则表达式验证投注内容是否合法
	 * A.和值投注
	 * a)单式：1
	 * b)复式：4,5,6
	 * B.三同号通选投注(豹子全包)
	 * a)单式：3T
	 * C.三同号单选投注(豹子单选)
	 * a)单式：111
	 * b)复式：111;222;333
	 * D.三不同号投注
	 * a)单式:1,2,3
	 * b)复式:1,2,3,4
	 * c)胆拖:1,2#3,4
	 * E.三连号通选投注(顺子全包)
	 * a)单式：3L
	 * F.二同号复选投注(对子复选)
	 * a)单式：11*
	 * b)复式：11*;22*;33*
	 * G.二同号单选投注(对子单选)
	 * a)单式:1,1,2
	 * b)复式:1,1,2;2,2,1;3,3,2;
	 * H.二不同号投注
	 * a)单式1,2
	 * b)复式 1,2,3
	 * c)胆拖1#2,3,4
	 * I.开奖结果格式：1,2,3
	 * 
	 * 2.拆分投注内容，验证投注号码个数(单式、复式的正则已验证，胆拖及直选模式要再验证所有区域号码的个数)、数值范围(正则已验证)、重复性
	 * 
	 * 3.返回重新计算的注数
	 * 
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO 
	 */
	@Override
	public int validatePlanContent(OrderDetailVO orderDetailVO) {
		ChildValidator childValidator = null;
		// 子玩法
		Integer childCode = orderDetailVO.getLotteryChildCode();
		switch (LotteryChild.valueOf(childCode)) {
			case JSK3_S:
			case JXK3_S:
				childValidator = k3SumValidator;
				break;
			case JSK3_TT3:
			case JXK3_TT3:
				childValidator = k3Tt3Validator;
				break;
			case JSK3_TD3:
			case JXK3_TD3:	
				childValidator = k3Td3Validator;
				break;
			case JSK3_BT3:
			case JXK3_BT3:
				childValidator = k3Bt3Validator;
				break;
			case JSK3_L3:
			case JXK3_L3:
				childValidator = k3L3Validator;
				break;
			case JSK3_TF2:
			case JXK3_TF2:
				childValidator = k3Tf2Validator;
				break;
			case JSK3_TD2:
			case JXK3_TD2:
				childValidator = k3Td2Validator;
				break;
			case JSK3_BT2:
			case JXK3_BT2:
				childValidator = k3Bt2Validator;
				break;
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		return childValidator.validate(orderDetailVO);
	}


	@Override
	public void validateLimit(OrderDetailVO orderDetailVO, List<?> limits, LimitTranslator limitTranslator) {
		/*
		 * 快3的和相当一种子玩法，限号不用考虑对就的单式是否要限号，而快乐扑克则要，和大乐透类似
		 * 只要有一个豹子限号，则豹子通告也限号，三连号通告也类似
		 * 
		 * 2017-6-21 跟运维确认，若限了三同号111，不会自动限号三同能选，因为是属于不同的子玩法，若要限则要后台配置"3T"，二同单与二同复也类似
		 */
		LimitValidator limitValidator = null;
		// 子玩法
		Integer childCode = orderDetailVO.getLotteryChildCode();
		switch (LotteryChild.valueOf(childCode)) {
		case JSK3_S:
		case JSK3_TD3:
		case JSK3_TF2:
		case JSK3_TD2:
		case JXK3_S:
		case JXK3_TD3:
		case JXK3_TF2:
		case JXK3_TD2:
			limitValidator = singleMultiLimitValidator;
			break;
		case JSK3_TT3:
		case JSK3_L3:
		case JXK3_TT3:
		case JXK3_L3:
			limitValidator = singleLimitValidator;
			break;
		case JSK3_BT3:
		case JSK3_BT2:
		case JXK3_BT3:
		case JXK3_BT2:
			limitValidator = commonLimitValidator;
			break;
		default:
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		limitValidator.validate(orderDetailVO, limits, limitTranslator);
	}


	
}
