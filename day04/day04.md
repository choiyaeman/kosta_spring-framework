# day04

![day04%20751879f290d7432885bab2bef761c0f3/Untitled.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled.png)

welcom page에서 메뉴를 클릭해서 요청하는 url은 화면이 요구되어야 한다. 프론트에게 요구를 한다. 그 결과 값을 웹 브라우저에서 보여준 다음에 프론트에서 이벤트가 발생했을 때 실제 비지니스 로직을 처리하겠다 하면 백엔드 쪽으로 간다. 서블릿이 쓰일 출처와 html 쓰일 출처가 서로 다르다. ajax를 요청할 때 보안 문제가 걸린다. 보안 문제를 해결하기 위해서는 요청되는 쪽에서 보안 정책을 설정해줘야 한다. 

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%201.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%201.png)

status: -1 → 응답은 되었는데 글쓰기가 실패할 경우 

- 자바스크립트로 XMLHttpRequest 사용하기

```java
<script>
    var xhr = new XMLHttpRequest();
    xhr.open("GET" , encodeURI(url) , true);
    xhr.onreadystatechange = function() {
        if(xhr.readyState == 4 && xhr.status == 200)
        {
            alert(xhr.responseText);
        }
    }
    xhr.send();
</script>
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
			url: $writeFormObj.attr("action"),
			method: $writeFormObj.attr("method"),
			data: $writeFormObj.serialize(), //form이 갖고있는 input태그들의 이름과 값이 모두 이콜 연산자를 갖는 문자열이 된다
			success:function(responseObj){ //응답성공이란 응답완료(readyState가 4), 응답코드가 200인 경우를 말함
				if(responseObj.status == 1){ //응답된 json객체의 status값이 1일 경우. 글쓰기 작업이 성공된 case
					$("header>ul>li>a.list").trigger("click"); //a태그들은 메뉴들의 영역. 게시판 메뉴 클릭이벤트 강제 발생
				}else{ //글쓰기 작업이 실패
					alert(responseObj.msg);
				}
			},
			error:function(jqXHR){ //응답자체에 문제가 있는 것
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
<form method="post" action="/boardback/write">
     글제목: <input type="text" name="board_title"><br>
     작성자 : <input type="text" name="board_writer"><br>
     비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="글쓰기"> 
</form>
</div>
```

- BoardWriteServlet.java

```java
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
```

get방식의 요청은 메시지 바디를 사용하지 않는 요청, post방식의 요청은 메시지 바디를 사용하는 요청

---

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%202.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%202.png)

글 하나를 클릭하면 클릭 된 글이 나타나야 하고 상세보기가 보여야 한다. 상세보기 작업이 필요하다.

클릭 이벤트가 발생했을 때 html태그가 갖고 있는 문서를 detail.html페이지를 만들 것이다. detail.html 페이지에는 글번호, 제목, 작성자, 작성일자, 조회수 가 텅 비어있는 html 페이지인데 먼저 로드가 된다면 ajax요청을 해서 ajax요청된 결과 값을 가져와서 조금 전 로드 해놓은 detail.html 영역에 채우기 작업을 한다. 아니면 1, 2번 절차를 안하고 3번 절차부터 해도 된다. 하지만 문제가 뭐냐면 클릭하자마자 ajax요청을 하면 성공 된 내용을 특정 영역에 채워야 한다. 직접 div태그 내용을 생성해서 DOM트리에 추가하는 방법이 있다. dom트리에다 자바스크립트 객체를 만들면 작업이 너무 많아진다. load라는 작업이 ajax요청이기 때문에 결국 작업을 두번하게 되는 것이다. 

파란색처럼 하면 오버헤드가 많이 걸린다 빨간색처럼 하려면 div영역에 DOM트리를 직접 만들어야한다.

일단 html페이지를 만들어두고 load한 후에 ajax요청하는 코드로 구성해보자~

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%203.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%203.png)

* : 0 또는 many 여러 개가 올 수 있고 없어도 된다 
? : 0 또는 1
ECMAScript : ⇒ 화살표 연산자, 람다식                                                                                                                 Complete : 로드가 완료되면 

