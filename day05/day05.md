# day05

![1](https://user-images.githubusercontent.com/63957819/110929354-380c4680-836b-11eb-842c-424ceb6f8172.png)

Servlet하나가 모든 요청을 받아 처리하게 되면 관리하기 편하다. 이런 것을 프론트 컨트롤러 패턴이라 한다. 요청이 들어오면 서블릿 하나가 요청을 받아낼 것인데 요청의 경로를 보면 /boardbackController/list 또는 /boardbackController/detail이 요청이 되는 경우가 있다. 들어오는 것을 서블릿이 감지해야 한다. 요청을 분석해서 list가 요청이 된 것인지 detail이 요청이 된 것인지 감지 할 수 있어야 한다. 서블릿에 가보면 if~else구문으로 처리가 되어 있다. 서블릿안에 if~else가 들어가게 되면 들어갈수록 코딩 양이 많아질 것이고 요청 별로 모두 if~else로 구성되었다가 요청이 하나 더 추가가 되었다라하면 서블릿 소스 코드를 변경해야 한다. 요청 url바뀌거나 추가되거나 삭제되는 경우 수가 생기면 서블릿 소스 코드의 재사용성이 떨어진다.

유지보수성이 높이려면 if~else구문을 빼버려야 한다. 일반 텍스트 파일 형태로 파일을 만든다. 요청 url이 list, detail가 들어올 때는 실제로 처리할 소스코드가 /list=BoardListController, /detail=BoardDetailController 이렇게 url값과 class이름을 맵핑을 시켜 놓은 파일을 서블릿에서 읽어낸다. 그 파일 안에 들어있는 list항목, detail항목을 찾아내서 해당하는 클래스 이름을 찾고 객체 생성해서 execute메소드를 호출하면 된다. 소스 코드의 변경 없이 url이 변경됨에 따라 텍스트 파일 내용만 변경되면 된다. 자바 소스 코드는 안 건드리는 거다.

1번 절차로는 일반 텍스트 파일이 아니라 프로퍼티스 파일을 사용하면 된다. 프로퍼티스 파일 안에다 텍스트 파일 내용을 저장을 해 놓는다. 그렇게 한 다음 텍스트 파일 읽지 말고 프로퍼티스 파일 읽게 되면 읽는 방법이 라이브러리로 제공이 되고 있다. 그 라이브러리를 쓰게 되면 훨씬 일반 텍스트 파일 읽는 것보다 쉽게 읽어 낼 수 있다. 자바 쪽에서 읽어올 수 있는 라이브러리가 있기 때문에 쓰는 것이다.

2번 절차로 if~else구문에 해당하는 객체 생성하지 않고 Class.forName 메소드를 이용해서 클래스를 JVM위쪽으로 올려놓고 newinstance메소드를 호출하게 되면 클래스 명에 해당하는 객체 생성을 하게 된다. 1번 절차로 찾아온 클래스이름 변수가 해당하는 객체를 생성하는 절차가 되는 것이다. 

3번 절차로 execute메소드를 호출하는 절차는 생성한 객체를 가지고서 getMethod를 이용해서 메서드를 찾아서 그 메서드를 호출하게 되는 코드는 invoke라는 메소드가 있다.

- BoardServlet.java

```java
	package control;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

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
		
		
		
//		Controller c = null;
//		if("/list".equals(subpath)) {
//			c = new BoardListController();
//		}else if("/detail".equals(subpath)) {
//			c = new BoardDetailController();
//		}
//		PrintWriter out = response.getWriter();
//		
//		if(c != null) {
//		String result;
//		try {
//			result = c.execute(request, response);
//			out.print(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//	  }
		//---------------------------------------------------
		Properties controllerEnv = new Properties();
		String propertiesRealPath = 
				getServletContext().getRealPath("controller.properties");
		controllerEnv.load(new FileInputStream(propertiesRealPath));
		String className = controllerEnv.getProperty(subpath);
		try {
			Class clazz = Class.forName(className); //클래스 이름을 찾아 JVM위쪽으로 올린다 -> runtime dynamic load
			
			Object obj = clazz.newInstance(); //객체 생성하기
			
			Method m = clazz.getDeclaredMethod("execute", 
					                         HttpServletRequest.class, HttpServletResponse.class); //두개의 매개변수를 갖는 execute메소드를 찾아와라
			m.invoke(obj, request, response); //execute메서드 호출하기
			
		} catch (ClassNotFoundException e) { //Class.forName
			e.printStackTrace();
		} catch (InstantiationException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (IllegalAccessException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (NoSuchMethodException e) { //clazz.getDeclaredMethod
			e.printStackTrace();
		} catch (SecurityException e) { //m.invoke
			e.printStackTrace();
		} catch (IllegalArgumentException e) { //m.invoke
			e.printStackTrace();
		} catch (InvocationTargetException e) { //m.invoke
			e.printStackTrace();
		} 	
   }
}
```

- controller.properties

```xml
/list=control.BoardListController
/detail=control.BoardDetailController
/modify=control.BoardModifyController
/remove=control.BoardRemoveController
/reply=control.BoardReplyController
/write=control.BoardWriteCotroller
```

WebContent>file만들기

Map계열로 많이 쓰이는 Hashtable하고 HashMap이 있다. Properties는 String타입으로 자료가 저장된다.

Class...파라미터 타입 의미는 개수 제한 없이 똑같은 타입으로 여러 개 전달 할 수 있다라는 의미

![2](https://user-images.githubusercontent.com/63957819/110929355-380c4680-836b-11eb-9eec-9288cb276f41.png)

첫 번째 방법으로 미리 객체를 하나 만들어서 각각의 컨트롤러에게 전달하면 된다.

두 번째 방법으로 서비스 객체가 매번 만들어지지 않고 객체를 미리 만들어 놓은 다음에 만들어진 객체를 갖다 쓰기만 하자 → 싱글톤 패턴

두 번째 방법을 쓰자~

- BoardListController.java

```java
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

public class BoardListController implements Controller {
	
	private static final long serialVersionUID = 1L;
	//private RepBoardService service = new RepBoardService();//에러발생
	private RepBoardService service = RepBoardService.getInstance();
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String word = request.getParameter("word");
		System.out.println("검색어:" + word);
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
			//out.print(mapper.writeValueAsString(map));
			return mapper.writeValueAsString(map); //값을 리턴하는 역할만
		} catch (FindException e) {
			e.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("status", -1);
			map.put("msg", e.getMessage());
			//out.print(mapper.writeValueAsString(map));
			return mapper.writeValueAsString(map);
		}
		
		
	}

}
```

- BoardDetailController.java

```java
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
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		//out.print(mapper.writeValueAsString(map));
		return mapper.writeValueAsString(map);
	}
}
```

- RepBoardService.java

```java
package com.my.service;

import java.util.List;

import com.my.dao.RepBoardDAOOracle;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.RepBoard;

public class RepBoardService {
	private RepBoardDAOOracle boardDAO;
	private static RepBoardService rbs = new RepBoardService();
	//public RepBoardService(){
	private RepBoardService() { //외부에서 RepBoardService 객체 생성하지 못하게 private으로 설정
		this.boardDAO = new RepBoardDAOOracle();//?
	}
	public static RepBoardService getInstance() { //클래스 이름 점으로 사용하기위해 static으로 선언, 누구나 접근할 수 있도록 public선언
		return rbs;
	}
	/**
	 * 게시글(원글)을 추가한다.
	 * @param board 부모글 번호가 0인 게시물
	 * @throws AddException
	 */
	public void writeBoard(RepBoard board) throws AddException{
		if(board.getParent_no() != 0) {
			board.setParent_no(0); //강제 0으로 세팅하기
		}
		boardDAO.insert(board);
	}
	/**
	 * 답글을 추가한다
	 * @param board 부모글 번호를 포함한 게시물
	 * @throws AddException 부모글 번호가 0이면 예외발생한다
	 */
	public void writeReply(RepBoard board) throws AddException{
		if(board.getParent_no() == 0) {
			throw new AddException("부모글번호가 없습니다");
		}
		boardDAO.insert(board);
	}
	/**
	 * 게시물을 전체 검색한다
	 * @return 게시물들
	 * @throws FindException 저장소처리 문제가 발생하거나 게시글이 없을때 예외가 발생한다
	 */
	public List<RepBoard> findAll() throws FindException{
		return boardDAO.selectAll();
	}
	/**
	 * 게시물번호에 해당하는 게시물을 검색하고,
	 * 조회수를 1증가한다.
	 * @param board_no 게시물 번호
	 * @return
	 * @throws FindException
	 */
	public RepBoard findByBoard_no(int board_no) throws FindException{
		RepBoard board = boardDAO.selectByBoard_no(board_no);
		try {
			boardDAO.updateBoardCnt(board_no);
		} catch (ModifyException e) {
			throw new FindException(e.getMessage());
		}
		return board;
	}
	/**
	 * 검색어에 만족하는 게시물들을 검색한다.
	 * @param word 검색어. 글제목 또는 작성자
	 * @return 게시물들
	 * @throws FindException 검색어에 만족하는 게시물이 없으면 예외가 발생한다.
	 */
	public List<RepBoard> findByBoard_titleORBoard_writer(String word) throws FindException{
		return boardDAO.selectByBoard_titleORBoard_writer(word);
	}
	public void modify(RepBoard board, String board_pwd) throws ModifyException{
		boardDAO.update(board, board_pwd);
	}
	public void remove(int board_no, String board_pwd) throws RemoveException{
		boardDAO.delete(board_no, board_pwd);
	}
}
```

- BoardServlet.java

```java
package control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

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
		
		//CORS정책
		response.setHeader("Access-Control-Allow-Origin", "*");
		//응답형식
		response.setContentType("application/json;charset=UTF-8");
		//응답출력스트림 얻기
		PrintWriter out = response.getWriter();
		
        request.setCharacterEncoding("utf-8");     
		//---------------------------------------------------
		Properties controllerEnv = new Properties();
		String propertiesRealPath = 
				getServletContext().getRealPath("controller.properties");
		controllerEnv.load(new FileInputStream(propertiesRealPath));
		String className = controllerEnv.getProperty(subpath);
		try {
			Class clazz = Class.forName(className); //클래스 이름을 찾아 JVM위쪽으로 올린다 -> runtime dynamic load
			
			Object obj = clazz.newInstance(); //객체 생성하기
			
			Method m = clazz.getDeclaredMethod("execute", HttpServletRequest.class, HttpServletResponse.class); //두개의 매개변수를 갖는 execute메소드를 찾아와라
			Object result = m.invoke(obj, request, response); //execute메서드 호출하기
			out.print(result);
		} catch (ClassNotFoundException e) { //Class.forName
			e.printStackTrace();
		} catch (InstantiationException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (IllegalAccessException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (NoSuchMethodException e) { //clazz.getDeclaredMethod
			e.printStackTrace();
		} catch (SecurityException e) { //m.invoke
			e.printStackTrace();
		} catch (IllegalArgumentException e) { //m.invoke
			e.printStackTrace();
		} catch (InvocationTargetException e) { //m.invoke
			e.printStackTrace();
		} 	
   }
}
```

실행결과>

![3](https://user-images.githubusercontent.com/63957819/110929358-38a4dd00-836b-11eb-95e0-31407b8f5554.png)

ContextPath는 고정해서 쓰지 않는게 관례이다.

![4](https://user-images.githubusercontent.com/63957819/110929360-393d7380-836b-11eb-87f2-27a4ee426b98.png)

import>WarFile>boardfront

- index.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인(index.html)</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 

<script>
//let backContextPath = "/boardback";
let backContextPath = "/boardbackController";
let frontContextPath = "/boardfrontController";
$(function(){	
	
	//var backContextPath = "/boardbackController";
	$("header").load(frontContextPath + '/header.html');
	$("footer").load(frontContextPath + '/footer.html');
	
	//--메뉴 클릭 시작--
	$("header").on("click","ul>li>a",function(event){
		var menu = $(event.target).attr("class");
		//alert("메뉴:" + $(event.target).attr("class"));
		var $sectionObj = $("section");
		switch(menu){
		case 'list'://게시판
			$sectionObj.load(frontContextPath + "/list.html");
			/* $.ajax({
				url: "/boardfront/list.html",
				method : "get",
				success: function(data){
					$sectionObj.html(data);
				}
			}); */
			break;
		case 'write'://글쓰기
			$sectionObj.load(frontContextPath + "/write.html");
			break;
		}
		return false;
	});
	//--메뉴 클릭 끝--

});
</script>
</head>
<body>
<header>
</header>

<section>
<h1>답변형 게시판 MVC실습</h1>
</section>

<footer>
</footer>
</body>
</html>
```

- write.html

```html
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 -->
<script>
$(function(){
	var $writeFormObj = $("section>div.write>form");
	$writeFormObj.submit(function(){
		$.ajax({
			url: backContextPath + $writeFormObj.attr("action"), // boardbackController/write
			method: $writeFormObj.attr("method"),
			data: $writeFormObj.serialize(),
			success:function(responseObj){//응답성공이란 응답완료(readyState가 4), 응답코드가 200인 경우를 말함
				if(responseObj.status == 1){ //글쓰기작업이 성공
					$("header>ul>li>a.list").trigger("click"); //
				}else{ //글쓰기작업이 실패
					alert(responseObj.msg);
				}
			},
			error:function(jqXHR){//응답실패
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
});
</script>
<style>
*{
  box-sizing: border-box;
}
</style>
<div class="write">
<form method="post" action="/write">
      글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="글쓰기"> 
</form>
</div>
```

---

![5](https://user-images.githubusercontent.com/63957819/110929362-393d7380-836b-11eb-9402-064779b6bf84.png)

최초로 주소 url요청이 되면 컨트롤 클래스를 찾아서 객체를 생성하러 간다. 그리고 그 객체가 생성될 때 서비스 변수에 해당하는 변수가 없으니깐 서비스 객체를 만든다. 만들 때 필요한 DAOOracle객체가 만들어진다. 

순서를 톰캣 구동하고 서블릿 컨텍스트 객체를 생성한다. 백엔드 쪽의 서비스 객체, DAO객체를 만들어 놓는다. 그리고 서블릿 객체를 만들자 5번 되기 전에 3,4번 미리 된다. 

db하고 연결은 대단히 속도가 떨어지는 작업이다. 그래서 database와 일 처리를 할 비지니스 로직을 미리 해 놓자. 서블릿컨택스트객체 생성되자마자 3, 4 객체 생성하자 서블릿 컨택스트를 구현한 하위 클래스를 만들어서 비지니스 로직을 미리 객체 생성하자

list요청할 때마다 BoardListController객체가 만들어진다. 컨트롤러를 싱글톤 패턴으로 만들어 놓고 매번 요청할 때마다 객체 생성 하는 게 아니라 서비스 패턴처럼 getinstance메서드만 호출해서 객체만 반환 받으면 된다. 

![6](https://user-images.githubusercontent.com/63957819/110929364-39d60a00-836b-11eb-858f-ae35ccc19076.png)

![7](https://user-images.githubusercontent.com/63957819/110929366-39d60a00-836b-11eb-81d1-bda25cbbb476.png)

- ContextLoaderListener.java

```java
package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.my.service.RepBoardService;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
	
	//ServletContext객체생성시 자동호출되는 메서드
    public void contextInitialized(ServletContextEvent sce)  { 
        RepBoardService service; //static변수가 포함되어있는 클래스가 jvm로드 되자마자 자동 초기화 되기 때문.
    	
    	
    }
    //ServletContext객체소멸시 자동호출되는 메서드
    public void contextDestroyed(ServletContextEvent sce)  { 
         
    }

	
}
```

![8](https://user-images.githubusercontent.com/63957819/110929324-33479280-836b-11eb-81ee-9e089e5ec53c.png)

![9](https://user-images.githubusercontent.com/63957819/110929326-33e02900-836b-11eb-99dd-7c247f3d67ad.png)

/*→요청 중에 어떠한 요청이라도 좋으니까 무조건 필터를 거치게 하겠다라는 뜻 서블릿에게 전달하겠다.

- CharacterEncodingFilter.java

```java
package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

//@WebFilter(initParams = ~~~utf-8)
public class CharacterEncodingFilter implements Filter {
	private String encoding = "";
	public void init(FilterConfig fConfig) throws ServletException {
		encoding = fConfig.getInitParameter("encoding");
	}

	//모든 요청시에 자동호출됨
	public void doFilter(ServletRequest request, 
			             ServletResponse response, 
			             FilterChain chain) throws IOException, ServletException {
		
		request.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}
```

![10](https://user-images.githubusercontent.com/63957819/110929328-3478bf80-836b-11eb-8395-daff0951dd59.png)

필터들이 서로 연결이 되어야 한다. 이어주는 매개변수가 FilterChain이다.

서블릿뿐만 아니라 필터에도 init-param을 web.xml에 등록할 수 있다.

web.xml복사해서 WEB-INF밑에 붙여넣기xml복사해서 WEB-INF밑에 붙여넣기

- web.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
version="3.0">
	<filter>
		<filter-name>MyFilter</filter-name>
		<filter-class>filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>MyFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```

---

본격적으로 Spring 수업 나가기~~~

![11](https://user-images.githubusercontent.com/63957819/110929332-35115600-836b-11eb-8bef-4704788282a3.png)

File>Switch Workspace>Other> 새로 만든 MySpring파일로 경로 지정

새로 MySpring으로 eclipse창 열어서 Help>Eclipse Marketplace> STS 검색>Spring Tools 3 Add-on~ 깔기

**EJB** - 기업형 분산 컴포넌트 개발기술

        public class ABean implements SessionBean{ }

특정 인터페이스로부터 구현된 하위 클래스를 만들어야 EJB에서 쓸 수 있는 클래스가 되는 거다.

POJO가 아니다. 여기서 POJO란 순수 옛날부터 썼던 자바 객체를 의미. POJO가 아니므로 확장성이 저하가 되고 EJB가 실행되기 위해서는 WAS가 필요하다.

**Spring** - POJO로 컴포넌트 개발 가능, WAS가 필요없음. EJB보다 훨씬 가볍고 빠르게 처리되고 코딩량도 단순하다.

invoke하는 작업, url path분석해서 처리 등  스프링 컨테이너가 알아서 해준다. 스프링 컨테이너 안에는 jackson 라이브러리가 있다. jackson라이브러리를 이용해서 Map을 추가하는 작업이 들어있어서 안써도 된다.

![12](https://user-images.githubusercontent.com/63957819/110929333-35115600-836b-11eb-816c-b8e5441973f5.png)

스프링 코딩 편하게 만들어 업그레드만 한 거고 스프링용 라이브러리가 있어야 한다.

![13](https://user-images.githubusercontent.com/63957819/110929335-35a9ec80-836b-11eb-8fe4-c1768cabb3cb.png)

[https://mvnrepository.com/artifact/org.springframework/spring-context/5.2.6.RELEASE](https://mvnrepository.com/artifact/org.springframework/spring-context/5.2.6.RELEASE)

spring context 5.2.6버전 라이브러리들이 디펜스에 의존 관계가 있기 때문에 한꺼번에 받으려면 

Maven선택 후 텍스트 복사 pom.xml 붙여넣기

File> New Java Project> di라는 프로젝트 만들기

configure>convert to maven> finish 하면 pom.xml 만들어진다. 거기에 maven 에서 복사한거를 denpendency안에다 붙여넣기

- pom.xml

```java
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>di</groupId>
  <artifactId>di</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  **<dependencies>
  <!-- https://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.6.RELEASE</version>
</dependency>

  </dependencies>**
  <build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

Spring의 특징

1) Dependency Injection : 의존성 주입

![14](https://user-images.githubusercontent.com/63957819/110929337-35a9ec80-836b-11eb-8e8a-a3a12834186c.png)

왼쪽은 클래스 다이어그램 그림이다. class들의 관계를 보면 DAO를 Service가 사용하는 관계(실선으로 표시)

오른쪽은 객체 다이어그램 그림이다. 객체들의 관계를 보면 실제로 Service에서 BoardDAOOracle객체를 쓴다.

실제로 사용할 자원을 외부 설정 파일에 설정 해놓고 주입을 하는 거다. 이것을 의존성 주입이라고 한다. 스프링에서 설정 파일은 properties파일을 쓰지 않는다. 설정 파일로 가장 적합한것은 web.xml이 적합하다.

src오른쪽 클릭> new> Spring Bean Configure file> 이름을 config.xml로 설정> beans 체크

![15](https://user-images.githubusercontent.com/63957819/110929338-36428300-836b-11eb-94cc-27be6e44b049.png)

![16](https://user-images.githubusercontent.com/63957819/110929342-36428300-836b-11eb-9386-16cd036b4962.png)

![17](https://user-images.githubusercontent.com/63957819/110929347-36db1980-836b-11eb-860e-33e093c26b5c.png)

boardbackController프로젝트에서 했던 com파일 그대로 복사해서 src에 붙여넣기

bean 에서 id속성은 중복x, name속성은 중복o

- config.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<bean name="b" class="com.my.vo.RepBoard">
		<property name="board_title" value="제목1"></property>
	</bean>
	
	<bean name="dao" class="com.my.dao.RepBoardDAOOracle"/>
	<bean name="service" class="com.my.service.RepBoardService">
		<property name="boardDAO" ref="dao"></property>
	</bean>
</beans>
```

- Test.java

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

public class Test {

	public static void main(String[] args) {
		//1. 스프링 컨테이너(엔진)를 시작시킨다
		//설정 파일의 bean태그에 있는 클래스타입의 객체가 자동생성되어 
		//Singleton형태로 관리됨
		ClassPathXmlApplicationContext ctx; //스프링엔진. 클래스가 있는 곳에 xml파일을 찾아서 컨테이너를 구동하라 의미
		String configLocation = "config.xml";
		ctx = new ClassPathXmlApplicationContext(configLocation);
		RepBoard b1 = ctx.getBean("b", com.my.vo.RepBoard.class); 
		RepBoard b2 = ctx.getBean("b", com.my.vo.RepBoard.class);
		System.out.println(b1 == b2); //true //springBean을 찾을때 한번 두번 똑같은 이름으로 찾아와서 변수로 담는다 같은 bean객체를 참조
		
		System.out.println(b1.getBoard_title()); //제목1
		System.out.println(b2.getBoard_title()); //제목1
		
		RepBoardService service = ctx.getBean("service", com.my.service.RepBoardService.class);
	}

}
```

근데 스프링은 서버가 따로 있는 게 아니고 스프링 컨테이너를 구동하는 코드를 만들어야 한다.

일반 자바빈이 아니라 스프링 빈이다. 스프링 빈이란 스프링 컨테이너에서 관리되는 객체를 말한다

- RepBoardService.java

```java
package com.my.service;

import java.util.List;

import com.my.dao.RepBoardDAO;
import com.my.dao.RepBoardDAOOracle;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.RepBoard;

public class RepBoardService {
	private RepBoardDAO boardDAO; //일반화된 인터페이스
	public RepBoardDAO getBoardDAO() {
		return boardDAO;
	}
	public void setBoardDAO(RepBoardDAO boardDAO) {
		System.out.println("setBoardDAO()호출됨");
		this.boardDAO = boardDAO;
	}
	
	/**
	 * 게시글(원글)을  추가한다
	 * @param board 부모글번호가 0인 게시물
	 * @throws AddException
	 */
	public void writeBoard(RepBoard board) throws AddException{
		if(board.getParent_no() != 0) {
			board.setParent_no(0);
		}
		boardDAO.insert(board);
	}
	/**
	 * 답글을 추가한다
	 * @param board 부모글번호를 포함한 게시물
	 * @throws AddException 부모글번호가 0이면 예외발생한다
	 */
	public void writeReply(RepBoard board) throws AddException{
		if(board.getParent_no() == 0) {
			throw new AddException("부모글번호가 없습니다");
		}
		boardDAO.insert(board);
	}
	/**
	 * 게시물을 전체 검색한다
	 * @return 게시물들
	 * @throws FindException 저장소처리문제가 발생하거나 게시글이 없을때 예외가 발생한다
	 */
	public List<RepBoard> findAll() throws FindException{
		return boardDAO.selectAll();
	}
	/**
	 * 게시물번호에 해당하는 게시물을 검색하고,
	 * 조회수를 1증가한다
	 * @param board_no 게시물번호
	 * @return
	 * @throws FindException
	 */
	public RepBoard findByBoard_no(int board_no) throws FindException{
		RepBoard board = boardDAO.selectByBoard_no(board_no);
		try {
			boardDAO.updateBoardCnt(board_no);
		} catch (ModifyException e) {
			throw new FindException(e.getMessage());
		}
		return board;
	}
	/**
	 * 검색어에 만족하는 게시물들을 검색한다.
	 * @param word 검색어. 글제목 또는 작성자
	 * @return 게시물들
	 * @throws FindException 검색어에 만족하는 게시물이 없으면 예외가 발생한다
	 */
	public List<RepBoard> findByBoard_titleORBoard_writer(String word) throws FindException{
		return boardDAO.selectByBoard_titleORBoard_writer(word);
	}
	public void modify(RepBoard board, String board_pwd) throws ModifyException{
		boardDAO.update(board, board_pwd);
	}
	public void remove(int board_no, String board_pwd) throws RemoveException{
		boardDAO.delete(board_no, board_pwd);
	}
}
```

![18](https://user-images.githubusercontent.com/63957819/110929350-36db1980-836b-11eb-93c7-8fa15f6bd3b9.png)

파일을 읽어서 RepBoard객체를 생성한다. 그 이름을 b라는 이름으로 관리를 한다. 이것이 ctx이다. ctx가 참조하는 곳을 찾아보면 b라이는 이름이라는 객체를 찾아올거다. b1변수가 참조하는 메모리는 b라는 이름으로 관리되는 객체 b2변수가 참조하는 메모리도 b라는 이름으로 관리되는 객체를 참조한다.

![19](https://user-images.githubusercontent.com/63957819/110929352-3773b000-836b-11eb-93ca-a6ef95ec25c9.png)

property에 의해서 setBoardDAO(dao)메서드가 자동 호출 

![20](https://user-images.githubusercontent.com/63957819/110929353-3773b000-836b-11eb-9b58-ecfaf9f44420.png)

setBoardDAO()호출됨 출력은 bean태그에 들어있는 property에 의해서 자동 호출이 된 거다.
