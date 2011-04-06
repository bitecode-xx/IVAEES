package com.physics;
import javax.media.opengl.*;
import java.util.*;

import com.jogamp.opengl.util.texture.*;

public class PhysicsMesh extends ForceGenerator {

	private double width, height;
	private int xres, yres;
	private PhysPoint[][] points;
	private Vec2D[][] texcoords;
	private SoftStickConstraint[][][] constraints;
	private ArrayList<SoftStickConstraint> allConstraints;
	private Texture texture;
	
	private double restLength;
	private double k = 1.0;
	private double breakage;
	private boolean breakable;
	
	public static final double defaultBreakage = 2.0;
	public static final double defaultSoftness = 0.5;
	
	public PhysicsMesh(double width, double height, int xres, int yres, Texture texture, double breakage) {
		this.breakage = breakage;
		this.breakable = breakage > 1.0;
		this.width = width;
		this.height = height;
		this.xres = xres;
		this.yres = yres;
		this.texture = texture;
		points = new PhysPoint[yres+1][xres+1];
		texcoords = new Vec2D[yres+1][xres+1];
		constraints = new SoftStickConstraint[yres][xres][4];
		allConstraints = new ArrayList<SoftStickConstraint>();
		generateCoords();
		generatePoints();
		
		double cellwidth = width/xres;
		double cellheight = height/yres;
		restLength = Math.sqrt(cellwidth*cellwidth + cellheight*cellheight);
	}
	
	public PhysPoint[][] getPoints() {
		return points;
	}
	
	public PhysicsMesh(double width, int xres, Texture texture, double breakage) {
		this.breakage = breakage;
		this.breakable = breakage > 1.0;
		
		int twidth = texture.getWidth();
		int theight = texture.getHeight();
		
		double height = (width*theight)/twidth;
		int yres = (int)Math.ceil((double)(xres*theight))/twidth;
		
		this.width = width;
		this.height = height;
		this.xres = xres;
		this.yres = yres;
		this.texture = texture;
		points = new PhysPoint[yres+1][xres+1];
		texcoords = new Vec2D[yres+1][xres+1];
		constraints = new SoftStickConstraint[yres][xres][4];
		allConstraints = new ArrayList<SoftStickConstraint>();
		generateCoords();
		generatePoints();
		
		double cellwidth = width/xres;
		double cellheight = height/yres;
		restLength = Math.sqrt(cellwidth*cellwidth + cellheight*cellheight);
	}
	
	public void setK(double k) {
		this.k = k;
	}
	
	public void translate(Vec2D t) {
		for(PhysPoint[] pa : points) {
			for(PhysPoint p : pa) {
				p.pos = p.pos.add(t);
				p.oldpos = p.oldpos.add(t);
			}
		}
	}
	
