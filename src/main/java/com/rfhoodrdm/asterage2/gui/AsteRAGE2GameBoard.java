/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.SpaceEffect;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.HomingMissile;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlasmaBolt;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PowerUpBaseObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollBaseShip;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JLabel;
import com.rfhoodrdm.asterage2.state.Asterage2State;

/**
 *
 * @author roberthood
 */
public class AsteRAGE2GameBoard
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final int boardWidth = 1200;
	public static final int boardHeight = 600;
	public static final Dimension gameBoardDimension = new Dimension (boardWidth, boardHeight);
	
	PopUpMessageLabel popUpMessage;		//holder for messages on the game screen.
	
	//references to other components
	Asterage2State asterage2State;		//reference to game state object
	
	ArrayList<StarPoint> starList;					//list of stars to draw in the background
	public static final int numberOfStars = 100;		//how many stars to draw
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public AsteRAGE2GameBoard()
	{
		super();		//call to super class constructor
		
		//initialize starting parameters of the panel
		this.setMaximumSize(gameBoardDimension);
		this.setMinimumSize(gameBoardDimension);
		this.setPreferredSize(gameBoardDimension);
		this.setSize(gameBoardDimension);
		this.setLayout(null);							//absolute layout
		
		starList = new ArrayList<>();					//make list of stars to display.
		createNewStarList();							//create a new star list when we initialize.
		
		popUpMessage = new PopUpMessageLabel();			//make a new pop up message label, and add it to the game board.
		add(popUpMessage);
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void setAsterage2State( Asterage2State passedState )	{	asterage2State = passedState;	} 
	public void repaintGameBoard ()	{ this.repaint(); }
	
	/**
	 * Pass forward a message to display to the pop-up label.
	 * @param message
	 * @param whatType 
	 */
	public void setPopUpText( String message, PopUpMessageLabel.MessageType whatType ) 
	{ 
		popUpMessage.setPopUpText(message, whatType);
	} //end method setPopUpText
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Custom redrawing of the game surface
	 * @param g 
	 */
	protected void paintComponent ( Graphics g )
	{
		super.paintComponent(g);	//call to super's paint component with graphics handle.
		
		paintStarField(g);			//show the stars in the background. paint stars first because they're furthest away
		paintAsteroids(g);			//paint each asteroid in play.
		paintPlasmaBolts(g);		//paint plasma bolts in play
		paintHomingMissiles(g);		//paint homing missiles in play
		paintTrolls(g);				//paint trolls on the game board.
		paintPowerUps(g);			//paint power ups on the game board
		paintSpaceEffects(g);		//paint space effects that do not belong to other objects.
		
		paintPlayerShip(g);			//paint the player ship. Do this last.
	} //end method paintComponent 
	
	/**
	 * Paints the animated field of stars.
	 * @param g 
	 */
	private void paintStarField( Graphics g )
	{
		//paint each star in the field by passing it a handle to the graphics object.
		for ( StarPoint currentStar: starList )
		{
			currentStar.paintStar( g, asterage2State.getFrameNumber() );		//have star draw itself
		} //end for loop iterating through stars to draw
	} //end method paintStarField
	
	private void paintAsteroids( Graphics g )
	{
		for ( Asteroid currentAsteroid: asterage2State.getAsteroidList() )
		{
			currentAsteroid.paintObject(g);
		} //end for loop iterating through asteroids.
	} //end method paintAsteroids
	
	private void paintPlayerShip( Graphics g )
	{
		PlayerShip playerShip = asterage2State.getPlayerShip();
		playerShip.paintObject(g);
	} //end method paintPlayerShip
	
	private void paintPlasmaBolts( Graphics g )
	{
		for ( PlasmaBolt currentPlasmaBolt : asterage2State.getPlasmaBoltList() )
		{
			currentPlasmaBolt.paintObject(g);
		} //end for loop iterating through the plasma bolt list.
	} //end method paintPlasmaBolts
	
	private void paintTrolls(Graphics g)
	{
		for ( TrollBaseShip currentTroll: asterage2State.getTrollShipList() )
		{
			currentTroll.paintObject(g);
		} //end for loop iterating through trolls
	} //end method paintTrolls
	
	private void paintPowerUps( Graphics g)
	{
		for ( PowerUpBaseObject currentPowerUp: asterage2State.getPowerUpList() )
		{
			currentPowerUp.paintObject(g);
		} //end for loop iterating through power ups
	} //end method paintPowerUps
	
	private void paintSpaceEffects ( Graphics g )
	{
		for ( SpaceEffect currentEffect: asterage2State.getSpaceEffectList() )
		{
			currentEffect.paintObject(g);
		} //end for loop iterating through space effects
	} //end method paintSpaceEffects
	
	private void paintHomingMissiles( Graphics g )
	{
		for ( HomingMissile currentHomingMissile: asterage2State.getHomingMissileList() )
		{
			currentHomingMissile.paintObject(g);
		} //end for loop iterating through homing missiles
	} //end method paintHomingMissiles
	
	
	private void createNewStarList()
	{
		//clear the previous list.
		starList.clear();
		
		//now make a bunch of stars
		for ( int count = 0;    count <= numberOfStars;    ++count )
		{
			//create a new star at a random location in the board.
			int randomXCoord = (int) Math.floor( Math.random() * boardWidth );
			int randomYCoord = (int) Math.floor( Math.random() * boardHeight );
			StarPoint newStar = new StarPoint ( randomXCoord, randomYCoord );
			starList.add( newStar );
		} //end for loop to create a new star for each one desired.
	} //end method createNewStarList
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static class PopUpMessageLabel 
	extends JLabel
	{
		public PopUpMessageLabel()
		{
			Dimension messageSize = new Dimension ( boardWidth, 100 );
			setSize(messageSize); setPreferredSize(messageSize); setMinimumSize(messageSize); setMaximumSize(messageSize);
			setLocation( 0, (boardHeight/2) );
			setFont ( new java.awt.Font( GameConstants.gameFont, Font.BOLD, GameConstants.Asterage2HUDFontSize ));
			setHorizontalAlignment(CENTER);
		} //end constructor
		
		public static enum MessageType
		{
			REWARD	( new Color (0x33, 0x99, 0x33) ),
			INFO	( new Color (0x33, 0x99, 0xCC) ),
			WARNING	( new Color (0xCC, 0x00, 0x00) );
			
			Color associatedColor;	//what color is associated with this message
			MessageType( Color passedColor ) { this.associatedColor = passedColor; }
			
			public Color getAssociatedColor() { return this.associatedColor; }
		}
		
		public void setPopUpText( String message, MessageType whatType )
		{
			//set the message text and associated color
			setText(message);
			setForeground( whatType.getAssociatedColor() );
		} //end void setPopUpText
	} //end PopUpMessageLabel
	
	
	public static class StarPoint 
	{
		//data members.
		private int xCoord = 0;					//x coordinate on game board
		private int yCoord = 0;					//y coordinate on game board
		Color starColor = Color.WHITE;			//what color to draw on field?
		Color baseColor = getBaseStarColor();	//what is the basic color of the star?
		
		/**
		 * Constructor
		 * @param passedXCoord
		 * @param passedYCoord 
		 */
		public StarPoint ( int passedXCoord, int passedYCoord )
		{
			//remember the location set when made
			this.xCoord = passedXCoord;
			this.yCoord = passedYCoord;
		}  //end constructor
		
		public void paintStar ( Graphics g, int frameNumber )
		{
			//draw as two lines crossing.
			checkChangeStarColor ( frameNumber );
			g.setColor( starColor );
			
			g.drawLine(xCoord -1, yCoord, xCoord+1, yCoord);
			g.drawLine(xCoord, yCoord-1, xCoord, yCoord+1);
		} //end method paintStar
		
		private void checkChangeStarColor( int frameNumber )
		{
			//see if we should randomly change the star color.
			boolean changeStarColor = ( frameNumber % 3 ) == 0;
			if ( changeStarColor )
			{
				starColor = getNewRandomStarColor ();
			} //end if check to see if we should change star color
		} //end method checkChangeStarColor
		
		private Color getNewRandomStarColor ()
		{
			int randomChance = (int) Math.floor ( Math.random() * 100 ) ;
			if ( randomChance < 1 )		return Color.LIGHT_GRAY;
			if ( randomChance < 2 )	return Color.CYAN;
			if ( randomChance < 3 ) return Color.ORANGE;
			if ( randomChance < 4 )	return Color.YELLOW;
			if ( randomChance < 5 ) return Color.WHITE;
			
			return baseColor;		//else just return the base color
		} //end method getNewRandomColor
		
		private static Color getBaseStarColor () 
		{
			int randomChance = (int) Math.floor ( Math.random() * 100 ) ;
	
			//if ( randomChance < 1 )		return Color.RED;
			if ( randomChance < 2 )		return Color.ORANGE;
			if ( randomChance < 3 )		return Color.CYAN;
			if ( randomChance < 30 )	return Color.YELLOW;
			return Color.WHITE;
		} //end method getBaseStarColor
		
	} //end class StarPoint definition
	
} //end class AsteRAGE2GameBoard definition
