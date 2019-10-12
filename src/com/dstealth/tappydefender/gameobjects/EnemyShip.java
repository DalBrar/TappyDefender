package com.dstealth.tappydefender.gameobjects;

import java.util.Random;

import com.dstealth.tappydefender.Game;
import com.dstealth.tappydefender.ResourceManager;

public class EnemyShip extends GameObject {
	private static final ResourceManager rm = Game.getResourceManager();

	public EnemyShip(int width, int height, int type) {
		super(width, height, rm.g_enemy1);
		
		Random gen = new Random();
		if (type == 0)
			type = gen.nextInt(3) + 1;
		
		// set specific AI type
		setAIType(type);
		
		// set top of screen so that half a ship can appear
		this.minX = 0 - this.getBitmap().getWidth();
		// set bottom of screen so that half a ship can appear
		this.maxY = height - (this.getBitmap().getHeight() / 2);
		
		this.x = width;
		this.y = gen.nextInt(this.maxY) - (this.getBitmap().getHeight() / 2);

		initializeHitbox();
	}

	@Override
    public void update(int playerSpeed) {
        // Move to the left
        this.x -= playerSpeed;
        this.x -= this.speed;

        // respawn when off screen
        if (this.x < (this.minX - this.getBitmap().getWidth())) {
            Random gen = new Random();
            setAIType(gen.nextInt(3)+1);
            this.x = this.maxX;
            this.y = gen.nextInt(this.maxY) - (this.getBitmap().getHeight() / 2);
        }

        // Refresh hit box location
        updateHitbox();
    }
	
	private void setAIType(int type) {
		Random gen = new Random();
		int baseSpeed = gen.nextInt(2);
		if (type == 3) {
			this.setBitmap(rm.g_enemy3);
			this.setBitMask(rm.g_enemy3M);
			this.speed = baseSpeed + 4;	// 4 - 5
		} else if (type == 2) {
			this.setBitmap(rm.g_enemy2);
			this.setBitMask(rm.g_enemy2M);
			this.speed = baseSpeed + 2;	// 2 - 3
		} else {
			this.setBitmap(rm.g_enemy1);
			this.setBitMask(rm.g_enemy1M);
			this.speed = baseSpeed;		// 0 - 1
		}
	}
}
