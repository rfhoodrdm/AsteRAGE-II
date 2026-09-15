/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameEffects.asterage1;

import utility.GameConstants;

import gameObjects.asterage1.SpaceObject;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.concurrent.ConcurrentLinkedQueue;
import objectBehaviors.asterage1.DeploysShields;

import java.awt.Color;

/**
 *
 * @author roberthood
 */
public class ShieldRing
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public final static int shieldRingLifespan = (int) Math.floor ( GameConstants.FRAMES_PER_SECOND * 1.5 );
	public final static double standardShieldWidth = 5.0;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public ShieldRing ( SpaceObject spaceObjectAttachment )
	{
		//invoke call to super constructor, Space Effect.
		super ( spaceObjectAttachment.getXPosition(), spaceObjectAttachment.getYPosition(),
				shieldRingLifespan, spaceObjectAttachment );
		
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public void paintToBoard ( Graphics g)
	{
		DeploysShields shieldBearingObject;
		if ( spaceObjectAttachment instanceof DeploysShields)
		{
			shieldBearingObject = (DeploysShields) spaceObjectAttachment;	//get a reference we can use for shield stats.
		} 
		else 
		{
			return;		//cannot work with objects that don't use shields.
		} 
		
		//calculate shield width by calculating remaining shield ratio.
		double currentShields = shieldBearingObject.getCurrentShields();
		double maxShields = shieldBearingObject.getMaxShields();
		double shieldRatio;
		
		if ( maxShields > 0.0)
		{
			shieldRatio =  currentShields  / maxShields ;
			if ( 0.0 > shieldRatio )
			{
				shieldRatio = 0.0;
			} //end if check for negative ratio
		} 
		else 
		{
			shieldRatio = 0.01;
		} 
		
		double shieldWidth = standardShieldWidth * shieldRatio;
		
		//convert to graphics 2d to get enhanced drawing capability.
		Graphics2D g2d = (Graphics2D) g;
		
		//get the location and other paint parameters.
		int offset = ( spaceObjectAttachment.getDiameter()  / 2 ) + 5;
		int diameter = (int) Math.floor ( offset * 2 );
		double startXPosition = spaceObjectAttachment.getXPosition();
		double startYPosition = spaceObjectAttachment.getYPosition();
		
		//select the color of the ring. It will fade to black over time.
		double fadeRatio = 1.0;
			
		if (0 == this.maxLifespan)
		{
			fadeRatio = 0.0;
		} //end if clause for lifeExpectancy = 0
		else
		{
			fadeRatio = (double) this.currentLifespan / this.maxLifespan;
			if ( 0.0 > fadeRatio )
			{
				fadeRatio = 0.0;
			} //end if check for negative ratio
		} //end else

		//set the color as white, faded by the appropriate amount.
		//the fade causes the color to shift from white to black.
		g2d.setPaint( new Color (	(int)	Math.floor(255 * fadeRatio), 
									(int)	Math.floor(255 * fadeRatio), 
									(int)	Math.floor(255 * fadeRatio)  )
						);
		
		//set brush stroke.
		g2d.setStroke( new BasicStroke ( (float) shieldWidth ) );
		
		//draw the shield ring.
		g2d.drawOval(	(int) Math.floor( startXPosition - offset), 
						(int) Math.floor( startYPosition - offset), 
						diameter, diameter);
		
		g2d.setStroke( new BasicStroke ( (float) 1.0 ) );
		
		
	} //end function paintToBoard
	
	public void killObject ( ConcurrentLinkedQueue <SpaceObject> spaceObjectList )
	{
		if ( this.spaceObjectAttachment instanceof DeploysShields )
		{
			DeploysShields shieldBearingObject = (DeploysShields) this.spaceObjectAttachment;
			shieldBearingObject.detachShields();
		} 
		spaceObjectList.remove ( this );
	} //end function 
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
