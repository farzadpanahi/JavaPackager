package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import io.github.fvarrui.javapackager.gradle.generators.CreateLibsFolder;
import io.github.fvarrui.javapackager.gradle.generators.CreateRunnableJar;
import io.github.fvarrui.javapackager.gradle.generators.CreateTarball;
import io.github.fvarrui.javapackager.gradle.generators.CreateWindowsExeLaunch4j;
import io.github.fvarrui.javapackager.gradle.generators.CreateZipball;
import io.github.fvarrui.javapackager.gradle.generators.GenerateDeb;
import io.github.fvarrui.javapackager.gradle.generators.GenerateRpm;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;

/**
 * Gradle context 
 */
public class GradleContext extends Context<Logger> {

	private Project project;
	
	public GradleContext(Project project) {
		super();
		this.project = project;
		
		// gradle dependant generators 
		getInstallerGenerators(Platform.linux).add(new GenerateDeb());
		getInstallerGenerators(Platform.linux).add(new GenerateRpm());
		
	}

	public Logger getLogger() {
		return project.getLogger();
	}

	public Project getProject() {
		return project;
	}

	@Override
	public File getRootDir() {
		return project.getRootDir();
	}

	@Override
	public File createRunnableJar(Packager packager) throws Exception {
		return new CreateRunnableJar().apply(packager);
	}

	@Override
	public File createLibsFolder(Packager packager) throws Exception {
		return new CreateLibsFolder().apply(packager);
	}

	@Override
	public File createTarball(Packager packager) throws Exception {
		return new CreateTarball().apply(packager);
	}

	@Override
	public File createZipball(Packager packager) throws Exception {
		return new CreateZipball().apply(packager);
	}

	@Override
	public File resolveLicense(Packager packager) throws Exception {
		// do nothing
		return null;
	}
	
	@Override
	public File createWindowsExe(WindowsPackager packager) throws Exception {
		return new CreateWindowsExeLaunch4j().apply(packager);	
	}

}
