package com.rfhoodrdm.asterage2.gui.asterage1;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.gameObjects.asterage1.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.asterage1.Asterage1State;

import lombok.extern.slf4j.Slf4j;

/**
 * The game board where the space objects are drawn for the Asterage 1 game.
 */
@Component
@Slf4j
public class Asterage1GameBoard
	extends PanelTemplate {
	
	private static final long serialVersionUID = -5205117325386321906L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public final int boardWidth = 1200;
	public final int boardHeight = 600;
	
	private final Asterage1State asterage1State;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage1GameBoard(Asterage1State asterage1State) {
		this.setBounds( 0, 0, boardWidth, boardHeight );
		//this.setBorder( null );								//get rid of the border; it's painting funny.
		this.asterage1State = asterage1State;
	} //end constructor
	

	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void repaintGameBoard() {
		this.repaint();
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		ConcurrentLinkedQueue<SpaceObject> spaceObjectList = asterage1State.getSpaceObjectList();
		for ( SpaceObject currentObject : spaceObjectList )	{
			if ( currentObject instanceof PlayerShip ) {
				continue;	//player ship goes last, to show it on the very top.
			} 
			
			currentObject.paintToBoard( g );
		}

		asterage1State.getPlayerShip().paintToBoard(g); //paint the ship last.
		Toolkit.getDefaultToolkit().sync();	//flush repaints, to cure stuttering.
	}
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
