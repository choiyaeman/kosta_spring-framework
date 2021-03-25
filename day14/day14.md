# day14

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled.png)

상세주소를 입력하지 않는 case

```xml
SQL> select id, pwd, name, addr1 from customer;

ID         PWD        NAME
---------- ---------- ------------------------------
ADDR1
------------------------------------------------------------
yaema      1234       최예만
1층

a9         1234       c
```

실행결과>

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%201.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%201.png)

- mybatis-config.xml

```xml
..
.
  <settings>
  	<setting name="jdbcTypeForNull" value="Null"/>
  </settings>
..
.
```

→ setNull이라는 문제가 발생할 경우 위에 코드처럼 해결하면 된다..

---

 

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%202.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%202.png)

String → view이름을 결정에서 return 하겠다 의미. attribute는 model로 관리가 된다. 그러므로 model로 가지고 오면 된다.

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%203.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%203.png)

바인딩 된 c에는 아이디, 이름, 비밀번호도 있고 c값을 model에 어트리뷰트에 추가해야 jsp이동 된 view단에서 내용을 확인할 수 있다. 다시 한번 활용해야 한다면 model어트리뷰트를 사용해야 한다. 사용하지 않고 파라미터 c를 쓰면 jsp단에서 가입 내용을 보고 싶으면 아이디를 꺼내오려면 요청 전달 데이터가 있기는 한데 c를 받아오는 게 아니라 id값으로 받아와야 한다. 바인딩 된 Customer라는 파라미터 객체 자체를 이동 된 jsp에서 사용하기 위해서는 결국 model의 attribute로 추가 해줘야 한다.

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%204.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%204.png)

비지니스 하고 일을 하는 Controller, 사용자들하고 일을 하는 영역 viewer 두 개로 나뉜다.

---

실습>

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%205.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%205.png)

- BoardController.java

```java
..
.
public class BoardController {
	@Autowired
	private RepBoardService service;

	@RequestMapping("/list")
	public ModelAndView list(String word) throws FindException{
		log.info("검색어:" + word);
		List<RepBoard> list;

		ModelAndView mnv = new ModelAndView();

		//2. 비지니스로직 호출
		if(word == null) { //전체검색
			list = service.findAll();
		}else { //검색어에 만족하는 검색
			list = service.findByBoard_titleORBoard_writer(word);
		}
		mnv.addObject("list", list);
		//mnv.setViewName("list");

		return mnv;
	}
	@RequestMapping("/detail")
	public ModelAndView detail(@RequestParam(value = "board_no", defaultValue = "0") 
	int board_no)  throws FindException {

		ModelAndView mnv = new ModelAndView();
		//try {
		//2. 비지니스로직 호출
		RepBoard board = service.findByBoard_no(board_no);
		//3. 요청속성으로 추가
		//request.setAttribute("board", board);
		mnv.addObject("board", board);
		//mnv.setViewName()생략하면 URL인 mnvsetViewName("board/detail")과 같음
		//} catch (FindException e) {
		//	mnv.addObject("exception", e);
		//	mnv.setViewName("board/error");
		//	e.printStackTrace();
		//}
		return mnv;
	}
	@RequestMapping("/modify")
	public ModelAndView modify(RepBoard board, 
			String certify_board_pwd) throws ModifyException{
		ModelAndView mnv = new ModelAndView();

		service.modify(board, certify_board_pwd);
		mnv.setViewName("redirect:/board/list");

		return mnv;
	}
	@RequestMapping("/remove")
	public ModelAndView remove(int board_no, String certify_board_pwd) throws RemoveException{
		ModelAndView mnv = new ModelAndView();
		//		try {
		service.remove(board_no, certify_board_pwd);
		//			String contextPath = request.getContextPath();
		//			response.sendRedirect(contextPath + "/list");
		mnv.setViewName("redirect:/board/list");

		return mnv;
	}
	@RequestMapping("/reply")
	public ModelAndView service(RepBoard board) throws AddException {
		ModelAndView mnv = new ModelAndView();
		//try {
		service.writeReply(board);
		mnv.setViewName("redirect:/board/list");

		return mnv;
	}
	@GetMapping("/write")
	public void showWrite() {}

	@PostMapping("/write")
	public ModelAndView write(RepBoard board) throws AddException{
		ModelAndView mnv = new ModelAndView();
		service.writeBoard(board);
		mnv.setViewName("redirect:/board/list");

		return mnv;
	}
}
```

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%206.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%206.png)

- variables.jsp

```jsx
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<c:set var="contextPath" value="${pageContext.request.contextPath }"/>
<c:set var="boardPath" value="/board"/>
```

- index.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/variables.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인(index.jsp)</title>
<script>
alert("${contextPath}${boardPath}");
</script>
</head>
<body>
<!-- <header></header> -->

