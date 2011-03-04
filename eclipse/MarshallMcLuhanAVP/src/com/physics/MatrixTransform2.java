package com.physics;

public class MatrixTransform2 implements Transform2D {
	
	public double a, b, c, d;
	
	public MatrixTransform2(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public Vec2D transform(Vec2D point) {
		Vec2D result = new Vec2D(a*point.x + b*point.y,
					c*point.x + d*point.y);
		return result;
	}

	public static MatrixTransform2 getRotationMatrix(double theta) {
		return new MatrixTransform2(Math.cos(theta), -Math.sin(theta), Math.sin(theta), Math.cos(theta));
	}
}
