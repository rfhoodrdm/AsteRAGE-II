package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.Asterage1State;

/**
 * The top-level panel for the Asterage 1 game.
 */
public class Asterage1GameScreen
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private Asterage1HUD asterage1HUD;
	private Asterage1State asterage1State;
	private Asterage1GameBoard asterage1GameBoard;
	
	
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
		
	} 
	
	public void setAsterage1State ( Asterage1State passedState )
	{
		asterage1State = passedState;
		asterage1HUD.setAsterage1State(passedState);
		asterage1GameBoard.setAsterage1State ( passedState );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
//	
//	/**
//	 * Receive and pass on the directive to update the stat display.
//	 */
//	private void refreshStatDisplay()
//	{
//		asterage1HUD.refreshStatDisplay();
//	} 
	
	/**
	 * Repaint the game board.
	 */
	public void repaintGameBoard() {
		asterage1HUD.refreshStatDisplay();
		asterage1GameBoard.repaintGameBoard();
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
}
