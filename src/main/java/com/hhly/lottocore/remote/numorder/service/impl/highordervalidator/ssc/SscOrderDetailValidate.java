package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc;

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

public abstract class SscOrderDetailValidate extends HighOrderDetailValidate {

	// 一星
	@Autowired
	private ChildValidator ssc1Validator;
	
	// 二星直选
	@Autowired
	private ChildValidator ssc2Validator;
	
	// 二星组选
	@Autowired
	private ChildValidator ssc2zValidator;
	
	// 三星直选
	@Autowired
	private ChildValidator ssc3Validator;
	
	// 三星组三
	@Autowired
	private ChildValidator ssc3z3Validator;
	
	// 三星组六
	@Autowired
	private ChildValidator ssc3z6Validator;
	
	// 五星直选和通选，两都的投注验证方式一样，只是开奖方式不同
	@Autowired
	private ChildValidator ssc5Validator;
	
	// 大小单双
	@Autowired
	private ChildValidator sscDxdsValidator;
	
	@Autowired
	private LimitValidator commonLimitValidator;
	
	@Autowired
	private LimitValidator positionLimitValidator;
	
	@Autowired
	private LimitValidator positionSumLimitValidator;
	
	@Autowired
	private LimitValidator singleMultiDantuoSumLimitValidator;
	
	@Autowired
	private LimitValidator z3LimitValidator;
	
	@Autowired
	private LimitValidator positionSingleLimitValidator;
	
	//private static Logger logger = LoggerFactory.getLogger(SscOrderDetailValidate.class);

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
		case CQSSC_2Z:
			limitValidator = singleMultiDantuoSumLimitValidator;
			break;
		// 二星直选和三星直选支持和值
		case CQSSC_2:
		case CQSSC_3:
			limitValidator = positionSumLimitValidator;
			break;
		case CQSSC_3Z3:
			limitValidator = z3LimitValidator;
			break;
		case CQSSC_3Z6:
			limitValidator = commonLimitValidator;
			break;
		case CQSSC_1:
		case CQSSC_5:
		case CQSSC_5T:
			limitValidator = positionLimitValidator;
			break;
		case CQSSC_DXDS:
			limitValidator = positionSingleLimitValidator;
			break;
		default:
			throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
	}
		limitValidator.validate(orderDetailVO, limits, limitTranslator);
	}
	
	/**
	 * @desc   验证投注内容
	 * 注意：不同的子玩法有不同的投注方式
	 * A.五星直选和通选
	 * a)单式：1|2|3|4|5
	 * b)复式：1,2,3,4|2|3,4|4|5,6,9
	 * B.直选N的格式，以三星直选为例
	 * a)单式：1|2|3
	 * b)复式：1,2|2,3,4|3
	 * c)和值单式：1
	 * d)和值复式：1,2,3,4
	 * C.组选N的格式，以三星组选为例
	 * a)单式：1,2,3
	 * b)复式：2,3,4,5,6
	 * c)胆拖：1,2#3,4,5,6
	 * d)和值单式：1
	 * e)和值复式：2,3,4,18
	 * D.一星
	 * a)单式：1
	 * b)复式：0,1,2,3
	 * E.大小单双
	 * a)单式：1|1
	 * b)复式：1,1|1,3
	 * F.开奖结果格式：1|2|3|4|5
	 * 大小单双说明：
	 * 大2 小1 单3 双4
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
			case CQSSC_1:
				childValidator = ssc1Validator;
				break;
			case CQSSC_2:
				childValidator = ssc2Validator;
				break;
			case CQSSC_2Z:
				childValidator = ssc2zValidator;
				break;
			case CQSSC_3:
				childValidator = ssc3Validator;
				break;
			case CQSSC_3Z3:
				childValidator = ssc3z3Validator;
				break;
			case CQSSC_3Z6:
				childValidator = ssc3z6Validator;
				break;
			case CQSSC_5:
			case CQSSC_5T:
				childValidator = ssc5Validator;
				break;
			case CQSSC_DXDS:
				childValidator = sscDxdsValidator;
				break;
			default:
				throw new ResultJsonException(ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE));
		}
		return childValidator.validate(orderDetailVO);
	}
}
