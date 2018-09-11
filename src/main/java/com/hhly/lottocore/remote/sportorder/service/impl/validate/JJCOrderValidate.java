package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.List;

import javax.annotation.Resource;

import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 竞技彩总分发验证控制类
 * @author longguoyou
 * @date 2017年12月2日
 * @compay 益彩网络科技有限公司
 */
@Service("jjcOrderValidate")
public class JJCOrderValidate extends SportsOrderValidate implements Validator{

	/** 竞彩足球订单明细校验*/
	@Resource(name="footballOrderValidate")
    private FootballOrderValidate footballOrderValidate ;
	
	/** 竞彩篮球订单明细校验*/
	@Resource(name="basketballOrderValidate")
	private BasketballOrderValidate basketballOrderValidate;
	
	/** 老足彩14场胜平负、任9胆拖、六场半全场、四场进球彩订单明细校验*/
	@Resource(name="footballOldOrderValidate")
	private FootballOldOrderValidate footballOldOrderValidate;
	
	/** 北京单场订单明细校验*/
	@Resource(name="footballPekingSingleOrderValidate")
	private FootballPekingSingleOrderValidate footballPekingSingleOrderValidate;

	/*冠亚军校验明细*/
	@Autowired
	private FootballGYJOrderValidate footballGYJOrderValidate;



	@Override
	public ResultBO<?> handle(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		Lottery lottery = Lottery.getLottery(Integer.valueOf(lotteryCode));
		switch (lottery) {
		case FB:
			return footballOrderValidate.handle(orderDetailVO, orderInfoVO, list);
		case BB:
			return basketballOrderValidate.handle(orderDetailVO, orderInfoVO, list);
		case BJDC:
		case SFGG:
			return footballPekingSingleOrderValidate.handle(orderDetailVO, orderInfoVO, list);
		case SFC:
		case ZC_NINE:
		case JQ4:
		case ZC6:
			return footballOldOrderValidate.handle(orderDetailVO, orderInfoVO, list);
		case CHP:
		case FNL:
			return footballGYJOrderValidate.handle(orderDetailVO, orderInfoVO, list);
		default:
			return null;
		}
	}
}
