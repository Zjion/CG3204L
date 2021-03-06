import java.io.*;
import java.util.*;
import java.net.*;

class AckSender extends Thread {
  private Thread t;
  private static String closeConnection = ".signoff";
  private static String messageError = "Message not sent. Please try again.";
  private static String quit = "has quit!";
  public String[] user;
  public String[] targetIP;
  public byte outBuf[];
  public int port;
  public int[] ackHolder;
  public volatile boolean running = true;
  InetAddress ipAddr;
  public DatagramSocket sock;
  Map<String, String> sendList;
  
  AckSender(DatagramSocket s, Map<String, String> clientList, int portnumber, String[] username, int[] ack, String[] ipToSend) {
    // create a packet buffer to store data from packets received.
    outBuf = new byte[1000];
    port = portnumber;
    sock = s;
    user = username;
    sendList = clientList;
    ackHolder = ack;
    targetIP = ipToSend;
  }
  
  public void run() {
    while(running)
    {
     if(ackHolder[0]>0)
     {
       sendMessage(".ACK", targetIP[0]);
       --ackHolder[0];
     }
    }
  }
  
  public boolean sendMessage(String message, String IP){
    //Thus, any message without : in it can be a control message.
     message = user[0] + ": " + message;
    outBuf = message.getBytes();
    //send message to others
    try {
      DatagramPacket outPkt = new DatagramPacket(outBuf,
    outBuf.length, InetAddress.getByName(IP), port);
      sock.send(outPkt);
    }
    catch(IOException e) {
      System.out.println("Unable to send packet out!");
      return false;
    }
    return true;
  }

  public void start() {
    System.out.println("Start sending");
    if (t == null)
    {
      t = new Thread(this);
      t.start();
    }
  }
}