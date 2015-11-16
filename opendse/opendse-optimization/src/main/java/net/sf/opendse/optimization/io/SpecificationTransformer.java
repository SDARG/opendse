package net.sf.opendse.optimization.io;

import net.sf.opendse.model.Specification;

public interface SpecificationTransformer {
	public int getPriority();

	public void transform(Specification specification);
}
