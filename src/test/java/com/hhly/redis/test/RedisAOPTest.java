//package com.hhly.redis.test;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.hhly.lottocore.base.util.UserUtil;
//import com.hhly.skeleton.base.util.TokenUtil;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.hhly.lottocore.persistence.issue.dao.LotteryIssueDaoMapper;
//import com.hhly.lottocore.persistence.lottery.dao.LotteryBettingMulDaoMapper;
//import com.hhly.lottocore.persistence.lottery.dao.LotteryChildDaoMapper;
//import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
//import com.hhly.lottocore.base.util.RedisUtil;
//import com.hhly.skeleton.base.constants.CacheConstants;
//import com.hhly.skeleton.base.util.ObjectUtil;
//import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
//import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
//import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
//import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
//import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
//import com.hhly.skeleton.user.bo.UserInfoBO;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@RunWith(SpringJUnit4ClassRunner.class) 
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//public class RedisAOPTest extends AbstractJUnit4SpringContextTests {
//	
//	@Autowired
//	public IGetData getData ;
//	
//	@Autowired
//	public IGetString getString ;
//	
//	@Autowired
//	public RedisUtil redisUtil;
//
//	@Autowired
//	private UserUtil userUtil;
//	
//	@Autowired
//	public LotteryTypeDaoMapper lotteryTypeDaoMapper;
//	
//	@Autowired
//	public LotteryChildDaoMapper lotteryChildDaoMapper;
//	
//	@Autowired
//	private LotteryIssueDaoMapper lotteryIssueDaoMapper;
//	
//	@Autowired
//	private LotteryBettingMulDaoMapper  lotteryBettingMulDaoMapper ;
//	
//	@Test
//	public void aopStringTest(){
//		System.err.println(getString.addString("1", "scott1"));
//		System.err.println(getString.updateString("1", "scott2"));
//		System.err.println(getString.getString("1"));
//		System.err.println(getString.deleteString("1"));
//	}
//	
//	
//	
//	@Test
//	public void aopTest(){
//		Map<String, TestData> maps = new HashMap<String, TestData>();
//		// 测试数据
//		maps.put("1", new TestData(1, "scott1", 15));
//		maps.put("2", new TestData(2, "scott2", 16));
//		maps.put("3", new TestData(3, "scott3", 17));
//		maps.put("4", new TestData(4, "scott4", 18));
//		GetData.init = maps ;
//		
//		//test-add 测试新增对象  测试缓存类型为String 
//		TestData test_add = new TestData(6,"scott6",60);
//		getData.addData("6",test_add);
//		
//		//test-get 测试从缓存读取数据
//		System.err.println(getData.getData("6").getName());
//		
//		
//		//test-update 测试修改数据
//		TestData test_update = new TestData(6,"scott_upate",600);
//		getData.updateData("6", test_update);
//		
//		
//		//test-delete 测试删除数据
//		getData.deleteData("6");
//		
//	}
//	
//	@Test//彩种加缓存
//	public void lotteryTypeTest(){
////		long begin = System.currentTimeMillis();
////		redisUtil.delObj(CacheConstants.LOTTERY_TYPE_CHILD_MAP);
////		Map<Integer, LotteryBO> getListLotChild = redisUtil.getObj(CacheConstants.LOTTERY_TYPE_CHILD_MAP,new HashMap<Integer, LotteryBO>());
////		if(ObjectUtil.isBlank(getListLotChild)){
////			LotteryVO lotteryVO = new LotteryVO();
////			//大彩种:小玩法集合
////			Map<Integer, LotteryBO> mapLottery = new HashMap<Integer, LotteryBO>();
////			//所有大彩种
////			List<LotteryBO> listLotteryBO = lotteryTypeDaoMapper.findMultipleFront(lotteryVO);
////			//所有子玩法
////			List<LotChildBO> listLotChild = null;
////			for(LotteryBO lotteryBO : listLotteryBO){
////				listLotChild = lotteryChildDaoMapper.findMultipleFront(new LotteryVO(lotteryBO.getLotteryCode()));
////				for(LotChildBO childBO : listLotChild){
////					childBO.setLotteryName(lotteryBO.getLotteryName());
////					childBO.setLevel((short)1);
////					childBO.setLotteryCode(childBO.getLotteryChildCode());
////				}
////				lotteryBO.setListLotChildBO(listLotChild);
////				mapLottery.put(lotteryBO.getLotteryCode(), lotteryBO);
////			}
////			redisUtil.addObj(CacheConstants.LOTTERY_TYPE_CHILD_MAP, mapLottery, null);
//			
////		}
////		getListLotChild = redisUtil.getObj(CacheConstants.LOTTERY_TYPE_CHILD_MAP,new HashMap<Integer, LotteryBO>());
////	    if(!ObjectUtil.isBlank(getListLotChild)){
////	    	for(Map.Entry<Integer, LotteryBO> entry : getListLotChild.entrySet()){
////	    		System.out.println("key:"+entry.getKey());
////	    		if(300 == entry.getKey()){
////	    		for(LotChildBO bo : entry.getValue().getListLotChildBO()){
////	    			System.out.println(bo.getLotteryCode()+":"+bo.getLotteryName()+":"+bo.getLotteryChildCode()+":"+bo.getChildName()+":"+bo.getLevel());
////	    		}
////	    			for(LotChildBO bo : entry.getValue()){
////	    			}
////	    			break;
////	    		}
////	    	}
////	    }
////	    System.out.println("耗时："+ (System.currentTimeMillis() - begin)/1000 + "秒");
//	}
//	@Test//彩期加缓存
//	public void lotteryIssueTest(){
////		long begin = System.currentTimeMillis();
////		redisUtil.delObj(CacheConstants.TICKET_CONFIG_MONITOR);
////		List<IssueBO> issueBOList = null;
//////		List<LotBettingMulBO> betMulBO = lotteryBettingMulDaoMapper.findMultipleFront(new LotteryVO(orderInfoVO.getLotteryCode()));
////		issueBOList = redisUtil.getObj(CacheConstants.TICKET_CONFIG_MONITOR, new ArrayList<IssueBO>());
////		//所有彩期
////		if(ObjectUtil.isBlank(issueBOList)){
////			issueBOList = lotteryIssueDaoMapper.findMultipleFront(new LotteryVO());
////			redisUtil.addObj(CacheConstants.TICKET_CONFIG_MONITOR, issueBOList, null);
////		}
////		issueBOList = redisUtil.getObj(CacheConstants.TICKET_CONFIG_MONITOR, new ArrayList<IssueBO>());
////		for(IssueBO issueBO : issueBOList){
////			System.out.println(issueBO.getIssueCode()+":"+ issueBO.getSaleStatus());
////		}
////		System.out.println("耗时："+ (System.currentTimeMillis() - begin)/1000 + "秒");
//	}
//	
//	@Test//投注注数，倍数截止时间信息表加缓存
//	public void lotteryBettingMultest(){
////		long begin = System.currentTimeMillis();
////		redisUtil.delObj(CacheConstants.LOTTERY_BETTING_MUL_LIST);
////		List<LotBettingMulBO> LotBettingMulBOList = null;
////	    LotBettingMulBOList = redisUtil.getObj(CacheConstants.LOTTERY_BETTING_MUL_LIST, new ArrayList<LotBettingMulBO>());
////		//所有彩期
////		if(ObjectUtil.isBlank(LotBettingMulBOList)){
////			LotBettingMulBOList = lotteryBettingMulDaoMapper.findMultipleFront(new LotteryVO());
////			redisUtil.addObj(CacheConstants.LOTTERY_BETTING_MUL_LIST, LotBettingMulBOList, null);
////		}
////		LotBettingMulBOList = redisUtil.getObj(CacheConstants.LOTTERY_BETTING_MUL_LIST, new ArrayList<LotBettingMulBO>());
////		for(LotBettingMulBO lotBettingMulBO : LotBettingMulBOList){
////			System.out.println(lotBettingMulBO.getLotteryCode()+":"+ lotBettingMulBO.getBettindNum()+":"+lotBettingMulBO.getMultipleNum());
////		}
////		System.out.println("耗时："+ (System.currentTimeMillis() - begin)/1000 + "秒");
//	}
//	@Test
//	public void getUserInfo(){
//		/*UserInfoBO userInfoBO = redisUtil.getObj("TOKEN_MEMBER_USER_C_33a65f63ccdf4ab29589ab3328879d81", new UserInfoBO());
//		System.out.println(userInfoBO);*/
//		UserInfoBO userInfoBO  = userUtil.getUserByToken("483113f91dcf422092b7b560c0e1a3e4");
//		System.out.println(userInfoBO);
//	}
//
//
//
//	@Test
//	public void setUserInfo(){
//		UserInfoBO userInfoBO = new UserInfoBO();
//		userInfoBO.setId(52);
//		userInfoBO.setNickname("bb");
//
//		String token = TokenUtil.createTokenStr();
//
//		userUtil.addUserCacheByToken(token,userInfoBO,CacheConstants.ONE_WEEK);
//	}
//
//}
