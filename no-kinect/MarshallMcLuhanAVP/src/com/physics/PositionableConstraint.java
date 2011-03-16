package com.physics;

public abstract class PositionableConstraint extends Constraint {
	public abstract void setPos(Vec2D pos);
	public abstract Vec2D getPos();
}
