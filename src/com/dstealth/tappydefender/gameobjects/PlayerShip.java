package com.dstealth.tappydefender.gameobjects;

import com.dstealth.tappydefender.helpers.SoundFile;
import com.dstealth.tappydefender.helpers.SpriteSheet;

public class PlayerShip extends GameObject {
    private static final int GRAVITY = -4;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 7;
    
    private int shieldStrength;
    private boolean boosting;
    private SoundFile booster;

	public PlayerShip(int width, int height) {
		super(width, height, SpriteSheet.createImageFromResource("ship.png"));
		this.setBitMask(SpriteSheet.createImageFromResource("bitmask_ship.png"));
		this.x = 25;
		this.y = 50;
		this.boosting = false;
		this.shieldStrength = 2;
		this.booster = SoundFile.create("booster.wav");
		
		initializeHitbox();
	}
	
	public void update() {
		update(this.speed);
	}
	
	/**
	 * playerSpeed param is unused for PlayerShip.
	 */
	@Override
	public void update(int playerSpeed) {
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
        updateHitbox();
	}
	
	@Override
	public void destroy() {
		this.setBitmap(SpriteSheet.createImageFromResource("ship_wrecked.png"));
		this.booster.stopLoop();
	}
	
	public int getShieldStrength()			{ return this.shieldStrength; }
	public void reduceShieldStrength()		{ this.shieldStrength--; }
	public void setBoosting(boolean doBoost) {
		if (!this.boosting && doBoost) {
			this.booster.playLoop();
			this.boosting = true;
		}
		else if (this.boosting && !doBoost) {
			this.booster.stopLoop();
			this.boosting = false;
		}
	}
}
