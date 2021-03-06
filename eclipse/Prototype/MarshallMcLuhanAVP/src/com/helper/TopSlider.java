package com.helper;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.physics.PhysicsEngine;

/**
 * 
 * Custom JPanel class to create a sliding effect selection system that contains
 * the various McLuhan Media Objects.
 * 
 * @author Jozef
 *
 */
@SuppressWarnings("unused")
public class TopSlider extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 450673558116706554L;
	private File[] images, text;
	private ImageIcon[] img, txt;
	private Component[] list;
	private JButton left, right;
	private Dimension nav = new Dimension(45,45),nav2 = new Dimension(75,75), btn = new Dimension(100,100);
	private int index,alt, start;
	private PhysicsEngine eng;
	private boolean type;
	private boolean[] types;
	//private Vector<Component> listAlt;
	private Vector<ImageIcon> lists;
	private TransitionButton[] btns;
	private int[] active;
	private Timer progress;
	
	public TopSlider(){
		this.setBackground(Color.BLACK);
		((FlowLayout)this.getLayout()).setAlignment(FlowLayout.LEFT);
	}
	
	/*
	 * Assigns the required file data for the selected themes images and text media
	 */
	public void letsFight(String[] images, String[] text){
		this.invalidate();
		this.text = new File[text.length];
		this.images = new File[images.length];
		for(int i=0;i<text.length;i++)
			this.text[i] = new File(text[i]);
		for(int i=0;i<images.length;i++)
			this.images[i] = new File(images[i]);
		index =0;
		genLists();
		setupPanel();
		setupListAlt();
		this.validate();
	}

	/*
	 * Assigns engine to recieve rendering calls
	 */
	public void setEngine(PhysicsEngine context){
		this.eng = context;
	}
	
	/*
	 * Deassigns engine to recieve rendering calls
	 */
	public void desetEngine(){
		this.eng = null;
		//listAlt = null;
		lists=null;
	}
	
	public PhysicsEngine getEng(){
		return eng;
	}
	
	/*
	 * Creates the 5 media 2 nav button objects and assigns their
	 * motion directions 
	 * 
	 */
	private void setupPanel() {
		left = new JButton(new ImageIcon("First_Button1.jpg"));
		left.setPreferredSize(nav2);
		left.setOpaque(false);
		left.setContentAreaFilled(false);
		left.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				moveListAlt(false);
				progress.restart();
			}
		});
		
		right = new JButton(new ImageIcon("Last_Button1.jpg"));
		right.setPreferredSize(nav2);
		right.setOpaque(false);
		right.setContentAreaFilled(false);
		right.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				moveListAlt(true);
				progress.restart();
			}
		});

		
	}

	/*
	 * Depricated
	 *
	 * Creates the buttons for all the media files
	 * and assigns them to the TopSlider panel. 
	 * 
	 
	private void setupList() {
		JPanel grid = gridPan();
		list = new Component[img.length+txt.length];
		for (int i=0;i<img.length;i++){
			list[i] = buttonIMG(img[i],i);
		}
		int j = img.length;
		for(int i=0;i<txt.length;i++){
			list[i+j] = buttonTXT(txt[i],i);
		}
		for(int i =0; i < 5; i++){
			grid.add(list[i]);
		}
		index = 4;
		
		//int count = 0;
	//	while(count < 5 ){
		//	if(index != list.length){
		//		grid.add(list[index]);
		//		index++;
	//			count++;
		//	}
	//		else{
	//			index=0;
	//		}
//		}
		this.invalidate();
		this.removeAll();
		this.add(left);
		this.add(grid);
		this.add(right);
		this.validate();
			
	}*/
	
	/*
	 * Creates the buttons for all the media files
	 * and assigns them to the TopSlider panel. 
	 * 
	 *
	private void setupListAlt() {
		listAlt = new Vector<Component>(img.length+txt.length);
		active = new int[5];
		JPanel grid = gridPan();
		
		for (int i=0;i<img.length;i++){
			listAlt.add(buttonIMG(img[i],i));
		}
		int j = img.length;
		for(int i=0;i<txt.length;i++){
			listAlt.add(buttonTXT(txt[i],i));
		}
		for(int i =0; i < 5; i++){
			grid.add(listAlt.elementAt(i));
			active[i] = i;
		}
		destroylists();
		this.invalidate();
		this.removeAll();
		this.add(left);
		this.add(grid);
		this.add(right);
		this.validate();

	}*/private void setupListAlt() {
		lists = new Vector<ImageIcon>();
		JPanel grid = gridPan();
		active = new int[img.length+txt.length];
		types = new boolean[img.length+txt.length];
		
		for (int i=0;i<img.length;i++){
			lists.add(img[i]);
			active[i] = i;
			types[i] = true;
		}
		for(int i=0;i<txt.length;i++){
			lists.add(txt[i]);
			active[img.length+i]=i;
			types[img.length+i]=false;
		}
		btns = new TransitionButton[5];
		for(int i =0; i < 5; i++){
			btns[i] = new TransitionButton(lists.elementAt(i),true,i,this,200*i);
		}
		for(int i =0; i < 5; i++){
			grid.add(btns[i]);
		}
		alt =0;
		destroylists();
		this.invalidate();
		this.removeAll();
		this.add(left);
		this.add(grid);
		this.add(right);
		this.validate();

	}
	
	private void destroylists() {
		for(int i=0;i<text.length;i++)
			this.text[i] = null;
		for(int i=0;i<images.length;i++)
			this.images[i] = null;
		for(int i=0;i<txt.length;i++)
			this.txt[i] = null;
		for(int i=0;i<img.length;i++)
			this.img[i] = null;
		
	}
	
	public void startTimer(){
		ActionListener loop = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				moveListAlt(true);
			}
		};

		progress = new Timer(15000,loop);
		progress.setRepeats(true);
		progress.start();
	}
	
	public void stopTimer(){
		progress.stop();
	}
	
	public void restart(){
		progress.restart();
	}

	/*
	 * Evaluates the button positions and 
	 * if true moves the images ahead by 5
	 * if false move the images back by 5 accounting
	 * for out of bounds index and wrap around
	 * 
	 *
	private void moveListAlt(boolean dir){
		JPanel grid = gridPan();
		if(dir){
			for(int i = 0; i< 5; i++){
				if(i == 4){
					if(active[i] == listAlt.size()-1)
						active[i] = 0;
					else
						active[i] = active[i]+1;
				}
				else
					active[i] = active[i+1];
			}
		}
		else{
			for(int i = 4; i>-1; i--){
				if(i == 0){
					if(active[i] == 0)
						active[i] = listAlt.size()-1;
					else
						active[i] = active[i]-1;
				}
				else
					active[i] = active[i-1];
			}
		}
		for(int i =0; i < 5; i++){
			grid.add(listAlt.elementAt(active[i]));
		}
		this.invalidate();
		this.removeAll();
		this.add(left);
		this.add(grid);
		this.add(right);
		this.validate();
	}*/
	private void moveListAlt(boolean dir){
		if(dir){
			if(alt+1 > lists.size()-1)
				start = 0;
			else
				start = alt+1;
			for(int i=0;i<5;i++){
				if(alt+1 > lists.size()-1)
					alt = 0;
				else
					alt = alt+1;
				btns[i].updates(lists.elementAt(alt));
				btns[i].indexed(active[alt]);
				btns[i].type(types[alt]);
				btns[i].begin();
			}
			alt = start;
			
		}
		else{
			if(alt-1 <0)
				alt = lists.size()-1;
			else
				alt = alt-1;
			for(int i=4;i>-1;i--){
				if(i == 0){
					btns[i].updates(lists.elementAt(alt));
					btns[i].indexed(active[alt]);
					btns[i].type(types[alt]);
				}
				else{
					btns[i].updates(btns[i-1].getup());
					btns[i].indexed(btns[i-1].getind());
					btns[i].type(btns[i-1].gettype());
				}
			}
			for(int i=0;i<5;i++){
				btns[i].begin();
			}	
		}
	}
	
	
	/*
	 * Depricated
	 
	private void moveList(boolean dir){
		JPanel grid = gridPan();
		if(list.length > 5){
			  if(!dir){
				  if(list.length<10)
					  index = index - list.length;
				  else
			          index = index - 10;
				  
				  if(index<0){
					  index= list.length + index;
				  }
			}
			int count = 0;
			while(count < 5 ){
				if(index != list.length){
					grid.add(list[index]);
					index++;
					count++;
				}
				else{
					index=0;
				}
			}

			this.invalidate();
			this.removeAll();
			this.add(left);
			this.add(grid);
			this.add(right);
			this.validate();
		}
	}/*

	/*
	 * Initialize the button to launch a specified image media object
	 * in the attached engine. 
	 * 
	 *
	private Component buttonIMG(ImageIcon img,int index) {
		JButton temp = new JButton(img);
		temp.setName(index+"");
		temp.setPreferredSize(btn);
		temp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int i = Integer.parseInt(((JButton)e.getSource()).getName());
				eng.pushTimerImg(i);
			}
		});


		return temp;
	}*/
	
	/*
	 * Initialize the button to launch the specified text media
	 * object in the attached engine.
	 * 
	 *
	private Component buttonTXT(ImageIcon img,int index) {
		JButton temp = new JButton(img);
		temp.setName(index+"");
		temp.setPreferredSize(btn);
		temp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int i = Integer.parseInt(((JButton)e.getSource()).getName());
				eng.pushTimerTxt(i);
			}
		});

		
		return temp;
	}*/
	
	/*
	 * creates the scrolling grid panel
	 * 
	 */
	private JPanel gridPan(){
		JPanel grid = new JPanel();
		GridLayout scroll = new GridLayout(0,5);
		scroll.setHgap(65);
		scroll.setVgap(5);
		grid.setLayout(scroll);
		grid.setSize(750,200);
		grid.setLocation(90,0);
		grid.setBackground(Color.BLACK);
		
		return grid;
	}

	/*
	 * create the image lists for the supplied media file objects
	 */
	private void genLists() {
		img = new ImageIcon[images.length];
		for(int i=0;i<images.length;i++){
			img[i] = modImage(images[i]);

		}
		txt = new ImageIcon[text.length];
		for(int i=0;i<text.length;i++){
			txt[i] = modImage(text[i]);

		}

	}
	
	/*
	 * resize the image to match the button sizes
	 */
	private ImageIcon modImage(File images2){
		ImageIcon newIcon;
		newIcon = new ImageIcon(images2.getPath());
		Image img = newIcon.getImage();
		Image newimg = img.getScaledInstance(85, 85,  java.awt.Image.SCALE_SMOOTH);
		
		return new ImageIcon(newimg);
	}

	
	


}
