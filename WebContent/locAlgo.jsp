<%@page import="action.LocationAware"%>

<%
LocationAware locImpl= new LocationAware();
String abc=locImpl.runLOC();
out.println(abc);
%>