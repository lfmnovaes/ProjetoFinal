package model;

import java.util.Random;

public enum TreeState {
	
	GROWN_TREE(-1, 20.0, 39.0),
	HOT_TREE(-1, 70.0, 150.0),
	RISK_FIRE(1, 195.0, 270.0),
	FRESH_LIT(5, 280.0, 350.0 ),
	ESTABLISHED_FIRE(10, 380.0, 550.0),
	GLOWING_EMBER(40, 600.0, 950.0),
	GREY_CHARCOAL(35, 300.0,550.0),
	ASHES(-1, 30.0, 270.0);
	
	private final int duration;
	private final double maxTemp;
	private final double minTemp;
	
	private TreeState(int duration, double maxTemp, double minTemp) {
		this.duration = duration;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
	}
	
	public double getTemperature(){
		Random random = new Random();
		return this.minTemp + (this.maxTemp - minTemp)*random.nextGaussian();
	}

	public int getDuration() {
		return duration;
	}
	
}
