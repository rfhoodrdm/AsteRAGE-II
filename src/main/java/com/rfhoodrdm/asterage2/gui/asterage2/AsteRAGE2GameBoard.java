
package com.rfhoodrdm.asterage2.gui.asterage2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.SpaceEffect;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.HomingMissile;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlasmaBolt;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PowerUpBaseObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollBaseShip;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.Asterage2State;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AsteRAGE2GameBoard 
	extends PanelTemplate {
	private static final long serialVersionUID = -3668628689807529101L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int boardWidth = 1200;
	public static final int boardHeight = 600;
	public static final Dimension gameBoardDimension = new Dimension (boardWidth, boardHeight);
	
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
		this.setMaximumSize(gameBoardDimension);
		this.setMinimumSize(gameBoardDimension);
		this.setPreferredSize(gameBoardDimension);
		this.setSize(gameBoardDimension);
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
			int randomXCoord = (int) Math.floor( Math.random() * boardWidth );
			int randomYCoord = (int) Math.floor( Math.random() * boardHeight );
			StarPoint newStar = new StarPoint ( randomXCoord, randomYCoord );
			starList.add( newStar );
		} 
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static class PopUpMessageLabel 
	extends JLabel {
		public PopUpMessageLabel() {
			Dimension messageSize = new Dimension ( boardWidth, 100 );
			setSize(messageSize); setPreferredSize(messageSize); setMinimumSize(messageSize); setMaximumSize(messageSize);
			setLocation( 0, (boardHeight/2) );
			setFont ( new java.awt.Font( GameConstants.gameFont, Font.BOLD, GameConstants.Asterage2HUDFontSize ));
			setHorizontalAlignment(CENTER);
		} 
		
		public static enum MessageType {
			REWARD	( new Color (0x33, 0x99, 0x33) ),
			INFO	( new Color (0x33, 0x99, 0xCC) ),
			WARNING	( new Color (0xCC, 0x00, 0x00) );
			
			Color associatedColor;	//what color is associated with this message
			MessageType( Color passedColor ) { this.associatedColor = passedColor; }
			
			public Color getAssociatedColor() { return this.associatedColor; }
		}
		
		public void setPopUpText( String message, MessageType whatType ) {
			//set the message text and associated color
			setText(message);
			setForeground( whatType.getAssociatedColor() );
		}
	} 
	
	public static class StarPoint {
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
		public StarPoint ( int passedXCoord, int passedYCoord ) {
			//remember the location set when made
			this.xCoord = passedXCoord;
			this.yCoord = passedYCoord;
		} 
		
		public void paintStar ( Graphics g, int frameNumber ) {
			//draw as two lines crossing.
			checkChangeStarColor ( frameNumber );
			g.setColor( starColor );
			
			g.drawLine(xCoord -1, yCoord, xCoord+1, yCoord);
			g.drawLine(xCoord, yCoord-1, xCoord, yCoord+1);
		} 
		
		private void checkChangeStarColor( int frameNumber ) {
			//see if we should randomly change the star color.
			boolean changeStarColor = ( frameNumber % 3 ) == 0;
			if ( changeStarColor ) {
				starColor = getNewRandomStarColor ();
			} 
		}
		
		private Color getNewRandomStarColor () {
			int randomChance = (int) Math.floor ( Math.random() * 100 ) ;
			if ( randomChance < 1 )	return Color.LIGHT_GRAY;
			if ( randomChance < 2 )	return Color.CYAN;
			if ( randomChance < 3 ) return Color.ORANGE;
			if ( randomChance < 4 )	return Color.YELLOW;
			if ( randomChance < 5 ) return Color.WHITE;
			if ( randomChance < 6 )	return Color.BLACK;
			
			return baseColor;		//else just return the base color
		}
		
		private static Color getBaseStarColor () {
			int randomChance = (int) Math.floor ( Math.random() * 100 ) ;
	
			if ( randomChance < 1 )		return Color.RED;
			if ( randomChance < 2 )		return Color.ORANGE;
			if ( randomChance < 3 )		return Color.CYAN;
			if ( randomChance < 30 )	return Color.YELLOW;
			return Color.WHITE;
		} 
	} 
	
}
