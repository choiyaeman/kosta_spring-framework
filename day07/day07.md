# day07

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled.png)

xml file에는 config level설정, dao 소스 코드에서는 event level을 설정해준다

실행결과>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%201.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%201.png)

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%202.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%202.png)

<appender name="console" class="org.apache.log4j.ConsoleAppender">

→ 로그 내용을 콘솔에 출력하겠다

%-5p : 다섯 자리를 유지하면 레벨의 종류를 출력 

: %c : 로그 이벤트가 발생된 클래스 이름

%m : 이벤트 메세지

%n : 줄 바꿈

%d{HH:mm:ss} : 로깅 이벤트가 발생한 시간

---

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%203.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%203.png)

maven repository로 가서 > spring-jdbc 검색 5.2.6버전 찾아가서 소스코드 pom.xml에 복붙 

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%204.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%204.png)

com.my.sql 파일 지워도 된다.

- pom.xml

```java
...
..
.
<!-- SPRING JDBC -->
<!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.2.6.RELEASE</version>
</dependency>
...
..
.
```

- root-context.html

```java
...
..
.
<bean id="dataSource"
	class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
		<property name="driverClass" value="oracle.jdbc.driver.OracleDriver"></property>
		<property name="url" value="jdbc:oracle:thin:@localhost:1521:xe"></property>
		<property name="username" value="scott"></property>
		<property name="password" value="tiger"></property>
	</bean>
...
..
.
```

- RepBoardDAOOracle.java

