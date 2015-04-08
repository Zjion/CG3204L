//
//  UDPClient.java
//
//  Bhojan Anand
//
import java.util.*;
import java.net.*;
import java.io.*;

public class UDPClient {
 
 public static void main (String args[]) throws Exception {

  //Use DatagramSocket for UDP connection
  DatagramSocket peerSock = null;
  String destAddr = null;
  DatagramPacket outPkt = null;
  DatagramPacket inPkt = null;
  String userSay = null;
  Map<String, String> clientList = new HashMap<String, String>();
  int port = 9060;
  //int port = Integer.parseInt(args[1]);
  try {
     peerSock = new DatagramSocket(port);
  } 
  catch(SocketException e) {
    System.out.println("Socket cannot be opened or bound");
    System.exit(-1);
  }
  String newLine = System.getProperty("line.separator");
  // Now create a packet (with destination addr and port)
  try {
    System.out.println(args[0]);
    destAddr = args[0];
  }
  catch(ArrayIndexOutOfBoundsException e) {
    System.out.println("There is no destination address to connect");
    System.exit(-1);
  }
  InetAddress addr = InetAddress.getByName(destAddr);
  Scanner input = new Scanner(System.in);
  TCPComm serverComm = new TCPComm(destAddr, clientList);
  serverComm.start();
  System.out.print("Welcome! Please enter your username: ");
  String username = input.nextLine();
  System.out.print("Please enter your password: ");
  String password = input.nextLine();
  //Do server syncing and thingamajigging here. If valid:
  System.out.println("List of chat rooms to join will be printed here, as pulled from server.");
  //Server sends list of chat rooms back
  System.out.print("Enter name of chat room to join: ");
  String chatRoom = input.nextLine();
  //Send chat room name to server
  
  //Server sends back list of clients currently in chat room, or notification that the room does not exist anymore (all have left.)

  //For testing purposes: This should actually be the server's info
clientList.put("192.168.0.110", "b");
clientList.put("192.168.0.111", "c");
  //An alternative method of doing this is to have the server send a complete list of client usernames and ips, then poll every user for their current room. But this is insane, in a way.
  //Client joins chat room by connecting to all clients associated.
  
  System.out.println("Start chatting!"+ newLine);
  //Start listener
  TalkToMe servingMe = new TalkToMe(peerSock, clientList);
  servingMe.start();
  //Start sender, connecting to all clients on the list.
  TalkToYou servingYou = new TalkToYou(peerSock, clientList, 9060, username);
  servingYou.start();
 }
}
