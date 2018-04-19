package model.visualElements;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPanelTempLabels extends JPanel{
	private static final int PREF_WIDTH = 200;
	private static final int PREF_HEIGHT = 400;
	
	private List<JLabel> jPanelLabels;
	
	public JPanelTempLabels(){
		this.jPanelLabels = new ArrayList<JLabel>();
	}
	
	public void addJPanel(String textLabel) {
		JLabel jLabel = new JLabel(textLabel);
		jLabel.setVerticalTextPosition(JLabel.TOP);
		jLabel.setHorizontalTextPosition(JLabel.LEFT);
		
		this.jPanelLabels.add(jLabel);
		this.add(jLabel);
	}
	
	public void updateJLabelText(String textLabel, int position){
		this.jPanelLabels.get(position).setText(textLabel);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(PREF_WIDTH, PREF_HEIGHT);
	}

}
