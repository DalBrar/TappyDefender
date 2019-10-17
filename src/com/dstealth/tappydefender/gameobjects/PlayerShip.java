package com.dstealth.tappydefender.gameobjects;

import java.awt.Graphics;

import com.dstealth.tappydefender.Game;
import com.dstealth.tappydefender.ResourceManager;
import com.dstealth.tappydefender.helpers.SoundFile;
import com.dstealth.tappydefender.helpers.SpriteSheet;

public class PlayerShip extends GameObject {
    private static final int GRAVITY = -4;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 7;

	private static final ResourceManager rm = Game.getResourceManager();
    private int shieldStrength;
    private boolean boosting;
    private SoundFile booster;
    private SpriteSheet boosterflame;
    private static final int BOOSTERFLAME_WIDTH = 24;

	public PlayerShip(int width, int height) {
		super(width, height, rm.g_player);
		this.setBitMask(rm.g_playerM);
		this.x = 25;
		this.y = 50;
		this.boosting = false;
		this.shieldStrength = 2;
		this.booster = rm.s_booster;
		
		initializeHitbox();
		
		this.boosterflame = new SpriteSheet("boosterflame.png", BOOSTERFLAME_WIDTH, 12);
		this.boosterflame.setAnimSpeed(3);
	}
	
	public void update() {
		update(this.speed);
	}
	
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
	public void draw(Graphics g) {
		if (this.boosting)
			this.boosterflame.animateAt(g, this.x-BOOSTERFLAME_WIDTH+1, this.y+17);
		g.drawImage(this.getBitmap(), this.x, this.y, null);
	}
	
	@Override
	public void destroy() {
		this.setBitmap(rm.g_playerW);
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
