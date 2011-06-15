package com.helper;

/**
 *  Plays an MP3 file using the JLayers MP3 library with Javazoom MP3-SPI GPL libraries.
 *  
 *  Based on R.J. Lorimer's MP3 player:http://www.javalobby.org/java/forums/t18465.html
 *  
 */

import java.io.File;
import java.io.IOException;
 
import javax.sound.sampled.*;
 
public class MP3 {
	
	private String filename;
	private AudioInputStream din;
	private SourceDataLine line;
	private boolean mute;
	private BooleanControl volume;
	
	// some lock somewhere...
	Object lock = new Object();
	// some paused variable
	volatile boolean paused = false;
	
	volatile boolean kill = false;

	
	// constructor that takes the name of an MP3 file
    public MP3(String filename) {
        this.filename = filename;
        mute = false;
    }
    
    public void togglemute(){
    	mute = !mute;
    	if(volume!=null)
    		volume.setValue(mute);
    }
    
    public boolean getMute(){
    	return mute;
    }
    
    
    
    public void pause() {
    	paused = true;
    }

    public void resume() {
    	synchronized(lock) {
    		paused = false;
    		lock.notifyAll();
    	}
    }


	public void play() {
		 // run in new thread to play in background
        new Thread() {
            public void run() {
		din = null;
		try {
			File file = new File(filename);
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			line = (SourceDataLine) AudioSystem.getLine(info);
			if(line != null) {
				line.open(decodedFormat);
				if (line.isControlSupported(BooleanControl.Type.MUTE)) {
		            volume = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
		            volume.setValue(mute);
		         }

				byte[] data = new byte[4096];
				// Start
				line.start();
				
				int nBytesRead;
				synchronized (lock) {
					while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
						if(kill)
							break;
						while (paused) {
							if(line.isRunning()) {
								line.stop();
							}
							try {
								lock.wait();
							}
							catch(InterruptedException e) {
							}
						}
					
						if(!line.isRunning()) {
							line.start();
						}
						line.write(data, 0, nBytesRead);
					}
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(din != null) {
				try { din.close(); } catch(IOException e) { }
			}
		}
            }
        }.start();
	}
	
	public void close() {
		kill = true;
		resume();
	}
}
