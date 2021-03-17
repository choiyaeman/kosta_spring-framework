package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.my.exception.RemoveException;
import com.my.service.RepBoardService;

@WebServlet("/remove")
public class BoardRemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String path ="/WEB-INF/views/error.jsp";
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
		String certify_board_pwd = request.getParameter("certify_board_pwd");
		
		try {
			service.remove(board_no, certify_board_pwd);
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/list");
		} catch (RemoveException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);
		}
	}
}
