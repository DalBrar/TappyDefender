package com.dstealth.tappydefender;

import java.awt.Graphics;
import java.util.Random;

public class SpaceDust extends GameObject {

    public SpaceDust(int width, int height) {
    	super(width, height, SpriteSheet.createBlankImage(1, 1, 255, 255, 255));

    	// Generate random speed and starting location
        Random generator = new Random();
        this.speed = generator.nextInt(10);

        this.x = generator.nextInt(this.maxX);
        this.y = generator.nextInt(this.maxY);
        
		updateHitbox();
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
    	g.drawImage(this.bitmap, this.x, this.y, null);
    	g.drawImage(this.bitmap, this.x+1, this.y, null);
    	g.drawImage(this.bitmap, this.x-1, this.y, null);
    	g.drawImage(this.bitmap, this.x, this.y+1, null);
    	g.drawImage(this.bitmap, this.x, this.y-1, null);
    }
}
