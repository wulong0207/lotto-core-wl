package com.hhly.lottocore.remote.sportorder.service.impl;

import java.util.Map;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.AbstractNumberOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * 普通数字彩入库前验证
 * @author longguoyou
 * @date 2017年12月2日
 * @compay 益彩网络科技有限公司
 */
@Service("numberOrderService")
public class NumberOrderService extends AbstractNumberOrderValidate{

	public ResultBO<?> validateOrder(OrderInfoVO orderInfoVO,Map<String, Object> map)throws Exception{
		ResultBO<?> resultBO = super.orderValidate(orderInfoVO, orderInfoVO.getOrderDetailList(), map);
		if(resultBO.isError()){return resultBO;}
		return ResultBO.ok();
	}
}