- detail.html

```html
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
-->
<script>
    $(function () {

        //--답글쓰기링크 클릭 시작--
        $("section>div.detail>a.reply").click(function () {
            $("section>div.reply").show();
            $("section>div.remove").hide();
            $("section>div.modify").hide();
            return false;
        });
        //--답글쓰기링크 클릭 끝--

        //--수정링크 클릭 시작--
        $("section>div.detail>a.modify").click(function () {

            $("section>div.reply").hide();
            $("section>div.remove").hide();

            $("section>div.modify>form>input[name=board_no]").val($("div.detail>span.board_no").html());
            $("section>div.modify>form>input[name=board_title]").val($("div.detail>span.board_title").html());
            $("section>div.modify").show(); //수정하기 창띄우기
            return false;
        });
        //--수정링크 클릭 끝--

        //--삭제링크 클릭 시작--
        $("section>div.detail>a.remove").click(function () {
            $("section>div.reply").hide();
            $("section>div.modify").hide();

            $("section>div.remove>form>input[name=board_no]").val($("div.detail>span.board_no").html());
            $("section>div.remove").show(); //삭제하기 창띄우기
            return false;
        });
        //--삭제링크 클릭 끝--

        //--답글쓰기 창에서 답글쓰기 클릭 시작--
        var $replyFormObj = $("section>div.reply>form");
        $replyFormObj.submit(function () {
            var parent_no = $("section>div.detail>span.board_no").html();
            $replyFormObj.find("input[name=parent_no]").val(parent_no);

            $.ajax({
                url: $replyFormObj.attr("action"),
                method: $replyFormObj.attr("method"),
                data: $replyFormObj.serialize(),
                success: function (responseObj) {
                    if (responseObj.status == 1) {
                        $("header>ul>li>a.list").trigger("click");
                    } else {
                        alert(responseObj.msg);
                    }
                },
                error: function (jqXHR) {
                    alert("에러:" + jqXHR.status);
                }
            });
            return false;
        });
        //--답글쓰기 창에서 답글쓰기 클릭 끝--

        //--수정하기 창에서 수정 클릭 시작--
        var $modifyFormObj = $("section>div.modify>form");
        $modifyFormObj.submit(function () {
            $.ajax({
                url: $modifyFormObj.attr("action"),
                method: $modifyFormObj.attr("method"),
                data: $modifyFormObj.serialize(),
                success: function (responseObj) {
                    if (responseObj.status == 1) {
                        $("header>ul>li>a.list").trigger("click");
                    } else {
                        alert(responseObj.msg);
                    }
                },
                error: function (jqXHR) {
                    alert("에러:" + jqXHR.status);
                }
            });
            return false;
        });
        //--수정하기 창에서 수정 클릭 끝--

        //--삭제하기 창에서 삭제 클릭 시작--
        var $removeFormObj = $("section>div.remove>form");
        $removeFormObj.submit(function () {
            $.ajax({
                url: $removeFormObj.attr("action"),
                method: $removeFormObj.attr("method"),
                data: $removeFormObj.serialize(),
                success: function (responseObj) {
                    if (responseObj.status == 1) {
                        $("header>ul>li>a.list").trigger("click");
                    } else {
                        alert(responseObj.msg);
                    }
                },
                error: function (jqXHR) {
                    alert("에러:" + jqXHR.status);
                }
            });
            return false;
        });
        //--삭제하기 창에서 삭제 클릭 끝--
    });
</script>
<style>
    * {
        box-sizing: border-box;
    }
</style>

<div class="detail">
    글번호 :<span class="board_no">29</span><br>
    제목 : <span class="board_title">aa</span><br>
    작성자 :<span class="board_writer">a</span><br>
    작성일자 :<span class="board_dt">2021-03-11</span><br>
    조회수:<span class="board_cnt">23</span><br>
    <hr>
    <a href="#" class="reply">답글쓰기</a>&nbsp;&nbsp;
    <a href="#" class="modify">수정</a>&nbsp;&nbsp;
    <a href="#" class="remove">삭제</a>
</div>

<div class="reply" style="display:none;">
    <form method="post" action="/boardback/reply">
        <input type="hidden" name="parent_no">
        답글제목: <input type="text" name="board_title"><br>
        작성자 : <input type="text" name="board_writer"><br>
        비밀번호:<input type="password" name="board_pwd" required><br>
        <input type="submit" value="답글쓰기">
    </form>
</div>

<div class="modify" style="display:none;">
    <form method="post" action="/boardback/modify">
        글번호 <input type="text" name="board_no" readonly=""><br>
        제목 : <input type="text" name="board_title"><br>
        작성자 :<span class="board_writer"></span><br>
        작성일자 :<span class="board_dt"></span><br>
        조회수: <span class="board_cnt"></span><br>
        기존 비밀번호 : <input type="password" name="certify_board_pwd" required=""><br>
        변경할 비밀번호:<input type="password" name="board_pwd" required=""><br>
        <input type="submit" value="수정">
    </form>
</div>
<div class="remove" style="display:none;">
    <form method="post" action="/boardback/remove">
        <input type="hidden" name="board_no">
        기존 비밀번호 : <input type="password" name="certify_board_pwd" required=""><br>
        <input type="submit" value="삭제">
    </form>
</div>
```

