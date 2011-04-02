package com.physics;

public class HandObject {
	private int x;
	private int y;
	private int hand;
	private int state;
	private boolean pressed;
	
	public HandObject(int x, int y, int hand, int state, boolean pressed) {
		this.x = x;
		this.y = y;
		this.hand = hand;
		this.state = state;
		this.pressed = pressed;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getHand() {
		return this.hand;
	}
	
	public int getState() {
		return this.state;
	}
	
	public boolean getPressed() {
		return this.pressed;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
}
