package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Specification;

/**
 * The {@link ImplementationEncoding} generates the set of {@link Constraint}s
 * that encode an implementation, that is a valid solution to the design problem
 * in the design space described by the {@link Specification}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ImplementationEncoding {

	/**
	 * Returns the set of {@link Constraint}s describing a valid implementation.
	 * 
	 * @param specification
	 *            The {@link Specification} describing the entire design space
	 *            of the design problem at hand.
	 * @return the set of {@link Constraint}s describing a valid implementation.
	 */
	public Set<Constraint> toConstraints(Specification specification);

}
