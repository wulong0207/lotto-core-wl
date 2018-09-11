package com.hhly.test;

import java.io.Serializable;

public class RedisObjTest  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String name ;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public RedisObjTest(String name, int age) {
		super();
		this.name = name;
		this.age = age;
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

	private int age ;

} 
