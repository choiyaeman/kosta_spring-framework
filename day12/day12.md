# day12

- CustomerController.java

```java
..
.
@PostMapping("/iddupchk")
	@ResponseBody
	public Map<String, Integer> idDupChk(String id) {
		Map<String, Integer> map = new HashMap<>();
		try {
			Customer c = service.findById(id);
			map.put("status", 1);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", -1);
		}
		return map;
	}
..
.
```

실행결과>

![1](https://user-images.githubusercontent.com/63957819/112120129-5262e180-8c01-11eb-981e-d7e04613d9d3.png)

---

- CustomerController.java

```java
..
.
@PostMapping("/signup")
	@ResponseBody
	public Map<String, Object> signup(Customer c) {
		Map<String, Object> map = new HashMap<>();
		try {
			service.add(c);
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
..
.
```

실행결과> cmd창으로 가입한 정보가 잘 들어가 있는지 확인

```jsx
SQL> select * from postal where buildingno='3611011200101720001000001';

no rows selected

SQL> select count(*) from postal;

  COUNT(*)
----------
        20

SQL> select buildingno from postal;

BUILDINGNO
--------------------------------------------------
3611010500100190001000001
3611010500104060000000001
3611010500105120002000001
..
.

SQL> select id,pwd,name,addr1 from customer;
..
.
ID         PWD        NAME
---------- ---------- ------------------------------
ADDR1
------------------------------------------------------------
yaema      1234       최예만
1층
```

---

![2](https://user-images.githubusercontent.com/63957819/112120135-53940e80-8c01-11eb-9d5a-7c309f71afe2.png)

vo와 dto의 공통점과 차이점?

vo나 dto는 자료를 갖고 있는 공통점이 있고 차이점으로는 vo는 값을 고스란히 가지고 있는 상태 dto는 값을 전달하기 쉽도록 약간 가공한 상태를 말한다.

Product.java는 vo 성격에 가깝다.. Postal.java는 dto 성격에 가깝다

Postal 테이블 구조를 보면 컬럼들이 엄청 많은데 컬럼들 전체를 다 갖지 않고 사용하기 편하게 가공 예를들어 city..

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
  	<typeAlias alias="Customer" type="com.my.vo.Customer"/>
  	<typeAlias alias="Product" type="com.my.vo.Product"/>
  </typeAliases>
  <mappers>
    <mapper resource="boardMapper.xml"/>
    <mapper resource="customerMapper.xml"/>
    <mapper resource="productMapper.xml"/>
  </mappers>
</configuration>
```

- productMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.ProductMapper">
  <!-- 상품번호로 검색 -->
  <select id="selectByNo" parameterType="string" resultType="Product">
  SELECT * FROM product WHERE prod_no=#{prod_no}
  </select>
  
  <!-- 상품번호나 이름으로 검색 -->
  <select id="selectByNoOrName" parameterType="string" resultType="Product">
  SELECT * FROM product WHERE prod_no LIKE '%${value}%' OR
                              prod_name LIKE '%${value}%'
  ORDER BY prod_name ASC
  </select>
  
  <!-- 상품전체 검색 -->
  <select id="selectAll" resultType="Product">
  SELECT * FROM product
  ORDER BY prod_name ASC
  </select>
</mapper>
```

string타입으로 ${} 사용할 때에는 string의 value프로퍼티로 사용해야 한다.

- ProductDAOOracle.java

```java
..
.
@Repository
public class ProductDAOOracle implements ProductDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	@Override
	public Product selectByNo(String prod_no) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Product p = session.selectOne("mybatis.ProductMapper.selectByNo", prod_no);
			if(p == null) {
				throw new FindException("상품이 없습니다.");
			}
			return p;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}

	@Override
	public List<Product> selectByNoOrName(String word) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			List<Product> list = session.selectList("mybatis.ProductMapper.selectByNoOrName", word);
			
			if(list.size() == 0) {
				throw new FindException("상품이 없습니다.");
			}
			return list;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
	
	@Override
	public List<Product> selectAll() throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			List<Product> list = session.selectList("mybatis.ProductMapper.selectAll");
			if(list.size() == 0) {
				throw new FindException("상품이 없습니다");
			}
			return list;
		}catch(Exception e){
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
..
.

}
```

- ProductDAOOracle.java-test

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
public class ProductDAOOracle {
	@Autowired
	private ProductDAO dao;
	
	@Test
	public void selectByNo() throws FindException {
		String prod_no = "C0001";
		String expName = "나이트로 바닐라 크림";
		
		Product p = dao.selectByNo(prod_no);
		assertNotNull(p);
		assertEquals(expName, p.getProd_name());
		
	}	

	@Test
	public void selectAll() throws FindException {		
		List<Product> list = dao.selectAll();
		int expListSize = 0;
		assertTrue(expListSize < list.size());
	}
	
	@Test
	public void selectByNoOrName() throws FindException, ModifyException {
		String word = "콜";
		List<Product> list = dao.selectByNoOrName(word);
		int expListSize = 6;
		assertEquals(expListSize,list.size());
	}
}
```

실행결과>

![3](https://user-images.githubusercontent.com/63957819/112120138-53940e80-8c01-11eb-9b80-a9b424387229.png)

---

- ProductService.java

```java
package com.my.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.dao.ProductDAO;
import com.my.exception.FindException;
import com.my.vo.Product;
@Service
public class ProductService {
	@Autowired
	private ProductDAO dao;
	
	public Product findByNo(String prod_no) throws FindException{
		return dao.selectByNo(prod_no);
	}
	
	public List<Product> findAll() throws FindException{
		return dao.selectAll();
	}
	public List<Product> findByNoOrName(String prod) 
			throws FindException{
		return dao.selectByNoOrName(prod);
	}
}
```

서블릿이 직접 응답하는 구조로 만들지 않고 jsp페이지로 이동하도록 했다.

- ProductController.java

```java
..
.
@Controller
public class ProductController {
	@Autowired
	private ProductService service;
	
	@RequestMapping("/product/list")
	public String list(@RequestParam(name = "prod", required = false, defaultValue = "")String word, Model model) { //요청전달데이터 prod를 word라는 변수로 받아오겠다는 의미
		//String word = request.getParameter("prod");
		//ModelAndView mnv = new ModelAndView();
		try {
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
		} catch (FindException e) {
			//mnv.addObject("e", e);
			//mnv.setViewName("errorresult");
			model.addAttribute("e", e);
			e.printStackTrace();
			return "errorresult";
		}
	}
	
//	public ModelAndView list(@RequestParam(name = "prod")String word) { //요청전달데이터 prod를 word라는 변수로 받아오겠다는 의미
//		//String word = request.getParameter("prod");
//		ModelAndView mnv = new ModelAndView();
//		try {
//			if(word == null) { //전체검색	
//				List<Product> list = service.findAll();
//				mnv.addObject("list", list);
//			}else { //검색어에 해당하는 상품들만 검색
//				List<Product> list = service.findByNoOrName(word);
//				mnv.addObject("list", list);
//			}
//			mnv.setViewName("productlistresult");
//		} catch (FindException e) {
//			mnv.addObject("e", e);
//			mnv.setViewName("errorresult");
//			e.printStackTrace();
//		}
//		return mnv;
//	}
	
	
}
```

spring에서도 mvc구조를 취하려면 ModelAndView타입으로 할 수 있겠지만 복사해서 선언부를 바꾸자! ModelAndView를 쪼개자~ 매개변수를 모델을 정하고 리턴 타입을 String을 쓰는 것과 효과가 똑같다. 

required = false → 전달이 안될 수도 있다는 것을 설정하자. 반드시 전달 되지 않아도 돼~ 전달이 안됐을 때는 기본 값을 빈 문자열로 설정.

실행결과>

![4](https://user-images.githubusercontent.com/63957819/112120140-542ca500-8c01-11eb-80f2-9421f1e78d25.png)

---

- ProductController.java

```java
..
.
@Controller
public class ProductController {
	@Autowired
	private ProductService service;
	
	@RequestMapping("/product/detail")
	public String detail(String prod_no, Model model) {
		Product p;
		try {
			p = service.findByNo(prod_no);
			model.addAttribute("p", p);
			return "productdetailresult";
		} catch (FindException e) {
			model.addAttribute("e", e);
			e.printStackTrace();
			return "errorresult";
		}	
	}
..
.	
}
```

실행결과>

![5](https://user-images.githubusercontent.com/63957819/112120142-54c53b80-8c01-11eb-85f7-9fdf9427d40e.png)

---

- ProductController.java

```java
..
.
@Controller
**@RequestMapping("/product/*")**
public class ProductController {
	@Autowired
	private ProductService service;
	
	@RequestMapping("/detail")
	public String detail(String prod_no, Model model) {
		Product p;
		try {
			p = service.findByNo(prod_no);
			model.addAttribute("p", p);
			return "productdetailresult";
		} catch (FindException e) {
			model.addAttribute("e", e);
			e.printStackTrace();
			return "errorresult";
		}	
	}
	
	@RequestMapping("/list")
	public String list(@RequestParam(name = "prod", required = false, defaultValue = "")String word, Model model) { //요청전달데이터 prod를 word라는 변수로 받아오겠다는 의미
		//String word = request.getParameter("prod");
		//ModelAndView mnv = new ModelAndView();
		try {
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
		} catch (FindException e) {
			//mnv.addObject("e", e);
			//mnv.setViewName("errorresult");
			model.addAttribute("e", e);
			e.printStackTrace();
			return "errorresult";
		}
	}
}
```

---

![6](https://user-images.githubusercontent.com/63957819/112120145-54c53b80-8c01-11eb-8198-9a6180fbfecb.png)

장바구니에서는 dao, vo가 필요 없다. mybatis쪽 이 필요 없으므로 쉽게 처리 가능하다. 컨트롤러용 메서드만 만들어주면 된다. 클라이언트에게 응답하지 않도록 구성되어 있으므로 PutCartServlet에서도 결과를 응답하지 않는다. front쪽에서도 응답 받은 결과가 있다해도 아무런 일을 하지 않는다. 응답할 내용이 없다면 메서드의 리턴타입을 ResponseEntity타입으로 응답을 해주면 된다

- CartController.java

```java
..
.
@Controller
@Log4j
public class CartController {
	@RequestMapping("/putcart")
	public ResponseEntity put(String prod_no, int quantity, HttpSession session) {
		// cart라는 이름의 속성값 얻기
		Map<String, Integer> cart = (Map) session.getAttribute("cart");

		// cart가 없으면
		if (cart == null) {
			cart = new HashMap<>();
			session.setAttribute("cart", cart);
		}

		// cart에서 상품번호에 해당하는 수량을 얻기
		Integer quantity2 = cart.get(prod_no);
		if (quantity2 != null) { // cart에 상품번호가 있는경우
			quantity += quantity2; // 수량을 증가
		}
		log.info("장바구니에 넣기할 상품번호:" + prod_no + ", 수량:" + quantity);
		// cart에 상품번호, 수량추가
		cart.put(prod_no, quantity);

		// 장바구니확인 테스트코드
		log.info("장바구니 넣기 확인");
		Set<String> keys = cart.keySet();// 키들
		for (String key : keys) {
			log.info(key + ":" + cart.get(key));
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
```

실행결과>

![7](https://user-images.githubusercontent.com/63957819/112120150-555dd200-8c01-11eb-9028-c61e383e6df4.png)

![8](https://user-images.githubusercontent.com/63957819/112120152-555dd200-8c01-11eb-80b6-93a05581d2bc.png)

---

![9](https://user-images.githubusercontent.com/63957819/112120154-55f66880-8c01-11eb-9f2b-0c88b8a20b6c.png)

문자열 형태로 자바스크립트가 json형태로 처리 해줄 거다. 

map에 추가된 내용을 반복 수행하면서 list에 담아서 반환만 해주면 된다. 리턴 타입을 Object타입으로 선언

- CartController.java

```jsx
..
.
@Controller
@Log4j
public class CartController {
	@Autowired
	private ProductService service;
	
	@RequestMapping("/viewcart")
	@ResponseBody
	public Object view(HttpSession session) { // 요청 전달데이터 없으므로 매개변수도 없다
		Map<String, Integer> cart = (Map) session.getAttribute("cart");
		if (cart == null || cart.size() == 0) { // 장바구니가 없거나 장바구니가 비어있다면
			// out.print("{\"status\": -1}");
			Map<String, Integer> map = new HashMap();
			map.put("status", -1);
			return map;
		}
		// 장바구니가 있는 경우
		// 맵생성
		Map<Product, Integer> map = new HashMap<>();

		// 장바구니의 상품번호별 상품정보얻기
		Set<String> prod_nos = cart.keySet();
		//ProductService service = new ProductService();
		for (String prod_no : prod_nos) {
			try {
				Product p = service.findByNo(prod_no);// 상품번호별 상품정보얻기
				int quantity = cart.get(prod_no);// 장바구니에 담긴 수량
				map.put(p, quantity); // 맵에 추가
			} catch (FindException e) {
				e.printStackTrace();
			}
		}
		List<Map<String, Object>> list = new ArrayList<>();
		for (Product p : map.keySet()) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("prod_no", p.getProd_no());
			map1.put("prod_name", p.getProd_name());
			map1.put("prod_price", p.getProd_price());
			int quantity = map.get(p);
			map1.put("quantity", quantity);
			list.add(map1);
		}
		return list;
	}
..
.
}
```

실행결과>

![10](https://user-images.githubusercontent.com/63957819/112120155-568eff00-8c01-11eb-8ede-78f43a45de37.png)

![11](https://user-images.githubusercontent.com/63957819/112120157-568eff00-8c01-11eb-913c-82cd87be7bbd.png)

---

![12](https://user-images.githubusercontent.com/63957819/112120160-57279580-8c01-11eb-9579-8cbe3bb3bf84.png)

장바구니 내용이 주문에 포함되어야 한다. db에 저장하는 작업이 필요 orderDAO 클래스 필요, OrderInfo, OrderLine테이블에 자료를 추가하는 작업이 수행되어야 한다. dao를 사용하는 쪽이기 때문에 mybatis를 사용해야 한다. 주문을 OrderInfo쪽에서 성공이 됐으나 OrderLine에 상세 정보를 추가하려는 데 문제가 발생하면 롤백 작업을 해야 한다. 두 테이블의 자료를 완벽히 처리가 완료가 되어야만 주문 작업이 성공됐다라고 보는 것이다. 한쪽이라도 어긋나서 저장이 안됐다면 원 상태로 다시 돌려야 한다. 주문 트랜잭션을 생각하면 된다.

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
  	<typeAlias alias="Customer" type="com.my.vo.Customer"/>
  	<typeAlias alias="Product" type="com.my.vo.Product"/>
  	**<typeAlias alias="OrderInfo" type="com.my.vo.OrderInfo"/>
  	<typeAlias alias="OrderLine" type="com.my.vo.OrderLine"/>**
  </typeAliases>
  <mappers>
    <mapper resource="boardMapper.xml"/>
    <mapper resource="customerMapper.xml"/>
    <mapper resource="productMapper.xml"/>
    **<mapper resource="orderMapper.xml"/>**
  </mappers>
</configuration>
```

- orderMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.OrderMapper">
	<insert id="insertInfo" parameterType="OrderInfo">
INSERT INTO order_info(order_no, order_id, order_dt)
VALUES (order_seq.NEXTVAL, **#{c.id}**, SYSDATE)
	</insert>
	
	<insert id="insertLine" parameterType="OrderLine">
INSERT INTO order_line(order_no, order_prod_no, order_quantity)
VALUES (order_seq.CURRVAL, **#{p.prod_no}**, #{order_quantity})
	</insert>
</mapper>
```

주문자 아이디 정보는 Customer의 id로 세팅이 되어있다. has a 관계이다.

주문 상품 번호는 전달로 파라미터 객체의 orderline타입의 객체의 p가 상품이고 product인 p가 갖고 있는 정보 중에 prod_no가 될 거다. 

- OrderDAOOracle.java

```java
..
.
@Repository
public class OrderDAOOracle implements OrderDAO {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Override
	public void insert(OrderInfo info) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			//info추가
			insertInfo(session, info);
			
			//info의 lines추가
			insertLines(session, info.getLines());
		}catch(Exception e) {
			throw new AddException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
	
	private void insertInfo(SqlSession session, OrderInfo info)  throws AddException{
		session.insert("mybatis.OrderMapper.insertInfo", info);
	}
	private void insertLines(SqlSession session, List<OrderLine> lines)  throws AddException{
		for(OrderLine line: lines) {
			session.insert("mybatis.OrderMapper.insertLine", line);
		}
	}
	
	@Override
	public List<OrderInfo> selectById(String order_id) throws FindException {
		
		return null;
	}

}
```

info에 주문 기본 정보 뿐만 아니라 상세 정보들이 들어있다.

- OrderDAOOracle-test

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
	
//	@Test
	public void selectById() throws FindException {
		String id = "id1";
		List<OrderInfo>  list = dao.selectById(id);
//		assertTrue(list.size() == 1);
		OrderInfo info = list.get(list.size());
		List<OrderLine> lines = info.getLines();
		
		int expSize = 3;
		assertTrue(lines.size() == expSize);
		
		int index = 0;
		for(int i=index; i<expSize; i++) {
			OrderLine line = lines.get(index);
			Product p = line.getP();
			String expProd_no = "C000" + (index+1);
			String expProd_name = "아메리카노";
			int expQuantity = index+1;
			assertEquals(expProd_no, p.getProd_no());
			assertEquals(expProd_name, p.getProd_name());
			assertEquals(expQuantity, line.getOrder_quantity());
		}
	}

	@Test
	public void insert() throws AddException {
		String id = "id1";
		OrderInfo info = new OrderInfo();
		Customer c = new Customer(); 
		c.setId(id);
		info.setC(c);
		List<OrderLine> lines = new ArrayList<>();
		info.setLines(lines);
		for(int i=1; i<=3; i++) {
			Product p = new Product();
			p.setProd_no("C000"+i);
			int order_quantity = i;
			OrderLine line = new OrderLine();
			line.setP(p);
			line.setOrder_quantity(order_quantity);
			lines.add(line);
		}
		info.setLines(lines);
	
		dao.insert(info);
	}
}
```

```bash
SQL> select * from order_info;

  ORDER_NO ORDER_ID   ORDER_DT
---------- ---------- --------
        14 id1        21/03/23

14 rows selected.

SQL> select * from order_line;

  ORDER_NO ORDER_PROD ORDER_QUANTITY
---------- ---------- --------------/
        14 C0001                   1
        14 C0002                   2
        14 C0003                   3
```

실행결과>

![13](https://user-images.githubusercontent.com/63957819/112120164-57279580-8c01-11eb-8051-d2f601820b22.png)

---

```java
..
.
	@Test
	public void insert() throws AddException {
		String id = "id1";
		OrderInfo info = new OrderInfo();
		Customer c = new Customer(); 
		c.setId(id);
		info.setC(c);
		
		List<OrderLine> lines = new ArrayList<>();
		info.setLines(lines);
		for(int i=1; i<=3; i++) {
			Product p = new Product();
			p.setProd_no("C000"+i);
			int order_quantity = i*100; //SQL구문오류 발생..최대 두자리를 넘어선 세자리로 지정했기 때문
			OrderLine line = new OrderLine();
			line.setP(p);
			line.setOrder_quantity(order_quantity);
			lines.add(line);
		}
		info.setLines(lines);
	
		dao.insert(info);
	}
}
```

실행결과>

![14](https://user-images.githubusercontent.com/63957819/112120166-57c02c00-8c01-11eb-9841-4535bab9ef58.png)

```bash
SQL> select * from order_info;

  ORDER_NO ORDER_ID   ORDER_DT
---------- ---------- --------
        1 id1        21/03/23
        2 id1        21/03/23

15 rows selected.

SQL> select * from order_line;
ORDER_NO ORDER_PROD ORDER_QUANTITY
---------- ---------- --------------
        1 C0001                   1
        1 C0002                   2
        1 C0003                   3
```

---

![15](https://user-images.githubusercontent.com/63957819/112120169-57c02c00-8c01-11eb-825f-2306680f5e5c.png)

주문 기본 테이블의 작업이 롤백이 되지 않고 남아있다..트랜잭션 처리를 개발자가 맡기면 안된다. 트랜잭션 처리를 도와주는 방법으로 선언적 트랜잭션 처리를 하면 된다. 즉 스프링 컨테이너가 알아서 할 거다.  AOP개념을 이용해서 트랜잭션 관리를 해보자.

`PROPAGATION_REQUIRED` : uncheckedException일어나면 트랜잭션 전체가 롤백 된다. uncheckedException이란 컴파일러에 의해서 감지되지 않는 exception이라 한다.

`PROPAGATION_NOT_SUPPORTED` : 첫 번째 트랜잭션이 보류되면서 두 번째 메소드가 트랜잭션 없이 실행이 된다.

`PROPAGATION_REQUIRES_NEW` : 첫 번째 트랜잭션이 보류되면서, 두 번째 메소드가 새로운 트랜잭션에서 실행이 된다.

![16](https://user-images.githubusercontent.com/63957819/112120171-5858c280-8c01-11eb-9245-b40f9bd91d78.png)

![17](https://user-images.githubusercontent.com/63957819/112120175-5858c280-8c01-11eb-83c3-c1bd650c8461.png)

- root-context.xml

```xml
..
.
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
**<tx:annotation-driven transaction-manager="transactionManager" />
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
     <property name="dataSource" ref="dataSource"/>
</bean>**
..
.
```

- OrderDAOOracle.java

```java
..
.
	
	@Override
	**@Transactional(rollbackFor = AddException.class) //AddException 발생했을때 롤백하겠다 의미**
	public void insert(OrderInfo info) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			
			//info추가
			insertInfo(session, info);
			
			//info의 lines추가
			insertLines(session, info.getLines());
			
			//session.commit();
		}catch(Exception e) {
			
			//session.rollback();
			throw new AddException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
	
	private void insertInfo(SqlSession session, OrderInfo info)  throws AddException{
		session.insert("mybatis.OrderMapper.insertInfo", info);
	}
	private void insertLines(SqlSession session, List<OrderLine> lines)  throws AddException{
		for(OrderLine line: lines) {
			session.insert("mybatis.OrderMapper.insertLine", line);
		}
	}
..
.
}
```

```bash
SQL> delete from order_line;

26 rows deleted.

SQL> delete from order_info;

15 rows deleted.

SQL> commit;

Commit complete.

SQL> select * from order_info;

no rows selected

SQL> select * from order_line;

no rows selected

SQL> select * from order_info;

  ORDER_NO ORDER_ID   ORDER_DT
---------- ---------- --------
        1 id1        21/03/23

SQL> select * from order_line;

  ORDER_NO ORDER_PROD ORDER_QUANTITY
---------- ---------- --------------
        1 C0001                   1
        1 C0002                   2
        1 C0003                   3

SQL> select * from order_info;

  ORDER_NO ORDER_ID   ORDER_DT
---------- ---------- --------
        1 id1        21/03/23

SQL> select * from order_line;

  ORDER_NO ORDER_PROD ORDER_QUANTITY
---------- ---------- --------------
        1 C0001                   1
        1 C0002                   2
        1 C0003                   3
```

---

- orderMapper.xml

```xml
..
.	
	<insert id="insertLine" parameterType="OrderLine">
INSERT INTO order_line(order_no, order_prod_no, order_quantity **<--오류가 났다고 가정!**
VALUES (order_seq.CURRVAL, #{p.prod_no}, #{order_quantity})
	</insert>
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
	
	@Override
	@Transactional //(rollbackFor = AddException.class) //AddException 발생했을때 롤백하겠다 의미
	public void insert(OrderInfo info) throws AddException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();		
			//info추가
			insertInfo(session, info);
			
			//info의 lines추가
			insertLines(session, info.getLines());
			
			//session.commit();
		//}catch(Exception e) {
			
			//session.rollback();
			//throw new AddException(e.getMessage());
		}finally {
			if(session != null) session.close();
		}
	}
	
	private void insertInfo(SqlSession session, OrderInfo info)  throws AddException{
		session.insert("mybatis.OrderMapper.insertInfo", info);
	}
	private void insertLines(SqlSession session, List<OrderLine> lines)  throws AddException{
		for(OrderLine line: lines) {
			session.insert("mybatis.OrderMapper.insertLine", line);
		}
	}
..
.
}
```

마이바티스 쪽에서 오류가 나면 DataAccessException형태로 가공이 돼서 우리에게 전달이 된다. DataAccessException의 상위 클래스는 RuntimeException이다. uncheckedexception이 전달이 되면 자동 롤백이 된다. try catch로 안 잡히면 메소드로 호출한 곳으로 떠넘겨진다. 예외가 자동 떠넘겨줬는데 그걸 잡아주는 catch가 없으면 RuntimeException은 컴파일러에 의해 감지되지 않는 exception이 발생하여 알아서 자동 롤백이 된다.

실행결과>

![18](https://user-images.githubusercontent.com/63957819/112120181-58f15900-8c01-11eb-8869-fa5fe4cf31c1.png)
