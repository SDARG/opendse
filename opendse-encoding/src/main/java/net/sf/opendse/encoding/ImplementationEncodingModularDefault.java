package net.sf.opendse.encoding;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link ImplementationEncodingModularDefault} performs the basic encoding
 * without any preprocessing or additional constraints.
 * 
 * @author Fedor Smirnov
 *
 */
public class ImplementationEncodingModularDefault extends ImplementationEncodingModularAbstract {

	@Inject
	public ImplementationEncodingModularDefault(SpecificationPreprocessor preprocessor,
			ApplicationEncoding applicationEncoding, MappingEncoding mappingEncoding, RoutingEncoding routingEncoding,
			AllocationEncoding allocationEncoding, SpecificationWrapper specificationWrapper) {
		super(preprocessor, applicationEncoding, mappingEncoding, routingEncoding, allocationEncoding,
				specificationWrapper);
	}

	@Override
	protected Set<Constraint> formulateAdditionalConstraints() {
		// Returns an empty set
		return new HashSet<Constraint>();
	}
}
