<%--
  Created by IntelliJ IDEA.
  User: 82755
  Date: 2021/8/10
  Time: 20:11
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
    <meta charset="UTF-8">
    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script src="ECharts/echarts.min.js"></script>
    <script type="text/javascript">

        $(function (){
            //页面加载完毕后，绘制统计图表
            getCharts();
        })


        function getCharts(){

        alert(123)

        }

    </script>
</head>
<body>

</body>
</html>
