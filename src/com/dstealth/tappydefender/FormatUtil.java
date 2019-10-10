package com.dstealth.tappydefender;

public class FormatUtil {
    private static final int DISTANCE_MODIFIER = 10;
	
    public static String formatTime(long time) {
        int sec = (int) (time / 1000);
        int dec = (int) ((time % 1000)/100);
        String output = "" + sec + "." + dec;
        return output + "s";
    }

    public static String formatDistance(float dist) {
    	return "" + ((int)(dist/DISTANCE_MODIFIER)) + " km";
    }
    
    public static String formatSpeed(int shipspeed) {
    	final double totalDistance = 1000.0;	// in km
    	final double avgTimeToComplete = 186.5; // in seconds
    	double multiplier = ((totalDistance / avgTimeToComplete) * 10);
    	double speed = ((int)(shipspeed * multiplier))/10.0;
    	return speed + " km/s";
    }
}
