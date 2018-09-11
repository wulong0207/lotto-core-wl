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
public class OrderServiceImpl_Chase_Xj11x5_Test {

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
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.XJ11X5.getName());
		mapper = new ObjectMapper();
	}
	
	/********************* 任2  start*******************************/
	@Test
	public void testAddChase_R2_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"01,02\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R2_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R2_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"01#04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"01,10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R2_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27302\",\"multiple\": \"1\",\"planContent\": \"11#10,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任2 end*******************************/
	
	/********************* 任3  start*******************************/
	@Test
	public void testAddChase_R3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"01,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R3_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R3_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"01#04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"01,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R3_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27303\",\"multiple\": \"1\",\"planContent\": \"11#10,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任3 end*******************************/
	
	/********************* 任4  start*******************************/
	@Test
	public void testAddChase_R4_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R4_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"20\",\"addCount\": \"5\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"10\",\"buyNumber\": \"5\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R4_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"08,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"20\",\"addCount\": \"5\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"10\",\"buyNumber\": \"5\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"08,01,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R4_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27304\",\"multiple\": \"1\",\"planContent\": \"11#10,09,08\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任4 end*******************************/
	
	/********************* 任5  start*******************************/
	@Test
	public void testAddChase_R5_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R5_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R5_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"07,08,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"01,08,07,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R5_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27305\",\"multiple\": \"1\",\"planContent\": \"11#10,09,08,07\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任5 end*******************************/
	
	/********************* 任6  start*******************************/
	@Test
	public void testAddChase_R6_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R6_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R6_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"06,07,08,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"01,06,08,07,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R6_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27306\",\"multiple\": \"1\",\"planContent\": \"11#10,09,08,07,06\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任6 end*******************************/
	
	/********************* 任7  start*******************************/
	@Test
	public void testAddChase_R7_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R7_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"32\",\"addCount\": \"8\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"16\",\"buyNumber\": \"8\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07,08\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R7_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R7_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R7_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"05,06,07,08,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R7_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"32\",\"addCount\": \"8\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"16\",\"buyNumber\": \"8\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"05,01,06,08,07,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R7_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27307\",\"multiple\": \"1\",\"planContent\": \"11#10,09,08,07,06,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任7 end*******************************/
	
	/********************* 任8  start*******************************/
	@Test
	public void testAddChase_R8_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07,08\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R8_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"36\",\"addCount\": \"9\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"18\",\"buyNumber\": \"9\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_R8_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R8_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R8_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"04,05,06,07,08,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R8_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"36\",\"addCount\": \"9\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"18\",\"buyNumber\": \"9\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"04,05,01,06,08,07,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_R8_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27308\",\"multiple\": \"1\",\"planContent\": \"11#10,09,08,07,06,05,04\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 任8 end*******************************/
	
	/********************* 前一  start*******************************/
	@Test
	public void testAddChase_Q1_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"01\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q1_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"36\",\"addCount\": \"9\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"18\",\"buyNumber\": \"9\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q1_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q1_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q1_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q1_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"36\",\"addCount\": \"9\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"18\",\"buyNumber\": \"9\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27309\",\"multiple\": \"1\",\"planContent\": \"04,05,01,06,08,07,10,11,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前一 end*******************************/
	
	/********************* 前二组选  start*******************************/
	@Test
	public void testAddChase_Q2z_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"01,02\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q2z_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"40\",\"addCount\": \"10\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"20\",\"buyNumber\": \"10\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q2z_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2z_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2z_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2z_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"40\",\"addCount\": \"10\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"20\",\"buyNumber\": \"10\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"01,02,03,10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2z_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27310\",\"multiple\": \"1\",\"planContent\": \"10#04,05,06,07,08,02,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前二组选 end*******************************/
	
	/********************* 前二直选  start*******************************/
	@Test
	public void testAddChase_Q2D_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"01|02\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q2D_Single_Success2() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"11|10\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q2D_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"01,02|03,01\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q2D_Multi_Success2() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"11,02|02,10\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2D_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2D_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2D_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"10|11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q2D_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27311\",\"multiple\": \"1\",\"planContent\": \"01,10|01,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前二直选 end*******************************/
	
	/********************* 前三组选  start*******************************/
	@Test
	public void testAddChase_Q3z_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"01,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3z_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"40\",\"addCount\": \"10\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"20\",\"buyNumber\": \"10\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"01,02,03,04,05\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3z_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"01,09#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3z_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3z_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"09,10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3z_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"40\",\"addCount\": \"10\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"20\",\"buyNumber\": \"10\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"01,02,03,10,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3z_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27312\",\"multiple\": \"1\",\"planContent\": \"10,09#04,05,06,07,08,02,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前三组选 end*******************************/
	
	/********************* 前三直选  start*******************************/
	@Test
	public void testAddChase_Q3D_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"01|02|03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3D_Single_Success2() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"11|10|09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3D_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"01,02,03|03,01|01,02\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Q3D_Multi_Success2() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"01,02,11|11,10|10,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3D_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"28\",\"addCount\": \"7\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"14\",\"buyNumber\": \"7\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"01#04,05,06,07,08,02,03\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3D_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"01,02,03#04,05,06,07,08,09\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3D_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"09|10|11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Q3D_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"273\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"27313\",\"multiple\": \"1\",\"planContent\": \"09|01,10|01,11\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 前三直选 end*******************************/
	
	private void ETL(OrderAddVO vo) {
		vo.setPlatform(PlatformType.WEB.getValue());
		vo.setSource(ClientType.ANDROID.getValue());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setIssueCode(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
