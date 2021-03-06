package com.dstealth.tappydefender;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import com.dstealth.tappydefender.gameobjects.Earth;
import com.dstealth.tappydefender.gameobjects.EnemyShip;
import com.dstealth.tappydefender.gameobjects.PlayerShip;
import com.dstealth.tappydefender.gameobjects.SpaceDust;
import com.dstealth.tappydefender.helpers.FormatUtil;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -6480320424545836573L;
    private static final int NUM_DUST = 20;
    private static final int ENEMY_STARTING_OFFSET = 300;
    
    // Game Loop constants & variables
	private static final int ONE_SECOND = 1000000000; // in nanoseconds
	private static final double TARGET_TPS = 60;
	private static final double TARGET_FPS = 60;
	private static final double OPTIMAL_UPDATE_TIME = ONE_SECOND / TARGET_TPS;
	private static final double OPTIMAL_RENDER_TIME = ONE_SECOND / TARGET_FPS;
	int fps = (int)TARGET_TPS;
	int tps = (int)TARGET_FPS;
	
	volatile boolean playing = false;
    volatile boolean collision = false;
    volatile boolean isPaused = false;
    public boolean isMainMenu = true;
    public boolean gameEnded;
    private boolean isVictory;
    Thread gameThread = null;
	
    private int screenX;
    private int screenY;
    
    // Resources
    private static final ResourceManager rm = new ResourceManager();
    Image bgImg;
    
    // Game Objects
    public PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;
    private EnemyShip enemy4;
    // Make some random space dust
    private ArrayList<SpaceDust> dustList;
    private Earth earth;

    // HUD variables
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime = 999000l;
    private int flashCounter = 0;
    private boolean flashTime = false;

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
		
		this.bgImg = rm.g_title.getScaledInstance(this.screenX, this.screenY, Image.SCALE_DEFAULT);
	}
	
	// this is the game loop using Variable Timestep algorithm
	@Override
	public void run() {
		//gameloopVaraibleTimestep();
		gameloopFixedTimestep();
		//gameloopMinecraft();
	}
	
	public void startMenu() {
        // Get main menu drawing
		this.isMainMenu = true;
		rm.s_menu.playLoop();
		this.startThread();
	}
	
	public void startGame() {
		if (MainWindow.doDebug)
			System.out.println(this.getWidth() + " - " + this.getHeight());
		
		this.isMainMenu = false;
		this.isPaused = false;
		
		rm.s_gameplay.playLoop();
		
        // Initialize our player ship
        this.player = new PlayerShip(this.getWidth(), this.getHeight());
        this.enemy1 = new EnemyShip(this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 1);
        this.enemy2 = new EnemyShip(this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 2);
        this.enemy3 = new EnemyShip(this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 3);
        this.enemy4 = new EnemyShip(this.getWidth()+ENEMY_STARTING_OFFSET, this.getHeight(), 0);
        this.earth  = new Earth(this.getWidth(), this.getHeight());

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
        this.isVictory = false;
        this.playing = true;
        rm.s_start.play();
        
		this.startThread();
	}

    // Make a new thread and start it
    private void startThread() {
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }

    // Clean up our thread if the game is interrupted or the player quits
	public void stop() {
		this.isMainMenu = false;
		this.gameEnded = true;
        this.playing = false;
        // stop sound loops
        rm.s_menu.stopLoop();
        rm.s_gameplay.stopLoop();
        try {
        	this.player.setBoosting(false);
        } catch (NullPointerException e) {}
        // stop thread
        try {
            this.gameThread.join();
        } catch (InterruptedException e) {

        }
	}
    
    // Toggle Pause/Resume
    public void togglePause() {
    	rm.s_pause.play();
    	this.isPaused = (this.isPaused) ? false : true;
        this.timeStarted = System.currentTimeMillis();
    }
    
    // ==================================================
    //		Game Loop Algorithms
    // ==================================================

    private void gameloopFixedTimestep() {
    	//At the very most we will update the game this many times before a new render.
    	//If you're worried about visual hitches more than perfect timing, set this to 1.
    	final int MAX_UPDATES_BEFORE_RENDER = 5;
    	
    	//We will need the last update time.
    	double lastUpdateTime = System.nanoTime();
    	//Store the last time we rendered.
    	double lastRenderTime = System.nanoTime();
    	
    	// for FPS
    	long lastLoopTime = System.nanoTime();
    	long lastFpsTime = 0;
		int frameCount = 0;
		int tickCount = 0;


		// keep looping round till the game ends using Exclusive-Or (^)
    	while (this.isMainMenu ^ this.playing)
    	{
    		long now = System.nanoTime();
    		int updateCount = 0;
    		frameCount++;

			//Do as many game updates as we need to, potentially playing catchup.
			while( now - lastUpdateTime > OPTIMAL_UPDATE_TIME && updateCount < MAX_UPDATES_BEFORE_RENDER )
			{
				update();
				lastUpdateTime += OPTIMAL_UPDATE_TIME;
				updateCount++;
				tickCount++;
			}

			//If for some reason an update takes forever, we don't want to do an insane number of catchups.
			//If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
			if ( now - lastUpdateTime > OPTIMAL_UPDATE_TIME)
			{
				lastUpdateTime = now - OPTIMAL_UPDATE_TIME;
			}

			//Render. To do so, we need to calculate interpolation for a smooth render.
			//float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
	        render();
			
			lastRenderTime = now;

			//Update the frames we got.
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			lastFpsTime += updateLength;
			if (lastFpsTime >= ONE_SECOND) {
				this.fps = frameCount;
				this.tps = tickCount;
				lastFpsTime = 0;
				frameCount = 0;
				tickCount = 0;
			}

			//Yield until it has been at least the target time between renders. This saves the CPU from hogging.
			while ( now - lastRenderTime < OPTIMAL_RENDER_TIME && now - lastUpdateTime < OPTIMAL_UPDATE_TIME)
			{
				Thread.yield();

				//This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
				//You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
				//FYI on some OS's this can cause pretty bad stuttering.
				try {Thread.sleep(1);} catch(Exception e) {} 

				now = System.nanoTime();
			}
    	}
    }
    
    /*
     * Game Loop Algorithms
    @SuppressWarnings("unused")
	private void gameloopVaraibleTimestep() {
    	long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		long lastFpsTime = 0;
		int frameCount = 0;
		
		// keep looping round till the game ends using Exclusive-Or (^)
		while (this.isMainMenu ^ this.playing)
		{
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			// Use deltaTime for in-game logic
			//double deltaTime = updateLength / ((double)OPTIMAL_TIME);
			
			// update the frame counter
			lastFpsTime += updateLength;
			frameCount++;
			
			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= ONE_SECOND)
			{
				this.fps = frameCount;
				this.tps = this.fps;
				lastFpsTime = 0;
				frameCount = 0;
			}
			
			// update & draw
	        update();
	        render();
			
			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give 
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
			try{
				final int ms = 1000000;
				long curr = System.nanoTime();
				long remainingTime = (now-curr + OPTIMAL_TIME) / ms;
				if (MainWindow.doDebug) {
					System.out.println("" + (now/ms) + " - " + (curr/ms) + " = " + ((now - curr)/ms) + " + " + (OPTIMAL_TIME/ms));
					System.out.println("Sleeping for " + remainingTime + " ms");
				}
				if (remainingTime > 0)
					Thread.sleep(remainingTime);
			} catch (IllegalArgumentException | InterruptedException e) {}
		}
    }

    @SuppressWarnings("unused")
    private void gameloopMinecraft() {
    	long lastTime = System.nanoTime();
    	double amountOfTicks = 60.0;
    	double OPTIMAL_TIME = 1000000000 / amountOfTicks;
    	double delta = 0;
    	long timer = System.currentTimeMillis();
    	long lastLoopTime = System.nanoTime();
    	long lastFpsTime = 0;
    	int frameCount = 0;
    	int tickCount = 0;

		// keep looping round till the game ends using Exclusive-Or (^)
    	while (this.isMainMenu ^ this.playing) {
    		long now = System.nanoTime();
    		delta += (now - lastTime)/ OPTIMAL_TIME;
    		lastTime = now;
    		frameCount++;
    		
			// play catch up if lagging behind
			while (delta >= 1.0) {
				update();
				delta--;
				tickCount++;
			}
			// render/draw graphics
			render();

			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			lastFpsTime += updateLength;
			if (lastFpsTime >= ONE_SECOND) {
				this.fps = frameCount;
				this.tps = tickCount;
				lastFpsTime = 0;
				frameCount = 0;
				tickCount = 0;
			}
    	}
    	
    }
    */
    
    // ==================================================
    //		Game Functions
    // ==================================================

	// tick method
	private void update() {
		updateFlashTimer();
		// update Game if game not paused
		if (!this.isMainMenu && this.playing && !this.isPaused) {
			updateCollisions();
			updateGameObjects();
			updateHUD();
	        updateWinLoseCondition();
		}
	}
	
	// render method
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();

		if (this.isMainMenu)
			drawMainMenu(g);
		else if (this.playing) {
			drawBackground(g);
			drawGameObjects(g);
	        drawHUDs(g);
		}
		
		renderFPS(g);
		
		g.dispose();
		bs.show();
	}
	
	// render and draw FPS
	private void renderFPS(Graphics g) {
		g.setColor(new Color(255, 255, 255, 100));
		g.setFont(rm.f_debug);
		g.drawString("TPS:" + this.tps + " FPS:" + this.fps, 1, this.getHeight()-rm.f_size_DEBUG-2);
	}

    // ==================================================
    //		Update Functions
    // ==================================================
	
	private void updateCollisions() {
        if (this.player.collidesWith(this.enemy1)) {
        	this.enemy1.destroy();; 
        	this.collision = true;
        }
        if (this.player.collidesWith(this.enemy2)) {
        	this.enemy2.destroy();;
        	this.collision = true;
        }
        if (this.player.collidesWith(this.enemy3)) {
        	this.enemy3.destroy();;
        	this.collision = true;
        }
        if (this.player.collidesWith(this.enemy4)) {
        	this.enemy4.destroy();;
        	this.collision = true;
        }

        if (!this.gameEnded && this.collision) {
            this.player.reduceShieldStrength();
            rm.s_bump.play();
            this.collision = false;
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
        else if (this.isVictory)
        	this.earth.update(this.player.getSpeed());
        
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
	
	private void updateWinLoseCondition() {
		// Check for Lose condition
        if (!this.gameEnded && this.player.getShieldStrength() < 0) {
            this.gameEnded = true;
            this.player.destroy();;
            rm.s_destroyed.play();
            this.player.setBoosting(false);
        }
        // Check for Victory condition
        else if (this.distanceRemaining < 0) {
            rm.s_win.play();
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
            this.isVictory = true;
            this.player.setBoosting(false);
        }
	}
	
	private void updateFlashTimer() {
		this.flashCounter++;
		if (this.flashCounter >= 22) {
			this.flashCounter = 0;
			this.flashTime = (this.flashTime) ? false : true;
		}
	}

    // ==================================================
    //		Draw Functions
    // ==================================================
	
	private void drawMainMenu(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.drawImage(this.bgImg, 0, 0, null);
		g.setColor(new Color(255, 255, 255, 200));
		
		if (this.flashTime)
			drawStringCenter(g, rm.f_subtitle, "Press ENTER to Start", 180);
		drawStringCenter(g, rm.f_menu_subtext, "CONTROLS:", 370);
		drawStringCenter(g, rm.f_controls, "SPACE = Fly/Boost", 390);
		drawStringCenter(g, rm.f_controls, "ENTER = Pause    ", 405);
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
        else if (this.isVictory)
        	this.earth.draw(g);
	}

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
		g.setFont(rm.f_hud);

		// Top text
		if (this.player.getShieldStrength() > 1)
			g.setColor(new Color(0, 255, 0, 225));
		else
    	if (this.player.getShieldStrength() == 1)
    		g.setColor(new Color(255, 255, 0, 225));
    	else
    		g.setColor(new Color(255, 0, 0, 225));
		g.drawString("Shields: " + this.player.getShieldStrength(), 5, rm.f_size_HUD);
		
		g.setColor(new Color(150, 150, 150, 225));
		drawStringCenter(g, rm.f_hud, "Time: " + FormatUtil.formatTime(this.timeTaken), rm.f_size_HUD);
		g.drawString("Fastest: " + FormatUtil.formatTime(this.fastestTime), this.screenX - 130, rm.f_size_HUD);
		// bottom text
		drawStringCenter(g, rm.f_hud, "Distance: " + FormatUtil.formatDistance(this.distanceRemaining), this.screenY - rm.f_size_HUD);
		g.drawString("Speed: " + FormatUtil.formatSpeed(this.player.getSpeed()), this.screenX - 130, this.screenY - rm.f_size_HUD);
	}
	
	private void drawGameOverScreen(Graphics g) {
		g.setColor(new Color(255, 255, 255, 200));
		
		drawStringCenter(g, rm.f_title, "Game Over", rm.f_size_TITLE);
		drawStringCenter(g, rm.f_menu_text, "Fastest Time: " + FormatUtil.formatTime(this.fastestTime), 100);
		drawStringCenter(g, rm.f_menu_text, "This Time: " + FormatUtil.formatTime(this.timeTaken), 130);
		if (this.distanceRemaining == 0) {
    		g.setColor(new Color(0, 255, 0, 200));
			drawStringCenter(g, rm.f_menu_text, "Made It Home!", 160);
		} else {
    		g.setColor(new Color(255, 0, 0, 200));
			drawStringCenter(g, rm.f_menu_text, "Distance Remaining: " + FormatUtil.formatDistance(this.distanceRemaining), 160);
		}
		
		g.setColor(new Color(255, 255, 255, 200));
		drawStringCenter(g, rm.f_menu_text, "Press ENTER to replay!", 250);
		drawStringCenter(g, rm.f_menu_text, "Press ESC to return to Menu", 280);
	}
	
	private void drawPauseHUD(Graphics g) {
		g.setColor(new Color(255, 255, 255, 200));
		
		if (this.flashTime)
			drawStringCenter(g, rm.f_title, "Paused", rm.f_size_TITLE+50);
		drawStringCenter(g, rm.f_menu_text, "Press ENTER to resume", 180);
		drawStringCenter(g, rm.f_menu_text, "Press ESC to return to Menu", 240);
	}
     
    private void drawStringCenter(Graphics g, Font f, String str, int y) {
		g.setFont(f);
		g.drawString(str, getStringCenter(g, f, str), y);
    }
    
    private int getStringCenter(Graphics g, Font f, String str) {
    	FontMetrics metrics = g.getFontMetrics(f);
    	return (this.screenX - metrics.stringWidth(str)) / 2;
    }
    
    
    // ==================================================
    //		Getters & Setters
    // ==================================================
    
    public static ResourceManager getResourceManager() { return rm; }
}
