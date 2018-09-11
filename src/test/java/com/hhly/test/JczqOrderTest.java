package com.hhly.test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;
import com.hhly.lottocore.remote.lotto.service.IJczqOrderService;
import com.hhly.lottocore.remote.lotto.service.impl.JczqOrderServiceImpl;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * 
 * @author longguoyou
 * @date 2017年2月5日 上午10:29:48
 * @Desc
 *
 */
public class JczqOrderTest {

	private static Logger logger = LoggerFactory.getLogger(JczqOrderServiceImpl.class);
	private String url;
	//private IJczqMainDataService serviceMain;
	private IJczqOrderService service;
	
	/**
	 * 连接超时
	 */
	private static final long CONNECT_TIME_OUT = 10000;
	/**
	 * 读取超时
	 */
	private static final long READ_TIME_OUT = 20000;
	
	/**
	 * @param <T>
	 * @param <T>
	 * @param c
	 * @param url
	 * @return 接口对象
	 * @throws MalformedURLException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRemoteService(Class<T> c, String url) throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		factory.setConnectTimeout(CONNECT_TIME_OUT);
		factory.setReadTimeout(READ_TIME_OUT);
		factory.setUser("lotto_core");
		factory.setPassword("_ecai2017");
		return (T) factory.create(c, url);
	}



    @Before
	public void before() throws MalformedURLException{//jczqMainDataService
//    	url = "http://localhost:8080/lotto-core/remote/jczqOrderService";
    	url = "http://localhost:8080/lotto-core/remote/jczqMainDataService";
		//serviceMain = HessianUtils.getRemoteService(IJczqMainDataService.class, url);
		service = getRemoteService(IJczqOrderService.class, "http://localhost:8080/lotto-core/remote/jczqOrderService");
	}

	@Test
	public void testMainData() {
		//@SuppressWarnings("unchecked")
		//List<SportDataFbHfWDFBO> boList = (List<SportDataFbHfWDFBO>)serviceMain.listMainData(SportEnum.SportFbSubWay.JCZQ_B.getValue());
//    	for(SportDataFbHfWDFBO bo : boList){
//    		//System.out.println("客队名全称："+ bo.getAwayTeamFullName());
//    	}
	}

	@Test
	public void testMethods(){
//    	System.out.println(service.getOrderNo(OrderEnum.NumberCode.ORDER_D.getCode()));
		System.out.println(service);
		logger.info("hello world .....");
		try {
			OrderInfoVO vo = new OrderInfoVO();
			vo.setOrderCode("O1234567890");
			vo.setLotteryCode(300);
			vo.setLotteryName("竞彩足球");
			vo.setLotteryIssue("170206 欧世外");
//			vo.setUserId(Long.parseLong("123456"));
			vo.setOrderAmount(Double.parseDouble("12"));
			vo.setMultipleNum(2);
			vo.setChannelId("手机客户端");
			vo.setIsDltAdd((short)0);
			OrderDetailVO od;
			List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
			for(int i = 0 ; i < 5 ; i++){
				od = new OrderDetailVO();
				od.setOrderCode(vo.getOrderCode());
				od.setCodeWay(1);
//				od.setBetNum(10 + i);
				orderDetailList.add(od);
			}
			vo.setOrderDetailList(orderDetailList);
			service.addOrder(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testImpl() {
//    	IJczqDataService goalSerivce = new JczqDataFbGoalServiceImpl();
//    	goalSerivce.listBaseData();
//    	goalSerivce.listMainData();
//    	goalSerivce = new JczqDataFbHfWDServiceImpl();
//    	goalSerivce.listBaseData();
//    	goalSerivce.listMainData();
    	//String content = "1|2|3|4|";
    	//String content1= "1^2^3^4";
    	//String content2= "1*2*3*4";
    	String content3= "1#2#3#4";
    	for(String str: content3.split(SymbolConstants.NUMBER_SIGN)){
    		System.out.println(str);
    	}

	}
}
