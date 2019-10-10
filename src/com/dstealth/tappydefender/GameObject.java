package com.dstealth.tappydefender;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public abstract class GameObject {

	protected int x, y, minX, minY, maxX, maxY, speed;
	protected BufferedImage bitmap;
	private Rect hitbox;
	
	public GameObject(int width, int height, BufferedImage image) {
		this.x = 0;
		this.y = 0;
		this.minX = 0;
		this.minY = 0;
		this.speed = 1;
		this.bitmap = image;
		this.maxX = width;
		this.maxY = height - this.bitmap.getHeight();
		initializeHitbox();
	}
	
	public abstract void update(int playerSpeed);
	
	// Needs to be called if GameObject's starting positions (x, y) are changed
	protected void initializeHitbox() {
		this.hitbox = new Rect(this.x, this.y, this.bitmap.getWidth(), this.bitmap.getHeight());
	}
	
	// called whenever the object is updated (moves)
	protected void updateHitbox() {
        this.hitbox.left = this.x;
        this.hitbox.top = this.y;
        this.hitbox.right = this.x + this.bitmap.getWidth();
        this.hitbox.bottom = this.y + this.bitmap.getHeight();
	}
	
	public void draw(Graphics g) {
		g.drawImage(this.bitmap, this.x, this.y, null);
	}
	
	public void destroy() {
		// moves it off screen below the minimum screen to allow resetting it 
		this.x = this.minX - 1000;
	}

	public int getSpeed()	{ return this.speed; }
	public Rect getHitbox()	{ return this.hitbox; }
}
