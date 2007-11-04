package de.berlios.marmota.core.server;

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

/**
 * Main-Entry class for the server.
 * @author sparrow
 */
public class Marmota {
	
	/**
	 * The programm's name
	 */
	public static String PNAME = "Marmota";
	
	/**
	 * The programm's major-version (The 1 in Version 1.2)
	 */
	public static int MAJORVERSION = 0;
	
	/**
	 * The programm's minor-version (The 2 in Version 1.2)
	 */
	public static int MINORVERSION = 0;

	/**
	 * The programm's version suffix (The 2 in Version 1.2)
	 */
	public static String VERSIONSUFFIX = "pre-alpha";

	
	/**
	 * Entry Method for the server. 
	 * Init process will be started here.
	 * @param args Parameter from the commandLine, not used at the moment
	 */
	public static void main(String[] args) {
		System.out.println(PNAME + " " + MAJORVERSION + "." + MINORVERSION + " " + VERSIONSUFFIX);
		System.out.println("(c) by the Marmota team (2007)");
		System.out.println("Visit: marmota.berlios.de");
		startingCoreSystem();
		displaySmallLicenseMessage();
	}
	
	/**
	 * This will start the coresystem
	 */
	private static void startingCoreSystem() {
		System.out.println("\nStarting the CORE System");
	}
	
	/**
	 * This will display a small license information
	 */
	private static void displaySmallLicenseMessage() {
		System.out.println("\nThis program comes with ABSOLUTELY NO WARRANTY!");
		System.out.println("This is free software, and you are welcome to redistribute it");
		System.out.println("under certain conditions; read the 'license.txt' for details.");
	}

}
