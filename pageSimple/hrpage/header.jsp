<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="hyerin.MenuReader" %>

<style>
    table.menu {
        width: 100%;
        background-color: #ff6090;
        color: #fff;
        padding: 10px;
        border-radius: 10px;
        text-align: center;
    }

    table.menu a {
        color: #fff;
        text-decoration: none;
        padding: 10px 20px;
        margin: 0 10px;
        border-radius: 5px;
        background-color: #ff4060;
    }

    table.menu a:hover {
        background-color: #ff3050;
    }
</style>

<%
    String filePath = application.getRealPath("/") + "pageHomework/menu.txt";
    List<String> menuNames = MenuReader.readMenuFromFile(filePath);
%>

<table class="menu">
    <tr align="center">
        <td><img src="https://old.greenart.co.kr/upimage/subject/teacher/20230725100229.png" alt="" style="height: 50px;"></td>
        <% for (String menuName : menuNames) { %>
            <td><a href="?cate=<%= menuName.toLowerCase() %>&main=<%= menuName.toLowerCase() %>"><%= menuName %></a></td>
        <% } %>
    </tr>
</table>
