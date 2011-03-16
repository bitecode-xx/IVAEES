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
import com.navmenu.McLuhanMain;

public class PhysicsEngine implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	
	private double moving = 0;
	
	private Texture textureactive;	
	private Texture[] imageque, textque, qteque;
	private TextureData[] imagequeTD, textqueTD, qtequeTD;
	
	private DistortableMesh mesh;
	private PhysicsMesh pmeshactive;
	private PhysicsMesh[] qts;
	
	private ParticleSystem physics;
	
	private int framecount = 0;
	private int quecount = 0, tquecount = 0;
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
	
	private File[] imageFiles, txtFiles, qteFiles;
	
	private Timer quepush = null;
	
	private boolean image, txt, video;
	
	
	

	public PhysicsEngine(TextureData[] source, TextureData[] texts, TextureData[] Quotes){
		quecount = 0;
		tquecount = 0;
		imagequeTD = source;
		imageque = new Texture[imagequeTD.length];
		qtequeTD = Quotes;
		qteque = new Texture[qtequeTD.length];
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
	}
	
	public PhysicsEngine(String source){
		this.source = source;
		initQue();
	}

	public PhysicsEngine(){
		source = "lolwut.jpg";
	}
	
	public void setTimer(Timer q) {
		this.quepush = q;
	}
	

	private void initQue() {
		List<File> filesAsList = FileFinder.findFiles(
				new File(source), ".*\\.jpg|.*\\.JPG|.*\\.gif|.*\\.png|.*\\.bmp");//|.*\\.flv|.*\\.mov|.*\\.pdf|.*\\.docx||.*\\.rtf");
		imageFiles = filesAsList.toArray(new File[filesAsList.size()]);
		imageque = new Texture[imageFiles.length];
		//for(int i=0;i<imageFiles.length;i++)
		//	System.err.println(imageFiles[i].getAbsolutePath());
		
		quecount = 0;
	}
	
	private void initTQue() {
		List<File> filesAsList = FileFinder.findFiles(
				new File(source), ".*\\.jpg|.*\\.JPG|.*\\.gif|.*\\.png|.*\\.bmp");//|.*\\.flv|.*\\.mov|.*\\.pdf|.*\\.docx||.*\\.rtf");
		imageFiles = filesAsList.toArray(new File[filesAsList.size()]);
		
		List<File> textAsList = FileFinder.findFiles(
				new File(texts), ".*\\.jpg|.*\\.JPG|.*\\.gif|.*\\.png|.*\\.bmp");//|.*\\.flv|.*\\.mov|.*\\.pdf|.*\\.docx||.*\\.rtf");
		
		txtFiles = textAsList.toArray(new File[textAsList.size()]);

		imageque = new Texture[imageFiles.length];
		textque = new Texture[txtFiles.length];
		//for(int i=0;i<imageFiles.length;i++)
		//	System.err.println(imageFiles[i].getAbsolutePath());
		
		quecount = 0;
		tquecount = 0;
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
	   
	    PhysicsMesh pm;
	    for(int i = 0;i<qts.length;i++){
	    	pm = qts[i];
	    	gl.glPushMatrix(); // save transformation matrix (again)
	    	{
	    	    gl.glTranslated(i*1.5, i*1.2,0);
	    	    pm.renderMesh(gl);
	    	}
	    	gl.glPopMatrix(); // revert to saved (again)
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
		    
		    gl.glRasterPos2d(-0.9, 0.9);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "FPS:      " + (int)Math.round(afps));
		    gl.glRasterPos2d(-0.9, 0.875);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Physics:  " + (int)Math.round(100*aphysics));
		    gl.glRasterPos2d(-0.9, 0.85);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Graphics: " + (int)Math.round(100*arendering));
		    gl.glRasterPos2d(-0.9, 0.825);
		    glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Free:     " + (int)Math.round(100*afree));
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
		quecount =0;
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_BLEND);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
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
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
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
			textureactive.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
			textureactive.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

			qteque[i] = textureactive;
		}
		

		textureactive = imageque[quecount];

		if(textque.length>0)
			mesh = new DistortableMesh(1.6,1.4, 18,18, textque[0]);
		else if (imageque.length>1)
			mesh = new DistortableMesh(1.6,1.4, 18,18, imageque[1]);
		else
			mesh = new DistortableMesh(1.6,1.4,18,18, textureactive);

		/*
		if(imageque.length>1)
			mesh = new DistortableMesh(1.4,1.4, 16,16, imageque[1]);
		else
			mesh = new DistortableMesh(1.4,1.4,16,16, textureactive);
		/*
		//mesh.setTransform(MatrixTransform2.getRotationMatrix(Math.PI/3));
		//mesh.setTransform(new MatrixTransform2(0.1, 0.2, 0.3, 0.4));
		mesh.setTransform(new Transform2D() {
			public Vec2D transform(Vec2D point) {
				Vec2D result = point.add(new Vec2D(0.3*Math.sin((point.y+moving)*5),
						0.3*Math.sin((point.x+moving)*5)));
				return result;
			}

		});*/
		
		
		physics = new ParticleSystem(new Vec2D(0, -0.4), 0.3333/60.0, new Vec2D(-1.3, -1.0), new Vec2D(1.3, 1.0));

		
		PhysicsMesh pm;
        for(int i = 0;i<qteque.length;i++){
        	pm = new PhysicsMesh(0.8, 16, qteque[i]);
            pm.setK(5);
            pm.addToSystem(physics);
            qts[i]=pm;
        	
        }
        
        pmeshactive = new PhysicsMesh(2.0, 40, textureactive);
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
		if(imageque.length>0 && textque.length > 0){
			if(image){
				image = false;
				txt = true;
				quecount+=1;
				if(quecount == imageque.length)
					quecount = 0;
				if(tquecount+1 == textque.length)
					mesh = new DistortableMesh(1.6,1.4,1,1, textque[0]);
				else
					mesh = new DistortableMesh(1.6,1.4,1,1, textque[tquecount+1]);
				textureactive = imageque[quecount];
				pmeshactive.delete();
				pmeshactive = new PhysicsMesh(2.0, 40, textureactive);
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
					mesh = new DistortableMesh(1.6,1.4,1,1, imageque[0]);
				else
					mesh = new DistortableMesh(1.6,1.4,1,1, imageque[quecount+1]);
				textureactive = textque[tquecount];
				pmeshactive.delete();
				pmeshactive = new PhysicsMesh(2.0, 40, textureactive);
				pmeshactive.setK(10);
				pmeshactive.addToSystem(physics);
			}
		}
			else{
				quecount+=1;
				if(quecount == imageque.length)
					quecount = 0;
				if(quecount+1 == imageque.length)
					mesh = new DistortableMesh(1.6,1.4,1,1, imageque[0]);
				else
					mesh = new DistortableMesh(1.6,1.4,1,1, imageque[quecount+1]);
				textureactive = imageque[quecount];
				pmeshactive.delete();
				pmeshactive = new PhysicsMesh(2.0, 40, textureactive);
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