- BoardDetailServlet.java

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
```

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%204.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%204.png)

답글쓰기 링크를 클릭했다면 div가 보여진다. 답글쓰기 버튼이 클릭 되었을 때 detail.html에 ajax요청이 되어 전달이 된다. BoardReplyServlet이 결과를 응답하게 되면 json형식으로 응답한다. 

- BoardReplyServlet.java

```java
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

@WebServlet("/reply")
public class BoardReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//CORS정책
		response.setHeader("Access-Control-Allow-Origin", "*");
		//응답형식지정
		response.setContentType("application/json;charset=UTF-8");
		//응답출력스트림 얻기
		PrintWriter out = response.getWriter();
		
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
		
		//json용 JACKSON Lib활용
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.writeReply(board);
			map.put("status", 1);
		} catch (AddException e) {
			//request.setAttribute("exception", e);
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//3.응답하기
		out.print(mapper.writeValueAsString(map));
	}

}
```

- BoardModifyServlet.java

```java
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
import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@WebServlet("/modify")
public class BoardModifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*"); //CORS해결
		response.setContentType("application/json;charset=UTF-8"); //응답형식지정
		PrintWriter out = response.getWriter(); //응답출력스트림얻기
		
		//1.요청전달데이터 얻기
		request.setCharacterEncoding("utf-8");
		String path ="/WEB-INF/views/error.jsp";
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
		String board_title = request.getParameter("board_title");
		String certify_board_pwd = request.getParameter("certify_board_pwd");
		String board_pwd = request.getParameter("board_pwd");
		RepBoard board = new RepBoard();
		board.setBoard_no(board_no);
		board.setBoard_title(board_title);
		board.setBoard_pwd(board_pwd);
		
		//json응답을 위해 Jackson Lib활용
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.modify(board, certify_board_pwd);
			map.put("status", 1);
		} catch (ModifyException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//3.응답하기
		out.print(mapper.writeValueAsString(map));
	}
}
```

- BoardRemoveServlet.java

```java
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
import com.my.exception.RemoveException;
import com.my.service.RepBoardService;

