package net.sf.opendse.optimization.encoding;

import java.util.Collection;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Specification;

/**
 * The {@link ImplementationEncoding} generates a set of {@link Constraint}s
 * describing all valid implementations that can be generated from the
 * {@link Specification} used for the design space exploration.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ImplementationEncoding {

	/**
	 * Generates a set of {@link Constraint}s describing all valid implementations
	 * that can be generated from the {@link Specification} used for the design
	 * space exploration.
	 * 
	 * @return a set of {@link Constraint}s describing all valid implementations
	 *         that can be generated from the {@link Specification} used for the
	 *         design space exploration
	 */
	public Collection<Constraint> toConstraints();

}
