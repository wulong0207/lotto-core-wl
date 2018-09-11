package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.validator;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator.RnValidator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.HighConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    组选
 * @author  Tony Wang
 * @date    2017年6月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class SscGroupValidator extends RnValidator{

	@Override
	protected String[] getNumRange() {
		return HighConstants.SSC_NUM_RANGE;
	}
	
	protected int validateSum(OrderDetailVO orderDetail) {
		// 请重写
		throw new ResultJsonException(ResultBO.err(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
//	@Override
//	protected int validateSingle(OrderDetailVO orderDetail) {
//		/*
//		 * 单式：1,2,3(组六)  0,1,1(组三) 0,1(组二)
//		 */
//		String planContent = orderDetail.getPlanContent();
//		String reg = "^([0-9])(,[0-9]){"+ (getMinSelect()-1) +"}$";
//		// 选号可重复，验证个数及数值范围
//		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
//		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		// 若单式通过格式验证，则注数一定为1注
//		return 1;
//	}

//	@Override
//	protected int validateMulti(OrderDetailVO orderDetail) {
//		/*
//		 * 复式：2,3,4,5,6
//		 */
//		String planContent = orderDetail.getPlanContent();
//		String reg = "^([0-9])(,[0-9]){"+ getMinSelect() +"}$";
//		// 选号可重复，验证个数及数值范围
//		Assert.isTrue(Pattern.matches(reg, planContent), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA);
//		Assert.isFalse(ArrayUtil.isRepeat(planContents), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		// 重算注数
//		return (int)NumberUtil.getCombinationCount(planContents.length, getMinSelect());
//	}
//
//	@Override
//	protected int validateDantuo(OrderDetailVO orderDetail) {
//		/*
//		 * 胆拖：1,2#3,4,5,6
//		 */
//		String planContent = orderDetail.getPlanContent();
//		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
//		// 胆拖包含胆码和拖码两部分
//		Assert.isTrue(planContents.length==2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 验证胆码和拖码的个数不超过10个，且各自不重复及不相互重复
//		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.NUMBER_SIGN);
//		// 需求变为页面可以下一注的胆拖，入订单时也是胆拖模式，但拆票时会折成单式票
//		// 胆码加拖码个数[getMinSelect(),10]个
//		Assert.isTrue((betNums.length >= getMinSelect() && betNums.length <= NUM_RANGE.length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 验证胆码加拖码的个数及重复性
//		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
//		String [] danma = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
//		String [] tuoma = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
//		// 胆码[1,getMinSelect())个
//		Assert.isTrue((danma.length >= 1 && danma.length < getMinSelect()), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		// 拖码[1,10)个
//		Assert.isTrue((tuoma.length >= 1 && tuoma.length < NUM_RANGE.length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
//		return (int)NumberUtil.getCombinationCount(tuoma.length, getMinSelect() - danma.length);
//	}

	
}
