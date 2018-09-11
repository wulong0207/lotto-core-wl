//package com.hhly.redis.test;
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
//public class GetString implements IGetString {
//	
//	private Logger log = LoggerFactory.getLogger(GetString.class);
//	
//	@UpdateThroughAssignCache(namespace="GETSTRING", cacheType=RedisCacheType.String,valueclass=String.class)
//	@Override
//	public String addString(@ParameterValueKeyProvider String uid ,String data) {
//		log.info("Test===>addString");
//		String say  = data ;
//		return say ;
//	}
//
//	@ReadThroughAssignCache(namespace="GETSTRING",cacheType=RedisCacheType.String,valueclass=String.class,expireTime=0)
//	@Override
//	public String getString(@ParameterValueKeyProvider String uid) {
//		log.info("Test===>getString");
//		String say  = "Test==>getString" ;
//		return say ;
//	}
//    
//	
//	@DeleteThroughAssignCache(namespace="GETSTRING",cacheType=RedisCacheType.String)
//	@Override
//	public boolean deleteString(@ParameterValueKeyProvider String uid) {
//		log.info("Test===>deleteString");
//		boolean say  = false ;
//		return say ;
//	}
//
//	
//	@UpdateThroughAssignCache(namespace="GETSTRING",cacheType=RedisCacheType.String,valueclass=String.class)
//	@Override
//	public String updateString(@ParameterValueKeyProvider String uid, String data) {
//		log.info("Test===>updateData");
//		String say  = data ;
//		return say ;
//	}
//
//}
