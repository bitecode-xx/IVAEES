package com.physics;
import javax.media.opengl.*;

public interface Constraint {
	public void satisfy();
	public void render(GL2 gl);
}
