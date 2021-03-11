package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.AddException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/write")
public class BoardWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path ="/WEB-INF/views/write.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(path);
		rd.forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*"); //CORS해결
		response.setContentType("application/json;charset=UTF-8"); //응답형식지정
		PrintWriter out = response.getWriter(); //응답출력스트림얻기
		
		//1.요청전달데이터 얻기
		request.setCharacterEncoding("utf-8"); //요청 메시지 바디영역의 인코딩설정
		String path ="/WEB-INF/views/error.jsp";
		String board_title = request.getParameter("board_title");
		String board_writer = request.getParameter("board_writer");
		String board_pwd = request.getParameter("board_pwd");
		
		RepBoard board = new RepBoard();
		board.setBoard_title(board_title);
		board.setBoard_writer(board_writer);
		board.setBoard_pwd(board_pwd);
		
		//json응답을 위해 Jackson Lib활용
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			//2.비지니스로직 호출
			service.writeBoard(board);
			map.put("status", 1);
		} catch (AddException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());	
		}
		//3.응답하기
		out.print(mapper.writeValueAsString(map));
	}
}
