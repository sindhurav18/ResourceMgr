<%@page import="com.amazonaws.auth.BasicAWSCredentials"%>
<%@ page import ="java.sql.*" %>
<%@ page import ="javax.sql.*" %>
<%@ page import ="java.util.List" %>
<%@ page import ="java.util.Map.Entry" %>
<%@ page import ="com.amazonaws.AmazonClientException" %>
<%@ page import ="com.amazonaws.AmazonServiceException" %>
<%@ page import ="com.amazonaws.auth.AWSCredentials" %>
<%@ page import ="com.amazonaws.auth.profile.ProfileCredentialsProvider" %>
<%@ page import ="com.amazonaws.regions.Region" %>
<%@ page import ="com.amazonaws.regions.Regions" %>
<%@ page import ="com.amazonaws.services.sqs.AmazonSQS" %>
<%@ page import ="com.amazonaws.services.sqs.AmazonSQSClient" %>
<%@ page import ="com.amazonaws.services.sqs.model.CreateQueueRequest" %>
<%@ page import ="com.amazonaws.services.sqs.model.DeleteMessageRequest" %>
<%@ page import ="com.amazonaws.services.sqs.model.DeleteQueueRequest" %>
<%@ page import ="com.amazonaws.services.sqs.model.Message" %>
<%@ page import ="com.amazonaws.services.sqs.model.ReceiveMessageRequest" %>
<%@ page import ="com.amazonaws.services.sqs.model.ReceiveMessageResult" %>
<%@ page import ="com.amazonaws.services.sqs.model.SendMessageRequest" %>
<%@ page import ="java.io.BufferedReader" %>
<%@ page import ="java.io.IOException" %>
<%@ page import ="java.io.InputStreamReader" %>
<%@ page import ="java.net.MalformedURLException" %>
<%@ page import ="java.net.URL" %>
<%@ page import ="java.net.URLConnection" %>
<%@ page import ="java.util.Date" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.HashMap" %>
<%@ page import ="java.util.HashSet" %>
<%@ page import ="java.util.LinkedHashMap" %>
<%@ page import ="java.util.List" %>
<%@ page import ="java.util.Random" %>
<%@ page import ="java.util.Set" %>

<%@ page import ="com.amazonaws.AmazonClientException" %>
<%@ page import ="com.amazonaws.auth.AWSCredentials" %>
<%@ page import ="com.amazonaws.auth.profile.ProfileCredentialsProvider" %>
<%@ page import ="com.amazonaws.regions.Region" %>
<%@ page import ="com.amazonaws.regions.Regions" %>
<%@ page import ="com.amazonaws.services.cloudwatch.AmazonCloudWatchClient" %>
<%@ page import ="com.amazonaws.services.cloudwatch.model.Datapoint" %>
<%@ page import ="com.amazonaws.services.cloudwatch.model.Dimension" %>
<%@ page import ="com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest" %>
<%@ page import ="com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult" %>
<%@ page import ="com.amazonaws.services.ec2.AmazonEC2Client" %>

<%@ page import ="com.amazonaws.services.ec2.model.DescribeImagesResult" %>
<%@ page import ="com.amazonaws.services.ec2.model.DescribeInstancesRequest" %>
<%@ page import ="com.amazonaws.services.ec2.model.DescribeInstancesResult" %>
<%@ page import ="com.amazonaws.services.ec2.model.Instance" %>
<%@ page import ="com.amazonaws.services.ec2.model.Reservation" %>

<HTML>
    <HEAD>
        <TITLE>Response </TITLE>
    </HEAD>

    <BODY>
        <H1 style="text-align: center;padding-bottom: 3%;">Request- Response </H1>

        <% 
        Class.forName("com.mysql.jdbc.Driver"); 
