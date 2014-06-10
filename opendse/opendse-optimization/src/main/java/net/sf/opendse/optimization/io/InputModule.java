package net.sf.opendse.optimization.io;

import net.sf.opendse.optimization.SpecificationWrapper;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.start.Constant;

public class InputModule extends IOModule {

	@File
	@Constant(namespace = SpecificationWrapperFilename.class, value = "filename")
	protected String filename = "";

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	protected void config() {
		bind(SpecificationWrapper.class).to(SpecificationWrapperFilename.class).in(SINGLETON);
	}

}
