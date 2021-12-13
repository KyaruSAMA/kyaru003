<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>汽车管理</title>
    <link href="${pageContext.request.contextPath}/static/libs/bootstrap-3.4.1-dist/css/bootstrap.css" type="text/css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/static/libs/jquery/jquery-3.6.0.js" type="text/javascript" rel="script"></script>
    <script src="${pageContext.request.contextPath}/static/libs/bootstrap-3.4.1-dist/js/bootstrap.js" type="text/javascript" rel="script"></script>
    <link href="${pageContext.request.contextPath}/static/css/style.css" type="text/css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.css">


    <script src="//cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
    <script src="//cdn.bootcss.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <style>
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

        .right {
            float: right;
        }

        .grid:hover {
            background: #efefef;
        }
        .open > a {
            background-color: lightseagreen !important;
        }
    </style>
</head>
<body>
<div class="container" id="container" style="width: 100%;">
    <div class="row clearfix" id="head">
        <div class="col-md-10 column" style="line-height:100px;text-align:center;
            font-size: xxx-large;color:gold;background-color: lightseagreen;">
            二嗨租车
        </div>
        <div class="col-lg-2" style="background: lightseagreen;height: 100px ;text-align: right">
            <div class="right">
                <ul class="nav navbar-nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle"
                           data-toggle="dropdown"
                           style="height: 50px;text-align: right">
                            <img alt="" class="img-circle" src="${sessionScope.mUser.imagePath}" width="38px" height="38px"/>
                            <span style="color: #FFFFFF;font-size: 15px">
                                <i>${sessionScope.mUser.username}</i>
                            </span>
                        </a>
                        <div class="dropdown-menu pull-right"
                             style="background: #FFFFFF;width: 320px;overflow: hidden">
                            <div style="margin-top: 16px;border-bottom: 1px solid #eeeeee">
                                <div style="text-align: center">
                                    <img class="img-circle" src="${sessionScope.mUser.imagePath}"
                                         style="width: 38px;height: 38px;"/>
                                </div>
                                <div style="color: #323534;text-align: center;line-height: 36px;font-size: 15px">
                                    <span>${sessionScope.mUser.username}</span>
                                </div>
                            </div>

                            <div class="row" style="margin-left: 15px;margin-right: 15px;margin-top: 10px">
                                <div class="col-md-4 text-center grid" onclick="location.href='${pageContext.request.contextPath}/jsp/myList.jsp';">
                                    <i class="fa fa-user" style="font-size: 25px;line-height: 45px;"></i>
                                    <a href="${pageContext.request.contextPath}/jsp/myList.jsp" style="padding: 0;margin-top: 6px;margin-bottom: 10px;font-size: 12px">
                                        个人中心</a>
                                </div>
                                <div class="col-md-4 text-center grid" onclick="location.href='${pageContext.request.contextPath}/userUpdate?userId=${sessionScope.mUser.id}';">
                                    <i class="fa fa-gear" style="font-size: 25px;line-height: 45px;"></i>
                                    &nbsp;
                                    <a href="userUpdate?userId=${sessionScope.mUser.id}" style="padding: 0;margin-top: 6px;margin-bottom: 10px;font-size: 12px">
                                        账号管理</a>
                                </div>

                                <div class="col-md-4 text-center grid"onclick="location.href='${pageContext.request.contextPath}/rentList';">
                                    <i class="fa fa-gear" style="font-size: 25px;line-height: 45px;"></i>
                                    <a href="${pageContext.request.contextPath}/rentList" style="padding: 0;margin-top: 6px;margin-bottom: 10px;font-size: 12px">
                                        租车记录</a>
                                </div>
                            </div>


                            <div class="row" style="margin-top: 20px">
                                <div class="text-center"
                                     style="padding: 15px;margin: 0;background: #f6f5f5;color: #323534;"
                                     onclick="location.href='loginOut';">
                                    <a href="${pageContext.request.contextPath}/loginOut" class="fa fa-sign-out">退出登入界面</a>
                                </div>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>

        </div>
    </div>
    <div class="row clearfix" id="body" style="display: flex;flex-direction: row;min-height: 600px">
       <jsp:include page="left.jsp">
           <jsp:param name="navItem" value="carManage"/>
       </jsp:include>

        <div class="col-md-10 column" id="content">
            <div class="row clearfix">
                <div class="col-md-12 column" id="title" style="background-color: lightskyblue;
                        height: 50px;display: flex;flex-direction: column;justify-content: center;text-align: center">
                    <h3 style="margin: 0">修改汽车</h3>
                </div>
            </div>
            <div class="row clearfix">
                <div class="col-md-12 column" id="main" style="padding: 0;display: flex;justify-content: center">
                    <form role="form" action="${pageContext.request.contextPath}/doCarUpdate" method="post" enctype="multipart/form-data"
                          style="width: 50%;margin-top: 20px">
                        <div class="form-group">
                            <label for="carId">汽车编号</label>
                            <input type="text" class="form-control" id="carId"
                                   name="carId"  value="${requestScope.mCar.id}">
                        </div>
                        <div class="form-group">
                            <label for="brand">品牌名</label>
                            <input type="text" class="form-control" id="brand"
                                   name="brand"   value="${requestScope.mCar.brand}">
                        </div>
                        <div class="form-group">
                            <label for="category">类型名</label>
                            <input type="text"  class="form-control" id="category"
                                   name="category"  value="${requestScope.mCar.category}">
                        </div>
                        <div class="form-group">
                            <label for="model">型号</label>
                            <input type="text"  class="form-control" id="model"
                                   name="model"  value="${requestScope.mCar.model}">
                        </div>
                        <div class="form-group">
                            <label for="carNumber">车牌号</label>
                            <input type="text"  class="form-control" id="carNumber"
                                   name="carNumber" value="${requestScope.mCar.carNumber}">
                        </div>
                        <div class="form-group">
                            <label for="comments">简介</label>
                            <input type="text"  class="form-control" id="comments"
                                   name="comments"  value="${requestScope.mCar.comments}">
                        </div>
                        <div class="form-group">
                            <label for="color">颜色</label>
                            <input type="text"  class="form-control" id="color"
                                   name="color" value="${requestScope.mCar.color}">
                        </div>
                        <div class="form-group">
                            <label for="price">汽车价格</label>
                            <input type="text"  class="form-control" id="price"
                                   name="price"  value="${requestScope.mCar.price}">
                        </div>
                        <div class="form-group">
                            <label for="rent">每日租金</label>
                            <input type="text"  class="form-control" id="rent"
                                   name="rent"  value="${requestScope.mCar.rent}">
                        </div>
                        <div class="form-group">
                            <label for="image">汽车图片</label>
                            <div>
                                <img  class="img-rounded" id="imagePreview"
                                      src="${requestScope.mCar.imagePath}" alt="无图片"
                                      style="width:200px;height: 150px;margin-bottom: 5px">
                            </div>
                            <input type="file"  id="image" name="image">
                        </div>
                        <div class="form-group">
                            <label for="usable">是否上架</label>
                            <input type="checkbox" id="usable" name="usable"
                                   style="margin-left: 20px;"
                            ${"是".equals(requestScope.mCar.usable)?"checked":""}>是
                        </div>
                        <div class="form-group" style="display: flex;justify-content: flex-end">
                            <button class="btn btn-default" type="reset">重置
                            </button>
                            <button class="btn btn-default" type="submit" style="margin-left: 30px">提交
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="row clearfix" id="foot">
        <div class="col-md-12 column"
             style="height: 50px;line-height: 50px;text-align: center;background-color: lightskyblue;">
            二嗨租车&copy;版权所有<a href="#" style="color: white">erhai@erhai.com</a>
        </div>
    </div>
</div>

<script>
    $(function () {
        $(".dropdown").mouseover(function () {
            $(this).addClass("open");
        });

        $(".dropdown").mouseleave(function () {
            $(this).removeClass("open");
        })
    })
    $(document).ready(function () {
        document.getElementById("image").addEventListener("change", function (event) {
            event.preventDefault()
            event.stopPropagation()

            const file = this.files[0]
            const url = window.URL.createObjectURL(file)
            $("#imagePreview").attr("src",url)
        })
    })
</script>
</body>
</html>
