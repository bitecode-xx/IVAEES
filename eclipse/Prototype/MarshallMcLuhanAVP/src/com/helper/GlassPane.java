package com.helper;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * We have to provide our own glass pane so that it can paint.
 */
@SuppressWarnings("unused")
public class GlassPane extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1360018254627150638L;
	private JLabel handOne,handTwo;
	private Dimension small = new Dimension(40,61),big = new Dimension(85,130);
    
    public GlassPane(){
    	this.setLayout(null);
    	handOne = new JLabel(new ImageIcon("rh1.png"));
    	handOne.setSize(small);
    	handOne.setLocation(0, 0);
    	handOne.setVisible(false);
    	
    	
    	handTwo = new JLabel(new ImageIcon("lh1.png"));
    	handTwo.setSize(small);
    	handTwo.setLocation(0, 0);
    	handTwo.setVisible(false);
    	
    	this.add(handOne);
    	this.add(handTwo);
    	this.setSize(new Dimension(1024,768));
    }
    public void setHandOne(Point p) {
    	handOne.setLocation(p.x-28,p.y-55);
    }

    public void setHandTwo(Point p) {
    	handTwo.setLocation(p.x-28,p.y-55);
    }

    public void mouseClicked(MouseEvent e) {
    	setHandOne(new Point(e.getX(),e.getY()));

    }
    
    public void enableHandOne(){
    	handOne.setVisible(true);
    }
    
    public void enableHandTwo(){
    	handTwo.setVisible(true);
    }
    
    public void disableHandOne(){
    	handOne.setVisible(false);
    }
    
    public void disableHandTwo(){
    	handTwo.setVisible(false);
    }
    
    public void activeHandOne(){
    	handOne.setIcon(new ImageIcon("rh2.png"));
    	handOne.setSize(big);
    }
    
    public void activeHandTwo(){
    	handTwo.setIcon(new ImageIcon("lh2.png"));
    	handTwo.setSize(big);
    }
    
    public void releaseHandOne(){
    	handOne.setIcon(new ImageIcon("rh1.png"));
    	handOne.setSize(small);
    }
    public void releaseHandTwo(){
    	handTwo.setIcon(new ImageIcon("lh1.png"));
    	handTwo.setSize(small);
    }

}

