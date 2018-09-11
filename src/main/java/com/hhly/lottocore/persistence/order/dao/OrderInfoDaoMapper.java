package com.hhly.lottocore.persistence.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.order.po.OrderDetailPO;
import com.hhly.lottocore.persistence.order.po.OrderInfoPO;
import com.hhly.skeleton.activity.bo.OrderInfoDetailBo;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderChannelBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderDetailInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoDetailLimitBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoLimitBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderStatisBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderStatisticsInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.UserNumOrderDetailBO;
import com.hhly.skeleton.lotto.base.order.bo.UserSportOrderDetailBO;
import com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.WinBO;
import com.hhly.skeleton.lotto.base.order.vo.ActivityOrderQueryInfoVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.OrderStatisticsQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.UserNumOrderDetailQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.UserSportOrderDetailQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.WinVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdConditionVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdDetailVO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdInfoVO;
import com.hhly.skeleton.task.order.vo.OrderChannelVO;

/**
 * @author huangb
 *
 * @Date 2016年11月30日
 *
 * @Desc 订单处理数据接口
 */
public interface OrderInfoDaoMapper {
	
	/**************************** Used to LOTTO ******************************/
	
	/**
	 * @param orderInfo
	 *            订单信息
	 * @return
	 * @throws Exception
	 * @Desc 添加订单信息
	 */
	int addOrder(OrderInfoPO orderInfo);

	/**
	 * @param orderDetails
	 *            订单明细
	 * @return
	 * @throws Exception
	 * @Desc 添加订单明细
	 */
	int addOrderDetail(@Param("list")List<OrderDetailPO> orderDetails);

	int addSingleOrderDetail(OrderDetailPO orderDetail);

	/**
	 * @desc 前端接口：用户中心-查询用户方案明细列表(分页查询-一个方案对应多个明细)
	 * @author huangb
	 * @date 2017年4月11日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询用户方案明细列表(分页查询-一个方案对应多个明细)
	 */
	List<UserNumOrderDetailBO> findPagingUserOrderDetail(UserNumOrderDetailQueryVO queryVO);
	
	/**
	 * @desc 前端接口：用户中心-查询用户方案明细列表数量(一个方案对应的明细数量)
	 * @author huangb
	 * @date 2017年4月11日
	 * @param queryVO
	 *            查询对象
	 * @return 前端接口：用户中心-查询用户方案明细列表数量(一个方案对应的明细数量)
	 */
	int findCountUserOrderDetail(UserNumOrderDetailQueryVO queryVO);
	
	/**
	 * 前端接口: 用户中心- 查询用户竞技彩方案明细列表数据
	 * @param queryVO
	 * @return
	 * @date 2017年7月21日下午5:54:46
	 * @author cheng.chen
	 */
	int findCountUserSportOrderDetail(UserSportOrderDetailQueryVO queryVO);
	
	
	/**
	 * @desc 前端接口：用户中心-查询用户竞技彩方案明细列表(分页查询-一个方案对应多个明细)
	 * @param queryVO
	 * @return
	 * @date 2017年7月21日下午5:54:46
	 * @author cheng.chen
	 */
	List<UserSportOrderDetailBO> findPagingUserSportOrderDetail(UserSportOrderDetailQueryVO queryVO);
	
	/**
	 * @desc   查询中信息
	 * @author Tony Wang
	 * @create 2017年8月12日
	 * @param win
	 * @return 
	 */
	List<WinBO> findWinInfo(WinVO win);
	
	
    /**
     * 批量修改订单状态
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2017年3月14日 下午3:23:28
     * @param ids
     * @param orderStatus
     * @return
     */
	int updateOrderStatus(@Param("ids")List<Integer> ids,@Param("orderStatus")Short orderStatus,@Param("modifyBy")String modifyBy);
    /*******************************订单列表，详情相关接口*****************/
	/**
	 * 查询订单列表
	 * @param orderQueryVo
	 * @return
	 */
	List<OrderBaseInfoBO> queryOrderListInfo(OrderQueryVo orderQueryVo);




	/**
	 * 抄单列表
	 *
	 * @param orderQueryVo
	 * @return
	 */
	List<OrderBaseInfoBO> querySingleCopyOrderList(OrderQueryVo orderQueryVo);

	/**
	 * 查询追号活动订单列表
	 *
	 * @param orderQueryVo
	 * @return
	 */
	List<OrderBaseInfoBO> queryAddOrderList(OrderQueryVo orderQueryVo);


