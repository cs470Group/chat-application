import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat {
	private ServerSocket serverSocket;
	private Scanner scanner;
	
	public Chat(int listeningPort) {
		this.startServer(listeningPort);
		(new CommandThread()).start();
	}
	
	public void startServer(int listeningPort) {
		System.out.println("The server is running!");
		try {
			// Create a server socket listening to given port number
			this.serverSocket = new ServerSocket(listeningPort);

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
		System.out.println(this.serverSocket.getLocalPort());
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
				} else if (command.equals("list")) {
					showConnections();
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
				try {
					// Listen for a connection request
					Socket socket = serverSocket.accept();
					
					// Create a new thread for the connection
					(new SocketThread(socket)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		class SocketThread extends Thread {
			private Socket socket;
			
			public SocketThread(Socket socket) {
				this.socket = socket;
			}
			
			public void run() {
				System.out.println("Connected! " + this.socket.getPort());
			}
		}
	}

	public static void main(String[] args) {
		new Chat(Integer.parseInt(args[0]));
	}

}
