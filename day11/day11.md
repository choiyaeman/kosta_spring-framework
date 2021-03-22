# day11

- RepBoardDAOOracle.java

```java
..
.
@Repository
@Qualifier(value = "oracle")
@Log4j
public class RepBoardDAOOracle implements RepBoardDAO {
//	@Autowired
//	@Qualifier("hikarids")
	
//	@Autowired
//	private DataSource ds;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	public void delete(int board_no, String board_pwd) throws RemoveException{
		SqlSession session = null;
		try {
			//SqlSession session = sqlSessionFactory.openSession();
			session = sqlSessionFactory.openSession();
			Map <String, Object> map = new HashMap<>();
			map.put("board_no", board_no);
			map.put("board_pwd", board_pwd);
			int rowcnt = session.delete("mybatis.RepBoardMapper.delete", map);
			if(rowcnt == 0) {
				throw new RemoveException("글번호가 없거나 비밀번호가 다릅니다");
			}
			session.commit();
		}catch(Exception e) {
			throw new RemoveException(e.getMessage());
		} finally {
			**if(session !=null ) session.close();**
		}
	}
..
.
```

세션 객체를 사용하고 마지막에는 꼭 close를 해줘야 한다.

openSession메서드 이용 시 unchecked exception이 발생할 수 있다. 알아서 try~catch 구문이 필요하다.

---

- mybatis-config.xml

```java
..
.
<!--   <properties>
  	<property name="username" value="scott"/>
  </properties>  -->

<!--  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="oracle.jdbc.driver.OracleDriver"/>
        <property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:XE"/>
        <property name="username" value="${username}"/>
        <property name="password" value="tiger"/>
      </dataSource>
    </environment>
  </environments> -->
..
.

```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled.png)

오라클 데이터베이스 사용하고 있는 커넥션풀이 있는데 그게 무엇이냐면 HikaryCP이다. 이 객체는 DataSource로부터 상속 받은 하위 클래스이다.  총 다섯 개의 커넥션 객체가 미리 준비 되어있는 거다. SqlSessionFactory라는 이름으로 Spring Container에 의해서 관리되고 dataSource 객체도 관리가 된다. 스프링 컨테이너를 다른 말로 스프링 엔진, Web ApplicationContext 객체라고도 부른다.

mybatis-config.xml 파일에서 <environments>태그의 역할은 mybatis가 연결 할 데이터베이스용 정보이다. 스프링 컨테이너에 의해서 관리되는 Mybatis용 객체는 hikaryCP를 참조해서 쓰고 있으므로 environments부분은 더 이상 필요 없다.

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%201.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%201.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%202.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%202.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%203.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%203.png)

클라이언트가 메뉴 로그인을 클릭 시 요청이 되고 <html>내용이 클라이언트에게 응답이 되고 렌더링이 되어서 로그인 버튼이 클릭 되었을 때 로그인 url을 또 요청한다. 

요청이 들어올 때 요청 url이 서로 값이 같다.. 실제 자원은 다르나 DispatcherServlet놈이 같은 url로 이해 해버린다. 

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%204.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%204.png)

