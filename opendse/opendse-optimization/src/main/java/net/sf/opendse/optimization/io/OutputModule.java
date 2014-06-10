package net.sf.opendse.optimization.io;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.start.Constant;

public class OutputModule extends IOModule {
	
	@File
	@Constant(namespace = ImplementationOutput.class, value = "filename")
	protected String filename = "";

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	protected void config() {
		addOptimizerStateListener(ImplementationOutput.class);
	}
	
	

}
