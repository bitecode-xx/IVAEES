package com.physics;
import javax.media.opengl.GL2;

public class SoftStickConstraint implements Constraint {
	protected PhysPoint a, b;
	public double length;
	public double softness;

	public SoftStickConstraint(PhysPoint a, PhysPoint b, double length, double softness) {
		this.a = a;
		this.b = b;
		this.length = length;
		this.softness = softness;
	}
	
	public SoftStickConstraint(PhysPoint a, PhysPoint b) {
		this.a = a;
		this.b = b;
		length = a.pos.dist(b.pos);
		this.softness = 0.5;
	}
	
	public SoftStickConstraint(PhysPoint a, PhysPoint b, double softness) {
		this.a = a;
		this.b = b;
		length = a.pos.dist(b.pos);
		this.softness = softness;
	}
	
	public void satisfy() {
		double invmass1 = 1.0/a.mass;
		double invmass2 = 1.0/b.mass;
		
		Vec2D delta = a.pos.sub(b.pos);
		double deltalength = delta.length();
		if(deltalength == 0) {
			System.out.println("FOOL");
		}
		double diff = softness*(deltalength - length)/(deltalength*(invmass1+invmass2));
		
		a.pos = a.pos.sub(delta.scale(invmass1*diff));
		b.pos = b.pos.add(delta.scale(invmass2*diff));
	}

	public void render(GL2 gl) {
		//gl.glEnable(GL2.GL_BLEND);
		//gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor3d(0.25, 0.25, 0.25);
		
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(a.pos.x, a.pos.y);
		gl.glVertex2d(b.pos.x, b.pos.y);
		gl.glEnd();
		
		//gl.glDisable(GL2.GL_BLEND);
	}
}
