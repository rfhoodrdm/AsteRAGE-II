package gui;

import gui.templates.PanelTemplate;
import state.Asterage1State;

/**
 * The top-level panel for the Asterage 1 game.
 */
public class Asterage1GameScreen
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	Asterage1HUD asterage1HUD;
	Asterage1State asterage1State;
	Asterage1GameBoard asterage1GameBoard;
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Asterage1GameScreen ()
	{
		super();
		
		//set size and layout
		this.setSize ( GUI.panelWidth, GUI.panelHeight );
		this.setLayout ( null );
		
		//create and initialize component objects.
		asterage1HUD = new Asterage1HUD();
		asterage1GameBoard = new Asterage1GameBoard();
		this.add( asterage1GameBoard );
		this.add( asterage1HUD );
		
	} //end constructor
	
	public void setAsterage1State ( Asterage1State passedState )
	{
		asterage1State = passedState;
		asterage1HUD.setAsterage1State(passedState);
		asterage1GameBoard.setAsterage1State ( passedState );
	} //end function setAsterage1State
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 * Receive and pass on the directive to update the stat display.
	 */
	public void refreshStatDisplay()
	{
		asterage1HUD.refreshStatDisplay();
	} //end function refresh.
	
	/**
	 * Repaint the game board.
	 */
	public void repaintGameBoard()
	{
		asterage1GameBoard.repaintGameBoard();
	} //end function repaintGUI
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
}
