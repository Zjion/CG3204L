/**
 * Created by anthonywo on 19/3/15.
 */

import java.io.*;
import java.io.File;

public class FileIO
{
    private static File userFile;
    private static File bufferFile;
    private static FileReader fileScan = null;
    private static FileWriter bufferPrint = null;

    private static BufferedReader reader = null; //for filescan
    private static BufferedWriter writer = null; //for bufferprint

    private static FileWriter in = null;
    private static FileWriter directPrint = null;

    private static String fileName;
    private static String bufferName;
    private static String userName;
    private static String ipAddress;

    private static String newLine = System.getProperty("line.separator");
    public FileIO(String filename, String buffername) throws IOException
    {
        fileName = filename;
        bufferName = buffername;

        userFile = new File(fileName);
        bufferFile = new File(bufferName);

        fileScan = new FileReader(userFile);
        bufferPrint = new FileWriter(bufferFile);
        directPrint = new FileWriter(userFile, true);

        reader = new BufferedReader(fileScan);
        writer = new BufferedWriter(bufferPrint);

    }

    public static void DeleteEntry(String ipaddress)
    {
        String lineToRemove = ipaddress;
        String currentLine;
        int index, length;
        try {
            while ((currentLine = reader.readLine()) != null)
            {
                //find last whitespace
                index = currentLine.lastIndexOf(' ');
                System.out.println(index);
                length = currentLine.length();
                System.out.println(length);
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.substring(index+1, length);
                if (trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine + newLine);
            }
            CloseFileStream();
            boolean successful = bufferFile.renameTo(userFile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void NewEntry(String username, String password)
    {
        try
        {
            directPrint.write("User:");
            directPrint.write(username);
            directPrint.write(newLine);
            directPrint.write("Password:");
            directPrint.write(password);
            directPrint.write(newLine);
            //CloseFileStream();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    public boolean checkUserPass(String username, String password)
    {
        try
        {
          userFile = new File(fileName);
          fileScan = new FileReader(userFile);
          reader = new BufferedReader(fileScan);
          //open file
          String currentLine;
          int index;
          String user;
          String pass;
          while ((currentLine = reader.readLine()) != null)
          {
            //User:"username"
            index = currentLine.lastIndexOf(':');
            user = currentLine.substring(index+1);
            if (!user.equals(username)) 
            {
              continue;
            }
            if ((currentLine = reader.readLine()) == null) break;
            //Password:"password"
            index = currentLine.lastIndexOf(':');
            pass = currentLine.substring(index+1);
            if (pass.equals(password))
            {
              reader.close();
              return true;
            }
          }
          reader.close();
          return false;
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        return false;
    }
    
    public boolean checkUserRegister(String username, String password)
    {
        try
        {
          userFile = new File(fileName);
          fileScan = new FileReader(userFile);
          reader = new BufferedReader(fileScan);
          //open file
          String currentLine;
          int index;
          String user;
          String pass;
          while ((currentLine = reader.readLine()) != null)
          {
            //User:"username"
            user = currentLine.substring(currentLine.lastIndexOf(':')+1);
            System.out.println(user);
            if (user.equals(username)) 
            {
              reader.close();
              return false;
            }
            if ((currentLine = reader.readLine()) == null) break;
            //Password:"password"
            pass = currentLine.substring(currentLine.lastIndexOf(':')+1);
            if (pass.equals(password))
            {
              reader.close();
              return true;
            }
          }
          reader.close();
          return false;
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        return false;
    }
    public static int retrieveClients()
    {
      int count = 0;
      String first = "";
      String second = "";
      String currentLine;
      try
      {
        userFile = new File(fileName);
        fileScan = new FileReader(userFile);
        reader = new BufferedReader(fileScan);
        while ((first = reader.readLine()) != null)
        {
          if((second = reader.readLine()) == null) break;
          if(first.startsWith("User:") && second.startsWith("Password:")) 
          {
            count++;
          }
          else return count;
        }
        return count;
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
      }      
      return count;
    }
    
    public static void CloseFileStream() throws IOException
    {
        writer.close();
        reader.close();
        directPrint.close();
    }

}
