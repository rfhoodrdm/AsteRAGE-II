package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.gui.Image;

public class Mythicite
extends SpaceObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	BufferedImage mythiciteSprite;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Mythicite(double xSpawnLocation, double ySpawnLocation)
	{
		super ( xSpawnLocation, ySpawnLocation,
				50, 0, 0, 20,
				0, 25, 0);
		this.mythiciteSprite = Image.MYTHICITE.getImage();
	}
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public BufferedImage selectSprite()
	{
		return this.mythiciteSprite;
	} //end function selectSprite
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
