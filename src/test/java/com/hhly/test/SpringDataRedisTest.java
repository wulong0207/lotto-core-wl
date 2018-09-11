package com.hhly.test;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SpringDataRedisTest extends AbstractJUnit4SpringContextTests  {
	
	
	/**
	 * 字符串缓存模板
	 */
	@Autowired
	private StringRedisTemplate strRedisTemplate;

	/**
	 * 对象，集合缓存模板
	 */
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	
	@Test
	public void  asetString(){
		// 字符串redis 存储
		ValueOperations<String, String> valOps = strRedisTemplate.opsForValue();
		valOps.set("scott2", "scott2");
	}
	
	
	
	@Test
	public void bgetString() {
		// 字符串redis 存储
		System.err.println(strRedisTemplate.opsForValue().get("scott2"));
	}
	
	
	@Test
	public void cdelString() {
		// 字符串redis 存储
		strRedisTemplate.delete("scott2");
	}
	
	
	@Test
	public void  dsetObj(){
		// 对象redis 存储
		ValueOperations<String, Object> objOps = redisTemplate.opsForValue();
		RedisObjTest rot = new RedisObjTest("scott4", 10);
		objOps.set("scott3",rot);
	}
	
	
	@Test
	public void egetObj() {
		// 对象redis 存储
		RedisObjTest rt = (RedisObjTest) redisTemplate.opsForValue().get("scott3");
		System.err.println(rt.getName());
		System.err.println(rt.getAge());
	}
	
	
	@Test
	public void fdelObj() {
		// 对象redis 存储
		redisTemplate.delete("scott3");
	}
	
	
}
