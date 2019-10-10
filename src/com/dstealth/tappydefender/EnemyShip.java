package com.dstealth.tappydefender;

import java.util.Random;

public class EnemyShip extends GameObject {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 5 - MIN_SPEED + 1;

	@SuppressWarnings("unused")
	public EnemyShip(Game game, int width, int height, int type) {
		super(width, height, SpriteSheet.createImageFromResource("enemy.png"));
		
		if (type == 3)
			this.bitmap = SpriteSheet.createImageFromResource("enemy3.png");
		else if (type == 2)
			this.bitmap = SpriteSheet.createImageFromResource("enemy2.png");
		
		// set top of screen so that half a ship can appear
		this.minX = 0 - this.bitmap.getWidth();
		// set bottom of screen so that half a ship can appear
		this.maxY = height - (this.bitmap.getHeight() / 2);
		
		// Generate random speed and starting positions
		Random gen = new Random();
		this.speed = gen.nextInt(MAX_SPEED) + MIN_SPEED;
		
		this.x = width;
		this.y = gen.nextInt(this.maxY) - (this.bitmap.getHeight() / 2);

		initializeHitbox();
	}

	@Override
    public void update(int playerSpeed) {
        // Move to the left
        this.x -= playerSpeed;
        this.x -= this.speed;

        // respawn when off screen
        if (this.x < (this.minX - this.bitmap.getWidth())) {
            Random generator = new Random();
            this.speed = generator.nextInt(MAX_SPEED) + MIN_SPEED;
            this.x = this.maxX;
            this.y = generator.nextInt(this.maxY) - (this.bitmap.getHeight() / 2);
        }

        // Refresh hit box location
        updateHitbox();
    }
}
