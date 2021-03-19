package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.AddException;
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

//@WebServlet("/write")
@Controller
public class BoardWriteController {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RepBoardService service;	
	
	@GetMapping("/write")
	//1번째 방법
	public void showWrite() { //view이름이나 요청된 url이 같을경우 void로 반환만 하면 된다 
//		String path ="/WEB-INF/views/write.jsp";
//		RequestDispatcher rd = request.getRequestDispatcher(path);
//		rd.forward(request, response);
		
//		ModelAndView mnv = new ModelAndView();
//		mnv.setViewName("write");
//		return;
	}
	//2번째 방법
//	public String showWrite() {
//		return "write"; //viewname만 반환해도 된다. 핸들러어뎁터가 jsp로 찾아낸다
//	}
	
	@PostMapping("/write")
	public ModelAndView write(RepBoard board) {
		ModelAndView mnv = new ModelAndView();
		try {
			service.writeBoard(board);
			mnv.setViewName("redirect:/list");
		} catch (AddException e) {	
			mnv.addObject("exception", e);
			e.printStackTrace();	
		}
		return mnv;	
	}
}
