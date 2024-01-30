<%@page import="hyerin.HyerinTempData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
   HyerinTempData hyerindata = new HyerinTempData(request);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Greenpresso</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            background-color: #fcd0c7; 
            font-family: 'Arial', sans-serif;
        }

        table {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        /* 헤더 스타일 */
        table header {
            background-color: #ff6090;
            color: #fff;
            padding: 10px;
            border-radius: 10px 10px 0 0;
            text-align: center;
        }

        /* 푸터 스타일 */
        table footer {
            background-color: #ff6090; 
            color: #fff;
            padding: 10px;
            border-radius: 0 0 10px 10px;
            text-align: center;
        }

        /* 메뉴 스타일 */
        td.menu-item {
            background-color: #ff6090; 
            color: #fff;
            text-align: center;
            padding: 10px;
            border-radius: 5px;
            margin: 5px;
        }

        /* 메인 내용 스타일 */
        td.main-content {
            padding: 20px;
        }
    </style>
</head>
<body>
    <table border="">
        <tr>
            <td colspan="2" header>
                <jsp:include page="hrpage/header.jsp"/>
            </td>
        </tr>
        <tr>
            <td width="100px">
                <jsp:include page="hrpage/menuu.jsp"/>
            </td>
            <td width="700px" mainn-content>
                <jsp:include page="<%=hyerindata.mainUrl %>"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" footer>
                <jsp:include page="hrpage/footer.jsp"/>
            </td>
        </tr>
    </table>
</body>
</html>
