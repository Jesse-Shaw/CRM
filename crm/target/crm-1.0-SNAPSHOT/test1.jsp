<%--
  Created by IntelliJ IDEA.
  User: 82755
  Date: 2021/8/5
  Time: 19:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>

<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript">

        $.ajax({
            url:"",
            data:{},
            type:"",
            dataType:"json",
            success:function (data){
            }
        })
    </script>
</head>
<body>





</body>
</html>
