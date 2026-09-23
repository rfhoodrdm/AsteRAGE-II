
package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.image.BufferedImage;


public class PowerUpSonicDisruptor
extends PowerUpBaseObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public PowerUpSonicDisruptor(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate);
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected BufferedImage getSimpleSprite()	{	return  Image.A2_POWER_UP_SONIC_DISRUPTOR.getImage();	}
	
	@Override	public void handlePickup(Asterage2State asterage2State)	
	{
		//set the flag to show that the sonic disruptor is equipped now. Then set the expired flag to true.
		asterage2State.getPlayerShip().setSonicDisruptorEquipped(true);
		setExpiredFlag(true);
	} //end method handlePickup
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class PowerUpSonicDisruptor definition
