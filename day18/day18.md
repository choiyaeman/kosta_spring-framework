# day18

![1](https://user-images.githubusercontent.com/63957819/113131898-be27f880-9258-11eb-9ac7-284a557c7563.png)

![2](https://user-images.githubusercontent.com/63957819/113131904-bf592580-9258-11eb-9b58-f12a8a5eb101.png)

- SampleController.java

```java
package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //servlet-context.xml에 context:component-scan 설정필요!
@RequestMapping("/sample/*")
public class SampleController {
	
	@GetMapping("/all")
	public void all() { //view이름이 /sample/all과 같음, view는 /WEB-INF/views/sample/all/all.jsp가 됨	
		System.out.println();
	}
	
	@GetMapping("/member")
	public void member() { //view이름이 /sample/member과 같음, view는 /WEB-INF/views/sample/member/member.jsp가 됨	
	}
	
	@GetMapping("/admin")
	public void admin() { //view이름이 /sample/admin과 같음, view는 /WEB-INF/views/sample/admin/admin.jsp가 됨	
	}
}
```

- servlet-context.xml

```xml
<context:component-scan base-package="com.my.security" />
<context:component-scan base-package="controller" />
```

- home.jsp

```jsx
<%@ page contentType="text/html;charset=utf-8" %> //한글값 깨짐없이 응답에 관련된 형식을 contentType속성으로 지정
```

- security-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:security="http://www.springframework.org/schema/security"
	xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<security:http>
		<security:intercept-url pattern="/sample/all" access="permitAll" />
		<!-- ROLE_MEMBER권한을 가진 사용자만 /sample/memeber에 접속할 수 있다 -->
		<security:intercept-url
			pattern="/sample/member" access="hasRole('ROLE_MEMBER')" />
		<!-- ROLE_ADMIN권한을 가진 사용자만 /sample/admin에 접속할 수 있다 -->
		<security:intercept-url
			pattern="/sample/admin" access="hasRole('ROLE_ADMIN')" />
			
		<!-- 인증성공되면 요청한 url로 redirect됨-->
		<security:form-login/>
	</security:http>
	
	<security:authentication-manager> <!-- 인증 관리 -->
	<security:authentication-provider> 
		<security:user-service> <!-- 사용자별 정보와 권한정보를 처리 --> 
			<security:user name="member" password="{noop}member" 
			authorities="ROLE_MEMBER"/> <!-- 권한부여 -->
			
			<security:user name="admin" password="{noop}admin" 
			authorities="ROLE_ADMIN, ROLE_MEMBER"/> <!-- 권한부여 -->
		</security:user-service>
	</security:authentication-provider>
	</security:authentication-manager>
</beans>
```

실행결과>

![3](https://user-images.githubusercontent.com/63957819/113131906-bff1bc00-9258-11eb-864c-b2b393df65e3.png)

![4](https://user-images.githubusercontent.com/63957819/113131907-bff1bc00-9258-11eb-93be-768c75bb950c.png)

![5](https://user-images.githubusercontent.com/63957819/113131908-c08a5280-9258-11eb-9e4a-a3afd155b17c.png)

![6](https://user-images.githubusercontent.com/63957819/113131910-c08a5280-9258-11eb-9684-62897ee68903.png)

![7](https://user-images.githubusercontent.com/63957819/113131911-c122e900-9258-11eb-827c-a1f4c94d17d3.png)

![8](https://user-images.githubusercontent.com/63957819/113131913-c122e900-9258-11eb-9df7-24af560afda4.png)

---

- CommonController.java

```java
package controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonController {
	@RequestMapping("/accessError")
	public void accessError(Authentication auth, Model model) {
		String userName = auth.getName();//인증된 유저 이름 확인
		model.addAttribute("msg", userName+"은 접근불가한 사이트입니다.");
	}
}
```

- accessError.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page import="java.util.*" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>accessError.jsp</title>
</head>
<body>
<h1>Access Denied Page</h1>
<hr>
<h2>SPRING_SECURITY_403_EXCEPTION.getMessage() : <c:out value="${SPRING_SECURITY_403_EXCEPTION.getMessage()}"/></h2>

<hr>
<h2>
인증된 아이디는 sec:authentication property="principal.username"으로 확인할 수 있다<br>
sec:authentication property="principal.username" : 
      <sec:authentication property="principal.username"/>
</h2>
<hr>
<h2>requestScope.msg : <c:out value="${requestScope.msg}"/></h2>

<hr>
<a href="${pageContext.request.contextPath}">메인메뉴로 가기</a>
</body>
</html>
```

![9](https://user-images.githubusercontent.com/63957819/113131914-c1bb7f80-9258-11eb-8756-a09f69da47a1.png)

Forbidden 에러를 해결하려면 에러 설정을 해주면 되는데

security tag lib등록 해주고 sec 태그 사용해서 로그인 시에 사용한 유저 네임을 확인할 수 있다.

실행결과>

![10](https://user-images.githubusercontent.com/63957819/113131918-c1bb7f80-9258-11eb-9d0b-63f316ae877c.png)

---

![11](https://user-images.githubusercontent.com/63957819/113131920-c2541600-9258-11eb-922b-ed09d03d6e94.png)

- myLogin.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>myLogin.jsp</title>
</head>
<body>
<form method="post" action="${pageContext.request.contextPath}/myLogin">
	username : <input type="text" name="username">
	password : <input type="password" name="password">
	<input type="submit">
</form>
</body>
</html>
```

- security-context.xml

```jsx
<!-- 인증성공되면 요청한 url로 redirect됨-->
		<!-- <security:form-login/> -->
		<security:form-login login-page="/myLogin"/>
```

- CommonController.java

```java
package controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonController {
	@RequestMapping("/accessError")
	public void accessError(Authentication auth, Model model) {
		if(auth != null) {
			String userName = auth.getName();//인증된 유저 이름 확인
			model.addAttribute("msg", userName+"은 접근불가한 사이트입니다.");
		}
	}
	
	@GetMapping("/myLogin")
	public void getMyLogin() { }
	@PostMapping("/myLogin")
	public void postMyLogin() {
		
	}

}
```

실행결과>

![12](https://user-images.githubusercontent.com/63957819/113131922-c2541600-9258-11eb-87ff-52c47edd7986.png)

![13](https://user-images.githubusercontent.com/63957819/113131924-c2ecac80-9258-11eb-8c96-5d2feb3dfd48.png)

/sample/member 요청 시 자동 myLogin 페이지로 리다이렉트 된다

접근 불가의 에러가 나오는데 CSRF는 보안 침해에 관련된 용어이다. 보안 공격 방법을 피하려면 csrf값이 설정돼서 페이지에 전달 되어야 한다.

- myLogin.jsp

```jsx
<form method="post" action="${pageContext.request.contextPath}/myLogin">
	username : <input type="text" name="username">
	password : <input type="password" name="password">
	<input name="${_csrf.parameterName}" 
	       type="hidden" 
	       value="${_csrf.token}">
	<input type="submit">
</form>
```

실행결과>

![14](https://user-images.githubusercontent.com/63957819/113131926-c2ecac80-9258-11eb-8be1-6e8b8cc87afa.png)

---

![15](https://user-images.githubusercontent.com/63957819/113131929-c3854300-9258-11eb-99ba-672fb325b45c.png)

실행결과>

![16](https://user-images.githubusercontent.com/63957819/113131930-c3854300-9258-11eb-94ff-9be977603419.png)

invalidate-session="true" ⇒ 로그아웃 시에 세션 제거 처리

---

- security-context.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xmlns:security="http://www.springframework.org/schema/security"
    	xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd
    		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    	<security:http>
    		<security:intercept-url pattern="/sample/all" access="permitAll" />
    		<security:intercept-url
    			pattern="/sample/member" access="hasRole('ROLE_MEMBER')" />
    		<security:intercept-url
    			pattern="/sample/admin" access="hasRole('ROLE_ADMIN')" />
    			
    		<!-- 로그인  
    		권한없이 /sample/member경로나 /sample/admin경로를 요청하면 
    		        login-page속성으로 redirect된다 
    		login-page속성 : 기본값이 /login  
    		인증성공되면 원래 요청된 경로로 redirect되고 
    		                        원래요청된 경로가 없으면  /경로로 redirect된다.
    		인증실패되면 login-page속성으로 redirect된다(이때 queryString으로 ?error전달)
    			  
    		authentication-success-handler-ref 속성: 로그인성공후 처리담당하는 AuthenticationSuccessHandler bean
    		                        로그인 성공되면 원래 요청된 경로가 아니라 권한에 맞는 경로로 redirect하려면 필요
    		-->		
    		<security:form-login  />
    		<!-- <security:form-login login-page="/myLogin"/> -->
    		
    		<!-- 권한이 없는 경우 403응답코드가 응답된다.ex)ROLE_MEMBER권한으로 /sample/admin요청한 경우
    		     403응답인 경우  error-page속성값에 지정된 url로 forward된다 : controller필요!
    		 -->
    		<security:access-denied-handler error-page="/accessError"/>
    		
    		<!-- 로그아웃 
    		logout-url속성 : 기본값이 /logout
    		invalidate-session속성 : 기본값이 true 
    		success-handler-ref 속성: 로그아웃후 처리담당하는  LogoutSuccessHandler bean
    		
    		로그아웃이 성공되면 
    		   security:form-login태그의 login-page속성URL로 redirect된다
    		   ex)/login?logout 
    		      /customLogin?logout 
    		-->
    		<security:logout  invalidate-session="true" />
    	</security:http>
    	
    	<security:authentication-manager><!-- 인증 관리 -->
    	<security:authentication-provider> 
    		<security:user-service> <!-- 사용자별 정보와 권한정보를 처리 -->
    			<security:user name="member" password="{noop}member" 
    			authorities="ROLE_MEMBER"/><!-- 권한부여 --> 
    			
    			<security:user name="admin" password="{noop}admin" 
    			authorities="ROLE_ADMIN, ROLE_MEMBER"/><!-- 권한부여 --> 
    		</security:user-service>
    	</security:authentication-provider>
    	</security:authentication-manager>
    </beans>
    ```

    실행결과>

![17](https://user-images.githubusercontent.com/63957819/113131932-c41dd980-9258-11eb-9a2c-0004410f4a30.png)

![18](https://user-images.githubusercontent.com/63957819/113131934-c41dd980-9258-11eb-8025-6188324138f6.png)
