/*
 * Marmota - Open-Source, easy to use Groupware
 * Copyright (C) 2007  The Marmota Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.berlios.marmota.core.client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * The Login-Window which ident the user.
 * It will be displayed as the first window.
 * @author sebmeyer
 */
public class LoginFrame extends JFrame {
	
	/** Generated SVUID */
	private static final long serialVersionUID = 9119212780202861108L;

	/** Textfield for the username */
	JTextField userField = new JTextField(20);
	
	/** Textfield for the password */
	JTextField passField = new JTextField(20);
	
	/** OK Button */
	JButton okBut = new JButton("OK");
	
	/** Cancel Button */
	JButton cancelBut = new JButton("Cancel");
	
	/**
	 * Default construct
	 */
	public LoginFrame() {
		this.setTitle("Marmota Login");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createLayout();
	}
	
	
	/**
	 * Init and create the Layout
	 */
	private void createLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		
		
		panel.add(new JLabel("User:", JLabel.RIGHT), "align right");
		panel.add(userField, "growx, wrap");
		panel.add(new JLabel("Password:", JLabel.RIGHT), "align right");
		panel.add(passField, "growx, wrap");
		
		JPanel southpanel = new JPanel();
		
		southpanel.add(okBut);
		southpanel.add(cancelBut);
		
		panel.add(southpanel, "south");
		
		this.add(panel);
	}

}
