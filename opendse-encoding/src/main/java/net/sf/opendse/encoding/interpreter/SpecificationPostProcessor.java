package net.sf.opendse.encoding.interpreter;

import org.opt4j.satdecoding.Model;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPostProcessor} performs the post processing of the
 * implementation-{@link Specification} after the decoding of the {@link Model}.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(SpecificationPostProcessorMulti.class)
public interface SpecificationPostProcessor {

	/**
	 * Performs the post processing of the implementation {@link Specification}
	 * after the decoding of the {@link Model}.
	 * 
	 * @param implementation
	 *            the implementation {@link Specification} after the decoding of the
	 *            {@link Model}
	 */
	public void postProcessImplementation(Specification implementation);
	
}