	public void addToSystem(ParticleSystem s) {
		for(PhysPoint[] pa : points) {
			for(PhysPoint p : pa) {
				s.addParticle(p);
			}
		}
		
		SoftStickConstraint c;
		for(int row = 0; row < yres; row++) {
			for(int column = 0; column < xres; column++) {
				if(breakable) {
					c = new BreakableSoftStickConstraint(points[row][column], points[row][column+1], defaultSoftness, breakage);
				}
				else {
					c = new SoftStickConstraint(points[row][column], points[row][column+1], defaultSoftness);
				}
				constraints[row][column][0] = c;
				if(row > 0) {
					constraints[row-1][column][2] = c;
				}
				s.addConstraint(c);
				allConstraints.add(c);
				if(breakable) {
					c = new BreakableSoftStickConstraint(points[row][column], points[row+1][column], defaultSoftness, breakage);
				}
				else {
					c = new SoftStickConstraint(points[row][column], points[row+1][column], defaultSoftness);
				}
				constraints[row][column][1] = c;
				if(column > 0) {
					constraints[row][column-1][3] = c;
				}
				s.addConstraint(c);
				allConstraints.add(c);
			}
		}
		
		for(int row = 0; row < yres; row++) {
			if(breakable) {
				c = new BreakableSoftStickConstraint(points[row][xres], points[row+1][xres], defaultSoftness, breakage);
			}
			else {
				c = new SoftStickConstraint(points[row][xres], points[row+1][xres], defaultSoftness);
			}
			constraints[row][xres-1][3] = c;
			s.addConstraint(c);
			allConstraints.add(c);
		}
		
		for(int column = 0; column < xres; column++) {
			if(breakable) {
				c = new BreakableSoftStickConstraint(points[yres][column], points[yres][column+1], defaultSoftness, breakage);
			}
			else {
				c = new SoftStickConstraint(points[yres][column], points[yres][column+1], defaultSoftness);
			}
			constraints[yres-1][column][2] = c;
			s.addConstraint(c);
			allConstraints.add(c);
		}
		
		/*for(int spacing = 1; spacing < 8; spacing++) {
			for(int row = 0; row < yres-spacing+1; row++) {
				for(int column = 0; column < xres-spacing+1; column++) {
					s.addConstraint(new SoftStickConstraint(points[row][column], points[row+spacing][column+spacing], 0.01));
					s.addConstraint(new SoftStickConstraint(points[row+spacing][column], points[row][column+spacing], 0.01));
				}
			}
		}*/
		
		for(int row = 0; row <= yres; row++) {
			for(int column = 0; column <= xres; column++) {
				for(int row2 = 0; row2 <= yres; row2++) {
					for(int column2 = 0; column2 <= xres; column2++) {
						if((row == row2 && Math.abs(column-column2) <= 1) ||
							(column== column2 && Math.abs(row-row2) <= 1)) {
							continue;
						}
						double dist = Math.sqrt((row-row2)*(row-row2) + (column-column2)*(column-column2));
						if(dist <= 3) {
							if(breakable) {
								c = new BreakableSoftStickConstraint(points[row][column], points[row2][column2], 0.05, breakage/dist);
							}
							else {
								c = new SoftStickConstraint(points[row][column], points[row2][column2], 0.05);
							}
							s.addConstraint(c);
							allConstraints.add(c);
						}
					}
				}
			}
		}
		
		//s.addForce(this);
	}
	
	public void delete() {
		for(PhysPoint[] pa : points) {
			for(PhysPoint p : pa) {
				p.delete();
			}
		}
		for(Constraint c : allConstraints) {
			c.delete();
		}
		allConstraints.clear();
	}
	
