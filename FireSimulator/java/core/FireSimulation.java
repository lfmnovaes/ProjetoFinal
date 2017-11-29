package core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.contikios.cooja.Cooja;
import org.contikios.cooja.Mote;
import org.contikios.cooja.PluginType;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.TimeEvent;
import org.contikios.cooja.VisPlugin;
import org.contikios.cooja.dialogs.MessageList;
import org.contikios.cooja.mspmote.SkyMote;
import org.contikios.cooja.mspmote.interfaces.*;
import org.jdom.Element;

import model.MoteRangeTree;
import model.Tree;
import model.TreeState;
import visualElements.JPanelTreeMesh;

@PluginType(PluginType.SIM_PLUGIN)
public class FireSimulation extends VisPlugin{
	
	private static final long serialVersionUID = 1L;
	
	private static final double TIME_BETWEEN_TURNS = 5.0;
	
	private static final double TEMPERATURE_RANGE = 7.0;
	
	private static final double SPACE_BETWEEN_TREES = 10.0;
	private static final double EXTRA_TREES = 2.0;
	
	private static final double GROWN_TREE_TO_HOT = 0.2;
	private static final double HOT_TREE_TO_RISK = 0.2;
	
	private static final double RISK_FIRE_IGNITES = 0.02;
	
	private static final double ESTABLISHED_FIRE_SPREAD = 0.1;
	private static final double GLOWING_FIRE_SPREAD = 0.4;
	
	private Tree[][] treeMesh;
	private ArrayList<Mote> motes;
	private ArrayList<MoteRangeTree> treesInRangeMote;
	
	private Simulation simulation;
	private MessageList logTemp;
	
	private JPanelTreeMesh malhaArvoresPainel;
	
	public FireSimulation(Simulation simulation, final Cooja gui) {
		super("FireSimulation", gui, false);
		this.simulation = simulation;
		
		this.logTemp = new MessageList();
	    
	    setSize(500,400);
	    
	    logTemp.addPopupMenuItem(null, true);
	    add(new JScrollPane(logTemp), BorderLayout.SOUTH);
	    
	    logTemp.addMessage("Fire Simulation started!");
	    
	    //setSize(500,200);
	    
	    malhaArvoresPainel = new JPanelTreeMesh();
	    malhaArvoresPainel.setSize(300, 200);
	    //malhaArvoresPainel.setPreferredSize(new Dimension(300,300));
	    
	    //getContentPane().add(malhaArvoresPainel, BorderLayout.NORTH);
	    
	    add(malhaArvoresPainel, BorderLayout.NORTH);
	    
	    malhaArvoresPainel.setLayout(new BoxLayout(malhaArvoresPainel, BoxLayout.X_AXIS));
	    
	    //malhaArvoresPainel.add(new JButton(startPlugin));
		
	}
	
	public void startPlugin() {
		if(simulation.getMotesCount() == 0){
			logTemp.addMessage("Simulation without motes! Create at least one mote before loading this plugin.");
			return;
		}
		
		motes = new ArrayList<Mote>(Arrays.asList(simulation.getMotes()));
		
		treesInRangeMote = new ArrayList<MoteRangeTree>();
		
		generateTrees();
		
		calculateTreesInRange();
		
		simulation.invokeSimulationThread(new Runnable() {
			public void run() {
			
				nextTurn.execute(simulation.getSimulationTime());
	        }
		});
	}
	
	public Collection<Element> getConfigXML() {
	    return null;
	}
	
	public boolean setConfigXML(Collection<Element> configXML, boolean visAvailable) {
		return true;
	}
	
