package com.physics;
import javax.media.opengl.GL2;


public class Repulsor extends ForceGenerator {
	public double magnitude;
	public double minradius;
	public Vec2D pos;
	private ParticleSystem physics;

	public Repulsor(Vec2D pos, double magnitude, double minradius, ParticleSystem physics) {
		this.pos = new Vec2D(pos);
		this.magnitude = magnitude;
		this.physics = physics;
		this.minradius = minradius;
	}
	
	public void accumulate() {
		for(PhysPoint p : physics.getParticles()) {
			Vec2D delta = pos.sub(p.pos);
			double dist = Math.max(delta.length(), minradius);
			p.accel = p.accel.sub(delta.normalized().scale(magnitude/(dist*dist*dist*dist*dist*dist)));
		}
	}

	public void render(GL2 gl) {
		gl.glColor3d(1, 0, 0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for(int i=0; i<32; i++) {
			double theta = i*Math.PI*2/16;
			gl.glVertex2d(pos.x+minradius*Math.cos(theta), pos.y+minradius*Math.sin(theta));
		}
		gl.glEnd();
	}

}
