/**
 * Created by anthonywo on 18/3/15.
 */

import java.io.*;
import java.net.*;

public class TCPServer
{
    static FileIO fileIO = new FileIO();
    static String filename = "/Users/anthonywo/IdeaProjects/TCPServer/src/user.txt";
    static String buffername ="/Users/anthonywo/IdeaProjects/TCPServer/src/buffername.txt";
    ServerSocket welcomeSocket;
    boolean serverStatus = true;

    //constructor for TCPServer()
    public TCPServer()
    {
        try
        {
            //bind socket to port 8002
            welcomeSocket = new ServerSocket(8002);
        }
        catch(IOException ioe)
        {
            System.out.println("Could not create socket. System Exiting");
            System.exit(-1);
        }
        while(serverStatus)
        {
            try
            {
                //listen and accept incoming client connection
                Socket incomingClientSocket = welcomeSocket.accept();
                //implement multithreading here
                ServiceThread serviceThread = new ServiceThread(incomingClientSocket);
                serviceThread.start();

            }
            catch(IOException ioe)
            {
                System.out.println("Error in accepting incoming connection.");
                ioe.printStackTrace();
            }
        }
        //server close command received, proceed to close server
        try
        {
            welcomeSocket.close();
            System.out.println("Server Closed.");
        }
        catch(IOException ioe)
        {
            System.out.println("Error in closing socket.");
            System.exit(-1);
        }

    }

    public static void main(String[] args)
    {
        new TCPServer();
    }

    //class ServiceThread defined to operate individual connection threads
    class ServiceThread extends Thread
    {
        Socket clientSocket;
        boolean inServiceThread = true;

        //constructor to call parent constructor
        public ServiceThread()
        {
            super();
        }

        //define
        ServiceThread(Socket s)
        {
            clientSocket = s;
        }

        public void run()
        {
            BufferedReader in = null;
            PrintWriter out = null;
            String username, ipaddress;

            System.out.println("New user connecting from: " + clientSocket.getInetAddress().getHostAddress());

            try
            {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                while(inServiceThread)
                {
                    fileIO.OpenFileStream(filename,buffername);
                    String clientInput = in.readLine();
                    System.out.println("Username: " + clientInput);
                    fileIO.NewEntry(clientInput, clientSocket.getInetAddress().getHostAddress());
                    System.out.println("\n");
                    System.out.println("List of connected users.");
                    fileIO.PrintEntry(out);

                    if(!serverStatus)
                    {
                        System.out.println("Server has stopped");
                        out.println("Server has stopped");
                        out.flush();
                        inServiceThread = false;
                    }

                    if(clientInput.equalsIgnoreCase("quit")) //quit
                    {
                        inServiceThread = false;
                        System.out.println("Client quitting without disconnect");
                    }
                    else if(clientInput.equalsIgnoreCase("disconnect"))
                    {
                        fileIO.DeleteEntry(clientSocket.getInetAddress().getHostAddress());
                        inServiceThread = false;
                        System.out.println("Stopping client thread and closing server");
                        serverStatus = false;
                    }

                    else
                    {
                        out.println("Server says: " + clientInput);
                        out.flush();
                    }
                    try
                    {
                        fileIO.CloseFileStream();
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                }
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }

            finally
            {
                try
                {
                    fileIO.CloseFileStream();
                    clientSocket.close();
                    System.out.println("Server stopped");
                }
                catch(IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }

    }

}
