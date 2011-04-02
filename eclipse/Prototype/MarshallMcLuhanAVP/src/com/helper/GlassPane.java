package com.helper;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * We have to provide our own glass pane so that it can paint.
 */
public class GlassPane extends JPanel {
    private Image rhand,lhand;
    private Point rhp, lhp;
    private JLabel handOne;
    
    public GlassPane(){
    	this.setLayout(null);
    	rhand = new ImageIcon("rh1.png").getImage();
    	handOne = new JLabel(new ImageIcon("rh1.png"));
    	lhand = new ImageIcon("lh1.png").getImage();
    	this.setSize(new Dimension(1024,768));
    }
    /*
    protected void paintComponent(Graphics g) {
    	System.err.println("rawwwtr");
    	if(rhp != null){
    		g.drawImage(rhand, rhp.x-25, rhp.y-25, null);

    	}
    	
    	if(lhp != null){
    		g.drawImage(lhand, lhp.x-25, lhp.y-25, null);
    	}
        
    }
     */
    public void setHandOne(Point p) {
        rhp = p;
       // repaint();
    }
    
    public void setHandTwo(Point p) {
        lhp = p;
      //  repaint();
    }
   
}

