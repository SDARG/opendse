package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;

/**
 * An {@link ApplicationConstraintGenerator} generates the constraints for
 * {@link ApplicationVariable}s with a certain application mode.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ApplicationConstraintGenerator {

	/**
	 * Generates the constraints enforcing a valid activation of the given
	 * {@link ApplicationVariable}s.
	 * 
	 * @param applicationVariables
	 *            set of {@link ApplicationVariable}s sharing the same activation
	 *            mode attribute
	 * @return a set of constraints enforcing a valid activation of
	 *         {@link ApplicationVariable}s
	 */
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables);

}
