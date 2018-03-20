package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;

/**
 * Generates the constraints for application elements with a certain application
 * mode. Returns the encoded application variables.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ApplicationModeConstraintGenerator {

	/**
	 * Generates the constraints for application elements with a certain application
	 * mode. Returns the encoded application variables.
	 * 
	 * @param applicationVariables
	 *            application variables sharing the same mode
	 * @param constraints
	 *            the constraints formulated hitherto
	 * @return the encoded variables that may be relevant to other encoding modules
	 */
	public Set<ApplicationVariable> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<Constraint> constraints);

}
