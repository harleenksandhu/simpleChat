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
  
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    //openConnection();
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
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if(message.charAt(0) == '#') {
    		handleCommand(message);
    	} else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles a command from the client
   */
  private void handleCommand(String command) {
	  if(command.equals("#quit")) {
		  quit();
	  } else if(command.equals("#logoff")) {
		  try {
			closeConnection();
		  } catch (IOException e) {}
	  } else if(command.startsWith("#sethost")) {
		  if(isConnected()) {
			  clientUI.display("You must log off before setting another host.");
			  return;
		  }
		  String host = command.substring(10, command.length()-1); //creates a substring of what is in between < and >
		  setHost(host);
	  } else if(command.startsWith("#setport")) {
		  if(isConnected()) {
			  clientUI.display("You must log off before setting another port.");
			  return;
		  }
		  int port = Integer.parseInt(command.substring(10, command.length()-1));
		  setPort(port);
	  } else if(command.equals("#login")) {
		  if(isConnected()) {
			  clientUI.display("You are already logged in.");
			  return;
		  }
		  try {
			openConnection();
		  } catch (IOException e) {}
	  } else if(command.equals("#gethost")) {
		  clientUI.display(getHost());
	  } else if(command.equals("#getport")) {
		  clientUI.display(Integer.toString(getPort()));
	  }
  }
  
  public void setLoginID(String loginID) {
	  this.loginID = loginID;
  }
  public String getLoginID() {
	  return loginID;
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
	  clientUI.display("The server has shut down. Quitting...");
	  quit(); 
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
  }
  

}
//End of ChatClient class
