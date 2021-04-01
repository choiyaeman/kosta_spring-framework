package com.my.vo;

public class Postal {
	private String zipcode;
	private String buildingno; //건물관리번호
	private String city; //시군구읍면
	private String doro; //도로명 건물본번-부번
	private String building;
	public Postal() {
		super();
	}
	public Postal(String zipcode, String buildingno, String city, String doro, String building) {
		super();
		this.zipcode = zipcode;
		this.buildingno = buildingno;
		this.city = city;
		this.doro = doro;
		this.building = building;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getBuildingno() {
		return buildingno;
	}
	public void setBuildingno(String buildingno) {
		this.buildingno = buildingno;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDoro() {
		return doro;
	}
	public void setDoro(String doro) {
		this.doro = doro;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
}
