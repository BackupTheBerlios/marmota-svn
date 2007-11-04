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

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * This is a little extension for ant to make it possible to create
 * a manifest-file for a .jar-archiv with all lib-jar-archives
 * in a directory
 * @author sebmeyer
 */
public class MFLibCollector extends Task {
	
	
	/**
	 * The directory with the libraries
	 */
	private String dir;
	
	
	/**
	 * The Mainclass of the project
	 */
	private String mainClass;
	
	
	/**
	 * The file in which the data should be saved
	 */
	private String outfile;
	
	
	/**
	 * The prefix for the files
	 * (The directory f.e.)
	 */
	private String prefix;
	
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			StringBuffer buffer = new StringBuffer();
			File dirfile = new File(dir);
			if (dirfile.isDirectory()) {
				System.out.println("Searching for libs in: " + dirfile);
				for (int i = 0; i < dirfile.listFiles().length; i++) {
					File file = dirfile.listFiles()[i];
					if (file.getName().toLowerCase().endsWith(".jar")) {
						buffer.append(prefix + file.getName() + " ");
					}
				}
				System.out.println("Creating file: " + outfile);
				FileOutputStream fos = new FileOutputStream(new File(outfile));
				fos.write("Main-Class: ".getBytes());
				fos.write(mainClass.getBytes());
				fos.write("\nClass-Path: ".getBytes());
				fos.write(buffer.toString().getBytes());
				fos.write("\n".getBytes());
			}
		} catch (Exception e) {
			System.out.println("Error while creating the lib-list: " + e.getMessage());
		}
	}
	
	
	/**
	 * Set the directory to search in
	 * @param dir the directory to search in
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	/**
	 * Setting the main-class
	 * @param mainClass the name of the main-class
	 */
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	
	/**
	 * Set the file in which the data should bes saved
	 * @param outfile the file in which the data should bes saved
	 */
	public void setOutfile(String outfile) {
		this.outfile = outfile;
	}
	
	
	/**
	 * Set the file-prefix
	 * @param prefix the file-prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
