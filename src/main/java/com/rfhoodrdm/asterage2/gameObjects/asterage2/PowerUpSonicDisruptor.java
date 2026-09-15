/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameObjects.asterage2;

import gui.GUI;
import java.awt.image.BufferedImage;
import state.Asterage2State;

/**
 *
 * @author roberthood
 */
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
	@Override	protected BufferedImage getSimpleSprite()	{	return  GUI.Image.A2_POWER_UP_SONIC_DISRUPTOR.getImage();	}
	
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
