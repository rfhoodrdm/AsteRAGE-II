package com.rfhoodrdm.asterage2.gui.asterage1;

import java.awt.Dimension;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * The top-level panel for the Asterage 1 game.
 */
@Component
@Slf4j
public class Asterage1GameScreen
	extends PanelTemplate {
	
	private static final long serialVersionUID = 3577262763724502776L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private final Asterage1HUD asterage1HUD;
	private final Asterage1GameBoard asterage1GameBoard;
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage1GameScreen (Asterage1HUD asterage1HUD, Asterage1GameBoard asterage1GameBoard) {
		super();
		
		//set size and layout
		Dimension panelSize = new Dimension(GUI.panelWidth, GUI.panelHeight);
		this.setSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setMaximumSize(panelSize);
		this.setLayout ( null );
		
		//create and initialize component objects.
		this.asterage1HUD = asterage1HUD;
		this.asterage1GameBoard = asterage1GameBoard;
		this.add( asterage1GameBoard );
		this.add( asterage1HUD );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
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
