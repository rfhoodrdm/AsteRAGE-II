
package com.rfhoodrdm.asterage2.gui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.gui.asterage1.Asterage1GameScreen;
import com.rfhoodrdm.asterage2.gui.asterage2.AsteRAGE2GameBoard;
import com.rfhoodrdm.asterage2.gui.asterage2.AsteRAGE2GameScreen;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.gui.titlescreen.SplashScreen;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.State;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GUI is the top class in the graphical user interface. It holds the game frame, game panels,
 * and sound manager. It also contains references to the other modules.
 */
@Slf4j
@Component
public class GUI {
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    
	//GUI constants.
	public final static int panelWidth = 1200;	    //height of one game panel.
    public final static int panelHeight = 700;	    //width of one game panel.
	
	private final State state;
	
	//references to GUI components.
	private final GameFrame gameFrame;
	private final SplashScreen splashScreen;
	private final Asterage1GameScreen asterage1GameScreen;
	private final AsteRAGE2GameScreen asterage2GameScreen;
	private final SoundManager soundManager;
	
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public GUI (State state, SoundManager soundManager, GameFrame gameFrame, 
			SplashScreen splashScreen, Asterage1GameScreen asterage1GameScreen, AsteRAGE2GameScreen asterage2GameScreen) {
		this.state = state;
		this.soundManager = soundManager;
		
		this.gameFrame = gameFrame;
		
		this.splashScreen = splashScreen;
		splashScreen.setVisible ( true );
		
		this.asterage1GameScreen = asterage1GameScreen;
		asterage1GameScreen.setVisible( false );
		
		this.asterage2GameScreen = asterage2GameScreen;
		asterage2GameScreen.setVisible(false);
	} 
	
	public void showInitialGUI ()	{
		gameFrame.setVisible( true );
		changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_TITLE_SCREEN);
		gameFrame.requestFocus();
	}

	//TODO: Make this the bridge between the GUI and the controller in postconstruction, above!
//	public void setController ( Controller passedController ) 	{
//		this.controller = passedController;
//		
//		//attach the key adapter to capture key presses and releases. Pass it the controller reference.
//		GameKeyAdapter gameKeyAdapter = new GameKeyAdapter();
//		gameFrame.addKeyListener ( gameKeyAdapter ); 
//		gameKeyAdapter.setController(controller);
//	}
	
//	/**
//	 * Set the master state reference.
//	 */
//	public void setState ( State passedState )	{
//		this.state = passedState;
//		
//		//set the relevant state reference for all the GUI's top level subcomponents.

//		asterage1GameScreen.setAsterage1State ( state.getAsterage1State() );
//		asterage2GameScreen.setAsterage2State( state.getAsterage2State() );
//	}
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void repaint() {
		performOnEDT( () -> {
			splashScreen.repaint();
			asterage1GameScreen.repaintGameBoard();
			asterage2GameScreen.updateGameDisplay();
			asterage2GameScreen.repaint();
		});
	}
	
	private void performOnEDT(Runnable task) {
		if(SwingUtilities.isEventDispatchThread()) {
			task.run();
		} else {
			SwingUtilities.invokeLater(task);
		}
	}
	
	
	public void changeCurrentGuiShown()	{
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
	 * Open a simple text field dialog box, posing a question to the player, and soliciting a text reply.
	 */
	public String getReplyDialog( String title, String question) {
		return JOptionPane.showInputDialog(		gameFrame,
										 		question,
												title,
										 		JOptionPane.QUESTION_MESSAGE);
	} 
	
	/**
	 * Asks the user if they want to return to main menu, thus aborting the game.
	 * @return Boolean verdict. True = yes, false = no.
	 */
	public boolean checkAsterage2AbortGameDialog () { 
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
	
	private void changeMusicSequence( SoundManager.SOUNDTRACK_SEQUENCE newSequence ) {
		soundManager.changeMusicSequence(newSequence);
	} 
}
