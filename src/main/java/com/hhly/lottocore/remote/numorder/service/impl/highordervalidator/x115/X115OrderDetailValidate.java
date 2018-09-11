package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115;

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

public abstract class X115OrderDetailValidate extends HighOrderDetailValidate {

	@Autowired
	private ChildValidator x115R2Validator;
	
	@Autowired
	private ChildValidator x115R3Validator;
	
	@Autowired
	private ChildValidator x115R4Validator;
	
	@Autowired
	private ChildValidator x115R5Validator;
	
	@Autowired
	private ChildValidator x115R6Validator;
	
	@Autowired
	private ChildValidator x115R7Validator;
	
	@Autowired
	private ChildValidator x115R8Validator;
	
	@Autowired
	private ChildValidator x115L3Validator;
	
	@Autowired
	private ChildValidator x115L4Validator;
	
	@Autowired
	private ChildValidator x115L5Validator;
	
	@Autowired
	private ChildValidator direct1Validator;
	
	@Autowired
	private ChildValidator direct2Validator;
	
	@Autowired
	private ChildValidator direct3Validator;
	
	@Autowired
	private LimitValidator commonLimitValidator;
	
	@Autowired
	private LimitValidator positionLimitValidator;
	
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
			// 山东十一选五
			case SD11X5_R2:
			case SD11X5_Q2_GROUP:
			case SD11X5_R3:
			case SD11X5_Q3_GROUP:
			case SD11X5_R4:
			case SD11X5_R5:
			case SD11X5_R6:
			case SD11X5_R7:
			case SD11X5_R8:
			case SD11X5_L4:
			case SD11X5_L5:
				
			// 广西十一选五
			case GX11X5_R2:
			case GX11X5_Q2_GROUP:
			case GX11X5_R3:
			case GX11X5_Q3_GROUP:
			case GX11X5_R4:
			case GX11X5_R5:
			case GX11X5_R6:
			case GX11X5_R7:
			case GX11X5_R8:
			// 广东十一选五
			case D11X5_R2:
			case D11X5_Q2_GROUP:
			case D11X5_R3:
			case D11X5_Q3_GROUP:
			case D11X5_R4:
			case D11X5_R5:
			case D11X5_R6:
			case D11X5_R7:
			case D11X5_R8:
			// 江西十一选五
			case JX11X5_R2:
			case JX11X5_Q2_GROUP:
			case JX11X5_R3:
			case JX11X5_Q3_GROUP:
			case JX11X5_R4:
			case JX11X5_R5:
			case JX11X5_R6:
			case JX11X5_R7:
			case JX11X5_R8:
			// 新疆十一选五
			case XJ11X5_R2:
			case XJ11X5_Q2_GROUP:
			case XJ11X5_R3:
			case XJ11X5_Q3_GROUP:
			case XJ11X5_R4:
			case XJ11X5_R5:
			case XJ11X5_R6:
			case XJ11X5_R7:
			case XJ11X5_R8:
			case XJ11X5_L4:
			case XJ11X5_L5:	
				limitValidator = commonLimitValidator;
				break;
			// 前一
			case SD11X5_Q1:
			// 前二直选
			// 当选择 1|1,2时，是单式还是复式   2017-6-8 产品确认入库时是原样作为复式票入订单表，拆票时会拆成单式票
			case SD11X5_Q2_DIRECT:
			// 前三直选  万位、千位、百位
			// 当选择 1|1,2|1,2,3时，是单式还是复式   2017-6-8 产品确认入库时是原样作为复式票入订单表，拆票时会拆成单式票
			case SD11X5_Q3_DIRECT:
			case SD11X5_L3:
				
	
				
			case D11X5_Q1:
			case D11X5_Q2_DIRECT:
			case D11X5_Q3_DIRECT:
				
			case JX11X5_Q1:
			case JX11X5_Q2_DIRECT:
			case JX11X5_Q3_DIRECT:
				
