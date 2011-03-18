package com.physics;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;

import javax.jws.Oneway;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.GLU;

import com.communication.Engine_Server;
import com.jogamp.opengl.impl.x11.glx.GLX;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;

/**
 * 
 * Secondary physics engine implementation which deals with the grabber effect feature of the
 * MIME system. 
 * 
 * 
 */
public class PhysicsGrabber implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private double moving = 0;

	private Texture textureactive;	

	private PhysicsMesh pmeshactive;

	private ParticleSystem physics;

	private int framecount = 0;
	private PointGravity pgrav;

	private static GLCanvas canvas;

	private long start, updated, finished, frameInterval;
	private double afps, aphysics, arendering, afree;

	private int width = 800, height = 800;
	private int mousex = 0, mousey = 0;
	private boolean dragging = false;
	private int mouse = -1;

	private double mx = -0.6, my = 0;

	private PositionableConstraint constraint1;
	private PositionableConstraint constraint2;
	private PositionableConstraint constraint3;

	private Vec2D newpos1, newpos2, newpos3;

	private String source;


	private Timer quepush = null;



	public PhysicsGrabber(){
		source = "McLuhan poster small.jpg";
	}

	public void setTimer(Timer q) {
		this.quepush = q;
	}


	

	private void update() {
		moving += 0.01;
		framecount++;
		if((framecount % 600) > 300) {
			//pgrav.magnitude = 0;
		}
		else {
			//pgrav.magnitude = 2.0;
		}

		if(mouse == 1) {
			newpos1 = new Vec2D(mx, my);
		}
		else if(mouse == 2){
			newpos2 = new Vec2D(mx, my);
		}
		else if(mouse == 3){
			newpos3 = new Vec2D(mx, my);
		}


		if(mouse == 1) {
			newpos1 = new Vec2D(mx, my);
			constraint1.setPos(constraint1.getPos().scale(0.666666).add(newpos1.scale(0.333333)));
		}
		else if(mouse == 2){
			newpos2 = new Vec2D(mx, my);
			constraint2.setPos(constraint2.getPos().scale(0.666666).add(newpos2.scale(0.333333)));
		}
		else if(mouse == 3){
			newpos3 = new Vec2D(mx, my);
			constraint3.setPos(constraint3.getPos().scale(0.666666).add(newpos3.scale(0.333333)));
		}
		physics.timestep();
		if(mouse == 1) {
			newpos1 = new Vec2D(mx, my);
			constraint1.setPos(constraint1.getPos().scale(0.5).add(newpos1.scale(0.5)));
		}
		else if(mouse == 2){
			newpos2 = new Vec2D(mx, my);
			constraint2.setPos(constraint2.getPos().scale(0.5).add(newpos2.scale(0.5)));
		}
		else if(mouse == 3){
			newpos3 = new Vec2D(mx, my);
			constraint3.setPos(constraint3.getPos().scale(0.5).add(newpos3.scale(0.5)));
		}
		physics.timestep();
		if(mouse == 1) {
			newpos1 = new Vec2D(mx, my);
			constraint1.setPos(newpos1);
		}
		else if(mouse == 2){
			newpos2 = new Vec2D(mx, my);
			constraint2.setPos(newpos2);
		}
		else if(mouse == 3){
			newpos3 = new Vec2D(mx, my);
			constraint3.setPos(newpos3);
		}

		if(pmeshactive.computeBrokenPercent() >= 0.95) {

			callTimer();
			if(quepush != null) {
				quepush.restart();
			}
		}

		physics.timestep();
	}

	private void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();
		//GLU glu = GLU.createGLU(gl);


		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);


		physics.render(gl);
		
		pmeshactive.renderMesh(gl);


		if(constraint1 != null) {
			constraint1.render(gl);
		}
		if(constraint2 != null) {
			constraint2.render(gl);
		}
		if(constraint3 != null) {
			constraint3.render(gl);
		}

		if(frameInterval != 0 ) {
			int fps = 1000000000/(int)frameInterval;
			int frametime = (int)(finished-start);
			double phystime = ((double)(updated-start))/frameInterval;
			double rendertime = ((double)(finished-updated))/frameInterval;
			double freetime = 1.0-(double)frametime/frameInterval;

			afps = 0.1*fps + 0.9*afps;
			aphysics = 0.1*phystime + 0.9*aphysics;
			arendering = 0.1*rendertime + 0.9*arendering;
			afree = 0.1*freetime + 0.9*afree;

		}

		if(dragging) {
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(mx, my);
			gl.glEnd();
		}

		gl.glFlush();
	}

	private void convertMouseCoordinates() {
		//TODO: replace with gluUnProject or something
		this.mx = 2*(double)mousex/width*(double)width/(double)height - (double)width/(double)height;
		this.my = 2*(double)(height-mousey)/height - 1.0;
	}

	public void display(GLAutoDrawable drawable) {
		long start = System.nanoTime();
		long frameInterval = start - this.start;
		update();
		long updated = System.nanoTime();
		render(drawable);
		long finished = System.nanoTime();

		this.start = start;
		this.frameInterval = frameInterval;
		this.updated = updated;
		this.finished = finished;


	}

	public void dispose(GLAutoDrawable drawable) {

	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		//gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);

		try{
			textureactive = TextureIO.newTexture(new File(source), true);

		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		textureactive.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);



		physics = new ParticleSystem(new Vec2D(0, -0.4), 0.3333/60.0, new Vec2D(-1.6, -1.0), new Vec2D(1.6, 1.0));

		pmeshactive = new PhysicsMesh(1.0, 28, textureactive);
		pmeshactive.setK(10);
		pmeshactive.addToSystem(physics);


		//pgrav = new PointGravity(new Vec2D(-0.5, 0.8), 0.5, 0.09, physics);

		//physics.addConstraint(new PointConstraint(pmesh.getPoints()[0][0]));

	     // physics.addForce(pgrav);

	}

	/*
	 * Early fake que on images.
	 * 
	 */
	public void callTimer(){
		
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;

		GL2 gl = drawable.getGL().getGL2();
		GLU glu = GLU.createGLU(gl);

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(-(double)width/(double)height, (double)width/(double)height, -1.0, 1.0);
	}

	private ArrayList<PhysPoint> getPointsInCircle(Vec2D pos, double radius) {
		ArrayList<PhysPoint> result = new ArrayList<PhysPoint>();
		double sqradius = radius*radius;
		for(PhysPoint p : physics.getParticles()) {
			Vec2D delta = pos.sub(p.pos);
			double sqdist = delta.dot(delta);
			if(sqdist <= sqradius) {
				result.add(p);
			}
		}
		return result;
	}

	public void keyPressed(KeyEvent e) {
		System.out.println("key");
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

		if(e.getKeyCode() == KeyEvent.VK_Z) {
			test();
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.mousex = arg0.getX();
		this.mousey = arg0.getY();
		this.dragging = true;
		convertMouseCoordinates();
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			if(constraint1 != null) {
				constraint1.delete();
			}
			
			ArrayList<PhysPoint> points = getPointsInCircle(new Vec2D(mx, my), 0.15);
			if(points.size() == 0) {
				constraint1 = new CircleConstraint(physics, new Vec2D(mx, my), 0.15);
			}
			else {
				constraint1 = new CentroidConstraint(points);
			}
			physics.addConstraint(constraint1);
			mouse = 1;
			newpos1 = new Vec2D(mx, my);
			pgrav = new PointGravity(new Vec2D(mx, my), 0.5, 0.09, physics);
			physics.addForce(pgrav);
		}
		else if(arg0.getButton() == MouseEvent.BUTTON2) {
			if(constraint2 != null) {
				constraint2.delete();
			}
			ArrayList<PhysPoint> points = getPointsInCircle(new Vec2D(mx, my), 0.15);
			if(points.size() == 0) {
				constraint2 = new CircleConstraint(physics, new Vec2D(mx, my), 0.15);
			}
			else {
				constraint2 = new CentroidConstraint(points);
			}
			physics.addConstraint(constraint2);
			mouse = 2;
			newpos2 = new Vec2D(mx, my);
		}
		else {
			if(constraint3 != null) {
				constraint3.delete();
			}
			ArrayList<PhysPoint> points = getPointsInCircle(new Vec2D(mx, my), 0.15);
			if(points.size() == 0) {
				constraint3 = new CircleConstraint(physics, new Vec2D(mx, my), 0.15);
			}
			else {
				constraint3 = new CentroidConstraint(points);
			}
			physics.addConstraint(constraint3);
			mouse = 3;
			newpos3 = new Vec2D(mx, my);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.mousex = arg0.getX();
		this.mousey = arg0.getY();
		this.dragging = false;
		convertMouseCoordinates();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.mousex = arg0.getX();
		this.mousey = arg0.getY();
		convertMouseCoordinates();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void test() {
		try {
			Engine_Server test = new Engine_Server();

			test.recvData();

			Date currentDate = new Date();
			long msec = currentDate.getTime();

			int button = 0;

			switch(test.getSelection()[0]) {
			case 1:
				button = MouseEvent.BUTTON1_MASK;
				break;
			case 2:
				button = MouseEvent.BUTTON2_MASK;
				break;
			case 3:
				button = MouseEvent.BUTTON3_MASK;
				break;
			default:
				break;
			}

			MouseEvent blah = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, msec, button, (int)test.getPosition()[0], (int)test.getPosition()[1], 1, false);

			mousePressed(blah);

			test.endServer();
		} catch (IOException ioe) {
			System.exit(-1);
		}
	}
}
