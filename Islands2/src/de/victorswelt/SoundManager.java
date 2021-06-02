package de.victorswelt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {
	public static final Sound SOUND_WAVE_1 = new Sound("sound/wave1.wav");
	public static final Sound SOUND_WAVE_2 = new Sound("sound/wave2.wav");
	public static final Sound SOUND_WAVE_3 = new Sound("sound/wave3.wav");
	
	public static final Sound SOUND_PLANE_1 = new Sound("sound/plane1.wav");
	
	public static final Sound SOUND_BUTTON_PRESSED = new Sound("sound/button2.au");
	
	
	
	private Mixer.Info mixerInfo[];
	private Mixer mixer;
	
	boolean initializedSuccessfully = false, enabled = true;
	
	private SoundManager() {
		
		// initialize the mixer
		mixerInfo = AudioSystem.getMixerInfo();
		if(mixerInfo.length > 0) {
			mixer = AudioSystem.getMixer(mixerInfo[0]);
			initializedSuccessfully = true;
			
			playSound(SOUND_WAVE_1);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			playSound(SOUND_PLANE_1);
		
		}
		
		
	}
	
	public void playSound(final Sound sound) {
		if(initializedSuccessfully && enabled)
			
			new Thread(new Runnable() {
				public void run() {
					try {
						SourceDataLine source;
						
						source = (SourceDataLine) mixer.getLine(sound.info);
						source.open();
						source.start();
						
						source.write(sound.data, 0, sound.data.length);
						
						source.flush();
						source.close();
						
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			}).start();
	}
	
	public void playSound(final AudioInputStream ais) {
		if(initializedSuccessfully && enabled)
		
		new Thread(new Runnable() {
			public void run() {
				try {
					AudioFormat format = ais.getFormat();
					
					
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					SourceDataLine source;
					
					source = (SourceDataLine) mixer.getLine(info);
					source.open();
					source.start();
					
					byte[] ba = new byte[1024];
					int bytesRead = 0;
					
					while(true) {
						bytesRead = ais.read(ba, 0, 1024);
						if(bytesRead == -1) {
							break;
							
						}
						source.write(ba, 0, bytesRead);
					}
					
					source.flush();
					source.close();
					
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		
	}
	
	//
	private static SoundManager soundManager;
	public static void init() {soundManager = new SoundManager();}
	public static SoundManager getInstance() {return soundManager;}
}

class Sound {
	byte data[];
	AudioFormat format;
	DataLine.Info info;
	
	Sound(String resourcePath) {
		try {
			
			// create an audio input stream
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(resourcePath));
			
			// get the info about the audio
			format = ais.getFormat();
			info = new DataLine.Info(SourceDataLine.class, format);
			
			// write the file to an in-memory array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int bytesRead;
			while(true) {
				bytesRead = ais.read(buf);
				if(bytesRead == -1)
					break;
				baos.write(buf, 0, bytesRead);
			}
			baos.flush();
			data = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		SoundManager.getInstance().playSound(this);
	}
}