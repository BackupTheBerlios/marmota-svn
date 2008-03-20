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

package de.berlios.marmota.core.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.berlios.marmota.core.common.userManagment.User;
import de.berlios.marmota.core.common.userManagment.UserRemoteInterface;

import net.miginfocom.swing.MigLayout;

/**
 * The Login-Window which ident the user.
 * It will be displayed as the first window.
 * @author sebmeyer
 */
public class LoginFrame extends JFrame {
	
	/** Generated SVUID */
	private static final long serialVersionUID = 9119212780202861108L;

	/** Textfield for the username */
	JTextField userField = new JTextField(20);
	
	/** Textfield for the password */
	JPasswordField passField = new JPasswordField(20);
	
	/** OK Button */
	JButton okBut = new JButton("OK");
	
	/** Cancel Button */
	JButton cancelBut = new JButton("Cancel");
	
	/**
	 * Default construct
	 */
	public LoginFrame() {
		this.setTitle("Marmota Login");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createLayout();
	}
	
	
	/**
	 * Init and create the Layout
	 */
	private void createLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		
		
		panel.add(new JLabel("User:", JLabel.RIGHT), "align right");
		panel.add(userField, "growx, wrap");
		panel.add(new JLabel("Password:", JLabel.RIGHT), "align right");
		panel.add(passField, "growx, wrap");
		
		JPanel southpanel = new JPanel();
		
		southpanel.add(okBut);
		okBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performLoginPressed();
			}
		});
		
		southpanel.add(cancelBut);
		cancelBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performCancelPressed();
			}
		});
		
		panel.add(southpanel, "south");
		this.add(panel);
	}


	/**
	 * Will be called when pressing the "OK"-Button
	 */
	protected void performLoginPressed() {
		try {
			UserRemoteInterface userManagment = (UserRemoteInterface) MarmotaClient.SERVER_REGISTRY.lookup("UserManagment");
			StringBuffer pass = new StringBuffer();
			for (int i = 0; i < passField.getPassword().length; i++) {
				pass.append(passField.getPassword()[i]);
			}
			User user = userManagment.login(userField.getText(), pass.toString());
			if (user == null) {
				JOptionPane.showMessageDialog(this, "Username or password wrong!", "Failed!", JOptionPane.ERROR_MESSAGE);
			} else {
				MarmotaClient.setCurrentuser(user);
				MarmotaClient.showMainWindow();
				this.dispose();
			}
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Will be called when pressing the "Cancel"-Button 
	 */
	protected void performCancelPressed() {
		System.exit(0);
	}

}
