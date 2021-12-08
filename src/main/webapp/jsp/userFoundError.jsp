<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/11/24
  Time: 16:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link href="static/libs/bootstrap-3.4.1-dist/css/bootstrap.css" type="text/css" rel="stylesheet">
    <script src="static/libs/jquery/jquery-3.6.0.js" type="text/javascript" rel="script"></script>
    <script src="static/libs/bootstrap-3.4.1-dist/js/bootstrap.js" type="text/javascript" rel="script"></script>
    <link href="static/css/style.css" type="text/css" rel="stylesheet">
    <style>
        a{
            width: 120px;
        }
        #left {
            padding: 0;
        }

        #left ul li {
            width: 100%;
        }

        #left ul li a {
            font-size: large;
            letter-spacing: 5px;
            border-radius: 0;
            border: 1px solid white;
        }
        #main a,#main a:hover,#main a:focus,#main a:visited,#main a:active{
            text-decoration: none;
        }
    </style>
</head>
<body>

<div style="height: 400px;display: flex;flex-direction: row;justify-content: center;align-content: center">
    <div class="pager panel-default" style="width: 40%">
        <div class="panel-heading">
            <h3 class="panel-title">
                结果
            </h3>
        </div>

        <div class="panel-body">
            <div style="display: flex;flex-direction: row;justify-content: center">
                <h3 class="text-danger">找不到用户</h3>
            </div>
            <div style="display: flex;flex-direction: row;justify-content: flex-end;margin-top: 20px">
                <a href="carList" class="btn btn-primary" role="button" style="margin-left: 20px">
                    返回用户列表
                </a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
