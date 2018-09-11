package com.hhly.lottocore.remote.numorder.service.impl.numvalidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hhly.lottocore.remote.numorder.service.Validator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hhly.lottocore.remote.numorder.service.AbstractOrderDetailValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * @desc 订单明细验证基类(提供明细验证流程)
 * @author huangb
 * @date 2017年3月13日
 * @company 益彩网络
 * @version v1.0
 */
public abstract class OrderDetailValidate extends AbstractOrderDetailValidate implements Validator {

	private static Logger logger = LoggerFactory.getLogger(OrderDetailValidate.class);

	/**
	 * @desc 订单明细的校验处理过程
	 * @author huangb
	 * @date 2017年3月13日
	 * @param orderDetailVO
	 *            订单明细
	 * @param orderInfoVO
	 *            订单
	 * @param list
	 *            限号集合
	 * @return 订单明细的校验处理过程
	 */
	@Override
	public ResultBO<?> handle(OrderDetailVO orderDetail, OrderInfoVO orderInfo, List<?> list) {
		logger.info("***********低频彩订单详情验证 begin!***********");

		try {
			// 1.验证内容类型
			verifyContentType(orderDetail);
			// 2.拆分投注内容
			BetContentVO betContent = genBetContent(orderDetail);
			// 3.验证投注内容(包括号码正确性、号码重复性)
			verifyBetContent(orderDetail, betContent);
			// 4.验证限号
			verifyLimitNum(orderDetail, betContent, list);
			// 5.生成投注内容对应的注数
			int betNum = genBetNum(orderDetail, betContent);
			logger.info("betNum：" + betNum);
			// 6.返回注数和单注金额给主流程
			Map<String, Integer> dataMap = new HashMap<String, Integer>();
			dataMap.put(Constants.BET_NUM_KEY, betNum);

			logger.info("***********低频彩订单详情验证end!***********");
			return ResultBO.ok(dataMap);
		} catch (ResultJsonException ex) {
			logger.error("***********低频彩订单详情验证异常!***********", ex);
			return ex.getResult();
		} catch (Exception e) {
			logger.error("***********低频彩订单详情验证错误!***********", e);
			return ResultBO.err(MessageCodeConstants.VALIDATE_ERROR_FIELD);
		}
	}

	/**
	 * @desc 追号内容的校验处理过程
	 * @author huangb
	 * @date 2017年3月13日
	 * @param orderDetailVO
	 *            订单明细/追号内容
	 * @param list
	 *            限号集合
	 * @return 追号内容的校验处理过程
	 */
	@Override
	public ResultBO<?> handleProcess(OrderDetailVO orderDetail, List<?> list) throws ResultJsonException {
		ResultBO<?> result = handle(orderDetail, null, list);
		if (result.isError()) {
			throw new ResultJsonException(result);
		}
		return result;
	}

	/**
	 * @desc 获取订单详情-注数：提供给验证外使用 （袁尚兵需要用到）
	 * @author huangb
	 * @date 2017年11月1日
	 * @param orderDetail
	 *            订单明细对象
	 * @param betContent
	 *            投注内容
	 * @return 获取订单详情-注数：提供给验证外使用 （袁尚兵需要用到）
	 */
	public int getBetNum(OrderDetailVO orderDetail, BetContentVO betContent) {
		return genBetNum(orderDetail, betContent);
	}
	
	/**
	 * @desc 验证内容类型正确性
	 * @author huangb
	 * @date 2017年3月13日
	 * @param orderDetail
	 *            订单明细对象
	 * @return 验证内容类型正确性
	 */
	protected abstract ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException;

	/**
	 * @desc 生成投注内容，用于将投注选号的字符串按不同规则拆分成选号数组
	 * @author huangb
	 * @date 2017年3月13日
	 * @param orderDetail
	 *            订单明细对象
	 * @return 生成投注内容，用于将投注选号的字符串按不同规则拆分成选号数组
	 */
	protected abstract BetContentVO genBetContent(OrderDetailVO orderDetail) throws ResultJsonException;

	/**
	 * @desc 生成投注注数
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细对象
	 * @param betContent
	 *            投注内容
	 * @return 生成投注注数
	 * @throws ResultJsonException
	 */
	protected abstract int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException;

	/**
	 * @desc 验证投注内容(包括号码正确性、号码重复性等)
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @return 验证投注内容(包括号码正确性、号码重复性等)
	 * @throws ResultJsonException
	 */
	protected abstract ResultBO<?> verifyBetContent(OrderDetailVO orderDetail, BetContentVO betContent)
			throws ResultJsonException;

	/**
	 * @desc 验证限号
	 * @author huangb
	 * @date 2017年3月14日
	 * @param orderDetail
	 *            订单明细
	 * @param betContent
	 *            投注内容
	 * @param list
	 *            限号集合
	 * @return 验证限号
	 * @throws ResultJsonException
	 */
	protected ResultBO<?> verifyLimitNum(OrderDetailVO orderDetail, BetContentVO betContent, List<?> list)
			throws ResultJsonException {
		return ResultBO.ok();
	}

	/**
	 * @desc 是否为有效长度范围
	 * @author huangb
	 * @date 2017年3月14日
	 * @param targetLen
	 *            目标长度
	 * @param minLen
	 *            最小长度
	 * @param maxLen
	 *            最大长度
	 * @return 是否为有效长度范围
	 */
	protected boolean validLenRange(int targetLen, int minLen, int maxLen) {
		if (targetLen < minLen || targetLen > maxLen) {
			return false;
		}
		return true;
	}

	/**
	 * @desc 是否为有效数字范围
	 * @author huangb
	 * @date 2017年3月14日
	 * @param targetNum
	 *            目标数字
	 * @param minNum
	 *            最小数字
	 * @param maxNum
	 *            最大数字
	 * @return 是否为有效数字范围
	 */
	protected boolean validNumRange(String targetNum, int minNum, int maxNum) {
		if (StringUtil.isBlank(targetNum) || !NumberUtil.isDigits(targetNum) || Integer.parseInt(targetNum) < minNum
				|| Integer.parseInt(targetNum) > maxNum) {
			return false;
		}
		return true;
	}
}
