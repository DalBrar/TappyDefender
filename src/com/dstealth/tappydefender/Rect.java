package com.dstealth.tappydefender;

public class Rect {
	public int left, top, right, bottom;

	public Rect(int x, int y, int width, int height) {
		this.left = x;
		this.top = y;
		this.right = width;
		this.bottom = height;
	}
	
	public static boolean intersects(Rect box1, Rect box2) {
		if (pointIntersects(box1.left,  box1.top, box2) ||
			pointIntersects(box1.left,  box1.bottom, box2) ||
			pointIntersects(box1.right, box1.bottom, box2) ||
			pointIntersects(box1.right, box1.top, box2))
			return true;
		return false;
	}
	
	private static boolean pointIntersects(int x, int y, Rect box) {
		if (x >= box.left  && y >= box.top &&
			x <= box.right && y <= box.bottom)
				return true;
		return false;
	}
}
