package com.hhly.lottocore.remote.sportorder.service.impl.validate.template;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.SZCOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.stereotype.Service;

/**
 * 数字彩验证模板
 * @author longguoyou
 * @date 2017年12月2日
 * @compay 益彩网络科技有限公司
 */
@Service
public class OrderValidateNumberTemplate extends CommonValidateTemplate {
	
	/** 数字彩订单明细校验*/
	@Resource(name="szcOrderValidate")
    private SZCOrderValidate szcOrderValidate;
	

	public ResultBO<?> validateOrderInfoBase(OrderInfoVO orderInfoVO, Map<String, Object> map) {
		return super.validateOrderInfoBase(orderInfoVO, map);
	}

	public ResultBO<?> validateOrderDetailInfoBase(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, Map<String, Object> map) {
		//1.验证必填字段(验空值和填充默认值)
		ResultBO<?> resultBO = super.validateOrderDetailInfoBase(orderInfoVO, orderDetailVO, map);
		if (resultBO.isError()) {
			return resultBO;
		}
		//2.每个方案内容格式是否正确,若竞技彩，验证过关方式正确性，包括子玩法的过关方式是否可售,每个方案是否包含限号内容,竞技彩提出每个方案的自定义赛事编码(存入BUY_SCREEN)
		resultBO = this.validateBetContent(orderInfoVO, orderDetailVO, (List<LimitNumberInfoBO>)map.get("limitCode"));
		if(resultBO.isError()){
			return resultBO;
		}
		//3.验证注数、倍数和金额关系(解析注数)
		resultBO = super.deal(orderInfoVO.getIsDltAdd(), orderInfoVO.getLotteryCode(), orderDetailVO, resultBO, (Boolean)map.get("flag"));
		if(resultBO.isError()){
			return resultBO;
		}
		return ResultBO.ok();
	}
	
	
	/**
     * 校验订单内容 betContent（分彩种，各子类去分别实现）
     *
     * @param orderInfoVO
     * @param orderDetailVO
     * @param list
     * @return
     */
    public  ResultBO<?>  validateBetContent(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, List<?> list){
        //另外搞一个类，专门校验内容的，就放在一个类里面就好。这个根据彩种做分支，调用那个类里面不同的方法
        return szcOrderValidate.handle(orderDetailVO, orderInfoVO, list);
    }
	
    /**大部分验证方法，都已经有，如需要封装其它验证 方法，加在下面*/
}
