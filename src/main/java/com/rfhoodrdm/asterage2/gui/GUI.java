
package com.rfhoodrdm.asterage2.gui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JOptionPane;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.State;

/**
 * GUI is the top class in the graphical user interface. It holds the game frame, game panels,
 * and sound manager. It also contains references to the other modules.
 */



@Slf4j
public class GUI
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    
	//GUI constants.
	public final static int panelWidth = 1200;	    //height of one game panel.
    public final static int panelHeight = 700;	    //width of one game panel.
	
	//references to other components.
	private Controller controller;
	private State state;
	
	//references to GUI components.
	private GameFrame gameFrame;
	private SplashScreen splashScreen;
	private Asterage1GameScreen asterage1GameScreen;
	private AsteRAGE2GameScreen asterage2GameScreen;
	private SoundManager soundManager;
	
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public GUI ()	{
		gameFrame = new GameFrame( this );
		
		splashScreen = new SplashScreen ( this );
		splashScreen.setVisible ( true );
		asterage1GameScreen = new Asterage1GameScreen();
		asterage1GameScreen.setVisible( false );
		asterage2GameScreen = new AsteRAGE2GameScreen();
		asterage2GameScreen.setVisible(false);
		soundManager = new SoundManager();
		soundManager.start();								//start the sound manager.
		
		gameFrame.setIgnoreRepaint(false);
		
		gameFrame.add( splashScreen );
		gameFrame.add ( asterage1GameScreen );
		gameFrame.add( asterage2GameScreen );
	} 
	
	public void showInitialGUI ()	{
		gameFrame.setVisible( true );
		changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_TITLE_SCREEN);
		gameFrame.requestFocus();
	}
	
	public void setController ( Controller passedController ) 	{
		this.controller = passedController;
		
		//attach the key adapter to capture key presses and releases. Pass it the controller reference.
		GameKeyAdapter gameKeyAdapter = new GameKeyAdapter();
		gameFrame.addKeyListener ( gameKeyAdapter ); 
		gameKeyAdapter.setController(controller);
	}
	
	/**
	 * Set the master state reference.

	 */
	public void setState ( State passedState )	{
		this.state = passedState;
		
		//set the relevant state reference for all the GUI's top level subcomponents.
		splashScreen.setTitleState( state.getTitleState() );
		splashScreen.setAsterage1State( state.getAsterage1State() );
		splashScreen.setAsterage2State( state.getAsterage2State() );
		asterage1GameScreen.setAsterage1State ( state.getAsterage1State() );
		asterage2GameScreen.setAsterage2State( state.getAsterage2State() );
	}
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Refresh the title game menu instead of repainting the whole screen. It saves computation power.
	 */
	public void refreshTitleGameMenu()
	{
		splashScreen.refreshTitleGameMenu();
	} 
	
	/**
	 * Repaint the entire splash screen, if something was added or changed.
	 */
	public void refreshSplashScreen()
	{
		splashScreen.repaint();
	} 
	
	/**
	 * Takes in a request to play a sound.
	 */
	public void playSoundForEvent ( SoundManager.SOUND_EVENT soundEvent )
	{
		soundManager.playSoundEvent( soundEvent );
	} 
	
	public void haltSoundForEvent ( SoundManager.SOUND_EVENT soundEvent )
	{
		soundManager.stopSoundEvent(soundEvent);
	}
	
	public void assertFocusOnFrame()
	{
		gameFrame.requestFocus();
	} 
	
	public void changeCurrentGuiShown()
	{
		//hide all of the GUI panels to avoid complex logic.
		splashScreen.setVisible(false);
		asterage1GameScreen.setVisible(false);
		asterage2GameScreen.setVisible(false);
		
		//get the current active state.
		CurrentState currentState = state.getCurrentState();
		log.debug("Changing GUI shown: {}", currentState);
		
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
				
		} 
	} 
	
	/**
	 * Makes a call to the sound manager on behalf of the caller to change the music being played.
	 * @param newSequence 
	 */
	public void changeMusicSequence( SoundManager.SOUNDTRACK_SEQUENCE newSequence )
	{
		soundManager.changeMusicSequence(newSequence);
	} 
	
	/**
	 * Receive and pass forward request to update the Asterage 1 stat display
	 */
	public void refreshAsterage1StatDisplay()
	{
		asterage1GameScreen.refreshStatDisplay();
	} 
	
	public void repaintAsterage1GameBoard ()
	{
		asterage1GameScreen.repaintGameBoard();
	}
	
	public void repaintAsteRAGE2Display()
	{
		asterage2GameScreen.repaint();
		asterage2GameScreen.updateGameDisplay();
	} 
	
	
	/**
	 * Open a simple text field dialog box, posing a question to the player, and soliciting a text reply.
	 */
	public String getReplyDialog( String title, String question)
	{
		return JOptionPane.showInputDialog(		gameFrame,
										 		question,
												title,
										 		JOptionPane.QUESTION_MESSAGE);
	} 
	
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
	} 
	
	
	public void addAsterage2HUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation ) {
		asterage2GameScreen.addHUDExplosion(whichSystemLocation);
	}
	
	public void addPowerUpPointsHUDExplosion() {
		asterage2GameScreen.addPowerUpPointsHUDExplosion();
	}
	
	public void setAsterage2PopUpText( String message, AsteRAGE2GameBoard.PopUpMessageLabel.MessageType whatType ) { 
		asterage2GameScreen.setPopUpText(message, whatType);
	} 
	
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */


}
