// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  //loginID of the user
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message){
	
	//messages beginning with # are considered a command  
	if(message.charAt(0) == '#') {
  		handleCommand(message);
  		
  	  //if not a command, send the message to the server
  	} else {
	    try {
	    	sendToServer(message);
	    } catch(IOException e){
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
  	}
  }
  
  /**
   * This method handles a commands received from the client.
   * 
   * @param command The specified command.
   */
  private void handleCommand(String command) {
	  
	  //terminates the client
	  if(command.equals("#quit")) {
		 quit();
		 
		//disconnects the client from the server
	  } else if(command.equals("#logoff")) {
		  try {
			closeConnection();
		  } catch (IOException e) {}
		  
		//sets the host if the client is logged off  
	  } else if(command.startsWith("#sethost")) {
		  if(isConnected()) {
			  clientUI.display("You must log off before setting another host.");
			  return;
		  }
		  
		  //creates a substring containing the host name (starting from index 9)
		  String host = command.substring(9); 
		  setHost(host);
		  
		//sets the port number if the client is logged off
	  } else if(command.startsWith("#setport")) {
		  if(isConnected()) {
			  clientUI.display("You must log off before setting another port.");
			  return;
		  }
		  
		  //creates a substring containing the port number and parsing the integer 
		  int port = Integer.parseInt(command.substring(9)); 
		  setPort(port);
		
		//connects the client to the server if they are not already connected
	  } else if(command.equals("#login")) {
		  if(isConnected()) {
			  clientUI.display("You are already logged in.");
			  return;
		  }
		  
		  try {
			openConnection();
		  } catch (IOException e) {}
		  
		//retrieves and displays the host name 
	  } else if(command.equals("#gethost")) {
		  clientUI.display(getHost());
		  
		//retrieves and displays the port number
	  } else if(command.equals("#getport")) {
		  clientUI.display(Integer.toString(getPort()));
	  }
  }
  
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
  //Hook methods 
  @Override
  protected void connectionException(Exception e) {
	  clientUI.display("The server has shut down.");
	  System.exit(0);
  }
  
  @Override
  protected void connectionClosed() {
	  clientUI.display("Your connection has closed.");
  }
  
  @Override
  protected void connectionEstablished() {
	  String message = "#login " + loginID;
	  try {
		sendToServer(message);
	  } catch (IOException e) {}
	  System.out.println(loginID + " has logged on.");
	  
  }
  

}
//End of ChatClient class
