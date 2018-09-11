package com.hhly.redis.test;

public interface IGetData {
	
	public  TestData addData(String uid ,TestData testData);
	
	
	public  TestData getData(String uid);
	
	
	public  boolean  deleteData(String uid);
	
	
	public TestData  updateData(String uid ,TestData testData);
	

}
