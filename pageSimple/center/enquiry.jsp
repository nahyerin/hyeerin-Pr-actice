<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>문의사항 게시판</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }

        header {
            background-color: #333;
            color: white;
            padding: 10px;
            text-align: center;
        }

        table {
            width: 80%;
            margin: 20px auto;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        th, td {
            border: 1px solid #ddd;
            padding: 15px;
            text-align: left;
        }

        th {
            background-color: #4CAF50;
            color: white;
        }

        a {
            text-decoration: none;
            color: #007bff;
        }

        tr:hover {
            background-color: #f5f5f5;
        }
    </style>
</head>
<body>
    <header>
        <h1>문의사항 게시판</h1>
    </header>
    <table>
        <tr>
            <th>ID</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
        </tr>
        <tr>
            <td>1</td>
            <td><a href="#">첫 번째 질문 제목</a></td>
            <td>사용자1</td>
            <td>2024-01-21</td>
        </tr>
        <tr>
            <td>2</td>
            <td><a href="#">두 번째 질문 제목</a></td>
            <td>사용자2</td>
            <td>2024-01-22</td>
        </tr>
        <tr>
            <td>3</td>
            <td><a href="#">세 번째 질문 제목</a></td>
            <td>사용자3</td>
            <td>2024-01-23</td>
        </tr>
    </table>
</body>
</html>
