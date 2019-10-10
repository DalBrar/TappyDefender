package com.dstealth.tappydefender;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
	
	private Game game;
	
	public KeyInput(Game g) {
		this.game = g;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		int key = e.getKeyCode();
		if (MainWindow.doDebug)
			System.out.println("Key pressed: " + key);
		
		if (this.game.isMainMenu) {
			// if ENTER key, start game
			if (key == KeyEvent.VK_ENTER) {
				this.game.isMainMenu = false;
				restartGame();
			}
		}
		else {
			// if ENTER key:
			if (key == KeyEvent.VK_ENTER) {
				// if in game, pause
				if (!this.game.gameEnded) {
					this.game.togglePause();
					return;
				}
				// else restart game
				else {
					restartGame();
					return;
				}
			}
			
			// if SPACE key is pressed, boost ship
			if (key == KeyEvent.VK_SPACE && !this.game.gameEnded) {
				this.game.player.setBoosting(true);
				return;
			}
			
			// if ESC key pressed and game is paused, restart game
			if (key == KeyEvent.VK_ESCAPE && this.game.isPaused) {
				restartGame();
				return;
			}
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
		this.game.start();
	}
}
