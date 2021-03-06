package com.dstealth.tappydefender.gameobjects;

import java.awt.Graphics;
import java.util.Random;

import com.dstealth.tappydefender.Game;
import com.dstealth.tappydefender.ResourceManager;

public class SpaceDust extends GameObject {
	private static final ResourceManager rm = Game.getResourceManager();

    public SpaceDust(int width, int height) {
    	super(width, height, rm.g_dust);

    	// Generate random speed and starting location
        Random generator = new Random();
        this.speed = generator.nextInt(10);

        this.x = generator.nextInt(this.maxX);
        this.y = generator.nextInt(this.maxY);
        
		initializeHitbox();
    }

    @Override
	public void update (int playerSpeed) {
        this.x -= playerSpeed;
        this.x -= this.speed;

        // respawn space dust
        if (this.x < this.minX) {
            this.x = this.maxX;
            Random generator = new Random();
            this.y = generator.nextInt(this.maxY);
            this.speed = generator.nextInt(5);
        }
    }
    
    @Override
    public void draw(Graphics g) {
    	g.drawImage(this.getBitmap(), this.x, this.y, null);
    	g.drawImage(this.getBitmap(), this.x+1, this.y, null);
    	g.drawImage(this.getBitmap(), this.x-1, this.y, null);
    	g.drawImage(this.getBitmap(), this.x, this.y+1, null);
    	g.drawImage(this.getBitmap(), this.x, this.y-1, null);
    }
}
