package net.sf.opendse.encoding.interpreter;

import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPostProcessorNone} does not alter the input
 * implementation in any way.
 * 
 * @author Fedor Smirnov
 *
 */
public class SpecificationPostProcessorNone implements SpecificationPostProcessor {

	@Override
	public Specification postProcessImplementation(Specification implementation) {
		return implementation;
	}

}
