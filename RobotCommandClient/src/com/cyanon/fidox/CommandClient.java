package com.cyanon.fidox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class CommandClient extends JFrame implements KeyListener, ActionListener {

	private static ResponseWindow responseWindow;
	private static boolean responseWindowReady = false;

	private static Socket socket;
	private static BufferedReader in;
	private static ObjectOutputStream out;
	
	private static ArrayList<Integer> commandBuffer;
	
	JTextField typingArea;
	static JTextArea displayArea;
	
	private boolean wDown = false;
	private boolean aDown = false;
	private boolean sDown = false;
	private boolean dDown = false;
	
	public static void main(String[] args) throws IOException {
		String serverAddress = JOptionPane.showInputDialog(
				"Welcome to FidoX Command Client!\n" +
				"Please enter the IP address assigned to FidoX.\n" +
				"Note that FidoX listens on port 34567 so please\n" +
				"ensure your network is set up for that FIRST. Ta.");
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run() 
			{
				createAndShowUI();
			}
		});
		spawnResponseWindowAndConnectSocket(serverAddress);
	}
	
	public CommandClient(String name)
	{
		super(name);
	}
	
	private static void setUpSocket(String serverAddress) throws IOException
	{
		socket = new Socket(serverAddress, 34567);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		
		try
		{
			postConnectionInfo();
		}
		catch (IOException e)
		{
			displayArea.append(e.getMessage());
		}
		
		commandBuffer = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++)
		{
			commandBuffer.add(0);
		}
		displayArea.append("Command buffer instantiated and ready for manipulation...");
	}
	
	private static void createAndShowUI()
	{
		CommandClient frame = new CommandClient("FidoX Command Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.populateUI();
		frame.pack();
		frame.setVisible(true);
	}
	
	private void populateUI()
	{
		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		
		typingArea = new JTextField(20);
		typingArea.addKeyListener(this);
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.setPreferredSize(new Dimension(640, 480));
		
		getContentPane().add(typingArea, BorderLayout.PAGE_START);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(clear, BorderLayout.PAGE_END);
	}
	
	private static void postConnectionInfo() throws IOException
	{
		displayArea.append("Welcome to FidoX V0.1b. Running connection test...\n");
		if (socket.isConnected())
		{
			displayArea.append("Connected to FidoX on " + socket.getInetAddress() + ":" + socket.getPort() + "\n");
			responseWindow.responseArea.append("Connected to FidoX on " + socket.getInetAddress() + ":" + socket.getPort() + "\n");
		}
		else
		{
			displayArea.append("I can't connect to anything on this address!");
			//TODO: do something useful here!! like display a yes/no prompt
		}
		if (socket.isInputShutdown())
		{
			displayArea.append("There's an incoming communication problem! I have no information from the module!\n");
		}
		if (socket.isOutputShutdown())
		{
			displayArea.append("There's an outgoing communication problem! Commands can't be sent to the module!\n");
		}
	}
	
	private static void spawnResponseWindowAndConnectSocket(String serverAddress) throws IOException
	{
		responseWindow = new ResponseWindow("FidoX Response Window");
		while (!responseWindowReady)
		{
			if (responseWindow.responseWindowReady)
			{
				setUpSocket(serverAddress);
				responseWindowReady = true;
			}
		}
	}
	
	private static boolean writeToResponseWindow(String message)
	{
		if (responseWindow.responseWindowReady)
		{
			responseWindow.responseArea.append(message);
			return true;
		}
		return false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		switch (key)
		{
			case ('w'):
				if (!wDown)
				{
					wDown = true;
					displayArea.append("W pressed...\n");
					typingArea.setText(null);
					commandBuffer.set(0, 1);
					break;
				}
				break;
			case ('a'):
				if (!aDown)
				{
					aDown = true;
					displayArea.append("A pressed...\n");
					typingArea.setText(null);
					commandBuffer.set(1, 1);
					break;
				}
				break;
			case ('s'):
				if (!sDown)
				{
					sDown = true;
					displayArea.append("S pressed...\n");
					typingArea.setText(null);
					commandBuffer.set(2, 1);
					break;
				}
				break;
			case ('d'):
				if (!dDown)
				{
					dDown = true;
					displayArea.append("D pressed...\n");
					typingArea.setText(null);
					commandBuffer.set(3, 1);
					break;
				}
				break;
			case ('b'): //buffer
				displayArea.append("Current command buffer: " + getCurrentCommandBufferAsString() + "\n");
				break;
			case ('t'): //transmit
				displayArea.append("Transmitting buffer : " + getCurrentCommandBufferAsString() + " : to socket for execution...\n");
				transmitBuffer();
				resetCommandBufferToZero();
				break;
		}
	}
	
	private void transmitBuffer()
	{
		try {
			out.writeObject(commandBuffer);
			out.flush();
		} catch (IOException e) {
			displayArea.append("There was a problem transmitting this command buffer to the slave!\n");
			displayArea.append(e.getMessage());
			e.printStackTrace();		}
	}
	
	private String getCurrentCommandBufferAsString()
	{
		return (commandBuffer.get(0) + " - "
				+ commandBuffer.get(1) + " - "
				+ commandBuffer.get(2) + " - "
				+ commandBuffer.get(3)).toString();
	}
	
	private void resetCommandBufferToZero()
	{
		commandBuffer.set(0, 0);
		commandBuffer.set(1, 0);
		commandBuffer.set(2, 0);
		commandBuffer.set(3, 0);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		switch (key)
		{
		case ('w'):
			wDown = false;
			displayArea.append("W released...\n");
			typingArea.setText(null);
			commandBuffer.set(0, 0);
			break;
		case ('a'):
			aDown = false;
			displayArea.append("A released...\n");
			typingArea.setText(null);
			commandBuffer.set(1, 0);
			break;
		case ('s'):
			sDown = false;
			displayArea.append("S released...\n");
			typingArea.setText(null);
			commandBuffer.set(2, 0);
			break;
		case ('d'):
			dDown = false;
			displayArea.append("D released...\n");
			typingArea.setText(null);
			commandBuffer.set(3, 0);
			break;
		}		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		displayArea.setText(null);
		typingArea.setText(null);
		typingArea.requestFocus();
	}

}
