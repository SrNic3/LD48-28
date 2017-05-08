package com.fireshield.ld28;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	public static synchronized void play(final String fileName, final boolean isLoop) {

		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(Game.class.getResource("/" + fileName));
					
					clip.open(inputStream);
					if(isLoop)
						clip.loop(500);
					clip.start();
				} catch (Exception e) {
					System.out.println("play sound error: " + e.getMessage() + " for " + fileName);
				}
			}
		}).start();
	}
}