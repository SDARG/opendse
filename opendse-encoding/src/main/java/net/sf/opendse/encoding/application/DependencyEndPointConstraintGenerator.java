package net.sf.opendse.encoding.application;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

/**
 * The {@link DependencyEndPointConstraintGenerator} formulates constraints that
 * ensure that a {@link Dependency} can not be activated if one of its end point
 * {@link Task}s is not activated.
 * 
 * @author Fedor Smirnov
 *
 */
public class DependencyEndPointConstraintGenerator {

	/**
	 * Formulates the constrains that ensure that a {@link Dependency} can not be
	 * activated if one of its end point {@link Task}s is not activated.
	 * 
	 * @param applicationVariables
	 *            set of all {@link ApplicationVariable}s used in the current
	 *            problem
	 * @return the constraint set that ensures that a {@link Dependency} can not be
	 *         activated if one of its end point {@link Task}s is not activated
	 */
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables) {
		Set<Constraint> result = new HashSet<Constraint>();
		// filter the dependencies
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				result.add(formulateDeactivationConstraint(dttVar, dttVar.getSourceTask()));
				result.add(formulateDeactivationConstraint(dttVar, dttVar.getDestinationTask()));
			}
		}
		return result;
	}

	/**
	 * The DTT variable may only be active if its endpoint task is active.
	 * 
	 * DTT - T <= 0
	 * 
	 * @param dttVar
	 * @param task
	 * @return the constraint that ensures that a dependency can not be active if
	 *         one of its end points is deactivated
	 */
	protected Constraint formulateDeactivationConstraint(DTT dttVar, Task task) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(dttVar));
		result.add(-1, Variables.p(Variables.varT(task)));
		return result;
	}
}