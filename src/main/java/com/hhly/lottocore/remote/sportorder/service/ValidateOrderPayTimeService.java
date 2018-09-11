package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

import java.util.List;

/**
 * 
 * @ClassName: ValidateOrderPayTimeService 
 * @Description: 调用支付之前的，是否超过支付时间规则限定范围工具类
	<li>提前截止时间=官方截止销售时间-本站截止销售时间</li>
	<li> 提前截止时间＜120S => 本站截止销售时间</li>
	<li> 120S≤提前截止时间＜300S =>本站截止销售时间+30S</li>
	<li>300S≤提前截止时间＜600S =>本站截止销售时间+60S</li>
	<li>600S≤提前截止时间 =>本站截止销售时间+120S</li>
 * @author wuLong
 * @date 2017年3月25日 上午11:19:21 
 * 
 */
public interface ValidateOrderPayTimeService {
	/**
	 * 
	 * @Description: 判断订单是否已超过支付时间
	 * @param orderCode 方案订单编号
	 * @return boolean true/false true可以支付，false不能支付了
	 * @throws Exception
	 * @author wuLong
	 * @date 2017年3月25日 上午11:43:59
	 */
	public boolean checkPayEndTime(String orderCode)throws Exception;
	/**
	 * 
	 * @Description: 检查截止时间
	 * @param orderInfoVO 订单基础信息
	 * @param betMulBO 彩种对应注数判断截止时间 
	 * @param lotteryBO 彩种
	 * @param endTime 根据注数、倍数与时间关系，获得的提前的时间(单位：正数秒)
	 * @return ResultBO<?>
	 * @throws Exception
	 * @author wuLong
	 * @date 2017年3月28日 下午2:11:53
	 */
	public ResultBO<?> checkFbAndBbEndTime(OrderInfoVO orderInfoVO, List<LotBettingMulBO> betMulBO , LotteryBO lotteryBO, Integer endTime, SportAgainstInfoBO againstInfoBO) throws Exception;
	
}
