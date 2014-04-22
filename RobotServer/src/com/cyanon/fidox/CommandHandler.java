package com.cyanon.fidox;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CommandHandler {

	private PrintWriter out;
	private InputStream in;
	private ObjectInputStream ois;
	
	private ArrayList<Integer> commandBuffer;
	
	private boolean running = false;
	
	@SuppressWarnings("unchecked")
	public CommandHandler(ServerSocket sSocket, Socket socket) {
		try 
		{
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			in = socket.getInputStream();
			ois = new ObjectInputStream(in);
			running = true;
		}
		catch (IOException e)
		{
			System.out.println("IOException thrown!\n");
			try
			{
				socket.close();
				sSocket.close();
			}
			catch (IOException e2)
			{
				System.out.println("Unable to abort connection! Killing self...\n");
				System.exit(-1);
			}
		}
		try
		{
			while (running)
			{
				while ((commandBuffer = (ArrayList<Integer>)ois.readObject()) != null)
				{
					processCommandBuffer(commandBuffer);
					System.out.println("Clearing command buffer...\n");
					resetCommandBufferToZero();
				}
				System.out.println("Reception loop terminated...");
			}
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Unable to understand this command buffer! Am I up to date?");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

	private void processCommandBuffer(ArrayList<Integer> commandBuffer) 
	{
		for (int i : commandBuffer)
		{
			System.out.print(i);
		}
		System.out.print("\n");
	}
	
	private void resetCommandBufferToZero()
	{
		commandBuffer.set(0, 0);
		commandBuffer.set(1, 0);
		commandBuffer.set(2, 0);
		commandBuffer.set(3, 0);
	}

}