- mvcspirng/pom.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.my</groupId>
	<artifactId>mvcspring</artifactId>
	<name>mvcspring</name>
	<packaging>war</packaging>
	<version>1.0.0-BUILD-SNAPSHOT</version>
	<properties>
		<java-version>1.8</java-version>
		<org.springframework-version>5.2.6.RELEASE</org.springframework-version>
		<org.aspectj-version>1.6.10</org.aspectj-version>
		<org.slf4j-version>1.6.6</org.slf4j-version>
	</properties>
	<dependencies>
		<!-- Spring -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>${org.springframework-version}</version>
			<exclusions>
				<!-- Exclude Commons Logging in favor of SLF4j -->
				<exclusion>
					<groupId>commons-logging</groupId>
					<artifactId>commons-logging</artifactId>
				 </exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>${org.springframework-version}</version>
		</dependency>
				
		<!-- AspectJ -->
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjrt</artifactId>
			<version>${org.aspectj-version}</version>
		</dependency>	
		
		<!-- Logging -->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>${org.slf4j-version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>jcl-over-slf4j</artifactId>
			<version>${org.slf4j-version}</version>
			<!-- <scope>runtime</scope> -->
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>${org.slf4j-version}</version>
			<!-- <scope>runtime</scope> -->
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.17</version>
			<exclusions>
				<exclusion>
					<groupId>javax.mail</groupId>
					<artifactId>mail</artifactId>
				</exclusion>
				<exclusion>
					<groupId>javax.jms</groupId>
					<artifactId>jms</artifactId>
				</exclusion>
				<exclusion>
					<groupId>com.sun.jdmk</groupId>
					<artifactId>jmxtools</artifactId>
				</exclusion>
				<exclusion>
					<groupId>com.sun.jmx</groupId>
					<artifactId>jmxri</artifactId>
				</exclusion>
			</exclusions>
			<!-- <scope>runtime</scope> -->
		</dependency>

		<!-- @Inject -->
		<dependency>
			<groupId>javax.inject</groupId>
			<artifactId>javax.inject</artifactId>
			<version>1</version>
		</dependency>
				
		<!-- Servlet -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>2.5</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>jsp-api</artifactId>
			<version>2.1</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
	
<!-- Test -->
<!-- https://mvnrepository.com/artifact/org.springframework/spring-test -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${org.springframework-version}</version>
    <!-- <scope>test</scope> -->
</dependency>

<!-- lombok -->
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.18</version>
    <!-- <scope>provided</scope> -->
</dependency>

<!-- SPRING JDBC -->
<!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.2.6.RELEASE</version>
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

<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.11.0</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.0.4</version>
</dependency>

		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.12</version>
			<!-- <scope>test</scope> -->
		</dependency>        
	</dependencies>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-eclipse-plugin</artifactId>
                <version>2.9</version>
                <configuration>
                    <additionalProjectnatures>
                        <projectnature>org.springframework.ide.eclipse.core.springnature</projectnature>
                    </additionalProjectnatures>
                    <additionalBuildcommands>
                        <buildcommand>org.springframework.ide.eclipse.core.springbuilder</buildcommand>
                    </additionalBuildcommands>
                    <downloadSources>true</downloadSources>
                    <downloadJavadocs>true</downloadJavadocs>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.5.1</version>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                    <compilerArgument>-Xlint:all</compilerArgument>
                    <showWarnings>true</showWarnings>
                    <showDeprecation>true</showDeprecation>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.2.1</version>
                <configuration>
                    <mainClass>org.test.int1.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- servlet-context.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/mvc"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:beans="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/mvc https://www.springframework.org/schema/mvc/spring-mvc.xsd
		http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

	<!-- DispatcherServlet Context: defines this servlet's request-processing infrastructure -->
	
	<!-- Enables the Spring MVC @Controller programming model -->
	**<annotation-driven />**

	<!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<!-- <resources mapping="/resources/**" location="/resources/" />  -->

	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<!-- <beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean> -->
	
	**<context:component-scan base-package="com.my.control" />**
	
	
	
</beans:beans>
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%205.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%205.png)

<context:component-scan base-package="com.my.control" /> 설정해줘야 한다.

<annotation-driven /> → JSON형태로 응답 가능

- TestController.java

```java
package com.my.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class TestController {
	@RequestMapping("/login")
	@ResponseBody
	public String login() {
		log.info("로그인 요청됨");
		return "login test";
	}
}
```

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%206.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%206.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%207.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%207.png)

확장자가 무엇이 붙건 다 컨트롤러가 처리하고 있다. 자원에 대한 요청인 경우, 실제 컨트롤러가 요청인 된 경우를 구분해야 한다.  자원 요청 시에 컨트롤러 호출되면 안된다. 그 즉시 자원 내용을 응답을 해야 한다. 그에 대한 설정이 필요한데 resources이다.

---

- servlet-context.html

```java
..
.	
	<!-- Enables the Spring MVC @Controller programming model -->
	<annotation-driven />

	<!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<!-- <resources mapping="/resources/**" location="/resources/" />  -->
	<resources  mapping="/html/**" location="/resources/html/"/>

	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<!-- <beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean> -->
	
	<context:component-scan base-package="com.my.control" />
..
.
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%208.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%208.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%209.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%209.png)

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2010.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2010.png)

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2011.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2011.png)

jsp페이지는 WEB-INF에 view밑에 넣어주자

- servlet-context.xml

```java
..
.
<beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean>
..
.
```

- Test.java

```java
package com.my.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class TestController {
	@RequestMapping("/login")
	@ResponseBody //응답 내용자체가 json형태로 
	public String login() {
		log.info("로그인 요청됨");
		return "login test";
	}

	@RequestMapping("/semanticcssjq") //view를 찾아 자동 이동 
	public void semanticcssjq() { }
}
```

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2012.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2012.png)

---

- web.xml

```java
..
.
<welcome-file-list>
		<welcome-file>/WEB-INF/views/index.jsp</welcome-file>
	</welcome-file-list>
