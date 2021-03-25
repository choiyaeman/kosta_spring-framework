package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.my.exception.AddException;
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/reply")
public class BoardReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String path ="/WEB-INF/views/error.jsp";
		//1.요청전달데이터 얻기
		String strParent_no = request.getParameter("parent_no");
		int parent_no = Integer.parseInt(strParent_no);
		String board_title = request.getParameter("board_title");
		String board_writer = request.getParameter("board_writer");
		String board_pwd = request.getParameter("board_pwd");
		RepBoard board = new RepBoard();
		board.setParent_no(parent_no);
		board.setBoard_title(board_title);
		board.setBoard_writer(board_writer);;
		board.setBoard_pwd(board_pwd);
		
		try {
			service.writeReply(board);
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/list");
		} catch (AddException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);
		}
		
	}

}
