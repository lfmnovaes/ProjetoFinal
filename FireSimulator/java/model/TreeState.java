package model;

import java.awt.Color;
import java.util.Random;

public enum TreeState {
	
	GROWN_TREE(-1, 20.0, 39.0, Color.GREEN),
	HOT_TREE(-1, 70.0, 150.0, Color.WHITE),
	RISK_FIRE(1, 195.0, 270.0, Color.LIGHT_GRAY),
	FRESH_LIT(5, 280.0, 350.0, Color.YELLOW ),
	ESTABLISHED_FIRE(10, 380.0, 550.0, Color.ORANGE),
	GLOWING_EMBER(40, 600.0, 950.0, Color.RED),
	GREY_CHARCOAL(35, 300.0,550.0, Color.DARK_GRAY),
	ASHES(-1, 30.0, 270.0, Color.GRAY);
	
	private final int duration;
	private final double maxTemp;
	private final double minTemp;
	private final Color color;
	
	private TreeState(int duration, double minTemp, double maxTemp, Color color) {
		this.duration = duration;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
		this.color = color;
	}
	
	public double getTemperature(){
		Random random = new Random();
		double media = (this.maxTemp + this.minTemp)/2;
		double desvioPadrao = (this.maxTemp - this.minTemp)/6;
		double result = desvioPadrao*random.nextGaussian() + media;
		while(result < this.minTemp || result > this.maxTemp){
			result = desvioPadrao*random.nextGaussian() + media;
			//System.out.println(media + "|" + desvioPadrao + "|" + result);
		}
		return result;
	}

	public int getDuration() {
		return duration;
	}

	public Color getColor() {
		return color;
	}
	
}
