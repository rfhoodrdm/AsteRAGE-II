

package com.rfhoodrdm.asterage2.gui.asterage2;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.gui.asterage2widgets.LivesLevelScoreWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.PowerPointsWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShieldsWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.ShipPowerupStatusWidget;
import com.rfhoodrdm.asterage2.gui.asterage2widgets.UpgradesMenuWidget;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AsteRAGE2HUD
	extends PanelTemplate {
	
	private static final long serialVersionUID = -5777319684397180053L;

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final Dimension hudDimension = new Dimension (1200, 100);
	
	private final Asterage2State asterage2State;		
	
	private final LivesLevelScoreWidget livesLevelScoreWidget;		//shows lives, level and score
	private final ShieldsWidget shieldsWidget;						//shows remaining shield strength
	private final UpgradesMenuWidget upgradesMenuWidget;			//shows purchasable upgrades
	private final PowerPointsWidget powerPointsWidget;				//shows accumulated power points
	private final ShipPowerupStatusWidget shipPowerupStatusWidget;	//shows which power-ups have been acquired and/or purchased.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public AsteRAGE2HUD(Asterage2State asterage2State,
			LivesLevelScoreWidget livesLevelScoreWidget, ShieldsWidget shieldsWidget, UpgradesMenuWidget upgradesMenuWidget, 
			PowerPointsWidget powerPointsWidget, ShipPowerupStatusWidget shipPowerupStatusWidget)	{
		super();
		
		this.asterage2State = asterage2State;
		this.livesLevelScoreWidget = livesLevelScoreWidget;
		this.shieldsWidget = shieldsWidget;
		this.upgradesMenuWidget = upgradesMenuWidget;
		this.powerPointsWidget = powerPointsWidget;
		this.shipPowerupStatusWidget = shipPowerupStatusWidget;
		
		this.setMaximumSize(hudDimension);
		this.setMinimumSize(hudDimension);
		this.setPreferredSize(hudDimension);
		this.setSize(hudDimension);
		
		initSubComponents();
	} 
	
	
	private void initSubComponents()	{ 
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

	} 

	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 * Have each HUD widget update the data that it is currently displaying
	 */
	public void updateGameDisplay()	{
		livesLevelScoreWidget.updateDisplay( asterage2State );
		shieldsWidget.updateDisplay( asterage2State );
		upgradesMenuWidget.updateDisplay( asterage2State );
		powerPointsWidget.updateDisplay( asterage2State );
		shipPowerupStatusWidget.updateDisplay( asterage2State );
	} 
	
	public void addHUDExplosion( ShipPowerupStatusWidget.SystemExplosionLocations whichSystemLocation )	{
		shipPowerupStatusWidget.addHUDExplosion(whichSystemLocation);
	}
	
	public void addPowerUpPointsHUDExplosion()	{
		powerPointsWidget.addPowerUpPointsHUDExplosion();
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */

	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} 
