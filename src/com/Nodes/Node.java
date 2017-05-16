package com.Nodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Node {
	ServerSocket listener;
	int sucx;
	int id ;
	public Node(int serverport,int id,int successor ){
		try {
			//intialize the port
			listener = new ServerSocket(serverport);
			//run forever
			sucx= successor;
			this.id = id;
			while (true) {
				Socket clientSocket = listener.accept();
			    System. out.println("Election message received");
			    Connection c = new Connection(clientSocket,successor,id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void beginElection(){
		Socket s;
		try {
			s = new Socket ("localhost", sucx);
			DataOutputStream out = new DataOutputStream ( s.getOutputStream());
			  //1 for elected 
			    //2 for election
			    int typeOfElection =2;
			    out.writeUTF (typeOfElection+","+id); 
				System.out.println("Sent data: " + typeOfElection+","+id);	  
				s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread( new  Runnable() {
			public void run() {
				
			}
		}).start();
	    
	}
	
	
}
class Connection extends Thread {
	  DataInputStream in;
	  DataOutputStream out;
	  Socket clientSocket;
	  int successor ;
	  int id;

	  public Connection (Socket aClientSocket,int succesor,int processID) {
	    try {
	      clientSocket = aClientSocket;
	      out = new DataOutputStream ( clientSocket.getOutputStream() );
	      in = new DataInputStream ( clientSocket.getInputStream() );
	      this.successor = succesor;
	      this.id= processID;
	      this.start();
	    } catch( IOException e) {System. out. println(" Connection:"+ e.getMessage());}
	  }

	  public void run(){
	    try {
	      String data = in.readUTF ();
	      String[] params =data.split(",");
		  //out.writeUTF(data);
	      int receivedID = Integer.parseInt(params[1]);
	     
	      int electedId = id;
	      //1 for elected message
	      //2 for election message
	      int typeOfElection = Integer.parseInt(params[0]);;
	      
	      if (typeOfElection==2) {
			if (receivedID == id) {
				//election is over 
				typeOfElection = 1;
			} else if (receivedID > electedId) {
				typeOfElection = 2;
				electedId = receivedID;
			} 
		}
		Socket s = new Socket ("localhost", successor);
	      DataOutputStream out = new DataOutputStream ( s.getOutputStream());
	      out.writeUTF (typeOfElection+","+electedId); 
		  System.out.println("Sent data: " + typeOfElection+","+electedId);	  

	      clientSocket.close();
		  s.close();
	    } catch( EOFException e) {System.out.println(" EOF:"+ e.getMessage());
	    } catch( IOException e) {System.out.println(" IO:"+ e.getMessage());}
	  }
	}
