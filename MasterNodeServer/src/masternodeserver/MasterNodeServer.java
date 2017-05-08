/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masternodeserver;

import SlaveNode.SlaveNode;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FEGTTH
 */
public class MasterNodeServer extends Thread {
       ServerSocket masterSocket;
       String hostName = "localhost";
       int sn1Port = 8002;
       int sn2Port = 8003;
       int sn3Port = 8004;
       SlaveNode sn1;
       SlaveNode sn2;
       SlaveNode sn3;
       

    public MasterNodeServer() throws IOException {
        this.sn1 = new SlaveNode(sn1Port,1);
        this.sn2 = new SlaveNode(sn2Port,2);
        this.sn3 = new SlaveNode(sn3Port,3);
        this.masterSocket = new ServerSocket(8001);
        this.start();
    }
    
    public void saveFile(DataInputStream d, DataOutputStream ds, int rf) throws IOException{
        
        switch (rf){
            case 2:
                Socket sn1Socket = new Socket(hostName,sn1Port);
                DataInputStream sn1In = new DataInputStream(sn1Socket.getInputStream());
                DataOutputStream sn1Out = new DataOutputStream(sn1Socket.getOutputStream());
                String slaveResponse1 = sn1In.readUTF();
                System.out.println(slaveResponse1);
                sn1Out.writeUTF("Store");
                slaveResponse1 = sn1In.readUTF();
                System.out.println(slaveResponse1);
                Socket sn2Socket = new Socket(hostName, sn2Port);
                DataInputStream sn2In = new DataInputStream(sn2Socket.getInputStream());
                DataOutputStream sn2Out = new DataOutputStream(sn2Socket.getOutputStream());
                String slaveResponse2 = sn2In.readUTF();
                System.out.println(slaveResponse2);
                sn2Out.writeUTF("Store");
                slaveResponse2 = sn2In.readUTF();
                System.out.println(slaveResponse2);
                FileOutputStream fs1 = sn1.createInputStream();
                FileOutputStream fs2 = sn2.createInputStream();
                int totalRead =0;
                int curr = 0;
                int remaining = 0;
           try {
               int count = d.available();
               remaining = count;
               byte[]reader = new byte[count]; 
               while((curr= d.read(reader,0, Math.min(reader.length,remaining)))> 0){
                   totalRead += curr;
                   remaining -= curr;
                   fs1.write(reader,0,curr);
                   fs2.write(reader,0,curr);
               }
               fs1.close();
               fs2.close();
               sn1Out.writeUTF("Sent");
               slaveResponse1 = sn1In.readUTF();
               System.out.println(slaveResponse1);
               sn2Out.writeUTF("Sent");
               slaveResponse2 = sn2In.readUTF();
               System.out.println(slaveResponse2);
           } catch (FileNotFoundException ex) {
               Logger.getLogger(MasterNodeServer.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(MasterNodeServer.class.getName()).log(Level.SEVERE, null, ex);
           }
        }       
    }
       @Override
    public void run(){
        while (true){
            try {
                Socket clientSocket = masterSocket.accept();
                DataOutputStream ds = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                ds.writeUTF("MasterNode is listening");
                String command;
                while((command=dis.readUTF())!= null){
                    if (command.startsWith("Store")){
                        String details[] = command.split(" ");
                        int repFactor= Integer.parseInt(details[1]);
                        ds.writeUTF("MasterNode Ready");
                        saveFile(dis,ds,repFactor);
                        ds.writeUTF("File has been stored");
                    }
                }
                
            }
            catch (IOException e){
                System.out.println("Failed to establish connection with client!");
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
           try {
               // TODO code application logic here
               MasterNodeServer mns = new MasterNodeServer();
           } catch (IOException ex) {
               Logger.getLogger(MasterNodeServer.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
    
}
