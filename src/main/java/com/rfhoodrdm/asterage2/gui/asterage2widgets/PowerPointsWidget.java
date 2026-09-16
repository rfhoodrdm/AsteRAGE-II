/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import com.rfhoodrdm.asterage2.gameEffects.asterage2.HUDExplosionEffect;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JProgressBar;
import com.rfhoodrdm.asterage2.state.Asterage2State;

/**
 *
 * @author roberthood
 */
public class PowerPointsWidget
extends BaseAsterageWidget
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	HUDLabel powerPointsDescriptionLabel;				//"P-UP:"
	JProgressBar powerPointsBar;						//shows remaining power points to spend
	
	HUDExplosionEffect hudExplosionEffect = null;		//effect shown if mythicite is jettisoned.

	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public PowerPointsWidget()
	{
		super();
		initializeSubComponents();
	} //end constructor
	
	@Override
	protected void initializeSubComponents()
	{
		//initialize components
		powerPointsDescriptionLabel = new HUDLabel("P-UP:", HUDLabel.HUDLabelType.STANDARD);
		powerPointsBar = new JProgressBar();
		powerPointsBar.setValue(0);
		powerPointsBar.setMaximum( Asterage2State.MAX_POWER_UP_POINTS );
		powerPointsBar.setStringPainted(true);

		//set basic layout info, and common layout attributes.
		GridBagLayout powerPointsLayout = new GridBagLayout();
		this.setLayout(powerPointsLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(5,10,5,10);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.gridy = 0;
		layoutInfo.weighty = 1;
		
		//now add the components.
		layoutInfo.gridx = 0;
		layoutInfo.weightx = 0;
		add ( powerPointsDescriptionLabel, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.weightx = 1;
		add ( powerPointsBar, layoutInfo );
		
	} //end method initializeSubComponents
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 *
	 * @param asterage2State
	 */
	@Override
	public void updateDisplay( Asterage2State asterage2State )
	{
		int currentPowerPoints = asterage2State.getPowerUpPoints();
		powerPointsBar.setValue(currentPowerPoints);
		powerPointsBar.setString( currentPowerPoints + "/" + Asterage2State.MAX_POWER_UP_POINTS );
	} //end method updateDisplay
	
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent(g);		//call to super's paintcomponent object
		
		//if there is an HUDExplosionEffect to draw, then draw it. 
		//Also, decrement the expired countdown timer, and remove it if it has expired.
		if ( null != hudExplosionEffect ) 
		{ 
			hudExplosionEffect.paintObject(g); 
			hudExplosionEffect.decrementExpiredCountdownTimer();
			if ( hudExplosionEffect.checkExpired() ) { hudExplosionEffect = null; }
		} //end if check for handling an hud explosion effect.

	} //end method paintComponent
	
	public void addPowerUpPointsHUDExplosion()
	{
		//calculate the boundaries of the effect.
		int xLocation = 0;												//flush with the left side.
		int width = this.getWidth();									//width of the panel
		int yLocation = powerPointsDescriptionLabel.getY() - 10;		//a small space above the label/bar
		int height = this.getHeight() - yLocation;						//fill the rest of the panel down.
		
		hudExplosionEffect = new HUDExplosionEffect( xLocation, yLocation, width, height );
	} //end method createNewHUDExplosion
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class PowerPointsWidget definition
