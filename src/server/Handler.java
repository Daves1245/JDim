package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import im.Message;

public class Handler implements Runnable {

	public int id;
	private Socket socket;
	private ObjectInputStream in;
	public String username;

	public Handler(Socket connection, int id) {
		this.socket = connection;
		this.id = id;
	}

	@Deprecated
	public void setUsername() {
		String username = JOptionPane.showInputDialog(null, "Username selection", "Please enter a unique username: ",
				JOptionPane.PLAIN_MESSAGE);
		JOptionPane.showMessageDialog(null, "A unique username has been chosen");
	};

	public void run() {
		try {
			InputStream is = socket.getInputStream();
			in = new ObjectInputStream(is);

			Runnable sendMessages = () -> {
				Message msg;
				try {
					while (true) {
						msg = (Message) in.readObject();
						PPCServer.sendMessage(msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

			sendMessages.run();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("signing out");
			signOut();
		}

	}

	public void signOut() {
		PPCServer.connections.remove(socket);
		// TODO PPCServer.usernames.remove(username); use names
		System.out.println("User: " + username + " signing out...");
		return;
	}
}
