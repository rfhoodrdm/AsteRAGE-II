package com.rfhoodrdm.asterage2.gameEffects.asterage1;

import com.rfhoodrdm.asterage2.gui.Image;

import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.LimitedLifespan;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.Graphics;

/**
 *	SpaceEffects are an extension of space objects, but have their own unique movement and drawing functions.
 * @author roberthood
 */
public abstract class SpaceEffect
extends SpaceObject
implements LimitedLifespan
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	SpaceObject spaceObjectAttachment;		//is this effect attached to any space object?
	BufferedImage noSprite;					//no sprite image, but have nothing to return just in case.
	int currentLifespan;
	int maxLifespan;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SpaceEffect ( double passedXPosition, double passedYPosition, int passedLifespan, SpaceObject passedSpaceObjectAttachment )
	{
		//invoke super constructor for SpaceObject, passing location parameters.
		//We only care about starting position. Movement and drawing are taken care of by the respective effect.
		super ( passedXPosition, passedYPosition,
				0, 0, 0, 0,
				0.0, 0.0, 0.0);
				
		//set local variables
		this.noSprite = Image.NO_IMAGE.getImage();
		this.spaceObjectAttachment = passedSpaceObjectAttachment;
		this.maxLifespan = passedLifespan;
		this.currentLifespan = maxLifespan;
	} //end function 
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Space effects have no image to return to drawing functions.
	 * @return 
	 */
	protected BufferedImage selectSprite ()
	{
		return this.noSprite;
	} //end function selectSprite
	
	/**
	 * SpaceEffects do not move as normal space objects do.
	 * If they are attached to another space object, then they move with that object. 
	 * If not, then they are stationary.
	 */
	@Override
	public void moveObject ()
	{
		if ( null == this.spaceObjectAttachment )
		{
			super.moveObject();		//use regular spaceobject movement if not tracking.
			return;
		} //end if block to check for no attached space object
		
		//else
		double newXPosition = spaceObjectAttachment.getXPosition();
		double newYPosition = spaceObjectAttachment.getYPosition();
		setPosition(newXPosition, newYPosition);
	} 
	
	/*		********************		LimitedLifespan Interface			******************	*/
	
	@Override
	public void resetLifespan ()
	{
		this.currentLifespan = this.maxLifespan;
	} //end function resetLifespan
	
	@Override
	public void ageObject ()
	{
		if ( this.currentLifespan > 0 )
		{
			this.currentLifespan -= 1;
		}
	} //end function ageObject
	
	@Override
	public boolean checkEndOfLifespan()
	{
		if ( this.currentLifespan <= 0 )
		{
			return true;
		}//end if
		
		//else
		return false;
	} //end function checkEndOfLifeSpan
	
	@Override
	public void endLifespan()
	{
		this.currentLifespan = 0;
	} //end function endLifespan
	
	/**
	 * Depends on the type of space effect. For example, shields are attached, so they have to be removed from the 
	 * reference of the object to which they are attached.
	 * @param spaceObjectList 
	 */
	@Override
	public abstract void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override
	public abstract void paintToBoard ( Graphics g );
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
