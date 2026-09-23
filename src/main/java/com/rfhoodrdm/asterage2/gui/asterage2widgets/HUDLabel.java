
package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;

/**
 * Base class of an HUD label, to display text on the HUD.
 */
public class HUDLabel
	extends JLabel {
	
	private static final long serialVersionUID = -6043460682914310528L;
	HUDLabelType whatType = HUDLabelType.STANDARD;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public HUDLabel( String textToDisplay, HUDLabelType passedType ) {
		super( textToDisplay );
		whatType = passedType;
		setCommonAttributes();
	} 
	
	public HUDLabel( HUDLabelType passedType ) {
		super();
		whatType = passedType;
		setCommonAttributes();
	} 
	
	private void setCommonAttributes() {
		String fontNameSelected = ( whatType == HUDLabelType.STANDARD ) ?
					GameConstants.gameFont  : GameConstants.monospaceGameFont;
		
		this.setFont( new java.awt.Font( fontNameSelected, Font.BOLD, GameConstants.Asterage2HUDFontSize ));
		this.setForeground( Color.RED );
		this.setHorizontalAlignment( SwingConstants.LEFT );
	}
	
	public static enum HUDLabelType
	{
		STANDARD,
		MONOSPACE;
	} 			
} 
