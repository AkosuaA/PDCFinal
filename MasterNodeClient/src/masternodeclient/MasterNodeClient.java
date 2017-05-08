/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masternodeclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FEGTTH
 */
public class MasterNodeClient {
public static Socket cSocket;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String fileToBeSent = "C:/Users/FEGTTH/Desktop/test.txt";
        Socket cSocket;
        try {
            cSocket = new Socket("localhost",8001);
            DataInputStream ds = new DataInputStream(cSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(cSocket.getOutputStream());
            String masterResponse = ds.readUTF();
            System.out.println(masterResponse);
            Scanner syst= new Scanner(System.in);
            String command = syst.nextLine();
            if(command.startsWith("Store")){
             String[] details = command.split(" ");
             String fileName = details[2];
             int repFactor = Integer.parseInt(details[1]);
             sendCommand(command, dos);
             String acknowledgement = ds.readUTF();
             System.out.println(acknowledgement);
             if (acknowledgement.equals("MasterNode Ready")){
             sendFile(fileName, repFactor, dos); 
             String confirmation = ds.readUTF();
             System.out.println(confirmation);
             command = syst.nextLine();
             }
            }
        } catch (IOException ex) {
            Logger.getLogger(MasterNodeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void sendCommand(String command, DataOutputStream ds) throws IOException{
        ds.writeUTF(command);
    }
    public static void sendFile(String fileName, int repFactor, DataOutputStream ds) throws IOException{
        File theFile = new File(fileName);
        byte[] arr = new byte[(int)theFile.length()];
        FileInputStream fs = new FileInputStream(fileName);
        while(fs.read(arr)>0){
            ds.write(arr);
        }
        ds.flush();
        fs.close();
    }
    
}
