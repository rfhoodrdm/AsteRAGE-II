package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShieldRing;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShipDebrisExplosion;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.DeploysShields;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.FiresBullets;

/**
 *
 */
public class TrollPod
extends SpaceObject
implements FiresBullets, DeploysShields
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final int trollPodSatelliteDistance = 100;
	
	BufferedImage trollPodSprite;
	int index;							//what number pod is this?
	TrollMothership trollMothership;	//mothership to which this pod is attached.
	
	int currentBulletCoolDown;
	int maxBulletCoolDown;
	int minBulletCoolDown;
	double currentShields;
	double maxShields;
	double shieldRegenerationRate;
	ShieldRing shieldRing = null;
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TrollPod ( TrollMothership passedMothership, int passedIndex )
	{
		super ( passedMothership.getPodXPosition(passedIndex), passedMothership.getPodYPosition(passedIndex),
				75, 0, 0,
				-1 * passedMothership.getRotationSpeed(),
				0.0, 10.0, 0.0 );
		
		//set the local references.
		this.trollMothership = passedMothership;
		this.index = passedIndex;
		this.trollPodSprite = Image.TROLL_POD.getImage();
		
		this.maxBulletCoolDown = GameConstants.FRAMES_PER_SECOND * 8;							//start at one bullet every eight seconds.
		this.minBulletCoolDown = 
				(int) Math.floor (GameConstants.FRAMES_PER_SECOND * 1 );						//minimum 1 second.
		this.currentBulletCoolDown = (-1 + (2 * index ) ) * GameConstants.FRAMES_PER_SECOND;	//don't come in firing.
		
		this.maxShields = 100.0;
		this.currentShields = 100.0;
		this.shieldRegenerationRate = 0.2;
	} //end function TrollPod
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override
	public BufferedImage selectSprite ()
	{
		return this.trollPodSprite;
	} //end function selectSprite
	
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
		
		Bullet trollBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle, Bullet.BULLET_OWNER.TROLL );
		spaceObjectList.add ( trollBullet );

		//reduce the pod's max bullet cooldown a bit every time we fire
		reduceBulletCoolDown();
		
	} //end function FireBullets.
	
	private void reduceBulletCoolDown ()
	{
		//decrement the max bullet cooldown a bit.
		this.maxBulletCoolDown -= 1;
		
		//don't go below the minimum threshold.
		if ( this.maxBulletCoolDown < this.minBulletCoolDown )
		{
			this.maxBulletCoolDown = this.minBulletCoolDown;
		} 
	} //end function reduceBulletCoolDown
	
	
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
		//remove this object from the list. Also tell the troll mothership that this pod is destroyed.
		spaceObjectList.remove(this);
		trollMothership.informPodDestroyed ( this.index );
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
	@Override
	public void moveObject()
	{
		//set the new position by querying the mothership about the new expected position.
		this.setPosition( trollMothership.getPodXPosition(index), trollMothership.getPodYPosition(index) );
		//rotate the object if it has a rotational speed
		this.angleFacing += this.rotationalSpeed;
		this.checkAndCorrectAngleFacing();											//no int overflow.
	} //end function moveObject
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
