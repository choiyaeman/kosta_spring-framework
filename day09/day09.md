# day09

![1](https://user-images.githubusercontent.com/63957819/111590881-092e1e80-880a-11eb-8749-22c4f9255445.png)

컨트롤러가 직접 View로 이동하지 않고 ModelAndView객체만 반환하고 반환한 객체를 Handler가 받는다. HandlerAdapter가 반환 된 ModelAndView객체를 Model분석, View이름 분석한다. 그리고 Handler가 결과를 DispatcherServlet에게 전달한다. DispatcherServlet이 반환 받은 모델 값과 View값을 보고 ViewResolver라는 helper클래스의 도움을 받아서 접두어/view/접미어를 view자원 찾기 일을 해준다.

도움을 받고 다시 반환을 받아서 DispatcherServlet이 View로 이동을 해버린다. DispatcherServlet이 모델 정보를 Request의 속성으로 추가하고 그 모델을 충분히 view에서 requestGetAttribute로 찾아온다. 

Controller가 반환하는 타입이 String타입인 경우 일단 헨들러가 String객체를 받아야 한다. String 객체를 받아서 view이름으로 판단해서 반환한다. 

Controller가 반환하는 타입이 void타입인 경우 헨들러가 요청 url을 View이름으로 분석 해버린다.

DispatcherServlet은 헨들러의 도움을 받아서 controller가 갖고 있는 매개변수 타입을 핸들러가 분석해서 전달 데이터를 매개변수에 맞게 변형 후 Controller메서드를 호출한다.

DispatcherServlet 순서는 먼저 (A)요청이 들어오면 DispatcherServlet이 HandlerMapping에게 도와 달라고 요청한다. HandlerMapping은 요청 url을 분석해서 적합한 Controller를 찾아낸다. 그 다음 (ㅠ(B)HandlerAdapter에게 도와달라고 한다. HandlerAdapter는 전달데이터를 매개변수에 맞게 변형후 Controller메서드를 호출하고 컨트롤러가 일을 한다음 값ㅇ르 반환하고 handlerapate가 분석해서 다시 DispatcherServlet받아서 (C)Model을 Request속성 추가하고 (D)ViewResolver에게 도와 달라하고 View자원을 찾고 이동할 case인 경우 (E)View로 이동한다.

![2](https://user-images.githubusercontent.com/63957819/111590886-0a5f4b80-880a-11eb-88a9-eb7be0c6d8c2.png)

![3](https://user-images.githubusercontent.com/63957819/111590887-0a5f4b80-880a-11eb-921d-03c5e615e0db.png)

![4](https://user-images.githubusercontent.com/63957819/111590888-0af7e200-880a-11eb-8214-bd8fc50c8cae.png)

왼쪽은 ModelAndView 오른쪽은 Servlet구조이다.

---

boardbackspring 프로젝트 파일>

- BoardController.java

```java
..
.
@CrossOrigin("*")
@Controller
@Log4j
public class BoardController {
	@Autowired
	private RepBoardService service;

	@RequestMapping("/write")
	@ResponseBody
	**public ResponseEntity<String> write(RepBoard board) {
		try {
//			//2.비지니스로직 호출
			service.writeBoard(board);	
			ResponseEntity<String> entity = 
					new ResponseEntity(HttpStatus.OK);
			return entity;
		}catch(AddException e) {
			ResponseEntity<String> entity = 
					new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); //->응답오류 500
			return entity;
		}
	}**

//	@RequestMapping("/write")
//	@ResponseBody
//	public Map<String, Object> write(RepBoard board) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			//2.비지니스로직 호출
//			service.writeBoard(board);			
//			map.put("status", 1);
//		} catch (AddException e) {
//			e.printStackTrace();
//			map.put("status", -1);	
//			map.put("msg", e.getMessage());
//		}		
//		//3.응답하기
//		return map;
//	}
..
.
```

- write.html

```java
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 -->
<script>
$(function(){
	var $writeFormObj = $("section>div.write>form");
	$writeFormObj.submit(function(){
		$.ajax({
			url: backContextPath + $writeFormObj.attr("action"), // /boardbackController/write
			method: $writeFormObj.attr("method"),
			data: $writeFormObj.serialize(),
			success:function(responseObj){//응답성공이란 응답완료(readyState가 4), 응답코드가 200인 경우를 말함
				/* if(responseObj.status == 1){ //글쓰기작업이 성공
					$("header>ul>li>a.list").trigger("click"); //
				}else{ //글쓰기작업이 실패
					alert(responseObj.msg);
				} */
				alert("성공");
			},
			error:function(jqXHR){//응답실패
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
});
..
.
```

- index.html

```java
..
.
<title>메인(index.html)</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 

<script>
//let backContextPath = "/boardback";
//let backContextPath = "/springmvc";
**let backContextPath = "/boardbackspring";**
//let frontContextPath = "/boardfrontController";
let frontContextPath = "/boardfrontspring";
..
.
```

실행결과>

![5](https://user-images.githubusercontent.com/63957819/111590891-0af7e200-880a-11eb-8201-e1066ca6d7fe.png)

비밀번호 범위 초과 시 응답 오류 500이 뜨는 것을 볼 수 있다.

---

![6](https://user-images.githubusercontent.com/63957819/111590893-0b907880-880a-11eb-9ace-5dae3a5c2694.png)

하이버네이트는 객체가 만들어짐에 따라 행이 추가되고 제거됨에 따라 삭제되고..자바 객체를 이용해서 행과 연결을 할 수 있기 때문에 SQL구문이 자바 소스에 없다.

요즘 테이블 관계가 릴레이션 데이터베이스로 구성되다 보니까 테이블과 테이블 사이 관계를 맺고 있을 때 객체로 어찌 영속성 유지를 할 것인가..영속성 유지를 하기 위해서는 has a 관계 설정을 잘 해야 한다. 

마이바티스는 SQL이 없어지는 게 아니라 SQL하고 자바소스 코드를 분리 하는 거다. 완벽한 영속성 유지를 해주는 기술은 아니다.

---

[https://mybatis.org/mybatis-3/ko/getting-started.html](https://mybatis.org/mybatis-3/ko/getting-started.html)

![7](https://user-images.githubusercontent.com/63957819/111590895-0b907880-880a-11eb-9971-4171c1152869.png)

mybatis이름으로 자바 프로젝트 만들기

![8](https://user-images.githubusercontent.com/63957819/111590898-0c290f00-880a-11eb-9914-de16d4aa8ede.png)

mybatis오른쪽 클릭> configure> maven 클릭> finish

![9](https://user-images.githubusercontent.com/63957819/111590899-0c290f00-880a-11eb-9211-1f6e1929d072.png)

mybatis검색 후> 3.5.4버전 maven 소스 복사해서 pom.xml붙이기

![10](https://user-images.githubusercontent.com/63957819/111590900-0cc1a580-880a-11eb-8ccd-c1a1baaddddd.png)

src> file> mybatis-config.xml파일 만들기

- pom.xml

```java
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <dependencies>
  <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4</version>
</dependency>
  
  </dependencies>
  <build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

- mybatis-config.xml(SqlSessionFactory 빌드하기)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="oracle.jdbc.driver.OracleDriver"/>
        <property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:XE"/>
        <property name="username" value="scott"/>
        <property name="password" value="tiger"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="boardMapper.xml"/>
  </mappers>
</configuration>
```

- boardMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.my.vo.RepBoard">
  <select id="selectByBoard_no" resultType="com.my.vo.RepBoard">
    SELECT * FROM repboard WHERE board_no=#{aaa}
  </select>
</mapper>
```

select한 결과 값에 해당하는 행 결과를 RepBoard타입의 객체를 만들어서 반환한다.

- Test.java

```java
import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.my.vo.RepBoard;

public class Test {

	public static void main(String[] args) {
		//XML에서 SqlSessionFactory 빌드하기
		String resource = "mybatis-config.xml"; //test클래스가 있는 디렉토리~
		InputStream inputStream;
		try {
			inputStream = Resources.getResourceAsStream(resource); //getResourceAsStream는 클래스들이 있는 경로 기준으로 xml리소스 파일을 찾아서 리턴을 한다는 뜻
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			//SqlSessionFactory에서 SqlSession 만들기
			SqlSession session = sqlSessionFactory.openSession();
			
			RepBoard b = session.selectOne("com.my.vo.RepBoard.selectByBoard_no", 1); //mapper네임과 아이디
			System.out.println(b.getBoard_title()+":"+b.getBoard_writer());
//			System.out.println(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
```

실행결과>

![11](https://user-images.githubusercontent.com/63957819/111590901-0cc1a580-880a-11eb-86a3-c2811023cca9.png)

![12](https://user-images.githubusercontent.com/63957819/111590902-0d5a3c00-880a-11eb-81f0-60c1e5e89d25.png)

namespace에 해당하는 mapper를 찾아간다 id에 해당하는 태그를 찾아서 태그를 갖고 있는 sql구문을 실행한다. 필요한 인자 값은 두 번째 파라미터로 #{aaa}에 자동 대입이 된다. 이렇게 1)sql구문이 처리가 되면 2)처리된 결과 값을 가져와서 resultType에 담아온다. repBoard라는 클래스 타입 객체가 자동 만들어져서 반환이 되는 거다.

- boardMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.my.vo.RepBoard">
  <select id="selectByBoard_no" resultType="com.my.vo.RepBoard">
    SELECT * FROM repboard WHERE board_no=#{aaa}
  </select>
  <select id="selectByBoard_titleORBoard_writer"
          parameterType="java.lang.String"
          resultType="com.my.vo.RepBoard">
    SELECT * FROM repboard WHERE board_title LIKE #{word} OR board_writer LIKE #{word}
  </select>
  <!-- <insert id="insert"
          parameterType="com.my.vo.RepBoard">
  	INSERT INTO repboard(board_no, parent_no, board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  
  	VALUES(board_seq.NEXTVAL, #{parent_no}, #{board_title}, #{board_writer}, SYSDATE, #{board_pwd}, 0)    
  </insert> -->
  
  <insert id="insert"
          parameterType="java.util.Map">
  	INSERT INTO repboard(board_no, parent_no, board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  
  	VALUES(board_seq.NEXTVAL, #{parent_no}, #{board_title}, #{board_writer}, SYSDATE, #{board_pwd}, 0)    
  </insert>
  
</mapper>
```

- Test.java

```java
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.my.vo.RepBoard;

public class Test {

	public static void main(String[] args) {
		//XML에서 SqlSessionFactory 빌드하기
		String resource = "mybatis-config.xml"; //test클래스가 있는 디렉토리~
		InputStream inputStream;
		try {
			inputStream = Resources.getResourceAsStream(resource); //getResourceAsStream는 클래스들이 있는 경로 기준으로 xml리소스 파일을 찾아서 리턴을 한다는 뜻
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			//SqlSessionFactory에서 SqlSession 만들기
			SqlSession session = sqlSessionFactory.openSession();
			
			//Query결과가 1개의 행인 경우에는 selectOne메서드를 호출한다
			//0개 행이 검색된 경우는 null
			RepBoard b = session.selectOne("com.my.vo.RepBoard.selectByBoard_no", 1); //mapper네임과 아이디
			System.out.println(b.getBoard_title()+":"+b.getBoard_writer());
//			System.out.println(b);
			
			//Query결과가 여러개의 행인 경우에는 selectList메서드를 호출한다
			//0개 행이 검색된 경우는 size가 0인 list를 반환한다(null 아님)
			List<RepBoard> list = session.selectList("com.my.vo.RepBoard.selectByBoard_titleORBoard_writer", "%글%");
			for(RepBoard b1: list) {
				System.out.println(b1.getBoard_title()+":"+b1.getBoard_writer());
			}
//			RepBoard b2 = new RepBoard(); //RepBoard타입 b2객체 생성
//			b2.setBoard_title("제목Mybatis");
//			b2.setBoard_writer("mybatis");
//			b2.setBoard_pwd("1");
//			session.insert("com.my.vo.RepBoard.insert", b2);
			Map<String, Object> map = new HashMap<>();
			map.put("parent_no", 0);
			map.put("board_title", "제목mybatis1");
			map.put("board_writer", "mybatis1");
			map.put("board_pwd", "1");
			session.insert("com.my.vo.RepBoard.insert", map);
			session.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
```

![13](https://user-images.githubusercontent.com/63957819/111590904-0df2d280-880a-11eb-8eb0-196191af23bc.png)

selectOne메서드는 객체 하나만 반환하는 구조이기 때문에 예상되는 결과는 한 개 행만 찾아와야 한다. 여러 행일 경우 selectList메서드를 써야 적합하다. 둘 다 검색어에 관련된 메서드이다~

![14](https://user-images.githubusercontent.com/63957819/111590906-0df2d280-880a-11eb-9028-31e7106634b9.png)

조건에 만족하는 행을 찾지 못하면 null을 반환한다. 

![15](https://user-images.githubusercontent.com/63957819/111590907-0e8b6900-880a-11eb-8c33-2fc47dcc29a9.png)

파라미터 타입이 객체 타입으로 명시가 되어있으면 객체의 프로티명을 정확히 써줘야 한다.

```xml
SQL> select board_no, parent_no, board_title, board_dt from repboard order by board_dt desc;

  BOARD_NO  PARENT_NO
---------- ----------
BOARD_TITLE                                                  BOARD_DT
------------------------------------------------------------ --------
        93          0
제목mybatis1                                                 21/03/18
..
.
```

결과> 

![16](https://user-images.githubusercontent.com/63957819/111590908-0e8b6900-880a-11eb-97f0-ce1b0a5f4a03.png)

- BoardMapper.xml

```java
..
.
<update id="updateBoardCnt"
          parameterType="java.lang.Integer">
	  UPDATE repboard SET board_cnt = board_cnt+1
	  WHERE board_no = #{board_no}
  </update>
..
.
```

- Test.java

```java
..
.
try {
				int rowcnt = session.update("com.my.vo.RepBoard.updateBoardCnt", 1);
				System.out.println("수정 건수:" + rowcnt); //0? 1	
				session.commit();
			}catch(Exception e) { //실행도중 문제 발생하면 프로그램이 죽어버린다 그러므로 예외 발생하면 그 즉시 catch구문으로 이동시켜 프로그램이 죽지 않도록 해야한다.
				e.printStackTrace();
			}
..
.
```

 실행결과>

![17](https://user-images.githubusercontent.com/63957819/111590909-0f23ff80-880a-11eb-9488-1ed7cc062652.png)

- BoardMapper.xml

```java
<delete id="delete"
          parameterType="java.util.HashMap">
  	DELETE repboard where board_no = #{board_no} AND board_pwd = #{board_pwd}        
  </delete>
```

- Test.java

```java
try {
				Map<String, Object> map = new HashMap();
				map.put("board_no", 73);//게시글번호
				map.put("board_pwd", 1);//비밀번호
				int rowcnt = session.delete("com.my.vo.RepBoard.delete", map); //--게시글 삭제
				if(rowcnt == 0) {
					System.out.println("글번호가 없거나 비밀번호가 다릅니다.");
				} else {
					session.commit();	
				}
			}catch(Exception e) { //실행도중 문제 발생하면 프로그램이 죽어버린다 그러므로 예외 발생하면 그 즉시 catch구문으로 이동시켜 프로그램이 죽지 않도록 해야한다.
					e.printStackTrace();
			}
```

---

- RepBoardMapper1.java

```java
package mybatis;

import org.apache.ibatis.annotations.Select;

public interface RepBoardMapper1 { //xml대신해서 만들어진 interface
	@Select("SELECT * FROM repboard WHERE board_no=#{board_no}")
	public com.my.vo.RepBoard selectByBoard_no(int board_no);
}
```

---

- mybatis-config.xml

```java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  **<properties>
  	<property name="username" value="scott"/>
  </properties>
  <typeAliases> //별칭주기
  	<typeAlias alias="RepBoard" type="com.my.vo.RepBoard"/>
  </typeAliases>**
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="oracle.jdbc.driver.OracleDriver"/>
        <property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:XE"/>
        <property name="username" value="**${username}**"/>
        <property name="password" value="tiger"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="boardMapper.xml"/>
    
  </mappers>
</configuration>
```

- BoardMapper.xml

```java
..
.
<mapper namespace="**mybatis.RepBoardMapper**">
  <select id="selectByBoard_no" resultType="com.my.vo.RepBoard">
    SELECT * FROM repboard WHERE board_no=#{aaa}
  </select>
  <select id="selectByBoard_titleORBoard_writer"
          parameterType="java.lang.String"
          resultType="RepBoard">
    SELECT * FROM repboard WHERE board_title LIKE #{word} OR board_writer LIKE #{word}
  </select>
  <!-- <insert id="insert"
          parameterType="RepBoard"> //별칭값 쓰기
  	INSERT INTO repboard(board_no, parent_no, board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  
  	VALUES(board_seq.NEXTVAL, #{parent_no}, #{board_title}, #{board_writer}, SYSDATE, #{board_pwd}, 0)    
  </insert> -->
  
  <insert id="insert"
          parameterType="java.util.Map">
  	INSERT INTO repboard(board_no, parent_no, board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  
  	VALUES(board_seq.NEXTVAL, #{parent_no}, #{board_title}, #{board_writer}, SYSDATE, #{board_pwd}, 0)    
  </insert>
  
  <update id="updateBoardCnt"
          parameterType="int"> **//int 내장된 객체**
	UPDATE repboard SET board_cnt = board_cnt+1
	WHERE board_no = #{board_no}
  </update>
  
  <delete id="delete"
          parameterType="map"> **//map 내장된 객체**
  	DELETE repboard where board_no = #{board_no} AND board_pwd = #{board_pwd}        
  </delete>
  
</mapper>
```

- Test.java

```java
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.my.vo.RepBoard;

public class Test {

	public static void main(String[] args) {
		//XML에서 SqlSessionFactory 빌드하기
		String resource = "mybatis-config.xml"; //test클래스가 있는 디렉토리~
		InputStream inputStream;
		try {
			inputStream = Resources.getResourceAsStream(resource); //getResourceAsStream는 클래스들이 있는 경로 기준으로 xml리소스 파일을 찾아서 리턴을 한다는 뜻
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			//SqlSessionFactory에서 SqlSession 만들기
			SqlSession session = sqlSessionFactory.openSession();
			
			//Query결과가 1개의 행인 경우에는 selectOne메서드를 호출한다
			//0개 행이 검색된 경우는 null
			RepBoard b = session.selectOne("**mybatis.RepBoardMapper**.selectByBoard_no", 1); //mapper네임과 아이디
			System.out.println(b.getBoard_title()+":"+b.getBoard_writer());
//			System.out.println(b);
			
			//Query결과가 여러개의 행인 경우에는 selectList메서드를 호출한다
			//0개 행이 검색된 경우는 size가 0인 list를 반환한다(null 아님)
			List<RepBoard> list = session.selectList("**mybatis.RepBoardMapper**.selectByBoard_titleORBoard_writer", "%글%");
			for(RepBoard b1: list) {
				System.out.println(b1.getBoard_title()+":"+b1.getBoard_writer());
			}
//			RepBoard b2 = new RepBoard(); //RepBoard타입 b2객체 생성
//			b2.setBoard_title("제목Mybatis");
//			b2.setBoard_writer("mybatis");
//			b2.setBoard_pwd("1");
//			session.insert("mybatis.RepBoardMapper.insert", b2);
			
//			Map<String, Object> map = new HashMap<>();
//			map.put("parent_no", 0);
//			map.put("board_title", "제목mybatis1");
//			map.put("board_writer", "mybatis1");
//			map.put("board_pwd", "1");
//			session.insert("mybatis.RepBoardMapper.insert", map);
			try {
				int rowcnt = session.update("**mybatis.RepBoardMapper**.updateBoardCnt", 1);
				System.out.println("수정 건수:" + rowcnt); //0? 1
				//session.commit();
			}catch(Exception e) { //실행도중 문제 발생하면 프로그램이 죽어버린다 그러므로 예외 발생하면 그 즉시 catch구문으로 이동시켜 프로그램이 죽지 않도록 해야한다.
				e.printStackTrace();
			}
			try {
				Map<String, Object> map = new HashMap();
				map.put("board_no", 93);//게시글번호
				map.put("board_pwd", 1);//비밀번호
				int rowcnt = session.delete("**mybatis.RepBoardMapper**.delete", map); //--게시글 삭제
				if(rowcnt == 0) {
					System.out.println("글번호가 없거나 비밀번호가 다릅니다.");
				} else {
					session.commit();	
				}
			}catch(Exception e) { //실행도중 문제 발생하면 프로그램이 죽어버린다 그러므로 예외 발생하면 그 즉시 catch구문으로 이동시켜 프로그램이 죽지 않도록 해야한다.
					e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
```
