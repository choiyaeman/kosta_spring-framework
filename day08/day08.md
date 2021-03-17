# day08

![day08%20a0c259420019472397cbda9e13f67307/Untitled.png](day08%20a0c259420019472397cbda9e13f67307/Untitled.png)

db에는 정확히 세팅이 되어 있는데 spring을 통해서 json형태로 결과 값을 가지고 오며는 이전 날짜가 출력이 된다. 버전 충돌 문제로 인해 timezone을 정확하게 설정해줘야 timezone에 해당하는 format으로 바꿔준다. → timezone = "Asia/Seoul"

- BoardController.java

```java
package control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardController {
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/write")
	@ResponseBody
	public Map<String, Object> write(RepBoard board) {
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
	
	@RequestMapping("/reply")
	@ResponseBody
	public Map<String, Object> reply(RepBoard board) throws Exception { //매개변수가 많아지므로 RepBoard타입으로 선언
//		String strParent_no = request.getParameter("parent_no");
//		int parent_no = Integer.parseInt(strParent_no);
//		String board_title = request.getParameter("board_title");
//		String board_writer = request.getParameter("board_writer");
//		String board_pwd = request.getParameter("board_pwd");
//		RepBoard board = new RepBoard();
//		board.setParent_no(parent_no);
//		board.setBoard_title(board_title);
//		board.setBoard_writer(board_writer);
//		board.setBoard_pwd(board_pwd);
		
//		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.writeReply(board);
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public Map<String, Object> remove(@RequestParam(required = false, defaultValue = "0")int board_no, String certify_board_pwd) throws Exception {				
//		ObjectMapper mapper = new ObjectMapper();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		mapper.setDateFormat(df);
		Map<String, Object> map = new HashMap<>();
		try {
			service.remove(board_no, certify_board_pwd);
			map.put("status", 1);
		} catch (RemoveException e) {
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@RequestMapping("/modify")
	@ResponseBody
	public Map<String, Object> modify(RepBoard board, String certify_board_pwd) {
		Map<String, Object> map = new HashMap<>();
		try {
			service.modify(board, certify_board_pwd);
			map.put("status", 1);
			
		} catch (ModifyException e) {
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@GetMapping(value = "/list")
	@ResponseBody
	//public List<RepBoard> execute(HttpServletRequest request, HttpServletResponse response) 
	public List<RepBoard> list(String word) throws Exception{
		//String word = request.getParameter("word");
		log.info("검색어:" + word);
		List<RepBoard> list = null;
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}		
		}catch (FindException e) {
			log.info(e.getMessage());
		}
		return list;
	}
	
	@RequestMapping("/detail")
	@ResponseBody
	public Map<String, Object> detail(@RequestParam(required = false, defaultValue = "0") int board_no) 
			throws Exception{
		
		//1.요청전달데이터 얻기
//		String strBoard_no = request.getParameter("board_no");
//		int board_no = Integer.parseInt(strBoard_no);
//		System.out.println(board_no);
		
		//json용 JACKSON Lib활용
//		ObjectMapper mapper = new ObjectMapper();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		mapper.setDateFormat(df); //json문자열로 변환될때 날짜형식을 지정
		
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
		//return mapper.writeValueAsString(map);
		return map;
	} 
}
```

![day08%20a0c259420019472397cbda9e13f67307/Untitled%201.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%201.png)

BoardController class파일 만들어서 하나로만 다 관리 할 수 있도록 만든다. 나머지 컨트롤러들 다 지우자 

- BoardController.java

```java
...
..
.@GetMapping(value = "/list")
	@ResponseBody
	//public List<RepBoard> execute(HttpServletRequest request, HttpServletResponse response) 
	public **Map<String, Object>** list(String word) throws Exception{
		//String word = request.getParameter("word");
		log.info("검색어:" + word);
		List<RepBoard> list = null;
		Map<String, Object> map = new HashMap<>();
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}
			map.put("list", list);
			map.put("status", 1);
		}catch (FindException e) {
			log.info(e.getMessage());
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
...
..
.
```

규격에 맞도록 List → Map타입으로 바꾸자

![day08%20a0c259420019472397cbda9e13f67307/Untitled%202.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%202.png)

- index.html

```java
..
.
<script>
//let backContextPath = "/boardback";
**let backContextPath = "/springmvc";**
//let frontContextPath = "/boardfrontController";
**let frontContextPath = "/boardfrontspring";**
$(function(){
..
.
```

실행결과>

![day08%20a0c259420019472397cbda9e13f67307/Untitled%203.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%203.png)

---

- list.html

```java
..
.
$(function(){
	 $.ajax({
		/*url: backContextPath + "/list",*/
		**url: "http://192.168.0.224:8888/"+backContextPath + "/list",**
		method: 'get',
		success: function(responseObj){
			alert(responseObj);
			showList(responseObj);
		}
	});//$ajax */
..
.
```

- BoardController.java

