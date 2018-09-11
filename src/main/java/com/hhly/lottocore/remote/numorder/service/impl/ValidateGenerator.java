package com.hhly.lottocore.remote.numorder.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.AbstractChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.DltChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.F3dChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.Pl3ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.Pl5ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.QlcChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.QxcChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.chasevalidator.SsqChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.k3.Jsk3ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.k3.Jxk3ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.kl10.Cqkl10ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.poker.SdPokerChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.ssc.CqsscChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115.D11x5ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115.Gx11x5ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115.Jx11x5ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115.Sd11x5ChaseValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highchasevalidator.x115.Xj11x5ChaseValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;

/**
 * @desc 验证器工厂(提供各彩种验证服务)
 * @author huangb
 * @date 2017年3月28日
 * @company 益彩网络
 * @version v1.0
 */
@Component
public class ValidateGenerator {

	/** 双色球追号验证服务 */
	@Resource(name = "ssqChaseValidate")
	private SsqChaseValidate ssqChaseValidate;
	/** 大乐透追号验证服务 */
	@Resource(name = "dltChaseValidate")
	private DltChaseValidate dltChaseValidate;
	/** 福彩3d追号验证服务 */
	@Resource(name = "f3dChaseValidate")
	private F3dChaseValidate f3dChaseValidate;
	/** 排列三追号验证服务 */
	@Resource(name = "pl3ChaseValidate")
	private Pl3ChaseValidate pl3ChaseValidate;
	/** 排列五追号验证服务 */
	@Resource(name = "pl5ChaseValidate")
	private Pl5ChaseValidate pl5ChaseValidate;
	/** 七乐彩追号验证服务 */
	@Resource(name = "qlcChaseValidate")
	private QlcChaseValidate qlcChaseValidate;
	/** 七星彩追号验证服务 */
	@Resource(name = "qxcChaseValidate")
	private QxcChaseValidate qxcChaseValidate;
	
	/** 山东十一选五追号验证服务 */
	@Autowired
	private Sd11x5ChaseValidate sd11x5ChaseValidate;
	/** 广东十一选五追号验证服务 */
	@Autowired
	private D11x5ChaseValidate d11x5ChaseValidate;
	/** 江西十一选五追号验证服务 */
	@Autowired
	private Jx11x5ChaseValidate jx11x5ChaseValidate;
	/** 新疆十一选五追号验证服务 */
	@Autowired
	private Xj11x5ChaseValidate xj11x5ChaseValidate;
	/** 广西十一选五追号验证服务 */
	@Autowired
	private Gx11x5ChaseValidate gx11x5ChaseValidate;
	/** 江苏快3 追号验证服务 */
	@Resource(name = "jsk3ChaseValidate")
	private Jsk3ChaseValidate jsk3ChaseValidate;
	/** 江西快3 追号验证服务 */
	@Resource(name = "jxk3ChaseValidate")
	private Jxk3ChaseValidate jxk3ChaseValidate;
	/** 重庆时时彩追号验证服务 */
	@Resource(name = "cqsscChaseValidate")
	private CqsscChaseValidate cqsscChaseValidate;
	/** 山东快乐扑克3追号验证服务 */
	@Resource(name = "sdPokerChaseValidate")
	private SdPokerChaseValidate sdPokerChaseValidate;
	/** 重庆快乐10分追号验证服务 */
	@Resource(name = "cqkl10ChaseValidate")
	private Cqkl10ChaseValidate cqkl10ChaseValidate;
//	/** 广东快乐10分追号验证服务 */
//	@Resource(name = "dkl10ChaseValidate")
//	private Dkl10ChaseValidate dkl10ChaseValidate;

	/**
	 * @desc 获取彩种的追号验证实例
	 * @author huangb
	 * @date 2017年3月28日
	 * @param orderAdd
	 *            追号对象
	 * @return 获取彩种的追号验证实例
	 */
	public AbstractChaseValidate getValidateInstance(OrderAddVO chase) {
		Assert.notNull(chase, "40501");
		Lottery lottery = Lottery.getLottery(chase.getLotteryCode());
		Assert.notNull(lottery, "40502");
		switch (lottery) {
		case SSQ:
			return ssqChaseValidate;
		case DLT:
			return dltChaseValidate;
		case F3D:
			return f3dChaseValidate;
		case PL3:
			return pl3ChaseValidate;
		case PL5:
			return pl5ChaseValidate;
		case QLC:
			return qlcChaseValidate;
		case QXC:
			return qxcChaseValidate;
		case SD11X5:
			return sd11x5ChaseValidate;
		case D11X5:
			return d11x5ChaseValidate;
		case JX11X5:
			return jx11x5ChaseValidate;
		case XJ11X5:
			return xj11x5ChaseValidate;
		case GX11X5:
			return gx11x5ChaseValidate;
		case JSK3:	   //江苏快3
			return jsk3ChaseValidate;  
		case JXK3:	   //江西快3
			return jxk3ChaseValidate;
		case CQSSC:
			return cqsscChaseValidate;
		case SDPOKER:
			return sdPokerChaseValidate;
		case CQKL10:
			return cqkl10ChaseValidate;
//		case DKL10:
//			return dkl10ChaseValidate;
		default:
			throw new ResultJsonException(ResultBO.err("40502"));
		}
	}
}
