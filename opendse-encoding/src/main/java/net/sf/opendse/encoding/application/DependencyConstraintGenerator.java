package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.ApplicationVariable;

/**
 * formulates the constraints stating that a dependency may only be active of
 * both its endpoint tasks are active
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(DependencyEndPointConstraintGenerator.class)
public interface DependencyConstraintGenerator {

	/**
	 * formulates the constraints stating that a dependency may only be active of
	 * both its endpoint tasks are active
	 * 
	 * @param applicationVariables
	 *            all relevant tasks and dependencies
	 * @param constraints
	 *            the constraints formulated hitherto
	 */
	public void toConstraints(Set<ApplicationVariable> applicationVariables, Set<Constraint> constraints);

}
