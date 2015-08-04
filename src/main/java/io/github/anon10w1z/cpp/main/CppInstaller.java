package io.github.anon10w1z.cpp.main;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CppInstaller {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			JFrame installerFrame = new JFrame("Craft++ Installer");
			installerFrame.setLayout(new BoxLayout(installerFrame.getContentPane(), BoxLayout.X_AXIS));
			installerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			String osName = System.getProperty("os.name").toUpperCase();
			String defaultInstallPath = osName.contains("WIN") ? System.getenv("AppData") : System.getProperty("user.home");
			if (osName.contains("MAC OS"))
				defaultInstallPath += "/Library/Application Support/minecraft";
			else if (!osName.contains("WIN"))
				defaultInstallPath += "/.minecraft";
			else
				defaultInstallPath += "\\.minecraft";
			defaultInstallPath += File.separatorChar + "mods" + File.separatorChar + CppModInfo.MOD_ID + '-' + CppModInfo.VERSION + ".jar";

			JPanel installationDirectoryPanel = new JPanel();
			JLabel installationDirectoryText = new JLabel("Installation Path: ");
			installationDirectoryPanel.add(installationDirectoryText);
			JTextField installationDirectoryField = new JTextField(defaultInstallPath);
			installationDirectoryPanel.add(installationDirectoryField);
			installerFrame.add(installationDirectoryPanel);

			JPanel installationPanel = new JPanel();
			JButton installButton = new JButton("Install Craft++");
			installButton.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {

				}

				@Override
				public void mousePressed(MouseEvent e) {

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					try {
						String installPath = installationDirectoryField.getText();
						File installedCppFile = new File(installPath);
						File cppFile = new File(URLDecoder.decode(CppInstaller.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
						Files.copy(cppFile.toPath(), installedCppFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						JOptionPane.showMessageDialog(installerFrame, "Successfully installed Craft++ at " + installPath, "Craft++ Installer", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(installerFrame, "Could not install Craft++ at " + installationDirectoryField.getText(), "Craft++", JOptionPane.INFORMATION_MESSAGE);
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {

				}
			});
			installationPanel.add(installButton);
			installerFrame.add(installationPanel);

			installerFrame.pack();
			installerFrame.setVisible(true);
			installerFrame.setLocationRelativeTo(null);
		});
	}
}
