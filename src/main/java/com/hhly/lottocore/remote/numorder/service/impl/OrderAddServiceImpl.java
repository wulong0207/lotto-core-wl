package com.hhly.lottocore.remote.numorder.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.cache.service.OrderInfoCacheService;
import com.hhly.lottocore.persistence.order.dao.OrderAddDaoMapper;
import com.hhly.lottocore.persistence.order.po.OrderAddPO;
import com.hhly.lottocore.remote.numorder.service.OrderAddService;
import com.hhly.lottocore.remote.numorder.service.impl.cancel.ChaseAddHandler;
import com.hhly.lottocore.remote.numorder.service.impl.cancel.ChaseCancelHandler;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseIssueStatus;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseStatus;
import com.hhly.skeleton.base.common.ChaseEnum.ChaseType;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.OrderNoUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderAddBO;
import com.hhly.skeleton.lotto.base.order.bo.UserChaseRefundBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;
import com.hhly.skeleton.lotto.base.order.vo.UserChaseDetailQueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @desc 追号服务
 * @author huangb
 * @date 2017年11月7日
 * @company 益彩网络
 * @version v1.0
 */
@Service("orderAddService")
public class OrderAddServiceImpl implements OrderAddService {

	private static Logger logger = LoggerFactory.getLogger(OrderAddServiceImpl.class);
	
	/**
	 * 追号计划数据接口
	 */
	@Autowired
	private OrderAddDaoMapper orderAddDaoMapper;
	/**
	 * 用户信息
	 */
	@Autowired
	private UserUtil userUtil;
	/**
	 * 追号/订单缓存服务
	 */
	@Resource(name="orderInfoCacheService")
	private OrderInfoCacheService orderInfoCacheService;
	/**
	 * 撤单处理
	 */
	@Autowired
	private ChaseCancelHandler chaseCancelHandler;
	/**
	 * 追号下单处理
	 */
	@Autowired
	private ChaseAddHandler chaseAddHandler;

	/**
	 * 数字彩校验生成器
	 */
	@Autowired
	private ValidateGenerator validateGen;
	

	
	@Override
	public ResultBO<?> addChase(OrderAddVO orderAdd) {
		logger.info("----------addChase satrt---------");
		// 1.追号计划入库前校验
		validateGen.getValidateInstance(orderAdd).handleProcess(orderAdd);
		// 2.追号计划入库
		// String orderAddCode = orderAddDaoMapper.getOrderAddCode();// 追号计划编号
		String orderAddCode = OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_JZ);
		OrderAddPO orderAddPO = new OrderAddPO(orderAddCode, orderAdd);
		orderAddDaoMapper.addOrderAdd(orderAddPO);
		// 3.追号内容入库(随机追号无投注内容)
		if (ChaseType.FIXED_NUMBER.getValue() == orderAdd.getAddType().shortValue()) {
			orderAddDaoMapper.addOrderAddContent(orderAddPO.getOrderAddContentList());
		}
		// 4.追号期数入库
		orderAddDaoMapper.addOrderAddIssue(orderAddPO.getOrderAddIssueList());
		// 如果包含当前期，生成当前期订单并入库（这点不处理；仅做追号入库；当期订单待追号计划支付成功后生成）20161220确认
		OrderAddBO data = new OrderAddBO(orderAddCode, orderAdd.getNoPayCount());
		
		// 20170608-附加动作->更新缓存未支付订单数
		orderInfoCacheService.updateOrderNoPayCount(orderAdd.getLotteryCode(), 1, orderAdd.getUserId());
		
