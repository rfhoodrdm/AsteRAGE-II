

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JProgressBar;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

@Component
public class ShieldsWidget
	extends BaseAsterageWidget {
	
	private static final long serialVersionUID = -7418156891737762977L;
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private HUDLabel shieldsDescriptionLabel;	//"Shields:"
	private JProgressBar shieldStrengthBar;		//shows remaining shields
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public ShieldsWidget()	{
		super();
		initializeSubComponents();
	}
	
	@Override
	protected void initializeSubComponents() {
		//initialize components
		shieldsDescriptionLabel = new HUDLabel("Shields:", HUDLabel.HUDLabelType.STANDARD);
		shieldStrengthBar = new JProgressBar();
		shieldStrengthBar.setValue(100);
		shieldStrengthBar.setForeground(Color.yellow);
		shieldStrengthBar.setBackground(Color.DARK_GRAY);
		
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
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 *
	 */
	@Override
	public void updateDisplay( Asterage2State asterage2State ) {
		int shieldsRemainingPercent = asterage2State.getRemainingShieldPercentage();
		shieldStrengthBar.setValue( shieldsRemainingPercent );
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
