
package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import javax.swing.JPanel;

import com.rfhoodrdm.asterage2.state.Asterage2State;

/**
 * Base class of Widgets for the HUD display in AsteRAGE 2
 */
public abstract class BaseAsterageWidget
	extends JPanel {
	private static final long serialVersionUID = 2856286573523841000L;
	
	public BaseAsterageWidget()	{
		//set common attributes here
		
		setOpaque( false );			//set to see-through background.
		//setBorder( new javax.swing.border.LineBorder(Color.yellow, 2, true));		//make more visible for debugging
	} 
	
	/**
	 * Initialize the sub-components of the widget.
	 */
	abstract protected void initializeSubComponents();
	abstract public void updateDisplay( Asterage2State asterage2State );
} 
