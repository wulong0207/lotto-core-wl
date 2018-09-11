package com.hhly.test;

import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderAddDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.remote.numorder.service.impl.OrderAddServiceImpl;
import com.hhly.lottocore.remote.ordercopy.service.impl.OrderCopyServiceImpl;
import com.hhly.lottocore.remote.sportorder.service.ValidateOrderPayTimeService;
import com.hhly.lottocore.remote.sportorder.service.impl.OrderServiceImpl;
import com.hhly.lottocore.remote.sportorder.service.impl.orderinfo.OrderSearchServiceImpl;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.TicketDetailServiceImpl;
import com.hhly.lottocore.remote.sportorder.service.impl.validate.OrderValidateMethod;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.HttpUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueOfficialTimeBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.bo.OrderFullDetailInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderStatisticsInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;


/**
 * 订单测试类
 * @author yuanshangbing
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class OrderServiceTest extends AbstractJUnit4SpringContextTests{

	public final String BET_CONTENT = "161128301[+1](3@1.57,0@2.27)|161128302[+1](1@1.89,0@4.21)|161128303[+1](0@4.21)^3_1";
	private Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);
	@Autowired
	private OrderSearchServiceImpl orderSearchServiceImpl;
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	@Autowired
	private OrderAddServiceImpl orderAddService;
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	@Autowired
	private OrderValidateMethod orderValidateMethod ;
	@Autowired
	private ValidateOrderPayTimeService orderPayTimeService;
	@Autowired
	private LotteryIssueDaoMapper lotteryIssueDaoMapper;
	@Autowired
	private OrderAddDaoMapper orderAddDaoMapper;
	@Autowired
	ValidateOrderPayTimeService validateOrderPayTimeService;

	@Autowired
	private OrderCopyServiceImpl iOrderCopyService;

	@Autowired
	private TicketDetailServiceImpl ticketDetailService;

	/*@Test
	public void testValidateOrder(){
        OrderValidator ball = orderServiceImpl.getOrderPlugin(30105);
        OrderInfoVO vo = new OrderInfoVO();
        String betContent = "1702207301_R[+11.5](3@1.57,0@2.27)^3_1";


        vo.setBetContent(betContent);
        vo.setLotteryCode(301);
        vo.setUserId(2l);
        vo.setLotteryIssue("2017022201");
        vo.setOrderAmount(12.00);
        vo.setChannelId("001");
        vo.setMultipleNum(4);
        vo.setGameNum(10);
        vo.setFlag("M");//玩法
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setLotteryChildCode(30101);
        orderDetailVO.setPlanContent(betContent);
        orderDetailVO.setBetNum(2);
        orderDetailVO.setMultiple(3);
        orderDetailVO.setAmount(12.00);
        List<OrderDetailVO> result = new ArrayList<OrderDetailVO>();
        result.add(orderDetailVO);
        vo.setOrderDetailList(result);
        try{
            ResultBO<?> resultbo = ball.handle(vo);
            resultbo = ball.handleDetails(vo, orderDetailVO);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Test
    public void testOrderBussiness(){
        OrderValidator ball = orderServiceImpl.getOrderPlugin(30001);
         System.out.println(ball);
         OrderInfoVO  orderInfoVO = new OrderInfoVO();
         List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
         OrderDetailVO orderDetailVO = new OrderDetailVO();
         orderDetailVO.setPlanContent(BET_CONTENT);
         orderDetailList.add(orderDetailVO);
         //非空验证 ： 彩种id、用户ID、 彩期、 订单总额、渠道ID、订单详情
         orderInfoVO.setLotteryCode(300);
         orderInfoVO.setUserId(2L);
         orderInfoVO.setLotteryIssue("161224");
         orderInfoVO.setFlag("M");
         orderInfoVO.setOrderAmount(1D);
         orderInfoVO.setChannelId("手机App");
         orderInfoVO.setBetContent(BET_CONTENT);
         orderInfoVO.setWinningDetail("1_1");
         orderInfoVO.setOrderDetailList(orderDetailList);
         try {
            ResultBO<?> result = ball.handle(orderInfoVO);
            if(result.isError()){
                System.out.println(result.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */
	@Test
	public void testOrderDetailBussiness(){
//    	 OrderService plugin = new OrderServiceImpl();
		OrderInfoVO  orderInfoVO = new OrderInfoVO();
		List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		orderDetailVO.setPlanContent(BET_CONTENT);
		orderDetailVO.setLotteryChildCode(30002);
		orderDetailVO.setBuyNumber(20);
		orderDetailVO.setMultiple(20);
		orderDetailVO.setAmount(800d);
		orderDetailVO.setCodeWay(1);
		orderDetailVO.setContentType(2);
		orderDetailVO.setBuyScreen("161128301,161128302,161128303");
		orderDetailList.add(orderDetailVO);
		//非空验证 ： 彩种id、用户ID、 彩期、 订单总额、渠道ID、订单详情
		orderInfoVO.setLotteryCode(300);
//		orderInfoVO.setUserId(2);
		orderInfoVO.setMultipleNum(100);
		orderInfoVO.setOrderAmount(800d);
		orderInfoVO.setLotteryIssue("170410");
//	   	 orderInfoVO.setOrderAmount(1D);
		orderInfoVO.setChannelId("手机App");
		orderInfoVO.setBetContent(BET_CONTENT);
		orderInfoVO.setWinningDetail("1_1");
		orderInfoVO.setIsDltAdd((short)0);
		orderInfoVO.setBuyScreen("161128301,161128302,161128303");
		orderInfoVO.setToken("645d42dbf24b43eb8c3a44791d37a23b");
		orderInfoVO.setOrderDetailList(orderDetailList);
		try {
			ResultBO<?> result = orderServiceImpl.addOrder(orderInfoVO);
			if(result.isError()){
				System.out.println(result.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOrderValidateMethod(){
		OrderInfoVO  orderInfoVO = new OrderInfoVO();
		List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		orderDetailVO.setPlanContent(BET_CONTENT);
		orderDetailVO.setLotteryChildCode(30002);
		orderDetailVO.setBuyNumber(20);
		orderDetailVO.setMultiple(20);
		orderDetailVO.setAmount(800d);
		orderDetailVO.setCodeWay(1);
		orderDetailList.add(orderDetailVO);
		//非空验证 ： 彩种id、用户ID、 彩期、 订单总额、渠道ID、订单详情
		orderInfoVO.setLotteryCode(300);
//		orderInfoVO.setUserId(2);
		orderInfoVO.setMultipleNum(100);
		orderInfoVO.setOrderAmount(800d);
		orderInfoVO.setLotteryIssue("17051");
//	   	 orderInfoVO.setOrderAmount(1D);
		orderInfoVO.setChannelId("手机App");
		orderInfoVO.setBetContent(BET_CONTENT);
		orderInfoVO.setWinningDetail("1_1");
		orderInfoVO.setOrderDetailList(orderDetailList);
		try {
			orderValidateMethod.verifyIssueInfo(orderInfoVO, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testOrderPayTime(){
		OrderInfoVO  orderInfoVO = new OrderInfoVO();
		List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		orderDetailVO.setPlanContent(BET_CONTENT);
		orderDetailVO.setLotteryChildCode(30002);
		orderDetailVO.setBuyNumber(20);
		orderDetailVO.setMultiple(20);
		orderDetailVO.setAmount(800d);
		orderDetailList.add(orderDetailVO);
		//非空验证 ： 彩种id、用户ID、 彩期、 订单总额、渠道ID、订单详情
		orderInfoVO.setLotteryCode(215);
//		orderInfoVO.setUserId(2);
		orderInfoVO.setMultipleNum(100);
		orderInfoVO.setOrderAmount(800d);
		orderInfoVO.setLotteryIssue("20170102075");
//	   	 orderInfoVO.setOrderAmount(1D);
		orderInfoVO.setChannelId("手机App");
		orderInfoVO.setBetContent(BET_CONTENT);
		orderInfoVO.setWinningDetail("1_1");
		orderInfoVO.setOrderDetailList(orderDetailList);
		try {
			IssueBO	lotteryIssueBO = lotteryIssueDaoMapper.findSingleFront(new LotteryVO(orderInfoVO.getLotteryCode(),orderInfoVO.getLotteryIssue()));
			orderPayTimeService.checkPayEndTime("O2016121916233800086");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//    public final String BET_CONTENT = "161128001_R[+1](3@1.57,0@2.27)_S(1@1.89,0@4.21)_Q(11@1.78,23@5.21)_Z(2@3.45,3@2.34)_B(33@2.31,31@4.00)#161128002_S(1@1.89,0@4.21)|161128003_Z(0@4.21)|161128004_R[-2](3@3.33)_Z(0@4.21)^3_1,4_1";

	@Test
	public void testQueryOrderListinfo() throws Exception{
		OrderQueryVo orderQueryVo = new OrderQueryVo();
		orderQueryVo.setPageIndex(0);
		orderQueryVo.setPageSize(20);
		orderQueryVo.setToken("cdde1e00e4b047c9ba3bd8dc46f863cb");
		orderQueryVo.setType(1);//中奖
		orderQueryVo.setSource(1);//移动端
		//orderQueryVo.setLotteryCode(100);
		//orderQueryVo.setBuyType(1);
		orderQueryVo.setType(1);
		/*orderQueryVo.setBeginDate(DateUtil.convertStrToDate("2016-12-01",DateUtil.DATE_FORMAT));
		orderQueryVo.setEndDate(DateUtil.convertStrToDate("2018-03-21",DateUtil.DATE_FORMAT));*/

		//System.out.println(JsonUtil.object2Json(orderQueryVo));
		ResultBO<?> result = orderSearchServiceImpl.queryOrderListInfo(orderQueryVo);
		System.out.println(result);
	}

	@Test
	public void testQueryOrderDetailInfo() throws Exception{
		//竞技彩
		ResultBO<?> orderDetailInfoBOs = orderSearchServiceImpl.queryOrderDetailInfo("O2017040610594501595", "cdde1e00e4b047c9ba3bd8dc46f863cb",null,0,null);
		System.out.println(orderDetailInfoBOs.getData());

		//追号
		/*ResultBO<?> orderDetailInfoBOs = orderSearchServiceImpl.queryOrderDetailInfo("O2017020510392700267", "cdde1e00e4b047c9ba3bd8dc46f863cb",null);
		System.out.println(orderDetailInfoBOs.getData());*/



	}

	@Test
	public void testCancelOrder() throws Exception {
		OrderSingleQueryVo orderQueryVo = new OrderSingleQueryVo();
		orderQueryVo.setToken("cdde1e00e4b047c9ba3bd8dc46f863cb");
		orderQueryVo.setPayStatus(2);
		orderQueryVo.setOrderCode("O2016121916562200100");
		ResultBO resultBO = orderServiceImpl.updateOrderStatus(orderQueryVo);

		/*PagingBO<OrderListInfoBO> result = orderSearchServiceImpl.queryOrderListInfo(orderQueryVo);
		System.out.println(result);*/
	}

	@Test
	public void testOrderStatis() throws Exception{
		OrderStatisticsQueryVo orderStatisticsQueryVo = new OrderStatisticsQueryVo();
		orderStatisticsQueryVo.setToken("cdde1e00e4b047c9ba3bd8dc46f863cb");
		orderStatisticsQueryVo.setSource(1);
		OrderStatisticsInfoBO orderStatisticsInfoBO= orderInfoDaoMapper.queryOrderStatisInfo(orderStatisticsQueryVo);

		/*PagingBO<OrderListInfoBO> result = orderSearchServiceImpl.queryOrderListInfo(orderQueryVo);
		System.out.println(result);*/
		System.out.println(orderStatisticsInfoBO);
	}

	@Test
	public void testHomeOrder() throws Exception{
		OrderQueryVo orderQueryVo = new OrderQueryVo();
		orderQueryVo.setToken("cdde1e00e4b047c9ba3bd8dc46f863cb");
		orderQueryVo.setStatus(1);
		ResultBO<?> result = orderSearchServiceImpl.queryHomeOrderList(orderQueryVo);

		/*PagingBO<OrderListInfoBO> result = orderSearchServiceImpl.queryOrderListInfo(orderQueryVo);
		System.out.println(result);*/
		System.out.println(result);
	}

	@Test
	public void getOrderInfoList(){
		List<OrderInfoBO> orderInfoBOs = orderInfoDaoMapper.getOrderInfoList(2, 2, Arrays.asList(300, 301));
		System.out.println(net.sf.json.JSONArray.fromObject(orderInfoBOs));

//		orderInfoDaoMapper.getOrderInfo("12345645465");
	}


	@Test
	public void testQueryOrderInfo() throws Exception{
		/*List<OrderDetailInfoBO> orderDetailInfoBOs = orderInfoDaoMapper.queryOrderDetailInfo("O2016121916562200100", 9527);

		System.out.println(orderDetailInfoBOs.size());*/
		/*PagingBO<OrderListInfoBO> result = orderSearchServiceImpl.queryOrderListInfo(orderQueryVo);
		System.out.println(result);*/
		ResultBO<?> orderDetailInfoBOs = orderSearchServiceImpl.queryOrderInfo("O2017040610594501595", "5b7f1a32e9bd48029f8285da3f0d39d7");
		System.out.println(orderDetailInfoBOs.getData());
	}

	/**
	 * @desc 测试-前端接口：用户中心-查询用户数字订单详情
	 * @author huangb
	 * @date 2017年4月12日
	 * @throws Exception
	 */
	@Test
	public void testQueryUserNumOrderDetail() throws Exception {
		UserNumOrderDetailQueryVO queryVO = new UserNumOrderDetailQueryVO("O2017041212082202147","5b7f1a32e9bd48029f8285da3f0d39d7",2,10);
		ResultBO<?> result = orderSearchServiceImpl.queryUserNumOrderDetail(queryVO);
		System.out.println(result.getSuccess());
	}
	/**
	 * @desc 测试-前端接口：用户中心-查询用户追号内容详情
	 * @author huangb
	 * @date 2017年4月12日
	 * @throws Exception
	 */
	@Test
	public void testQueryUserChaseContent() throws Exception {
		UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO("Z2017042118421304875", "01cf9f3fab5d479fa55779a44de5d622",0,10);
		ResultBO<?> result = orderSearchServiceImpl.queryUserChaseContent(queryVO);
		System.out.println(result.getSuccess());
	}
	/**
	 * @desc 测试-前端接口：用户中心-查询用户追号内容详情
	 * @author huangb
	 * @date 2017年4月12日
	 * @throws Exception
	 */
	@Test
	public void testQueryUserChaseDetail() throws Exception {
		UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO("Z2017041118562903557", "5b7f1a32e9bd48029f8285da3f0d39d7",1,10);
		ResultBO<?> result = orderSearchServiceImpl.queryUserChaseDetail(queryVO);
		System.out.println(result.getSuccess());
	}

	/**
	 * @desc 前端接口(调用)：用户中心-查询正在执行的追号彩期
	 * @author huangb
	 * @date 2017年4月13日
	 * @throws Exception
	 */
	@Test
	public void testFindCurChasingIssue() throws Exception {
		UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO("Z2017041118562903557", "5b7f1a32e9bd48029f8285da3f0d39d7");
		queryVO.setUserId(9527);
		String result = orderAddDaoMapper.findCurChasingIssue(queryVO);
		System.out.println(result);
	}
	/**
	 * @desc 前端接口：用户中心-用户终止追号任务 （用户撤单：追号期号中所有等待追号的全撤）
	 * @author huangb
	 * @date 2017年4月13日
	 * @throws Exception
	 */
	@Test
	public void testUpdChaseStatusAsUserCancel() throws Exception {
		UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO("JZ1705262056110100014", "025e61e6e5374c4cb11e2cdcd1e2cadb");
		ResultBO<?> result = orderAddService.updChaseStatusAsUserCancel(queryVO);
		System.out.println(result.getSuccess());
	}
	/**
	 * @desc 前端接口：用户中心-查询追号计划中奖金额（税前或税后）的组成明细
	 * @author huangb
	 * @date 2017年4月13日
	 * @throws Exception
	 */
	@Test
	public void testQueryUserChaseWinningDetail() throws Exception {
		UserChaseDetailQueryVO queryVO = new UserChaseDetailQueryVO("Z2017041118562903557", "5b7f1a32e9bd48029f8285da3f0d39d7");
		ResultBO<?> result = orderSearchServiceImpl.queryUserChaseWinningDetail(queryVO);
		System.out.println(result.getSuccess());
	}
	/**
	 * @desc 前端接口：用户中心-查询追号计划中奖金额（税前或税后）的组成明细
	 * @author huangb
	 * @date 2017年4月13日
	 * @throws Exception
	 */
	@Test
	public void testQueryOfficialTime() throws Exception {
		IssueOfficialTimeBO data = orderSearchServiceImpl.queryOfficialTime(Lottery.FB.getName());//Lottery.SSQ.getName(),Lottery.SD11X5.getName()
		//System.out.println(data.getOfficialStartTimeStr()+"|"+data.getOfficialEndTimeStr());
		System.out.println(data.getLastOfficialEndTime()+"|"+data.getOfficialStartTime()+"|"+data.getOfficialEndTime());
	}

	public static void main(String[] args) {
		/*List<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		System.out.println(JsonUtil.objectToJson(a));*/

		Set<Integer> set = new TreeSet<Integer>();
		set.add(3);
		set.add(1);
		set.add(2);
		System.out.println(set);
	}

	@Test
	public void queryOrderListForOrderCodes() throws Exception {
		List<OrderQueryVo> orderQueryVoList= new ArrayList<OrderQueryVo>();
		/*OrderQueryVo o = new OrderQueryVo();
		o.setOrderCode("D1705051049270100002");
		o.setBuyType(1);
		orderQueryVoList.add(o);*/

		OrderQueryVo o1 = new OrderQueryVo();
		o1.setOrderCode("O2017020511234500270");
		o1.setBuyType(2);
		orderQueryVoList.add(o1);



		String token ="cdde1e00e4b047c9ba3bd8dc46f863cb";
		ResultBO<?> resultBO = orderSearchServiceImpl.queryOrderListForOrderCodes(orderQueryVoList,token);
		System.out.println(resultBO);
	}

	
	@Test
	public void testOrderPayTime1()throws Exception{
		validateOrderPayTimeService.checkPayEndTime("D1705222111310100048");
	}

	@Test
	public void testBuildOrderInfo()throws Exception{
		OrderFullDetailInfoBO orderFullDetailInfoBO = iOrderCopyService.buildOrderInfo("D17102110000616600115");
		System.out.println(orderFullDetailInfoBO);
	}

	@Test
	public void testTicketList() throws Exception{
		final OrderQueryVo orderQueryVo = new OrderQueryVo();
        orderQueryVo.setToken("a796da394f23478092b15842b5a2fa1e");
        //双色球：D1705221152200100022 大乐透：D1705181821020100076 福彩3D:D1707141014400100108
		//排列五：Z17081819504116600008 排列三：D17090522231316600182
		orderQueryVo.setOrderCode("D17090522231316600182");
		orderQueryVo.setPageIndex(0);
		orderQueryVo.setPageSize(10);
		ResultBO<?> resultBO = ticketDetailService.queryTicketDetailInfo(orderQueryVo);
		System.out.println(resultBO);
	}

	@Test
	public void test()throws Exception{
		/*Map<String,String> map = new HashMap<String,String>();
		map.put("langId","2");
		List a = JsonUtil.jsonToArray(HttpUtil.doPost("http://tj.1332255.com/tjDetail/findTjDetailJc",map),Map.class);
		System.out.println(a);*/
		ResultBO<?> resultBO = iOrderCopyService.getCopyOrderInfoForPay(563,null);
		System.out.println(resultBO);
	}



}
