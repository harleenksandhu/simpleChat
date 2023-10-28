package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;
import edu.seg2105.client.common.*;
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
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
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
  
  public void handleMessageFromServerUI(String msg) {
	  if(msg.charAt(0) == '#') {
		  handleCommand(msg);
	  }  else {
		  serverUI.display(msg);
		  sendToAllClients("SERVER MSG> " + msg);
	  }
	  
  }
  
  private void handleCommand(String command) {
	  if(command.equals("#quit")) {
		  System.exit(0);
	  } else if(command.equals("#stop")) {
		  stopListening();
	  } else if(command.equals("#close")) {
		  try {
			close();
		  } catch (IOException e) {}
	  } else if(command.startsWith("#setport")) {
		  if(isListening()) {
			  System.out.println("You must close the server before setting a new port.");
			  return;
		  }
		  int port = Integer.parseInt(command.substring(10, command.length()-1));
		  setPort(port);
	  } else if(command.equals("#start")) {
		  if(isListening()) {
			  System.out.println("Already listening for connections.");
		  } else {
			  try {
				listen();
			  } catch (IOException e) {}
		  }
	  } else if(command.equals("#getport")) {
		  System.out.println(Integer.toString(getPort()));
	  }
  }
  
  //Class methods ***************************************************
  
  
  //Hook methods
  @Override
  protected void clientConnected(ConnectionToClient c) {
	  System.out.println("Client " + c.toString() + " has connected!");
  }
  @Override
  protected void clientDisconnected(ConnectionToClient c) {
	  System.out.println("Client has disconnected");
  }
  
  @Override
  protected void clientException(ConnectionToClient c, Throwable e) {
	  System.out.println("Client is disconnecting...");
	  try {
		c.close();
	  } catch (IOException e1) {}
  }
  
}
//End of EchoServer class
