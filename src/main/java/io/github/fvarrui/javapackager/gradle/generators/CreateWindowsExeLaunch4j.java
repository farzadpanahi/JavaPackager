package io.github.fvarrui.javapackager.gradle.generators;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.common.generators.WindowsArtifactGenerator;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Creates Windows native executable on Gradle context
 */
public class CreateWindowsExeLaunch4j extends WindowsArtifactGenerator {
	
	private File genericManifest;
	private File genericIcon;
	private File genericJar;
	private File genericExe;
	
	public CreateWindowsExeLaunch4j() {
		super("Windows EXE");
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
		String jreMinVersion = packager.getJreMinVersion();
		File jarFile = packager.getJarFile();
		
		try {
			// creates a folder only for launch4j assets
			createAssets(packager);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		String jarPath = winConfig.isWrapJar() ? genericJar.getAbsolutePath() : jarFile.getName();
		
		Launch4jLibraryTask l4jTask = createLaunch4jTask();
		l4jTask.setHeaderType(winConfig.getHeaderType().toString());
		l4jTask.setJar(jarPath);
		l4jTask.setDontWrapJar(!winConfig.isWrapJar());
		l4jTask.setOutfile(genericExe.getName());
		l4jTask.setIcon(genericIcon.getAbsolutePath());
		l4jTask.setManifest(genericManifest.getAbsolutePath());
		l4jTask.setMainClassName(mainClass);
		l4jTask.setClasspath(new HashSet<>(packager.getClasspaths()));
		l4jTask.setChdir(useResourcesAsWorkingDir ? "." : "");
		l4jTask.setBundledJrePath(bundleJre ? jreDirectoryName : "%JAVA_HOME%");
		if (!StringUtils.isBlank(jreMinVersion)) { 
			l4jTask.setJreMinVersion(jreMinVersion);
		}
		l4jTask.getJvmOptions().addAll(vmArgs);
		l4jTask.setVersion(winConfig.getProductVersion());
		l4jTask.setTextVersion(winConfig.getTxtProductVersion());
		l4jTask.setCopyright(winConfig.getCopyright());
		l4jTask.setCompanyName(winConfig.getCompanyName());
		l4jTask.setFileDescription(winConfig.getFileDescription());
		l4jTask.setProductName(winConfig.getProductName());
		l4jTask.setInternalName(winConfig.getInternalName());
		l4jTask.setTrademarks(winConfig.getTrademarks());
		l4jTask.setLanguage(winConfig.getLanguage());
		l4jTask.setLibraryDir("");
		l4jTask.getActions().forEach(action -> action.execute(l4jTask));

		sign(genericExe, packager);
		
		FileUtils.copyFileToFile(genericExe, executable);
		
		return executable;
	}
	
	private Launch4jLibraryTask createLaunch4jTask() {
		return Context.getGradleContext().getProject().getTasks().create("launch4j_" + UUID.randomUUID(), Launch4jLibraryTask.class);
	}
	
	private void createAssets(WindowsPackager packager) throws Exception {
		
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File jarFile = packager.getJarFile();

		File launch4j = new File(Context.getGradleContext().getProject().getBuildDir(), "launch4j");
		if (!launch4j.exists()) {
			launch4j.mkdirs();
		}
		
		genericManifest = new File(launch4j, "app.exe.manifest");
		genericIcon = new File(launch4j, "app.ico");
		genericJar = new File(launch4j, "app.jar");
		genericExe = new File(launch4j, "app.exe");
		
		FileUtils.copyFileToFile(manifestFile, genericManifest);
		FileUtils.copyFileToFile(iconFile, genericIcon);
		FileUtils.copyFileToFile(jarFile, genericJar);
		
	}

}
