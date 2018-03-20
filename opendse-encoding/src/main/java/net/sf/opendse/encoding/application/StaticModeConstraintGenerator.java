package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;

/**
 * Formulates the constraints for the static application parts. These parts are
 * always included into the implementation application.
 * 
 * @author Fedor Smirnov
 *
 */
public class StaticModeConstraintGenerator implements ApplicationModeConstraintGenerator {

	@Override
	public Set<ApplicationVariable> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<Constraint> constraints) {
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof T) {
				T tVar = (T) applVar;
				constraints.add(includeTask(tVar));
			} else {
				DTT dttVar = (DTT) applVar;
				constraints.add(includeDependency(dttVar));
			}
		}
		return applicationVariables;
	}

	/**
	 * Formulates the constraint to include the task into the implementation
	 * application.
	 * 
	 * T = 1
	 * 
	 * @param tVar
	 * @return constraint enforcing the activation of the given task
	 */
	protected Constraint includeTask(T tVar) {
		Constraint result = new Constraint(Operator.EQ, 1);
		result.add(Variables.p(tVar));
		return result;
	}

	/**
	 * Formulates the constraint to include the dependency into the implementation
	 * application.
	 * 
	 * DTT = 1
	 * 
	 * @param dttVar
	 * @return constraint enforcing the activation of the given variable
	 */
	protected Constraint includeDependency(DTT dttVar) {
		Constraint result = new Constraint(Operator.EQ, 1);
		result.add(Variables.p(dttVar));
		return result;
	}
}
