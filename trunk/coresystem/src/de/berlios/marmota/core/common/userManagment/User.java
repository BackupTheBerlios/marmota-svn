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

package de.berlios.marmota.core.common.userManagment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * This represents the User in the Database.
 * It will be used to confirm the login and handle ther user-
 * managment.
 * @author sebmeyer
 * 
 */
@Entity
@Table(name = "marmota_core_user")
public class User implements Serializable {

	private static final long serialVersionUID = 1621422576067196991L;
	private String comment;
	private String fullName;
	private List<Group> groups = new ArrayList<Group>();
	private Integer id;
	private String password;
	private String username;
	
	/**
	 * @return The comment
	 */
	@Column
	public String getComment() {
		return comment;
	}
	
	/**
	 * @return The Fulname of the user
	 */
	@Column
	public String getFullName() {
		return fullName;
	}
	
	/**
	 * Returns a List with all Groups of the user
	 * @return a List with all Groups of the user
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	public List<Group> getGroups() {
		return groups;
	}
	
	/**
	 * @return The ID of the user
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	
	/**
	 * @return The password for the user
	 */
	@Column
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return The username for the user
	 */
	@Column
	public String getUsername() {
		return username;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return true if the user is in the admin-group
	 */
	@Transient 
	public boolean isUserAdmin() {
		for (Group g : getGroups()) {
			if (g.getGroupname().equals("admin")) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Overrides the "toString" Method to get the Username
	 * when the method is called
	 * @see java.lang.Object#toString()
	 */
	@Transient
	public String toString() {
		return this.getUsername();
	}
	

}
