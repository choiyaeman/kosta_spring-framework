package controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
	@PreAuthorize("isAuthenticated()") //인증된 경우만
	@GetMapping(value = "/order/view", produces = "application/json;charset=utf-8") // /order/view경로에 접근할 수 있다
	@ResponseBody
	public String view(Authentication auth) {
		String userName = auth.getName();
		return userName + "님의 주문목록입니다";
	}
	
	@PreAuthorize("hasRole('ROLE_MEMBER')") //허가받은 권한이 'ROLE_MEMBER'인 경우만 
	@GetMapping(value = "/order/put", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String put() {
		return "주문을 추가했습니다";
	}
}
