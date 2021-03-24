# day13

- orderMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.OrderMapper">
	<resultMap id="orderMap"  type="com.my.vo.OrderInfo" autoMapping="true">
  		<id property="order_no" column="order_no"/>
  		<association property="c" javaType="com.my.vo.Customer" autoMapping="true">
  			<id property="id" column="order_id"/>
  			  
  		</association>
  		<collection property="lines" ofType="com.my.vo.OrderLine" autoMapping="true">
  			<id property="order_no" column="order_no" />
  			<id property="p.prod_no" column="order_prod_no" />
  			
  			<result property="p.prod_no" column="order_prod_no" />
  			<result property="p.prod_name" column="prod_name" />
  			<result property="p.prod_price" column="prod_price"/>  			
  		</collection>
  </resultMap>

	<insert id="insertInfo" parameterType="OrderInfo">
INSERT INTO order_info(order_no, order_id, order_dt)
VALUES (order_seq.NEXTVAL, #{c.id}, SYSDATE)
	</insert>
	
	<insert id="insertLine" parameterType="OrderLine">
INSERT INTO order_line(order_no, order_prod_no, order_quantity)
VALUES (order_seq.CURRVAL, #{p.prod_no}, #{order_quantity})
	</insert>
	<select id="selectById" resultMap="orderMap"  parameterType="string">
    SELECT info.order_no, info.order_dt,
           line.order_prod_no, line.order_quantity,
           p.prod_name, p.prod_price
	FROM order_info info JOIN order_line line ON (info.order_no = line.order_no)
                              JOIN product p ON (line.order_prod_no = p.prod_no)
	WHERE info.order_id=#{order_id}
	ORDER BY info.order_no DESC, order_prod_no ASC
  </select>
	
</mapper>
```

- OrderDAOOracle.java

```java
..
.
@Repository
public class OrderDAOOracle implements OrderDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
..
.	
	@Override
	public List<OrderInfo> selectById(String order_id) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			List<OrderInfo> list = session.selectList("mybatis.OrderMapper.selectById", order_id);
			if(list.size() == 0) {
				throw new FindException("주문내역이 없습니다.");
			}
			return list;
		}catch(Exception e){
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}

}
```

- OrderDAOOracle.java-test

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
public class OrderDAOOracle {
	@Autowired
	private OrderDAO dao;
	
	@Test
	public void selectById() throws FindException {
		String id = "id1";
		List<OrderInfo>  list = dao.selectById(id);
//		assertTrue(list.size() == 1);
		OrderInfo info = list.get(0); //최근 주문내역만 반환, get(list.size()-1) //첫주문내역
		List<OrderLine> lines = info.getLines();
		
		int expSize = 3;
		assertTrue(lines.size() == expSize);
		
		int index = 0;
		for(int i=index; i<expSize; i++) {
			OrderLine line = lines.get(index);
			Product p = line.getP();
			String expProd_no = "C000" + (index+1);
			//String expProd_name = "나이트로 바닐라 크림";
			int expQuantity = index+1;
			assertEquals(expProd_no, p.getProd_no());
			//assertEquals(expProd_name, p.getProd_name());
			assertEquals(expQuantity, line.getOrder_quantity());
		}
	}
..
.
}
```

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled.png)

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%201.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%201.png)

---

- OrderService.java

```java
..
.
@Service
@Log4j
public class OrderService {
	@Autowired
	private OrderDAO dao;
	public void add(OrderInfo info) throws AddException {
		log.info("orderdao=" + dao);
		dao.insert(info);
	}
	public List<OrderInfo> findById(String order_id) throws FindException {
		return dao.selectById(order_id);
	}
}
```

- OrderController.java