	/**
	 * 查询订单列表总数
	 * @param orderQueryVo
	 * @return
	 */
	Integer queryOrderListInfoCount(OrderQueryVo orderQueryVo);

	/**
	 * 抄单列表条数
	 *
	 * @param orderQueryVo
	 * @return
	 */
	Integer querySingleCopyOrderListCount(OrderQueryVo orderQueryVo);

	/**
	 * 查询追号活动订单总条数
	 *
	 * @param orderQueryVo
	 * @return
	 */
	Integer queryAddOrderListCount(OrderQueryVo orderQueryVo);


	/**
	 * 修改订单状态
	 * @param payStatus
	 * @param orderCode
	 * @param userId
	 */
	void updateSingleOrderStatus(@Param("payStatus")Integer payStatus,@Param("orderCode")String orderCode,@Param("userId")Integer userId);

	/**
	 * 查询订单详情
	 * @param orderDetailVO
	 * @return
	 */
	List<OrderDetailInfoBO> queryOrderDetailInfo(OrderDetailVO orderDetailVO);

	/**
	 * 查询单式上传订单详情
	 * @param orderDetailVO
	 * @return
	 */
	List<OrderDetailInfoBO> querySingleUploadDetailInfo(OrderDetailVO orderDetailVO);

	/**
	 * 查询订单统计信息
	 * @param orderStatisticsQueryVo
	 * @return
	 */
	OrderStatisticsInfoBO queryOrderStatisInfo(OrderStatisticsQueryVo orderStatisticsQueryVo);
	/**
	 * 
	 * @Description: 根据订单编号查询订单信息表
	 * @param orderCode 订单编号
	 * @return
	 * @author wuLong
	 * @date 2017年3月27日 下午4:22:07
	 */
	OrderInfoBO getOrderInfo(@Param("orderCode")String orderCode);

	/**
	 * 用户首页订单（最多显示八条）
	 * @param orderQueryVo
	 * @return
	 */
	List<OrderBaseInfoBO> queryHomeOrderList(OrderQueryVo orderQueryVo);

	/**
	 * 用户首页，统计最近七天投注次数和中奖次数
	 * @param orderQueryVo
	 * @return
	 */
	OrderStatisBO statisOrderBetAndWinCount(OrderQueryVo orderQueryVo);

	/**
	 * 用户首页，统计最近七天追号计划投注次数
	 * @param userId
	 * @return
	 */
	Integer statisAddOrderBetAndWinCount(Integer userId);


	/**
	 * 
	 * @Description: 通过订单状态和彩种id集合查询订单基本信息和订单详情信息
	 * @param orderStatus 订单状态 
	 * @param lotteryCodes 彩种id集合
	 * @param payStatus 支付状态
	 * @return List OrderInfoBO
	 * @author wuLong
	 * @date 2017年4月5日 下午2:42:16
	 */
	List<OrderInfoBO> getOrderInfoList(@Param("orderStatus") Integer orderStatus,@Param("payStatus") Integer payStatus,@Param("lotteryCodes")List<Integer> lotteryCodes);


	/**
	 * 根据订单号查询订单
	 * @param orderCode
	 * @param userId
	 * @return
	 */
	OrderBaseInfoBO queryOrderInfo(@Param("orderCode")String orderCode,@Param("userId")Integer userId);


	/**
	 * 抄单查询订单基本信息
	 *
	 * @param orderCode
	 * @param userId
	 * @return
	 */
	OrderBaseInfoBO queryOrderCopyInfo(@Param("orderCode") String orderCode, @Param("userId") Integer userId);

	/**
	 * 查询未支付的订单详情
	 * @param orderListCode 查询OrderCode列表
	 * @param userId 用户ID
	 * @param lotteryCode 彩种编号
	 * @param lotteryIssue 彩期编号
	 * @param flag 是否需要判断彩期参数 1：是
	 * @return
	 */
	List<OrderInfoLimitBO> queryNoPayOrderList(@Param("orderCodes")List<String> orderListCode,@Param("userId")Integer userId, @Param("lotteryCode")String lotteryCode,@Param("lotteryIssue")String lotteryIssue,@Param("flag")String flag);
	
	/**
	 * 查询未支付的订单总数
	 * @param orderListCode 查询OrderCode列表
	 * @param userId 用户ID
	 * @param lotteryCode 彩种编号
	 * @param lotteryIssue 彩期编号
	 * @return
	 */
	int queryNoPayOrderListCount(@Param("orderCodes")List<String> orderListCode,@Param("userId")Integer userId, @Param("lotteryCode")String lotteryCode, @Param("lotteryIssue")String lotteryIssue);

