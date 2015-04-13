import java.io.*;
import java.util.*;
import java.net.*;

class ServerThread extends Thread {
 private Thread t;
 String newLine = System.getProperty("line.separator");
 String username;
 String password;
 String ip;
 String requestMessageLine="";
 String fileName="";
 Boolean user = false;
 int lineNo = 0;
 List<ChatRoom> chatRoomList;
 int totalUsers = 0;
 int myPort = 80;
 boolean[] threadSignal;
 boolean running = false;
 StringTokenizer tokenizedLine = new StringTokenizer("");
 BufferedReader inFromClient;
 DataOutputStream outToClient;
 Socket connectionSocket;
 
 public ServerThread()
 {
   super();
 }

 ServerThread(List<ChatRoom> mainChatRooms, boolean[] signal, Socket s)
 {
  connectionSocket = s; 
  threadSignal = signal;
  chatRoomList = mainChatRooms; //Pass the reference to the thread for active updating.
  running = true;
 }

 
 public void testfunc() throws Exception
 {
     threadSignal[0]=true;
      for(;;)
      {
        // listen (i.e. wait) for connection request
   System.out.println("Starting new loop.");
   
   // set up the read and write end of the communication socket
   try
   {
   inFromClient = new BufferedReader (
                 new InputStreamReader(connectionSocket.getInputStream()));
   outToClient = new DataOutputStream (
                 connectionSocket.getOutputStream());
   }
   catch(IOException e)
   {
     System.out.println("Can't create input/output streams.");
       e.printStackTrace();
   }
   System.out.println ("Web server waiting for request on port " + myPort);
      // retrieve first line of request and set up for parsing
   try
   {
   requestMessageLine = inFromClient.readLine();
   }
   catch(Exception e)
   {
     e.printStackTrace();
     running = false;
     break;
   }
   System.out.println ("Request: " + requestMessageLine);

   while(requestMessageLine!=null && !requestMessageLine.startsWith("GET ")) //catch null error
   {
     try
     {
     requestMessageLine = inFromClient.readLine();
     }
        catch(Exception e)
   {
     e.printStackTrace();
     return;
   }
     System.out.println("requestMessageLine: "+ requestMessageLine);
     //GET requests are the only ones going to the server, so cut out errors.
   }
   
   FileIO listOfClients = new FileIO("clientList.txt", "clientListBuffer.txt");
   if(requestMessageLine!=null)
   {
   tokenizedLine = new StringTokenizer(requestMessageLine);
   
   if (tokenizedLine.nextToken().equals("GET"))
   {
     //GET requests form most of the requests to server.
     fileName = tokenizedLine.nextToken();
     System.out.println("Filename requested is : " + fileName);
     //A GET request for this should check if Client is permitted to receive this file.
     System.out.println("Socket: " + connectionSocket.getRemoteSocketAddress());
     ip = connectionSocket.getRemoteSocketAddress().toString().split("\\:")[0].substring(1);
     System.out.println(ip);
     
     if(fileName.startsWith("/clientList.txt"))
     {
     File file = new File("clientList.txt");
     int numOfBytes = (int) file.length();
     FileInputStream inFile;

     inFile = new FileInputStream ("clientList.txt");
 
     byte[] fileInBytes = new byte[numOfBytes];
     
     inFile.read(fileInBytes);
     int chatIndex = 0;
     requestMessageLine = inFromClient.readLine(); //User.
      System.out.println ("User: " + requestMessageLine);
      username = requestMessageLine;
      requestMessageLine = inFromClient.readLine(); //Password
      System.out.println("Password: " + requestMessageLine);
      password = requestMessageLine;

      if (listOfClients.checkUserPass(username,password))
      {
        System.out.println("User "+username+" authenticated.");  

        String totallength="";
        Client newClient = new Client(username, password, ip); //Valid client, create a object representing it
      for(int i=0;i<chatRoomList.size();i++)
      {
        //Calculate size first for HTTP protocol
        totallength+=chatRoomList.get(i).name + newLine;
      } //Expand string for names of chat rooms
      totallength+="!EOC" + newLine;
      for(int i=0;i<chatRoomList.size();i++)
      {
        for(int j=0;j<chatRoomList.get(i).clientList.size();j++)
        {
          totallength+=chatRoomList.get(i).clientList.get(j).getName()+"\n";
        }
      }  //Expand string for names of users
      totallength+="!EOU" + newLine;
      outToClient.writeBytes("HTTP/1.0 200 Document Follows"+newLine);
      outToClient.writeBytes ("Content-Length: " + totallength.length() + newLine);
      outToClient.writeBytes (newLine);
      //outToClient.write(fileInBytes, 0, numOfBytes);
      outToClient.writeBytes (totallength); //Everything. Strings have max capacity of 2 billion characters, so it should be fine.
      try
      {
      requestMessageLine = inFromClient.readLine(); //HTTP request
      requestMessageLine = inFromClient.readLine(); //This is the chat room from the client.
      }
      catch(IOException e)
      {
        System.out.println("User has disconnected before entering a chat room: Not logged.");
        running = false;
        break;
      }
      Boolean foundRoom = false;
      totallength = ""; //Reset the string
      for(int i=0;i<chatRoomList.size();i++)
      {
        if(chatRoomList.get(i).name.equals(requestMessageLine))
        {
          foundRoom = true;
          chatIndex = i;
          chatRoomList.get(i).addClient(newClient); //Add the new client to chatroom if found.
          System.out.println("User has joined existing room " + chatRoomList.get(i).name);
          totallength+="users"+newLine;
          for(int j=0;j<chatRoomList.get(i).clientList.size();j++)
          {
            totallength+=chatRoomList.get(i).clientList.get(j).getName()+newLine;
            totallength+=chatRoomList.get(i).clientList.get(j).getIP()+newLine;
          }
          totallength+="!EOUC"+newLine;
          System.out.println(totallength);
          break;
        }
      }
      if(foundRoom == false)
      {
        System.out.println("New room created with name " + requestMessageLine + " by " + username);
        chatIndex = chatRoomList.size(); //1 less than actual size after addition
        chatRoomList.add(new ChatRoom(newClient, requestMessageLine)); //If not, create new chatroom with client as founder.
        totallength="newroom"+newLine;
      }
      //outToClient.writeBytes("HTTP/1.0 200 Document Follows"+newLine);
      //outToClient.writeBytes ("Content-Length: " + totallength.length()+newLine);
      //outToClient.writeBytes (newLine);
      outToClient.writeBytes (totallength);
      
      //Wait for client to send back DC signal
      Boolean disconnect = false;
      while(disconnect!=true)
      {
        try
        {
        requestMessageLine = inFromClient.readLine();
        }
        catch(IOException e) //Any disconnect indicate signing off.
        {
          chatRoomList.get(chatIndex).removeClient(newClient);
          System.out.println(newClient.getName() + " has been removed from room " + chatRoomList.get(chatIndex).name);
          System.out.println(newClient.getName() + " has signed off.");
          running = false;
          break;
        }
      }
      }
      else
      {
         outToClient.writeBytes("HTTP/1.0 200 Document Follows"+newLine);
         outToClient.writeBytes ("Content-Length: " + 8 + newLine);
         outToClient.writeBytes("invalid"+newLine);
      }
            
     }
     
     else if(fileName.startsWith("/index.html"))
     {
       System.out.println("index.html was requested.");
       while(!requestMessageLine.equals(""))
       {
              requestMessageLine = inFromClient.readLine();
              System.out.println("RML: "+ requestMessageLine);
              //Empty out unnecessary input from browser.
       }
       // Main page, any server can request.
      /* File file = new File("index.html");
     int numOfBytes = (int) file.length();
     FileInputStream inFile = new FileInputStream ("index.html");
     byte[] fileInBytes = new byte[numOfBytes];
     inFile.read(fileInBytes);*/
     
     File file = new File("index.html");
     FileReader indexFile= new FileReader(file);
     BufferedReader br = new BufferedReader(indexFile);
     int activeUsers = 0;
     for(int i=0;i<chatRoomList.size();i++)
      {
        activeUsers+=chatRoomList.get(i).clientList.size();
      }
      totalUsers = listOfClients.retrieveClients();
      String activeU = ("Active users: "+activeUsers+newLine);
      String totalU = ("Total users: "+totalUsers+newLine);
      outToClient.writeBytes("HTTP/1.0 200 Document Follows"+newLine);
      long lengthContent = activeU.length() + totalU.length() + file.length() + 9;
      System.out.println("Length is: " + lengthContent);
      outToClient.writeBytes ("Content-Length: " + lengthContent + newLine);
      outToClient.writeBytes (newLine);
      String lineRead;
      while((lineRead = br.readLine()) != null)
      {
        outToClient.writeBytes(lineRead + newLine);
      }
      /*
      outToClient.writeBytes("<html>");
      outToClient.writeBytes("No spaces or special characters.");
      outToClient.writeBytes("<form action=\"submit.html\" method=\"get\">");
      outToClient.writeBytes("<input type=\"text\" name=\"cf_name\"><br>");
      outToClient.writeBytes("Your password<br>");
      outToClient.writeBytes("<input type=\"text\" name=\"cf_pass\"><br>");
      outToClient.writeBytes("<input type=\"submit\" value=\"Register\">");
      outToClient.writeBytes("<input type=\"reset\" value=\"Clear\">");
      outToClient.writeBytes("</form>");
      */
      //outToClient.write(fileInBytes, 0, numOfBytes);
      outToClient.writeBytes(activeU);
      outToClient.writeBytes(totalU);
      outToClient.writeBytes("</html>"+newLine);
      System.out.println("Appended stuff.");
      System.out.println("Finished sending files.");
      running = false;
      connectionSocket.close();
     }
     
     else if(fileName.startsWith("/submit.html")) //Registration, so some string longer than cf_pass+cf_user+submit.html
     {
     /*  File file = new File("submit.html");
     int numOfBytes = (int) file.length();
     FileInputStream inFile = new FileInputStream ("submit.html");
     byte[] fileInBytes = new byte[numOfBytes];
     inFile.read(fileInBytes);*/
     
    File file = new File("submit.html");
     FileReader indexFile= new FileReader(file);
     BufferedReader br = new BufferedReader(indexFile);
     int activeUsers = 0;
     for(int i=0;i<chatRoomList.size();i++)
     {
       activeUsers+=chatRoomList.get(i).clientList.size();
     }
     
      System.out.println(fileName);
      String[] parts = fileName.split("\\?");
      parts = parts[1].split("\\&");
      username = parts[0].substring(8);
      System.out.println("Username: " + username);
      password = parts[1].substring(8);
      System.out.println("Password: " + password);
      //Should be entered into database.
             
      FileWriter writer = new FileWriter("clientList.txt", true);
      writer.write("User:");
      writer.write(username);
      writer.write("\n");
      writer.write("Password:");
      writer.write(password);
      writer.write("\n");
      writer.close();
      
      totalUsers = listOfClients.retrieveClients();
      String activeU = ("Active users: "+activeUsers+newLine);
      String totalU = ("Total users: "+totalUsers+newLine);
      outToClient.writeBytes("HTTP/1.0 200 Document Follows"+newLine);
     long lengthContent = activeU.length() + totalU.length() + file.length() + 9;
      System.out.println("Length is: " + lengthContent);
      outToClient.writeBytes ("Content-Length: " + lengthContent + newLine);
      outToClient.writeBytes (newLine);
      //outToClient.write(fileInBytes, 0, numOfBytes); //Omit </html> tag here:
      String lineRead;
      while((lineRead = br.readLine()) != null)
      {
        outToClient.writeBytes(lineRead + newLine);
      }
      
      outToClient.writeBytes(activeU);
      outToClient.writeBytes(totalU);
      outToClient.writeBytes("</html>"+newLine);
      System.out.println("Appended stuff.");
      System.out.println("Finished sending files.");
      connectionSocket.close();
     }
     
      //Process info here with known user and password for registration. 
     
   //connectionSocket.close();   
   //inFromClient.close();
   //outToClient.close();
   }
    else
     {
      System.out.println ("Bad Request Message");
     }
   }
 }
 }

 public void run() {
  while(running)
   {
    try
    {
    testfunc();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      try
      {
      connectionSocket.close();
      }
      catch(Exception e2)
      {
        e2.printStackTrace();
      }
      running = false;
    }
   }
 }

 public void start() {
  System.out.println("New server thread started.");
  if(t == null)
   {
    t = new Thread(this);
    t.start();
   }
 }
 
}
