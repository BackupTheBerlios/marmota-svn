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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import de.berlios.marmota.core.common.userManagment.User;

/**
 * This is the entry-class for the client.
 * It will be inited and started by the WebStart-System.
 * @author sebmeyer
 */
public class MarmotaClient {
	
	/**
	 * We need the Host-Name or the IP-Adress of the server
	 * to communicate with him.
	 * The server send it to us as the first start-argument.
	 */
	public static String SERVER_HOST;
	
	/**
	 * RMI-Port of the server.
	 * The server send it to us as the second start-argument.
	 */
	public static Integer SERVER_RMI_PORT;
	
	/**
	 * The RMI-Registry on the server
	 * We need it for the RMI-Connections
	 */
	public static Registry SERVER_REGISTRY;
	
	/**
	 * The current user. Set while logging in 
	 */
	private static User currentuser;
	
	private static MainWindow mainwindow;

	/**
	 * This is the main entry class for the marmota client.
	 * First we need to identify the user
	 * @param args 1 = The Server's Host-Name, 2 = The Server's RMI-Port
	 */
	public static void main(String[] args) {
		
		SERVER_HOST = args[0];
		SERVER_RMI_PORT = Integer.parseInt(args[1]);
		
		try {
			SERVER_REGISTRY = LocateRegistry.getRegistry(SERVER_HOST, SERVER_RMI_PORT);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		LoginFrame lf = new LoginFrame();
		
		// lf.setSize(400, 200);
		lf.pack();
		lf.setLocationRelativeTo(null);
		lf.setVisible(true);

	}
	
	/**
	 * Show the MainWindow
	 */
	public static void showMainWindow() {
		mainwindow = new MainWindow();
		mainwindow.setTitle("Marmota Client - " + currentuser.getFullName());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainwindow.setSize(dim.width / 5 * 4, dim.height / 5 * 4);
		mainwindow.setLocationRelativeTo(null);
		mainwindow.setVisible(true);
	}

	public static User getCurrentuser() {
		return currentuser;
	}

	public static void setCurrentuser(User currentuser) {
		MarmotaClient.currentuser = currentuser;
	}

}
