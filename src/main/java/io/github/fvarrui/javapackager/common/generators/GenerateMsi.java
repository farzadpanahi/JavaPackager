package io.github.fvarrui.javapackager.common.generators;

import static io.github.fvarrui.javapackager.utils.CommandUtils.execute;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

/**
 * Creates a MSI file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateMsi extends WindowsArtifactGenerator {

	public GenerateMsi() {
		super("MSI installer");
	}
	
	@Override
	public boolean skip(WindowsPackager packager) {
		return !packager.getWinConfig().isGenerateMsi() || !Platform.windows.isCurrentPlatform();
	}
	
	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		
		File msmFile = new GenerateMsm().doApply(packager);
		Logger.info("MSM file generated in " + msmFile);

		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".wxs");
		VelocityUtils.render("windows/wxs.vtl", wxsFile, packager);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// pretiffy wxs
		XMLUtils.prettify(wxsFile);
	
		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".wixobj");
		execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msiFile = new File(outputDirectory, name + "_" + version + ".msi");
		execute("light", "-spdb", "-out", msiFile, wixobjFile);

		// setup file
		if (!msiFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}
		
		// sign installer
		sign(msiFile, packager);
		
		return msiFile;
	}
	
}
