package net.sf.opendse.optimization.io;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.start.Constant;

import net.sf.opendse.model.SpecificationTypeBased;
import net.sf.opendse.model.SpecificationWrapper;

/**
 * Input module for {@link SpecificationTypeBased}s imported from tgff-files.
 * 
 * @author Valentina Richthammer
 */
public class InputTgffModule extends IOModule {

	@File(".tgff")
	@Constant(namespace = SpecificationWrapperTypeBased.class, value = "tgffFileName")
	protected String tgffFileName = "";

	public String getTgffFileName() {
		return tgffFileName;
	}

	public void setTgffFileName(String tgffFileName) {
		this.tgffFileName = tgffFileName;
	}

	@Override
	protected void config() {
		bind(SpecificationWrapper.class).to(SpecificationWrapperTypeBased.class).in(SINGLETON);
	}

}
