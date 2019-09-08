//Server Class

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame
{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    
    public Server()
    {
      // The title of the messenger program
    	super("Instant Messenger");
    	// Instantiates userText (where the user types probably)
    	userText = new JTextField();
    	// User can't type in the message box unless connected to someone else
    	// Change setEditable to true once we are connected to someone
    	userText.setEditable(false);
    	// Creates an actionListener for userText that will allow for an action to be carried out
    	userText.addActionListener(
    	        // Example of anonymous classes
    	        // That method in this case is overidden from class method
    			new ActionListener()
    			{
    			    // Whenever user hits enter, this method is called
    				public void actionPerformed(ActionEvent event)
    				{
    				    // Sends whatever message we type into the text field (method not created yet)
    					sendMessage(event.getActionCommand());
    					// Sets the text box empty after sending the message
    					userText.setText("");
    				}
    			});
    	// Adds the userText field to JFrame and displays it in the Northern component of the JFrame
    	add(userText, BorderLayout.NORTH);
    	// Initialize the area where the user can see the text
    	chatWindow = new JTextArea();
    	// Adds a scrollable view of the chat window to the JFrame
    	add(new JScrollPane(chatWindow));
    	// Sets size of JFrame
    	setSize(300,150);
    	// Makes the JFrame visible
    	setVisible(true);
    }
    
    // Sets up and runs the server
    public void startRunning()
    {
        try
        {
            // Sticks the program at the port number 6789
            // The second parameter determines how many people are allowed to sit and wait at the port to talk to you (in this case, a 100)
            server = new ServerSocket(6789,100);
            // This while loops runs forever
            while(true) 
            {
                try
                {
                   // Connect and have conversation
                   // Once we are done having the conversation, we will throw an error
    				waitForConnection(); // Once the application starts running on the server, you need to wait till you have someone to connect with to start a conversation
    				setupStreams(); // Once someone is connected, it setps up the output and input stream to send/receive messages
    				whileChatting(); // Once we are connected, we need this to send messages back and forth during our chat
                }
                catch(EOFException eofException) // end of stream connected exception
                {
                    // An EOFException is mainly used my data input streams to signal end of stream/connection
                    showMessage("\n Server ended the connection!");
                }
                finally
                {
                    // Runs method closeCrap to signal the end of the stream, close all the sockets, and so on
                    closeConnection();
                }
            }
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace(); // If we mess something up, we can see what we did wrong
        }
    }
        
    // Wait for connection, then display connection information
    private void waitForConnection() throws IOException
    {
        showMessage("Waiting for someone to connect... \n");
        // Once someone asks to connect to us, it accepts a connection to the socket
        // A connection/socket is created between you and another computer
        connection = server.accept(); // Doesn't create a billion empty socket; it only creates a socket one time, when it is connected to someone
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }
        
    // Get stream to send and receive data
    private void setupStreams() throws IOException
    {
        output = new ObjectOutputStream(connection.getOutputStream()); // Creates the pathway that allows us to connect to another computer
        output.flush(); // Makes sure all the data is sent
        input = new ObjectInputStream(connection.getInputStream()); // Creates the pathway to receive messages from someone else
        showMessage("\n Streams are now setup! \n");
    }
        
    // During the chat conversation
    private void whileChatting() throws IOException
    {
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do
        {
            // Have a conversation
            try
            {
                // Input is the socket whereby clients can send stuff to us
                message = (String) input.readObject(); // We will read whatever they typed to us and make sure it's a string, and then store it in message
                showMessage("\n" + message);
            }
            catch (ClassNotFoundException classNotFoundException)
            {
                showMessage("\n I don't know what that user sent!"); // If someone sends you a weird text whose type cannot be converted to String
            }
        }
        while(!message.equals("CLIENT - END")); // If client types "END", we exit the chat
    }
        
    // Close streams and sockets after you are done chatting
    private void closeConnection()
    {
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try
        {
            // Close the output stream, input stream, and the overall connection/socket
            output.close();
            input.close();
            connection.close();
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace(); // If we mess something up, we can see what we did wrong
        }
    }
    
    // Send a message to client (computer connected to server)
    private void sendMessage(String message)
    {
    	try
    	{
    		// Create object then send to output stream
    		// Then goes to client, then sends to other person
    		output.writeObject("SERVER - " + message);
    		// In case any bytes left over, clear stream
    		output.flush();
    		// See history of conversation
    		showMessage("\n SERVER - " + message);
    	}
    	catch(IOException ioe)
    	{
    		chatWindow.append("\n ERROR: I CAN'T SEND THE MESSAGE");
    	}
    }
    
    // Updates chatWindow
    private void showMessage(final String text)
    {
    	// Creates thread that updates GUI
    	// Updates text inside chat window
    	SwingUtilities.invokeLater
    	(
			new Runnable()
			{
				public void run()
				{
					// Add new line of text at the bottom of the window
					// Appends message to end of document
					// Then updates chat window 
					// ONLY UPDATES CHATWINDOW
					chatWindow.append(text);
				}
			}
		);
    }
    
    // Lets the user type into the box
    // This is here because you don't want to send things when
    // No one is connected to the other side of stream
    private void ableToType(final boolean tof)
    {
    	SwingUtilities.invokeLater
    	(
			new Runnable()
			{
				public void run()
				{
					// Sets the box to be editted by typing
					// Or not able to type stuff in
					userText.setEditable(tof);
				}
			}
		);
    }
} 
    
