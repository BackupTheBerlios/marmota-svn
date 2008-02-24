/*
 * Marmota - Open-Source, easy to use Groupware
 * Copyright (C) 2007, 2008  The Marmota Team
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.berlios.marmota.core.client.userManagment.UserManagmentFrame;

/**
 * The Mainframe of the client.
 * @author sebmeyer
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = -6703155924511296399L;
	
	/**
	 * The Main-Menu-Bar
	 */
	private JMenuBar mainMenu = new JMenuBar();
	
	/**
	 * The administration-menu in the Menu-Bar
	 */
	private JMenu adminMenu = new JMenu("Administration");
	
	public MainWindow() {
		this.setJMenuBar(mainMenu);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* This will be called if the user is in the Group "admin".
		   It adds the admin-menu to the main-menu and the core-admin-menuitems*/
		if (MarmotaClient.getCurrentuser().isUserAdmin()) {
			generateAdminMenu();
		}
		
	}
	
	/**
	 * Generates the admin-menu for the main-menu-bar
	 */
	private void generateAdminMenu() {
		this.getMainMenu().add(adminMenu);
		JMenuItem userManagmentMenu = new JMenuItem("User- and Groupmanagment");
		userManagmentMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserManagmentFrame umf = new UserManagmentFrame();
				umf.pack();
				umf.setLocationRelativeTo(null);
				umf.setVisible(true);
			}
		});
		adminMenu.add(userManagmentMenu);
	}

	public JMenuBar getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(JMenuBar mainMenu) {
		this.mainMenu = mainMenu;
	}

	public JMenu getAdminMenu() {
		return adminMenu;
	}

	public void setAdminMenu(JMenu adminMenu) {
		this.adminMenu = adminMenu;
	}
	
	

}
