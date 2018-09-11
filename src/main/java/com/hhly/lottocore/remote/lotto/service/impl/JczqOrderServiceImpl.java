package com.hhly.lottocore.remote.lotto.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.order.po.OrderDetailPO;
import com.hhly.lottocore.persistence.order.po.OrderInfoPO;
import com.hhly.lottocore.remote.lotto.service.IJczqOrderService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
/**
 * 
 * @author longguoyou

 * @date 2017年2月6日 上午9:44:15

 * @desc  竞彩足球订单服务实现
 *
 */
@SuppressWarnings("rawtypes")
@Service("jczqOrderService")
public class JczqOrderServiceImpl implements IJczqOrderService {
    
	/**
	 * 订单数据接口
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	
	@Override
	public ResultBO addOrder(OrderInfoVO orderInfo) throws Exception {
//		OrderPluginFootball validate = new OrderPluginFBJCZJQ();
		OrderInfoPO po = new OrderInfoPO();
		BeanUtils.copyProperties(orderInfo, po);
		po.setUserId(orderInfo.getUserId());
		int rs = orderInfoDaoMapper.addOrder(po);
		if(orderInfo.getOrderDetailList().size() > 0){//根据明细如入库
			//订单明细
			List<OrderDetailPO> orderDetails = new ArrayList<OrderDetailPO>();
			OrderDetailPO odPo = null;
			List<OrderDetailVO> listOrderDetailVO = orderInfo.getOrderDetailList();
			for(int i = 0 ; i < listOrderDetailVO.size() ; i++){
				odPo = new OrderDetailPO();
				BeanUtils.copyProperties(listOrderDetailVO.get(i), odPo);
				odPo.setBuyNumber(listOrderDetailVO.get(i).getBuyNumber());
				orderDetails.add(odPo);
			}
			orderInfoDaoMapper.addOrderDetail(orderDetails);
		}else{//如果明细为空，则根据投注内容
			if(orderInfo.getBetContent().lastIndexOf(SymbolConstants.SEMICOLON) > -1){//存在分号分割
				List<OrderDetailPO> orderDetails = new ArrayList<OrderDetailPO>();
				String content = orderInfo.getBetContent().substring(orderInfo.getBetContent().lastIndexOf(SymbolConstants.UP_CAP)+1);
				OrderDetailPO odPo = null;
				for(String numStr : content.split(SymbolConstants.SEMICOLON)){
					odPo = new OrderDetailPO();
					odPo.setBuyNumber(Integer.parseInt(numStr));
					orderDetails.add(odPo);
				}
				orderInfoDaoMapper.addOrderDetail(orderDetails);
			}
		}
		return ResultBO.ok(rs);
	}
	
}
