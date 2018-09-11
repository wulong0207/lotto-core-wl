/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.FootballGYJOrderValidate;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
import com.hhly.lottocore.remote.sportorder.service.ValidateService;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.OrderValidateMethod;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.LotteryPr;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * 验证服务实现
 * @author longguoyou
 * @date 2017年12月1日
 * @compay 益彩网络科技有限公司
 */
@Service("validateService")
public class ValidateServiceImpl extends OrderValidateMethod  implements ValidateService  {

	
	private static Logger logger = LoggerFactory.getLogger(ValidateServiceImpl.class);
	
	@Autowired
	public LotteryChildDaoMapper lotteryChildDaoMapper;
	
	/** 竞技彩订单明细校验*/
	@Autowired
    private SportOrderService sportOrderService;
	
	/** 竞技彩（单式上传）订单明细校验*/
	@Autowired
    private SingleUploadOrderService singleUploadOrderService;

	/** 数字彩订单明细校验*/
	@Autowired
	private NumberOrderService numberOrderService;


	@Override
	public ResultBO<?> validateOrder(OrderInfoVO orderInfoVO, Map<String,Object> map) throws Exception {
		logger.info("订单验证开始");
		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		LotteryPr lott = LotteryEnum.getLottery(Integer.valueOf(lotteryCode));
		switch (lott) {
		case BJDC:
		case JJC:
		case ZC:
		case GYJ:
			//根据categoryId 分发验证模板
			if(orderInfoVO.getIsSingleOrder() == Constants.NUM_1){
				return singleUploadOrderService.validateOrder(orderInfoVO, map);
			}
			return sportOrderService.validateOrder(orderInfoVO, map);
		case GPC:
		case SZC:
			return numberOrderService.validateOrder(orderInfoVO, map);
		default:
			return null;
		}
	}
}
