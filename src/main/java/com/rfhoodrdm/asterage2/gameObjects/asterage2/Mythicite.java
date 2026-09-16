/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.state.Asterage2State;

/**
 *
 * @author roberthood
 */
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
	
	@Override	protected BufferedImage getSimpleSprite()	{ return GUI.Image.A2_MYTHICITE.getImage();	}
	
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
