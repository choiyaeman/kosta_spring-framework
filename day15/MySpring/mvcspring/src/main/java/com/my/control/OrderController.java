package com.my.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.service.OrderService;
import com.my.vo.Customer;
import com.my.vo.OrderInfo;
import com.my.vo.OrderLine;
import com.my.vo.Product;
@Controller

public class OrderController{
	@Autowired
	private OrderService service;
	
	@ResponseBody
	@RequestMapping("/putorder")
	public Map<String, Object>  putOrder(HttpSession session) throws AddException{ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		Map<String, Integer> cart= (Map)session.getAttribute("cart"); //장바구니얻기

		OrderInfo info = new OrderInfo(); 
		//주문기본정보설정
		Customer c = new Customer();
		c.setId(loginedId);
		info.setC(c); //주문자ID설정

		//장바구니내용을 주문상세정보객체로 만들어서 리스트에 추가
		List<OrderLine> lines = new ArrayList<>();
		for(String prod_no: cart.keySet()) {
			int quatinty = cart.get(prod_no);
			//주문상세정보객체
			OrderLine line = new OrderLine(); 

			Product p = new Product(); 
			p.setProd_no(prod_no);
			line.setP(p);

			line.setOrder_quantity(quatinty);

			//주문상세정보객체를 리스트에 추가
			lines.add(line); 
		}
		//리스트를 주문기본에 설정
		info.setLines(lines);
		
		//try {
			service.add(info); 
			session.removeAttribute("cart"); //장바구니 삭제
			map.put("status", 1);
//		} catch (AddException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
	}
	
	@RequestMapping("/vieworder")
	@ResponseBody
	public Map<String, Object>  viewOrder(HttpSession session) throws FindException{ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		List<OrderInfo> list;
		//try {
			//loginedId = "aaaaa"; //없는 아이디로 검색시 FindException예상
			list = service.findById(loginedId);
			map.put("status", 1);
			map.put("list", list);
//		} catch (FindException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
		
	}
}
