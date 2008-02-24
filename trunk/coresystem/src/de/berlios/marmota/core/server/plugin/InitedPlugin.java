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

package de.berlios.marmota.core.server.plugin;

import java.io.File;
import java.util.Vector;


/**
 * This class represents a plugin which was initialized by
 * the Marmota-core.
 * This contains the basic informations for the plugin.
 * @author sebmeyer
 */
public class InitedPlugin {
	
	/** The name of the autor */
	private String author;
	
	/** The dependencies of the plugin */
	private Vector<PluginDepend> dependencies = new Vector<PluginDepend>();
	
	/** The file of the plugin */
	private File file;
	
	/** The fullname of the plugin */
	private String fullname;
	
	/** The Mainclass which implements the Plugin-Interface */
	private String mainclass;
	
	/** The name of the plugin */
	private String name;
	
	/** The version of the plugin */
	private double version;

	/**
	 * Adds an dependencie to the plugin 
	 * @param dependence The dependencie
	 */
	public void addDependence(PluginDepend dependence) {
		dependencies.add(dependence);
	}

	/**
	 * Get the name of the author
	 * @return the name of the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Get all dependencies as a vector
	 * @return all dependencies as a vector
	 */
	public Vector<PluginDepend> getDependencies() {
		return dependencies;
	}

	/**
	 * Get the file of the plugin
	 * @return the file of the plugin
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Get the fullname of the plugin
	 * @return the fullname of the plugin
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * Returns the MainClass of the plugin
	 * @return the MainClass of the plugin
	 */
	public String getMainclass() {
		return mainclass;
	}

	/**
	 * Get the name of the plugin
	 * @return the name of the plugin 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the version of the plugin
	 * @return the version of the plugin
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * Set the name of the author
	 * @param autor the name of the author
	 */
	public void setAuthor(String autor) {
		this.author = autor;
	}
	
	/**
	 * Set the file of the plugin
	 * @param file the file of the plugin
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Set the fullname of the plugin
	 * @param fullname the fullname of the plugin
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * Set the MainClass of the plugin
	 * @param mainclass the MainClass of the plugin
	 */
	public void setMainclass(String mainclass) {
		this.mainclass = mainclass;
	}

	/**
	 * Set the name of the plugin
	 * @param name the name of the plugin
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the version of the plugin
	 * @param version the version of the plugin
	 */
	public void setVersion(double version) {
		this.version = version;
	}

}
