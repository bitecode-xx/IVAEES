package com.physics;
import javax.media.opengl.GL2;
import java.util.*;

public class CentroidConstraint extends PositionableConstraint {
	private ArrayList<PhysPoint> points;
	private double radius;
	public Vec2D pos;

	public CentroidConstraint(Collection<PhysPoint> points, Vec2D pos, double radius) {
		this.points = new ArrayList<PhysPoint>(points);
		this.pos = new Vec2D(pos);
		this.radius = radius;
	}
	
	public CentroidConstraint(Collection<PhysPoint> points) {
		this.points = new ArrayList<PhysPoint>(points);
		this.pos = getCentroid();
		this.radius = 0.15;
	}
	
	private Vec2D getCentroid() {
		Vec2D centroid = new Vec2D();
		PhysPoint p;
		for(Iterator<PhysPoint> iter = points.iterator(); iter.hasNext();) {
			p = iter.next();
			if(pos != null && p.pos.sub(pos).length() > radius*1.5) {
				iter.remove();
			}
			else {
				centroid = centroid.add(p.pos);
			}
		}
		centroid = centroid.scale(1.0/points.size());
		return centroid;
	}
	
	public void setPoints(Collection<PhysPoint> points) {
		this.points.clear();
		this.points.addAll(points);
	}
	
	public void satisfy() {
		Vec2D centroid = getCentroid();
		
		Vec2D delta = pos.sub(centroid);
		
		for(PhysPoint p : points) {
			p.pos = p.pos.add(delta);
		}
	}

	public void render(GL2 gl) {
		gl.glColor3d(0, 0.5, 1);
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
