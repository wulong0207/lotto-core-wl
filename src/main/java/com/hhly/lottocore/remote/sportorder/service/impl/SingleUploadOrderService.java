package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hhly.skeleton.base.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.ParentSportOrderValidate;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.template.OrderValidateSportTemplate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * 单式上传，入库前验证
 * @author yuanshangbing
 * @version 1.0
 * @desc
 * @date 2017/11/29 11:41
 * @company 益彩网络科技公司
 */
@Service("singleUploadOrderService")
public class SingleUploadOrderService extends ParentSportOrderValidate {

    @Autowired
    private OrderValidateSportTemplate orderValidateSportTemplate;
    
    public ResultBO<?> validateOrder(OrderInfoVO orderInfoVO, Map<String, Object> map)throws Exception{
        return this.orderValidate(orderInfoVO, orderInfoVO.getOrderDetailList(), map);
    }

    /**
     * 单式上传订单校验方法重写
     * @param orderInfoVO
     * @param orderDetailVOList
     * @param map
     * @return
     */
    public ResultBO<?> orderValidate(OrderInfoVO orderInfoVO, List<OrderDetailVO> orderDetailVOList, Map<String, Object> map)throws Exception{
    	//1.校验订单基础信息
        ResultBO<?> resultBO = orderValidateSportTemplate.validateOrderInfoBase(orderInfoVO, map);
    	if(resultBO.isError()){return resultBO;}
        Double total = 0d;
        int betNum = 0;
        //2.校验订单明细
    	for(OrderDetailVO orderDetailVO : orderDetailVOList){
    		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
    		resultBO = orderValidateSportTemplate.validateBetContent(betContent, orderDetailVO, orderInfoVO.getLotteryCode(), (List<LimitNumberInfoBO>)map.get("limitCode"), orderInfoVO.getIsSingleOrder());
    		if(resultBO.isError()){return resultBO;}
    		resultBO = orderValidateSportTemplate.deal(orderInfoVO.getIsDltAdd(), orderInfoVO.getLotteryCode(), orderDetailVO, resultBO, (Boolean)map.get("flag"));
    		if(resultBO.isError()){return resultBO;}
            total = MathUtil.add(total,orderDetailVO.getAmount());//计算用些方法
            betNum += orderDetailVO.getBuyNumber();
    	}
        //3.订单金额和订单详情总金额验证是否一致
        resultBO = orderValidateSportTemplate.validateOrderInfoAmountAndDetailInfoAmount(orderInfoVO, total, betNum);
        if(resultBO.isError()){return resultBO;}
        //4. 验证支付截止时间
        resultBO = orderValidateSportTemplate.validateEndSysTime(orderInfoVO, map);
        if(resultBO.isError()){return resultBO;}
        return ResultBO.ok();
    }

}
