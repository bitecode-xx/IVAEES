package com.physics;

public class BreakableSoftStickConstraint extends SoftStickConstraint {
	public double breakageFactor;
	private boolean broken = false;
	
	public BreakableSoftStickConstraint(PhysPoint a, PhysPoint b, double softness, double breakageFactor) {
		super(a, b, softness);
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
		if(deltalength/length >= breakageFactor) {
			this.delete();
			broken = true;
			return;
		}
		if(deltalength == 0) {
			//Avoid division by zero
			b.pos = b.pos.add(new Vec2D((Math.random()-0.5)/1000000, (Math.random()-0.5)/1000000));
			satisfy();
			return;
		}
		double diff = softness*(deltalength - length)/(deltalength*(invmass1+invmass2));
		
		a.pos = a.pos.sub(delta.scale(invmass1*diff));
		b.pos = b.pos.add(delta.scale(invmass2*diff));
	}
}