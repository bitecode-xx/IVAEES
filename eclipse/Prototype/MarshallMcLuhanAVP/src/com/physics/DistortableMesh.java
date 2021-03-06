package com.physics;
import javax.media.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class DistortableMesh {

	private double width, height;
	private int xres, yres;
	private Vec2D[][] points;
	private Vec2D[][] texcoords;
	private Texture texture;
	private Transform2D transform = null;
	
	public DistortableMesh(double width, double height, int xres, int yres, Texture texture) {
		this.width = width;
		this.height = height;
		this.xres = xres;
		this.yres = yres;
		this.texture = texture;
		points = new Vec2D[yres+1][xres+1];
		texcoords = new Vec2D[yres+1][xres+1];
		generateCoords();
	}
	
	public DistortableMesh(double width, int xres, Texture texture) {
		int twidth = texture.getWidth();
		int theight = texture.getHeight();
		
		double height = (width*theight)/twidth;
		int yres = (int)Math.ceil((double)(xres*theight))/twidth;
		
		this.width = width;
		this.height = height;
		this.xres = xres;
		this.yres = yres;
		this.texture = texture;
		points = new Vec2D[yres+1][xres+1];
		texcoords = new Vec2D[yres+1][xres+1];
		generateCoords();
	}
	
	private void generateCoords() {
		for(int row = 0; row <= yres; row++) {
			double t = (double)row/yres;
			for(int column = 0; column <= xres; column++) {
				double s = (double)column/xres;
				texcoords[row][column] = new Vec2D(s, t);
			}
		}
	}
	
	public void setTransform(Transform2D transform) {
		this.transform = transform;
	}
	
	private void computeTransform() {
		double cellwidth = width/xres;
		double cellheight = height/yres;
		
		for(int row = 0; row <= yres; row++) {
			double y = (height/2) - row*cellheight;
			for(int column = 0; column <= xres; column++) {
				double x = -(width/2) + column*cellwidth;
				
				if(transform != null) {
					Vec2D transformed = transform.transform(new Vec2D(x, y));
					points[row][column] = transformed;
				}
				else {
					points[row][column] = new Vec2D(x, y);
				}
			}
		}
	}

	public void render(GL2 gl) {

		computeTransform();
		
		texture.bind();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		for(int row = 0; row < yres; row++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int column = 0; column <= xres; column++) {
				Vec2D point = points[row][column];
				Vec2D texcoord = texcoords[row][column];
				
				gl.glTexCoord2d(texcoord.x, texcoord.y);
				gl.glVertex2d(point.x, point.y);
				
				point = points[row+1][column];
				texcoord = texcoords[row+1][column];
				
				gl.glTexCoord2d(texcoord.x, texcoord.y);
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
		}
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	public void renderWireframe(GL2 gl) {
		computeTransform();
		
		for(int row = 0; row <= yres; row++) {
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int column = 0; column <= xres; column++) {
				Vec2D point = points[row][column];
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
		}
		
		for(int column = 0; column <= xres; column++) {
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int row = 0; row <= yres; row++) {
				Vec2D point = points[row][column];
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
		}
		
	}
}
