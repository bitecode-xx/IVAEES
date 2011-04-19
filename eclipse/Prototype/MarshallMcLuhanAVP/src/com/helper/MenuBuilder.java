package com.helper;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.navmenu.BackgroundPanel;

public class MenuBuilder {
	
	private JButton[] g1, g2,g3,g4;
	private BackgroundPanel t1, t2, t3, t4;
	
	public MenuBuilder(){
		g1 = createBtns("Menus/cityasclassroom/classroom button");
		setSizes(g1,132,100);
		g2 = createBtns("Menus/extensionsofman/extensions button");
		setSizes(g2,110,72);
		g3 = createBtns("Menus/gloablvillage/village button");
		setSizes(g3,151,93);
		g4 = createBtns("Menus/mediumisthemessage/message button");
		setSizes(g4,121,136);
		setcordsg1();
		setcordsg2();
		setcordsg3();
		setcordsg4();
		createThemes();
	}

	private void setcordsg1() {
		g1[0].setLocation(70, 194);
		g1[1].setLocation(262, 52);
		g1[2].setLocation(454, 197);
		g1[3].setLocation(799, 53);
		
	}

	private void setcordsg3() {
		g3[0].setLocation(209, 57);
		g3[1].setLocation(528, 333);
		g3[2].setLocation(718, 176);
		g3[3].setLocation(101, 391);
		
		
	}

	private void setcordsg2() {
		g2[0].setLocation(406, 111);
		g2[1].setLocation(569, 221);
		g2[2].setLocation(284, 364);
		g2[3].setLocation(515, 556);
		
		
		
	}

	private void setcordsg4() {
		g4[0].setLocation(48, 201);
		g4[1].setLocation(571, 268);
		g4[2].setLocation(821, 112);
		g4[3].setLocation(228, 504);
		
		
	}

	private void setSizes(JButton[] g, int w, int h) {
		g[0].setSize(new Dimension(w,h));
		g[1].setSize(new Dimension(w,h));
		g[2].setSize(new Dimension(w,h));
		g[3].setSize(new Dimension(w,h));
		
	}

	private void createThemes() {
		t1 = new BackgroundPanel("Menus/cityasclassroom/classroomiconR.jpg",0,0,new Dimension(1024,768));
		t1.setLayout(null);
		t1.setSize(1024,768);
		t2 = new BackgroundPanel("Menus/extensionsofman/extensionsofmaniconR.jpg",0,0,new Dimension(1024,768));
		t2.setLayout(null);
		t2.setSize(1024,768);
		t3 = new BackgroundPanel("Menus/gloablvillage/globalvillageiconR.jpg",0,0,new Dimension(1024,768));
		t3.setLayout(null);
		t3.setSize(1024,768);
		t4 = new BackgroundPanel("Menus/mediumisthemessage/mediummessageiconR.jpg",0,0,new Dimension(1024,768));
		t4.setLayout(null);
		t4.setSize(1024,768);
		
	}
	private JButton[] createBtns(String path) {
		int os,ro;
		JButton temp;
		JButton[] list;
		list = new JButton[4];
		for(int i = 0;i<4;i++){
			os = i+1;
			ro = i+5;
			temp = new JButton(new ImageIcon(path+os+".png"));
			//temp = new JButton(new ImageIcon(path+ro+".png"));
			//temp.setRolloverIcon(new ImageIcon(path+os+".png"));
			temp.setName(i+"");			
			//temp.setBorder(null);
			list[i] = temp;
		}
		
		return list;
	}
	
	
	public JButton[] getGroup(int i){
		if(i == 1)
			return g1;
		else if(i == 2)
			return g2;
		else if(i == 3)
			return g3;
		else if(i == 4)
			return g4;
		else
			return null;
		
	}
	
	public BackgroundPanel getBack(int i){
		if(i == 1)
			return t1;
		else if(i == 2)
			return t2;
		else if(i == 3)
			return t3;
		else if(i == 4)
			return t4;
		else
			return null;
	}

}
