/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import objectBehaviors.asterage2.PursuesPlayer;
import static controller.Asterage2Controller.Ship_System_Destruction_Target.*;
import gameEffects.asterage2.ShipExplosionEffect;
import gameEffects.asterage2.SonicDisruptorEffect;
import gameEffects.asterage2.TrollLaser;
import gameEffects.asterage2.WarpOutEffect;
import gameObjects.asterage2.Asteroid;
import static gameObjects.asterage2.Asteroid.Asteroid_Type_Size.*;
import gameObjects.asterage2.HomingMissile;
import gameObjects.asterage2.Mythicite;
import gameObjects.asterage2.PlasmaBolt;
import gameObjects.asterage2.PlayerShip;
import gameObjects.asterage2.PlayerShip.Ship_Status;
import gameObjects.asterage2.PowerUpBaseObject;
import static gameObjects.asterage2.PowerUpBaseObject.POWER_UP_SPAWN_CHANCE;
import gameObjects.asterage2.PowerUpGravityNet;
import gameObjects.asterage2.PowerUpShieldRestoration;
import gameObjects.asterage2.PowerUpSonicDisruptor;
import gameObjects.asterage2.SpaceObject;
import gameObjects.asterage2.TrollBaseShip;
import gameObjects.asterage2.TrollMiningPod;
import gameObjects.asterage2.TrollMothership;
import gameObjects.asterage2.TrollScoutShip;
import gameObjects.asterage2.TrollWeaponPod;
import gui.AsteRAGE2GameBoard;
import gui.AsteRAGE2GameBoard.PopUpMessageLabel.MessageType;
import gui.GUI;
import gui.SoundManager;
import gui.asterage2widgets.ShipPowerupStatusWidget;
import gui.input.GameKeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import objectBehaviors.asterage2.FiresTrollLaser;
import state.Asterage2State;
import state.Asterage2State.GameState;
import state.Asterage2State.PowerUpMenuOption;
import utility.DebugManager;
import utility.GameConstants;
import utility.RandomizedNumbers;
import utility.ThetaCorrector;

/**
 *
 * @author roberthood
 */
