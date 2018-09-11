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
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class OrderServiceImpl_Order_Gx11x5_Test {

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
		Set<String> tokens = strRedisTemplate.keys("c_core_member_info*");
		token = tokens.iterator().next().replace("c_core_member_info", "");
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.GX11X5.getName());
		mapper = new ObjectMapper();
	}
	
	
	/********************* 任2 start*******************************/
	@Test
	public void testAddOrder_R2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02,07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Multi_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02,07,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02,07,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		//assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02,07,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02,07,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_PlanContent2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"01,03#04,05,06,07,10,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_PlanContent_Length_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"01,08,03,04,05,06,07,11#10,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"01#04,05,06,07,10,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Limit_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"11#10,05\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Limit_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"02#04,06\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27102,\"multiple\":1,\"planContent\":\"01#04,05,06,07,10,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任2 end *******************************/
	
	/********************* 任5 start *******************************/
	@Test
	public void testAddOrder_R5_Multi_Content_Err() throws Exception {
		String jsonInString = 
			"{\"buyType\":1,\"orderAmount\":2,\"channelId\":\"H5端\",\"isDltAdd\":0,\"lotteryIssue\":\"17061951\",\"lotteryCode\":271,\"multipleNum\":1,\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":1,\"contentType\":2,\"lotteryChildCode\":\"27105\",\"multiple\":1,\"planContent\":\"08,09,13,15,22,24,29,30\"}],\"platform\":2,\"token\":\"56880b64a08a4c10aa1ea0244dc1efab\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	/********************* 任5 end *******************************/
	
	/********************* 任8 start*******************************/
	@Test
	public void testAddOrder_R8_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R8_Single_PlanContent2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,08,11,10\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R8_Single_PlanContent_Repeat_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,11,11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_REPEAT));
	}
	
	@Test
	public void testAddOrder_R8_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,10,11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R8_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,10,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R8_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,10,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R8_Danutuo_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"02,07,03,04,05,06,10,11,08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R8_Danutuo_PlanContent2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"1,2,3#4,5,6,7,10,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R8_Danutuo_PlanContent_Length_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06,07,11#10,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_R8_Danutuo_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":18,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":18,\"buyNumber\":9,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06#10,09,07,11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_R8_Danutuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27108,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06#10,09,07,11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任8 end *******************************/
	
	/********************* 前1 start*******************************/
	@Test
	public void testAddOrder_Q1_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"02,07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q1_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Q1_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"02,04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_Q1_Multi_Length_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q1_Multi_Length2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06,10,02,07,11,08,03\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	
	@Test
	public void testAddOrder_Q1_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27109,\"multiple\":1,\"planContent\":\"02,04,06\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前1 end *******************************/
	
	/********************* 前2 start*******************************/
	@Test
	public void testAddOrder_Q2_Multi_Limit_Error() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01,10,09|11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_Q2_Multi_Limit_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01,10,03|09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		assertTrue(ret.isOK());
	}
	
	/********************* 前2  end*******************************/
	@Test
	public void testAddOrder_Q2_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01|04|07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01|04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Q2_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01,02|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_AMOUNT_NOT_EQUAL_ORDER_DETAIL_AMOUNT));
	}
	

	public void testAddOrder_Q2_Multi_Length2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01,02|02|03|04\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27111,\"multiple\":1,\"planContent\":\"01,02|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	/********************* 前3 start*******************************/
	@Test
	public void testAddOrder_Q3_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"02|07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"02|06|08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Q3_Multi_Money_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"02|06,04|08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE));
	}
	
	@Test
	public void testAddOrder_Q3_Multi_Length_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"02|06,04|08|11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q3_Multi_Length2_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"01,02,03,04,05,06,10,02,07,11,08,03|08|01\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_Q3_Multi_Limit_Error() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"01,02,09|10|11\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.HIGH_BET_NUM_LIMIT));
	}
	
	@Test
	public void testAddOrder_Q3_Multi_Limit_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"01,02,03|03|02\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		System.out.println(ret);
		assertTrue(ret.isOK());
	}
	
	
	@Test
	public void testAddOrder_Q3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27113,\"multiple\":1,\"planContent\":\"01,04|01,04|01,04,05,06,07\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前3 end *******************************/
	
	
	/********************* 乐选3 *******************************/
	@Test
	public void testAddOrder_L3_Single_PlanContent_Err() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17061043\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27115,\"multiple\":1,\"planContent\":\"02|07|09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
		assertTrue(ret.getErrorCode().equals(MessageCodeConstants.BET_CONTENT_NOT_MATCH));
	}
	
	@Test
	public void testAddOrder_L3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27115,\"multiple\":1,\"planContent\":\"02|06|08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_L3_mult_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27115,\"multiple\":1,\"planContent\":\"02,03|06|08\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_L4_single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":10,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27116,\"multiple\":1,\"planContent\":\"02,06,08,09\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_L4_mult_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":50,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":50,\"buyNumber\":5,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":27116,\"multiple\":1,\"planContent\":\"02,06,08,07,05\"},{\"amount\":14,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27117,\"multiple\":1,\"planContent\":\"02,06,08,09,10\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_L4_l5_single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":24,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27116,\"multiple\":1,\"planContent\":\"02,06,07,05\"},{\"amount\":14,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":27117,\"multiple\":1,\"planContent\":\"02,06,08,09,10\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_L4_dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":271,\"lotteryIssue\":\"17101343\",\"multipleNum\":1,\"orderAmount\":20,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":20,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":27116,\"multiple\":1,\"planContent\":\"02,06,08#09,10\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	private void ETL(OrderInfoVO vo) {
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setLotteryIssue(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
