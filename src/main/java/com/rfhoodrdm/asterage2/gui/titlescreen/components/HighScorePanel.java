package com.rfhoodrdm.asterage2.gui.titlescreen.components;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.state.asterage1.Asterage1State;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HighScorePanel 
	extends JPanel
{
	private static final long serialVersionUID = 3992518089885656950L;
	private final Asterage1State asterage1State;		
	private final Asterage2State asterage2State;		

	public HighScorePanel (Asterage1State a1State, Asterage2State a2State) {
		this.asterage1State = Objects.requireNonNull(a1State);
		this.asterage2State = Objects.requireNonNull(a2State);
		
		setVisible(false);			//initially invisible
		//setBackground( Color.black);
		setOpaque(false);
		setBounds(5, 5, 1190, 475);
		this.setLayout(null);			// honor sub-component coordinates.
		
		JLabel titleLabel = new JLabel("AsteRAGE All-Stars:");
		titleLabel.setFont( new Font( GameConstants.gameFont, Font.BOLD, 48) );
		titleLabel.setBounds(5, 5, 1190, 50);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setForeground( new Color (0x66, 0x99, 0xFF ));
		add(titleLabel);
		
		JLabel asterage1Label = new JLabel("AsteRAGE Classic");
		asterage1Label.setFont( new Font( GameConstants.gameFont, Font.BOLD, 24) );
		asterage1Label.setBounds(5, 65, 595, 25);
		asterage1Label.setHorizontalAlignment(SwingConstants.CENTER);
		asterage1Label.setForeground( new Color (0x66, 0x99, 0xFF ));
		add(asterage1Label);
		
		JLabel asterage2Label = new JLabel("AsteRAGE 2: to Infinity!");
		asterage2Label.setFont( new Font( GameConstants.gameFont, Font.BOLD, 24) );
		asterage2Label.setBounds(605, 65, 595, 25);
		asterage2Label.setHorizontalAlignment(SwingConstants.CENTER);
		asterage2Label.setForeground( new Color (0x66, 0x99, 0xFF));
		add(asterage2Label);
		
		//Make the high scores table.
		TableModel highScoresModel = new AbstractTableModel() {
			@Override
			public int getColumnCount () {return 7; }
			
			@Override
			public int getRowCount () {return 10; }
			
			/**
			 * Get the relevant piece of information from the high score state objects.
			 * @param row	Represents which place (1st through 10th) being referenced in this call.
			 * @param column	Represents the type of information being retrieved.
			 * @return Object representing the high score entry's piece of information.
			 */
			@Override
			public Object getValueAt( int row, int column) { 
				switch ( column ) {
					case 0:		//Name from Asterage 1.
						return asterage1State.getHighScoreNameAtPlace( row );
						
					case 1:		//Level achieved from Asterage 1.
						return asterage1State.getHighScoreLevelAtPlace( row );
						
					case 2:		//Points achieved from Asterage 1.
						return asterage1State.getHighScorePointsAtPlace( row );
						
					
					case 4:		//Name from Asterage 2
						return asterage2State.getHighScoreNameAtPlace( row );
						
					case 5:		//Level achieved from Asterage 2.
						return asterage2State.getHighScoreLevelAtPlace( row );
						
					case 6:		//Points Achieved from Asterage 2
						return asterage2State.getHighScorePointsAtPlace( row );
						
						
					default:
					case 3:		//spacer column between high score states.
						return "";

				} 
			} 
		};
		
		JTable highScoresTable = new JTable( highScoresModel );
		highScoresTable.setBounds(50, 100, 1100, 375);
		highScoresTable.setBackground( Color.black );
		highScoresTable.setOpaque(false);
		highScoresTable.setGridColor(Color.black);
		highScoresTable.setForeground(Color.white);
		highScoresTable.setRowHeight(35);
		highScoresTable.setFont( new Font ( "Skia", Font.BOLD, 20) );

		for ( int row = 0; row < 10; ++row ) {
			for ( int column = 0; column < 7; ++column)	{
				highScoresTable.setValueAt(  Integer.toString(row * column) , row, column);
			} 
		} 
		
		add(highScoresTable );
	} 
} 