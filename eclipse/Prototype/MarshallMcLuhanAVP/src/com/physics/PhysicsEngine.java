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

import org.pirelenito.multimedia.jmf.MoviePlayer;
import org.pirelenito.multimedia.jmf.plugin.IGLTextureRenderer;

import com.communication.Engine_Server;
import com.jogamp.opengl.impl.x11.glx.GLX;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;

public class PhysicsEngine implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private double moving = 0;

	private Texture textureactive, textvid;	
	private Texture[] imageque, textque, qteque;
	private TextureData[] imagequeTD, textqueTD, qtequeTD;

	private DistortableMesh mesh;
	private PhysicsMesh pmeshactive, pmhide;
	private PhysicsMesh[] qts;

	private ParticleSystem physics;

	private int framecount = 0;
	private int quecount = 0, tquecount = 0, vidcount = 0, qtecount=0;
	private PointGravity pgrav;

	private static JFrame frame;
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

	private String source,texts;

	private File[] imageFiles, txtFiles, qteFiles, vidFiles;

	private Timer quepush = null;

	private boolean image, txt, video;
	
	private MoviePlayer player;
	
	private IGLTextureRenderer renderer;


	public PhysicsEngine(TextureData[] source, TextureData[] texts, TextureData[] Quotes, File[] videos){
		quecount = 0;
		tquecount = 0;
		vidcount= 0;
		imagequeTD = source;
		imageque = new Texture[imagequeTD.length];
		qtequeTD = Quotes;
		qteque = new Texture[qtequeTD.length];
		vidFiles = videos;
		qts = new PhysicsMesh[qtequeTD.length];
		if(texts != null){			
			textqueTD = texts;
			textque = new Texture[textqueTD.length];
		}
		else{
			textqueTD = new TextureData[0];
			textque = new Texture[0];
		}
		
		image = false;
		txt = true;
		video = false;
	}	
	/*
	public PhysicsEngine(String source, String texts){
		this.source = source;
		if(texts != null){			
			this.texts = texts;
			initTQue();
		}
		else
			initQue();
		image = false;
		txt = true;
		video = false;
	}*/

	public PhysicsEngine(String source){
		this.source = source;
		//initQue();
	}

	public PhysicsEngine(){
		source = "lolwut.jpg";
	}

	public void setTimer(Timer q) {
		this.quepush = q;
	}

	
	public void end(){
		player.stop();
		
	}
	
	private void play(){
		player.setLoop(false);
		player.play();
		renderer = player.getRenderer();
	}
	
	private void setMovie(File vid){
		try {
			player = new MoviePlayer (vid.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		pmhide = new PhysicsMesh(1.5, 20, textvid, 0);
		pmhide.translate(new Vec2D(0.2,0.2));
		pmhide.setK(5);
		pmhide.addToSystem(physics);
		play();
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
			if(video){
				end();
			}			
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
		/*
	    mesh.renderWireframe(gl);

	    gl.glPushMatrix();
	    gl.glTranslated(1.0, 0, 0);
	    mesh.render(gl);
	    gl.glPopMatrix();

	    gl.glPushMatrix();
	    gl.glTranslated(-1.0, 0, 0);
	    mesh.render(gl);
	    gl.glPopMatrix();
		 */

		physics.render(gl);
		mesh.render(gl);
		
		if(video){
			if(renderer != null){
				if (renderer.render(gl))
					try {
						textvid = renderer.getTexture();
					} catch (Exception e) {
						e.printStackTrace();
					}
					pmhide.updateTexture(textvid);// = new DistortableMesh(1.4,1.2,18,18, textvid);
					pmhide.renderMesh(gl);
			}
		}
		else if(qts.length>0){
			PhysicsMesh pm;
			pm = qts[qtecount];
			pm.renderMesh(gl);
		}
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

			/*gl.glRasterPos2d(-0.9, 0.9);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "FPS:      " + (int)Math.round(afps));
		    gl.glRasterPos2d(-0.9, 0.875);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Physics:  " + (int)Math.round(100*aphysics));
		    gl.glRasterPos2d(-0.9, 0.85);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Graphics: " + (int)Math.round(100*arendering));
		    gl.glRasterPos2d(-0.9, 0.825);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Free:     " + (int)Math.round(100*afree));
			 */
		}

		if(dragging) {
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(mx, my);
			gl.glEnd();
		}

		gl.glFlush();
	}

	private void convertMouseCoordinates() {
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
		quecount =0;
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
		gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		//can be used to set specific color trans
		//gl.glColor4f(255, 255, 255, 100);

		//for(int i=0;i<imageFiles.length;i++){
		for(int i=0;i<imagequeTD.length;i++){
			try{
				//textureactive = TextureIO.newTexture(imageFiles[i], true);
				textureactive = TextureIO.newTexture(imagequeTD[i]);

			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

			imageque[i] = textureactive;
		}
		//for(int i=0;i<txtFiles.length;i++){
		for(int i=0;i<textqueTD.length;i++){
			try {

				//textureactive = TextureIO.newTexture(txtFiles[i], true);
				textureactive = TextureIO.newTexture(textqueTD[i]);

			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

			textque[i] = textureactive;
		}
		for(int i=0;i<qtequeTD.length;i++){
			try {
				textureactive = TextureIO.newTexture(qtequeTD[i]);

			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

			qteque[i] = textureactive;
		}
		
		textvid = imageque[quecount];

		textureactive = imageque[quecount];

		if(textque.length>0)
			mesh = new DistortableMesh(2.6,2.0, 18,18, textque[0]);
		else if (imageque.length>1)
			mesh = new DistortableMesh(2.6,2.0, 18,18, imageque[1]);
		else
			mesh = new DistortableMesh(2.6,2.0,18,18, textureactive);


		physics = new ParticleSystem(new Vec2D(0, -0.4), 0.3333/60.0, new Vec2D(-1.6, -1.0), new Vec2D(1.6, 1.0));

		PhysicsMesh pm;
		for(int i = 0;i<qteque.length;i++){
			pm = new PhysicsMesh(0.8, 16, qteque[i], 0);
			pm.translate(new Vec2D(Math.random()-0.5, Math.random()-0.5));
			pm.setK(5);
			pm.addToSystem(physics);
			qts[i]=pm;

		}

		pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
		pmeshactive.setK(10);
		pmeshactive.addToSystem(physics);
		

		//pgrav = new PointGravity(new Vec2D(-0.5, 0.8), 4.5, 0.09, physics);

		// physics.addConstraint(new PointConstraint(pmesh.getPoints()[0][0]));

		//  physics.addForce(pgrav);
	}

	/*
	 * Early fake que on images.
	 * 
	 */
	public void callTimer(){
		if(!video){
			if(vidcount < vidFiles.length){
				setMovie(vidFiles[vidcount]);
			}
			else {
				vidcount = 0;
				setMovie(vidFiles[vidcount]);
			}
			vidcount++;
			video = true;
		}
		else{
			end();
			qtecount++;
			if(qtecount >= qts.length)
				qtecount = 0;
			video = false;
		}
		if(imageque.length>0 && textque.length > 0){
			if(image){
				image = false;
				txt = true;
				quecount+=1;
				if(quecount == imageque.length)
					quecount = 0;
				if(tquecount+1 == textque.length)
					mesh = new DistortableMesh(2.6,2.0,1,1, textque[0]);
				else
					mesh = new DistortableMesh(2.6,2.0,1,1, textque[tquecount+1]);
				textureactive = imageque[quecount];
				pmeshactive.delete();
				pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
				pmeshactive.setK(10);
				pmeshactive.addToSystem(physics);
			}
			else if(txt){
				image = true;
				txt = false;
				tquecount+=1;
				if(tquecount == textque.length)
					tquecount = 0;
				if(quecount+1 == imageque.length)
					mesh = new DistortableMesh(2.6,2.0,1,1, imageque[0]);
				else
					mesh = new DistortableMesh(2.6,2.0,1,1, imageque[quecount+1]);
				textureactive = textque[tquecount];
				pmeshactive.delete();
				pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
				pmeshactive.setK(10);
				pmeshactive.addToSystem(physics);
			}
		}
		else{
			quecount+=1;
			if(quecount == imageque.length)
				quecount = 0;
			if(quecount+1 == imageque.length)
				mesh = new DistortableMesh(2.6,2.0,1,1, imageque[0]);
			else
				mesh = new DistortableMesh(2.6,2.0,1,1, imageque[quecount+1]);
			textureactive = imageque[quecount];
			pmeshactive.delete();
			pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
			pmeshactive.setK(10);
			pmeshactive.addToSystem(physics);
		}

		/*
		quecount+=1;
		if(quecount == imageque.length)
			quecount = 0;
		if(quecount+1 == imageque.length)
			mesh = new DistortableMesh(1.4,1.4,1,1, imageque[0]);
		else
			mesh = new DistortableMesh(1.4,1.4,1,1, imageque[quecount+1]);
		textureactive = imageque[quecount];
		pmeshactive.delete();
		pmeshactive = new PhysicsMesh(1.2, 24, textureactive);
		pmeshactive.setK(10);
		pmeshactive.addToSystem(physics);
		 */
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
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
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

	public void mouseReleased(MouseEvent arg0) {
		this.mousex = arg0.getX();
		this.mousey = arg0.getY();
		this.dragging = false;
		convertMouseCoordinates();
	}

	public void mouseDragged(MouseEvent arg0) {
		this.mousex = arg0.getX();
		this.mousey = arg0.getY();
		convertMouseCoordinates();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void pushTimerImg(int iloc){
		if(!video){
			if(vidcount < vidFiles.length){
				setMovie(vidFiles[vidcount]);
			}
			else {
				vidcount = 0;
				setMovie(vidFiles[vidcount]);
			}
			vidcount++;
			video = true;
		}
		else{
			end();
			qtecount++;
			if(qtecount >= qts.length)
				qtecount = 0;
			video = false;
		}
		quecount = iloc;
		if(textque.length > 0){
			image = false;
			txt = true;
			if(quecount == imageque.length)
				quecount = 0;
			if(tquecount+1 == textque.length)
				mesh = new DistortableMesh(2.6,2.0,1,1, textque[0]);
			else
				mesh = new DistortableMesh(2.6,2.0,1,1, textque[tquecount+1]);
			textureactive = imageque[quecount];
			pmeshactive.delete();
			pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
			pmeshactive.setK(10);
			pmeshactive.addToSystem(physics);
		}

		else{
			if(quecount == imageque.length)
				quecount = 0;
			if(quecount+1 == imageque.length)
				mesh = new DistortableMesh(2.6,2.0,1,1, imageque[0]);
			else
				mesh = new DistortableMesh(2.6,2.0,1,1, imageque[quecount+1]);
			textureactive = imageque[quecount];
			pmeshactive.delete();
			pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
			pmeshactive.setK(10);
			pmeshactive.addToSystem(physics);
		}
		if(quepush != null) {
			quepush.restart();
		}

	}

	public void pushTimerTxt(int tloc){
		if(!video){
			if(vidcount < vidFiles.length){
				setMovie(vidFiles[vidcount]);
			}
			else {
				vidcount = 0;
				setMovie(vidFiles[vidcount]);
			}
			vidcount++;
			video = true;
		}
		else{
			end();
			qtecount++;
			if(qtecount >= qts.length)
				qtecount = 0;
			video = false;
		}
		tquecount = tloc;
		image = true;
		txt = false;
		if(tquecount == textque.length)
			tquecount = 0;
		if(quecount+1 == imageque.length)
			mesh = new DistortableMesh(2.6,2.0,1,1, imageque[0]);
		else
			mesh = new DistortableMesh(2.6,2.0,1,1, imageque[quecount+1]);
		textureactive = textque[tquecount];
		pmeshactive.delete();
		pmeshactive = new PhysicsMesh(2.0, 40, textureactive, PhysicsMesh.defaultBreakage);
		pmeshactive.setK(10);
		pmeshactive.addToSystem(physics);
		if(quepush != null) {
			quepush.restart();
		}

	}
}
