package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.template.OrderValidateNumberTemplate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

public abstract class AbstractNumberOrderValidate {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractNumberOrderValidate.class);
	
	@Autowired
    private OrderValidateNumberTemplate orderValidateNumberTemplate;

    /**
     * 订单校验总入口，调用AbstractOrderValidateSportTemplate组装校验（如竟足，竟篮，北单，老足彩可以直接调用）。
     * 有特殊需求的校验，可以在子类覆盖自方法
     * @param orderInfoVO
     * @param orderDetailVOList
     * @param map
     * @return
     */
    public ResultBO<?> orderValidate(OrderInfoVO orderInfoVO, List<OrderDetailVO> orderDetailVOList, Map<String, Object> map)throws Exception{
    	logger.info("数字彩：订单验证开始");
    	ResultBO<?> resultBO = null;
        //1.校验订单基本信息
    	resultBO = orderValidateNumberTemplate.validateOrderInfoBase(orderInfoVO, map);
    	if(resultBO.isError()){return resultBO;}
    	Double total = 0d;
		int betNum = 0;
        //3.校验订单详情
        for(OrderDetailVO orderDetailVO : orderDetailVOList){
        	resultBO = orderValidateNumberTemplate.validateOrderDetailInfoBase(orderInfoVO, orderDetailVO, map);
        	if(resultBO.isError()){return resultBO;}
        	total = MathUtil.add(total,orderDetailVO.getAmount());//计算用些方法
		    betNum += orderDetailVO.getBuyNumber();
        }
        //4.订单金额和订单详情总金额验证是否一致
        resultBO = orderValidateNumberTemplate.validateOrderInfoAmountAndDetailInfoAmount(orderInfoVO, total, betNum);
        if(resultBO.isError()){return resultBO;}
        //5. 验证支付截止时间
        resultBO = orderValidateNumberTemplate.validateEndSysTime(orderInfoVO, map);
	    logger.info("数字彩：订单验证结束");
        return resultBO;
    }

}