..
.
```

- servlet-context.xml

```java
..
.
<resources mapping="/html/**" location="/resources/html/"/>
**<resources mapping="/images/**" location="/resources/images/"/>**
..
.
```

**  → 모든 것의 의미를 뜻함.

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2013.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2013.png)

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2014.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2014.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2015.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2015.png)

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2016.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2016.png)

---

- log4j.xml

```java
..
.
<root>
		<priority value="info" />
		<appender-ref ref="console" />
	</root>
..
.
```

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2017.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2017.png)

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2018.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2018.png)

먼저 mybatis용 config파일하고 mapper파일 설정 해주자!

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2019.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2019.png)

boardbackspring 프로젝트에 있는 boardMapper, mybatis-config.xml 복사해서 mvcspring프로젝트 src/main/java 밑에 붙여 넣자!

- mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <properties>
  	<property name="username" value="scott"/>
  </properties>
  <typeAliases>
  	<typeAlias alias="RepBoard" type="com.my.vo.RepBoard"/>
  	**<typeAlias alias="Customer" type="com.my.vo.Customer"/>**
  </typeAliases>
  <mappers>
    <mapper resource="boardMapper.xml"/>
    **<mapper resource="customerMapper.xml"/>**
  </mappers>
</configuration>
```

- customerMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.customerMapper">
  <insert id="insert" parameterType="Customer">
INSERT INTO customer(id, pwd, name, buildingno, addr1)
VALUES (#{id},#{pwd},#{name},#{postal.buildingno},#{addr1})
  </insert>
  <select id="selectById" parameterType="string" resultType="Customer">
SELECT 
id
,pwd
,name
,postal.buildingno "postal.buildingno"
,zipcode "postal.zipcode" 
,sido ||' ' || NVL(sigungu, ' ') ||' ' || NVL(eupmyun, ' ')  "postal.city"  
,doro || ' ' || DECODE(NVL(building2,'0'), building1, building1 ||'-' || building2) "postal.doro" 
,NVL(building, ' ') "postal.building"
,addr1
FROM   customer c LEFT OUTER JOIN postal postal ON (c.buildingno=postal.buildingno) 
WHERE id=#{id}
  </select>
</mapper>
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2020.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2020.png)

주문자 정보에 대한 기본 값을 postal로 설정 해 놓고 postal에 해당하는 상세주소 addr1로 Customer가 구성되어 있다. Customer와 Person 자식 부모 관계이다. Customer가 has a 관계로 Postal를 참조하고 있다. id, pwd는 Person이 갖고 있다. 

Customer하고 Postal하고 join이 안되어있다. 그래서 join을 이용해서 복잡한 sql구문을 만들어보자

오라클에서는 컬럼 명, 테이블 명 등 대문자로 관리가 된다. 컬럼에 대한 별칭 지정할 때 소문자 지정도 하고 특수 문자 점도 보이도록 하려면 앞에 큰 따옴표가 있어야 한다. 왜 만들어야 하는가? 소문자를 유지, 별칭 명에 특수 문자 점을 넣어주기 위해 그리고 멤버 변수의 값을 대입해야 하기 때문이다. 이렇게 점을 찍어서 has a 관계를 찾아 갈 수 있다.

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2021.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2021.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2022.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2022.png)

hikari, mybatis, component-scan 부분이 있어야 한다.

pom.xml이용하면 라이브러리를 다운로드해서 쓰는 것

빌드패스는 웹으로는 사용하는 게 아니고 로컬로 테스트할 때

톰캣lib 웹으로 필요한 라이브러리일 때 갖다 쓰는 것

버전 관리까지 하려면 pom.xml사용하는 게 안정적이다.

