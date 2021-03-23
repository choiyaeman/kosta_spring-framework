package com.my.dao;

import java.util.List;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.vo.OrderInfo;

public interface OrderDAO {
	/**
	 * 주문기본정보와 상세정보들을 저장소에 추가한다
	 * @param info 주문기본정보와 상세정보들
	 */
	void insert(OrderInfo info) throws AddException;
	
	/**
	 * 주문자아이디가 주문한 주문기본정보들을 검색한다
	 * @param order_id 주문자아이디
	 * @return 주문상세들을 포함한 주문기본목록들
	 * @throws FindException 주문정보가 없을때 예외발생한다
	 */
	List<OrderInfo>selectById(String order_id) throws FindException;
}
