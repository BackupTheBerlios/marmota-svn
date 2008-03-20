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


package de.berlios.marmota.core.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import de.berlios.marmota.core.common.userManagment.UserRemoteInterface;
import de.berlios.marmota.core.server.userManagment.UserManagment;
import de.berlios.marmota.core.server.webserver.WebServer;
import de.berlios.marmota.core.server.plugin.InitedPlugin;


/**
 * Main-Entry class for the server.
 * @author sebmeyer
 */
public class Marmota {
	
	/**
	 * Contains the configuration-file marmota.cfg
	 */
	public static Properties CONFIG = new Properties();
	
	/**
	 * Static instance to use the logging-system
	 */
	private static Logger LOGGER = Logger.getRootLogger();
	
	/**
	 * The programm's major-version (The 1 in Version 1.2)
	 */
	public static int MAJORVERSION = 0;
	
	/**
	 * The programm's minor-version (The 2 in Version 1.2)
	 */
	public static int MINORVERSION = 0;

	/**
	 * This Vector will contain the information about
	 * all collected plugins
	 */
	private static Vector<InitedPlugin> PLUGINS = new Vector<InitedPlugin>();
	
	/**
	 * The programm's name
	 */
	public static String PNAME = "Marmota";
	
	/**
	 * The Registry for the RMI-System
	 */
	static Registry REGISTRY;
	
	/**
	 * The programm's version suffix (like alpha or beta)
	 */
	public static String VERSIONSUFFIX = "pre-alpha";
	
	/**
	 * Webserver for the application
	 */
	private static WebServer webserver;
	
	/**
	 * Contains the Name of all client-plugins
	 */
	private static Vector<String> clientPluginNames = new Vector<String>();
	
