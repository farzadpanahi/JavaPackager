package io.github.fvarrui.javapackager.gradle.generators;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

import io.github.fvarrui.javapackager.common.generators.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CreateLibsFolder extends ArtifactGenerator<Packager> {
	
	public Copy copyLibsTask;
	
	public CreateLibsFolder() {
		super("Libs folder");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCopyDependencies();
	}
	
	@Override
	protected File doApply(Packager packager) {
		
		File libsFolder = new File(packager.getJarFileDestinationFolder(), "libs");
		Project project = Context.getGradleContext().getProject();
	
		copyLibsTask = (Copy) project.getTasks().findByName("copyLibs");
		if (copyLibsTask == null) {
			copyLibsTask = project.getTasks().create("copyLibs", Copy.class);
		}
		copyLibsTask.from(project.getConfigurations().getByName("default"));
		copyLibsTask.into(project.file(libsFolder));
		copyLibsTask.getActions().forEach(action -> action.execute(copyLibsTask));
		
		return libsFolder;
	}

}
