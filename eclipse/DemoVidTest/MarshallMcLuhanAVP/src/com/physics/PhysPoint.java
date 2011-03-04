package com.physics;
import javax.media.opengl.*;

public class PhysPoint {
	public Vec2D pos;
	public Vec2D oldpos;
	public Vec2D accel;
	public double mass;
	
	public PhysPoint(Vec2D pos, Vec2D oldpos, double mass) {
		this.pos = new Vec2D(pos);
		this.oldpos = new Vec2D(oldpos);
		this.accel = new Vec2D();
		this.mass = mass;
	}
	
	public PhysPoint(Vec2D pos) {
		this(pos, pos, 1);
	}
	
	public void render(GL2 gl) {
		gl.glVertex2d(pos.x, pos.y);
	}
}
