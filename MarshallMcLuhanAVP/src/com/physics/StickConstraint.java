package com.physics;
import javax.media.opengl.GL2;

public class StickConstraint extends Constraint {
	protected PhysPoint a, b;
	public double length;

	public StickConstraint(PhysPoint a, PhysPoint b, double length) {
		this.a = a;
		this.b = b;
		this.length = length;
	}
	
	public StickConstraint(PhysPoint a, PhysPoint b) {
		this.a = a;
		this.b = b;
		length = a.pos.dist(b.pos);
	}
	
	public void satisfy() {
		double invmass1 = 1.0/a.mass;
		double invmass2 = 1.0/b.mass;
		
		Vec2D delta = a.pos.sub(b.pos);
		double deltalength = delta.length();
		if(deltalength == 0) {
			System.out.println("FOOL");
		}
		double diff = (deltalength - length)/(deltalength*(invmass1+invmass2));
		
		a.pos = a.pos.sub(delta.scale(invmass1*diff));
		b.pos = b.pos.add(delta.scale(invmass2*diff));
	}

	public void render(GL2 gl) {
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(a.pos.x, a.pos.y);
		gl.glVertex2d(b.pos.x, b.pos.y);
		gl.glEnd();
	}
}
