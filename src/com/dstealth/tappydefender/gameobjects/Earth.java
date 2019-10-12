package com.dstealth.tappydefender.gameobjects;

import com.dstealth.tappydefender.Game;

public class Earth extends GameObject {

	public Earth(int width, int height) {
		super(width, height, Game.g_earth);
		this.x = width;
		this.minX = width - (this.getBitmap().getWidth()/3);
		this.speed = 1;
	}

	@Override
	public void update(int playerSpeed) {
		if (this.x > this.minX) {
			this.x = this.x - this.speed;
		}
	}
}
