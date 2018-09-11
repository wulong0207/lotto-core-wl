package com.hhly.lottocore.numorder.remote.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import com.hhly.lottocore.remote.sportorder.service.impl.OrderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhly.lottocore.remote.numorder.service.BaseValidateService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class OrderServiceImpl_Order_Jxk3_Test {

	@Autowired
	private OrderServiceImpl orderService;
	
	@Autowired
	private BaseValidateService baseValidateService;
	
    @Autowired
    private StringRedisTemplate strRedisTemplate;
	  
	private String token;
	private IssueBO currIssue;
	private ObjectMapper mapper;
	

	
	@Before
	public void setUp() throws JsonParseException, JsonMappingException, IOException {
		Set<String> tokens = strRedisTemplate.keys("TOKEN_MEMBER_USER_C_*");
		token = tokens.iterator().next().replace("TOKEN_MEMBER_USER_C_", "");
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.JXK3.getName());
		mapper = new ObjectMapper();
	}
	
	// 测试支付过期时间有问题
	@Test
	public void testAddOrder_Sum_Single_Paytime_Err() throws Exception {
		String jsonInString = 	
				
				"{ \"orderAmount\": 42, \"categoryId\": 0, \"isDltAdd\": 0, \"channelId\": \"5\", \"multipleNum\": 1, \"orderDetailList\": [{  \"planContent\": \"3\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23401,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"17,18\",  \"buyNumber\": 2,  \"amount\": 4,  \"lotteryChildCode\": 23401,  \"contentType\": 2,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"3T\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23407,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"222\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23406,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"555,666\",  \"buyNumber\": 2,  \"amount\": 4,  \"lotteryChildCode\": 23406,  \"contentType\": 2,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"33#4\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23402,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"55*\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23403,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"44,55#4,5\",  \"buyNumber\": 2,  \"amount\": 4,  \"lotteryChildCode\": 23402,  \"contentType\": 2,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"3,4,5\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23405,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"3L\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23408,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"3,4,5,6\",  \"buyNumber\": 4,  \"amount\": 8,  \"lotteryChildCode\": 23405,  \"contentType\": 2,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"2,5\",  \"buyNumber\": 1,  \"amount\": 2,  \"lotteryChildCode\": 23404,  \"contentType\": 1,  \"codeWay\": 1,  \"multiple\": 1 }, {  \"planContent\": \"4,5,6\",  \"buyNumber\": 3,  \"amount\": 6,  \"lotteryChildCode\": 23404,  \"contentType\": 2,  \"codeWay\": 1,  \"multiple\": 1 }], \"tabType\": 0, \"buyType\": 1, \"platform\": 4, \"token\": \"34f1d98853ae4609b91c660e65c6db29\", \"lotteryIssue\": \"20170822039\", \"lotteryCode\": 234}";
				
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		vo.setToken("1234e4ccf4934a39b2c4918d30d3d93b");
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Sum_Single_Paytime_Err2() throws Exception {
		String jsonInString = 	
				
				"	{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":\"36\",\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"20170830014\",\"multipleNum\":1,\"orderAmount\":\"34\",\"orderDetailList\":[{\"amount\":\"16\",\"buyNumber\":8,\"codeWay\":1,\"contentType\":2,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"4,6,8,10,12,14,16,18\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23407,\"multiple\":1,\"planContent\":\"3T\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23406,\"multiple\":1,\"planContent\":\"444\"},{\"amount\":\"4\",\"buyNumber\":2,\"codeWay\":1,\"contentType\":2,\"lotteryChildCode\":23403,\"multiple\":1,\"planContent\":\"55*,66*\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"33#1\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23403,\"multiple\":1,\"planContent\":\"66*\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23408,\"multiple\":1,\"planContent\":\"3L\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23405,\"multiple\":1,\"planContent\":\"1,2,5\"},{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":23404,\"multiple\":1,\"planContent\":\"4,5\"}],\"platform\":3,\"tabType\":0,\"token\":\"d109797bccf54833bc084df4ec750228\"}";		
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	/********************* 和值 start*******************************/
	@Test
	public void testAddOrder_Sum_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"02,07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Sum_Single_PlanContent_Err2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"01,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Sum_Single_PlanContent_Err3() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"1,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Sum_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Sum_Mulit_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"171020028\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Sum_Limit_Error() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23401,\"multiple\":1,\"planContent\":\"3,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	/********************* 和值 end *******************************/
	
	/********************* 三同号通选 start *******************************/
	@Test
	public void testAddOrder_Tt3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23407,\"multiple\":1,\"planContent\":\"3T\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	@Test
	public void testAddOrder_Tt3_Multi_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23407,\"multiple\":1,\"planContent\":\"3T\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 三同号通选  end *******************************/
	
	/********************* 三同号单选 start *******************************/
	@Test
	public void testAddOrder_Td3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23406,\"multiple\":1,\"planContent\":\"111\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Td3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23406,\"multiple\":1,\"planContent\":\"111,222\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三同号单选  end *******************************/
	
	/********************* 三不同号 start *******************************/
	@Test
	public void testAddOrder_Bt3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23405,\"multiple\":1,\"planContent\":\"1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Bt3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23405,\"multiple\":1,\"planContent\":\"1,2,3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三不同号  end *******************************/
	
	/********************* 三连号通选 start *******************************/
	@Test
	public void testAddOrder_L3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23408,\"multiple\":1,\"planContent\":\"3L\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_L3_Multi_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23408,\"multiple\":1,\"planContent\":\"3L\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 三连号通选  end *******************************/
	
	/********************* 二同号复选 start *******************************/
	@Test
	public void testAddOrder_Tf2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23403,\"multiple\":1,\"planContent\":\"11*\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Tf2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23403,\"multiple\":1,\"planContent\":\"11*,22*\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二同号复选  end *******************************/
	
	/********************* 二同号单选 start *******************************/
	@Test
	public void testAddOrder_Td2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"11#2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Td2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"11#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Td2_Multi_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"11,22,33#1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Td2_Multi_Betcontent_Error() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	@Test
	public void testAddOrder_Td2_Multi_Betcontent_Error2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23402,\"multiple\":1,\"planContent\":\"11#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	/********************* 二同号单选  end *******************************/
	
	/********************* 二不同号 start *******************************/
	@Test
	public void testAddOrder_Bt2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":23404,\"multiple\":1,\"planContent\":\"1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	@Test
	public void testAddOrder_Bt2_Multi_Betcontent_Error() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":234,\"lotteryIssue\":\"2017061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":23404,\"multiple\":1,\"planContent\":\"1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	/********************* 二不同号  end *******************************/
	
	private void ETL(OrderInfoVO vo) {
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setLotteryIssue(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
