package com.physics;

public final class Vec2D {
	public final double x;
	public final double y;
	
	public Vec2D() {
		x = 0;
		y = 0;
	}
	
	public Vec2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2D(Vec2D b) {
		x = b.x;
		y = b.y;
	}
	
	public Vec2D add(Vec2D b) {
		return new Vec2D(x+b.x, y+b.y);
	}
	
	public Vec2D sub(Vec2D b) {
		return new Vec2D(x-b.x, y-b.y);
	}
	
	public double dot(Vec2D b) {
		return x*b.x + y*b.y;
	}
	
	public Vec2D scale(double s) {
		return new Vec2D(x*s, y*s);
	}
	
	public Vec2D clamp(Vec2D lower, Vec2D upper) {
		return new Vec2D(Math.max(Math.min(x, upper.x), lower.x), Math.max(Math.min(y, upper.y), lower.y));
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public double dist(Vec2D b) {
		double dx = x - b.x;
		double dy = y - b.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public Vec2D normalized() {
		return this.scale(1.0/this.length());
	}
}
