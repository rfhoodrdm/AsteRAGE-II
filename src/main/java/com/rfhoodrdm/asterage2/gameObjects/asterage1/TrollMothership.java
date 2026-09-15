package gameObjects.asterage1;
import gameEffects.asterage1.ShieldRing;
import gameEffects.asterage1.ShipDebrisExplosion;
import gameEffects.asterage1.TrollLaser;
import gui.GUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;
import objectBehaviors.asterage1.FiresBullets;
import objectBehaviors.asterage1.DeploysShields;
import objectBehaviors.asterage1.FiresSuperLaser;
import objectBehaviors.asterage1.FiresTractorBeam;
import utility.DebugManager;
import utility.GameConstants;

/**
 *
 */
public class TrollMothership
extends SpaceObject
implements FiresBullets, DeploysShields, FiresSuperLaser, FiresTractorBeam
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	BufferedImage mothershipSprite;
	int currentBulletCoolDown;
	int maxBulletCoolDown;
	int minBulletCoolDown;
	double currentShields;
	double maxShields;
	double shieldRegenerationRate;
	ShieldRing shieldRing = null;
	PlayerShip playerShip;			//refernece to player's ship.
	TrollPod[] trollPod;			//set of accompanying troll pods.
	
	int currentLaserCooldown;															//current cooldown state of the laser weapon
	public final static int maxLaserCooldown = GameConstants.FRAMES_PER_SECOND * 10;		//cooldown required between firing.
	int currentTractorCooldown;															//current cooldown of the tractor beam.
	public final int maxTractorCooldown = GameConstants.FRAMES_PER_SECOND * 20;			//cooldown required between firing.
	
	SpaceObject tractorTarget;															//what is the tractor beam firing at?
	TRACTOR_BEAM_STATUS tractorBeamStatus;												//current status, on or off.
	double currentTractorRange;															//how far out is the tractor beam reaching?
	double tractorReachIncrement = 3.0;													//how fast does it reach out. 
	
	double tractorSpacerMaxOffset = 15;
	double tractorSpacerStartOffset;
	double tractorWaveSpeed = 2.0;
	
	int currentTractorColor;
	int startingTractorColor;
	Color[] tractorColorArray = new Color[6];
	
	public static final int superLaserLevelAcquired = 9;
	public static final int tractorBeamLevelAcquired = 10;
	public static final double chanceForSpawnPerLevel = 0.001;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TrollMothership( double passedXPosition, double passedYPosition, PlayerShip passedPlayerShip)
	{
		//call the super class constructor, SpaceObject.
		super ( passedXPosition, passedYPosition, 
				150, 42,
				0, 2,
				5.0, 10.0, 1.0);
		
		//set local variables.
		this.mothershipSprite = GUI.Image.TROLL_MOTHERSHIP.getImage();
		this.maxBulletCoolDown = GameConstants.FRAMES_PER_SECOND * 4;		//start at one bullet every four seconds.
		this.minBulletCoolDown = 
				(int) Math.floor (GameConstants.FRAMES_PER_SECOND * 1 );	
		this.maxShields = 600.0;
		this.currentShields = 600.0;
		this.shieldRegenerationRate = 0.5;
		this.playerShip = passedPlayerShip;
		this.tractorTarget = null;
		this.tractorBeamStatus = TRACTOR_BEAM_STATUS.OFF;
		this.currentTractorRange = 0.0;
		tractorSpacerStartOffset = tractorSpacerMaxOffset;
		
		currentTractorColor = 0;
		startingTractorColor = 0;
		tractorColorArray[0] = new Color( 255, 255, 255 );
		tractorColorArray[1] = new Color( 150, 150, 215 );
		tractorColorArray[2] = new Color( 100, 100, 175 );
		tractorColorArray[3] = new Color ( 50, 50, 135 );
		tractorColorArray[4] = new Color ( 25, 25, 95 );
		tractorColorArray[5] = new Color ( 0, 0, 55 );
		
		trollPod = new TrollPod[5];											//make new troll pod array. Waste a space for readability.
		
		this.restartBulletCoolDown();										//don't come in firing.
		this.restartLaserCooldown();
		this.restartTractorCooldown();
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public BufferedImage selectSprite ()
	{
		return this.mothershipSprite;
	} //end function selectSprite
	
	@Override
	public void paintToBoard ( Graphics g )
	{
		//invoke the super object's paint method.
		super.paintToBoard ( g );
	
		Graphics2D g2d = (Graphics2D) g;
				
		//If the mothership is firing a tractor beam, we must paint that to the board.
		if ( getTractorStatus() == TRACTOR_BEAM_STATUS.TARGETING )
		{
			//check for null target first.
			if ( null == this.tractorTarget )
			{
				DebugManager.logMessage(3, "Tractor beam engaged, but with no target.");
				return;
			} 
			
			//If we haven't reached all the way to the target yet, then calculate at which coordinates
			//the beam will reach, given its current length and the angle to the target.
			double dx = this.tractorTarget.getXPosition() - this.getXPosition();
			double dy = this.tractorTarget.getYPosition() - this.getYPosition();
			//System.out.println("dx = " + dx + " dy = " + dy );
			
			double fireAngleRadians =  Math.atan2(dx, -1 * dy); 
			
			double reachX = this.currentTractorRange * Math.sin( fireAngleRadians);
			double reachY = -1 * this.currentTractorRange * Math.cos ( fireAngleRadians );
			
			double xTarget = this.getXPosition() + reachX;
			double yTarget = this.getYPosition() + reachY;
			
			drawTractorBeam( xTarget, yTarget, g2d );
			
		} //end if block for proccing a draw of a targetting tractor beam.
		
		if ( getTractorStatus() == TRACTOR_BEAM_STATUS.TRACTORING_TARGET )
		{
			//check for null target first.
			if ( null == this.tractorTarget )
			{
				DebugManager.logMessage(3, "Tractor beam engaged, but with no target.");
				return;
			} 
			
			//If the tractor beam has locked on, then simply copy out the x and y coordinates of the target.
			double xTarget = this.tractorTarget.getXPosition();
			double yTarget = this.tractorTarget.getYPosition();
			
			drawTractorBeam( xTarget, yTarget, g2d );
		} //end if block for proccing a draw of a locked on tractor beam.
		
	} //end function paintToBoard definition
	
	
	
	/**
	 * Where should this troll pod be along the x axis?
	 * @param index
	 * @return 
	 */
	public double getPodXPosition ( int index )
	{
		//get the angle of the pod. Rotate the pods 90 degrees around the mothership.
		double angleOfPod = Math.toRadians ( this.getAngleFacing() + ( 90 * index ) );
		double xOffset = TrollPod.trollPodSatelliteDistance * Math.sin(angleOfPod );
		return this.getXPosition() + xOffset;
	} 
	/**
	 * Where should this troll pod be along the y axis?
	 * @param index
	 * @return 
	 */
	public double getPodYPosition( int index )
	{
		//get the angle of the pod. Rotate the pods 90 degrees around the mothership.
		double angleOfPod = Math.toRadians ( this.getAngleFacing() + ( 90 * index ) );
		double yOffset = TrollPod.trollPodSatelliteDistance * Math.cos(angleOfPod ) * -1;
		return this.getYPosition() + yOffset;
	} //end function getPodYPosition
	
	/**
	 * Mark the reference for the destroyed troll pod as null.
	 * @param destroyedPodIndex 
	 */
	public void informPodDestroyed ( int destroyedPodIndex )
	{
		trollPod[destroyedPodIndex] = null;
		DebugManager.logMessage(5, "Troll pod destroyed. Index: " + destroyedPodIndex );
	} //end function informPodDestroyed
	
	public void attachTrollPod ( TrollPod newPod, int indexOfPod )
	{
		trollPod[indexOfPod] = newPod;
		DebugManager.logMessage(5, "Attaching new pod at location: " + indexOfPod);
	} //end function attachTrollPod
	
	//Find an unused pod index.
	public int getUnusedPodIndex ( )
	{
		//go through the pods array
		for (int index = 1;		index <= 4;		++index)
		{
			//if we find a null reference, i.e. a pod was destroyed, then return that index.
			if ( null == trollPod[index] )
			{
				return index;
			} //end if check for a null pod reference
		} //end for loop iterating through troll pod array.
		
		//else we haven't found a pod.
		return 0;
	} //end function getUnusedPodIndex
	
	/*		********************		FiresBullets Interface			******************	*/
	/*      ******************************************************************************  */
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
		
		//Fire a spreadshot worth of bullets.
		int spreadshotAngle = 5;
		Bullet trollBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle, Bullet.BULLET_OWNER.TROLL );
		spaceObjectList.add ( trollBullet );
		trollBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle + spreadshotAngle, Bullet.BULLET_OWNER.TROLL );
		spaceObjectList.add ( trollBullet );
		trollBullet = new Bullet ( this.getXPosition(), this.getYPosition(), fireAngle - spreadshotAngle, Bullet.BULLET_OWNER.TROLL );
		spaceObjectList.add ( trollBullet );
		
		//reduce the mothership's max bullet cooldown a bit every time we fire
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
	/*      **********************************************************************************  */
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
			this.currentShields = 0.0;
			this.regenerateShields();
			
		} //end if block to check for dead ship, lol.
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
	
	/*		********************		FiresSuperLaser Interface			******************	*/
	/*      **********************************************************************************  */
	
	@Override
	public boolean checkLaserCooldown()
	{
		if ( 0 == this.currentLaserCooldown )
		{
			return true;
		} 
		//else
		return false;
	} //end function checkLaserCooldown
	
	/**
	 * Reduces the current cooldown of the super laser by 1 tick.
	 */
	@Override
	public void decrementLaserCooldown()
	{
		if ( this.currentLaserCooldown > 0 )
		{
			this.currentLaserCooldown -= 1;
		} 
	} //end function decrementLaserCooldown
	
	/**
	 * Sets the cooldown of the super laser to 0. E.g. fire now.
	 */
	@Override
	public void finishLaserCooldown()
	{
		this.currentLaserCooldown = 0;
	} //end function finishLaserCooldown
	
	/**
	 * Resets the laser cooldown to maximum.
	 */
	@Override
	public void restartLaserCooldown()
	{
		this.currentLaserCooldown = this.maxLaserCooldown;
	} //end function restartLaserCooldown
	
	/**
	 * Create a new laser effect and add it to the space object list. No need to aim, it is auto-tracking.
	 * @param playerShip
	 * @param spaceObjectList 
	 */
	@Override
	public void fireLaser ( SpaceObject target, ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		//Make the laser object and add it to the list.
		TrollLaser newTrollLaser = new TrollLaser( target, this );
		spaceObjectList.add( newTrollLaser );
	} //end function fireLaser
	
	
	/*		********************		FiresTractorBeam Interface			******************	*/
	/*      **********************************************************************************  */
	
	@Override
	public boolean checkTractorReadiness()
	{
		//If the cooldown is ready, and there is a missing pod, and the tractor beam is not currently firing, we can start.
		if (	(0 == currentTractorCooldown )	&& 
				( true == trollPodMissing() )	&&
				( TRACTOR_BEAM_STATUS.OFF == this.tractorBeamStatus )
			)
		{
			return true;
		} //end if block to check if the tractor beam is ready to fire.
		//else
		return false;
	}
	
	@Override
	public void decrementTractorCoolDown()
	{
		if ( this.currentTractorCooldown > 0 )
		{
			this.currentTractorCooldown -= 1;
		}
	}
	
	@Override
	public void finishTractorCoolDown()
	{
		this.currentTractorCooldown = 0;
	}
	
	@Override
	public void restartTractorCooldown()
	{
		this.currentTractorCooldown = this.maxTractorCooldown;
	}
	
	@Override
	public void fireTractor( SpaceObject passedTarget )
	{
		//Set the current target of the tractor beam to the one given, and set the tractor status to targeting.
		this.tractorTarget = passedTarget;
		this.tractorBeamStatus = TRACTOR_BEAM_STATUS.TARGETING;
	} //end function fireTractor
	
	@Override
	public void lockOnTractorBeam ( SpaceObject passedTarget )
	{
		//If the ship was the prior target, release it's tractor flag.
		releaseTractorFlagOnShipTarget( tractorTarget);
		
		//set the current target of the tractor beam to the one give, and set the status to tractoring target.
		this.tractorTarget = passedTarget;
		this.tractorBeamStatus = TRACTOR_BEAM_STATUS.TRACTORING_TARGET;
		
		//If the ship is the new target, set it's tractor flag.
		setTractorFlagOnShipTarget( tractorTarget);
		
	} //end function lockOnTractorBeam
	
	@Override
	public void disengageTractor()
	{
		//If the ship was the prior target, release it's tractor flag.
		releaseTractorFlagOnShipTarget( tractorTarget);
		
		//Set the target to null, the status to off, and the cooldown to max. Set the range of the tractor beam to 0.
		this.tractorTarget = null;
		this.tractorBeamStatus = TRACTOR_BEAM_STATUS.OFF;
		this.currentTractorRange = 0.0;
		this.restartTractorCooldown();	
	} //end function disengageTractor
	
	/**
	 * Set the player ship's being tractored flag to true, if that's what we're tractoring. Else ignore it.
	 * @param target 
	 */
	private void setTractorFlagOnShipTarget( SpaceObject target )
	{
		if ( target instanceof PlayerShip )
		{
			PlayerShip playerShip = (PlayerShip) target;
			playerShip.setShipBeingTractoredFlag(true);
		} 
	} //end function setTractorFlagOnShipTarget
	
	/**
	 * Set the player ship's being tractored flag to flase, if that's what we're tractoring. Else ignore it.
	 * @param target 
	 */
	private void releaseTractorFlagOnShipTarget ( SpaceObject target )
	{
		if ( target instanceof PlayerShip )
		{
			PlayerShip playerShip = (PlayerShip) target;
			playerShip.setShipBeingTractoredFlag(false);
		} 
	} //end function releaseTractorFlagOnShipTarget
	
	@Override
	public void incrementTractorRange( )
	{
		this.currentTractorRange += this.tractorReachIncrement;
	} //end function incrementTractorRange
	
	public TRACTOR_BEAM_STATUS getTractorStatus()
	{
		return this.tractorBeamStatus;
	} 
	
	@Override
	public SpaceObject getTractorTarget()
	{
		return this.tractorTarget;
	} //end function getTractorTarget
	
	@Override
	public double getCurrentTractorRange()
	{
		return this.currentTractorRange;
	} //end function getCurrentTractorRange
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override
	public void moveObject ()
	{
		double moveAdjustment = 1.5;
		
		double playerXPosition = playerShip.getXPosition();
		double playerYPosition = playerShip.getYPosition();
		double newXPosition = this.getXPosition();
		double newYPosition = this.getYPosition();
		
		//Move towards the player.
		//adjust x position.
		if ( this.getXPosition() > playerXPosition )
		{
			newXPosition -= moveAdjustment;
		} 
		else if ( this.getXPosition() < playerXPosition )
		{
			newXPosition += moveAdjustment;
		} 
		//adjust y position.
		if ( this.getYPosition() > playerYPosition )
		{
			newYPosition -= moveAdjustment;
		} 
		else if ( this.getYPosition() < playerYPosition )
		{
			newYPosition += moveAdjustment;
		} 
		
		//set the new position.
		this.setPosition(newXPosition, newYPosition);
		//rotate the object if it has a rotational speed
		this.angleFacing += this.rotationalSpeed;
		
	} //end function 
	
	
	/**
	 * Check and see if we need to refill an empty position for a troll pod.
	 * Check positions 1 to 4. We waste space 0 for readability.
	 * @return 
	 */
	private boolean trollPodMissing()
	{
		//go through the pods array
		for (int index = 1;		index <= 4;		++index)
		{
			//if we find a null reference, i.e. a pod was destroyed, then return true.
			if ( null == trollPod[index] )
			{
				return true;
			} //end if check for a null pod reference
		} //end for loop iterating through troll pod array.
		
		//else
		return false;
		
	} //end function trollPodMissing
	
	/**
	 * Draw the tractor beam, targeting the location specified.
	 * @param xTarget
	 * @param yTarget 
	 */
	private void drawTractorBeam ( double passedXTarget, double passedYTarget, Graphics2D g2d )
	{
		int xAttacker = (int) Math.floor(this.getXPosition() );
		int yAttacker = (int) Math.floor(this.getYPosition() );
		int xTarget = (int) Math.floor(passedXTarget );
		int yTarget = (int) Math.floor(passedYTarget);
		
		Color tractorColor = new Color ( 100, 0, 225 );
		g2d.setColor(tractorColor);

		g2d.setStroke( new BasicStroke ( (float) 7.0 ) );
		double dx = xTarget - xAttacker;
		double dy = yTarget - yAttacker;
		double distanceToTarget = Math.sqrt( Math.pow(dx, 2) + Math.pow(dy, 2) );
		double tractorAngleRadians =  Math.atan2(dx, -1 * dy);
		double tractorSpacing = 21.0;
		
		//When a wave of a particular color reaches the ship, advance through the color palette to make it
		//appear as though the waves are traveling through space. 
		//Also reset the spacer.
		this.tractorSpacerStartOffset -= this.tractorWaveSpeed;
		if ( this.tractorSpacerStartOffset < 0 )
		{
			this.tractorSpacerStartOffset += this.tractorSpacerMaxOffset;
			this.startingTractorColor = (startingTractorColor + 1 ) % 6;
			
		}
		
		//now draw the waves.
		for ( int spacerIndex = 1;	((tractorSpacing * spacerIndex)+ tractorSpacerStartOffset) < distanceToTarget;	++ spacerIndex )
		{
			double spacerDX = ((tractorSpacing * spacerIndex) + tractorSpacerStartOffset) * Math.sin( tractorAngleRadians );
			double spacerDY = ((tractorSpacing * spacerIndex) + tractorSpacerStartOffset) * Math.cos( tractorAngleRadians ) * -1;
			int waveCenterX = (int) Math.floor (spacerDX + xAttacker);
			int waveCenterY = (int) Math.floor (spacerDY + yAttacker);
			int waveArcDegrees = (int) Math.floor(Math.toDegrees(tractorAngleRadians) );
			
			//Cycle through the color palette.
			currentTractorColor = ( spacerIndex + startingTractorColor) % 6;
			
			//half at start, full size at ship.
			double distanceRatio = ((tractorSpacing * spacerIndex)+ tractorSpacerStartOffset) / distanceToTarget;
			int waveSize = 38 + (int) Math.floor ( distanceRatio * 37 );	
			int halfWaveSize = waveSize / 2;
			
			g2d.setColor(tractorColorArray[currentTractorColor] );
			g2d.drawArc(	waveCenterX - halfWaveSize, 
							waveCenterY - halfWaveSize, 
							waveSize, 
							waveSize, 
							((90 - waveArcDegrees) % 360) -45, 
							90);
			
		} 
		
		g2d.setStroke( new BasicStroke ( (float) 1.0 ) );
		
	} //end function drawTractorBeam
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum TRACTOR_BEAM_STATUS
	{
		OFF,
		TARGETING,
		TRACTORING_TARGET;
	} //end enum TRACTOR_BEAM_STATUS definition
} //end class TrollMothership definition.
