package com.navmenu;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;



public class BackgroundPanel extends JPanel{

	private Image image;
	public BackgroundPanel(String path)
	{
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
			g.drawImage(image, 0,0,1024,740, this); //(image,location x, location y, size x, size y)
	}
}
