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
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.service.RepBoardService;

public class BoardRemoveController implements Controller {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = RepBoardService.getInstance();	
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {		response.setHeader("Access-Control-Allow-Origin", "*");
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
		String certify_board_pwd = request.getParameter("certify_board_pwd");
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(df);
		Map<String, Object> map = new HashMap<>();
		try {
			service.remove(board_no, certify_board_pwd);
			map.put("status", 1);
			
		} catch (RemoveException e) {
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return mapper.writeValueAsString(map);
		
	}

}
