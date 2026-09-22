package com.rfhoodrdm.asterage2.gui.titlescreen.components;

import java.awt.Color;
import java.awt.Graphics;

import com.rfhoodrdm.asterage2.gui.GUI;

public class StarPoint {

	private int xCoordinate; 
	private int yCoordinate;
	private int speed; 

	private StarPoint(int passedXLocation, int passedYLocation) {
		xCoordinate = passedXLocation;
		yCoordinate = passedYLocation;
		speed = (int) Math.floor(Math.random() * 4) + 1; // from 1 to 4.
	}

	public static StarPoint createNewRandomStarPoint() {
		return new StarPoint((int) Math.floor(Math.random() * GUI.panelWidth),
				(int) Math.floor(Math.random() * GUI.panelHeight));
	} 

	public static StarPoint createNewEdgeStar() {
		// can spawn anywhere along top or right edge.
		int locationRange = GUI.panelWidth + GUI.panelHeight;
		int randomLocation = (int) Math.floor(Math.random() * locationRange);

		if (randomLocation < GUI.panelWidth) {
			// then it's a top edge star.
			return new StarPoint(randomLocation, 0);
		} else {
			// else it's a right edge star
			return new StarPoint(GUI.panelWidth, randomLocation - GUI.panelWidth);
		}
	}

	public boolean checkExpired() {
		// if we've traveled off of the right edge or bottom edge, then it has expired.
		return ((xCoordinate < 0) || (yCoordinate > GUI.panelHeight));
	}

	public void paintStar(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawLine(xCoordinate - 1, yCoordinate, xCoordinate + 1, yCoordinate);
		g.drawLine(xCoordinate, yCoordinate - 1, xCoordinate, yCoordinate + 1);
	}

	public void moveStar() {
		xCoordinate -= speed;
		yCoordinate += speed;
	}

}
