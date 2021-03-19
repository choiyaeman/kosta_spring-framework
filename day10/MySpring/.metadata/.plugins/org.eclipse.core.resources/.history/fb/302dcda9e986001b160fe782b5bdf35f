package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/modify")
public class BoardModifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String path ="/WEB-INF/views/error.jsp";
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
		String board_title = request.getParameter("board_title");
		String certify_board_pwd = request.getParameter("certify_board_pwd");
		String board_pwd = request.getParameter("board_pwd");
		RepBoard board = new RepBoard();
		board.setBoard_no(board_no);
		board.setBoard_title(board_title);
		board.setBoard_pwd(board_pwd);
		
		try {
			service.modify(board, certify_board_pwd);
			//path = "/WEB-INF/views/modify.jsp";
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/list");
		} catch (ModifyException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);
		}
		
	}

}
