package net.sf.opendse.encoding.application;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import net.sf.opendse.encoding.constraints.Constraints;
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
				result.add(Constraints.generateNegativeImplication(Variables.varT(dttVar.getSourceTask()), dttVar));
				result.add(Constraints.generateNegativeImplication(Variables.varT(dttVar.getDestinationTask()), dttVar));
			}
		}
		return result;
	}
}