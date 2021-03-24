package com.my.vo;

public class Customer extends Person{	
	private String id;
	transient private String pwd;
	private Postal postal;
	private String addr1;
	public Customer() {
		super();
	}
	public Customer(String name) {
		super(name);
	}
	public Customer(String id, String pwd, Postal postal) {		
		this(id, pwd, null, postal, null);
	}
	public Customer(String id, String pwd, String name, Postal postal, String addr1) {
		super();
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.postal = postal;
		this.addr1 = addr1;
	}
	
	public Customer(String id, String pwd, String name) {
		this(id,pwd,name,null, null);
	}
	public Customer(String id, String pwd) {
		this(id,pwd,null,null, null);
	}
	
	
	public Customer(String id, String pwd, Postal postal, String addr1) {
		super();
		this.id = id;
		this.pwd = pwd;
		this.postal = postal;
		this.addr1 = addr1;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", postal=" + postal + ", addr1=" + addr1 + "]";
	}
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Postal getPostal() {
		return postal;
	}
	public void setPostal(Postal postal) {
		this.postal = postal;
	}
	
	
	
	
}
