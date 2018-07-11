package net.sf.opendse.optimization.io;

import java.io.FileNotFoundException;
import java.util.Set;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import net.sf.opendse.io.ReaderTGFF;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * {@link SpecificationWrapper} for {@lińk SpecificationTypeBased}s.
 * 
 * @author Valentina Richthammer
 */
public class SpecificationWrapperTypeBased extends SpecificationWrapperInstance {

	private static ReaderTGFF reader = new ReaderTGFF();

	@Inject
	public SpecificationWrapperTypeBased(
			@Constant(namespace = SpecificationWrapperTypeBased.class, value = "tgffFileName") String tgffFileName)
			throws FileNotFoundException {

		super(new SpecificationTransformerTypeBased().transform((reader.read(tgffFileName))));
	}

	@Override
	@Inject(optional = true)
	public void setSpecificationTransformers(Set<SpecificationTransformer> transformers) {
		super.setSpecificationTransformers(transformers);
	}
}
