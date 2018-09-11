package com.hhly.lottocore.numorder.remote.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
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
public class OrderServiceImpl_Order_Cqssc_Test {

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
		Iterator<String> it = tokens.iterator();
		token = it.next().replace("TOKEN_MEMBER_USER_C_", "");
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.CQSSC.getName());
		mapper = new ObjectMapper();
	}
	
	// 前端调试
	@Test
	public void testAddOrder_2_Single_Success2() throws Exception {
		String jsonInString = 
				
				"	{buyScreen:\"\",buyType:1,channelId:6,clientType:2,isDltAdd:0,lotteryCode:201,lotteryIssue:\"20170824081\",multipleNum:1,orderAmount:2,orderDetailList:[ {  amount:2,  buyNumber:1,  codeWay:1,  contentType:1,  lotteryChildCode:20107,  multiple:1,  planContent:\"6|5\" }],platform:2,token:\"1e7c4297c12e453aad15b6b765a9e8f6\"}";		
		
		
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	
	/********************* 五星直选 start*******************************/
	@Test
	public void testAddOrder_5_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1|2|3|4|6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
//	@Test
//	public void testAddOrder_5_Single_Success2() throws Exception {
//		String jsonInString = 	
//			"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":\"4\",\"isDltAdd\":0,\"lotteryCode\":105,\"lotteryIssue\":\"2017198\",\"multipleNum\":1,\"orderAmount\":\"2\",\"orderDetailList\":[{\"amount\":\"2\",\"buyNumber\":1,\"codeWay\":1,\"contentType\":1,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1|8|8|4|2\"}],\"platform\":3,\"tabType\":0,\"token\":\"347660fb8b674428a5c8726f9dd6c58b\"}";		
//		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
//		ETL(vo);
//		ResultBO<?> ret = orderService.addOrder(vo);
//		assertTrue(ret.isOK());
//	}
	
	@Test
	public void testAddOrder_5_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1|2|3|4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_5_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1,2,3,4|3|3,4|4|5,6,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_5_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":48,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":48,\"buyNumber\":24,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1,2,3,4|2|3,4|4|6,7,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_5_Danutuo_NotSupported_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1,2#3,4,5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_5_Sum_NotSupported_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_5_Single_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1|2|3|4|5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_5_Multi_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6250,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6250,\"buyNumber\":3125,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1,2,3,4,5|1,2,3,4,5|1,2,3,4,5|1,2,3,4,5|1,2,3,4,5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_5_Limit_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6250,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6250,\"buyNumber\":3125,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1,2,3,4,5|1,2,3,4,5|1,2,3,4,5|1,2,3,4,5|1,2,3,4,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	/********************* 五星直选 end *******************************/
	
	/********************* 五星通选 start*******************************/
	@Test
	public void testAddOrder_5T_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1|2|3|4|6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_5T_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1|2|3|4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_5T_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1,2,3,4|2|3,4|4|7,6,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_5T_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":48,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":48,\"buyNumber\":24,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1,2,3,4|2|3,4|4|7,6,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_5T_Danutuo_NotSupported_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1,2#3,4,5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_5T_Single_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20101,\"multiple\":1,\"planContent\":\"1|2|3|4|5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_5T_Sum_NotSupported_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_5T_Single_2_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1|2|3|4|5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_5T_Multi_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":48,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":48,\"buyNumber\":24,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20102,\"multiple\":1,\"planContent\":\"1,2,3,4|2|3,4|4|5,6,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	/********************* 五星通选 end *******************************/
	
	
	/********************* 三星直选 start*******************************/
	@Test
	public void testAddOrder_3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1|2|3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"7|8|9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1|2|3|4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_3_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1,2|2,3,4|3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1,2|2,3,4|3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1,7|2,8,4|9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3_Danutuo_NotSupported_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"1,2#3,4,5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_3_Sum() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":138,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":138,\"buyNumber\":69,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3_Sum_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":138,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":138,\"buyNumber\":69,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20103,\"multiple\":1,\"planContent\":\"24\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	/********************* 三星直选 end *******************************/
	
	/********************* 三星组三 start*******************************/
	@Test
	public void testAddOrder_3Z3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"0,1,1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"0,1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z3_Multi_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":40,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":40,\"buyNumber\":20,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"3,4,5,6,7\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z3_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"0#1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z3_Sum_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"0#1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_3Z3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"8,9,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3Z3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"8,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3Z3_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20104,\"multiple\":1,\"planContent\":\"9#8\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 三星组三 end *******************************/
	
	/********************* 三星组六 start *******************************/
	@Test
	public void testAddOrder_3Z6_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"0,1,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z6_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"0,1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z6_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"0,1#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_3Z6_Sum_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"0#1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_3Z6_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"7,8,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3Z6_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"0,7,8,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_3Z6_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20105,\"multiple\":1,\"planContent\":\"7,8#2,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 三星组六 end *******************************/
	
	/********************* 二星直选 start *******************************/
	@Test
	public void testAddOrder_2_Single() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"1|1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2_Multi() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"1,2|1,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2_Sum() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2_Sum_2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":14,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":14,\"buyNumber\":7,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	
	@Test
	public void testAddOrder_2_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":14,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":14,\"buyNumber\":7,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_2_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"8|9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_2_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"1,8|9,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_2_Sum_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"17\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_2_Sum_2_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":10,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":5,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20106,\"multiple\":1,\"planContent\":\"17,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 二星直选 end *******************************/
	
	/********************* 二星组选 start *******************************/
	@Test
	public void testAddOrder_2z_Single() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"1,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2z_Multi() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2z_Dantuo() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"1#2,3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2z_Sum() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"0\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2z_Sum_2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"6,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_2z_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"9,8\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_2z_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"8,2,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}

	@Test
	public void testAddOrder_2z_Sum_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"17\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_2z_Sum_2_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20107,\"multiple\":1,\"planContent\":\"6,17\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 二星组选 end *******************************/
	
	/********************* 一星 start *******************************/
	@Test
	public void testAddOrder_1_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"0\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_1_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"0,1,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_1_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"0,1#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_1_Sum_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"0#1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_1_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_1_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20108,\"multiple\":1,\"planContent\":\"0,1,2,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 一星 end *******************************/
	
	/********************* 大小单双 start *******************************/
	@Test
	public void testAddOrder_Dxds_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"1|1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	// 大小单双改为不支持复式
	@Test
	public void testAddOrder_Dxds_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"1,2|1,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_Dxds_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"0,1#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_Dxds_Sum_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":6,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"0#1\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	@Test
	public void testAddOrder_Dxds_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"3|4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	// 大小单双改为不支持复式
	@Test
	public void testAddOrder_Dxds_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":201,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":20109,\"multiple\":1,\"planContent\":\"1,3|1,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	/********************* 大小单双 end *******************************/
	
	private void ETL(OrderInfoVO vo) {
		vo.setLotteryIssue(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
