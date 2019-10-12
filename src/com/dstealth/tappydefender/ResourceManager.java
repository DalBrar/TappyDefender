package com.dstealth.tappydefender;

import java.awt.Font;
import java.awt.image.BufferedImage;

import com.dstealth.tappydefender.helpers.SoundFile;
import com.dstealth.tappydefender.helpers.SpriteSheet;

public class ResourceManager {
    // Font settings
	public final String font				= "consolas";
	public final int f_size_TITLE			= 60;
	public final int f_size_SUBTITLE		= 40;
	public final int f_size_MENU_TEXT		= 25;
	public final int f_size_MENU_SUBTEXT	= 18;
	public final int f_size_HUD				= 15;
	public final int f_size_CONTROLS		= 14;
	public final int f_size_DEBUG			= 12;
	public final Font f_title				= new Font(font, Font.BOLD, f_size_TITLE);
	public final Font f_subtitle			= new Font(font, Font.BOLD, f_size_SUBTITLE);
	public final Font f_menu_text			= new Font(font, Font.BOLD, f_size_MENU_TEXT);
	public final Font f_menu_subtext		= new Font(font, Font.BOLD, f_size_MENU_SUBTEXT);
	public final Font f_hud					= new Font(font, Font.BOLD, f_size_HUD);
	public final Font f_controls			= new Font(font, Font.BOLD, f_size_CONTROLS);
	public final Font f_debug				= new Font(font, Font.PLAIN, f_size_DEBUG);

    // Sounds
	public final SoundFile s_booster		= SoundFile.create("booster.wav");
    public final SoundFile s_menu			= SoundFile.create("mainmenu.wav");
    public final SoundFile s_gameplay		= SoundFile.create("gameplay.wav");
    public final SoundFile s_start			= SoundFile.create("start.wav");
    public final SoundFile s_win			= SoundFile.create("win.wav");
    public final SoundFile s_bump			= SoundFile.create("bump.wav");
    public final SoundFile s_destroyed		= SoundFile.create("destroyed.wav");
    public final SoundFile s_pause			= SoundFile.create("pause.wav");
    
    // Graphic Images
    public final BufferedImage g_player		= SpriteSheet.createImageFromResource("ship.png");
    public final BufferedImage g_playerM	= SpriteSheet.createImageFromResource("bitmask_ship.png");
    public final BufferedImage g_playerW	= SpriteSheet.createImageFromResource("ship_wrecked.png");
    public final BufferedImage g_enemy1		= SpriteSheet.createImageFromResource("enemy1.png");
    public final BufferedImage g_enemy2		= SpriteSheet.createImageFromResource("enemy2.png");
    public final BufferedImage g_enemy3		= SpriteSheet.createImageFromResource("enemy3.png");
    public final BufferedImage g_enemy1M	= SpriteSheet.createImageFromResource("bitmask_enemy1.png");
    public final BufferedImage g_enemy2M	= SpriteSheet.createImageFromResource("bitmask_enemy2.png");
    public final BufferedImage g_enemy3M	= SpriteSheet.createImageFromResource("bitmask_enemy3.png");
    public final BufferedImage g_dust		= SpriteSheet.createBlankImage(1, 1, 255, 255, 255);
    public final BufferedImage g_earth		= SpriteSheet.createImageFromResource("earth.png");
    public final BufferedImage g_title		= SpriteSheet.createImageFromResource("background.jpg");

	public ResourceManager() {}
}