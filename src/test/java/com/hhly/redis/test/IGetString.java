package com.hhly.redis.test;

public interface IGetString {
	
	public  String addString(String uid ,String data);
	
	
	public  String getString(String uid);
	
	
	public  boolean  deleteString(String uid);
	
	
	public String  updateString(String uid ,String data);
	

}
