# day03

WEB-INF는 url로 접근할 수 없는 히든 디렉토리이다. jsp페이지를 일반 사용자가 직접 접근하지 못하게 하려면 WEB-INF에 넣어줘야 한다.

오른쪽 마우스 import> WAR file> boardmvcsample

WEB-INF 밑에 있는 views에 index.jsp는 welcom page로 등록이 안되어 있기 때문에 web.xml파일을 만들어줘야 한다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled.png)

서버사이드 쪽에서의 상대경로 지정을 /부터 시작하면 절대경로를 boardmvcsample까지 설정한 것과 같은 효과이다 웹 컨텍스트 경로 밑에서부터 서버에서의 /이다. 즉 웹컨텍스트 내부이다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%201.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%201.png)

클라이언트 쪽에서의 상대경로 지정을 /부터 시작하면 호스트명 포트번호 다음에 나오는 path부분이다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%202.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%202.png)

절차 상 먼저 프론트 단 1)가짜 데이터가 들어있는 **화면 구성**부터 한다. 그리고 백엔드 단 2)**Service, DAO 구성** 3) **Servlet 구성** 4) **JSP문법 추가**(가짜데이터를 실데이터로 변환**)**한다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%203.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%203.png)

인터페이스에 코멘트가 달려있으면 코멘트가 그대로 적용된다.

비지니스 로직이란 여기서 가장 중요한 로직을 호출한다는 건데 결국 서비스단 메소드를 호출한다 의미이다. JSP가 매번 하는 일은 서블릿이 요청 속성으로 추가해 놓은 호출 결과를 요청 속성 값을 얻어서 실제 데이터로 변환한다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%204.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%204.png)

검색할 값은 제목이나 작성자로 제한을 해놨다. 글이란 검색 값을 입력하고 검색을 클릭했을 경우 이것도 결국 조건에 만족하는 게시물들만 검색해오는 건데 비슷한 패턴으로 가는 코드들은 굳이 서블릿을 또 만들지 말고 하나의 서블릿과 하나의 jsp를 재사용할 수 있다. 

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%205.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%205.png)

이 행에는 tr태그 안에는 td태그가 여러 개가 있는데 첫 번째 td태그가 글 번호이고 그 다음은 제목, 작성자, 작성일자 정보들로 채워져 있다. 글 번호를 포함하고 있는 tr태그가 클릭 되었을 때 상세정보로 볼 수 있게 하자~

가짜 데이터로 화면 구성 한 다음에 백엔드 단에서 service, dao 완성하고 서블릿에서 jsp forward되는거 보고 jsp를 바꿔주면 된다.

프라이머키 게시물 번호는 최대 한 개 게시물만 검색이 된다. 여러 게시물이 검색 될리 없다

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%206.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%206.png)

답글 쓰기를 클릭하게 되면 화면이 하나 보일 거다. 자바스크립트로 div를 show하거나 hide로 처리하면 된다. 답글쓰기 버튼이 클릭 되면 서블릿 요청하러 가야 한다. 여기서 데이터를 전달할 때 부모 글 번호가 필요하다 왜냐하면 답글이기 때문이다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%207.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%207.png)

답글쓰기 성공 되면 응답 결과로 답글 쓰기 성공까지 jsp에 응답해야 되는가 고민해야 한다. 쓰기가 성공을 했다는 과정을 해보자면 결과 값을 이 한 줄 코드를 응답하는 것이 올바른 방법인가.. 아무리 MVC구조를 지켜준다 해도 퍼포먼스만 떨어트리는 작업이 아닌가 고민해볼 필요가 있다. 답글 쓰기 성공 안보여주고 성공이 되면 그 즉시 주소 url을 바꿔줄 것이다. 글 목록으로 이동하고 싶다. 하나는 포워드 방식을 사용해서 이동이 있고 굳이 jsp로 이동하지 않고 다른 페이지로 url바꾸고 싶다 하면 클라이언트 차원에서의 재요청이 있다. 클라이언트 차원에서의 재요청을 하자.

- sqlplus

```jsx
SQL> DROP SEQUENCE board_seq;

Sequence dropped.

SQL> CREATE SEQUENCE board_seq
  2  START WITH 21;

Sequence created.
```

