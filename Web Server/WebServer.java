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
   String fileName ="";
   Boolean user= false;
   int lineNo = 0;
   List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>(); //Stores all chatrooms created on server.
   

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
   
   Socket connectionSocket = listenSocket.accept();

   // set up the read and write end of the communication socket
   BufferedReader inFromClient = new BufferedReader (
                 new InputStreamReader(connectionSocket.getInputStream()));
   DataOutputStream outToClient = new DataOutputStream (
                 connectionSocket.getOutputStream());

   // retrieve line of request and set up for parsing
   for(;;)
   {
   System.out.println ("Web server waiting for request on port " + myPort);
   while(!requestMessageLine.startsWith("GET "))
   {
     requestMessageLine = inFromClient.readLine();
     System.out.println("requestMessageLine: "+ requestMessageLine);
     //GET requests are the only ones going to the server, so cut out errors.
   }
   System.out.println ("Request: " + requestMessageLine);
   if(requestMessageLine!=null)
   {
   tokenizedLine = new StringTokenizer(requestMessageLine);
   }
   
   if (tokenizedLine.nextToken().equals("GET"))
   {
     //GET requests form most of the requests to server.
     fileName = tokenizedLine.nextToken();
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
     BufferedReader reader = new BufferedReader(new FileReader(file));
     
     requestMessageLine = inFromClient.readLine(); //User.
      System.out.println ("User: " + requestMessageLine);
      username = requestMessageLine;
      requestMessageLine = inFromClient.readLine(); //Password
      System.out.println("Password: " + requestMessageLine);
      password = requestMessageLine;
      
      if (checkUserPass(username, password, reader))
      {
      //if valid when checked against a file, then: 
        System.out.println("User: "+username+" authenticated");
        //Here the server should send the list of users.
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
      
      requestMessageLine = inFromClient.readLine(); //HTTP request
      requestMessageLine = inFromClient.readLine(); //This is the chat room from the client.
      Boolean foundRoom = false;
      totallength = ""; //Reset the string
      for(int i=0;i<chatRoomList.size();i++)
      {
        if(chatRoomList.get(i).name.equals(requestMessageLine))
        {
          foundRoom = true;
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
        chatRoomList.add(new ChatRoom(newClient, requestMessageLine)); //If not, create new chatroom with client as founder.
        totallength="newroom\n";
      }
      outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
      outToClient.writeBytes ("Content-Length: " + totallength.length() + "\r\n");
      outToClient.writeBytes ("\r\n");
      outToClient.writeBytes (totallength);
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
       // Main page, any server can request.
       File file = new File(fileName);
     int numOfBytes = (int) file.length();
     FileInputStream inFile = new FileInputStream (fileName);
     byte[] fileInBytes = new byte[numOfBytes];
     inFile.read(fileInBytes);
     
     outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
      outToClient.writeBytes ("Content-Length: " + numOfBytes + "\r\n");
      outToClient.writeBytes ("\r\n");
      outToClient.write(fileInBytes, 0, numOfBytes);
      addUsersToPage("/index.html");
     }
     
     else if(fileName.startsWith("/submit.html")) //Registration, so some string longer than cf_pass+cf_user+submit.html
     {
       File file = new File("/submit.html");
     int numOfBytes = (int) file.length();
     FileInputStream inFile = new FileInputStream ("/submit.html");
     byte[] fileInBytes = new byte[numOfBytes];
     inFile.read(fileInBytes);
     
      outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
      outToClient.writeBytes ("Content-Length: " + numOfBytes + "\r\n");
      outToClient.writeBytes ("\r\n");
      outToClient.write(fileInBytes, 0, numOfBytes);
     
       System.out.println(fileName);
       String[] parts = fileName.split("\\?");
       parts = parts[1].split("\\&");
       username = parts[0].substring(8);
        System.out.println("Username: " + username);
        password = parts[1].substring(8);
       System.out.println("Password: " + password);
       //Should be entered into database.
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
 
 public static boolean checkUserPass(String username, String password, BufferedReader reader)
    {
        try
        {
          String currentLine;
          int index;
          String user;
          String pass;
          while ((currentLine = reader.readLine()) != null)
          {
            //User:"username"
            index = currentLine.lastIndexOf(':');
            user = currentLine.substring(index+1);
            if ((currentLine = reader.readLine()) == null) break;
            if (!user.equals(username)) continue;
            //Password:"password"
            index = currentLine.lastIndexOf(':');
            pass = currentLine.substring(index+1);
            if (pass.equals(password))
            {
              return true;
            }
          }
          return false;
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        return false;
    }
 
 static void addUsersToPage(String filename)
 {
   BufferedReader br = null;
   FileReader reader = null;
   try
   {
   System.out.println("Attempting to print");
   PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename+".temp")));
   File file = new File(filename); 
   //reader = new FileReader("src/index.html");
   br = new BufferedReader(reader);
   String line;
   while((line = br.readLine())!=null)
   {
     System.out.println(line);
   }
   }
   catch(FileNotFoundException ex)
   {
    System.out.println("File not found."); 
    System.out.println("File was supposed to be: " + filename);
   }
   catch(IOException ex)
   {
     System.out.println("Unable to write to file.");
   }
 }
 
}