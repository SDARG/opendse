package net.sf.opendse.encoding.interpreter;

import org.opt4j.satdecoding.Model;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.ImplementationEvaluator;

/**
 * The {@link SpecificationPostProcessor} performs the post processing of the
 * implementation-{@link Specification} after the decoding of the {@link Model}.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(SpecificationPostProcessorNone.class)
public interface SpecificationPostProcessor {

	/**
	 * Performs the post processing of the implementation {@link Specification}
	 * after the decoding of the {@link Model}.
	 * 
	 * @param implementation
	 *            the implementation {@link Specification} after the decoding of the
	 *            {@link Model}
	 * @return the implementation that will be available to the
	 *         {@link ImplementationEvaluator}s
	 */
	public Specification postProcessImplementation(Specification implementation);
}
