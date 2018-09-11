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
public class OrderServiceImpl_Chase_Cqssc_Test {

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
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.CQSSC.getName());
		mapper = new ObjectMapper();
	}
	
	// 对接PC前端
	@Test
	public void testAddChase_3z3_Single_Success22() throws Exception {
		String jsonInString = 
				
				"{\"orderAddContentList\":[{\"planContent\":\"9,7,7\",\"multiple\":\"1\",\"lotteryChildCode\":\"20104\",\"contentType\":1,\"codeWay\":1,\"buyNumber\":1,\"amount\":2}],\"addAmount\":20,\"addCount\":10,\"multipleNum\":10,\"orderAddIssueList\":[{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814057\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814058\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814059\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814060\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814061\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814062\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814063\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814064\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814065\",\"multiple\":1},{\"buyAmount\":2,\"chaseBetNum\":1,\"issueCode\":\"20170814066\",\"multiple\":1}],\"activityId\":\"\",\"addType\":\"1\",\"stopCondition\":\"\",\"channelId\":\"2\",\"isDltAdd\":\"0\",\"lotteryCode\":\"201\",\"platform\":\"1\",\"issueCode\":\"20170814057\",\"token\":\"48342ed69f7d44349eddf4847a859c74\",\"stopType\":\"3\"}";		
		
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		vo.setSource(ClientType.PC.getValue());
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	/********************* 五星直选  start*******************************/
	@Test
	public void testAddChase_5_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_5_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20101\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|5,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 五星直选  end*******************************/
	
	/********************* 五星通选  start*******************************/
	@Test
	public void testAddChase_5T_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_5T_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5T_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5T_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5T_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_5T_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20102\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|5,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 五星通选  end*******************************/
	
	/********************* 三星直选  start*******************************/
	@Test
	public void testAddChase_3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"1|2|3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"1,2|2,3,4|3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3_Sum() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"27\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3_Sum_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"24\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"7|8|9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"24\",\"addCount\": \"6\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"12\",\"buyNumber\": \"6\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20103\",\"multiple\": \"1\",\"planContent\": \"1,7|2,8,4|9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三星直选  end*******************************/
	
	/********************* 三星组三  start*******************************/
	@Test
	public void testAddChase_3z3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"0,1,1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3z3_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"0,1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3z3_Dantuo_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"0#1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"1,2,3,4|2|3,4|4|6,7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"9,8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z3_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"9,8\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z3_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20104\",\"multiple\": \"1\",\"planContent\": \"9#8\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三星组三  end*******************************/
	
	/********************* 三星组六  start*******************************/
	@Test
	public void testAddChase_3z6_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"0,1,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3z6_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"0,1,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_3z6_Dantuo_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"0,1#2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z6_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z6_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"7,8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z6_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"6,7,8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_3z6_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20105\",\"multiple\": \"1\",\"planContent\": \"7,8#9,1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三星组六  end*******************************/
	
	/********************* 二星直选  start*******************************/
	@Test
	public void testAddChase_2_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"1|1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_2_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"1,2|1,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_2_Sum() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"1,12,13,14,15\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"0,1#2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"8|9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"6,7,8|1,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2_Sum_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20106\",\"multiple\": \"1\",\"planContent\": \"8,17\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二星直选  end*******************************/
	
	/********************* 二星组选  start*******************************/
	@Test
	public void testAddChase_2z_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"1,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_2z_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"1,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_2z_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"1#3,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_2z_Sum() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"0,8,9,10,11,12\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2z_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2z_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"7,8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2z_Dantuo_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"8#7,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_2z_Sum_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20107\",\"multiple\": \"1\",\"planContent\": \"8,17\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二星组选  end*******************************/
	
	/********************* 一星  start*******************************/
	@Test
	public void testAddChase_1_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"0\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_1_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"1,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_1_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"1#3,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_1_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"0,8,9,10,11,12\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_1_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_1_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20108\",\"multiple\": \"1\",\"planContent\": \"8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 一星  end*******************************/
	
	/********************* 大小单双 start*******************************/
	@Test
	public void testAddChase_Dxds_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"1|1\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	// 大小单双改为不支持复式
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dxds_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"1|2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dxds_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"1#3,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dxds_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"96\",\"addCount\": \"24\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"48\",\"buyNumber\": \"24\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"0,8,9,10,11,12\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dxds_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"3|4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Dxds_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"20109\",\"multiple\": \"1\",\"planContent\": \"3|4,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 大小单双  end*******************************/
	
	private void ETL(OrderAddVO vo) {
		vo.setPlatform(PlatformType.WEB.getValue());
		vo.setSource(ClientType.ANDROID.getValue());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setIssueCode(currIssue.getIssueCode());
		vo.setToken(token);
		vo.setActivityId(null);
	}
}
