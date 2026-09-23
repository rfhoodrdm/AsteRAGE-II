package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import com.rfhoodrdm.asterage2.gui.Image;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.FiresBullets;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.DeploysShields;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShieldRing;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShipDebrisExplosion;

/**
 * The troll scout is an independently navigating foe of the player.
 * It flies around the screen and attempts to destroy the player by firing bullets.
 */
public class TrollScout
extends SpaceObject
implements FiresBullets, DeploysShields
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	BufferedImage trollSprite;
	int currentBulletCoolDown;
	int maxBulletCoolDown;
	double currentShields;
	double maxShields;
	double shieldRegenerationRate;
	ShieldRing shieldRing = null;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TrollScout ( double passedXPosition, double passedYPosition)
	{
		//call the super class constructor, SpaceObject.
		super ( passedXPosition, passedYPosition, 
				75, 0,
				0, 5,
				5.0, 15.0, 0.0);
		
		//set the local variables.
		this.maxBulletCoolDown = GameConstants.FRAMES_PER_SECOND * 2;	//one bullet every two seconds.
		this.currentBulletCoolDown = maxBulletCoolDown;					//don't come in firing.
		this.trollSprite = Image.TROLL_SCOUT.getImage();
		
		this.maxShields = 100.0;
		this.currentShields = 100.0;
		this.shieldRegenerationRate = 0.2;
		
		//start off in a random direction.
		plotNewCourse( 1 );
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override
	protected BufferedImage selectSprite()
	{
		return this.trollSprite;
	} //end function selectSprite
	
	/**
	 * initiate a change in direction for this troll scout.
	 * 
	 */
	public void plotNewCourse( int gameLevel )
	{
		//can plot a curved course 25% of the time, if the game status is of sufficient level.
		if ( (gameLevel >= 6 ) && ( Math.random() < 0.25 ) )
		{
			newRandomCurvedCourse();
		}
		else
		{
			newRandomStraightDirection();
		}
		
	} //end function plotNewCourse
	
	
	/*		********************		FiresBullets Interface			******************	*/
	@Override
	public void fireBullet ( ConcurrentLinkedQueue <SpaceObject> spaceObjectList )
	{
		//Find the player ship and get its current position, angle of direction, and velocity magnitude.
		double playerXPosition = 0.0;
		double playerYPosition = 0.0;
		int playerVelocityAngle = 0;
		double playerVelocity = 0.0;
		for ( SpaceObject currentObject: spaceObjectList )
		{
			if ( currentObject instanceof PlayerShip)
			{
				//found it.
				playerXPosition = currentObject.getXPosition();
				playerYPosition = currentObject.getYPosition();
				playerVelocityAngle = currentObject.getVelocityAngle();
				playerVelocity = currentObject.getVelocity();
				break;
			}  //end if block to gather stats.
		} //end for loop to iterate through objects and find the ship

		//Now, iterate through possible collision points.
		//Calculate where the player ship will be after each tick, and see if a bullet fired
		//at that position would collide with it.
		int fireAngle = 0;
		for ( int deltaTime = 1;	deltaTime <= Bullet.bulletMaxLifespan;		++deltaTime )
		{
			//calculate the new ship position, taking into account coordinate translation.				
			PlayerShip fakePlayerShip = new PlayerShip ( playerXPosition , playerYPosition, playerVelocityAngle, playerVelocity );
			for ( int counter = 1; counter <= deltaTime; ++ counter )
			{
				fakePlayerShip.moveObject();		//move once for every deltatime segment that has elapsed.
			}
			
			//calculate the angle from the origin point to the projected 
			double dx = fakePlayerShip.getXPosition() - this.getXPosition();
			double dy = fakePlayerShip.getYPosition() - this.getYPosition();
			//System.out.println("dx = " + dx + " dy = " + dy );
			
			fireAngle = (int) Math.floor( Math.toDegrees( Math.atan2(dx, -1 * dy))); 
			//System.out.println("Fireangle = " + fireAngle + " AdjustedFireAngle = " + fireAngle );
			
			//create a fake bullet.
			//calculate where the bullet would be after that amount of time.
			Bullet fakeBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle, Bullet.BULLET_OWNER.TROLL );
			for ( int counter = 1; counter <= deltaTime; ++ counter )
			{
				fakeBullet.moveObject();		//move once for every deltatime segment that has elapsed.
			}
			
			//see if our projected bullet hits the projected ship position.
			if ( true == fakePlayerShip.checkForCollision(fakeBullet) )
			{
				//we've found our intercept point.
				break;
			}

			//if we don't have the angle this time, loop around, unless of course we're at the last iteration.
			//in which case fire at the last calculated angle. ( Or at random, if we want to.) 
			
		} //end loop to iterate through game frames, looking for the first successful collision.
		
		//Fire the bullet!
		Bullet trollBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle, Bullet.BULLET_OWNER.TROLL );
		spaceObjectList.add ( trollBullet );
		
	} //end function FireBullets.
	
	
	@Override
	public void restartBulletCoolDown()
	{
		this.currentBulletCoolDown = this.maxBulletCoolDown;
	} //end function restartBulletCooldown.
	
	@Override
	public void finishBulletCoolDown()
	{
		this.currentBulletCoolDown = 0;
	} //end function finishBulletCooldown
	
	@Override
	public void decrementBulletCoolDown()
	{
		if ( this.currentBulletCoolDown > 0 )
		{
			this.currentBulletCoolDown -= 1;
		} 
	}//end function decrementBulletCoolDown
	
	@Override
	public boolean checkBulletCoolDown()
	{
		if ( 0 == this.currentBulletCoolDown )
		{
			return true;
		} 
		//else
		return false;
	}//end function checkBulletCoolDown
	
	/*		********************		DeploysShields Interface			******************	*/
	@Override
	public void regenerateShields()
	{
		this.currentShields += this.shieldRegenerationRate;
		if ( this.currentShields > this.maxShields )
		{
			this.currentShields = this.maxShields;
		} //end if block to check for over the shield limit
	} //end function regenerateShields
	
	@Override
	public void damageShields ( double amountDamage, ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		this.currentShields -= amountDamage;
		if ( this.currentShields < 0)
		{
			this.killObject(spaceObjectList);
			if ( this.hasShieldsAttached() )
			{
				spaceObjectList.remove( this.shieldRing );
			} //end if to get rid of shield object.
			
		} //end if block to check for dead ship.
	} //end function damageShields
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		//create a new ship explosion effect.
		spaceObjectList.add ( new ShipDebrisExplosion ( this.getXPosition(), this.getYPosition(), 
								ShipDebrisExplosion.DebrisExplosionOwner.TROLL) );
		//remove this object from the list.
		spaceObjectList.remove(this);
	} //end function killObject
	
	@Override
	public double getCurrentShields ()
	{
		return this.currentShields;
	} //end function getShields
	
	@Override
	public double getMaxShields ()
	{
		return this.maxShields;
	} //end function getMaxShields
	
	@Override
	public void detachShields ()
	{
		this.shieldRing = null;
	} //end function detachShields
	
	@Override
	public void attachShields ( ShieldRing passedShieldRing )
	{
		this.shieldRing = passedShieldRing;
	} //end function attachshields
	
	@Override
	public boolean hasShieldsAttached ()
	{
		if ( null == this.shieldRing )
		{
			return false;
		} //end if
		
		//else
		return true;
	} //end function hasShieldsAttached
	
	@Override
	public void renewShieldEffect ()
	{
		if ( null != this.shieldRing)
		{
			this.shieldRing.resetLifespan();
		} //end if to check for null reference
	} //end function renewShieldEffect
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Helper function that fires a bullet in a random direction, if no other preference can be found.
	 * @param spaceObjectList 
	 */
	private void fireRandomBullet ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		int angle = (int) Math.floor ( Math.random() * 360 );
		
		Bullet bullet = new Bullet (	this.getXPosition(),
										this.getYPosition(),
										angle,
										Bullet.BULLET_OWNER.TROLL ) ;
		spaceObjectList.add( bullet );
	} //end function fireRandomBullet
	
	/**
	 * New direction in a random angle, no curves.
	 */
	private void newRandomStraightDirection()
	{
		this.velocityAngle = (int) Math.floor (Math.random() * 360);
		this.currentAcceleration = 0.0;
		this.velocity = (this.maxVelocity * 0.5) + (this.maxVelocity * 0.5 * Math.random() );
	} //end function newRandomStraightDirection
	
	/**
	 * New curved course.
	 */
	private void newRandomCurvedCourse()
	{
		this.velocityAngle = (int) Math.floor (Math.random() * 360);
		this.currentAcceleration = 2.5;
	} //end function newRandomCurvedCourse
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end TrollScout class definition