dao는 게시물을 insert한다 개념만 대입하면 된다. 서비스 단에서 답글 쓰기, 글 쓰기 용 서비스를 나눠 놓는 거다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%208.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%208.png)

전체흐름 이미지

BoardWriteServlet에서 get방식은 입력 화면, post 방식은 실제 처리될 db에 글쓰기 내용이 추가될 수 있도록 구성했다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%209.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%209.png)

웹컨텍스트 경로를 jsp든가 servlet에서 충분히 얻을수있다. 모든 jsp페이지에는 el, jstsl기반으로 해주자

웹컨텍스트 path가 boardmvcsample이라면 boardmvcsample이 된다.

war파일 만들기 → boardmvcsample오른쪽 클릭> export> WAR file

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2010.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2010.png)

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2011.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2011.png)

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2012.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2012.png)

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2013.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2013.png)

---

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2014.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2014.png)

톰켓에 배포할 프로젝트 두 개. 프론트 단에는 자바스크립트 소스가 전혀 없고 html로만 구성이 된다. 백엔드 단에는 프론트에 관련된 부분은 전혀 존재하지 않고 WEB-INF에 클래스들만 구성되는 프로젝트가 별개로 있는 거다. 모두 게시판에 관련된 프로젝트인데 하나는 프로젝트 또 다른 하나는 백엔드가 관련되어 있는 프로젝트인 거다. 서로 요청과 응답을 하면서 처리 될 구성이다.

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2015.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2015.png)

일단 프로젝트 두 개(boardback, boardfront)를 만들어보자~

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2016.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2016.png)

index.html페이지가 welcom page의 역할을 한다. 여기서 게시판을 클릭하면 list.html페이지가 section영역에 보일 거다. 

데이터는 오라클에 있고 그 데이터를 사용하는 곳이 boardback이라는 프로젝트이다. 

Same-origin-policy는 같은 프로젝트에 있는 자원을 요청하는 것은 아무 문제가 없다. 같은 프로젝트가 아닌 다른 프로젝트의 자원을 사용하려 하면 보안 상 문제가 되어버린다. 그래서 외부에 노출되어도 관계없는 이미지, CSS, 스크립트 들은 출처 정책에 관여하지 않지만 다른 프로젝트에 있는 서블릿을 요청한다면 외부에서 요청 하는 것을 금지해야 한다. ajax에 위배된다.

- TestServlet.java

```java
package control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("이곳은 boardback프로젝트의 test서블릿입니다");
	}

}
```

- test.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>test.html</title>
</head>
<body>
<h1>(동일출처정책 테스트: boardfront에서 ajax로 boardback의 servlet요청)</h1>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
	$.ajax({
		url: "http://localhost:8888/boardback/test",
		success: function(data){
			alert(data);
		},
		error: function(jqXHR){
			alert("오류:" + jqXHR.status);
		}
	});
</script>
</body>
</html>
```

실행결과>

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2017.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2017.png)

ipconfig로 내 아이피를 확인 한 다음 로컬호스트로 띄우는게 아니라 자기 ip주소를 띄우는 거다. 실행하면 오류가 발생한다. 동일 출처 정책을 반영한 거다. 오류가 나야 정상이다.

기본 출처가 동일출처 정책에 위배 됐다는 메시지이다. 서로 다른 자원 접근하는 거는 불가능하다. 즉 ajax요청을 할 수 없는거다. 접근하려면 백엔드 쪽의 Servlet소스 header에 외부에서 나 요청해도 괜찮다라고 설정 해주면 된다. → response.setHeader("Access-Control-Allow-Origin", "*");

실행결과>

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2018.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2018.png)

---

![day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2019.png](day03%2022c1e70fe81f489ca158197c541966c6/Untitled%2019.png)

글쓰기는 사용자로부터 입력란을 갖고 있다 write.html페이지를 만들고 section부분에 입력란 부분만 보여주면 된다

클릭해서 해주는 것은 index.html 작성자가 해줘야 될 일이고, 글 쓰기 관리자는 write.html 페이지를 만들어줘야 한다