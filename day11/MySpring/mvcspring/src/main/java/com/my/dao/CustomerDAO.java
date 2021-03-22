package com.my.dao;

import java.util.List;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.Customer;

public interface CustomerDAO {
	//모든 메서드는 public abstract이 생략됨
	/**
	 * 저장소에 고객정보를 저장한다
	 * @param c 고객객체
	 * @throws AddException 아이디가 이미 존재하는 경우,
	 *                      저장소가 꽉찬경우 발생한다
	 *                      
	 */
	void insert(Customer c) throws AddException;
	
	/**
	 * 저장소의 모든고객을 반환한다
	 * @return 저장소의 모든고객
	 * @throws FindException 고객이 한명이 없으면 발생한다
	 */
	abstract List<Customer> selectAll() throws FindException;
	
	/**
	 * 저장소의 아이디에 해당고객을 반환한다
	 * @param id 아이디
	 * @return 고객객체
	 * @throws FindException 아이디에 해당고객이 없으면 발생한다
	 */
	public abstract Customer selectById(String id) 
			throws FindException;
	
	/**
	 * 고객정보를 수정한다. 단, 아이디는 수정할 수 없다
	 * @param c  변경될 내용이 담겨있는 고객객체
	 * @return   변경된 고객객체
	 * @throws  수정실패시 예외발생한다
	 */
	Customer update(Customer c) throws ModifyException;
	
	/**
	 * 고객정보를 삭제한다
	 * @param id 아이디
	 * @return 삭제실패시 예외발생한다
	 */
	Customer delete(String id) throws RemoveException;
}
