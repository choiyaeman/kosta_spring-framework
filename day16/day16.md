# day16

REST는 네트워크 아키텍처 원리의 모음이다. '네트워크 아키텍처 원리'란 자원을 정의하고 주소를 지정한다. 주소를 지정하는 방법으로 url로 지정하는 방법을 알고 있다. 자원을 정한다는 서버로 자원을 전송해야 하는데 추가를 위한 전송인지 수정을 위한 전송인지 명확하게 목적을 정의해야 한다는 말이다.

RESTful하고 REST하고 같은 용어이다.

<REST 아키텍처에 적용되는 6가지 제한 조건>

1.인터페이스 일관성

2.무상태: 각 요청 간 클라이언트의 콘텍스트가 서버에 저장되어서 는 안 된다. → 정적 페이지로 만들어 놓지 말아라 뜻이다.

3.캐시 처리 가능

4.계층화

5.Code on demand

6.클라이언트/서버 구조

---

![day16%208be749f5e469497c891c978e8f679060/Untitled.png](day16%208be749f5e469497c891c978e8f679060/Untitled.png)

요청 url에서 bags라는 값이 b, 1234라는 값이 pid에 대입. 

b라는 경로 값이 그대로 b에 대입 b는 aaa로 지정 pid라는 경로 값이 pid로 대입. PathVariable어노테이션으로 path값을 지정할 수 있고 원하는 매개변수로 정할 수 있다.

RequestBody어노테이션으로 요청 시에 전달되는 데이터를 json형태로 가정한다. 

 

![day16%208be749f5e469497c891c978e8f679060/Untitled%201.png](day16%208be749f5e469497c891c978e8f679060/Untitled%201.png)

검색, 삭제할 때는 요청 데이터가 필요 없으나 데이터 추가, 수정 할 때에는 요청 데이터를 전송해야 한다.

자바스크립트 단에서 어찌 json형태로 요청해야 하는가? json라이브러리를 이용해서 요청된 전달 데이터를 바꿔주면 된다.

- pom.xml

```xml
<properties>
		<java-version>1.6</java-version>
		<org.springframework-version>5.2.6.RELEASE</org.springframework-version>
		<org.aspectj-version>1.6.10</org.aspectj-version>
		<org.slf4j-version>1.6.6</org.slf4j-version>
	</properties>
..
.
<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.5.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <compilerArgument>-Xlint:all</compilerArgument>
                    <showWarnings>true</showWarnings>
                    <showDeprecation>true</showDeprecation>
                </configuration>
</plugin>
..
.
```

![day16%208be749f5e469497c891c978e8f679060/Untitled%202.png](day16%208be749f5e469497c891c978e8f679060/Untitled%202.png)

pom.xml오른쪽 클릭> Maven> Update Project...

![day16%208be749f5e469497c891c978e8f679060/Untitled%203.png](day16%208be749f5e469497c891c978e8f679060/Untitled%203.png)

Window> Preference> Workspace, Web → UTF-8로 지정

- rest.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>./html/rest.html</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
$(function(){	
	$("form>input[type=button]").click(function(){
		//form객체의 입력 내용을 문자열로 변환
		console.log($("form").serialize());
		
		//form객체의 입력 내용을 배열로 변환
		console.log($("form").serializeArray());
		/* $.ajax({
			url :
			method :
			data : "{\"tno\": 1, \"owner\": \"owner1\", \"grade\": \"1등급\"}",
			
		}); */
	});
});

</script>
</head>
<body>
<form>
티켓번호: <input type="text" name="tno" value="1"><br>
소유주: <input type="text" name="owner" value="owner1"><br>
등급: <input type="text" name="grade" value="1등급"><br>
<input type="button" value="전송">
</form>
</body>
</html>
```

- servlet-context.xml

```xml
..
.
<!-- <resources mapping="/resources/**" location="/resources/" /> -->
	<resources mapping="/html/**" location="/resources/html/" />
