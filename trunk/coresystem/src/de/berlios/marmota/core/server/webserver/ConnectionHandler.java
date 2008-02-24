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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

import de.berlios.marmota.core.server.Marmota;


/**
 * This will handle the incomming connections
 * @author sebmeyer
 */
public class ConnectionHandler extends Thread {
	
	/**
	 * The document-root-directory for the sites insite
	 * the jar-package
	 */
	private static final String docRoot = "/de/berlios/marmota/core/server/webserver/html";
	
	/**
	 * Contains all known mimetimes
	 */
	private static final String[][] mimetypes = {
		{"html", "text/html"},
		{"htm",  "text/html"},
		{"txt",  "text/plain"},
		{"gif",  "image/gif"},
		{"jpg",  "image/jpeg"},
		{"jpeg", "image/jpeg"},
		{"png",  "image/png"},
		{"jnlp", "application/x-java-jnlp-file"}
		};

	/** The socket for the connection */
	Socket socket;
	
	/** Internal id for the handler */
	long handlerid;
	
	/**
	 * The client will send a HOST-Information in his request.
	 * So we know what is the name of the server for the host.
	 * We need this information to give it back to the marmota-client, 
	 * because the client must know hot to reach the server.
	 */
	String SERVER_HOST = null;
	
	
	/**
	 * Create the Handler
	 * @param socket The socket with the connection
	 * @param handlerid the id of the connection
	 */
	public ConnectionHandler(Socket socket, long handlerid) {
		this.socket = socket;
		this.handlerid = handlerid;
		Marmota.getLogger().debug("Conhandler: " + handlerid + " INIT from : " + socket.getInetAddress());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Marmota.getLogger().debug("Conhandler: " + handlerid + " START from : " + socket.getInetAddress());
		// Parsing incomin data
		Vector<String> incomingLines = new Vector<String>();
		try {
			InputStream is = socket.getInputStream();
			int i = 0;
			StringBuffer buffer = new StringBuffer();
			while ((i=is.read()) >= 0) {
				char c = (char) i;
				// Ignoring CarrierReturns
				if (c == '\r') {
					continue;
				}
				// recognize NewLines
				if (c == '\n') {
					if (buffer.length() == 0) {
						break;
					} else {
						incomingLines.add(buffer.toString());
						Marmota.getLogger().debug("Conhandler: " + handlerid + " In from Client: " + buffer.toString());
						buffer = new StringBuffer();
					}
				} else {
					buffer.append(c);
				}
			}
			String requestedPage = null;
			String host_with_port = null;
			for (int count = 0; count < incomingLines.size(); count++) {
				// System.out.println("Incoming request: " + incomingLines.get(count));
				if (incomingLines.get(count).startsWith("GET")) {
					String[] getParts = incomingLines.get(count).split(" ");
					requestedPage = getParts[1];
				}
				if (incomingLines.get(count).startsWith("Host")) {
					String[] getParts = incomingLines.get(count).split(" ");
					host_with_port = getParts[1];
					SERVER_HOST = host_with_port.split(":")[0];
				}
			}
			// Which site should be send?
			Marmota.getLogger().info("Conhandler: " + handlerid + " REQUEST from " + socket.getInetAddress() + ") for: " + requestedPage);
			if (requestedPage.equals("/") || requestedPage.equals("/index.html") || requestedPage.contains("..")) {
				requestedPage = ("/start.html");
			}
			// The subdirectory /client on will be used to send the clientdate to the clients
			if (requestedPage != null && requestedPage.startsWith("/client/")) {
				sendClientData(socket.getOutputStream(), requestedPage, host_with_port);
			} else {
				sendData(socket.getOutputStream(), requestedPage);
			}
			is.close();
			socket.close();
			Marmota.getLogger().debug("Conhandler: " + handlerid + " ENDS succesfully from : " + socket.getInetAddress());
		} catch (Exception e) {
			Marmota.getLogger().error("Conhandler: " + handlerid + " ERROR "  + e.getMessage());
			Marmota.getLogger().error("Conhandler: " + handlerid + " ERROR - Connections comes from: " + socket.getInetAddress());
			Marmota.getLogger().error(e.getStackTrace());
		}
	}
	
	
	/**
	 * Sending the client-data to the client
	 * @param outputStream The OutputStream towards the client
	 * @param requestedPage The requested Page
	 * @param host The Host (send by the client in his request)
	 */
	private void sendClientData(OutputStream os,
								String requestedPage, String host) {
		String page = requestedPage.substring(8);
		// First testing for .jnlp-descriptor
		if (page.equals("marmota.jnlp")) {
			sendjnlpdescriptor(os, host);
		} else {
			try {
				Marmota.getLogger().debug("Conhandler: " + handlerid + " REQUEST RESOURCE for: " + page);
				// Looking for the requested name in the pluings
				Vector<String> pluginnames = Marmota.getClientPluginNames();
				boolean pluginfound = false;
				for (int i = 0; i < pluginnames.size(); i++) {
					if (pluginnames.get(i).equals(page)) {
						Marmota.getLogger().info("Conhandler: " + handlerid + " SEND client resource: " + page);
						pluginfound = true;
						File localfile = null;
						if (page.startsWith("lib/")) {
							localfile = new File("./lib/client/" + page.substring(4));
						} else {
							localfile = new File("./plugins/" + page);
						}
						InputStream is = localfile.toURI().toURL().openStream();
						int in;
						while ((in = is.read()) > -1) {
							os.write(in);
						}
					}
				}
				if (!pluginfound) {
					Marmota.getLogger().warn("Conhandler: " + handlerid + " WARN no such resource: " + page);
					this.writeStringToStream(httpError(404, "Page not found"), os);
				}
				os.close();
			} catch (IOException e) {
				Marmota.getLogger().error("Conhandler: " + handlerid + " ERROR while SENDING RESOURCE to the client: " + page + " : "+ e.getMessage());
			}
		}
	}
	
	
	/**
	 * Generating and sending the .jnlp-descritor
	 * @param os OutputStream to send data to the client
	 * @param host The adress which was send from the client with the http-request
	 */
	private void sendjnlpdescriptor(OutputStream os, String host) {
		try {
			// Getting all filenames of the client-plugins
			Vector<String> pluginnames = Marmota.getClientPluginNames();
			// Sending standard header
			Marmota.getLogger().info("Connection handler " + handlerid + "(" + socket.getInetAddress() + "): sending jnlp");
			sendHeader(os, new URL("http://" + host + "/client/marmota.jnlp"));
			// Generating the descriptor
			StringBuffer sendBuffer = new StringBuffer();
			sendBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sendBuffer.append("<jnlp codebase=\"http://" + host + "/client/\" href=\"marmota.jnlp\">\n");
			sendBuffer.append("<information>\n");
			sendBuffer.append("<title>Marmota - A Groupware System</title>\n");
			sendBuffer.append("<vendor>The Marmota Development Team</vendor>\n");
			sendBuffer.append("<homepage href=\"http://marmota.berlios.de\"/>\n");
			sendBuffer.append("<description>An open source, easy to install Groupware-System</description>\n");
			sendBuffer.append("</information>\n");
			sendBuffer.append("<resources>\n");
			sendBuffer.append("<j2se version=\"1.6+\"/>\n");
			for (int i = 0; i < pluginnames.size(); i++) {
				sendBuffer.append("<jar href=\"" + pluginnames.get(i) + "\"/>\n");
			}
			sendBuffer.append("</resources>\n");
			sendBuffer.append("<application-desc main-class=\"de.berlios.marmota.core.client.MarmotaClient\">\n");
			sendBuffer.append("<argument>" + SERVER_HOST + "</argument>\n");
			sendBuffer.append("<argument>" + Marmota.CONFIG.getProperty("rmiserver_port") + "</argument>\n");
			sendBuffer.append("</application-desc>\n");
			sendBuffer.append("</jnlp>\n");
			os.write(sendBuffer.toString().getBytes());
			os.close();
		} catch (IOException e) {
			Marmota.getLogger().error("Connection handler " + handlerid + " ERROR sending jnlp  : " + e.getMessage());
			Marmota.getLogger().error("Connection handler " + handlerid + " from: " + socket.getInetAddress());
			Marmota.getLogger().error(e.getStackTrace());
			
		}
	}
	

