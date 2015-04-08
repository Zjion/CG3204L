import java.io.*;
import java.net.*;

public class TCPClient {
    // Constructor
    public TCPClient() { }

    // Builds GET request, opens socket, waits for response, closes
    public static void main(String[] args) throws Exception{
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //For each hostname
            line = br.readLine(); 
            //Resolve the hostname to an IP address
            InetAddress ip = InetAddress.getByName(line);

            //Open socket on ip address
            //Socket socket = new Socket(ip, 80);  <==port 80 for http
            //Open socket for testing. run on port 9000
            Socket socket = new Socket(ip, 9000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send request
            out.println("GET /clientList.txt HTTP/1.1");
            out.println("guest");
            System.out.println("Sent username.");
            out.println("hunter2");
            System.out.println("Sent password.");
            
            String response = null;
            //Read one line of input
            while((response = in.readLine())!=null)
            {
            System.out.println("Response from "+line+": "+response);
            }
            System.out.println("done with output.");
               
}
}