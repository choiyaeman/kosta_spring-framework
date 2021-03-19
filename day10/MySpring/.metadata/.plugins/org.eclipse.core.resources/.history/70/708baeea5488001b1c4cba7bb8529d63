package com.my.vo;

public class OrderLine {
/*
 * ORDER_NO                                  NOT NULL NUMBER
 ORDER_PROD_NO                             NOT NULL VARCHAR2(5)
 ORDER_QUANTITY                            NOT NULL NUMBER(2)
 */
	private int order_no;
	//private String order_prod_no;
	private Product p;
	private int order_quantity;
	//private OrderInfo info; //관리자 입장에서 주문상세에대한 내역을 보려할때 
	public OrderLine() {
		super();
		// TODO Auto-generated constructor stub
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
