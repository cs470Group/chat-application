import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat {
	private static final int LISTENING_PORT = 8000;
	private ServerSocket serverSocket;
	private Scanner scanner;
	
	public Chat() {
		this.startServer();
		(new CommandThread()).start();
	}
	
	public void startServer() {
		System.out.println("The server is running!");
		try {
			// Create a server socket listening to port 8000
			this.serverSocket = new ServerSocket(LISTENING_PORT);

			(new ListeningThread()).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
		try {
			this.serverSocket.close();
			this.scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
		
		return false;
	}
	
	class CommandThread extends Thread {
		public void run() {
			scanner = new Scanner(System.in);
			
			while(true) {
				String command = scanner.nextLine();
				
				if (command.equals("help")) {
					showHelp();
				} else if(command.equals("myip")) {
					showRealIP();
				} else if (command.equals("myport")) {
					showListeningPort();
				} else if (command.contains("connect")) {
					String info = command.substring("connect ".length());
					String[] arr = info.split(" ");
					connectToServer(arr[0], Integer.parseInt(arr[1]));
				} else if (command.equals("exit")) {
					exit();
				}
			}
		}
	}
	
	class ListeningThread extends Thread {
		public void run() {
			while(true) {
				// Listen for a connection request
				try {
					Socket socket = serverSocket.accept();
					System.out.println("Connected!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new Chat();
	}

}
