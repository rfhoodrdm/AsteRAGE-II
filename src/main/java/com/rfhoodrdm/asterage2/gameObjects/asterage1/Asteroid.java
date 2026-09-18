package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.GetsDestroyed;
import com.rfhoodrdm.asterage2.state.Asterage1ScoreState;

/**
 * Represents either a small, medium, or large asteroid object.
 * @author roberthood
 */



@Slf4j
public class Asteroid
extends SpaceObject
implements GetsDestroyed
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public final static double asteroidDamage = 1.5;
	ASTEROID_SIZE asteroidSize;
	BufferedImage smallAsteroidSprite;
	BufferedImage mediumAsteroidSprite;
	BufferedImage largeAsteroidSprite;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Asteroid( double passedXPosition, double passedYPosition, ASTEROID_SIZE whatSize )
	{
		//call the super class constructor, SpaceObject, with some arguments supplied according to asteroid size.
		super ( passedXPosition, passedYPosition, 
				whatSize.diameter(), whatSize.randomVelocityAngle(),
				0, whatSize.randomRotation(),
				whatSize.randomSpeed(), 25.0, 0.0);
				
		//set the local variables.
		this.asteroidSize = whatSize;
		loadSprites();
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override
	protected BufferedImage selectSprite ()
	{
		switch (this.asteroidSize)
		{
			case SMALL:
				return smallAsteroidSprite;
			case MEDIUM:
				return mediumAsteroidSprite;
			case LARGE:
			default:
				return largeAsteroidSprite;
		} //end switch 
	} //end function selectSprite
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList, Asterage1ScoreState scoreState )
	{
		//Store some attributes about the current asteroid, such as position and size.
		//We may need these to spawn new asteroids.
		double oldXPosition = this.xPosition;
		double oldYPosition = this.yPosition;
		ASTEROID_SIZE oldSize = this.asteroidSize;
		
		//remove the old asteroid from the game list.
		spaceObjectList.remove(this);
		
		//Now, decide if we should make some new asteroids.
		int numberNewMedium = 0;
		int numberNewSmall = 0;
		switch ( this.asteroidSize)
		{
			case LARGE:
				// 2-3 new medium asteroids, Chance of 0-2 small.
				numberNewMedium = ((int) Math.floor( Math.random() * 2)) + 2;
				numberNewSmall = ((int) Math.floor( Math.random() * 5)) - 2;
				if ( numberNewSmall < 0)
				{
					numberNewSmall = 0;
				} 
				break;
			case MEDIUM:
				numberNewSmall = ((int) Math.floor( Math.random() * 2)) + 2;
				break;
			case SMALL:
			default:
				//do nothing additional.	
				break;
		} //end switch
		
		log.trace("Make {} medium and {} new small asteroids.", numberNewMedium, numberNewSmall);
		//Now, spawn the indicated number of asteroids, of the given type.
		for ( int counter = 1; counter <= numberNewMedium; ++ counter )
		{
			spaceObjectList.add ( new Asteroid( oldXPosition, oldYPosition, ASTEROID_SIZE.MEDIUM ) );
		} //end for loop to make medium asteroids.
		for ( int counter = 1; counter <= numberNewSmall; ++ counter )
		{
			spaceObjectList.add ( new Asteroid( oldXPosition, oldYPosition, ASTEROID_SIZE.SMALL ) );
		} //end for loop to make small asteroids.
		
	} //end function killObject
	
	public ASTEROID_SIZE getSize()
	{
		return this.asteroidSize;
	} //end function getSize

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	private void loadSprites()
	{
		smallAsteroidSprite = Image.SMALL_ASTEROID.getImage();
		mediumAsteroidSprite = Image.MEDIUM_ASTEROID.getImage();
		largeAsteroidSprite = Image.LARGE_ASTEROID.getImage();
	} //end function loadSprites
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum ASTEROID_SIZE
	{
		SMALL,
		MEDIUM,
		LARGE;
		
		//The speed of an asteroid, given its size.
		public double randomSpeed ()
		{
			switch (this)
			{
				case SMALL:
					return Math.random() * 15.0;
				case MEDIUM:
					return Math.random() * 10.0;
				case LARGE:
				default:
					return Math.random() * 5.0;
			} //end switch
		} //end function maxSpeed
		
		// The rotation of the asteroid, if any
		public int randomRotation()
		{
			if ( Math.random() > .4 )
			{
				return 0;
			}
			
			switch (this)
			{
				case SMALL:
					return (int) Math.floor( Math.random() * 11 ) -5;
				case MEDIUM:
					return (int) Math.floor( Math.random() * 5 ) -2;
				case LARGE:
				default:
					return (int) Math.floor( Math.random() * 3 ) -1;
			} //end switch
		} //end function rotationRange
		
		public int pointValue()
		{
			return 50;
		} //end function pointValue
		
		public int randomVelocityAngle()
		{
			return (int) Math.floor ( Math.random() * 360);
		} //end function randomVelocityAngle
		
		public int diameter()
		{
			switch ( this )
			{
				case SMALL:
					return 34;
				case MEDIUM:
					return 66;
				case LARGE:
				default:
					return 100;
			} //end switch 
		} //end function size
	} //end enum ASTEROID SIZE definition
} //end class asteroid definition
