/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author roberthood
 */
public class PowerUpMenuIconWidget
extends JButton
{
	public static final Dimension powerUpIconDimension = new Dimension(50,50);
	
	public PowerUpMenuIconWidget( Icon icon )
	{
		super(icon);
		setCommonAttributes();
		setMaximumSize(powerUpIconDimension);
		setMinimumSize(powerUpIconDimension);
		setPreferredSize(powerUpIconDimension);
		setContentAreaFilled(false);
		setSize(powerUpIconDimension);
	} //end constructor with Icon
	
	private void setCommonAttributes()
	{
		setBorderPainted(false);		//don't show the border, only the icon
	}
} //end class PowerUpMenuIconWidget
