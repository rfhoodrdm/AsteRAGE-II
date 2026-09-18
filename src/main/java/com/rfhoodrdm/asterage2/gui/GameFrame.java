package com.rfhoodrdm.asterage2.gui;

import javax.swing.JFrame;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;

import java.awt.Color;

/**
 * GameFrame is the main frame for the entire program gui. It is where the main panel is placed, which houses
 * the main title screen, and AsteRAGE 1 and AsteRAGE 2 game panels.
 */
public class GameFrame
extends JFrame
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private GUI gui;		//reference to top level component.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public GameFrame ( GUI passedGUI )
	{
		super ( GameConstants.GAME_NAME + " - " + 
				GameConstants.GAME_VERSION);						//display game title and version in window.
		this.gui = passedGUI;
		
		//set the parameters of this component.
		this.setSize( GUI.panelWidth , GUI.panelHeight + 25);	//use given component sizes. Add some pixels to accomodate menu bar size differences
		this.setLayout( null );									//layout will honor coordinates of sub components.
		this.setLocationRelativeTo( null );						//center on screen
		this.setBackground ( new Color ( 0, 0, 0) );			//solid black background
		this.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );	//exit game on closing of window.
		
		this.setIgnoreRepaint(false);
	} //end constructor.
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
