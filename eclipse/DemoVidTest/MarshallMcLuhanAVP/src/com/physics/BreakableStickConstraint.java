package com.physics;

public class BreakableStickConstraint extends StickConstraint {
	public double breakageFactor;
	private boolean broken = false;

	public BreakableStickConstraint(PhysPoint a, PhysPoint b, double breakageFactor) {
		super(a, b);
		this.breakageFactor = breakageFactor;
	}
	
	public boolean isBroken() {
		return broken;
	}

	public void satisfy() {
		double invmass1 = 1.0/a.mass;
		double invmass2 = 1.0/b.mass;
		
		Vec2D delta = a.pos.sub(b.pos);
		double deltalength = delta.length();
		if(deltalength == 0) {
			System.out.println("FOOL");
		}
		if(deltalength/length >= breakageFactor) {
			this.delete();
			broken = true;
			return;
		}
		
		double diff = (deltalength - length)/(deltalength*(invmass1+invmass2));
		
		a.pos = a.pos.sub(delta.scale(invmass1*diff));
		b.pos = b.pos.add(delta.scale(invmass2*diff));
	}
}
