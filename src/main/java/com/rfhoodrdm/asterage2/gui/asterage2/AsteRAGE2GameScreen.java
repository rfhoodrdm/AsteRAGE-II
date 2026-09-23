package com.rfhoodrdm.asterage2.gui.asterage2;

import java.awt.GridBagConstraints;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AsteRAGE2GameScreen
	extends PanelTemplate {
	private static final long serialVersionUID = 4504007243932733606L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private final AsteRAGE2HUD asterage2HUD;					//hud panel for game stats.
	private final AsteRAGE2GameBoard asterage2GameBoard;		//game board for all game pieces

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public AsteRAGE2GameScreen(Asterage2State asterage2State, AsteRAGE2HUD asterage2HUD, AsteRAGE2GameBoard asterage2GameBoard) {
		super();		//call to super class constructor

		this.asterage2HUD = asterage2HUD;
		this.asterage2GameBoard = asterage2GameBoard;
		
		//set size and layout
		this.setSize ( GUI.panelWidth, GUI.panelHeight );
		this.setLayout ( new java.awt.GridBagLayout() );
		
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 0;
		layoutInfo.weightx = 1;
		layoutInfo.weighty = 1;
		layoutInfo.fill = GridBagConstraints.BOTH;
		this.add( asterage2GameBoard, layoutInfo);
		
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 1;
		layoutInfo.weightx = 1;
		layoutInfo.weighty = 0.0;
		layoutInfo.fill = GridBagConstraints.HORIZONTAL;
		this.add ( asterage2HUD , layoutInfo );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void updateGameDisplay()	{	
		asterage2HUD.updateGameDisplay();	
	} 
	
	public void addHUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation )	{
		asterage2HUD.addHUDExplosion(whichSystemLocation);
	}
	public void addPowerUpPointsHUDExplosion()	{
		asterage2HUD.addPowerUpPointsHUDExplosion();
	}
	 
	public void setPopUpText( String message, AsteRAGE2GameBoard.PopUpMessageLabel.MessageType whatType ) { 
		asterage2GameBoard.setPopUpText(message, whatType);
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} 
