package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Specification;
import net.sf.opendse.encoding.variables.InterfaceVariable;

/**
 * Interface for the classes providing constraints describing a valid
 * implementation.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ImplementationEncoding {

	/**
	 * formulates the constraint set describing a valid implementation
	 * 
	 * @param specification
	 *            a {@link Specification} describing the design space of the current
	 *            problem
	 * @return set of {@link Constraint}s describing valid implementations
	 */
	public Set<Constraint> toConstraints(Specification specification);

	/**
	 * return the variables encoding the implementation information
	 * 
	 * @return the set of variables describing the encoded implementation
	 */
	public Set<InterfaceVariable> getInterfaceVariables();
}
