package com.dstealth.tappydefender;

import java.util.Random;

public class SpaceDust {
    public int x, y;
    
    private int speed;
    private int minX;
    @SuppressWarnings("unused")
	private int minY;
    private int maxX;
    private int maxY;

    public SpaceDust(int screenX, int screenY) {
        this.minX = 0;
        this.minY = 0;
        this.maxX = screenX;
        this.maxY = screenY;

        Random generator = new Random();
        this.speed = generator.nextInt(10);

        this.x = generator.nextInt(this.maxX);
        this.y = generator.nextInt(this.maxY);
    }

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
}
