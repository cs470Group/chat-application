import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat {
	private static final int LISTENING_PORT = 8000;
	
	public Chat() {
//		this.startServer();
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			String command = scanner.nextLine();
			
			if (command.equals("help")) {
				this.showHelp();
			} else if(command.equals("myip")) {
				this.showRealIP();
			} else if (command.equals("myport")) {
				this.showListeningPort();
			} else if (command.contains("connect")) {
				String info = command.substring("connect ".length());
				String[] arr = info.split(" ");
				this.connectToServer(arr[0], Integer.parseInt(arr[1]));
			} else if (command.equals("exit")) {
				this.exit();
			}
		}
	}
	
	public void startServer() {
		System.out.println("The server is running!");
		try {
			// Create a server socket listening to port 8000
			ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
			
			while(true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();
				System.out.println("Connected!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void startClient() {
//		System.out.println("Client method!");
//		// Create a socket to connect to the server
//		try {
//			Socket socket = new Socket("localhost", 8000);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/* Display information about the available user interface options / command manual */
	public void showHelp() {
		
	}
	
	/* Displays the IP address of this process */
	public void showRealIP() {
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
	}
	
	/* Display the port on which this process is listening for incoming connections */
	public void showListeningPort() {
		System.out.println(LISTENING_PORT);
	}
	
	public boolean connectToServer(String destination, int port) {
		// Create a socket to connect to the server
		try {
			Socket socket = new Socket(destination, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/* Display a numbered list of all connections this process is part of */
	public void showConnections() {
		
	}
	
	/* Terminates connection with host associated with id */
	public boolean closeConnection(int id) {
		return false;
	}
	
	/* Sends message to the host associated with id */
	public boolean sendMessage(int id, String message) {
		return false;
	}
	
	/* Closes all connections and terminates this process */
	public boolean exit() {
		System.exit(0);
		
		return false;
	}

	public static void main(String[] args) {
		new Chat();
	}

}
