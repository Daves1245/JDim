package im;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

/**
 * 
 * A <code> Client </code> connects to a <code> PPCServer </code> and chats with
 * other clients by the use of a <code> Handler </code>, which regulate the
 * traffic of input and output
 * 
 * @author David Santamaria <br/>
 *         Accomplices: <br/>
 *         Nicholas Pipitone, Jacob Magnuson
 * @version 0.1.1
 *
 */
public class Client {

	// Server connections and I/O
	private Socket connection; // TODO - Fix server issues, then implement back
	static public ObjectInputStream in;
	private ObjectOutputStream out;

	// Frame components
	private JFrame frame;
	private CustomTextPane chatWindow;
	// private JTextField userText;
	private CustomTextField userText;
	private JLabel label;
	private StyleContext sc = new StyleContext();
	final DefaultStyledDocument doc = new DefaultStyledDocument(sc);

	private BufferedImage backgroundImage;

	public static final int PORT = 6789;

	private static Font font;

	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gd = ge.getDefaultScreenDevice();

	private static final Color BACKGROUND_COLOR = new Color(204, 255, 255);

	public Client(String ip) throws UnknownHostException, IOException {
		// login();

		// Creates a connection to the server
		System.out.println("Looking for server...");
		connection = new Socket(InetAddress.getByName(ip), PORT);
		System.out.println("Connected to server " + InetAddress.getByName(ip));
		System.out.println();

		// Initializes output stream from the server
		System.out.println("Initializing output stream...");
		out = new ObjectOutputStream(connection.getOutputStream());
		System.out.println("Output stream has been initialized.");
		System.out.println();

		// Initializes the input stream
		System.out.println("Initializing input stream. ..");
		in = new ObjectInputStream(connection.getInputStream());
		System.out.println("Input stream initialized, now chatting.");

		if (!gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
			JOptionPane.showMessageDialog(null, "Translucency is not supported");
			System.exit(-1);
		}

		JFrame.setDefaultLookAndFeelDecorated(true);

		font = new Font("Consolar", 1, 13);
		ClassLoader classLoader = getClass().getClassLoader();
		BufferedImage iconImage = ImageIO.read(new File(classLoader.getResource(Filenames.ICON_IMAGE).getFile()));
		backgroundImage = ImageIO.read(new File(classLoader.getResource(Filenames.BACKGROUND_PIC).getFile()));

		// Initialize and display frame
		initFrame();
		frame.setOpacity(0.85f);
		frame.setVisible(true);

		frame.setIconImage(iconImage);
	}

