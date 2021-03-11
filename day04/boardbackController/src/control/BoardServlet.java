package control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BoardServlet
 */
@WebServlet("/*")
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contextPath = request.getContextPath(); // ex: http://localhost:8888/boardbackController/list라면
								                       // boardbackController를 반환
		String requestURI = request.getRequestURI(); // /boardbackController/list
		String subpath = requestURI.substring(contextPath.length(), requestURI.length()); //contextPath.length() ->boardconroller서부터 requestURI.length()->list uri 끝까지
		System.out.println("BoardServlet이 요청됨 subpath=" + subpath);
		
		Controller c = null;
		if("/list".equals(subpath)) {
			c = new BoardListController();
		}else if("/detail".equals(subpath)) {
			c = new BoardDetailController();
		}
		PrintWriter out = response.getWriter();
		
		if(c != null) {
		String result;
		try {
			result = c.execute(request, response);
			out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	  }
   }
}
