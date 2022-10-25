import common.ChatIF;
import java.util.Scanner;

public class ServerConsole implements ChatIF {
	// Class variables *************************************************

	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	// Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	EchoServer server;
	
	/**
	 * Scanner to read from the console
	 */
	Scanner fromConsole;
	
	/**
	 * Constructs an instance of the ServerConsole UI.
	 *
	 * @param port The port to run on.
	 */
	public ServerConsole(int port) {
		server = new EchoServer(port, this);
		try {
			server.listen();
		} catch(Exception e) {
			System.out.println("ERR - Could not listen for clients! Terminating Server.");
			System.exit(1);
		}
		
		// Create scanner object to read from console
		fromConsole = new Scanner(System.in);
	}
	
	// Instance methods ************************************************

	/**
	 * This method waits for input from the console. Once it is received, it sends
	 * it to the servers's message handler.
	 */
	public void accept() {
		try {

			String message;

			while (true) {
				message = fromConsole.nextLine();
				server.handleMessageFromServerUI(message);
			}
		} catch (Exception ex) {
			System.out.println("Unexpected error while reading from console!");
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the Server UI.
	 *
	 * @param args[0] The port to connect to.
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT;

		try {
			port = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			port = DEFAULT_PORT;
		}
		ServerConsole serverChat = new ServerConsole(port);
		serverChat.accept(); // Wait for console data
	}

}