@WebServlet("/remove")
public class BoardRemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*"); //CORS해결
		response.setContentType("application/json;charset=UTF-8"); //응답형식지정
		PrintWriter out = response.getWriter(); //응답출력스트림얻기
		
		//1.요청전달데이터 얻기
		request.setCharacterEncoding("utf-8");
		String path ="/WEB-INF/views/error.jsp";
		String strBoard_no = request.getParameter("board_no");
		int board_no = Integer.parseInt(strBoard_no);
		String certify_board_pwd = request.getParameter("certify_board_pwd");
		
		//json응답을 위해 Jackson Lib활용
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.remove(board_no, certify_board_pwd);
			map.put("status", 1);
		} catch (RemoveException e) {
			request.setAttribute("exception", e);
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//3.응답하기
		out.print(mapper.writeValueAsString(map));
	}
}
```

---

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%205.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%205.png)

주로 아파치 서버에는 자바 웹 엔진이 없는 프론트 프로젝트를 배포하고 톰캣 서버에는 자바 관련 프로젝트인 백엔드 프로젝트를 배포한다.

톰캣 서버는 오픈소스 프로젝트로 구성되어 있는 서버이다 보니까 불안정하다. 근데 아파치 서버는 대단히 안전한 서버이다.  불안정하지만 톰캣 서버를 쓰는 이유는 톰캣 서버 안에는 서블릿과 jsp실행시켜줄 수 있는 엔진이 있다.

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%206.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%206.png)

제우스 또는 웹로직 또는 웹스퍼어를 WAS라 부른다. 굉장히 안정적인 서버들이다. 거기에 다 덧붙여서 트래픽 처리를 잘 해준다. 빠른 트랜잭션을 해준다. 동시다발적인 일 처리를 빠르게 대처 해준다. 예를 들어 예약 시스템을 갖고 있는 것들은 필수로 WAS가 필요하다. 미들웨어로 제우스가 있다.

미들웨어는 말 그대로 중간 역할의 의미이다. 대용량 요청이 들어왔을 때 안정적인 트랜잭션 처리를 해준다.

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%207.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%207.png)

서블릿 객체의 수를 줄일려면 서블릿 객체 하나만 만들면된다.

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%208.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%208.png)

Property> Web Project Settings> boardbackController로 이름 바꿔주기

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%209.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%209.png)

위와 같이 안할경우 그대로 복붙한 상태에서 컨텍스를 복붙하면 배포하려는 프로젝트 이름 배포하고 Modules를 보면 path가 boardback이라는 똑같은 이름으로 배포되게 된다. 그러므로 Property 설정에서 이름 바꿔주기!!

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%2010.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%2010.png)

* → 확장자 모든 것의 의미. 

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%2011.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%2011.png)

구체화된 url패턴이 우선이다. /*이면 html, img파일 모든 servlet을 타게 된다.

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%2012.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%2012.png)

Servlet 하나만 만들기로 했음으로 BoardListServlet, DetailServlet은 더 이상 Servlet으로 만들면 안된다. HttpServlet으로부터 상속 받지 않게 설정. 일반 클래스니깐 메서드 이름이 더 이상 doGet, doPost, Service로 설계 되지 않아도 된다. 서블릿은 아니지만 실제 비지니스 로직을 호출하고 결과값을 응답하는 놈들이기 때문에 부모 클래스나 인터페이스를 이용해서 묶어 줄 거다. 서블릿과의 연결을 끊고 나름대로 인터페이스를 만들어서 하위 클래스를 설계할 거다. 이름도 BoardListController, BoardDetailController로 바꿔주고 메서드도 통일된 메서드를 갖도록 하자

![day04%20751879f290d7432885bab2bef761c0f3/Untitled%2013.png](day04%20751879f290d7432885bab2bef761c0f3/Untitled%2013.png)

Controller 인터페이스 만들기

- Controller.java

```java
package control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {
   String execute(HttpServletRequest request, 
		           HttpServletResponse response
		         ) throws Exception;
}
```

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

//@WebServlet("/list")
public class BoardListController implements Controller {
	
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();
	
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json;charset=UTF-8");
		//PrintWriter out = response.getWriter();
		
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

//@WebServlet("/detail")
public class BoardDetailController implements Controller {
	private static final long serialVersionUID = 1L;
	private RepBoardService service = new RepBoardService();	
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//CORS정책
		response.setHeader("Access-Control-Allow-Origin", "*");
		//응답형식지정
		response.setContentType("application/json;charset=UTF-8");
		//응답출력스트림 얻기
		//PrintWriter out = response.getWriter(); 
		
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

- BoardServlet.java

```java
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
```