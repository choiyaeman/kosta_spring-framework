# day17

스프링은 크게 di, aop, springmvc 모듈 등등 내재 되어 있어서 필요한 모듈을 갖다 쓰면 된다. di 모듈은 무엇이고 aop 모듈이 무엇인지 알아야 한다.

![1](https://user-images.githubusercontent.com/63957819/112958908-e0991380-917d-11eb-9ea4-d9afef75921d.png)

핵심로직의 윗 부분에 미리 처리해줘야 할 작업이 있다. 그 작업을 before, 이후에 처리해줘야 할 작업을 after라고 하자. 핵심로직 전단에서 수행할 작업과 핵심 로직 후에 처리해 할 로직들이 이곳 저곳에서 쓰인다. 공통로직과 비지니스 로직을 엮는 절차를 Weaving이라 부른다. AOP(AspectOrientedProgramming)는 공통로직이 여러 핵심 로직에 수평적으로 들어간다. 이외 반해 OOP같은 경우 공통로직을 부모 쪽에 들어간다. 이 구조는 수직 구조를 바라보는 관점이다.

`Target` : 핵심로직들을 포함하고 있는 객체

 `JoinPoint` : waving 대상이 될 핵심로직

`PointCut` : JoinPoint 중에서 공통로직이 weaving된 핵심로직

`advice` 의 종류로는 before, after, around 놈들이 있다. 이 advice는 어느 PointCut에 공통 사항을 weaving할 것인가 이다. 즉 weaving 방법이다.

![2](https://user-images.githubusercontent.com/63957819/112958913-e1ca4080-917d-11eb-8b27-4a6a2d94596e.png)

advice를 이용해서 PointCut에 공통로직을 Weaving한다라고 보면 되는데 weaving하는 시점에 대해 알아볼 필요가 있다. 

Weaving 시점으로 1. Target객체용 클래스의 컴파일시에 Weaving 2. Target객체용 클래스 클래스의 로딩시에 Weaving 3. Target객체용 클래스의 런타임시에 Waving 하는 방법들이 있다. 

Target객체용클래스 내용이 변하는 게 아니라 weaving된 공통 내용이 들어가는 프록시 객체가 자동 생성되고 Target객체 대신 사용된다.

프록시 객체는 Target객체를 멤버 변수로 갖고 있다. 그리고 똑같이 메서드가 자동 만들어진다. 원래 핵심로직을 담고 있는 객체를 대리자 객체라 한다. 대리자 객체를 만들어서 Target객체를 보호하는 거다.

---

![3](https://user-images.githubusercontent.com/63957819/112958915-e262d700-917d-11eb-8579-b80852d718aa.png)

![4](https://user-images.githubusercontent.com/63957819/112958919-e2fb6d80-917d-11eb-84ad-e8d9d4923047.png)

maven repository> Spring Context다운 받으면 aop라이브러리를 같이 다운 받을 수 있다.

![5](https://user-images.githubusercontent.com/63957819/112958921-e2fb6d80-917d-11eb-9e73-64f3ddbff650.png)

![6](https://user-images.githubusercontent.com/63957819/112958924-e3940400-917d-11eb-8f1e-4ba5aacb620f.png)

![7](https://user-images.githubusercontent.com/63957819/112958928-e3940400-917d-11eb-9f37-82c0d567ec52.png)

![8](https://user-images.githubusercontent.com/63957819/112958932-e42c9a80-917d-11eb-8ae3-48fd72a32caa.png)

- pom.xml

```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.6.RELEASE</version>
</dependency>
  <!-- https://mvnrepository.com/artifact/org.aspectj/aspectjrt -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.6</version>
</dependency>
 <!-- https://mvnrepository.com/artifact/org.aspectj/aspectjweaver -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.6</version>
</dependency>
```

- config.xml

```xml
<aop:aspectj-autoproxy></aop:aspectj-autoproxy> <!-- 프록시자동생성 -->
	<context:component-scan base-package="aop"/>
	<context:component-scan base-package="com.my.service"/>
```

- Target.java

```java
package com.my.service;

import org.springframework.stereotype.Service;

@Service
public class Target { //핵심로직을 담고 있는 클래스
	public void a() { //JoinPoint
		System.out.println("a()메서드호출됨");
	}
	public void b() { //JoinPoint
		System.out.println("b()메서드호출됨");
	}
	public void c() { //JoinPoint
		System.out.println("c()메서드호출됨");
	}
	public void d() { //JoinPoint
		System.out.println("d()메서드호출됨");
	}
}
```

- MyAdvice.java

```java
package aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAdvice {
	@Before("execution(* a(..))")
	public void before() {
		System.out.println("공통사항 before()호출됨");
	}
}
```

Advice에서는 공통사항을 어느 핵심 로직에 끼워 넣기를 할지 결정을 하는 거다.

a(..)여기서 ..은 매개변수가 몇 개가 되든 관계가 없다는 뜻이다. 만약 a메서드 한 개 또 오버로드 된 a메서드가 있다고 하면 두 개의 joinpoint가 모여서 pointcut의 역할을 한다

- Test.java

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.my.service.Target;

public class Test {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx;
		String configLocation = "config.xml";
		ctx = new ClassPathXmlApplicationContext(configLocation);
		Target t = ctx.getBean("target", com.my.service.Target.class);
		//t.d();
		t.a();
	}

}
```

실행결과>

![9](https://user-images.githubusercontent.com/63957819/112958933-e42c9a80-917d-11eb-9a16-0ad9a2566a1b.png)

---

- Target.java

```java
public int b(int num) { //joinpoint
		System.out.println("b()메서드호출됨");
		return num*10;
	}
```

- MyAdvice.java

```java
@Around("execution(* b(..))")
	public int around(ProceedingJoinPoint pjp) throws Throwable { //pointcut에 등록된 핵심 메서드
		System.out.println("공통사항 around()시작됨");
		String joinPointMethodName = 
				pjp.getSignature().getName();
		System.out.println("join point 메서드명:" + joinPointMethodName);
		int parameter = (Integer)pjp.getArgs()[0];
		System.out.println("전달된 파라미터값: " + parameter);
		
		int result = (Integer)pjp.proceed(); //join point(핵심)메서드 호출
		
		System.out.println("메서드 호출결과:" + result);
		System.out.println("공통사항 around()종료됨");
		return result*10;
	}
```

- Test.java

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.my.service.Target;

public class Test {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx;
		String configLocation = "config.xml";
		ctx = new ClassPathXmlApplicationContext(configLocation);
		Target t = ctx.getBean("target", com.my.service.Target.class);
		//t.d();
		//t.a();
		int result = t.b(3);
		System.out.println(result);
	}

}
```

실행결과>

![10](https://user-images.githubusercontent.com/63957819/112958934-e4c53100-917d-11eb-87a9-9d635745f4a2.png)

![11](https://user-images.githubusercontent.com/63957819/112958937-e55dc780-917d-11eb-8d80-cf681b63c7a5.png)

---

## <파일 업로드 처리>

업로드를 하면 서버로 xxx.png파일형식이 먼저 오고 내용이 전송이 된다. 

10Mb파일이 서버로 업로드가 됐다면 10Mb짜리를 다음 view에서 img태그를 이용해서 src속성에 해당하는 경로로 찾아가서 그대로 보여주면 이미지 크기가 커진다. width, height로 크기 지정을 하면 내가 원하는 크기만큼 이미지가 만들어질 수는 있으나 어찌 됐든 요청, 응답이므로 응답할 내용이 10Mb내용이 응답이 되어야 한다.  스피닝 현상을 줄이려면 빨리빨리 이미지 보려면 큰 이미지 파일을 가지고 오면 안된다. 10Mb에 해당하는 작은 이미지 파일이 만들어질 필요가 있다. 그럴려면 썸네일을 요청해서 응답을 받으면 된다.  

---

## <Spring Web Security를 이용한 로그인 처리>

- 인증과 권한 부여

![12](https://user-images.githubusercontent.com/63957819/112958939-e55dc780-917d-11eb-9aab-31133dcee856.png)

A사람은 b회사의 안쪽에 있는 db를 고치려고 온 사람이다. DBA라고 가정하자. A라는 사람이 회사에 오면 인증 절차를 거쳐야 한다. 신분증을 제시, 허가서 승인을 받아서 확인이 되면 허가를 받아 안으로 들어온다. 그리고 데이터 센터에 접근 할 수 있는 아이디 카드 발급을 받는데 그것은 권한 담당자가 권한을 부여 해준다. 허가증을 받았기 때문에 데이터 센터에 들어갈 수 있다. 근데 인가 받은 부분이 데이터 센터에만 들어갈 수  있는 거지 인사 팀에 접속할 수 있는 인가를 받은 게 아니다.

CEO사람도 권한 담당자가 CEO에게 권한을 부여 해야 한다. 근데 CEO는 어디나 갈 수 있다. 

- pom.xml

```xml
<!-- spring security core -->
      <!-- https://mvnrepository.com/artifact/org.springframework.security/spring-security-core -->
      <dependency>
         <groupId>org.springframework.security</groupId>
         <artifactId>spring-security-core</artifactId>
         <version>5.2.6.RELEASE</version>
      </dependency>

      <!-- spring security web -->
      <!-- https://mvnrepository.com/artifact/org.springframework.security/spring-security-web -->
      <dependency>
         <groupId>org.springframework.security</groupId>
         <artifactId>spring-security-web</artifactId>
         <version>5.2.6.RELEASE</version>
      </dependency>

      <!-- spring security config -->
      <!-- https://mvnrepository.com/artifact/org.springframework.security/spring-security-config -->
      <dependency>
         <groupId>org.springframework.security</groupId>
         <artifactId>spring-security-config</artifactId>
         <version>5.2.6.RELEASE</version>
      </dependency>
      
      <!-- spring security taglibs -->
      <!-- https://mvnrepository.com/artifact/org.springframework.security/spring-security-taglibs -->
      <dependency>
         <groupId>org.springframework.security</groupId>
         <artifactId>spring-security-taglibs</artifactId>
         <version>5.2.6.RELEASE</version>
      </dependency>
```

maven repository> spring security검색> core, web, config, taglib 4개 복붙

- web.xml

```xml
<filter>
		<filter-name>springSecurityFilterChain</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>springSecurityFilterChain</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

<!-- The definition of the Root Spring Container shared by all Servlets and Filters -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/spring/root-context.xml
		/WEB-INF/spring/security-context.xml
		</param-value>
	</context-param>
```

/WEB-INF/spring/security-context.xml -->보안에 관련된 설정 파일

- security-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:security="http://www.springframework.org/schema/security"
	xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<security:http>
		<security:form-login/>
	</security:http>
	
	<security:authentication-manager>
	</security:authentication-manager>
</beans>
```

실행결과>

![13](https://user-images.githubusercontent.com/63957819/112958943-e5f65e00-917d-11eb-8a82-dbb9e13d527d.png)

![14](https://user-images.githubusercontent.com/63957819/112958948-e5f65e00-917d-11eb-80ad-c3bc4a7a77b9.png)

spring security에서 제공되는 로그인 페이지

---

![15](https://user-images.githubusercontent.com/63957819/112958953-e68ef480-917d-11eb-96bf-c95df91a6511.png)

두 번째 방법을 많이 쓴다. 같은 웹 프로젝트에 있어야 자원을 공유할 수 있다.

프로젝트를 완벽히 분리를 한다면 일반 고객용에서 문제가 발생될 error.jsp랑 관리자용에서 문제가 발생될 error.jsp가 있어야 한다. 같이 공유해서 쓰지 못하므로 똑같은 내용이 양쪽에 있어야 하므로 프로젝트 내용이 늘어난다.

![16](https://user-images.githubusercontent.com/63957819/112958955-e68ef480-917d-11eb-9f53-93333c59c0e9.png)

앞 단에 신분증 제시해주세요 라고 하는 인증 절차가 필요하다. memeber, admin은 인증 절차를 거치고, all은 인증 절차를 거치지 않는다. 즉 memeber, admin로 요청이 들어왔다 하면 인증이 일단 통과가 되어야 한다.

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
		<security:form-login/>
	</security:http>
	
	<security:authentication-manager>
	<security:authentication-provider> 
		<security:user-service> 
			<security:user name="member" password="{noop}member" 
			authorities="ROLE_MEMBER"/> 
		</security:user-service>
	</security:authentication-provider>
	</security:authentication-manager>
</beans>
```

실행결과>

![17](https://user-images.githubusercontent.com/63957819/112958957-e7278b00-917d-11eb-8c2f-b33c4848e453.png)

 access값이 permitAll이 아닌 경우 리다이렉트 돼서 login페이지로 이동.

![18](https://user-images.githubusercontent.com/63957819/112958958-e7278b00-917d-11eb-9505-64e4bab0494a.png)

로그인 성공 돼서 인증이 성공 된 case이다. 실패가 된 경우에는 다시 로그인 페이지가 보인다.
