package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.mapping.DefaultMappingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;

/**
 * encodes valid mappings and returns the variables containing the mapping
 * information
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(DefaultMappingEncoding.class)
public interface MappingEncoding {

	/**
	 * Formulates the mapping constraints and adds them to the input constraint set.
	 * Returns the set of variables describing the implementation mapping.
	 * 
	 * @param mappings
	 * @param applicationVariables
	 * @param constraints
	 * @return set of variables describing the mapping decisions
	 */
	public Set<MappingVariable> toConstraints(Mappings<Task, Resource> mappings,
			Set<ApplicationVariable> applicationVariables, Set<Constraint> constraints);
}
