package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.variables.ApplicationVariable;

/**
 * encodes valid applications and returns the variables containing the application information
 * 
 * @author Fedor Smirnov
 *
 */
public interface ApplicationEncoding {

	/**
	 * Formulates the application constraints and adds them to the input
	 * constraint set. Returns a set of variables describing the relevant nodes
	 * and edges of the application graph.
	 * 
	 * @param application the application graph of the specification
	 * @param constraints the set of constraints to describe the implementation
	 * @return set of variables describing the implementation application
	 */
	public Set<ApplicationVariable> toConstraints(Application<Task, Dependency> application, Set<Constraint> constraints);
	
}
