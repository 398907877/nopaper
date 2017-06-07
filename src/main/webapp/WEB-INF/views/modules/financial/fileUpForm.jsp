<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
	
	<style type="text/css">
	
	.control-group{
	
	float:right;}
	
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
	</ul><br/>
	
	
	
	<form:form id="inputForm" modelAttribute="fclWkJh"  method="post" class="form-horizontal" style="width:700px">
		
		
		
		<div class="row">
  <div class="span12">
  
 
 <div class="page-header">
  <h1>开户信息录入 <small>提供自动回填和打印</small></h1>
</div>
    
    
    
    <div class="row">
      <div class="span4">  



                   
                          
                         
		
		
		<div class="control-group left">
			<label  class="control-label ">姓名:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>

			</div>
		</div>
		
				<div class="control-group">
			<label class="control-label">xingbie:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>

			</div>
		</div>
		
		
		
		<div class="control-group">
			<label class="control-label">拼音:</label>
			<div class="controls">
                <form:input path="money"  class="required"/>
			</div>
			
		</div>


		<div class="control-group">
			<label class="control-label">chushengriqi:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
				<div class="control-group">
			<label class="control-label">mingzu:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">证件号码:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">有效期限:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
		

		

				   </div>
		      <div class="span4"> 
		      
		      <div class="control-group">
			<label class="control-label">发证机关:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">证件地址:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机号码:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">固定电话:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">现居住地:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮政编码:</label>
			<div class="controls">
				<form:input path="money" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电子邮件:</label>
			<div class="controls">
				<form:input path="money"  class="required"/>
			</div>
		</div>
		      
		     
		
		
		</div>
		
		
		      <div class="span4"> 
		      
		      
		      <img src="https://www.baidu.com/img/gaokao_pc_22894732028445b2e2caaf21ebc5e508.png" class="img-rounded">
		      
		      <img src="https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2004621097,1850558574&fm=26&gp=0.jpg" class="img-rounded">
		      

		      
		      
		      </div>
    </div>
    
    
  </div>
</div>

		<div class="form-actions" style="padding-left:70px">
			<input  class="btn btn-primary"  type="button" value="清除内容" />
			<input  class="btn btn-primary"  type="button" value="开户人资料" />
			<input  class="btn btn-primary"  type="button" value="代理人资料" />
			<input  class="btn btn-primary"  type="button" value="联网核查" />
			<input  class="btn btn-primary"  type="button" value="打印凭单" />
			<input  class="btn btn-primary"  type="button" value="业务办理" />
		</div>

	</form:form>

 
 
 

 <script>
 function getPath(obj,fileQuery,transImg) {

	  var imgSrc = '', imgArr = [], strSrc = '' ;

	  if(window.navigator.userAgent.indexOf("MSIE")>=1){ // IE浏览器判断
	  if(obj.select){
	   obj.select();
	   var path=document.selection.createRange().text;
	   alert(path) ;
	   obj.removeAttribute("src");
	   imgSrc = fileQuery.value ;
	   imgArr = imgSrc.split('.') ;
	   strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
	   if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
	   obj.setAttribute("src",transImg);
	   obj.style.filter=
	    "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+path+"', sizingMethod='scale');"; // IE通过滤镜的方式实现图片显示
	   }else{
	   //try{
	   throw new Error('File type Error! please image file upload..'); 
	   //}catch(e){
	   // alert('name: ' + e.name + 'message: ' + e.message) ;
	   //}
	   }
	  }else{
	   // alert(fileQuery.value) ;
	   imgSrc = fileQuery.value ;
	   imgArr = imgSrc.split('.') ;
	   strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
	   if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
	   obj.src = fileQuery.value ;
	   }else{
	   //try{
	   throw new Error('File type Error! please image file upload..') ;
	   //}catch(e){
	   // alert('name: ' + e.name + 'message: ' + e.message) ;
	   //}
	   }

	  }

	  } else{
	  var file =fileQuery.files[0];
	  var reader = new FileReader();
	  reader.onload = function(e){

	   imgSrc = fileQuery.value ;
	   imgArr = imgSrc.split('.') ;
	   strSrc = imgArr[imgArr.length - 1].toLowerCase() ;
	   if(strSrc.localeCompare('jpg') === 0 || strSrc.localeCompare('jpeg') === 0 || strSrc.localeCompare('gif') === 0 || strSrc.localeCompare('png') === 0){
	   obj.setAttribute("src", e.target.result) ;
	   }else{
	   //try{
	   throw new Error('File type Error! please image file upload..') ;
	   //}catch(e){
	   // alert('name: ' + e.name + 'message: ' + e.message) ;
	   //}
	   }

	   // alert(e.target.result); 
	  }
	  reader.readAsDataURL(file);
	  }
	 }

	 function show(){
	  //以下即为完整客户端路径
	  var file_img=document.getElementById("img"),
	  iptfileupload = document.getElementById('iptfileupload') ;
	  getPath(file_img,iptfileupload,file_img) ;
	 }
	 function show1(){
		  //以下即为完整客户端路径
		  var file_img=document.getElementById("img1"),
		  iptfileupload = document.getElementById('iptfileupload1') ;
		  getPath(file_img,iptfileupload,file_img) ;
		 }

 </script>

</body>
</html>