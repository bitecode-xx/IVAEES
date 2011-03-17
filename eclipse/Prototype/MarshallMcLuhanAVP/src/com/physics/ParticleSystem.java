package com.physics;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class ParticleSystem {
	private LinkedList<PhysPoint> particles = new LinkedList<PhysPoint>();
	private LinkedList<Constraint> constraints = new LinkedList<Constraint>();
	private LinkedList<ForceGenerator> forces = new LinkedList<ForceGenerator>();
	
	private Vec2D gravity;
	private double timestep;
	
	private Vec2D bl, tr;
	
	private int numIters = 1;
	
	public ParticleSystem(Vec2D gravity, double timestep, 
			Vec2D bottomleft, Vec2D topright) {
		this.gravity = gravity;
		this.timestep = timestep;
		
		bl = bottomleft;
		tr = topright;
	}
	
	public LinkedList<PhysPoint> getParticles() {
		return particles;
	}
	
	public void addParticle(PhysPoint a) {
		particles.add(a);
	}
	
	public void addConstraint(Constraint c) {
		constraints.add(c);
	}
	
	public void addForce(ForceGenerator f) {
		forces.add(f);
	}
	
	public void timestep() {
		accumulateForces();
		verlet();
		satisfyConstraints();
	}
	
	public void setIterCount(int i) {
		numIters = i;
	}
	
	private void verlet() {
		ListIterator<PhysPoint> iter = particles.listIterator();
		while(iter.hasNext()) {
			PhysPoint p = iter.next();
			if(p.toBeDeleted()) {
				iter.remove();
			}
			else {
				Vec2D temp = new Vec2D(p.pos);
				//p.pos = p.pos.add(p.pos.sub(p.oldpos).add(p.accel.scale(timestep*timestep)));
				p.pos = p.pos.add(p.pos.sub(p.oldpos).scale(0.995).add(p.accel.scale(timestep*timestep)));
				//p.pos = p.pos.scale(2).sub(p.oldpos).add(p.accel.scale(timestep*timestep));
				p.oldpos = temp;
			}
		}
	}
	
	private void satisfyConstraints() {
		for(int i=0; i<numIters; i++) {
			ListIterator<Constraint> iter = constraints.listIterator();
			while(iter.hasNext()) {
				Constraint c = iter.next();
				if(c.toBeDeleted()) {
					iter.remove();
				}
				else {
					c.satisfy();
				}
			}
			
			for(PhysPoint p : particles) {
				p.pos = p.pos.clamp(bl, tr);
			}
		}
	}
	
	private void accumulateForces() {
		for(PhysPoint p : particles) {
			p.accel = gravity;
		}
		ListIterator<ForceGenerator> iter = forces.listIterator();
		while(iter.hasNext()) {
			ForceGenerator f = iter.next();
			if(f.toBeDeleted()) {
				iter.remove();
			}
			else {
				f.accumulate();
			}
		}
	}
	
	public void render(GL2 gl) {
		gl.glColor3d(1, 1, 1);
		gl.glPointSize(3);
		
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex2d(bl.x, bl.y);
		gl.glVertex2d(bl.x, tr.y);
		gl.glVertex2d(tr.x, tr.y);
		gl.glVertex2d(tr.x, bl.y);
		gl.glVertex2d(bl.x, bl.y);
		gl.glEnd();
		
		/*
		gl.glBegin(GL.GL_POINTS);
		for(PhysPoint p : particles) {
			p.render(gl);
		}
		gl.glEnd();
		
		for(Constraint c : constraints) {
			c.render(gl);
		}
		for(ForceGenerator f : forces) {
			f.render(gl);
		}
		*/
	}
}
