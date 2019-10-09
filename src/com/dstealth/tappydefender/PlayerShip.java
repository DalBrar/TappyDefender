package com.dstealth.tappydefender;
import java.awt.image.BufferedImage;

public class PlayerShip {
    private static final int GRAVITY = -4;
    private static final int MIN_SPEED = 1;
    public static final int MAX_SPEED = 7;

    public BufferedImage bitmap;
    public int x, y;
    public Rect hitbox;
    public int speed = 0;
    public int strength;
    public boolean boosting;

    private int minY;
    private int maxY;

	public PlayerShip(Game game, int width, int height) {
		this.x = 25;
		this.y = 50;
		this.speed = 1;
		this.bitmap = SpriteSheet.createImageFromResource("ship.png");
		this.boosting = false;
		this.strength = 2;		
		this.minY = 0;
		this.maxY = height - this.bitmap.getHeight();
		this.hitbox = new Rect(this.x, this.y, this.bitmap.getWidth(), this.bitmap.getHeight());
	}
	
	public void update() {
        // are we boosting?
        if (this.boosting) {
            // speed up
        	this.speed += 1;
        } else {
            // slow down
        	this.speed -= 5;
        }

        // Constrain top speed
        if (this.speed > MAX_SPEED)
        	this.speed = MAX_SPEED;

        // Never stop completely
        if (this.speed < MIN_SPEED)
        	this.speed = MIN_SPEED;

        // move the ship up or down
        this.y -= this.speed + GRAVITY;

        // But don't let the ship stray off the screen
        if (this.y < this.minY)
        	this.y = this.minY;

        if (this.y > this.maxY)
        	this.y = this.maxY;

        // Refresh hit box location
        this.hitbox.left = this.x;
        this.hitbox.top = this.y;
        this.hitbox.right = this.x + this.bitmap.getWidth();
        this.hitbox.bottom = this.y + this.bitmap.getHeight();
	}
	
	public void shipWrecked() {
		this.bitmap = SpriteSheet.createImageFromResource("ship_wrecked.png");
	}
	
	public void animate() {
		if (this.boosting) {
			// temp
			this.bitmap = SpriteSheet.createImageFromResource("ship.png");
			//TODO: add separate boosting effects based on speed
		} else
			this.bitmap = SpriteSheet.createImageFromResource("ship.png");
	}
}
