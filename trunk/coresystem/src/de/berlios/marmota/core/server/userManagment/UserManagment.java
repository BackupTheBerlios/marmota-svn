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

package de.berlios.marmota.core.server.userManagment;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.berlios.marmota.core.common.userManagment.Group;
import de.berlios.marmota.core.common.userManagment.User;
import de.berlios.marmota.core.common.userManagment.UserRemoteInterface;
import de.berlios.marmota.core.server.Marmota;

/**
 * This is the controller for the User and Group-Managment.
 * It Implements the RMI-Remote-Interface. The Client will use
 * this interface to communicate with the server and run these
 * methods.
 * @author sebmeyer
 *
 */
public class UserManagment implements UserRemoteInterface, Serializable {

	
	private static final long serialVersionUID = -4068289402771481961L;


	/** 
	 * Adds a User to the database or updates an existing User.
	 * Returns the ID of the user
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#addOrUpdateUser(de.berlios.marmota.core.common.userManagment.User)
	 */
	public int addOrUpdateUser(User user) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		Transaction trans = session.beginTransaction();
		try {
			for (Group g : user.getGroups()) {
				session.saveOrUpdate(g);
			}
			session.saveOrUpdate(user);
			trans.commit();
			session.close();
			Marmota.getLogger().debug("User saved: " + user.getUsername());
			return user.getId();
		} catch (Exception e) {
			if (trans.isActive()) {
				trans.rollback();
			}
			if (session.isOpen()) {
				session.close();
			}
			Marmota.getLogger().error("Failed to save User: " + e.getMessage());
			return -1;
		}
	}

	
	/**
	 * Delet an User from the database
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#deleteUserByID(int)
	 */
	public boolean deleteUserByID(int id) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		Transaction trans = session.beginTransaction();
		try {
			User user = getUserByID(id);
			session.delete(user);
			trans.commit();
			session.close();
		} catch (Exception e) {
			if (trans.isActive()) {
				trans.rollback();
			}
			if (session.isOpen()) {
				session.close();
			}
			Marmota.getLogger().warn("Failed to delete User: " + e.getMessage());
		}
		return true;
	}

	
	/**
	 * load the User from the database
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#getUserByID(int)
	 */
	public User getUserByID(int id) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		User user = null;
		try {
			user = (User) session.load(User.class, id);
			session.close();
		} catch (Exception e) {
			Marmota.getLogger().warn("Failed to load User: " + e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
		return user;
	}

	
	/** Load the user by loginname
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#getUserByName(int)
	 */
	public User getUserByName(String uname) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		User user = null;
		try {
			Query query = session.createQuery("select u from User u where u.username = ?");
			query.setString(0, uname);
			user = (User) query.uniqueResult();
			session.close();
			if (user == null) {
				Marmota.getLogger().debug("Can't load User -> Query result is null!");
				return null;
			}
		} catch (Exception e) {
			Marmota.getLogger().warn("Failed to load User: " + e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
		return user;
	}


	/**
	 * This should ne used to log the user in.
	 * TODO The passwort must be encryptet on the client!!! 
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#login(java.lang.String, java.lang.String)
	 */
	public User login(String username, String password) throws RemoteException {
		System.out.println("Login angeworfen");
		Session session = Marmota.SESSION_FACTORY.openSession();
		User user = null;
		try {
			Query query = session.createQuery("select u from User u where u.username = ? and u.password = ?");
			query.setString(0, username);
			query.setString(1, password);
			user = (User) query.uniqueResult();
			session.close();
			if (user != null) {
				return user;
			} else {
				if (Marmota.CONFIG.getProperty("admin_enabled").toLowerCase().equals("true")) {
					if (username.toLowerCase().equals("admin") && password.equals(Marmota.CONFIG.getProperty("admin_password"))) {
						System.out.println("Es stimmt alles");
						user = new User();
						user.setFullName("Administrator");
						user.setUsername("admin");
						user.setComment("The admin-account should only been used to create the other user-accounts!");
						Group g = new Group();
						g.setGroupname("admin");
						if (user.getGroups() == null) {
							ArrayList<Group> groups = new ArrayList<Group>();
							user.setGroups(groups);
						}
						user.getGroups().add(g);
					}
				}
			}
			return user;
		} catch (Exception e) {
			Marmota.getLogger().info("Failed to login with username: " + username + " Exception: " + e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
	}


	/** 
	 * Creates a new group
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#createNewGroup(java.lang.String)
	 */
	public Integer addOrUpdaeGroup(Group g) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		Transaction trans = session.beginTransaction();
		try {
			session.save(g);
			trans.commit();
			session.close();
		} catch (Exception e) {
			Marmota.getLogger().warn("Failed to save group: " + e.getMessage());
			if (trans.isActive()) {
				trans.rollback();
			}
			if (session.isOpen()) {
				session.close();
			}
			return -1;
		}
		return g.getId();
	}
	
	/**
	 * Load a group from the database
	 * @param name The name of the group
	 * @return the group
	 */
	private Group loadGroupByName(String name) {
		Session session = Marmota.SESSION_FACTORY.openSession();
		Group group = null;
		try {
			Query query = session.createQuery("select g from Group g where g.groupname = ?");
			query.setString(0, name);
			group = (Group) query.uniqueResult();
			session.close();
			if (group != null) {
				Marmota.getLogger().info("Can't save group '" + name + "', allready exists.");
				return null;
			}
			return group;
		} catch (Exception e) {
			Marmota.getLogger().warn("Failed to load group for validate existing group by creating: " + e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
	}
	

	/**
	 * Delete a group from the Database
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#deleteGroupByName()
	 */
	public boolean deleteGroupByName(String name) throws RemoteException {
		Group group = loadGroupByName(name);
		if (group == null) {
			return false;
		} else {
			Session session = Marmota.SESSION_FACTORY.openSession();
			Transaction trans = session.beginTransaction();
			session.delete(group);
			trans.commit();
			session.close();
			return true;
		}
		
	}


	/** 
	 * Get all Groups
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#getGroups()
	 */
	@SuppressWarnings("unchecked")
	public List<Group> getGroups() throws RemoteException {
		ArrayList<Group> groupsForClient = new ArrayList();
		Session session = Marmota.SESSION_FACTORY.openSession();
		List<Group> groupList = null;
		try {
			Query query = session.createQuery("select g from Group g");
			groupList = query.list();
			for (Group g : groupList) {
				groupsForClient.add(g);
			}
			session.close();
		} catch (Exception e) {
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
		return groupsForClient;
	}


	/** 
	 * Get all users
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#getUsers()
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsers() throws RemoteException {
		ArrayList<User> listForClient = new ArrayList<User>();
		Session session = Marmota.SESSION_FACTORY.openSession();
		List<User> userList = null;
		try {
			Query query = session.createQuery("select u from User u");
			userList = query.list();
			for (int i = 0; i < userList.size(); i++) {
				User u = userList.get(i);
				session.evict(u);
				listForClient.add(u);
			}
			session.close();
		} catch (Exception e) {
			Marmota.getLogger().error("Failed to get Users from DB: " + e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
			return null;
		}
		return listForClient;
	}
	
	
	/** 
	 * Delete a group from the server
	 * @see de.berlios.marmota.core.common.userManagment.UserRemoteInterface#deleteGroup(de.berlios.marmota.core.common.userManagment.Group)
	 */
	public boolean deleteGroup(Group g) throws RemoteException {
		Session session = Marmota.SESSION_FACTORY.openSession();
		Transaction trans = session.beginTransaction();
		try {
			session.save(g);
			trans.commit();
			session.close();
			return true;
		} catch (Exception e) {
			if (trans.isActive()) {
				trans.rollback();
			}
			if (session.isOpen()) {
				session.close();
			}
			return false;
		}
		
	}

}
