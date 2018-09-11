package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jsk3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jxk3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.Cqkl10OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.SdPokerOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.CqsscOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.D11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Gx11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Jx11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Sd11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Xj11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.DltOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.F3dOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.Pl3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.Pl5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.QlcOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.QxcOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.SsqOrderDetailValidate;
import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * 数字彩总分发验证控制类
 * @author longguoyou
 * @date 2017年12月2日
 * @compay 益彩网络科技有限公司
 */
@Service("szcOrderValidate")
public class SZCOrderValidate implements Validator{

	/** 双色球订单明细校验 */
	@Resource(name="ssqOrderDetailValidate")
	private SsqOrderDetailValidate ssqOrderDetailValidate;
	/** 大乐透订单明细校验 */
	@Resource(name="dltOrderDetailValidate")
	private DltOrderDetailValidate dltOrderDetailValidate;
	/** 福彩3D订单明细校验 */
	@Resource(name="f3dOrderDetailValidate")
	private F3dOrderDetailValidate f3dOrderDetailValidate;
	/** 排列三订单明细校验 */
	@Resource(name="pl3OrderDetailValidate")
	private Pl3OrderDetailValidate pl3OrderDetailValidate;
	/** 排列五订单明细校验 */
	@Resource(name="pl5OrderDetailValidate")
	private Pl5OrderDetailValidate pl5OrderDetailValidate;
	/** 七乐彩订单明细校验 */
	@Resource(name="qlcOrderDetailValidate")
	private QlcOrderDetailValidate qlcOrderDetailValidate;
	/** 七星彩订单明细校验 */
	@Resource(name="qxcOrderDetailValidate")
	private QxcOrderDetailValidate qxcOrderDetailValidate;
	
	/** 山东十一选五订单明细校验 */
	@Resource(name="sd11x5OrderDetailValidate")
	private Sd11x5OrderDetailValidate sd11x5OrderDetailValidate;
	/** 广东十一选五订单明细校验 */
	@Resource(name="d11x5OrderDetailValidate")
	private D11x5OrderDetailValidate d11x5OrderDetailValidate;
	
	/** 江西十一选五订单明细校验 */
	@Resource(name="jx11x5OrderDetailValidate")
	private Jx11x5OrderDetailValidate jx11x5OrderDetailValidate;
	
	/** 新疆十一选五订单明细校验 */
	@Resource(name="xj11x5OrderDetailValidate")
	private Xj11x5OrderDetailValidate xj11x5OrderDetailValidate;
	
	/** 广西十一选五订单明细校验 */
	@Resource(name="gx11x5OrderDetailValidate")
	private Gx11x5OrderDetailValidate gx11x5OrderDetailValidate;
	
	/** 江苏快3订单明细校验 */
	@Resource(name="jsk3OrderDetailValidate")
	private Jsk3OrderDetailValidate jsk3OrderDetailValidate;
	
	/** 江西快3订单明细校验 */
	@Resource(name="jxk3OrderDetailValidate")
	private Jxk3OrderDetailValidate jxk3OrderDetailValidate;
	
	/** 重庆时时彩订单明细校验 */
	@Resource(name="cqsscOrderDetailValidate")
	private CqsscOrderDetailValidate cqsscOrderDetailValidate;
	
	/** 快乐扑克订单明细校验 */
	@Resource(name="sdPokerOrderDetailValidate")
	private SdPokerOrderDetailValidate sdPokerOrderDetailValidate;
	
	/** 重庆快乐十分订单明细校验 */
	@Resource(name="cqkl10OrderDetailValidate")
	private Cqkl10OrderDetailValidate cqkl10OrderDetailValidate;
	
//	/** 广东快乐十分订单明细校验 */
//	@Resource(name="dkl10OrderDetailValidate")
//	private Dkl10OrderDetailValidate dkl10OrderDetailValidate;
	

	@Override
	public ResultBO<?> handle(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
		Lottery lottery = Lottery.getLottery(Integer.valueOf(lotteryCode));
		switch (lottery) {
		case SSQ:
			return ssqOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case DLT:
			return dltOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case F3D:
			return f3dOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case PL3:
			return pl3OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case PL5:
			return pl5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case QLC:
			return qlcOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case QXC:
			return qxcOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case SD11X5:
			return sd11x5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case D11X5:
			return d11x5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case JX11X5:
			return jx11x5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case XJ11X5:
			return xj11x5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case GX11X5:
			return gx11x5OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case JSK3:
			return jsk3OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case JXK3:
			return jxk3OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case CQSSC:
			return cqsscOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case SDPOKER:
			return sdPokerOrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		case CQKL10:
			return cqkl10OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
//		case DKL10:
//			return dkl10OrderDetailValidate.handle(orderDetailVO, orderInfoVO, list);
		default:
			return null;
		}
	}
}