- root-context.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd">
	
	<!-- Root Context: defines shared resources visible to all other web components -->
	<!-- Root Context: defines shared resources visible to all other web components -->
	<bean id="hikariConfig" class="com.zaxxer.hikari.HikariConfig"> 
	  <!-- 
	  <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver"/>
 	  <property name="jdbcUrl" value="jdbc:oracle:thin:@127.0.0.1:1521:XE"/>   
 	  -->
 	  <property name="driverClassName" 	
 	          value="net.sf.log4jdbc.sql.jdbcapi.DriverSpy"/> 
  	  <property name="jdbcUrl" 
  	          value="jdbc:log4jdbc:oracle:thin:@127.0.0.1:1521:XE"/>
 	  
      <property name="username" value="scott"/> 
	  <property name="password" value="tiger"/> 
	  <property name="minimumIdle" value="5" />
      <property name="maximumPoolSize" value="10" />
<!--       <property name="connectionTestQuery" value="select 1 from sys.dual" /> -->
<!--       <property name="connectionTimeout"  value="300000" /> -->    	
</bean>

<bean id="dataSource" 
           class="com.zaxxer.hikari.HikariDataSource" 
           destroy-method="close"> 
      <qualifier value="hikarids"/>
      <constructor-arg ref="hikariConfig" />      
</bean>
<bean class="org.mybatis.spring.SqlSessionFactoryBean"
		  id="sqlSessionFactory">
		<property name="dataSource"      ref="dataSource"></property>
		<property name="configLocation"  value="classpath:mybatis-config.xml">
		</property>
</bean>		
</beans>
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2023.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2023.png)

- pom.xml

```xml
..
.
<!-- https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc6 -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0.4</version>
</dependency>
..
.
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2024.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2024.png)

SqlSessionFactory에서 session객체를 얻는다. session을 사용할 때 발생하는 예외는 DataAccessException이라는 예외로 반환이 된다. SQLException을 DataAcessException으로 가공을 해놨다. 상속관계로 RuntimeException으로 상속 받은 DataAccessException이므로 catch로 Exception을 잡지 않으면 SQL구문에서 문제가 났을 때 프로그래밍 그 자리에서 죽어버린다.

dao패키지의 클래스들이 자동 스프링에 의해서 관리되도록 Component-scan해주자

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2025.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2025.png)

예를들어 #{id} 는 파라미터로 전달된 객체의 getId메서드가 자동 호출이 돼서 첫 번째 물음표에 전달이 되서 세팅이 된다. 파라미터 타입은 자바빈 형태여야 한다. 자바빈 형태란 매개 변수 없는 생성자, setter, getter이어야 한다. 

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2026.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2026.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2027.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2027.png)

- root-context.xml

```xml
..
.
<context:component-scan base-package="com.my.dao"/>
..
.
```

- CustomerDAOOracle.java

```java
..
.
@Repository
public class CustomerDAOOracle implements CustomerDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	@Override
	public void insert(Customer c) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("mybatis.customerMapper.insert", c);
		}catch(Exception e) {
			Throwable causeException = **e.getCause()**; //warning예외를 얻어내는 방법
			if(causeException instanceof 
					SQLIntegrityConstraintViolationException) {
				SQLIntegrityConstraintViolationException scve = (SQLIntegrityConstraintViolationException)causeException;
				if(scve.getErrorCode() == 1) {//PK중복
					throw new AddException("이미 사용중인 아이디입니다.");
				}else { //CK, NOT NULL, FK
					throw new AddException(e.getMessage());
				}
			}else {
				throw new AddException(e.getMessage());
			}
		}finally {
			if(session != null) session.close();
		}
	}
..
.

}
```

- CustomerDAOOracle.java-test

```java
..
.
//Spring용 단위테스트
//@WebAppConfiguration //JUnit5인 경우 
@RunWith(SpringJUnit4ClassRunner.class) //Juni4인 경우

