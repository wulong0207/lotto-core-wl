package com.hhly.lottocore.numorder.remote.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

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
import com.hhly.lottocore.remote.numorder.service.impl.OrderAddServiceImpl;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.ChaseEnum.ClientType;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderEnum.PlatformType;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class OrderServiceImpl_Chase_Sdpoker_Test {

	@Autowired
	private OrderAddServiceImpl orderAddService;
	
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
	public void testAddChase_R1_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22501\",\"multiple\": \"1\",\"planContent\": \"A\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R1_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22501\",\"multiple\": \"1\",\"planContent\": \"A,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R1_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22501\",\"multiple\": \"1\",\"planContent\": \"A,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R1_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22501\",\"multiple\": \"1\",\"planContent\": \"K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R1_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22501\",\"multiple\": \"1\",\"planContent\": \"A,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任1 end*******************************/
	
	/********************* 任2 start*******************************/
	@Test
	public void testAddChase_R2_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"A,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R2_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"A,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R2_Dantuo_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"A#2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"A,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22502\",\"multiple\": \"1\",\"planContent\": \"Q#A,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任2 end*******************************/
	
	/********************* 任3 start*******************************/
	@Test
	public void testAddChase_R3_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"A,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R3_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R3_Dantuo_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"A,2#3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"A,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22503\",\"multiple\": \"1\",\"planContent\": \"J,Q#A,k\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任3 end*******************************/
	
	/********************* 任4 start*******************************/
	@Test
	public void testAddChase_R4_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R4_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"20\",\"addCount\": \"5\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"10\",\"buyNumber\": \"5\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R4_Dantuo_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"A,2,3#4,5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"20\",\"addCount\": \"5\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"10\",\"buyNumber\": \"5\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"A,10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22504\",\"multiple\": \"1\",\"planContent\": \"10,J,Q#K,5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任4 end*******************************/
	
	/********************* 任5 start*******************************/
	@Test
	public void testAddChase_R5_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R5_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5,7\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R5_Dantuo_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4#5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"9,10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"A,9,10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22505\",\"multiple\": \"1\",\"planContent\": \"9,10,J,Q#K,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任5 end*******************************/
	
	/********************* 任6 start*******************************/
	@Test
	public void testAddChase_R6_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R6_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5,6,7\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R6_Dantuo_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"A,2,3,4,5#6,7\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"8,9,10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"A,8,9,10,J,Q,K\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22506\",\"multiple\": \"1\",\"planContent\": \"8,9,10,J,Q#K,7\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 任6 end*******************************/
	
	/********************* 同花 start*******************************/
	@Test
	public void testAddChase_Th_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22507\",\"multiple\": \"1\",\"planContent\": \"1T\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Th_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22507\",\"multiple\": \"1\",\"planContent\": \"1T,2T,3T,AT\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Th_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22507\",\"multiple\": \"1\",\"planContent\": \"1T,2T#3T,AT\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Th_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22507\",\"multiple\": \"1\",\"planContent\": \"4T\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Th_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22507\",\"multiple\": \"1\",\"planContent\": \"1T,2T,3T,4T\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 同花 end*******************************/
	
	/********************* 顺子 start*******************************/
	@Test
	public void testAddChase_Sz_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22508\",\"multiple\": \"1\",\"planContent\": \"A23\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Sz_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22508\",\"multiple\": \"1\",\"planContent\": \"A23,234,QKA,XYZ\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sz_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22508\",\"multiple\": \"1\",\"planContent\": \"1T,2T#3T,AT\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sz_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22508\",\"multiple\": \"1\",\"planContent\": \"JQK\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sz_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22508\",\"multiple\": \"1\",\"planContent\": \"A23,234,JQK,XYZ\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 顺子 end*******************************/
	
	/********************* 对子 start*******************************/
	@Test
	public void testAddChase_Dz_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22509\",\"multiple\": \"1\",\"planContent\": \"AA\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Dz_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22509\",\"multiple\": \"1\",\"planContent\": \"33,QQ,1010,XX\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dz_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22509\",\"multiple\": \"1\",\"planContent\": \"1T,2T#3T,AT\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dz_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22509\",\"multiple\": \"1\",\"planContent\": \"KK\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dz_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22509\",\"multiple\": \"1\",\"planContent\": \"KK,QQ,1010,XX\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 对子 end*******************************/
	
	/********************* 豹子 start*******************************/
	@Test
	public void testAddChase_Bz_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22510\",\"multiple\": \"1\",\"planContent\": \"AAA\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Bz_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22510\",\"multiple\": \"1\",\"planContent\": \"AAA,222,QQQ,YYY\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bz_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"22510\",\"multiple\": \"1\",\"planContent\": \"1T,2T#3T,AT\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bz_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"22510\",\"multiple\": \"1\",\"planContent\": \"KKK\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bz_Mulit_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"22510\",\"multiple\": \"1\",\"planContent\": \"AAA,222,KKK,YYY\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	/********************* 豹子 end*******************************/

	private void ETL(OrderAddVO vo) {
		vo.setPlatform(PlatformType.WEB.getValue());
		vo.setSource(ClientType.ANDROID.getValue());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setIssueCode(currIssue.getIssueCode());
		vo.setToken(token);
		vo.setActivityId(null);
	}
}
