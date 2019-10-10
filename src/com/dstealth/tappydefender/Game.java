package com.dstealth.tappydefender;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -6480320424545836573L;
    private static final int NUM_DUST = 20;
    private static final int ENEMY_STARTING_OFFSET = 300;
	
	volatile boolean playing = false;
    volatile boolean collision = false;
    volatile boolean isPaused = false;
    public boolean isMainMenu = true;
    public boolean gameEnded;
    Thread gameThread = null;
	
    private int screenX;
    private int screenY;

    // Sound stuff
    private SoundFile s_start;
    private SoundFile s_win;
    private SoundFile s_bump;
    private SoundFile s_destroyed;
    private SoundFile s_pause;
    
    // Game Objects
    public PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;
    private EnemyShip enemy4;
    // Make some random space dust
    private ArrayList<SpaceDust> dustList;

    // HUD variables
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime = 999000l;
    private int flashTime = 0;

	public Game(int width, int height) {
		Dimension dim = new Dimension(width, height);
		this.setSize(dim);
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);
		this.setMinimumSize(dim);
		this.screenX = width;
		this.screenY = height;
		
		// Initialize Listeners
		this.addKeyListener(new KeyInput(this));
		
        // Initialize sounds
        this.s_bump		 = SoundFile.create("bump.wav");
        this.s_destroyed = SoundFile.create("destroyed.wav");
        this.s_start 	 = SoundFile.create("start.wav");
        this.s_win		 = SoundFile.create("win.wav");
        this.s_pause	 = SoundFile.create("pause.wav");
	}
	
	public void showMenu() {
        // Get main menu drawing
        this.gameThread = new Thread(this);
        this.gameThread.start();
	}
	
	public void start() {
		if (MainWindow.doDebug)
			System.out.println(this.getWidth() + " - " + this.getHeight());
		
		this.isMainMenu = false;
		this.isPaused = false;
		
        // Initialize our player ship
        this.player = new PlayerShip(this, this.getWidth(), this.getHeight());
        this.enemy1 = new EnemyShip(this, this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 1);
        this.enemy2 = new EnemyShip(this, this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 2);
        this.enemy3 = new EnemyShip(this, this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 3);
        Random r = new Random();
        int shipType = r.nextInt(3)+1;
        this.enemy4 = new EnemyShip(this, this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), shipType);

        // Initialize space dust
		this.dustList = new ArrayList<SpaceDust>();
        for (int i = 0; i < NUM_DUST; i++) {
            SpaceDust dust = new SpaceDust(this.screenX, this.screenY);
            this.dustList.add(dust);
        }

        // Reset time and distance
        this.distanceRemaining = 10000; // 1000 km
        this.timeTaken = 0;
        this.timeStarted = System.currentTimeMillis();

        this.gameEnded = false;
        this.s_start.play();
        
		this.resume();
	}
	
	public void stop() {
		this.gameEnded = true;
		pause();
	}
	
	// this is the game loop
	@Override
	public void run() {
		// run main menu screen
		while (this.isMainMenu) {
			drawMainMenu();
		}
		// run game screens
        while(!this.isMainMenu && this.playing) {
        	update();
            draw();
            control();
        }
	}

    // Clean up our thread if the game is interrupted or the player quits
    private void pause() {
        this.playing = false;
        try {
            this.gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    private void resume() {
        this.playing = true;
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }
    
    // Toggle Pause/Resume
    public void togglePause() {
    	this.s_pause.play();
    	this.isPaused = (this.isPaused) ? false : true;
        this.timeStarted = System.currentTimeMillis();
    }
    
    // ==================================================
    //		Game Loop Functions
    // ==================================================

	// tick method
	private void update() {
		// update Game if game not paused
		if (!this.isPaused) {
			updateCollisions();
			updateGameObjects();
			updateHUD();
	        updateVictoryCondition();
		}
	}
	
	// render method
	private void draw() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();

		drawBackground(g);
		drawGameObjects(g);
        drawHUDs(g);
		
		g.dispose();
		bs.show();
	}
	
	// thread speed control method
	private void control() {
        if (!this.gameEnded && this.player.getShieldStrength() < 0) {
            this.gameEnded = true;
            this.player.destroy();;
            this.s_destroyed.play();
        }
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {

        }
	}

    // ==================================================
    //		Update Functions
    // ==================================================
	
	private void updateCollisions() {
        if (Rect.intersects(this.player.getHitbox(), this.enemy1.getHitbox())) {
        	this.enemy1.destroy();; 
        	this.collision = true;
        }
        if (Rect.intersects(this.player.getHitbox(), this.enemy2.getHitbox())) {
        	this.enemy2.destroy();;
        	this.collision = true;
        }
        if (Rect.intersects(this.player.getHitbox(), this.enemy3.getHitbox())) {
        	this.enemy3.destroy();;
        	this.collision = true;
        }
        if (Rect.intersects(this.player.getHitbox(), this.enemy4.getHitbox())) {
        	this.enemy4.destroy();;
        	this.collision = true;
        }

        if (!this.gameEnded && this.collision) {
            this.player.reduceShieldStrength();
            this.s_bump.play();
            System.out.println("taking a hit");
        }
	}
	
	private void updateGameObjects() {
        if (!this.gameEnded) {
            // Update the player
            this.player.update();
            // Update the enemies
            this.enemy1.update(this.player.getSpeed());
            this.enemy2.update(this.player.getSpeed());
            this.enemy3.update(this.player.getSpeed());
            this.enemy4.update(this.player.getSpeed());
        }
        // Update space dust
        for (SpaceDust dust : this.dustList) {
            dust.update(this.player.getSpeed());
        }
	}
	
	private void updateHUD() {
		if (!this.gameEnded) {
            // subtract distance to home planet based on current speed
            this.distanceRemaining -= this.player.getSpeed();
            // How long has the player been flying?
            this.timeTaken += System.currentTimeMillis() - this.timeStarted;
            this.timeStarted = System.currentTimeMillis();
        }
	}
	
	private void updateVictoryCondition() {
		if (this.distanceRemaining < 0) {
            this.s_win.play();
            // check for new fastest time
            if (this.timeTaken < this.fastestTime) {
                this.fastestTime = this.timeTaken;
                // save new high score
                //editor.putLong(fastestStr, fastestTime);
                //editor.commit();
            }

            //avoid ugly negative numbers in the HUD
            this.distanceRemaining = 0;

            // end the game
            this.gameEnded = true;
            this.player.setBoosting(false);;
        }
	}
    
    private void updateFlashTime() {
    	this.flashTime++;
    	if (this.flashTime >= 10)
    		this.flashTime = 0;
    }

    // ==================================================
    //		Draw Functions
    // ==================================================
	
	private void drawMainMenu() {
		updateFlashTime();
		
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		BufferedImage bg = SpriteSheet.createImageFromResource("background.jpg");
		Image img = bg.getScaledInstance(this.screenX, this.screenY, Image.SCALE_DEFAULT);
		g.drawImage(img, 0, 0, null);

    	final int fontSizeL = 40;
    	final int fontSizeS = 18;
    	final int fontSizeT = 14;
		Font fontL = new Font("consolas", 1, fontSizeL);
		Font fontS = new Font("consolas", 1, fontSizeS);
		Font fontT = new Font("consolas", 1, fontSizeT);
		g.setColor(new Color(255, 255, 255, 200));
		
		if (this.flashTime <= 5)
			drawStringCenter(g, fontL, "Press ENTER to Start", 180);
		drawStringCenter(g, fontS, "CONTROLS:", 370);
		drawStringCenter(g, fontT, "SPACE = Fly/Boost", 390);
		drawStringCenter(g, fontT, "ENTER = Pause    ", 405);
		
		g.dispose();
		bs.show();
	}
	
	private void drawBackground(Graphics g) {
		// draw red background if collision else black background
		if (!this.gameEnded && this.collision) {
			g.setColor(Color.red);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
            this.collision = false;
		} else {
			g.setColor(Color.black);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	
	private void drawGameObjects(Graphics g) {
		// draw space dust
		g.setColor(Color.white);
        for (SpaceDust dust : this.dustList) {
        	dust.draw(g);
        }

        // Draw the player
        this.player.draw(g);
        
        // Draw the enemies
        if (!this.gameEnded) {
        	this.enemy1.draw(g);
        	this.enemy2.draw(g);
        	this.enemy3.draw(g);
        	this.enemy4.draw(g);
        }
	}

    // Draw appropriate HUD
	private void drawHUDs(Graphics g) {
        if (!this.gameEnded) {
        	drawGameplayHUD(g);
            if (this.isPaused)
            	drawPauseHUD(g);
        }
        else
        	drawGameOverScreen(g);
	}
	
	private void drawGameplayHUD(Graphics g) {
    	final int fontSize = 15;
		Font font = new Font("consolas", 1, fontSize);
		g.setFont(font);

		// Top text
		if (this.player.getShieldStrength() > 1)
			g.setColor(new Color(0, 255, 0, 225));
		else
    	if (this.player.getShieldStrength() == 1)
    		g.setColor(new Color(255, 255, 0, 225));
    	else
    		g.setColor(new Color(255, 0, 0, 225));
		g.drawString("Shields: " + this.player.getShieldStrength(), 5, fontSize);
		
		g.setColor(new Color(150, 150, 150, 225));
		drawStringCenter(g, font, "Time: " + FormatUtil.formatTime(this.timeTaken), fontSize);
		g.drawString("Fastest: " + FormatUtil.formatTime(this.fastestTime), this.screenX - 130, fontSize);
		// bottom text
		drawStringCenter(g, font, "Distance: " + FormatUtil.formatDistance(this.distanceRemaining), this.screenY - fontSize);
		g.drawString("Speed: " + FormatUtil.formatSpeed(this.player.getSpeed()), this.screenX - 130, this.screenY - fontSize);
	}
	
	private void drawGameOverScreen(Graphics g) {
    	final int fontSize1 = 25;
    	final int fontSize2 = 60;
		Font fontS = new Font("consolas", 1, fontSize1);
		Font fontL = new Font("consolas", 1, fontSize2);
		g.setColor(new Color(255, 255, 255, 200));
		
		drawStringCenter(g, fontL, "Game Over", fontSize2);
		drawStringCenter(g, fontS, "Fastest Time: " + FormatUtil.formatTime(this.fastestTime), 100);
		drawStringCenter(g, fontS, "This Time: " + FormatUtil.formatTime(this.timeTaken), 130);
		if (this.distanceRemaining == 0) {
    		g.setColor(new Color(0, 255, 0, 200));
			drawStringCenter(g, fontS, "Made It Home!", 160);
		} else {
    		g.setColor(new Color(255, 0, 0, 200));
			drawStringCenter(g, fontS, "Distance Remaining: " + FormatUtil.formatDistance(this.distanceRemaining), 160);
		}
		
		g.setColor(new Color(255, 255, 255, 200));
		drawStringCenter(g, fontS, "Press ENTER to replay!", 250);
	}
	
	private void drawPauseHUD(Graphics g) {
		updateFlashTime();
    	
    	final int fontSizeS = 25;
    	final int fontSizeL = 60;
		Font fontS = new Font("consolas", 1, fontSizeS);
		Font fontL = new Font("consolas", 1, fontSizeL);
		g.setColor(new Color(255, 255, 255, 200));
		
		if (this.flashTime <= 5)
			drawStringCenter(g, fontL, "Paused", fontSizeL+50);
		drawStringCenter(g, fontS, "Press ENTER to resume", 180);
		drawStringCenter(g, fontS, "Press ESC to restart", 240);
	}
    
    private int getStringCenter(Graphics g, Font font, String str) {
    	FontMetrics metrics = g.getFontMetrics(font);
    	return (this.screenX - metrics.stringWidth(str)) / 2;
    }
    
    private void drawStringCenter(Graphics g, Font font, String str, int y) {
		g.setFont(font);
		g.drawString(str, getStringCenter(g, font, str), y);
    }
}