	public void generateTrees(){
		
		double minX = motes.get(0).getInterfaces().getPosition().getXCoordinate();
		double maxX = motes.get(0).getInterfaces().getPosition().getXCoordinate();
		double minY = motes.get(0).getInterfaces().getPosition().getYCoordinate();
		double maxY = motes.get(0).getInterfaces().getPosition().getYCoordinate();
		
		
		
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
				Double x = minX + (i-(int)EXTRA_TREES)*SPACE_BETWEEN_TREES;
				Double y = minY + (j-(int)EXTRA_TREES)*SPACE_BETWEEN_TREES;
				
				Double offsetX = (minX + -1*EXTRA_TREES*SPACE_BETWEEN_TREES) < 0 ? (minX + -1*EXTRA_TREES*SPACE_BETWEEN_TREES)*-1 + 5 : 0;
				Double offsetY = (minY + -1*EXTRA_TREES*SPACE_BETWEEN_TREES) < 0 ? (minY + -1*EXTRA_TREES*SPACE_BETWEEN_TREES)*-1 + 5: 0;
				
				treeMesh[i][j] = new Tree(x, y);
				
				malhaArvoresPainel.addJPanelTree(x.intValue() + offsetX.intValue(), y.intValue() + offsetY.intValue(), 5, 5);
				
				//logTemp.addMessage("Arvores criada na posicao: (" + (x.intValue() + offsetX.intValue()) + ", " + (y.intValue() + offsetY.intValue()) + ")");
			}
		}
	}
	
	public void calculateTreesInRange() {
		for(int t=0; t<this.motes.size(); t++){
			
			MoteRangeTree moteRangeTree = new MoteRangeTree(this.motes.get(t));
			
			for(int i=0; i<treeMesh[0].length; i++){
				for(int j=0; j<treeMesh.length; j++){
					if(MoteRangeTree.checkTreeRangeMote(treeMesh[j][i], this.motes.get(t), TEMPERATURE_RANGE)){
						moteRangeTree.addTree(treeMesh[j][i]);
					}
				}
			}
			
			treesInRangeMote.add(t, moteRangeTree);
		}
	}
	
	private TimeEvent nextTurn = new TimeEvent(0) {
	    public void execute(long t) {
	    	updateFireSpread();
	    	
	    	updateTreeStates();
	    	
	    	updateMotesSensorTemperature();
	    	
	    	simulation.scheduleEvent(this, (long) (simulation.getSimulationTime() + TIME_BETWEEN_TURNS*1000*Simulation.MILLISECOND));
	    }
	};
	
	public void closePlugin() {
		nextTurn.remove();
	}
	
	public void updateMotesSensorTemperature(){
		double totalRange = 0.0;
		double totalTemperatureRange = 0.0;
		
		for(int i=0; i<this.motes.size(); i++){
			for(int j=0; j<treesInRangeMote.get(i).getTreesInRange().size(); j++){
				totalRange += treesInRangeMote.get(i).getTreesRange().get(j);
				
				totalTemperatureRange += treesInRangeMote.get(i).getTreesInRange().get(j).getTreeTemperature()*(TEMPERATURE_RANGE - treesInRangeMote.get(i).getTreesRange().get(j));
			}
			
			SkyMote skyMote = (SkyMote)this.motes.get(i);
			
			SkyTemperature skyTemperature = (SkyTemperature) skyMote.getInterfaces().get("SkyTemperature");
			
			skyTemperature.setTemperature(totalTemperatureRange/totalRange);
			
			logTemp.addMessage("Temperature do mote " + motes.get(i).getID() + ": " + totalTemperatureRange/totalRange);
			
			totalRange = 0.0;
			totalTemperatureRange = 0.0;
		}
	}
			
	    
	public void updateFireSpread(){
		for(int i=0; i<treeMesh[0].length; i++){
			for(int j=0; j<treeMesh.length; j++){
				if(treeMesh[j][i].getTreeState() == TreeState.ESTABLISHED_FIRE){
					if((j+1) < treeMesh.length) {
						spreadEstablished(treeMesh[j+1][i]);
					}
					
					if((j-1) >= 0) {
						spreadEstablished(treeMesh[j-1][i]);
					}
					
					if((i+1) < treeMesh[0].length) {
						spreadEstablished(treeMesh[j][i+1]);
					}
					
					if((i-1) >= 0) {
						spreadEstablished(treeMesh[j][i-1]);
					}
				} else if(treeMesh[j][i].getTreeState() == TreeState.GLOWING_EMBER) {
					if((j+1) < treeMesh.length) {
						spreadGlowing(treeMesh[j+1][i]);
					}
					
					if((j-1) >= 0) {
						spreadGlowing(treeMesh[j-1][i]);
					}
					
					if((i+1) < treeMesh[0].length) {
						spreadGlowing(treeMesh[j][i+1]);
					}
					
					if((i-1) >= 0) {
						spreadGlowing(treeMesh[j][i-1]);
					}
				}				
			}
		}
	}
	
	public void updateTreeStates(){
		
		for(int i=0; i<treeMesh[0].length; i++){
			for(int j=0; j<treeMesh.length; j++){
				if(treeMesh[j][i].getTreeState() == TreeState.ASHES){
					continue;
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.GROWN_TREE){
					if(Math.random() <= GROWN_TREE_TO_HOT){
						treeMesh[j][i].setTreeState(TreeState.HOT_TREE);
					}
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.HOT_TREE){
					if(Math.random() <= HOT_TREE_TO_RISK){
						treeMesh[j][i].setTreeState(TreeState.RISK_FIRE);
					} else {
						treeMesh[j][i].setTreeState(TreeState.GROWN_TREE);
					}
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.RISK_FIRE){
					if(Math.random() <= RISK_FIRE_IGNITES){
						treeMesh[j][i].setTreeState(TreeState.FRESH_LIT);
						logTemp.addMessage("Tree: [" + j + "][" + i + "] just lit!");
					} else {
						treeMesh[j][i].setTreeState(TreeState.HOT_TREE);
					}
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.FRESH_LIT){
					updateTreeOnFire(treeMesh[j][i], TreeState.ESTABLISHED_FIRE);
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.ESTABLISHED_FIRE){
					updateTreeOnFire(treeMesh[j][i], TreeState.GLOWING_EMBER);
				}

				if(treeMesh[j][i].getTreeState() == TreeState.GLOWING_EMBER){
					updateTreeOnFire(treeMesh[j][i], TreeState.GREY_CHARCOAL);
				}
				
				if(treeMesh[j][i].getTreeState() == TreeState.GREY_CHARCOAL){
					updateTreeOnFire(treeMesh[j][i], TreeState.ASHES);
					logTemp.addMessage("Tree: [" + j + "][" + i + " became ashes!");
					
				}
			}
		}
	}
	
	private void spreadEstablished(Tree destination){
		if(destination.getTreeState() == TreeState.GROWN_TREE || destination.getTreeState() == TreeState.HOT_TREE || destination.getTreeState() == TreeState.RISK_FIRE){
			if(Math.random() <= ESTABLISHED_FIRE_SPREAD){
				destination.setTreeState(TreeState.FRESH_LIT);
				logTemp.addMessage("Fire spreding !");
			}
		}
	}
	
	private void spreadGlowing(Tree destination){
		if(destination.getTreeState() == TreeState.GROWN_TREE || destination.getTreeState() == TreeState.HOT_TREE || destination.getTreeState() == TreeState.RISK_FIRE){
			if(Math.random() <= GLOWING_FIRE_SPREAD){
				destination.setTreeState(TreeState.FRESH_LIT);
				logTemp.addMessage("Fire spreding !");
			}
		}
	}
	
	private void updateTreeOnFire(Tree tree, TreeState newTreeState){
		if(tree.getTimer() == 0){
			tree.setTreeState(newTreeState);
		} else {
			tree.setTimer(tree.getTimer() - 1);
		}
	}
	
	private Action startPlugin = new AbstractAction("Start"){
		public void actionPerformed(ActionEvent e){
			logTemp.addMessage("Começo!");
		}
	};
	
	public static void main(String[] args) {
		//FireSimulation fireSimulation = new FireSimulation(-14.3, 70.7, -50.1, 47.8, null);
    }
	
}
