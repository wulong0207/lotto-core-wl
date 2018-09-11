package com.hhly.lottocore.remote.sportorder.service;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.WinBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.WinVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdInfoVO;

/**
 * @author huangb
 *
 * @Date 2016年11月30日
 *
 * @Desc 订单服务
 */
public interface IOrderService {

	/**
	 * @param orderInfo
	 *            订单信息
	 * @return
	 * @throws Exception
	 * @Desc 添加订单信息
	 */
	ResultBO<?> addOrder(OrderInfoVO orderInfo) throws Exception;

	/**
	 * 更新订单状态
	 * 
	 * @param orderSingleQueryVo
	 * @return
	 */
	ResultBO<?> updateOrderStatus(OrderSingleQueryVo orderSingleQueryVo) throws Exception;
	
	/**
	 * 更新订单状态
	 * @param orderStatus 订单状态
	 * @param orderId 订单id
	 * @param modifyBy 修改人
	 * @author wuLong
	 * @date 2017-04-19
	 */
	ResultBO<?> updateOrderStatus(OrderEnum.OrderStatus orderStatus,Integer orderId,String modifyBy);

	/**
	 * 批量取消订单
	 * @param orderCodes
	 * @param token 
	 * @param lotteryCode 彩种编号
	 * @return
	 */
	ResultBO<?> batchCancelOrderList(List<String> orderCodes, String token, Integer lotteryCode);

	/**
	 * 单式上传 下订单
	 * @param orderInfoSingleUploadVO
	 * @return
	 */
	ResultBO<?> addSingleUploadOrder(OrderInfoSingleUploadVO orderInfoSingleUploadVO) throws Exception;

	/**
	 * @desc   查询中奖信息
	 * @author Tony Wang
	 * @create 2017年8月12日
	 * @param win
	 * @return 
	 */
	List<WinBO> findWinInfo(WinVO win);
	
	/**
	 * @desc   查询符合条件的订单数
	 * @author Tony Wang
	 * @create 2017年8月15日
	 * @param vo
	 * @return 
	 */
	int countOrderInfo(OrderInfoQueryVO vo);

	/**
	 * 获取订单信息
	 * @param orderCode
	 * @return
	 */
	OrderInfoBO getOrderInfo(String orderCode);
	
	/**
	 * @param orderInfo
	 *            分析师推荐方案信息
	 * @return
	 * @throws Exception
	 * @Desc 添加分析师推荐方案信息
	 */
	ResultBO<?> addRcmdOrder(RcmdInfoVO rcmdInfoVO) throws Exception;
	
}
