package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.Map;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.ParentSportOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * 普通竞技彩入库前验证
 * @author yuanshangbing
 * @version 1.0
 * @desc
 * @date 2017/11/29 11:39
 * @company 益彩网络科技公司
 */
@Service("sportOrderService")
public class SportOrderService extends ParentSportOrderValidate {

    public ResultBO<?> validateOrder(OrderInfoVO orderInfoVO, Map<String, Object> map)throws Exception{
        ResultBO<?> result = super.orderValidate(orderInfoVO,orderInfoVO.getOrderDetailList(),map);
        if(result.isError()){return result;}
        return ResultBO.ok();
    }
}
