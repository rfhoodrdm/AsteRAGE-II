
package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;

public class Mythicite
extends PowerUpBaseObject
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public final double MYTHICITE_DROP_RATE = 0.01;		//how frequently does mythicite appear on the game board
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Mythicite(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate);
		randomizeStartingVelocityAndDirection();		//random direction and semi-random velocity
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected BufferedImage getSimpleSprite()	{ return Image.A2_MYTHICITE.getImage();	}
	
	/**
	 * What to do when this power up is collected.
	 * @param asterage2State 
	 */
	public void handlePickup( Asterage2State asterage2State )
	{
		//add 1 power-up point and set the expired flag to true.
		asterage2State.setPowerUpPoints( asterage2State.getPowerUpPoints() + 1);
		
		setExpiredFlag(true);
	} //end method handlePickup
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
} //end class Mythicite definition
