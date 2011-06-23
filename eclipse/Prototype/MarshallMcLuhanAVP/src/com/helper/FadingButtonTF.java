package com.helper;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.Animator.Direction;
import org.jdesktop.animation.timing.Animator.RepeatBehavior;
import org.jdesktop.animation.timing.TimingTarget;
/*
 * FadingButtonTF.java
 *
 * Created on May 3, 2007, 7:20 AM
 *
 * Copyright (c) 2007, Sun Microsystems, Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 *
 * @author Chet
 */
public class FadingButtonTF extends JButton 
        implements ActionListener, TimingTarget {

    float alpha = 0.0f,beta = 0.0f;                 // current opacity of button
    Animator animator;                  // for later start/stop actions
    int animationDuration = 400; 	// each cycle will take 2 seconds
    BufferedImage buttonImage = null;
    private ImageIcon image;
    private ActionListener stop, start;
    private boolean mute;
    
    /** Creates a new instance of FadingButtonTF */
    public FadingButtonTF(ImageIcon im) {
        super(im);
        image = im;
        mute = false;
        setOpaque(false);
        this.setContentAreaFilled(false);
        listeners();
        animator = new Animator(animationDuration/1, this);
        animator.setStartFraction(0.0f);
        animator.setStartDirection(Direction.FORWARD);
        addActionListener(this);
    }
    
    public boolean isMuted(){
    	return mute;
    }
    
    public void setmute(boolean mute){
    	this.mute = mute;
    }
    
    
    private void listeners(){
    	start = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				animator.start();
				alpha = 1.0f;
				Timer t = new Timer(animationDuration,stop);
	    		t.setRepeats(false);
	    		t.start();
			}
		};
		stop = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				animator.stop();
			}
		};  
    }
    
    public void paint(Graphics g) {
        // Create an image for the button graphics if necessary
        if (buttonImage == null || buttonImage.getWidth() != getWidth() ||
                buttonImage.getHeight() != getHeight()) {
            buttonImage = getGraphicsConfiguration().
                    createCompatibleImage(getWidth(), getHeight());
        }
        Graphics gButton = buttonImage.getGraphics();
        gButton.setClip(g.getClip());
        
        //  Have the superclass render the button for us
        super.paint(gButton);
        
        // Make the graphics object sent to this paint() method translucent
        Graphics2D g2d  = (Graphics2D)g;
        if(Float.isNaN(alpha)){
        	//System.err.println("Alpha Value: "+alpha);
        	alpha = beta;
        }
        else
        	beta = alpha;
        AlphaComposite newComposite = 
        	AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(newComposite);
        
        // Copy the button's image to the destination graphics, translucently
        g2d.drawImage(buttonImage, 0, 0, null);
    }
    
    /**
     * This method receives click events, which start and stop the animation
     */
    public void actionPerformed(ActionEvent ae) {
   
    }
    // Ununsed MouseListener implementations
    public void begin() {
    	if (!animator.isRunning()) {
    		//this.setVisible(true);
    		Timer t = new Timer(800,start);
    		t.setRepeats(false);
    		t.start();
    	}
    }
    public void end() {
    }
    public void repeat() {}
    
    public void disable(){
    	alpha = 0.0f;
    	beta = 0.0f;
    }
    
    /**
     * TimingTarget implementation: this method sets the alpha of our button
     * to be equal to the current elapsed fraction of the animation
     */
    public void timingEvent(float fraction) {
        alpha = fraction;
        // redisplay our cbutton
        repaint();
    }

}
