package net.sf.opendse.optimization.io;

import net.sf.opendse.model.Specification;

/**
 * A {@link SpecificationTransformer} changes the {@link Specification} before
 * the design space exploration starts.
 * 
 * @author Felix Reimann
 *
 */
public interface SpecificationTransformer {

	/**
	 * Changes the {@link Specification}.
	 * 
	 * @param specification
	 *            the specification to change
	 */
	public void transform(Specification specification);

	/**
	 * The priority changes the order in which {@link SpecificationTransformer}s
	 * are executed.
	 * 
	 * @return the priority
	 */
	public int getPriority();
}
