package com.physics;

public class HandObject {
	private int x;
	private int y;
	private int hand;
	private int state;
	
	public HandObject(int x, int y, int hand, int state) {
		this.x = x;
		this.y = y;
		this.hand = hand;
		this.state = state;
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
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setState(int state) {
		this.state = state;
	}
}
