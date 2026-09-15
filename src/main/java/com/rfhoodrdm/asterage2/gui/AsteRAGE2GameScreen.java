/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import gui.asterage2widgets.ShipPowerupStatusWidget;
import gui.templates.PanelTemplate;
import java.awt.GridBagConstraints;
import state.Asterage2State;

/**
 *
 * @author roberthood
 */
public class AsteRAGE2GameScreen
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	AsteRAGE2HUD asterage2HUD;					//hud panel for game stats.
	AsteRAGE2GameBoard asterage2GameBoard;		//game board for all game pieces
	Asterage2State asterage2State;				//reference to game state object
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public AsteRAGE2GameScreen()
	{
		super();		//call to super class constructor
		
		//set size and layout
		this.setSize ( GUI.panelWidth, GUI.panelHeight );
		this.setLayout ( new java.awt.GridBagLayout() );
		
		asterage2GameBoard = new AsteRAGE2GameBoard();
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 0;
		layoutInfo.weightx = 1;
		layoutInfo.weighty = 1;
		layoutInfo.fill = GridBagConstraints.BOTH;
		this.add( asterage2GameBoard, layoutInfo);
		
		
		asterage2HUD = new AsteRAGE2HUD();
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 1;
		layoutInfo.weightx = 1;
		layoutInfo.weighty = 0.0;
		layoutInfo.fill = GridBagConstraints.HORIZONTAL;
		this.add ( asterage2HUD , layoutInfo );
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void setAsterage2State ( Asterage2State passedState )
	{
		asterage2State = passedState;
		asterage2HUD.setAsterage2State(passedState);
		asterage2GameBoard.setAsterage2State ( passedState );
	} //end function setAsterage1State
	
	
	public void updateGameDisplay()	{	asterage2HUD.updateGameDisplay();	} //end method updateGameDisplay
	
	public void addHUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation )
	{
		asterage2HUD.addHUDExplosion(whichSystemLocation);
	} //end method addHUDExplision
	
	public void addPowerUpPointsHUDExplosion()
	{
		asterage2HUD.addPowerUpPointsHUDExplosion();
	} //end method addPowerUpPointsHUDExplosion
	 
	public void setPopUpText( String message, AsteRAGE2GameBoard.PopUpMessageLabel.MessageType whatType ) 
	{ 
		asterage2GameBoard.setPopUpText(message, whatType);
	} //end method setPopUpText
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class AsteRAGE2GameScreen definition
