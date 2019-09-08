//Client Class

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{
	// Initialize variables used in messaging
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	// Constructor
	public Client(String host) // Host is address of server
	{
		// Title of client
		super("Client!");
		// Assign address to serverIP
		serverIP = host;
		// Initialize as textField
		userText = new JTextField();
		// Can't write in textbox
		userText.setEditable(false);
		userText.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					// Whenever user hits enter
					sendMessage(event.getActionCommand());
					// Clear box after hit enter
					userText.setText("");
				}
			}
		);
		// Adds textbox to JFrame and puts it on the top
		add(userText, BorderLayout.NORTH);
		// Initialize window for chat history
		chatWindow = new JTextArea();
		// Add chatWindow to the center
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		// Window size
		setSize(300,150);
		// Able to see
		setVisible(true);
	}
	
	//Connect to server
	public void startRunning()
	{
		try
		{
			connectToServer(); //  Tries to connect to server
			setupStreams(); // If connect set up output input streams
			whileChatting(); // Able to chat
		}
		catch(EOFException eofException) // Throw if client exits
		{
			showMessage("\n Client terminated the connection");
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		finally
		{
			// Close connection if error or clinent exits
			closeConnection();
		}
	}
	
	// Connect to server
	private void connectToServer() throws IOException
	{
		showMessage("Attempting connection... \n");
		// Connects to server
		// The first argument is the passes in IP address
		// The second argument is the port 
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		// Show message if connected
		showMessage("Connection Established! Connected to: " + connection.getInetAddress().getHostName());
	}
	
	// Set up streams
	private void setupStreams() throws IOException
	{
		// Set up input stream
		output = new ObjectOutputStream(connection.getOutputStream());
		// Flush anything that is potentially in the stream
		output.flush();
		// Set up input stream
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n The streams are now set up! \n");
	}
	
	// While chatting with server
	private void whileChatting() throws IOException
	{
		// User is able to type
		ableToType(true);
		do
		{
			try
			{
				// Converts input to string object
				message = (String) input.readObject();
				// Displays message
				showMessage("\n" + message);
			}
			catch(ClassNotFoundException classNotFoundException)
			{
				showMessage("Unknown data received!"); // If input cannot be converted to string object
			}
		}
		while(!message.equals("SERVER - END"));	
	}
	
	// Close connection
	private void closeConnection()
	{
		showMessage("\n Closing the connection!");
		// User not able to type
		ableToType(false);
		try
		{
			// Close streams and socket
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	// Send message to server
	private void sendMessage(String message)
	{
		try
		{
			output.writeObject("CLIENT - " + message); // Writes message to output stream
			output.flush(); // Flush remaining bytes
			showMessage("\nCLIENT - " + message); // Displays message on window
		}
		catch(IOException ioException)
		{
			chatWindow.append("\n Oops! Something went wrong!");
		}
	}
	
	// Update chat window
	private void showMessage(final String message)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					// Updates chat window
					chatWindow.append(message);
				}
			}
		);
	}
	
	// Allows user to type
	private void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					userText.setEditable(tof);
				}
			}
		);
	}
}
