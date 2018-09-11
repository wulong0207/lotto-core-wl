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
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class OrderServiceImpl_Order_Sdpoker_Test {

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
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.SDPOKER.getName());
		mapper = new ObjectMapper();
	}
	
	
	/********************* 任1 start*******************************/
	@Test
	public void testAddOrder_R1_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22501,\"multiple\":1,\"planContent\":\"A\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R1_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22501,\"multiple\":1,\"planContent\":\"A,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R1_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22501,\"multiple\":1,\"planContent\":\"A,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R1_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22501,\"multiple\":1,\"planContent\":\"K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R1_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22501,\"multiple\":1,\"planContent\":\"A,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任1 end*******************************/
	
	/********************* 任2 start*******************************/
	@Test
	public void testAddOrder_R2_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"A,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"A,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"A#2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R2_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R2_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":6,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":6,\"buyNumber\":3,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"A,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R2_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22502,\"multiple\":1,\"planContent\":\"Q#A,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任2 end*******************************/
	
	/********************* 任3 start*******************************/
	@Test
	public void testAddOrder_R3_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"A,2,3\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"A,2,3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"A,2#3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R3_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R3_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":8,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":8,\"buyNumber\":4,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"A,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R3_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22503,\"multiple\":1,\"planContent\":\"J,Q#A,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任3 end*******************************/
	
	/********************* 任4 start*******************************/
	@Test
	public void testAddOrder_R4_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"A,2,3,4\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":10,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":5,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"A,2,3,4,5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"A,2,3#4,5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R4_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R4_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":10,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":10,\"buyNumber\":5,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"A,10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R4_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22504,\"multiple\":1,\"planContent\":\"J,Q,K#A,10\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任4 end*******************************/
	
	/********************* 任5 start*******************************/
	@Test
	public void testAddOrder_R5_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"A,2,3,4,5\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"A,2,3,4,5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"A,2,3,4#5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R5_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"9,10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R5_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":12,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":12,\"buyNumber\":6,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"A,9,10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R5_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22505,\"multiple\":1,\"planContent\":\"10,J,Q,K#A,9\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任5 end*******************************/
	
	/********************* 任6 start*******************************/
	@Test
	public void testAddOrder_R6_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"A,2,3,4,5,6\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R6_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":14,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":14,\"buyNumber\":7,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"A,2,3,4,5,6,7\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R6_Dantuo_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"A,2,3,4,5#6,7\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_R6_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"8,9,10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R6_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":14,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":14,\"buyNumber\":7,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"A,8,9,10,J,Q,K\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_R6_Dantuo_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22506,\"multiple\":1,\"planContent\":\"8,9,10,J,Q#K,7\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 任6 end*******************************/
	
	/********************* 同花 start*******************************/
	@Test
	public void testAddOrder_Th_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22507,\"multiple\":1,\"planContent\":\"1T\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Th_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22507,\"multiple\":1,\"planContent\":\"3T,AT\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Th_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22507,\"multiple\":1,\"planContent\":\"A,2\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Th_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22507,\"multiple\":1,\"planContent\":\"4T\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Th_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22507,\"multiple\":1,\"planContent\":\"3T,4T\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 同花 end*******************************/
	
	/********************* 顺子 start*******************************/
	@Test
	public void testAddOrder_Sz_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22508,\"multiple\":1,\"planContent\":\"A23\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Sz_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22508,\"multiple\":1,\"planContent\":\"A23,234\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Sz_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22508,\"multiple\":1,\"planContent\":\"A23,234\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Sz_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22508,\"multiple\":1,\"planContent\":\"JQK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Sz_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22508,\"multiple\":1,\"planContent\":\"A23,JQK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 顺子 end*******************************/
	
	/********************* 对子 start*******************************/
	@Test
	public void testAddOrder_Dz_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22509,\"multiple\":1,\"planContent\":\"AA\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Dz_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22509,\"multiple\":1,\"planContent\":\"AA,22\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Dz_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22509,\"multiple\":1,\"planContent\":\"A23,234\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Dz_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22509,\"multiple\":1,\"planContent\":\"KK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Dz_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22509,\"multiple\":1,\"planContent\":\"AA,KK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 对子 end*******************************/
	
	/********************* 豹子 start*******************************/
	@Test
	public void testAddOrder_Bz_Single_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22510,\"multiple\":1,\"planContent\":\"AAA\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Bz_Multi_Success() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22510,\"multiple\":1,\"planContent\":\"AAA,222\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddOrder_Bz_Dantuo_Fail() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":3,\"lotteryChildCode\":22510,\"multiple\":1,\"planContent\":\"A23,234\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Bz_Single_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":2,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":2,\"buyNumber\":1,\"codeWay\":2,\"contentType\":1,\"lotteryChildCode\":22510,\"multiple\":1,\"planContent\":\"KKK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddOrder_Bz_Multi_Limit() throws Exception {
		String jsonInString = 	
				"{\"buyScreen\":\"\",\"buyType\":1,\"channelId\":0,\"isDltAdd\":0,\"lotteryCode\":107,\"lotteryIssue\":\"17061539\",\"multipleNum\":1,\"orderAmount\":4,\"tabType\":\"\",\"orderDetailList\":[{\"amount\":4,\"buyNumber\":2,\"codeWay\":2,\"contentType\":2,\"lotteryChildCode\":22510,\"multiple\":1,\"planContent\":\"AAA,KKK\"}],\"platform\":1,\"token\":\"07c5cc7c93624f58bd91693ae89d11b5\"}";
		OrderInfoVO vo = mapper.readValue(jsonInString, OrderInfoVO.class);
		ETL(vo);
		ResultBO<?> ret = orderService.addOrder(vo);
		assertTrue(ret.isError());
	}
	/********************* 豹子 end*******************************/

	private void ETL(OrderInfoVO vo) {
		vo.setLotteryIssue(currIssue.getIssueCode());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setToken(token);
	}
}
