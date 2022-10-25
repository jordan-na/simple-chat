// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import java.io.IOException;
import ocsf.*;
import common.ChatIF;
/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	
	/**
	 * The login id of the client
	 */
	private String loginId;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String loginId, String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		this.loginId = loginId;
		openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromClientUI(String message) {
		try {
			String[] commands = message.split(" ");
			if (commands[0].charAt(0) == '#') {
				switch (commands[0]) {
					case "#quit":
						quit();
						break;
					case "#logoff":
						if(isConnected()) closeConnection();
						else clientUI.display("Client is already logged off (disconnected from server)");
						break;
					case "#sethost":
						if(commands.length != 2) {
							clientUI.display("Please follow the syntax #sethost <host>");
						} else {
							if(!isConnected()) { 
								setHost(commands[1]);	
								clientUI.display("Host set to " + commands[1]);
							} else { 
								clientUI.display("Client needs to be logged off (disconnected from server) to set host");
							}
						}
						break;
					case "#setport":
						if(commands.length != 2) {
							clientUI.display("Please follow the syntax #setport <port>");
						} else {
							long port;
							try {
								port = Integer.parseInt(commands[1]);
								if(port < 0 || port > 65535) {
									clientUI.display("Port number must be between 0 and 65535 (inclusive)");
								} else {
									if(!isConnected()) {
										setPort((int) port);
										clientUI.display("Port number set to " + (int) port);
									} else { 
										clientUI.display("Client needs to be logged off (disconnected from server) to set port");
									}
								}
							} catch(NumberFormatException e) {
								clientUI.display("Port number must only contain digits [0-9]");
							}
						}
						break;
					case "#login":
						if(!isConnected()) openConnection();
						else clientUI.display("Client is already logged in (connected to server)");
						break;
					case "#gethost":
						clientUI.display(getHost());
						break;
					case "#getport":
						clientUI.display(Integer.toString(getPort()));
						break;
					default:
						clientUI.display("Command " + message + " not found");
				}
			} else {
				sendToServer(message);
			}
		} catch (IOException e) {
			clientUI.display("Could not send message to server.  Terminating client.");
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
			clientUI.display("Client terminated");
		} catch (IOException e) {
			clientUI.display("Error quitting client");
		}
		System.exit(0);
	}
	
	@Override
	public void connectionEstablished() {
		clientUI.display("Connection to server established");
		try {
			sendToServer("#login " + loginId);
		} catch(IOException e) {
			clientUI.display("Could not send message to server.  Terminating client.");
			quit();
		}
		
	}


	@Override
	public void connectionClosed() {
		clientUI.display("Connection to server closed");
	}

	@Override
	public void connectionException(Exception exception) {
		clientUI.display("The server has shut down");
		quit();
	}

}
//End of ChatClient class
