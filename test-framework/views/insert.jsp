<%@page import="app.models.Emp" %>

<% 
    Emp e = (Emp) request.getAttribute("emp");
%>
<p><%= e.getId() %></p>
<p><%= e.getName() %></p>
<p><%= e.getEmbauche() %></p>
