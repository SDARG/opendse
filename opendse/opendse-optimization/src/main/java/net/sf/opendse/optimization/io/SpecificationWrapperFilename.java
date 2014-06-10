package net.sf.opendse.optimization.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.sf.opendse.io.SpecificationReader;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class SpecificationWrapperFilename extends SpecificationWrapperInstance {

	
	static SpecificationReader reader = new SpecificationReader();


	@Inject
	public SpecificationWrapperFilename(
			@Constant(namespace = SpecificationWrapperFilename.class, value = "filename") String filename) throws FileNotFoundException {
		super(reader.read(new FileInputStream(filename)));
	}

}
