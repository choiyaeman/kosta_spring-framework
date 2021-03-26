# day15

- BoardController.java

```java
@Controller
@RequestMapping("/board/*")
@Log4j
public class BoardController {
	@Autowired
	private RepBoardService service;

	@RequestMapping("/list")
//	public ModelAndView list(String word) throws FindException{
	public ModelAndView list(String word, 
			                @RequestParam(value = "currentPage", 
			                              required = false, 
			                              defaultValue = "1") int currentPage) throws FindException{	
	log.info("검색어:" + word);
		List<RepBoard> list;

		ModelAndView mnv = new ModelAndView();

		//2. 비지니스로직 호출
		if(word == null) { //전체검색
			//list = service.findAll();
			int cnt_per_page = 10;
			list = service.findAll(currentPage, cnt_per_page);
		}else { //검색어에 만족하는 검색
			list = service.findByBoard_titleORBoard_writer(word);
		}
		mnv.addObject("list", list);
		//mnv.setViewName("list");

		return mnv;
	}
```

name하고 value는 같은 놈이다. `required`를 false로 설정하면 요청 전달 데이터로 전달이 안되면 기본 값으로 알아서 바인딩 하겠다라는 뜻이다. `defaultValue`는 필수 파라미터로 전달되지 않을 경우 자동 바인딩 할 기본 값을 뜻한다.

- RepBoardService.java

```java
public List<RepBoard> findAll(int currentPage, int cnt_per_page) throws FindException{
		return boardDAO.selectAll(currentPage, cnt_per_page);
	}
```

- RepBoardDAO.java

```java
public List<RepBoard> selectAll(int currentPage, int cnt_per_page) throws FindException;
```

- RepBoardDAOOracle.java

```java
@Override
	public List<RepBoard> selectAll(int currentPage, int cnt_per_page) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String, Integer> map = new HashMap<>();
			map.put("currentPage", currentPage);
			map.put("cnt_per_page", cnt_per_page);
			List<RepBoard> list = session.selectList(
					                      "mybatis.RepBoardMapper.selectAllPerPage",
					                      map);
			if(list.size() == 0) {
				throw new FindException("게시글이 없습니다");
			}
			return list;
		} catch(Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session !=null ) session.close();
		}
	}
```

- boardMapper.xml

```java
<select id="selectAllPerPage" parameterType="map" resultType="RepBoard">
SELECT * 
FROM (
  SELECT rownum r, level, repboard.*
	FROM repboard 
	START WITH parent_no = 0
	CONNECT BY PRIOR board_no = parent_no
	ORDER SIBLINGS BY board_no DESC
)
WHERE r BETWEEN  FUN_START_ROW(#{currentPage}, #{cnt_per_page}) 
             AND FUN_END_ROW(#{currentPage}, #{cnt_per_page})
  </select>
```

한 페이지 당 몇 건씩 보여줘야 할지 앞 단에 줘야지 mapper쪽에서 파라미터로 전달이 돼야 페이지 당 보여줄 목록 수에 따르는 해당 페이지만 검색 해올 수 있는 거다. 가장 유연한 위치는 컨트롤러이다. 아니면 서비스 단에서 해도 된다. 그러나 DAO단에서 하는 것은 유연하지 않은 방법이다. 컨트롤러가 결정자가 되도록 해보자

실행결과>

