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

public class BoardDetailController implements Controller {
	private static final long serialVersionUID = 1L;
	//private RepBoardService service = new RepBoardService();
	private RepBoardService service = RepBoardService.getInstance();
	public String execute(HttpServletRequest request, HttpServletResponse response) 
			throws Exception{
		
		//1.요청전달데이터 얻기
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
//		System.out.println(board_no);
		
		//json용 JACKSON Lib활용
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(df); //json문자열로 변환될때 날짜형식을 지정
		
		Map<String, Object> map = new HashMap<>();
		try {
			//2.비지니스로직 호출
			RepBoard board = service.findByBoard_no(board_no);
			
			map.put("status", 1);
			map.put("board", board);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//응답
		//out.print(mapper.writeValueAsString(map));
		return mapper.writeValueAsString(map);
	}

}
