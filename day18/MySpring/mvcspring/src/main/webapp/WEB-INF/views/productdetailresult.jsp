<%@page import="com.my.vo.Product"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
Product p = (Product)request.getAttribute("p");
%>
<script>
$(function(){
	$('div.productdetail>div.detail>ul>li>input[type=button]').click(function(){
		var prod_no = '<%=p.getProd_no()%>';
		var quantity = 
$('div.productdetail>div.detail>ul>li>input[type=number]').val();
		var url = "./putcart";
		var method = "get";
		var data = {prod_no:prod_no, 
				    quantity: quantity};
		$.ajax({
			url:url,
			method: method,
			data: data,
			success: function(data){
alert('장바구니 넣기 성공: 상품번호=' + prod_no + ", 수량=" + quantity);
				$("div.bg").show();
			}
		});
	});
	
	var bg = $('div.bg');
	
    $("div.modal").find("span.productlist").click(function(event){
    	alert("계속하기 클릭 ");
	    bg.hide();//배경레이어용DIV 사라지기
	    
	    //상품목록메뉴에 클릭이벤트 강제발생
	    $("body > header > nav.large > ul > li > a[href='product/list']").trigger("click");
	    
	});

	$("div.modal").find("span.viewcart").click(function(){
	    alert("장바구니보기 클릭");
	    bg.hide();//배경레이어용DIV 사라지기
	});

});
</script>
<style>
    div.bg{
        display: none;
        position: fixed;
        z-index: 9999;
        border: 1px solid;
        left: 0px;
        top: 100px;
        width: 100%;
        height: 100%;
        margin: 10px;
        overflow: auto;
        /* 레이어 색깔 */
        background-color: rgba(199, 196, 196, 0.4)
    }
    div.modal{
        display: block;
        position: relative;
        border: 1px solid orange;
        border-radius: 10px;
        background-color: papayawhip;
        /* box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19); */

        /* 배경레이어 보다 앞에 보이기*/
        z-index:10000;
        width: 40%;
        height: 100px;
        /* div center 정렬*/
        top: 40%;
        margin: 0 auto;
        text-align: center;
        line-height: 100px;
    }
    #my_modal>span{
        margin-left: 10px;
        margin-right: 10px;
    }
    #my_modal>span:hover{
        background-color: darkblue;
        color: white;
    }
</style>
<div class="productdetail" style="width:500px;">
   <img src="./images/<%=p.getProd_no()%>.jpg" style="float:left; width:40%">
   <div class="detail" style="float:right;">
      <ul style="list-style-type: none; padding: 0 10px; margin: 0">
         <li>상품번호: <%=p.getProd_no()%></li>
         <li>상품명: <%=p.getProd_name()%></li>
         <li>가격: <%=p.getProd_price()%></li>
         <li>수량: <input type="number" value="1" min="1" max="99"></li>
         <li><input type="button" value="장바구니 넣기"></li>
      </ul>
   </div>
</div>
<div class="bg">
    <div class="modal">
        <span class="productlist">계속하기</span> <span class="viewcart">장바구니보기</span>
    </div>
</div> 
