

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gui.Image;

import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.TakesDamage;
import static com.rfhoodrdm.asterage2.state.Asterage2State.POINT_AWARDS.*;


public class Asteroid
extends SpaceObject
implements Expires, TakesDamage
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	Asteroid_Type_Size asteroidSize;		//what size is this asteroid?
	
	boolean expired;				//has the asteroid expired? E.g. it could have been destroyed.
	
	public static final double DAMAGE_RATING = 1.0;		//how much damage will this inflict on impact?
	
	public static final double BASE_ASTEROID_DURABILITY = PlasmaBolt.DAMAGE_RATING;		//base durability is taking 1 asteroid shot.
	private double currentDurability;
	
	public static final double SMALL_ASTEROID_MAX_SPEED = 15.0;
	public static final double MEDIUM_ASTEROID_MAX_SPEED = 8.0;
	public static final double LARGE_ASTEROID_MAX_SPEED = 3.0;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	/**
	 * Constructor, takes coordinate and asteroid size to spawn a new asteroid.
	 * @param passedxCoordinate
	 * @param passedyCoordinate
	 * @param whatSize 
	 */
	public Asteroid ( double passedxCoordinate, double passedyCoordinate, Asteroid_Type_Size whatSize )
	{
		super ( passedxCoordinate, passedyCoordinate, getAsteroidRadiusBySize(whatSize) );
		
		//remember the size
		this.asteroidSize = whatSize;
		
		//starting velocity is random value between half and full value of max velocity.
		//movement angle is random chance between 0 and 360.
		randomizeStartingVelocityAndDirection();		
		
		//chance of asteroid rotating. Not so much as to be a distraction, though.
		int chanceOfRotation = (int) Math.floor ( Math.random() * 100 );
		if ( chanceOfRotation < 50 )
		{
			//give a random rotation from -3 to 3?
			int rotationRange = 3;
			int rotationValue = (int) Math.floor (( Math.random() * 2 * rotationRange ) - rotationRange );
			setRotationalVelocity(rotationValue);
		} //end if check for a random rotation 
		
		//asteroids should spawn facing random directions.
		int randomFacingAngle = (int) Math.floor ( Math.random() * 360 );
		setFacingAngleDegrees(randomFacingAngle);
		
		currentDurability = calculateMaxDurability();
	} //end constructor
	
	/**
	 * Alterative constructor, takes a parent object and sets location from it.
	 * @param parentObject
	 * @param whatSize 
	 */
	public Asteroid ( SpaceObject parentObject, Asteroid_Type_Size whatSize )
	{
		this (	( null != parentObject ) ? parentObject.getxCoordinate() : 0 , 
				( null != parentObject ) ? parentObject.getyCoordinate() : 0, 
				whatSize );	
	} //end constructor with parent.
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public Asteroid_Type_Size getAsteroidSize() { return this.asteroidSize; }
	
	/**
	 * Max asteroid velocity depends on size.
	 * @return 
	 */
	@Override
	protected double getMaxVelocity()
	{
		switch ( asteroidSize )
		{
			case LARGE_WHITE:	
			case LARGE_TAN:
			case LARGE_RED:
			case LARGE_PURPLE:
				return LARGE_ASTEROID_MAX_SPEED;		
			
			case MEDIUM_WHITE: 
			case MEDIUM_TAN:
			case MEDIUM_RED:
			case MEDIUM_PURPLE:
				return MEDIUM_ASTEROID_MAX_SPEED;		
			
			
			case SMALL_WHITE: 
			case SMALL_TAN:
			case SMALL_RED:
			case SMALL_PURPLE:
				return SMALL_ASTEROID_MAX_SPEED;		
			
			default:
				 return 0.0;
		} //end switch based on asteroid size
	} //end method getMaxVelocity
	
	public boolean checkExpired() { return this.expired; }
	public void setExpiredFlag( boolean newFlag ) { this.expired = newFlag; }
	@Override	protected boolean checkAffectedByGravityNet() { return true; }	 //Asteroids are ALWAYS affected by gravity net
	
	@Override
	public void takeDamage(double damageAmount)
	{
		//deduct the damage from the asteroid's current durability. 
		//if this takes the asteroid to 0 or less, then the asteroid expires.
		currentDurability -= damageAmount;
		if ( 0.0 >= currentDurability ) { setExpiredFlag(true); }
	} //end method takeDamage
	
	public long getPointValue()
	{
		switch ( asteroidSize )
		{
			case LARGE_WHITE:	
			case LARGE_TAN:
			case LARGE_RED:
			case LARGE_PURPLE:
				return LARGE_ASTEROID_HIT.getPointAward();
			
			case MEDIUM_WHITE: 
			case MEDIUM_TAN:
			case MEDIUM_RED:
			case MEDIUM_PURPLE:
				return MEDIUM_ASTEROID_HIT.getPointAward();
			
			case SMALL_WHITE: 
			case SMALL_TAN:
			case SMALL_RED:
			case SMALL_PURPLE:
				return SMALL_ASTEROID_HIT.getPointAward();
			
			default:
				return 0;	
		} //end switch based on asteroid size
		
	} //end method getPointValue
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private static int getAsteroidRadiusBySize( Asteroid_Type_Size whatSize )
	{
		switch ( whatSize )
		{
			case LARGE_WHITE:	
			case LARGE_TAN:
			case LARGE_RED:
			case LARGE_PURPLE:
				return 50;		//half of 100
			
			case MEDIUM_WHITE: 
			case MEDIUM_TAN:
			case MEDIUM_RED:
			case MEDIUM_PURPLE:
				return 33;		//half of 66
			
			case SMALL_WHITE: 
			case SMALL_TAN:
			case SMALL_RED:
			case SMALL_PURPLE:
				return 17;		//half of 33
			
			default:
				throw new IllegalArgumentException ("Asteroid does not conform to known sizes. Cannot determine radius.");
		} //end switch based on asteroid size
	} //end method getAsteroidRadiusBySize

	@Override
	protected BufferedImage getSimpleSprite()
	{
		Asteroid_Type_Size mySize = getAsteroidSize();
		switch ( mySize )
		{
			case SMALL_WHITE:		return Image.A2_ASTEROID_SMALL_WHITE.getImage();
			case MEDIUM_WHITE:		return Image.A2_ASTEROID_MEDIUM_WHITE.getImage(); 
			case LARGE_WHITE:		return Image.A2_ASTEROID_LARGE_WHITE.getImage();
				
			case SMALL_TAN:			return Image.A2_ASTEROID_SMALL_TAN.getImage();
			case MEDIUM_TAN:		return Image.A2_ASTEROID_MEDIUM_TAN.getImage();
			case LARGE_TAN:			return Image.A2_ASTEROID_LARGE_TAN.getImage();
				
			case SMALL_RED:			return Image.A2_ASTEROID_SMALL_RED.getImage();
			case MEDIUM_RED:		return Image.A2_ASTEROID_MEDIUM_RED.getImage();
			case LARGE_RED:			return Image.A2_ASTEROID_LARGE_RED.getImage();
				
			case SMALL_PURPLE:		return Image.A2_ASTEROID_SMALL_PURPLE.getImage();
			case MEDIUM_PURPLE:		return Image.A2_ASTEROID_MEDIUM_PURPLE.getImage();
			case LARGE_PURPLE:		return Image.A2_ASTEROID_LARGE_PURPLE.getImage();
				
			default:
				//shouldn't come here, but handle any other case by returning Null.
				return null;
		} //end switch based on asteroid size
	} //end method getSimpleSprite

	private double calculateMaxDurability() 
	{ 
		Asteroid_Type_Size mySize = getAsteroidSize();
		switch ( mySize )
		{
			case SMALL_PURPLE:
			case MEDIUM_PURPLE:
			case LARGE_PURPLE:
				return 8 * BASE_ASTEROID_DURABILITY;	//Purple asteroids have 8x base durability.
			
			case SMALL_RED:			
			case MEDIUM_RED:		
			case LARGE_RED:
				return 4 * BASE_ASTEROID_DURABILITY;	//Red asteroids have 4x base durability.
			
			case SMALL_TAN:			
			case MEDIUM_TAN:		
			case LARGE_TAN:			
				return 2 * BASE_ASTEROID_DURABILITY;	//Tan asteroids have 2x base durability.
				
			case SMALL_WHITE:		
			case MEDIUM_WHITE:		
			case LARGE_WHITE:
			default:
				return BASE_ASTEROID_DURABILITY;		//White asteroids have base durability.
		} //end switch based on asteroid type	
	}  //end method calculateMaxDurability

	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum Asteroid_Type_Size
	{
		SMALL_WHITE(1),
		MEDIUM_WHITE(2),
		LARGE_WHITE(4),
		
		SMALL_TAN(2),
		MEDIUM_TAN(4),
		LARGE_TAN(8),
		
		SMALL_RED(4),
		MEDIUM_RED(8),
		LARGE_RED(16),
		
		SMALL_PURPLE(8),
		MEDIUM_PURPLE(16),
		LARGE_PURPLE(32);
		
		int asteroidGenerationPointValue;
		
		Asteroid_Type_Size( int passedValue )
		{
			this.asteroidGenerationPointValue = passedValue;
		} //end constructor
		
		public int generationPointValue() { return this.asteroidGenerationPointValue; }
	} //end enum asteroid_size definition
} //end class Asteroid definition
