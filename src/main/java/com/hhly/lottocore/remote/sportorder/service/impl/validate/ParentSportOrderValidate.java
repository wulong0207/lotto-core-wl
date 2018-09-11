package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.template.OrderValidateSportTemplate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 竞彩校验总入口，有不同业务的可以在子类覆盖此方法
 * @date 2017/11/29 11:24
 * @company 益彩网络科技公司
 */
@Service
public class ParentSportOrderValidate {
	
	private static Logger logger = LoggerFactory.getLogger(ParentSportOrderValidate.class);

    @Autowired
    private OrderValidateSportTemplate orderValidateSportTemplate;

    /**
     * 订单校验总入口，调用AbstractOrderValidateSportTemplate组装校验（如竟足，竟篮，北单，老足彩可以直接调用）。
     * 有特殊需求的校验，可以在子类覆盖自方法
     * @param orderInfoVO
     * @param orderDetailVOList
     * @param map
     * @return
     */
    public ResultBO<?> orderValidate(OrderInfoVO orderInfoVO, List<OrderDetailVO> orderDetailVOList, Map<String, Object> map)throws Exception{
    	logger.info("订单验证开始");
        //1.校验订单基本信息
		ResultBO<?> resultBO = orderValidateSportTemplate.validateOrderInfoBase(orderInfoVO, map);
    	if(resultBO.isError()){return resultBO;}
    	Double total = 0d;
		int betNum = 0;
        //2.校验订单详情
        for(OrderDetailVO orderDetailVO : orderDetailVOList){
        	resultBO = orderValidateSportTemplate.validateOrderDetailInfoBase(orderInfoVO, orderDetailVO, map);
        	if(resultBO.isError()){return resultBO;}
        	total = MathUtil.add(total,orderDetailVO.getAmount());//计算用些方法
		    betNum += orderDetailVO.getBuyNumber();
        }
		//3.验证赛事编号
		resultBO = orderValidateSportTemplate.validateSystemCode(orderInfoVO);
		if(resultBO.isError()){return resultBO;}
        //4.订单金额和订单详情总金额验证是否一致
        resultBO = orderValidateSportTemplate.validateOrderInfoAmountAndDetailInfoAmount(orderInfoVO, total, betNum);
        if(resultBO.isError()){return resultBO;}
        //5. 验证支付截止时间
        resultBO = orderValidateSportTemplate.validateEndSysTime(orderInfoVO, map);
		if(resultBO.isError()){return resultBO;}
	    logger.info("订单验证结束");
        return ResultBO.ok();
    }
}
