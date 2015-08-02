package io.github.anon10w1z.cpp.main;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CppInstaller {
	public static void main(String[] args) {
		try {
			String osName = System.getProperty("os.name").toUpperCase();
			String installedCppPath = osName.contains("WIN") ? System.getenv("AppData") : System.getProperty("user.home");
			if (osName.contains("MAC OS"))
				installedCppPath += "/Library/Application Support/minecraft";
			else if (!osName.contains("WIN"))
				installedCppPath += "/.minecraft";
			else
				installedCppPath += "\\.minecraft";
			installedCppPath += File.separatorChar + "mods" + File.separatorChar + "craft++-" + CppModInfo.VERSION + ".jar";
			File installedCppFile = new File(installedCppPath);
			File cppFile = new File(URLDecoder.decode(CppInstaller.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
			Files.copy(cppFile.toPath(), installedCppFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JOptionPane.showMessageDialog(null, "Installed Craft++ at " + installedCppPath, "Craft++ Installer", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
