package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import io.github.fvarrui.javapackager.common.generators.ArtifactGenerator;
import io.github.fvarrui.javapackager.common.generators.GenerateDmg;
import io.github.fvarrui.javapackager.common.generators.GenerateMsi;
import io.github.fvarrui.javapackager.common.generators.GenerateMsm;
import io.github.fvarrui.javapackager.common.generators.GeneratePkg;
import io.github.fvarrui.javapackager.common.generators.GenerateSetup;
import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.maven.MavenContext;
import io.github.fvarrui.javapackager.model.Platform;

/**
 * Building-tool context 
 */
public abstract class Context<T> {
	
	public Context() {
		super();
		
		// building tool independent generators
		getInstallerGenerators(Platform.mac).add(new GenerateDmg());
		getInstallerGenerators(Platform.mac).add(new GeneratePkg());
		getInstallerGenerators(Platform.windows).add(new GenerateSetup());
		getInstallerGenerators(Platform.windows).add(new GenerateMsm());
		getInstallerGenerators(Platform.windows).add(new GenerateMsi());
		
	}
	
	// common properties
	
	public abstract File getRootDir();
	public abstract T getLogger();

	// platform independent functions
	
	public abstract File createRunnableJar(Packager packager) throws Exception;
	public abstract File createLibsFolder(Packager packager) throws Exception;
	public abstract File createTarball(Packager packager) throws Exception;
	public abstract File createZipball(Packager packager) throws Exception;
	public abstract File resolveLicense(Packager packager) throws Exception;
	public abstract File createWindowsExe(WindowsPackager packager) throws Exception;
	
	// installer producers
	
	private Map<Platform, List<ArtifactGenerator<? extends Packager>>> installerGeneratorsMap = new HashedMap<>();
	
	public List<ArtifactGenerator<? extends Packager>> getInstallerGenerators(Platform platform) {
		List<ArtifactGenerator<? extends Packager>> platformInstallers = installerGeneratorsMap.get(platform);
		if (platformInstallers == null) {
			platformInstallers = new ArrayList<>();
			installerGeneratorsMap.put(platform, platformInstallers);
		}
		return platformInstallers;
	}
	
	// static context

	private static Context<?> context;

	public static Context<?> getContext() {
		return context;
	}

	public static void setContext(Context<?> context) {
		Context.context = context;
	}
	
	public static boolean isMaven() {
		return context instanceof MavenContext; 
	}

	public static boolean isGradle() {
		return context instanceof GradleContext; 
	}
	
	public static MavenContext getMavenContext() {
		return (MavenContext) context;
	}

	public static GradleContext getGradleContext() {
		return (GradleContext) context;
	}
	
}
