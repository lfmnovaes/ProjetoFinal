package model;

import java.awt.Rectangle;

public class Tree {
	
	private double xPosition;
	private double yPosition;
	private TreeState treeState;
	private int timer;
	
	
	public Tree(double xPosition, double yPosition) {
		super();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.treeState = TreeState.GROWN_TREE;
		this.timer = 0;
	}
	public TreeState getTreeState() {
		return treeState;
	}
	public void setTreeState(TreeState treeState) {
		this.treeState = treeState;
		this.timer = treeState.getDuration();
	}
	public double getxPosition() {
		return xPosition;
	}
	public double getyPosition() {
		return yPosition;
	}
	
	public double getTreeTemperature(){
		return treeState.getTemperature();
	}
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	@Override
	public String toString() {
		return "Tree [xPosition=" + xPosition + ", yPosition=" + yPosition + ", treeState=" + treeState + "]";
	}

}
