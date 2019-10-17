package com.dstealth.tappydefender.helpers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

import javax.imageio.ImageIO;

public class SpriteSheet {

	private static final String resFolder = "/res/images/";
	private static final int PURPLE = getColorCode(181, 0, 249);
	private BufferedImage bitmap;
	private String imageName;
	private final int spriteWidth;
	private final int spriteHeight;
	// animation variables
	private boolean animHorizontal;
	private int animSpeed;
	private int animTickNum;
	private int animFrameH;
	private int animFrameV;
	private int animTotalFramesH;
	private int animTotalFramesV;
	
	public SpriteSheet(String image, int spriteW, int spriteH) {
		this.imageName = image;
		this.bitmap = createImageFromResource(image);
		this.spriteWidth = spriteW;
		this.spriteHeight = spriteH;
		
		this.animHorizontal = true;
		this.animSpeed = 1;
		this.animFrameH = 0;
		this.animFrameV = 0;
		this.animTickNum = 0;
		
		this.animTotalFramesH = this.bitmap.getWidth() / this.spriteWidth;
		this.animTotalFramesV = this.bitmap.getHeight() / this.spriteHeight;
	}

	// ========== Object Methods ========== //
	public BufferedImage getImage(int col, int row) {
		return getImage(col, row, this.spriteWidth, this.spriteHeight);
	}
	
	public BufferedImage getImage(int col, int row, int width, int height) {
		if (this.bitmap == null)
			return getNullImage(width, height);
		try {
			return this.bitmap.getSubimage(col * this.spriteWidth, row * this.spriteHeight, width, height);
		}
		catch (RasterFormatException e) {
			System.err.println("BufferedImage " + this.imageName + " at col,row (" + col + "," + row + "): " + e.getMessage() + ".");
			return getNullImage(width, height);
		}
		
	}
	
	public void setAnimHorizontal() { this.animHorizontal = true; }
	public void setAnimVertical()	{ this.animHorizontal = false; }
	public void setAnimSpeed(int s) {
		if (s < 1)
			this.animSpeed = 1;
		else
			this.animSpeed = s;
	}
	
	public void animateAt(Graphics g, int x, int y) {
		BufferedImage img = getImage(this.animFrameH, this.animFrameV);
		g.drawImage(img,  x,  y,  null);
		this.updateFrames();
	}
	
	private void updateFrames() {
		this.animTickNum++;
		
		if (this.animTickNum >= this.animSpeed) {
			this.animTickNum = 0;
			
			if (this.animHorizontal) {
				this.animFrameH++;
				if (this.animFrameH >= this.animTotalFramesH)
					this.animFrameH = 0;
			}
			else {
				this.animFrameV++;
				if (this.animFrameV >= this.animTotalFramesV)
					this.animFrameV = 0;
			}
		}
	}
	
	// ========== Factory Methods ========== //
	private static BufferedImage getNullImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				img.setRGB(i, j, PURPLE);
			}
		}
		return img;
	}
	
	public static BufferedImage createBlankImage(int width, int height, int r, int g, int b) {
		int colorCode = getColorCode(r, g, b);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				img.setRGB(i, j, colorCode);
			}
		}
		return img;
	}

	public static BufferedImage createImageFromResource(String image) {
		try {
			return ImageIO.read(SpriteSheet.class.getResource(resFolder + image));
		}
		catch (Exception e) {
			System.err.println("Failed to create BufferedImage from resource: " + image);
			return null;
		}
	}
	
	// ========== Helper Methods ========== //
	private static int getColorCode(int r, int g, int b) {
		final int CR = 65536, CG = 256, CB = 1;
		return (CR * r) + (CG * g) + (CB * b);	
	}

}

