# chat-application
 README file for Project 1: Chat Application
 
 Completed By: Jeanne Pascua, Raymond Wu, and Alyssa Solon
 
    ===========================
          Description
    ===========================
 
    The Chat application is a program that allows 3 clients, at most, to connect and send messages to each other.
 
 
    ===========================
          User Intructions
    ===========================
 
    To Run:

      .bat and .jar has to be in the same directory

      .\chat <port number> on Windows Machines

      ./chat <port number> on Linux-based Machines
  
    To view your IP address, type in myip
  
    To view your port, type in myport
  
    To view the list of clients that are connected, type in list
  
    To connect to server, type in connect <destination> <port no.>
      Example: connect 198.168.20.21
    
    To send a message, type in send <the id of the client you want to send a message to> "message"
      Example: send 1 "hello"
  
    To end a connection, type in terminate <id>
      Example: terminate 2
    
    To close all connections and terminate the process, type in exit.
