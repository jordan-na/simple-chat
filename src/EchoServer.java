// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import ocsf.*;
import common.ChatIF;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatIF serverUI;

	/**
	 * Indicates if the server is stopped. Set to true by default.
	 */
	private boolean stopped = true;
	
	/**
	 * Indicates if the server is closed. Set to true by default.
	 */
	private boolean closed = true;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);
	}

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port     The port number to connect on.
	 * @param serverUI The port number to connect on.
	 */
	public EchoServer(int port, ChatIF serverUI) {
		super(port);
		this.serverUI = serverUI;
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String message = (String) msg;
		String[] messages = message.split(" ");
		if(messages[0].equals("#login")) {
			if(client.getInfo("id") != null) {
				try {
					client.sendToClient("Command #login is only allowed to be first command after client connects");
					client.close();	
				} catch(IOException e) {
					serverUI.display("Error");
				}
			} else {
				if(messages.length != 2) {
					serverUI.display("Login command must follow #login <loginid> syntax");
				} else {
					client.setInfo("id", messages[1]);
					System.out.println("Message received: " + msg + " from " + client.getInfo("id"));
					System.out.println(client.getInfo("id") + " has logged on");
					this.sendToAllClients(client.getInfo("id") + " has logged on");
				}
			}
		} else {
			System.out.println("Message received: " + msg + " from " + client.getInfo("id"));
			this.sendToAllClients(client.getInfo("id") + "> " + msg);	
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromServerUI(String message) {
		String[] commands = message.split(" ");
		if (commands[0].charAt(0) == '#') {
			switch (commands[0]) {
			case "#quit":
				try {
					close();
					serverUI.display("Server terminated");
				} catch (IOException e) {
					serverUI.display("Error quitting server");
				}
				System.exit(1);
				break;
			case "#stop":
				stopListening();
				break;
			case "#close":
				try {
					close();
				} catch (IOException e) {
					serverUI.display("Error closing server");
				}
				break;
			case "#setport":
				if(commands.length != 2) {
					serverUI.display("Please follow the syntax #setport <port>");
				} else {
					long port;
					try {
						port = Integer.parseInt(commands[1]);
						if(port < 0 || port > 65535) {
							serverUI.display("Port number must be between 0 and 65535 (inclusive)");
						} else {
							if(closed) {
								setPort((int) port);
								serverUI.display("Port number set to " + (int) port);
							} else { 
								serverUI.display("Server needs to be closed to set port");
							}
						}
					} catch(NumberFormatException e) {
						serverUI.display("Port number must only contain digits [0-9]");
					}
				}
				break;
			case "#start":
				if(stopped) {
					try {
						listen();
					} catch(IOException e) {
						serverUI.display("Error starting server");
					}
				} else {
					serverUI.display("Server has already been started");
				}
				break;
			case "#getport":
				serverUI.display(Integer.toString(getPort()));
				break;
			default:
				serverUI.display("Command " + message + " not found");
			}
		} else {
			message = "SERVER MSG> " + message;
			sendToAllClients(message);
			serverUI.display(message);
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
		stopped = false;
		closed = false;
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
		stopped = true;
	}
	
	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	@Override
	protected void serverClosed() {
//		System.out.println("Server has closed all connections to clients.");
		closed = true;
	}

	@Override
	public void clientConnected(ConnectionToClient client) {
		System.out.println("A new client has connected to the server.");
	}

	@Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		clientDisconnected(client);
	}
	
	@Override
	synchronized public void clientDisconnected(ConnectionToClient client) {
		System.out.println(client.getInfo("id") + " has disconnected.");
	}


	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the server instance (there is
	 * no UI in this phase).
	 *
	 * @param args[0] The port number to listen on. Defaults to 5555 if no argument
	 *                is entered.
	 */
	public static void main(String[] args) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(args[0]); // Get port from command line
		} catch (Throwable t) {
			port = DEFAULT_PORT; // Set port to 5555
		}

		EchoServer sv = new EchoServer(port);

		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}
//End of EchoServer class