	/**
	 * 查询合买未支付订单总数
	 * @param userId 用户ID
	 * @param lotteryCode 彩种编号
	 * @return
	 */
	int queryNoPayOrderGroupListCount(@Param("userId")Integer userId, @Param("lotteryCode")String lotteryCode);

	/**
	 * 批量取消订单
	 * @param orderListCode
	 * @param userId
	 */
	void batchCancelOrderList(@Param("orderCodes")List<String> orderListCode,@Param("userId")Integer userId,@Param("payStatus")short payStatus);
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年4月28日
	 * @param listDetailOrderCode
	 * @return
	 */
	List<OrderInfoDetailLimitBO> queryListFromOrderDetail(@Param("orderCodes")List<String> listDetailOrderCode);
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年4月28日
	 * @param listAddedContentOrderCode
	 * @return
	 */
	List<OrderInfoDetailLimitBO> queryListFromOrderAddedContent(@Param("orderCodes")List<String> listAddedContentOrderCode);

	/**
	 * 根据订单编号集合查询订单
	 * @param orderCodeList
	 * @param addCodeList
	 * @param userId
	 * @return
	 */
	List<OrderBaseInfoBO> queryOrderListForOrderCodes(@Param("orderCodeList")List<String> orderCodeList,@Param("addCodeList")List<String> addCodeList,@Param("userId")Integer userId);
	
	/**
	 * 修改撤销理由到订单备注
	 * @param param
	 * @return
	 * @date 2017年6月11日上午11:06:11
	 * @author cheng.chen
	 */
	int updCancelOrderRemark(Map<String, String > param);

	/**
	 * 查询活动参与数量
	 * @param activityOrderQueryInfoVO
	 * @return
	 */
	Integer queryJoinActivityOrderCount(ActivityOrderQueryInfoVO activityOrderQueryInfoVO);

	/**
	 * @desc   查询符合条件的订单数
	 * @author Tony Wang
	 * @create 2017年8月15日
	 * @param vo
	 * @return
	 */
	int count(OrderInfoQueryVO vo);

	/**
	 * 查询当前用户未支付的活动订单编号
	 * @param activityOrderQueryInfoVO
	 * @return
	 */
	List<String> queryNoPayActivityOrderNo(ActivityOrderQueryInfoVO activityOrderQueryInfoVO);


	/**
	 * 抄单更新订单类别
	 *
	 * @param vo
	 * @return
	 */
	int updOrderType(com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO vo);

	/**
	 * 用户中奖轮播信息
	 * @return
	 */
	List<UserWinInfoBO> queryUserWinInfo(@Param("lotteryCodes") List<String> lotteryCodes);
	
	/**
	 * 年会活动订单详情
	 * @desc 
	 * @create 2018年1月9日
	 * @param orderQueryVo
	 * @return ResultBO<?>
	 */
	OrderInfoDetailBo queryYearOrderInfoDetail(OrderQueryVo orderQueryVo);


	List<OrderBaseInfoBO> queryOrderStandingList(OrderSingleQueryVo orderSingleQueryVo);

	Integer queryOrderStandingListCount(OrderSingleQueryVo orderSingleQueryVo);
	/**************************** Used to CMS ******************************/

	List<OrderChannelBO> queryChannelOrderList(OrderChannelVO vo);
	
	/***************************推单改版*****************************************/
	
	/**
	 * 
	 * @Description 查询分析师当天推荐次数
	 * @author HouXiangBao289
	 * @param rcmdInfoVO
	 * @return
	 */
	int getRcmdDayOrderCount(RcmdInfoVO rcmdInfoVO);
	
	/**
	 * 
	 * @Description 查找分析师是否有相同串关场次方案 
	 * @author HouXiangBao289
	 * @param rcmdConditionVO
	 * @return
	 */
	int findRcmdRepeatOrder(RcmdConditionVO rcmdConditionVO);
	
	/**
	 * @param rcmdInfo
	 *            推荐方案信息
	 * @return
	 * @throws Exception
	 * @Desc 添加推荐方案信息
	 */
	int addRcmdOrder(RcmdInfoVO rcmdInfo);
	
	/**
	 * 
	 * @Description 批量添加推荐方案
	 * @author HouXiangBao289
	 * @param list
	 * @return
	 */
	int addRcmdDetailList(@Param("list") List<RcmdDetailVO> orderlist);
}