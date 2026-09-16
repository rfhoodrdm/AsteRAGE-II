/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.state.Asterage2State;
import com.rfhoodrdm.asterage2.gui.GUI;

/**
 *
 * @author roberthood
 */
public class PowerUpGravityNet
extends PowerUpBaseObject
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public PowerUpGravityNet(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate);
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected BufferedImage getSimpleSprite()	{	return  GUI.Image.A2_POWER_UP_GRAVITY_NET.getImage();	}
	
	@Override	public void handlePickup(Asterage2State asterage2State)	
	{
		//set the flag to show that the gravity net is equipped now. Then set the expired flag to true.
		asterage2State.getPlayerShip().setGravityNetEquipped(true);
		setExpiredFlag(true);
	} //end method handlePickup
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	
} //end PowerUpGravityNet definition
