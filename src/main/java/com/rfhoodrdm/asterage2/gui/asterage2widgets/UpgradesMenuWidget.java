/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import com.rfhoodrdm.asterage2.gui.GUI;
import javax.swing.ImageIcon;
import com.rfhoodrdm.asterage2.state.Asterage2State;

import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption;
import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.*;

/**
 *
 * @author roberthood
 */
public class UpgradesMenuWidget
extends BaseAsterageWidget
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	HUDLabel powerUpMenuDescription;		//"Select:"
	
	PowerUpMenuIconWidget decelerationPowerUp;		//power up to slow down the ship
	PowerUpMenuIconWidget shieldGeneratorPowerUp;	//power up for increased shield regeneration
	PowerUpMenuIconWidget homingMissileShotPowerUp;	//power up to shoot a guided missile at the aliens
	PowerUpMenuIconWidget extraLifePowerUp;			//power up for an extra life.
	PowerUpMenuIconWidget multiShotPowerUp;			//power up to shoot multiple shots at once.
	
	ImageIcon decelerationOptionIcon; 
	ImageIcon decerationOptionSelectedIcon;
	ImageIcon shieldGeneratorOptionIcon;
	ImageIcon shieldGeneratorOptionSelectedIcon;
	
	ImageIcon homingMissileOptionIcon;
	ImageIcon homingMissileOptionSelectedIcon;
	ImageIcon extraLifeOptionIcon;
	ImageIcon extraLifeOptionSelectedIcon;
	
	ImageIcon multiShotOptionIcon;
	ImageIcon multiShotOptionSelectedIcon;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public UpgradesMenuWidget()
	{
		super();					//call to super's constructor
		initializeSubComponents();	//initialize the pieces of this widget
	} //end constructor
	
	@Override
	protected void initializeSubComponents()
	{
		decelerationOptionIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_DECELERATION.getImage() );
		decerationOptionSelectedIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_DECELERATION_SELECTED.getImage() );
		shieldGeneratorOptionIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_SHIELD_GENERATOR.getImage() );
		shieldGeneratorOptionSelectedIcon =  new ImageIcon( GUI.Image.A2_POWER_UP_ICON_SHIELD_GENERATOR_SELECTED.getImage() );
		
		homingMissileOptionIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_HOMING_MISSILE.getImage() );
		homingMissileOptionSelectedIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_HOMING_MISSILE_SELECTED.getImage() );
		extraLifeOptionIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_POINTS.getImage() );
		extraLifeOptionSelectedIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_POINTS_SELECTED.getImage() );

		multiShotOptionIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_MULTISHOT.getImage() );
		multiShotOptionSelectedIcon = new ImageIcon( GUI.Image.A2_POWER_UP_ICON_MULTISHOT_SELECTED.getImage() );
		
		powerUpMenuDescription = new HUDLabel("Select:", HUDLabel.HUDLabelType.STANDARD);
		
		decelerationPowerUp = new PowerUpMenuIconWidget( decelerationOptionIcon );
		shieldGeneratorPowerUp = new PowerUpMenuIconWidget( shieldGeneratorOptionIcon );
		homingMissileShotPowerUp = new PowerUpMenuIconWidget( homingMissileOptionIcon );
		extraLifePowerUp = new PowerUpMenuIconWidget( extraLifeOptionIcon );
		multiShotPowerUp = new PowerUpMenuIconWidget( multiShotOptionIcon );
		
		//set basic layout info, and common layout attributes.
		GridBagLayout upgradesMenuLayout = new GridBagLayout();
		this.setLayout(upgradesMenuLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(0,5,0,5);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.gridy = 0;
		layoutInfo.weighty = 0;
		
		layoutInfo.gridx = 0;
		layoutInfo.weightx = 0.0;
		layoutInfo.anchor = GridBagConstraints.LINE_START;
		add ( powerUpMenuDescription, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( decelerationPowerUp, layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( homingMissileShotPowerUp, layoutInfo );
		
		layoutInfo.gridx = 3;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( shieldGeneratorPowerUp, layoutInfo );
	
		layoutInfo.gridx = 4;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( extraLifePowerUp, layoutInfo );
		
		layoutInfo.gridx = 5;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( multiShotPowerUp, layoutInfo );
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
		//get the currently selected powerup from the state object, and show the appropriate icons for all menu options.
		PowerUpMenuOption currentlySelectedOption = asterage2State.getCurrentSelectedPowerUpMenuOption();
		
		ImageIcon decelerationIconToDisplay = (currentlySelectedOption == DECELERATION )
						?	decerationOptionSelectedIcon : decelerationOptionIcon;
		decelerationPowerUp.setIcon(decelerationIconToDisplay);
		
		ImageIcon shieldGeneratorIconToDisplay = (currentlySelectedOption == SHIELD_GENERATOR )
						?	shieldGeneratorOptionSelectedIcon : shieldGeneratorOptionIcon;
		shieldGeneratorPowerUp.setIcon(shieldGeneratorIconToDisplay);
		
		ImageIcon homingMissileIconToDisplay = (currentlySelectedOption == HOMING_MISSILE )
						?	homingMissileOptionSelectedIcon : homingMissileOptionIcon;
		homingMissileShotPowerUp.setIcon(homingMissileIconToDisplay);
		
		ImageIcon extraLifeIconToDisplay = (currentlySelectedOption == EXTRA_POINTS )
						?	extraLifeOptionSelectedIcon : extraLifeOptionIcon;
		extraLifePowerUp.setIcon(extraLifeIconToDisplay);
		
		ImageIcon multiShotIconToDisplay = (currentlySelectedOption == MULTISHOT )
						?	multiShotOptionSelectedIcon : multiShotOptionIcon;
		multiShotPowerUp.setIcon(multiShotIconToDisplay);
		
	} //end method updateDisplay
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class UpgradesMenuWidget
