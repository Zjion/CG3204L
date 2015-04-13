import java.io.*;
import java.net.*;
import java.util.*;

public class TestThreadServer
{
 public static void main(String args[]) throws Exception
 {
  //Main file. All threads should start from here.
 String newLine = System.getProperty("line.separator");
 List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
 int totalusers = 0;
 boolean[] signal={true};
 boolean serverRunning = true;
 ServerSocket welcomeSocket = new ServerSocket(9000);

 
 while(serverRunning)
 {
   System.out.println("Out of try loop.");
   try
   {
     System.out.println("Starting try loop.");
     Socket incomingClientSocket = welcomeSocket.accept();
 ServerThread firstthread = new ServerThread(chatRoomList, signal, incomingClientSocket);
 System.out.println("Thread created.");
 firstthread.start(); //Start the thread and rely on it to start the next when accessed, and so on...
   }
   catch(IOException ioe)
   {
     System.out.println("Error in accepting incoming connection.");
     ioe.printStackTrace();
   }
   /*try
   {
     welcomeSocket.close();
     System.out.println("Server closed.");
   }
   catch(IOException ioe)
   {
     System.out.println("Error in closing socket.");
     System.exit(-1);
   }*/
}
 
}
}