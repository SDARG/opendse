package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.model.properties.DependencyPropertyService.ActivationModes;

/**
 * An {@link ApplicationModeConstraintGenerator} generates the constraints for
 * {@link ApplicationVariable}s with a certain application mode.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ApplicationModeConstraintGenerator {

	/**
	 * Generates the constraints enforcing a valid activation of the given {@link ApplicationVariable}s.
	 * 
	 * @param applicationVariables set of {@link ApplicationVariable}s sharing the same {@link ActivationModes}
	 * @return a set of constraints enforcing a valid activation of {@link ApplicationVariable}s
	 */
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables);

}
