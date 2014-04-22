package com.cyanon.fidox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ResponseWindow extends JFrame implements ActionListener {

	JTextArea responseArea;
	
	public boolean responseWindowReady = false;
	
	public ResponseWindow(String name)
	{
		super(name);
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run() 
			{
				createAndShowUI();
			}
		});
	}
	
	private void createAndShowUI()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.populateUI();
		this.pack();
		this.setVisible(true);
	}
	
	private void populateUI()
	{
		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		
		responseArea = new JTextArea();
		responseArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(responseArea);
		scrollPane.setPreferredSize(new Dimension(640, 480));
		
		getContentPane().add(scrollPane, BorderLayout.PAGE_START);
		getContentPane().add(clear, BorderLayout.PAGE_END);
		responseWindowReady = true;
	}
	
//	public boolean postMessageToResponseWindow(String message, Boolean clear)
//	{
//		if (responseArea != null)
//		{
//			if (clear)
//			{
//				responseArea.setText(null);
//			}
//			responseArea.append(message + "\n");
//			return true;
//		}
//		return false;
//	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		responseArea.setText(null);
	}

}
