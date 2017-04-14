package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import im.Message;

/**
 * A server that hosts and runs a chat-based C2S2C connection.
 * 
 * @author David Santamaria <br/>
 *         Accomplices: <br/>
 *         Nicholas Pipitone, Jacob Magnuson
 * @version 0.1.0
 */
public class PPCServer {

	/**
	 * An enjoyable experience for all
	 */

	/** An array of all the clients connected to the server */
	public static ArrayList<Socket> connections;

	/**
	 * An array that holds output streams that the <code< sendMessage </<code>
	 * method sends messages to
	 */
	public static ArrayList<ObjectOutputStream> outs;
	/**
	 * The server's socket - used to create connections with clients
	 */
	static protected ServerSocket server;

	/**
	 * A boolean value that returns whether or not the server is running at the
	 * moment of evaluation
	 */
	private static boolean isRunning;

	/**
	 * A fun and enjoyable application that spews out random facts every so
	 * often minutes
	 */

	/**
	 * Sends the parameter <code> msg </code> to all other clients connected to
	 * the server at the time at which the message is sent
	 * 
	 * @param msg
	 *            The message to be sent to all other clients in the chat
	 */
	static public void sendMessage(Message msg) {

		System.out.println(msg);
		
		for (ObjectOutputStream oStream : outs) {
			try {
				oStream.writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void createUser() {

	}

	/**
	 * Creates an instance of the server if, and only if, there is not already
	 * an instance of the class
	 */
	public PPCServer() {

		// Singleton
		if (isRunning) {
			return;
		}
		isRunning = true;

		/* Initialize connections and outs */
		connections = new ArrayList<Socket>();
		outs = new ArrayList<ObjectOutputStream>();

		try {
			InetAddress ip = InetAddress.getLocalHost();
			System.out.println(InetAddress.getByName(ip.getHostName()));
			System.out.println(ip.getHostAddress());
			server = new ServerSocket(6789); // If you're reading this, no. Just
												// no. Stop, please exit and
												// just NO.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server
	 */
	public void run() {
		try {
			System.out.println("Starting process");

			// Create a new handler for all connections to the server
			for (int i = 0; true; i++) {
				System.out.println("Listening for client connections...");
				connections.add(server.accept()); // Wait's until a user has
													// connected
				System.out.println("A user has been added to the connections");

				// Add the new user's stream to the list of output streams
				outs.add(new ObjectOutputStream(connections.get(connections.size() - 1).getOutputStream()));

				// Create's a handler for each connection and starts it on it's
				// own thread
				new Thread(new Handler(connections.get(connections.size() - 1), i)).start();

				// Inform the world
				sendMessage(new Message("A USER HAS CONNECTED"));
			}
		} catch (IOException e) { // Failed to send message
			e.printStackTrace();
		} finally {
			try {
				server.close(); // Close the server when done
				System.out.println("Closing server...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			PPCServer server = new PPCServer();
			server.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}