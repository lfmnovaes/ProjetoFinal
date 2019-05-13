package model.visualElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.contikios.cooja.Mote;

public class JPanelTreeMesh extends JPanel{
	private static final int PREF_WIDTH = 300;
	private static final int PREF_HEIGHT = 300;
	
	private List<RectangleTree> jPanelTrees;
	private List<Mote> jPanelMotes;
	private Color fillColor;
	
	public JPanelTreeMesh(){
		jPanelTrees = new ArrayList<RectangleTree>();
		jPanelMotes = new ArrayList<Mote>();
	}
	
	public void setFillColor(Color fillColor){
		this.fillColor = fillColor;
	}
	
	public void addJPanelTree(RectangleTree jPanelTree){
		jPanelTrees.add(jPanelTree);
	}
	
	public void addMote(Mote mote){
		jPanelMotes.add(mote);
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
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		for(Mote mote: jPanelMotes){
			g2.setColor(Color.BLACK);
			Ellipse2D.Double shapeCirculo = new Ellipse2D.Double(mote.getInterfaces().getPosition().getXCoordinate() + 40.0 - 5.0, mote.getInterfaces().getPosition().getYCoordinate() + 40.0 - 5.0, 10, 10);
			g2.draw(shapeCirculo);
			
			g2.setColor(Color.BLUE);
			Ellipse2D.Double shapeBorda = new Ellipse2D.Double(mote.getInterfaces().getPosition().getXCoordinate() + 40.0 - 20.0, mote.getInterfaces().getPosition().getYCoordinate() + 40.0 - 20.0, 40, 40);
			g2.draw(shapeBorda);
		}
		
	}
}