..
.
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%204.png](day16%208be749f5e469497c891c978e8f679060/Untitled%204.png)

---

- rest.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>./html/rest.html</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
$(function(){	
	$("form>input[type=button]").click(function(){
		//form객체의 입력 내용을 문자열로 변환[{...}, {...}, {...}]
		console.log($("form").serialize());
		
		//form객체의 입력 내용을 배열로 변환
		//0: {name: "tno", value: "1"}
		//1: {name: "owner", value: "owner1"}
		//2: {name: "grade", value: "1등급"}
		console.log($("form").serializeArray());
		
		var formSerializeArray = $("form").serializeArray();
		var jsonObj = {}; //json용 일반객체
		for(var i=0; i<formSerializeArray.length; i++){
			var obj = formSerializeArray[i];
			jsonObj[obj.name] = obj.value;
		}
		console.log(jsonObj);
		//JSON.parse(); 문자열을 JSON객체로 변환
		var data = JSON.stringify(jsonObj); //일반객체를 JSON객체용 문자열로 변환
		console.log(data);
		/* $.ajax({
			url :
			method :
			data : "{\"tno\": 1, \"owner\": \"owner1\", \"grade\": \"1등급\"}",
			
		}); */
	});
});

</script>
</head>
<body>
<form>
티켓번호: <input type="text" name="tno" value="1"><br>
소유주: <input type="text" name="owner" value="owner1"><br>
등급: <input type="text" name="grade" value="1등급"><br>
<input type="button" value="전송">
</form>
</body>
</html>
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%205.png](day16%208be749f5e469497c891c978e8f679060/Untitled%205.png)

---

![day16%208be749f5e469497c891c978e8f679060/Untitled%206.png](day16%208be749f5e469497c891c978e8f679060/Untitled%206.png)

구글 웹스토어> rest client 검색>Yet Anoter REST Client설치

## <POST>

- HomeController.java

```java
@RestController
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@PostMapping("/write")
	public void post(@RequestBody String data) {
		logger.info(data);
	}
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%207.png](day16%208be749f5e469497c891c978e8f679060/Untitled%207.png)

![day16%208be749f5e469497c891c978e8f679060/Untitled%208.png](day16%208be749f5e469497c891c978e8f679060/Untitled%208.png)

Request Details 소스코드 복사해서 rest.html ajax안에 붙여넣기

- rest.html

```html
$(function(){	
	$("form>input[type=button]").click(function(){
		//form객체의 입력 내용을 문자열로 변환[{...}, {...}, {...}]
		console.log($("form").serialize());
		
		//form객체의 입력 내용을 배열로 변환
		//0: {name: "tno", value: "1"}
		//1: {name: "owner", value: "owner1"}
		//2: {name: "grade", value: "1등급"}
		console.log($("form").serializeArray());
		
		var formSerializeArray = $("form").serializeArray();
		var jsonObj = {}; //json용 일반객체
		for(var i=0; i<formSerializeArray.length; i++){
			var obj = formSerializeArray[i];
			jsonObj[obj.name] = obj.value;
		}
		console.log(jsonObj);
		//JSON.parse(); 문자열을 JSON객체로 변환
		var data = JSON.stringify(jsonObj); //일반객체를 JSON객체용 문자열로 변환
		console.log(data);
		$.ajax({
		   url : '/resttest/write'
		   method : 'POST',
	     data : data,
			
		   "transformRequest": [
		    null
		    ],
		    "transformResponse": [
		     null
		    ],
		    "jsonpCallbackParam": "callback",

		    "headers": {
		    "Accept": "application/json, text/plain, */*",
		    "Content-Type": "application/json;charset=utf-8"
		    },
		});
	});
});
```

- HomeController.java

```java
@RestController
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@PostMapping("/write")
	public void post(@RequestBody String data) {//JSON요청데이터가 String타입으로 전달
		                           //VO     vo   {//JSON요청데이터가 VO타입으로 전달
		logger.info(data);
	}
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%209.png](day16%208be749f5e469497c891c978e8f679060/Untitled%209.png)

