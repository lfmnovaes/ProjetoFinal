package core;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.contikios.cooja.Cooja;
import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.VisPlugin;
import org.contikios.cooja.dialogs.MessageListText;

import model.Tree;
import model.TreeState;

public class FireSimulation extends VisPlugin{
	
	private static final long serialVersionUID = 1L;
	
	private static final double SPACE_BETWEEN_TREES = 5.0;
	private static final double EXTRA_TREES = 2.0;
	
	private static final double ESTABLISHED_FIRE_SPREAD = 0.1;
	private static final double GLOWING_FIRE_SPREAD = 0.4;
	
	private Tree[][] treeMesh;
	private ArrayList<Mote> motes;
	
	private Simulation simulation;
	private MessageListText log;
	
	public FireSimulation(Simulation simulation, final Cooja gui) {
		super("FireSimulation", gui, false);
		this.simulation = simulation;
		
		this.log = new MessageListText();
		
	}
	
	public void startPlugin() {
		if(simulation.getMotesCount() == 0){
			log.addMessage("Simulation without motes! Create at least one mote before loading this plugin.");
			return;
		}
		
		motes = new ArrayList<Mote>(Arrays.asList(simulation.getMotes()));
		
		generateTrees();
	}
	
	public void generateTrees(){
		
		double minX = motes.get(0).getInterfaces().getPosition().getXCoordinate();
		double maxX = motes.get(0).getInterfaces().getPosition().getXCoordinate();
		double minY = motes.get(0).getInterfaces().getPosition().getYCoordinate();
		double maxY = motes.get(0).getInterfaces().getPosition().getYCoordinate();;
		
		
		
		for(int i=1; i<motes.size(); i++) {
			if(motes.get(i).getInterfaces().getPosition().getXCoordinate() < minX){
				minX = motes.get(i).getInterfaces().getPosition().getXCoordinate();
			} else if(motes.get(i).getInterfaces().getPosition().getXCoordinate() > maxX) {
				maxX = motes.get(i).getInterfaces().getPosition().getXCoordinate();
			}
			
			if(motes.get(i).getInterfaces().getPosition().getYCoordinate() < minY){
				minY = motes.get(i).getInterfaces().getPosition().getYCoordinate();
			} else if(motes.get(i).getInterfaces().getPosition().getYCoordinate() > maxY) {
				maxY = motes.get(i).getInterfaces().getPosition().getYCoordinate();
			}
		}		
		
		int xTreeValue = new Double((maxX - minX)/SPACE_BETWEEN_TREES + EXTRA_TREES*2).intValue() + 1;
		int yTreeValue = new Double((maxY - minY)/SPACE_BETWEEN_TREES + EXTRA_TREES*2).intValue() + 1;
		
		treeMesh = new Tree[xTreeValue][yTreeValue];
		
		for(int i=0; i<xTreeValue; i++) {
			for(int j=0; j<yTreeValue; j++) {
				treeMesh[i][j] = new Tree(minX + (i-(int)EXTRA_TREES)*SPACE_BETWEEN_TREES, minY + (j-(int)EXTRA_TREES)*SPACE_BETWEEN_TREES);			
			}
		}
	}
	
	public void updateFireSpread(){
		for(int i=0; i<treeMesh[0].length; i++){
			for(int j=0; j<treeMesh.length; j++){
				if(treeMesh[j][i].getTreeState() == TreeState.ESTABLISHED_FIRE){
					if(treeMesh[j+1][i] != null) {
						spreadEstablished(treeMesh[j+1][i]);
					}
					
					if(treeMesh[j-1][i] != null) {
						spreadEstablished(treeMesh[j-1][i]);
					}
					
					if(treeMesh[j][i+1] != null) {
						spreadEstablished(treeMesh[j][i+1]);
					}
					
					if(treeMesh[j][i-1] != null) {
						spreadEstablished(treeMesh[j][i-1]);
					}
				} else if(treeMesh[j][i].getTreeState() == TreeState.GLOWING_EMBER) {
					if(treeMesh[j+1][i] != null) {
						spreadGlowing(treeMesh[j+1][i]);
					}
					
					if(treeMesh[j-1][i] != null) {
						spreadGlowing(treeMesh[j-1][i]);
					}
					
					if(treeMesh[j][i+1] != null) {
						spreadGlowing(treeMesh[j][i+1]);
					}
					
					if(treeMesh[j][i-1] != null) {
						spreadGlowing(treeMesh[j][i-1]);
					}
				}				
				
				
				if(treeMesh[j][i].getTreeState() == TreeState.FRESH_LIT){					
					if(treeMesh[j][i].getTimer() == 0){
						treeMesh[j][i].setTreeState(TreeState.ESTABLISHED_FIRE);
					} else {
						treeMesh[j][i].setTimer(treeMesh[j][i].getTimer() - 1);;
					}
				}
			}
		}
	}
	
	private void spreadEstablished(Tree destination){
		if(destination.getTreeState() == TreeState.GROWN_TREE || destination.getTreeState() == TreeState.HOT_TREE || destination.getTreeState() == TreeState.RISK_FIRE){
			if(Math.random() <= ESTABLISHED_FIRE_SPREAD){
				destination.setTreeState(TreeState.FRESH_LIT);
				destination.setTimer(TreeState.FRESH_LIT.getDuration());
			}
		}
	}
	
	private void spreadGlowing(Tree destination){
		if(destination.getTreeState() == TreeState.GROWN_TREE || destination.getTreeState() == TreeState.HOT_TREE || destination.getTreeState() == TreeState.RISK_FIRE){
			if(Math.random() <= GLOWING_FIRE_SPREAD){
				destination.setTreeState(TreeState.FRESH_LIT);
				destination.setTimer(TreeState.FRESH_LIT.getDuration());
			}
		}
	}
	
	public static void main(String[] args) {
		//FireSimulation fireSimulation = new FireSimulation(-14.3, 70.7, -50.1, 47.8, null);
    }
	
}
