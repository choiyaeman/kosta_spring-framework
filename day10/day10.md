# day10

![1](https://user-images.githubusercontent.com/63957819/111795540-a5d7e580-890a-11eb-97f7-a69204c3e9b1.png) 

>interface가 mapper파일을 대신할 수 있다.

>int라는 별칭은 원래의 자료형이 java.lang.Integer타입이다. 이렇게 mybatis에는 내장된 별칭이 설정 되어있다. resultType 또는 resultMap 속성은 반드시 있어야 한다.
#
![2](https://user-images.githubusercontent.com/63957819/111795543-a7091280-890a-11eb-9add-92fd8ab8462e.png)

`resultType` : 자료구조 형태로 리턴 해야 할 경우 즉 여러 행을 검색하는 경우 컬렉션타입자체가아닌 컬렉션이 포함된 타입이 될 수 있다. 즉 리스트 타입이나 컬렉션 타입으로 명시하는 게 아니라 자료형으로 명시해줘야 한다.

`insert, update and delete` : dml구문을 처리하는 태그들

한번에 여려 행을 insert할 경우 `foreach` 태그를 이용해서 배열로 전달된 파라미터를 반복문으로 처리 할 수 있다.  
#
![3](https://user-images.githubusercontent.com/63957819/111795545-a7a1a900-890a-11eb-9236-ec65d9a51937.png)

?바인드 변수는 값의 위치에만 올 수 있다. 즉 value에만 올 수 있다. 태그가 점점 많아지면 많아질수록 관리하기 힘들어진다. 하나로 합치는데 오름차순을 할 것인지 내림차순으로 할 것인지 물음표로 한다 하면 안된다. 물음표는 값의 위치에만 올 수 있기 때문이다. 그러므로 #{} 못쓴다.

문자열로 대신하고 싶을 때는 $를 쓰자  

#
![4](https://user-images.githubusercontent.com/63957819/111795547-a7a1a900-890a-11eb-9c64-e779049f4b48.png)

- boardMapper.xml

```java
..
.
<select id="selectByBoard_titleORBoard_writer"
          parameterType="**hashmap**"
          resultType="RepBoard">
    SELECT * FROM repboard WHERE board_title LIKE **'%${word}%'** OR board_writer LIKE **'%${word}%'**
    ORDER BY **${o}**
  </select>
..
.
```

- Test.java

```java
..
.
//Query결과가 여러개의 행인 경우에는 selectList메서드를 호출한다
			//0개 행이 검색된 경우는 size가 0인 list를 반환한다(null 아님)
			Map<String, String> map1 = new HashMap<>();
			map1.put("word", "힘");
			map1.put("o", "board_no DESC"); //최신글 보기위해 내림차순 설정
			List<RepBoard> list = session.selectList("mybatis.RepBoardMapper.selectByBoard_titleORBoard_writer", map1);
			for(RepBoard b1: list) {
				System.out.println(b1.getBoard_title()+":"+b1.getBoard_writer());
			}
..
.
```

실행결과>

![5](https://user-images.githubusercontent.com/63957819/111795548-a83a3f80-890a-11eb-8ec1-fed07ca8dacb.png)

---

![6](https://user-images.githubusercontent.com/63957819/111795552-a83a3f80-890a-11eb-9093-3dca838a3fd2.png)

resultMap은 고객과 상품 주문 테이블을 생각 해보면 여러 관계를 갖고 있듯이 join을 쓸텐데 여러 컬럼들을 가지고 올 경우라면 resultMap을 써야 한다. 

위의 그림을 보면 resultType을 특정 클래스로 설정하면 order_no만 쓰고 끝나버린다. 검색해온 컬럼들이 자동 매핑이 되도록 하려면 resultMap이 필요하다.  
#
![7](https://user-images.githubusercontent.com/63957819/111795554-a8d2d600-890a-11eb-90b7-730cb9c34ea7.png)

→엄청 복잡..  


![8](https://user-images.githubusercontent.com/63957819/111795555-a8d2d600-890a-11eb-9821-a058ca338ba7.png)

→resultMap쓰자  


```java
**주문자id에 해당 주문내역 조회하기**
SELECT info.order_no, info.order_dt,
           line.order_prod_no, line.order_quantity,
           p.prod_name, p.prod_price
FROM order_info info JOIN order_line line ON (info.order_no = line.order_no)
                     JOIN product p ON (line.order_prod_no = p.prod_no)
WHERE info.order_id='id2'
```

![9](https://user-images.githubusercontent.com/63957819/111795556-a96b6c80-890a-11eb-9d9b-9a8d85a7f6fc.png)

- mybatis-config.xml

```java
<mappers>
    <mapper resource="boardMapper.xml"/>
    **<mapper resource="orderMapper.xml"/>** 
  </mappers>
```

- orderMapper.xml

```java
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="mybatis.OrderMapper">
  	<resultMap id="orderMap" type="com.my.vo.OrderInfo" autoMapping="true">
  		<id property="order_no" column="order_no"/>
  		
  		<association property="c" javaType="com.my.vo.Customer" autoMapping="true">
  			<id property="id" column="order_id"/>
  			
  		</association>
  		<collection property="lines" ofType="com.my.vo.OrderLine" autoMapping="true">
  			<id property="order_no" column="order_no"/>
  			<id property="p.prod_no" column="order_prod_no"/>

  			<result property="p.prod_no" column="order_prod_no"/>
  			<result property="p.prod_name" column="prod_name"/>
  			<result property="p.prod_price" column="prod_price"/>
  		</collection>
  	</resultMap>
  	<select id="selectById" resultMap="orderMap" parameterType="string">
	SELECT info.order_no, info.order_dt,
           line.order_prod_no, line.order_quantity,
           p.prod_name, p.prod_price
	FROM order_info info JOIN order_line line ON (info.order_no = line.order_no)
                         JOIN product p ON (line.order_prod_no = p.prod_no)
	WHERE info.order_id=#{order_id}
  	</select>
  </mapper>
```

![10](https://user-images.githubusercontent.com/63957819/111795558-a96b6c80-890a-11eb-8a0e-9b9dae5ee89d.png)

`association`은 1:1의 관계 `collection`은 1:N의 관계이다.

OrderInfo를 1로보고 Orderlines를 N으로 본다.

`autoMapping="true"` 컬럼명과 프로퍼티명 같을 경우 생략 가능  

- Test.java

```java
..
.

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
			..
			.
			
			**List<OrderInfo> infos = session.selectList("mybatis.OrderMapper.selectById", "id2");
			System.out.println(infos.size());** //총행수는 6개인데 orderinfo객체의 개수는 3개 이유는 orderinfo객체 하나에 orderline이 여러개 들어갈수 있기 때문이다
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
```

실행결과>

![11](https://user-images.githubusercontent.com/63957819/111795559-aa040300-890a-11eb-89da-b5d0d97a8ad7.png)

![12](https://user-images.githubusercontent.com/63957819/111795562-aa9c9980-890a-11eb-9d0d-ccd75c372a74.png)

총 행 수는 6개인데 OrderInfo객체의 개수는 3개인 이유는 OrderInfo객체 하나에 OrderIine이 여러 개 들어갈  수 있기 때문이다.  


---

![13](https://user-images.githubusercontent.com/63957819/111795564-aa9c9980-890a-11eb-9848-3cff21c55d90.png)

![14](https://user-images.githubusercontent.com/63957819/111795567-ab353000-890a-11eb-93d8-804976b2e14c.png)

![15](https://user-images.githubusercontent.com/63957819/111795569-ab353000-890a-11eb-8b41-55866221b1cf.png)

![16](https://user-images.githubusercontent.com/63957819/111795571-abcdc680-890a-11eb-9592-d408348c8be3.png)

---

maven repository> mybatis, mybatis-spring 둘다 복사해서 복붙  


![17](https://user-images.githubusercontent.com/63957819/111795573-abcdc680-890a-11eb-9c11-64f3c163bbdd.png)

- boardbackspring/pom.xml

```java
..
.
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
..
.
```

![18](https://user-images.githubusercontent.com/63957819/111795576-ac665d00-890a-11eb-833d-b462078b5ead.png)

mybatis프로젝트에 있는 mybatis-config.xml, boardMapper.xml을 mybackspring src/main/java에 붙여 넣기  

- root-context.xml

```java
..
.
<bean class="org.mybatis.spring.SqlSessionFactoryBean"
		id="sqlSessionFactory">
	<property name="dataSource" ref="dataSource"></property>
	<property name="configLocation" value="classpath:mybatis-config.xml">
	</property>
	</bean>
..
.
```

![19](https://user-images.githubusercontent.com/63957819/111795578-ac665d00-890a-11eb-9e87-dbad32088a5a.png)

![20](https://user-images.githubusercontent.com/63957819/111795579-acfef380-890a-11eb-9320-5dfc9e9a33a1.png)

- RepBoardDAOOracle.java

```java
..
.
@Autowired
	private SqlSessionFactory sqlSessionFactory;

//	public RepBoard selectByBoard_no(int board_no) throws FindException{
//		Connection con = null;
//		try {
//			//con = MyConnection.getConnection();
//			con = ds.getConnection();
//		}catch(Exception e) {
//			throw new FindException(e.getMessage());
//		}
//		PreparedStatement pstmt = null;
//		String selectAllSQL = "SELECT *\r\n" + 
//				"FROM repboard\r\n" + 
//				"WHERE board_no=?";
//		ResultSet rs = null;
//		try {
//			pstmt = con.prepareStatement(selectAllSQL);
//			pstmt.setInt(1, board_no);
//			rs = pstmt.executeQuery();
//			if(rs.next()) {
//				int parent_no = rs.getInt("parent_no");
//				String board_title = rs.getString("board_title");
//				String board_writer = rs.getString("board_writer");
//				Date board_dt = rs.getDate("board_dt");
//				String board_pwd = rs.getString("board_pwd");
//				int board_cnt = rs.getInt("board_cnt");
//				RepBoard board = new RepBoard(board_no, parent_no, board_title, board_writer, board_dt, board_pwd, board_cnt);
//				return board;
//			}else{
//				throw new FindException("게시글이 없습니다");
//			}
//		}catch(SQLException e) {
//			throw new FindException(e.getMessage());
//		}finally {
//			//MyConnection.close(con, pstmt, rs);
//		}
//	}
	
	public RepBoard selectByBoard_no(int board_no) throws FindException{
		//SqlSessionFactory에서 SqlSession 만들기
		//Query결과가 1개의 행인 경우에는 selectOne메서드를 호출한다
		//0개 행이 검색된 경우는 null을 반환한다
		SqlSession session = sqlSessionFactory.openSession();
		RepBoard b = session.selectOne("mybatis.RepBoardMapper.selectByBoard_no", board_no);
		if(b == null) {
			throw new FindException("게시글이 없습니다");
		}
		return b;
	}
..
.
```

- src/test/java → RepBoardDao

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
		int board_no = 68;	
		int expParent_no = 0;
		String expBoard_title = "힘들다";
		String expBoard_writer = "최콩쥐";
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
	
	//@Test
	public void selectAll() throws FindException {	
		List<RepBoard> list = dao.selectAll();
//		System.out.println(list.size()); //7
		int expListSize =  7;
		assertTrue(expListSize == list.size());
	}

}
```

실행결과>

![21](https://user-images.githubusercontent.com/63957819/111795582-acfef380-890a-11eb-9113-60c4a9c434e0.png)

---

- RepBoardDAOOracle.java

```java
package com.my.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.sql.MyConnection;
//import com.my.sql.MyConnection;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@Repository
@Qualifier(value = "oracle")
@Log4j
public class RepBoardDAOOracle implements RepBoardDAO {
//	@Autowired
//	@Qualifier("hikarids")
	
	@Autowired
	private DataSource ds;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	public void delete(int board_no, String board_pwd) throws RemoveException{
		try {
			SqlSession session = sqlSessionFactory.openSession();
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
		}
	}
	public void update(RepBoard board, String board_pwd) throws ModifyException{
		if(board.getBoard_title() == null && board.getBoard_pwd() == null) {
			throw new ModifyException("수정할 내용이 없습니다");
		}
		try {
			SqlSession session = sqlSessionFactory.openSession();
			Map<String, Object> map = new HashMap<>();
			map.put("board", board);
			map.put("board_pwd", board_pwd);
			int rowcnt = session.update("mybatis.RepBoardMapper.update", map);
			if(rowcnt == 0) {
				throw new ModifyException("게시글이 없습니다");
			}
			session.commit();
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}
	}
	
	public void updateBoardCnt(int board_no) throws ModifyException{
		try {
			SqlSession session = sqlSessionFactory.openSession();
			int rowcnt = session.update("mybatis.RepBoardMapper.updateBoardCnt", board_no);
			if(rowcnt == 0) {
				throw new ModifyException("게시글이 없습니다");
			}
			session.commit();
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}
	}
	public RepBoard selectByBoard_no(int board_no) throws FindException{
		//SqlSessionFactory 에서 SqlSession 만들기
		//Query결과가 1개의 행인 경우에는 selectOne메서드를 호출한다
		//0개행이 검색된 경우는 null을 반환한다
		try {
			SqlSession session = sqlSessionFactory.openSession();
			RepBoard b = session.selectOne("mybatis.RepBoardMapper.selectByBoard_no", board_no);
			if(b == null) {
				throw new FindException("게시글이 없습니다");
			}
			return b;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
	}
	
	public List<RepBoard> selectByBoard_titleORBoard_writer(String word) throws FindException{
		try {
			SqlSession session = sqlSessionFactory.openSession();
			HashMap<String, String> map = new HashMap<>();
			map.put("word", word);
			map.put("o", "board_no DESC");
		
			List<RepBoard> list = session.selectList(
					"mybatis.RepBoardMapper.selectByBoard_titleORBoard_writer"
					, map);
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
	}
	public List<RepBoard> selectAll() throws FindException{
		try {
			SqlSession session = sqlSessionFactory.openSession();
			List<RepBoard> list = session.selectList("mybatis.RepBoardMapper.selectAll");
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}
	}
	public void insert(RepBoard board) throws AddException{
		try {
			SqlSession session = sqlSessionFactory.openSession();
			session.insert("mybatis.RepBoardMapper.insert", board);
			session.commit();//???
		}catch(Exception e) {
			throw new AddException(e.getMessage());
		}
	}
//	public static void main(String[] args) {
//		RepBoardDAOOracle dao = new RepBoardDAOOracle();
//		String board_title = "테스트1";
//		String board_writer = "작성자1";
//		String board_pwd = "p1";
//		RepBoard board = new RepBoard(board_title, board_writer, board_pwd);
//		
//		try {
//			dao.insert(board);
//		} catch (AddException e) {
//			e.printStackTrace();
//		}
		
//		int parent_no = 6;
//		String board_title = "테스트1-답1";
//		String board_writer = "작성자2";
//		String board_pwd = "p2";
//		RepBoard board = new RepBoard(parent_no, board_title, board_writer, board_pwd);
//		
//		try {
//			dao.insert(board);
//		} catch (AddException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			System.out.println(dao.selectAll());
//		} catch (FindException e) {
//			e.printStackTrace();
//		}
		
//		int board_no = 6;
//		try {
//			System.out.println(dao.selectByBoard_no(board_no));
//		} catch (FindException e) {
//			e.printStackTrace();
//		}
		
//		String word = "2";
//		try {
//			System.out.println(dao.selectByBoard_titleORBoard_writer(word));
//		} catch (FindException e) {
//			e.printStackTrace();
//		}
	
//		int board_no = 6;
//		try {
//			dao.updateBoardCnt(board_no);
//		} catch (ModifyException e) {
//			e.printStackTrace();
//		}
		
//		int board_no = 6;
//		String board_pwd = "upp1";
//		RepBoard board = new RepBoard();
//		board.setBoard_no(board_no);
//		board.setBoard_pwd(board_pwd);
//		try {
//			dao.update(board, "p1");
//		} catch (ModifyException e) {
//			e.printStackTrace();
//		}
		
//		int board_no = 6;
//		String board_title = "up제목1";
//		String board_pwd = "upp2";
//		RepBoard board = new RepBoard();
//		board.setBoard_no(board_no);
//		board.setBoard_title(board_title);
//		board.setBoard_pwd(board_pwd);
//		try {
//			dao.update(board, "upp1");
//		} catch (ModifyException e) {
//			e.printStackTrace();
//		}
		

		
//		int board_no = 7;
//		String board_pwd = "p2";
//		try {
//			dao.delete(board_no, board_pwd);
//		} catch (RemoveException e) {
//			e.printStackTrace();
//		}
//	}
}
```

- baordMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.RepBoardMapper">
  <delete id="delete" parameterType="map">
  	DELETE repboard  where board_no = #{board_no} AND board_pwd = #{board_pwd}
  </delete>
  <update id="update" parameterType="map">
  UPDATE repboard 
  <set>
  <if test="board.board_title != null">board_title=#{board.board_title},</if>
  <if test="board.board_pwd != null">board_pwd=#{board.board_pwd}</if>
  </set>
  WHERE board_no = #{board.board_no} AND board_pwd = #{board_pwd}
  </update>
  
  <update id="updateBoardCnt"
  		  parameterType="int">	
  		UPDATE repboard SET board_cnt = board_cnt+1
        WHERE board_no = #{board_no}
  </update>
  
  <select id="selectByBoard_no" 
          resultType="RepBoard">
    SELECT * FROM repboard WHERE board_no=#{aaa}
  </select>
  <select id="selectByBoard_titleORBoard_writer"
          parameterType="hashmap" 
          resultType="RepBoard">
    SELECT * FROM repboard 
    WHERE board_title LIKE '%${word}%' OR board_writer LIKE '%${word}%'  
    ORDER BY  ${o}    
  </select>
  
  <select id="selectAll" resultType="RepBoard">
	SELECT level, repboard.*
	FROM repboard 
	START WITH parent_no = 0
	CONNECT BY PRIOR board_no = parent_no
	ORDER SIBLINGS BY board_no DESC
  </select>
  
  <insert id="insert"
          parameterType="RepBoard">
  	INSERT INTO repboard(board_no,        parent_no,    board_title, board_writer, board_dt, BOARD_PWD, board_cnt)  
	VALUES		 (board_seq.NEXTVAL,   #{parent_no}, #{board_title}, #{board_writer},   SYSDATE, #{board_pwd},         0)
		
  </insert>
</mapper>
```

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

import com.my.dao.RepBoardDAO;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

//Spring용 단위테스트
//@WebAppConfiguration //JUnit5인 경우 
@RunWith(SpringJUnit4ClassRunner.class) //Juni4인 경우

//Spring 컨테이너용 XML파일 설정
@ContextConfiguration(locations={
		"file:src/main/webapp/WEB-INF/spring/root-context.xml", 
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@Log4j
public class RepBoardDAOOracle {
	@Autowired
	@Qualifier("oracle")
	private RepBoardDAO dao;
	//private RepBoardDAO dao = new com.my.dao.RepBoardDAOOracle();
	
//	@Test
	public void selectByBoard_no() {
		int board_no = 68; 
		int expParent_no = 0;
		String expBoard_title = "힘들다";
		String expBoard_writer = "최콩쥐"; 
		try {
			RepBoard b = dao.selectByBoard_no(board_no);
			assertNotNull(b);			
//			assertEquals(expParent_no, b.getParent_no());
			assertTrue(expParent_no  == b.getParent_no());
			assertEquals(expBoard_title, b.getBoard_title());
			assertEquals(expBoard_writer, b.getBoard_writer());			
		} catch (FindException e) {
			e.printStackTrace();
		}
	}	
	//@Test
	public void selectAll() throws FindException {		
		List<RepBoard> list = dao.selectAll();
		int expListSize = 21;
		assertTrue(expListSize == list.size());
	}
	
	@Test
	public void update() throws FindException, ModifyException {
		int board_no = 2;
		
		RepBoard board = dao.selectByBoard_no(board_no);
		String board_pwd = board.getBoard_pwd();
		//------제목과 비번 모두 변경----------
		log.info("제목과 비번 모두 변경");
		String expectedTitle = "upd-mybatis3";
		String expectedPwd = "upd3";
		board.setBoard_title(expectedTitle);
		board.setBoard_pwd(expectedPwd);		
		dao.update(board, board_pwd);
		
		board = dao.selectByBoard_no(board_no);
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
		
		
		//-----제목만 변경----------
		log.info("제목만 변경");
		board_pwd = board.getBoard_pwd();
		
		expectedTitle = "upd-mybatis3";
		board.setBoard_title(expectedTitle);
		board.setBoard_pwd(null);
		log.error(board.getBoard_title() + ":" + board.getBoard_pwd());
		dao.update(board, board_pwd);
		board = dao.selectByBoard_no(board_no);		
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
		
		//-----비번만 변경----------
		log.info("비번만 변경");
		board_pwd = board.getBoard_pwd();
		expectedPwd = "upd2";
		board.setBoard_title(null);
		board.setBoard_pwd(expectedPwd);
		dao.update(board, board_pwd);
		board = dao.selectByBoard_no(board_no);		
		assertEquals(expectedTitle, board.getBoard_title());
		assertEquals(expectedPwd, board.getBoard_pwd());
	}
	
	//@Test
	public void updateCnt() throws ModifyException, FindException {
		int board_no = 1;
		int beforeCnt = dao.selectByBoard_no(board_no).getBoard_cnt();
		dao.updateBoardCnt(board_no);
		
		RepBoard board = dao.selectByBoard_no(board_no);
		int expectedCnt = beforeCnt+1;
		assertEquals(expectedCnt, board.getBoard_cnt());
	}
	
	//@Test
	public void delete() throws FindException, RemoveException {
		int board_no = 60;
		String board_pwd = dao.selectByBoard_no(board_no).getBoard_pwd();
		dao.delete(board_no, board_pwd);
	}
}
```

- log4j.xml

```java
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE log4j:configuration SYSTEM "http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

	<!-- Appenders -->
	<appender name="console" class="org.apache.log4j.ConsoleAppender">
		<param name="Target" value="System.out" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%-5p: %c - %m%n" />
		</layout>
	</appender>
	
	<!-- Application Loggers -->
	<logger name="com.my.vo">
		<level value="error" />
	</logger>
	
	<!-- 3rdparty Loggers -->
	<logger name="org.springframework.core">
		<level value="info" />
	</logger>	
	
	<logger name="org.springframework.beans">
		<level value="info" />
	</logger>
	
	<logger name="org.springframework.context">
		<level value="info" />
	</logger>

	<logger name="org.springframework.web">
		<level value="info" />
	</logger>

	<!-- Root Logger -->
	<root>
		<priority value="info" />
		<appender-ref ref="console" />
	</root>	
</log4j:configuration>
```

실행결과>

![22](https://user-images.githubusercontent.com/63957819/111795583-ad978a00-890a-11eb-8e9b-2a7481c6c5ea.png)
