package model;

import java.util.ArrayList;
import org.contikios.cooja.Mote;

public class MoteRangeTree {

	private final Mote mote;
	private ArrayList<Tree> treesInRange;
	
	public MoteRangeTree(Mote mote) {
		super();
		this.mote = mote;
		this.treesInRange = new ArrayList<Tree>();
	}	
	
	public void addTree(Tree tree){
		treesInRange.add(tree);
	}

	public Mote getMote() {
		return mote;
	}

	public ArrayList<Tree> getTreesInRange() {
		return treesInRange;
	}
	
	public static boolean checkTreeRangeMote(Tree tree, Mote mote, double range){
		
		double x1 = tree.getxPosition();
		double x2 = mote.getInterfaces().getPosition().getXCoordinate();
		
		double y1 = tree.getyPosition();
		double y2 = mote.getInterfaces().getPosition().getYCoordinate();
		
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) <= range;
	}
	
}