---

## <PUT>

- HomeController.java

```java
@PutMapping("/modify/{tno}/{owner}")
	public void put(@PathVariable int tno,
			        @PathVariable String owner,
			        @RequestBody String data) {
		logger.info(tno + ":" + owner + ":" + data);
	}
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2010.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2010.png)

- rest.html

```html
$.ajax(
			{
				  "method": "PUT",
				  "transformRequest": [
				    null
				  ],
				  "transformResponse": [
				    null
				  ],
				  "jsonpCallbackParam": "callback",
				  "url": "/resttest/modify/1/최예만",
				  "headers": {
				    "Accept": "application/json, text/plain, */*",
				    "Content-Type": "application/json;charset=utf-8"
				  },
				  "data": data,
				  "timeout": {}
				}
		);
```

---

## <DELETE>

- HomeController.java

```java
@DeleteMapping("/remove/{tno}")
	public void delete(@PathVariable int tno) {
		logger.info("티켓번호:" + tno);
	}
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2011.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2011.png)

- rest.html

```html
$.ajax({
				  "method": "DELETE",
				  "transformRequest": [
				    null
				  ],
				  "transformResponse": [
				    null
				  ],
				  "jsonpCallbackParam": "callback",
				  "url": "/resttest/remove/1",
				  "headers": {
				    "Accept": "application/json, text/plain, */*"
				  },
				  "data": "",
				  "timeout": {}
		});
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2012.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2012.png)

---

## <GET>

- HomeController.java

```java
@GetMapping("/detail/{tno}")
	public void get(@PathVariable int tno) {
		logger.info("티켓번호:" + tno);
	}
	
	@GetMapping( value = {"/list", "/list/{word}"}) //여려 형태가 올 수 있음 -> 배열 형태로 설정
	//public void get1(@PathVariable String word) { --> 500error default값이 없다 Optional지정 해줘야 함. JDK1.8버전부터 지원
		public void get1(@PathVariable("word") Optional<String>optWord) {
		String word = null;
		if(optWord.isPresent()) { //optWord가 null이 아닌 경우
			word = optWord.get(); //내용을 word변수에 대입
		}
		logger.info("전체검색 또는 검색어로 검색");
		logger.info("전체검색 또는 검색어로 검색 : word=" + word);
	}
```

`Optional` 객체는 타입 제네릭으로 설정해 놓은 값이 null값인지 아닌지 비교 해주는 자료형이다.

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2013.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2013.png)

![day16%208be749f5e469497c891c978e8f679060/Untitled%2014.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2014.png)

![day16%208be749f5e469497c891c978e8f679060/Untitled%2015.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2015.png)

- rest.html

```html
$.ajax({
			"method": "GET",
			  "transformRequest": [
			    null
			  ],
			  "transformResponse": [
			    null
			  ],
			  "jsonpCallbackParam": "callback",
			  "url": "/resttest/list/",
			  "headers": {
			    "Accept": "application/json, text/plain, */*"
			  },
			  "timeout": {}
		});
```

실행결과>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2016.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2016.png)

---

실습>

![day16%208be749f5e469497c891c978e8f679060/Untitled%2017.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2017.png)

![day16%208be749f5e469497c891c978e8f679060/Untitled%2018.png](day16%208be749f5e469497c891c978e8f679060/Untitled%2018.png)

먼저 back의 controll부터 만들고 완성이 되면 확장 프로그램 이용해서 테스트 해보고 front쪽 바꾸는 거다.

- BoardController.java

```java
..
.
@CrossOrigin("*")
//@Controller
//@RestController("/board/*")
@RestController
@Log4j
public class BoardController {
	@Autowired
	private RepBoardService service;
	
