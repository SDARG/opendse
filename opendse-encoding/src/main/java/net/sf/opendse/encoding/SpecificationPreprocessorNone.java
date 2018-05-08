package net.sf.opendse.encoding;

import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPreprocessorNone} does not alter the {@link Specification} provided by the user at all.
 * 
 * @author Fedor Smirnov
 *
 */
public class SpecificationPreprocessorNone implements SpecificationPreprocessor{

	@Override
	public Specification preprocessSpecification(Specification userSpecification) {
		return userSpecification;
	}
}
