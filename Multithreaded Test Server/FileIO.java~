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
    
    public static boolean checkUserPass(String username, String password)
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
    public static void CloseFileStream() throws IOException
    {
        writer.close();
        reader.close();
        directPrint.close();
    }

}
