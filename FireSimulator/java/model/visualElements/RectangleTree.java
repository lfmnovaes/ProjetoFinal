package model.visualElements;

import java.awt.Rectangle;

import model.Tree;

public class RectangleTree extends Rectangle{
	Tree tree;
	
	public RectangleTree(int x, int y, int width, int height, Tree tree){
		super(x,y,width,height);
		this.tree = tree;
	}

	public Tree getTree() {
		return tree;
	}
}
