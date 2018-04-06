package net.sf.opendse.optimization.encoding;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

import net.sf.opendse.model.Specification;

/**
 * The {@code Interpreter} receives a {@code Model} that satisfies the
 * constraints and determines the corresponding {@code Specification}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public interface Interpreter {

	/**
	 * Returns the implementation {@link Specification} that is created from the
	 * {@link Specification} describing the entire search space and the
	 * {@link Model} that satisfies the {@link Constraint}.
	 * 
	 * @param specification
	 *            the {@link Specification} describign the entire search space
	 * @param model
	 *            the {@link Model} that satisfies the {@link Constraint}s
	 * @return the {@link Specification} describing a concrete solution of the
	 *         problem at hand
	 */
	public Specification toImplementation(Specification specification, Model model);

}
