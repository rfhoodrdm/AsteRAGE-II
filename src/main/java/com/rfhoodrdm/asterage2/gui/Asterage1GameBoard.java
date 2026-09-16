package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import java.awt.Color;
import com.rfhoodrdm.asterage2.state.Asterage1State;
import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.PlayerShip;
/**
 * The game board where the space objects are drawn for the Asterage 1 game.
 */
public class Asterage1GameBoard
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public final int boardWidth = 1200;
	public final int boardHeight = 600;
	
	Asterage1State asterage1State;
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage1GameBoard()
	{
		this.setBounds( 0, 0, boardWidth, boardHeight );
		//this.setBorder( null );								//get rid of the border; it's painting funny.
	} //end constructor
	
	public void setAsterage1State ( Asterage1State passedState )
	{
		this.asterage1State = passedState;
	} //end function setAsterage1State
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public void repaintGameBoard()
	{
		this.repaint();
	} //end function repaintGameBoard
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override
	protected void paintComponent( Graphics g )
	{
		//call super class paint 
		super.paintComponent(g);
		
		//grab the list of SpaceObjects and paint them to the screen.
		ConcurrentLinkedQueue<SpaceObject> spaceObjectList = asterage1State.getSpaceObjectList();
		for ( SpaceObject currentObject : spaceObjectList )
		{
			//check if the current object is the player ship. We draw that last, below.
			if ( currentObject instanceof PlayerShip )
			{
				continue;	//skip this iteration.
			} 
			//else
			currentObject.paintToBoard( g );
			
		} //end for loop iterating through the list.
		
		//paint the ship last.
		asterage1State.getPlayerShip().paintToBoard(g);
		
		
	} //end function paintComponent.
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
