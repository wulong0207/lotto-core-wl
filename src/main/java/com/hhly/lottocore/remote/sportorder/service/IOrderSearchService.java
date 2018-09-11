package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.bo.OrderChannelBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.*;

import com.hhly.skeleton.task.order.vo.OrderChannelVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;


/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 订单查询业务
 * @date 2017/3/15 16:39
 * @company 益彩网络科技公司
 */
public interface IOrderSearchService {

    ///////////PC端接口////////////////
    /**
     * 分页查询投注列表
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderListInfo(OrderQueryVo orderQueryVo)throws Exception;

	/**
	 * 订单状态处理方法，原先是订单列表内部方法，现在暴露出来，给合买个人主页里面的战绩列表使用
	 * @param orderListInfoBO
	 */
	void buildAllOrderInfo(OrderBaseInfoBO orderListInfoBO);

	/**
	 * 分页查询追号投注列表
	 * @return
	 * @throws Exception
	 */
	ResultBO<?> queryAddOrderListInfo(OrderQueryVo orderQueryVo)throws Exception;

	/**
	 *
	 * @param orderCode
	 * @param token
	 * @param source 1 Pc端； 2 移动端，默认PC端
	 * @param userId 积分兑换接口专用，其他地方调用传token即可，这个就不要传了
	 * @param orderGroupContentId 合买跟单表主键ID，只有合买跟单的单才有，其他没有，前端传过来
	 * @return
	 * @throws Exception
	 */
    ResultBO<?> queryOrderDetailInfo(String orderCode,String token,Integer source,Integer userId,Integer orderGroupContentId) throws Exception;

    /**
     * 订单统计信息
     * @param orderStatisticsQueryVo
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderStatisInfo(OrderStatisticsQueryVo orderStatisticsQueryVo) throws Exception;

    /**
     * 首页订单，不包括追号订单（最多显示八条）
     */
    ResultBO<?> queryHomeOrderList(OrderQueryVo orderQueryVo)throws Exception;

    /**
     * 查询订单，包括追号订单
     * @param orderCode 可能是订单编号，可能是追号编号
     * @param token
     * @return
     */
    ResultBO<?> queryOrderInfo(String orderCode,String token) throws Exception;


    /**
     * 查询出票失败原因
     * @param orderSingleQueryVo
     * @return
     * @throws Exception
     */
    ResultBO<?> queryLotteryFailReason(OrderSingleQueryVo orderSingleQueryVo)throws Exception;

    /**
     * 查询中奖追停原因
     * @param orderSingleQueryVo
     * @return
     */
    ResultBO<?> queryAddOrderStopReason(@RequestBody OrderSingleQueryVo orderSingleQueryVo) throws Exception;


	/**
	 * @desc 前端接口：用户中心-查询用户数字彩方案详情
	 * @author huangb
	 * @date 2017年4月11日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询用户数字彩方案详情
	 * @throws ResultJsonException
	 */
	ResultBO<?> queryUserNumOrderDetail(UserNumOrderDetailQueryVO queryVO) throws ResultJsonException;
	
    /**
     * 查询用户竞技彩奖金优化, 单场致胜方案详情
     * @param queryVO
     * @return
     * @throws ResultJsonException
     * @date 2017年7月21日下午4:20:46
     * @author cheng.chen
     */
    ResultBO<?> queryUserSportOrderDetail(UserSportOrderDetailQueryVO queryVO) throws ResultJsonException; 

	/**
	 * @desc 前端接口：用户中心-查询用户追号内容详情
	 * @author huangb
	 * @date 2017年4月11日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询用户追号内容详情
	 * @throws ResultJsonException
	 */
	ResultBO<?> queryUserChaseContent(UserChaseDetailQueryVO queryVO) throws ResultJsonException;

	/**
	 * @desc 前端接口：用户中心-查询用户追号明细详情
	 * @author huangb
	 * @date 2017年4月11日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询用户追号明细详情
	 * @throws ResultJsonException
	 */
	ResultBO<?> queryUserChaseDetail(UserChaseDetailQueryVO queryVO) throws ResultJsonException;
	
	/**
	 * @desc 前端接口：用户中心-查询追号计划中奖金额（税前或税后）的组成明细，追号彩期关联追号订单获取
	 * @author huangb
	 * @date 2017年4月13日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询追号计划中奖金额（税前或税后）的组成明细，追号彩期关联追号订单获取
	 */
	ResultBO<?> queryUserChaseWinningDetail(UserChaseDetailQueryVO queryVO)throws ResultJsonException;

    /**
     * 查询订单流程信息
     * @param orderCode 订单编号或者追号编号
     * @return
     */
    ResultBO<?> queryOrderFlowInfoList(String orderCode,String token) throws Exception;


    /**
	 * 查询未支付订单详情列表
	 * @author longguoyou
	 * @date 2017年4月28日
	 * @param orderQueryVO
	 * @return
	 */
	ResultBO<?> queryNoPayOrderDetailList(OrderQueryVo orderQueryVO);

    /**
     * 根据订单编号集合查询订单信息（orderCode，buyType）
     * @return
     */
    ResultBO<?> queryOrderListForOrderCodes(List<OrderQueryVo> orderQueryVoList,String token);

	/**
	 * 查询活动参与数量
	 * @param activityOrderQueryInfoVO
	 * @return
	 */
	ResultBO<?> queryJoinActivityOrderCount(ActivityOrderQueryInfoVO activityOrderQueryInfoVO) throws Exception;

	/**
	 * 查询当前用户没有支付的活动订单
	 * @param activityOrderQueryInfoVO
	 * @return
	 */
	ResultBO<?> queryNoPayActivityOrderNo(ActivityOrderQueryInfoVO activityOrderQueryInfoVO) throws Exception;

	/**
	 * 用户中奖轮播信息
	 * @return
	 * @throws Exception
	 */
	ResultBO<?> queryUserWinInfo() throws Exception;
	
	/**
	 * 
	 * @desc 年会活动订单详情
	 * @create 2018年1月9日
	 * @param orderQueryVo
	 * @return ResultBO<?>
	 */
	ResultBO<?> queryYearOrderInfoDetail(OrderQueryVo orderQueryVo);

	/**
	 * @author zhouyang
	 * @desc 查询渠道订单列表
	 * @create 2018-6-7
	 * @param vo
	 * @return
	 */
	List<OrderChannelBO> queryChannelOrderList(OrderChannelVO vo);

}
