/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.gui.asterage2widgets.LivesLevelScoreWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.PowerPointsWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShieldsWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.UpgradesMenuWidget;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import com.rfhoodrdm.asterage2.state.Asterage2State;

/**
 *
 * @author roberthood
 */
public class AsteRAGE2HUD
extends PanelTemplate
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final Dimension hudDimension = new Dimension (1200, 100);
	
	//references to other components
	Asterage2State asterage2State;		//reference to game state object
	
	LivesLevelScoreWidget livesLevelScoreWidget;		//shows lives, level and score
	ShieldsWidget shieldsWidget;						//shows remaining shield strength
	UpgradesMenuWidget upgradesMenuWidget;				//shows purchasable upgrades
	PowerPointsWidget powerPointsWidget;				//shows accumulated power points
	ShipPowerupStatusWidget shipPowerupStatusWidget;	//shows which power-ups have been acquired and/or purchased.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public AsteRAGE2HUD()
	{
		super();	//call to super class's constructor
		this.setMaximumSize(hudDimension);
		this.setMinimumSize(hudDimension);
		this.setPreferredSize(hudDimension);
		this.setSize(hudDimension);
		
		initSubComponents();
	} //end constructor
	
	
	private void initSubComponents()
	{ 
		livesLevelScoreWidget = new LivesLevelScoreWidget();
		shieldsWidget = new ShieldsWidget();
		upgradesMenuWidget = new UpgradesMenuWidget();
		powerPointsWidget = new PowerPointsWidget();
		shipPowerupStatusWidget = new ShipPowerupStatusWidget();
		
		GridBagLayout hudLayout = new GridBagLayout();
		setLayout( hudLayout );
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.fill = GridBagConstraints.BOTH;
		
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 0;
		layoutInfo.weightx = 0.0;
		layoutInfo.weighty = 0.1;
		add( livesLevelScoreWidget, layoutInfo );
		
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 1;
		layoutInfo.weightx = 0.1;
		layoutInfo.weighty = 0.1;
		add( shieldsWidget, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.gridy = 0;
		layoutInfo.weightx = 0.1;
		layoutInfo.weighty = 0.1;
		add( upgradesMenuWidget, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.gridy = 1;
		layoutInfo.weightx = 0.1;
		layoutInfo.weighty = 0.1;
		add( powerPointsWidget, layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.gridy = 0;
		layoutInfo.gridheight = 2;
		layoutInfo.weightx = 0.1;
		layoutInfo.weighty = 0.1;
		add( shipPowerupStatusWidget, layoutInfo );
	
		
	} //end method initSubComponents
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void setAsterage2State( Asterage2State passedState )	{	asterage2State = passedState;	} 
	
	/**
	 * Have each HUD widget update the data that it is currently displaying
	 */
	public void updateGameDisplay()
	{
		livesLevelScoreWidget.updateDisplay( asterage2State );
		shieldsWidget.updateDisplay( asterage2State );
		upgradesMenuWidget.updateDisplay( asterage2State );
		powerPointsWidget.updateDisplay( asterage2State );
		shipPowerupStatusWidget.updateDisplay( asterage2State );
	} //end method updateGameDisplay
	
	public void addHUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation )
	{
		shipPowerupStatusWidget.addHUDExplosion(whichSystemLocation);
	} //end method addHUDExplision
	
	public void addPowerUpPointsHUDExplosion()
	{
		powerPointsWidget.addPowerUpPointsHUDExplosion();
	} //end method addPowerUpPointsHUDExplosion
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class AsteRAGE2HUD definition
