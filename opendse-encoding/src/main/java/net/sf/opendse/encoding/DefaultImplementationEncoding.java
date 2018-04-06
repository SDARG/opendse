package net.sf.opendse.encoding;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link DefaultImplementationEncoding} performs the basic encoding without
 * any preprocessing or additional constraints.
 * 
 * @author Fedor Smirnov
 *
 */
public class DefaultImplementationEncoding extends AbstractImplementationEncoding {

	@Inject
	public DefaultImplementationEncoding(ApplicationEncoding applicationEncoding, MappingEncoding mappingEncoding,
			RoutingEncoding routingEncoding, AllocationEncoding allocationEncoding, SpecificationWrapper specificationWrapper) {
		super(applicationEncoding, mappingEncoding, routingEncoding, allocationEncoding, specificationWrapper);
	}

	@Override
	protected void preprocessSpecification() {
		// Does nothing.
	}

	@Override
	protected Set<Constraint> formulateAdditionalConstraints() {
		// Returns an empty set
		return new HashSet<Constraint>();
	}
}
