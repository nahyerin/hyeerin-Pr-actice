<%@page import="hyerin.HyerinMenu"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    ArrayList<HyerinMenu> menuu = (ArrayList<HyerinMenu>) request.getAttribute("menuu");
%>
<h3>그린프레소<br/>알아보기</h3>
<%
    if (menuu != null) {
%>
    <table width="100%">
        <% for (HyerinMenu a : menuu) { %>
            <tr>
                <td><a href="?cate=<%=a.catee %>&main=<%=a.mainn %>"><%=a.titlee %></a></td>
            </tr>
        <% } %>
    </table>
<%
    } else {
        out.println("메뉴가 없습니다.");
    }
%>
