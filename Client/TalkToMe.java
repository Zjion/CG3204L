import java.io.*;
import java.util.*;
import java.net.*;

class TalkToMe extends Thread {
  private Thread t;
  public byte inBuf[];
  public static int[] ACK = {0};
  public String[] user;
  public String[] ipToSend={""};
  public int portNum;
  public DatagramPacket inPkt;
  public DatagramSocket sock;
  Map<String, String> receiveList;
  
  TalkToMe(DatagramSocket s, Map<String, String> clientList, int portNumber, String[] username) {
    // create a packet buffer to store data from packets received.
    inBuf = new byte[1000];
    inPkt = new DatagramPacket(inBuf, inBuf.length);
    sock = s;
    portNum = portNumber;
    user = username;
    receiveList = clientList;
  }
  
  
  public void run() {
    while( !sock.isClosed()) {
      //receive reply from others
      try {
        sock.receive(inPkt);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      // convert reply to string
      
      String reply = new String(inPkt.getData(), 0, inPkt.getLength());
      String ipReceived = inPkt.getAddress().toString().substring(1);
      ipToSend[0] = ipReceived;
      String[] partition = reply.split(":", 2);
      if (receiveList.containsKey(ipReceived)) //If it already exists, check if username is the same.
      {
        if(!receiveList.get(ipReceived).equals(partition[0]))
        {
          System.out.println(receiveList.get(ipReceived) + " has changed name to " + partition[0]); //Notify the user of the change in name
          receiveList.put(ipReceived, partition[0]); //Update the username
        }
      }
      else //In the event that the other client does not send the .notify message.
      {
        System.out.println(partition[0] + " has joined the chatroom with IP "+ ipReceived);
        receiveList.put(ipReceived, partition[0]);
      }
      if(partition[1].equals(" .signoff"))
      {
        System.out.println(partition[0] + " has signed off.");
        receiveList.remove(ipReceived);
      }
      if(!partition[1].equals(" .notify") && !partition[1].equals(" .signoff") && !partition[1].equals(" .ACK"))
      {
      System.out.println(reply);
      ACK[0]++;
      }
      if(partition[1].equals(" .ACK"))
      {
        String realName=receiveList.get(ipReceived);
        System.out.println("Acknowledged by " + realName + " (" + ipReceived + ")");
      }
    }
  }
  
  public void start() {
    System.out.println("Start receiving");
    if (t == null)
    {
      t = new Thread(this);
      t.start();
    }
    AckSender ackSend = new AckSender(sock, receiveList, portNum, user, ACK, ipToSend); 
    ackSend.start();
  }
}