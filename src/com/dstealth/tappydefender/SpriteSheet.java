package com.dstealth.tappydefender;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

import javax.imageio.ImageIO;

public class SpriteSheet {

	private static final String resFolder = "/res/images/";
	private static final int PURPLE = getColorCode(181, 0, 249);
	private BufferedImage bitmap;
	private String imageName;
	private int spriteWidth;
	private int spriteHeight;
	
	public SpriteSheet(String image, int spriteW, int spriteH) {
		this.imageName = image;
		this.bitmap = createImageFromResource(image);
		this.spriteWidth = spriteW;
		this.spriteHeight = spriteH;
	}

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
	
	private static int getColorCode(int r, int g, int b) {
		final int CR = 65536, CG = 256, CB = 1;
		return (CR * r) + (CG * g) + (CB * b);	
	}
}

