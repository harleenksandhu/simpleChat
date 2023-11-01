package server.ui;

import java.util.Scanner;

import client.common.*;
import server.backend.*;

public class ServerConsole implements ChatIF {
	
	//default port to connect on
	final public static int DEFAULT_PORT = 5555;
	
	//instance of the server that creates this console
	EchoServer server;
	
	//scanner to read console input
	Scanner fromConsole;
	
	/**
	 * Constructs an instance of the ServerConsole UI
	 * 
	 * @param port The port to connect on
	 */
	public ServerConsole(int port) {
		server = new EchoServer(port, this);
		fromConsole = new Scanner(System.in);
	}
	
    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("SERVER MESSAGE> " + message);
	}

	/**
	 * This method waits for input from the console.  Once it is 
	 * received, it sends it to the client's message handler.
	 */
	public void accept(){
		try{
	      String message;
	      
	      while (true) {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } catch (Exception ex) {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	/**
	 * This method is responsible for the creation of the Server UI
	 *
	 * @param args[0] The port number to listen on.  Defaults to 5555 
	 * 		if no argument is entered.
	 */
	public static void main(String[] args) {
		int port = 0; //Port to listen on

	    try {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    } catch(Throwable t){
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole serverConsole = new ServerConsole(port);
	  
	    try {
	      serverConsole.server.listen(); //Start listening for connections
	      serverConsole.accept(); //Waits for console data
	      
	    } catch (Exception ex) {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	}

}
