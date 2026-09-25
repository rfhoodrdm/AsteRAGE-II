

package com.rfhoodrdm.asterage2.state.asterage2;

import static com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid.Asteroid_Type_Size.LARGE_PURPLE;
import static com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid.Asteroid_Type_Size.LARGE_RED;
import static com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid.Asteroid_Type_Size.LARGE_TAN;
import static com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid.Asteroid_Type_Size.LARGE_WHITE;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.controller.asterage2.Asterage2Controller;
import com.rfhoodrdm.asterage2.controller.asterage2.Asterage2Controller.AsteroidPointCard;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
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
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.asterage2.AsteRAGE2GameBoard;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.ControlsWeaponsPods;
import com.rfhoodrdm.asterage2.sounds.SoundEvent;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.asterage2.constants.GameState;
import com.rfhoodrdm.asterage2.state.asterage2.constants.MultiShotDirection;
import com.rfhoodrdm.asterage2.state.asterage2.constants.PowerUpMenuOption;
import com.rfhoodrdm.asterage2.state.highscore.HighScoreDirectory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Asterage2State
	implements RequiresLoadedData {
	
	public static int POP_UP_MESSAGE_MAX_EXPIRED_COUNTER = GameConstants.FRAMES_PER_SECOND;	//how long until message expires
	public static int POP_UP_MESSAGE_CLEAR_TIME = GameConstants.FRAMES_PER_SECOND / 3;		//at what point in the countdown is the message cleared?
	public static final int MAX_SPECIAL_ITEM_DROPS_PER_LEVEL = 2;
	public static final long POINTS_REQUIRED_PER_EXTRA_LIFE = 20000;						//number of required points for each extra life.
	public static final int MAX_WARP_OUT_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 5;	//5 seconds
	public static final int MIN_SHIP_SYSTEM_LEVEL = 0;										//lower bound on ship system level
	public static final int MAX_SHIP_SYSTEM_LEVEL = 2;										//how many times may a system be upgraded.
	public static final int MAX_POWER_UP_POINTS = 5;		//how many points can the player possibly collect at once?
	public static final int MIN_POWER_UP_POINTS = 0;		//lowest possible number of points the player may have at once.
	public static final int MAX_TROLL_SCOUT_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 30;					//one every 30 seconds.
	public static final int TROLL_SCOUT_COUNTDOWN_DEFERMENT_BONUS = GameConstants.FRAMES_PER_SECOND * 10;		//defer 10 seconds
	public static final int TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 2;			//3 seconds worth

	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private HighScoreDirectory highScoreDirectory;
	
	//graphical display data members
	@Getter private int frameNumber = 0;			//what redraw frame are we on? Out of however many frames per second.
	
	//game stats data members
	@Getter @Setter private int gameLevel;					//what level is currently being played?
	@Getter @Setter private int remainingShips;				//how many ships does the player have left?
	@Getter @Setter(AccessLevel.PRIVATE) private int extraLivesAwarded;			//how many extra lives have been awarded already?
	@Getter @Setter private long score;						//how many points has the player earned?
	@Getter private GameState gameState;			//what is the current state of the current game?
	@Getter @Setter private boolean requestToSpawnShipFlag;	//set to true when the player requests to spawn the ship.
	
	@Getter private int warpOutCountdown;			//countdown timer to end of warp-out sequence.
	
	//information about various ship's systems and upgrade levels.
	
	private int powerUpPoints;										//how many power up points have been collected.
	private PowerUpMenuOption currentSelectedPowerUpMenuOption;		//which option from the menu is currently selected?
	
	@Getter @Setter private MultiShotDirection multiShotDirection;				//which side of the ship is the multi shot fired on?
	
	//List of In-Play Game objects
	private PlayerShip playerShip;
	private ConcurrentLinkedQueue<Asteroid> asteroidList;			//list of asteroids in play
	private ConcurrentLinkedQueue<PlasmaBolt> plasmaBoltList;		//list of plasma bolts on the board
	private ConcurrentLinkedQueue<TrollBaseShip> trollShipList;		//list of enemy ships in play
	private ConcurrentLinkedQueue<PowerUpBaseObject> powerUpList;	//list of power ups available on the game board
	private ConcurrentLinkedQueue<SpaceEffect> spaceEffectList;		//list of space effects not attached to other objects.
	private ConcurrentLinkedQueue<HomingMissile> homingMissileList;	//list of homing missiles currently on the screen.
	
	@Getter private boolean popUpMessageBeingShown;		//is a message currently on the screen.
	@Getter private int popUpMessageRemainingCountdown;				//countdown to expired message.

	//troll state objects 
	@Getter @Setter private int remainingTrollScoutCountdown;
	@Getter @Setter private int countSpecialItemsDroppedThisLevel;
	
	@Getter @Setter private boolean spawnedTrollMothershipRecently = false;
	private boolean trollMothershipSpawnCountdownInProgress = false;
	private int currentTrollMothershipSpawnCountdown = TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN;
	
	private SoundManager soundManager; //TODO: this should not be called from state, perhaps. Refactor!
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage2State (SoundManager soundManager) {
		this.soundManager = soundManager;	//TODO: see comment above. Refactor!
		initializeAsterage2State();
	}  
	
	@Override
	public void loadRequiredData(DataLoader dataLoader) {
		highScoreDirectory = new HighScoreDirectory( HighScoreDirectory.GAME_IDENTIFIER.ASTERAGE2,
				dataLoader);
	}
	
	public void initializeAsterage2State() {
		//initialize the game state variables.
		setGameLevel(1);													//default game level is 1.
		setRemainingShips(3);												//starting number of lives 
		setExtraLivesAwarded(0);											//we have awarded 0 extra lives so far
		setScore(0l);														//starting score is 0 points.
		setGameState( GameState.RUNNING );									//start game as running.
		setRequestToSpawnShipFlag(false);									//no initial request to spawn the ship.
		multiShotDirection = MultiShotDirection.LEFT;						//start on the left side. 
		currentSelectedPowerUpMenuOption = PowerUpMenuOption.fromInt(0);	//start at the leftmost option at the start.
		resetTrollScoutCountdown();											//set troll scout countdown to max.
		
		//initialize in-play game objects.
		double playerShipXLocation = AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.width / 2;				//generated at center of game board.
		double playerShipYLocation = AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.height / 2;			//generated at center of game board
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
		
		generateAsteroidsForLevel();
	} 

	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void setGameState( GameState newGameState ) { 
		this.gameState = newGameState; 
		//if the state is WARPING OUT, then reset the warping out timer.
		if ( GameState.WARPING_OUT == newGameState ) {
			resetWarpingOutCountDown();
		} 
	} 
	
	public void resetTrollScoutCountdown() { this.remainingTrollScoutCountdown = MAX_TROLL_SCOUT_COUNTDOWN; }
	public void grantTrollScoutCountdownDeferment() { this.remainingTrollScoutCountdown += TROLL_SCOUT_COUNTDOWN_DEFERMENT_BONUS;}
	public boolean checkTimeToSpawnTrollScout() { return ( 0 == getRemainingTrollScoutCountdown() ); }
	public void decrementTrollScoutCountdown() {
		//decrement by 1. Don't go below 0.
		remainingTrollScoutCountdown -= 1;
		if ( 0 > remainingTrollScoutCountdown ) { remainingTrollScoutCountdown = 0; }
	} 
	
	private void resetWarpingOutCountDown() { warpOutCountdown = MAX_WARP_OUT_COUNTDOWN; }
	public void decrementWarpingOutCountDown() {
		//decrement by 1, don't go below 0.
		warpOutCountdown = ( warpOutCountdown > 0 ) ?
				(warpOutCountdown - 1) : 0;				
	}
	
	public int getRemainingShieldPercentage() 	{
		//TODO: This needs to be an Optional check 
		if ( null == playerShip ) return 0;		//if no ship, show no shields.
		
		return playerShip.getRemainingShieldPercentage();
	} 
	
	public int getPowerUpPoints() { return this.powerUpPoints; } //return 5; }//debug code
	public void setPowerUpPoints( int passedPowerUpPoints ) {
		//set the number of power up points.
		//If the argument is below the minimum, use the minimum instead. 
		//Likewise, if the argument is above the maximum, use the maximum instead.
		if ( passedPowerUpPoints < MIN_POWER_UP_POINTS )	{	powerUpPoints = MIN_POWER_UP_POINTS;	}
		else if ( passedPowerUpPoints > MAX_POWER_UP_POINTS )	{ powerUpPoints = MAX_POWER_UP_POINTS; }
		else { powerUpPoints = passedPowerUpPoints; }
	}
	
	public PowerUpMenuOption getCurrentSelectedPowerUpMenuOption() { return this.currentSelectedPowerUpMenuOption; }
	public void setCurrentSelectedPowerUpMenuOption ( PowerUpMenuOption passedOption ) 	{ 
		if ( null != passedOption ) 		{
			this.currentSelectedPowerUpMenuOption = passedOption;
		} 
	} 
	
	/**
	 * Check the current level of the system, if applicable.
	 */
	public boolean checkSystemAtMaxLevel( PowerUpMenuOption whichSystem ) {
		return switch (whichSystem) {
			case DECELERATION -> 		getDecelerationLevel() == MAX_SHIP_SYSTEM_LEVEL ;
			case SHIELD_GENERATOR -> 	getShieldGeneratorLevel() == MAX_SHIP_SYSTEM_LEVEL;
			case HOMING_MISSILE -> 		getHomingMissileLevel() == MAX_SHIP_SYSTEM_LEVEL ;
			case EXTRA_POINTS ->		false;	//extra points are never maxed out.
			case MULTISHOT ->			getMultishotLevel() == MAX_SHIP_SYSTEM_LEVEL;
		};
	} 
	
	/**
	 * Upgrade the system designated.
	 */
	public void upgradeSystem( PowerUpMenuOption whichSystem )	{
		//check first to see if the system is at max level already, just to be safe.
		if ( true == checkSystemAtMaxLevel(whichSystem)) { 
			log.warn("Cannot upgrade system: {}. Already at max level.", whichSystem);
			return;
		} 
		
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
				log.warn("Award points through system upgrade.");
				break;
				
			case MULTISHOT:
				currentSystemLevel = getMultishotLevel();
				setMultiShopLevel(currentSystemLevel + 1);
				break;
		} 
	} 
	
	public void incrementFrameNumber() 	{
		frameNumber = (frameNumber + 1) % GameConstants.FRAMES_PER_SECOND;
	}
	
	
	//Space objects getters/setters and manipulators
	//TODO: return a defensive copy?
	public PlayerShip getPlayerShip() { return this.playerShip; }
	
	/**
	 * Generic method to add a space object to its corresponding list.
	 * Performs some basic sorting, but won't react well to being passed an object it doesn't understand.
	 */
	public void addSpaceObjectToLists( SpaceObject objectToAdd ) {
		//ASSUMPTION: the object should conform to one and only one of these categories.
		//if the object is of type Asteroid, add it to the asteroid list.
		if		( objectToAdd instanceof Asteroid )			{ addAsteroid((Asteroid)objectToAdd); }
		else if	( objectToAdd instanceof TrollBaseShip)		{ addTrollShip((TrollBaseShip)objectToAdd); }
		else if	( objectToAdd instanceof PlasmaBolt )		{ addPlasmaBolt((PlasmaBolt)objectToAdd); }
		else if ( objectToAdd instanceof PowerUpBaseObject) { addPowerUp((PowerUpBaseObject)objectToAdd); }
		else if ( objectToAdd instanceof HomingMissile)		{ addHomingMissile((HomingMissile) objectToAdd ); }
		
		//else we cannot add it, because we don't know of what type it is.
		else {
			log.error("Cannot add object of unrecognized type.");
		} 
	} 
	
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
	
	public void toggleMultiShotDirection()	{
		MultiShotDirection newDirection = (MultiShotDirection.LEFT.equals(getMultiShotDirection()))
				?  MultiShotDirection.RIGHT
				:  MultiShotDirection.LEFT;
		setMultiShotDirection(newDirection);
	} 
	
	
	public ConcurrentLinkedQueue<TrollBaseShip> getTrollShipList() { return trollShipList; }
	public void addTrollShip( TrollBaseShip trollToAdd ) { trollShipList.add(trollToAdd); }
	public void removeExpiredTrollShips( Asterage2Controller asterage2Controller, GUI gui )	{
		for ( TrollBaseShip currentTroll: trollShipList )		{
			if ( true == currentTroll.checkExpired() )			{
				trollShipList.remove(currentTroll);
				
				//if the destroyed troll was a weapons pod, tell the owning parent to detach it.
				if ( currentTroll instanceof TrollWeaponPod )				{
					detachPodFromParent( (TrollWeaponPod) currentTroll );
				} 
				
				//if the destroyed troll was a controller of weapons pods, the pods must be signalled that they are free.
				if ( currentTroll instanceof ControlsWeaponsPods )				{
					((ControlsWeaponsPods) currentTroll).freeAllPodsUponDeath();
				} 
				
				//if the destroyed troll was a Troll Mining Pod, we have to check why it died.
				//if it died from damage, we must initiate the countdown to summon a troll mothership,
				//and continue just as if it were any other troll ship being destroyed.
				//if it died from old age, then we must generate a warp-out effect and play that sound effect instead, then 
				//continue through to the bottom of this loop iteration.
				if ( currentTroll instanceof TrollMiningPod )				{
					boolean podDiedOfDamage = ((TrollMiningPod) currentTroll).checkPodDiedOfDamage();
					if ( podDiedOfDamage )					{
						beginMothershipSpawnCountdown();
					} else {
						//warp out pod and skip rest of loop.
						soundManager.playSoundEvent(SoundEvent.A2_WARPING_OUT);
						addSpaceEffect(new WarpOutEffect( currentTroll, WarpOutEffect.WarpEffectSize.SMALL ));
						continue;
					} 
				} 
				
				//award points, and play sound effects appropriate to a ship being destroyed. Generate an explosion.
				asterage2Controller.awardPoints( currentTroll.getPointValueDestroy() );
				ShipExplosionEffect trollShipExplosion = new ShipExplosionEffect(	currentTroll.getxCoordinateAsInt(), 
																					currentTroll.getyCoordinateAsInt(), 
																					ShipExplosionEffect.ExplosionType.TROLL, 
																					currentTroll.getSpatialRadius() * 3);
			
				addSpaceEffect ( trollShipExplosion );
				soundManager.playSoundEvent(SoundEvent.A2_TROLL_DESTROYED);
				
			} 
		} 
	} 
	
	private void detachPodFromParent( TrollWeaponPod deadPod )	{
		//get the parent. If the parent is not null, then instruct it to detach the dead pod.
		ControlsWeaponsPods podController = deadPod.getParentShip();
		if ( null != podController ) {
			podController.detachDeadWeaponPod(deadPod);
		} 
	} 
	
	// TODO: return defensive copy instead?
	public ConcurrentLinkedQueue<PowerUpBaseObject> getPowerUpList() { return powerUpList; }
	public void addPowerUp( PowerUpBaseObject powerUpToAdd ) { powerUpList.add(powerUpToAdd); }
	public void removeExpiredPowerUps() {
		for ( PowerUpBaseObject currentPowerUp: powerUpList ) {
			if ( true == currentPowerUp.checkExpired() ) {
				powerUpList.remove(currentPowerUp);
			} 
		} 
	} 
	
	//TODO: make defensive copy?
	public ConcurrentLinkedQueue<HomingMissile> getHomingMissileList() { return this.homingMissileList; }
	public void addHomingMissile( HomingMissile newMissile ) { homingMissileList.add(newMissile); }
	public void removeExpiredHomingMissiles() {	
		for ( HomingMissile currentHomingMissile: homingMissileList ) {
			if ( true == currentHomingMissile.checkExpired() ) {
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
			} 
		} 
	} 
	
	public int getCountPlayerHomingMissiles() {
		int playerMissileCount = 0;		//initial value, starts at 0.
		
		//iterate through missiles and count the player ones.
		for ( HomingMissile currentHomingMissile : homingMissileList ) {
			if ( HomingMissile.MissileType.PLAYER == currentHomingMissile.getMissileType() ) {
				playerMissileCount += 1;
			}
		} 
		
		return playerMissileCount;
	}
	
	//TODO: return defensive copy instead?
	public ConcurrentLinkedQueue<SpaceEffect> getSpaceEffectList() { return this.spaceEffectList; }
	public void addSpaceEffect( SpaceEffect effectToAdd ) { spaceEffectList.add(effectToAdd); }
	
	public void ageSpaceEffectsAndRemoveExpired ()	{
		for ( SpaceEffect currentEffect: spaceEffectList ) {
			currentEffect.decrementExpiredCountdownTimer();
			if ( true == currentEffect.checkExpired() )	{
				spaceEffectList.remove( currentEffect );
			} 
		} 
	} 
	
	/**
	 * Is a ship exploding on the game board?
	 */
	public boolean checkShipExplosionEffectOnBoard() {
		for ( SpaceEffect currentEffect : spaceEffectList )	{
			if ( currentEffect instanceof ShipExplosionEffect )	{
				return true;
			} 
		} 
		
		return false;
	} 
	
	public void incrementExtraLivesAwarded() { extraLivesAwarded += 1; }
	public long getPointsRequiredForNextExtraLife() { 
		return POINTS_REQUIRED_PER_EXTRA_LIFE * (getExtraLivesAwarded() + 1);
	} 
	
	public void resetPopUpMessageCountdown() {
		popUpMessageBeingShown = true;
		popUpMessageRemainingCountdown = POP_UP_MESSAGE_MAX_EXPIRED_COUNTER;
	}
	
	/**
	 * Decrement by 1, but don't go below 0.
	 */
	public void decrementPopUpCountdown() {
		popUpMessageRemainingCountdown = Math.max(popUpMessageRemainingCountdown - 1, 0);
	}
	
	// High score directory exposed interface.
	public String getHighScoreNameAtPlace( int place )	{
		return highScoreDirectory.getNameAtPlace(place);
	}
	
	public String getHighScoreLevelAtPlace( int place )	{
		return highScoreDirectory.getLevelAtPlace(place);
	}
	
	public String getHighScorePointsAtPlace( int place ) {
		return highScoreDirectory.getPointsAtPlace(place);
	}
	
	public boolean doesScoreRankTopTen( long points ) {
		return highScoreDirectory.doesScoreRankTopTen( points );
	}
	
	public void insertNewRecord ( String name, int level, long points )	{
		highScoreDirectory.insertNewRecord(name, level, points);
	}
	
	//weapons systems interface methods.
	public int getHomingMissileLevel()				{	return playerShip.getHomingMissileLevel();	}
	public int getMultishotLevel()					{	return playerShip.getMultiShotLevel();	}
	public boolean checkSonicDisruptorEquipped()	{	return playerShip.checkSonicDisruptorEquipped();	}
	public int getDecelerationLevel()				{	return playerShip.getDecelerationLevel();	}
	public int getShieldGeneratorLevel()			{	return playerShip.getShieldGeneratorLevel();	}
	public boolean checkGravityNetEquipped()		{	return playerShip.checkGravityNetEquipped();	}
	
	public void setHomingMissileLevel( int passedLevel ) {
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL)) {
			playerShip.setHomingMissileLevel(passedLevel);
		} 
	} 
	
	public void setMultiShopLevel( int passedLevel ) {
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL)) {
			playerShip.setMultiShotLevel(passedLevel);
		} 
	} 
	
	public void setDecelerationLevel( int passedLevel )	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL)) {
			playerShip.setDecelerationLevel(passedLevel);
		} 
	} 
	
	public void setShieldGeneratorLevel( int passedLevel )	{
		if ((passedLevel >= MIN_SHIP_SYSTEM_LEVEL) && (passedLevel <= MAX_SHIP_SYSTEM_LEVEL))		{
			playerShip.setShieldGeneratorLevel(passedLevel);
		} 
	} 
	
	public void setSonicDisruptorEquipped( boolean passedEquippedValue ) { playerShip.setSonicDisruptorEquipped(passedEquippedValue); }
	public void setGravityNetEquipped( boolean passedEquippedValue ) { playerShip.setGravityNetEquipped(passedEquippedValue); }
	
	public void incrementSpecialItemDropCounter() { this.countSpecialItemsDroppedThisLevel += 1; }
	public boolean checkSpecialItemDropsLeftThisLevel() 
	{
		//return the value resulting from comparing to see if the number of drops is below the max for the level.
		return ( getCountSpecialItemsDroppedThisLevel() < MAX_SPECIAL_ITEM_DROPS_PER_LEVEL );
	}
	public void resetSpecialItemDropCounter() { this.countSpecialItemsDroppedThisLevel = 0; }
	

	public boolean checkMothershipSpawnCountdownActive() { return this.trollMothershipSpawnCountdownInProgress; }
	public void beginMothershipSpawnCountdown() {
		//set the countdown to max, set the spawn_recently flag to true, and set the flag of a countdown in progress to true.
		spawnedTrollMothershipRecently = true;
		trollMothershipSpawnCountdownInProgress = true;
		currentTrollMothershipSpawnCountdown = TROLL_MOTHERSHIP_MAX_SPAWN_COUNTDOWN;
	} 
	
	public boolean advanceTrollMothershipSpawnCountdown() {
		//first check if a countdown is even in progress. If not then do nothing and return false.
		if ( false == trollMothershipSpawnCountdownInProgress) { 
			return false; 
		}
		
		//else advance the countdown and return true if the counter is at 0, or false if not.
		//Also if the countdown has reached 0, set the flag of a countdown in progress to false.
		currentTrollMothershipSpawnCountdown -= 1;
		currentTrollMothershipSpawnCountdown = (currentTrollMothershipSpawnCountdown >= 0 ) ? currentTrollMothershipSpawnCountdown : 0;
		if ( 0 == currentTrollMothershipSpawnCountdown ) {
			trollMothershipSpawnCountdownInProgress = false;
			return true;
		} else {
			return false;
		} 
	}
	
	/**
	 * Check to make sure we can only spawn one of any such kind of ship at one time.
	 */
	public boolean checkMothershipOrTrollMiningPodInPlay() {
		for ( TrollBaseShip currentTroll: getTrollShipList() ) {
			if ( currentTroll instanceof TrollMothership ||
					currentTroll instanceof TrollMiningPod ) {
				return true;
			} 
		} 
		
		//if we get here, then no match.
		return false;
	} 
	
	public void generateAsteroidsForLevel() {	
		//add one large white asteroid's worth of points to the point card for each level.
		//max of 78, since with the two extra starting asteroids, that makes 80, for 10 large purple asteroids.
		int asteroidPoints = getGameLevel() * LARGE_WHITE.generationPointValue();
		asteroidPoints = ( asteroidPoints >= (78 * LARGE_WHITE.generationPointValue()) ) ? 
						78 * LARGE_WHITE.generationPointValue() :
						asteroidPoints;
		
		AsteroidPointCard pointCard = new AsteroidPointCard();		//make a new asteroid point card, and add base asteroids.
		pointCard.addBaseStartingAsteroids();
		pointCard.creditPoints(asteroidPoints);
		
		//go through the while loop, incrementing the number of asteroids that we're going to spawn for this level.
		while ( pointCard.getRemainingPointsToSpend() >= LARGE_WHITE.generationPointValue() )	{
			pointCard.addToAsteroidCount(LARGE_WHITE, 1);
		} 
		
		final int LARGE_ASTEROID_CAP = 10;
		final int EXCHANGE_RATE = 2;
		
		//if we have too many large white asteroids, switch some out for large tans.
		//exchange large whites for 1 large tan as long as we are able.
		boolean tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_WHITE) > LARGE_ASTEROID_CAP;
		boolean haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_WHITE) >= EXCHANGE_RATE;
		while( tooManyLargeAsteroids && haveExchangeAvailable )	{
			pointCard.decrementFromAsteroidCount(LARGE_WHITE, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_TAN, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_WHITE) + pointCard.getAsteroidCount(LARGE_TAN)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_WHITE) >= EXCHANGE_RATE;
		}
		
		//similarly, if we have too many tan asteroids, switch some out for large red ones.
		//exchange large tan asteroids for 1 large red as long as we are able.
		tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_TAN) > LARGE_ASTEROID_CAP;
		haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_TAN) >= EXCHANGE_RATE;
		while( tooManyLargeAsteroids && haveExchangeAvailable )	{
			pointCard.decrementFromAsteroidCount(LARGE_TAN, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_RED, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_TAN) + pointCard.getAsteroidCount(LARGE_RED)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_TAN) >= EXCHANGE_RATE;
		} 
		
		
		//lastly, if we have too many red asteroids, switch some out for large purple ones.
		//exchange large red asteroids for purple ones as long as we are able.
		tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_RED) > LARGE_ASTEROID_CAP;
		haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_RED) >= EXCHANGE_RATE;
		while ( tooManyLargeAsteroids && haveExchangeAvailable ) {
			pointCard.decrementFromAsteroidCount(LARGE_RED, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_PURPLE, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_RED) + pointCard.getAsteroidCount(LARGE_PURPLE)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_RED) >= EXCHANGE_RATE;
		}
		
		//generate the asteroids for this level and add them to the game board.
		ArrayList<SpaceObject> generatedAsteroids = generateAsteroidsFromPointCard ( pointCard, null );
		for ( SpaceObject currentObject : generatedAsteroids )	{
			addSpaceObjectToLists(currentObject);
		}
		
				
		//lines to add troll ships for testing.
		//asterage2State.addTrollShip( new TrollScoutShip(0) );			// add troll ship for debugging								
		//asterage2State.addTrollShip( new TrollWeaponPod(null) );		// add a loose troll weapon pod for debugging.
		//asterage2State.addTrollShip( new TrollMiningPod(0.0, 0.0) );	// add a new mining pod for debugging.	
		//asterage2State.beginMothershipSpawnCountdown();				// add troll mothership for debugging.
	} 
	
	public ArrayList<SpaceObject> generateAsteroidsFromPointCard ( AsteroidPointCard pointCard, SpaceObject parentObject ) {
		ArrayList<SpaceObject> generatedAsteroids = new ArrayList<>();
		
		//go through and create as many asteroid of each type as the score card says we must.
		for ( Asteroid.Asteroid_Type_Size currentTypeAndSize : Asteroid.Asteroid_Type_Size.values() ) {
			for ( int count = 1;	count <= pointCard.getAsteroidCount(currentTypeAndSize);	++count )
			{
				double randomXCoordinate = Math.floor( Math.random() * AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.width);
				double randomYCoordinate = Math.floor( Math.random() * AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.height);			
				Asteroid newAsteroid = (null != parentObject) ?
						new Asteroid ( parentObject, currentTypeAndSize ) :
						new Asteroid(randomXCoordinate, randomYCoordinate, currentTypeAndSize);
				generatedAsteroids.add( newAsteroid );
			} 
		} 
		
		return generatedAsteroids;
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */

} 
