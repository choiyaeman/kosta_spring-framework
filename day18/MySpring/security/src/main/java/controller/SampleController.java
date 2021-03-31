package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //servlet-context.xml에 context:component-scan 설정필요!
@RequestMapping("/sample/*")
public class SampleController {
	
	@GetMapping("/all")
	public void all() { //view이름이 /sample/all과 같음, view는 /WEB-INF/views/sample/all.jsp가 됨
		System.out.println();
	}
	
	@GetMapping("/member")
	public void member() { //view이름이 /sample/all과 같음, view는 /WEB-INF/views/sample/all.jsp가 됨
	}
	
	@GetMapping("/admin")
	public void admin() { //view이름이 /sample/all과 같음, view는 /WEB-INF/views/sample/all.jsp가 됨
	}
}