	@PostMapping("/board/write")
	public ResponseEntity<String> write(@RequestBody RepBoard board) {
		try {
//			//2.비지니스로직 호출
			service.writeBoard(board);	
			ResponseEntity<String> entity = 
					new ResponseEntity(HttpStatus.OK);
			return entity;
		}catch(AddException e) {
			ResponseEntity<String> entity = 
					new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			return entity;
		}
	}
	
	@RequestMapping("/board/reply")
	public Map<String, Object> reply(@RequestBody RepBoard board) throws Exception {
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
	
	@DeleteMapping("/board/{board_no}/{certify_board_pwd}")
	public Map<String, Object> remove(@PathVariable int board_no, @PathVariable String certify_board_pwd) throws Exception {		
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
	@PutMapping("/board/{board_no}/{certify_board_pwd}")
	public Map<String, Object> modify(
			@RequestBody RepBoard board,
			@PathVariable int board_no,
			@PathVariable String certify_board_pwd) {
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
	
	@GetMapping(value={"/board/list", "/board/list/{word}"})
	@ResponseBody
	public Map<String, Object> list(@PathVariable(name = "word")  Optional<String>optWord)	throws Exception{
		String word= null;
	    if (optWord.isPresent()) {
	    	word = optWord.get();   
	    }
		log.info("검색어:" + word);
		List<RepBoard> list = null;
		Map<String, Object>map = new HashMap<>();
		try {
			if(word == null) {
				list = service.findAll();
			}else {
				list = service.findByBoard_titleORBoard_writer(word);
			}
			map.put("list", list);
			map.put("status", 1);
		}catch (FindException e) {
			log.info(e.getMessage());
			map.put("status", -1);
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	@GetMapping("/board/{board_no}")
	public Map<String, Object> detail(@PathVariable int board_no) 
			throws Exception{
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
		return map;
	}
}
```

- Index.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메인(index.html)</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
//let backContextPath = "/boardback";
//let backContextPath = "/springmvc";
//let backContextPath = "/boardbackspring";
let backContextPath = "/boardbackrestspring";
let boardPath = "/board";
//let frontContextPath = "/boardfrontController";
//let frontContextPath = "/boardfrontspring";
let frontContextPath = "/boardfrontrestspring";
//form을 json으로 변환해주는 함수 :  	사용법- $("#form").serializeObject()
jQuery.fn.serializeObject = function() {
    var obj = null;
    try {
        if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
            var arr = this.serializeArray();
            if (arr) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }//if ( arr ) {
        }
    } catch (e) {
        alert(e.message);
    } finally {
}
 
    return obj;
};
$(function(){	
	
	//var backContextPath = "/boardbackController";
	$("header").load(frontContextPath + '/header.html');
	$("footer").load(frontContextPath + '/footer.html');
	
	//--메뉴 클릭 시작--
	$("header").on("click","ul>li>a",function(event){
		var menu = $(event.target).attr("class");
		//alert("메뉴:" + $(event.target).attr("class"));
		var $sectionObj = $("section");
		switch(menu){
		case 'list'://게시판
			$sectionObj.load(frontContextPath + "/list.html");
			break;
		case 'write'://글쓰기
			$sectionObj.load(frontContextPath + "/write.html");
			break;
		}
		return false;
	});
	//--메뉴 클릭 끝--

});
</script>
</head>
<body>
<header>
</header>

<section>
<h1>답변형 게시판 MVC실습</h1>
</section>

<footer>
</footer>
</body>
</html>
```

- list.html

```html
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 --><script>
function showList(responseObj){
	console.log(responseObj);
	//테이블객체
	var $tableObj =$("section>div.list>table");
	
	$("section>div.list>table tr.copy").remove();
	
	//원본행객체
	var $trOriginObj = $("section>div.list>table tr.origin");
	$trOriginObj.show(); //원본행객체 보여주기
	if(responseObj.status == -1){
		alert(responseObj.msg);
		return false;
	}else{
		$(responseObj.list).each(function(index, element){
			var $trCopyObj = $trOriginObj.clone(); //원본행객체의 복제본만들기
			$trCopyObj.addClass("copy"); //복제본행의 클래스속성으로 copy설정
			$trCopyObj.removeClass("origin"); //복제본행의 클래스속성중 origin제거
			
			//복제본행의 내용 채우기 : 게시글번호
			$trCopyObj.find("td.board_no").html(element.board_no);
			
			var str = '';
			for(var i=1; i<element.level;i++){
				str +='&#10149;';
			}
			//복제본행의 내용 채우기 : 게시글제목
			$trCopyObj.find("td.board_title").html(str+element.board_title);
			
			//복제본행의 내용 채우기 : 게시글작성자
			$trCopyObj.find("td.board_writer").html(element.board_writer);
			
			//복제본행의 내용 채우기 : 작성일자
			$trCopyObj.find("td.board_dt").html(element.board_dt);
			
			//복제본행의 내용 채우기 : 조회수
			$trCopyObj.find("td.board_cnt").html(element.board_cnt);
			
			//테이블객체의 자식객체로 복제본행을 추가
			$tableObj.append($trCopyObj);
		}); //each
		
		$trOriginObj.hide();
	}
}
$(function(){
	alert("list요청 url:"+ backContextPath + boardPath +"/list");
	 $.ajax({
		url: backContextPath + boardPath + "/list",
		method: 'get',
		success: function(responseObj){
			alert(responseObj);
			showList(responseObj);
		}
	});//$ajax */
	
	//--게시물 클릭 시작--
	$("section>div.list>table").on("click", "tr>td", function(event){
		var board_no = $(event.target).parent().children("td.board_no").html().trim();
		
		$("section").load("detail.html", function(){
			$.ajax({
				//url: backContextPath+"/detail",
				url: backContextPath + boardPath + "/"+ board_no,
				method: "get",
				//data: "board_no=" + board_no,
				success:function(responseObj){
					var board = responseObj.board;
					if(responseObj.status == 1){
						$("div.detail>span.board_no").html(board.board_no);
						$("div.detail>span.board_title").html(board.board_title);
						$("div.detail>span.board_writer").html(board.board_writer);
						$("div.detail>span.board_dt").html(board.board_dt);
						$("div.detail>span.board_cnt").html(board.board_cnt);
					}else{
						alert(responseObj.msg);
					}
				},
				error:function(jqXHR){
					alert("에러:" + jqXHR.status);
				}
			}); 
		});
		return false;
	});
	//--게시물 클릭 끝--
	
	//--검색버튼 클릭 시작--
	$("section>div.list>form>input[type=button]").click(function(){
		 $.ajax({
			 	url: backContextPath+boardPath+"/list/" + 
			 	           $("section>div.list>form>input[name=word]").val(),
				//url: backContextPath+"/list",
				method: 'get',
				//data: 'word=' + $("section>div.list>form>input[name=word]").val(),
				success: function(responseObj){
					showList(responseObj);
				}
		});
		return false;
	});
	//--검색버트 클릭 끝--
});
</script>
<style>
*{
  box-sizing: border-box;
}
section>div.list>table{
  border: 1px solid; border-collapse: collapse; width:50%;
}
section>div.list>table tr>td{
  border: 1px solid;
}
section>div.list>table tr>td.board_no{ width: 10%; text-align: right; }
section>div.list>table tr>td.board_title{ width: 40%; }
section>div.list>table tr>td.board_writer{ width: 20%; }
section>div.list>table tr>td.board_dt{ width: 20%; }
section>div.list>table tr>td.board_cnt{ width: 10%; text-align: right; }
</style>
</head>
<div class="list">

<form>
  <input type="hidden" name="board_no">
  <input type="search" name="word"><input type="button" value="검색">
</form>
<table>

 <tbody>
 <tr class="origin">
   <td class="board_no"></td>
   <td class="board_title"></td>
   <td class="board_writer"></td>
   <td class="board_dt"></td>
   <td class="board_cnt"></td>
 </tr>
</tbody></table>
</div>
```

- detail.html

```html
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 -->
 <script>
$(function(){
	//--답글쓰기링크 클릭 시작--
	$("section>div.detail>a.reply").click(function(){
		$("section>div.reply").show();
		$("section>div.remove").hide();
		$("section>div.modify").hide();
		return false;
	});
	//--답글쓰기링크 클릭 끝--
	
	//--수정링크 클릭 시작--
	$("section>div.detail>a.modify").click(function(){
		
		$("section>div.reply").hide();
		$("section>div.remove").hide();
		
		$("section>div.modify>form>input[name=board_no]").val($("div.detail>span.board_no").html());
		$("section>div.modify>form>input[name=board_title]").val($("div.detail>span.board_title").html());
		$("section>div.modify").show(); //수정하기 창띄우기
		return false;
	});
	//--수정링크 클릭 끝--
	
	//--삭제링크 클릭 시작--
	$("section>div.detail>a.remove").click(function(){
		$("section>div.reply").hide();
		$("section>div.modify").hide(); 
		
		$("section>div.remove>form>input[name=board_no]").val($("div.detail>span.board_no").html());
		$("section>div.remove").show(); //삭제하기 창띄우기
		return false;
	});
	//--삭제링크 클릭 끝--
	
	//--답글쓰기 창에서 답글쓰기 클릭 시작--
	var $replyFormObj = $("section>div.reply>form");
	$replyFormObj.submit(function(){
		var parent_no = $("section>div.detail>span.board_no").html();
		$replyFormObj.find("input[name=parent_no]").val(parent_no);
		var url = backContextPath + boardPath +  $replyFormObj.attr("action");
		
		var formSerializeArray = $replyFormObj.serializeArray();
		var object = {};
		for (var i = 0; i < formSerializeArray.length; i++){
		    object[formSerializeArray[i]['name']] = formSerializeArray[i]['value'];
		}		 
		var data = JSON.stringify(object);
		
		$.ajax({
			"method": "POST",
		    "transformRequest": [
		      null
		    ],
		    "transformResponse": [
		      null
		    ],
		    "jsonpCallbackParam": "callback",
		    "url": url,
		    "headers": {
		      "Accept": "application/json, text/plain, */*",
		      "Content-Type": "application/json;charset=utf-8"
		    },
		    "data": data,
			success:function(responseObj){
				if(responseObj.status == 1){
					$("header>ul>li>a.list").trigger("click");
				}else{
					alert(responseObj.msg);
				}
			},
			error:function(jqXHR){
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
	//--답글쓰기 창에서 답글쓰기 클릭 끝--
	
	//--수정하기 창에서 수정 클릭 시작--
	var $modifyFormObj = $("section>div.modify>form");
	$modifyFormObj.submit(function(){
		var board_no = $modifyFormObj.find("input[name=board_no]").val();
		var certify_board_pwd = $modifyFormObj.find("input[name=certify_board_pwd]").val();
		var url = backContextPath + boardPath + "/" + board_no + "/" + certify_board_pwd;
		
		var formSerializeArray = $modifyFormObj.serializeArray();
		var object = {};
		for (var i = 0; i < formSerializeArray.length; i++){
		    object[formSerializeArray[i]['name']] = formSerializeArray[i]['value'];
		}		 
		var data = JSON.stringify(object);
		
		$.ajax({
		    "method": "PUT",
		    "transformRequest": [
		      null
		    ],
		    "transformResponse": [
		      null
		    ],
		    "jsonpCallbackParam": "callback",
		    "url": url,
		    "headers": {
		      "Accept": "application/json, text/plain, */*",
		      "Content-Type": "application/json;charset=utf-8"
		    },
		    "data": data,
			success:function(responseObj){
				if(responseObj.status == 1){
					$("header>ul>li>a.list").trigger("click");
				}else{
					alert(responseObj.msg);
				}
			},
			error:function(jqXHR){
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
	//--수정하기 창에서 수정 클릭 끝--
	
	//--삭제하기 창에서 삭제 클릭 시작--
	var $removeFormObj = $("section>div.remove>form");
	$removeFormObj.submit(function(){
		var url = backContextPath  + boardPath +  "/" + $removeFormObj.find("input[name=board_no]").val() + "/" + $removeFormObj.find("input[name=certify_board_pwd]").val();
		$.ajax({
			"method": "DELETE",
			"transformRequest": [
			  null
			],
			"transformResponse": [
			  null
			],
			"jsonpCallbackParam": "callback",
			"url": url,
			"headers": {
			  "Accept": "application/json, text/plain, */*"
			},
			success:function(responseObj){
				if(responseObj.status == 1){
					$("header>ul>li>a.list").trigger("click");
				}else{
					alert(responseObj.msg);
				}
			},
			error:function(jqXHR){
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
	//--삭제하기 창에서 삭제 클릭 끝--
});
</script>
<style>
*{
  box-sizing: border-box;
}

</style>

<div class="detail">
글번호 :<span class="board_no"></span><br>
제목 : <span class="board_title"></span><br>
작성자 :<span class="board_writer"></span><br>
작성일자 :<span class="board_dt"></span><br>
조회수:<span class="board_cnt"></span><br>
<hr>
<a href="#" class="reply">답글쓰기</a>&nbsp;&nbsp;
<a href="#" class="modify">수정</a>&nbsp;&nbsp;
<a href="#" class="remove">삭제</a>
</div>

<div class="reply" style="display:none;">
  <form method="post" action="/reply">
  <input type="hidden" name="parent_no"> 
      답글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="답글쓰기"> 
  </form>
</div>

<div class="modify" style="display:none;">
 <!-- <form method="post" action="/modify"> -->
 <form>
    글번호  <input type="text" name="board_no"  readonly><br>
    제목 : <input type="text" name="board_title"><br>
    작성자 :<span class="board_writer"></span><br>
    작성일자 :<span class="board_dt"></span><br>
    조회수: <span class="board_cnt"></span><br>
    기존 비밀번호 : <input type="password" name="certify_board_pwd" required><br>
    변경할 비밀번호:<input type="password" name="board_pwd" required><br>
  <input type="submit" value="수정"> 
  </form>
</div>
<div class="remove" style="display:none;">
  <form method="post" action="/remove">
   <input type="hidden" name="board_no" >
      기존 비밀번호 : <input type="password" name="certify_board_pwd" required><br>
   <input type="submit"  value="삭제">
  </form>
</div>
```

- write.html

```html
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 -->
<script>
$(function(){
	var $writeFormObj = $("section>div.write>form");
	$writeFormObj.submit(function(){
		var formSerializeArray = $writeFormObj.serializeArray();
		var object = {};
		for (var i = 0; i < formSerializeArray.length; i++){
		    object[formSerializeArray[i]['name']] = formSerializeArray[i]['value'];
		}		 
		var data = JSON.stringify(object);
		
		$.ajax({
			"method": "POST",
		    "transformRequest": [
		      null
		    ],
		    "transformResponse": [
		      null
		    ],
		    "jsonpCallbackParam": "callback",
		    "url": backContextPath  + boardPath + $writeFormObj.attr("action"),
		    "headers": {
		      "Accept": "application/json, text/plain, */*",
		      "Content-Type": "application/json;charset=utf-8"
		    },
		    "data": data,
			success:function(responseObj){//응답성공이란 응답완료(readyState가 4), 응답코드가 200인 경우를 말함
				alert("성공");
				$("header>ul>li>a.list").trigger("click");
			},
			error:function(jqXHR){//응답실패
				alert("에러:" + jqXHR.status);
			}
		});
		return false;
	});
});
</script>
<style>
*{
  box-sizing: border-box;
}
</style>
<div class="write">
<form method="post" action="/write">
      글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="글쓰기"> 
</form>
</div>
```