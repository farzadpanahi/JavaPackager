package io.github.fvarrui.javapackager.maven.generators;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import io.github.fvarrui.javapackager.common.generators.WindowsArtifactGenerator;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.JarUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates Windows executable with WinRun4j 
 */
public class CreateWindowsExeWinRun4j extends WindowsArtifactGenerator {
	
	public CreateWindowsExeWinRun4j() {
		super("Windows WinRun4j EXE");
	}
	
	@Override
	public boolean skip(WindowsPackager packager) {
		if (!Platform.windows.isCurrentPlatform()) {
			Logger.error(getArtifactName() + " only can be generated on Windows!");
			return true;
		}
		return false;
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		
		String name = packager.getName();
		File executable = packager.getExecutable();
		File jarFile = packager.getJarFile();
		File assetsFolder = packager.getAssetsFolder();
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File libsFolder = packager.getLibsFolder();
		File appFolder = packager.getAppFolder();
		String mainClass = packager.getMainClass();
		
		// makes winrun4j assets folder 
		File winRun4jFolder = FileUtils.mkdir(assetsFolder, "winrun4j");
		
		// creates generic manifest 
		File genericManifest = new File(winRun4jFolder, "app.exe.manifest");
		FileUtils.copyFileToFile(manifestFile, genericManifest);

		// creates generic manifest 
		File genericIcon = new File(winRun4jFolder, "app.ico");
		FileUtils.copyFileToFile(iconFile, genericIcon);

		// creates generic exe 
		File genericExe = new File(winRun4jFolder, "app.exe");
		FileUtils.copyResourceToFile("/windows/WinRun4J64.exe", genericExe);

		// copies rcedit command line tool (needed to manipulate exe)  
		File rcedit = new File(winRun4jFolder, "rcedit.exe");
		FileUtils.copyResourceToFile("/windows/rcedit-x64.exe", rcedit);
				
		// generates ini file
		File genericIni = new File(winRun4jFolder, "app.ini");
		VelocityUtils.render("windows/ini.vtl", genericIni, packager);
		Logger.info("INI file generated in " + genericIni.getAbsolutePath() + "!");
		
		// process EXE with rcedit-x64.exe
		CommandUtils.execute(rcedit.getAbsolutePath(), genericExe, "--set-icon", genericIcon);
		CommandUtils.execute(rcedit.getAbsolutePath(), genericExe, "--application-manifest", genericManifest);

		// creates libs folder if it doesn't exist
		if (libsFolder == null) {
			libsFolder = FileUtils.mkdir(appFolder, "libs");
		}
		
		// copies JAR to libs folder		
		FileUtils.copyFileToFolder(jarFile, libsFolder);

		// copies winrun4j launcher helper library (needed to work around 
		File winrun4jJar = new File(libsFolder, "winrun4j-launcher-0.0.1.jar");
		FileUtils.copyResourceToFile("/windows/winrun4j-launcher-0.0.1.jar", winrun4jJar);

		// generates winrun4j properties pointing to main class
		File propertiesFile = new File(winRun4jFolder, "winrun4j.properties");
		Properties properties = new Properties();
		properties.setProperty("main.class", mainClass);
		properties.store(new FileOutputStream(propertiesFile), "WinRun4J Helper Launcher Properties");
				
		// copies winrun4j properties to launcher jar
		JarUtils.addFileToJar(winrun4jJar, propertiesFile);
		
		// copies ini file to app folder
		File iniFile = new File(appFolder, name + ".ini");
		FileUtils.copyFileToFile(genericIni, iniFile);

		// signs generated exe file
		sign(genericExe, packager);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(genericExe, executable);
		
		return executable;
	}

}
