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

package de.berlios.marmota.core.server.webserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import de.berlios.marmota.core.server.Marmota;


/**
 * This is the main-class for a small webserer which can
 * answer http-requests.
 * It is used to bring the client-application via WebStart
 * to the client
 * @author sebmeyer
 */
public class WebServer extends Thread {
	
	/** The server socket for the WSServer */
	private ServerSocket wsservsock;
	
	
	/**
	 * Init the ServerSocket and waits for
	 * the connection
	 * @param port The port an which the server will listen
	 * @throws IOException 
	 */
	public WebServer(int port) throws IOException {
		this.setName("WebServer");
		this.wsservsock = new ServerSocket(port);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		long idcounter = 0;
		while (true) {
			try {
				Socket socket = wsservsock.accept();
				ConnectionHandler ch = new ConnectionHandler(socket, idcounter);
				ch.start();
				idcounter++;
			} catch (IOException e) {
				Marmota.getLogger().fatal("Critical exception in the webserver system!: " + e.getMessage());
				Marmota.getLogger().fatal(e.getStackTrace());
				Marmota.getLogger().fatal("Trying to shutdown the server!");
				Marmota.shutdownServer(30000, "Exception in the Webserver, System will go down!");
			}
		}
	}
	
}