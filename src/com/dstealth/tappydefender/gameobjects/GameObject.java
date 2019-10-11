package com.dstealth.tappydefender.gameobjects;

import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {

	protected int x, y, minX, minY, maxX, maxY, speed;
	private BufferedImage bitmap;
	private BufferedImage bitmask;
	private Rectangle hitbox;
	
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
	
	public void draw(Graphics g) {
		g.drawImage(this.bitmap, this.x, this.y, null);
	}
	
	// Needs to be called if GameObject's starting positions (x, y) are changed
	protected void initializeHitbox() {
		this.hitbox = new Rectangle(this.x, this.y, this.bitmap.getWidth(), this.bitmap.getHeight());
	}
	
	// called whenever the object is updated (moves)
	protected void updateHitbox() {
        this.hitbox.setLocation(this.x, this.y);
	}
	
	protected void setBitmap(BufferedImage image) {
		this.bitmap = image;
	}
	
	protected void setBitMask(BufferedImage image) {
		this.bitmask = image;
	}
	
	public void destroy() {
		// moves it off screen below the minimum screen to allow resetting it 
		this.x = this.minX - 1000;
	}
	
	public boolean collidesWith(GameObject obj) {
		// check if most general collision exists
		if (this.hitbox.intersects(obj.getHitbox())) {
			return collidesPixelPreciselyWith(obj);
		}
		return false;
	}

	public int getSpeed()				{ return this.speed; }
	public Rectangle getHitbox()		{ return this.hitbox; }
	protected BufferedImage getBitmap()	{ return this.bitmap; }
	
	private boolean collidesPixelPreciselyWith(GameObject obj) {
		final int TRANSPARENT = 16777215;
		
		int xstart = (int) Math.max(this.hitbox.getMinX(), obj.hitbox.getMinX()),
			ystart = (int) Math.max(this.hitbox.getMinY(), obj.hitbox.getMinY()),
			xend   = (int) Math.min(this.hitbox.getMaxX(), obj.hitbox.getMaxX()),
			yend   = (int) Math.min(this.hitbox.getMaxY(), obj.hitbox.getMaxY());
		
		for (int iy = ystart; iy < yend; iy++) {
			for (int ix = xstart; ix < xend; ix++) {
				int lx1 = (int) (ix - this.hitbox.getMinX()),
					ly1 = (int) (iy - this.hitbox.getMinY()),
					lx2 = (int) (ix - obj.hitbox.getMinX()),
					ly2 = (int) (iy - obj.hitbox.getMinY());
				
				try {
					int pixA = this.bitmask.getRGB(lx1, ly1);
					int pixB = obj.bitmask.getRGB(lx2, ly2);
					
					if (pixA != TRANSPARENT && pixA == pixB)
						return true;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					// will occur when contact with enemy ship that  moved partially out of screen
					return false;
				}
			}
		}
		return false;
	}
}
