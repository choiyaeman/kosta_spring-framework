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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.AddException;
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;
@Controller
public class BoardWriteCotroller {
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/write")
	@ResponseBody
	public Map<String, Object> execute(RepBoard board) {
		Map<String, Object> map = new HashMap<>();
		try {
			//2.비지니스로직 호출
			service.writeBoard(board);			
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}		
		//3.응답하기
		return map;
	}

}
