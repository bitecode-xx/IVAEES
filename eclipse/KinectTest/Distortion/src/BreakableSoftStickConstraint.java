
public class BreakableSoftStickConstraint extends SoftStickConstraint {
	public double breakageFactor;
	private ParticleSystem physics;
	private boolean broken = false;
	
	public BreakableSoftStickConstraint(PhysPoint a, PhysPoint b, double softness, ParticleSystem physics, double breakageFactor) {
		super(a, b, softness);
		this.breakageFactor = breakageFactor;
		this.physics = physics;
	}
	
	public boolean isBroken() {
		return broken;
	}
	
	public void satisfy() {
		double invmass1 = 1.0/a.mass;
		double invmass2 = 1.0/b.mass;
		
		Vec2D delta = a.pos.sub(b.pos);
		double deltalength = delta.length();
		if(deltalength/length >= breakageFactor) {
			physics.removeConstraint(this);
			broken = true;
			return;
		}
		if(deltalength == 0) {
			System.out.println("FOOL");
		}
		double diff = softness*(deltalength - length)/(deltalength*(invmass1+invmass2));
		
		a.pos = a.pos.sub(delta.scale(invmass1*diff));
		b.pos = b.pos.add(delta.scale(invmass2*diff));
	}
}