```java
package com.my.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

//import com.my.dao.test.RepBoardDAOOracle;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
//import com.my.sql.MyConnection;
import com.my.vo.RepBoard;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;

@Repository
@Qualifier(value = "oracle")
@Log4j
public class RepBoardDAOOracle implements RepBoardDAO {
	
//	데이터 소스 객체가 여러개일 경우
//	@Autowired
//	@Qualifier("hikarids")
	
	@Autowired
	private DataSource ds;
	
	public void delete(int board_no, String board_pwd) throws RemoveException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new RemoveException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String deleteSQL = "DELETE repboard  where board_no = ? AND board_pwd = ?";
		try {
			pstmt = con.prepareStatement(deleteSQL);
			pstmt.setInt(1, board_no);
			pstmt.setString(2, board_pwd);
			int rowcnt = pstmt.executeUpdate();
			if(rowcnt == 0) {
				throw new RemoveException("글번호가 없거나 비밀번호가 다릅니다");
			}
		} catch (SQLException e) {
			throw new RemoveException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt);
		}
	}
	public void update(RepBoard board, String board_pwd) throws ModifyException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String updateSQL1 = "UPDATE repboard SET ";
		boolean flag = false; 
		//제목
		if(board.getBoard_title() != null) {
			updateSQL1 += "board_title='"+ board.getBoard_title()+"'";
			flag = true;
		}
		
		//비밀번호
		if(flag) {
			updateSQL1 += ",";
		}
		if(board.getBoard_pwd() != null) {
			updateSQL1 += "board_pwd='"+ board.getBoard_pwd()+"'";
			flag = true;
		}
		System.out.println(updateSQL1);
		if(!flag) {
			throw new ModifyException("수정할 내용이 없습니다");
		}
		//----------
		String updateSQL2 = "\r\nWHERE board_no = "+board.getBoard_no()+
				" AND board_pwd = '"+ board_pwd +"'";
		String updateSQL = updateSQL1 + updateSQL2;
		try {
			pstmt = con.prepareStatement(updateSQL);
			int rowcnt = pstmt.executeUpdate();
			if(rowcnt == 0) {
				throw new ModifyException("글번호가 없거나 비밀번호가 다릅니다");
			}
		} catch (SQLException e) {
			throw new ModifyException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt);
		}
	}

	public void updateBoardCnt(int board_no) throws ModifyException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String updateBoardCntSQL = "\r\n" + 
				"UPDATE repboard SET board_cnt = board_cnt+1\r\n" + 
				"WHERE board_no = ?";
		try {
			pstmt = con.prepareStatement(updateBoardCntSQL);
			pstmt.setInt(1, board_no);
			int rowcnt = pstmt.executeUpdate();
			if(rowcnt == 0) {
				throw new ModifyException("글번호가 없습니다");
			}
		} catch (SQLException e) {
			throw new ModifyException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt);
		}
	}
	
	public RepBoard selectByBoard_no(int board_no) throws FindException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String selectAllSQL = "SELECT *\r\n" + 
				"FROM repboard\r\n" + 
				"WHERE board_no=?";
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(selectAllSQL);
			pstmt.setInt(1, board_no);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int parent_no = rs.getInt("parent_no");
				String board_title = rs.getString("board_title");
				String board_writer = rs.getString("board_writer");
				Date board_dt = rs.getDate("board_dt");
				String board_pwd = rs.getString("board_pwd");
				int board_cnt = rs.getInt("board_cnt");
				RepBoard board = new RepBoard(board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt);
				return board;
			}else{
				throw new FindException("게시글이 없습니다");
			}
		}catch(SQLException e) {
			throw new FindException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt, rs);
		}
	}
	
	public List<RepBoard> selectByBoard_titleORBoard_writer(String word) throws FindException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String selectAllSQL = "SELECT repboard.*\r\n" + 
				"FROM repboard\r\n" + 
				"WHERE board_title LIKE ? OR board_writer LIKE ?\r\n" + 
				"ORDER BY board_no DESC";
		ResultSet rs = null;
		List<RepBoard> list = new ArrayList<>();
		try {
			pstmt = con.prepareStatement(selectAllSQL);
			pstmt.setString(1, "%"+word+"%");
			pstmt.setString(2, "%"+word+"%");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int board_no = rs.getInt("board_no");
				int parent_no = rs.getInt("parent_no");
				String board_title = rs.getString("board_title");
				String board_writer = rs.getString("board_writer");
				Date board_dt = rs.getDate("board_dt");
				String board_pwd = rs.getString("board_pwd");
				int board_cnt = rs.getInt("board_cnt");
				RepBoard board = new RepBoard(board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt);
				list.add(board);
			}
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		}catch(SQLException e) {
			throw new FindException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt, rs);
		}
	}
	public List<RepBoard> selectAll() throws FindException{
		Connection con = null;
		//System.out.println("selectAll-1");
		
		//event level
		log.info("selectAll-1 : info");
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
		//System.out.println("selectAll-2");
		log.debug("selectAll-2 : debug");
		PreparedStatement pstmt = null;
		String selectAllSQL = "SELECT level, repboard.*\r\n" + 
				"FROM repboard\r\n" + 
				"START WITH parent_no = 0\r\n" + 
				"CONNECT BY PRIOR board_no = parent_no\r\n" + 
				"ORDER SIBLINGS BY board_no DESC";
		//System.out.println("selectAll-3" + selectAllSQL);
		log.warn("selectAll-3 : warn" + selectAllSQL);
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
			log.error("selectAll-4 : error, list.size=" + list.size());
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		}catch(SQLException e) {
			throw new FindException(e.getMessage());
		}finally {
			//MyConnection.close(con, pstmt, rs);
		}
	}
	public void insert(RepBoard board) throws AddException{
		Connection con = null;
		try {
			//con = MyConnection.getConnection();
			con = ds.getConnection();
		}catch(Exception e) {
			throw new AddException(e.getMessage());
		}
		PreparedStatement pstmt = null;
		String insertSQL = "INSERT INTO repboard(\r\n" + 
				"   board_no,               parent_no, board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  VALUES    \r\n" + 
				"   (board_seq.NEXTVAL,             ?,            ?,          ?,   SYSDATE,    ?,         0)";
		
		try {
			pstmt = con.prepareStatement(insertSQL);
			pstmt.setInt(1, board.getParent_no());
			pstmt.setString(2, board.getBoard_title());
			pstmt.setString(3, board.getBoard_writer());
			pstmt.setString(4, board.getBoard_pwd());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} finally {
			//MyConnection.close(con, pstmt);
		}	
	}
//	public static void main(String[] args) {
//		RepBoardDAOOracle dao = new RepBoardDAOOracle();
//
//	}
}
```

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%205.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%205.png)

dataSource타입의 객체가 ds라는 변수에 자동 upcasting되어서 주입이 된다. ds가 SimpleDriverDataSource객체를 참조하게 된다.

일 처리가 많아 라이브러리의 도움을 받아서 라이브러리가 알아서 db하고 연결을 끊을 거다.

---

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%206.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%206.png)

