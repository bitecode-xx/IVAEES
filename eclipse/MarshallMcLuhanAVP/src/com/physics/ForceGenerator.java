package com.physics;
import javax.media.opengl.GL2;


public interface ForceGenerator {
	public void accumulate();
	public void render(GL2 gl);
}
