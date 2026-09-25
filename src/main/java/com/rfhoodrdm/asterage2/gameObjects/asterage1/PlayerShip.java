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
 * PlayerShip is the component on the game board representing the player. 
 * It inherits from SpaceObject because it is an object belonging on the game board.
 */
public class PlayerShip
extends SpaceObject
implements FiresBullets, DeploysShields
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	BufferedImage shipSprite;
	BufferedImage destroyedShipSprite;
	BufferedImage damagedShipSprite;
	int currentBulletCoolDown;
	int maxBulletCooldown;
	boolean firingWeapons;
	double currentShields;
	double maxShields;
	public final static double shieldRegenerationRate = 0.05;
	SHIP_STATUS shipStatus;
	boolean shipBeingTractored;
	ShieldRing shieldRing = null;
	int currentTractorHullStress;
	int maximumTractorHullStress = (int) Math.floor (GameConstants.FRAMES_PER_SECOND * 2.0) ;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public PlayerShip ()
	{
		this ( GameConstants.GAME_BOARD_WIDTH/2, GameConstants.GAME_BOARD_HEIGHT/2, 0, 0.0 );			//constructor with center of screen as arguments.
	}
	
	public PlayerShip( double passedXPosition, double passedYPosition, int passedVelocityAngle, double passedVelocity)
	{
		//invoke superclass with initial arguments for SpaceObject initialization.
		super ( passedXPosition, passedYPosition,		//initial coordinates.
				75,										//diameter of ship
				passedVelocityAngle, 0, 0,				//velocity angle, initial facing angle, rotational velocity				
				passedVelocity, 20.0, 0.0 );			//starting velocity, max velocity, current acceleration rate.
				
		//set the local variables.
		this.currentBulletCoolDown = 0;
		this.maxBulletCooldown = GameConstants.FRAMES_PER_SECOND / 2;	//one bullet per half second.
		this.firingWeapons = false;
		
		this.maxShields = 100.0;
		this.currentShields = this.maxShields;
		
		this.shipStatus = SHIP_STATUS.ALIVE;
		this.shipBeingTractored = false;
		this.resetTractorHullStress();
		
		//load the ship's sprites
		loadSprites();
		
	} 

	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	//restore all ship attributes to newly spawned state.
	public void respawnShip() {
		this.shipStatus = SHIP_STATUS.ALIVE;
		this.currentShields = this.maxShields;
		this.shieldRing = null;
		this.setPosition( GameConstants.GAME_BOARD_WIDTH/2, GameConstants.GAME_BOARD_HEIGHT/2 );
		this.angleFacing = 0;
		this.velocityAngle = 0;
		this.velocity = 0.0;
		this.currentAcceleration = 0.0;
		this.shipBeingTractored = false;
		
	} 
	
	public void rotateClockWise() {
		//don't respond to controls if not alive.
		if (getShipStatus() != SHIP_STATUS.ALIVE) {
			return;
		}
		this.rotationalSpeed = 8;
	} 
	
	public void rotateCounterClockwise()
	{
		//don't respond to controls if not alive.
		if (getShipStatus() != SHIP_STATUS.ALIVE) {
			return;
		}
		this.rotationalSpeed = -8;
	} 

	public void stopRotating ()	{
		this.rotationalSpeed = 0;
	} 
	
	public void accelerate()	{
		//don't respond to controls if not alive.
		if (getShipStatus() != SHIP_STATUS.ALIVE)		{
			return;
		}
		this.currentAcceleration = 0.75;
	} 
	
	public void stopAccelerating ()	{
		this.currentAcceleration = 0.0;
	} 
	
	@Override
	public double getCurrentAcceleration ()	{
		//If the ship is being tractored
		if ( true == shipBeingTractored )
		{
			return -1.0;
		} 

		return super.getCurrentAcceleration();
	} 
	
	public void brake()	{
		//don't respond to controls if not alive.
		if (getShipStatus() != SHIP_STATUS.ALIVE)
		{
			return;
		}
		this.currentAcceleration = -0.04;
	} 

	public void stopBraking()
	{
		this.currentAcceleration = 0.0;
	} 
	
	public void startFiringWeapons (){
		//don't respond to controls if not alive.
		if (getShipStatus() != SHIP_STATUS.ALIVE)
		{
			return;
		}
		this.firingWeapons = true;
	} 
	
	
	public void stopFiringWeapons()	{
		this.firingWeapons = false;
	} 
	
	public SHIP_STATUS getShipStatus()	{
		return this.shipStatus;
	} 
	
	public void setShipStatus ( SHIP_STATUS passedStatus )	{
		this.shipStatus = passedStatus;
	} 
	
	public void setShipBeingTractoredFlag ( boolean newFlag )	{
		this.shipBeingTractored = newFlag;
	} 
	
	/*		********************		FiresBullets Interface			******************	*/
	@Override
	public void fireBullet ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList ){
		Bullet newBullet = new Bullet ( this.xPosition, this.yPosition, this.angleFacing, Bullet.BULLET_OWNER.PLAYER);
		spaceObjectList.add ( newBullet );
	
	} 
	
	@Override
	public boolean checkBulletCoolDown()	{
		return (0 == this.currentBulletCoolDown) && this.firingWeapons;
	} 
	
	@Override
	public void decrementBulletCoolDown() {
		if ( this.currentBulletCoolDown > 0 ) {
			this.currentBulletCoolDown -= 1;
		} 
	} 
	
	@Override
	public void finishBulletCoolDown()	{
		this.currentBulletCoolDown = 0;
	}
	
	@Override
	public void restartBulletCoolDown()
	{
		this.currentBulletCoolDown = this.maxBulletCooldown;
	} 
	
	/*		********************		DeploysShields Interface			******************	*/
	@Override
	public void regenerateShields()	{
		//dont regenerate shields if not alive.
		if ( getShipStatus() != SHIP_STATUS.ALIVE )	{
			return;
		} 
		
		this.currentShields += this.shieldRegenerationRate;
		if ( this.currentShields > this.maxShields )	{
			this.currentShields = this.maxShields;
		}
	}
	
	@Override
	public void damageShields ( double amountDamage, ConcurrentLinkedQueue<SpaceObject> spaceObjectList )	{
		this.currentShields -= amountDamage;
		if ( this.currentShields < 0)		{
			this.killObject(spaceObjectList);
			if ( this.hasShieldsAttached() )			{
				spaceObjectList.remove( this.shieldRing );
			} 	
		}
	}
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )	{
		//special case. Set the status of the ship to destroyed. The game will be able to cope with this.
		setShipStatus( SHIP_STATUS.DESTROYED );
		//play the ship destroyed sound?
		this.stopAccelerating();			//stop all gain of speed.
		this.stopFiringWeapons();			//stop firing if we are destroyed.
		if ( this.shieldRing != null )		{
			this.shieldRing.endLifespan();		//kill the shield effect, if there is one.
		}
		
		//create a new ship explosion effect.
		spaceObjectList.add ( new ShipDebrisExplosion ( this.getXPosition(), this.getYPosition(), 
								ShipDebrisExplosion.DebrisExplosionOwner.PLAYER) );
		
	} 
	
	@Override
	public double getCurrentShields (){
		return this.currentShields;
	} 
	
	@Override
	public double getMaxShields (){
		return this.maxShields;
	} 
	
	@Override
	public void detachShields (){
		this.shieldRing = null;
	} 
	
	@Override
	public void attachShields ( ShieldRing passedShieldRing ){
		this.shieldRing = passedShieldRing;
	} 
	
	@Override
	public boolean hasShieldsAttached ()	{
		return null != this.shieldRing;
	}
	
	@Override
	public void renewShieldEffect (){
		if ( null != this.shieldRing)	{
			this.shieldRing.resetLifespan();
		} 
	} 
	
	/**
	 * Checks to see if the player ship has reached its maximum hull stress-- player pod appears then.
	 */
	public boolean checkTractorHullStressLimit ()	{
		return 0 == this.currentTractorHullStress;
	} 
	
	/**
	 * Reset tractor hull stress to maximum
	 */
	public void resetTractorHullStress() {
		this.currentTractorHullStress = this.maximumTractorHullStress;
	} 
	
	/**
	 * Decrement the tractor hull stress by 1 
	 */
	public void decrementTractorHullStress () {
		if ( this.currentTractorHullStress > 0 ) {
			this.currentTractorHullStress -= 1;
		} 
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Fetches all of the relevant images needed to draw the ship to the game board.
	 */
	protected void loadSprites()	{
		this.shipSprite = Image.SHIP_SPRITE.getImage();
		this.damagedShipSprite = Image.DAMAGED_SHIP_SPRITE.getImage();
		this.destroyedShipSprite = Image.NO_IMAGE.getImage();
	} 
	
	/**
	 * Selects which image to draw to the screen, given the ship's current state.
	 * @return BufferedImage to be drawn to the screen, to represent the ship.
	 */
	@Override
	protected BufferedImage selectSprite ()	{
		//return the sprite based on the ship condition.
		switch ( this.shipStatus )		{
			case ALIVE:
				return this.shipSprite;
				
			case ANNIHILATED:
				return this.damagedShipSprite;
				
			case DESTROYED:
			default:
				return this.destroyedShipSprite;
		} 
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum SHIP_STATUS
	{
		ALIVE,
		DESTROYED,
		ANNIHILATED;
	} 		
}
