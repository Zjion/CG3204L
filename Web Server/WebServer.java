import java.io.*;
import java.net.*;
import java.util.*;

class WebServer
{
 public static void main (String args[]) throws Exception
  {
   String username;
   String password;
   String ip;
   String requestMessageLine="";
   String newLine = System.getProperty("line.separator");
   String fileName ="";
   Boolean user= false;
   int lineNo = 0;
   List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>(); //Stores all chatrooms created on server.
   int totalUsers = 0;
   
   // check if a port number is given as the first command line argument
   // if not argument is given, use port number 80
   int myPort = 80;
   if (args.length > 0)
     {
      try {
    myPort = Integer.parseInt(args[0]);
   } 
      catch (ArrayIndexOutOfBoundsException e) 
          {
    System.out.println("Need port number as argument");
    System.exit(-1);
   } 
      catch (NumberFormatException e) 
          {
    System.out.println("Please give port number as integer.");
    System.exit(-1);
   }
     }

   // set up connection socket
   ServerSocket listenSocket = new ServerSocket (myPort);

  StringTokenizer tokenizedLine = new StringTokenizer("");
   // listen (i.e. wait) for connection request
   
   for(;;)
   {
        // listen (i.e. wait) for connection request
   System.out.println("Starting new loop.");
   Socket connectionSocket = listenSocket.accept();
   
   // set up the read and write end of the communication socket
   BufferedReader inFromClient = new BufferedReader (
                 new InputStreamReader(connectionSocket.getInputStream()));
   DataOutputStream outToClient = new DataOutputStream (
                 connectionSocket.getOutputStream());

   System.out.println ("Web server waiting for request on port " + myPort);
      // retrieve first line of request and set up for parsing
   requestMessageLine = inFromClient.readLine();
   System.out.println ("Request: " + requestMessageLine);

   while(requestMessageLine!=null && !requestMessageLine.startsWith("GET ")) //catch null error
   {
     requestMessageLine = inFromClient.readLine();
     System.out.println("requestMessageLine: "+ requestMessageLine);
     //GET requests are the only ones going to the server, so cut out errors.
   }

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
       FileInputStream inFile = new FileInputStream ("clientList.txt");
     byte[] fileInBytes = new byte[numOfBytes];
     inFile.read(fileInBytes);
     
     requestMessageLine = inFromClient.readLine(); //User.
      System.out.println ("User: " + requestMessageLine);
      username = requestMessageLine;
      requestMessageLine = inFromClient.readLine(); //Password
      System.out.println("Password: " + requestMessageLine);
      password = requestMessageLine;
      
      FileIO listOfClients = new FileIO("clientList.txt", "clientListBuffer.txt");

      if (listOfClients.checkUserPass(username,password))
      {
        System.out.println("User "+username+" authenticated.");  

        String totallength="";
        Client newClient = new Client(username, password, ip); //Valid client, create a object representing it
        for(int i=0;i<chatRoomList.size();i++)
        {
        //Calculate size first for HTTP protocol
          totallength+=chatRoomList.get(i).name + "\n";
        } //Expand string for names of chat rooms
        totallength+="!EOC\n";
        for(int i=0;i<chatRoomList.size();i++)
        {
          for(int j=0;j<chatRoomList.get(i).clientList.size();j++)
          {
            totallength+=chatRoomList.get(i).clientList.get(j).getName()+"\n";
          }
        }  //Expand string for names of users
        totallength+="!EOU\n";
        outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
        outToClient.writeBytes ("Content-Length: " + totallength.length() + "\r\n");
        outToClient.writeBytes ("\r\n");
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
        }
        Boolean foundRoom = false;
        totallength = ""; //Reset the string
        int chatIndex = chatRoomList.size();
        for(int i=0;i<chatRoomList.size();i++)
        {
          if(chatRoomList.get(i).name.equals(requestMessageLine))
          {
            foundRoom = true;
            chatIndex = i;
            chatRoomList.get(i).addClient(newClient); //Add the new client to chatroom if found.
            totallength+="users\n";
            for(int j=0;j<chatRoomList.get(i).clientList.size();j++)
            {
              totallength+=chatRoomList.get(i).clientList.get(j).getName()+"\n";
              totallength+=chatRoomList.get(i).clientList.get(j).getIP()+"\n";
            }
            totallength+="!EOUC\n";
            break;
          }
        }
        if(foundRoom == false)
        {
          System.out.println("New room created with name " + requestMessageLine + " by " + username);
          chatIndex = chatRoomList.size(); //1 less than actual size after addition
          chatRoomList.add(new ChatRoom(newClient, requestMessageLine)); //If not, create new chatroom with client as founder.
          totallength="newroom\n";
        }
        outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
        outToClient.writeBytes ("Content-Length: " + totallength.length() + "\r\n");
        outToClient.writeBytes ("\r\n");
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
            break;
          }
        }
      }
      else
      {
         outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
         outToClient.writeBytes ("Content-Length: " + 8 + "\r\n");
         outToClient.writeBytes("invalid\n");
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
       System.out.println(fileName);
       String[] parts = fileName.split("\\?");
       parts = parts[1].split("\\&");
       username = parts[0].substring(8);
       System.out.println("Username: " + username);
       password = parts[1].substring(8);
       System.out.println("Password: " + password);
       //Should be entered into database.
       connectionSocket.close();      
       FileWriter writer = new FileWriter("clientList.txt", true);
       writer.write("User:");
       writer.write(username);
       writer.write("\n");
       writer.write("Password:");
       writer.write(password);
       writer.write("\n");
       writer.close();
       totalUsers++;
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
 
}