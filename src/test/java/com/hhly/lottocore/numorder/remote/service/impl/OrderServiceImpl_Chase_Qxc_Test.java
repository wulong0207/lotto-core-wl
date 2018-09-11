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
public class OrderServiceImpl_Chase_Qxc_Test {

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
		currIssue = baseValidateService.findCurIssue(LotteryEnum.Lottery.QXC.getName());
		mapper = new ObjectMapper();
	}
	
	@Test
	public void testAddChase_Single_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"10701\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|5|6|7\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Single_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"4\",\"addCount\": \"1\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"2\",\"buyNumber\": \"1\",\"codeWay\": \"1\",\"contentType\": \"1\",\"lotteryChildCode\": \"10701\",\"multiple\": \"1\",\"planContent\": \"9|9|9|9|9|9|9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isError());
	}
	
	@Test
	public void testAddChase_Muliti_Success() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"10701\",\"multiple\": \"1\",\"planContent\": \"1|2|3|4|5|6|7,8\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isOK());
	}
	
	@Test(expected=ResultJsonException.class)
	public void testAddChase_Muliti_Limit() throws Exception {
		String jsonInString = "{\"activityId\": \"123456\",\"addAmount\": \"8\",\"addCount\": \"2\",\"addIssues\": \"2\",\"addMultiples\": \"1\",\"addType\": \"1\",\"channelId\": \"android\",\"isDltAdd\": \"0\",\"issueCode\": \"17061313\",\"lotteryCode\": \"215\",\"multipleNum\": \"2\",\"orderAddContentList\": [{\"amount\": \"4\",\"buyNumber\": \"2\",\"codeWay\": \"1\",\"contentType\": \"2\",\"lotteryChildCode\": \"10701\",\"multiple\": \"1\",\"planContent\": \"9|9|9|9|9|9|8,9\"}],\"token\": \"06badcfde0dc4f0a8a15663d83c4a263\",\"stopCondition\": \"\",\"stopType\": \"3\"}";
		OrderAddVO vo = mapper.readValue(jsonInString, OrderAddVO.class);
		ETL(vo);
		ResultBO<?> ret = orderAddService.addChase(vo);
		assertTrue(ret.isError());
	}

	private void ETL(OrderAddVO vo) {
		vo.setPlatform(PlatformType.WEB.getValue());
		vo.setSource(ClientType.ANDROID.getValue());
		vo.setLotteryCode(currIssue.getLotteryCode());
		vo.setIssueCode(currIssue.getIssueCode());
		vo.setToken(token);
	}
}
