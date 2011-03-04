import javax.media.opengl.GL2;

public class MobileCircleConstraint implements Constraint {
	private ParticleSystem physics;
	public PhysPoint particle;
	public double radius;
	
	public MobileCircleConstraint(ParticleSystem physics, PhysPoint pos, double radius) {
		this.physics = physics;
		this.particle = pos;
		this.radius = radius;
	}
	
	public void satisfy() {
		double sqradius = radius*radius;
		for(PhysPoint p : physics.getParticles()) {
			if(p == particle) {
				continue;
			}
			Vec2D delta = p.pos.sub(particle.pos);
			double sqdist = delta.dot(delta);
			if(sqdist < sqradius) {
				Vec2D displacement = delta.scale(radius/Math.sqrt(sqdist)).sub(delta);
				p.pos = p.pos.add(displacement.scale(particle.mass/(particle.mass+p.mass)));
				particle.pos = particle.pos.add(displacement.scale(-p.mass/(particle.mass+p.mass)));
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
			gl.glVertex2d(particle.pos.x+radius*Math.cos(theta), particle.pos.y+radius*Math.sin(theta));
		}
		gl.glEnd();
	}

}
