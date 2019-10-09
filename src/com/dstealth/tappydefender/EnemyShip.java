package com.dstealth.tappydefender;

import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemyShip {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 5 - MIN_SPEED + 1;

    public BufferedImage bitmap;
    public int x, y;
    public int speed = 1;
    public Rect hitbox;

    // Detect enemies leaving the screen
    private int minX;
    private int maxX;

    // Spawn enemies within the screen bounds
    @SuppressWarnings("unused")
	private int minY;
    private int maxY;

	@SuppressWarnings("unused")
	public EnemyShip(Game game, int width, int height, int type) {
		if (type == 3)
			this.bitmap = SpriteSheet.createImageFromResource("enemy3.png");
		else if (type == 2)
			this.bitmap = SpriteSheet.createImageFromResource("enemy2.png");
		else
			this.bitmap = SpriteSheet.createImageFromResource("enemy.png");
		
		this.minX = 0 - this.bitmap.getWidth();
		this.minY = 0;
		this.maxX = width;
		this.maxY = height - (this.bitmap.getHeight() / 2);
		
		Random gen = new Random();
		this.speed = gen.nextInt(MAX_SPEED) + MIN_SPEED;
		
		this.x = width;
		this.y = gen.nextInt(this.maxY) - (this.bitmap.getHeight() / 2);
		
		this.hitbox = new Rect(this.x, this.y, this.bitmap.getWidth(), this.bitmap.getHeight());
	}
	
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
        this.hitbox.left = this.x;
        this.hitbox.top = this.y;
        this.hitbox.right = this.x + this.bitmap.getWidth();
        this.hitbox.bottom = this.y + this.bitmap.getHeight();
    }
}
