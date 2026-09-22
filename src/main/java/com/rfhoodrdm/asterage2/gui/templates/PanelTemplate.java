
package com.rfhoodrdm.asterage2.gui.templates;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;


/**
 * PanelTemplate holds the default panel characteristics common to all game panels,
 * used to enforce sameness of look and feel.
 */
abstract public class PanelTemplate
	extends JPanel
{
	private static final long serialVersionUID = -6163599982299353851L;

	/**
	 * The constructor sets all the default, common attributes of the game panels.
	 */
	public PanelTemplate ()	{
		setBackground( Color.BLACK );		// background color, black.
		setLayout ( null );					//honor coordinates of sub components.
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, 
						new Color( 255, 255, 255), new Color( 150, 150, 150) );
		this.setBorder( etchedBorder );
		
		this.setIgnoreRepaint(false);
		this.setDoubleBuffered(true);
	} 
}
