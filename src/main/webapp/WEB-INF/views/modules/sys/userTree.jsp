<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>用户树</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var $1 = $.noConflict();
    </script>
    <script src="${ctxStatic}/jquery/jquery-3.1.0.min.js" type="text/javascript"></script>
    <link href="${ctxStatic}/orgChart/css/font-awesome.min.css" rel="stylesheet"/>
    <link href="${ctxStatic}/orgChart/css/jquery.orgchart.css" rel="stylesheet"/>
    <script src="${ctxStatic}/orgChart/js/jquery.orgchart.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(function() {
            var dataUrl = "${ctx}/sys/user/linkUserTree";
            <c:if test="${type == '1'}">
                dataUrl = "${ctx}/sys/user/reUserTree";
            </c:if>
            $('#chart-container').orgchart({
                'data': dataUrl,
                'pan': true,
                'zoom':true,
                'nodeContent': 'title',
                'clickEvent':function(nodeData){
                    if(nodeData.id != 'null'){
                        window.location.href='${ctx}/sys/user/registForm?loginName='+nodeData.name;
                    }else{
                        window.location.href='${ctx}/sys/user/registForm?linkperson='+nodeData.parentId;
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/user/info">个人信息</a></li>
    <li><a href="${ctx}/sys/user/modifyPwd">修改密码</a></li>
    <li <c:if test="${type == '0'}">class="active"</c:if>><a href="${ctx}/sys/user/userTreeView?type=0">接点树</a></li>
    <li <c:if test="${type == '1'}">class="active"</c:if>><a href="${ctx}/sys/user/userTreeView?type=1">推荐树</a></li>
</ul>
<div id="chart-container"></div>
</body>
</html>
