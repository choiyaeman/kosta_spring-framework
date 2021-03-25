<%@ page contentType="text/html; charset=UTF-8"%>
<style>
div.searchproduct{

    width: 180px;
    height: 32px;
    margin : 10px;
    top: 9px;
    border: 1px solid #ccc;
    border-radius: 5px;
    position: relative;
    vertical-align: baseline;
}

div.searchproduct>input[name=product]{
    border: none;
    color: #777;
    font-size: 12px;
    height: 22px;
    left: 0;
    padding: 0 10px;
    position: absolute;
    top: 5px;
    width: 80%;
}
div.searchproduct>img{
	position: absolute;
    z-index: 1;
    /* vertical-align: middle; */
    max-width: 100%;
    right: 0;
    top: 5px;
    
}
</style>
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
 -->
 <script>
$(function(){
	$("div.searchproduct>img").click(function(){
		//var url = "./searchproduct";
		var url = "./product/list";
		
		var method = "post";
		var data = "prod=" + $("div.searchproduct>input[name=product]").val();
		
		$("section>article>div.productlist>div.product").remove();
		
		$.ajax({
			url : url,
			method : method,
			data : data,
			success: function(data){
				$("section>article").html(data);
			},
			error: function(jqXHR){
				alert("오류:" + jqXHR.status);
			}
		});//end of ajax
		return false;
	});
});
</script>
<div class="searchproduct">
   <input type="text" placeholder="통합검색" name="product" >
  <!--  <input type="button" value="검색"> -->
   <img alt="통합검색" src="./images/icon_magnifier_black.png">
</div>