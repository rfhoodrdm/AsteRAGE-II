

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;


public class PowerUpShieldRestoration
extends PowerUpBaseObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public PowerUpShieldRestoration(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate);
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected BufferedImage getSimpleSprite()	{	return  Image.A2_POWER_UP_SHIELD_RESTORATION.getImage();	}
	
	@Override	public void handlePickup(Asterage2State asterage2State)	
	{
		//reset the player ship's shields to maximum. Then set the expired flag to true.
		asterage2State.getPlayerShip().setShieldStrength( PlayerShip.MAX_SHIELD_STRENGTH );
		setExpiredFlag(true);
	} //end method handlePickup
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
}