<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<%@page import="app.models.Emp, java.util.Vector" %>
<body>
    <% Vector<Emp> emps = (Vector<Emp>)request.getAttribute("emps"); %>
    <h1>emp works</h1>
    <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Cumque optio voluptate, repellendus dolorum est ducimus, vitae repudiandae ipsam commodi explicabo ex pariatur eum animi dolore neque iusto fuga, quae quisquam!</p>
    <% for(Emp e : emps) { %>
        <p><%= e.getName() %></p>
    <% } %>
</body>
</html>