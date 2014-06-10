package net.sf.opendse.optimization.io;

import net.sf.opendse.encoding.RoutingFilter;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

public class SpecificationWrapperInstance implements SpecificationWrapper {

	protected final Specification specification;
	
	public SpecificationWrapperInstance(Specification specification) {
		super();
		this.specification = specification;
		RoutingFilter.filter(this.specification);
	}

	@Override
	public Specification getSpecification() {
		return specification;
	}

}
