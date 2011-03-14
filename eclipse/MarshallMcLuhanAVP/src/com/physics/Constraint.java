package com.physics;
import javax.media.opengl.*;

public abstract class Constraint {
	public abstract void satisfy();
	public abstract void render(GL2 gl);
	
	private boolean toDelete = false;
	public void delete() {
		this.toDelete = true;
	}
	
	public boolean toBeDeleted() {
		return toDelete;
	}
}
