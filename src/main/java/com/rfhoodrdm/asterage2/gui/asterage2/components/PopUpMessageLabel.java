package com.rfhoodrdm.asterage2.gui.asterage2.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.asterage2.AsteRAGE2GameBoard;

import lombok.Getter;

public class PopUpMessageLabel 
	extends JLabel {
	
	private static final long serialVersionUID = -1365184913233342775L;

	public PopUpMessageLabel() {
		Dimension messageSize = new Dimension(AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.width, 100);
		setSize(messageSize);
		setPreferredSize(messageSize);
		setMinimumSize(messageSize);
		setMaximumSize(messageSize);
		setLocation(0, (AsteRAGE2GameBoard.GAME_BOARD_DIMENSION.height/ 2));
		setFont(new java.awt.Font(GameConstants.gameFont, Font.BOLD, GameConstants.Asterage2HUDFontSize));
		setHorizontalAlignment(CENTER);
	}

	public static enum MessageType {
		REWARD(new Color(0x33, 0x99, 0x33)), INFO(new Color(0x33, 0x99, 0xCC)), WARNING(new Color(0xCC, 0x00, 0x00));

		@Getter
		Color associatedColor;

		MessageType(Color passedColor) {
			this.associatedColor = passedColor;
		}
	}

	public void setPopUpText(String message, MessageType whatType) {
		// set the message text and associated color
		setText(message);
		setForeground(whatType.getAssociatedColor());
	}
}