//Spring 컨테이너용 XML파일 설정
@ContextConfiguration(locations={
		"file:src/main/webapp/WEB-INF/spring/root-context.xml", 
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@Log4j
public class CustomerDAOOracle {
	@Autowired
	private CustomerDAO dao;
..
.
	@Test
	public void insert() throws AddException {
		String id = "sid1";
		String expPwd = "spwd1";
		String expName = "네임1";
		String expBuildingno = "3611011200201000000000006";
		Postal expPostal = new Postal();
		expPostal.setBuildingno(expBuildingno);
		String expAddr1 = "1동1호";
		Customer c = new Customer(id, expPwd, expName, expPostal, expAddr1);
		dao.insert(c);
	}
}
```

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2028.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2028.png)

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2029.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2029.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2030.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2030.png)

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2031.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2031.png)

같은 아이디 insert할 경우 getCause() 메서드를 이용하여 warning예외를 얻어내기

---

일반 마이바티스는 session.commit을 해야 한다. 그러나 spring 마이바티스는 commit명령어가 없어도 insert가 된다. spring용 마이바티스는 auto commit이다. 

- CustomerDAOOracle.java

```java
..
.
@Override
	public Customer selectById(String id) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Customer c = session.selectOne("mybatis.customerMapper.selectById", id);
			if(c == null) {
				throw new FindException("아이디에 해당 고객이 없습니다.");
			}
			return c;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
..
.
```

- CustomerDAOOracle.java-test

```java
..
.
public class CustomerDAOOracle {
	@Autowired
	private CustomerDAO dao;
	
	@Test
	public void selectById() throws FindException {
		String id = "sid1";
		String expPwd = "spwd1";
		String expName = "네임1"; 
		String expCity = "세종특별자치시";
		String expDoro = "마음안1로";
		String expBuilding = "가락마을17단지";
		String expAddr1 = "1동1호";
		
		Customer  c = dao.selectById(id);
		assertNotNull(c);
		assertEquals(expPwd, c.getPwd());	
		assertEquals(expName, c.getName());
		assertEquals(expCity, c.getPostal().getCity().trim());
		assertEquals(expDoro, c.getPostal().getDoro().trim());
		assertEquals(expBuilding, c.getPostal().getBuilding().trim());
		assertEquals(expAddr1, c.getAddr1());
		
	}
..
.
```

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2032.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2032.png)

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2033.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2033.png)

- root-context.xml

```java
..
.
<context:component-scan base-package="com.my.dao"/>
**<context:component-scan base-package="com.my.service"/>**
..
.
```

- CustomerService.java

```java
package com.my.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.dao.CustomerDAO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.vo.Customer;

@Service
public class CustomerService {
	@Autowired
	private CustomerDAO dao; //CustomerDAO주입받기
	/**
	 * 고객 가입한다.
	 * @param c
	 * @throws AddException
	 */
	public void add(Customer c) throws AddException{
		dao.insert(c);
	}
	/**
	 * 아이디에 해당 고객을 검색한다.
	 * @param id 아이디
	 * @return
	 * @throws FindException 고객이 없거나 문제가 발생한 경우 예외가 발생한다.
	 */
	public Customer findById(String id) throws FindException{
		return dao.selectById(id);
	}
	
	/**
	 * 
	 * @param id 아이디
	 * @param pwd 비밀번호
	 * @return 고객
	 * @throws FindException 아이디, 비번이 일치하지 않을경우 예외 발생한다.
	 */
	public Customer login(String id, String pwd) throws FindException{
		Customer c = dao.selectById(id); 
		if(c.getPwd().equals(pwd)) { //id에 해당하는 고객의 비번까지 확인. selecById 반환값을 가지고 가공하는 작업
			return c;
		}else {
			throw new FindException("로그인 실패");
		}
	}
	
}
```

private CustomerDAO dao = new CustomerDAOOracle(); → spring에 의해서 관리되는 객체를 사용한다는게 아니고 새로 객체를 생성해서 쓰겠다는 의미이므로 적절치 않다. 그러므로 주입받는 코드가 필요!

---

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2034.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2034.png)

- servlet-context.xml

```java
..
.
**<context:component-scan base-package="com.my.control" />**
..
.
```

- CustomerController.java

```java
package com.my.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.my.exception.FindException;
import com.my.service.CustomerService;
import com.my.vo.Customer;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@PostMapping("/login")
	@ResponseBody //json형태로 응답하겠다~
	public Map<String, Object> login(String id, String pwd, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			Customer c = service.login(id, pwd);
			session.setAttribute("loginInfo", id); //session에 고객 id정보를 loginInfo라는 attribute라고 추가. 매개변수로 받아오면 된다.
			map.put("status", 1);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@RequestMapping("/logout")
	public ResponseEntity logout(HttpSession session) {
		session.removeAttribute("loginInfo");
		return new ResponseEntity<>(HttpStatus.OK); //OK: 응답코드 200번. ok가 응답이 되면 success함수가 호출이 된다
	}
}
```

실행결과>

![day11%2087c0088853024c61b3ca34f7738df667/Untitled%2035.png](day11%2087c0088853024c61b3ca34f7738df667/Untitled%2035.png)

로그인 성공 후 semanticcssjq로 넘어가는 것을 확인할 수 있다.