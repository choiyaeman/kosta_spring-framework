<%@page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>시멘틱태그-CSS-jQuery[JSP]</title>
        <style>
            * { 
                box-sizing: border-box;

                color : #000000; 
                /* font-size: 1.25em; */
            }
            header { background: #f6f5ef; 
                     margin: 5px auto;
                     position: relative;
            } 
            /*section{background-color:#eef2f3; height:500px; margin-bottom: 5px;}
            section>article.one{background-color: #b3ffd9}
            section>article.two{background-color: #1e3932;color:white;}*/
            footer{ background-color: #2C2A29;  color:#fff;  }
            header, footer { height:100px; /* width: 1100px; */ width:100%; }
            header>nav>ul{ list-style-type: none; padding: 0px;}
            header>nav>ul>li{ 
             width:100px; 
             display: inline-block; 
             margin: 0px 10px; 
             text-align: center;
            }

            header>nav>ul>li>a{
                text-decoration: none;
            }
            header>nav>ul>li>a:hover{
                background-color: black;
                color: white;
                font-weight: bold;
            }
            
            header>h1{
                width: 30%;    
                margin: 0 auto;
                height: 100%;
                display: inline-block;
                position: relative;
                border: 1px solid;
            }
            /* 이미지 로고 */
            header>h1>a{
                display: block;
                width : 100%;
                height: 100%;
                margin: 0;
                padding: 0;
                
                position: absolute;
                top: 10px;
                left: 0px;
                
                background-image: url('./images/logo.png');
                background-repeat: no-repeat;
            }
            header>nav{
                display: inline-block;
                width : 60%;
                height: 100%;
                border: 1px solid;
                position: absolute;
                top: 0px;
                right: 0px;
            }
            section{
                background-color:#eef2f3; 
                width: 100%;
                height:500px; 
                margin : 5px;
            }
            section>article{
                width: 80%;
                height: 100%;
                float: left;
            }
           
            section>aside{
                width: 20%;
                height:100%;
                float: right;               
            }
            section>aside>div.strawberry, section>aside>div.plcc{
            	width: 100%;
            }
            /* 광고이미지 반응*/
            section>aside .pc-badge{
            	width: 80%;
                display: block;
            }
            section>aside .mobile-badge {
                display: none;
            }
            
            @media screen and (max-width: 960px){
                section>aside .pc-badge {
                    display: none;
                }
               section>aside .mobile-badge {
               		width: 80%;
                   display: block;
                }
            }

            /*메뉴 반응*/
            header>nav.small{
                display:none;
            }
            @media screen and (min-width: 641px) and (max-width: 960px){
                header>nav.small{
                    display:inline-block;
                }
                header>nav.large{
                    display:none;
                }
            }
        </style>
        <style>
        div.viewcart{
            box-sizing: border-box;
            width: 100%;
            height: 300px;
        }
        div.viewcart>table{
            border-collapse: collapse;
        }
        div.viewcart>table tr{
            
        }
        div.viewcart>table, div.viewcart>table th, div.viewcart>table td{
            border: 1px solid;
        }   
        </style>
        <style>
          div.vieworder{
            box-sizing: border-box;
            width: 100%;
            height: 500px;
            overflow: auto;
        }
        div.vieworder>table{
            border-collapse: collapse;
        }
        div.vieworder>table tr{
            
        }
        div.vieworder>table, div.vieworder>table th, div.vieworder>table td{
            border: 1px solid;
        }   
        </style>
<!--jquery사용-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
<script>
let backContextPath = '${pageContext.request.contextPath}';
$(function(){            	
//DOM트리에서 메뉴객체들 모두찾기
var $menuObj = $("header>nav>ul>li>a");
//메뉴가 클릭되면 
$menuObj.click(function(event){
    $("section>article").empty(); //aricle영역 지우기
    //메뉴객체의 href속성값 얻기
    var hrefValue = $(event.target).attr("href");//ex)login.html, signup.html

    alert("메뉴:" + hrefValue+"를 선택했습니다.");
    switch(hrefValue){//메뉴별
        case 'logout': //로그아웃메뉴
        $.ajax({
            url:hrefValue,
            method: 'get',
            success: function(data){//성공응답
                location.href=backContextPath+"/semanticcssjq";
            },
            error: function(jqXHR){//실패응답
                    alert("AJAX요청응답 실패 : 에러코드=" + jqXHR.status);
                } 
        });
        break;
        case 'viewcart': //장바구니보기메뉴
        $.ajax({
            url: hrefValue,
            method: 'get',
            success:function(responseObj){
                if(responseObj.status == undefined){ //장바구니가 있는경우
                    var $tableObj = $("<table>");//객체생성  //$("table") <-선택자이용해서 객체를 DOM에서 찾기
                    var tableData = "<tr><th>상품번호</th><th>상품명</th><th>가격</th><th>수량</th></tr>";
                    var arr = responseObj;
                    $(arr).each(function(index, element){
    //console.log("장바구니 내용 상품번호-" + element.prod_no + ", 상품명-" + element.prod_name+ ", 상품가격-" + element.prod_price + ", 수량-" + element.quantity);
                        tableData += '<tr>';
                        tableData += '<td>';
                        tableData += element.prod_no;
                        tableData += '</td>';
                        
                        tableData += '<td>';
                        tableData += element.prod_name;
                        tableData += '</td>';
                        
                        tableData += '<td>';
                        tableData += element.prod_price;
                        tableData += '</td>';
                        
                        tableData += '<td>';
                        tableData += element.quantity;
                        tableData += '</td>';
                        tableData += '</tr>';
                    });
                    $tableObj.html(tableData);
                    var $viewcartObj = $("<div class=viewcart>");
                    var $h1Obj = $("<h1>장바구니</h1>");
                    $viewcartObj.append($h1Obj);
                    $viewcartObj.append($tableObj);
                    var $btObj = $('<button>주문하기</button>'); //'<input type="button" value="주문하기">'
                    $viewcartObj.append($btObj);
                    $("section>article").append($viewcartObj);
                    
                }else if(responseObj.status == -1){//장바구니가 없는경우
                    alert("장바구니가 비었습니다");
                }
            }
        });
        break;
        case "vieworder":
      	  $.ajax({
                url: hrefValue,
                method: 'get',
                success:function(responseObj){
                    if(responseObj.status == 1){ 
                        var $tableObj = $('<table>');//객체생성  //$("table") <-선택자이용해서 객체를 DOM에서 찾기
                        var tableData = "<tr><th>주문번호</th><th>주문일자</th><th>상품번호</th><th>상품명</th>    <th>가격</th><th>주문수량</th></tr>";
                        var arr = responseObj.list;
                        $(arr).each(function(index, info){
                        	var rowSize = info.lines.length;
                            tableData += '<tr>';
                            tableData += '<td rowspan="'+ rowSize + '">';
                            tableData += info.order_no;
                            tableData += '</td>';
                            tableData += '<td rowspan="'+ rowSize + '">';
                            tableData += info.order_dt;
                            tableData += '</td>';
                            $(info.lines).each(function(index, line){
                            	if(index > 0){
                            		tableData += '</tr>';
                            		tableData += '<tr>';
                            	}
                            	tableData += '<td>'+ line.p.prod_no+'</td>';
                            	tableData += '<td>'+ line.p.prod_name+'</td>';
                            	tableData += '<td>'+ line.p.prod_price+'</td>';
                            	tableData += '<td>'+ line.order_quantity+'</td>';
                            });
                            tableData += '</tr>';
                        });
                        $tableObj.html(tableData);
                        var $vieworderObj = $('<div class=vieworder>');
                        var $h1Obj = $("<h1>주문내역</h1>");
                        $vieworderObj.append($h1Obj);
                        $vieworderObj.append($tableObj);
                        $("section>article").append($vieworderObj);
                        
                    }else if(responseObj.status == -1){
                        alert(responseObj.msg);
                    }else if(responseObj.status == 0){
                    	alert("로그인부터 하세요");
                    }
                }
            });
        	break;
        default: //그외의 메뉴
            $.ajax({
                url: hrefValue, //요청URL
                method: "get", //요청방식
                success: function(data){ //data는 응답내용
                    $("section>article").html(data);
                }, //성공응답
                error: function(jqXHR){
                    alert("AJAX요청응답 실패 : 에러코드=" + jqXHR.status);
                } //실패응답
            });
        break;
        }
        return false; 
    });
            
              	
    //DOM에 장바구니보기의 주문하기버튼 객체가 없어서 click()함수가 효과없음!
    /* $("div.viewcart>button").click(function(){
        alert("주문하기 버튼 클릭!!!!");
    }); */

    //----------이벤트처리 ------------------
    //DOM에 향후 추가될 자식객체의 이벤트 처리
    //$(부모객체).on(이벤트종류, 자식객체, 콜백함수)

    //---장바구니보기메뉴의 주문하기버튼 클릭이벤트 START---
    $("section>article").on('click', 'div.viewcart>button', function(){
        $.ajax({
            url : './putorder',
            success: function(responseObj){
                if(responseObj.status == 1){
                    alert("주문성공!");
                    console.log( $("header>nav.large>ul>li>a[href='product/list']"));
                    $("header>nav.large>ul>li>a[href ='product/list']").trigger("click");
                    
                }else if(responseObj.status == 0){
                    alert("로그인부터 하세요");
                    console.log( $("header>nav.large>ul>li>a[href='./html/login.html']"));
                    
                    $('header>nav.large>ul>li>a[href="./html/login.html"]').trigger("click");
                }else if(responseObj.status == -1){
                    alert("주문실패: " + responseObj.msg); 
                }
            },
            error: function(jqXHR){
                alert("오류:" + jqXHR.status);
            }
        });
        return false;
    });
    //---장바구니보기메뉴의 주문하기버튼 클릭이벤트 END---
            	
    //상품목록메뉴 클릭이벤트를 강제 발생
    $("header>nav>ul>li>a[href='product/list']").trigger("click");
});
    </script>
</head>
<body>
    <header>
        <!--<h1>스타벅스</h1>-->
        <h1><a href="#"></a></h1>
        <nav class="large">
            <ul>
 <%
 if(session.getAttribute("loginInfo") == null){ //로그인성공안된 경우
 %>
                   <li><a href="./html/login.html">로그인</a></li>
                    <li><a href="./html/signup.html">가입</a></li>
<%
}else{
%>					<li><a href="logout">로그아웃</a></li>
<%
}
%>
                    <li><a href="product/list">상품목록</a></li>
                    <li><a href="viewcart">장바구니</a></li>
<%
 if(session.getAttribute("loginInfo") != null){ //로그인성공된 경우
 %>
                    <li><a href="vieworder">주문목록</a></li>
<%} %>              
					<li><a href="./html/upload.html">파일업로드</a>      
                </ul>
            </nav>
            <nav class="small">
                <ul>
 <%
 if(session.getAttribute("loginInfo") == null){
 %>
                    <li><a href="./html/login.html">로그인</a></li>
                    <li><a href="./html/signup.html">가입</a></li>
<%
}else{
%>					<li><a href="logout">로그아웃</a></li>
<%
}
%>                 
                </ul>
            </nav>
			
        </header>
        <section>
            <article>article...</article>
            <aside>
                <div class="strawberry">
                    <a href="https://www.starbucks.co.kr/whats_new/newsView.do?seq=4012" title="자세히 보기">
                        <img src="https://image.istarbucks.co.kr/upload/common/img/main/2021/strawberrymd_pc_210112.png" alt="" class="pc-badge">
                        <img src="https://image.istarbucks.co.kr/upload/common/img/main/2021/strawberrymd_mo_210112.png" alt="" class="mobile-badge">
                    </a>
                </div>
                <div class="plcc">      
                    <a href="/plcc/promotionView.do?eventCode=STH02" title="hyundai card + starbucks">
                        <img src="https://image.istarbucks.co.kr/upload/common/img/main/2020/plcc_badge_pc.png" alt="" class="pc-badge">
                        <img src="https://image.istarbucks.co.kr/upload/common/img/main/2020/plcc_badge_mobile.png" alt="" class="mobile-badge">
                    </a>
                </div>
            </aside>
        </section>
        <footer>
            사업자등록번호 : 201-81-21515 (주)스타벅스커피 코리아 대표이사 : 송 데이비드 호섭 TEL : 1522-3232 개인정보 책임자 : 장석현
ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.
        </footer>        
    </body>
</html>