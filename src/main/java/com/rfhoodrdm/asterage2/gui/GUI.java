
package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.state.State;
import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;

import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 * GUI is the top class in the graphical user interface. It holds the game frame, game panels,
 * and sound manager. It also contains references to the other modules.
 */
public class GUI
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    
	//GUI constants.
	public final static int panelWidth = 1200;	    //height of one game panel.
    public final static int panelHeight = 700;	    //width of one game panel.
	
	//references to other components.
	Controller controller;
	State state;
	
	//references to GUI components.
	GameFrame gameFrame;
	SplashScreen splashScreen;
	Asterage1GameScreen asterage1GameScreen;
	AsteRAGE2GameScreen asterage2GameScreen;
	SoundManager soundManager;
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public GUI ()
	{
		//create and assemple the GUI components. Pass references to gui top level, for feedback.
		gameFrame = new GameFrame( this );
		
		splashScreen = new SplashScreen ( this );
		splashScreen.setVisible ( true );
		asterage1GameScreen = new Asterage1GameScreen();
		asterage1GameScreen.setVisible( false );
		asterage2GameScreen = new AsteRAGE2GameScreen();
		asterage2GameScreen.setVisible(false);
		soundManager = new SoundManager();
		soundManager.start();								//start the sound manager.
		
		
		gameFrame.add( splashScreen );
		gameFrame.add ( asterage1GameScreen );
		gameFrame.add( asterage2GameScreen );
	} //end constructor
	
	public void showInitialGUI ()
	{
		//show the GUI!
		gameFrame.setVisible( true );
		changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_TITLE_SCREEN);
		gameFrame.requestFocus();
		
	}
	
	public void setController ( Controller passedController )
	{
		this.controller = passedController;
		
		//attach the key adapter to capture key presses and releases. Pass it the controller reference.
		GameKeyAdapter gameKeyAdapter = new GameKeyAdapter();
		gameFrame.addKeyListener ( gameKeyAdapter ); 
		gameKeyAdapter.setController(controller);
	} //end function setController
	
	/**
	 * Set the master state reference.
	 * @param passedState 
	 */
	public void setState ( State passedState )
	{
		this.state = passedState;
		
		//set the relevant state reference for all the GUI's top level subcomponents.
		splashScreen.setTitleState( state.getTitleStateObject() );
		splashScreen.setAsterage1State( state.getAsterage1StateObject() );
		splashScreen.setAsterage2State( state.getAsterage2StateObject() );
		asterage1GameScreen.setAsterage1State ( state.getAsterage1StateObject() );
		asterage2GameScreen.setAsterage2State( state.getAsterage2StateObject() );
	} //end function setState
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Refresh the title game menu instead of repainting the whole screen. It saves computation power.
	 */
	public void refreshTitleGameMenu()
	{
		splashScreen.refreshTitleGameMenu();
	} //end function refreshTitleGameMenu
	
	/**
	 * Repaint the entire splash screen, if something was added or changed.
	 */
	public void refreshSplashScreen()
	{
		splashScreen.repaint();
	} //end function refreshSplashScreen
	
	/**
	 * Takes in a request to play a sound.
	 * @param soundEvent 
	 */
	public void playSoundForEvent ( SoundManager.SOUND_EVENT soundEvent )
	{
		soundManager.playSoundEvent( soundEvent );
	} //end function playSoundForEvent
	
	public void haltSoundForEvent ( SoundManager.SOUND_EVENT soundEvent )
	{
		soundManager.stopSoundEvent(soundEvent);
	} //end function haltSoundForEvent
	
	public void assertFocusOnFrame()
	{
		gameFrame.requestFocus();
	} //end function assertFocusOnFrame
	
	public void changeCurrentGuiShown()
	{
		//hide all of the GUI panels to avoid complex logic.
		splashScreen.setVisible(false);
		asterage1GameScreen.setVisible(false);
		asterage2GameScreen.setVisible(false);
		
		//get the current active state.
		GameConstants.CURRENT_STATE currentState = state.getCurrentActiveState();
		DebugManager.logMessage(5, "Changing GUI shown: " + currentState.toString() );
		
		//show the gui corresponding to the active state.
		//start the initial music sequence.
		switch ( currentState )
		{
			case TITLE_SCREEN:
				splashScreen.setVisible( true );
				changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_TITLE_SCREEN);
				break;
			
			case ASTERAGE_1:
				asterage1GameScreen.setVisible ( true );
				changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.NONE);
				break;
			
			case ASTERAGE_2:
				asterage2GameScreen.setVisible( true );
				break;
			
			default:
				//error
				break;
				
		} //end switch based on state
	} //end function changeCurrentGuiShown
	
	/**
	 * Makes a call to the sound manager on behalf of the caller to change the music being played.
	 * @param newSequence 
	 */
	public void changeMusicSequence( SoundManager.SOUNDTRACK_SEQUENCE newSequence )
	{
		soundManager.changeMusicSequence(newSequence);
	} //end function changeMusicSequence
	
	/**
	 * Receive and pass forward request to update the Asterage 1 stat display
	 */
	public void refreshAsterage1StatDisplay()
	{
		asterage1GameScreen.refreshStatDisplay();
	} //end function refreshAsterage1StatDisplay
	
	public void repaintAsterage1GameBoard ()
	{
		asterage1GameScreen.repaintGameBoard();
	} //end function repaintAsterage1GameBoard
	
	public void repaintAsteRAGE2Display()
	{
		asterage2GameScreen.repaint();
		asterage2GameScreen.updateGameDisplay();
	} //end method repaintAsteRAGE2Display
	
	
	/**
	 * Open a simple text field dialog box, posing a question to the player, and soliciting a text reply.
	 * @param title
	 * @param question
	 * @return String message collected from the player.
	 */
	public String getReplyDialog( String title, String question)
	{
		return JOptionPane.showInputDialog(		gameFrame,
										 		question,
												title,
										 		JOptionPane.QUESTION_MESSAGE);
	} //end function getReplyDialog
	
	/**
	 * Asks the user if they want to return to main menu, thus aborting the game.
	 * @return Boolean verdict. True = yes, false = no.
	 */
	public boolean checkAsterage2AbortGameDialog ()
	{ 
		String[] options = { "Continue Game", "Return to Menu" };
		
		int selection =  JOptionPane.showOptionDialog(	gameFrame, 
														"Do you wish to end the current game and return to the title menu?", 
														"Return to Menu?", 
														JOptionPane.YES_NO_OPTION, 
														JOptionPane.WARNING_MESSAGE, 
														null, 
														options, 
														options[0]);
		boolean verdict = (1 == selection );
		System.out.println("Selection: " + selection + "\t Verdict: " + verdict );
		
		//return true if the selection was "Return to Menu" 
		return verdict;
	} //end method openAsterage2AbortGameDialog
	
	
	public void addAsterage2HUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation )
	{
		asterage2GameScreen.addHUDExplosion(whichSystemLocation);
	} //end method addHUDExplision
	
	public void addPowerUpPointsHUDExplosion()
	{
		asterage2GameScreen.addPowerUpPointsHUDExplosion();
	} //end method addPowerUpPointsHUDExplosion
	
	public void setAsterage2PopUpText( String message, AsteRAGE2GameBoard.PopUpMessageLabel.MessageType whatType ) 
	{ 
		asterage2GameScreen.setPopUpText(message, whatType);
	} //end method setPopUpText
	
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum Image
	{
		//Title screen graphics
		DEFAULT ( "DEFAULT.png" ),
		TITLE_GRAPHIC ( "TITLE_GRAPHIC.png"),
		GAME_SELECTOR_ICON_WHITE ( "GAME_SELECTOR_ICON_WHITE.png"),
		NO_IMAGE("NO_IMAGE.png"),
		
		//Asterage 1 graphics
		EXTRA_LIFE_ICON ( "EXTRA_LIFE_ICON.png"),
		SHIP_SPRITE ("SHIP_SPRITE.png"),
		DAMAGED_SHIP_SPRITE ("DAMAGED_SHIP_SPRITE.png"),
		PLAYER_BULLET("PLAYER_BULLET.png"),
		PLAYER_DEBRIS("PLAYER_DEBRIS.png"),
		ENEMY_BULLET("ENEMY_BULLET.png"),
		ENEMY_DEBRIS("ENEMY_DEBRIS.png"),
		TROLL_SCOUT("TROLL_SCOUT.png"),
		TROLL_MOTHERSHIP("TROLL_MOTHERSHIP.png"),
		TROLL_POD("TROLL_POD.png"),
		LARGE_ASTEROID("LARGE_ASTEROID.png"),
		MEDIUM_ASTEROID("MEDIUM_ASTEROID.png"),
		SMALL_ASTEROID("SMALL_ASTEROID.png"),
		TROLL_1("TROLL_1.png"),
		TROLL_2("TROLL_2.png"),
		POD_SPRITE("POD_SPRITE.png"),
		MYTHICITE("MYTHICITE.png"),
		
		
		//AsteRAGE 2 Graphics:
		A2_PLAYER_SHIP("A2_PLAYER_SHIP.png"),
		A2_TROLL_MINING_POD("A2_TROLL_MINING_POD.png"),
		
		A2_ASTEROID_SMALL_WHITE("A2_ASTEROID_SMALL_WHITE.png"),
		A2_ASTEROID_MEDIUM_WHITE("A2_ASTEROID_MEDIUM_WHITE.png"),
		A2_ASTEROID_LARGE_WHITE("A2_ASTEROID_LARGE_WHITE.png"),
		
		A2_ASTEROID_SMALL_TAN("A2_ASTEROID_SMALL_TAN.png"),
		A2_ASTEROID_MEDIUM_TAN("A2_ASTEROID_MEDIUM_TAN.png"),
		A2_ASTEROID_LARGE_TAN("A2_ASTEROID_LARGE_TAN.png"),
		
		A2_ASTEROID_SMALL_RED("A2_ASTEROID_SMALL_RED.png"),
		A2_ASTEROID_MEDIUM_RED("A2_ASTEROID_MEDIUM_RED.png"),
		A2_ASTEROID_LARGE_RED("A2_ASTEROID_LARGE_RED.png"),
		
		A2_ASTEROID_SMALL_PURPLE("A2_ASTEROID_SMALL_PURPLE.png"),
		A2_ASTEROID_MEDIUM_PURPLE("A2_ASTEROID_MEDIUM_PURPLE.png"),
		A2_ASTEROID_LARGE_PURPLE("A2_ASTEROID_LARGE_PURPLE.png"),
		
		A2_PLASMA_BOLT_PLAYER("A2_PLASMA_BOLT_PLAYER.png"),
		A2_PLASMA_BOLT_ENEMY("A2_PLASMA_BOLT_ENEMY.png"),
		
		A2_HOMING_MISSILE_PLAYER("A2_HOMING_MISSILE_PLAYER.png"),
		A2_HOMING_MISSILE_TROLL("A2_HOMING_MISSILE_TROLL.png"),
		
		A2_WARP_OUT_GRAPHIC("A2_WARP_OUT_GRAPHIC.png"),
		
		//Power-up menu icons
		A2_POWER_UP_ICON_DECELERATION("A2_POWER_UP_ICON_DECELERATION.png"),
		A2_POWER_UP_ICON_DECELERATION_SELECTED("A2_POWER_UP_ICON_DECELERATION_SELECTED.png"),
		A2_POWER_UP_ICON_MULTISHOT("A2_POWER_UP_ICON_MULTISHOT.png"),
		A2_POWER_UP_ICON_MULTISHOT_SELECTED("A2_POWER_UP_ICON_MULTISHOT_SELECTED.png"),
		
		A2_POWER_UP_ICON_HOMING_MISSILE("A2_POWER_UP_ICON_HOMING_MISSILE.png"),
		A2_POWER_UP_ICON_HOMING_MISSILE_SELECTED("A2_POWER_UP_ICON_HOMING_MISSILE_SELECTED.png"),
		A2_POWER_UP_ICON_SHIELD_GENERATOR("A2_POWER_UP_ICON_SHIELD_GENERATOR.png"),
		A2_POWER_UP_ICON_SHIELD_GENERATOR_SELECTED("A2_POWER_UP_ICON_SHIELD_GENERATOR_SELECTED.png"),
		
		A2_POWER_UP_ICON_POINTS("A2_POWER_UP_ICON_POINTS.png"),
		A2_POWER_UP_ICON_POINTS_SELECTED("A2_POWER_UP_ICON_POINTS_SELECTED.png"),
		
		//Ship system icons
		A2_SYSTEM_ICON_DECELERATION_LEVEL_0("A2_SYSTEM_ICON_DECELERATION_LEVEL_0.png"),
		A2_SYSTEM_ICON_DECELERATION_LEVEL_1("A2_SYSTEM_ICON_DECELERATION_LEVEL_1.png"),
		A2_SYSTEM_ICON_DECELERATION_LEVEL_2("A2_SYSTEM_ICON_DECELERATION_LEVEL_2.png"),
		
		A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_0("A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_0.png"),
		A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_1("A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_1.png"),
		A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_2("A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_2.png"),
		
		A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_0("A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_0.png"),
		A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_1("A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_1.png"),
		A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_2("A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_2.png"),
		
		A2_SYSTEM_ICON_MULTISHOT_LEVEL_0("A2_SYSTEM_ICON_MULTISHOT_LEVEL_0.png"),
		A2_SYSTEM_ICON_MULTISHOT_LEVEL_1("A2_SYSTEM_ICON_MULTISHOT_LEVEL_1.png"),
		A2_SYSTEM_ICON_MULTISHOT_LEVEL_2("A2_SYSTEM_ICON_MULTISHOT_LEVEL_2.png"),
		
		A2_SYSTEM_ICON_SONIC_DISRUPTOR_DISABLED("A2_SYSTEM_ICON_SONIC_DISRUPTOR_DISABLED.png"),
		A2_SYSTEM_ICON_SONIC_DISRUPTOR_ENABLED("A2_SYSTEM_ICON_SONIC_DISRUPTOR_ENABLED.png"),
		
		A2_SYSTEM_ICON_GRAVITY_NET_DISABLED("A2_SYSTEM_ICON_GRAVITY_NET_DISABLED.png"),
		A2_SYSTEM_ICON_GRAVITY_NET_ENABLED("A2_SYSTEM_ICON_GRAVITY_NET_ENABLED.png"),
		
		
		//power up icons
		A2_POWER_UP_GRAVITY_NET("A2_POWER_UP_GRAVITY_NET.png"),
		A2_POWER_UP_SHIELD_RESTORATION("A2_POWER_UP_SHIELD_RESTORATION.png"),
		A2_POWER_UP_SONIC_DISRUPTOR("A2_POWER_UP_SONIC_DISRUPTOR.png"),
		A2_MYTHICITE("A2_MYTHICITE.png");
		
		
			
		private String filename;
		private BufferedImage image;
		
		Image ( String passedFilename )
		{
			this.filename = passedFilename;
		} //end 
		
		public String getFilename ()
		{
			return this.filename;
		} //end function getFilename
		public BufferedImage getImage ()
		{
			return this.image;
		} //end function getImage
		public void setImage ( BufferedImage passedImage )
		{
			this.image = passedImage;
		} //end function setImage
		
	} //end enumerated constant ImageList definition
}
