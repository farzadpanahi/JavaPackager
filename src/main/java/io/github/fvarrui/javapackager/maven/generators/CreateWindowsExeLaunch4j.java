package io.github.fvarrui.javapackager.maven.generators;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.common.generators.WindowsArtifactGenerator;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CreateWindowsExeLaunch4j extends WindowsArtifactGenerator {
	
	public CreateWindowsExeLaunch4j() {
		super("Windows Launch4j EXE");
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		
		List<String> vmArgs = packager.getVmArgs();
		WindowsConfig winConfig = packager.getWinConfig();
		File executable = packager.getExecutable();
		String mainClass = packager.getMainClass();
		boolean useResourcesAsWorkingDir = packager.isUseResourcesAsWorkingDir();
		boolean bundleJre = packager.getBundleJre();
		String jreDirectoryName = packager.getJreDirectoryName(); 
		String classpath = packager.getClasspath();
		String jreMinVersion = packager.getJreMinVersion();
		File jarFile = packager.getJarFile();
		File assetsFolder = packager.getAssetsFolder();
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();

		File launch4jFolder = FileUtils.mkdir(assetsFolder, "launch4j");		
		File genericManifest = new File(launch4jFolder, "app.exe.manifest");
		File genericIcon = new File(launch4jFolder, "app.ico");
		File genericJar = new File(launch4jFolder, "app.jar");
		File genericExe = new File(launch4jFolder, "app.exe");
		
		FileUtils.copyFileToFile(manifestFile, genericManifest);
		FileUtils.copyFileToFile(iconFile, genericIcon);
		FileUtils.copyFileToFile(jarFile, genericJar);
		
		String jarPath = winConfig.isWrapJar() ? genericJar.getAbsolutePath() : jarFile.getName();
	
		List<Element> optsElements = vmArgs.stream().map(arg -> element("opt", arg)).collect(Collectors.toList());
		
		List<Element> jreElements = new ArrayList<>();
		jreElements.add(element("opts", optsElements.toArray(new Element[optsElements.size()])));
		jreElements.add(element("path", bundleJre ? jreDirectoryName : "%JAVA_HOME%"));
		if (!StringUtils.isBlank(jreMinVersion)) {
			jreElements.add(element("minVersion", jreMinVersion));
		}
		
		List<Element> pluginConfig = new ArrayList<>();
		pluginConfig.add(element("headerType", "" + winConfig.getHeaderType()));
		pluginConfig.add(element("jar", jarPath));
		pluginConfig.add(element("dontWrapJar", "" + !winConfig.isWrapJar()));
		pluginConfig.add(element("outfile", genericExe.getAbsolutePath()));
		pluginConfig.add(element("icon", genericIcon.getAbsolutePath()));
		pluginConfig.add(element("manifest", genericManifest.getAbsolutePath()));
		pluginConfig.add(
				element("classPath",
						element("mainClass", mainClass),
						element("preCp", classpath),
						element("addDependencies", "false")
					)
				);
		pluginConfig.add(element("chdir", useResourcesAsWorkingDir ? "." : ""));		
		pluginConfig.add(element("jre", jreElements.toArray(new Element[jreElements.size()])));
		pluginConfig.add(
					element("versionInfo", 
						element("fileVersion", winConfig.getFileVersion()),
						element("txtFileVersion", winConfig.getTxtFileVersion()),
						element("productVersion", winConfig.getProductVersion()),
						element("txtProductVersion", winConfig.getTxtProductVersion()),
						element("copyright", winConfig.getCopyright()),
						element("companyName", winConfig.getCompanyName()),
						element("fileDescription", winConfig.getFileDescription()),
						element("productName", winConfig.getProductName()),
						element("internalName", winConfig.getInternalName()),
						element("originalFilename", winConfig.getOriginalFilename()),
						element("trademarks", winConfig.getTrademarks()),
						element("language", winConfig.getLanguage())
					)
				);

		// invokes launch4j plugin to generate windows executable
		executeMojo(
				plugin(
						groupId("com.akathist.maven.plugins.launch4j"), 
						artifactId("launch4j-maven-plugin"),
						version("1.7.25")
				),
				goal("launch4j"),
				configuration(pluginConfig.toArray(new Element[pluginConfig.size()])),
				Context.getMavenContext().getEnv()
			);
		
		sign(genericExe, packager);
		
		FileUtils.copyFileToFile(genericExe, executable);

		return executable;
	}

}
