package net.sf.opendse.encoding;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorMulti;
import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPreprocessor} preprocesses the {@link Specification}
 * provided by the user. The preprocessing takes place before the encoding.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(SpecificationPreprocessorMulti.class)
public interface SpecificationPreprocessor {

	/**
	 * Generates the {@link Specification} that is to be used for the encoding
	 * process based on the specification provided by the user.
	 * 
	 * @param userSpecification
	 *            the {@link Specification} provided by the user
	 */
	public void preprocessSpecification(Specification userSpecification);
	
}
