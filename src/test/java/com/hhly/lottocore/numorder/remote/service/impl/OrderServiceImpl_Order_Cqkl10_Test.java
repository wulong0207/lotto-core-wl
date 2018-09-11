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
public class OrderServiceImpl_Order_Cqkl10_Test {

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
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.CQKL10.getName());
		mapper = new ObjectMapper();
	}
	
	// 对接前端
	@Test
	public void testAddOrder_D2_Multi_front() throws Exception {
		String jsonInString = 	
				
				"	{ \"orderAmount\" : 2, \"categoryId\" : 0, \"isDltAdd\" : 0, \"channelId\" : 5, \"multipleNum\" : 1, \"orderDetailList\" : [ { \"planContent\" : \"01|01,02\", \"buyNumber\" : 1, \"amount\" : 2, \"lotteryChildCode\" : 22205, \"contentType\" : 2, \"codeWay\" : 1, \"multiple\" : 1}], \"tabType\" : 0, \"buyType\" : 1, \"platform\" : 4, \"token\" : \"60028f79c1c141e4859db074fb0e8595\", \"lotteryIssue\" : \"20170909048\", \"verifyOpen\" : 0, \"lotteryCode\" : 222}";		
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	/********************* 前一数投 start*******************************/
	@Test
	public void testAddOrder_St_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_St_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"19\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_St_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_St_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02,07,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_St_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02,18,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_St_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02,07,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_St_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02,07,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		//assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_St_Danutuo_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22201,\"multiple\":1,\"planContent\":\"02,07,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	
	/********************* 前一数投 end *******************************/
	
	/********************* 前一红投 start*******************************/
	@Test
	public void testAddOrder_Ht_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"19\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Ht_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Ht_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_Ht_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		//assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Ht_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_Ht_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"02,07,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Ht_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		//assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_Ht_Danutuo_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22202,\"multiple\":1,\"planContent\":\"02,07,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	/********************* 前一红投 end *******************************/
	
	/********************* 任二 start *******************************/
	@Test
	public void testAddOrder_R2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"01,02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"1,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"01,02,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"01,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R2_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"02,07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"01,05,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22203,\"multiple\":1,\"planContent\":\"20#02,03,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 任二      end *******************************/
	
	/********************* 选二连组 start *******************************/
	@Test
	public void testAddOrder_G2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"01,02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G2_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"1,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_G2_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_G2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"01,02,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G2_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"01,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_G2_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"02,07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_G2_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"01,05,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_G2_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G2_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22204,\"multiple\":1,\"planContent\":\"20#02,03,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 选二连组     end *******************************/
	
	/********************* 选二连直 start *******************************/
	@Test
	public void testAddOrder_D2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"01|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D2_Single_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"20|19\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D2_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"01|18|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_D2_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"19|20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_D2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"01|02,03,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D2_Multi_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"20|02,19,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D2_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"19|18,20,01\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_D2_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"02|07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_D2_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"01|05,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_D2_Danutuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22205,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	/********************* 选二连直     end *******************************/
	
	/********************* 任三 start *******************************/
	@Test
	public void testAddOrder_R3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01,02,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01,18,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"18,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01,02,03,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01,19,20,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R3_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"02,03,07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R3_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01,02,05,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R3_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22206,\"multiple\":1,\"planContent\":\"20#02,18,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 任三     end *******************************/
	
	/********************* 选三前组 start *******************************/
	@Test
	public void testAddOrder_G3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01,02,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G3_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01,18,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_G3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"18,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_G3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01,02,03,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01,19,20,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_G3_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"02,03,07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_G3_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01,02,05,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_G3_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_G3_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22207,\"multiple\":1,\"planContent\":\"20#02,18,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 选三前组     end *******************************/
	
	/********************* 选三前直 start *******************************/
	@Test
	public void testAddOrder_D3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"01|02|03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D3_Single_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"20|19|18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D3_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"01|18,17|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_D3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"18|19|20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_D3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"01|02,03,04|05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D3_Multi_Success2() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"20|02,19,04|18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_D3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"18|19|18,20,01\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_D3_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"02|07,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_D3_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"01|05,18|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_D3_Danutuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22208,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_TYPE_FIELD));
	}
	/********************* 选三前直     end *******************************/
	
	/********************* 任四 start *******************************/
	@Test
	public void testAddOrder_R4_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01,02,03,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01,02,18,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R4_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"17,18,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R4_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":10,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":5,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01,02,03,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01,17,19,20,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R4_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"02,03,07,21,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R4_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01,02,05,18,19\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R4_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":168,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":168,\"buyNumber\":84,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22209,\"multiple\":1,\"planContent\":\"20#02,18,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 任四     end *******************************/
	
	/********************* 任选五 start *******************************/
	@Test
	public void testAddOrder_R5_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01,02,03,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Single_PlanContent_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01,02,03,18,21\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R5_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"16,17,18,19,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R5_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01,16,17,19,20,18\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R5_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"02,03,04,07,21,20\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R5_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01,02,03,05,18,19\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R5_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":252,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":252,\"buyNumber\":126,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"01#02,03,14,15,16,17,18,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Danutuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":222,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":72,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":72,\"buyNumber\":36,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22210,\"multiple\":1,\"planContent\":\"20#02,18,14,15,16,17,19,04,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	/********************* 任选五     end *******************************/
	
	private void ETL(OrderInfoVO vo) {
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setLotteryIssue(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
