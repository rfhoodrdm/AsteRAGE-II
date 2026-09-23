package com.rfhoodrdm.asterage2.gui.asterage2.components;

import java.awt.Color;
import java.awt.Graphics;

public class StarPoint {
	//data members.
	private int xCoord = 0;					//x coordinate on game board
	private int yCoord = 0;					//y coordinate on game board
	Color starColor = Color.WHITE;			//what color to draw on field?
	Color baseColor = getBaseStarColor();	//what is the basic color of the star?
	
	/**
	 * Constructor
	 * @param passedXCoord
	 * @param passedYCoord 
	 */
	public StarPoint ( int passedXCoord, int passedYCoord ) {
		//remember the location set when made
		this.xCoord = passedXCoord;
		this.yCoord = passedYCoord;
	} 
	
	public void paintStar ( Graphics g, int frameNumber ) {
		//draw as two lines crossing.
		checkChangeStarColor ( frameNumber );
		g.setColor( starColor );
		
		g.drawLine(xCoord -1, yCoord, xCoord+1, yCoord);
		g.drawLine(xCoord, yCoord-1, xCoord, yCoord+1);
	} 
	
	private void checkChangeStarColor( int frameNumber ) {
		//see if we should randomly change the star color.
		boolean changeStarColor = ( frameNumber % 3 ) == 0;
		if ( changeStarColor ) {
			starColor = getNewRandomStarColor ();
		} 
	}
	
	private Color getNewRandomStarColor () {
		int randomChance = (int) Math.floor ( Math.random() * 100 ) ;
		if ( randomChance < 1 )	return Color.LIGHT_GRAY;
		if ( randomChance < 2 )	return Color.CYAN;
		if ( randomChance < 3 ) return Color.ORANGE;
		if ( randomChance < 4 )	return Color.YELLOW;
		if ( randomChance < 5 ) return Color.WHITE;
		if ( randomChance < 6 )	return Color.BLACK;
		
		return baseColor;		//else just return the base color
	}
	
	private static Color getBaseStarColor () {
		int randomChance = (int) Math.floor ( Math.random() * 100 ) ;

		if ( randomChance < 1 )		return Color.RED;
		if ( randomChance < 2 )		return Color.ORANGE;
		if ( randomChance < 3 )		return Color.CYAN;
		if ( randomChance < 30 )	return Color.YELLOW;
		return Color.WHITE;
	} 
} 