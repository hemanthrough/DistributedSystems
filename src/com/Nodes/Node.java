package com.Nodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Node extends Thread {
	ServerSocket listener;
	int sucx;
	
	final int id ;
	final int successor;
	final int serverport;
	int coordinator = 0;
	Socket s;
	public Node(int serverport,int id,int successor ){
		this.serverport = serverport;
		this.successor = successor;
		this.id = id;
		startDemon();
		sucx = successor;
	}
	
	
	//starts the election
	void beginElection(){
		
		try {
			s = new Socket ("localhost", sucx);
			DataOutputStream out = new DataOutputStream ( s.getOutputStream());
			  //2 for election
			    int typeOfElection =2;
			    out.writeUTF (typeOfElection+","+id); 
				System.out.println("pid "+id +"Sent data: " + typeOfElection+","+id);	  
				s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
	}
	//start listening 
	public void startDemon(){
		this.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//intialize the port
			listener = new ServerSocket(serverport);
			//run forever
			while (true) {
				//System.out.println("start listening " +this.id);
				Socket clientSocket = listener.accept();
			    ///System. out.println("Election message received");
			    Connection c = new Connection(clientSocket,successor,id,this);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void setCordinator(int selected_cordinator){
		coordinator = selected_cordinator;
	}
	
}
class Connection extends Thread {
	  DataInputStream in;
	  DataOutputStream out;
	  Socket clientSocket;
	  int successor ;
	  int id;
	  final Node currentNode;

	  public Connection (Socket aClientSocket,int succesor,int processID,Node sender) {
		  currentNode =sender;
		  try {
			  //init next node
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
		  //get participant or coorinator details 
	      int participant = Integer.parseInt(params[1]);
	     // to itself for comparison
	      int id2BeTramsmitted = id;
	      //1 for elected message
	      //2 for election message
	      int typeOfElection = Integer.parseInt(params[0]);;
	      
	      if (typeOfElection==2) {
	    	  // if election
			if (participant == id) {
				System.out.println("elction is over starting elected message"+id );
				//election is over 
				//chooses itself as coordinator and starts elected message
				typeOfElection = 1;

				currentNode.setCordinator(participant);
				
			} else if (participant > id2BeTramsmitted) {
				// if the sender has higher id chage the participant
				
				typeOfElection = 2;
				id2BeTramsmitted = participant;
				System.out.println("sending election message"+id +"sent id data "+id2BeTramsmitted);
			} 
		}
	      //if elected message
	      else if(typeOfElection ==1){
	    	  //if its turns around it stops broadcast
	    	  if (participant == id) {
					//election broadcast is over stop broadcast 
	    		  System.out.println("stoping the elected message");	
	    		  this.stop();
	    		  	
				}
	    	  //set the coordinator transfer to other nodes
	    	  else{
	    		  id2BeTramsmitted = participant;
	    		  typeOfElection = 1;
	    		  currentNode.setCordinator(participant);
	    		  System.out.println("pid "+id +"changing cordinator to "+participant);
	    	  }
	      }
		Socket s = new Socket ("localhost", successor);
	      DataOutputStream out = new DataOutputStream ( s.getOutputStream());
	      out.writeUTF (typeOfElection+","+id2BeTramsmitted); 
		  System.out.println("PID " +id +"Sending data: " + typeOfElection+","+id2BeTramsmitted);	  

	      clientSocket.close();
		  s.close();
	    } catch( EOFException e) {System.out.println(" EOF:"+ e.getMessage());
	    } catch( IOException e) {System.out.println(" IO:"+ e.getMessage());}
	  }
	}
