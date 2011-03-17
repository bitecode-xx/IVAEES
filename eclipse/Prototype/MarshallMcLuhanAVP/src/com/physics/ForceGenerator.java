package com.physics;
import javax.media.opengl.GL2;


public abstract class ForceGenerator {
	public abstract void accumulate();
	public abstract void render(GL2 gl);
	
	private boolean toDelete = false;
	public void delete() {
		this.toDelete = true;
	}
	
	public boolean toBeDeleted() {
		return toDelete;
	}
}
