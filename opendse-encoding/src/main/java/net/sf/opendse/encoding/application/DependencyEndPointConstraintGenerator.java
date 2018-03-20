package net.sf.opendse.encoding.application;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Task;

public class DependencyEndPointConstraintGenerator implements DependencyConstraintGenerator{

	@Override
	public void toConstraints(Set<ApplicationVariable> applicationVariables, Set<Constraint> constraints) {
		// filter the dependencies
		for (ApplicationVariable applVar : applicationVariables) {
			if(applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				constraints.add(formulateDeactivationConstraint(dttVar, dttVar.getSourceTask()));
				constraints.add(formulateDeactivationConstraint(dttVar, dttVar.getDestinationTask()));
			}
		}
	}
	
	/**
	 * The DTT variable may only be active if its endpoint task is active. 
	 * 
	 * DTT - T <= 0
	 * 
	 * @param dttVar
	 * @param task
	 * @return
	 */
	protected Constraint formulateDeactivationConstraint(DTT dttVar, Task task) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(dttVar));
		result.add(-1, Variables.p(Variables.var(task)));
		return result;
	}
}