public class Asterage2Controller
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	GUI gui;
	Controller controller;
	Asterage2State asterage2State;
	
	
	public static final int WARP_OUT_COUNTDOWN_SHIP_WARP_EVENT = GameConstants.FRAMES_PER_SECOND * 2;	//at 2 seconds left.
	public static final int WARP_OUT_COUNTDOWN_LEVEL_UP_EVENT = 0;										//when countdown expired.
	
	
	public static final String PAUSE_MESSAGE = "PAUSE";					//message to show if we're paused.
	public static final String LEVEL_UP_MESSAGE = "LEVEL COMPLETE!";	//message to show once stage is complete
	public static final String EXTRA_LIFE_MESSAGE = "EXTRA LIFE!";		//message to show if extra life is awarded.
	public static final String CURRENT_LEVEL_MESSAGE = "LEVEL:  ";		//message indicating what level it is.
	public static final String GAME_OVER_MESSAGE = "Game Over. Press ENTER for new game, or DELETE/ESC to quit.";	//game over message.
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Asterage2Controller()
	{
		
	} //end constructor
	
	public void setAsterage2State ( Asterage2State passedState )
	{
		this.asterage2State = passedState;
	} //end funciton setAsterage1State
	
	public void setGUI ( GUI passedGUI )
	{
		this.gui = passedGUI;
	} //end function setGUI
	
	public void setController ( Controller passedController )
	{
		this.controller = passedController;
	} //end function setController
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void updateState()
	{
		//check the current state of the game. Perform a state update according to the game state.
		GameState currentGameState = asterage2State.getGameState();
		
		switch ( currentGameState )
		{
			case RUNNING:
				asterage2State.incrementFrameNumber();		//increment the frame number for each update, for those that track delta T.
				checkToDestroyShip();						//see if the ship has been destroyed.
				moveAndRotateObjects();						//move and rotate all objects on the game board
				regenerateShields();						//regenerate shields of all objects deploying them
				checkToFirePlasmaBolts();					//handle objects firing plasma bolts.
				checkToFireTrollLasers();					//handle trolls firing their laser wepaons.
				checkCollisions();							//check to see which objects collide with other objects.
				checkSonicDisruptorImpacts();				//check to see if any objects are under sonic disruptor fire, and resolve.
				ageObjects();								//age objects like space effects that last a finite amount of time.
				removeExpiredObjects();						//remove expired plasma bolts, etc.
				checkForGameOver();							//check if game-over conditions have been met or not.
				checkForLevelComplete();					//check to see if all objectives are complete, to advance to next stage
				advancePopUpMessageExpiration();			//advance pop-up message countdown to expiration
				checkToSpawnShip();							//see if we should spawn a new player ship.
				checkToSpawnTrollShip();					//see if we should spawn a troll ship of some kind
				checkToAdvanceTrollMothershipCountdown();	//see if there is a currently active countdown for a troll mothership, and advance the timing.
				break;
				
			case PAUSE:
				advancePopUpMessageExpiration();			//advance pop-up message countdown to expiration
				showPauseMessage();							//show pause message on screen.
				break;
				
			case GAME_OVER:
				asterage2State.incrementFrameNumber();		//increment the frame number for each update, for those that track delta T.
				moveAndRotateObjects();						//move and rotate all objects on the game board
				ageObjects();								//age objects like space effects that last a finite amount of time.
				removeExpiredObjects();						//remove expired plasma bolts, etc.
				advancePopUpMessageExpiration();			//advance pop-up message countdown to expiration
				showGameOverMessage();						//show game over message on screen.
				break;
				
			case WARPING_OUT:
				asterage2State.incrementFrameNumber();		//increment the frame number for each update, for those that track delta T.
				moveAndRotateObjects();						//move and rotate all objects on the game board
				ageObjects();								//age objects like space effects that last a finite amount of time.
				removeExpiredObjects();						//remove expired plasma bolts, etc.
				doWarpoutCountdownSequence ();				//Perform the warp-out sequence of events.
				advancePopUpMessageExpiration();			//advance pop-up message countdown to expiration
				break;
				
		} //end switch based on game state

		gui.repaintAsteRAGE2Display();				//redraw the game screen and update the hud
	} //end function updateState
	
	/**
	 * Entry point of the logic to determine how to react to key presses.
	 * @param key
	 * @param whichEvent 
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent )
	{	
		//Key up, or key down?
		switch ( whichEvent )
		{
			case UP:
				processKeyUpEvent( keyCode );
				break;
				
			case DOWN:
				processKeyDownEvent( keyCode );
				break;
				
			default:
				//do nothing
				return;
		} //end switch based on key up or key down.
	} //end function processKeyEvent
	
	public void beginNewGame()
	{
		asterage2State.initializeAsterage2State();		//initialize the state.
		generateAsteroidsForLevel();					//set up the initial game board.
		selectSoundTrackForLevel();						//play the correct soundtrack.
	} //end method beginNewGame
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private void processKeyDownEvent ( int keyCode )
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		GameState currentState = asterage2State.getGameState();
		boolean activelyAcceptingPlayerControls = ( GameState.RUNNING == currentState );
		
		
		switch ( keyCode )
		{	
			//player movement controls
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if ( activelyAcceptingPlayerControls ) { playerShip.accelerate(true);}
				break;
					
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				if ( activelyAcceptingPlayerControls ) { playerShip.decelerate(true); }
				break;
			
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				if ( activelyAcceptingPlayerControls ) { playerShip.rotateClockwise( true );  }
				break;

			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				if ( activelyAcceptingPlayerControls ) { playerShip.rotateCounterClockwise( true ); }
				break;
				
				
			//weapons controls
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_K:
			case KeyEvent.VK_J:
				if ( activelyAcceptingPlayerControls ) { firePlasmaBoltsOrSpawnShip(); }
				break;
				
			case KeyEvent.VK_I:
				if ( activelyAcceptingPlayerControls ) { fireSonicDisruptor(true); }
				break;
				
			case KeyEvent.VK_U:
				if ( activelyAcceptingPlayerControls ) { fireHomingMissiles(); }
				break;
				
				
			//power up menu controls
			case KeyEvent.VK_Y:
				if ( activelyAcceptingPlayerControls ) { cyclePowerUpMenu(Power_Up_Menu_Cycle_Option.LEFT); }
				break;
				
			case KeyEvent.VK_O:
				if ( activelyAcceptingPlayerControls ) { cyclePowerUpMenu(Power_Up_Menu_Cycle_Option.RIGHT); }
				break;
				
			case KeyEvent.VK_G:
				if ( activelyAcceptingPlayerControls ) { purchasePowerUpFromMenu(); }
				break;
				
				
			//game state controls
			case KeyEvent.VK_P:
				pauseOrUnPauseGame();
				break;
				
			case KeyEvent.VK_ENTER:
				restartGameOnGameOver();
				break;
				
			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_BACK_SPACE:
				boolean returningToMainMenu = gui.checkAsterage2AbortGameDialog();
				if ( returningToMainMenu ) { controller.switchActiveState(GameConstants.CURRENT_STATE.TITLE_SCREEN); }
				break;
		} //end switch based on key code
	} //end method processKeyDownEvent
	
	private void processKeyUpEvent ( int keyCode )
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		switch ( keyCode )
		{
			//player movement controls
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				playerShip.accelerate(false);
				break;

			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				playerShip.decelerate(false);
				break;
			
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				playerShip.rotateClockwise( false );
				break;

			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				playerShip.rotateCounterClockwise( false );
				break;
				
			
			//weapons controls
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_K:
			case KeyEvent.VK_J:
				playerShip.setPlasmaBoltFiringState(false);
				playerShip.resetPlasmaBoltCooldown();
				break;
				
			case KeyEvent.VK_I:
				fireSonicDisruptor(false);
				break;
			
		} //end switch based on key code
	} //end method processKeyUpEvent
	
	/**
	 * Check the keystroke against our list of used keys. 
	 * If the key is used, return true. Else return false.
	 * @param key
	 * @return Boolean value representing the decision of if the key is a used key. True = yes, false = no.
	 */
	@Deprecated
	private boolean checkIfKeyUsed ( int  keyCode )
	{
		switch ( keyCode )
		{
			case 'w':
			case 's':
			case 'a':
			case 'd':
			case 'k':
			case KeyEvent.VK_SPACE:
				return true;
			
			default:
				System.out.println("Key code: " + keyCode + " not used.");
				return false;
		} //end switch
	} //end function checkIfKeyUsed
	
	private void generateAsteroidsForLevel()
	{	
		//add one large white asteroid's worth of points to the point card for each level.
		//max of 78, since with the two extra starting asteroids, that makes 80, for 10 large purple asteroids.
		int asteroidPoints = asterage2State.getGameLevel() * LARGE_WHITE.generationPointValue();
		asteroidPoints = ( asteroidPoints >= (78 * LARGE_WHITE.generationPointValue()) ) ? 
						78 * LARGE_WHITE.generationPointValue() :
						asteroidPoints;
		
		AsteroidPointCard pointCard = new AsteroidPointCard();		//make a new asteroid point card, and add base asteroids.
		pointCard.addBaseStartingAsteroids();
		pointCard.creditPoints(asteroidPoints);
		
		//go through the while loop, incrementing the number of asteroids that we're going to spawn for this level.
		while ( pointCard.getRemainingPointsToSpend() >= LARGE_WHITE.generationPointValue() )
		{
			pointCard.addToAsteroidCount(LARGE_WHITE, 1);
		} //end while loop to calculate new asteroids.
		
		final int LARGE_ASTEROID_CAP = 10;
		final int EXCHANGE_RATE = 2;
		
		//if we have too many large white asteroids, switch some out for large tans.
		//exchange large whites for 1 large tan as long as we are able.
		boolean tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_WHITE) > LARGE_ASTEROID_CAP;
		boolean haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_WHITE) >= EXCHANGE_RATE;
		while( tooManyLargeAsteroids && haveExchangeAvailable )
		{
			pointCard.decrementFromAsteroidCount(LARGE_WHITE, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_TAN, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_WHITE) + pointCard.getAsteroidCount(LARGE_TAN)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_WHITE) >= EXCHANGE_RATE;
		} //compensate with large tan asteroids.
		
		//similarly, if we have too many tan asteroids, switch some out for large red ones.
		//exchange large tan asteroids for 1 large red as long as we are able.
		tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_TAN) > LARGE_ASTEROID_CAP;
		haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_TAN) >= EXCHANGE_RATE;
		while( tooManyLargeAsteroids && haveExchangeAvailable )
		{
			pointCard.decrementFromAsteroidCount(LARGE_TAN, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_RED, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_TAN) + pointCard.getAsteroidCount(LARGE_RED)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_TAN) >= EXCHANGE_RATE;
		} //compensate with large red asteroids.
		
		
		//lastly, if we have too many red asteroids, switch some out for large purple ones.
		//exchange large red asteroids for purple ones as long as we are able.
		tooManyLargeAsteroids = pointCard.getAsteroidCount(LARGE_RED) > LARGE_ASTEROID_CAP;
		haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_RED) >= EXCHANGE_RATE;
		while ( tooManyLargeAsteroids && haveExchangeAvailable )
		{
			pointCard.decrementFromAsteroidCount(LARGE_RED, EXCHANGE_RATE);
			pointCard.addToAsteroidCount(LARGE_PURPLE, 1);
			
			//recheck conditions to continue
			tooManyLargeAsteroids = (pointCard.getAsteroidCount(LARGE_RED) + pointCard.getAsteroidCount(LARGE_PURPLE)) > LARGE_ASTEROID_CAP;
			haveExchangeAvailable = pointCard.getAsteroidCount(LARGE_RED) >= EXCHANGE_RATE;
		} //end while loop to compensate with large purple asteroids.
		
		
		//generate the asteroids for this level and add them to the game board.
		ArrayList<SpaceObject> generatedAsteroids = generateAsteroidsFromPointCard ( pointCard, null );
		for ( SpaceObject currentObject : generatedAsteroids )
		{
			asterage2State.addSpaceObjectToLists(currentObject);
		} //end for loop iterating through newly generated asteroids.
		
		showCurrentLevelMessage();		//show what level we've just entered.
		
		//lines to add troll ships for testing.
		//asterage2State.addTrollShip( new TrollScoutShip(0) );			// add troll ship for debugging								
		//asterage2State.addTrollShip( new TrollWeaponPod(null) );		// add a loose troll weapon pod for debugging.
		//asterage2State.addTrollShip( new TrollMiningPod(0.0, 0.0) );	// add a new mining pod for debugging.	
		//asterage2State.beginMothershipSpawnCountdown();				// add troll mothership for debugging.
	} //end method createAsteroidsForLevelStart
	
	private ArrayList<SpaceObject> generateAsteroidsFromPointCard ( AsteroidPointCard pointCard, SpaceObject parentObject )
	{
		ArrayList<SpaceObject> generatedAsteroids = new ArrayList<>();
		
		//go through and create as many asteroid of each type as the score card says we must.
		for ( Asteroid.Asteroid_Type_Size currentTypeAndSize : Asteroid.Asteroid_Type_Size.values() )
		{
			for ( int count = 1;	count <= pointCard.getAsteroidCount(currentTypeAndSize);	++count )
			{
				double randomXCoordinate = Math.floor( Math.random() * AsteRAGE2GameBoard.boardWidth);
				double randomYCoordinate = Math.floor( Math.random() * AsteRAGE2GameBoard.boardHeight);			
				Asteroid newAsteroid = (null != parentObject) ?
						new Asteroid ( parentObject, currentTypeAndSize ) :
						new Asteroid(randomXCoordinate, randomYCoordinate, currentTypeAndSize);
				generatedAsteroids.add( newAsteroid );
			} //end for loop iterating through the count of an asteroid
		} //end for loop iterating through asteroid types.
		
		return generatedAsteroids;
	} //end method generateAsteroidsFromPointCard
	
	private void moveAndRotateObjects()
	{
		//move each object in the state. We do this to each list to avoid having to compile a list of
		//objects each time we update.
		int gameBoardWidth = AsteRAGE2GameBoard.boardWidth;
		int gameBoardHeight = AsteRAGE2GameBoard.boardHeight;
		boolean gravityNetActive = asterage2State.checkGravityNetEquipped();
		
		//move the ship
		PlayerShip playerShip = asterage2State.getPlayerShip();
		playerShip.moveAndRotate( gameBoardWidth, gameBoardHeight, gravityNetActive );
		
		//move the asteroids.
		for ( Asteroid currentAsteroid :  asterage2State.getAsteroidList() )
		{	
			currentAsteroid.moveAndRotate( gameBoardWidth, gameBoardHeight, gravityNetActive );
		} //end for loop iterating through asteroids
		
		//move plasma bolts
		for ( PlasmaBolt currentPlasmaBolt : asterage2State.getPlasmaBoltList() )
		{
			currentPlasmaBolt.moveAndRotate(gameBoardWidth, gameBoardHeight, gravityNetActive );
		} //end for loop iterating through plasmaBolts
		
		//move the trolls
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() )
		{
			//check to see if the troll ship needs a pursuit target. If so, then set it. Then move.
			if ( currentTroll instanceof PursuesPlayer ) { ((PursuesPlayer) currentTroll).setPursuitTarget(playerShip); }
			currentTroll.moveAndRotate(gameBoardWidth, gameBoardHeight, gravityNetActive);
		} //end for loop iterating through trolls
		
		//move the power ups
		for ( PowerUpBaseObject currentPowerUp: asterage2State.getPowerUpList() )
		{
			currentPowerUp.moveAndRotate(gameBoardWidth, gameBoardHeight, gravityNetActive);
		} //end for loop iterating through power ups
		
		//move the homing missiles.
		for ( HomingMissile currentHomingMissile: asterage2State.getHomingMissileList() )
		{
			currentHomingMissile.moveAndRotate(gameBoardWidth, gameBoardHeight, gravityNetActive);
		} //end for loop iterating through homing missiles.
	} //end method moveAndRotateObjects
	
	private void checkToFirePlasmaBolts()
	{
		//player ship first.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		playerShip.decrementPlasmaBoltCooldown();
		boolean playerReadyWaitingAndAble =	(true == playerShip.getPlasmaBoltFiringState()) &&
											(false == playerShip.checkPlasmaBoltsCoolingDown()) &&
											(true == playerShip.checkShipInPlay()) ; 
									
		if ( playerReadyWaitingAndAble )
		{
			//create a plasma bolt, add it to the state to keep track of it, and start the plasma bolt cooldown.
			PlasmaBolt newPlasmaBolt = PlasmaBolt.createPlayerPlasmaBolt (	playerShip.getxCoordinate(), 
																			playerShip.getyCoordinate(), 
																			playerShip.getFacingAngleDegrees() );
			playerShip.startPlasmaBoltCooldown();
			asterage2State.addPlasmaBolt( newPlasmaBolt );
			
			//also fire multishot plasma bolts
			int numberOfMultiShots = asterage2State.getMultishotLevel();
			for ( int count = 1;	count <= numberOfMultiShots;  ++count )
			{
				//calculate the angle for which we should fire this multi shot. 
				//Toggle the direction in any case (we'll come back a 2nd time for the 2nd shot in case of level 2 system.)
				int variationDegree = (Asterage2State.Multi_Shot_Direction.RIGHT == asterage2State.getMultiShotDirection() ) ?
						PlasmaBolt.MULTISHOT_ARC_VARIATION : (-1 * PlasmaBolt.MULTISHOT_ARC_VARIATION);
				int multiShotAngle = playerShip.getFacingAngleDegrees() + variationDegree; 
				PlasmaBolt multiShotPlasmaBolt = 
						PlasmaBolt.createPlayerPlasmaBolt(	playerShip.getxCoordinate(), 
															playerShip.getyCoordinate(), 
															multiShotAngle);
				asterage2State.addPlasmaBolt( multiShotPlasmaBolt );
				asterage2State.toggleMultiShotDirection();
			} //end for loop to generate multishot plasma bolts.
			
			//play the player plasma bolt sound effect
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_PLASMA_BOLT_FIRE);
		} //end if check to generate a player plasma bolt.
		
		
		//now check troll ships.
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() )
		{
			currentTroll.decrementPlasmaBoltCooldown();
			//trolls in play (and so are on this list) are always waiting, so they only need check cooldown and that
			//the player ship is actually in play -- they don't shoot at nothing.
			if ( false == currentTroll.checkPlasmaBoltsCoolingDown() )
			{
				//reset cooldown immediately, then check to see if the ship is in play before firing. 
				//We don't disrupt our firing pattern just because the player ship is not a current valid target.
				currentTroll.startPlasmaBoltCooldown();
				
				if ( true == playerShip.checkShipInPlay())
				{
					createTrollTrackingPlasmaBolt( playerShip, currentTroll );
					gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_ENEMY_PLASMA_BOLT_FIRE);	//play sound effect for enemy fire
				} //end if check for in-play player ship.	
			} //end if check for readiness to fire
		} //end for loop iterating through 
	} //end method checkToFirePlasmaBolts
	
	
	private void checkToFireTrollLasers()
	{
		//get a reference to the player ship, as we may need it later.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		//iterate through each troll ship on the game board
		//checking to see if they are equipped with a laser and ready to fire.
		for ( TrollBaseShip currentTroll : asterage2State.getTrollShipList() )
		{
			if ( currentTroll instanceof FiresTrollLaser )
			{
				//decrement the cooldown of the troll laser, and then see if the ship is ready to fire.
				FiresTrollLaser laserTroll = (FiresTrollLaser) currentTroll;
				laserTroll.decrementTrollLaserCooldown();
				
				boolean laserEquipped = laserTroll.checkTrollLaserEquipped();
				boolean laserReadyToFire = !(laserTroll.checkTrollLaserCoolingDown());
				boolean shipInPlay = playerShip.checkShipInPlay();
				
				//if we have a laser equipped, and are ready to fire at an existant ship:
				if ( laserEquipped && laserReadyToFire && shipInPlay )
				{
					//fire the weapon! Then reset the cooldown of the laser.
					createNewTrollLaserEffect( playerShip, currentTroll);
					currentTroll.resetTrollLaserCooldown();
				} //end if clause for ready to fire at an existant ship
				else if ( laserReadyToFire )
				{
					//else if we either don't have a weapon equipped, or else the player ship
					//is not in play currently, just reset the laser cooldown and consider this a missed opportunity.
					currentTroll.resetTrollLaserCooldown();
				} //end else clause to handle laser cooldown reset 
			} //end if check for if the troll is a class of ship that fires troll lasers at all.
		} //end for loop iterating through troll ships.
	} //end method checkToFireTrollLasers
	
	/**
	 * Creates a new troll laser effect.
	 * @param playerShip
	 * @param currentTroll 
	 */
	private void createNewTrollLaserEffect ( PlayerShip playerShip, TrollBaseShip currentTroll )
	{
		//create a new Troll Laser effect, then add it to the game state.
		TrollLaser newTrollLaserEffect = new TrollLaser(currentTroll, playerShip );
		newTrollLaserEffect.resetCoundownToMax();
		
		asterage2State.addSpaceEffect(newTrollLaserEffect);
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_LASER_FIRED);
		playerShip.takeDamage( TrollLaser.DAMAGE_RATING );
	} //end method fireTrollLaser
	
	/**
	 * Remove all other objects from the game board that have a status of expired.
	 */
	private void removeExpiredObjects()
	{
		asterage2State.removeExpiredPlasmaBolts();
		asterage2State.removeExpiredAsteroids();
		asterage2State.removeExpiredTrollShips(this, gui);
		asterage2State.removeExpiredPowerUps();
		asterage2State.removeExpiredEffects();
		asterage2State.removeExpiredHomingMissiles();
	} //end method removeExpiredObjects
	
	/**
	 * Master method to handle collisions during one game update.
	 */
	private void checkCollisions()
	{
		//do checking on each set of relevant collisions 
		//	1) Plasma Bolts (any kind ) with asteroids.
		//  2) Asteroids with player ship.
		//	3) Player ship and power up objects.
		//  4) Player Plasma Bolts and Troll ships
		//  5) Troll Plasma Bolts and Player ship
		//  6) Player Missiles and Troll ships
		
		checkAsteroidAndPlasmaBoltCollision();
		checkAsteroidAndPlayerShipCollision();
		checkPowerUpAndPlayerShipCollision();
		checkTrollAndPlasmaBoltCollision();
		checkPlayerAndPlasmaBoltCollision();
		checkTrollAndHomingMissileCollision();
	} //end method handleCollisons
	
	
	
	private void checkTrollAndHomingMissileCollision()
	{
		//go through each homing missile. 
		//If it is a player homing missile, check it against its target to see if a collision occurs.
		for ( HomingMissile currentHomingMissile : asterage2State.getHomingMissileList() )
		{
			SpaceObject target = currentHomingMissile.getTarget();
			
			if (	false == (HomingMissile.MissileType.PLAYER == currentHomingMissile.getMissileType()) ||
					null == target ) 
			{ 
				continue; //skip for non-player missiles or missiles that have null as target
			} 
			
			if ( false == (target instanceof TrollBaseShip) )
			{
				//should not reach here, as player homing missiles are designed only to target trolls 
				//null case is checked above.
				currentHomingMissile.setExpiredFlag(true);	//destroy missile immediately
				DebugManager.logMessage(2, "Player Missile cannot target non-troll ships. Destroying missile.");
				return;
			}	//end if check for a non-valid target.
			
			TrollBaseShip currentTroll = (TrollBaseShip) target;
			if (	false == currentHomingMissile.checkExpired() &&
					true == currentHomingMissile.checkCollision(currentTroll) )
			{
				//then a collision occured. Mark the homing missile as expired.
				//Then apply damage to the troll ship.
				currentHomingMissile.setExpiredFlag(true);
				currentTroll.takeDamage(HomingMissile.HOMING_MISSILE_DAMAGE_RATING);
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_SHIELD_IMPACT);
			} //end if check for a missile to troll collision

		} //end outer for loop iterating through homing missiles.
	} //end method checkTrollAndHomingMissileCollision
	
	
	private void checkPlayerAndPlasmaBoltCollision()
	{
		//first check to see if the player ship is in play. If not, no collision is possible.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		if ( false == playerShip.checkShipInPlay() ) { return; }
		
		//iterate through plasma bolts, checking for if they are a troll shot and cause a collision with the player
		for ( PlasmaBolt currentPlasmaBolt : asterage2State.getPlasmaBoltList() )
		{
			if (	false == currentPlasmaBolt.checkExpired() &&
					PlasmaBolt.PlasmaBoltType.ENEMY == currentPlasmaBolt.getPlasmaBoltType() &&
					true == playerShip.checkCollision(currentPlasmaBolt) )
			{
				//player ship takes some damage. Mark the plasma bolt as expended now.
				currentPlasmaBolt.setExpiredFlag(true);
				double damageAmount = PlasmaBolt.DAMAGE_RATING;
				playerShip.takeDamage( damageAmount );
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SHIELD_IMPACT);
			} //end if check for collision
		} //end for loop iterating through 
	} //end method checkPlayerAndPlasmaBoltCollision
	

	private void checkTrollAndPlasmaBoltCollision()
	{
		//iterate through plasma bolts and troll ships. If neither are expired, then check for collisions.
		//do troll ships as the outer for loop to save time if we don't have one spawned.
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() )
		{
			for ( PlasmaBolt currentPlasmaBolt : asterage2State.getPlasmaBoltList() )
			{
				if (	false == currentTroll.checkExpired() &&
						false == currentPlasmaBolt.checkExpired() &&
						PlasmaBolt.PlasmaBoltType.PLAYER == currentPlasmaBolt.getPlasmaBoltType() &&
						true == currentPlasmaBolt.checkCollision(currentTroll) )
				{
					resolveCollisionTrollPlasmaBolt( currentPlasmaBolt, currentTroll );
				} //end if check for collision between troll and player plasma bolt
			} //end for loop iterating through plasma bolts
		} //end for loop iterating through troll ships
	} //end method checkTrollAndPlasmaBoltCollision
	
	
	private void resolveCollisionTrollPlasmaBolt( PlasmaBolt plasmaBolt, TrollBaseShip currentTroll )
	{
		//If collision occurs, then apply damage to the troll ship.
		//award points for hitting the troll ship and destroying it, if appropriate.
		plasmaBolt.setExpiredFlag(true);
		currentTroll.takeDamage( PlasmaBolt.DAMAGE_RATING );
		awardPoints( currentTroll.getPointValueHit() );
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_SHIELD_IMPACT);
		
	} //end method resolveCollisionTrollPlasmaBolt
	
	private void checkAsteroidAndPlasmaBoltCollision()
	{
		//iterate through plasma bolts and asteroids. If neither are expired, then check for collision.
		//if collision occured, then handle spawning new asteroids, if appropriate.
		for ( Asteroid currentAsteroid : asterage2State.getAsteroidList() )
		{	
			for ( PlasmaBolt currentPlasmaBolt: asterage2State.getPlasmaBoltList())
			{
				if ( (false == currentAsteroid.checkExpired()) && (false == currentPlasmaBolt.checkExpired()) )
				{
					boolean collisionDetected = currentAsteroid.checkCollision( currentPlasmaBolt );
					if ( collisionDetected )
					{
						resolveCollisionAsteroidPlasmaBolt ( currentAsteroid, currentPlasmaBolt );
					} //end if check for collision of objects
				} //end if check for expired objects
			} //end for loop iterating through plasma bolts
		} //end for loop iterating through asteroids.
	} //end method checkAsteroidAndPlasmaBoltCollision
	
	/**
	 * Resolves collision between plasma bolts and asteroids.
	 * @param currentAsteroid
	 * @param currentPlasmaBolt 
	 */
	private void resolveCollisionAsteroidPlasmaBolt( Asteroid currentAsteroid, PlasmaBolt currentPlasmaBolt )
	{
		//The plasma bolt is expired.
		//The asteroid is damaged or expired, and spawns new asteroids.
		currentPlasmaBolt.setExpiredFlag(true);				
		currentAsteroid.takeDamage(PlasmaBolt.DAMAGE_RATING);		//asteroid marks itself as expired if that's true.
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_ASTEROID_IMPACT);
		
		if ( currentPlasmaBolt.getPlasmaBoltType() == PlasmaBolt.PlasmaBoltType.PLAYER)
		{
			awardPoints( currentAsteroid.getPointValue() );				//award the score for the asteroid, if the plasma bolt was the player's.
		} //end if check for point awards.
		
		//get the group of new asteroids and/or powerups.
		for ( SpaceObject objectToAdd: generateNewAsteroidsFromImpact(currentAsteroid) )
		{
			asterage2State.addSpaceObjectToLists(objectToAdd);
		} //end for loop iterating through 
		
	} //end method resolveCollisionAsteroidPlasmaBolt
	
	private void checkAsteroidAndPlayerShipCollision()
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		//if the ship is not in play, then no collision is possible. 
		if ( false == playerShip.checkShipInPlay() ) { return; }
		
		for ( Asteroid currentAsteroid : asterage2State.getAsteroidList() )
		{
			boolean impactDetected = playerShip.checkCollision(currentAsteroid);
			if ( impactDetected )
			{
				//player ship takes some damage.
				double damageAmount = Asteroid.DAMAGE_RATING;
				playerShip.takeDamage( damageAmount );
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SHIELD_IMPACT);
			} //end if check to handle asteroid impact.
		} //end for loop iterating through asteroids to check for impact.
	} //end method checkAsteroidAndPlayerShipCollision
	
	/**
	 * Generates a list of new asteroids to add to the game board after a collision.
	 * Also decides if power ups or special systems drop onto the game board.
	 * @param parentAsteroid 
	 */
	private ArrayList<SpaceObject> generateNewAsteroidsFromImpact( Asteroid parentAsteroid )
	{
		ArrayList<SpaceObject> generatedObjectList = new ArrayList<>();
		//if ( false == parentAsteroid.checkExpired() ) { return generatedObjectList; }	//Only generate asteroids if the parent is expired.
		
		//else, we generate some asteroids
		AsteroidPointCard pointCard = new AsteroidPointCard();
		Asteroid.Asteroid_Type_Size currentAsteroidSize = parentAsteroid.getAsteroidSize();
		
		if ( false == parentAsteroid.checkExpired() )
		{
			//spawn a small asteroid of matching color if the parent was a durability grade above white.
			switch ( parentAsteroid.getAsteroidSize() )
			{
				case LARGE_TAN:
				case MEDIUM_TAN:
					pointCard.addToAsteroidCount( SMALL_TAN, 1 );
					break;
					
				case LARGE_RED:
				case MEDIUM_RED:
					pointCard.addToAsteroidCount( SMALL_RED, 1 );
					break;
					
				case LARGE_PURPLE:
				case MEDIUM_PURPLE:
					pointCard.addToAsteroidCount(SMALL_PURPLE, 1);
					break;
					
					
			} //end switch based on asteroid size and type
		} //end if clause for a non-destroyed asteroid.
		else
		{
			//generate a full complement of asteroids.
			switch ( parentAsteroid.getAsteroidSize() )
			{
				case LARGE_WHITE:
					//2 medium + 30% chance of +1 medium. 20% chance each of two small asteroid spawns.
					pointCard.addToAsteroidCount(MEDIUM_WHITE, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(MEDIUM_WHITE, 1); }
					for ( int count = 1; count <= 2;  ++count )
					{
						if (RandomizedNumbers.random100() < 20 ) { pointCard.addToAsteroidCount(SMALL_WHITE, 1); }
					}
					break;

				case MEDIUM_WHITE:
					//2 small + 30% chance of +1 small asteroid.
					pointCard.addToAsteroidCount(SMALL_WHITE, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(SMALL_WHITE, 1); }
					break;

				case LARGE_TAN:
					//2 medium + 30% chance of +1 medium. 20% chance each of two small asteroid spawns.
					pointCard.addToAsteroidCount(MEDIUM_TAN, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(MEDIUM_TAN, 1); }
					for ( int count = 1; count <= 2;  ++count )
					{
						if (RandomizedNumbers.random100() < 20 ) { pointCard.addToAsteroidCount(SMALL_TAN, 1); }
					}
					break;

				case MEDIUM_TAN:
					//2 small + 30% chance of +1 small asteroid.
					pointCard.addToAsteroidCount(SMALL_TAN, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(SMALL_TAN, 1); }
					break;

				case LARGE_RED:
					//2 medium + 30% chance of +1 medium. 20% chance each of two small asteroid spawns.
					pointCard.addToAsteroidCount(MEDIUM_RED, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(MEDIUM_RED, 1); }
					for ( int count = 1; count <= 2;  ++count )
					{
						if (RandomizedNumbers.random100() < 20 ) { pointCard.addToAsteroidCount(SMALL_RED, 1); }
					}
					break;

				case MEDIUM_RED:
					//2 small + 30% chance of +1 small asteroid.
					pointCard.addToAsteroidCount(SMALL_RED, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(SMALL_RED, 1); }
					break;
					
				case LARGE_PURPLE:
					//2 medium + 30% chance of +1 medium. 20% chance each of two small asteroid spawns.
					pointCard.addToAsteroidCount(MEDIUM_PURPLE, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(MEDIUM_PURPLE, 1); }
					for ( int count = 1; count <= 2;  ++count )
					{
						if (RandomizedNumbers.random100() < 20 ) { pointCard.addToAsteroidCount(SMALL_PURPLE, 1); }
					}
					break;
					
					
				case MEDIUM_PURPLE:
					//2 small + 30% chance of +1 small asteroid.
					pointCard.addToAsteroidCount(SMALL_PURPLE, 2);
					if (RandomizedNumbers.random100() < 30 ) { pointCard.addToAsteroidCount(SMALL_PURPLE, 1); }
					break;

				default:
					//no added asteroids.
					break;
			} //end switch based on asteroid size
		} //end else clause for asteroids which are actually destroyed.
		
		//spawn the asteroids.
		generatedObjectList.addAll( generateAsteroidsFromPointCard(pointCard, parentAsteroid) );
		
		//random chance of PowerUp spawning from asteroids.
		if ( checkPowerUpSpawnChance() )
		{
			generatedObjectList.add( spawnPowerUp(parentAsteroid.getxCoordinate(), parentAsteroid.getyCoordinate()) );	
		} //end if check for spawning a randomized power up.
		
		//also a random chance of spawning a troll mining pod from asteroids.
		if ( checkTrollMiningPodSpawnChance() )
		{
			TrollMiningPod newTrollMiningPod = new TrollMiningPod(parentAsteroid.getxCoordinate(), parentAsteroid.getyCoordinate());
			generatedObjectList.add( newTrollMiningPod );
		} //end if check to make a troll mining pod.
		
		return generatedObjectList;
	} //end method generateNewAsteroids
	
	/**
	 * Determine if a troll mining pod should appear.
	 * @return 
	 */
	private boolean checkTrollMiningPodSpawnChance()
	{
		//The following conditions must hold true to spawn a troll mining pod:
		//	1) There must be no mining pod in play.
		//	2) There must be no troll mothership in play.
		//	3) There must not be a troll mothership about to spawn ( in the midst of countdown ).
		//	4) No more than one troll mothership per 5 levels
		//	5) Random number generator must generate a number below the target chance. 165 in 10000
		//	6) Game level is at/over 30.

		int currentGameLevel = asterage2State.getGameLevel();
		if (	currentGameLevel >= TrollMothership.TROLL_MINING_SHIP_LEVEL_SPAWN_THRESHOLD &&
				false == asterage2State.getSpawnedTrollMothershipRecently() &&
				false == asterage2State.checkMothershipSpawnCountdownActive()	&&
				false == asterage2State.checkMothershipOrTrollMiningPodInPlay()	&&
				TrollMothership.TROLL_MINING_SHIP_RANDOM_SPAWN_THRESHOLD >= RandomizedNumbers.random10000() )
		{
			return true;
		} //end if check for spawning a troll mining pod.
		
		//else no.
		return false;
	} //end method checkTrollMiningPodSpawnChance
	
	private void checkToAdvanceTrollMothershipCountdown()
	{
		boolean timeToSpawnMothership = asterage2State.advanceTrollMothershipSpawnCountdown();		//advance the countdown, if one is active.
		if ( timeToSpawnMothership )
		{
			spawnTrollMothership();
		} //end if check for signal to
	} //end method checkToAdvanceTrollMothershipCountdown
	
	/**
	 * Do a randomized check to see if a power up will drop.
	 * Limit drops to 2 per level for now.
	 * @param asterage2State
	 * @return 
	 */
	private boolean checkPowerUpSpawnChance( )
	{
		//first check to see if we have drops left to perform.
		
		if ( false == asterage2State.checkSpecialItemDropsLeftThisLevel() ) { return false; }		//no drops left -> don't bother
		
		int randomizedRoll = RandomizedNumbers.random100();
		boolean dropVerdict = ( randomizedRoll < POWER_UP_SPAWN_CHANCE );
		
		if ( true == dropVerdict )
		{
			asterage2State.incrementSpecialItemDropCounter();
			return true;
		}
		else
		{
			return false;
		}
		
	} //end method checkPowerUpSpawnChance
	
	/**
	 * We know a power up will drop. Decide which one.
	 * Small chance of a ship system dropping. Else it will be mythicite.
	 * @param asterage2State
	 * @return 
	 */
	private PowerUpBaseObject spawnPowerUp( double parentXCoordinate, double parentYCoordinate )
	{
		int randomizedChance = RandomizedNumbers.random100();
		
		if ( randomizedChance < PowerUpBaseObject.POWER_UP_SPECIAL_SYSTEM_CHANCE)
		{
			//return a ship system that we don't have already ( or else mythicite if we do.)
			return spawnShipSystem( parentXCoordinate, parentYCoordinate );
		} //end if check for a ship system.
		
		return new Mythicite( parentXCoordinate, parentYCoordinate);
	} //end method spawnPowerUp
	
	
	private PowerUpBaseObject spawnShipSystem( double parentXCoordinate, double parentYCoordinate )
	{
		boolean alreadyHaveGravityNet = asterage2State.checkGravityNetEquipped();
		boolean alreadyHaveSonicDisruptor = asterage2State.checkSonicDisruptorEquipped();
		
		int randomizedChance = RandomizedNumbers.random100();
		
		//equal chance of each system/bonus. If we already have it, spawn mythicite instead.
		if ( randomizedChance < 33 )
		{
			return new PowerUpShieldRestoration(parentXCoordinate, parentYCoordinate);
		} //end if chance for gravity net
		else if ( (randomizedChance < 66) && (false == alreadyHaveGravityNet) )
		{
			return new PowerUpGravityNet(parentXCoordinate, parentYCoordinate);
		} //end if chance for gravity net
		else if ( (randomizedChance < 100) && (false == alreadyHaveSonicDisruptor) )
		{
			return new PowerUpSonicDisruptor(parentXCoordinate, parentYCoordinate);
		} //end if chance for sonic disruptor
		
		//failing all else, spawn mythicite.
		return new Mythicite( parentXCoordinate, parentYCoordinate);
		
	} //end method spawnShipSystem
	
	private void checkPowerUpAndPlayerShipCollision()
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		//if the ship is not in play, then no collision is possible. 
		if ( false == playerShip.checkShipInPlay() ) { return; }
		
		for ( PowerUpBaseObject currentPowerUp : asterage2State.getPowerUpList() )
		{
			//check to see if the power up has expired. If so, do not pick up.
			if ( currentPowerUp.checkExpired() ) { continue; }
			
			//check to see if the power up has been picked up by the player.
			boolean pickupDetected = playerShip.checkCollision(currentPowerUp);
			if ( pickupDetected )
			{
				//have the power up modify the state in whatever what it does.
				currentPowerUp.handlePickup( asterage2State );
				
				//play the appropriate sound for the pickup:
				if ( currentPowerUp instanceof Mythicite ) 
				{ 
					gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_MYTHICITE_PICKUP); 
				}
				else 
				{ 
					gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_SPECIAL_POWERUP_PICKUP);
				}
			} //end if check to handle power up pickup.
		} //end for loop iterating through powerups to check for impact.
	} //end method checkPowerUpAndPlayerShipCollision
	
	
	private void regenerateShields()
	{
		//regenerate shields of the player ship and troll ships that have them.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		playerShip.regenerateShields();
		
		for ( TrollBaseShip currentTroll : asterage2State.getTrollShipList() )
		{
			currentTroll.regenerateShields();
		} //end for loop iterating through trolls
	} //end method regenerateShields
	
	
	private void createTrollTrackingPlasmaBolt ( PlayerShip playerShip, TrollBaseShip trollShip )
	{
		double trollX = trollShip.getxCoordinate();
		double trollY = trollShip.getyCoordinate();
		
		//now, go through frame iterations, calculating out where the ship is going to be, and 
		//seeing if a plasma bolt fired towards that location will cause a collision
		//if so, then fire at that angle. If not, then either go on to the next iteration, or quit when
		//we've checked the plasma bolt max range for troll weapons
		
		//create a target player ship at which we will shoot.
		PlayerShip fakePlayerShip = PlayerShip.makeNewPlayerShip(playerShip.getxCoordinate() , playerShip.getyCoordinate());
		fakePlayerShip.setMovementAngleDegrees( playerShip.getMovementAngleDegrees() );
		fakePlayerShip.setMovementVelocity( playerShip.getMovementVelocity() );
		fakePlayerShip.setShipStatus(Ship_Status.IN_PLAY);
		int firingAngle = 0;	//initialize the firing angle at which the troll will fire a plasma bolt.
		int numberOfIterations = (int) Math.floor(PlasmaBolt.MAX_TROLL_PLASMA_BOLT_RANGE / PlasmaBolt.MAX_PLASMA_BOLT_VELOCITY) + 1;
		for ( int currentIteration = 1;   currentIteration <= numberOfIterations;  ++currentIteration)
		{
			//move the fake ship one step, according to it's last known velocity.
			fakePlayerShip.moveAndRotate(AsteRAGE2GameBoard.boardWidth, AsteRAGE2GameBoard.boardHeight, asterage2State.checkGravityNetEquipped());
	
			//calculate the firing angle from the troll to this proposed location and create a plasma bolt to represent the shot.
			double deltaX = fakePlayerShip.getxCoordinate() - trollX;
			double deltaY = fakePlayerShip.getyCoordinate() - trollY;
			firingAngle = (int) Math.floor( Math.toDegrees( Math.atan2(deltaX, -1 * deltaY)));  
			PlasmaBolt proposedBolt = PlasmaBolt.createTrollPlasmaBolt(trollX, trollY, firingAngle);
			
			for (int moveIterations = 1;  moveIterations <= currentIteration ;  ++moveIterations )
			{
				proposedBolt.moveAndRotate(AsteRAGE2GameBoard.boardWidth, AsteRAGE2GameBoard.boardHeight, asterage2State.checkGravityNetEquipped());
			} //end for loop moving the plasma bolt along to its destination.
			
			if ( true == fakePlayerShip.checkCollision(proposedBolt) )
			{
				//if they collide, then we've found the angle to fire at. Break out of our loop.
				//If not, then we keep going
				break;
			} //end if check for a collision

		} //end while loop to search for optimal firing angle
		
		//use the firing angle we've calculated to launch troll plasma bolts
		//we need to know the multishot system level of the troll ship to properly generate the number of bolts
		//generate 1 center bolt, then 2 side bolts for every level of the multishot system.
		int trollMultishotLevel = trollShip.getMultiShotLevel();
		PlasmaBolt trollPlasmaBolt = PlasmaBolt.createTrollPlasmaBolt(trollX, trollY, firingAngle);
		asterage2State.addPlasmaBolt(trollPlasmaBolt);
		
		for ( int extraBoltSet = 1;    extraBoltSet <= trollMultishotLevel;    ++extraBoltSet )
		{
			int clockwiseAngle = firingAngle + ( PlasmaBolt.MULTISHOT_ARC_VARIATION * extraBoltSet );
			int counterClockwiseAngle = firingAngle - ( PlasmaBolt.MULTISHOT_ARC_VARIATION * extraBoltSet );
			PlasmaBolt clockwiseBolt = PlasmaBolt.createTrollPlasmaBolt(trollX, trollY, clockwiseAngle);
			PlasmaBolt counterClockwiseBolt = PlasmaBolt.createTrollPlasmaBolt(trollX, trollY, counterClockwiseAngle);
			asterage2State.addPlasmaBolt(clockwiseBolt);
			asterage2State.addPlasmaBolt(counterClockwiseBolt);
		} //end for loop to generate extra plasma bolts.
		
	} //end method createTrollTrackingPlasmaBolt
	
	private void createTrollDirectionalPlasmaBolt ( PlayerShip playerShip, TrollBaseShip trollShip )
	{
		int playerX = playerShip.getxCoordinateAsInt();
		int playerY = playerShip.getyCoordinateAsInt();
		int trollX = trollShip.getxCoordinateAsInt();
		int trollY = trollShip.getyCoordinateAsInt();
		
		int deltaX = playerX - trollX;
		int deltaY = playerY - trollY;
		
		int firingAngle = (int) Math.floor( Math.toDegrees( Math.atan2(deltaX, -1 * deltaY))); 
		
		//create the bolt and add it to the game's list of objects.
		PlasmaBolt trollPlasmaBolt = PlasmaBolt.createTrollPlasmaBolt(trollX, trollY, firingAngle);
		asterage2State.addPlasmaBolt(trollPlasmaBolt);
	} //end method createTrollTrackingPlasmaBolt
	
	
	
	private void cyclePowerUpMenu(Power_Up_Menu_Cycle_Option whichWayToCycle)
	{
		//get the current option, decide whether to go forward or back in the menu, and then save the new option.
		PowerUpMenuOption currentOption = asterage2State.getCurrentSelectedPowerUpMenuOption();
		PowerUpMenuOption nextOption = ( Power_Up_Menu_Cycle_Option.LEFT == whichWayToCycle )?
						currentOption.getPrevious() : currentOption.getNext();
		asterage2State.setCurrentSelectedPowerUpMenuOption(nextOption);
		
	} //end method cyclePowerUpMenu 
	
	/**
	 * Check to see if the game may be paused or unpaused, given the current state.
	 * If so, then either pause or unpause the game.
	 */
	private void pauseOrUnPauseGame()
	{
		//get the current game state.
		GameState currentGameState = asterage2State.getGameState();
		if ( GameState.RUNNING == currentGameState )
		{
			asterage2State.setGameState(GameState.PAUSE);
		} //end if check for game already running
		else if ( GameState.PAUSE == currentGameState )
		{
			asterage2State.setGameState(GameState.RUNNING);
		} //end if check for game that is already paused
	} //end method pauseOrUnPauseGame
	
	/**
	 * If the ship is not spawned and has an extra life, spawn it.
	 * If the ship is already spawned, fire a plasma bolt.
	 */
	private void firePlasmaBoltsOrSpawnShip()
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		GameState currentGameState = asterage2State.getGameState();
		
		if ( (true == playerShip.checkShipInPlay()) && (GameState.RUNNING == currentGameState) )
		{
			playerShip.setPlasmaBoltFiringState( true );
		} //end if check to handle firing plasma bolts
		else 
		{
			//set the flag to request spawning a ship in the asterage 2 state.
			asterage2State.setRequestToSpawnShipFlag(true);
		} //end else block to request spawning a ship
	} //end method firePlasmaBoltsOrSpawnShip
	
	
	/**
	 * Examines game state and flags to see if spawning a ship should be performed.
	 */
	private void checkToSpawnShip()
	{
		boolean requestToSpawnFlag = asterage2State.checkRequestToSpawnShipFlag();
		if ( false == requestToSpawnFlag ) { return; }		//nothing to do
		asterage2State.setRequestToSpawnShipFlag(false);	//set the request flag to false, since we've seen it. 
		
		//check the game state, to see if we're in a state to spawn a new ship.
		//also check the number of lives remaining
		GameState currentGameState = asterage2State.getGameState();
		int remainingShips = asterage2State.getRemainingShips();
		PlayerShip playerShip = asterage2State.getPlayerShip();
		boolean shipInPlay = playerShip.checkShipInPlay();
		boolean shipDestroyed = playerShip.checkShipIsDestroyed();
		boolean shipExplosionOnBoard = asterage2State.checkShipExplosionEffectOnBoard();	//see if a ship is exploding. I.e. Do not insta-spawn after death.
		if ( (remainingShips <= 0 && shipDestroyed) || (GameState.RUNNING != currentGameState) || shipInPlay || shipExplosionOnBoard) { return; } //no lives left or not in state to spawn.
		
		//do spawn the ship. Decrement the number of lives remaining only if the ship was previously destroyed.
		//restore shields only if we were previously destroyed.
		boolean shipWasDestroyed = playerShip.checkShipIsDestroyed();
		if ( shipWasDestroyed ) 
		{ 
			asterage2State.setRemainingShips(remainingShips - 1);	//cost is 1 ship life.
			playerShip.resetShipStats( true );						//respawn ship at center, at full health
		}
		else
		{
			playerShip.resetShipStats( false );						//respawn ship at center, do not restore shields 
		}
		
		playerShip.setShipStatus(PlayerShip.Ship_Status.IN_PLAY);
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SPAWNS);
		
	} //end method checkToSpawnShip
	
	private void checkToSpawnTrollShip()
	{
		//*********** TROLL SCOUTS ****************
		//*****************************************
		
		//decrement the troll scout spawn counter by 1, not going below 0.
		asterage2State.decrementTrollScoutCountdown();
		
		if ( true == asterage2State.checkTimeToSpawnTrollScout() )
		{
			asterage2State.resetTrollScoutCountdown();
			int trollUpgradeLevel = calculateEnemyUpgradePoints( asterage2State.getPlayerShip() );
			asterage2State.addTrollShip( new TrollScoutShip(trollUpgradeLevel) );
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_APPEARS);
		} //end if check for generating a new troll scout
	} //end method checkToSpawnTrollShip
	
	private void spawnTrollMothership()
	{
		//get the number of upgrade points we have to spend.
		int trollUpgradeLevel = calculateEnemyUpgradePoints( asterage2State.getPlayerShip() );
		
		//spawn the new mothership. Then, query it for any weapons pods that it created; we must add those too.
		TrollMothership newTrollMothership = new TrollMothership(trollUpgradeLevel);
		asterage2State.addTrollShip(newTrollMothership);
		
		List<TrollWeaponPod> newPodList = newTrollMothership.getAttachedWeaponPods();
		for ( TrollWeaponPod currentPod : newPodList )
		{
			//check if the current pod is null. If not null, add it to the list.
			if ( null != currentPod )
			{
				asterage2State.addTrollShip( currentPod );
			} //end if check for null.
		} //end for loop iterating through attached weapons pods.
		
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_MOTHERSHIP_APPEARS);
	} //end method spawnTrollMothership
	
	/**
	 * Helper method, determines how many upgrade points a troll ship is afforded, depending on the upgrade
	 * level of the player ship.
	 * @param playerShip
	 * @return 
	 */
	private int calculateEnemyUpgradePoints( PlayerShip playerShip )
	{
		int calculatedPoints = 0;		//initialize return value
		
		//now tabulate how many points should be given to troll ships, based on how many upgrade systems the
		//player ship has.
		calculatedPoints += playerShip.getShieldGeneratorLevel();					// one point for each shield upgrade
		calculatedPoints += playerShip.getMultiShotLevel();							// one point for each multishot upgrade
		if ( playerShip.checkGravityNetEquipped() ) { calculatedPoints += 1; }		//one point for gravity net
		if ( playerShip.checkSonicDisruptorEquipped())	{ calculatedPoints += 1;}	//one point for sonic disruptor
		
		return calculatedPoints;
	} //end method calculateEnemyUpgradePoints
	
	/**
	 * Examines game state and flags to see if the player ship has been destroyed.
	 */
	private void checkToDestroyShip()
	{
		//If the player ship's shields are at 0 or less, then set the ship state to destroyed.
		//only destroy the ship if it was in play to start with.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		double shieldsRemaining = playerShip.getShieldStrength();
		boolean shipInPlay = playerShip.checkShipInPlay();
		if ( shipInPlay && (0.0 >= shieldsRemaining) )
		{
			playerShip.setShipStatus(Ship_Status.EXPIRED);		//player ship is destroyed
			degradeRandomShipSystem();							//player loses one random system.
			spawnPlayerExplosion();								//display an explosion at the site of the player's death
			
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_DESTROYED);
			
			//halt anything to do with the sonic disruptor firing.
			gui.haltSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SONIC_DISRUPTOR_ACTIVE);	//stop playing sonic disruptor sound
			playerShip.resetSonicDisruptorCooldown();
			playerShip.setFiringSonicDisruptor(false);
		} //end if check to destroy the ship
	} //end method checkToDestroyShip
	
	
	private void spawnPlayerExplosion()
	{
		//get the location of the explosion to display. Create the explosion effect, then add it to our lists.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		int explosionXCoordinate = playerShip.getxCoordinateAsInt();
		int explosionYCoordinate = playerShip.getyCoordinateAsInt();
		
		ShipExplosionEffect shipExplosionEffect = new ShipExplosionEffect (	explosionXCoordinate, 
																			explosionYCoordinate, 
																			ShipExplosionEffect.ExplosionType.PLAYER,
																			playerShip.getSpatialRadius() * 3 );
		asterage2State.addSpaceEffect ( shipExplosionEffect );
		
	} //end method spawnPlayerExplosion
	
	/**
	 * One randomly selected ship system that is above base level is destroyed when the ship is destroyed.
	 */
	private void degradeRandomShipSystem()
	{
		//first take stock of the ship systems that could potentially degrade.
		ArrayList<Ship_System_Destruction_Target> candidateSystems = new ArrayList<>();
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		if ( playerShip.getDecelerationLevel() > 0 ) { candidateSystems.add(DECELERATION); }
		if ( playerShip.getShieldGeneratorLevel() > 0 ) { candidateSystems.add(SHIELD_GENERATOR); }
		if ( playerShip.checkGravityNetEquipped() ) { candidateSystems.add(GRAVITY_NET); }
		
		if ( playerShip.getMultiShotLevel() > 0 ) { candidateSystems.add(MULTISHOT); }
		if ( playerShip.getHomingMissileLevel() > 0 ) { candidateSystems.add(HOMING_MISSILES); }
		if ( playerShip.checkSonicDisruptorEquipped() ) { candidateSystems.add(SONIC_DISRUPTOR); }

		if ( asterage2State.getPowerUpPoints() > 0 ) { candidateSystems.add(MYTHICITE); }
		
		//if there are no ship systems to destroy, then return. We're done.
		int numberOfCandidates = candidateSystems.size();
		if ( 0 >= numberOfCandidates ) { return; }
		
		//choose one of the systems at random and degrade its level.
		int randomSelection = (int) Math.floor( Math.random() * numberOfCandidates );
		Ship_System_Destruction_Target target = candidateSystems.get(randomSelection);
		
		//destroy/degrade the system.
		switch ( target )
		{
			case DECELERATION:
				playerShip.setDecelerationLevel(0);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.DECELERATOR);
				break;
				
			case SHIELD_GENERATOR:
				playerShip.setShieldGeneratorLevel(0);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.SHIELD_GENERATOR);
				break;
			
			case GRAVITY_NET:
				playerShip.setGravityNetEquipped(false);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.GRAVITY_NET);
				break;
				
			case MULTISHOT:
				playerShip.setMultiShotLevel(0);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.MULTI_SHOT);
				break;
				
			case HOMING_MISSILES:
				playerShip.setHomingMissileLevel(0);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.HOMING_MISSILE);
				break;
			
			case SONIC_DISRUPTOR:
				playerShip.setSonicDisruptorEquipped(false);
				gui.addAsterage2HUDExplosion(ShipPowerupStatusWidget.SystemExplosionLocations.SONIC_DISRUPTOR);
				break;
				
			case MYTHICITE:
				asterage2State.setPowerUpPoints(0);
				gui.addPowerUpPointsHUDExplosion();
				break;
		} //end switch based on which system is to be degraded.
		
		
	} //end method degradeRandomShipSystem
	
	/**
	 * Turn on or turn off the sonic disruptor.
	 * @param firingDisruptorFlag 
	 */
	private void fireSonicDisruptor( boolean firingDisruptorFlag )
	{
		//check to see if the sonic disruptor is equipped or not. 
		//If so, it may be fired.
		DebugManager.logMessage(6, "Sonic disruptor: Equipped: " + asterage2State.checkSonicDisruptorEquipped() +
					"  Firing: " + firingDisruptorFlag );
		if ( false == asterage2State.checkSonicDisruptorEquipped() ) { return; }
		
		PlayerShip playerShip = asterage2State.getPlayerShip();	
		boolean shipInPlay = playerShip.checkShipInPlay();
		
		//start or stop the sound effect for the player sonic disruptor, as appropriate.
		if ( firingDisruptorFlag && shipInPlay )
		{
			playerShip.setFiringSonicDisruptor(true);
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SONIC_DISRUPTOR_ACTIVE);
		}
		else
		{
			playerShip.resetSonicDisruptorCooldown();			//if the flag is false (disruptor is turning off, then resest the cooldown until the next pulse.
			gui.haltSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SONIC_DISRUPTOR_ACTIVE);
			playerShip.setFiringSonicDisruptor(false);
		}
		
						
	} //end method fireSonicDisruptor
	
	/**
	 * Check to see if firing homing missiles can be fired by the player right now.
	 */
	private void fireHomingMissiles()
	{
		//player checks to shoot homing missiles first.
		//get the current level of the homing missile system of the player ship, and get the number of player missiles on screen.
		PlayerShip playerShip = asterage2State.getPlayerShip();
		int homingMissileSystem = asterage2State.getHomingMissileLevel();
		int playerHomingMissilesOnScreen = asterage2State.getCountPlayerHomingMissiles(); 
		boolean playerCanFireHomingMissiles = ( homingMissileSystem > playerHomingMissilesOnScreen ) && playerShip.checkShipInPlay();
		
		if ( false == playerCanFireHomingMissiles ) { return; }		//do not fire
		
		//select a troll target to attack
		TrollBaseShip trollTarget = selectTrollTarget();
		
		//create a new player missile and add it to the lists.
		HomingMissile newMissile = new HomingMissile( playerShip, HomingMissile.MissileType.PLAYER, trollTarget );
		asterage2State.addHomingMissile(newMissile);
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_HOMING_MISSILES_FIRED);
		
	} //end method fireHomingMissiles
	
	/**
	 * Surveys the game board and finds the troll with firing angle closest to the direction in which
	 * the player ship is pointing, if any exist on the board. If not, returns null.
	 * @return Troll Ship closest in angle to direction of Trololo facing angle, or null if none.
	 */
	private TrollBaseShip selectTrollTarget()
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		TrollBaseShip targettedShip = null;		//return value, initially null.
		int currentAngleToBeat = 360;			//current angle to beat in comparison.
		
		//iterate through the troll ships in our list, picking out the one with the smallest difference in angle.
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() )
		{
			double deltaX = currentTroll.getxCoordinate() - playerShip.getxCoordinate() ;
			double deltaY = currentTroll.getyCoordinate() - playerShip.getyCoordinate();
			int trollAngle = ThetaCorrector.correctThetaRange(90 + (int) Math.floor(Math.toDegrees(Math.atan2( deltaY , deltaX ))));
			int angleDifference = calculateAngleDifference( trollAngle, playerShip.getFacingAngleDegrees() );
			
			DebugManager.logMessage(6, "Troll at ( " + currentTroll.getxCoordinateAsInt() + " , " + currentTroll.getyCoordinateAsInt()  +
						" ) gives angle: " + angleDifference + " \t Player Angle: " + playerShip.getFacingAngleDegrees() +
							" troll Angle: " + trollAngle);
			
			if ( angleDifference < currentAngleToBeat )
			{
				//remember this angle as the current low record and remember which ship gave it.
				currentAngleToBeat = angleDifference;
				targettedShip = currentTroll;
			} //end if check to see if the angle difference is lowest yet
		} //end for loop iterating through troll ships.
		
		return targettedShip;		//return whichever ship was selected, if any.
	} //end method selectTrollTarget
	
	private int calculateAngleDifference ( int firstAngle, int secondAngle )
	{
		//subtract the smaller number from the larger number, whichever that might be.
		int rawDistance = ( firstAngle > secondAngle) ?
					(firstAngle - secondAngle) : (secondAngle - firstAngle);
		
		//next if that value is larger than 180, then the true distance is 360 - that value.
		return  (rawDistance > 180 ) ?
				(360 - rawDistance ) : rawDistance;
		
	} //end method calculateAngleDifference
	
	/**
	 * Try to purchase the selected power-up from the menu
	 */
	private void purchasePowerUpFromMenu()
	{
		//first, determine which power up was selected and how much it costs. 
		//Also find out how many points the player has to spend, and weather or not the system is at max upgrade level.
		PowerUpMenuOption selectedPowerUp = asterage2State.getCurrentSelectedPowerUpMenuOption();
		if ( null == selectedPowerUp) { return; }  // cannot do anything with no power up.
		int powerUpCost = selectedPowerUp.getCost();
		int pointsAvailableToSpend = asterage2State.getPowerUpPoints();
		boolean systemIsAtMaxLevel = asterage2State.checkSystemAtMaxLevel( selectedPowerUp );
		
		//if we don't have enough points, or the system is already at max upgrade level, then deny the purchase.
		if (	(powerUpCost > pointsAvailableToSpend)  || 
				systemIsAtMaxLevel)
		{
			DebugManager.logMessage(5 , "Not enough points to purchase upgrade, or system at max level: " + selectedPowerUp);
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_PURCHASE_DENIED);
			return;
		} //end if check for insufficient points
		
		//if we've reached this point, allow the purchase. 
		//deduct the points required for the purchase, and upgrade the system/award point value to score
		if ( selectedPowerUp == PowerUpMenuOption.EXTRA_POINTS )
		{
			awardPoints( PowerUpMenuOption.getExtraPointsPowerUpValue() );
		}
		else
		{
			asterage2State.upgradeSystem ( selectedPowerUp );
		}
		int pointsRemaining = pointsAvailableToSpend - powerUpCost;
		asterage2State.setPowerUpPoints(pointsRemaining);
		gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_PURCHASE_ACCEPTED);
	} //end method purchasePowerUpFromMenu
	
	
	private void checkSonicDisruptorImpacts()
	{
		checkPlayerSonicDisruptorImpacts();		//handle damage to asteroids and enemies by player
	} //end method checkSonicDisruptorImpacts
	
	private void checkPlayerSonicDisruptorImpacts()
	{
		//check to see if the sonic disruptor has finished its cooldown or not.
		//if so, then check for impacts. If not, then 
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		if ( false == playerShip.checkFiringSonicDisruptor() ) { return; }	//no calculations/interactions if not firing.
		
		if ( playerShip.checkSonicDisruptorCoolingDown() )
		{
			playerShip.decrementSonicDisruptorCooldown();
			return;
		} //end if check for sonic disruptor cooling down.
		
		//else, it's time for a sonic disruptor pulse
		//get the player's sonic disruptor
		SonicDisruptorEffect playersDisruptor = playerShip.getSonicDisruptor();		
		ArrayList<SpaceObject> newGeneratedObjects = new ArrayList<>();		//list to hold new asteroids and powerups created by pulse
		
		//first, go through the list of asteroids and check whether impacts have occured.
		for ( Asteroid currentAsteroid: asterage2State.getAsteroidList() )
		{
			boolean impactDetected = playersDisruptor.checkSonicDisruptorImpact(currentAsteroid);
			if (impactDetected)
			{
				//apply damage to the asteroid, award points for the hit, and generate any asteroids resulting.
				currentAsteroid.takeDamage( SonicDisruptorEffect.DISRUPTOR_PULSE_DAMAGE );
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_ASTEROID_IMPACT);
				awardPoints( currentAsteroid.getPointValue() );				
				newGeneratedObjects.addAll( generateNewAsteroidsFromImpact(currentAsteroid) );
			} //end if check for positive impact.
		} //end for loop to iterate through list of asteroids for impact.
		
		//now, add the newly generated asteroids to the list of objects, so they won't be affected by the pulse that created them.
		for ( SpaceObject newObject: newGeneratedObjects )
		{
			asterage2State.addSpaceObjectToLists(newObject);
		} //end for loop iterating through new asteroids to add.
		
		//now check impacts against troll ships.
		for ( TrollBaseShip trollShip : asterage2State.getTrollShipList() )
		{
			boolean impactDetected = playersDisruptor.checkSonicDisruptorImpact(trollShip);
			if (impactDetected)
			{
				//troll takes damage. Points are awarded.
				trollShip.takeDamage(SonicDisruptorEffect.DISRUPTOR_PULSE_DAMAGE);
				awardPoints( trollShip.getPointValueHit() );
				gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_TROLL_SHIELD_IMPACT);
			} //end if check for positive impact
		} //end for loop iterating through 
		
		//reset the cooldown after checking for impacts.
		playerShip.resetSonicDisruptorCooldown();
		
	} //end method handlePlayerSonicDisruptorImpacts
	
	
	private void ageObjects()
	{
		//age objects that have finite lifespans. E.g. space effects, and the pop-up message.
		asterage2State.ageSpaceEffects();
	} //end method ageObjects
	
	private void checkForLevelComplete()
	{
		boolean trollShipsClear = asterage2State.getTrollShipList().isEmpty();
		boolean powerUpsCollected = asterage2State.getPowerUpList().isEmpty();
		boolean asteroidsClear = asterage2State.getAsteroidList().isEmpty();
		boolean noMothershipPending = !(asterage2State.checkMothershipSpawnCountdownActive());
		
		if ( trollShipsClear && powerUpsCollected && asteroidsClear && noMothershipPending )
		{
			//level up conditions met. Switch state to WARPING OUT, and show Level completion message.
			//also, grand a deferral bonus to spawning troll ships
			asterage2State.setGameState(GameState.WARPING_OUT);
			showLevelUpMessage();
			asterage2State.grantTrollScoutCountdownDeferment();
			gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.NONE);		//stop music currently playing, and
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_VICTORY_FANFARE);	//play fanfare
		} //end if check for level up conditions being met
	} //end method checkForLevelComplete
	
	
	private void doWarpoutCountdownSequence ()
	{
		//first decrement the countdown by 1. Then perform the sequence of events for warp-out.
		asterage2State.decrementWarpingOutCountDown();
		int warpoutCountdown = asterage2State.getWarpingOutCountDown();
		PlayerShip playerShip = asterage2State.getPlayerShip();
		
		if (	(warpoutCountdown <= WARP_OUT_COUNTDOWN_SHIP_WARP_EVENT) &&
				(true ==playerShip.checkShipInPlay()) )
		{
			//set the ship status to UNSPAWNED; Create a new warp out effect and add it to the effect list.
			WarpOutEffect warpOutEffect = new WarpOutEffect ( playerShip, WarpOutEffect.WarpEffectSize.NORMAL );
			asterage2State.addSpaceEffect(warpOutEffect);
			playerShip.setShipStatus(Ship_Status.UNSPAWNED);
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_WARPING_OUT);
			gui.haltSoundForEvent(SoundManager.SOUND_EVENT.A2_PLAYER_SONIC_DISRUPTOR_ACTIVE);	//stop playing sonic disruptor noise.
		} //end if check to handle ship in play when countdown reaches ship warping out event
		
		if ( warpoutCountdown <= WARP_OUT_COUNTDOWN_LEVEL_UP_EVENT)
		{
			//set the game state to playing, do the level up logic.
			asterage2State.setGameState(GameState.RUNNING);
			performStageLevelUp();
		} //end if check for end of warp out countdown
		
	} //end method doWarpoutCountdownSequence
	
	
	private void performStageLevelUp()
	{
		asterage2State.setGameLevel( asterage2State.getGameLevel() + 1);	//advance the level counter.
		generateAsteroidsForLevel();										//make the asteroids for this level
		asterage2State.resetSpecialItemDropCounter();						//reset counter to drop special items
		selectSoundTrackForLevel();											//play the soundtrack for the current game level.
	
		//check to see if the flag that a troll mothership was recently spawned should be reset.
		//(does so every couple of levels.)
		if ( 0 == asterage2State.getGameLevel() % TrollMothership.TROLL_MOTHERSHIP_LEVEL_SPAWN_FREQUENCY )
		{
			asterage2State.setSpawnedTrollMothershipRecently(false);
			DebugManager.logMessage(5, "Resetting troll mothership spawn chance.");
		}
	} //end method performStageLevelUp
	
	private void selectSoundTrackForLevel()
	{
		//get the current game level, and find the result modulo the number of soundtracks that we have.
		//then, select the sound track for the current level.
		int currentLevel = asterage2State.getGameLevel();
		final int NUMBER_OF_TRACKS = 4;
		int soundTrackIndex = (currentLevel - 1) % NUMBER_OF_TRACKS;
		
		DebugManager.logMessage(6, "SoundTrack index selected: " + soundTrackIndex);
		
		switch ( soundTrackIndex )
		{
			case 3:
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_TRACK4);
				break;
				
			case 2:
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_TRACK3);
				break;
			
			case 1: 
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_TRACK2);
				break;
				
			case 0: 
			default:
				//the first track is also the default in case we get into trouble.
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_TRACK1);
				break;
		} //end switch based on sound track index computed.
	} //end method 
	
	
	/**
	 * Responsible for awarding points to the score, and for handling other logic, such as awarding extra lives.
	 * All points awarded should be done through this method.
	 * @param pointAward
	 */
	public void awardPoints( long pointAward )
	{
		//award points. Then do checking for extra life award, etc.
		long currentPoints = asterage2State.getScore();
		long newPoints = currentPoints + pointAward;
		asterage2State.setScore( newPoints );
		
		//if the current score exceeds the total required to gain an extra life, award the life.
		//then, increase the past awarded lives, which in turn increases the required total.
		//also show the congratulatory message.
		if ( newPoints >= asterage2State.getPointsRequiredForNextExtraLife() )
		{
			int livesLeft = asterage2State.getRemainingShips();
			asterage2State.setRemainingShips( livesLeft + 1);
			asterage2State.incrementExtraLivesAwarded();
			showExtraLifeMessage();
			gui.playSoundForEvent(SoundManager.SOUND_EVENT.A2_EXTRA_LIFE_AWARDED);
		} //end if check to award extra life.
		
	} //end method awardPoints 
	
	
	private void checkForGameOver()
	{
		//check for game over conditions.
		// 1) No more ships remaining
		// 2) Ship is destroyed
		// 3) Game state is RUNNING
		// 4) No ship explosion is currently displaying. Let the ship be completely destroyed.
		boolean shipDestroyed = asterage2State.getPlayerShip().checkShipIsDestroyed();
		boolean noLivesLeft = (asterage2State.getRemainingShips() <= 0 );
		boolean gameStateWasRunning = ( asterage2State.getGameState() == Asterage2State.GameState.RUNNING );
		boolean shipExplosionOnBoard = asterage2State.checkShipExplosionEffectOnBoard();
		if ( shipDestroyed && noLivesLeft && gameStateWasRunning && !shipExplosionOnBoard)
		{
			//gave is now over. Set game state to Game over.
			asterage2State.setGameState(GameState.GAME_OVER);
			//check for a high score in the top 10.
			long score = asterage2State.getScore();
			if ( true == asterage2State.doesScoreRankTopTen( score ) )
			{
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_CELEBRATION);
				recordNewTop10ScoreEntry(score);
			} //end if check for a new top 10 score record.
			else
			{
				//standard game over.
				gui.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_GAME_OVER);
			} //end else clause for non-top ten score
		} //end if check for game-over conditions
	} //end method checkForGameOver
	
	private void recordNewTop10ScoreEntry (long score)
	{
		//get the name the player wants to enter as their high score entry.
		String highScoreName = gui.getReplyDialog (
							"NEW HIGH SCORE!",
							"Congratulations! Your performance has earned you a place in the AsteRAGE \n" +
							"hall of fame! Enter your name as you wish for it to appear in the \n" +
							"high score list! (Up to 8 letters)" );
		highScoreName += "         ";	//pad with 8 spaces, so we can be sure to grab 8 characters.
		String highScoreNameToSave = highScoreName.substring(0, 8);
		int currentLevel = asterage2State.getGameLevel();

		//Save the new record.
		DebugManager.logMessage(5, highScoreNameToSave + " has secured a position of fame... for now!");
		asterage2State.insertNewRecord(highScoreNameToSave, currentLevel, score);
	} //end method recordNewTop10ScoreEntry
	
	private void restartGameOnGameOver()
	{
		//check to see if the game is in game-over state. If so, then reinitialize the state.
		if ( asterage2State.getGameState() == GameState.GAME_OVER )
		{
			beginNewGame();
		} //end if check for game over state.
	} //end method restartGameOnGameOver
	
	
	private void advancePopUpMessageExpiration()
	{
		//first decrement the counter.
		//if the counter shows expired, and a message is being displayed, clear the message.
		asterage2State.decrementPopUpCountdown();
		
		boolean showingMessage = asterage2State.checkPopUpMessageBeingShown();
		boolean timeToClearMessage = asterage2State.getPopUpMessageRemainingCountdown() < asterage2State.POP_UP_MESSAGE_CLEAR_TIME;
		if ( showingMessage && timeToClearMessage )
		{
			gui.setAsterage2PopUpText("", AsteRAGE2GameBoard.PopUpMessageLabel.MessageType.INFO);
		} //end if check to blank message.
	} //end method advancePopUpMessageExpiration
	
	private void showPauseMessage()
	{
		//if the countdown is 0 (no message being shown) set the pop-up message to the pause message, and reset the counter.
		if ( asterage2State.getPopUpMessageRemainingCountdown() <= 0 )
		{
			gui.setAsterage2PopUpText( PAUSE_MESSAGE, MessageType.INFO);
			asterage2State.resetPopUpMessageCountdown();
		} //end if check for a message not being shown.
	} //end method showPauseMessage
	
	private void showLevelUpMessage()
	{
		//show level up message regardless of whether or not other messages are still being displayed.
		gui.setAsterage2PopUpText( LEVEL_UP_MESSAGE, MessageType.REWARD);
		asterage2State.resetPopUpMessageCountdown();
	} //end method showLevelUpMessage
	
	private void showExtraLifeMessage()
	{
		//show extra life message regardless of whether or not other messages are still being displayed.
		gui.setAsterage2PopUpText( EXTRA_LIFE_MESSAGE, MessageType.REWARD);
		asterage2State.resetPopUpMessageCountdown();
	} //end method showExtraLifeMessage
	
	private void showCurrentLevelMessage()
	{
		gui.setAsterage2PopUpText( CURRENT_LEVEL_MESSAGE + asterage2State.getGameLevel(), MessageType.INFO );
		asterage2State.resetPopUpMessageCountdown();
	} //end method showCurrentLevelMessage
	
	private void showGameOverMessage()
	{
		//if the countdown is 0 (no message being shown) set the pop-up message to the game-over message, and reset the counter.
		if ( asterage2State.getPopUpMessageRemainingCountdown() <= 0 )
		{
			gui.setAsterage2PopUpText( GAME_OVER_MESSAGE, MessageType.WARNING);
			asterage2State.resetPopUpMessageCountdown();
		} //end if check for a message not being shown.
	} //end method showGameOverMessage
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	/**
	 * Types of requests to shift the option selected by the power up menu.
	 */
	public static enum Power_Up_Menu_Cycle_Option
	{
		LEFT,
		RIGHT;
	} //end enum Power Up Menu Cycle Option definition
	
	
	public static enum Ship_System_Destruction_Target
	{
		DECELERATION,
		SHIELD_GENERATOR,
		GRAVITY_NET,
		HOMING_MISSILES,
		MULTISHOT,
		SONIC_DISRUPTOR,
		MYTHICITE;
	} //end enum ship system destruction target definition
	
	
	/**
	 * The asteroid pointcard is used to keep track of what asteroids are desired to be spawned.
	 */
	public static class AsteroidPointCard
	{
		HashMap<Asteroid.Asteroid_Type_Size, Integer> asteroidTotalMap;
		
		int remainingPointsToSpend;
				
		public AsteroidPointCard()
		{
			asteroidTotalMap = new HashMap<>();
			remainingPointsToSpend = 0;
		}
		
		//points methods
		public int getRemainingPointsToSpend() { return remainingPointsToSpend; }
		public void creditPoints( int howManyToAdd ) { this.remainingPointsToSpend += howManyToAdd; }
		public void chargePoints( int howManyToSubtract) { this.remainingPointsToSpend -= howManyToSubtract; }
		
		//Asteroid methods.
		
		public void addBaseStartingAsteroids()
		{
			addToAsteroidCount( Asteroid.Asteroid_Type_Size.LARGE_WHITE, 2 );
			creditPoints( Asteroid.Asteroid_Type_Size.LARGE_WHITE.generationPointValue() * 2 );		//credit points, otherwise we get charged.
		}
		
		public int getAsteroidCount ( Asteroid.Asteroid_Type_Size whatSize ) 
		{ 
			//check to see if we have a null Integer object before trying to dereference it.
			Integer countAsInteger =  asteroidTotalMap.get(whatSize); 
			return ( null != countAsInteger ) ?
						countAsInteger.intValue() : 0;
		} //end method getAsteroidCount
		public void addToAsteroidCount ( Asteroid.Asteroid_Type_Size whatSize, int howMany )
		{
			int previousCount = getAsteroidCount(whatSize);
			int newCount = previousCount + howMany;
			asteroidTotalMap.put(whatSize, newCount);
			chargePoints( whatSize.generationPointValue() * howMany );		//charge points for the additions
		} //end method addToAsteroidCount
		public void decrementFromAsteroidCount( Asteroid.Asteroid_Type_Size whatSize, int howMany )
		{
			int previousCount = getAsteroidCount(whatSize);
			int newCount = ( (previousCount - howMany) >= 0 ) ?
						(previousCount - howMany) : 0;							//decrement, but don't go below zero.
			asteroidTotalMap.put(whatSize, newCount);
			creditPoints( (previousCount - newCount) * whatSize.generationPointValue() );	//charge back for asteroids actually taken away.
		} //end method addToAsteroidCount
	} //end class AsteroidPointCard definition
	
	
} //end class Asterage2Controller