```java
..
.
@Controller
public class OrderController{
	@Autowired
	private OrderService service;
	
	@ResponseBody
	@RequestMapping("/putorder")
	public Map<String, Object>  putOrder(HttpSession session){ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		Map<String, Integer> cart= (Map)session.getAttribute("cart"); //장바구니얻기

		OrderInfo info = new OrderInfo(); 
		//주문기본정보설정
		Customer c = new Customer();
		c.setId(loginedId);
		info.setC(c); //주문자ID설정

		//장바구니내용을 주문상세정보객체로 만들어서 리스트에 추가
		List<OrderLine> lines = new ArrayList<>();
		for(String prod_no: cart.keySet()) {
			int quatinty = cart.get(prod_no);
			//주문상세정보객체
			OrderLine line = new OrderLine(); 

			Product p = new Product(); 
			p.setProd_no(prod_no);
			line.setP(p);

			line.setOrder_quantity(quatinty);

			//주문상세정보객체를 리스트에 추가
			lines.add(line); 
		}
		//리스트를 주문기본에 설정
		info.setLines(lines);
		
		try {
			service.add(info); 
			session.removeAttribute("cart"); //장바구니 삭제
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@RequestMapping("/vieworder")
	@ResponseBody
	public Map<String, Object>  viewOrder(HttpSession session){ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		List<OrderInfo> list;
		try {
			list = service.findById(loginedId);
			map.put("status", 1);
			map.put("list", list);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
		
	}
}
```

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%202.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%202.png)

날짜 형태로 포맷 되려면  vo에가서 JsonFormat 어노테이션을 설정하면 된다.

- OrderInfo.java

```java
..
.
public class OrderInfo {
	private int order_no;
	//private String order_id;//??
	private Customer c;
	**@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")**
	private java.util.Date order_dt;
..
.
```

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%203.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%203.png)

---

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%204.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%204.png)

비슷한 일을 매번 처리해야 한다는 불편한 일이 생긴다. 이 컨트롤러를 도와주는 어노테이션 도움을 받으면 된다. 컨트롤러의 앞 단 또는 뒷 단에 하고 싶은 일을 끼워 넣기를 할 수 있는데 이 개념 자체가 AOP가 되는 거다. 핵심 로직은 컨트롤러가 되는 거고 컨트롤러의 일 처리 전후의 공통 사항으로 컨트롤러어드바이스로 작성해서 원하는 위치에 끼워 넣기 하면 된다.

- OrderController.java

```java
..
.
@Controller
public class OrderController{
	@Autowired
	private OrderService service;
	
	@ResponseBody
	@RequestMapping("/putorder")
	public Map<String, Object>  putOrder(HttpSession session) **throws AddException**{ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		Map<String, Integer> cart= (Map)session.getAttribute("cart"); //장바구니얻기

		OrderInfo info = new OrderInfo(); 
		//주문기본정보설정
		Customer c = new Customer();
		c.setId(loginedId);
		info.setC(c); //주문자ID설정

		//장바구니내용을 주문상세정보객체로 만들어서 리스트에 추가
		List<OrderLine> lines = new ArrayList<>();
		for(String prod_no: cart.keySet()) {
			int quatinty = cart.get(prod_no);
			//주문상세정보객체
			OrderLine line = new OrderLine(); 

			Product p = new Product(); 
			p.setProd_no(prod_no);
			line.setP(p);

			line.setOrder_quantity(quatinty);

			//주문상세정보객체를 리스트에 추가
			lines.add(line); 
		}
		//리스트를 주문기본에 설정
		info.setLines(lines);
		
		//try {
			service.add(info); 
			session.removeAttribute("cart"); //장바구니 삭제
			map.put("status", 1);
//		} catch (AddException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
	}
	
	@RequestMapping("/vieworder")
	@ResponseBody
	public Map<String, Object>  viewOrder(HttpSession session) **throws FindException**{ 
		Map<String, Object> map = new HashMap<>();
		//----로그인 여부----
		String loginedId = (String)session.getAttribute("loginInfo");
		if(loginedId == null) { //로그인 안된 경우
			map.put("status", 0);
			return map;
		}
		//로그인된 경우
		List<OrderInfo> list;
		//try {
			loginedId = "aaaaa"; //없는 아이디로 검색시 FindException예상
			list = service.findById(loginedId);
			map.put("status", 1);
			map.put("list", list);
//		} catch (FindException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
		
	}
}
```

- OrderControllerAdivce.java

```java
..
.
@ControllerAdvice(assignableTypes = com.my.control.OrderController.class)
public class OrderControllerAdvice {
	
	@ExceptionHandler
	@ResponseBody
	public Object except(Exception e) {
		Map<String, Object> map = new HashMap<>();
		e.printStackTrace();
		map.put("status", -1);
		map.put("msg", e.getMessage());
		return map;
	}
}
```

- servlet-context.xml

