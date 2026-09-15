
package gui.templates;

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
	/**
	 * The constructor sets all the default, common attributes of the game panels.
	 */
	public PanelTemplate ()
	{
		setBackground( new Color ( 0, 0, 0) );		// background color, black.
		setLayout ( null );							//honor coordinates of sub components.
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, 
						new Color( 255, 255, 255), new Color( 150, 150, 150) );
		this.setBorder( etchedBorder );
		
	} //end constructor
	
}
