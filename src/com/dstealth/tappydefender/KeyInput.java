package com.dstealth.tappydefender;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
	
	private Game game;
	private int key;
	
	public KeyInput(Game g) {
		this.game = g;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		this.key = e.getKeyCode();
		if (MainWindow.doDebug)
			System.out.println("Key pressed: " + this.key);
		
		if (this.game.isMainMenu) {
			// main menu
			doMenuCommands();
		}
		else {
			// if game running
			if (!this.game.gameEnded) {
				if (!this.game.isPaused)
					// not paused
					doGameRunningCommands();
				else
					// paused
					doPauseMenuCommands();
			}
			else
				// game ended
				doGameEndedCommands();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		int key = e.getKeyCode();
		if (MainWindow.doDebug)
			System.out.println("Key released: " + key);
		
		// if SPACE key is released, stop ship
		if (key == KeyEvent.VK_SPACE && !this.game.gameEnded && !this.game.isMainMenu) {
			this.game.player.setBoosting(false);
		}
	}
	
	private void restartGame() {
		this.game.stop();
		this.game.startGame();
	}
	
	private void quitGame() {
		this.game.stop();
		this.game.startMenu();;
	}
	
	// ==================================================
	//		Menu Inputs
	// ==================================================
	
	private void doMenuCommands() {
		restartGameOn(KeyEvent.VK_ENTER);
	}
	
	private void doGameRunningCommands() {
		togglePauseOn(KeyEvent.VK_ENTER);
		boostShipOn(KeyEvent.VK_SPACE);
	}
	
	private void doPauseMenuCommands() {
		togglePauseOn(KeyEvent.VK_ENTER);
		quitGameOn(KeyEvent.VK_ESCAPE);
	}
	
	private void doGameEndedCommands() {
		restartGameOn(KeyEvent.VK_ENTER);
		quitGameOn(KeyEvent.VK_ESCAPE);
	}
	
	// ==================================================
	//		Actions
	// ==================================================
	
	private void restartGameOn(int ikey) {
		if (this.key == ikey) {
			restartGame();
		}
	}
	
	private void quitGameOn(int ikey) {
		if (this.key == ikey)
			quitGame();
	}
	
	private void togglePauseOn(int ikey) {
		if (this.key == ikey)
			this.game.togglePause();
	}
	
	private void boostShipOn(int ikey) {
		if (this.key == ikey)
			this.game.player.setBoosting(true);
	}
}
