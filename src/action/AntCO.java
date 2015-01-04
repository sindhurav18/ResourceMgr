package action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import sun.org.mozilla.javascript.internal.ast.ReturnStatement;
import action.AntColonyOptimization;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class AntCO {
	
		public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException, InterruptedException, ExecutionException{
				
				 AWSCredentials credentials = null;
				 String returnMsg = null;

					try {
						//System.out.println("hey");
						
						BasicAWSCredentials credentials1 = new BasicAWSCredentials("accesskey","pwd");
						//System.out.println("yo");
						
						credentials  = credentials1;
						//System.out.println(credentials.getAWSAccessKeyId());
						
						//credentials1 = new ProfileCredentialsProvider("default").getCredentials();
					} catch (Exception e) {
					    throw new AmazonClientException(
					            "Cannot load the credentials from the credential profiles file. " +
					            "Please make sure that your credentials file is at the correct " +
					            "location (/Users/KovidReddy/.aws/credentials), and is in valid format.",
					            e);
					}

					
					
					Class.forName("com.mysql.jdbc.Driver"); 
					java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://cloudtech.cafwmc855han.us-west-2.rds.amazonaws.com/cloudtech","root","sindhu77"); 
					//Receive requests from SQS
					AmazonSQS sqs = new AmazonSQSClient(credentials);
					Region usWest2 = Region.getRegion(Regions.US_WEST_2);
					sqs.setRegion(usWest2);
					String requestQueueURL = "https://sqs.us-west-2.amazonaws.com/066339621334/DevQueue";
					//lISTING Queues
					 System.out.println("Listing all queues in your account.\n");
					  
					 // Receive messa
					 //Use While
					    System.out.println("Receiving messages from MyQueue.\n");
					    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(requestQueueURL);
					    //ReceiveMessageResult receiveMessageResponse =  sqs.receiveMessage(receiveMessageRequest);    
						
					    List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
						returnMsg = messages.toString();
						
						
							
						
					    while(!messages.isEmpty()){
						//List<Message> messages= receiveMessageResponse.getMessages();
					    String Resource = null;
					    String reqId = null;
					    String reqMemory=null;
					    String Location=null;
					    String Latitude = null;
					    String Longitude = null;
					    String Time=null;
					    String Platform = null;
					    String Type=null;
					    System.out.println("writing");

					    for (Message message : messages) {
					        String fullMsg = message.getBody();
					        String strSplit[]= fullMsg.split(";");
					        reqId=strSplit[0];
					        Resource=strSplit[1];
					        reqMemory=strSplit[2];
					        Location=strSplit[3];
					        Time=strSplit[4];
					        Platform=strSplit[5];
					        Type=strSplit[6];
					        Latitude=strSplit[7];
					        Longitude=strSplit[8];
					        
					        System.out.println("to tsp");

					        
					        
					        
					        File write_file = new File("berlin52.tsp");
					    	  String Content = reqId+" "+Latitude+" "+Longitude+"\n";
					    	  try 
					    	  {
					    	      RandomAccessFile raf = new RandomAccessFile(write_file, "rw");
					    	      long length = raf.length();
					    	      System.out.println(length);
					    	      raf.setLength(length + 1); //+ (integer value) for spacing
					    	      raf.seek(raf.length());
					    	      raf.writeBytes(Content);
					    	      raf.close();
					    	  } 
					    	  catch (Exception e) {
					    	      System.out.println(e);
					    	  }
					        System.out.println("writing to berlin52.tsp");
					    }

					    
					    File write_file = new File("berlin52.tsp");
				    	  String Content = "EOF";
				    	  try 
				    	  {
				    	      RandomAccessFile raf = new RandomAccessFile(write_file, "rw");
				    	      long length = raf.length();
				    	      System.out.println(length);
				    	      raf.setLength(length + 1); //+ (integer value) for spacing
				    	      raf.seek(raf.length());
				    	      raf.writeBytes(Content);
				    	      raf.close();
				    	  } 
				    	  catch (Exception e) {
				    	      System.out.println(e);
				    	  }
					 
					      
					    	 
					      
					     
					    
					    
					    
					    //Connection to Instances SQL
					Statement st= con.createStatement(); 
					returnMsg= returnMsg + "select InstID,AMI,Memory,URL,cpuLoad,Latitude,Longitude from Instance WHERE AMI='"+Platform+"'"+" AND Memory >='"+reqMemory+"'";
					//ResultSet rset=st.executeQuery("Select InstID,Latitude,Longitude from Instance into outfile 'berlin.tsp'");
					ResultSet rs=st.executeQuery("select InstID,AMI,Memory,URL,cpuLoad,Latitude,Longitude from Instance WHERE AMI='"+Platform+"'"+" AND Memory >='"+reqMemory+"'"); 
					ArrayList<String> instanceIds = new ArrayList<String>();
					HashMap<String, Integer> instanceLoad = new HashMap<String, Integer>();
					HashMap<String, Double> instanceLat = new HashMap<String, Double>();
					HashMap<String, Double> instanceLon = new HashMap<String, Double>();
					HashMap<String, Double> instanceMem = new HashMap<String, Double>();
					HashMap<String, String> instanceURL = new HashMap<String, String>();
					
					while(rs.next()){
						returnMsg= returnMsg + "In rs.next";
						  instanceIds.add(rs.getString("InstID"));
						  instanceLoad.put(rs.getString("InstID"),rs.getInt("cpuLoad"));
						  instanceLat.put(rs.getString("InstID"),rs.getDouble("Latitude"));
						  instanceLon.put(rs.getString("InstID"),rs.getDouble("Longitude"));
						  instanceMem.put(rs.getString("InstID"), rs.getDouble("Memory"));
						  instanceURL.put(rs.getString("InstID"),rs.getString("URL"));
						
						 for(int i=0;i<instanceIds.size();i++){
							 //System.out.println(instanceIds.get(i));
							 
						 }
					}


					long initTime = System.currentTimeMillis();
		
		AntColonyOptimization antColonyOptimization = new AntColonyOptimization();
		String allocatedHostID=antColonyOptimization.start();
		
		System.out.println(instanceLoad.get(allocatedHostID));
		
		int newProcPower=(int) (3.8 + 20);
		instanceLoad.put(allocatedHostID,newProcPower);
		Statement updState= con.createStatement(); 
		returnMsg= returnMsg + "Updating Database Below";
		double memory =11.19;
		double netmemory = memory- Double.parseDouble(reqMemory);
		updState.executeUpdate("UPDATE Instance SET cpuLoad='"+newProcPower+"'"+",Memory='"+netmemory+"'"+" WHERE InstID='"+allocatedHostID+"'"); 
		returnMsg= returnMsg + "After Update";
		
		//Insert into Response 
		Statement insResponse= con.createStatement(); 
		long endTime = System.currentTimeMillis();
		double respTime = endTime-initTime;
		Calendar calendar = Calendar.getInstance();
	    java.sql.Timestamp startTimeofRequest = new java.sql.Timestamp(calendar.getTime().getTime());
	    
	    //Instance allocInst = new Instance();
	    //allocInst.setInstanceId(allocatedHostID);
	    String publicURL = instanceURL.get(allocatedHostID);
	    
	    returnMsg=returnMsg + "INSERT INTO Response (resInstID,resMemory,AMI,resTime,startTime,"
	    		+ "reqId,URL,Algorithm) VALUES('"+allocatedHostID+"','"+reqMemory+"','"+Resource+"','"+respTime+"','"
	    		+startTimeofRequest+"','"+reqId+"','"+publicURL+"',)";
	    String reqAlgo = "Ant Colony Algorithm";
		   insResponse.executeUpdate("INSERT INTO Response (resInstID,resMemory,AMI,resTime,startTime,"
		    		+ "reqId,URL,Algorithm) VALUES('"+allocatedHostID+"','"+reqMemory+"','"+Resource+"','"+respTime+"','"
		    		+startTimeofRequest+"','"+reqId+"','"+publicURL+"','"+reqAlgo+"')");
		// Delete a message
	      //  System.out.println("Deleting a message.\n");
		String messageRecieptHandle = messages.get(0).getReceiptHandle();
	    sqs.deleteMessage(new DeleteMessageRequest(requestQueueURL, messageRecieptHandle));
		
		//Update in mysql with the load
		//System.out.println("Initial Host Value="+initialHostID);
		//System.out.println("MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024));

	//System.out.println("MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024));

	//System.out.println("EndTime="+endTime);
	//System.out.println("InitTime="+initTime);
	//System.out.println("Resource Allocation Time:" +(endTime-initTime));
	messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
	    }
	}
}
		
		
		
		
		
		
		