	/**
	 * Should be used to create a http-header for the data
	 * which should be send
	 * @param os The Ouputstream of the socket
	 * @param url The url of the file which should been send
	 * @throws IOException 
	 */
	private void sendHeader(OutputStream os, URL url) throws IOException {
		// Major Head-Informations
		StringBuffer headbuffer = new StringBuffer();
		headbuffer.append("HTTP/1.0 200 OK\r\n");
		headbuffer.append("Server: " + Marmota.PNAME + "WebStart-WebServer " + 
				Marmota.MAJORVERSION + "." + Marmota.MINORVERSION + " " + 
				Marmota.VERSIONSUFFIX + "\r\n");
		// Sending content type
		String mimestring = "application/octet-stream";
		for (int i = 0; i < mimetypes.length; ++i) {
			if (url.toString().endsWith(mimetypes[i][0])) {
				mimestring = mimetypes[i][1];
				break;
			}
		}
		headbuffer.append("Content-type: " + mimestring + "\r\n");
		// Empty Line to show the client the end of the header
		headbuffer.append("\r\n");
		this.writeStringToStream(headbuffer.toString(), os);
	}
	
	
	/**
	 * Sending the default html-page to the client
	 * @param os The Ouputstream of the socket
	 */
	private void sendData(OutputStream os, String page) {
		URL url = this.getClass().getResource(docRoot + page);
		try {
			if (url == null || url.toString() == null) {
				throw new FileNotFoundException("URL is null");
			}
			sendHeader(os, url);
			InputStream urlInput = url.openStream();
			int i = 0;
			while ((i=urlInput.read()) >= 0) {
				os.write(i);
			}
			urlInput.close();
			os.close();
		} catch (FileNotFoundException e) {
			Marmota.getLogger().warn("Connection handler " + handlerid + " EXCEPTION  while sending: " + e.getMessage());
			Marmota.getLogger().warn("Connection handler " + handlerid + " can't find requested page: " + page);
			try {
				this.writeStringToStream(httpError(404, "Page not found"), os);
			} catch (IOException e1) {
				Marmota.getLogger().error("Connection handler " + handlerid + " ERROR : can't send errorpage from FNF" + e1.getMessage());
				e1.printStackTrace();
			}
		} catch (IOException e) {
			Marmota.getLogger().warn("Connection handler " + handlerid + " EXCEPTION " + " while sending: " + e.getMessage());
			Marmota.getLogger().warn("Connection handler " + handlerid + " EXCEPTION : client request: " + page);
			try {
				this.writeStringToStream(httpError(404, "unknown error"), os);
			} catch (IOException e1) {
				Marmota.getLogger().error("Connection handler " + handlerid + " ERROR in handler, can't send errorpage" + e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Helpfull class to send a string to an outputstream
	 * @param string The send to string
	 * @param os The OputputStream to send through
	 * @throws IOException Should be handled by the caller
	 */
	private void writeStringToStream(String string, OutputStream os) throws IOException {
		for (int i = 0; i < string.length(); i++) {
			os.write(string.getBytes()[i]);
		}
	}
	
	
	/**
	* Constructs an error-page for the client
	*/
	private String httpError(int code, String description) {
		StringBuffer buffer = new StringBuffer();
		Marmota.getLogger().debug("Handler " + handlerid + " sends errorpage with: " + code + ", " + description);
		buffer.append("HTTP/1.0 " + code + " " + description + "\r\n");
		buffer.append("Content-type: text/html\r\n\r\n");
		buffer.append("<html>");
		buffer.append("<head>");
		buffer.append("<title>WebServer-Error</title>");
		buffer.append("</head>");
		buffer.append("<body>");
		buffer.append("<h1>HTTP/1.0 " + code + "</h1>");
		buffer.append("<h3>" + description + "</h3>");
		buffer.append("</body>");
		buffer.append("</html>");
		return buffer.toString();
	}
	
} 