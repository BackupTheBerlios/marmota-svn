package de.berlios.marmota.core.server;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

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
 * @author sebmeyer
 */
public class Marmota {
	
	/**
	 * Contains the configuration-file marmota.cfg
	 */
	static Properties config = new Properties();
	
	/**
	 * Static instance to use the logging-system
	 */
	private static Logger logger = Logger.getRootLogger();
	
	/**
	 * The programm's major-version (The 1 in Version 1.2)
	 */
	public static int MAJORVERSION = 0;
	
	/**
	 * The programm's minor-version (The 2 in Version 1.2)
	 */
	public static int MINORVERSION = 0;

	/**
	 * The programm's name
	 */
	public static String PNAME = "Marmota";
	
	/**
	 * The programm's version suffix (like alpha or beta)
	 */
	public static String VERSIONSUFFIX = "pre-alpha";

	
	/**
	 * This will display a small license information
	 */
	private static void displaySmallLicenseMessage() {
		System.out.println("\nThis program comes with ABSOLUTELY NO WARRANTY!");
		System.out.println("This is free software, and you are welcome to redistribute it");
		System.out.println("under certain conditions; read the 'license.txt' for details.");
	}
	
	
	/**
	 * Loading the configuration-file
	 */
	private static void loadConfig() {
		System.out.print("\nLoading Config: ");
		try {
			URL url = Marmota.class.getClass().getResource("/marmota.cfg");
			config.load(url.openStream());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	
	/**
	 * Entry Method for the server. 
	 * Init process will be started here.
	 * @param args Parameter from the commandLine, not used at the moment
	 */
	public static void main(String[] args) {
		System.out.println(PNAME + " " + MAJORVERSION + "." + MINORVERSION + " " + VERSIONSUFFIX);
		System.out.println("(c) by the Marmota team (2007)");
		System.out.println("Visit: marmota.berlios.de");
		loadConfig();
		startingLogSystem();
		displaySmallLicenseMessage();
	}
	

	/**
	 * Init, config and starting the Log-System
	 */
	private static void startingLogSystem() {
		System.out.print("\nStarting the logging-system");
		try {
			SimpleLayout simpLayout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(simpLayout);
			logger.addAppender(consoleAppender);
			PatternLayout patLayout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
			FileAppender fileAppender = new FileAppender(patLayout, "marmota_mess.log", false);
			logger.addAppender(fileAppender);
			String loglevel = config.getProperty("log_level");
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF
			if (loglevel.toLowerCase().equals("all")) {
				logger.setLevel(Level.ALL);
			} else if (loglevel.toLowerCase().equals("debug")) {
				logger.setLevel(Level.DEBUG);
			} else if (loglevel.toLowerCase().equals("info")) {
				logger.setLevel(Level.INFO);
			} else if (loglevel.toLowerCase().equals("warn")) {
				logger.setLevel(Level.WARN);
			} else if (loglevel.toLowerCase().equals("error")) {
				logger.setLevel(Level.ERROR);
			} else if (loglevel.toLowerCase().equals("fatal")) {
				logger.setLevel(Level.FATAL);
			} else if (loglevel.toLowerCase().equals("off")) {
				logger.setLevel(Level.FATAL);
			} else {
				System.out.print(" ... no cofig found, using WARN\n");
				logger.setLevel(Level.WARN);
			}
			System.out.print("  Loglevel is now: " + loglevel + "\n");
		} catch(Exception ex) {
			System.out.println(ex);
		}
		logger.info("Logging system init");
	}

}
