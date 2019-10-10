package com.dstealth.tappydefender;

import java.awt.image.BufferedImage;

public abstract class GameObject {

	public int x, y, minX, minY, maxX, maxY, speed;
	public Rect hitbox;
	public BufferedImage bitmap;
	
	public GameObject(int width, int height, BufferedImage image) {
		this.x = 0;
		this.y = 0;
		this.minX = 0;
		this.minY = 0;
		this.speed = 1;
		this.bitmap = image;
		this.maxX = width;
		this.maxY = height - this.bitmap.getHeight();
		updateHitbox();
	}
	
	// Needs to be called if GameObject's starting positions (x, y) are changed
	protected void updateHitbox() {
		this.hitbox = new Rect(this.x, this.y, this.bitmap.getWidth(), this.bitmap.getHeight());
	}
	
	public abstract void update(int playerSpeed);
}
