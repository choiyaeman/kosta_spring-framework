package com.my.vo;

import java.io.Serializable;

public class Person implements Serializable{
	protected String name;
	//매개변수없는 생성자
	public Person() {
		super();
	}
	
	//name을 초기화하는 생성자
	public Person(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + "]";
	}
	
}
