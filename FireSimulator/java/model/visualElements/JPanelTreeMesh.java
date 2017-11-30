package model.visualElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class JPanelTreeMesh extends JPanel{
	private static final int PREF_WIDTH = 300;
	private static final int PREF_HEIGHT = 300;
	
	private List<RectangleTree> jPanelTrees;
	private Color fillColor;
	
	public JPanelTreeMesh(){
		jPanelTrees = new ArrayList<RectangleTree>();
	}
	
	public void setFillColor(Color fillColor){
		this.fillColor = fillColor;
	}
	
	public void addJPanelTree(RectangleTree jPanelTree){
		jPanelTrees.add(jPanelTree);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(PREF_WIDTH, PREF_HEIGHT);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		for(RectangleTree jPanelTree: jPanelTrees){
			g2.setColor(jPanelTree.getTree().getTreeState().getColor());
			g2.fillRect(jPanelTree.x, jPanelTree.y, jPanelTree.width, jPanelTree.height);
			g2.setColor(Color.BLACK);
			g2.draw(jPanelTree);
		}
	}
}
