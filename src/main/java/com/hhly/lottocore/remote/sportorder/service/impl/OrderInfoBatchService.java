package com.hhly.lottocore.remote.sportorder.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.persistence.order.po.OrderDetailPO;
import com.hhly.skeleton.base.constants.Constants;
import org.springframework.stereotype.Service;


/**
 * @author wuLong
 * @create 2017/5/9 14:20
 */
@Service("orderInfoBatchService")
public class OrderInfoBatchService {
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;

    public void addOrderDetailInfoList(List<OrderDetailPO> orderDetailList) throws SQLException {
    	int lenyz = orderDetailList.size();
		int dxz = lenyz % Constants.NUM_FOUR_THOUSAND ;
		int toIndexz = (dxz == 0?Constants.NUM_FOUR_THOUSAND:dxz);
		addOrderDetailListRecursion(orderDetailList.subList(lenyz-toIndexz, lenyz), Constants.NUM_0, lenyz, orderDetailList);
    }
    
    private void addOrderDetailListRecursion(List<OrderDetailPO> orderDetailList, int i, int len, List<OrderDetailPO> list){
    	if(len == 0){
    		return ;
    	}
    	int l = len / Constants.NUM_FOUR_THOUSAND;
		int dx = len % Constants.NUM_FOUR_THOUSAND;
		int subSize = l>=1?Constants.NUM_FOUR_THOUSAND:dx;
		int toIndex = i+subSize;
		addOrderDetailListRecursion(list.subList(i, toIndex), i, len-subSize, list);
		orderInfoDaoMapper.addOrderDetail(orderDetailList);
    }


    
}
