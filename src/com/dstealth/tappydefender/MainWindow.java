package com.dstealth.tappydefender;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow extends Canvas {

	public static boolean doDebug = false;
	
	private static final long serialVersionUID = 7054107532787801813L;
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = ((WINDOW_WIDTH/12)*9);	// 4:3 aspect ratio
	private static final String TITLE = "TappyDefender by Dalbir Brar";
	private static final int OFFSET_BORDER = 5;
	private static final int OFFSET_TITLEBAR = 28;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		//doDebug = true;
		new MainWindow(WINDOW_WIDTH, WINDOW_HEIGHT, TITLE);
	}
	
	public MainWindow(int width, int height, String title) {
		JFrame frame = new JFrame(title);
		Dimension dim = new Dimension(width, height);
		
		frame.setPreferredSize(dim);
		frame.setMaximumSize(dim);
		frame.setMinimumSize(dim);
		
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ensures that the threads terminate when window is closed.
		frame.setLocationRelativeTo(null);		// null = starts in center, without this would start in top corner
		frame.setUndecorated(false);
		frame.setVisible(true);
		
		if (doDebug)
			System.out.println("Window size: " + frame.getSize());
		
		Game game = new Game(width - OFFSET_BORDER, height - OFFSET_TITLEBAR);
		frame.add(game);
		game.requestFocus();
		game.startMenu();
	}
}