<%@include file="/WEB-INF/views/header.jsp" %>
<section>
<h1>답변형 게시판 MVC실습</h1>
</section>
<!-- <footer></footer> -->
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>
```

- list.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
**<%@include file="/WEB-INF/views/variables.jsp" %><%--jstl용 변수들이 선언 : contextPath, boardPath --%>**
**<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>**
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%-- <c:set var="contextPath" value="${pageContext.request.contextPath }"/> --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list.jsp</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
<script>
$(function(){
	//--게시물 클릭 시작--
	$("section>table tr>td").click(function(event){
		var board_no = $(event.target).parent().children("td.board_no").html().trim();
		$("section>form>input[name=board_no]").val(board_no);
		var $formObj = $("form");
		$formObj.attr("method", "get");
		$formObj.attr("action", "**${contextPath}${boardPath}**/detail");
		$formObj.submit();
		return false;
	});
	//--게시물 클릭 끝--
	
	//--검색버튼 클릭 시작--
	$("section>form>input[type=button]").click(function(){
		var $formObj = $("form");
		$formObj.attr("method", "get");
		$formObj.attr("action", "**${contextPath}${boardPath}**/list");
		$formObj.submit();
		return false;
	});
	//--검색버트 클릭 끝--
});
</script>
<style>
*{
  box-sizing: border-box;
}
section>table{
  border: 1px solid; border-collapse: collapse; width:50%;
}
section>table tr>td{
  border: 1px solid;
}
section>table tr>td.board_no{ width: 10%; text-align: right; }
section>table tr>td.board_title{ width: 40%; }
section>table tr>td.board_writer{ width: 20%; }
section>table tr>td.board_dt{ width: 20%; }
section>table tr>td.board_cnt{ width: 10%; text-align: right; }
</style>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<section>
<form>
  <input type="hidden" name="board_no">
  <input type="search" name="word"><input type="button" value="검색">
</form>
<table>
<c:forEach items="${requestScope.list}" var="board">
 <tr>
   <td class="board_no">${board.board_no}</td>
   <td class="board_title">
      <c:forEach begin="2" end="${board.level}" step="1">&#10149;
      </c:forEach>${board.board_title}
   </td>
   <td class="board_writer">${board.board_writer}</td>
   <td class="board_dt">
   **<fmt:formatDate value="${board.board_dt}" pattern="yyyy-MM-dd"/> </td>**
   <td class="board_cnt">${board.board_cnt}</td>
 </tr>
</c:forEach>
</table>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>
```

- detail.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
**<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@include file="/WEB-INF/views/variables.jsp" %>**
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<c:set var="board" value="${requestScope.board}"/>
<%-- <c:set var="contextPath" value="${pageContext.request.contextPath }"/>
 --%>
 <!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list.jsp</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
<script>
$(function(){
	//--답글쓰기 클릭 시작--
	$("section>div.detail>a.reply").click(function(){
		$("section>div.reply").show();
		$("section>div.remove").hide();
		$("section>div.modify").hide();
		return false;
	});
	//--답글쓰기 클릭 끝--
	
	//--수정링크 클릭 시작--
	$("section>div.detail>a.modify").click(function(){
		$("section>div.reply").hide();
		$("section>div.remove").hide();
		$("section>div.modify").show(); //수정하기 창띄우기
		return false;
	});
	//--수정링크 클릭 끝--
	
	//--삭제링크 클릭 시작--
	$("section>div.detail>a.remove").click(function(){
		$("section>div.reply").hide();
		$("section>div.modify").hide(); 
		$("section>div.remove").show(); //삭제하기 창띄우기
		return false;
	});
	//--삭제링크 클릭 끝--
	
});
</script>
<style>
*{
  box-sizing: border-box;
}

</style>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<section>

<div class="detail">
글번호 :${board.board_no}<br>
제목 : ${board.board_title}<br>
작성자 :${board.board_writer}<br>
작성일자 :**<fmt:formatDate value="${board.board_dt}" pattern="yyyy-MM-dd"/><br>**
조회수:${board.board_cnt }<br>
<hr>
<a href="#" class="reply">답글쓰기</a>
<a href="#" class="modify">수정</a>&nbsp;&nbsp;
<a href="#" class="remove">삭제</a>
</div>
<div class="reply" style="display:none;">
  <form method="post" action="**${contextPath}${boardPath}**/reply">
   <input type="hidden" name="parent_no" value="${board.board_no}">
      답글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="답글쓰기"> 
  </form>
  
</div>
<div class="modify" style="display:none;">
  <form method="post" action="**${contextPath}${boardPath}**/modify">
    글번호  <input type="text" name="board_no" value="${board.board_no}" readonly><br>
    제목 : <input type="text" name="board_title" value="${board.board_title}"><br>
    작성자 :${board.board_writer}<br>
    작성일자 :${board.board_dt }<br>
    조회수: ${board.board_cnt }<br>
    기존 비밀번호 : <input type="password" name="certify_board_pwd" required><br>
    변경할 비밀번호:<input type="password" name="board_pwd" required><br>
  <input type="submit" value="수정"> 
  </form>
</div>
<div class="remove" style="display:none;">
  <form method="post" action="**${contextPath}${boardPath}**/remove">
   <input type="hidden" name="board_no" value="${board.board_no}">
      기존 비밀번호 : <input type="password" name="certify_board_pwd" required><br>
   <input type="submit"  value="삭제">
  </form>
</div>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>
```

- write.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/variables.jsp" %>
<c:set var="board" value="${requestScope.board}"/>
<%-- <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
 --%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list.jsp</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
<script>

</script>
<style>
*{
  box-sizing: border-box;
}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<section>
  <form method="post" action=**"<c:out value="${contextPath}${boardPath}/write" />"**>
      글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="글쓰기"> 
  </form>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>
```

---

참고>

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%207.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%207.png)

---

![day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%208.png](day14%204e0d14f4de5a47058182ab013ca8ced0/Untitled%208.png)