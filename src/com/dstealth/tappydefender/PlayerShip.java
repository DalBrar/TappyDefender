package com.dstealth.tappydefender;

public class PlayerShip extends GameObject {
    private static final int GRAVITY = -4;
    private static final int MIN_SPEED = 1;
    public static final int MAX_SPEED = 7;

    public int strength;
    public boolean boosting;

	public PlayerShip(Game game, int width, int height) {
		super(width, height, SpriteSheet.createImageFromResource("ship.png"));
		this.x = 25;
		this.y = 50;
		this.boosting = false;
		this.strength = 2;
		
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
