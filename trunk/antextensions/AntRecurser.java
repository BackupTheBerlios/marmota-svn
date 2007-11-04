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

import java.io.BufferedInputStream;
import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * This is extension for Ant to make it possible to run ant-files
 * recursive in several directories.
 * 
 * So you can give this extension a directory and it will search for
 * special files in this directory (and its subdirectories) to run the
 * scripts
 * 
 * @author sebmeyer
 */
public class AntRecurser extends Task {
	
	
	/**
	 * The direcotry in which the files will be searched
	 */
	private String dir;
	
	
	/**
	 * The filename which should be found (!no regex or wildcards!)
	 */
	private String searchFile;
	
	
	/**
	 * The which should be executed in the ant-file
	 */
	private String target;
	
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		File basedir = new File(dir);
		if (basedir.isDirectory()) {
			recurse(basedir);
		}
	}
	
	
	/**
	 * Recursive calling
	 * @param f The file to search in
	 */
	private void recurse(File f) {
		try  {
			for (int i = 0; i < f.listFiles().length; i++) {
				File childFile = f.listFiles()[i];
				if (childFile.isDirectory()) {
					recurse(childFile);
				} else {
					if (childFile.getName().toLowerCase().equals(searchFile.toLowerCase())) {
						System.out.println("Workin with BUILD-FILE: " + childFile.getPath());
						String command = "ant " + target + " " + childFile.getName();
						Process p = Runtime.getRuntime().exec(command, null, childFile.getParentFile());
						p.waitFor();
						BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
						while (true) {
							int stin = bis.read();
							if (stin == -1) {
								break;
							} else {
								System.out.print((char)stin);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to exit recursive ant-file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Set the directory in which we search
	 * @param dir The directory to serach in
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	
	/**
	 * Set the filename which should be found
	 * @param filename The filename which should be found
	 */
	public void setSearchFile(String filename) {
		this.searchFile = filename;
	}
	
	
	/**
	 * Set the target which should be called in the build-script
	 * @param target The target which should be called in the build-script
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}