서비스의 구성이 디렉토리처럼 구성이 되어있어서 나름대로 설계를 할 수 있다. 톰캣 서버가 시작하게 되면 서비스 layer 미리 활성화 시켜 놓을 수 있다. 데이터 베이스와 일을 할 수 있는 서비스를 등록할 수 있다. 즉 미리 소캣을 여러 개 만들어 놓고 서비스를 활성화 시킬 수 있다. 라이브러리의 도움을 받아서 쓸 수 있는데 이런 것을 커넥션풀이라 한다. 요청할 때 dao를 통해서 db하고 연결 절차를 하게 되면 선 요청 후 연결인데 요청한 사람 입장에서 속도가 지연되는 것을 볼 수 밖에 없다.

그러므로 선 연결을 먼저 해 놓는다. 구조는 디렉토리 형태의 구조이고 이름에 해당하는 서비스 실제 객체를 연결해서 쓰인다. 

커넥션풀 라이브러리가 아주 다양하다 공통점이 있어야 하는데 반드시 지켜줘야 할 조건은 인터페이스를 구현한 하위 클래스가 되도록 설계가 되어야 한다.

JNDI(Java Naming and Directory Interface)는 디렉터리 서비스에서 제공하는 데이터 및 객체를 발견(discover)하고 참고(lookup)하기 위한 자바 API다.

new키워드로 Initialcontext객체 생성한다. 서비스 영역의 진입하겠다 의미이다. lookup으로 서비스 영역을 찾아간다. java:comp/env → 서비스 영역 서비스 영역에 정확히 들어간 다음에 찾아갈 경로로 찾아간다.  모두 데이터베이스 소스 인터페이스 타입으로만 구현한 타입이다.

의존성 주입이 가장 좋은 패턴이 dataSource이다. 다른 형태의 커넥션풀을 쓰게 된다 해도 자료형은 dataSource타입이 되기 때문에 자바소스코드는 전혀 변경될게 없다

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%207.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%207.png)

프레임워크가 하라는 대로 하는 거다. 그렇지만 라이브러리는 개발자가 하고싶은대로 바꿔서 쓰면 된다.

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%208.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%208.png)

커넥션 풀을 이용해서 주입을 해보자~ 요즘 각광을 받고 있는 hikarids를 써보자.

maven repository사이트에 들어가서 hikaricp 검색>3.4.5버전 maven 소스 복사 후 pom.xml에 붙여넣기

destory-method는 해당 객체가 spring컨테이너에 의해서 소멸될 때 자동 호출 될 메서드를 결정

minimumIdle은 미리 최소 ??개의 커넥션을 만들겠다라는 의미. 

maximumPoolSize은 최대 커넥션 개수를 만들겠다 의미

---

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%209.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%209.png)

sql쿼리의 결과 값을 보려면 라이브러리를 설치해야 한다. maven repository사이트에 들어가서 log4jdbc-log4j2 검색해서 1.16 소스코드 복사 후 pom.xml에 붙여넣자

- pom.xml

```java

...
..
.
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
...
..
.
```

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2010.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2010.png)

log4jdbc.log4j2.properties를 두 개resources라이브러리에 넣어주기

- root-context.xml

```java
...
..
.
<bean id="hikariConfig" class="com.zaxxer.hikari.HikariConfig"> 
	  <!-- 
	  <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver"/>
 	  <property name="jdbcUrl" value="jdbc:oracle:thin:@127.0.0.1:1521:XE"/>  
 	  -->
 	  **<property name="driverClassName" value="net.sf.log4jdbc.sql.jdbcapi.DriverSpy"/> 
    <property name="jdbcUrl" value="jdbc:log4jdbc:oracle:thin:@127.0.0.1:1521:XE"/>**
 	   
    <property name="username" value="scott"/> 
	  <property name="password" value="tiger"/> 
 	  <property name="minimumIdle" value="5" />
<!--       <property name="maximumPoolSize" value="10" /> -->
<!--       <property name="connectionTestQuery" value="select 1 from sys.dual" /> -->
<!--       <property name="connectionTimeout"  value="300000" /> -->    	
</bean>
...
..
.
```

실행 결과값>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2011.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2011.png)

---

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2012.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2012.png)

control 패키지 복사해서 src/main/java에 붙여 넣기 

dispatcher 서블릿이 컨트롤러의 역할을 해주므로 boardservlet은 필요 없다. 완전히 삭제해준다.

컨트롤러 인터페이스로 구현한 하위클래스로 구성되어 있으므로 상위 인터페이스를 없앨거다

컨트롤러가 스프링 컨테이너에 관리될 객체가 필요하다

 contorller어노테이션을 쓰기 위해서는  <annotation-driven /> 필요하다

