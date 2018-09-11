package com.hhly.lottocore.remote.numorder.service.impl.numvalidator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.constants.NUMConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @desc    七星彩订单详情验证
 * @author  Tony Wang
 * @date    2017年7月4日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Component
public class QxcOrderDetailValidate extends OrderDetailValidate {

	private static Logger logger = LoggerFactory.getLogger(QxcOrderDetailValidate.class);

	@Override
	protected ResultBO<?> verifyContentType(OrderDetailVO orderDetail) throws ResultJsonException {
		// 1.按内容类型，将投注内容分割并校验长度
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型(子玩法下面分内容类型)
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		Assert.notNull(lotChild, "40437", planContent);
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		Assert.notNull(type, "40401");
		switch (lotChild) {
		case QXC_PT:
			//(包含单式、复式)
			switch (type) {
			case SINGLE:
				Assert.isTrue(Pattern.matches(NUMConstants.QXC_REGEX_NORMAL_SINGLE, planContent), "40402", planContent);
				break;
			case MULTIPLE:
				Assert.isTrue(Pattern.matches(NUMConstants.QXC_REGEX_NORMAL_MULTIPLE, planContent), "40402", planContent);
				// 至少8个号码
				String [] allNums = StringUtils.tokenizeToStringArray(planContent, ",|");
				Assert.isTrue(allNums.length>7, "40402", planContent);
				// 验证各个位置上的号码不重复
				String [] betNums = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
				for(String postiont : betNums) {
					String[] betcodes = StringUtils.tokenizeToStringArray(postiont, SymbolConstants.COMMA);
					Assert.isFalse(ArrayUtil.isRepeat(betcodes), "40556", planContent);
				}
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
		logger.debug("QxcOrderDetailValidate genBetContent begin!");
		// 投注内容
		String planContent = orderDetail.getPlanContent();
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		// 投注内容分割长度
		String[] betCode = null;
		switch (lotChild) {
		case QXC_PT:
			// 直选(包含单式、复式、和值)
			switch (type) {
			case SINGLE:
			case MULTIPLE:
				betCode = StringUtils.tokenizeToStringArray(planContent, SymbolConstants.VERTICAL_BAR);
				return new BetContentVO(StringUtils.tokenizeToStringArray(betCode[0], SymbolConstants.COMMA),
						StringUtils.tokenizeToStringArray(betCode[1], SymbolConstants.COMMA), 
						StringUtils.tokenizeToStringArray(betCode[2], SymbolConstants.COMMA),
						StringUtils.tokenizeToStringArray(betCode[3], SymbolConstants.COMMA),
						StringUtils.tokenizeToStringArray(betCode[4], SymbolConstants.COMMA),
						StringUtils.tokenizeToStringArray(betCode[5], SymbolConstants.COMMA),
						StringUtils.tokenizeToStringArray(betCode[6], SymbolConstants.COMMA)
						);
			default:
				throw new ResultJsonException(ResultBO.err("40403"));
			}
		default:
			throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
		}
	}

	@Override
	protected int genBetNum(OrderDetailVO orderDetail, BetContentVO betContent) throws ResultJsonException {
		logger.debug("QxcOrderDetailValidate genBetNum begin!");
		// 子玩法类型
		LotteryChild lotChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
		// 内容类型
		BetContentType type = BetContentType.getContentType(orderDetail.getContentType());
		switch (lotChild) {
		case QXC_PT:
			// 直选(包含单式、复式)
			switch (type) {
			case SINGLE:
				return 1;
			case MULTIPLE:
				int area1Len = betContent.getArea1().length;// 百位号码数
				int area2Len = betContent.getArea2().length;// 十位号码数
				int area3Len = betContent.getArea3().length;// 个位号码数
				int area4Len = betContent.getArea4().length;// 个位号码数
				int area5Len = betContent.getArea5().length;// 个位号码数
				int area6Len = betContent.getArea6().length;// 个位号码数
				int area7Len = betContent.getArea7().length;// 个位号码数
				// 注数 = 百区域选号数*十区域选号数*个区域选号数
				return area1Len * area2Len * area3Len * area4Len * area5Len * area6Len * area7Len;
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
		// 方法verifyContentType中，正则表达式已经验证过
		return ResultBO.ok();
	}

	@Override
	protected ResultBO<?> verifyLimitNum(OrderDetailVO orderDetail, BetContentVO betContent, List<?> list)
			throws ResultJsonException {
		// 无限号配置，不做限号处理
		if (CollectionUtils.isEmpty(list)) {
			logger.info("无限号配置，不做限号处理！！！！");
			return ResultBO.ok();
		}
		// 内容类型
		try {
			// 1.限号集合(同一彩种可以配多条限号；每条限号下根据玩法又可以配多条限号内容(同玩法都可以配多条内容))
			@SuppressWarnings("unchecked")
			List<LimitNumberInfoBO> limitList = (List<LimitNumberInfoBO>) list;
			for (LimitNumberInfoBO limit : limitList) {
				for (LimitNumberDetailBO limitDtl : limit.getLimitNumberList()) {
					if( Objects.equals(orderDetail.getLotteryChildCode() , limitDtl.getLotteryChildCode()) ){
						if (ObjectUtil.isBlank(limitDtl.getLimitContent())) {
							logger.info("限号内容为空，不做限号处理！！！！");
							continue;
						}
						/**20170619确认：：限号配置中只会按每种玩法的单式内容形式进行配置，内容分割规则对应单式玩法内容规则(但是这里不硬性要求符号对应，支持下面三种符号分割，只要分割后的数字个数与单式玩法的数字个数一致即可)
						 * 如果是不符合的限号配置会直接忽略
						 */
						// 1.2.配置的限号数字集合,且每种玩法的单式內容都是3个号码，所以这里统一验证一下
						String[] limitContent = StringUtils.tokenizeToStringArray(limitDtl.getLimitContent(), ",#|");
						if (ObjectUtil.isBlank(limitContent) || limitContent.length != 7) {
							logger.info("配置的限号内容：" + limitDtl.getLimitContent() + ",不符合规则，只能配置单式内容类型，忽略该配置项！！！！");
							continue;
						}
						// 七星暂时只有普通玩法的单复式
						boolean same = true;
						String[] betNums = StringUtils.tokenizeToStringArray(orderDetail.getPlanContent(), SymbolConstants.VERTICAL_BAR);
						for(int i = 0 ; i < betNums.length ; i++) {
							String[] positions = StringUtils.tokenizeToStringArray(betNums[i], SymbolConstants.COMMA);
							if(!ArrayUtil.contains(positions, limitContent[i])) {
								same = false;
								break;
							}
						}
						// 断言投注号码跟限号不同
						Assert.isFalse(same, "40698", orderDetail.getPlanContent(), limitDtl.getLimitContent());
					}
					
				}
			}
		} catch (ResultJsonException e) {
			throw e;
		} catch (Exception e) {
			logger.error("限号配置错误或不符合规则！", e);
			throw new ResultJsonException(ResultBO.err("40699"));
		}
		return ResultBO.ok();
	}
}
