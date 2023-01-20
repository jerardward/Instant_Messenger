import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

//    Constructor
    public Client(String host) {
        super("Client mofo");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

//    Connect to server
    public void startRunning() {
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eofException) {
            showMessage("\n Client terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeCrap();
        }
    }

//    Connect to server
    private void connectToServer() throws IOException {
        showMessage("Attemption connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }

//    Set up streams to send and receive messages
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Dude your streams are now good to go! \n");
    }

//    While chatting with server
    private void whileChatting() throws IOException {
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n I don't know that object type");
            }
        } while (!message.equals("SERVER - END"));
    }

//    Close the streams and sockets
    private void closeCrap() {
        showMessage("\n Closing crap down...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

//    Send messages to server
    private void sendMessage(String message) {
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("CLIENT - " + message);
        } catch (IOException ioException) {
            chatWindow.append("\n Something messed up sending message hoss!");
        }
    }

//    Change/update chatWindow
    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );
    }

//    Gives user permission to type crap in to the text box
    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }

}
