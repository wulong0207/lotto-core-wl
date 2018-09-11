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
public class OrderServiceImpl_Chase_Jsk3_Test {

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
		Set<String> tokens = strRedisTemplate.keys("c_core_member_info*");
		token = tokens.iterator().next().replace("c_core_member_info", "");
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.JSK3.getName());
		mapper = new ObjectMapper();
	}
	
//	/**和值*/
//	JSK3_S(23301,"和值"),
//	/**二同号单选*/
//	JSK3_TD2(23302,"二同号单选"),
//	/**二同号复选*/
//	JSK3_TF2(23303,"二同号复选"),
//	/**二不同号*/
//	JSK3_BT2(23304,"二不同号"),
//	/**三不同号*/
//	JSK3_BT3(23305,"三不同号"),
//	/**三同号单选*/
//	JSK3_TD3(23306,"三同号单选"),
//	/**三同号通选*/
//	JSK3_TT3(23307,"三同号通选"),
//	/**三连号通选*/
//	JSK3_L3(23308,"三连号通选"),
	
	/********************* 和值 start*******************************/
	@Test
	public void testAddChase_Sum_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23301\",\"multiple\": \"1\",\"planContent\": \"17\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChaseWithOutVerify(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sum_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23301\",\"multiple\": \"1\",\"planContent\": \"18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChaseWithOutVerify(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Sum_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23301\",\"multiple\": \"1\",\"planContent\": \"16,17\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sum_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23301\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Sum_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23301\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 和值 end*********************************/
	
	/********************* 三同号通选 start*******************************/
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tt3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23307\",\"multiple\": \"1\",\"planContent\": \"3T\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tt3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23307\",\"multiple\": \"1\",\"planContent\": \"3T\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tt3_Multi_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23307\",\"multiple\": \"1\",\"planContent\": \"16,17\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tt3_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23307\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tt3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23307\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三同号通选 end*********************************/
	
	/********************* 三同号单选 start*******************************/
	@Test
	public void testAddChase_Td3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"555\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"666\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Td3_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"111,222\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td3_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"666,222\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td3_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23306\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三同号单选 end*********************************/
	
	/********************* 三不同号 start*******************************/
	@Test
	public void testAddChase_Bt3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"1,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"4,5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Bt3_Multi_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"1,4,5,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt3_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"1,4,5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Bt3_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"1,2#3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23305\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三不同号 end*********************************/
	
	/********************* 三连号通选 start*******************************/
	@Test(expected=ResultJsonException.class)
	public void testAddChase_L3_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23308\",\"multiple\": \"1\",\"planContent\": \"3L\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_L3_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23308\",\"multiple\": \"1\",\"planContent\": \"3L\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_L3_Multi_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23308\",\"multiple\": \"1\",\"planContent\": \"1,4,5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_L3_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23308\",\"multiple\": \"1\",\"planContent\": \"1,2#3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_L3_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23308\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 三连号通选 end*********************************/
	
	/********************* 二同号复选 start*******************************/
	@Test
	public void testAddChase_Tf2_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"11*\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tf2_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"66*\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Tf2_Multi() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"11*,22*,33*,44*\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tf2_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"11*,66*,33*,44*\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tf2_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"1,2#3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Tf2_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23303\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二同号复选 end*********************************/
	
	/********************* 二同号单选 start*******************************/
	@Test
	public void testAddChase_Td2_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"11#2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test
	public void testAddChase_Td2_Single_Success2() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"66#5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td2_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"55#6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Td2_Multi() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"11,44#2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Td2_Multi2() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"11,66#5,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Td2_Multi3() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"11,22,33#1,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td2_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"11,55#2,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td2_Dantuo_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"1,2#3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Td2_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23302\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二同号单选 end*********************************/
	
	/********************* 二不同号 start*******************************/
	@Test
	public void testAddChase_Bt2_Single_Success() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"1,2\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		System.out.println(ret);
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt2_Single_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"5,6\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Bt2_Multi() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"1,2,3\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt2_Multi_Limit() throws Exception {
		String jsonInString = "{\"addAmount\": \"16\",\"addCount\": \"4\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"8\",\"buyNumber\": \"4\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"6,2,5\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test
	public void testAddChase_Bt2_Dantuo() throws Exception {
		String jsonInString = "{\"addAmount\": \"12\",\"addCount\": \"3\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"6\",\"buyNumber\": \"3\",\"codeWay\": \"1\",\"contentType\": \"3\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"1#2,3,4\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Bt2_Sum_Fail() throws Exception {
		String jsonInString = "{\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"6\",\"lotteryChildCode\": \"23304\",\"multiple\": \"1\",\"planContent\": \"16,18\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	/********************* 二不同号 end*********************************/

	private void ETL(OrderAddVO vo) {
		vo.setPlatform(PlatformType.WEB.getValue());
		vo.setSource(ClientType.ANDROID.getValue());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setIssueCode(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
