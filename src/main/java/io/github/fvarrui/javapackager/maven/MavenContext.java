package io.github.fvarrui.javapackager.maven;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.maven.generators.CreateLibsFolder;
import io.github.fvarrui.javapackager.maven.generators.CreateRunnableJar;
import io.github.fvarrui.javapackager.maven.generators.CreateTarball;
import io.github.fvarrui.javapackager.maven.generators.CreateWindowsExe;
import io.github.fvarrui.javapackager.maven.generators.CreateZipball;
import io.github.fvarrui.javapackager.maven.generators.GenerateDeb;
import io.github.fvarrui.javapackager.maven.generators.GenerateRpm;
import io.github.fvarrui.javapackager.maven.generators.ResolveLicenseFromPOM;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;

/**
 * Maven context 
 */
public class MavenContext extends Context<Log> {

	private Log logger;
	private ExecutionEnvironment env;
	
	public MavenContext(ExecutionEnvironment env, Log logger) {
		super();
		
		this.env = env;
		this.logger = logger;
		
		// maven dependant generators 		
		getInstallerGenerators(Platform.linux).add(new GenerateDeb());
		getInstallerGenerators(Platform.linux).add(new GenerateRpm());
		
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public Log getLogger() {
		return logger;
	}

	@Override
	public File getRootDir() {
		return env.getMavenProject().getBasedir();
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
		return new ResolveLicenseFromPOM().apply(packager);
	}
	
	@Override
	public File createWindowsExe(WindowsPackager packager) throws Exception {
		return new CreateWindowsExe().apply(packager);
	}

}
