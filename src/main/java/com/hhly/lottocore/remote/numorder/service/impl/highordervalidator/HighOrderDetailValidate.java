package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hhly.lottocore.remote.numorder.service.Validator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module.LimitTranslator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * @desc    高频彩订单详情验证
 * @author  Tony Wang
 * @date    2017年3月23日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class HighOrderDetailValidate extends AbstractOrderDetailValidate implements Validator{

	private static Logger logger = LoggerFactory.getLogger(HighOrderDetailValidate.class);

	@Override
	public ResultBO<?> handle(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> limits) {
		try {
			logger.info("***********高频彩订单详情验证 begin!***********");
			validateLotteryChildCode(orderDetailVO);
			validateContentType(orderDetailVO);
			validateCodeWay(orderDetailVO);
			Map<String, Integer> dataMap = new HashMap<String, Integer>();
			dataMap.put(Constants.BET_NUM_KEY, validatePlanContent(orderDetailVO));
			validateLimit(orderDetailVO, limits, getLimitTranslator());
			logger.info("***********高频彩订单详情验证end!***********");
			return ResultBO.ok(dataMap);
		} catch (ResultJsonException e) {
			logger.error("***********高频彩订单详情验证异常!***********", e);
			return e.getResult();
		} catch (Exception e) {
			logger.error("***********高频彩订单详情验证错误!***********", e);
			return ResultBO.err(MessageCodeConstants.VALIDATE_ERROR_FIELD);
		}
		
	}
	
	/**
	 * @desc 追号内容的校验处理过程(提供给追号计划验证用)
	 * @author huangb
	 * @date 2017年12月15日
	 * @param orderDetailVO
	 *            订单明细/追号内容
	 * @param list
	 *            限号集合
	 * @return 追号内容的校验处理过程(提供给追号计划验证用)
	 */
	@Override
	public ResultBO<?> handleProcess(OrderDetailVO orderDetail, List<?> list) throws ResultJsonException {
		ResultBO<?> result = handle(orderDetail, null, list);
		if (result.isError()) {
			throw new ResultJsonException(result);
		}
		return result;
	}


	private static final LimitTranslator DEFAULT_LIMIT_TRANSLATOR = new LimitTranslator() {
		@Override
		public String translate(String originalLimitContent,Integer lotteryChildCode) {
			// 默认限号内容不转换
			return originalLimitContent;
		}
		@Override
		public boolean whetherTraslate(Integer lotteryChildCode) {
			// 默认限号内容不转换
			return false;
		}
	};
	
	public LimitTranslator getLimitTranslator() {
		return DEFAULT_LIMIT_TRANSLATOR;
	}

	/**
	 * @desc   验证是否为当前彩种的合法子玩法
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO 
	 */
	public abstract void validateLotteryChildCode(OrderDetailVO orderDetailVO);
	
	/**
	 * @desc   验证投注方式   1：单式；2：复式；3：胆拖 。高频彩没有4：混合；5：上传,以后可能遗漏、走势投注
	 * 			OrderValidateMethod的verifyOrderDetailRequired()只验证非空
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO 
	 */
	public final void validateContentType(OrderDetailVO orderDetailVO) {
		Integer type = orderDetailVO.getContentType();
		Assert.isTrue(OrderEnum.BetContentType.contain(type), MessageCodeConstants.BET_CONTENT_TYPE_FIELD);
	}
	
	/**
	 * @desc   验证选号方式 1：手选；2：机选；3：上传
	 * OrderValidateMethod的verifyOrderDetailRequired()只验证非空
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO 
	 */
	public final void validateCodeWay(OrderDetailVO orderDetailVO) {
		Assert.isTrue(OrderEnum.CodeWay.contain(orderDetailVO.getCodeWay()), MessageCodeConstants.BET_CODE_WAY_ILLEGAL);
	}
	
	/**
	 * @desc   验证投注内容
	 * 注意：不同的子玩法有不同的投注方式
	 * 1. 根据子玩法及投注方式，用正则表达式验证投注内容是否合法
	 * 如：十一选五格式(没有前导0)：
	 * A.任选N的格式，以任五为例
	 * a)单式：1,2,3,4,5
	 * b)复式：1,2,3,4,5,6,7
	 * c)胆拖：1,2,3#4,5,6,7,8,9
	 * B.组选N的格式，以组选三为例
	 * a) 单式：1,2,3
	 * b) 复式：1,2,3,4,5,6,7
	 * c) 胆拖：1,2#4,5,6,7,8,9
	 * C.直组N的格式，以直选三为例
	 * a)单式：1|2|3
	 * b)复式：1,2,3|2|1,2,3
	 * 
	 * 2.拆分投注内容，验证投注号码个数(正则已验证)、数值范围(正则已验证)、重复性
	 * 
	 * 3.返回重新计算的注数
	 * 
	 * @author Tony Wang
	 * @create 2017年3月24日
	 * @param orderDetailVO
	 * @return 
	 */
	public abstract int validatePlanContent(OrderDetailVO orderDetailVO);
	
	/**
	 * @desc   验证注数、倍数是否超过限制
	 * @author Tony Wang
	 * @create 2017年3月23日
	 * @param orderDetailVO
	 */
	/* 注意：若限号信息的前导0与投注选号的前导0不一致，很可能导致验证不通过
	 * 快3的投注方案有些玩法是有字母的，不能转成数字
	 * 11选5
	 * 任选N胆拖：01,02,03#04,05,06,07,08,09
	 * 快3
	 * 三同号通选单式：3T
	 * 三同号单选复式：111;222;333
	 * 
	 * 
	 */
	//public abstract void validateLimit(OrderDetailVO orderDetailVO, List<?> limits);
	public abstract void validateLimit(OrderDetailVO orderDetailVO, List<?> limits, LimitTranslator limitTranslator);
	
	// TODO 是否要验证 上传的投注内容地址
	//@NotNull
	//private String bettingContentUrl;
	/**
	 * TODO 是否要验证 选号模式（对应页面上的选号页签） 方便运营统计选号来源
	 */
	// private Integer betMode;
}
