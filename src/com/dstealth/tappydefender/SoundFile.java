package com.dstealth.tappydefender;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;

public class SoundFile {

	final String filename;
	final URL url;
	private static final String resFolder = "/res/sounds/";
	
	private SoundFile(String fileName) {
		this.filename = fileName;
		this.url = SoundFile.class.getResource(resFolder + this.filename);
	}
	
	public static SoundFile create(String filename) {
		SoundFile sf = new SoundFile(filename);
		sf.validateUrl();
		sf.loadSoundIntoMemory();
		return sf;
	}
	
	public void play() { playSound(false); }
	//public void loop() { playSound(true); }
	
	private void playSound(boolean loop)	{ playSound(loop, false); }
	private void loadSoundIntoMemory()		{ if (this.url != null) playSound(false, true); }
	private void validateUrl() {
		if (this.url == null)
			System.err.println(this.filename + ": Could not be loaded as it does not exist.");
	}
	
	private void playSound(boolean loop, boolean loadIntoMemory) {
		if (this.url != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(SoundFile.this.url);
							 Clip clip = AudioSystem.getClip();)
					{
						AudioListener listener = new AudioListener();
						clip.addLineListener(listener);
						clip.open(audioInputStream);
						if (loop)
							clip.loop(Clip.LOOP_CONTINUOUSLY);
						
						if (!loadIntoMemory)
							clip.start();
						listener.waitUntilDone();
					}
					catch (LineUnavailableException e) {
						System.err.println(SoundFile.this.filename + ": Unable to laod clip due to resource restrictions: LineUnavailableException");
					}
					catch (SecurityException e) {
						System.err.println(SoundFile.this.filename + ": Unable to laod clip due to security restrictions: SecurityExceptionException");
					}
					catch (IllegalArgumentException e) {
						System.err.println(SoundFile.this.filename + ": Unable to load clip. System does not support clip instance through any installed mixers: IllegalArgumentException");
					}
					catch (IOException e1) {
						System.err.println(SoundFile.this.filename + ": Unable to load sound file: IOException");
					}
					catch (UnsupportedAudioFileException e1) {
						System.err.println(SoundFile.this.filename + ": Unable to load sound file. URL does not point to a valid audio file: UnsupportedAudioFileException");
					}
					catch (InterruptedException e) {
						System.err.println(SoundFile.this.filename + ": " + e.getMessage());
					}
				}
			}).start();
		}
	}

	protected class AudioListener implements LineListener {
		private boolean done = false;

		@Override
		public synchronized void update(LineEvent event) {
			Type eventType = event.getType();
			if (eventType == Type.STOP || eventType == Type.CLOSE) {
				this.done = true;
				notifyAll();
			}
		}

		public synchronized void waitUntilDone() throws InterruptedException {
			while (!this.done) {
				wait();
			}
		}
	}
}