![1](https://user-images.githubusercontent.com/63957819/112599420-41111380-8e53-11eb-8712-4c5fce58a15c.png)

---

- Mapper.xml

```java
<select id="selectCount" resultType="int">
  SELECT COUNT(*) FROM repboard
  </select>
```

- RepBoardDAOOracle.java

```java
public int selectCount() throws FindException{
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			int count = session.selectOne("mybatis.RepBoardMapper.selectCount");
			return count;
		}catch(Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session !=null ) session.close();
		}
	}
```

- RepBoardDAO.java

```java
public int selectCount() throws FindException;
```

- RepBoardService.java

```java
public int findCount() throws FindException{
		return boardDAO.selectCount();
	}
```

- BoardController.java

```java
public ModelAndView list(String word, 
			                @RequestParam(value = "currentPage", 
			                              required = false, 
			                              defaultValue = "1") int currentPage) throws FindException{	
	log.info("검색어:" + word);
		List<RepBoard> list;

		ModelAndView mnv = new ModelAndView();

		//2. 비지니스로직 호출
		if(word == null) { //전체검색
			//list = service.findAll();
			int cnt_per_page = 10;
			list = service.findAll(currentPage, cnt_per_page);//게시물 목록얻기
			int totalCnt = service.findCount(); //게시물전체수
			
			//총페이지수 계산하기
			int totalPage = (int)Math.ceil((double)totalCnt/cnt_per_page);
			log.info("총게시물수:" + totalCnt + "총페이지수:" + totalPage);
		}else { //검색어에 만족하는 검색
			list = service.findByBoard_titleORBoard_writer(word);
		}
		mnv.addObject("list", list);
		//mnv.setViewName("list");

		return mnv;
	}
```

실행결과>

![2](https://user-images.githubusercontent.com/63957819/112599425-42424080-8e53-11eb-889f-ed4c19da06b1.png)

---

- BoardController.java

```java
//총페이지수 계산하기
			int totalPage = (int)Math.ceil((double)totalCnt/cnt_per_page);
			log.info("총게시물수:" + totalCnt + "총페이지수:" + totalPage);
			mnv.addObject("totalPage", totalPage);
```

view에서 쓰일 Model을 추가 시켜줘야 한다. 총 페이지 수도 추가하자 

- list.jsp

```java
<%--페이지 목록 1,2,3... --%>
<c:forEach begin="1" end="${requestScope.totalPage}" step="1" var="i">
  [<span>${i}</span>]&nbsp;&nbsp;&nbsp;
</c:forEach>
```

실행결과>

![3](https://user-images.githubusercontent.com/63957819/112599430-42424080-8e53-11eb-9f21-5f65b42e0b07.png)

---

```java
ex) 최대 페이지 5일 경우
현재 보고 있는 페이지가 1페이지면 목록은 1, 2, 3
현재 보고 있는 페이지가 2페이지면 목록은 1, 2, 3
현재 보고 있는 페이지가 3페이지면 목록은 1, 2, 3
현재 보고 있는 페이지가 4페이지면 목록은 4, 5
현재 보고 있는 페이지가 5페이지면 목록은 4, 5
```

최종 페이지가 5페이지이므로 6페이지는 보이면 안된다. 페이지 목록을 그룹으로 조절 해야 한다. 시작 페이지와 끝 페이지 계산이 되어야 한다. 컨트롤러 단에서 계산 로직이 필요하다. Jsp에게 맡겨도 되지만 권장하지 않는다.

- BoardController.java

```java
//총페이지수 계산하기
			int totalPage = (int)Math.ceil((double)totalCnt/cnt_per_page);
			log.info("총게시물수:" + totalCnt + "총페이지수:" + totalPage);
			mnv.addObject("totalPage", totalPage);
			
			int cnt_per_page_group = 3; //페이지 목록수
			int startPage = ((currentPage-1)/cnt_per_page_group)*cnt_per_page_group+1;
			int endPage = startPage+cnt_per_page_group-1;
			if(totalPage<endPage) {
				endPage = totalPage;
			}
			mnv.addObject("startPage", startPage);
			mnv.addObject("endPage", endPage);
```

- list.jsp

```java
<%--페이지 목록 1,2,3... --%>
<c:if test="${requestScope.startPage > 1}">
[<span>&#9664;</span>] <%--prev --%>
</c:if>
<c:forEach begin="${requestScope.startPage}" end="${requestScope.endPage}" step="1" var="i">
  [<span>${i}</span>]&nbsp;&nbsp;&nbsp;
</c:forEach>
<c:if test="${requestScope.endPage < requestScope.totalPage }">
[<span>&#9654;</span>] <%--next --%>
</c:if>
```

[https://unicode-table.com/kr/](https://unicode-table.com/kr/) 참고

실행결과>

![4](https://user-images.githubusercontent.com/63957819/112599431-42dad700-8e53-11eb-8e30-c3c4d10ea859.png)

![5](https://user-images.githubusercontent.com/63957819/112599433-42dad700-8e53-11eb-997b-e53f49e0e3c8.png)

---

- list.jsp

```jsx
<%--페이지목록 1,2,3... --%>
<ul style="list-style-type: none; padding: 0px">
<c:if test="${requestScope.startPage > 1}">
  <li style="display: inline-block;"><span class="${requestScope.startPage-1}">&#9754;</span>&nbsp;&nbsp;&nbsp;</li> <%--prev --%>
</c:if>
<c:forEach begin="${requestScope.startPage}" 
           end="${requestScope.endPage}" 
           step="1" 
           var="i">
  <li style="display: inline-block;">
    <c:choose>
    <c:when test="${i == param.currentPage}"> <%--페이지목록값이 현재페이지인 경우 --%>
       <span class="${i}" style="font-weight: bold;color: red;">
       ${i}
       </span>
    </c:when>
    <c:otherwise>
    <span class="${i}">${i}</span>
    </c:otherwise>
    </c:choose>
    &nbsp;&nbsp;&nbsp;
  </li>
</c:forEach>
<c:if test="${requestScope.endPage < requestScope.totalPage }">
  <li style="display: inline-block;"><span class="${requestScope.endPage+1}">&#9654;</span></li><%--next --%>
</c:if>
</ul>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
<script>
$("ul>li>span").click(function(event){
	var classValue = $(event.target).attr("class");
	location.href="http://localhost:8888/mvcspring/board/list?currentPage="+ classValue;
});
</script>
```

```jsx
location.href="/mvcspring/board/list?currentPage="+ classValue; //상대경로
```

경로를 클라이언트에 맞게 주면 된다. 상대 경로를 준다면 /mvcspring/board/list?currentPage="+ classValue; 하면 된다. 앞에 /붙으면 path부터이고 /없으면 현재 경로부터 이다. 상대 경로로 하겠다 하면 /를 붙이면 된다. 상대 경로를 주는 걸 권장한다.

실행결과>

![6](https://user-images.githubusercontent.com/63957819/112599435-43736d80-8e53-11eb-8619-f3e48d7dc4b4.png)

---

상품 목록, 게시판 목록, 주문 목록을 볼 때 페이징을 하고 싶다면 윗 부분에 페이지에 해당하는 내용, 아래 쪽에 페이지 그룹핑이 나와야 한다. 공통 사항들을 추출을 해보자. 서로 목록 내용은 다를 거다. 하지만 어쨌든 목록이 나와야 하고 totalCnt, cnt_per_page, cnt_per_page_group, startPage, endPage, currentPage, totalPage정보가 공통적으로 있다고 하자. 이동할 url도 미리 결정하면 좋다.

 List용도가 있어야 하는데 상품, 게시판, 주문 여러 개로 코딩량이 많아져서 자료형은 결정 하는 게 아니라 그때마다 바뀔 수 있도록 타입 제네릭을 써야 한다. 타입제네릭은 PageGroupBean에 전달해주면 된다.

이 자료가 vo의 성격보다는 dto의 성격이 좀 크다.  컨트롤러 쪽에서는 복잡한 계산이 들어가면 안된다. 따로 컴포넌트화해서 만들어야 한다. 

- PageGroupBean.java

```java
..
.
@Log4j
public class PageGroupBean<T> {
	private int cnt_per_page = 10; //페이지별 보여줄 목록수 *
	private int cnt_per_page_group = 3; //페이지 그룹에 보여줄 페이지수 *
	private int totalCnt; //총 목록 수 *
	private int startPage; //시작 페이지 *
	private int endPage; //끝 페이지 *
	private int currentPage; //현재 페이지 *
	private int totalPage; //총 페이지 수 *
	private String targetURL; //페이지 클릭 시 이동할 URL명 *
	private List<T> list; //목록 ex)게시판: List<RepBoard>, 상품목록: List<Product>, 주문목록: List<OrderInfo> *
	
	public PageGroupBean() {
		super();
	}
	
	public PageGroupBean(int totalCnt, int currentPage, List<T> list, String targetURL) {
		this(totalCnt, currentPage, list, targetURL, 10, 3); //this생성자를 이용해서 핵심역할하는 세번째 생성자로 호출이 될수있게 구성
	}
	public PageGroupBean(int totalCnt, 
			             int currentPage, 
			             List<T> list,
			             String targetURL,
			             int cnt_per_page, 
			             int cnt_per_page_group) {
		this.totalCnt = totalCnt;
		this.currentPage = currentPage;
		this.list = list;
		this.cnt_per_page = cnt_per_page;
		this.cnt_per_page_group = cnt_per_page_group;
		this.targetURL = targetURL;
		
		this.totalPage = (int)Math.ceil((double)totalCnt/cnt_per_page); //총페이지수 계산이 가능
		log.info("총게시물수:" + totalCnt + "총페이지수:" + totalPage);
		
		startPage = ((currentPage-1)/cnt_per_page_group)*cnt_per_page_group+1;
		endPage = startPage+cnt_per_page_group-1;
		if(totalPage<endPage) {
			endPage = totalPage;
		}
	}
..
.	
}
```

공통적인 것들을 멤버 변수로 선언하자

- BoardController.java

```java
@RequestMapping("/list")
//	public ModelAndView list(String word) throws FindException{
	public ModelAndView list(String word, 
			                @RequestParam(value = "currentPage", 
			                              required = false, 
			                              defaultValue = "1") int currentPage) throws FindException{	
	log.info("검색어:" + word);
		List<RepBoard> list;

		ModelAndView mnv = new ModelAndView();

		//2. 비지니스로직 호출
		if(word == null) { //전체검색
			//list = service.findAll();
			int cnt_per_page = 10;
			list = service.findAll(currentPage, cnt_per_page);//게시물 목록얻기
			int totalCnt = service.findCount(); //게시물전체수
			
//			//총페이지수 계산하기
//			int totalPage = (int)Math.ceil((double)totalCnt/cnt_per_page);
//			log.info("총게시물수:" + totalCnt + "총페이지수:" + totalPage);
//			mnv.addObject("totalPage", totalPage);
//			
//			int cnt_per_page_group = 3; //페이지 목록수
//			int startPage = ((currentPage-1)/cnt_per_page_group)*cnt_per_page_group+1;
//			int endPage = startPage+cnt_per_page_group-1;
//			if(totalPage<endPage) {
//				endPage = totalPage;
//			}
//			mnv.addObject("startPage", startPage);
//			mnv.addObject("endPage", endPage);
			String targetURL = "/board/list";
			PageGroupBean<RepBoard> pgb = 
					new PageGroupBean<>(totalCnt, currentPage, list, targetURL); //변수선언할때 목록의 자료형을 적어주면 된다. 상품이면 <Product>, 주문이면 <OrderInfo>..
			mnv.addObject("pageGroupBean", pgb);
		}else { //검색어에 만족하는 검색
			list = service.findByBoard_titleORBoard_writer(word);
			PageGroupBean<RepBoard> pgb = new PageGroupBean<>();
			pgb.setList(list);
			mnv.addObject("pageGroupBean", pgb);
		}
		//mnv.addObject("list", list);
		//mnv.setViewName("list");

		return mnv;
	}
```

- list.jsp

```java
..
.
<table>
<c:forEach items="${requestScope.pageGroupBean.list}" var="board">
 <tr>
   <td class="board_no">${board.board_no}</td>
   <td class="board_title">
      <c:forEach begin="2" end="${board.level}" step="1">&#10149;
      </c:forEach>${board.board_title}
   </td>
   <td class="board_writer">${board.board_writer}</td>
   <td class="board_dt">
   <fmt:formatDate value="${board.board_dt}" pattern="yyyy-MM-dd"/> </td>
   <td class="board_cnt">${board.board_cnt}</td>
 </tr>
</c:forEach>
</table>

<%--페이지목록 1,2,3... --%>
<ul style="list-style-type: none; padding: 0px">
<c:if test="${requestScope.pageGroupBean.startPage > 1}">
  <li style="display: inline-block;"><span class="${requestScope.pageGroupBean.startPage-1}">&#9754;</span>&nbsp;&nbsp;&nbsp;</li> <%--prev --%>
</c:if>
<c:forEach begin="${requestScope.pageGroupBean.startPage}" 
           end="${requestScope.pageGroupBean.endPage}" 
           step="1" 
           var="i">
  <li style="display: inline-block;">
    <c:choose>
    <c:when test="${i == requestScope.pageGroupBean.currentPage}"> <%--페이지목록값이 현재페이지인 경우 --%>
       <span class="${i}" style="font-weight: bold;color: red;">
       ${i}
       </span>
    </c:when>
    <c:otherwise>
    <span class="${i}">${i}</span>
    </c:otherwise>
    </c:choose>
    &nbsp;&nbsp;&nbsp;
  </li>
</c:forEach>
<c:if test="${requestScope.pageGroupBean.endPage < requestScope.pageGroupBean.totalPage }">
  <li style="display: inline-block;"><span class="${requestScope.pageGroupBean.endPage+1}">&#9654;</span></li><%--next --%>
</c:if>
</ul>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
<script>
$("ul>li>span").click(function(event){
	var classValue = $(event.target).attr("class");
	location.href="/mvcspring${requestScope.pageGroupBean.targetURL}?currentPage="+ classValue;
});
</script>
</body>
</html>
```

---

![7](https://user-images.githubusercontent.com/63957819/112599437-43736d80-8e53-11eb-960e-a6420aac1b96.png)

모바일 애플리케이션에서도 백엔드를 요청해서 결과를 앱으로 가져갈 수 있다.

웹 브라우저 같은 경우 안에는 렌더링 엔진이 포함되어있어서 html 응답을 받게 되면 렌더링 엔진에 의해 해석을 해서 화면에 예쁘게 보여지게 된다. 근데 앱 자체가 html 응답 받았다고 예상 해보면 받는 것까지는 아무 문제가 없다. 그 응답 내용이 html 태그인데 img같은 태그를 응답 받았다고 하면 앱이 해석해야 하는데 렌더링 엔진이 없으므로 앱 안 에다가 렌더링 엔진을 끼어 넣던가 html 태그로 응답 받는 것을 포기 해야 한다.

back의 형태는 html 응답이 되면 제약 사항이 너무 많다. 웹 브라우저로만 요청을 해야 한다. 다양한 형태로 요청을 만들 서버 페이지를 만들어야 한다면 json형태 또는 xml형태로 응답을 해줘야 한다.

REST쪽에서 규약을 걸어서 URL만 봐서는 요청 목적이 명확하지 않아 요청 방식을 결정을 해 놨다.

`POST` : 추가한다는 의미, `GET` : 조회하겠다, `PUT/PATCH` : 수정하겠다, `DELETE` :삭제하겠다

쿼리 스트링은 225문자까지 밖에 쓸 수 없다 근데 쿼리 스트링이 없어지면 많은 문자를 보낼 수 있게 된다. 실제 사용할 resource는 list이고 /다음에 나오는 값은 currentPage로 인식하고 그 다음은 word라 인식하게 컨트롤러에서 정의만 잘 해두면 된다. REST의 궁극적인 목표는 url을 줄이고 경로로 표현하자 의미이다. url의 구조를 이용하여 데이터를 전달하는 방법을 취하고 있다.
