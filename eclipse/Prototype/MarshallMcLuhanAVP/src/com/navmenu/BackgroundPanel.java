package com.navmenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * 
 * Extended JPanel that paints a supplied image for the given x,y location and
 * widht/height values as the background. 
 * 
 */
public class BackgroundPanel extends JPanel{

	private Image image;
	private Dimension size;
	private int x,y;
	public BackgroundPanel(String path, int x, int y,Dimension size)
	{
		this.x = x;
		this.y = y;
		this.size = size;
		try
		{
			image = (new ImageIcon(path).getImage());
		}
		catch(Exception e){/*handled in paintComponent()*/}
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(image != null) 
			g.drawImage(image, x,y,size.width,size.height, this); //(image,location x, location y, size x, size y)
	}

}