java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://cloudtech.cafwmc855han.us-west-2.rds.amazonaws.com/cloudtech", "root", "sindhu77");

            Statement statement = connection.createStatement() ;
            ResultSet resultset = 
                statement.executeQuery("select reqId,AMI,resInstID,URL,resTime from Response where Algorithm='ArtificialBee'") ; 
            Statement statement2 = connection.createStatement() ;
            ResultSet resultset2 = 
                    statement2.executeQuery("select reqId,AMI,resInstID,URL,resTime from Response where Algorithm='AntColony'") ; 
            Statement statement3 = connection.createStatement() ;
            ResultSet resultset3 = 
                    statement3.executeQuery("select reqId,AMI,resInstID,URL,resTime from Response where Algorithm='PSOAlgorithm'") ; 
            Statement statement4 = connection.createStatement() ;
            ResultSet resultset4 = 
                    statement4.executeQuery("select reqId,AMI,resInstID,URL,resTime from Response where Algorithm='LocationAware'") ; 
        %>
        <TABLE>
    
       <TR >
       <TD style="padding-left: 4%;padding-top: 2%;    padding-right: 2%;    padding-bottom: 2%;">
            <H2>Artificial Bee </H2>
        <TABLE BORDER="1">
            <TR style=" background-color: rgb(50, 150, 70); color: white;">
              <!--   <TH>Instance Id</TH>
                <TH>Memory</TH>
                <TH>AMI</TH>
                <TH>RESPONSE TIME</TH>
                  <TH>START TIME</TH>
                    <TH>REQUEST ID</TH>
                      <TH>RESPONSE URL</TH>
                      <TH>ALGORITHM</TH>-->
                       
                       <TH>REQUEST ID</TH>
                       <TH>AMI</TH>
                       <TH>Instance Id</TH>
                       <TH>RESPONSE URL</TH>
                       <TH>RESPONSE TIME</TH>
            </TR>
            <% while(resultset.next()){ %>
            <TR>
                <TD> <%= resultset.getString(1) %></td>
                 <TD> <%=resultset.getString(2) %></td>
                <TD> <%= resultset.getString(3) %></TD>
                <TD> <%=resultset.getString(4) %></td>
                <TD> <%= resultset.getDouble(5) %></TD>                
                
              </TR>
            <% } %>
        </TABLE>
        </TD>

        <TD style="padding-left: 4%;padding-top: 2%;    padding-right: 2%;    padding-bottom: 2%;">
        <H2>Ant Colony </H2>
        <TABLE BORDER="1">
            <TR style=" background-color: rgb(50, 150, 70); color: white;">
              <!--   <TH>Instance Id</TH>
                <TH>Memory</TH>
                <TH>AMI</TH>
                <TH>RESPONSE TIME</TH>
                  <TH>START TIME</TH>
                    <TH>REQUEST ID</TH>
                      <TH>RESPONSE URL</TH>
                      <TH>ALGORITHM</TH>-->
                       
                       <TH>REQUEST ID</TH>
                       <TH>AMI</TH>
                       <TH>Instance Id</TH>
                       <TH>RESPONSE URL</TH>
                       <TH>RESPONSE TIME</TH>
            </TR>
            <% while(resultset2.next()){ %>
            <TR>
                <TD> <%= resultset2.getString(1) %></td>
                 <TD> <%=resultset2.getString(2) %></td>
                <TD> <%= resultset2.getString(3) %></TD>
                <TD> <%=resultset2.getString(4) %></td>
                <TD> <%= resultset2.getDouble(5) %></TD>                
                
              </TR>
            <% } %>
        </TABLE>
        </TD>
        </TR>
       
       <TR>
        <TD style="padding-left: 4%;padding-top: 2%;    padding-right: 2%;    padding-bottom: 2%;">
        <H2>PSO</H2>
        <TABLE BORDER="1">
            <TR style=" background-color: rgb(50, 150, 70); color: white;">
              <!--   <TH>Instance Id</TH>
                <TH>Memory</TH>
                <TH>AMI</TH>
                <TH>RESPONSE TIME</TH>
                  <TH>START TIME</TH>
                    <TH>REQUEST ID</TH>
                      <TH>RESPONSE URL</TH>
                      <TH>ALGORITHM</TH>-->
                       
                       <TH>REQUEST ID</TH>
                       <TH>AMI</TH>
                       <TH>Instance Id</TH>
                       <TH>RESPONSE URL</TH>
                       <TH>RESPONSE TIME</TH>
            </TR>
            <% while(resultset3.next()){ %>
            <TR>
                <TD> <%= resultset3.getString(1) %></td>
                 <TD> <%=resultset3.getString(2) %></td>
                <TD> <%= resultset3.getString(3) %></TD>
                <TD> <%=resultset3.getString(4) %></td>
                <TD> <%= resultset3.getDouble(5) %></TD>                
                
              </TR>
            <% } %>
        </TABLE>
        </TD>
       <!--  </div>
        <div style="margin-left: 55%;margin-top: -15.5%;"> -->
        
        <TD style="padding-left: 4%;padding-top: 2%;    padding-right: 2%;    padding-bottom: 2%;">
        <H2>Location Aware </H2>
        <TABLE BORDER="1">
            <TR style=" background-color: rgb(50, 150, 70); color: white;">
              <!--   <TH>Instance Id</TH>
                <TH>Memory</TH>
                <TH>AMI</TH>
                <TH>RESPONSE TIME</TH>
                  <TH>START TIME</TH>
                    <TH>REQUEST ID</TH>
                      <TH>RESPONSE URL</TH>
                      <TH>ALGORITHM</TH>-->
                       
                       <TH>REQUEST ID</TH>
                       <TH>AMI</TH>
                       <TH>Instance Id</TH>
                       <TH>RESPONSE URL</TH>
                       <TH>RESPONSE TIME</TH>
            </TR>
            <% while(resultset4.next()){ %>
            <TR>
                <TD> <%= resultset4.getString(1) %></td>
                 <TD> <%=resultset4.getString(2) %></td>
                <TD> <%= resultset4.getString(3) %></TD>
                <TD> <%=resultset4.getString(4) %></td>
                <TD> <%= resultset4.getDouble(5) %></TD>                
                
              </TR>
            <% } %>
        </TABLE>
       
       </TD>
       </TR>
        </TABLE>
    </BODY>
</HTML>