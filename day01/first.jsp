<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>first.jsp</title>
</head>
<body>
첫번째 JSP입니다
JSP의 구성요소
html element

jsp element
    1.scripting element
      1)scriptlet : .java파일 _jspService()내부에 들어감 <br>
      				<% int i=10; %>
      				<% out.print(i); %>
      				<% String a = request.getParameter("a"); %>
      2)expression : .java파일 _jspService()내부에 들어감
      				 out.print()가 자동 호출됨.
      				<%=i %>
      3)declaration : .java파일 _jspService()외부에 들어감
      	    메서드, 인스턴스변수 선언시에 사용
      	            <%!int i;//인스턴스변수 선언 %>
      <hr>
      i변수값 : <%=i %>
      i인스턴스변수값: <%=this.i %>
            
    2.directive element
      page directive : .java파일이 generated될때 필요한 정보를 기술(pagedirectivetest.jsp)
      속성들 - import,  
          contentType,
          buffer: 응답 내용이 쌓인 버퍼 크기를 설정 none또는 kb단위로 크기설정 가능. 기본값은 8kb, 
          autoflush,
          errorPage : 페이지에서 예외가 발생하면 자동 이동 될 url을 기술(ex: errorPage= "errorresult.jsp"), 
          isErrorPage : 일반페이지가 아니라 예외처리전용 페이지를 알릴 때 true값으로 기술. exception이라는 미리 선언된 변수 사용가능 (ex: isErrorPage="true")),
          session,
          language(생략가능), pageEncoding(생략가능)
         
      include directive : .java파일이 generated될때 다른 자원을 포함(정적포함)
      taglib directive
      
    3.action tag element
      jsp:include action : 실행시 포함 (동적포함)
      jsp:useBean action : EL로 대체
      jsp:setProperty action
      jsp:getProperty action
      
<hr>
<h1>Expression Language( EL )</h1>
${1+2}는 <%=1+2 %>와 같음<br>
${1/2}<%--0.5 --%>는 <%=1/2 %><%--0 --%>와 다름<br>
${1%2}는 ${1 mod 2}와 같음<br>

<%String str1 = null;
  String str2 = "";
%>
str1값이 null이거나 빈 문자열인가 : ${empty str1 } ,<%=str1 == null || str1.equals("")%><br>
str2값이 null이거나 빈 문자열인가 : ${empty str2 }<br>
<h3>EL 내장객체</h3>
<ul>
	<li>requestScope, sessionScope, applicationScope<br>
	<%--EL처리값이 null이면 빈문자열""로 변환해서 출력한다 --%>
	요청속성 c값: ${requestScope.c},<%out.print(request.getAttribute("c"));%>
	</li>
	<li>param<br>
	요청전달 데이터 id값: ${param.id}, <%=request.getParameter("id") %>
	</li>
	<li>pageContext</li>
	요청객체는 EL기본내장객체가 제공되지 않는다. pageContext기본내장객체를 통해 요청객체를 찾아야한다.<br>
	${pageContext.request.requestURI}, <%=request.getRequestURI() %>
</ul>
</body>
</html>