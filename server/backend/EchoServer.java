package server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;
import client.common.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //the key used to get/set loginID in the hashmap
  String loginKey = "loginID";
  
  //reference to the server console that allows access to display()
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The server console.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message = (String) msg;
	  //Server echoes the message to itself
	  System.out.println("Message received: " + msg + " from " + client.getInfo(loginKey));
	  
	  //Dealing with login information
	  if(message.startsWith("#login")) {
		  //The client sends #login after their initial login. If #login is received at any other time, 
		  //the server sends an error message back to the client and terminates the connection. 
		  if(client.getInfo(loginKey) != null) {
			  try {
				client.sendToClient("ERROR - #login can only be used as the first command. Terminating client.");
				client.close();
			  } catch (IOException e) {}
		  } else {
			  client.setInfo(loginKey, message.substring(7)); //creates a substring containing the login ID (starting at index 7)
		  }
	  } else {
		  //server sends the message received to all clients with the loginID of the client that sent it
	  	  this.sendToAllClients(client.getInfo(loginKey) + "> " + message); 
	  }

  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * This method handles messages received from the server console.
   * 
   * @param msg The message received from the console.
   */
  public void handleMessageFromServerUI(String msg) {
	  //messages beginning with # are considered a command
	  if(msg.charAt(0) == '#') {
		  handleCommand(msg);
	  }  else {
		  //non-command messages are displayed on the server and client consoles
		  serverUI.display(msg);
		  sendToAllClients("SERVER MESSAGE> " + msg);
	  }
	  
  }
  
  /**
   * This method handles command messages received from the 
   * end-user of the server. 
   * 
   * @param command The specified command.
   */
  
  private void handleCommand(String command) {
	  
	  //terminates the server
	  if(command.equals("#quit")) { 
		  System.exit(0);
		  
		//the server stops listening for connections  
	  } else if(command.equals("#stop")) { 
		  stopListening();
	  
		//the server stops listening and disconnects all clients	  
	  } else if(command.equals("#close")) { 
		  try {
			close();
		  } catch (IOException e) {}
		  
		//sets the port number if the server is closed
	  } else if(command.startsWith("#setport")) {
		  if(isListening() || getNumberOfClients() > 0) {
			  System.out.println("You must close the server before setting a new port.");
			  return;
		  } 
		  
		  //creates a substring containing the port and parses the integer
		  int port = Integer.parseInt(command.substring(9)); 
		  setPort(port);
		
		//starts listening for connections if not already doing so 
	  } else if(command.equals("#start")) {
		  if(isListening()) {
			  System.out.println("Already listening for connections.");
			  return;
		  }
		  try {
			listen();
		  } catch (IOException e) {}
		  
		//retrieves and prints the port number
	  } else if(command.equals("#getport")) {
		  System.out.println(Integer.toString(getPort()));
	  }
  }
  
  //Class methods ***************************************************
  
  
  //Hook methods
  @Override
  protected void clientConnected(ConnectionToClient c) {
	  System.out.println("A new client has connected to the server.");
  }
  @Override
  protected void clientDisconnected(ConnectionToClient c) {
	  System.out.println(c.getInfo(loginKey) + " has disconnected");
  }
  
  @Override
  protected void clientException(ConnectionToClient c, Throwable e) {
	  try {
		c.close();
	  } catch (IOException e1) {}
  }
  
}
//End of EchoServer class
