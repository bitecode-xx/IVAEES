package com.navmenu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;
import com.physics.PhysicsEngine;


public class McLuhanMain extends JFrame{
	
	private String[] DIRS = {"City as Classroom","Extensions of Man","Global Village",
			"Homage to Marshall McLuhan","Hot and Cool Media","Laws of Media","McLuhan Pics",
			"Media Ecology","Privacy and Identity in the Electronic Age","The Medium is the Message"
			,"The Medium is the Massage"};

	private JFrame frame;
	private GLCanvas canvas;
	//TODO convert glnav to class to handle menu grabbing/interaction in gl scene. Might need to be added as an extension to the main gl scene
	private JPanel menu,mnav,glnav,p1, p2;
	
	private JButton page2, page1, pmen;
	
	private Dimension size = new Dimension(1024,768);
	
	private FPSAnimator animator;
	private PhysicsEngine app;
	
	private Timer quepush;
	
	public McLuhanMain() {
        frame = this;
        frame.setName("McLuhan Server");
        frame.setSize(size);
        
        menu = new JPanel();
        
        CardLayout system = new CardLayout();
        menu.setLayout(system);
        
        initSelections();
        initMNav();
        initGLNav();
        
      
        menu.add("Main One",p1);
        menu.add("Main Two",p2);
        
        frame.getContentPane().add(BorderLayout.CENTER,menu);
        frame.getContentPane().add(BorderLayout.EAST,mnav);
        frame.getContentPane().add(BorderLayout.WEST,glnav);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        

	}


	private void initSelections() {
		JButton temp;
		FlowLayout flowtop,flowbottom;
		JPanel flowp1,flowp2,flowp3,flowp4;
		
		flowp1 = new JPanel();
		flowp2 = new JPanel();
		flowp3 = new JPanel();
		flowp4 = new JPanel();
		
		flowtop = new FlowLayout();
		flowtop.setHgap(50);
		flowtop.setVgap(100);
		
		flowbottom = new FlowLayout();
		flowbottom.setHgap(50);
		flowbottom.setVgap(50);
		
		flowp1.setLayout(flowtop);
		flowp1.setBackground(Color.BLACK);
		flowp2.setLayout(flowbottom);
		flowp2.setBackground(Color.BLACK);
		
		flowp3.setLayout(flowtop);
		flowp3.setBackground(Color.BLACK);
		flowp4.setLayout(flowbottom);
		flowp4.setBackground(Color.BLACK);
		
		p1 = new JPanel();
		p1.setLayout(new GridLayout(2,1));
		p1.setSize(size);
		p1.setBackground(Color.BLACK);
		
		p2 = new JPanel();
		p2.setLayout(new GridLayout(2,1));
		p2.setSize(size);
		p2.setBackground(Color.BLACK);
		
		
        
		for(int i=0;i<DIRS.length;i++){
			temp = new JButton("<html>"+DIRS[i]+"</html>");
			temp.setName(DIRS[i]);
			temp.setPreferredSize(new Dimension(241,206));
			
			temp.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae) {
					page1.setEnabled(false);
					page1.setVisible(false);
					page2.setEnabled(false);
					page2.setVisible(false);
					pmen.setEnabled(true);
					pmen.setVisible(true);
					validate();
					initCanvas("McLuhan/"+((JButton)ae.getSource()).getName());
					menu.add("ogl",canvas);
					menu.validate();
					activateAnimation();
					((CardLayout)menu.getLayout()).show(menu, "ogl");
				}});
			if(i < 3)
				flowp1.add(temp);
			else if( i < 6 )
				flowp2.add(temp);
			else if(i < 9)
				flowp3.add(temp);
			else
				flowp4.add(temp);
		}
	
		
		p1.add(flowp1);
		p1.add(flowp2);
		p2.add(flowp3);
		p2.add(flowp4);
		
	}
	
	private void initGLNav(){
		pmen = new JButton(new ImageIcon("First_Button1.jpg"));
		pmen.setPreferredSize(new Dimension(50,50));
		pmen.setOpaque(false);
		pmen.setContentAreaFilled(false);
		pmen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				pmen.setEnabled(false);
				pmen.setVisible(false);
				page1.setEnabled(false);
				page1.setVisible(false);
				page2.setEnabled(true);
				page2.setVisible(true);
				((CardLayout)menu.getLayout()).show(menu, "Main One");
				menu.remove(canvas);
				canvas = null;
				animator.stop();
				menu.validate();
				quepush.stop();
				
			}
		});
		
		pmen.setEnabled(false);
		pmen.setVisible(false);
		
		glnav = new JPanel();
		glnav.setBackground(Color.black);
		glnav.setLayout(new FlowLayout());
		((FlowLayout)glnav.getLayout()).setVgap(675);
		
		glnav.add(pmen);
	}
	
	private void initMNav(){
		
		mnav = new JPanel();
		mnav.setBackground(Color.black);
		mnav.setLayout(new FlowLayout());
		((FlowLayout)mnav.getLayout()).setVgap(675);
		
		
		page1 = new JButton(new ImageIcon("First_Button1.jpg"));
		page1.setPreferredSize(new Dimension(50,50));
		page1.setOpaque(false);
		page1.setContentAreaFilled(false);
		
		page2 = new JButton(new ImageIcon("Last_Button1.jpg"));
		page2.setPreferredSize(new Dimension(50,50));
		page2.setOpaque(false);
		page2.setContentAreaFilled(false);
		
		page1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				page1.setEnabled(false);
				page1.setVisible(false);
				page2.setEnabled(true);
				page2.setVisible(true);
				((CardLayout)menu.getLayout()).show(menu, "Main One");	
			}
		});
	
		page2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				page2.setEnabled(false);
				page2.setVisible(false);
				page1.setEnabled(true);
				page1.setVisible(true);
				((CardLayout)menu.getLayout()).show(menu, "Main Two");	
			}
		});
				
		page1.setEnabled(false);
		page1.setVisible(false);
		page2.setEnabled(true);
		page2.setVisible(true);
		
		mnav.add(page1);
		mnav.add(page2);
	}

	private void activateAnimation() {
		animator = new FPSAnimator(60);
        animator.add(canvas);
        animator.start();
        
	}

	private void initCanvas(String path) {
		GLProfile.initSingleton(true);
		GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
        
        app = new PhysicsEngine(path);
        canvas.addGLEventListener(app);
        canvas.addKeyListener(app);
        canvas.addMouseListener(app);
        canvas.addMouseMotionListener(app);
        
        canvas.requestFocus();
        
        ActionListener timeout = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				app.callTimer();
				canvas.display();
			}
		};
		quepush = new Timer(8000,timeout);
		quepush.setRepeats(true);
		quepush.start();
	}
}