	/**
	 * Hibernate's Session-Factory.
	 */
	public static SessionFactory SESSION_FACTORY;

	
	/**
	 * Collecting and init the plugins
	 */
	private static void collectPlugins() {
		LOGGER.info("Collecting Server Plugins...");
		// Getting all .jar-Files from the plugin-directory using a FileFilter
		File pluginDir = new File("./plugins");
		FileFilter jarFilter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith("_server.jar")) {
					return true;
				}
				return false;
			}
		};
		File[] files = pluginDir.listFiles(jarFilter);
		if (files != null) {
			for (File f : files) {
				initPlugin(f);
			}
		}
	}
	
	
	/**
	 * This will display a small license information
	 */
	private static void displaySmallLicenseMessage() {
		System.out.println("\nThis program comes with ABSOLUTELY NO WARRANTY!");
		System.out.println("This is free software, and you are welcome to redistribute it");
		System.out.println("under certain conditions; read the 'license.txt' for details.");
	}
	
	
	/**
	 * Return the Logger
	 * @return the Logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
	
	
	public static Registry getRegistry() {
		return REGISTRY;
	}
	
	
	/**
	 * Reading the informations from the plugin's jar and init it.
	 * @param f The jar-file of the plugin
	 */
	private static void initPlugin(File f) {
		try {
			LOGGER.debug("Try to init plugin: " + f.getName());
			JarFile jar = new JarFile(f);
			Manifest mf = jar.getManifest();
			Attributes att = mf.getMainAttributes();
			InitedPlugin plugin = new InitedPlugin();
			plugin.setFile(f);
			plugin.setAuthor(att.getValue("Author"));
			plugin.setFullname(att.getValue("Full-Name"));
			plugin.setName(att.getValue("Plugin-Name"));
			plugin.setVersion(Double.parseDouble(att.getValue("Version")));
			plugin.setMainclass(att.getValue("Plugin-Main-Class"));
			LOGGER.debug("Plugin was init: File: " + f.getName() + "  Author: " + att.getValue("Author") +
					" Fullname: " + att.getValue("Full-Name") + "  Name: " + att.getValue("Name") +
					" Plugin-Main-Class: " + att.getValue("Plugin-Main-Class"));
			LOGGER.info("Plugin was init: " + att.getValue("Name") + " from " + f.getName());
		} catch (IOException e) {
			LOGGER.error("Failed to load jar-plugin: " + f.getName() + " : " + e.getMessage());
		}
	}
	
	
	/**
	 * Loading the configuration-file
	 */
	private static void loadConfig() {
		System.out.print("\nLoading Config: ");
		try {
			URL url = Marmota.class.getClass().getResource("/marmota.cfg");
			CONFIG.load(url.openStream());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n Can't load config-file MARMOTA.CFG.");
			System.exit(1);
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
		startLogSystem();
		collectPlugins();
		collectClientData();
		startHibernate();
		mapHibernateDataClasses();
		startRMIServer();
		startWebServer();
		displaySmallLicenseMessage();
	}
	
	
	/**
	 * Giving the Mapping-Data to the Hibernate-Framework.
	 * This should be collected from the core and the plugins.
	 */
	private static void mapHibernateDataClasses() {
	}
	
	
	/**
	 * Collect the Data which will send via WebStart
	 * to the client
	 */
	private static void collectClientData() {
		LOGGER.info("Start Collecting Client-Data");
		// Collecting all client-data in the plugin-directory
		File dirFile = new File("./plugins");
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith("_client.jar")) {
					return true;
				}
				return false;
			}
		};
		File[] files = dirFile.listFiles(filter);
		if (files != null && files.length > 0) {
			for (File f : files) {
				LOGGER.info("Client-Data added: " + f.getName());
				clientPluginNames.add(f.getName());
			}
		}
		// Collecting the libraries for the clients
		dirFile = new File("./lib/client");
		filter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".jar")) {
					return true;
				}
				return false;
			}
		};
		files = dirFile.listFiles(filter);
		if (files != null && files.length > 0) {
			for (File f : files) {
				LOGGER.info("Client-Lib-Data added: " + f.getName());
				clientPluginNames.add("lib/" + f.getName());
			}
		}
	}
	
	
	/**
	 * This method will shutdown the server. The server will try to
	 * shutdown controlled.
	 * After a specified time the server will exit.
	 * A warn-message to all clients will be send.
	 * @param time Time in milliseconds before the servers goes down
	 * @param message A message to be displayed on the clients and logged in the log-file
	 */
	public static void shutdownServer(int time, String message) {
		try {
			Thread.sleep(time);
			LOGGER.info("Server shutdwon: " + message);
			System.exit(0);
		} catch (InterruptedException e) {
			LOGGER.warn(e.getStackTrace());
			System.exit(0);
		}
	}
	

	/**
	 * This will startup the Hibernate Framework
	 */
	private static void startHibernate() {
		LOGGER.info("Starting Hibernate-Framework...");
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		// Read the config from the marmota.cfg and put it into the Hibernate-Config
		Properties props = new Properties();
		props.setProperty("hibernate.connection.driver_class", CONFIG.getProperty("db_driver"));
		props.setProperty("hibernate.connection.url", CONFIG.getProperty("db_conurl"));
		props.setProperty("hibernate.connection.username", CONFIG.getProperty("db_user"));
		props.setProperty("hibernate.connection.password", CONFIG.getProperty("db_pass"));
		props.setProperty("hibernate.dialect", CONFIG.getProperty("db_dialect"));
		props.setProperty("hibernate.hbm2ddl.auto", "update");
		props.setProperty("hibernate.show_sql", CONFIG.getProperty("db_showsql"));
		props.setProperty("hibernate.format_sql", CONFIG.getProperty("db_formatsql"));
		configuration.setProperties(props);
		LOGGER.info("[Marmota Hibernate] Collecting Hibernate-Mapping-Data");
		configuration.addAnnotatedClass(de.berlios.marmota.core.common.userManagment.User.class);
		configuration.addAnnotatedClass(de.berlios.marmota.core.common.userManagment.Group.class);
		configuration.buildMappings();
		SESSION_FACTORY = configuration.buildSessionFactory();
	}
	
	
	/**
	 * Init, config and starting the Log-System
	 */
	private static void startLogSystem() {
		System.out.print("\nStarting the logging-system");
		try {
			SimpleLayout simpLayout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(simpLayout);
			LOGGER.addAppender(consoleAppender);
			PatternLayout patLayout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
			FileAppender fileAppender = new FileAppender(patLayout, "marmota_mess.log", false);
			LOGGER.addAppender(fileAppender);
			String loglevel = CONFIG.getProperty("log_level");
			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF
			if (loglevel.toLowerCase().equals("all")) {
				LOGGER.setLevel(Level.ALL);
			} else if (loglevel.toLowerCase().equals("debug")) {
				LOGGER.setLevel(Level.DEBUG);
			} else if (loglevel.toLowerCase().equals("info")) {
				LOGGER.setLevel(Level.INFO);
			} else if (loglevel.toLowerCase().equals("warn")) {
				LOGGER.setLevel(Level.WARN);
			} else if (loglevel.toLowerCase().equals("error")) {
				LOGGER.setLevel(Level.ERROR);
			} else if (loglevel.toLowerCase().equals("fatal")) {
				LOGGER.setLevel(Level.FATAL);
			} else if (loglevel.toLowerCase().equals("off")) {
				LOGGER.setLevel(Level.FATAL);
			} else {
				System.out.print(" ... no cofig found, using WARN\n");
				LOGGER.setLevel(Level.WARN);
			}
			System.out.print("  Loglevel is now: " + loglevel + "\n");
		} catch(Exception ex) {
			System.out.println(ex);
		}
		LOGGER.info("Logging system init done");
	}
	
	/**
	 * Starting the RMI-Server
	 */
	private static void startRMIServer() {
		System.out.print("\nStarting the RMI-Server: ");
		try {
			int port = Integer.parseInt(CONFIG.getProperty("rmiserver_port"));
			LocateRegistry.createRegistry(port);
			REGISTRY = LocateRegistry.getRegistry(port);
			System.out.print(" OK (Port:" + port +")\n");
			LOGGER.info("RMI-Server is up now on port " + port);
			UserManagment usermanagment = new UserManagment();
			UserRemoteInterface uri = (UserRemoteInterface) UnicastRemoteObject.exportObject(usermanagment, 0);
			REGISTRY.bind("UserManagment", uri);
		} catch (Exception e) {
			LOGGER.fatal("Fatal error while init the RMI-Server: " + e.getMessage());
			LOGGER.fatal(e.getStackTrace());
			System.exit(1);
		}
}
	
	
	/**
	 * Start and init the webserver
	 */
	private static void startWebServer() {
		System.out.print("\nStarting the Webserver: ");
		try {
			int port = Integer.parseInt(CONFIG.getProperty("webserver_port"));
			webserver = new WebServer(port);
			webserver.start();
			System.out.print(" OK (Port:" + port +")\n");
			LOGGER.info("WebServer is up now on port " + port);
		} catch (Exception e) {
			LOGGER.fatal("Fatal error while init the webserver: " + e.getMessage());
			LOGGER.fatal(e.getStackTrace());
			System.exit(1);
		}
	}
	
	
	/**
	 * Returns the names of the plugin-files which contains the data
	 * for the clients.
	 * @return the names of the client-jars files
	 */
	public static Vector<String> getClientPluginNames() {
		return clientPluginNames;
	}
	

}
