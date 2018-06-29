package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ProxyEncoderLazy} does not do anything and relies on postprocessing for the generation of the correct implementation.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxyEncoderLazy implements ProxyEncoder {

	@Override
	public Set<Constraint> toConstraints(Task communication, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables, Set<ApplicationVariable> applicationVariables) {
		return new HashSet<Constraint>();
	}
	
}