			case XJ11X5_Q1:
			case XJ11X5_Q2_DIRECT:
			case XJ11X5_Q3_DIRECT:
			case XJ11X5_L3:		
				
			case GX11X5_Q1:
			case GX11X5_Q2_DIRECT:
			case GX11X5_Q3_DIRECT:
				limitValidator = positionLimitValidator;
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
			case SD11X5_R2:
			case SD11X5_Q2_GROUP:
			// 二	
			case D11X5_R2:
			case D11X5_Q2_GROUP:
			case JX11X5_R2:
			case JX11X5_Q2_GROUP:
			case XJ11X5_R2:
			case XJ11X5_Q2_GROUP:
			case GX11X5_R2:
			case GX11X5_Q2_GROUP:
				childValidator = x115R2Validator;
				break;
			//三	
			case SD11X5_R3:
			case SD11X5_Q3_GROUP:	
			case D11X5_R3:
			case D11X5_Q3_GROUP:
			case JX11X5_R3:
			case JX11X5_Q3_GROUP:
			case XJ11X5_R3:
			case XJ11X5_Q3_GROUP:
			case GX11X5_R3:
			case GX11X5_Q3_GROUP:
				childValidator = x115R3Validator;
				break;
			//四	
			case SD11X5_R4:
			case D11X5_R4:
			case JX11X5_R4:
			case XJ11X5_R4:
			case GX11X5_R4:
				childValidator = x115R4Validator;
				break;
			//五	
			case SD11X5_R5:
			case D11X5_R5:
			case JX11X5_R5:
			case XJ11X5_R5:
			case GX11X5_R5:
				childValidator = x115R5Validator;
				break;
			//六	
			case SD11X5_R6:
			case D11X5_R6:
			case JX11X5_R6:
			case XJ11X5_R6:
			case GX11X5_R6:
				childValidator = x115R6Validator;
				break;
				
			case SD11X5_R7:
			case D11X5_R7:
			case JX11X5_R7:
			case XJ11X5_R7:
			case GX11X5_R7:
				childValidator = x115R7Validator;
				break;
				
			case SD11X5_R8:
			case D11X5_R8:
			case JX11X5_R8:
			case XJ11X5_R8:
			case GX11X5_R8:
				childValidator = x115R8Validator;
				break;
				
			// 前一
			case SD11X5_Q1:
			case D11X5_Q1:
			case JX11X5_Q1:
			case XJ11X5_Q1:
			case GX11X5_Q1:
				childValidator = direct1Validator;
				break;
				
			// 前二直选
			// 当选择 1|1,2时，是单式还是复式   2017-6-8 产品确认入库时是原样作为复式票入订单表，拆票时会拆成单式票
			case SD11X5_Q2_DIRECT:
			case D11X5_Q2_DIRECT:
			case JX11X5_Q2_DIRECT:
			case XJ11X5_Q2_DIRECT:
			case GX11X5_Q2_DIRECT:
				childValidator = direct2Validator;
				break;
			// 前三直选  万位、千位、百位
			// 当选择 1|1,2|1,2,3时，是单式还是复式   2017-6-8 产品确认入库时是原样作为复式票入订单表，拆票时会拆成单式票
			case SD11X5_Q3_DIRECT:
			case D11X5_Q3_DIRECT:
			case JX11X5_Q3_DIRECT:
			case XJ11X5_Q3_DIRECT:
			case GX11X5_Q3_DIRECT:
				childValidator = direct3Validator;
				break;
			case SD11X5_L3:
			case XJ11X5_L3:
				childValidator = x115L3Validator;
				break;
			case SD11X5_L4:
			case XJ11X5_L4:
				childValidator = x115L4Validator;
				break;
			case SD11X5_L5:
			case XJ11X5_L5:
				childValidator = x115L5Validator;
				break;
				
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		return childValidator.validate(orderDetailVO);
	}
}
