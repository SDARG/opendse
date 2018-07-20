package net.sf.opendse.encoding;

import org.opt4j.satdecoding.Model;

import net.sf.opendse.model.Specification;

/**
 * The {@link ImplementationInterpreter} is used to create an implementation
 * based on the {@link Model} containing a variable assignment satisfying the
 * constraint set encoding valid implementations.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ImplementationInterpreter {

	/**
	 * Returns the implementation based on the {@link Model} containing a variable
	 * assignment satisfying the constraint set encoding valid implementations.
	 * 
	 * @param specification
	 *            the {@link Specification} describing the entire search space of
	 *            the problem at hand
	 * @param model
	 *            the {@link Model} {@link Model} containing a variable assignment
	 *            satisfying the constraint set encoding valid implementations
	 * @return the implementation based on the {@link Model} containing a variable
	 *         assignment satisfying the constraint set encoding valid
	 *         implementations
	 */
	public Specification toImplementation(Specification specification, Model model);

}
