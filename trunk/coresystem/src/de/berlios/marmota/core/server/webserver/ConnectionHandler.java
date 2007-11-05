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

package de.berlios.marmota.core.server.webserver;

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
	 * Create the Handler
	 * @param socket The socket with the connection
	 * @param handlerid the id of the connection
	 */
	public ConnectionHandler(Socket socket, long handlerid) {
		this.socket = socket;
		this.handlerid = handlerid;
		Marmota.LOGGER.debug("Connection handler init: " + handlerid + " from : " + socket.getInetAddress());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Marmota.LOGGER.debug("Connection handler start: " + handlerid + " from : " + socket.getInetAddress());
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
						buffer = new StringBuffer();
					}
				} else {
					buffer.append(c);
				}
			}
			String requestedPage = null;
			for (int count = 0; count < incomingLines.size(); count++) {
				if (incomingLines.get(count).startsWith("GET")) {
					String[] getParts = incomingLines.get(count).split(" ");
					requestedPage = getParts[1];
					break;
				}
			}
			// Which site should be send?
			Marmota.LOGGER.info("Connection handler " + handlerid + "(" + socket.getInetAddress() + ") has a request for: " + requestedPage);
			if (requestedPage.equals("/") || requestedPage.equals("/index.html") || requestedPage.contains("..")) {
				requestedPage = ("/start.html");
			}
			sendData(socket.getOutputStream(), requestedPage);
			is.close();
			socket.close();
			Marmota.LOGGER.debug("Connection handler succesfully ends: " + handlerid + " from : " + socket.getInetAddress());
		} catch (Exception e) {
			Marmota.LOGGER.error("Error in handler " + handlerid + " : " + e.getMessage());
			Marmota.LOGGER.error("Connection come from: " + socket.getInetAddress());
			Marmota.LOGGER.error(e.getStackTrace());
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
			Marmota.LOGGER.warn("Exception in handler " + handlerid + " while sending: " + e.getMessage());
			Marmota.LOGGER.warn("handler " + handlerid + ": client request: " + page);
			try {
				this.writeStringToStream(httpError(404, "Page not found"), os);
			} catch (IOException e1) {
				Marmota.LOGGER.error("Exception in handler " + handlerid + ", can't send errorpage from FNF" + e1.getMessage());
				e1.printStackTrace();
			}
		} catch (IOException e) {
			Marmota.LOGGER.warn("Exception in handler " + handlerid + " while sending: " + e.getMessage());
			Marmota.LOGGER.warn("\thandler " + handlerid + ": client request: " + page);
			try {
				this.writeStringToStream(httpError(404, "unknown error"), os);
			} catch (IOException e1) {
				Marmota.LOGGER.error("Exception in handler " + handlerid + ", can't send errorpage" + e1.getMessage());
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
		Marmota.LOGGER.debug("Handler " + handlerid + " sends errorpage with: " + code + ", " + description);
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