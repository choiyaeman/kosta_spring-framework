package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/list")
public class BoardListServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		request.setCharacterEncoding("utf-8");
		String word = request.getParameter("word");
		System.out.println(word);
		List<RepBoard> list;
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(df);
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("status", 1);
			map.put("list", list);
			out.print(mapper.writeValueAsString(map));;
		} catch (FindException e) {
			e.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("status", -1);
			map.put("msg", e.getMessage());
			out.print(mapper.writeValueAsString(map));
		}
		
		
	}

}
