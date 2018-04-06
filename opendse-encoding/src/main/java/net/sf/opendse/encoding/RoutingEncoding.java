package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.routing.FlexibleRoutingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.MappingVariable;

/**
 * The {@link RoutingEncoding} encodes valid routings of communication tasks. An
 * encoder implementing this interface has to encode the {@link CR} and the
 * {@link CLRR} variables for each communication task that can possibly be
 * active in an implementation.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(FlexibleRoutingEncoding.class)
public interface RoutingEncoding {

	/**
	 * Formulates and returns a set of constraints describing routings that fulfill
	 * the data dependencies of the application encoded by the
	 * {@link ApplicationVariable}s. The information about the mapping is hereby
	 * taken from the provided set of {@link MappingVariable}s, while the provided
	 * {@link Routings} capture the part of the architecture that can be used to
	 * transmit the messages. The formulated constraints must encode the {@link CR}
	 * and the {@link CLRR} variables for each communication task that can possibly
	 * be active.
	 * 
	 * @param applicationVariables
	 *            set of {@link ApplicationVariable}s containing the application
	 *            information
	 * @param mappingVariables
	 *            set of {@link MappingVariable}s containing the mapping information
	 * @param routings
	 *            map mapping each communication onto an {@link Architecture} that
	 *            can be used to route the message
	 * @return a set of routing constraints enforcing a valid assignment of the
	 *         {@link CR} and the {@link CLRR} variables
	 */
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings);
}