	public double computeBrokenPercent() {
		if(!breakable) {
			return 0;
		}
		int brokenNum = 0;
		for(SoftStickConstraint c : allConstraints) {
			if(((BreakableSoftStickConstraint)c).isBroken()) {
				brokenNum++;
			}
		}
		return (double)brokenNum/(double)allConstraints.size();
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
	
	private void generatePoints() {
		double mass = 10.0/((xres+1)*(yres+1));
		
		double cellwidth = width/xres;
		double cellheight = height/yres;
		
		for(int row = 0; row <= yres; row++) {
			double y = (height/2) - row*cellheight;
			for(int column = 0; column <= xres; column++) {
				double x = -(width/2) + column*cellwidth;
				
				points[row][column] = new PhysPoint(new Vec2D(x, y), new Vec2D(x, y), mass);
			}
		}
	}
	
	public void updateTexture(Texture tex){
		this.texture = tex;
	}

	public void renderMesh(GL2 gl) {
		
		texture.bind();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		for(int row = 0; row < yres; row++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int column = 0; column <= xres; column++) {
				
				boolean broken0 = false, broken1 = false, broken2 = false, broken3 = false;
				if(breakable) {
					if(((BreakableSoftStickConstraint)constraints[row][Math.min(column, xres-1)][0]).isBroken()) { broken0 = true; }
					if(((BreakableSoftStickConstraint)constraints[row][Math.min(column, xres-1)][1]).isBroken()) { broken1 = true; }
					if(((BreakableSoftStickConstraint)constraints[row][Math.min(column, xres-1)][2]).isBroken()) { broken2 = true; }
					if(((BreakableSoftStickConstraint)constraints[row][Math.min(column, xres-1)][3]).isBroken()) { broken3 = true; }
				}
				
				Vec2D point = points[row][column].pos;
				Vec2D texcoord = texcoords[row][column];
				
				gl.glTexCoord2d(texcoord.x, texcoord.y);
				gl.glVertex2d(point.x, point.y);
				
				point = points[row+1][column].pos;
				texcoord = texcoords[row+1][column];
				
				gl.glTexCoord2d(texcoord.x, texcoord.y);
				gl.glVertex2d(point.x, point.y);
				
				if(broken0 || broken1 || broken2 || broken3) {
					gl.glEnd();
					if(column < xres) {
						gl.glBegin(GL2.GL_TRIANGLES);
						
						if(!broken0 && !broken1) {
							point = points[row][column].pos;
							texcoord = texcoords[row][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row+1][column].pos;
							texcoord = texcoords[row+1][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row][column+1].pos;
							texcoord = texcoords[row][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
						}
						
						if(!broken1 && !broken2) {
							point = points[row][column].pos;
							texcoord = texcoords[row][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row+1][column].pos;
							texcoord = texcoords[row+1][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row+1][column+1].pos;
							texcoord = texcoords[row+1][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
						}
						
						if(!broken2 && !broken3) {
							point = points[row+1][column].pos;
							texcoord = texcoords[row+1][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row+1][column+1].pos;
							texcoord = texcoords[row+1][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row][column+1].pos;
							texcoord = texcoords[row][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
						}
						
						if(!broken3 && !broken0) {
							point = points[row+1][column+1].pos;
							texcoord = texcoords[row+1][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row][column+1].pos;
							texcoord = texcoords[row][column+1];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
							
							point = points[row][column].pos;
							texcoord = texcoords[row][column];
							gl.glTexCoord2d(texcoord.x, texcoord.y);
							gl.glVertex2d(point.x, point.y);
						}
						
						gl.glEnd();
					}
					gl.glBegin(GL2.GL_QUAD_STRIP);
				}
			}
			gl.glEnd();
		}
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	public void renderWireframe(GL2 gl) {
		for(int row = 0; row <= yres; row++) {
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int column = 0; column <= xres; column++) {
				Vec2D point = points[row][column].pos;
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
		}
		
		for(int column = 0; column <= xres; column++) {
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3d(1.0, 1.0, 1.0);
			for(int row = 0; row <= yres; row++) {
				Vec2D point = points[row][column].pos;
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
		}
	}
	
	private void spring(PhysPoint a, PhysPoint b) {
		Vec2D restPoint = b.pos.sub(a.pos).normalized().scale(restLength);
		Vec2D force = b.pos.sub(a.pos).sub(restPoint).scale(-k);
		b.accel = b.accel.add(force.scale(1.0/b.mass));
		a.accel = a.accel.add(force.scale(-1.0/a.mass));
	}

	public void accumulate() {
		if(true) {
		for(int row = 0; row < yres; row++) {
			for(int column = 0; column < xres; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+1][column+1];
				spring(a, b);
				
				a = points[row][column+1];
				b = points[row+1][column];
				spring(a, b);
			}
		}
		
		for(int row = 0; row < yres-1; row++) {
			for(int column = 0; column < xres-1; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+2][column+2];
				spring(a, b);
				
				a = points[row][column+2];
				b = points[row+2][column];
				spring(a, b);
			}
		}
		}
		for(int row = 0; row < yres-2; row++) {
			for(int column = 0; column < xres-2; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+3][column+3];
				spring(a, b);
				
				a = points[row][column+3];
				b = points[row+3][column];
				spring(a, b);
			}
		}
	}

	public void render(GL2 gl) {
		if(true) {
		gl.glColor3d(1, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		for(int row = 0; row < yres; row++) {
			for(int column = 0; column < xres; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+1][column+1];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
				
				a = points[row][column+1];
				b = points[row+1][column];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
			}
		}
		gl.glEnd();
		gl.glColor3d(1, 0.5, 0);
		gl.glBegin(GL2.GL_LINES);
		for(int row = 0; row < yres-1; row++) {
			for(int column = 0; column < xres-1; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+2][column+2];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
				
				a = points[row][column+2];
				b = points[row+2][column];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
			}
		}
		gl.glEnd();
		}
		gl.glColor3d(1, 1, 0);
		gl.glBegin(GL2.GL_LINES);
		for(int row = 0; row < yres-2; row++) {
			for(int column = 0; column < xres-2; column++) {
				PhysPoint a = points[row][column];
				PhysPoint b = points[row+3][column+3];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
				
				a = points[row][column+3];
				b = points[row+3][column];
				
				gl.glVertex2d(a.pos.x, a.pos.y);
				gl.glVertex2d(b.pos.x, b.pos.y);
			}
		}
		gl.glEnd();
	}
}
