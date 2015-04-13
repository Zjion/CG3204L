import java.io.*;
import java.net.*;
import java.util.*;

class TCPComm extends Thread{
  private Thread t;
  InetAddress ip;
  Socket socket;
  PrintWriter out;
  BufferedReader in;
  String[] username;
  String password;
  String inputChatRoom;
  Boolean approved = false;
  Boolean validInput = false;
  Boolean dontStoreNextIP = false;
  int[] sgn;
  Map<String, String> TCPList;
  public TCPComm(String IP, Map<String, String> clientList, String[] mainUser)
  {
  try
  {
  username = mainUser;
  ip = InetAddress.getByName(IP);
  socket = new Socket(ip, 80);
  TCPList = clientList;
  out = new PrintWriter(socket.getOutputStream(), true);
  in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
  }
  
  public void run()
  {
    while(!socket.isClosed())
    {try
      {
      //Any errant output from server.
      String line = in.readLine();
      System.out.println(line);
      }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    }
  }
  
  public void start()
  {
    System.out.print("Enter username. ");
    Scanner input = new Scanner(System.in);
    username[0] = input.nextLine();
    System.out.print("Enter password. ");
    password = input.nextLine();
    System.out.println("Connecting to server...");
    out.println("GET /clientList.txt HTTP/1.1");
    out.println(username[0]);
    out.println(password); //Last send to server should not have newline to prevent unlikely bugs
    try
    {
      System.out.println("Receiving stuff.");
      String line="";
      for(int i=0;i<3;i++)
      {
      line = in.readLine(); //Ignore the first two lines and grab status
      }
      if(line.equals("invalid"))
      {
        System.out.println("Invalid username or password.");
        System.exit(0);
      }
      else
      {
        System.out.println(line); //Third line was correct, but isn't this just blank? Leave it for now.
        approved = true;
        System.out.println("List of chat rooms: ");
    while((line = in.readLine())!=null) //Processing list of users.
        {
      if(!line.equals("!EOC"))
      {
          System.out.println(line);
      }   
          if(line.equals("!EOC"))
          {
            System.out.println("--End of Chat Room List--");
            break;
          }
        }
    System.out.println("List of users: ");
    while((line = in.readLine())!=null)
    {
      if(!line.equals("!EOU"))
      {
      System.out.println(line);
      }
      if(line.equals("!EOU"))
      {
        System.out.println("--End of User List--");
        break;
      }
    }
    while(!validInput)
    {
      System.out.println("Enter the name of the chat room to join; If it does not exist, one will be created.");
      inputChatRoom = input.nextLine();
      if(!inputChatRoom.startsWith("!") && !inputChatRoom.trim().equals(""))
      {
        validInput = true;
      }
    }
     System.out.println("Joining room " + inputChatRoom);
     out.println("GET /joinRoom HTTP/1.1");
     out.println(inputChatRoom);
     line=in.readLine(); //HTTP
     line=in.readLine(); //length
     line=in.readLine(); //space
      line=in.readLine();
      if(line.equals("users"))
      {
        String storeUser="";
        String storeIP="";
        System.out.println("List of users in "+inputChatRoom+":");
        Boolean isUser = true;
        while((line = in.readLine())!=null)
        {
           if(line.equals("!EOUC"))
          {
            break;
          }
          else
          {  
          if(isUser == true)
          {
            System.out.print("User: ");
          System.out.println(line);
          if(!line.equals(username[0]))
          {
          storeUser = line;
          }
          else
          {
            dontStoreNextIP = true;
          }
            isUser = false;
          }
          else if(isUser == false)
          {
            System.out.print("IP: ");
            System.out.println(line);
            if(dontStoreNextIP == false)
            {
            storeIP = line;
            TCPList.put(storeIP, storeUser);
            }
            else
            {
              dontStoreNextIP = true;
            }
            isUser = true; //Flipflop them to know which, as these will always be in pairs.
          }
          }
        }
        
      }
      else if(line.equals("newroom"))
      {
        System.out.println(inputChatRoom + " is empty.");
      }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
}