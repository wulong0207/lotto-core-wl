package com.hhly.lottocore.remote.sportorder.service.impl.validate.template;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hhly.skeleton.base.constants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.sportorder.service.impl.validate.JJCOrderValidate;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.SportsOrderValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 竞彩订单校验模板类，只提供服务，不涉及业务。后续要再加校验，再此类里面增加
 * @date 2017/11/29 10:29
 * @company 益彩网络科技公司
 */
@Component("orderValidateSportTemplate")
public  class OrderValidateSportTemplate extends CommonValidateTemplate{
	
	/** 竞技彩订单明细校验*/
	@Resource(name="jjcOrderValidate")
    private JJCOrderValidate jjcOrderValidate;

	@Autowired
	@Qualifier("sportsOrderValidate")
	private SportsOrderValidate sportOrderValidate;

    /////orderInfo层级的校验
    /**
     * 基础校验
     *
     * @param orderInfoVO
     * @param map
     * @return
     */
    public ResultBO<?> validateOrderInfoBase(OrderInfoVO orderInfoVO, Map<String, Object> map) {
		return super.validateOrderInfoBase(orderInfoVO, map);
    }

    //orderDetailInfo层级的校验

    /**
     * 基础校验
     *
     * @param orderInfoVO
     * @param map
     * @return
     */
    public ResultBO<?> validateOrderDetailInfoBase(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, Map<String, Object> map) {
    	
    	//1.方案详情基础验证
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
    	return jjcOrderValidate.handle(orderDetailVO, orderInfoVO, list);
    }
    
    
    /**大部分验证方法，都已经有，如需要封装其它验证 方法，加在下面*/
 
    /**
     * 彩种开关(子玩法,订单和详情彩种的关联验证)
     *
     * @param orderInfoVO
     * @param orderDetailVO
     * @param lotteryBO
     * @return
     */
    public ResultBO<?> validateLotterChildSwitch(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, LotteryBO lotteryBO) {
    	return null;
    }
    
	

    /**
     * 详情倍数,注数,金额验证(值范围)
     *
     * @param orderInfoVO
     * @param orderDetailVO
     * @param map
     * @return
     */
    public ResultBO<?> validateVarifyBoundary(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, Map<String, Object> map) {
		return null;
    }


    /**
     * 验证详情里面注数，倍数，金额的关系
     * @param addType
     * @param lotteryCode
     * @param orderDetailVO
     * @param resultBO
     * @param flag
     * @return
     */
    public ResultBO<?> validateBetAmount(Short addType, Integer lotteryCode, OrderDetailVO orderDetailVO, ResultBO<?> resultBO, boolean flag){
        return null;
    }
    
    /**
	 * 竞技彩：每个方案倍数、注数、金额校验(同投注内容对比.大乐透追号价钱为3元), 限号
	 * @author longguoyou
	 * @date 2017年3月3日 上午10:46:34
	 * @param orderDetailVO
	 * @param lotteryCode
	 * @param list 限号列表
	 * @return
	 */
    public ResultBO<?> validateBetContent(String[] betContent, OrderDetailVO orderDetailVO, int lotteryCode, List<?> list,Short isSingleOrder){
		//1.限号
		ResultBO<?> result = sportOrderValidate.limitlotteryCode(orderDetailVO,list,betContent,lotteryCode,isSingleOrder);
		if(result.isError()){
			return result;
		}
		//2.解析返回投注注数
		return sportOrderValidate.verifyBetNumBoundary(lotteryCode, orderDetailVO, betContent,isSingleOrder);
	}
    
    
    /**
     * 验证赛事编号合法性
     */
    public ResultBO<?> validateSystemCode(OrderInfoVO orderInfoVO){
    	if(JCZQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfoVO.getLotteryCode()) || 
    			BJDCConstants.checkLotteryId(orderInfoVO.getLotteryChildCode()) || JCConstants.gyjCheckLotteryId(orderInfoVO.getLotteryCode())){
    		ResultBO<?> resultBO = verifySystemCodes(orderInfoVO);
    		if(resultBO.isError()){
    			return resultBO;
    		}
    	}
    	return ResultBO.ok();
    }
    
    
    /**
	 * 验证赛事编号合法性
	 * @author longguoyou
	 * @date 2017年7月7日
	 * @param orderInfoVO
	 * @return
	 */
	private ResultBO<?> verifySystemCodes(OrderInfoVO orderInfoVO){
		String[] paramSystemCodes = orderInfoVO.getBuyScreen().split(SymbolConstants.COMMA);
		//解析的赛事编号与传参个数一致
		if(paramSystemCodes.length != orderInfoVO.getMatchSet().size()){
			return ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE);
		}
		if(!ObjectUtil.isBlank(paramSystemCodes)){
			for(String betSystemCodes : paramSystemCodes){
				//解析的赛事编号不包含传递的赛事编号
				if(!orderInfoVO.getMatchSet().contains(betSystemCodes)){
					return ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE);
				}
			}
		}else{
			return ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}
}