/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.asterage2widgets;

import java.awt.Color;
import javax.swing.JPanel;
import state.Asterage2State;

/**
 * Base class of Widgets for the HUD display in AsteRAGE 2
 * @author roberthood
 */
public abstract class BaseAsterageWidget
extends JPanel
{
	public BaseAsterageWidget()
	{
		//set common attributes here
		setOpaque( false );			//set to see-through background.
		//setBorder( new javax.swing.border.LineBorder(Color.yellow, 2, true));		//make more visible for debugging
	} //end constructor
	
	/**
	 * Initialize the sub-components of the widget.
	 */
	abstract protected void initializeSubComponents();
	abstract public void updateDisplay( Asterage2State asterage2State );
} //end class BaseAsterageWidget