```java
..
.
import lombok.extern.log4j.Log4j;
**@CrossOrigin("*")**
@Controller
@Log4j
public class BoardController {
	@Autowired
	private RepBoardService service;
..
.
```

다른 사람 url에 접속해보자~ 그럴려면 BoardController에서 @CrossOrigin("*") 설정 해줘야 한다.

→ @CrossOrigin("*") 어느 url이건 허용하겠다.

---

- list.jsp

```java
..
.
$(function(){
	//--게시물 클릭 시작--
	$("section>table tr>td").click(function(event){
		var board_no = $(event.target).parent().children("td.board_no").html().trim();
		$("section>form>input[name=board_no]").val(board_no);
		var $formObj = $("form");
		$formObj.attr("method", "get");
		$formObj.attr("action", "**${contextPath}**/detail");
		$formObj.submit();
		return false;
	});
..
.
```

새로 project 파일을 만들어보자

![day08%20a0c259420019472397cbda9e13f67307/Untitled%204.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%204.png)

![day08%20a0c259420019472397cbda9e13f67307/Untitled%205.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%205.png)

![day08%20a0c259420019472397cbda9e13f67307/Untitled%206.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%206.png)

![day08%20a0c259420019472397cbda9e13f67307/Untitled%207.png](day08%20a0c259420019472397cbda9e13f67307/Untitled%207.png)

- servlet-context.xml

```java
..
.
**<context:component-scan base-package="control" />**
..
.
```

- RepBoardDAOOracle.java

```java
package com.my.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

..
.
@Repository
public class RepBoardDAOOracle implements RepBoardDAO {
	@Autowired
	**private DataSource ds;**
	public void delete(int board_no, String board_pwd) throws RemoveException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			**con = ds.getConnection();**
		}catch(Exception e) {
			throw new RemoveException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String deleteSQL = "DELETE repboard  where board_no = ? AND board_pwd = ?";
		try {
			pstmt = con.prepareStatement(deleteSQL);
			pstmt.setInt(1, board_no);
			pstmt.setString(2, board_pwd);
			int rowcnt = pstmt.executeUpdate();
			if(rowcnt == 0) {
				throw new RemoveException("글번호가 없거나 비밀번호가 다릅니다");
			}
		} catch (SQLException e) {
			throw new RemoveException(e.getMessage());
		}finally {
			**MyConnection.close(con, pstmt);** // <--이 코드를 살려둔다
			                                  // con.close()가 connection이 사라지는게 아니라
			                                  // HikariCP에게 connection을 반환한다.
		}
	}
..
.
```

- RepBoardService.java

```java
..
.
@Service
public class RepBoardService {
	@Autowired
	private RepBoardDAO boardDAO; //일반화된 interface타입 권장
..
.
```

- BoardDetailController.java

```java
package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardDetailController {
	
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/detail")
	public ModelAndView detail(int board_no) throws ServletException {
//		String path ="/WEB-INF/views/error.jsp";
//		//1.요청전달데이터 얻기
//		String strBoard_no = request.getParameter("board_no");
//		int board_no = Integer.parseInt(strBoard_no);
		log.info("아이디:" + board_no);
		
		ModelAndView mnv = new ModelAndView();
		try {
			//2.비지니스로직 호출
			RepBoard board = service.findByBoard_no(board_no);
			//3.요청속성으로 추가
			//request.setAttribute("board", board);
			mnv.addObject("board", board);
			//path = "/WEB-INF/views/detail.jsp";
			//mnv.setViewName("detail"); 안써도 가능 동일한 view이름을 찾아낸다
		} catch (FindException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			mnv.setViewName("error");
			e.printStackTrace();
		}
		//4.View로 이동
//		RequestDispatcher rd = request.getRequestDispatcher(path);
//		rd.forward(request, response);
		return mnv;
	}

}
```

- BoardListController.java

```java
package control;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardListController {
	
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service; //service객체가 주입되도록 설정
	
	@RequestMapping("/list")
	public ModelAndView list(String word) {
//		String path ="/WEB-INF/views/error.jsp";
//		request.setCharacterEncoding("utf-8");
		
		//1. 요청 전달데이터 얻기
		//String word = request.getParameter("word");
//		System.out.println(word);
		log.info("검색어:" + word);
		
		List<RepBoard> list;
		
		ModelAndView mnv = new ModelAndView(); //ModelAndView 객체생성
		try {
			//2. 비지니스로직 호출
			if(word == null) { //전체검색
				list = service.findAll();
			}else { //검색어에 만족하는 검색
				list = service.findByBoard_titleORBoard_writer(word);
			}
			//3. 요청속성으로 호출결과값 추가
			//request.setAttribute("list", list);
			mnv.addObject("list", list);
			//path = "/WEB-INF/views/list.jsp";
			mnv.setViewName("list"); //view이름만 적어주면 된다
		} catch (FindException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			mnv.setViewName("error");
			e.printStackTrace();
		}
		//4. View로 이동
//		RequestDispatcher rd = request.getRequestDispatcher(path);
//		rd.forward(request, response);
		return mnv;
	}

}
```