		logger.info("----------addChase end---------");
		return ResultBO.ok(data);
	}
	

	@Override
	public ResultBO<?> addChaseWithOutVerify(OrderAddVO orderAdd) {
		logger.info("----------addChaseWithOutVerify 追号计划入库快捷接口 satrt---------");
		// 1.追号计划入库前校验
		validateGen.getValidateInstance(orderAdd).handleProcessWithoutVerify(orderAdd);
		// 2.追号计划入库
		// String orderAddCode = orderAddDaoMapper.getOrderAddCode();// 追号计划编号
		String orderAddCode = OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_JZ);
		OrderAddPO orderAddPO = new OrderAddPO(orderAddCode, orderAdd);
		orderAddDaoMapper.addOrderAdd(orderAddPO);
		// 3.追号内容入库(随机追号无投注内容)
		if (ChaseType.FIXED_NUMBER.getValue() == orderAdd.getAddType().shortValue()) {
			orderAddDaoMapper.addOrderAddContent(orderAddPO.getOrderAddContentList());
		}
		// 4.追号期数入库
		orderAddDaoMapper.addOrderAddIssue(orderAddPO.getOrderAddIssueList());
		// 如果包含当前期，生成当前期订单并入库（这点不处理；仅做追号入库；当期订单待追号计划支付成功后生成）20161220确认
		OrderAddBO data = new OrderAddBO(orderAddCode, orderAdd.getNoPayCount());
		
		// 20170608-附加动作->更新缓存未支付订单数
		orderInfoCacheService.updateOrderNoPayCount(orderAdd.getLotteryCode(), 1, orderAdd.getUserId());
		
		logger.info("----------addChaseWithOutVerify 追号计划入库快捷接口 end---------");
		return ResultBO.ok(data);
	}

	@Override
	public ResultBO<?> userChase(OrderAddVO orderAdd) {
		return chaseAddHandler.userChase(orderAdd);
	}
	
	
	@Override
	public ResultBO<?> userChaseWithOutVerify(OrderAddVO orderAdd) {
		return chaseAddHandler.userChaseWithOutVerify(orderAdd);
	}

	@Override
	public ResultBO<?> findChaseRefundAsUserCancel(UserChaseDetailQueryVO queryVO) {
		// 1.获取用户并验证
		UserInfoBO userInfo = userUtil.getUserByToken(queryVO.getToken());
		Assert.notNull(userInfo, MessageCodeConstants.TOKEN_LOSE_SERVICE);
		// 2.查询指定条件的撤单数据（追号计划和对应追号期信息）,判断是否符合撤单条件
		UserChaseDetailQueryVO tmp = new UserChaseDetailQueryVO(queryVO.getOrderAddCode(), userInfo.getId(), ChaseIssueStatus.CHASE_WAITING.getValue());
		
		// 3.退款总额（查询剩余“等待追号”的金额总和）
		UserChaseRefundBO userChaseRefund = orderAddDaoMapper.findChaseRefundAmount(tmp);

		return ResultBO.ok(userChaseRefund);
	}
	
	@Override
	public ResultBO<?> findChaseAsUserCancel(UserChaseDetailQueryVO queryVO) {
		// 1.获取用户并验证
		UserInfoBO userInfo = userUtil.getUserByToken(queryVO.getToken());
		Assert.notNull(userInfo, MessageCodeConstants.TOKEN_LOSE_SERVICE);
		// 2.查询指定条件的撤单数据（追号计划和对应追号期信息）,判断是否符合撤单条件
		UserChaseDetailQueryVO tmp = new UserChaseDetailQueryVO(queryVO.getOrderAddCode(), userInfo.getId(), ChaseIssueStatus.CHASE_WAITING.getValue());
		OrderAddBO target = orderAddDaoMapper.findChaseCancel(tmp);
		Assert.notNull(target, MessageCodeConstants.REPEAL_ISSUE_CAN_NOT_FIND);
		return ResultBO.ok(target);
	}


	@Override
	public ResultBO<?> updChaseStatusAsUserCancel(UserChaseDetailQueryVO queryVO) {
		// 1.获取用户并验证
		UserInfoBO userInfo = userUtil.getUserByToken(queryVO.getToken());
		Assert.notNull(userInfo, MessageCodeConstants.TOKEN_LOSE_SERVICE);
		// 2.查询指定条件的撤单数据（追号计划和对应追号期信息）,判断是否符合撤单条件
		UserChaseDetailQueryVO tmp = new UserChaseDetailQueryVO(queryVO.getOrderAddCode(), userInfo.getId(), ChaseIssueStatus.CHASE_WAITING.getValue());
		//int result = orderAddDaoMapper.findCountChaseCancel(tmp);
		//Assert.isTrue(result > Constants.NUM_0, MessageCodeConstants.REPEAL_ISSUE_CAN_NOT_FIND);
		//OrderAddBO target = orderAddDaoMapper.findChaseCancel(tmp);
		//Assert.notNull(target, MessageCodeConstants.REPEAL_ISSUE_CAN_NOT_FIND);
		
		// 2.1.活动相关的判断（有些活动禁止撤单）
		// verifyActivityAsUserCancel(target);

		// 3.退款总额（查询剩余“等待追号”的金额总和）
		UserChaseRefundBO userChaseRefund = orderAddDaoMapper.findChaseRefundAmount(tmp);
		Double totalRefundAmount = userChaseRefund.getRefundAmount();
		// 4.剩余追号期数明细状态修改为"用户撤单中8"(“等待追号5”-“用户撤单中8”)
		int result = orderAddDaoMapper.updChaseIssue(new UserChaseDetailQueryVO(queryVO.getOrderAddCode(), userInfo.getId(),
				ChaseIssueStatus.CHASE_WAITING.getValue(), ChaseIssueStatus.USER_CANCELLING.getValue()));
		if (result <= Constants.NUM_0) {
			logger.info("用户撤单,修改撤单信息=>编号：" + queryVO.getOrderAddCode() + "，无追号计划彩期更新，动作结束！");
			return ResultBO.ok();
		}
		// 5.修改追号计划状态为"用户撤单4"("追号中1"-"用户撤单4"),追号结束时间、已追期数同步更新
		result = orderAddDaoMapper.updChaseEnd(new UserChaseDetailQueryVO(queryVO.getOrderAddCode(), userInfo.getId(),
				ChaseStatus.CHASING.getValue(), ChaseStatus.USER_CANCEL.getValue()));
		if (result <= Constants.NUM_0) {
			logger.info("用户撤单,修改撤单信息=>编号：" + queryVO.getOrderAddCode() + "，无追号计划更新，动作结束！");
			return ResultBO.ok();
		}
		// 6.执行退款(保留，用户中心提供接口)
		// a>先判断是否有退款金额
		if (null == totalRefundAmount || NumberUtil.compareTo(totalRefundAmount, Constants.NUM_0) <= Constants.NUM_0) {
			logger.info("用户撤单,修改撤单信息=>编号：" + queryVO.getOrderAddCode() + "，无退款总额或总额不大于0，动作结束！");
			return ResultBO.ok();
		}
		
		return ResultBO.ok(userChaseRefund);
	}

	/**
	 * @desc 当用户撤单时的活动验证
	 * @author huangb
	 * @date 2017年8月17日
	 * @param target 撤单的目标追号计划
	 */
	/*private void verifyActivityAsUserCancel(OrderAddBO target) {
		if(null == target) {
			return;
		}
//		// 1. 1分钱活动的撤单验证，该活动的追号计划不允许撤单 
//		Assert.isTrue(!( !ObjectUtil.isBlank(target.getActivityId()) && (target.getActivityId().equals(yfgcActivityCode) ||target.getActivityId().equals(jxYfgcActivityCode)) ), 
//						MessageCodeConstants.YFGC_NOT_ALLOW_CANCEL);

		// ....后续活动
	}*/
	
	@Override
	public ResultBO<?> userCancel(UserChaseDetailQueryVO queryVO) {
		return chaseCancelHandler.cancelChase(queryVO);
	}
	
	@Override
	public int countOrderAdd(OrderAddQueryVO vo) {
		return orderAddDaoMapper.count(vo);
	}

	@Override
	public List<OrderAddBO> findOrderAdd(OrderAddQueryVO vo) {
		return orderAddDaoMapper.find(vo);
	}
}
