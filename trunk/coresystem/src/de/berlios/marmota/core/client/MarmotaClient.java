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

import javax.swing.JFrame;

/**
 * This is the entry-class for the client.
 * It will be inited and started by the WebStart-System.
 * @author sebmeyer
 */
public class MarmotaClient {

	/**
	 * This is the main entry class for the marmota client.
	 * First we need to identify the user
	 * @param args CommandLinse-Options. Not used at the moment
	 */
	public static void main(String[] args) {
		
		LoginFrame lf = new LoginFrame();
		
		// lf.setSize(400, 200);
		lf.pack();
		lf.setLocationRelativeTo(null);
		lf.setVisible(true);

	}

}
