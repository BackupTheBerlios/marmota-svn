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

package de.berlios.marmota.core.server.plugin;

/**
 * This class contains the informatins about the
 * dependencies of a plugin
 * @author sebmeyer
 */
public class PluginDepend {
	
	/** The condition of the version, like
	 * = | > | >= | < | <=
	 */
	private String condition;

	/** The plugin name of the dependence */
	private String name;
	
	/** The plugin version of the dependence */
	private double version;
	
	/**
	 * Standard construct without parameter
	 */
	public PluginDepend() {
		
	}
	
	/**
	 * Constructer which allows to set all parameters
	 * @param name The name of the plugin on which the plugin depends
	 * @param condition The condition of the version
	 * @param version The version on which this plugin depends
	 */
	public PluginDepend(String name, String condition, double version) {
		this.name = name;
		this.condition = condition;
		this.version = version;
	}

	/**
	 * Get the condition of the version, like 
	 * = | > | >= | < | <= 
	 * @return condition of the version
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Returns the plugin name of the dependence
	 * @return The plugin name of the dependence
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the version of the dependence
	 * @return the version of the dependence
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * Set the condition of the version, like
	 * = | > | >= | < | <=
	 * @param condition condition of the version
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	/**
	 * Set the plugin name of the dependence 
	 * @param name the plugin name of the dependence
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the version of the dependence
	 * @param version the version of the dependence
	 */
	public void setVersion(double version) {
		this.version = version;
	}

}
