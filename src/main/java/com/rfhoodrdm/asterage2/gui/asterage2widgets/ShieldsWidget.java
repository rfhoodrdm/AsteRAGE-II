

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JProgressBar;

import com.rfhoodrdm.asterage2.state.Asterage2State;


public class ShieldsWidget
extends BaseAsterageWidget
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	HUDLabel shieldsDescriptionLabel;	//"Shields:"
	JProgressBar shieldStrengthBar;		//shows remaining shields
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public ShieldsWidget()
	{
		super();
		initializeSubComponents();
	} //end constructor
	
	@Override
	protected void initializeSubComponents()
	{
		//initialize components
		shieldsDescriptionLabel = new HUDLabel("Shields:", HUDLabel.HUDLabelType.STANDARD);
		shieldStrengthBar = new JProgressBar();
		shieldStrengthBar.setValue(100);
		
		//set basic layout info, and common layout attributes.
		GridBagLayout shieldsLayout = new GridBagLayout();
		this.setLayout(shieldsLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(5,10,5,10);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.gridy = 0;
		layoutInfo.weighty = 1;
		
		//now add the components.
		layoutInfo.gridx = 0;
		layoutInfo.weightx = 0;
		add ( shieldsDescriptionLabel, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.weightx = 1;
		add ( shieldStrengthBar, layoutInfo );
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
		int shieldsRemainingPercent = asterage2State.getRemainingShieldPercentage();
		shieldStrengthBar.setValue( shieldsRemainingPercent );
	} //end method updateDisplay
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
} //end class ShieldsWidget