```xml
..
.
<context:component-scan base-package="com.my.control" />
**<context:component-scan base-package="com.my.advice" />**
..
.
```

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%205.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%205.png)

---

- ProductController.java

```java
..
.
@Controller
@RequestMapping("/product/*")
public class ProductController {
	@Autowired
	private ProductService service;
	
	@RequestMapping("/detail")
	public String detail(String prod_no, Model model) throws FindException{
		Product p;
//		try {
			p = service.findByNo(prod_no);
			model.addAttribute("p", p);
			return "productdetailresult";
//		} catch (FindException e) {
//			model.addAttribute("e", e);
//			e.printStackTrace();
//			return "errorresult";
//		}	
	}
	
	@RequestMapping("/list")
	public String list(@RequestParam(name = "prod", required = false, defaultValue = "")String word, Model model) throws FindException{ //요청전달데이터 prod를 word라는 변수로 받아오겠다는 의미
		//String word = request.getParameter("prod");
		//ModelAndView mnv = new ModelAndView();
//		try {
			if(word.equals("")) { //전체검색	
				List<Product> list = service.findAll();
				//mnv.addObject("list", list);
				model.addAttribute("list", list);
			}else { //검색어에 해당하는 상품들만 검색
				List<Product> list = service.findByNoOrName(word);
				//mnv.addObject("list", list);
				model.addAttribute("list", list);
			}
			return "productlistresult";
//		} catch (FindException e) {
//			//mnv.addObject("e", e);
//			//mnv.setViewName("errorresult");
//			model.addAttribute("e", e);
//			e.printStackTrace();
//			return "errorresult";
//		}
	}
}
```

- ProductControllerAdvice.java

```java
..
.

@ControllerAdvice(assignableTypes = com.my.control.ProductController.class)
public class ProductControllerAdvice {
	@ExceptionHandler
	public String except(Exception e, Model model) {
		model.addAttribute("e", e);
		return "errorresult"; //view name
	}
}
```

- errorresult.jsp

```jsx
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

Exception e = (Exception)request.getAttribute("e");
if(e == null){
	return;
}
%>
<script>
alert('<%=e.getMessage()%>');
</script>
```

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%206.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%206.png)

---

- CustomerController.java

```java
..
.
@Controller
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@PostMapping("/login")
	@ResponseBody //json형태로 응답하겠다~
	public Map<String, Object> login(String id, String pwd, HttpSession session) throws FindException{
		Map<String, Object> map = new HashMap<>();
		//try {
			Customer c = service.login(id, pwd);
			session.setAttribute("loginInfo", id); //session에 고객 id정보를 loginInfo라는 attribute라고 추가. 매개변수로 받아오면 된다.
			map.put("status", 1);
//		} catch (FindException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
	}
	
	@RequestMapping("/logout")
	public ResponseEntity logout(HttpSession session) {
		session.removeAttribute("loginInfo");
		return new ResponseEntity<>(HttpStatus.OK); //OK: 응답코드 200번. ok가 응답이 되면 success함수가 호출이 된다
	}
	
	@PostMapping("/iddupchk")
	@ResponseBody
	public Map<String, Integer> idDupChk(String id) throws FindException{
		Map<String, Integer> map = new HashMap<>();
		//try {
			Customer c = service.findById(id);
			map.put("status", 1);
//		} catch (FindException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//		}
		return map;
	}
	
	@PostMapping("/signup")
	@ResponseBody
	public Map<String, Object> signup(Customer c) throws AddException{
		Map<String, Object> map = new HashMap<>();
		//try {
			service.add(c);
			map.put("status", 1);
//		} catch (AddException e) {
//			e.printStackTrace();
//			map.put("status", -1);
//			map.put("msg", e.getMessage());
//		}
		return map;
	}	
}
```

둘 다 공통적이므로 ordercontroller하고 customercontroller 같이 묶자~ OrderControllerAdivce → CustomerOrderControllerAdvice이름으로 바꾸자

- CustomerOrderControllerAdvice.java

```java
package com.my.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.my.control.OrderController;

@ControllerAdvice(assignableTypes = {com.my.control.OrderController.class,
		                             com.my.control.CustomerController.class})
public class CustomerOrderControllerAdvice {
	@ExceptionHandler
	@ResponseBody
	public Object except(Exception e) {
		Map<String, Object> map = new HashMap<>();
		e.printStackTrace();
		map.put("status", -1);
		map.put("msg", e.getMessage());
		return map;
	}
}
```

