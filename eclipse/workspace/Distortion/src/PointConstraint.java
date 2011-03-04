import javax.media.opengl.GL2;


public class PointConstraint implements Constraint {
	private PhysPoint a;
	private Vec2D loc;
	
	public PointConstraint(PhysPoint a, Vec2D location) {
		this.a = a;
		loc = location;
	}
	
	public PointConstraint(PhysPoint a) {
		this.a = a;
		loc = new Vec2D(a.pos);
	}

	public void satisfy() {
		a.pos = new Vec2D(loc);
	}

	public void render(GL2 gl) {
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(loc.x-0.03, loc.y-0.03);
		gl.glVertex2d(loc.x+0.03, loc.y+0.03);
		gl.glVertex2d(loc.x-0.03, loc.y+0.03);
		gl.glVertex2d(loc.x+0.03, loc.y-0.03);
		gl.glEnd();
	}

}