- BoardListController.java

```java
package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;
@Controller
@Log4j
public class BoardListController {
	private static final long serialVersionUID = 1L;
	//private RepBoardService service = new RepBoardService();//에러발생
	//private RepBoardService service = RepBoardService.getInstance(); //서비스 객체생성
	@Autowired
	private RepBoardService service;
	
	//@RequestMapping(value = "/list", method = RequestMethod.GET)
	@GetMapping(value = "/list")
	public List<RepBoard> execute(HttpServletRequest request, HttpServletResponse response) 
			throws Exception{
		String word = request.getParameter("word");
		System.out.println("검색어:" + word);
		log.info("검색어:" + word);
		List<RepBoard> list = null;
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}		
		}catch (FindException e) {
			log.info(e.getMessage());
		}
		return list;
	}
	/*public String execute(HttpServletRequest request, HttpServletResponse response) 
			throws Exception{
		
		String word = request.getParameter("word");
		System.out.println("검색어:" + word);
		List<RepBoard> list;
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(df);
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("status", 1);
			map.put("list", list);
			//out.print(mapper.writeValueAsString(map));
			return mapper.writeValueAsString(map);
		} catch (FindException e) {
			e.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("status", -1);
			map.put("msg", e.getMessage());
			//out.print(mapper.writeValueAsString(map));
			return mapper.writeValueAsString(map);
		}
		
	}*/
}
```

객체 생성이 끝났으면 이 컨트롤러가 요청 됐을 때 excute메소드가 자동호출이 되도록 설계를 해야한다.

스프링 라이브러이에서는 jackson라이브러리를 내장하고 있기 때문에 return타입을 스트링이 아니라 객체 타입으로 리턴 해도 된다.

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2013.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2013.png)

springmvc 오른쪽 클릭> properties> Web Project Settings에 들어가 Context root 이름 지정

@ResponseBody는 mvc구조를 따르지 않고 컨트롤러가 직접 응답하겠다 의미.

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2014.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2014.png)

maven repository> jackson databind 검색> maven소스 복사 후 pom.xml에 붙이기

- pom.xml

```java
...
..
.
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.11.0</version>
</dependency>
...
..
.
```

실행결과>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2015.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2015.png)

---

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2016.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2016.png)

Dispatcher는 Frontcontroller 요청을 받는 일을 하고 그 요청을 controller에게 전달하는데 @Controller 붙여있는 객체를 찾아간다. request가 컨트롤러의 GetMapping메서드에게 전달이 된다.

model은 반환 되는 값이 void, string 무엇이든 간에 model타입으로 변환해서 반환해주는데 view단에서 이동할 때도 쓰인다. 3번 절차가 servlet에서 setAttribute였지만 spring으로 오게 되면 모델 만드는 코드로 바뀌어져야 한다. ModelAndView 타입으로 객체를 생성 한 다음 객체를 이용해서 addObject 결과를 추출하고 이동할 View 이름을 설정하고 return을 한다. 스프링 컨테이너가 view하고 model하고 쪼개버린다. @ResponseBody 응답 내용을 json라이브러리를 이용해서 변환해준다. 

void으로 선언한 경우 view를 이름 없이 요청한 url 맵핑 되는 jsp를 찾아가게 되어있다

void, String 이외의 List, RepBoard는 json형태로 응답해야 하므로 @ResponseBody필요

- BoardListController.java

```java
package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;
@Controller
@Log4j
public class BoardListController {
	private static final long serialVersionUID = 1L;
	//private RepBoardService service = new RepBoardService();//에러발생
	//private RepBoardService service = RepBoardService.getInstance(); //서비스 객체생성
	@Autowired
	private RepBoardService service;
	
	//@RequestMapping(value = "/list", method = RequestMethod.GET)
	@GetMapping(value = "/list")
	@ResponseBody
	//public List<RepBoard> execute(HttpServletRequest request, HttpServletResponse response) 
	**public List<RepBoard> execute(String word) throws Exception{**
		//String word = request.getParameter("word");
		log.info("검색어:" + word);
		List<RepBoard> list = null;
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}		
		}catch (FindException e) {
			log.info(e.getMessage());
		}
		return list;
	}
}
```

실행결과>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2017.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2017.png)

---

- BoardDetailController.java

