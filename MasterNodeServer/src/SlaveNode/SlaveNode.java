/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SlaveNode;

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
public class SlaveNode extends Thread{
    public ServerSocket s;
    public int id;
        public SlaveNode(int portNumber, int slaveNumber){
        try {
            s = new ServerSocket(portNumber);
            id = portNumber;
            System.out.println("Slave Node " + slaveNumber +" online");
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(SlaveNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
        @Override
    public void run() {
      while(true){
          try {
              Socket masterSockClient = s.accept();
              DataOutputStream ds = new DataOutputStream(masterSockClient.getOutputStream());
                DataInputStream dis = new DataInputStream(masterSockClient.getInputStream());
                ds.writeUTF("SlaveNode " + (id-8001) + " is listening");
                String command;
                while((command=dis.readUTF())!= null){
                    if (command.startsWith("Store")){
                        ds.writeUTF("SlaveNode " + (id-8001) + " Ready");
                    }
                    if(command.startsWith("Sent")){
                        ds.writeUTF("File has been stored on SlaveNode " + (id-8001) );
                    }
                }
          } catch (IOException ex) {
              Logger.getLogger(SlaveNode.class.getName()).log(Level.SEVERE, null, ex);
          }
      }  
    }
    
    public FileOutputStream createInputStream(){
     File file = new File("C:\\Users\\FEGTTH\\Desktop\\sn"+(id-8001));
     FileOutputStream fs = null;
        if(!file.exists()){
        file.mkdirs();
        }
        File readFile = new File("C:\\Users\\FEGTTH\\Desktop\\sn" +(id-8001)+"\\received.txt");
        try {   
            readFile.createNewFile();
            fs = new FileOutputStream("C:\\Users\\FEGTTH\\Desktop\\sn" +(id-8001)+"\\received.txt");
        } catch (IOException ex) {
            Logger.getLogger(SlaveNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fs;
    }

}
