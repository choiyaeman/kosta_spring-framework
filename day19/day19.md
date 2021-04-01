# day19

## <사용자정의 로그인>

- security-context.xml

```xml
<!-- <security:form-login  /> -->
		<security:form-login login-page="/myLogin"/>
```

- CommonController.java

```java
@GetMapping("/myLogin")
	/**
	 * 인증실패된 후 또는 로그아웃후에 로그인페이지로 리다이렉트 된다. 이때 queryString이 전달됨
	 * error         logout
	 */
	public void getMyLogin(String error, String logout, Model model) {
		if(error != null) { //인증실패인 경우 전달된 요청전달데이터 error
			//error내용을 view에 전달
			model.addAttribute("error", "인증실패되었습니다."); //?
		}
		if(logout != null) { //로그아웃된 경우
			model.addAttribute("logout", "로그아웃되었습니다.");
		}
	}
```

- myLogin.jsp

```jsx
<h2>${requestScope.error}</h2>
<h2>${requestScope.logout}</h2>
```

실행결과>

![1](https://user-images.githubusercontent.com/63957819/113275910-3e626280-931a-11eb-84cb-06886a29e2ce.png)

---

## <사용자정의 로그아웃>

- security-context.xml

```xml
<!-- <security:logout  invalidate-session="true" /> -->
		<security:logout invalidate-session="true" logout-url="/myLogout"/>
```

- CommonController.java

```java
@GetMapping("/myLogout")
	public void getMyLogout() { }
	
	@PostMapping("/myLogout")
	public void postMyLogout() { }
```

로그아웃 후에는 자동으로 로그인 페이지로 리다이렉트 되고 로그아웃 정보가 전달이 될 거다.

실행결과>

![2](https://user-images.githubusercontent.com/63957819/113275914-3f938f80-931a-11eb-9176-7fb2655256f9.png)

![3](https://user-images.githubusercontent.com/63957819/113275915-3f938f80-931a-11eb-99b0-664b11cce91f.png)

---

## <Handler설정>

![4](https://user-images.githubusercontent.com/63957819/113275916-402c2600-931a-11eb-9669-359af2b9163d.png)

post방식의 url이 요청이 된다. 이전에 요청된 페이지가 무엇인지 확인해 보고 인증 성공 시 1.인증 설정에 맞는 권한을 부여해주고 2. 원래 요청된 경로로 이동을 한다. 스프링의 내장되어있는post방식의 로그인이다. 핸들러 작성해서 다른 곳으로 이동할 수 있게 바꾸자~

![5](https://user-images.githubusercontent.com/63957819/113275920-40c4bc80-931a-11eb-9124-879496b5b811.png)

내장되어있는 헨들러를 통해서 원래의 url로 리다이렉트 했던 구문을 사용자 정의 핸들러를 만들어서 이런 작업을 할 수 있도록 해보자. 로그인 처리 후에 뭔가 하고 싶은 일이 있다 하면 컨트롤러를 쓰는게 아니라 인증이 성공 된 후의 헨들러를 작성해야 한다.

![6](https://user-images.githubusercontent.com/63957819/113275921-40c4bc80-931a-11eb-8f47-cda958dde7b2.png)

![7](https://user-images.githubusercontent.com/63957819/113275923-415d5300-931a-11eb-9597-6c860afa9746.png)

- MyLoginSuccessHandler.java

```java
..
.
/**
 * 인증성공(로그인성공)후 처리 될 핸들러
 * @author KOSTA
 *
 */
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			                            HttpServletResponse response,
			                            Authentication auth) throws IOException, ServletException {
		String contextPath = request.getContextPath();
		String userName = auth.getName(); //인증된 username
		if(userName.equals("admin")) {
			response.sendRedirect(contextPath + "/sample/admin");
		}else if(userName.equals("member")) {
			response.sendRedirect(contextPath + "/sample/member");
		}else {
			response.sendRedirect(contextPath + "/");
		}

	}

}
```

- security-context.xml

```xml
<bean id="myLoginSuccess" class="handler.MyLoginSuccessHandler"></bean>
<security:form-login login-page="/myLogin" 
		                     authentication-success-handler-ref="myLoginSuccess"/>
```

실행결과>

![8](https://user-images.githubusercontent.com/63957819/113275925-415d5300-931a-11eb-8668-cc049992be51.png)

핸들러가 가로채기를 해서 userName이 admin이라고 하면 /sample/admin 으로 리다이렉트 된다. member로 로그인하면 /sample/member로 가도록 설계 한 거다.

security에서는 filter하고 intercept개념으로 controller를 제어하는 개념이므로 handler를 통해서 정의하도록 되어 있다.

---

![9](https://user-images.githubusercontent.com/63957819/113275929-41f5e980-931a-11eb-9649-5d72774e7d2b.png)

실패가 되면 로그인 페이지를 다시 redirect 하러 간다. 그 일 처리를 기본 내장되어 있는 헨들러가 해주는 거다. 이 일을 이렇게 진행 안하고 새로운 실패 용 헨들러를 만들어서 다른 일을 하게 만들어 줄 거다. 실패 이유를 적어서 전달 해보자~

![10](https://user-images.githubusercontent.com/63957819/113275930-41f5e980-931a-11eb-8858-ccabaadc6204.png)

- MyLoginFailerController.java

```java
..
.
public class MyLoginFailerHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
			                            HttpServletResponse response,
			                            AuthenticationException exception) throws IOException, ServletException {
		response.sendRedirect(request.getContextPath()+"/myLogin?error="+exception.getMessage());

	}

}
```

- security-context.xml

```xml
<bean id="myLoginFail" class="handler.MyLoginFailerHandler"/>
<security:form-login login-page="/myLogin" 
		                     authentication-success-handler-ref="myLoginSuccess"
		                     authentication-failure-handler-ref="myLoginFail"
		                     />
```

- CommonController.java

```java
@GetMapping("/myLogin")
	/**
	 * 인증실패된 후 또는 로그아웃후에 로그인페이지로 리다이렉트 된다. 이때 queryString이 전달됨
	 * error         logout
	 */
	public void getMyLogin(String error, String logout, Model model) {
		if(error != null) { //인증실패인 경우 전달된 요청전달데이터 error
			//error내용을 view에 전달
			//model.addAttribute("error", "인증실패되었습니다."); //?
			model.addAttribute("error", error);
		}
		if(logout != null) { //로그아웃된 경우
			model.addAttribute("logout", "로그아웃되었습니다.");
		}
	}
```

실행결과>

![11](https://user-images.githubusercontent.com/63957819/113275931-428e8000-931a-11eb-958b-947516f5f611.png)

---

## <데이터베이스를 이용해서 권한 설정>

- pom.xml

```xml
<!-- DB :ojdbc6.jar, spring-jdbc, HikariCP, log4jdbc~~~, mybatis, mybatis-spring -->	
<!-- https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc6 -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0.4</version>
</dependency>

<!-- SPRING JDBC -->
<!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${org.springframework-version}</version>
</dependency>
	  
<!-- https://mvnrepository.com/artifact/com.zaxxer/HikariCP -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>3.4.5</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.bgee.log4jdbc-log4j2/log4jdbc-log4j2-jdbc4.1 -->
<dependency>
    <groupId>org.bgee.log4jdbc-log4j2</groupId>
    <artifactId>log4jdbc-log4j2-jdbc4.1</artifactId>
    <version>1.16</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
	<groupId>org.mybatis</groupId>
	<artifactId>mybatis</artifactId>
	<version>3.4.6</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
<dependency>
	<groupId>org.mybatis</groupId>
	<artifactId>mybatis-spring</artifactId>
	<version>1.3.2</version>
</dependency>
<!--  DB : END -->

<!-- JSON : jackson-databind-->	
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.9.5</version>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.9.5</version>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.dataformat</groupId>
	<artifactId>jackson-dataformat-xml</artifactId>
	<version>2.9.5</version>
</dependency>
<!-- JSON END -->	
	
<!-- LOMBOK -->	
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
	<version>1.18.0</version>
	<scope>provided</scope>
</dependency>

<!-- spring-test -->
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-test</artifactId>
	<version>${org.springframework-version}</version>
</dependency>
```

- root-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:task="http://www.springframework.org/schema/task"
	xsi:schemaLocation="http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-4.3.xsd
		http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd
		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.3.xsd
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.3.xsd">	

	<bean id="hikariConfig" class="com.zaxxer.hikari.HikariConfig">
		<property name="driverClassName"
			value="net.sf.log4jdbc.sql.jdbcapi.DriverSpy"></property>
		<property name="jdbcUrl"
			value="jdbc:log4jdbc:oracle:thin:@localhost:1521:XE"></property>
		<property name="username" value="scott"></property>
		<property name="password" value="tiger"></property>

	</bean>

	<!-- HikariCP configuration -->
	<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource"
		destroy-method="close">
		<constructor-arg ref="hikariConfig" />
	</bean>

	<!-- mybatis-spring -->
	<bean id="sqlSessionFactory"
		class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource"></property>
	</bean>

	<!-- transaction -->
	<bean id="transactionManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>
	</bean>

	<!-- @Transactional 어노테이션 사용가능 -->
	<tx:annotation-driven />
	
</beans>
```

```sql
SQL> create table tbl_member(userid varchar2(50) not null primary key,
  2     userpw varchar2(100) not null,
  3      username varchar2(100) not null,
  4      regdate date default sysdate,
  5      updatedate date default sysdate,
  6      enabled char(1) default '1');

Table created.

SQL> desc tbl_member
 Name                                      Null?    Type
 ----------------------------------------- -------- ----------------------------
 USERID                                    NOT NULL VARCHAR2(50)
 USERPW                                    NOT NULL VARCHAR2(100)
 USERNAME                                  NOT NULL VARCHAR2(100)
 REGDATE                                            DATE
 UPDATEDATE                                         DATE
 ENABLED                                            CHAR(1)
```

![12](https://user-images.githubusercontent.com/63957819/113275932-428e8000-931a-11eb-8b38-6a48e83ffb66.png)

데이터를 제거 해야 되는지 행이 계속 유지될지 쓸모없는 상태로 만들 것인지 결정 해야 한다. 인증 용 테이블에는 인증의 활성화, 비활성화를 알리는 컬럼이 하나 반드시 있어야 한다. 인증 용도로 쓰이기 위해서는 값이 1값으로 세팅이 되어 있어야 한다.

```sql
SQL> create table tbl_member_auth(
  2  userid varchar2(50) not null,
  3  auth varchar2(50) not null,
  4  constraint fk_member_auth foreign key(userid) references tbl_member(userid)
  5  );

Table created.

SQL> desc tbl_member_auth
 Name                                      Null?    Type
 ----------------------------------------- -------- ----------------------------
 USERID                                    NOT NULL VARCHAR2(50)
 AUTH                                      NOT NULL VARCHAR2(50)
```

![13](https://user-images.githubusercontent.com/63957819/113275935-43271680-931a-11eb-8468-3510dd3d4077.png)

두 개의 컬럼을 복합키로 만들어야 적절한 pk역할을 하는 것이다.

```sql
SQL> ALTER TABLE tbl_member_auth
  2  ADD CONSTRAINT tbl_member_auth_pk PRIMARY KEY(userid, auth);

Table altered.
```

- security-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:security="http://www.springframework.org/schema/security"
	xsi:schemaLocation="http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<bean id="myLoginSuccess" class="handler.MyLoginSuccessHandler"></bean>
	<bean id="myLoginFail" class="handler.MyLoginFailerHandler"/>
	<bean id="passwordEncoder" class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"/>
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
		<!-- <security:form-login  /> --> <!-- 내장된 /login과 /logout사용함 -->
		<!-- <security:form-login login-page="/myLogin"/> --> <!-- 사용자정의 /myLogin를 사용하면 get, post 모두 /logout사용불가 -->
		<security:form-login login-page="/myLogin" 
		                     authentication-success-handler-ref="myLoginSuccess"
		                     authentication-failure-handler-ref="myLoginFail"
		                     />
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
		<!-- <security:logout  invalidate-session="true" /> -->
		<security:logout invalidate-session="true" logout-url="/myLogout"/>
	</security:http>
	
	<security:authentication-manager><!-- 인증 관리 -->
	<security:authentication-provider> 
		<!-- <security:user-service> --> <!-- 사용자별 정보와 권한정보를 처리 -->
		<!-- 	<security:user name="member" password="{noop}member" 
			authorities="ROLE_MEMBER"/> --><!-- 권한부여 --> 
			
			<!-- <security:user name="admin" password="{noop}admin" 
			authorities="ROLE_ADMIN, ROLE_MEMBER"/> --><!-- 권한부여 --> 
		<!-- </security:user-service> -->
		
		<!-- 인증용 쿼리 : users-by-username-query
	          권한부여용 쿼리 : authorities-by-username-query
	    -->
		<security:jdbc-user-service 
		    data-source-ref="dataSource"
		    users-by-username-query=
		       "SELECT userid, userpw, enabled FROM tbl_member WHERE userid=?"
		    authorities-by-username-query=
		       "SELECT userid, auth FROM tbl_member_auth WHERE userid=?"
		/>
		<security:password-encoder ref="passwordEncoder"/>
	</security:authentication-provider>
	</security:authentication-manager>
</beans>
```

![14](https://user-images.githubusercontent.com/63957819/113275936-43271680-931a-11eb-8189-9fc782d81e79.png)

id가 있어야 하고 비밀번호가 서로 같아야 한다. 그리고 enabled값이 1값이어야 한다. 검색에 대한 컬럼 순서도 정확히 맞춰줘야 한다. 

1값이 인증의 허가 값이다. enabled 값이 0으로 세팅 되어서 검색이 되면 인증 실패가 되어 버린다. 비활성화 된 인증이다. 즉 1은 true의 의미를 담고 있고 1이 아니면 false의 의미이다.

- MemberTest.java

```java
..
.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
  "file:src/main/webapp/WEB-INF/spring/root-context.xml",
  "file:src/main/webapp//WEB-INF/spring/security-context.xml"
  })
@Log4j
public class MemberTest {

  @Setter(onMethod_ = @Autowired)
  private PasswordEncoder pwencoder;
  
  @Setter(onMethod_ = @Autowired)
  private DataSource ds;
  
//  @Test
  public void testInsertMember() {

    String sql = "insert into tbl_member(userid, userpw, username) values (?,?,?)";
    
    for(int i = 1; i <= 10; i++) {
      
      Connection con = null;
      PreparedStatement pstmt = null;
      
      try {
        con = ds.getConnection();
        pstmt = con.prepareStatement(sql);
        
        pstmt.setString(2, pwencoder.encode("pw" + i));
        
        if(i <5) {
          
          pstmt.setString(1, "user"+i);
          pstmt.setString(3,"일반사용자"+i);
          
        }else if (i <8) {
          
          pstmt.setString(1, "manager"+i);
          pstmt.setString(3,"운영자"+i);
          
        }else {
          
          pstmt.setString(1, "admin"+i);
          pstmt.setString(3,"관리자"+i);
          
        }
        
        pstmt.executeUpdate();
        
      }catch(Exception e) {
        e.printStackTrace();
      }finally {
        if(pstmt != null) { try { pstmt.close();  } catch(Exception e) {} }
        if(con != null) { try { con.close();  } catch(Exception e) {} }
        
      }
    }//end for
  }
  
@Test
  public void testInsertAuth() {
    String sql = "insert into tbl_member_auth (userid, auth) values (?,?)";
    
    for(int i = 1; i <= 10; i++) {
      
      Connection con = null;
      PreparedStatement pstmt = null;
      
      try {
        con = ds.getConnection();
        pstmt = con.prepareStatement(sql);
      
        
        if(i <5) {
          
          pstmt.setString(1, "user"+i);
          pstmt.setString(2,"ROLE_USER");
          
        }else if (i <8) {
          
          pstmt.setString(1, "manager"+i);
          pstmt.setString(2,"ROLE_MEMBER");
          
        }else {
          
          pstmt.setString(1, "admin"+i);
          pstmt.setString(2,"ROLE_ADMIN");
          
        }
        
        pstmt.executeUpdate();
        
      }catch(Exception e) {
        e.printStackTrace();
      }finally {
        if(pstmt != null) { try { pstmt.close();  } catch(Exception e) {} }
        if(con != null) { try { con.close();  } catch(Exception e) {} }
        
      }
    }//end for
  } 
}
```

실행결과>

![15](https://user-images.githubusercontent.com/63957819/113275938-43bfad00-931a-11eb-8abf-85359eadda93.png)

```sql
SQL> SELECT userid, userpw from tbl_member;

USERID
--------------------------------------------------------------------------------
USERPW
--------------------------------------------------------------------------------
user1
$2a$10$MdJN3A/RZyPHIg9qCbIKOuIT0babjFbnE7TPzvOB8UInM07tRDl7e

user2
$2a$10$CQm7z.LiNxSGt.4qVTxZI.ogsEn7FFnkZSNTM16/YsTlvapRnKLom

user3
$2a$10$F43AB6dJLKKmMLeHlA/yzepBVMIKrvwTLbgniGjfTZ2fw4R6C18QW

USERID
--------------------------------------------------------------------------------
USERPW
--------------------------------------------------------------------------------
user4
$2a$10$k6GDE6i2VhtdsI6fQ1djre6qK9LC6/d5ytXvbkSe07s41EO9yzn6u

manager5
$2a$10$zz4UUnXeIRuEx3SbN7GkWOuCOb9kPvTa.eKUWVu2D.71BPF7af6uC

manager6
$2a$10$FTMVxN19R88CvYwuiY0bWO2ON3j2qTh8lfKN8aC0DPFsUyHm8.1tm

USERID
--------------------------------------------------------------------------------
USERPW
--------------------------------------------------------------------------------
manager7
$2a$10$xSn2uEcEkneyVLNasMFVReaodV8EbD0.LLtSdSUPlD7kY6YmExcz.

admin8
$2a$10$C8gJ4kJj/cl4WAu2v.31A.ILjuD8XQgGMNbQUi4R5wjwpcw96nq66

admin9
$2a$10$IzM1dgk69VO1a7XvSuDph.Bj.FB00N4/ch/br6oKBVFT02m2HIXxe

USERID
--------------------------------------------------------------------------------
USERPW
--------------------------------------------------------------------------------
admin10
$2a$10$CopZo0RUBM4A8UtIJRMLv..1OYEmNW0Hx8y1p920d6Ud9qFRB1SYe

10 rows selected.
```

```sql
SQL> SELECT userid, auth from tbl_member_auth;

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
admin10
ROLE_ADMIN

admin8
ROLE_ADMIN

admin9
ROLE_ADMIN

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
manager5
ROLE_MEMBER

manager6
ROLE_MEMBER

manager7
ROLE_MEMBER

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
user1
ROLE_USER

user2
ROLE_USER

user3
ROLE_USER

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
user4
ROLE_USER

10 rows selected.

SQL> select userid, enabled from tbl_member where userid LIKE 'manager%';

USERID
--------------------------------------------------------------------------------
EN
--
manager5
1

manager6
1

manager7
1

SQL> select userid, auth from tbl_member_auth where userid like 'manager%';

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
manager5
ROLE_MEMBER

manager6
ROLE_MEMBER

manager7
ROLE_MEMBER

SQL> select userid, auth from tbl_member_auth where userid like 'admin%';

USERID
--------------------------------------------------------------------------------
AUTH
--------------------------------------------------------------------------------
admin10
ROLE_ADMIN

admin8
ROLE_ADMIN

admin9
ROLE_ADMIN

SQL> UPDATE tbl_member SET enabled=0 WHERE userid='admin10';

1 row updated.

SQL> INSERT INTO tbl_member_auth(userid, auth) VALUES ('admin9', 'ROLE_MEMBER');

1 row created.

SQL> commit;

Commit complete.
```

---

## <스프링 시큐리티 처리>

![16](https://user-images.githubusercontent.com/63957819/113275940-43bfad00-931a-11eb-8f23-f36e18c00122.png)

![17](https://user-images.githubusercontent.com/63957819/113275941-44584380-931a-11eb-83ea-549d99d9381b.png)

servlet-context.xml > Namespaces> security항목 체크

- OrderController.java

```java
..
.
@Controller
public class OrderController {
	@PreAuthorize("isAuthenticated()") //인증된 경우만
	@GetMapping(value = "/order/view", produces = "application/json;charset=utf-8") // /order/view경로에 접근할 수 있다
	@ResponseBody
	public String view(Authentication auth) {
		String userName = auth.getName();
		return userName + "님의 주문목록입니다";
	}
}
```

실행결과>

![18](https://user-images.githubusercontent.com/63957819/113275942-44584380-931a-11eb-9960-19ff1a41b9fc.png)

---

- OrderContrller.java

```java
@PreAuthorize("hasRole('ROLE_MEMBER')") //허가받은 권한이 'ROLE_MEMBER'인 경우만 
	@GetMapping(value = "/order/put", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String put() {
		return "주문을 추가했습니다";
	}
```

실행결과>

![19](https://user-images.githubusercontent.com/63957819/113275943-44f0da00-931a-11eb-96f9-75b9fe748f38.png)
