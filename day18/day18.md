# day18

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%201.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%201.png)

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

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%202.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%202.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%203.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%203.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%204.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%204.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%205.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%205.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%206.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%206.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%207.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%207.png)

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

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%208.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%208.png)

Forbidden 에러를 해결하려면 에러 설정을 해주면 되는데

security tag lib등록 해주고 sec 태그 사용해서 로그인 시에 사용한 유저 네임을 확인할 수 있다.

실행결과>

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%209.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%209.png)

---

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2010.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2010.png)

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

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2011.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2011.png)

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2012.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2012.png)

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

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2013.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2013.png)

---

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2014.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2014.png)

실행결과>

![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2015.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2015.png)

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

    ![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2016.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2016.png)

    ![day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2017.png](day18%208f7fbd81d37343158381b0e4645e44c7/Untitled%2017.png)