- BoardModifyController.java

```java
package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardModifyController {
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service;	
	
	@RequestMapping("/modify")
	public ModelAndView modify(RepBoard board, 
			                   String certify_board_pwd) {	
		ModelAndView mnv = new ModelAndView();
		try {
			service.modify(board, certify_board_pwd);
			//path = "/WEB-INF/views/modify.jsp";
			//String contextPath = request.getContextPath();
			//response.sendRedirect(contextPath + "/list");
			mnv.setViewName("redirect:/list"); //앞에 접두어처럼 redirect를 붙이면 해당 view이름으로 리다이렉션된다
		} catch (ModifyException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			e.printStackTrace();
//			RequestDispatcher rd = request.getRequestDispatcher(path);
//			rd.forward(request, response);
			mnv.setViewName("error"); //error.jsp로 알아서 forward
		}
		return mnv;
	}

}
```

- web.xml

```java
	<filter>
	    <filter-name>encodingFilter</filter-name>
	    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	    <init-param>
	        <param-name>encoding</param-name>
	        <param-value>UTF-8</param-value>
	    </init-param>
	</filter>
	<filter-mapping>
		  <filter-name>encodingFilter</filter-name> 
		  <url-pattern>/*</url-pattern>
	</filter-mapping>
```

한글깨지므로 한글깨짐 방지 설정해주기

- BoardRemoveController.java

```java
package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.RemoveException;
import com.my.service.RepBoardService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class BoardRemoveController {
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/remove")
	public ModelAndView remove(int board_no, String certify_board_pwd) {
		//request.setCharacterEncoding("utf-8");
		//String path ="/WEB-INF/views/error.jsp";
		//String strBoard_no = request.getParameter("board_no");
		//int board_no = Integer.parseInt(strBoard_no);
		//String certify_board_pwd = request.getParameter("certify_board_pwd");
		ModelAndView mnv = new ModelAndView();
		try {
			service.remove(board_no, certify_board_pwd);
//			String contextPath = request.getContextPath();
//			response.sendRedirect(contextPath + "/list");
			mnv.setViewName("redirect:/list");
		} catch (RemoveException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			mnv.setViewName("error");
			e.printStackTrace();
//			RequestDispatcher rd = request.getRequestDispatcher(path);
//			rd.forward(request, response);
		}
		return mnv;
	}
}
```

- BoardReplyController.java

```java
package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.AddException;
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@Controller
public class BoardReplyController {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/reply")
	public ModelAndView reply(RepBoard board) throws ServletException, IOException {
//		request.setCharacterEncoding("utf-8");
//		String path ="/WEB-INF/views/error.jsp";
//		//1.요청전달데이터 얻기
//		String strParent_no = request.getParameter("parent_no");
//		int parent_no = Integer.parseInt(strParent_no);
//		String board_title = request.getParameter("board_title");
//		String board_writer = request.getParameter("board_writer");
//		String board_pwd = request.getParameter("board_pwd");
//		RepBoard board = new RepBoard();
//		board.setParent_no(parent_no);
//		board.setBoard_title(board_title);
//		board.setBoard_writer(board_writer);;
//		board.setBoard_pwd(board_pwd);
		
		ModelAndView mnv = new ModelAndView();
		try {
			service.writeReply(board);
//			String contextPath = request.getContextPath();
//			response.sendRedirect(contextPath + "/list");
			mnv.setViewName("redirect:/list");
		} catch (AddException e) {
			//request.setAttribute("exception", e);
			mnv.addObject("exception", e);
			mnv.setViewName("error");
			e.printStackTrace();
//			RequestDispatcher rd = request.getRequestDispatcher(path);
//			rd.forward(request, response);
		}
		return mnv;
	}

}
```

- BoardWriteController.java

```java
package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.exception.AddException;
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

//@WebServlet("/write")
@Controller
public class BoardWriteController {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RepBoardService service;	
	
	@GetMapping("/write")
	//1번째 방법 -void반환
	public void showWrite() { //view이름이나 요청된 url이 같을경우 void로 반환만 하면 된다 
//		String path ="/WEB-INF/views/write.jsp";
//		RequestDispatcher rd = request.getRequestDispatcher(path);
//		rd.forward(request, response);
		
//		ModelAndView mnv = new ModelAndView();
//		mnv.setViewName("write");
//		return;
	}
	//2번째 방법 -string반환
//	public String showWrite() {
//		return "write"; //viewname만 반환해도 된다. 핸들러어뎁터가 jsp로 찾아낸다
//	}
	
	@PostMapping("/write")
	public ModelAndView write(RepBoard board) {
		ModelAndView mnv = new ModelAndView();
		try {
			service.writeBoard(board);
			mnv.setViewName("redirect:/list");
		} catch (AddException e) {	
			mnv.addObject("exception", e);
			e.printStackTrace();	
		}
		return mnv;	
	}
}
```