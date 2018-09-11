//package com.hhly.redis.test;
//
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.hhly.redis.annotation.DeleteThroughAssignCache;
//import com.hhly.redis.annotation.ParameterValueKeyProvider;
//import com.hhly.redis.annotation.ReadThroughAssignCache;
//import com.hhly.redis.annotation.RedisCacheType;
//import com.hhly.redis.annotation.UpdateThroughAssignCache;
//
//@Service
//public class GetData implements IGetData {
//	
//	private Logger log = LoggerFactory.getLogger(GetData.class);
//	
//	public static Map<String, TestData> init = null ;
//     
//	@UpdateThroughAssignCache(namespace="GETDATA", cacheType=RedisCacheType.String,valueclass=TestData.class)
//	@Override
//	public TestData addData(@ParameterValueKeyProvider String uid ,TestData testData) {
//		log.info("Test===>addData");
//		init.put(uid, testData);
//		return testData ;
//	}
//
//	@ReadThroughAssignCache(namespace="GETDATA",cacheType=RedisCacheType.String,valueclass=TestData.class,expireTime=0)
//	@Override
//	public TestData getData(@ParameterValueKeyProvider String uid) {
//		log.info("Test===>getData");
//		return init.get(uid);
//	}
//    
//	
//	@DeleteThroughAssignCache(namespace="GETDATA",cacheType=RedisCacheType.String)
//	@Override
//	public boolean deleteData(@ParameterValueKeyProvider String uid) {
//		log.info("Test===>deleteData");
//		init.remove(uid);
//		return true;
//	}
//
//	
//	@UpdateThroughAssignCache(namespace="GETDATA",cacheType=RedisCacheType.String,valueclass=TestData.class)
//	@Override
//	public TestData updateData(@ParameterValueKeyProvider String uid, TestData testData) {
//		log.info("Test===>updateData");
//		init.put(uid, testData);
//		return testData;
//	}
//
//}