```java
package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.exception.FindException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

@Controller
public class BoardDetailController {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/detail")
	@ResponseBody
	public Map<String, Object> execute(@RequestParam(required = false, defaultValue = "0") int board_no) 
			throws Exception{
		
		//1.요청전달데이터 얻기
//		String strBoard_no = request.getParameter("board_no");
//		int board_no = Integer.parseInt(strBoard_no);
//		System.out.println(board_no);
		
		//json용 JACKSON Lib활용
//		ObjectMapper mapper = new ObjectMapper();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		mapper.setDateFormat(df); //json문자열로 변환될때 날짜형식을 지정
		
		Map<String, Object> map = new HashMap<>();
		try {
			//2.비지니스로직 호출
			RepBoard board = service.findByBoard_no(board_no);
			
			map.put("status", 1);
			map.put("board", board);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		//응답
		//out.print(mapper.writeValueAsString(map));
		//return mapper.writeValueAsString(map);
		return map;
	}

}
```

int타입으로 자동형 변환 되어야 하면 값이 전달되지 않을 때의 기본 값 설정을 해줘야 한다.

defaultValue 값을 0으로 줬다 하면 게시글이 없을 경우 0번으로 처리 한다.

실행결과>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2018.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2018.png)

- BoardReplyController.java

```java
...
..
.
@Controller
public class BoardReplyController {
	private static final long serialVersionUID = 1L;
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/reply")
	@ResponseBody
	public Map<String, Object> execute(RepBoard board) throws Exception { //매개변수가 많아지므로 RepBoard타입으로 선언
//		String strParent_no = request.getParameter("parent_no");
//		int parent_no = Integer.parseInt(strParent_no);
//		String board_title = request.getParameter("board_title");
//		String board_writer = request.getParameter("board_writer");
//		String board_pwd = request.getParameter("board_pwd");
//		RepBoard board = new RepBoard();
//		board.setParent_no(parent_no);
//		board.setBoard_title(board_title);
//		board.setBoard_writer(board_writer);
//		board.setBoard_pwd(board_pwd);
		
//		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.writeReply(board);
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}

}
```

실행결과>

![day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2019.png](day07%20a42f6be2f9d74ed4970050b2de345cf7/Untitled%2019.png)

- BoardModifyController.java

```java
...
..
.
@Controller
public class BoardModifyController {
	@Autowired
	private RepBoardService service;
	@RequestMapping("/modify")
	@ResponseBody
	public Map<String, Object> execute(RepBoard board, String certify_board_pwd) {
		Map<String, Object> map = new HashMap<>();
		try {
			service.modify(board, certify_board_pwd);
			map.put("status", 1);
			
		} catch (ModifyException e) {
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
}
```

- BoardRemoveController.java

```java
...
..
.
@Controller
public class BoardRemoveController {
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/remove")
	@ResponseBody
	public Map<String, Object> execute(@RequestParam(required = false, defaultValue = "0")int board_no, String certify_board_pwd) throws Exception {		
		
//		ObjectMapper mapper = new ObjectMapper();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		mapper.setDateFormat(df);
		Map<String, Object> map = new HashMap<>();
		try {
			service.remove(board_no, certify_board_pwd);
			map.put("status", 1);
		} catch (RemoveException e) {
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
}
```

- BoardWriterController.java

```java
...
..
.
@Controller
public class BoardWriteCotroller {
	@Autowired
	private RepBoardService service;
	
	@RequestMapping("/write")
	@ResponseBody
	public Map<String, Object> execute(RepBoard board) {
		Map<String, Object> map = new HashMap<>();
		try {
			//2.비지니스로직 호출
			service.writeBoard(board);			
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}		
		//3.응답하기
		return map;
	}
}
```

- RepBoard.java

```java
...
..
.
//@Component(value = "b")
public class RepBoard {
	private int level;
	private int board_no;
	private int parent_no;
	private String board_title;
	private String board_writer;
	
	**@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")**
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
http://localhost:8888/springmvc/list
http://localhost:8888/springmvc/reply?parent_no=24&board_title=spring&board_writer=최예만&board_pwd=1234
http://localhost:8888/springmvc/modify?board_no=54&board_title=ttt4&board_writer=텍스트&board_pwd=1234&certify_board_pwd=1234
http://localhost:8888/springmvc/remove?board_no=1&certify_board_pwd=1234
http://localhost:8888/springmvc/write?board_no=1&parent_no=0&board_title=스프링&board_writer=콩쥐&board_dt=2020/03/16&board_pwd=1&board_cnt=0
```