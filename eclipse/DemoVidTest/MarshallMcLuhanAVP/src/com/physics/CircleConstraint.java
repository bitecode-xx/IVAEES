package com.physics;
import javax.media.opengl.GL2;


public class CircleConstraint extends PositionableConstraint {
	private ParticleSystem physics;
	public Vec2D pos;
	public double radius;
	
	public CircleConstraint(ParticleSystem physics, Vec2D pos, double radius) {
		this.physics = physics;
		this.pos = new Vec2D(pos);
		this.radius = radius;
	}
	
	public void satisfy() {
		double sqradius = radius*radius;
		for(PhysPoint p : physics.getParticles()) {
			Vec2D delta = p.pos.sub(pos);
			double sqdist = delta.dot(delta);
			if(sqdist < sqradius) {
				p.pos = pos.add(delta.scale(radius/Math.sqrt(sqdist)));
			}
		}
	}

	@Override
	public void render(GL2 gl) {
		// TODO Auto-generated method stub
		gl.glColor3d(0, 0, 1);
		gl.glBegin(GL2.GL_LINE_LOOP);
		for(int i=0; i<32; i++) {
			double theta = i*Math.PI*2/32;
			gl.glVertex2d(pos.x+radius*Math.cos(theta), pos.y+radius*Math.sin(theta));
		}
		gl.glEnd();
	}

	@Override
	public void setPos(Vec2D pos) {
		// TODO Auto-generated method stub
		this.pos = pos;
	}

	@Override
	public Vec2D getPos() {
		// TODO Auto-generated method stub
		return pos;
	}
}
