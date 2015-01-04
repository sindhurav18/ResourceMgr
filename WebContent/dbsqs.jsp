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
<%@ page import ="com.amazonaws.services.sqs.model.SendMessageRequest" %>
 <%@page import="action.PSOAlgorithm"%>
<%

Class.forName("com.mysql.jdbc.Driver"); 
java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://cloudtech.cafwmc855han.us-west-2.rds.amazonaws.com/cloudtech","root","sindhu77"); 
Statement st= con.createStatement(); 
ResultSet rs=st.executeQuery("select * from request"); 

AWSCredentials credentials = null;

try {

	BasicAWSCredentials credentials1 = new BasicAWSCredentials("AKIAIXN2AXGA6R6SIXWA","OIxnvh3LQwCzyTTk0l11o3A+kxaTRqff5s9iFDst");

	credentials  = credentials1;
	out.println(credentials.getAWSAccessKeyId());
	

} catch (Exception e) {
    throw new AmazonClientException(
            "Cannot load the credentials from the credential profiles file. " +
            "Please make sure that your credentials file is at the correct " +
            "location (/Users/Sindhura/.aws/credentials), and is in valid format.",
            e);
}

AmazonSQS sqs = new AmazonSQSClient(credentials);
Region usWest2 = Region.getRegion(Regions.US_WEST_2);
sqs.setRegion(usWest2);

out.println("===========================================");
out.println("Getting Started with Amazon SQS");
out.println("===========================================\n");

try {
    // Create a queue
    out.println("Creating a new SQS queue called MyQueue.\n");
    CreateQueueRequest createQueueRequest = new CreateQueueRequest("DevQueue");
    String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	out.println("MyQueueURL=" +myQueueUrl);
    // List queues
    System.out.println("Listing all queues in your account.\n");
    for (String queueUrl : sqs.listQueues().getQueueUrls()) {
        out.println("  QueueUrl: " + queueUrl);
    }
    out.println();

    // Send a message
   out.println("Sending a message to MyQueue.\n");
   //establish mysql connection - cloud
   String requestMsgBdy = null; 
   while(rs.next()){
	   requestMsgBdy=rs.getInt("reqId")+";"+rs.getString("Resource")+";"+rs.getString("Memory")+";"+rs.getString("Location")+";"+ rs.getString("Time")+";"+rs.getString("Platform")+";"+rs.getString("Type");
	   sqs.sendMessage(new SendMessageRequest(myQueueUrl,requestMsgBdy));
   }  
    
} catch (AmazonServiceException ase) {
    out.println("Caught an AmazonServiceException, which means your request made it " +
            "to Amazon SQS, but was rejected with an error response for some reason.");
    out.println("Error Message:    " + ase.getMessage());
    out.println("HTTP Status Code: " + ase.getStatusCode());
    out.println("AWS Error Code:   " + ase.getErrorCode());
    out.println("Error Type:       " + ase.getErrorType());
    out.println("Request ID:       " + ase.getRequestId());
} catch (AmazonClientException ace) {
    out.println("Caught an AmazonClientException, which means the client encountered " +
            "a serious internal problem while trying to communicate with SQS, such as not " +
            "being able to access the network.");
    out.println("Error Message: " + ace.getMessage());
}
//Calling Load Balancer 
PSOAlgorithm psoImpl= new PSOAlgorithm();
String a=psoImpl.runPSO();
out.println(a);
%>