	public static void main(String[] args) {
		try (Scanner in = new Scanner(System.in)) {
			System.out.print("Hello, please enter the IP address of the server you are attempting to connect to: ");
			Client c = new Client(JOptionPane.showInputDialog(null,
					"Hello, please input the ip of the server you are attempting to connect to: "));
			c.run();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param ip
	 *            String representation of the IP of the server
	 * @param port
	 *            The port number that the server is hosted on
	 * @throws MalformedURLException
	 * @throws IOException
	 *             When the initialization of either I/O stream fails
	 * @throws UnknownHostException
	 * 
	 */
	public void initFrame() throws MalformedURLException {
		// Frame
		initJFrame();
		initUserText();
		initChatWindow();
		/*
		 * t TODO initConvoPane();
		 */
		addAll();
		// frame.setAlwaysOnTop(true );
		frame.pack();
	}

	private void addAll() {
		frame.add(chatWindow, BorderLayout.CENTER);
		frame.add(userText, BorderLayout.SOUTH);
		frame.add(new JScrollPane(chatWindow));
	}

	private void initJFrame() {
		frame = new JFrame("JDIM - Client");
		frame.setLayout(new BorderLayout());
		frame.setPreferredSize(new Dimension(500, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 500);
		System.out.println(frame.getWidth() + " " + frame.getHeight());
		frame.setLocation(Screen.width - 495, Screen.height - 340);
	}

	private void initUserText() {
		userText = new CustomTextField(backgroundImage);
		userText.setEditable(true);
		userText.addActionListener(getActionListener());
		userText.setPreferredSize(new Dimension(1, 35));
		userText.setBackground(Color.LIGHT_GRAY);
		userText.setForeground(Color.GREEN);
		userText.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				int kcode = e.getKeyCode();

				if (e.isControlDown()) {

					if (kcode == KeyEvent.VK_L) {
						clearChat();
					}

					if (kcode == KeyEvent.VK_C) {
						sendClearChatMessage();
					}
				}
			}

			private void sendClearChatMessage() {
				try {
					sendMessage(new Message(Message.Type.CLEAR));
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error in sending dispose chat history message");
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

		});
	}

	private void initChatWindow() {
		chatWindow = new CustomTextPane(backgroundImage, doc);
		chatWindow.setEditable(false);
		chatWindow.setFont(font);
		chatWindow.setBackground(BACKGROUND_COLOR);
		chatWindow.setOpaque(false);
		// chatWindow.setLineWrap(true);
		// chatWindow.setWrapStyleWord(true);
		chatWindow.setForeground(Color.GREEN);// new Color(102, 178, 255));
	}

	/**
	 * 
	 * Loads the image that is linked to from parameter url
	 * 
	 * @param url
	 *            - The url of the picture to be displayed
	 * @throws MalformedURLException
	 */
	void showImage(String url) throws MalformedURLException {
		label = new JLabel(new ImageIcon(new URL(url)));
		frame.remove(label);
		frame.add(label, BorderLayout.EAST);
		/**
		 * TODO Add fixed size to prevent taking up of extra space, perhaps move
		 * into chatPane
		 * 
		 * Add esthetics
		 * 
		 */
	}

	private ActionListener getActionListener() {
		return e -> {
			if (!e.getActionCommand().equals(null)) {
				if (e.getActionCommand().length() > 0)
					if (e.getActionCommand().charAt(0) == '/') {
						switch (e.getActionCommand().substring(1).toLowerCase()) {
						case "clear":
							clearChat();
							break;
						case "help":
							showMessage("Suggestions: \n/end\n/setTextColor\n/setColor\n/changeUsername\n"
									+ "/thisIsntReallyAnActualCommand");
							break;
						case "end":
							try {
								sendMessage(new Message(null, " a user left the conversation", ""));
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
							break;

						case "settextcolor":
						case "setcolor":

							String color = JOptionPane.showInputDialog(null, "Please type the new color:");

							if (color.equals("default")) {
								chatWindow.setBackground(BACKGROUND_COLOR);
							}

							if (e.getActionCommand().substring(1).toLowerCase().equals("setcolor")) {
								chatWindow.setBackground(getColor(color));
							} else {
								chatWindow.setForeground(getColor(color));
							}

							break;
						case "thisisntreallyanactualcommand":
							showMessage("I lied");
							break;
						default:
							showMessage("Command " + e.getActionCommand() + " is undefined");
							break;
						}
					} else {
						try {
							System.out.println(e.getActionCommand());
							showMessage(new Message(e.getActionCommand()).toString());
							System.out.println(e.getActionCommand());
							sendMessage(new Message(e.getActionCommand()));
						} catch (IOException ioe) {
							ioe.printStackTrace();
						} finally {
							userText.setText("");
						}
					}
			}
			userText.setText("");
		};
	}

	private Color getColor(String color) {

		if (color.charAt(0) == '#') {
			return new Color(Integer.parseInt(color.substring(1)));
		}

		switch (color.toLowerCase()) {
		case "green":
			return Color.GREEN;
		case "blue":
			return Color.BLUE;
		case "black":
			return Color.BLACK;
		case "orange":
			return Color.ORANGE;
		case "yellow":
			return Color.yellow;
		case "gray":
			return Color.GRAY;
		case "cyan":
			return Color.CYAN;
		case "red":
			return Color.RED;
		}
		return null;
	}

	private void clearChat() {
		try {
			while (chatWindow.getText().length() > 0) {
				chatWindow.setText(chatWindow.getText().substring(chatWindow.getText().length() - 1));
				Thread.sleep(100);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * 
	 * Sends Message to output stream to be read by the server
	 * 
	 * @param msg
	 *            The message to be sent
	 * @throws IOException
	 *             Whenever <code> writeObject </code> fails (There may be more
	 *             than one output Streams)
	 */
	public void sendMessage(Message msg) throws IOException {
		if (msg.text.contains("http")) {
			showImage(msg.toString());
		}
		out.writeObject(msg);
	}

	public void trySendMessage(Message msg) {
		try {
			sendMessage(msg);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void showMessage(String str) {
		if (str.contains("http")) {
			System.out.println("detected url link... attempting to display image");
			try {
				showImage(str);
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(null, e.getStackTrace());
				e.printStackTrace();
			}
		}
		SwingUtilities.invokeLater(() -> {
			chatWindow.setText(chatWindow.getText() + "\n" + str);
			chatWindow.setSize(chatWindow.getWidth() - 1, chatWindow.getHeight() - 1);
		});
	}

	/**
	 * @deprecated
	 */
	public void run() {
		Runnable send = () -> {
			try (Scanner sc = new Scanner(System.in)) {
				while (true) {
					System.out.print("Enter message: ");
					sendMessage(new Message(sc.nextLine()));
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "IOException in Runnable thread");
				e.printStackTrace();
			}
		};

		Runnable read = () -> {
			while (true) {
				try {
					Message msg = nextMessage();

					/*
					 * TODO More message manipulation, evaluation. Sending
					 * notifications, alerts, etc through messages, such as
					 * commands to clear chat and other functions
					 */

					if (msg.type == Message.Type.CLEAR) {
						clearChat();
					}

					showMessage(msg.text);
					System.out.println(msg);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		send.run();
		read.run();
	}

	/**
	 * 
	 * @return The next message read in from the input stream
	 * @throws ClassNotFoundException
	 * 
	 * @throws IOException
	 * 
	 */
	public Message nextMessage() throws ClassNotFoundException, IOException {
		return (Message) in.readObject();
	}
}

class CustomTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4515243703831482126L;
	private BufferedImage img;

	public CustomTextField(BufferedImage img) {
		this.img = img;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		if (img != null) {
			int x = getWidth() - img.getWidth();
			int y = getHeight() - img.getHeight();
			g2d.drawImage(img, x, y, this);
		}
		super.paintComponent(g2d);
		g2d.dispose();
	}
}

class CustomTextPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1972347361350323717L;
	private BufferedImage img;

	public CustomTextPane(BufferedImage img) {
		this.img = img;
		setOpaque(false);
	}

	public CustomTextPane(BufferedImage img, DefaultStyledDocument doc) {
		super(doc);
		this.img = img;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		if (img != null) {
			int x = getWidth() - img.getWidth();
			int y = getHeight() - img.getHeight();
			g2d.drawImage(img, x, y, this);
		}
		super.paintComponent(g2d);
		g2d.dispose();
	}
}

class CustomTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1739008519508062043L;
	private BufferedImage img;

	public CustomTextArea(BufferedImage img) {
		super(20, 20);
		this.img = img;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		if (img != null) {
			int x = getWidth() - img.getWidth();
			int y = getHeight() - img.getHeight();
			g2d.drawImage(img, x, y, this);
		}
		super.paintComponent(g2d);
		g2d.dispose();
	}
}