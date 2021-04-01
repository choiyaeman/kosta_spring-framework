package com.my.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.dao.OrderDAO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.vo.OrderInfo;

import lombok.extern.log4j.Log4j;
@Service
@Log4j
public class OrderService {
	@Autowired
	private OrderDAO dao;
	public void add(OrderInfo info) throws AddException {
		log.info("orderdao=" + dao);
		dao.insert(info);
	}
	public List<OrderInfo> findById(String order_id) throws FindException {
		return dao.selectById(order_id);
	}
}
