import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Chat {
	private ServerSocket serverSocket;
	private Scanner scanner;
	private HashMap<Integer, Socket> sockets;
	private int count;
	private DataInputStream inputFromClient;
	private DataOutputStream outputToClient;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	
	public Chat(int listeningPort) {
		this.sockets = new HashMap<Integer, Socket>();
		this.count = 0;
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
		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println("         List of Available Commands");
		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println("help                                     - Displays command manual.");
		System.out.println("myip                                     - Displays actual IP of computer.");
		System.out.println("myport                                   - Displays listening port.");
		System.out.println("connect <destination> <port no>          - Establishes TCP connection to <destination> at <port no>.");
		System.out.println("list                                     - Displays numbered list of connections connected to this process.");
		System.out.println("terminate <connection id>                - Terminates connection associated to the id.");
		System.out.println("send <connection id> <message>           - Sends message to host associated with the connection id.");
		System.out.println("exit                                     - Closes all connections and terminates this process.");
		System.out.println("-------------------------------------------------------------------------------------------------------------");
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
			Socket clientSocket = new Socket(destination, port);
			count++;
			sockets.put(count, clientSocket);
			fromServer = new DataInputStream(clientSocket.getInputStream());
			toServer = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Successfully connected to " + destination + " at port number " + port + ".");
		} catch (UnknownHostException e) {
			System.out.println("Failed to connect to " + destination + " at port number " + port + ".");
		} catch (IOException e) {
			System.out.println("Failed to connect to " + destination + " at port number " + port + ".");
		}
		return false;
	}
	
	/* Display a numbered list of all connections this process is part of */
	public void showConnections() {
		if (!sockets.isEmpty()) {
			System.out.println("id:   IP Address        Port No.");
			for (Integer i : this.sockets.keySet()) {
				System.out.println(i + ":    " + this.sockets.get(i).getInetAddress().getHostAddress() + "       " + this.sockets.get(i).getPort());
			}
		}
	}
	
	/* Terminates connection with host associated with id */
	public boolean closeConnection(int id) {
		if (sockets.containsKey(id)) {
			try {
				sockets.get(id).close();
				sockets.remove(id);
				count--;
				this.toServer.writeChars("Successfully terminated connection.");
//				System.out.println("Successfully terminated connection.");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			System.out.println(id + " is not a valid connection.");
			return false;
		}
	}
	
	/* Sends message to the host associated with id */
	public boolean sendMessage(int id, String message) {
		return false;
	}
	
	/* Closes all connections and terminates this process */
	public boolean exit() {
		try {
			this.serverSocket.close();
			for (Integer i : sockets.keySet()) {
				this.sockets.get(i).close();
			}
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
				} else if (command.contains("terminate")) {
					String info = command.substring("terminate ".length());
					String[] arr = info.split(" ");
					closeConnection(Integer.parseInt(arr[0]));
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
					Socket connectionSocket = serverSocket.accept();
					count++;
					sockets.put(count, connectionSocket);
					inputFromClient = new DataInputStream(connectionSocket.getInputStream());
					outputToClient = new DataOutputStream(connectionSocket.getOutputStream());
					
					// Create a new thread for the connection
					(new SocketThread(connectionSocket)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		class SocketThread extends Thread {
			private Socket connectionSocket;
			
			public SocketThread(Socket socket) {
				this.connectionSocket = socket;
			}
			
			public void run() {
				System.out.println(connectionSocket.getInetAddress().getHostAddress() + " has successfully connected to you at port " + this.connectionSocket.getPort() + ".");
				
				while(true) {
					try {
						System.out.println(inputFromClient.readChar());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		new Chat(Integer.parseInt(args[0]));
	}

}
