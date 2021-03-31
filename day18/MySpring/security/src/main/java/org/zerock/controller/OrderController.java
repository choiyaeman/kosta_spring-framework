package org.zerock.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
	@GetMapping("/orderAll")
	@ResponseBody
	public String all() {
		return "order";
	}
	@GetMapping("/addOrder")
//	@PreAuthorize("isAuthenticated()")
	@PreAuthorize("hasRole('ROLE_MEMBER')")
	@ResponseBody
	public String preAutheticated() {
		return "addOrder";
	}
	
}
