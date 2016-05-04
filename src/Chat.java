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
	private HashMap<Integer, DataOutputStream> outputStreams;
	private HashMap<Integer, DataInputStream> inputStreams;
	private int count;
	
	public Chat(int listeningPort) {
		sockets = new HashMap<Integer, Socket>();
		outputStreams = new  HashMap<Integer, DataOutputStream>();
		inputStreams = new HashMap<Integer, DataInputStream>();
		count = 0;
		startServer(listeningPort);
		(new CommandThread()).start();
	}
	
	public void startServer(int listeningPort) {
		System.out.println("The server is running!");
		try {
			// Create a server socket listening to given port number
			serverSocket = new ServerSocket(listeningPort);

			(new ListeningThread()).start();
		} catch (IOException e) {
//			e.printStackTrace();
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
		System.out.println(serverSocket.getLocalPort());
	}
	
	public boolean connectToServer(String destination, int port) {
		// Create a socket to connect to the server	
		try {
			Socket clientSocket = new Socket(destination, port);
			
			count++;
			sockets.put(count, clientSocket);
			
			outputStreams.put(count, new DataOutputStream(clientSocket.getOutputStream()));
			inputStreams.put(count, new DataInputStream(clientSocket.getInputStream()));
			
			System.out.println("Successfully connected to " + clientSocket.getInetAddress().getHostAddress() + " at port number " + clientSocket.getPort() + ".");
			
			(new SocketThread(count, clientSocket)).start();
		} catch (UnknownHostException e) {
			System.out.println("Failed to connect to " + destination + " at port number " + port + ".");
		} catch (IOException e) {
			System.out.println("Failed to connect to " + destination + " at port number " + port + ".");
		}
		return false;
	}
	
	/* Display a numbered list of all connections  process is part of */
	public void showConnections() {
		if (!sockets.isEmpty()) {
			System.out.println("id:   IP Address        Port No.");
			for (Integer i : sockets.keySet()) {
				System.out.println(i + ":    " + sockets.get(i).getInetAddress().getHostAddress() + "       " + sockets.get(i).getPort());
			}
		} else {
			System.out.println("There are no connections.");
		}
	}
	
	/* Terminates connection with host associated with id */
	public boolean closeConnection(int id) {
		if (sockets.containsKey(id)) {
			try {
				System.out.println("Successfully terminated connection with " + sockets.get(id).getInetAddress().getHostAddress() + ".");
				outputStreams.get(id).writeBoolean(false);
				outputStreams.get(id).writeUTF(Inet4Address.getLocalHost().getHostAddress() + " has terminated the connection.");
				
				sockets.get(id).close();
				sockets.remove(id);
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
		try {
			outputStreams.get(id).writeBoolean(true);
			outputStreams.get(id).writeUTF(message);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/* Closes all connections and terminates this process */
	public boolean exit() {
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
			for (Integer i : sockets.keySet()) {
				if (!sockets.get(i).isClosed())
					sockets.get(i).close();
			}
			scanner.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
					try {
						String info = command.substring("connect ".length());
						String[] arr = info.split(" ");
						connectToServer(arr[0], Integer.parseInt(arr[1]));
					} catch (Exception e) {
						System.out.println("Please enter a valid connection.");
					}
				} else if (command.contains("terminate")) {
					try {
						String info = command.substring("terminate ".length());
						String[] arr = info.split(" ");
						closeConnection(Integer.parseInt(arr[0]));
					} catch (Exception e) {
						System.out.println("Please enter a valid id.");
					}
				} else if (command.contains("send")) {
					try {
						String info = command.substring("send ".length());
						String[] arr = info.split(" ");
						String message = command.substring("send ? ".length());
						sendMessage(Integer.parseInt(arr[0]), message);
					} catch (Exception e) {
						System.out.println("Please enter a valid message.");
					}
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
					
					System.out.println(connectionSocket.getInetAddress().getHostAddress() + " has successfully connected to you at port " + connectionSocket.getPort() + ".");
					
					count++;
					sockets.put(count, connectionSocket);
					
					outputStreams.put(count, new DataOutputStream(connectionSocket.getOutputStream()));
					inputStreams.put(count, new DataInputStream(connectionSocket.getInputStream()));
					
					// Create a new thread for the connection
					(new SocketThread(count, connectionSocket)).start();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	class SocketThread extends Thread {
		private int id;
		private Socket connectionSocket;
		
		public SocketThread(int count, Socket socket) {
			id = count;
			connectionSocket = socket;
		}
		
		public void run() {
			try {	
				DataInputStream inputFromClient = new DataInputStream(connectionSocket.getInputStream());
								
				while(true) {
					boolean flag = inputFromClient.readBoolean();
					String message = inputFromClient.readUTF();
					
					if (!flag) {
						System.out.println(message);
					} else {
						System.out.println("Message received from " + connectionSocket.getInetAddress().getHostAddress());
						System.out.println("Sender's Port: " + connectionSocket.getPort());
						System.out.println("Message: " + message);
						flag = false;
					}
				}
			} catch (IOException e1) {
				sockets.remove(id);
			}
		}
	}

	public static void main(String[] args) {
		new Chat(Integer.parseInt(args[0]));
	}

}
