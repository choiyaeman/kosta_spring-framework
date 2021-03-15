# day06

스프링 컨테이너에 의해서 관리되는 객체를 스프링 빈 또는 줄여서 빈이라 부른다.

<bean name="b" class="com.my.vo.RepBoard"> → RepBoard객체인데 b라는 이름으로 관리되는 객체이다.

클래스 타입이 같아도 이름이 다르면 서로 다른 객체이다.

xml파일이 만들어져 있으면 한눈에 전체 구조를 파악할 수 있으나 xml파일이 점점 많아지면 관리하기 어려워 진다. 그래서 xml간소화 시키는 작업을 해보자. bean태그로 등록 안해도 자동 bean으로 등록될 수 있어야 한다.

![1](https://user-images.githubusercontent.com/63957819/111128101-e3114000-85b7-11eb-92bd-004b2973b7ee.png)

beans탭에 가서 하위요소context 선택

![2](https://user-images.githubusercontent.com/63957819/111128103-e4426d00-85b7-11eb-9b23-3a1ca9b7fa52.png)

context탭에 가서 beans오른쪽 클릭> component-scan선택하고 base-package 설정

- config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd">
	<!--
	<bean name="b" class="com.my.vo.RepBoard">
		<property name="board_title" value="제목1"></property>
	</bean>
	
	<bean name="dao" class="com.my.dao.RepBoardDAOOracle"/>
	<bean name="service" class="com.my.service.RepBoardService">
		<property name="boardDAO" ref="dao"></property>
	</bean>
	-->
	<!-- com.my.vo패키지의 
	     Component 어노테이션이 설정된 클래스들을 찾아 스프링빈으로 등록한다 -->
	<context:component-scan base-package="com.my.vo"></context:component-scan>
	<context:component-scan base-package="com.my.dao"></context:component-scan>
	<context:component-scan base-package="com.my.service"></context:component-scan>
	
</beans>
```

- RepBoard.java

```java
package com.my.vo;

import java.util.Date;

import org.springframework.stereotype.Component;

**@Component(value = "b")**
public class RepBoard {
	private int level;
	private int board_no;
	private int parent_no;
	private String board_title;
	private String board_writer;
	private Date board_dt;
	private String board_pwd;
	private int board_cnt;
	public RepBoard() {
	}
....
...
..
.
```

예를들어 component-scan의 베이스 패키지를 com.my.vo를 설정했다면 해당 com.my.vo 클래스에 찾아가 @Component( value = ?) 값을 주면 된다.

![3](https://user-images.githubusercontent.com/63957819/111128104-e4426d00-85b7-11eb-9c8c-f1f1ea2ecad2.png)

Component 어노테이션은 타깃이 @Target(value={TYPE}) 이므로 클래스 선언 위에만 쓰일 수 있는 어노테이션이다.

어노테이션 사이에서 상속 관계가 있다. Repository 어노테이션은 Component로 부터 상속 받은 어노테이션이다. 즉 하위 어노테이션이다. dao는 db와 저장하는 클래스이기 때문에 Repository어노테잇녀 사용한다. 

Service 어노테이션도 Component의 하위 어노테이션이다.

사실상 Component어노테이션을 써도 크게 관계는 없다. 그냥 의미를 좀 더 명확하기 위해 dao는 Repository, Service는 Service, vo는 Component로 해주는 거다.

![4](https://user-images.githubusercontent.com/63957819/111128106-e4db0380-85b7-11eb-8073-042bc40c6426.png)

Autowired 어노테이션은 누구에게 누구를 감아준다. 예를들어 서비스에게 dao를 묶어 주겠다.. Autowired가 붙어있는 메소드는 자동 호출이 된다. 서비스 객체가 생성되자마자 Autowired메서드가 자동 호출이 된다. 

여기서 메소드가 호출이 될 때 주입 될 객체가 이름을 보고 찾아가는 게 아니라 매개변수의 자료형에 해당하는 타입을 보고 찾아 주입이 되는 거다. 매개변수의 자료형에 해당하는 값으로 채워진다.

![5](https://user-images.githubusercontent.com/63957819/111128107-e5739a00-85b7-11eb-88b0-ef6b7a375e3c.png)

근데 다른 데이터베이스가 있다고 하면 즉 db의 종류가 다르다고 가정하자~ dao가 두 개가 있다 이 두 개의 클래스가 Repository어노테이션이 선언이 되어 있으니깐 스프링 컨테이너에서 관리가 되어야 하는데 이름이 중복이 되어 있다. 이름을 바꿔주면 잘 관리가 된다. 그런데 autowired 어노테이션이 붙어 있는 메소드가 자동 호출이 되려할때 boardDAO에 대입 될 인자 값을 자료형으로 찾는데 RepBoardDAO가 두 개가 있으므로 혼란이 되어 오류가 난다. 

![6](https://user-images.githubusercontent.com/63957819/111128110-e5739a00-85b7-11eb-9cfe-1ae06390da77.png)

동일 자료형일 경우 Repository 값은 필요 없고 Qulifier 어노테이션을 주어 값을 설정하면 된다.

매개변수 앞에도 어노테이션 사용 가능

![7](https://user-images.githubusercontent.com/63957819/111128111-e60c3080-85b7-11eb-9478-2d1471f69d13.png)

매개변수 없는 생성자는  스프링 컨테이너에서 자동으로 이루어지긴 하는데 강제로 특정 매개변수 있는 생성자로 호출되게 해보면 이때에도 Autowired어노테이션 설정 해준다. Autowired 주는 방법은 세 가지가 있다.

DI방법으로는

1)setter 주입 xml설정: <property> , annotation설정: @Autowired
2)constructor 주입 xml설정:<constructor>, annotation설정: @Autowired

```java
@Service(value = "service")
public class RepBoardService {
//	3번째 방법
	@Autowired
	@Qualifier("oracle")
	private RepBoardDAO boardDAO; //일반화된 인터페이스타입으로 선언만 해두고 외부 설정 파일을 통해서 실제로 사용할 자원을 결정해 놓고 의존성 주입하는 것
//  2번째 방법
//	@Autowired
//	public RepBoardService(@Qualifier("oracle")RepBoardDAO boardDAO) { //매개변수 갖는 생성자는 특정 설정을 해줘야 객체가 생성될 때 자동생성
//		this.boardDAO = boardDAO;
//	}
	public RepBoardDAO getBoardDAO() {
		return boardDAO;
	}
//  1번째 방법	
//	@Autowired
//	public void setBoardDAO(@Qualifier("oracle") RepBoardDAO boardDAO) {
//		System.out.println("setBoardDAO()호출됨");
//		this.boardDAO = boardDAO;
//	}
...
..
.
```

- config.xml

```java
...
..
.
<!-- com.my.vo패키지의 
	     Component 어노테이션이 설정된 클래스들을 찾아 스프링빈으로 등록한다
	     @Component, @Service, @Repository, @Controller, @RestController..
	         @Autowired등의
	     어노테이션 사용가능
	-->
	<context:component-scan base-package="com.my.vo"></context:component-scan>
	<context:component-scan base-package="com.my.dao"></context:component-scan>
	<context:component-scan base-package="com.my.service"></context:component-scan>
	
	<!--component-scan태그없이도 
	    @Required, @Autowired어노테이션 사용하려면 아래 태그가 필요  -->
	**<context:annotation-config></context:annotation-config>**
...
..
.
```

Autowired 어노테이션은 component-scan태그가 없으면 사용할 수 없다. 태그 없이도 쓸 수 있게 하려면 annotation-config 태그를 사용해야 한다.

---

![8](https://user-images.githubusercontent.com/63957819/111128113-e6a4c700-85b7-11eb-9e8f-a12ac2ede768.png)

레거시스란 예전부터 제공하는 프로젝트 구조

![9](https://user-images.githubusercontent.com/63957819/111128115-e73d5d80-85b7-11eb-8ecc-236288faa7c1.png)

![10](https://user-images.githubusercontent.com/63957819/111128118-e73d5d80-85b7-11eb-98d0-d826c6e24366.png)

![11](https://user-images.githubusercontent.com/63957819/111128122-e7d5f400-85b7-11eb-83b1-f08326f4cd72.png)

spring용 설정 파일 이름은 관계없고 그 대신 web.xml의 DispatcherServlet init-param에 반드시 등록이 되어 있어야 한다

![12](https://user-images.githubusercontent.com/63957819/111128124-e86e8a80-85b7-11eb-8604-e5f6780305e8.png)

기본 자바 버전이 1.6이므로 1.8로 맞추자

![13](https://user-images.githubusercontent.com/63957819/111128127-e86e8a80-85b7-11eb-98bb-b150f9dc2687.png)

DispathcherServlet에서 명시한 xml파일 값을 설정해두면 알아서 톰캣이 구동이 될 때 스프링 컨테이너를 구동을 한다. 웹 애플리케이션컨텍스트의 형태이다. WebApplicationContext는 알아서 자동 구동이 되는데 이것을 DispatcherServlet이 해준다.

ClassPathXmlApplicationContext도 있고 WebApplicationContext도 있는 거다. 관리해줄 컨테이너의 형태는 WebApplicationContext가 되어야 한다. 우리가 직접 new키워드로 생성하는게 아니고 DispatcherServlet에게 맡겨야 한다.

![14](https://user-images.githubusercontent.com/63957819/111128129-e9072100-85b7-11eb-8ce2-a8da9b83f024.png)

spring을 쓰려면 spring-webmvc가 반드시 필요하다.

![15](https://user-images.githubusercontent.com/63957819/111128130-e99fb780-85b7-11eb-9039-908f3181b5ed.png)

[https://mvnrepository.com/artifact/org.springframework/spring-test/5.2.6.RELEASE](https://mvnrepository.com/artifact/org.springframework/spring-test/5.2.6.RELEASE)

<version>${org.springframework-version}</version> 코드로 채우기

- pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.my</groupId>
	<artifactId>a</artifactId>
	<name>springmvc</name>
	<packaging>war</packaging>
	<version>1.0.0-BUILD-SNAPSHOT</version>
	<properties>
		<java-version>**1.8**</java-version>
		<org.springframework-version>**5.2.6.RELEASE**</org.springframework-version>
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
	
		**<!-- Test -->
<!-- https://mvnrepository.com/artifact/org.springframework/spring-test -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${org.springframework-version}</version>
    <!-- <scope>test</scope> -->
</dependency>**
		
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>**4.12**</version>
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

![16](https://user-images.githubusercontent.com/63957819/111128131-e99fb780-85b7-11eb-867c-18efa16000e7.png)

![17](https://user-images.githubusercontent.com/63957819/111128132-ea384e00-85b7-11eb-9145-70d4e2ed1dfa.png)

src>test>java 오른쪽 클릭> new> Junit Test Case> com.my.dao.test

com.my.dao하면 위의 src/main/java의 com.my.dao와 이름이 같으므로 에러..

- RepBoardDAOOracle.java

    ```java
    package com.my.dao.test;

    import static org.junit.Assert.assertEquals;
    import static org.junit.Assert.assertNotNull;
    import static org.junit.Assert.assertTrue;

    import java.util.List;

    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
    import org.springframework.test.context.web.WebAppConfiguration;

    import com.my.dao.RepBoardDAO;
    import com.my.exception.FindException;
    import com.my.vo.RepBoard;

    //Spring용 단위 테스트
    //@WebAppConfiguration
    @RunWith(SpringJUnit4ClassRunner.class) //Junit4인 경우

    //Spring 컨테이너용 XML파일 설정
    @ContextConfiguration(locations={
    		"file:src/main/webapp/WEB-INF/spring/root-context.xml", 
    		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})

    public class RepBoardDAOOracle {
    	@Autowired
    	@Qualifier("oracle")
    	private RepBoardDAO dao;
    	//private RepBoardDAO dao = new com.my.dao.RepBoardDAOOracle();
    	
    	@Test
    	public void selectByBoard_no() {		
    		int board_no = 44;	
    		int expParent_no = 0;
    		String expBoard_title = "어려워ㅠㅠ";
    		String expBoard_writer = "최예만";
    		try {
    			RepBoard b = dao.selectByBoard_no(board_no);
    			assertNotNull(b); //b변수가 반드시 존재해야 한다.
    			
    //			assertEquals(expParent_no, b.getParent_no());
    			assertTrue(expParent_no == b.getParent_no());
    			assertEquals(expBoard_title, b.getBoard_title());
    			assertEquals(expBoard_writer, b.getBoard_writer());
    			
    		} catch (FindException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	@Test
    	public void selectAll() throws FindException {
    		RepBoardDAO dao = new com.my.dao.RepBoardDAOOracle();
    		
    		List<RepBoard> list = dao.selectAll();
    //		System.out.println(list.size()); //7
    		int expListSize =  7;
    		assertTrue(expListSize == list.size());
    	}

    }
    ```

![18](https://user-images.githubusercontent.com/63957819/111128136-ea384e00-85b7-11eb-8502-b53e3b53458b.png)

![19](https://user-images.githubusercontent.com/63957819/111128140-ead0e480-85b7-11eb-9969-3c0d166be975.png)

- servlet-contex.xml

```xml
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
	<!-- <annotation-driven /> -->

	<!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<!-- <resources mapping="/resources/**" location="/resources/" /> -->

	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<!-- <beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean> -->
	
	<context:component-scan base-package="com.my.vo" />
	<context:component-scan base-package="com.my.dao" />
	<context:component-scan base-package="com.my.service" />
	
	
</beans:beans>
```

Run As > JUnit Test

![20](https://user-images.githubusercontent.com/63957819/111128143-ead0e480-85b7-11eb-8ea4-6a4f946c4b29.png)

톰켓 켜지도 않고 단위 테스트를 할 수 있다.

root-context.xml, servlet-context.xml 파일 중   tomcat에 먼저 로딩이 되는 것은 root-context.xml이다. 

ServletContext 타입이 먼저 객체가 생성이 되고 파라미터가 자동 채워진다. web.xml파일이 갖고 있는 context-param의 값으로 서블릿 컨택스트의 파라미터 값이 채워진다. 컨텍스트 param처리가 먼저 된다.  Servlet객체가 나중에 만들어진다.  즉 init-param이 나중에 만들어진다.

즉 비지니스로직에 관련된 설정(root-context.xml)을 먼저하고 컨트롤러와 view에 관련된 일(servlet-context.xml)을 나중에 한다.

![21](https://user-images.githubusercontent.com/63957819/111128145-eb697b00-85b7-11eb-98b7-ca2bbf259138.png)

Namespaces> context 체크 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd">
	
	<!-- Root Context: defines shared resources visible to all other web components -->
	<!-- <context:component-scan base-package="com.my.vo" /> -->
	<context:component-scan base-package="com.my.dao" />
	<context:component-scan base-package="com.my.service" />
</beans>
```

serlvet-context.xml → root-context.xml 비지니스로직으로 옮기기. 

dao 객체와 service객체만 관리되도록..

```java
package com.my.vo;

import java.util.Date;

import org.springframework.stereotype.Component;

//@Component(value = "b")
public class RepBoard {
	private int level;
	private int board_no;
	private int parent_no;
	private String board_title;
	private String board_writer;
	private Date board_dt;
	private String board_pwd;
	private int board_cnt;

	public RepBoard() {
		System.out.println("RepBoard객체 생성됨");
	}
...
..
.
```

```java
private Restaurant restaurant;
@Autowired
public void setRestaurant(Restaurant r) {
	this.restaurant = r;
}

=>lombok
@Setter(onMethod = {@Autowired})
private Restaurant restaurant;
```

lombok 설치>

![22](https://user-images.githubusercontent.com/63957819/111128146-ec021180-85b7-11eb-8ac7-9d59ce76fa24.png)

[https://projectlombok.org/](https://projectlombok.org/) >download(1.8.18)> 다운로드 된 디렉토리로 이동> specify location 클릭> eclipse.exe 경로로 지정

![23](https://user-images.githubusercontent.com/63957819/111128152-ec9aa800-85b7-11eb-9087-6b826e4f769b.png)

[https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.18](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.18)

pom.xml에 붙이기

```java
...
..
.
<!-- lombok -->
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.18</version>
    <!-- <scope>provided</scope> -->
</dependency>
...
..
.
```

- RepBoardDAOOracle.java

```java
@Log4j
...
..
.
public List<RepBoard> selectAll() throws FindException{
		Connection con = null;
		//System.out.println("selectAll-1");
		**log.info("selectAll-1");**
		try {
			con = MyConnection.getConnection();
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
		//System.out.println("selectAll-2");
		**log.debug("selectAll-2 : debug");**
		PreparedStatement pstmt = null;
		String selectAllSQL = "SELECT level, repboard.*\r\n" + 
				"FROM repboard\r\n" + 
				"START WITH parent_no = 0\r\n" + 
				"CONNECT BY PRIOR board_no = parent_no\r\n" + 
				"ORDER SIBLINGS BY board_no DESC";
		//System.out.println("selectAll-3" + selectAllSQL);
		**log.warn("selectAll-3 : warn" + selectAllSQL);**
		ResultSet rs = null;
		List<RepBoard> list = new ArrayList<>();
		try {
			pstmt = con.prepareStatement(selectAllSQL);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int level = rs.getInt("level");
				int board_no = rs.getInt("board_no");
				int parent_no = rs.getInt("parent_no");
				String board_title = rs.getString("board_title");
				String board_writer = rs.getString("board_writer");
				Date board_dt = rs.getDate("board_dt");
				String board_pwd = rs.getString("board_pwd");
				int board_cnt = rs.getInt("board_cnt");
				RepBoard board = new RepBoard(level, board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt);
				list.add(board);
			}
			//System.out.println("selectAll-4 list.size=" + list.size());
			**log.error("selectAll-4 list.size=" + list.size());**
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		}catch(SQLException e) {
			throw new FindException(e.getMessage());
		}finally {
			MyConnection.close(con, pstmt, rs);
		}
	}
...
..
.
```

실행결과>

![24](https://user-images.githubusercontent.com/63957819/111128155-ec9aa800-85b7-11eb-8922-d78b55ce07e4.png)
