/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.state;

import com.rfhoodrdm.asterage2.gameObjects.asterage1.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollScout;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollMothership;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollPod;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.MessageText;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author roberthood
 */
public class Asterage1State
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	//global game data.
	int extraLives;					//number of lives remaining for the player.
	int level;						//current game level.
	int mythicite;					//how much mythicite collected.
	int livesAwarded;				//how many 1-ups has the player earned?
	boolean alienHasSpawnedFlag;	//has the alien appeared yet?
	
	Asterage1ScoreState asterage1ScoreState;	//tracks number of points accumulated.
	DataLoader dataLoader;						//data loader reference
	HighScoreDirectory highScoreDirectory;
	GAME_STATUS gameStatus;
	PlayerShip playerShip;
	ConcurrentLinkedQueue<SpaceObject> spaceObjectList;
	MessageText officialMessage;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage1State( DataLoader passedLoader )
	{
		dataLoader = passedLoader;
		asterage1ScoreState = new Asterage1ScoreState();
		highScoreDirectory = new HighScoreDirectory(HighScoreDirectory.GAME_IDENTIFIER.ASTERAGE1,
													dataLoader);
		initializeAsterage1State();

		//no initializing MessageText, we need that void if not used.
	} //end constructor.
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Get the current game status.
	 * @return 
	 */
	public GAME_STATUS getGameStatus()
	{
		return this.gameStatus;
	} //end fucntion getGameStatus
	
	public void setGameStatus ( GAME_STATUS newStatus)
	{
		this.gameStatus = newStatus;
	} //end function setGameStatus
	
	/**
	 * Advance the level count by 1, and modify the game state accordingly. E.g. make new asteroids.
	 */
	public void promoteToNextLevel()
	{
		this.level += 1;
		spawnAsteroidsForNewLevel();
	} //end function promoteToNextLevel
	
	/**
	 * Initializes the game state
	 */
	public void initializeAsterage1State()
	{
		level = 1;
		asterage1ScoreState.resetPlayerScore();
		extraLives = 4;
		livesAwarded = 0;
		alienHasSpawnedFlag = false;
		
		//Game status is set to new game on start.
		gameStatus = GAME_STATUS.NEW_GAME;
		
		playerShip = new PlayerShip();								//new ship object.
		playerShip.setShipStatus(PlayerShip.SHIP_STATUS.DESTROYED);	//not yet added to the board.
		
		spaceObjectList = new ConcurrentLinkedQueue<SpaceObject>();
		spaceObjectList.add ( playerShip );
		
		//spawnNewTrollMothership(spaceObjectList);
		spawnAsteroidsForNewLevel();
		this.officialMessage = null;
		
	} //end function initializeAsterage1State
	
	public int getLevel()
	{
		return this.level;
	} //end function getScore
	public int getScore ()
	{
		return asterage1ScoreState.getScore();
	} //end function getScore
	public Asterage1ScoreState getScoreStateObject ()
	{
		return this.asterage1ScoreState;
	} //end function getScoreStateObject
	public int getExtraLives ()
	{
		return this.extraLives;
	} //end function getExtraLives
	public void incrementExtraLives()
	{
		this.extraLives += 1;
	} //end function incrementExtraLives
	public void decrementExtraLives()
	{
		this.extraLives -= 1;
	} 
	public int getPlayerShields()
	{
		return (int)Math.round( playerShip.getCurrentShields() );
	} //end function getPlayerShields
	public int getPlayerMythicite()
	{
		return mythicite;
	} //end function getPlayerMythicite
	public void incrementLivesAwarded()
	{
		this.livesAwarded += 1;
	} 
	public int getLivesAwarded()
	{
		return this.livesAwarded;
	} 
	public boolean getAlienHasSpawnedFlag ()
	{
		return this.alienHasSpawnedFlag;
	} 
	/**
	 * No arguments. Just set it.
	 */
	public void setAlienHasSpawnedFlag()
	{
		alienHasSpawnedFlag = true;
	} 
	
	public PlayerShip getPlayerShip ()
	{
		return this.playerShip;
	}//end function getPlayerShip
	public ConcurrentLinkedQueue<SpaceObject> getSpaceObjectList()
	{
		return this.spaceObjectList;
	} //end function getSpaceObjectList
	public MessageText getOfficialMessage ()
	{
		return this.officialMessage;
	} 
	public void setOfficialMessage ( MessageText passedMessageText )
	{
		this.officialMessage = passedMessageText;
	} //end function setOfficialMessage
	public void removeOfficialMessage()
	{
		this.officialMessage = null;
	} //end function removeOfficialMessage
	
	public boolean doesScoreRankTopTen( long points )
	{
		return highScoreDirectory.doesScoreRankTopTen( points );
	} //end function doesScoreRankTopTen
	
	public void insertNewRecord ( String name, int level, long points )
	{
		highScoreDirectory.insertNewRecord(name, level, points);
	} //end function insertNewRecord
	
	
	/**
	 * Reset the game state to be a new game.
	 */
	public void resetGame()
	{
		initializeAsterage1State();
	} 
	
	/**
	 * Award the corresponding number of points to the task accomplished by the player.
	 * @param accomplishment 
	 */
	@Deprecated
	public void awardPoints ( Asterage1ScoreState.SCORE_SYSTEM accomplishment )
	{
		
	} //end function awardPoints
	
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	public void spawnNewTrollScout ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		//The troll scout either spawns along the top/bottom edge, or left/right edge.
		//50% chance of either.
		TrollScout newScout = null;
		if ( Math.random() > .5 )
		{
			//along top
			double x = Math.random() * 1200;
			newScout = new TrollScout ( x, 0 );
		}
		else
		{
			//along side.
			double y = Math.random() * 600;
			newScout = new TrollScout ( 0, y );
		} 
		spaceObjectList.add ( newScout );
	} //end function spawnNewTrollScout
	
	public void spawnNewAsteroid ( Asteroid.ASTEROID_SIZE whatSize, ConcurrentLinkedQueue<SpaceObject> spaceObjectList)
	{
		//get the player's current x and y position. Only spawn asteroids 1/2 a screen away from it.
		double playerX = this.playerShip.getXPosition();
		double playerY = this.playerShip.getYPosition();
		
		double asteroidXRandom = ( Math.random() * 600.0 );
		double asteroidYRandom = ( Math.random() * 300.0 );
		
		double asteroidX = playerX + asteroidXRandom + 300;
		double asteroidY = playerY + asteroidYRandom + 150;
		
		Asteroid newAsteroid = new Asteroid( asteroidX, asteroidY, whatSize);
		spaceObjectList.add( newAsteroid );
	} //end function spawnNewAsteroid
	
	public void spawnNewTrollMothership( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		TrollMothership newMothership;
		if ( Math.random() > .5 )
		{
			//along top
			double x = Math.random() * 1200;
			newMothership = new TrollMothership ( x, 0.0, playerShip );
		}
		else
		{
			//along side.
			double y = Math.random() * 600;
			newMothership = new TrollMothership ( 0.0, y, playerShip );
		} 
		spaceObjectList.add ( newMothership );
		
		//Now create and add the weapons pods.
		for ( int counter = 1; counter <= 4; ++ counter )
		{
			TrollPod newPod = new TrollPod ( newMothership, counter );
			newMothership.attachTrollPod ( newPod, counter );
			spaceObjectList.add ( newPod );
			
		} //end for loop to create weapons pods for the mothership.

	} //end function spawnNewTrollMothership
	
	/**
	 * Spawn a new player ship in the middle of the game board.
	 */
	public void spawnNewPlayerShip ()
	{
		playerShip.respawnShip();
		extraLives -= 1;
	} //end function spawnNewPlayerShip
	
	private void spawnAsteroidsForNewLevel ()
	{
		int numberLargeAsteroids = 2 + ( 2 * level );
		for ( int count = 1;	count <= numberLargeAsteroids;	++count )
		{
			spawnNewAsteroid ( Asteroid.ASTEROID_SIZE.LARGE, spaceObjectList);
		} //end for loop to create new large asteroids.
		
		//create from 0 to 2 medium asteroids.
		int numberMediumAsteroids = (int) Math.floor ( Math.random() * 5 );
		for ( int count = 1;	count <= numberMediumAsteroids;		++count )
		{
			spawnNewAsteroid ( Asteroid.ASTEROID_SIZE.MEDIUM, spaceObjectList);
		} //end for loop to create new medium asteroids.
		
		//create from 0 to 2 small asteroids.
		int numberSmallAsteroids = (int) Math.floor ( Math.random() * 5 );
		for ( int count = 1;	count <= numberSmallAsteroids;		++count )
		{
			spawnNewAsteroid ( Asteroid.ASTEROID_SIZE.SMALL, spaceObjectList);
		} //end for loop to create new small asteroids.
		
	} //end function spawnAsteroidsForLevel
	
	
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
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum GAME_STATUS
	{
		NEW_GAME,
		IN_PROGRESS,
		PAUSE,
		GAME_OVER;
		
	} //end enum GAME_STATUS definition.
}