---

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%207.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%207.png)

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%208.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%208.png)

파일업로드 url을 ./html/upload.html으로 바꾸자~

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%209.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%209.png)

servlet-api가 2점대 버전인 경우 파일 업로드 지원을 잘 안 해준다 그래서 3점대 버전으로 해준다.

mavenrepository사이트로 들어가서 commons-fileupload검색

- mvcspring/pom.xml

```xml
..
.
<!-- 파일업로드/다운로드 -->
<!-- https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>

..
.
```

- servlet-context.xml

```xml
..
.
<beans:bean id="multipartResolver"
		class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
	<beans:property name="defaultEncoding" value="UTF-8"></beans:property>
	<beans:property name="maxUploadSize" value="104857568"></beans:property>
	<beans:property name="maxUploadSizePerFile" value="2097152"></beans:property>
<!-- 	
    <beans:property name="uploadTempDir" value="file:/C:/upload/tmp"></beans:property>
	<beans:property name="maxInMemorySize" value="10485756"></beans:property>
 -->	
    </beans:bean>
..
.
```

파일 업로드 컨트롤러 용을 위한 설정이기 때문에 servlet-context.xml에 넣자

- UploadController.java

```java
..
.
@Controller
@Log4j
public class UploadController {
	@Autowired
	private ServletContext servletContext;
	
	@PostMapping("/upload")
	@ResponseBody
	public void upload(MultipartFile file1, String t) { // 빈문자열을 간단히 응답하고싶어서 void로 선언. ResponseBody가 없으면 MVC구조로 이해해서 view를 찾아내서 view로 forward된다..
		log.info("t=" + t);
		log.info(file1.getOriginalFilename());
		log.info(file1.getSize());
		
		String uploadPath = servletContext.getRealPath("upload"); //업로드될 경로를 설정하기 위해서 -> 실제경로
		log.info(uploadPath);
		
		//String fileName = file1.getOriginalFilename(); //실제파일이름
		//String fileName = "id1_" + file1.getOriginalFilename();
		
		//Universal Unique Identifier
		String fileName = UUID.randomUUID()+"_" + file1.getOriginalFilename(); //UUID.randomUUID() -> 파일 이름 앞에 고유한 값을 만들어 낼 수 있다
		
		File target = new File(uploadPath, fileName);	        
        
		//경로 생성
        if ( ! new File(uploadPath).exists()) { //해당 디렉토리가 있는가 물어보는것
        	log.info("경로생성");
            new File(uploadPath).mkdirs();
        }
        
        //파일 생성
        try {
            FileCopyUtils.copy(file1.getBytes(), target);
            log.info("파일 복사");
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
}
```

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2010.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2010.png)

a.png파일 내용이 네트워크를 통과해서 Controllr까지 전송이 되고 input태그의 name값과 MutipartFile의 파라미터하고 같아야 한다. MutilpartFile은 파일이 아니라 스트림이다. 파일 내용이 한 바이트씩 서버에 전송이 된 거고 그 전송된 내용 스트림이 MultipartFile인 거다.

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2011.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2011.png)

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2012.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2012.png)

---

OrderDAOOracle.java-test 단위테스트 시 오류가 난다..

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2013.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2013.png)

톰캣이 켜져 있으면 ServletContext자동 생성해서 문제가 안되는데 만약 톰캣이 안 켜져있는 상태에서 ServletContext를 못쓴다.

```xml
..
.
<beans:bean class="org.springframework.mock.web.MockServletContext"/>
..
.
```

단위테스트를 위한 객체이다. MockServletContext객체가 스프링 컨테이너에 의해 관리가 되어야 톰캣 실행 안 해도 단위 테스트를 할 수 있다.

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2014.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2014.png)

이런 문제를 모두 해결하려면 servlet-api 버전을 3.1버전 이상으로 올리면 된다.

- pom.xml

```xml
..
.
<!-- Servlet -->
		<!-- <dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>2.5</version>
			<scope>provided</scope>
		</dependency> -->
		
<!-- https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
**<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
</dependency>**
..
.
```

실행결과>

![day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2015.png](day13%202fc9f29872be4adfbb8752c85304596d/Untitled%2015.png)