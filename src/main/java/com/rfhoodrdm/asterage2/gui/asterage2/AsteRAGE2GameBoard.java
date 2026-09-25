
package com.rfhoodrdm.asterage2.gui.asterage2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.SpaceEffect;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.HomingMissile;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlasmaBolt;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PowerUpBaseObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollBaseShip;
import com.rfhoodrdm.asterage2.gui.asterage2.components.PopUpMessageLabel;
import com.rfhoodrdm.asterage2.gui.asterage2.components.StarPoint;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AsteRAGE2GameBoard 
	extends PanelTemplate {
	private static final long serialVersionUID = -3668628689807529101L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final Dimension GAME_BOARD_DIMENSION 
		= new Dimension (GameConstants.GAME_BOARD_WIDTH, GameConstants.GAME_BOARD_HEIGHT);
	
	private PopUpMessageLabel popUpMessage;				//holder for messages on the game screen.
	private final Asterage2State asterage2State;		//reference to game state object
	
	private ArrayList<StarPoint> starList;				//list of stars to draw in the background
	public static final int numberOfStars = 100;		//how many stars to draw
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public AsteRAGE2GameBoard(Asterage2State asterage2State)	{
		super();		
		
		//initialize starting parameters of the panel
		this.setMaximumSize(GAME_BOARD_DIMENSION);
		this.setMinimumSize(GAME_BOARD_DIMENSION);
		this.setPreferredSize(GAME_BOARD_DIMENSION);
		this.setSize(GAME_BOARD_DIMENSION);
		this.setLayout(null);							//absolute layout
		
		starList = new ArrayList<>();					//make list of stars to display.
		createNewStarList();							//create a new star list when we initialize.
		
		popUpMessage = new PopUpMessageLabel();			//make a new pop up message label, and add it to the game board.
		add(popUpMessage);
		
		this.asterage2State = asterage2State;
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void repaintGameBoard ()	{ 
		this.repaint(); 
		}
	
	/**
	 * Pass forward a message to display to the pop-up label.
	 */
	public void setPopUpText( String message, PopUpMessageLabel.MessageType whatType ) { 
		popUpMessage.setPopUpText(message, whatType);
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Custom redrawing of the game surface
	 */
	protected void paintComponent ( Graphics g ) {
		super.paintComponent(g);	//call to super's paint component with graphics handle.
		
		paintStarField(g);			//show the stars in the background. paint stars first because they're furthest away
		paintAsteroids(g);			//paint each asteroid in play.
		paintPlasmaBolts(g);		//paint plasma bolts in play
		paintHomingMissiles(g);		//paint homing missiles in play
		paintTrolls(g);				//paint trolls on the game board.
		paintPowerUps(g);			//paint power ups on the game board
		paintSpaceEffects(g);		//paint space effects that do not belong to other objects.
		
		paintPlayerShip(g);			//paint the player ship. Do this last.
		
		Toolkit.getDefaultToolkit().sync();	//flush repaints, to cure stuttering.
	} 
	
	/**
	 * Paints the animated field of stars.
	 */
	private void paintStarField( Graphics g ) {
		for ( StarPoint currentStar: starList )	{
			currentStar.paintStar( g, asterage2State.getFrameNumber() );		//have star draw itself
		} 
	}
	
	private void paintAsteroids( Graphics g ) {
		for ( Asteroid currentAsteroid: asterage2State.getAsteroidList() )
		{
			currentAsteroid.paintObject(g);
		} //end for loop iterating through asteroids.
	} 
	
	private void paintPlayerShip( Graphics g ) {
		PlayerShip playerShip = asterage2State.getPlayerShip();
		playerShip.paintObject(g);
	} 
	
	private void paintPlasmaBolts( Graphics g ) {
		for ( PlasmaBolt currentPlasmaBolt : asterage2State.getPlasmaBoltList() ) {
			currentPlasmaBolt.paintObject(g);
		} 
	}
	
	private void paintTrolls(Graphics g) {
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() ) {
			currentTroll.paintObject(g);
		} 
	}
	
	private void paintPowerUps( Graphics g) {
		for ( PowerUpBaseObject currentPowerUp: asterage2State.getPowerUpList() ) {
			currentPowerUp.paintObject(g);
		} 
	} 
	
	private void paintSpaceEffects ( Graphics g ) {
		for ( SpaceEffect currentEffect: asterage2State.getSpaceEffectList() ) {
			currentEffect.paintObject(g);
		} 
	} 
	
	private void paintHomingMissiles( Graphics g ) {
		for ( HomingMissile currentHomingMissile: asterage2State.getHomingMissileList() ) {
			currentHomingMissile.paintObject(g);
		} 
	}
	
	
	private void createNewStarList() {
		//clear the previous list.
		starList.clear();
		
		//now make a bunch of stars
		for ( int count = 0;    count <= numberOfStars;    ++count ) {
			//create a new star at a random location in the board.
			int randomXCoord = (int) Math.floor( Math.random() * GAME_BOARD_DIMENSION.width );
			int randomYCoord = (int) Math.floor( Math.random() * GAME_BOARD_DIMENSION.height );
			StarPoint newStar = new StarPoint ( randomXCoord, randomYCoord );
			starList.add( newStar );
		} 
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	

}
