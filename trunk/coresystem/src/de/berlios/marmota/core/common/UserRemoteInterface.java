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

package de.berlios.marmota.core.common;

import java.rmi.Remote;
import java.rmi.RemoteException;



/**
 * This is the Remote-Interface which will be used for the RMI-Communications.
 * It should be the only way for the client to perform user-operations on the server
 * 
 * @author sebmeyer
 */
public interface UserRemoteInterface extends Remote {
	
	public boolean addOrUpdateUser(User user) throws RemoteException;
	
	public User getUserByID(int id) throws RemoteException;
	
	public User getUserByName(String uname) throws RemoteException;
	
	public boolean deleteUserByID(int id) throws RemoteException;
	
	public User login(String username, String password) throws RemoteException;
	

}
