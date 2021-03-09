# day01  

![1](https://user-images.githubusercontent.com/63957819/110299060-9ddd9300-8038-11eb-8fad-7e5cc8f3ae0d.png)

요즘 경향을 살펴보자 하면 B번 형태의 경향이 더 많다 했는데 그렇다고 B번 형태로만 구조를 항상 만들 수 는 없다. 실제 현업에서 구성되어 있는 프로젝트가 A같은 구조가 아직도 많다.

![2](https://user-images.githubusercontent.com/63957819/110299066-9f0ec000-8038-11eb-9a38-8446a32d4125.png)

MVC구조로는 요청을 받는 servlet과 응답을 받는 view가 있다. 응답 된 결과 값이 링크 클릭으로 이동했을 경우 기존 페이지가 지워지고 응답 내용으로만 채워지게 되어있다. 응답 내용이 로그인 성공 또는 로그인 실패라 해보자. 화면을 채우는 것 까지는 좋은데 그 위의 메뉴들이 없어질 거 아니겠어요.. 요청된 컨트롤러에 집중되어야 하기 때문에 컨트롤러에 따라서 다르다. 응답 된 jsp결과로는 성공과 실패 두 개의 결과 값만 두 개의 비중을 차지한다. 윗단의 메뉴를 채우기 위해서 다시 또 로그인, 로그아웃, 가입 메뉴를 또 똑같이 그리기는 어려울 거다. 그래서 메뉴를 해당 jsp페이지에서 include를 시키는 거다. 메뉴들은 포함.. 아래쪽 footer내용도 똑같은 내용으로 구성되어있으면 포함 시키면 된다. 상품 목록 보기 용 응답 결과로는 당연히 상품 내용일 것이다. 윗단의 메뉴, 아랫단의 footer부분은 항상 메인 페이지에 따라가는 부분이기 때문에 포함되어야 한다. 그러므로 include해야 한다.

include태그, include지시자를 이용해서 포함하는 두 개의 방법이 있다.

![3](https://user-images.githubusercontent.com/63957819/110299069-9fa75680-8038-11eb-8b2b-50b8d87584c4.jpg)

자바 bean은 자바 컴포넌트를 의미한다. 여기서 컴포넌트란 재 사용성이 높은 큰 덩어리 즉 클래스들의 모임을 말한다. 사용자들이 값을 사용할 수 있도록 노출을 할 수 있어야 한다. 해당 별 컴포넌트 색상, 크기도 지정할 수 있도록 즉 접근이 되어 있을 수 있도록 되어야 한다.  멤버 변수를 외부에서 직접 접근 안하고 메서드를 public을 선언해서 set메서드에 접근할 수 있도록 한다. 자바 컴포넌트를 다른 말로 자바 빈이라 부른다. 자바 빈이 되기 위해서는 첫 번째 public 클래스어야 하고 두 번째 public 매개변수 없는 생성자가 있어야 한다.

- first.jsp

```jsx
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
```

- userbeantest.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>usebeantest.jsp</title>
</head>
<body>
<jsp:useBean id="c" class="com.my.vo.Customer" scope="request"></jsp:useBean>
<jsp:setProperty property="pwd" name="c" value="p1"/>
<jsp:getProperty property="pwd" name="c"/>

<%--
Customer c = request.getAttribute("c");
if(c == null){
	c = new Customer();
	request.setAttribute("c", c);
}
c.setPwd("p1");
out.print(c.getPwd());
 --%>
</body>
</html>
```

다섯 줄 쓰는 것 보다 jsp:useBean 쓰는 것을 선호한다. request대신에 session을 쓰면 session.getAttribute랑 같은 효과이고 application은 application.getAttribute랑 같은 효과이다.

session은 클라이언트 별, request는 요청 단위로, application은 Servlet Context 타입으로 만들어지는 객체이다. 

Servlet context객체를 getRealPath 메서드를 쓰게 되면 지금 사용 중인 프로젝트의 실제 경로를 얻어 낼 수 있다. attribute가 갖고 있는 객체는 세 개인데 https session, servlet, request있다.

useBean 태그는 jsp코드를 단순화 시키기 위한 문법이다.

![4](https://user-images.githubusercontent.com/63957819/110299077-a0d88380-8038-11eb-9548-ea2fc5724c46.png)

$() : jQuery, ${} : EL문법 → EL은 서버 사이드에서 실행, jQuery는 클라이언트 사이드에서 실행되는 문법이다.

EL표기법은 처리 값이 null이면 빈 문자열""로 변환해서 출력해주므로 expression보다는 EL표기법을 권장한다. 요청 객체 자체를 찾으려면 pageContext부터 출발해야 한다. EL표기법으로는 메서드로 호출 하는 게 아니라 점찍고 get메서드 빼버리고 property이름만 쓰면 된다.

![5](https://user-images.githubusercontent.com/63957819/110299084-a209b080-8038-11eb-849e-2d0ad8008e57.png)

${_._} → 점 연산자 앞에 자바빈객체, 맵 뒤에는 프로퍼티, 키가 올 수 있다. 

현재 사용 중인 Jsp정보를 담고 있는 객체가 pageContext이다.

![6](https://user-images.githubusercontent.com/63957819/110299098-a3d37400-8038-11eb-950b-a1ebdf124a18.png)

${requestScope.c.pwd} → request에 저장되어있는 어트리뷰트 중에서 c를 찾고 c라는 어트리뷰트 중에서 pwd를 찾는다.

![7](https://user-images.githubusercontent.com/63957819/110299107-a635ce00-8038-11eb-9fe3-6d2f387aa334.png)

getProperty태그는 확장성이 떨어진다. 그러므로 EL 표기법을 권장한다.

EL은 expression language로 표현 언어를 집중하기 때문에 변수를 선언한다 거나 if조건, 반복문을 갖지 않는다. 갖기 위해서는 JSTL(Jsp Standard Tag Library)문법이 필요하다. 원래 이걸 apache그룹에서 JSTL을 제공했다. EL은 표현하는 거에만 집중한다고 생각하면 되고 조건, 반복 처리를 하기 위해서는 JSTL문법이 필요하다는 것을 알면 된다. 

[https://mvnrepository.com/artifact/javax.servlet/jstl/1.2](https://mvnrepository.com/artifact/javax.servlet/jstl/1.2) → jar파일로 다운로드 → lib에 붙여넣기

![8](https://user-images.githubusercontent.com/63957819/110299121-a8982800-8038-11eb-83df-38ecadc365bb.png)

- jstltest.jsp

```jsx
<%@page import="java.util.ArrayList"%>
<%@page import="com.my.vo.Product"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>jstltest.jsp</title>
</head>
<body>
<%--c:set 변수 선언용 JSTL태그 --%>
<c:set var="num" value="123"></c:set>
<%--c:if 조건문용 JSTL태그 --%>
<c:if test="${num%2==0}">
짝수입니다
</c:if>

<c:choose>
<c:when test="${num%2==0}">
짝수입니다
</c:when>
<c:otherwise>
홀수입니다
</c:otherwise>
</c:choose>

<hr>
<%--요청전달데이터opt값이 add인경우는 '가입작업을 선택했습니다'를 출력
                     findAll인 경우는 '조회작업을 선택했습니다'를 출력
                                          없는 경우(null이거나 ""인 경우)는 '작업을 선택하세요를 출력'하시오.
    http://localhost:888/myback/jstltest.jsp?opt=add
    http://localhost:888/myback/jstltest.jsp?opt=findAll
    http://localhost:888/myback/jstltest.jsp
 --%>
<c:set var="optValue" value="${param.opt}"/>
<c:choose>
  <c:when test="${optValue == 'add'}">가입작업을 선택했습니다</c:when>
  <c:when test="${optValue == 'findAll'}">조회작업을 선택했습니다</c:when>
  <c:when test="${empty optValue}">작업을 선택하세요</c:when>
  <c:otherwise>그외의 작업을 선택했습니다</c:otherwise>
</c:choose>

<hr>
<%--c:forEach 반복문용 JSTL태그 --%>
<c:forEach begin="10" end="20" step="2" var="i">
${i}&nbsp;&nbsp;
</c:forEach>
<hr>

<% //servlet에서 아래작업수행후 forward된 경우
List<Product> list = new ArrayList<>();
list.add( new Product("c0001", "아메리카노", 1000));
list.add( new Product("c0002", "아이스아메리카노", 1000));
list.add( new Product("c0003", "라테", 1500));
request.setAttribute("list", list);
%>
<c:forEach items="${requestScope.list}" var="p">
  ${p.prod_no} : ${p.prod_name} : ${p.prod_price}:
  <fmt:formatNumber pattern="#,##0">${p.prod_price}</fmt:formatNumber>
  <br>
</c:forEach>

<%--
List<Product> list = request.getAttribute("list");
DecimalFormat df = new DecimalFormat("9,990");
for(product p: list){
    df.format(p.getProd_price());
}
 --%>

<c:set var="str" value="최예만자바JSPHTMLELJSTLSPRING"/>
<c:set var="str1" value="오라클"/>
<c:if test="${fn:contains(str, str1)}">
</c:if>

</body>
</html>
```

prefix 접두어 core모듈 의미. Jstl 기본 문법이 자바 기반이 아니라 EL기반이기 때문에 ${} 연산식을 써줘야 한다. else태그는 없다 그거를 대신할 수 있는 것이 choose태그이다. 

0패턴은 값이 없으면 무시해버리고 0으로 처리해라 의미

jsp가 직접 sql를 다룰 일이 없으므로 sql모듈은 할 필요가 없다.
