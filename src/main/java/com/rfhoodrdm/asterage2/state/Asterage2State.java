/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.state;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.controller.Asterage2Controller;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.ShipExplosionEffect;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.SpaceEffect;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.WarpOutEffect;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.HomingMissile;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlasmaBolt;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PowerUpBaseObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.SpaceObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollBaseShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollMiningPod;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollMothership;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollWeaponPod;
import com.rfhoodrdm.asterage2.gui.AsteRAGE2GameBoard;
import com.rfhoodrdm.asterage2.gui.GUI;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.ControlsWeaponsPods;
import com.rfhoodrdm.asterage2.sounds.SoundManager;

import static com.rfhoodrdm.asterage2.state.Asterage2State.POINT_AWARDS.EXTRA_POINT_PURCHASE;

import com.rfhoodrdm.asterage2.utility.DebugManager;

/**
 *
 * @author roberthood
 */
public class Asterage2State
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private HighScoreDirectory highScoreDirectory;
	private DataLoader dataLoader;
	
	//graphical display data members
	private int frameNumber = 0;			//what redraw frame are we on? Out of however many frames per second.
	
	
	//game stats data members
	private int gameLevel;					//what level is currently being played?
	private int remainingShips;				//how many ships does the player have left?
	private int extraLivesAwarded;			//how many extra lives have been awarded already?
	private long score;						//how many points has the player earned?
	private GameState gameState;			//what is the current state of the current game?
	private boolean requestToSpawnShipFlag;	//set to true when the player requests to spawn the ship.
	
	public static final long POINTS_REQUIRED_PER_EXTRA_LIFE = 20000;		//number of required points for each extra life.
	
	private int warpOutCountdown;			//countdown timer to end of warp-out sequence.
	public static final int MAX_WARP_OUT_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 5;	//5 seconds
	
	
	//information about various ship's systems and upgrade levels.
	public static final int MIN_SHIP_SYSTEM_LEVEL = 0;		//lower bound on ship system level
	public static final int MAX_SHIP_SYSTEM_LEVEL = 2;		//how many times may a system be upgraded.
	private int powerUpPoints;								//how many power up points have been collected.
	public static final int MAX_POWER_UP_POINTS = 5;		//how many points can the player possibly collect at once?
	public static final int MIN_POWER_UP_POINTS = 0;		//lowest possible number of points the player may have at once.
	private PowerUpMenuOption currentSelectedPowerUpMenuOption;		//which option from the menu is currently selected?
	
	private Multi_Shot_Direction multiShotDirection;			//which side of the ship is the multi shot fired on?
	
	
	//List of In-Play Game objects
	private PlayerShip playerShip;
	private ConcurrentLinkedQueue<Asteroid> asteroidList;			//list of asteroids in play
	private ConcurrentLinkedQueue<PlasmaBolt> plasmaBoltList;		//list of plasma bolts on the board
	private ConcurrentLinkedQueue<TrollBaseShip> trollShipList;		//list of enemy ships in play
	private ConcurrentLinkedQueue<PowerUpBaseObject> powerUpList;	//list of power ups available on the game board
	private ConcurrentLinkedQueue<SpaceEffect> spaceEffectList;		//list of space effects not attached to other objects.
	private ConcurrentLinkedQueue<HomingMissile> homingMissileList;	//list of homing missiles currently on the screen.
	
	private boolean popUpMessageBeingShown;													//is a message currently on the screen.
	private int popUpMessageExpiredCounter;													//countdown to expired message.
	public static int POP_UP_MESSAGE_MAX_EXPIRED_COUNTER = GameConstants.FRAMES_PER_SECOND;	//how long until message expires
	public static int POP_UP_MESSAGE_CLEAR_TIME = GameConstants.FRAMES_PER_SECOND / 3;		//at what point in the countdown is the message cleared?
	
	
	//troll state objects and constants.
	public static final int MAX_TROLL_SCOUT_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 30;	//one every 30 seconds.
	public static final int TROLL_SCOUT_COUNTDOWN_DEFERMENT_BONUS = GameConstants.FRAMES_PER_SECOND * 10;	//defer 10 seconds
	private int remainingTrollScoutCountdown;
	
	private int countSpecialItemsDroppedThisLevel;
	public static final int MAX_SPECIAL_ITEM_DROPS_PER_LEVEL = 2;
	
	private boolean spawnedTrollMothershipRecently = false;
	private boolean trollMothershipSpawnCountdownInProgress = false;
	public static final int TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 2;	//3 seconds worth
	private int currentTrollMothershipSpawnCountdown = TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN;
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Asterage2State ( DataLoader passedLoader )
	{
		dataLoader = passedLoader;
		highScoreDirectory = new HighScoreDirectory( HighScoreDirectory.GAME_IDENTIFIER.ASTERAGE2,
														dataLoader);
		initializeAsterage2State();
	}  //end constructor
	
	public void initializeAsterage2State()
	{
		//initialize the game state variables.
		setGameLevel(1);													//default game level is 1.
		setRemainingShips(3);												//starting number of lives 
		setExtraLivesAwarded(0);											//we have awarded 0 extra lives so far
		setScore(0l);														//starting score is 0 points.
		setGameState( GameState.RUNNING );									//start game as running.
		setRequestToSpawnShipFlag(false);									//no initial request to spawn the ship.
		multiShotDirection = Multi_Shot_Direction.LEFT;						//start on the left side. 
		currentSelectedPowerUpMenuOption = PowerUpMenuOption.fromInt(0);		//start at the leftmost option at the start.
		resetTrollScoutCountdown();											//set troll scout countdown to max.
		
		//initialize in-play game objects.
		double playerShipXLocation = AsteRAGE2GameBoard.boardWidth / 2;								//generated at center of game board.
		double playerShipYLocation = AsteRAGE2GameBoard.boardHeight / 2;							//generated at center of game board
		playerShip = PlayerShip.makeNewPlayerShip(playerShipXLocation, playerShipYLocation);		//make a new player ship object.
		
		asteroidList = new ConcurrentLinkedQueue<>();				//clear space for new asteroid list
		plasmaBoltList = new ConcurrentLinkedQueue<>();				//clear space for new list of plasma bolts in play
		trollShipList = new ConcurrentLinkedQueue<>();				//clear space for new list of troll ships
		powerUpList = new ConcurrentLinkedQueue<>();				//clear space for new list of powerup objects
		spaceEffectList = new ConcurrentLinkedQueue<>();			//clear space for new list of space effects.
		homingMissileList = new ConcurrentLinkedQueue<>();			//clear space for new homing missile list.
	
		countSpecialItemsDroppedThisLevel = 0;							//reset counter of special drops to 0.
		setSpawnedTrollMothershipRecently(false);						//set flag to show recent mothership spawn to false.
		trollMothershipSpawnCountdownInProgress = false;				//set countdown-in-progress flag to false
		currentTrollMothershipSpawnCountdown = 
				TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN;					//reset countdown to max.
	} //end method initializeAsterage2State

	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public int getGameLevel() { return this.gameLevel; }
	public void setGameLevel( int passedGameLevel ) { this.gameLevel = passedGameLevel; }
	
	public int getRemainingShips() { return this.remainingShips; }
	public void setRemainingShips( int passedRemainingShips ) { this.remainingShips = passedRemainingShips; }
	
	public long getScore() { return this.score;}
	public void setScore( long passedScore ) { this.score = passedScore; }
	
	public boolean checkRequestToSpawnShipFlag()	{	return requestToSpawnShipFlag;	}
	public void setRequestToSpawnShipFlag(boolean requestToSpawnShipFlag)	{	this.requestToSpawnShipFlag = requestToSpawnShipFlag;	}
	
	
	public GameState getGameState() { return this.gameState; }
	public void setGameState( GameState newGameState ) 
	{ 
		this.gameState = newGameState; 
		//if the state is WARPING OUT, then reset the warping out timer.
		if ( GameState.WARPING_OUT == newGameState )
		{
			resetWarpingOutCountDown();
		} //end if check for warping out state counter reset
	} //end method setGameState
	
	public int getRemainingTrollScoutCountdown() { return this.remainingTrollScoutCountdown; }
	public void setRemainingTrollScoutCountdown( int newCountDown ) { this.remainingTrollScoutCountdown = newCountDown; }
	public void resetTrollScoutCountdown() { this.remainingTrollScoutCountdown = MAX_TROLL_SCOUT_COUNTDOWN; }
	public void grantTrollScoutCountdownDeferment() { this.remainingTrollScoutCountdown += TROLL_SCOUT_COUNTDOWN_DEFERMENT_BONUS;}
	public boolean checkTimeToSpawnTrollScout() { return ( 0 == getRemainingTrollScoutCountdown() ); }
	public void decrementTrollScoutCountdown() 
	{
		//decrement by 1. Don't go below 0.
		remainingTrollScoutCountdown -= 1;
		if ( 0 > remainingTrollScoutCountdown ) { remainingTrollScoutCountdown = 0; }
	} //end method decrementTrollScoutCountdown
	
	
	public int getWarpingOutCountDown() { return this.warpOutCountdown; }
	private void resetWarpingOutCountDown() { warpOutCountdown = MAX_WARP_OUT_COUNTDOWN; }
	public void decrementWarpingOutCountDown() 
	{
		//decrement by 1, don't go below 0.
		warpOutCountdown = ( warpOutCountdown > 0 ) ?
				(warpOutCountdown - 1) : 0;				
	} //end method decrementWarpoutCownDown

	
	public int getRemainingShieldPercentage() 
	{
		if ( null == playerShip ) return 0;		//if no ship, show no shields.
		
		return playerShip.getRemainingShieldPercentage();
	} //end method getRemainingShieldPercentage
	
	public int getPowerUpPoints() { return this.powerUpPoints; } //return 5; }//debug code
	public void setPowerUpPoints( int passedPowerUpPoints ) 
	{
		//set the number of power up points.
		//If the argument is below the minimum, use the minimum instead. 
		//Likewise, if the argument is above the maximum, use the maximum instead.
		if ( passedPowerUpPoints < MIN_POWER_UP_POINTS )	{	powerUpPoints = MIN_POWER_UP_POINTS;	}
		else if ( passedPowerUpPoints > MAX_POWER_UP_POINTS )	{ powerUpPoints = MAX_POWER_UP_POINTS; }
		else { powerUpPoints = passedPowerUpPoints; }
	} //end method setPowerUpPoints
	
	
	public PowerUpMenuOption getCurrentSelectedPowerUpMenuOption() { return this.currentSelectedPowerUpMenuOption; }
	public void setCurrentSelectedPowerUpMenuOption ( PowerUpMenuOption passedOption ) 
	{ 
		if ( null != passedOption ) 
		{
			this.currentSelectedPowerUpMenuOption = passedOption;
		} //end if check for null case
	} //end method setCurrentSelectedPowerUpMenuOption
	
	/**
	 * Check the current level of the system, if applicable.
	 * @param whichSystem
	 * @return 
	 */
	public boolean checkSystemAtMaxLevel( PowerUpMenuOption whichSystem )
	{
		switch (whichSystem)
		{
			case DECELERATION:
				return ( getDecelerationLevel() == MAX_SHIP_SYSTEM_LEVEL );
				
			case SHIELD_GENERATOR:
				return ( getShieldGeneratorLevel() == MAX_SHIP_SYSTEM_LEVEL );
				
			case HOMING_MISSILE:
				return ( getHomingMissileLevel() == MAX_SHIP_SYSTEM_LEVEL );
				
			case EXTRA_POINTS:
				return false;	//extra points are never maxed out.
				
			case MULTISHOT:
				return ( getMultishotLevel() == MAX_SHIP_SYSTEM_LEVEL );
				
			default: 
				DebugManager.logMessage(3, "Cannot check max level of system: " + whichSystem + " is not recognized.");
				return false;
		} //end switch based on which system
	} //end method checkSystemAtMaxLevel
	
	/**
	 * Upgrade the system designated.
	 * @param whichSystem 
	 */
	public void upgradeSystem( PowerUpMenuOption whichSystem )
	{
		//check first to see if the system is at max level already, just to be safe.
		if ( true == checkSystemAtMaxLevel(whichSystem)) 
		{ 
			DebugManager.logMessage(3, "Cannot upgrade system: " + whichSystem + ". Already at max level.");
			return;
		} //end if check for system already at max level 
		
		int currentSystemLevel;		//what level is the system currently at?
		switch ( whichSystem )
		{
			case DECELERATION:
				currentSystemLevel = getDecelerationLevel();
				setDecelerationLevel( currentSystemLevel + 1);
				break;
				
			case SHIELD_GENERATOR:
				currentSystemLevel = getShieldGeneratorLevel();
				setShieldGeneratorLevel(currentSystemLevel + 1);
				break;
				
			case HOMING_MISSILE:
				currentSystemLevel = getHomingMissileLevel();
				setHomingMissileLevel(currentSystemLevel + 1);
				break;
				
			case EXTRA_POINTS:
				//awarded via controller object. Shouldn't reach here.
				DebugManager.logMessage(3, "Award points through system upgrade.");
				break;
				
			case MULTISHOT:
				currentSystemLevel = getMultishotLevel();
				setMultiShopLevel(currentSystemLevel + 1);
				break;
		} //end switch based on which ship system is being upgraded.
	} //end method upgradeSystem
	
	
	public int getFrameNumber() { return this.frameNumber; }
	public void incrementFrameNumber() 
	{
		frameNumber += 1;
		if ( frameNumber >= GameConstants.FRAMES_PER_SECOND )
		{
			frameNumber = 0;
		} //check for upper bounds. Reset to 0 if passed.
	} //end method incrementFrameNumber
	
	
	//Space objects getters/setters and manipulators
	
	public PlayerShip getPlayerShip() { return this.playerShip; }
	
	/**
	 * Generic method to add a space object to its corresponding list.
	 * Performs some basic sorting, but won't react well to being passed an object it doesn't understand.
	 * @param objectToAdd 
	 */
	public void addSpaceObjectToLists( SpaceObject objectToAdd )
	{
		//ASSUMPTION: the object should conform to one and only one of these categories.
		//if the object is of type Asteroid, add it to the asteroid list.
		if		( objectToAdd instanceof Asteroid )			{ addAsteroid((Asteroid)objectToAdd); }
		else if	( objectToAdd instanceof TrollBaseShip)		{ addTrollShip((TrollBaseShip)objectToAdd); }
		else if	( objectToAdd instanceof PlasmaBolt )		{ addPlasmaBolt((PlasmaBolt)objectToAdd); }
		else if ( objectToAdd instanceof PowerUpBaseObject) { addPowerUp((PowerUpBaseObject)objectToAdd); }
		else if ( objectToAdd instanceof HomingMissile)		{ addHomingMissile((HomingMissile) objectToAdd ); }
		
		//else we cannot add it, because we don't know of what type it is.
		else
		{
			DebugManager.logMessage(2, "Cannot add object of unrecognized type.");
		} //end else clause to note error of inability to add the object.
	} //end method addSpaceObjectToLists
	
	public void addAsteroid( Asteroid asteroidToAdd )			{	asteroidList.add( asteroidToAdd );	} //end method addAsteroid
	public ConcurrentLinkedQueue<Asteroid> getAsteroidList()	{ return this.asteroidList; }
	public void removeExpiredAsteroids()
	{
		for ( Asteroid currentAsteroid: asteroidList )
		{
			if ( true == currentAsteroid.checkExpired() ) 
			{
				asteroidList.remove(currentAsteroid);
			} //end if check for expired
		} //end for loop iterating through asteroids.
		
	} //end method removeExpiredPlasmaBolts
	
	public void addPlasmaBolt ( PlasmaBolt plasmaBoltToAdd )		{ plasmaBoltList.add(plasmaBoltToAdd); }
	public ConcurrentLinkedQueue<PlasmaBolt> getPlasmaBoltList ()	{ return this.plasmaBoltList; }
	public void removeExpiredPlasmaBolts()
	{
		for ( PlasmaBolt currentPlasmaBolt: plasmaBoltList )
		{
			if ( true == currentPlasmaBolt.checkExpired() )
			{
				plasmaBoltList.remove(currentPlasmaBolt);
			} //end if check for expired
		} //end for loop iterating through plasma bolts
	} //end method removeExpiredPlasmaBolts
	public Multi_Shot_Direction getMultiShotDirection () { return this.multiShotDirection; }
	private void setMultiShotDirection ( Multi_Shot_Direction newDirection ) { this.multiShotDirection = newDirection; }
	public void toggleMultiShotDirection()
	{
		//make it the opposite of whatever it is currently.
		Multi_Shot_Direction currentDirection = getMultiShotDirection ();
		if ( Multi_Shot_Direction.LEFT == currentDirection )
		{
			setMultiShotDirection(Multi_Shot_Direction.RIGHT);
		}
		else
		{
			setMultiShotDirection(Multi_Shot_Direction.LEFT);
		}
	} //end method toggleMultiShotDirection
	
	
	public ConcurrentLinkedQueue<TrollBaseShip> getTrollShipList() { return trollShipList; }
	public void addTrollShip( TrollBaseShip trollToAdd ) { trollShipList.add(trollToAdd); }
	public void removeExpiredTrollShips( Asterage2Controller asterage2Controller, GUI gui )
	{
		for ( TrollBaseShip currentTroll: trollShipList )
		{
			if ( true == currentTroll.checkExpired() )
			{
				trollShipList.remove(currentTroll);
				
				//if the destroyed troll was a weapons pod, tell the owning parent to detach it.
				if ( currentTroll instanceof TrollWeaponPod )
				{
					detachPodFromParent( (TrollWeaponPod) currentTroll );
				} //end if check for a dead weapon pod.
				
				//if the destroyed troll was a controller of weapons pods, the pods must be signalled that they are free.
				if ( currentTroll instanceof ControlsWeaponsPods )
				{
					((ControlsWeaponsPods) currentTroll).freeAllPodsUponDeath();
				} //end if check for death of weapons pods controller.
				
				//if the destroyed troll was a Troll Mining Pod, we have to check why it died.
				//if it died from damage, we must initiate the countdown to summon a troll mothership,
				//and continue just as if it were any other troll ship being destroyed.
				//if it died from old age, then we must generate a warp-out effect and play that sound effect instead, then 
				//continue through to the bottom of this loop iteration.
				if ( currentTroll instanceof TrollMiningPod )
				{
					boolean podDiedOfDamage = ((TrollMiningPod) currentTroll).checkPodDiedOfDamage();
					if ( podDiedOfDamage )
					{
						beginMothershipSpawnCountdown();
					} //end if check for 
					else
					{
						//warp out pod and skip rest of loop.
						gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_WARPING_OUT);
						addSpaceEffect(new WarpOutEffect( currentTroll, WarpOutEffect.WarpEffectSize.SMALL ));
						continue;
					} //end else clause for pod which did not die of player damage.
				} //end if check for a troll mining pod
				
				//award points, and play sound effects appropriate to a ship being destroyed. Generate an explosion.
				asterage2Controller.awardPoints( currentTroll.getPointValueDestroy() );
				ShipExplosionEffect trollShipExplosion = new ShipExplosionEffect(	currentTroll.getxCoordinateAsInt(), 
																					currentTroll.getyCoordinateAsInt(), 
																					ShipExplosionEffect.ExplosionType.TROLL, 
																					currentTroll.getSpatialRadius() * 3);
			
				addSpaceEffect ( trollShipExplosion );
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_DESTROYED);
				
			} //end if check for expiration and removal.
		} //end for loop iterating through troll ships
	} //end method removeExpiredTrollShips
	
	private void detachPodFromParent( TrollWeaponPod deadPod )
	{
		//get the parent. If the parent is not null, then instruct it to detach the dead pod.
		ControlsWeaponsPods podController = deadPod.getParentShip();
		if ( null != podController )
		{
			podController.detachDeadWeaponPod(deadPod);
		} //end if check 
	} //end method detachPodFromParent
	
	public ConcurrentLinkedQueue<PowerUpBaseObject> getPowerUpList() { return powerUpList; }
	public void addPowerUp( PowerUpBaseObject powerUpToAdd ) { powerUpList.add(powerUpToAdd); }
	public void removeExpiredPowerUps()
	{
		for ( PowerUpBaseObject currentPowerUp: powerUpList )
		{
			if ( true == currentPowerUp.checkExpired() )
			{
				powerUpList.remove(currentPowerUp);
			} //end if check for expired power up
		} //end for loop iterating through power ups in the list
	} //end method removeExpiredPowerUps
	
	
	public ConcurrentLinkedQueue<HomingMissile> getHomingMissileList() { return this.homingMissileList; }
	public void addHomingMissile( HomingMissile newMissile ) { homingMissileList.add(newMissile); }
	public void removeExpiredHomingMissiles()
	{	
		for ( HomingMissile currentHomingMissile: homingMissileList )
		{
			if ( true == currentHomingMissile.checkExpired() )
			{
				//remove the homing missile from the list, and then generate a small ship explosion, based on the 
				//type of missile and last location.
				homingMissileList.remove( currentHomingMissile );
				ShipExplosionEffect.ExplosionType whatExplosionType = 
						( HomingMissile.MissileType.PLAYER == currentHomingMissile.getMissileType() ) ?
							ShipExplosionEffect.ExplosionType.PLAYER : ShipExplosionEffect.ExplosionType.TROLL;
				ShipExplosionEffect newExplosionEffect =
						new ShipExplosionEffect(	currentHomingMissile.getxCoordinateAsInt(),
													currentHomingMissile.getyCoordinateAsInt(),
													whatExplosionType,
													currentHomingMissile.getSpatialRadius() );
				addSpaceEffect(newExplosionEffect);
			} //end if check for expired homing missile
		} //end for loop iterating through homing missiles
	} //end method removeExpiredHomingMissiles
	
	public int getCountPlayerHomingMissiles()
	{
		int playerMissileCount = 0;		//initial value, starts at 0.
		
		//iterate through missiles and count the player ones.
		for ( HomingMissile currentHomingMissile : homingMissileList )
		{
			if ( HomingMissile.MissileType.PLAYER == currentHomingMissile.getMissileType() )
			{
				playerMissileCount += 1;
			} //end if check for missile type as belonging to a player
		} //end for loop iterating through homing missiles
		
		return playerMissileCount;
	} //end method getCountPlayerHomingMissiles
	
	
	public ConcurrentLinkedQueue<SpaceEffect> getSpaceEffectList() { return this.spaceEffectList; }
	public void addSpaceEffect( SpaceEffect effectToAdd ) { spaceEffectList.add(effectToAdd); }
	public void removeExpiredEffects ()
	{
		for ( SpaceEffect currentEffect: spaceEffectList )
		{
			if ( true == currentEffect.checkExpired() )
			{
				spaceEffectList.remove( currentEffect );
			} //end if check for expired space effect
		} //end for loop iterating through space effects
	} // end method removeExpiredEffects
	
	public void ageSpaceEffects ()
	{
		for ( SpaceEffect currentEffect: spaceEffectList )
		{
			currentEffect.decrementExpiredCountdownTimer();
		} //end for loop iterating through space effects
	} //end method ageSpaceEffects
	
	/**
	 * Is a ship exploding on the game board?
	 * @return 
	 */
	public boolean checkShipExplosionEffectOnBoard()
	{
		for ( SpaceEffect currentEffect : spaceEffectList )
		{
			if ( currentEffect instanceof ShipExplosionEffect )
			{
				//if we find one, we can stop and return true immediately.
				return true;
			} //end if check for ship explosion type of effect
		} //end for loop 
		
		//if we've made it this far, then the answer is no. return false.
		return false;
	} //end method checkShipExplosionEffectOnBoard
	
	
	private void setExtraLivesAwarded ( int livesAwardedTallyToSet ) { this.extraLivesAwarded = livesAwardedTallyToSet; }
	public int getExtraLivesAwarded() { return this.extraLivesAwarded; }
	public void incrementExtraLivesAwarded() { extraLivesAwarded += 1; }
	public long getPointsRequiredForNextExtraLife() 
	{ 
		return POINTS_REQUIRED_PER_EXTRA_LIFE * (getExtraLivesAwarded() + 1);
	} //end method getPointsRequiredForNextExtraLife() 
	
	
	
	public boolean checkPopUpMessageBeingShown() { return popUpMessageBeingShown; }
	public int getPopUpMessageRemainingCountdown() { return popUpMessageExpiredCounter; }
	public void resetPopUpMessageCountdown()
	{
		//set the flag to true, and set the counter to max.
		popUpMessageBeingShown = true;
		popUpMessageExpiredCounter = POP_UP_MESSAGE_MAX_EXPIRED_COUNTER;
	} //end method resetPopUpMessageCountdown
	public void decrementPopUpCountdown() 
	{
		//decrement by 1 if it will not take it below 0.
		popUpMessageExpiredCounter = ( popUpMessageExpiredCounter > 0 ) ?
				( popUpMessageExpiredCounter - 1 ) : 0;
	} //end method decrementPopUpCountdown
	
	
	
	// High score directory exposed interface.
	public String getHighScoreNameAtPlace( int place )
	{
		return highScoreDirectory.getNameAtPlace(place);
	}
	public String getHighScoreLevelAtPlace( int place )
	{
		return highScoreDirectory.getLevelAtPlace(place);
	}
	public String getHighScorePointsAtPlace( int place )
	{
		return highScoreDirectory.getPointsAtPlace(place);
	}
	public boolean doesScoreRankTopTen( long points )
	{
		return highScoreDirectory.doesScoreRankTopTen( points );
	} //end function doesScoreRankTopTen
	
	public void insertNewRecord ( String name, int level, long points )
	{
		highScoreDirectory.insertNewRecord(name, level, points);
	} //end function insertNewRecord
	
	

	//weapons systems interface methods.
	public int getHomingMissileLevel()				{	return playerShip.getHomingMissileLevel();	}
	public int getMultishotLevel()					{	return playerShip.getMultiShotLevel();	}
	public boolean checkSonicDisruptorEquipped()	{	return playerShip.checkSonicDisruptorEquipped();	}
	public int getDecelerationLevel()				{	return playerShip.getDecelerationLevel();	}
	public int getShieldGeneratorLevel()			{	return playerShip.getShieldGeneratorLevel();	}
	public boolean checkGravityNetEquipped()		{	return playerShip.checkGravityNetEquipped();	}
	
	public void setHomingMissileLevel( int passedLevel )
	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL))
		{
			playerShip.setHomingMissileLevel(passedLevel);
		} //end if check for out of bounds.
	} //end method setHomingMissileLevel
	
	public void setMultiShopLevel( int passedLevel )
	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL))
		{
			playerShip.setMultiShotLevel(passedLevel);
		} //end if check for out of bounds.
	} //end method setMultiShopLevel
	
	public void setDecelerationLevel( int passedLevel )
	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL))
		{
			playerShip.setDecelerationLevel(passedLevel);
		} //end if check for out of bounds.
	} //end method setDecelerationLevel
	
	public void setShieldGeneratorLevel( int passedLevel )
	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL))
		{
			playerShip.setShieldGeneratorLevel(passedLevel);
		} //end if check for out of bounds.
	} //end method setShieldGeneratorLevel
	
	public void setSonicDisruptorEquipped( boolean passedEquippedValue ) { playerShip.setSonicDisruptorEquipped(passedEquippedValue); }
	public void setGravityNetEquipped( boolean passedEquippedValue ) { playerShip.setGravityNetEquipped(passedEquippedValue); }
	
	
	public int getSpecialItemDropCounter() { return this.countSpecialItemsDroppedThisLevel; }
	public void setSpecialItemDropCounter( int newValue ) { this.countSpecialItemsDroppedThisLevel = newValue; }
	public void incrementSpecialItemDropCounter() { this.countSpecialItemsDroppedThisLevel += 1; }
	public boolean checkSpecialItemDropsLeftThisLevel() 
	{
		//return the value resulting from comparing to see if the number of drops is below the max for the level.
		return ( getSpecialItemDropCounter() < this.MAX_SPECIAL_ITEM_DROPS_PER_LEVEL );
	} //end method specialItemDropsLeftThisLevel
	public void resetSpecialItemDropCounter() { this.countSpecialItemsDroppedThisLevel = 0; }
	

	public boolean	getSpawnedTrollMothershipRecently(){ return this.spawnedTrollMothershipRecently; }
	public void setSpawnedTrollMothershipRecently( boolean newFlag ) { spawnedTrollMothershipRecently = newFlag; }
	public boolean checkMothershipSpawnCountdownActive() { return this.trollMothershipSpawnCountdownInProgress; }
	public void beginMothershipSpawnCountdown() 
	{
		//set the countdown to max, set the spawn_recently flag to true, and set the flag of a countdown in progress to true.
		spawnedTrollMothershipRecently = true;
		trollMothershipSpawnCountdownInProgress = true;
		currentTrollMothershipSpawnCountdown = TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN;
	} //end method beginMothershipSpawnCountdown
	
	public boolean advanceTrollMothershipSpawnCountdown()
	{
		//first check if a countdown is even in progress. If not then do nothing and return false.
		if ( false == trollMothershipSpawnCountdownInProgress) { return false; }
		
		//else advance the countdown and return true if the counter is at 0, or false if not.
		//Also if the countdown has reached 0, set the flag of a countdown in progress to false.
		currentTrollMothershipSpawnCountdown -= 1;
		currentTrollMothershipSpawnCountdown = (currentTrollMothershipSpawnCountdown >= 0 ) ? currentTrollMothershipSpawnCountdown : 0;
		if ( 0 == currentTrollMothershipSpawnCountdown )
		{
			trollMothershipSpawnCountdownInProgress = false;
			return true;
		} //end if check for case of countdown over.
		else
		{
			return false;
		} //end if check for case of countdown still in progress.
	} //end metho advanceTrollMothershipSpawnCountdown;
	
	/**
	 * Check to make sure we can only spawn one of any such kind of ship at one time.
	 * @return 
	 */
	public boolean checkMothershipOrTrollMiningPodInPlay()
	{
		for ( TrollBaseShip currentTroll: getTrollShipList() )
		{
			if ( currentTroll instanceof TrollMothership ||
					currentTroll instanceof TrollMiningPod )
			{
				return true;
			} //end if check for a match
		} //end for loop iterating through each troll ship, looking for a mining pod or a mothership.
		
		//if we get here, then no match.
		return false;
	} //end method checkMothershipOrTrollMiningPodInPlay
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum PowerUpMenuOption
	{
		//each enum should have an index to identify it. All indicies should be contiguous.
		DECELERATION(0, 1),
		HOMING_MISSILE(1, 2),
		SHIELD_GENERATOR(2, 3),
		EXTRA_POINTS(3, 4),
		MULTISHOT(4, 5);
		
		int index;
		int cost;
		
		PowerUpMenuOption( int passedIndex,int passedCost )
		{
			this.index = passedIndex;
			this.cost = passedCost;
		} //end constructor
		
		public int toInt() { return this.index; }
		public static PowerUpMenuOption fromInt ( int indexToMatch ) 
		{
			//go through each option, looking for a match. 
			//If no match found, throw an exception rather than returning null.
			for ( PowerUpMenuOption currentOption : values() )
			{
				if ( indexToMatch == currentOption.toInt() ) 
				{ 
					return currentOption; 
				}
			} //end for loop iterating through Power Up Menu Options
			
			throw new IllegalArgumentException ( "Cannot match Power Up option with index of : " + indexToMatch );
		} //end menu from 
		
		public PowerUpMenuOption getNext() 
		{
			//get the current index, and add one, adjusting for the limit.
			int currentIndex = this.toInt();
			int indexModulus = values().length;
			int indexToFind = (currentIndex + 1) % indexModulus;
			
			return fromInt ( indexToFind );
		} //end method getNext
		
		public PowerUpMenuOption getPrevious()
		{
			//check if we are at 0. If so, then we go to the highest indexed item. If not, just subtract 1.
			int currentIndex = this.toInt();
			int maxIndex = values().length - 1;
			int indexToFind = ( currentIndex > 0 ) ?  (currentIndex -1)	:	maxIndex;
			
			return fromInt ( indexToFind );
		} //end method getPrevious
		
		public int getCost() { return this.cost; } 
		
		public static long getExtraPointsPowerUpValue() { return EXTRA_POINT_PURCHASE.getPointAward(); }
	} //end enum PowerUpMenuOption
	
	
	public static enum Multi_Shot_Direction
	{
		LEFT,
		RIGHT;
	} //end enum Multi Shot Direction
	
	
	public static enum GameState
	{
		RUNNING,
		PAUSE,
		GAME_OVER,
		WARPING_OUT;
	} //end enum GameState definition
	
	
	public static enum POINT_AWARDS
	{
		LARGE_ASTEROID_HIT(10l),
		MEDIUM_ASTEROID_HIT(10l),
		SMALL_ASTEROID_HIT(10l),
		
		TROLL_BASIC_SHIP_HIT(5l),
		TROLL_BASIC_SHIP_DESTROYED(250l),
		TROLL_MOTHERSHIP_DESTROYED(1000l),
		
		EXTRA_POINT_PURCHASE( POINTS_REQUIRED_PER_EXTRA_LIFE/2 );
		
		long pointAward;
		
		POINT_AWARDS( long passedValue ) { this.pointAward = passedValue; }
		
		public long getPointAward() { return this.pointAward; }
	}
} //end class Asterage2State
