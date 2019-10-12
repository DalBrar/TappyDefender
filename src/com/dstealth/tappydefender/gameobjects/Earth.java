package com.dstealth.tappydefender.gameobjects;

import com.dstealth.tappydefender.Game;
import com.dstealth.tappydefender.ResourceManager;

public class Earth extends GameObject {
	private static final ResourceManager rm = Game.getResourceManager();

	public Earth(int width, int height) {
		super(width, height, rm.g_earth);
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
