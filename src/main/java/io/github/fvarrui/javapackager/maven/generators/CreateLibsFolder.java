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

import org.apache.maven.plugin.MojoExecutionException;

import io.github.fvarrui.javapackager.common.generators.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CreateLibsFolder extends ArtifactGenerator<Packager> {

	public CreateLibsFolder() {
		super("Dependencies");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCopyDependencies();
	}
	
	@Override
	protected File doApply(Packager packager) {
		
		File libsFolder = new File(packager.getJarFileDestinationFolder(), "libs");
	
		// invokes 'maven-dependency-plugin' plugin to copy dependecies to app libs folder
		try {
			
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"), 
							artifactId("maven-dependency-plugin"), 
							version("3.1.1")
					),
					goal("copy-dependencies"),
					configuration(
							element("outputDirectory", libsFolder.getAbsolutePath())
					),
					Context.getMavenContext().getEnv()
				);
			
		} catch (MojoExecutionException e) {
			
			throw new RuntimeException("Error copying dependencies: " + e.getMessage());
			
		}

		return libsFolder;
	}

}
