package com.my.vo;

public class OrderLine {
	private int order_no;
	private Product p;
	private int order_quantity;
	public OrderLine() {
		super();
	}
	
	public OrderLine(int order_no, Product p, int order_quantity) {
		super();
		this.order_no = order_no;
		this.p = p;
		this.order_quantity = order_quantity;
	}
	
	@Override
	public String toString() {
		return "OrderLine [order_no=" + order_no + ", p=" + p + ", order_quantity=" + order_quantity + "]";
	}
	
	public int getOrder_no() {
		return order_no;
	}
	public void setOrder_no(int order_no) {
		this.order_no = order_no;
	}
	public Product getP() {
		return p;
	}
	public void setP(Product p) {
		this.p = p;
	}
	public int getOrder_quantity() {
		return order_quantity;
	}
	public void setOrder_quantity(int order_quantity) {
		this.order_quantity = order_quantity;
	}
	
	
}
