package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.childvalidator;

import java.util.Arrays;

import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;


public abstract class LnValidator extends AbstractChildValidator {

	@Override
	protected int validateSingle(OrderDetailVO orderDetail) {
		String planContent = orderDetail.getPlanContent();
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.VERTICAL_BAR);
		// 选号个数
		Assert.isTrue(betNums.length == getMinSelect(), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 若单式通过格式验证，则注数一定为1注
		return 1;
	}

	@Override
	protected int validateMulti(OrderDetailVO orderDetail) {
		String planContent = orderDetail.getPlanContent();
		String[] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.VERTICAL_BAR);
		// 选号个数
		Assert.isTrue(betNums.length > getMinSelect() && betNums.length <= getNumRange().length, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		//重新计算注数
		Integer lotteryChildCode = orderDetail.getLotteryChildCode();
		LotteryChild lotteryChild = LotteryChild.getByValue(lotteryChildCode);
		//乐选3和乐选4,5的计算方式不同
		switch (lotteryChild) {
		case SD11X5_L4:
		case SD11X5_L5:	
		case XJ11X5_L4:
		case XJ11X5_L5:
			return (int)NumberUtil.getCombinationCount(betNums.length, getMinSelect());
		case SD11X5_L3:	
		case XJ11X5_L3:	
			String[] bets = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
			Assert.isTrue(bets.length==3 && bets[0]!=null &&bets[1]!=null &&bets[2]!=null, MessageCodeConstants.BET_CONTENT_NOT_MATCH);
			return bets[0].split(SymbolConstants.COMMA).length*bets[1].split(SymbolConstants.COMMA).length*bets[2].split(SymbolConstants.COMMA).length;
		default:
			return 0;
		}
	}

	@Override
	protected int validateDantuo(OrderDetailVO orderDetail) {
		String planContent = orderDetail.getPlanContent();
		String [] planContents = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.NUMBER_SIGN);
		// 胆拖包含胆码和拖码两部分
		Assert.isTrue(planContents.length==2, MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.NUMBER_SIGN);
		// 胆码加拖码个数[getMinSelect(),getNumRange().length]个
		Assert.isTrue((betNums.length >= getMinSelect() && betNums.length <= getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 选号范围
		Assert.isTrue(ArrayUtil.containsAll(getNumRange(), betNums), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 重复性
		Assert.isFalse(ArrayUtil.isRepeat(betNums), MessageCodeConstants.HIGH_BET_NUM_REPEAT, planContent);
		// 验证胆码加拖码的个数
		String [] danma = StringUtils.tokenizeToStringArray(planContents[0], SymbolConstants.COMMA);
		String [] tuoma = StringUtils.tokenizeToStringArray(planContents[1], SymbolConstants.COMMA);
		// 胆码[1,getMinSelect())个
		Assert.isTrue((danma.length >= 1 && danma.length < getMinSelect()), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		// 拖码[1,13)个
		Assert.isTrue((tuoma.length >= 1 && tuoma.length < getNumRange().length), MessageCodeConstants.BET_CONTENT_NOT_MATCH, planContent);
		return (int)NumberUtil.getCombinationCount(tuoma.length, getMinSelect() - danma.length);
	}
	
	public static void main(String[] args) {
		String planContent = "01,02,03";
		String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.COMMA+SymbolConstants.VERTICAL_BAR);
		System.out.println(Arrays.toString(betNums));
	}
}
