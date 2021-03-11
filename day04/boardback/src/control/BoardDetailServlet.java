package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/detail")
public class BoardDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//CORS정책
		response.setHeader("Access-Control-Allow-Origin", "*");
		//응답형식지정
		response.setContentType("application/json;charset=UTF-8");
		//응답출력스트림 얻기
		PrintWriter out = response.getWriter(); 
		
		//1.요청전달데이터 얻기
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
//		System.out.println(board_no);
		
		//json용 JACKSON Lib활용
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //M:월, m:분
		mapper.setDateFormat(df); //json문자열로 변환될때 날짜형식을 지정
		
		Map<String, Object> map = new HashMap<>();	
		try {
			//2.비지니스로직 호출 => 가장 핵심 로직이다. db에서부터 글 번호에 해당하는 게시글을 검색해오는 것
			RepBoard board = service.findByBoard_no(board_no);

			map.put("status", 1);
			map.put("board", board);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//3.응답하기
		out.print(mapper.writeValueAsString(map));
	}
}
