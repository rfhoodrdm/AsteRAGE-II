package com.rfhoodrdm.asterage2.dataloading.popup;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.sounds.Sound;

@Component
public class DataLoaderPopup {

	private final Dimension loaderSize = new Dimension ( 500, 200 );
	private final Dimension componentSize = new Dimension ( 500, 100 );
	
    private int totalDataToLoad = 0;
    private int dataLoadsCompleted = 0;

    private JFrame loaderFrame;
    private JPanel loaderPanel;
    private JLabel loaderLabel;
    private JProgressBar loaderProgressBar;
    
    
    public DataLoaderPopup() {

		//initialize the gui components.
		loaderFrame = new JFrame ("Loading " + GameConstants.GAME_NAME + " data..." );
		loaderFrame.setDefaultCloseOperation(  JFrame.DO_NOTHING_ON_CLOSE );
		loaderFrame.setLayout ( null );
		loaderFrame.setSize ( loaderSize );
		loaderFrame.setLocationRelativeTo( null );

		loaderPanel = new JPanel();
		loaderPanel.setSize ( loaderSize );
		loaderPanel.setMinimumSize( loaderSize );
		loaderPanel.setLayout ( new BorderLayout() );
		loaderPanel.setBackground(new java.awt.Color(0, 0, 0));

		loaderLabel = new JLabel();
		loaderLabel.setSize( componentSize );
		loaderLabel.setMinimumSize ( componentSize );
		loaderLabel.setPreferredSize ( componentSize );
		loaderLabel.setMaximumSize ( componentSize );
		loaderLabel.setText ( "Loading data...");
		loaderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		loaderLabel.setFont(new java.awt.Font("Courier New", 1, 20)); // NOI18N
		loaderLabel.setForeground(new java.awt.Color(255, 255, 0));

		loaderProgressBar = new JProgressBar();
		loaderProgressBar.setSize ( componentSize );
		loaderProgressBar.setMinimumSize ( componentSize );
		loaderProgressBar.setPreferredSize ( componentSize );
		loaderProgressBar.setMaximumSize ( componentSize );
		loaderProgressBar.setStringPainted ( true );
		
		//assemble everything.
		loaderFrame.add ( loaderPanel );
		loaderPanel.add ( loaderLabel );
		loaderPanel.add ( loaderProgressBar );
		
		
		// initial display values:
		loaderLabel.setText ( "Load begin.");
		loaderProgressBar.setValue(0);
		int soundCount = Sound.values().length;
		int imageCount = Image.values().length;
		int highScoreFileCount = 2;
		
		totalDataToLoad = imageCount + soundCount + highScoreFileCount;
    }
    
    public void setVisible(boolean isVisible) {
    	loaderFrame.setVisible(isVisible);
    }
      
    public void calculateAndDisplayProgress() {
		int completedPercent = (totalDataToLoad > 0) 
				?  ( 100 * dataLoadsCompleted ) / totalDataToLoad
				:  100;

		loaderProgressBar.setValue( completedPercent );
		loaderProgressBar.setString( completedPercent + "%" );
    }
    
    public void updateLabel(String message) {
    	loaderLabel.setText(message);
    }
    
    public void incrementDataLoadsCompleted() {
    	dataLoadsCompleted += 1;
    }
    
    /* **********************************************************************************
     * 	                                 Private Methods
     * **********************************************************************************/
}
