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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.RemoveException;
import com.my.service.RepBoardService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardRemoveController {
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/remove")
	public ModelAndView remove(int board_no, String certify_board_pwd) {
		//request.setCharacterEncoding("utf-8");
		//String path ="/WEB-INF/views/error.jsp";
		//String strBoard_no = request.getParameter("board_no");
		//int board_no = Integer.parseInt(strBoard_no);
		//String certify_board_pwd = request.getParameter("certify_board_pwd");
		ModelAndView mnv = new ModelAndView();
		try {
			service.remove(board_no, certify_board_pwd);
//			String contextPath = request.getContextPath();
//			response.sendRedirect(contextPath + "/list");
			mnv.setViewName("redirect:/list");
		} catch (RemoveException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			mnv.setViewName("error");
			e.printStackTrace();
//			RequestDispatcher rd = request.getRequestDispatcher(path);
//			rd.forward(request, response);
		}
		return mnv;
	}
}
