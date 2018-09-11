package com.hhly.redis.test;

public class TestData {
	
	
	private int uid ;
	
	private String name ;
	
	
	private int age ;


	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}


	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}


	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}


	public TestData(int uid, String name, int age) {
		super();
		this.uid = uid;
		this.name = name;
		this.age = age;
	}
	
	
	
	
	
	
	
	
	

}
