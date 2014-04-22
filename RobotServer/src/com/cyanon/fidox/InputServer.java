package com.cyanon.fidox;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InputServer {

	private static CommandHandler commandHandler;
	
	private static InputStream in;
	
	public static void main(String[] args) throws IOException {
		ServerSocket sSocket = new ServerSocket(34567);
		
		System.out.println("Robot server V0.1b running...");
		try 
		{
			System.out.println("Awaiting new connections/instructions...\n");
			while (true)
			{
				Socket socket = sSocket.accept();
				try
				{
					commandHandler = new CommandHandler(sSocket, socket);
				}
				finally
				{
					socket.close();
				}
			}
		}
		finally
		{
			sSocket.close();
		}
	}
}
