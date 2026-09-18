package com.rfhoodrdm.asterage2.gameEffects.asterage1;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * The ShipDebrisExplosion effect shows a bunch of debris flying out from a central location.
 * It is used when a player or troll ship is destroyed.
 * @author roberthood
 */
public class ShipDebrisExplosion
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public final static int debrisExplosionLifespan = GameConstants.FRAMES_PER_SECOND * 1;
	public final static int debrisExplosionRadius = 100;
	public final static int numberOfDebrisFragments = 12;
	BufferedImage playerShipDebris;
	BufferedImage trollShipDebris;
	DebrisExplosionOwner explosionOwner;
	DebrisStats[] debrisStats = new DebrisStats[ numberOfDebrisFragments ];
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public ShipDebrisExplosion( double passedXLocation, double passedYLocation, DebrisExplosionOwner passedOwner)
	{
		//invoke super class contructor, of SpaceEffect.
		super ( passedXLocation, passedYLocation, debrisExplosionLifespan, null );
		
		//Set local parameters
		playerShipDebris = Image.PLAYER_DEBRIS.getImage();
		trollShipDebris = Image.ENEMY_DEBRIS.getImage();
		this.explosionOwner = passedOwner;		//who owns it?
		
		//initialize the DebrisStats array with new objects and random angles.
		for ( int index = 0;	index < numberOfDebrisFragments;			++index )
		{
			debrisStats[index] = new DebrisStats( ) ;		//self-initializing
		} //end for loop to intialize debris fragments.
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override
	public void paintToBoard ( Graphics g )
	{
		//For each debrisFragment, calculate its new position by applying the angle, life expectancy of the effect, and the
		//radius of the effect.
		double effectProgress = ( maxLifespan - currentLifespan) / (double) maxLifespan;
		double currentRadius = effectProgress * debrisExplosionRadius;
		
		for ( int index = 0; index < numberOfDebrisFragments; ++ index)
		{
			DebrisStats currentDebrisStats = this.debrisStats[ index ];
			//calculate the fragment position.
			//We could correct for angle translation, but the angles are random anyway.
			double newXPosition = this.getXPosition() + ( currentRadius * Math.cos(Math.toRadians(currentDebrisStats.projectileAngle) ) ) ;
			double newYPosition = this.getYPosition() + ( currentRadius * Math.sin(Math.toRadians(currentDebrisStats.projectileAngle) ) ) ;
			
			int angleFacing = currentDebrisStats.getAngleFacing();
			currentDebrisStats.rotateDebris();	//rotate here, since debris is not a true object.
			
			//paint the fragment
			drawDebrisFragment( g, newXPosition, newYPosition, angleFacing );
		} //end for loop to iterate through debris fragments
	} //end function paintToBoard
	
	private void drawDebrisFragment ( Graphics g, double xPosition, double yPosition, int angleFacing)
	{
		int offset = 10;	//width of debris.
		double rotationRequired = Math.toRadians(angleFacing);
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, offset, offset);
		AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
		
		g.drawImage(	op.filter( getDebrisSprite (), null ),
						((int)Math.round(xPosition) - offset),
						((int)Math.round(yPosition) - offset ),
						null );
	}
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		//Just need to kill the effect when it's done.
		spaceObjectList.remove(this);
	} //end function killObject
	
	/**
	 * Returns the type of object that created this explosion: player, or troll.
	 * @return 
	 */
	public ShipDebrisExplosion.DebrisExplosionOwner getOwner ()
	{
		return this.explosionOwner;
	}
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	private BufferedImage getDebrisSprite ()
	{
		//get the player or troll debris sprite, as needed.
		switch ( this.getOwner() )
		{
			case PLAYER:
				return this.playerShipDebris;
			case TROLL:
			default:
				return this.trollShipDebris;
		} 
	}//end function getDebrisSprite
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum DebrisExplosionOwner
	{
		PLAYER,
		TROLL;
	} //end DebrisExplosionOwner enum definition
	
	private class DebrisStats
	{
		int projectileAngle;
		int angleFacing;
		int rotationSpeed;
		
		DebrisStats ( )
		{
			this.projectileAngle = (int) Math.floor ( Math.random() * 360 );
			this.angleFacing = (int) Math.floor ( Math.random() * 360 );
			this.rotationSpeed = (int) Math.floor ( Math.random() * 7 ) - 5;
		} 
		
		public int getProjectileAngle()
		{
			return this.projectileAngle;
		} 
		public int getAngleFacing ()
		{
			return this.angleFacing;
		} //end function 
		public void rotateDebris()
		{
			this.angleFacing += this.rotationSpeed;
		} 
	} //end inner class DebrisStats definition
} //end ShipDebrisExplosion definition.
