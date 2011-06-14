package org.pirelenito.multimedia.jmf;

import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;

import org.pirelenito.multimedia.jmf.plugin.IGLTextureRenderer;

import com.physics.PhysicsEngine;

/**
 * Interface to JMF player and has helper functions to work
 * with the GL renderer
 * 
 * @author Paulo Ragonha
 */
public class MoviePlayer implements ControllerListener  {
	
	/**
	 * JMF player
	 */
	private Player player;
	
	/**
	 * Loop flag
	 */
	private boolean loop, rendered, audio;
	private PhysicsEngine linkback;

	public MoviePlayer (String filename, boolean audio) throws Exception {
		Manager.setHint(Manager.PLUGIN_PLAYER, true);
		Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true)); 
		// open the file.
		this.audio = audio;
		player = Manager.createPlayer( new URL("file:" + filename) );
		player.addControllerListener(this);
		player.realize();
		player.prefetch();
		player.addControllerListener(this);
		rendered = false;
		// wait for it to be done.
		while(player.getTargetState() != Player.Prefetched );
		//player.getGainControl().setMute(audio);
	}
	
	/**
	 * @return GL texture renderer
	 */
	public IGLTextureRenderer getRenderer () {
		if(rendered)
			return (IGLTextureRenderer) player.getControl("javax.media.renderer.VideoRenderer");
		else 
			return null;
	}
	
	public void muteAudio(){
		player.getGainControl().setMute(true);
	}
	public boolean getMute(){
		return player.getGainControl().getMute();
	}
	
	public void unMuteAudio(){
		player.getGainControl().setMute(false);
	}

	public void setRender(PhysicsEngine linkback){
		this.linkback = linkback;
	}
	
	public void play() {
		player.start();
		while(player.getTargetState() != Player.Started );
	}
	
	public Time getLen(){
		return player.getDuration();
	}
	
	public void pause() {
		player.stop();
	}
	
	public void stop() {
		pause();
		rewind();
	}
	
	public void rewind() {
		player.setMediaTime(new Time(0));
	}
	
	/**
	 * Set player to auto loop
	 * @param loop
	 */
	public void setLoop (boolean loop) {
		this.loop = loop;
	}
	
	/**
	 * Check current loop condition
	 * @return
	 */
	public boolean isLoop () {
		return loop;
	}

	public void controllerUpdate(ControllerEvent event) {
		// this players is auto loop!
		if (event instanceof EndOfMediaEvent && loop) {
			rewind();
			play();
		}
		else if(event instanceof RealizeCompleteEvent ) {
			rendered = true;
			linkback.setRender(getRenderer());
			player.getGainControl().setMute(audio);
		}
	}

}
