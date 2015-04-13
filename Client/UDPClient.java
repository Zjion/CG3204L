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
  Map clientList = new HashMap();
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

  //All the logging on is done in here.
  String[] username={""}; 
  TCPComm serverComm = new TCPComm(destAddr, clientList, username);
  serverComm.start();
    
 
  //For testing purposes: This should actually be the server's info, which would have been entered into clientList by TCPComm
  
//clientList.put("192.168.0.110", "b");
//clientList.put("192.168.0.111", "c");
  
  //An alternative method of doing this is to have the server send a complete list of client usernames and ips, then poll every user for their current room. But this is insane, in a way.
  //Client joins chat room by connecting to all clients associated.
  
  System.out.println("Start chatting!"+ newLine);
  //If it got this far, everything is in order.
  //Start listener
  TalkToMe servingMe = new TalkToMe(peerSock, clientList, 9060, username);
  servingMe.start();
  //Start sender, connecting to all clients on the list.
  TalkToYou servingYou = new TalkToYou(peerSock, clientList, 9060, username);
  servingYou.start();
 }
}
