package io.github.fvarrui.javapackager.maven.generators;

import java.io.File;

import io.github.fvarrui.javapackager.common.generators.WindowsArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CreateWindowsExe extends WindowsArtifactGenerator {
	
	public CreateWindowsExe() {
		super("Windows EXE");
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		switch (packager.getWinConfig().getExeCreationTool()) {
		case launch4j: return new CreateWindowsExeLaunch4j().apply(packager);
		case winrun4j: return new CreateWindowsExeWinRun4j().apply(packager);
		default: return null;
		}
	}

}
